package com.allen.customergraphview.utils;

import android.graphics.Point;

import com.allen.customergraphview.exception.GraphException;
import com.allen.customergraphview.model.Floor;
import com.allen.customergraphview.model.GraphModel;
import com.allen.customergraphview.model.Node;

import java.util.List;

/**
 * 根据节点图数据计算绘制图形相关的数据
 *
 * @author Renjy
 */
public class CalculateUtil {

    private static String[] defaultColors = {"#FBB367", "#80B1D2", "#FB8070", "#CC99FF", "#B0D961",
            "#99CCCC", "#BEBBD8", "#FFCC99", "#8DD3C8", "#FF9999",
            "#CCEAC4", "#BB81BC", "#FBCCEC", "#CCFF66", "#99CC66",
            "#66CC66", "#FF6666", "#FFED6F", "#ff7f50", "#87cefa"};

    /**
     * 计算节点图的相关数据
     *
     * @param graphModel     节点图数据
     * @param rectMaxRadius  最大内切圆的半径
     * @param allowMaxRadius 允许半径的最大值
     * @param centerPoint    View的中心点
     */
    public static void calculateData(GraphModel graphModel, float rectMaxRadius, float allowMaxRadius, Point centerPoint) throws GraphException {
        //1、先计算比重
        calculateWeight(graphModel);
        //2、计算单位比重所占角度
        if (graphModel.getmWeightSum() <= 0 || graphModel.getmMaxWeight() <= 0) {
            throw new GraphException("数据比重值错误,比重总和小于0");
        }
        float unitWeightAngle = 360.0f / graphModel.getmWeightSum();
        //3、计算最大比重圆的半径（和允许最大值的半径进行比较取二者较小值）
        float maxWeightRadius;
        if (unitWeightAngle * graphModel.getmMaxWeight() > 36) {
            maxWeightRadius = allowMaxRadius;
        } else {
            maxWeightRadius = (float) (rectMaxRadius * Math.sin(unitWeightAngle * graphModel.getmMaxWeight()) / (1 + 2 * Math.sin(unitWeightAngle * graphModel.getmMaxWeight())));
        }
        //4、计算单位比重圆的半径
        float unitWeightRadius = maxWeightRadius / graphModel.getmMaxWeight();
        //5、计算每一个node的圆的中心点和半径
        calculateNodes(graphModel, unitWeightAngle, rectMaxRadius - maxWeightRadius, centerPoint, unitWeightRadius);
        //6、设置绘制不同类型的图形画笔的颜色，也就是设置不同楼层数据的颜色
        setFloorColor(graphModel, defaultColors);

    }

    /**
     * 计算节点图的相关数据
     *
     * @param graphModel     节点图数据
     * @param rectMaxRadius  最大内切圆的半径
     * @param allowMaxRadius 允许半径的最大值
     * @param centerPoint    View的中心点
     * @param colors         不同类型的颜色
     */
    public static void calculateData(GraphModel graphModel, float rectMaxRadius, float allowMaxRadius, Point centerPoint, String[] colors) throws GraphException {
        //1、先计算比重
        calculateWeight(graphModel);
        //2、计算单位比重所占角度
        if (graphModel.getmWeightSum() <= 0 || graphModel.getmMaxWeight() <= 0) {
            throw new GraphException("数据比重值错误,比重总和小于0");
        }
        float unitWeightAngle = 360.0f / graphModel.getmWeightSum();
        //3、计算最大比重圆的半径（和允许最大值的半径进行比较取二者较小值）
        float maxWeightRadius = (float) (2 * Math.PI * rectMaxRadius / graphModel.getmWeightSum());
        maxWeightRadius = Math.min(maxWeightRadius, allowMaxRadius);
        //4、计算单位比重圆的半径
        float unitWeightRadius = maxWeightRadius / graphModel.getmMaxWeight();
        //5、计算每一个node的圆的中心点和半径
        calculateNodes(graphModel, unitWeightAngle, rectMaxRadius, centerPoint, unitWeightRadius);
        //6、设置绘制不同类型的图形画笔的颜色，也就是设置不同楼层数据的颜色
        if (null == colors) {
            colors = defaultColors;
        } else if (colors.length < 1) {
            colors = defaultColors;
        }
        setFloorColor(graphModel, colors);
    }

    /**
     * 设置楼层颜色
     */
    private static void setFloorColor(GraphModel graphModel, String[] colors) {
        List<Floor> floors = graphModel.getFloors();
        for (int i = 0; i < floors.size(); i++) {
            Floor floor = floors.get(i);
            floor.setColor(colors[i % colors.length]);
        }

    }

    /**
     * 计算每一个节点的圆的中心点和半径
     *
     * @param graphModel       节点图数据
     * @param unitWeightAngle  单位比重的角度
     * @param rectMaxRadius    最大内切圆的半径
     * @param centerPoint      View中心点
     * @param unitWeightRadius 单位比重的角度
     * @throws GraphException
     */
    private static void calculateNodes(GraphModel graphModel, float unitWeightAngle, float rectMaxRadius, Point centerPoint, float unitWeightRadius) throws GraphException {
        float centerAngle = 0;
        if (graphModel.getNodes().size() > 0) {
            List<Node> nodes = graphModel.getNodes();
            if (null == nodes) {
                throw new GraphException("nodes 数据为空");
            }
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                float nodeAngle = unitWeightAngle * node.getSymbolSize();
                centerAngle += nodeAngle;
                Point point = angleTransformPoint(rectMaxRadius, centerAngle - nodeAngle / 2);
                Point nodeCenterPoint = reTransform(centerPoint, point);
                node.setCenterPoint(nodeCenterPoint);
                float radius = unitWeightRadius * node.getSymbolSize();
                node.setRadius(radius);
                node.setSweepAngle(nodeAngle);
            }

        }
    }

    /**
     * 计算数据最大比重和比重之和
     *
     * @param graphModel 节点图的数据
     */
    private static void calculateWeight(GraphModel graphModel) throws GraphException {
        int weightSum = 0;
        int maxWeight = 0;
        if (null != graphModel) {
            List<Node> nodes = graphModel.getNodes();
            if (null != nodes && nodes.size() > 0) {
                for (Node node : nodes) {
                    int symbolSize = node.getSymbolSize();
                    weightSum += symbolSize;
                    if (symbolSize > maxWeight) {
                        maxWeight = symbolSize;
                    }
                }
            }
            graphModel.setmWeightSum(weightSum);
            graphModel.setmMaxWeight(maxWeight);
        } else {
            throw new GraphException("数据为空");
        }
    }

    /**
     * 以View中心点为右手规则直角坐标系的圆点，计算某个角度对应的坐标点
     *
     * @param radius 半径
     * @param angle  角度
     * @return 角度所对应的点
     */
    private static Point angleTransformPoint(float radius, float angle) {
        Point point = new Point();
        point.x = (int) (radius * Math.cos(angle * Math.PI / 180));
        point.y = (int) (-radius * Math.sin(angle * Math.PI / 180));
        return point;
    }

    /**
     * 将基于圆心坐标的点转化为屏幕坐标点
     *
     * @param centerPoint View中心点
     * @param point       要转换的点
     * @return 转化后点的坐标
     */
    private static Point reTransform(Point centerPoint, Point point) {
        Point transformPoint = new Point();
        transformPoint.x += point.x + centerPoint.x;
        transformPoint.y = centerPoint.y - point.y;
        return transformPoint;
    }
}
