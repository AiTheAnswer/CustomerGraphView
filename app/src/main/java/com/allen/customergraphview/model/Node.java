package com.allen.customergraphview.model;

import android.graphics.Path;

public class Node {
    /**
     * id : Hn_P00012S00448
     * name : 真熙家
     * symbolSize : 1
     * source : null
     * category : 4F
     * target : null
     * value : null
     * lineStyle : null
     */

    private String id;
    private String name;
    private int symbolSize;
    private Object source;
    private String category;
    private Object target;
    private Object value;
    //新增绘制图形所需的属性
    //圆的半径
    private float radius;
    //圆的中心点
    private PointF centerPointF;
    //圆的路径
    private Path path;
    //圆的起始角度
    private float mStartAngle;
    //圆的旋转角度的角度
    private float mSweepAngle;
    //圆的终止角度
    private float mEndAngle;
    //圆的中心点的角度
    private float mCenterAngle;

    public Node(String id, String name, int symbolSize, Object source, String category, Object target, Object value) {
        this.id = id;
        this.name = name;
        this.symbolSize = symbolSize;
        this.source = source;
        this.category = category;
        this.target = target;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSymbolSize() {
        return symbolSize;
    }

    public void setSymbolSize(int symbolSize) {
        this.symbolSize = symbolSize;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public PointF getCenterPointF() {
        return centerPointF;
    }

    public void setCenterPointF(PointF mCenterPointF) {
        this.centerPointF = mCenterPointF;
    }

    public Path getPath() {
        path = new Path();
        path.addCircle(getCenterPointF().x, getCenterPointF().y, getRadius(), Path.Direction.CCW);
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public float getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.mSweepAngle = sweepAngle;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(float startAngle) {
        this.mStartAngle = startAngle;
    }


    public float getEndAngle() {
        return mEndAngle;
    }

    public void setEndAngle(float endAngle) {
        this.mEndAngle = endAngle;
    }

    public float getCenterAngle() {
        return mCenterAngle;
    }

    public void setCenterAngle(float centerAngle) {
        this.mCenterAngle = centerAngle;
    }
}
