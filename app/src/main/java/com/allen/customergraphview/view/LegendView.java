package com.allen.customergraphview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.allen.customergraphview.model.Floor;
import com.allen.customergraphview.model.LineFloor;
import com.allen.customergraphview.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 图例View
 */
public class LegendView extends View {
    //View的宽度
    private int mWidth;
    //View的高度
    private int mHeight;
    private Context mContext;
    //所有行图例的集合
    private List<LineFloor> mLineFloors;
    //绘制图例的画笔
    private Paint mLegendPaint;
    //图例之间的间隔
    private int mLegendPadding;
    //图例的单行的高度
    private float mSingleLegendHeight;
    //图例总高度
    private float mLegendHeight;
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
        //单行图例的高度
        mSingleLegendHeight = DensityUtil.dip2px(mContext, 30);
        //绘制图例的画笔
        mLegendPaint = new Paint();
        mLegendPaint.setAntiAlias(true);
        mLegendPaint.setTextSize(DensityUtil.sp2px(mContext, 16));
        mLegendPaint.setColor(Color.BLACK);
        //图例之间的间隔
        mLegendPadding = DensityUtil.dip2px(mContext, 40);
    }

    public void setData(List<Floor> data) {
        mFloors = data;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLegend(canvas);
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
                float y = floor.getStartY();
                int rectColorWith = DensityUtil.dip2px(mContext, 10);
                RectF rectColor = new RectF(padding + rectColorWith + floor.getStartX(), y - rectColorWith, padding + rectColorWith + floor.getStartX() + rectColorWith, y);
                mLegendPaint.setColor(Color.parseColor(floor.getColor()));
                canvas.drawRect(rectColor, mLegendPaint);
                mLegendPaint.setColor(Color.BLACK);
                canvas.drawText(name, padding + rectColorWith * 3 + floor.getStartX(), y, mLegendPaint);
            }

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //View 的宽高
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        //计算绘制图例所需的数据
        calculateLegend();
        setMeasuredDimension(mWidth, (int) mLegendHeight);
    }

    /**
     * 计算绘制图例所需的数据
     */
    private void calculateLegend() {
        float dp15 = DensityUtil.dip2px(mContext, 15);
        if (null != mFloors) {
            mLineFloors = new ArrayList<>();
            LineFloor wrapLine = new LineFloor();

            float mLegendSumWidth = 0;
            int line = 0;
            for (Floor floor : mFloors) {
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

    public float getLegendHeight() {
        return mLegendHeight;
    }

}
