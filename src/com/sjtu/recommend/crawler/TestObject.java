package com.sjtu.recommend.crawler;

import java.util.ArrayList;
import java.util.List;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

public class TestObject {

	public static void main(String[] args) throws Exception {
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,"newfile/filmlink/MovieLenLinksFilterSame");
		System.out.println("before="+filmList.size());
		int i=0;
		for(FilmObject f:filmList){
//			if(f.links.size()>50){
				System.out.println(f.nameurl);
//				for(Pair p:f.links){
//					System.out.println(p.predicate+","+p.object);
//				}
//				i++;
//				System.out.println();
//			}
		}
//		System.out.println(i);
	}
}
