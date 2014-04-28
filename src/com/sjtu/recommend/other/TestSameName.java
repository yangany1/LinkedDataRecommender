package com.sjtu.recommend.other;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TestSameName {
	public static void main(String[] args)throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"files/ldsd/filmcode"));

		String line = null;
		List<String> names=new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			String name = line.split(" ")[0];
		
			if(names.contains(name))
				System.out.println(name);
			else {
				names.add(name);
			}
		}
		System.out.println(names.size());
		br.close();
	}
}
