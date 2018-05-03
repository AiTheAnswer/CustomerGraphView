package com.allen.customergraphview.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 同一行的图例集合
 */
public class LineFloor {
    //一行图例的集合
    private List<Floor> floors = new ArrayList<>();
    //行内容的宽度
    private int rowContentWidth;
    //左右两边的间隔
    private float mPaddingLeftOrRight;



    /**
     * 添加图例
     *
     * @param floor 图例
     */
    public void addFloor(Floor floor) {
        rowContentWidth += floor.getWidth();
        floors.add(floor);
    }

    /**
     * 获取一行图例的内容宽度
     *
     * @return 图例内容宽度
     */
    public int getRowContentWidth() {
        return rowContentWidth;
    }

    public float getPaddingLeftOrRight() {
        return mPaddingLeftOrRight;
    }

    public void setPaddingLeftOrRight(float paddingLeftOrRight) {
        this.mPaddingLeftOrRight = paddingLeftOrRight;
    }

    public List<Floor> getFloors() {
        return floors;
    }
}
