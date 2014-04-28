package com.sjtu.recommend.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class SplitLearningAndTest {

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"newfile/new_2_rating"));
		
		FileWriter writer1 = new FileWriter("newfile/ratings_new_learning.dat", true);
		FileWriter writer2 = new FileWriter("newfile/ratings_new_testing.dat", true);
		BufferedWriter bw1 = new BufferedWriter(writer1);
		BufferedWriter bw2 = new BufferedWriter(writer2);
		
		
	
		
		
		String line = null;
		int i=0;
		while ((line = br.readLine()) != null) {
				i++;
				if(i%2==0){
					bw1.write(line+"\n");
				}else{
					bw2.write(line+"\n");
				}
				
		}
		br.close();
		bw1.close();
		writer1.close();
		bw2.close();
		writer2.close();
	}
}
