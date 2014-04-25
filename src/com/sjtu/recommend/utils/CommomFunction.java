package com.sjtu.recommend.utils;

import java.io.FileReader;
import java.util.List;

public class CommomFunction {

	public static String FILM_LINKS="newfile/filmlink/MovieLenLinksFilterSame";
	public static String FILM_LINKS_DELETE_OBJECT_STRING="files/filmlink/5000OutLinks_DeleteStringLinks";
	public static String FILM_LINKS_PREDICATE_FREQUENCY="files/filmlink/5000OutLinksPredicateFrequency";
	public static String FILM_LINKS_DELETE_LOW_PREDICATE="files/filmlink/5000OutLinks_DeleteLowFrequency";
	public static String FILM_LINKS_PREDICATE_FILTER_LOW="files/filmlink/5000OutLinksPredicateFilterLow";
	
	
	public static String PREDICATE_SAME_MANNAL="files/predicate/sameMeaningPredicateMannal";
	
	public static String Filter_PREDICATE_SAME="files/predicate/FilterSameMeaningPredicate";
	
	public static String FILM_LINKS_DELETE_SAME_PREDICATE="files/filmlink/5000OutLinks_DeleteSamePredicate";
	
	public static int filmNum = 1582;
	public static double frefilter=0.002;
	
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
 	}
}
