package cn.uestc.preprocessing;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

public class MyReplaceMissingValues {
    public static void main(String[] args) throws Exception {
        DataSource source = new DataSource("data/labor.arff");
        Instances instances = source.getDataSet();
        int dim = instances.numAttributes();
        int num = instances.numInstances();
        double[] meanV = new double[dim];
        for (int i = 0; i < dim; i++) {
            meanV[i] = 0;
            int count = 0;
            for (int j = 0; j < num; j++) {
                if (!instances.instance(j).isMissing(i)) {
                    meanV[i] += instances.instance(j).value(i);
                    count++;
                }
            }
            meanV[i] = meanV[i] / count;
        }
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < num; j++) {
                if (instances.instance(j).isMissing(i)) {
                    instances.instance(j).setValue(i, meanV[i]);
                }
            }
        }
        DataSink.write("data/labor_missing.arff", instances);
    }
}
