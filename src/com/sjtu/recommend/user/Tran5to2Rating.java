package com.sjtu.recommend.user;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tran5to2Rating {

	public static void main(String[] args) throws Exception{
		Map<Integer,List<Rating>> userRatings=new HashMap<Integer,List<Rating>>();
		BufferedReader br = new BufferedReader(new FileReader(
				"files/data/ratings_new.dat"));
		
		FileWriter writer1 = new FileWriter("newfile/new_2_rating", true);
		BufferedWriter bw1 = new BufferedWriter(writer1);
		
		
		String line = null;
		int i=0;
		while ((line = br.readLine()) != null) {
			String[] list=line.split("::");
			int userid=Integer.parseInt(list[0]);
			int movieid=Integer.parseInt(list[1]);
			int rating=Integer.parseInt(list[2]);
			int time=Integer.parseInt(list[3]);
			if(userRatings.containsKey(userid)){
				List<Rating> rlist=userRatings.get(userid);
				rlist.add(new Rating(movieid, rating, time));
			}else {
				List<Rating> rList=new ArrayList<Rating>();
				rList.add(new Rating(movieid, rating, time));
				userRatings.put(userid, rList);
			}
		}
		br.close();
	
		
		
		for(int userid:userRatings.keySet()){
			double sum=0;
			for(Rating r:userRatings.get(userid)){
				sum+=r.rating;
			}
			sum=sum/userRatings.get(userid).size();
			for(Rating r:userRatings.get(userid)){
				if(r.rating>=sum){
					bw1.write(userid+"::"+r.movieid+"::"+r.rating+"::"+r.time+"\n");
				}
			}
			
		}
		bw1.close();
		writer1.close();
	}
}
