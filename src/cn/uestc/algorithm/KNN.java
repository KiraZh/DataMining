package cn.uestc.algorithm;

import cn.uestc.utils.dataUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KNN {

    private final int k;
    private final ArrayList<Data> trainSet;
    private final ArrayList<Data> testSet;
    public KNN(String path1, String path2, int k) {
        this.trainSet = loadData(path1);
        this.testSet = loadData(path2);
        this.k = k;
    }

    public static void main(String[] args) {
        KNN knn = new KNN("data/3/forKNN/train.txt", "data/3/forKNN/test.txt", 1);
        knn.predict();
    }

    /**
     * 从制定的文件位置读取数据
     *
     * @param path 文件的位置
     * @return 返回数据集
     */
    public static ArrayList<Data> loadData(String path) {
        ArrayList<Data> dataset = new ArrayList<>();
        File file = new File(path);
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] temp = line.trim().split(",");
                double[] data = new double[temp.length - 1];
                for (int i = 0; i < data.length; i++) {
                    data[i] = Double.parseDouble(temp[i]);
                }
                Data dataObject = new Data(data, temp[temp.length - 1]);
                dataset.add(dataObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }

    /**
     * 分类步骤
     *
     * @param testData 测试数据点
     * @return 返回分类结果的标签
     */
    public String classify(Data testData) {
        int size = trainSet.size();
        double[] distances = new double[size];
        HashMap<String, Integer> votes = new HashMap<>();   //用于计算每个标签的投票数
        //计算测试数据点到训练集各点的距离
        for (int i = 0; i < size; i++) {
            distances[i] = dataUtils.getDistance(testData.data, trainSet.get(i).data);
        }
        //选择距离最短的k个
        for (int i = 0; i < k; i++) {
            double min = Integer.MAX_VALUE;
            int index = -1;
            //找最短距离和点的序号
            for (int j = 0; j < size; j++) {
                if (distances[j] < min) {
                    min = distances[j];
                    index = j;
                }
            }
            if (index == -1) {
                System.out.println("error");
                System.exit(1);
            }
            //将得到的最短距离的点加入投票
            if (votes.containsKey(trainSet.get(index).label)) {
                int temp = votes.get(trainSet.get(index).label);
                votes.put(trainSet.get(index).label, temp + 1);
            } else {
                votes.put(trainSet.get(index).label, 1);
            }
            distances[index] = Integer.MAX_VALUE;
        }
        //找投票中的最大值，也就是分类结果
        int max = 0;
        String str = null;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > max) {
                str = entry.getKey();
                max = entry.getValue();
            }
        }
        return str;
    }

    /**
     * 预测测试数据集，打印预测结果与准确度
     */
    public void predict() {
        int num = 0;
        //对测试集中的每一个点进行分类
        for (Data data : testSet) {
            String trueLabel = data.label;
            String predLabel = classify(data);
            System.out.println("数据：" + data.toString());
            System.out.println("真实标签：" + trueLabel + " 预测标签：" + predLabel);
            if (trueLabel.equals(predLabel)) {
                num++;
            }
        }
        System.out.println("-----------------------");
        //计算准确度
        System.out.println("准确率：" + new Float(num) / testSet.size());
    }

    public static class Data {
        double[] data;
        String label;

        Data(double[] data, String label) {
            this.data = data;
            this.label = label;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "data=" + Arrays.toString(data) +
                    ", label='" + label + '\'' +
                    '}';
        }
    }
}
