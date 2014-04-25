package com.sjtu.recommend.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmObject {
	public String nameurl;
	public List<Pair> links=new ArrayList<Pair>();
	public Map<String,Integer> linkNummap=new HashMap<String,Integer>();
	
	public void calculateLinkNum(){
		for(Pair p:links){
			if(linkNummap.containsKey(p.predicate)){
				linkNummap.put(p.predicate, linkNummap.get(p.predicate)+1);
			}else{
				linkNummap.put(p.predicate, 1);
			}
		}
	}

}
