package com.allen.customergraphview.model;

/**
 * 楼层实体类(对应图形数据类目)
 *
 * @author Renjy
 */
public class Floor {
    /**
     * name : 4F
     */

    private String name;
    private String color;

    public Floor(String name) {
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
}
