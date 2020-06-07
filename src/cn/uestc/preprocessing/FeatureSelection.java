package cn.uestc.preprocessing;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.*;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FeatureSelection {
    public static void main(String[] args) throws Exception{
        //read data
        DataSource source = new DataSource("data/1/iris.arff");
        Instances instances = source.getDataSet();
        //process
        ASEvaluation ae = new InfoGainAttributeEval();
        Ranker ranker = new Ranker();
        ranker.setNumToSelect(3);   //最大特征数
        ranker.setThreshold(0.0);   //阈值
        AttributeSelection as = new AttributeSelection();
        as.setEvaluator(ae);
        as.setSearch(ranker);
        as.setInputFormat(instances);
        Instances newInstances = Filter.useFilter(instances,as);
        DataSink.write("data/1/iris_fs.arff",newInstances);
    }
}
