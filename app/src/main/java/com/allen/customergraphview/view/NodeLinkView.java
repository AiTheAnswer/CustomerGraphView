package com.allen.customergraphview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.allen.customergraphview.model.Category;
import com.allen.customergraphview.model.Label;
import com.allen.customergraphview.model.NodeLinkModel;
import com.allen.customergraphview.model.Node;
import com.allen.customergraphview.model.PointF;
import com.allen.customergraphview.utils.NodeLinkUtil;
import com.allen.customergraphview.utils.DensityUtil;

import java.util.List;

/**
 * 节点流程图的自定义View
 *
 * @author Renjy
 */
public class NodeLinkView extends ScaleView {
    private Context mContext;
    private String[] colors = {"#FBB367", "#80B1D2", "#FB8070", "#CC99FF", "#B0D961",
            "#99CCCC", "#BEBBD8", "#FFCC99", "#8DD3C8", "#FF9999",
            "#CCEAC4", "#BB81BC", "#FBCCEC", "#CCFF66", "#99CC66",
            "#66CC66", "#FF6666", "#FFED6F", "#ff7f50", "#87cefa"};
    //View的宽度
    private int mWidth;
    //View的高度
    private int mHeight;
    //圆环的圆心坐标
    private PointF mCenterPointF;
    //节点流程图内容的范围
    private RectF rectF;
    //图例和圆环之间的间隔
    private int legendPadding;
    //图例名称的最大长度
    private float mLegendMaxTextLength;
    //图例文字大小
    private float mLegendTextSize;
    //View 最大内切圆的半径
    private float mRectMaxRadius;
    //比重最大圆所允许的最大半径
    private float mAllowMaxCircleRadius;
    //节点图的数据对象
    private NodeLinkModel mNodeGraphModel;
    //绘制节点的画笔
    private Paint mNodePaint;
    //绘制连接线的画笔
    private Paint mLinkLinePaint;
    //绘制图例文字的画笔
    private Paint mLegendTextPaint;
    //View当前的状态
    private TYPE mViewTyp = TYPE.INIT;
    //图例和内容区域之间的间隔
    private int mLegendPaddingBottom;
    //节点流程图的父容器
    private NodeLinkGroup nodeLinkGroup;
    //画布缩放平移后显示的区域
    private Rect clipBounds;

    /**
     * View 当前的状态
     */
    enum TYPE {
        INIT,//初始化状态（用户未设置数据）
        DATA,//用户设置了数据，且数据不为空
        NO_DATA//用户设置了，但是数据为空
    }

    public NodeLinkView(Context context) {
        this(context, null);
    }

    public NodeLinkView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeLinkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    /**
     * 初始化绘制需要的对象
     */
    private void init() {
        //View中心点
        mCenterPointF = new PointF();
        //图例和节点之间的间隔
        legendPadding = DensityUtil.dip2px(mContext, 2);
        //图例文本的最大长度
        mLegendMaxTextLength = DensityUtil.dip2px(mContext, 15);
        //图例文字大小
        mLegendTextSize = DensityUtil.sp2px(mContext, 10);
        //绘制节点的画笔
        mNodePaint = new Paint();
        mNodePaint.setAntiAlias(true);
        mNodePaint.setStyle(Paint.Style.FILL);
        mNodePaint.setStrokeWidth(DensityUtil.dip2px(mContext, 1));
        //绘制图例文字的画笔
        mLegendTextPaint = new Paint();
        mLegendTextPaint.setAntiAlias(true);
        mLegendTextPaint.setTextSize(mLegendTextSize);
        mLegendTextPaint.setColor(Color.BLACK);
        //绘制连接线的画笔
        mLinkLinePaint = new Paint();
        mLinkLinePaint.setAntiAlias(true);
        mLinkLinePaint.setStyle(Paint.Style.STROKE);
        mLinkLinePaint.setStrokeWidth(DensityUtil.dip2px(mContext, 2));
        //图例和内容之间的间隔
        mLegendPaddingBottom = DensityUtil.dip2px(mContext, 15);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //View 的宽高
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        //计算节点图的中心点
        mCenterPointF.x = mWidth / 2;
        mCenterPointF.y = (mHeight + mLegendPaddingBottom) / 2;
        float contentRadius = Math.min(mCenterPointF.x, mHeight - mCenterPointF.y);
        //计算节点图的内容区域
        rectF = new RectF(mCenterPointF.x - contentRadius, mCenterPointF.y - contentRadius, mCenterPointF.x + contentRadius, mCenterPointF.y + contentRadius);
        initData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clipBounds = canvas.getClipBounds();
        drawLinkLine(canvas);
        drawNodes(canvas);
        drawLegendText(canvas);

    }

