package com.sjtu.recommend.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.CsvReader;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

/**
 * 分析出具有相似含义的谓词
 * @author luo
 *
 */
public class RelateAnalysis {
	

	public static void main(String[] args) throws Exception {
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList, CommomFunction.FILM_LINKS_DELETE_LOW_PREDICATE);
		
		Set<String> linkSet;
		Map<String, Integer> linkCode = new HashMap<String, Integer>();
		Map<Integer, String> linkCodeReverse = new HashMap<Integer, String>();
		BufferedReader br = new BufferedReader(new FileReader(
				CommomFunction.FILM_LINKS_PREDICATE_FILTER_LOW));
		String line = null;
		int i = 1;
		while ((line = br.readLine()) != null) {
			String name = line.split(",")[0];
			linkCode.put(name, i);
			linkCodeReverse.put(i, name);
			i++;
		}
		
		int b[][] = new int[100][100];
		for (i = 0; i < 100; i++)
			for (int j = 0; j < 100; j++)
				b[i][j] = 0;
		int c[][] = new int[100][100];
		for (i = 0; i < 100; i++)
			for (int j = 0; j < 100; j++)
				c[i][j] = 0;
		for (FilmObject f : filmList) {
			linkSet = new HashSet<String>();
			for (Pair p : f.links) {
				linkSet.add(p.predicate);
			}
			for (Pair p1 : f.links) {
				for (Pair p2 : f.links) {
					if ((!p1.predicate.equals(p2.predicate))) {
						if (linkCode.containsKey(p1.predicate)
								&& linkCode.containsKey(p2.predicate)) {
							boolean same = isSameLink(p1.object, p2.object);
							if (same) {
								c[linkCode.get(p1.predicate)][linkCode.get(p2.predicate)] += 1;
								c[linkCode.get(p2.predicate)][linkCode.get(p1.predicate)] += 1;
								// System.out.println(p1.link+","+p2.link+","+p1.subject+","+p2.subject);
							}

						}
					}
				}
			}

			for (String l1 : linkSet) {
				for (String l2 : linkSet) {
					if (!l1.equals(l2) && linkCode.containsKey(l1)
							&& linkCode.containsKey(l2)) {
						b[linkCode.get(l1)][linkCode.get(l2)] += 1;
						b[linkCode.get(l2)][linkCode.get(l1)] += 1;
					}
				}
			}
		}

		FileWriter writer = new FileWriter("files/predicate/sameMeaningPredicate", false);
		BufferedWriter bw = new BufferedWriter(writer);
		
		
		for (i = 1; i <= linkCode.size(); i++) {
			for (int j = i + 1; j <= linkCode.size(); j++) {
				double r = c[i][j] * 1.0 / b[i][j];
				if (r > 0.7)
//					bw.write(linkCodeReverse.get(i) + ","
//							+ linkCodeReverse.get(j) + "," + r+"\n");
				bw.write(linkCodeReverse.get(i) + ","
						+ linkCodeReverse.get(j) +"\n");
			}
		}
		bw.close();
		writer.close();

	}

	public static boolean isSameLink(String s1, String s2) {
		boolean flag = false;
			if (s1.equals(s2))
				flag = true;
		return flag;

	}

	private static String last(String inputString) {
		return inputString.substring(inputString.lastIndexOf("/") + 1,
				inputString.length());

	}
}
