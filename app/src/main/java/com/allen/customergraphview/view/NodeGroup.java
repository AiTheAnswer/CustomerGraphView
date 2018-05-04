package com.allen.customergraphview.view;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.allen.customergraphview.model.NodeGraphModel;

/**
 * 节点流程图布局（节点图+ MarkerView）
 *
 * @author Renjy
 */
public class NodeGroup extends FrameLayout {
    private Context mContext;
    private NodeView nodeView;
    private MarkerView markerView;

    public NodeGroup(Context context) {
        this(context, null);
    }

    public NodeGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    /**
     * 初始化
     * 添加节点图和MarkerView
     */
    private void init() {
        removeAllViews();
        nodeView = new NodeView(mContext);
        markerView = new MarkerView(mContext);
        markerView.setVisibility(View.GONE);
        addView(nodeView);
        addView(markerView);
    }

    /**
     * 设置节点图的数据
     *
     * @param nodeGraphModel 节点流程图的数据
     */
    public void setData(NodeGraphModel nodeGraphModel) {
        nodeView.setData(nodeGraphModel);
        invalidate();
    }

    /**
     * 显示MarkerView
     *
     * @param point      点击的屏幕的位置点
     * @param width      节点图的宽度
     * @param height     节点图的高度
     * @param markerText markerView 要显示的文本
     */
    public void showMarkView(Point point, int width, int height, String markerText) {
        markerView.setVisibility(View.GONE);
        markerView.refreshContent(point, width, height, markerText);
    }
}
