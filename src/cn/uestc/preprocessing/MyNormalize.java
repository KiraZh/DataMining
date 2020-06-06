package cn.uestc.preprocessing;


import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;


public class MyNormalize {
    public static void main(String[] args) throws Exception {
        //读取数据
        DataSource source = new DataSource("data/iris.arff");
        Instances instances = source.getDataSet();
        //归一化
        Normalize norm = new Normalize();
        norm.setInputFormat(instances);
        Instances newInstances = Filter.useFilter(instances, norm);
        //打印数据
        try {
            int numOfAttributes = newInstances.numAttributes();
            //属性名
            for (int i = 0; i < numOfAttributes; i++) {
                Attribute attribute = newInstances.attribute(i);
                System.out.print(attribute.name() + "  ");
            }
            System.out.println();
            //实例
            int numOfInstance = newInstances.numInstances();
            for (int i = 0; i < numOfInstance; i++) {
                Instance instance = newInstances.instance(i);
                System.out.println(instance.toString() + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //保存数据
        DataSink.write("data/iris_norm.arff", newInstances);
    }

}
