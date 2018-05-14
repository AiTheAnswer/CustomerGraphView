package com.allen.customergraphview.model;

import java.util.List;

/**
 * 节点流程图的实体类
 *
 * @author Renjy
 */
public class NodeLinkModel {

    private List<Category> floors;
    private List<Node> nodes;
    private List<Label> labels;
    //比重之和
    private int mWeightSum;
    //最大的比重
    private int mMaxWeight;

    public List<Category> getCategories() {
        return floors;
    }

    public void setCategories(List<Category> floors) {
        this.floors = floors;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }


    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public int getmWeightSum() {
        return mWeightSum;
    }

    public void setmWeightSum(int mWeightSum) {
        this.mWeightSum = mWeightSum;
    }

    public int getmMaxWeight() {
        return mMaxWeight;
    }

    public void setmMaxWeight(int mMaxWeight) {
        this.mMaxWeight = mMaxWeight;
    }
}
