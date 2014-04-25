package com.sjtu.recommend.other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sjtu.recommend.utils.CommomFunction;

/**
 * 把没有的电影的数据删除
 * @author luo
 *
 */
public class FilterRatingsData {

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

	public static void loadCodes(List<Integer> codes) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"files/data/movielencodeMap"));

		String line = null;
		while ((line = br.readLine()) != null) {
			int num = Integer.parseInt(line.split(",")[0]);
			codes.add(num);
		}
		br.close();
	}
	
	public static void main(String[] args) throws Exception{
		List<Integer> codes=new ArrayList<Integer>();
		loadCodes(codes);
		
		FileWriter writer = new FileWriter(
				"files/data/ratings_new.dat", false);
		BufferedWriter bw = new BufferedWriter(writer);
		
		BufferedReader br = new BufferedReader(new FileReader(
				"files/data/ratings.dat"));
		String line = null;
		while ((line = br.readLine()) != null) {
			int code =Integer.parseInt( line.split("::")[1]);
			if(codes.contains(code)){
				bw.write(line+"\n");
			}
		}
		bw.close();
		br.close();
	}
	
}
