package com.allen.customergraphview.model;

import android.graphics.Path;
import android.graphics.Point;

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
    private LineStyle lineStyle;
    //新增绘制图形所需的属性
    //圆的半径
    private float radius;
    //圆的中心点
    private Point centerPoint;
    //圆的路径
    private Path path;
    //圆的中心点的角度
    private float mSweepAngle;

    public Node(String id, String name, int symbolSize, Object source, String category, Object target, Object value, LineStyle lineStyle) {
        this.id = id;
        this.name = name;
        this.symbolSize = symbolSize;
        this.source = source;
        this.category = category;
        this.target = target;
        this.value = value;
        this.lineStyle = lineStyle;
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

    public Object getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(Point mCenterPoint) {
        this.centerPoint = mCenterPoint;
    }

    public Path getPath() {
        path = new Path();
        path.addCircle(getCenterPoint().x, getCenterPoint().y, getRadius(), Path.Direction.CCW);
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
}
