package com.allen.customergraphview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.allen.customergraphview.exception.GraphException;
import com.allen.customergraphview.model.Floor;
import com.allen.customergraphview.model.NodeGraphModel;
import com.allen.customergraphview.model.LineFloor;
import com.allen.customergraphview.model.Link;
import com.allen.customergraphview.model.Node;
import com.allen.customergraphview.utils.CalculateUtil;
import com.allen.customergraphview.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点流程图的自定义View
 *
 * @author Renjy
 */
public class NodeView extends View {
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
    private Point mCenterPoint;
    //节点流程图内容的范围
    private Rect rect;
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
    private NodeGraphModel mNodeGraphModel;
    //绘制节点的画笔
    private Paint mNodePaint;
    //绘制连接线的画笔
    private Paint mLinkLinePaint;
    //绘制图例文字的画笔
    private Paint mLegendTextPaint;
    //View当前的状态
    private TYPE mViewTyp = TYPE.INIT;
    //图例的单行的高度
    private float mSingleLegendHeight;
    //图例总高度
    private float mLegendHeight;
    //绘制图例的画笔
    private Paint mLegendPaint;
    //所有行图例的集合
    private List<LineFloor> mLineFloors;
    //图例和内容区域之间的间隔
    private int mLegendPaddingBottom;
    //图例之间的间隔
    private int mLegendPadding;
    //节点流程图的父容器
    private NodeGroup nodeGroup;

    /**
     * View 当前的状态
     */
    enum TYPE {
        INIT,//初始化状态（用户为设置数据）
        DATA,//用户设置了数据，且数据不为空
        NO_DATA//用户设置了，但是数据为空
    }

    public NodeView(Context context) {
        this(context, null);
    }

