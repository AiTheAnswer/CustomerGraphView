package com.allen.customergraphview.model;

import java.util.List;

/**
 * 节点流程图的实体类
 *
 * @author Renjy
 */
public class NodeGraphModel {

    private List<Floor> floors;
    private List<Node> nodes;
    private List<Label> labels;
    private List<Link> links;
    //比重之和
    private int mWeightSum;
    //最大的比重
    private int mMaxWeight;

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
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

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
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
