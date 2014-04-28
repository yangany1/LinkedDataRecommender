package com.sjtu.recommend.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;


public class LDSD {
	public static double pfilter = 0.1;
	public static String resultCoding = "files/ldsd/filmcode";
	public static String resultFile = "files/ldsd/resultFile";

	public static List<FilmObject> filmInputList;
	public static Map<String, FilmObject> inFilmMap;
	public static Map<String, Integer> filmCode;

	public static FilmObject getInputFilmFromList(String filmName) {
		for (FilmObject f : filmInputList) {
			if (f.nameurl.equals(filmName)) {
				return f;
			}
		}
		return null;
	}
	// 获得object的频率
	public static void LoadPredicateFrequency(
			Map<String, Double> predicateFrequency) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					"files/predicate/predicate_frequency"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				String[] array = line.split(",");
				String object = array[0];
				double fre = Double.parseDouble(array[1]);
				if (fre >= pfilter)
					predicateFrequency.put(object, fre);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		Map<String, Double> pFrequency = new HashMap<String, Double>();// object的频率
		LoadPredicateFrequency(pFrequency);
		System.out.println("start");
		long time1 = System.currentTimeMillis();
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS_DELETE_SAME_PREDICATE);
		

		inFilmMap = new HashMap<String, FilmObject>();

		filmCode = new HashMap<String, Integer>();
		int codenum = 1;
		FileWriter writer = new FileWriter(resultCoding, true);
		BufferedWriter bw = new BufferedWriter(writer);

		for (FilmObject f : filmList) {
			f.calculateLinkNum();
			filmCode.put(f.nameurl, codenum);
			System.out.println(f.nameurl + " " + codenum);
			bw.write(f.nameurl + " " + codenum + "\n");
			codenum++;
		}

		bw.close();
		writer.close();

		int filmNum = codenum--;
		float[][] result = new float[filmNum + 1][filmNum + 1];
		writer = new FileWriter(resultFile, true);
		bw = new BufferedWriter(writer);

		for (int i = 0; i < filmList.size() - 1; i++) {
			for (int j = i + 1; j < filmList.size(); j++) {
				FilmObject film1 = filmList.get(i);
				FilmObject film2 = filmList.get(j);

				if (film1.nameurl.equals(film2.nameurl))
					continue;
				float rele = calculateLDSD(film1, film2,pFrequency);
				int film1Code = filmCode.get(film1.nameurl);
				int film2Code = filmCode.get(film2.nameurl);
				result[film1Code][film2Code] = rele;
				bw.write(film1Code + "," + film2Code + ","
						+ String.format("%.4f", rele) + "\n");
			}
		}
		bw.close();
		writer.close();
		System.out.println("end");
		long time2 = System.currentTimeMillis();
		System.out.println("time consume=" + (time2 - time1) / 1000);
	}

	public static float calculateLDSD(FilmObject film1, FilmObject film2,Map<String, Double> pFrequency) {
		float all = (float) (LDSDiwo(film1, film2 ,pFrequency) );
		// double all=LDSDiwi(film1, film2);
		all = 1 / (1 + all);
		return all;
	}

	// 得到某个对象某种link的个数
	public static int getFilmlinksNumber(FilmObject film1, String link) {
		return film1.linkNummap.get(link);
	}

	


	// 计算间接权值距离out
	public static float LDSDiwo(FilmObject film1, FilmObject film2,Map<String, Double> pFrequency) {
		float sum = 0;
		for (Pair p1 : film1.links) {
			for (Pair p2 : film2.links) {
				if (p1.predicate.equals(p2.predicate) && p1.object.equals(p2.object)) {
					int linksNum = getFilmlinksNumber(film1, p1.predicate);
					if (pFrequency.containsKey(p1.predicate))
						sum = (float) (sum + 1 / (1 + Math.log(linksNum)));
				}
			}
		}
		return sum;
	}

	
	
	

	private static String last(String inputString) {
		return inputString.substring(inputString.lastIndexOf("/") + 1,
				inputString.length());

	}
}