    public NodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        //单行图例的高度
        mSingleLegendHeight = DensityUtil.dip2px(mContext, 30);
        //绘制图例的画笔
        mLegendPaint = new Paint();
        mLegendPaint.setAntiAlias(true);
        mLegendPaint.setTextSize(DensityUtil.sp2px(mContext, 16));
        mLegendPaint.setColor(Color.BLACK);
        //图例和内容之间的间隔
        mLegendPaddingBottom = DensityUtil.dip2px(mContext, 15);
        //图例之间的间隔
        mLegendPadding = DensityUtil.dip2px(mContext, 40);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //View 的宽高
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        //计算绘制图例所需的数据
        calculateLegend();
        //计算节点图的中心点
        mCenterPoint.x = mWidth / 2;
        mCenterPoint.y = (int) ((mHeight + mLegendHeight + mLegendPaddingBottom) / 2);
        int contentRadius = Math.min(mCenterPoint.x, mHeight - mCenterPoint.y);
        //计算节点图的内容区域
        rect = new Rect(mCenterPoint.x - contentRadius, mCenterPoint.y - contentRadius, mCenterPoint.x + contentRadius, mCenterPoint.y + contentRadius);
        initData();
    }

    /**
     * 计算绘制图例所需的数据
     */
    private void calculateLegend() {
        float dp15 = DensityUtil.dip2px(mContext, 15);
        if (null != mNodeGraphModel) {
            mLineFloors = new ArrayList<>();
            LineFloor wrapLine = new LineFloor();
            List<Floor> floors = mNodeGraphModel.getFloors();
            float mLegendSumWidth = 0;
            int line = 0;
            for (Floor floor : floors) {
                Rect rect = new Rect();
                mLegendPaint.getTextBounds(floor.getName(), 0, floor.getName().length(), rect);
                float width = rect.width() + mLegendPadding;
                float height = rect.height();
                if (wrapLine.getRowContentWidth() + width <= mWidth - dp15 * 2) {
                    floor.setLine(line);
                    floor.setStartX(mLegendSumWidth);
                    floor.setStartY(mSingleLegendHeight * line + height + (mSingleLegendHeight - height) / 2);
                    floor.setWidth(width);
                    wrapLine.addFloor(floor);
                } else {
                    mLineFloors.add(wrapLine);
                    line++;
                    mLegendSumWidth = 0;
                    floor.setLine(line);
                    floor.setStartX(mLegendSumWidth);
                    floor.setStartY(mSingleLegendHeight * line + height + (mSingleLegendHeight - height) / 2);
                    floor.setWidth(width);
                    wrapLine = new LineFloor();
                    wrapLine.addFloor(floor);
                }
                floor.setLineLegendHeight(mSingleLegendHeight);
                mLegendSumWidth += width;
            }
            mLineFloors.add(wrapLine);
            mLegendHeight = mSingleLegendHeight * (line + 1);
            for (LineFloor lineFloor : mLineFloors) {
                lineFloor.setPaddingLeftOrRight((mWidth - lineFloor.getRowContentWidth()) / 2);
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLegend(canvas);
        drawLinkLine(canvas);
        drawNodes(canvas);
        drawLegendText(canvas);

    }

    /**
     * 绘制图例 （上面居中）
     *
     * @param canvas 画布
     */
    private void drawLegend(Canvas canvas) {
        //绘制内容区域
        //canvas.drawRect(rect, mLegendPaint);
        for (LineFloor lineFloor : mLineFloors) {
            for (Floor floor : lineFloor.getFloors()) {
                String name = floor.getName();
                float padding = lineFloor.getPaddingLeftOrRight();
                float y = mCenterPoint.y - mRectMaxRadius - mLegendMaxTextLength - legendPadding - mLegendHeight - mLegendPaddingBottom + floor.getStartY();
                int rectColorWith = DensityUtil.dip2px(mContext, 10);
                RectF rectColor = new RectF(padding + rectColorWith + floor.getStartX(), y - rectColorWith, padding + rectColorWith + floor.getStartX() + rectColorWith, y);
                mLegendPaint.setColor(Color.parseColor(floor.getColor()));
                canvas.drawRect(rectColor, mLegendPaint);
                mLegendPaint.setColor(Color.BLACK);
                canvas.drawText(name, padding + rectColorWith * 3 + floor.getStartX(), y, mLegendPaint);
            }

        }

    }

    /**
     * 绘制图例文本
     *
     * @param canvas 画布
     */
    private void drawLegendText(Canvas canvas) {
        List<Node> nodes = mNodeGraphModel.getNodes();
        float previousSweepAngle = 3f;
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
                    canvas.rotate(180 - rotate, mCenterPoint.x, mCenterPoint.y);
                    canvas.rotate(rotate + 3, mCenterPoint.x, mCenterPoint.y);
                    isFirst = true;
                }
                canvas.rotate((sweepAngle + previousSweepAngle) / 2, mCenterPoint.x, mCenterPoint.y);
                canvas.drawText(node.getName(), rect.right - mLegendMaxTextLength - (mAllowMaxCircleRadius - node.getRadius()), mCenterPoint.y, mLegendTextPaint);

            } else if (isFirst) {//左半圆区域第一个
                canvas.rotate(-rotate, mCenterPoint.x, mCenterPoint.y);
                canvas.rotate(-93, mCenterPoint.x, mCenterPoint.y);
                canvas.rotate((sweepAngle + previousSweepAngle) / 2, mCenterPoint.x, mCenterPoint.y);

                canvas.drawText(node.getName(), rect.left + mLegendMaxTextLength + (mAllowMaxCircleRadius - node.getRadius()) - textRect.right, mCenterPoint.y, mLegendTextPaint);
                isFirst = false;
            } else {//左半圆区域
                canvas.rotate((sweepAngle + previousSweepAngle) / 2, mCenterPoint.x, mCenterPoint.y);

                canvas.drawText(node.getName(), rect.left + mLegendMaxTextLength + (mAllowMaxCircleRadius - node.getRadius()) - textRect.right, mCenterPoint.y, mLegendTextPaint);
            }
            rotate += (sweepAngle + previousSweepAngle) / 2;
            previousSweepAngle = sweepAngle;
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
        List<Link> links = mNodeGraphModel.getLinks();
        for (Link link : links) {
            Node sourceNode = getNodeFormId(link.getSource());
            Node targetNode = getNodeFormId(link.getTarget());
            if (null == sourceNode || null == targetNode) {
                return;
            }
            Path linePath = link.getLinePath();
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
    public void setData(NodeGraphModel nodeGraphModel) {
        if (null == nodeGraphModel) {
            return;
            //TODO 暂无数据
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
        List<Floor> floors = mNodeGraphModel.getFloors();
        for (Floor floor : floors) {
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
        mRectMaxRadius = rect.width() / 2 - mLegendMaxTextLength - legendPadding;
        //允许比重最大圆的半径 = 36度 内切圆的周长 /4
        mAllowMaxCircleRadius = (float) (mRectMaxRadius * (Math.sin(Math.PI / 10) / (1 + 2 * Math.sin(Math.PI / 10))));
        if (mViewTyp == TYPE.DATA) {
            try {
                CalculateUtil.calculateData(mNodeGraphModel, mRectMaxRadius, mAllowMaxCircleRadius, mCenterPoint);
            } catch (GraphException e) {
                e.printStackTrace();
                if (TextUtils.isEmpty(e.getMessage())) {
                    Log.e("GraphView", e.getMessage());
                }
            }
        }
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
     * 点击事件的处理
     *
     * @param point 点击的点
     */
    private void onActionDown(Point point) {
        if (null == nodeGroup) {
            nodeGroup = (NodeGroup) getParent();
        }
        Node node = inNodeCircle(point);
        if (node != null) {
            nodeGroup.showMarkView(point, mWidth, mHeight, node.getName());
            return;
        }
        Link link = inLinkLine(point);
        if (link != null) {
            Node sourceNode = getNodeFormId(link.getSource());
            Node targetNode = getNodeFormId(link.getTarget());
            nodeGroup.showMarkView(point, mWidth, mHeight, sourceNode.getName() + " > " + targetNode.getName() + " " + link.getValue());
            return;
        }

    }


    /**
     * 判断点击点是否在连接线上
     *
     * @param point 点击点
     */
    private Link inLinkLine(Point point) {
        List<Link> links = mNodeGraphModel.getLinks();
        for (Link link : links) {
            if (pointInPath(link.getLinkPath(), point)) {
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
        List<Node> nodes = mNodeGraphModel.getNodes();
        for (Node node : nodes) {
            if (pointInPath(node.getPath(), point)) {
                return node;
            }
        }
        return null;
    }

    /**
     * 判断点击点是否在某个路径内
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

}