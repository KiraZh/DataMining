package cn.uestc.algorithm;

import cn.uestc.utils.PicUtility;

import java.io.BufferedReader;
import java.io.File;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class Kmeans {
    private int k;
    private ArrayList<double[]> dataSet;
    private int dim;

    public Kmeans(String path,int k) {
        this.k = k;
//        this.dataSet = new ArrayList<>();
        this.dataSet = loadData(path);
        this.dim = dataSet.get(0).length;

    }

    public static void main(String[] args){
        Kmeans kmeans = new Kmeans("data/4/data.txt",3);
//        kmeans.loadData("data/4/data.txt");
        kmeans.cluster();
    }

    //读取数据
    private ArrayList<double[]> loadData(String path) {
        ArrayList<double[]> dataset = new ArrayList<>();
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                String[] temp = line.trim().split(" ");
                double[] data = new double[temp.length];
                for(int i = 0;i < data.length; i++){
                    data[i] = Double.parseDouble(temp[i]);
                }
                dataset.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private void cluster(){
        double[][] centroid = getCentroid();
        int[] assignment = new int[dataSet.size()]; //存储数据点的分配结果
        boolean clusterChanged = true;  //有没有点的簇分配结果发生改变
        while(clusterChanged) {
            clusterChanged = false;
            int[] clusterNum = new int[k];  //一个簇的点数
            double[][] clusterSum = new double[k][dim];     //一个簇的各点的和
            for (int i = 0; i < dataSet.size(); i++) {      //遍历dataSet中的每一个点
                double[] data = dataSet.get(i);
                double minDis = Double.MAX_VALUE;
                for (int j = 0; j < k; j++) {   //遍历每一个质心
                    double distance = getDistance(centroid[j], data);   //计算距离
                    if (distance < minDis) {       //找最小距离
//                        if(j != assignment[i]) {    //如果点的归属发生改变
//                            clusterChanged = true;
//                        }
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
            for(int i = 0;i < k;i++) {
                double[] data = new double[dim];
                for (int j = 0; j < dim; j++) {
                    if (clusterNum[i] != 0) {
                        data[j] = clusterSum[i][j] / clusterNum[i];     //求点的平均
                    } else {
                        data[j] = Math.random() * 100;
                    }
                }
                if(getDistance(centroid[i],data) != 0){
                    clusterChanged = true;
                }
                centroid[i] = data;
            }

            //可视化
            ArrayList<ArrayList<double[]>> clusters = new ArrayList<ArrayList<double[]>>();
            for(int i = 0;i < k;i++){
                clusters.add(new ArrayList<double[]>());    //3个列表
            }
            for(int i = 0;i<dataSet.size();i++){
                clusters.get(assignment[i]).add(dataSet.get(i));
            }
            double[][][] doubles = new double[k][][];
            for(int i = 0;i < k;i++){
                double[][] doubles1 = new double[clusters.get(i).size()][];
                for(int j = 0;j < doubles1.length;j++){
                    doubles1[j] = clusters.get(i).get(j);
                }
                doubles[i] = doubles1;
            }
            System.out.println("cluster mean:");
            for(int i = 0;i < centroid.length;i++){
                for(double x : centroid[i]){
                    System.out.print(x + " ");
                }
                System.out.println();
            }
            PicUtility.show(doubles,k);
        }
    }

    //质心初始化
    private double[][] getCentroid(){
        Random random = new Random();
        double[][] centroid = new double[k][dim];
        for(int i = 0;i < k;i++){
            double[] data = new double[dim];
            for(int j = 0;j < dim;j++){
                data[j] = random.nextDouble()*100;  //随机，这里的100是根据数据集观察得来
            }
            centroid[i] = data;
        }
        return centroid;
    }

    //求距离
    public static double getDistance(double[] data1, double[] data2) {
        double sum = 0;
        for (int i = 0; i < data1.length; i++) {
            sum += Math.pow(data1[i] - data2[i], 2);    //平方
        }
        return Math.sqrt(sum);  //开方
    }
}
