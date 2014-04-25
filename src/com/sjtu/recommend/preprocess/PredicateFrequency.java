package com.sjtu.recommend.preprocess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

/**
 * 计算谓词出现的频率
 * 
 * @author luo
 * 
 */
public class PredicateFrequency {

	public static void main(String[] args) throws Exception {
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS_DELETE_OBJECT_STRING);

		
		FileWriter writer = new FileWriter(
				CommomFunction.FILM_LINKS_PREDICATE_FREQUENCY, false);
		BufferedWriter bw = new BufferedWriter(writer);
		
		Map<String, Integer> linkMap = new HashMap<String, Integer>();
		for (FilmObject f : filmList) {
			Set<String> links = new HashSet<String>();
			for (Pair p : f.links) {
				if (links.contains(p.predicate)) {
					continue;
				} else {
					links.add(p.predicate);
					if (linkMap.keySet().contains(p.predicate)) {
						linkMap.put(p.predicate, linkMap.get(p.predicate) + 1);
					} else {
						linkMap.put(p.predicate, 1);

					}
				}

			}
		}
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				linkMap.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());

			}
		});

		System.out.println(infoIds.size());
		for (int i = 0; i < infoIds.size(); i++) {
			String id = infoIds.get(i).getKey();
			int num = infoIds.get(i).getValue();
			double freqency = num * 1.0 / CommomFunction.filmNum;
			if (freqency >= 0) {
				// if(id.contains("http://dbpedia.org/ontology/"))
				System.out.println(id + "," + String.format("%.4f", freqency));
				bw.write(id + "," + String.format("%.4f", freqency)+"\n");
				// System.out.println(String.format("%.4f", freqency));
			}
		}
		bw.close();
		writer.close();
	}

}
