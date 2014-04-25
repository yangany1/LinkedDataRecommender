package com.sjtu.recommend.crawler;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.CsvReader;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;


public class FilteSamelink {
	public static void loadFilmObjectFromFile(List<FilmObject> filmList,String filename)
 			throws Exception {
 		CsvReader reader = new CsvReader(new FileReader(filename),',');
 		reader.readHeaders();
 		String[] headers = reader.getHeaders();
 		String lastName = "";
 		FilmObject film = null;
 		while(reader.readRecord()){
 			String newname=reader.get(headers[0]);
 			if(!newname.equals(lastName)){
 				if (film != null) {
 					filmList.add(film);
 				}
 				film = new FilmObject();
 				film.nameurl = newname;
 				film.links.add(new Pair(reader.get(headers[1]), reader.get(headers[2])));
 				lastName = newname;
 			}else {
 				film.links.add(new Pair(reader.get(headers[1]), reader.get(headers[2])));
 			}
 		}
 		filmList.add(film);
 		System.out.println(filmList.size());
 	}
	
	public static void main(String[] args) throws Exception{
		System.out.println("start");
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,"newfile/filmlink/MovieLenLinks");
		Set<String> plist;
		FileWriter writer = new FileWriter("newfile/filmlink/MovieLenLinksFilterSame", true);
		BufferedWriter bw = new BufferedWriter(writer);
		for(FilmObject f:filmList){
			plist=new HashSet<String>();
			for(Pair p:f.links){
				String other="\""+p.predicate+"\",\""+p.object+"\"";
				plist.add(other);
			}
			for(String s:plist){
				String all="\""+f.nameurl+"\","+s+"\n";
				bw.write(all);
			}
		}
		bw.close();
		writer.close();
	}
}
