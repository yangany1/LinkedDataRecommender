package com.sjtu.recommend.crawler;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.sjtu.recommend.utils.CsvReader;

public class LoadLinks {
	public static void getResourceURL(List<String> rlist) throws IOException {
		CsvReader reader = new CsvReader(new FileReader(
				"newfile/sourceFolder/mapping-movielens-dbpedia-1M.csv"), ',');
		reader.readHeaders();
		String[] headers = reader.getHeaders();
		while (reader.readRecord()) {
			String name = reader.get(headers[1]);
			if (name.contains("http://dbpedia.org/resource/"))
				rlist.add(reader.get(headers[1]));
		}

	}

	public static void main(String[] args) throws IOException {
		List<String> rlist = new ArrayList<String>();
		getResourceURL(rlist);
		System.out.println(rlist.size());
		for (String s : rlist) {
			System.out.println(s);
			getFilmlinksObject(s);
		}

	}

	public static void getFilmlinksObject(String s) {
//		s = s.substring(1, s.length() - 1);
		String queryString = URLEncoder.encode("select distinct <" + s
				+ "> ?op ?object where {<" + s + "> ?op ?object }");
		String url = "http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query="
				+ queryString + "&format=text%2Fcsv&timeout=30000&debug=on";
//		System.out.println(url);
		sendHttpRequest(url, "newfile/filmlink/MovieLenLinks",
				"\"callret-0\",\"op\",\"object\"\n");
	}

	public static void sendHttpRequest(String url, String filename,
			String replaceString) {
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

}
