package com.sjtu.recommend.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

public class ObjectFrequency {
	public static void main(String[] args) throws Exception {

		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS_DELETE_SAME_PREDICATE);
		
		
		Map<String, Integer> objectMap=new HashMap<String, Integer>();
		for(FilmObject f:filmList){
			for(Pair p:f.links){
				String object = p.object;
				if (objectMap.containsKey(object)) {
					objectMap.put(object, objectMap.get(object) + 1);
				} else {
					objectMap.put(object, 1);
				}
			}
		}
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(
				objectMap.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());

			}
		});

//		System.out.println(infoIds.size());
		for (int i = 0; i < infoIds.size(); i++) {
			String id = infoIds.get(i).getKey();
			int num = infoIds.get(i).getValue();
			System.out.println(id + "^" + String.format("%.4f", 1.0*num/CommomFunction.filmNum));
		}
	}
}
