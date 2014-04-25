package com.sjtu.recommend.other;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.CsvReader;

public class MapMovielenToDBpedia {

	public static void movieLenCode(Map<String, Integer> map)
			throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"files/data/movies.dat"));

		String line = null;
		while ((line = br.readLine()) != null) {
			String name = line.split("::")[1];
			int num = Integer.parseInt(line.split("::")[0]);
			map.put(name, num);
		}
		br.close();
	}

	public static void LoadDBpediaNames (List<String> list) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"files/filmlink/filmnames"));

		String line = null;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
	}

	public static void getMapMovielenDBpedia(Map<String,Integer> map)
			throws IOException {
		CsvReader reader = new CsvReader(new FileReader(
				"newfile/sourceFolder/mapping-movielens-dbpedia-1M.csv"), ',');
		reader.readHeaders();
		String[] headers = reader.getHeaders();
		while (reader.readRecord()) {
			int code = Integer.parseInt(reader.get(headers[0]));
			String name = reader.get(headers[1]);
			map.put(name, code);
		}

	}

	public static void main(String[] args) throws Exception {
		Map<String, Integer> movielenMap = new HashMap<String, Integer>();
		getMapMovielenDBpedia(movielenMap);
		System.out.println(movielenMap.size());
		
		Map<String, Integer> movielenMapNew = new HashMap<String, Integer>();
		
		List<String> DBpediaNames=new ArrayList<String>();
		LoadDBpediaNames(DBpediaNames);
		System.out.println(DBpediaNames.size());
		
		for(String s:DBpediaNames){
			if(movielenMap.containsKey(s)){
				movielenMapNew.put(s, movielenMap.get(s));
			}
		}
		
		
		System.out.println("new size="+movielenMapNew.size());
		for(String s:movielenMapNew.keySet()){
			System.out.println(movielenMapNew.get(s)+","+s);
		}
	}
}