    /**
     * 绘制图例文本
     *
     * @param canvas 画布
     */
    private void drawLegendText(Canvas canvas) {
        float scale = getScale();
        mLegendTextPaint.setTextSize(Math.max(mLegendTextSize / scale, 1));
        List<Node> nodes = mNodeGraphModel.getNodes();
        float previousSweepAngle = 3;
        float previousCenterAngle = 0;
        float sweepAngleSum = 0;
        float rotate = 0;
        boolean isFirst = true;
        for (Node node : nodes) {
            float sweepAngle = node.getSweepAngle();
            sweepAngleSum += (sweepAngle + previousSweepAngle) / 2;
            Rect textRect = new Rect();
            mLegendTextPaint.getTextBounds(node.getName(), 0, node.getName().length(), textRect);
            if (sweepAngleSum <= 90 || sweepAngleSum >= 270) {//右半圆区域
                if (!isFirst) {
                    canvas.rotate(180 - rotate, mCenterPointF.x, mCenterPointF.y);
                    canvas.rotate(rotate + 3, mCenterPointF.x, mCenterPointF.y);
                    isFirst = true;
                }
                canvas.rotate((sweepAngle + previousSweepAngle) / 2, mCenterPointF.x, mCenterPointF.y);
                canvas.drawText(node.getName(), rectF.right - mLegendMaxTextLength - (mAllowMaxCircleRadius - node.getRadius()), mCenterPointF.y, mLegendTextPaint);

            } else if (isFirst) {//左半圆区域第一个
                canvas.rotate(-rotate, mCenterPointF.x, mCenterPointF.y);
                canvas.rotate(previousCenterAngle - 1.5f  - 180, mCenterPointF.x, mCenterPointF.y);
                canvas.rotate((sweepAngle + previousSweepAngle) / 2, mCenterPointF.x, mCenterPointF.y);

                canvas.drawText(node.getName(), rectF.left + mLegendMaxTextLength + (mAllowMaxCircleRadius - node.getRadius()) - textRect.right, mCenterPointF.y, mLegendTextPaint);
                isFirst = false;
            } else {//左半圆区域
                canvas.rotate((sweepAngle + previousSweepAngle) / 2, mCenterPointF.x, mCenterPointF.y);

                canvas.drawText(node.getName(), rectF.left + mLegendMaxTextLength + (mAllowMaxCircleRadius - node.getRadius()) - textRect.right, mCenterPointF.y, mLegendTextPaint);
            }
            rotate += (sweepAngle + previousSweepAngle) / 2;
            previousSweepAngle = sweepAngle;
            previousCenterAngle = node.getCenterAngle();
        }
    }

