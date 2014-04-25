package com.sjtu.recommend.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjtu.recommend.utils.CommomFunction;

/**
 * 生成没有重复含义的谓词
 * @author luo
 *
 */
public class PredicateWithOutSame {

	public static void main(String[] args) throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				CommomFunction.FILM_LINKS_PREDICATE_FILTER_LOW));

		Map<String, Double> predicatesList=new HashMap<String, Double>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String predicate = line.split(",")[0];
			double frequency = Double.parseDouble(line.split(",")[1]);
			predicatesList.put(predicate, frequency);
		}
		
		
		BufferedReader br2 = new BufferedReader(new FileReader(
				CommomFunction.PREDICATE_SAME_MANNAL));

		while ((line = br2.readLine()) != null) {
			String[] ps=line.split(",");
			double maxFre=0;
			String maxPredicate=null;
			for(int i=0;i<ps.length;i++){
				double fre=predicatesList.get(ps[i]);
				if(fre>maxFre){
					maxFre=fre;
					maxPredicate=ps[i];
				}
			}
			
			for(int i=0;i<ps.length;i++){
				if(!ps[i].equals(maxPredicate)){
					predicatesList.remove(ps[i]);
				}
			}
 		}
		
		FileWriter writer = new FileWriter(
				CommomFunction.Filter_PREDICATE_SAME, false);
		BufferedWriter bw = new BufferedWriter(writer);
		
		List<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(
				predicatesList.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				if(o2.getValue()>o1.getValue())
					return 1;
				else if(o2.getValue()<o1.getValue())
					return -1;
				else {
					return 0;
				}

			}
		});

		System.out.println(infoIds.size());
		for (int i = 0; i < infoIds.size(); i++) {
			String id = infoIds.get(i).getKey();
			double freqency = infoIds.get(i).getValue();
			bw.write(id + "," + String.format("%.4f", freqency)+"\n");
		}
		bw.close();
		writer.close();
	}
}
