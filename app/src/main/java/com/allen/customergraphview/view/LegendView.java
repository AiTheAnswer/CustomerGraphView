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

import com.allen.customergraphview.model.Category;
import com.allen.customergraphview.model.LineLegend;
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
    private List<LineLegend> mLineLegends;
    //绘制图例的画笔
    private Paint mLegendPaint;
    //图例之间的间隔
    private int mLegendPadding;
    //图例的单行的高度
    private float mSingleLegendHeight;
    //图例总高度
    private float mLegendHeight;
    private List<Category> mCategories;

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

    public void setData(List<Category> data) {
        mCategories = data;
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
        for (LineLegend lineLegend : mLineLegends) {
            for (Category category : lineLegend.getCategories()) {
                String name = category.getName();
                float padding = lineLegend.getPaddingLeftOrRight();
                float y = category.getStartY();
                int rectColorWith = DensityUtil.dip2px(mContext, 10);
                RectF rectColor = new RectF(padding + rectColorWith + category.getStartX(), y - rectColorWith, padding + rectColorWith + category.getStartX() + rectColorWith, y);
                mLegendPaint.setColor(Color.parseColor(category.getColor()));
                canvas.drawRect(rectColor, mLegendPaint);
                mLegendPaint.setColor(Color.BLACK);
                canvas.drawText(name, padding + rectColorWith * 3 + category.getStartX(), y, mLegendPaint);
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
        if (null != mCategories) {
            mLineLegends = new ArrayList<>();
            LineLegend wrapLine = new LineLegend();

            float mLegendSumWidth = 0;
            int line = 0;
            for (Category category : mCategories) {
                Rect rect = new Rect();
                mLegendPaint.getTextBounds(category.getName(), 0, category.getName().length(), rect);
                float width = rect.width() + mLegendPadding;
                float height = rect.height();
                if (wrapLine.getRowContentWidth() + width <= mWidth - dp15 * 2) {
                    category.setLine(line);
                    category.setStartX(mLegendSumWidth);
                    category.setStartY(mSingleLegendHeight * line + height + (mSingleLegendHeight - height) / 2);
                    category.setWidth(width);
                    wrapLine.addFloor(category);
                } else {
                    mLineLegends.add(wrapLine);
                    line++;
                    mLegendSumWidth = 0;
                    category.setLine(line);
                    category.setStartX(mLegendSumWidth);
                    category.setStartY(mSingleLegendHeight * line + height + (mSingleLegendHeight - height) / 2);
                    category.setWidth(width);
                    wrapLine = new LineLegend();
                    wrapLine.addFloor(category);
                }
                category.setLineLegendHeight(mSingleLegendHeight);
                mLegendSumWidth += width;
            }
            mLineLegends.add(wrapLine);
            mLegendHeight = mSingleLegendHeight * (line + 1);
            for (LineLegend lineLegend : mLineLegends) {
                lineLegend.setPaddingLeftOrRight((mWidth - lineLegend.getRowContentWidth()) / 2);
            }
        }

    }

    public float getLegendHeight() {
        return mLegendHeight;
    }

}