    /**
     * 绘制各个节点的圆
     *
     * @param canvas 画布
     */
    private void drawNodes(Canvas canvas) {
        List<Node> nodes = mNodeGraphModel.getNodes();
        for (Node node : nodes) {
            PointF centerPointF = node.getCenterPointF();
            float radius = node.getRadius();
            String color = getFloorColor(node.getCategory());
            mNodePaint.setColor(Color.parseColor(color));
            mNodePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerPointF.x, centerPointF.y, radius, mNodePaint);
        }
    }

    /**
     * 绘制连接线
     *
     * @param canvas 画布
     */
    private void drawLinkLine(Canvas canvas) {
        List<Label> labels = mNodeGraphModel.getLabels();
        for (Label label : labels) {
            Node sourceNode = getNodeFormId(label.getSourceShopId());
            Node targetNode = getNodeFormId(label.getTargetShopId());
            if (null == sourceNode || null == targetNode) {
                return;
            }
            Path linePath = label.getLinePath();
            String color = getFloorColor(sourceNode.getCategory());
            mLinkLinePaint.setColor(Color.parseColor(color));
            canvas.drawPath(linePath, mLinkLinePaint);
        }
    }

    /**
     * 设置节点图的数据
     *
     * @param nodeGraphModel 节点图的数据实体对象
     */
    public void setData(NodeLinkModel nodeGraphModel) {
        if (null == nodeGraphModel) {
            return;
        }
        mViewTyp = TYPE.DATA;
        this.mNodeGraphModel = nodeGraphModel;
        invalidate();

    }

    /**
     * 获取节点通过节点id
     *
     * @param source 节点id
     */
    private Node getNodeFormId(String source) {
        Node fromNode = null;
        List<Node> nodes = mNodeGraphModel.getNodes();
        for (Node node : nodes) {
            if (node.getId().equals(source)) {
                fromNode = node;
                break;
            }
        }
        return fromNode;
    }

    /**
     * 通过楼层名称获取颜色
     *
     * @param category 楼层名称
     */
    private String getFloorColor(String category) {
        String color = colors[0];
        List<Category> categories = mNodeGraphModel.getCategories();
        for (Category floor : categories) {
            if (floor.getName().equals(category)) {
                color = floor.getColor();
                break;
            }
        }
        return color;
    }

    /**
     * 根据测量控件的宽高来计算节点图绘制所需的数据
     */
    private void initData() {
        //最大内切圆的半径
        mRectMaxRadius = rectF.width() / 2 - mLegendMaxTextLength - legendPadding;
        //允许比重最大圆的半径 = 36度 内切圆的周长 /4
        mAllowMaxCircleRadius = (float) (mRectMaxRadius * (Math.sin(Math.PI / 10) / (1 + 2 * Math.sin(Math.PI / 10))));
        if (mViewTyp == TYPE.DATA) {
            try {
                NodeLinkUtil.calculateData(mNodeGraphModel, mRectMaxRadius, mAllowMaxCircleRadius, mCenterPointF);
            } catch (Exception e) {
                Log.e("NodeLinkView", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    float downX = 0;
    float downY = 0;
    PointF pointF = new PointF();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                pointF.x = downX;
                pointF.y = downY;
                onActionDown(pointF);
                break;
            case MotionEvent.ACTION_MOVE:
                float upX = event.getX();
                float upY = event.getY();
                if ((upX - downX) > 20 || (upY - downY) > 20) {
                    if (null == nodeLinkGroup) {
                        nodeLinkGroup = (NodeLinkGroup) getParent();
                    }
                    nodeLinkGroup.hideMarkerView();
                }
                break;

        }

        return true;
    }

    /**
     * 点击事件的处理
     *
     * @param pointF 点击的点
     */
    private void onActionDown(PointF pointF) {
        //将屏幕上点击的物理点坐标转换为画布缩放平移后显示对应的点
        PointF pointFToScaleCanvas = switchPointToScaleCanvas(pointF);
        if (null == nodeLinkGroup) {
            nodeLinkGroup = (NodeLinkGroup) getParent();
        }
        Node node = inNodeCircle(pointFToScaleCanvas);
        if (node != null) {
            nodeLinkGroup.showMarkView(pointF, mWidth, mHeight, node.getName());
            return;
        }
        Label label = inLinkLine(pointFToScaleCanvas);
        if (label != null) {
            Node sourceNode = getNodeFormId(label.getSourceShopId());
            Node targetNode = getNodeFormId(label.getTargetShopId());
            nodeLinkGroup.showMarkView(pointF, mWidth, mHeight, sourceNode.getName() + " > " + targetNode.getName() + " " + label.getCorrelationDegree());
            return;
        }
        nodeLinkGroup.hideMarkerView();

    }

    /**
     * 将物理点击的点坐标转换为画布缩放平移后的点
     *
     * @param pointF 点击的点
     * @return 装换后的点
     */
    private PointF switchPointToScaleCanvas(PointF pointF) {
        PointF switchPointF = new PointF();
        switchPointF.x = (clipBounds.width() * 1f / mWidth) * pointF.x + clipBounds.left;
        switchPointF.y = (clipBounds.height() * 1f / mHeight) * pointF.y + clipBounds.top;
        return switchPointF;
    }

    /**
     * 判断点击点是否在连接线上
     *
     * @param pointF 点击点
     */
    private Label inLinkLine(PointF pointF) {
        List<Label> labels = mNodeGraphModel.getLabels();
        for (Label label : labels) {
            if (pointInPath(label.getLinkPath(), pointF)) {
                return label;
            }
        }
        return null;
    }

    /**
     * 判断点击的是否在节点圆上
     *
     * @param pointF 点击点
     * @return true 在圆上 false 不在圆上
     */
    private Node inNodeCircle(PointF pointF) {
        List<Node> nodes = mNodeGraphModel.getNodes();
        for (Node node : nodes) {
            if (pointInPath(node.getPath(), pointF)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 判断点击点是否在某个路径内
     *
     * @param path   路径
     * @param pointF 所要判断的点
     * @return true 在路径内 false 不在路径内
     */
    private boolean pointInPath(Path path, PointF pointF) {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        return region.contains((int) pointF.x, (int) pointF.y);
    }

}
