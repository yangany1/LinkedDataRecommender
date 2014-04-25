package com.sjtu.recommend.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

/**
 * 删除出现频率较小的谓词的三元组
 * 
 * @author luo
 * 
 */
public class DeleteLowFrequencyPredicate {

	public static void main(String[] args) throws Exception {
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS_DELETE_OBJECT_STRING);
		BufferedReader br = new BufferedReader(new FileReader(
				CommomFunction.FILM_LINKS_PREDICATE_FREQUENCY));

		ArrayList<String> predicatesList = new ArrayList<String>();
		String line = null;
		while ((line = br.readLine()) != null) {
			String predicate = line.split(",")[0];
			double frequency = Double.parseDouble(line.split(",")[1]);
			if (frequency >= CommomFunction.frefilter) {
				predicatesList.add(predicate);
			}
		}

		FileWriter writer = new FileWriter(
				CommomFunction.FILM_LINKS_DELETE_LOW_PREDICATE, false);
		BufferedWriter bw = new BufferedWriter(writer);
		for (FilmObject f : filmList) {
			Set<String> links = new HashSet<String>();
			for (Pair p : f.links) {
				if ( !predicatesList.contains(p.predicate)) {
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
