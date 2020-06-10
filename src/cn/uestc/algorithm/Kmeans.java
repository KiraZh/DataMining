package cn.uestc.algorithm;

import cn.uestc.utils.PicUtility;
import cn.uestc.utils.dataUtils;

import java.util.ArrayList;
import java.util.Random;

public class Kmeans {
    private final int k;
    private final ArrayList<double[]> dataSet;
    private final int dim;

    public Kmeans(String path, int k) {
        this.k = k;
        this.dataSet = dataUtils.loadData(path);
        this.dim = dataSet.get(0).length;
    }

    public static void main(String[] args) {
        Kmeans kmeans = new Kmeans("data/4/data.txt", 3);
        kmeans.cluster();
    }


    /**
     * 聚类
     */
    private void cluster() {
        double[][] centroid = getCentroid();
        int[] assignment = new int[dataSet.size()]; //此数组用于存储数据点的分配结果
        boolean clusterChanged = true;  //点的簇分配结果是否发生改变，发生改变为true
        while (clusterChanged) {
            clusterChanged = false;
            int[] clusterNum = new int[k];  //用于记录一个簇中点的个数
            double[][] clusterSum = new double[k][dim];     //一个簇的各点数值的和
            for (int i = 0; i < dataSet.size(); i++) {      //遍历dataSet中的每一个点
                double[] data = dataSet.get(i);
                double minDis = Double.MAX_VALUE;
                for (int j = 0; j < k; j++) {   //遍历每一个质心
                    double distance = dataUtils.getDistance(centroid[j], data);   //计算距离
                    if (distance < minDis) {       //找最小距离
                        assignment[i] = j;  //更新点的簇分配结果
                        minDis = distance;
                    }
                }
                //点聚类完毕
                clusterNum[assignment[i]]++;
                for (int j = 0; j < dim; j++) {
                    clusterSum[assignment[i]][j] += data[j];
                }
            }
            //更新质心
            for (int i = 0; i < k; i++) {
                double[] data = new double[dim];
                for (int j = 0; j < dim; j++) {
                    if (clusterNum[i] != 0) {
                        data[j] = clusterSum[i][j] / clusterNum[i];     //求点的平均
                    } else {
                        data[j] = Math.random() * 100;
                    }
                }
                if (dataUtils.getDistance(centroid[i], data) != 0) {
                    clusterChanged = true;
                }
                centroid[i] = data;
            }
            //可视化
            visualize(centroid, assignment);
        }
    }

    /**
     * 用于可视化聚类过程
     *
     * @param centroid   质心
     * @param assignment 点的分配结果
     */
    private void visualize(double[][] centroid, int[] assignment) {
        ArrayList<ArrayList<double[]>> clusters = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            clusters.add(new ArrayList<>());
        }
        for (int i = 0; i < dataSet.size(); i++) {
            clusters.get(assignment[i]).add(dataSet.get(i));
        }
        double[][][] doubles = new double[k][][];
        for (int i = 0; i < k; i++) {
            double[][] doubles1 = new double[clusters.get(i).size()][];
            for (int j = 0; j < doubles1.length; j++) {
                doubles1[j] = clusters.get(i).get(j);
            }
            doubles[i] = doubles1;
        }
        System.out.println("cluster mean:");
        for (int i = 0; i < centroid.length; i++) {
            for (double x : centroid[i]) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
        PicUtility.show(doubles, k);
    }

    /**
     * 质心的初始化
     *
     * @return 返回质心数组，共k个点
     */
    private double[][] getCentroid() {
        Random random = new Random();
        double[][] centroid = new double[k][dim];
        for (int i = 0; i < k; i++) {
            double[] data = new double[dim];
            for (int j = 0; j < dim; j++) {
                data[j] = random.nextDouble() * 100;  //随机，这里的100是根据数据集观察得来
            }
            centroid[i] = data;
        }
        return centroid;
    }
}
