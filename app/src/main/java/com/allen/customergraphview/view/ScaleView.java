package com.allen.customergraphview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 可以缩放的View
 *
 * @author Renjy
 */
public class ScaleView extends View implements ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private Context mContext;
    private Matrix mScaleMatrix;
    private int mTouchSlop;
    private int mWidth;
    private int mHeight;
    // *********双击放大与缩小*********
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;
    /**
     * 放大的最大值
     */
    private float mMaxScale;
    /**
     * 捕获用户多指触控时缩放的比例
     */
    private ScaleGestureDetector mScaleGestureDetector;
    // **********自由移动的变量***********
    /**
     * 记录上一次多点触控的数量
     */
    private int mLastPointerCount;
    private boolean isCanDrag;
    private float mLastX;
    private float mLastY;
    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    public ScaleView(Context context) {
        this(context, null);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();

        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {//双击时回调
                        if (isAutoScale) {
                            return true;
                        }
                        float x = e.getX();
                        float y = e.getY();
                        float scale = getScale();
                        if (scale < mMaxScale) {
                            postDelayed(new AutoScaleRunnable(scale + 0.2f, x, y), 0);
                        }
                        isAutoScale = true;
                        return true;
                    }
                });


    }

    private void init() {
        mMaxScale = 5;
        mScaleMatrix = new Matrix();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.concat(mScaleMatrix);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0;
        float y = 0;
        // 拿到多点触控的数量
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;
        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    isCheckLeftAndRight = isCheckTopAndBottom = true;
                    // 如果宽度小于控件宽度，不允许横向移动
                    if (rectF.width() < getWidth()) {
                        isCheckLeftAndRight = false;
                        dx = 0;
                    }
                    // 如果高度小于控件高度，不允许纵向移动
                    if (rectF.height() < getHeight()) {
                        isCheckTopAndBottom = false;
                        dy = 0;
                    }
                    mScaleMatrix.postTranslate(dx, dy);
                    checkBorderWhenTranslate();
                    invalidate();
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
            default:
                break;
        }

        return false;
    }

    /**
     * 自动放大与缩小
     *
     * @author zhangyan@lzt.com.cn
     */
    private class AutoScaleRunnable implements Runnable {
        /**
         * 缩放的目标值
         */
        private float mTargetScale;
        // 缩放的中心点
        private float x;
        private float y;


        /**
         * @param mTargetScale
         * @param x
         * @param y
         */
        public AutoScaleRunnable(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {
            //进行缩放
            mScaleMatrix.postScale(mTargetScale / getScale(), mTargetScale / getScale(), x, y);
            checkBorderAndCenterWhenScale();
            postInvalidate();
            isAutoScale = false;

        }
    }

    /**
     * 当移动时进行边界检查
     */
    private void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }
        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }
        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }
        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 判断是否是move
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        // 缩放范围的控制
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > 1 && scaleFactor < 1.0f)) {
            if (scale * scaleFactor < 1) {
                scaleFactor = 1 / scale;
            }
            // 缩放
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            invalidate();
        }
        return true;
    }

    /**
     * 获得图片放大缩小以后的宽和高，以及left，right，top，bottom
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();
        rectF.set(0, 0, mWidth, mHeight);
        matrix.mapRect(rectF);
        return rectF;
    }

    /**
     * 在缩放的时候进行边界以及我们的位置的控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 缩放时进行边界检测，防止出现白边
        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }
            if (rectF.right < width) {
                deltaX = width - rectF.right;
            }
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }

        /**
         * 如果宽度或高度小于空间的宽或者高，则让其居中
         */
        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2f;
        }

        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2f;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * 获取当前画布的缩放值
     *
     * @return 缩放值
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }
}
