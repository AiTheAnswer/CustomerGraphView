package com.allen.customergraphview.utils;

import android.graphics.Path;

import com.allen.customergraphview.model.Category;
import com.allen.customergraphview.model.NodeLinkModel;
import com.allen.customergraphview.model.Link;
import com.allen.customergraphview.model.Node;
import com.allen.customergraphview.model.PointF;

import java.util.List;

/**
 * 根据节点图数据计算绘制图形相关的数据
 *
 * @author Renjy
 */
public class NodeLinkUtil {

    private static String[] defaultColors = {"#FBB367", "#80B1D2", "#FB8070", "#CC99FF", "#B0D961",
            "#99CCCC", "#BEBBD8", "#FFCC99", "#8DD3C8", "#FF9999",
            "#CCEAC4", "#BB81BC", "#FBCCEC", "#CCFF66", "#99CC66",
            "#66CC66", "#FF6666", "#FFED6F", "#ff7f50", "#87cefa"};

    /**
     * 计算节点图的相关数据
     *
     * @param nodeGraphModel 节点图数据
     * @param rectMaxRadius  最大内切圆的半径
     * @param allowMaxRadius 允许半径的最大值
     * @param centerPointF    View的中心点
     */
    public static void calculateData(NodeLinkModel nodeGraphModel, float rectMaxRadius, float allowMaxRadius, PointF centerPointF) throws Exception {
        //1、先计算比重
        calculateWeight(nodeGraphModel);
        //2、计算单位比重所占角度
        if (nodeGraphModel.getmWeightSum() <= 0 || nodeGraphModel.getmMaxWeight() <= 0) {
            throw new Exception("数据比重值错误,比重总和小于0");
        }
        float unitWeightAngle = 360.0f / nodeGraphModel.getmWeightSum();
        //3、计算最大比重圆的半径（和允许最大值的半径进行比较取二者较小值）
        float maxWeightRadius;
        if (unitWeightAngle * nodeGraphModel.getmMaxWeight() > 36) {
            maxWeightRadius = allowMaxRadius;
        } else {
            maxWeightRadius = (float) (rectMaxRadius * Math.sin(unitWeightAngle * nodeGraphModel.getmMaxWeight()) / (1 + 2 * Math.sin(unitWeightAngle * nodeGraphModel.getmMaxWeight())));
        }
        //4、计算单位比重圆的半径
        float unitWeightRadius = maxWeightRadius / nodeGraphModel.getmMaxWeight();
        //5、计算每一个node的圆的中心点和半径
        calculateNodes(nodeGraphModel, unitWeightAngle, rectMaxRadius - maxWeightRadius, centerPointF, unitWeightRadius);
        //6、计算连接线和连接区域的路径
        calculateLinkPaths(nodeGraphModel, rectMaxRadius, centerPointF);
        //7、设置绘制不同类型的图形画笔的颜色，也就是设置不同楼层数据的颜色
        setFloorColor(nodeGraphModel, defaultColors);
    }

    /**
     * 计算节点图的相关数据
     *
     * @param nodeGraphModel 节点图数据
     * @param rectMaxRadius  最大内切圆的半径
     * @param allowMaxRadius 允许半径的最大值
     * @param centerPointF    View的中心点
     * @param colors         不同类型的颜色
     */
    public static void calculateData(NodeLinkModel nodeGraphModel, float rectMaxRadius, float allowMaxRadius, PointF centerPointF, String[] colors) throws Exception {
        //1、先计算比重
        calculateWeight(nodeGraphModel);
        //判断数据比重是否有错误
        if (nodeGraphModel.getmWeightSum() <= 0 || nodeGraphModel.getmMaxWeight() <= 0) {
            throw new Exception("数据比重值错误,比重总和小于0");
        }
        //2、计算单位比重所占角度
        float unitWeightAngle = 360.0f / nodeGraphModel.getmWeightSum();
        //3、计算最大比重圆的半径（和允许最大值的半径进行比较取二者较小值）
        float maxWeightRadius = (float) (2 * Math.PI * rectMaxRadius / nodeGraphModel.getmWeightSum());
        maxWeightRadius = Math.min(maxWeightRadius, allowMaxRadius);
        //4、计算单位比重圆的半径
        float unitWeightRadius = maxWeightRadius / nodeGraphModel.getmMaxWeight();
        //5、计算每一个node的圆的中心点和半径
        calculateNodes(nodeGraphModel, unitWeightAngle, rectMaxRadius, centerPointF, unitWeightRadius);
        //6、设置绘制不同类型的图形画笔的颜色，也就是设置不同楼层数据的颜色
        if (null == colors) {
            colors = defaultColors;
        } else if (colors.length < 1) {
            colors = defaultColors;
        }
        setFloorColor(nodeGraphModel, colors);
    }

