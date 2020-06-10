package cn.uestc.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class dataUtils {
    /**
     * 读取数据
     *
     * @param path 数据集的路径
     * @return 返回数据集的list
     */
    public static ArrayList<double[]> loadData(String path) {
        ArrayList<double[]> dataset = new ArrayList<>();
        try {
            File file = new File(path);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] temp = line.trim().split(" ");
                double[] data = new double[temp.length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = Double.parseDouble(temp[i]);
                }
                dataset.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    /**
     * 计算两个点的欧式距离
     *
     * @param data1 点1
     * @param data2 点2
     * @return 返回欧式距离
     */
    public static double getDistance(double[] data1, double[] data2) {
        double sum = 0;
        for (int i = 0; i < data1.length; i++) {
            sum += Math.pow(data1[i] - data2[i], 2);    //平方
        }
        return Math.sqrt(sum);  //开方
    }
}
