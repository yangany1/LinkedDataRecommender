package com.sjtu.recommend.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sjtu.recommend.utils.*;

/**
 * 删除Link的Object为非uri标识的 同时删除出现频率较小的predicate
 * 
 * @author luo
 * 
 */
public class DeleteLinkObjectIsString {

	public static double frefilter = 0.001;

	public static void main(String[] args) throws Exception {
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS);

		FileWriter writer = new FileWriter(
				CommomFunction.FILM_LINKS_DELETE_OBJECT_STRING, false);
		BufferedWriter bw = new BufferedWriter(writer);

//		BufferedReader br = new BufferedReader(new FileReader(
//				"files/sourceFolder/5000OutLinkFrequencyRaw"));

//		ArrayList<String> predicatesList = new ArrayList<String>();
//		String line = null;
//		while ((line = br.readLine()) != null) {
//			String predicate = line.split(",")[0];
//			double frequency=Double.parseDouble(line.split(",")[1]);
//			if(frequency>=frefilter){
//				predicatesList.add(predicate);
//			}
//		}

		for (FilmObject f : filmList) {
			Set<String> links = new HashSet<String>();
			for (Pair p : f.links) {
				if(!p.object.contains("http://")){
//				if (!p.object.contains("http://")||!predicatesList.contains(p.predicate)) {
					continue;
				} else {
					String s = "\"" + f.nameurl + "\",\"" + p.predicate
							+ "\",\"" + p.object + "\"\n";
					bw.write(s);
				}

			}
		}

		bw.close();
		writer.close();
	}
}
