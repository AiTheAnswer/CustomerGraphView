package com.allen.customergraphview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.allen.customergraphview.model.GraphModel;

/**
 * 节点图布局
 *
 * @author Renjy
 */
public class NodeGroup extends LinearLayout {
    private Context mContext;
    private LegendView legendView;
    private GraphView graphView;

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

    private void init() {
        removeAllViews();
        legendView = new LegendView(mContext);
        graphView = new GraphView(mContext);
        addView(legendView);
        addView(graphView);
    }

    public void setData(GraphModel graphModel) {
        legendView.setData(graphModel);
        graphView.setData(graphModel);
        invalidate();
    }
}