    /**
     * 计算连接线和连接线点击有效区域的路径
     */
    private static void calculateLinkPaths(NodeLinkModel nodeGraphModel, float rectMaxRadius, PointF centerPointF) {
        List<Link> links = nodeGraphModel.getLinks();
        // for (Link link : links) {
        for (int i = 0; i < links.size(); i++) {
            Link link = links.get(i);
            Node sourceNode = getNodeFormId(nodeGraphModel, link.getSource());
            Node targetNode = getNodeFormId(nodeGraphModel, link.getTarget());
            if (null == sourceNode || null == targetNode) {
                return;
            }
            Path linkPath = new Path();
            PointF sourcePointF = sourceNode.getCenterPointF();
            PointF targetPointF = targetNode.getCenterPointF();
            // + - 2 是增加线的点击范围
            PointF pointF1 = reTransform(centerPointF, angleTransformPoint(rectMaxRadius, sourceNode.getCenterAngle() - 2));
            PointF pointF2 = reTransform(centerPointF, angleTransformPoint(rectMaxRadius, sourceNode.getCenterAngle() + 2));
            PointF pointF3 = reTransform(centerPointF, angleTransformPoint(rectMaxRadius, targetNode.getCenterAngle() - 2));
            PointF pointF4 = reTransform(centerPointF, angleTransformPoint(rectMaxRadius, targetNode.getCenterAngle() + 2));
            linkPath.moveTo(pointF1.x, pointF1.y);
            linkPath.lineTo(pointF2.x, pointF2.y);
            linkPath.quadTo(centerPointF.x, centerPointF.y, pointF3.x, pointF3.y);
            linkPath.lineTo(pointF4.x, pointF4.y);
            linkPath.quadTo(centerPointF.x, centerPointF.y, pointF1.x, pointF1.y);
            link.setLinkPath(linkPath);
            Path linePath = new Path();
            linePath.moveTo(sourcePointF.x, sourcePointF.y);
            linePath.quadTo(centerPointF.x, centerPointF.y, targetPointF.x, targetPointF.y);
            link.setLinePath(linePath);

        }
    }

    /**
     * 计算数据最大比重和比重之和
     *
     * @param nodeGraphModel 节点图的数据
     */
    private static void calculateWeight(NodeLinkModel nodeGraphModel) {
        int weightSum = 0;
        int maxWeight = 0;
        if (null != nodeGraphModel) {
            List<Node> nodes = nodeGraphModel.getNodes();
            if (null != nodes && nodes.size() > 0) {
                for (Node node : nodes) {
                    int symbolSize = node.getSymbolSize();
                    weightSum += symbolSize;
                    if (symbolSize > maxWeight) {
                        maxWeight = symbolSize;
                    }
                }
            }
            nodeGraphModel.setmWeightSum(weightSum);
            nodeGraphModel.setmMaxWeight(maxWeight);
        }
    }

    /**
     * 计算每一个节点的圆的中心点和半径
     *
     * @param nodeGraphModel   节点图数据
     * @param unitWeightAngle  单位比重的角度
     * @param rectMaxRadius    最大内切圆的半径
     * @param centerPointF      View中心点
     * @param unitWeightRadius 单位比重的角度
     * @throws Exception
     */
    private static void calculateNodes(NodeLinkModel nodeGraphModel, float unitWeightAngle, float rectMaxRadius, PointF centerPointF, float unitWeightRadius) {
        float endAngle = 0;
        if (nodeGraphModel.getNodes().size() > 0) {
            List<Node> nodes = nodeGraphModel.getNodes();
            if (null == nodes) {
                return;
            }
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                float nodeAngle = unitWeightAngle * node.getSymbolSize();
                endAngle += nodeAngle;
                PointF pointF = angleTransformPoint(rectMaxRadius, endAngle - nodeAngle / 2);
                PointF nodeCenterPointF = reTransform(centerPointF, pointF);
                node.setCenterPointF(nodeCenterPointF);
                float radius = unitWeightRadius * node.getSymbolSize();
                node.setRadius(radius);
                node.setStartAngle(endAngle - nodeAngle);
                node.setCenterAngle(endAngle - nodeAngle / 2);
                node.setEndAngle(endAngle);
                node.setSweepAngle(nodeAngle);
            }

        }
    }

    /**
     * 设置楼层颜色
     */
    private static void setFloorColor(NodeLinkModel nodeGraphModel, String[] colors) {
        List<Category> categories = nodeGraphModel.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            category.setColor(colors[i % colors.length]);
        }

    }


    /**
     * 获取节点通过节点id
     *
     * @param nodeGraphModel 节点数据
     * @param source         节点id
     */
    private static Node getNodeFormId(NodeLinkModel nodeGraphModel, String source) {
        Node fromNode = null;
        List<Node> nodes = nodeGraphModel.getNodes();
        for (Node node : nodes) {
            if (node.getId().equals(source)) {
                fromNode = node;
                break;
            }
        }
        return fromNode;
    }

    /**
     * 以View中心点为右手规则直角坐标系的圆点，计算某个角度对应的坐标点
     *
     * @param radius 半径
     * @param angle  角度
     * @return 角度所对应的点
     */
    private static PointF angleTransformPoint(float radius, float angle) {
        PointF pointF = new PointF();
        pointF.x = (int) (radius * Math.cos(angle * Math.PI / 180));
        pointF.y = (int) (-radius * Math.sin(angle * Math.PI / 180));
        return pointF;
    }

    /**
     * 将基于圆心坐标的点转化为屏幕坐标点
     *
     * @param centerPointF View中心点
     * @param pointF       要转换的点
     * @return 转化后点的坐标
     */
    private static PointF reTransform(PointF centerPointF, PointF pointF) {
        PointF transformPointF = new PointF();
        transformPointF.x += pointF.x + centerPointF.x;
        transformPointF.y = centerPointF.y - pointF.y;
        return transformPointF;
    }
}
