package com.sjtu.recommend.evalution;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.mysql.jdbc.log.Log;
import com.sjtu.recommend.user.Rating;
import com.sjtu.recommend.user.UserRatingObject;
import com.sjtu.recommend.user.UserSequenceModel;
import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;

public class UserRelatedModel {
	public static double pfilter = 0.1;

	public static Map<String, Double> pFrequency;
	
	public static Map<String, Double> predicateWeight;
	public static Map<String, Double> sortMapByValue(Map<String, Double> oriMap) {
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>(
					oriMap.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<String, Double>>() {
						@Override
						public int compare(Entry<String, Double> o1,
								Entry<String, Double> o2) {
							if (o2.getValue() < o1.getValue())
								return 1;
							else if (o2.getValue() > o1.getValue())
								return -1;
							else {
								return 0;
							}
						}
					});
			Iterator<Map.Entry<String, Double>> iter = entryList.iterator();
			Map.Entry<String, Double> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}

	// 从数据库获得用户的历史记录
	public static List<Rating> getUserRatingsFromMysql(int userid,String tablename) {
		List<Rating> rList = new ArrayList<Rating>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager
					.getConnection(
							"jdbc:mysql://localhost/paper2?useUnicode=true&characterEncoding=utf-8",
							"root", "luo");
			String sql = "select * from " + tablename + " where userid="
					+ userid + " order by time asc";  // 查询数据的sql语句
			Statement st = (Statement) conn.createStatement(); // 创建用于执行静态sql语句的Statement对象，st属局部变量
			ResultSet rs = st.executeQuery(sql); // 执行sql查询语句，返回查询数据的结果集
			// System.out.println("最后的查询结果为：");
			while (rs.next()) { // 判断是否还有下一个数据
				Rating r = new Rating();
				r.movieid = rs.getInt("movieid");
				r.rating = rs.getInt("rating");
				r.time = rs.getInt("time");
				rList.add(r);
			}
			conn.close(); // 关闭数据库连接

		} catch (Exception e) {
			System.out.println("查询数据失败");
		}
		return rList;
	}

	public static UserRatingObject getUserRatingObject(int userid,String tablename) {
		UserRatingObject userRating = new UserRatingObject();
		userRating.userid = userid;
		userRating.userRatings = getUserRatingsFromMysql(userid, tablename);
		return userRating;

	}

	public static void printMap(Map<String, Double> oriMap) {
		for (String s : oriMap.keySet()) {
			String id = s;
			double fre = oriMap.get(s);
			System.out.println(id + "," + fre);

		}
	}
	/**
	 * 获得所有资源对象和编码
	 * 
	 * @param filmList
	 * @param filmCode
	 * @throws Exception
	 */
	public static void loadFilmListandFilmCode(List<FilmObject> filmList,
			Map<String, Integer> filmCode) throws Exception {
		CommomFunction.loadFilmObjectFromFile(filmList,
				CommomFunction.FILM_LINKS_DELETE_SAME_PREDICATE);

		// filmCode 用于根据资源名得到相应的资源类
		for (int i = 0; i < filmList.size(); i++) {

			FilmObject f = filmList.get(i);
			f.calculateLinkNum();
			filmCode.put(f.nameurl, i);
		}
	}

	public static void loadPredicate(List<String> plist) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"files/predicate/predicate_frequency"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String predicate = line.split(",")[0];
			double frequency = Double.parseDouble(line.split(",")[1]);
			if (frequency >= pfilter) {
				plist.add(predicate);
			}
		}
		br.close();
	}

	public static Set<String> getSamePredicate(FilmObject a, FilmObject b) {
		Set<String> plist = new HashSet<String>();
		for (Pair p1 : a.links) {
			for (Pair p2 : b.links) {
				if (p1.predicate.equals(p2.predicate)
						&& p1.object.equals(p2.object)) {
					plist.add(p1.predicate);
//					System.out.println(p1.predicate + "," + p1.object);
				}
			}
		}

		return plist;
	}

	// 获得object的频率
	public static void LoadPredicateFrequency(
			Map<String, Double> predicateFrequency) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					"files/predicate/predicate_frequency"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				String[] array = line.split(",");
				String object = array[0];
				double fre = Double.parseDouble(array[1]);
				if (fre >= pfilter)
					predicateFrequency.put(object, fre);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		List<FilmObject> filmList = new ArrayList<FilmObject>();
		Map<String, Integer> filmCode = new HashMap<String, Integer>();
		loadFilmListandFilmCode(filmList, filmCode);

		double totalMRR = 0;
		int totalNum = 0;

		//谓词列表
		List<String> predicateList = new ArrayList<String>();
		loadPredicate(predicateList);

		pFrequency = new HashMap<String, Double>();// 谓词的频率
		LoadPredicateFrequency(pFrequency);
		
		

		Map<Integer, String> movieCode = new HashMap<Integer, String>();
		UserSequenceModel.movieLenCode(movieCode);

		for (int k = CommomFunction.testStart; k <=CommomFunction.testEnd; k++) {
//			System.out.println(k);
			int userid = k;
			UserRatingObject userRating = getUserRatingObject(userid,"rating");

			UserSequenceModel.meanRating(userRating);
			if (userRating.userRatings.size() < 20)
				continue;

			totalNum++;
			// System.out.println("用户id=" + userid);
//			UserRatingObject userRatingLearning = new UserRatingObject();
//			UserRatingObject userRatingTesting = new UserRatingObject();
//			UserRatingObject userRatingLearning =LDSDModel.getUserRatingLearningObject(userid);
//			UserRatingObject userRatingTesting = LDSDModel.getUserRatingTestingObject(userid);
		
			UserRatingObject userRatingLearning = getUserRatingObject(userid,"rating_learning");
			UserRatingObject userRatingTesting = getUserRatingObject(userid,"rating_test");
			// 将用户打分分为学习集合和测试集合
//			UserSequenceModel.splitUserRating(userRating, userRatingLearning,
//					userRatingTesting);
			
			//当前用户的谓词权重
			predicateWeight = new HashMap<String, Double>();
			for (String s : predicateList) {
				predicateWeight.put(s, 1.0);
			}
			List<Rating> uRateList = userRatingLearning.userRatings;
			Set<Integer> userHistory = new HashSet<Integer>();
			
			for (int i = 0; i < uRateList.size() - 1; i++) {
				
				Rating before = uRateList.get(i);
				Rating after = uRateList.get(i + 1);
				
				userHistory.add(before.movieid);
				userHistory.add(after.movieid);
				
				String bname = movieCode.get(before.movieid);
				String aname = movieCode.get(after.movieid);

				FilmObject bObject = filmList.get(filmCode.get(bname));
				FilmObject aObject = filmList.get(filmCode.get(aname));

				Set<String> samePredicate = getSamePredicate(bObject, aObject);
				for (String p : samePredicate) {
					if (predicateWeight.containsKey(p))
						predicateWeight.put(p, predicateWeight.get(p) + 1);
				}

			}
//			System.out.println("learn size="+userHistory.size());
//			System.out.println();
			for (String p : predicateWeight.keySet()) {
				double fre = pFrequency.get(p);
				double number=predicateWeight.get(p);
				if(number>=1.999)
				predicateWeight.put(p,
						1+number * Math.log(1 / fre));
				else {
					predicateWeight.put(p,
							1.0);
				}
			}

//			printMap(predicateWeight);
			
			Map<String, Double> allResult = new HashMap<String, Double>();
			for (int i = 0; i < filmList.size(); i++) {
				FilmObject f = filmList.get(i);
				double rele = 0;
				for (Integer movie : userHistory) {
					int mid=filmCode.get(movieCode
							.get(movie));
					FilmObject f2 = filmList.get(mid);
					double r = calculateLDSD(f, f2);
					rele += r;

				}
				// System.out.println(f.nameurl+","+rele);
				allResult.put(f.nameurl, rele);
			}

			Map<String, Double> sortResult = sortMapByValue(allResult);
			Map<String, Integer> sortNum = new HashMap<String, Integer>();
			int i = 0;
			for (String s : sortResult.keySet()) {
				i++;
				sortNum.put(s, i);
			}

			double MRR = 0;

//			System.out.println("test size="+userRatingTesting.userRatings.size());
			for (Rating r : userRatingTesting.userRatings) {
				String name = movieCode.get(r.movieid);
				// System.out.println(name + "," + sortNum.get(name));
				MRR += 1.0 / sortNum.get(name);

			}
			 System.out.println("用户" + userid + "的MRR值为" + MRR);
			totalMRR += MRR;
			// System.out.println();
		}

		 System.out.println(" average MRR=" + totalMRR / totalNum);

	}

	public static float calculateLDSD(FilmObject film1, FilmObject film2) {
		float all = (float) (LDSDiwo(film1, film2));
		// double all=LDSDiwi(film1, film2);
		all = 1 / (1 + all);
		return all;
	}

	// 得到某个对象某种link的个数
	public static int getFilmlinksNumber(FilmObject film1, String link) {
		return film1.linkNummap.get(link);
	}

	// 计算间接权值距离out
	public static float LDSDiwo(FilmObject film1, FilmObject film2) {
		float sum = 0;
		for (Pair p1 : film1.links) {
			for (Pair p2 : film2.links) {
				if (p1.predicate.equals(p2.predicate)
						&& p1.object.equals(p2.object)) {
					int linksNum = getFilmlinksNumber(film1, p1.predicate);
					if (predicateWeight.containsKey(p1.predicate))
//						sum = (float) (sum + 1 / (1 + Math.log(linksNum)));
						sum = (float) (sum + predicateWeight.get(p1.predicate)
								* 1 / (1 + Math.log(linksNum)));
				}
			}
		}
		return sum;
	}
}
