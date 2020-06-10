package cn.uestc.algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Apriori {
    private final Map<Integer, Set<String>> database; //数据库
    private final float minSup;   //最小支持度
    private final float minConf;  //最小置信度
    private final Integer databaseCount; //事务数

    private final Map<Integer, Set<Set<String>>> freqItemSet;
    private final HashMap<Set<String>, Float> itemSetWithSup;
    private final HashMap<Set<String>, HashMap<Set<String>, Float>> associationRules;

    public Apriori(Map<Integer, Set<String>> database, float minSup, float minConf) {
        this.database = database;
        this.minSup = minSup;
        this.minConf = minConf;
        this.databaseCount = this.database.size();
        freqItemSet = new TreeMap<>();
        itemSetWithSup = new HashMap<>();
        associationRules = new HashMap<>();

    }

    public static void main(String[] args) throws IOException {
        float minSup = 0.2f;
        float minConf = 0.7f;
        Map<Integer, Set<String>> DB = getDatabase();
        Apriori apr = new Apriori(DB, minSup, minConf);
        apr.findAllFreqItemSet();
        apr.findAssociationRules();
    }

    /**
     * 读取数据与数据初始化
     *
     * @return 返回事务数据库
     * @throws IOException
     */
    private static Map<Integer, Set<String>> getDatabase() throws IOException {
        String fn = "data/2/data.txt";
        File file = new File(fn);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        Map<Integer, Set<String>> DB = new HashMap<>();
        String line;
        String sp = ",";    //分隔符
        int num = 0;
        //读入数据，生成map
        while ((line = br.readLine()) != null) {
            String[] temp = line.trim().split(sp);
            Set<String> set = new TreeSet<>();
            for (int i = 0; i < temp.length; i++) {
                set.add(temp[i].trim());
            }
            num++;
            DB.put(num, set);
        }
        return DB;
    }

    /**
     * 找出所有的频繁项集
     */
    public void findAllFreqItemSet() {
        Map<Set<String>, Float> freqOneItemSet = this.findFreqOneItemSet();
        //频繁一项集
        itemSetWithSup.putAll(freqOneItemSet);
        freqItemSet.put(1, freqOneItemSet.keySet());
        System.out.println("频繁1" + "项集：" + freqOneItemSet);
        //频繁K项集
        int k = 2;
        while (true) {
            Set<Set<String>> C = apriori_gen(k, freqItemSet.get(k - 1));    //由k-1项集生成k项集
            Map<Set<String>, Float> freqKItemSet = findFreqKItemSet(k, C);  //求出频繁k项集
            if (freqKItemSet.isEmpty()) {
                break;
            } else {
                itemSetWithSup.putAll(freqKItemSet);
                freqItemSet.put(k, freqKItemSet.keySet());
                System.out.println("频繁" + k + "项集：" + freqKItemSet);
            }
            k++;
        }
    }

    /**
     * 遍历整个数据集，找出所有一项集，并计算计数，
     * 如果 support >= min_sup，则将其放入频繁一项集的变量中，
     * 用 Map<Set<String>, Float>存储项集及对应的支持度，
     * 返回频繁一项集。
     *
     * @return <频繁1项集，支持度>
     */
    public Map<Set<String>, Float> findFreqOneItemSet() {
        HashMap<Set<String>, Integer> oneItemMap = new HashMap<>();
        //支持度计数
        for (Map.Entry<Integer, Set<String>> entry : database.entrySet()) {
            Set<String> itemSet = entry.getValue();
            for (String item : itemSet) {
                Set<String> key = new HashSet<>();
                key.add(item.trim());
                if (!oneItemMap.containsKey(key)) {
                    oneItemMap.put(key, 1);
                } else {
                    int value = 1 + oneItemMap.get(key);
                    oneItemMap.put(key, value);
                }
            }
        }
        //计算支持度，判断是否大于最小支持度
        return getSetFloatMap(oneItemMap);
    }

    /**
     * 找到频繁k项集（对生成的k项集进行支持度计数）
     *
     * @param k             项数
     * @param candiKItemSet 候选K项集
     * @return <频繁k项集，支持度>
     */
    public Map<Set<String>, Float> findFreqKItemSet(int k, Set<Set<String>> candiKItemSet) {
        HashMap<Set<String>, Integer> itemMap = new HashMap<>();
        //支持度计数
        for (Map.Entry<Integer, Set<String>> entry : database.entrySet()) {
            for (Set<String> itemSet : candiKItemSet) {
                if (entry.getValue().containsAll(itemSet)) {
                    if (itemMap.containsKey(itemSet)) {
                        int value = itemMap.get(itemSet) + 1;
                        itemMap.put(itemSet, value);
                    } else {
                        itemMap.put(itemSet, 1);
                    }
                }
            }
        }
        //计算支持度与判断
        return getSetFloatMap(itemMap);
    }

    /**
     * 计算支持度
     *
     * @param itemMap 项集
     * @return 项集与支持度的map
     */
    private Map<Set<String>, Float> getSetFloatMap(HashMap<Set<String>, Integer> itemMap) {
        Map<Set<String>, Float> map = new HashMap<>();
        for (Map.Entry<Set<String>, Integer> entry : itemMap.entrySet()) {
            float support = entry.getValue() / new Float(databaseCount);
            if (support >= minSup) {
                map.put(entry.getKey(), support);
            }
        }
        return map;
    }


    /**
     * 输入k和频繁k项集，得到潜在k+1项集（包括连接步和剪枝步）
     *
     * @param k            项数
     * @param freqKItemSet 频繁k项集
     * @return 候选K+1项集
     */
    public Set<Set<String>> apriori_gen(int k, Set<Set<String>> freqKItemSet) {
        Set<Set<String>> candiFreqKItemSet = new HashSet<>();
        for (Set<String> set : freqKItemSet) {
            for (Set<String> set1 : freqKItemSet) {
                if (!set.equals(set1)) {
                    //连接步
                    Set<String> commItems = new HashSet<>(set);
                    commItems.retainAll(set1);
                    //确保前k-2项相同
                    if (commItems.size() == k - 2) {
                        Set<String> candiItems = new HashSet<>();
                        candiItems.addAll(set);
                        candiItems.addAll(set1);
                        if (!has_infrequent_subset(candiItems, freqKItemSet)) {
                            candiFreqKItemSet.add(candiItems);
                        }
                    }
                }
            }
        }
        return candiFreqKItemSet;
    }

    /**
     * 判断是否剪枝，如果候选k+1项集有k项子集不在频繁k项集中，则返回false
     *
     * @param candiItems 候选的k+1项集
     * @param freqKItems 频繁k项集
     * @return 是否剪枝
     */
    public Boolean has_infrequent_subset(Set<String> candiItems, Set<Set<String>> freqKItems) {
        //得到candiItem（k+1）的所有k项子集
        //如果有子集不在频繁k项集中，则返回false
        for (String item : candiItems) {
            Set<String> subSet = new HashSet<>(candiItems);
            subSet.remove(item);
            if (!freqKItems.contains(subSet)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 找到关联规则
     */
    public void findAssociationRules() {
        freqItemSet.remove(1);
        for (Map.Entry<Integer, Set<Set<String>>> entry : freqItemSet.entrySet()) {
            for (Set<String> itemSet : entry.getValue()) {    //遍历所有频繁项集（k>1)
                Set<Set<String>> subSet = properSubSet(itemSet);
                for (Set<String> set1 : subSet) {
                    Set<String> set2 = new HashSet<>(itemSet);
                    set2.removeAll(set1);
                    float conf = itemSetWithSup.get(itemSet) / itemSetWithSup.get(set1);
                    if (conf >= minConf) {
                        HashMap<Set<String>, Float> map = new HashMap<>();
                        if (associationRules.containsKey(set1)) {
                            map = associationRules.get(set1);
                        }
                        map.put(set2, conf);
                        associationRules.put(set1, map);
                        System.out.println("关联规则" + set1 + "->" + set2 + "  confidence: " + conf);
                    }
                }
            }
        }
    }


    /**
     * 求输入集合的真子集
     *
     * @param itemSet 集合
     * @return 真子集
     */
    public Set<Set<String>> properSubSet(Set<String> itemSet) {
        Set<Set<String>> subSets = new HashSet<>();
        List<String> list = new ArrayList<>(itemSet);
        int max = 1 << itemSet.size();
        for (int i = 0; i < max; i++) {
            int index = 0;
            int temp = i;
            Set<String> currentCharList = new HashSet<>();
            while (temp > 0) {
                if ((temp & 1) > 0) {
                    currentCharList.add(list.get(index));
                }
                temp >>= 1;
                index++;
            }
            subSets.add(currentCharList);
        }
        Set<String> empty = new HashSet<>();
        subSets.remove(itemSet);
        subSets.remove(empty);
        return subSets;
    }
}
