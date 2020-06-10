package cn.uestc.algorithm;

import cn.uestc.utils.PicUtility;
import cn.uestc.utils.dataUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


//1. REPEAT
//2. 从数据库中抽取一个未处理过的点；
//3. IF 抽出的点是核心点 THEN找出所有从该点密度可达的对象，形成一个簇
//4. ELSE 抽出的点是边缘点(非核心对象)，跳出本次循环，寻找下一点；
//5. UNTIL 所有点都被处理；
public class DBScan {
    private final double radius;  //半径
    private final int minPts;     //最少数目
    private final ArrayList<Data> dataSet = new ArrayList<>();    //数据集

    public DBScan(double r, int m, String path) {
        this.radius = r;
        this.minPts = m;
        ArrayList<double[]> values = dataUtils.loadData(path);
        for (double[] value : values) {     //读入数据
            Data data = new Data(value);
            this.dataSet.add(data);
        }
    }

    public static void main(String[] args) {
        double radius = 2.0;
        int minPts = 10;
        String filePath = "data/4/data.txt";
        DBScan dbScan = new DBScan(radius, minPts, filePath);
        dbScan.cluster();
    }

    /**
     * 聚类
     */
    private void cluster() {
        Map<Integer, ArrayList<Data>> clusters = new HashMap<>();   //存储聚类的结果（id，数据点集）
        int id = 1;     //簇的编号从1开始
        for (int i = 0; i < dataSet.size(); i++) {
            Data data = dataSet.get(i);
            if (!data.isVisited) {      //对未处理的点进行操作
                data.isVisited = true;
                ArrayList<Data> neighbours = getNeighbours(data);   //获取点的所有临近点
                if (neighbours.size() >= minPts) {  //对于临近点大于最少点数的点进行处理
                    data.clusterID = id;
                    clusters.put(id, expand(data, neighbours));
                    id++;
                }
            }
        }
        //将噪声也放入结果中便于画图
        ArrayList<Data> temp = new ArrayList<>();
        for (Data data : dataSet) {
            if (data.clusterID == -1) {
                temp.add(data);
            }
        }
        clusters.put(0, temp);
        show(clusters);
    }

    /**
     * 绘制聚类结果图像
     *
     * @param clusters 聚类的结果
     */
    private void show(Map<Integer, ArrayList<Data>> clusters) {
        double[][][] doubles = new double[clusters.size()][][];
        for (int i = 0; i < clusters.size(); i++) {
            double[][] doubles1 = new double[clusters.get(i).size()][];
            for (int j = 0; j < doubles1.length; j++) {
                doubles1[j] = clusters.get(i).get(j).value;
            }
            doubles[i] = doubles1;
        }
        PicUtility.show(doubles, clusters.size());
    }

    /**
     * 计算给定点的临近点
     *
     * @param data 数据点
     * @return 所有临近点
     */
    private ArrayList<Data> getNeighbours(Data data) {
        ArrayList<Data> neighbours = new ArrayList<>();
        for (Data data1 : dataSet) {
            if (dataUtils.getDistance(data.value, data1.value) < radius) {
                neighbours.add(data1);
            }
        }
        return neighbours;
    }

    /**
     * 扩展簇
     *
     * @param core       核心点
     * @param neighbours 核心点的临近点集
     * @return 同一簇的所有点
     */
    private ArrayList<Data> expand(Data core, ArrayList<Data> neighbours) {
        ArrayList<Data> results = new ArrayList<>();
        results.add(core);  //将核心点先加入结果集中
        ArrayList<Data> candidates = new ArrayList<>();
        candidates.addAll(neighbours);
        candidates.remove(core);    //生成候选集
        //循环查找所有符合条件的点，直到候选集为空
        while (candidates.size() != 0) {
            //对候选集中所有的未处理的点进行操作
            Data data = candidates.get(0);
            if (!data.isVisited) {
                data.isVisited = true;
                //找临近点，如果大于最少个数，就将其加入候选集
                ArrayList<Data> neiDatas = getNeighbours(data);
                if (neiDatas.size() >= minPts) {
                    for (Data temp : neiDatas) {  //加入候选集
                        if (!candidates.contains(temp)) {   //防止重复加点的情况
                            candidates.add(temp);
                        }
                    }
                }
                //将操作的点放入核心点所在的簇
                data.clusterID = core.clusterID;
                results.add(data);
            }
            //从候选集中去除已经处理过的点
            candidates.remove(data);
        }
        return results;
    }

    private class Data {    //Data：数值、是否处理过、簇的标号
        double[] value;
        boolean isVisited;
        int clusterID;

        Data(double[] value) {
            this.value = value;
            this.isVisited = false; //初始化为未处理
            this.clusterID = 0;    //0指噪声
        }
    }


}
