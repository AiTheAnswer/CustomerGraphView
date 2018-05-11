package com.allen.customergraphview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.allen.customergraphview.model.NodeLinkModel;
import com.allen.customergraphview.model.PointF;

/**
 * 节点流程图布局（节点图+ MarkerView）
 *
 * @author Renjy
 */
public class NodeLinkGroup extends FrameLayout {
    private Context mContext;
    private NodeLinkView nodeLinkView;
    private MarkerView markerView;
    //图例的高度
    private int legendHeight;

    public NodeLinkGroup(Context context) {
        this(context, null);
    }

    public NodeLinkGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NodeLinkGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        nodeLinkView = new NodeLinkView(mContext);
        markerView = new MarkerView(mContext);
        markerView.setVisibility(View.GONE);
        addView(nodeLinkView);
        addView(markerView);
    }


    /**
     * 设置节点图的数据
     *
     * @param nodeGraphModel 节点流程图的数据
     */
    public void setData(NodeLinkModel nodeGraphModel) {
        nodeLinkView.setData(nodeGraphModel);
        invalidate();
    }

    /**
     * 显示MarkerView
     *
     * @param pointF      点击的屏幕的位置点
     * @param width      节点图的宽度
     * @param height     节点图的高度
     * @param markerText markerView 要显示的文本
     */
    public void showMarkView(PointF pointF, int width, int height, String markerText) {
        markerView.setVisibility(View.GONE);
        markerView.refreshContent(pointF, width, height, markerText);
    }

    /**
     * 隐藏MarkerView
     */
    public void hideMarkerView() {
        markerView.hide();
    }
}
