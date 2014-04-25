package com.sjtu.recommend.user;

import java.util.Map;

import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

public class CommonRecommenderModel {

	/*
	 * 
	 * 计算用户的序列模型与某个电影的相似度
	 * film为电影类
	 * seqMap为object与对应的权值
	 */
	public static double getUserSequenceSimiWithMovie(FilmObject film,Map<String, Double> seqMap){
		double rele=0;
		for(Pair p:film.links){
			if(seqMap.containsKey(p.object)){
				rele+=seqMap.get(p.object);
			}
		}
		return rele;
	}
}
