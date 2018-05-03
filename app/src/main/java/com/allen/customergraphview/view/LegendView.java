package com.allen.customergraphview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.allen.customergraphview.model.Floor;
import com.allen.customergraphview.model.GraphModel;
import com.allen.customergraphview.utils.DensityUtil;

import java.util.List;

/**
 * 图例View
 */
public class LegendView extends View {
    private Context mContext;
    private int mHeight;
    private int mWidth;
    private Paint mLegendPaint;
    private List<Floor> mFloors;

    public LegendView(Context context) {
        this(context, null);
    }

    public LegendView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LegendView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        mLegendPaint = new Paint();
        mLegendPaint.setAntiAlias(true);
        mLegendPaint.setTextSize(DensityUtil.sp2px(mContext, 16));
    }

    public void setData(GraphModel graphModel) {
        mFloors = graphModel.getFloors();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFloors == null) {
            return;
        }
        float startX = 0;
        float startY = DensityUtil.dip2px(mContext, 5);
        for (Floor floor : mFloors) {
            Rect bounds = new Rect();
            mLegendPaint.getTextBounds(floor.getName(), 0, floor.getName().length(), bounds);
            canvas.drawText(floor.getName(), startX, bounds.height() + startY, mLegendPaint);
            startX += bounds.width();
            startX += DensityUtil.dip2px(mContext, 20);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
