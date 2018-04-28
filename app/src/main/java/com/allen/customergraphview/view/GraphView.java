package com.allen.customergraphview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.allen.customergraphview.exception.GraphException;
import com.allen.customergraphview.model.Floor;
import com.allen.customergraphview.model.GraphModel;
import com.allen.customergraphview.model.Link;
import com.allen.customergraphview.model.Node;
import com.allen.customergraphview.utils.CalculateUtil;
import com.allen.customergraphview.utils.DensityUtil;

import java.util.List;

/**
 * 节点流程图
 *
 * @author Renjy
 */
public class GraphView extends View {
    private Context mContext;
    private String[] colors = {"#FBB367", "#80B1D2", "#FB8070", "#CC99FF", "#B0D961",
            "#99CCCC", "#BEBBD8", "#FFCC99", "#8DD3C8", "#FF9999",
            "#CCEAC4", "#BB81BC", "#FBCCEC", "#CCFF66", "#99CC66",
            "#66CC66", "#FF6666", "#FFED6F", "#ff7f50", "#87cefa"};
    private int mWidth;
    private int mHeight;
    //圆环的圆心坐标
    private Point mCenterPoint;
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
    private GraphModel mGraphModel;
    //绘制节点的画笔
    private Paint mNodePaint;
    //绘制图例文字的画笔
    private Paint mLegendPaint;
    //View当前的状态
    private TYPE mViewTyp = TYPE.INIT;

    /**
     * View 当前的状态
     */
    enum TYPE {
        INIT,//初始化状态（用户为设置数据）
        DATA,//用户设置了数据，且数据不为空
        NO_DATA//用户设置了，但是数据为空
    }

    public GraphView(Context context) {
        this(context, null);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    /**
     * 初始化绘制需要的对象
     */
    private void init() {
        //View中心点
        mCenterPoint = new Point();
        //图例和节点之间的间隔
        legendPadding = DensityUtil.dip2px(mContext, 5);
        //图例文本的最大长度
        mLegendMaxTextLength = DensityUtil.dip2px(mContext, 30);
        //图例文字大小
        mLegendTextSize = DensityUtil.sp2px(mContext, 13);
        //绘制节点的画笔
        mNodePaint = new Paint();
        mNodePaint.setAntiAlias(true);
        mNodePaint.setStyle(Paint.Style.FILL);
        mNodePaint.setStrokeWidth(DensityUtil.dip2px(mContext, 1));
        //绘制图例文字的画笔
        mLegendPaint = new Paint();
        mLegendPaint.setAntiAlias(true);
        mLegendPaint.setTextSize(mLegendTextSize);
        mLegendPaint.setColor(Color.BLACK);

    }

    /**
     * 设置节点图的数据
     *
     * @param graphModel 节点图的数据实体对象
     */
    public void setData(GraphModel graphModel) {
        if (null == graphModel) {
            //TODO 暂无数据
        }
        mViewTyp = TYPE.DATA;
        this.mGraphModel = graphModel;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
        List<Node> nodes = mGraphModel.getNodes();
        float previousSweepAngle = 3f;
        for (Node node : nodes) {
            float sweepAngle = node.getSweepAngle();
            canvas.rotate((sweepAngle + previousSweepAngle) / 2, mCenterPoint.x, mCenterPoint.y);
            if ((sweepAngle + previousSweepAngle) / 2 > 5) {
                canvas.drawText(node.getName(), mWidth - mLegendMaxTextLength - (mAllowMaxCircleRadius - node.getRadius()), mHeight / 2, mLegendPaint);
            }
            previousSweepAngle = sweepAngle;
        }
    }

    /**
     * 绘制各个节点的圆
     *
     * @param canvas 画布
     */
    private void drawNodes(Canvas canvas) {
        List<Node> nodes = mGraphModel.getNodes();
        for (Node node : nodes) {
            Point centerPoint = node.getCenterPoint();
            float radius = node.getRadius();
            String color = getFloorColor(node.getCategory());
            mNodePaint.setColor(Color.parseColor(color));
            mNodePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerPoint.x, centerPoint.y, radius, mNodePaint);
        }
    }

