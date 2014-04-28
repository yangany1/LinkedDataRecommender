package com.sjtu.recommend.other;

import java.util.ArrayList;
import java.util.List;

import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

public class FindSame {

	public static FilmObject FindByUrl(List<FilmObject> filmList, String url) {
		FilmObject f = null;
		for (FilmObject fo : filmList) {
			if (fo.nameurl.equals(url)) {
				f = fo;
				break;
			}
		}
		return f;
	}

	public static void main(String[] args) throws Exception {
		String url1 = "http://dbpedia.org/resource/Girl_in_the_Cadillac";
		String url2 = "http://dbpedia.org/resource/Dear_God";
		List<FilmObject> filmList = new ArrayList<FilmObject>();
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS_DELETE_SAME_PREDICATE);
		FilmObject f1 = FindByUrl(filmList, url1);
		FilmObject f2 = FindByUrl(filmList, url2);
		for (Pair p1 : f1.links) {
			for (Pair p2 : f2.links) {
				if (p1.predicate.equals(p2.predicate)
						&& p1.object.equals(p2.object)) {
					System.out.println(p1.predicate + "," + p1.object);
				}
			}
			// System.out.println(url1);
			// for(Pair p1:f1.links)
			// System.out.println(p1.predicate+","+p1.object);
			// for(Pair p2:f2.links)
			// System.out.println(p2.predicate+","+p2.object);
			// }

		}

	}
}
