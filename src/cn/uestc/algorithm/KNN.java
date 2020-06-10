package cn.uestc.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KNN {

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

    private int k;
    private ArrayList<Data> trainSet;
    private ArrayList<Data> testSet;

    public KNN(String path1,String path2, int k) {
        this.trainSet = this.loadData(path1);
        this.testSet = this.loadData(path2);
        this.k = k;
    }

    public static void main(String[] args) {
        KNN knn = new KNN("data/3/forKNN/train.txt","data/3/forKNN/test.txt",1);
        knn.predict();
    }

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

    public String classify(Data testData) {
        int size = trainSet.size();
        double[] distances = new double[size];
        HashMap<String, Integer> votes = new HashMap<>();
        for (int i = 0; i < size; i++) {
            distances[i] = getDistance(testData, trainSet.get(i));
        }
        for (int i = 0; i < k; i++) {
            double min = Integer.MAX_VALUE;
            int index = -1;
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
            if (votes.containsKey(trainSet.get(index).label)) {
                int temp = votes.get(trainSet.get(index).label);
                votes.put(trainSet.get(index).label, temp + 1);
            } else {
                votes.put(trainSet.get(index).label, 1);
            }
            distances[index] = Integer.MAX_VALUE;
        }
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

    public static double getDistance(Data data1, Data data2) {
        double sum = 0;
        for (int i = 0; i < data1.data.length; i++) {
            sum += Math.pow(data1.data[i] - data2.data[i], 2);
        }
        return Math.sqrt(sum);
    }

    public void predict(){
        int num = 0;
        for(Data data:testSet){
            String trueLabel = data.label;
            String predLabel = classify(data);
            System.out.println("数据：" + data.toString());
            System.out.println("真实标签：" + trueLabel + " 预测标签：" + predLabel);
            if(trueLabel.equals(predLabel)){
                num++;
            }
        }
        System.out.println("-----------------------");
        System.out.println("准确率：" + new Float(num)/testSet.size());
    }
}