    /**
     * 绘制连接线
     *
     * @param canvas 画布
     */
    private void drawLinkLine(Canvas canvas) {
        List<Link> links = mGraphModel.getLinks();
        for (Link link : links) {
            Node sourceNode = getNodeFormId(link.getSource());
            Node targetNode = getNodeFormId(link.getTarget());
            if (null == sourceNode || null == targetNode) {
                return;
            }
            Path path = new Path();
            Point sourcePoint = sourceNode.getCenterPoint();
            Point targetPoint = targetNode.getCenterPoint();
            path.moveTo(sourcePoint.x, sourcePoint.y);
            path.quadTo(mCenterPoint.x, mCenterPoint.y, targetPoint.x, targetPoint.y);
            link.setPath(path);
            String color = getFloorColor(sourceNode.getCategory());
            mNodePaint.setStyle(Paint.Style.STROKE);
            mNodePaint.setStrokeWidth(1);
            mNodePaint.setColor(Color.parseColor(color));
            canvas.drawPath(path, mNodePaint);
        }
    }

    /**
     * 获取节点通过节点id
     *
     * @param source 节点id
     */
    private Node getNodeFormId(String source) {
        Node fromNode = null;
        List<Node> nodes = mGraphModel.getNodes();
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
        List<Floor> floors = mGraphModel.getFloors();
        for (Floor floor : floors) {
            if (floor.getName().equals(category)) {
                color = floor.getColor();
                break;
            }
        }
        return color;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mCenterPoint.x = mWidth / 2;
        mCenterPoint.y = mHeight / 2;
        initData();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Point point = new Point();
                point.x = (int) event.getX();
                point.y = (int) event.getY();
                onActionDown(point);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 点击的时候
     *
     * @param point 点击的点
     */
    private void onActionDown(Point point) {
        Node node = inNodeCircle(point);
        if (node != null) {
            Toast.makeText(mContext, node.getName(), Toast.LENGTH_SHORT).show();
            return;
        }
        Link link = inLinkLine(point);
        if (link != null) {
            Node sourceNode = getNodeFormId(link.getSource());
            Node targetNode = getNodeFormId(link.getTarget());
            Toast.makeText(mContext, sourceNode.getName() + " > " + link.getValue() + " " + targetNode.getName(), Toast.LENGTH_SHORT).show();
            return;
        }

    }

    /**
     * 判断点击点是否在连接线上
     *
     * @param point 点击点
     */
    private Link inLinkLine(Point point) {
        List<Link> links = mGraphModel.getLinks();
        for (Link link : links) {
            if (pointInPath(link.getPath(), point)) {
                return link;
            }
        }
        return null;
    }

    /**
     * 判断点击的是否在节点圆上
     *
     * @param point 点击点
     * @return true 在圆上 false 不在圆上
     */
    private Node inNodeCircle(Point point) {
        List<Node> nodes = mGraphModel.getNodes();
        for (Node node : nodes) {
            if (pointInPath(node.getPath(), point)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 判断某个点是否在某个路径内
     *
     * @param path  路径
     * @param point 所要判断的点
     * @return true 在路径内 false 不在路径内
     */
    private boolean pointInPath(Path path, Point point) {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        Region region = new Region();
        region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        return region.contains(point.x, point.y);
    }

    /**
     * 根据测量控件的宽高来计算绘制内容图形的尺寸
     */
    private void initData() {
        //最大内切圆的半径
        mRectMaxRadius = Math.min(mCenterPoint.x, mCenterPoint.y) - mLegendMaxTextLength - legendPadding;
        //允许比重最大圆的半径 = 36度 内切圆的周长 /4
        mAllowMaxCircleRadius = (float) (mRectMaxRadius * (Math.sin(Math.PI / 10) / (1 + 2 * Math.sin(Math.PI / 10))));
        if (mViewTyp == TYPE.DATA) {
            try {
                CalculateUtil.calculateData(mGraphModel, mRectMaxRadius, mAllowMaxCircleRadius, mCenterPoint);
            } catch (GraphException e) {
                e.printStackTrace();
                if (TextUtils.isEmpty(e.getMessage())) {
                    Log.e("GraphView", e.getMessage());
                }
            }
        }
    }


}
