package com.allen.customergraphview.model;

import android.graphics.Path;

public class Link {
    /**
     * id : null
     * name : null
     * symbolSize : null
     * source : Hn_P00012S00287
     * category : null
     * target : Hn_P00012S00285
     * value : 99.99%
     * lineStyle : {"width":24}
     */

    private Object id;
    private Object name;
    private Object symbolSize;
    private String source;
    private String category;
    private String target;
    private String value;
    private LineStyle lineStyle;
    //连接线的路径，用于判断是否在某个点上
    private Path path;

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Object getSymbolSize() {
        return symbolSize;
    }

    public void setSymbolSize(Object symbolSize) {
        this.symbolSize = symbolSize;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}