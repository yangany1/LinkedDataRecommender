package com.sjtu.recommend.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

public class Crawler5000filmsLinks {

	public static void mainthread() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"sourceFile/5000FileName"));
		String line = null;
		int i=1;
		while ((line = br.readLine()) != null) {
			System.out.println(i++);
			getFilmlinksObject(line);
			getObjectlinksFilm(line);
		}
	}

	public static void getFilmlinksObject(String s) {
		s = s.substring(1, s.length() - 1);
		String queryString = URLEncoder.encode("select distinct <" + s
				+ "> ?op ?object where {<" + s + "> ?op ?object }");
		String url = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query="
				+ queryString + "&format=text%2Fcsv&timeout=30000&debug=on";
		sendHttpRequest(url,"newfile/filmlink/MovieLenLinks","\"callret-0\",\"op\",\"object\"\n");
	}

	public static void getObjectlinksFilm(String s) {
		s = s.substring(1, s.length() - 1);
		String queryString = URLEncoder.encode("select distinct  ?object ?op <"
				+ s + "> where { ?object ?op <" + s + "> }");
		String url = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query="
				+ queryString + "&format=text%2Fcsv&timeout=30000&debug=on";
		sendHttpRequest(url,"newfile/filmlink/MovieLenLinks","\"object\",\"op\",\"callret-2\"\n");
	}

	public static void sendHttpRequest(String url,String filename,String replaceString) {
		HttpClient httpclient = new HttpClient();
		httpclient.getHostConfiguration().setProxy("127.0.0.1", 8087);
		GetMethod getmethod = new GetMethod(url);
		try {
			httpclient.executeMethod(getmethod);
			String s = getmethod.getResponseBodyAsString();
			FileWriter writer = new FileWriter(filename, true);
			BufferedWriter bw = new BufferedWriter(writer);
			s = s.replace(replaceString, "");
			bw.write(s);
			System.out.println(s);
			bw.close();
			writer.close();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 释放
			getmethod.releaseConnection();
		}
	}

//	public static void main(String[] args) throws HttpException, IOException {
//		mainthread();
//	}

}
