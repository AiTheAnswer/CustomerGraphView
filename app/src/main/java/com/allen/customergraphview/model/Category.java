package com.allen.customergraphview.model;

/**
 * 楼层实体类(对应图形数据类目)
 *
 * @author Renjy
 */
public class Category {
    /**
     * name : 4F
     */

    private String name;
    private String color;
    //当前图例在第几行
    private int line;
    //图例的开始的x轴位置
    private float startX;
    //图例开始的y轴位置
    private float startY;
    //图例每一行的高度
    private float mLineLegendHeight;
    //图例的宽度
    private float width;
    //图例的高度
    private float height;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getLineLegendHeight() {
        return mLineLegendHeight;
    }

    public void setLineLegendHeight(float lineLegendHeight) {
        this.mLineLegendHeight = lineLegendHeight;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
