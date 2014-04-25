package com.sjtu.recommend.user;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

public class UserTest {

	public static void loadUserHistoryFromFile(List<String> userHistory) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("files/preference/user1"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				userHistory.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void LoadObjectFrequency(Map<String, Double> objectFrequency) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					"files/object/objectFrequency"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				String[] array = line.split("\\^");
				String object = array[0];
				double fre = Double.parseDouble(array[1]);
				objectFrequency.put(object, fre);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS_DELETE_SAME_PREDICATE);

		List<String> userHistory = new ArrayList<String>();
		loadUserHistoryFromFile(userHistory);

		// filmCode 用于根据资源名得到相应的资源类
		Map<String, Integer> filmCode = new HashMap<String, Integer>();

		for (int i = 0; i < filmList.size(); i++) {
			FilmObject f = filmList.get(i);
			filmCode.put(f.nameurl, i);
		}

		Map<String, Double> objectFrequency = new HashMap<String, Double>();// object的频率
		LoadObjectFrequency(objectFrequency);

		Map<String, Integer> objectCount = new HashMap<String, Integer>();

		for (String resource : userHistory) {
//			resource = "http://dbpedia.org/resource/" + resource;
			FilmObject f = filmList.get(filmCode.get(resource));
			for (Pair p : f.links) {
				String object = p.object;
				if (objectCount.containsKey(object)) {
					objectCount.put(object, objectCount.get(object) + 1);
				} else {
					objectCount.put(object, 1);
				}
			}
		}

		Map<String, Double> objectValue = new HashMap<String, Double>();
		for (String object : objectCount.keySet()) {
			int number = objectCount.get(object);
			double fre = objectFrequency.get(object);
			if (number >= 2) {
				objectValue.put(object, number *Math.log( 1.0 / fre));
			}
		}

		List<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(
				objectValue.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				if (o2.getValue() > o1.getValue())
					return 1;
				else if (o2.getValue() < o1.getValue())
					return -1;
				else {
					return 0;
				}

			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			String id = infoIds.get(i).getKey();
			double fre = infoIds.get(i).getValue();
			System.out.println(id + "," + fre);

		}

	}
}
