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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sjtu.recommend.user.Rating;
import com.sjtu.recommend.user.UserRatingObject;
import com.sjtu.recommend.user.UserSequenceModel;
import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;
/**
 * 根据电影的受欢迎程度进行排序推荐
 * 根据电影被用户评价的次数
 * @author luo
 *
 */
public class ScoreModel_Recall {
	public static int filmNumber=1582;
	// 从数据库获得用户的历史记录
	public static List<Rating> getUserRatingsFromMysql(int userid) {
		List<Rating> rList = new ArrayList<Rating>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager
					.getConnection(
							"jdbc:mysql://localhost/paper2?useUnicode=true&characterEncoding=utf-8",
							"root", "luo");
			String sql = "select * from new_rating_test where userid=" + userid
					+ " order by time asc"; // 查询数据的sql语句
			Statement st = (Statement) conn.createStatement(); // 创建用于执行静态sql语句的Statement对象，st属局部变量
			ResultSet rs = st.executeQuery(sql); // 执行sql查询语句，返回查询数据的结果集
//			System.out.println("最后的查询结果为：");
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

	public static UserRatingObject getUserRatingObject(int userid) {
		UserRatingObject userRating = new UserRatingObject();
		userRating.userid = userid;
		userRating.userRatings = getUserRatingsFromMysql(userid);
		return userRating;

	}

	// 获得object的频率
	public static void LoadObjectFrequency(Map<String, Double> objectFrequency) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					"files/object/objectFrequency"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String line = null;
		try {
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				String[] array = line.split("\\^");
				String object = array[0];
				double fre = Double.parseDouble(array[1]);
				objectFrequency.put(object, fre);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 电影编号和名字对应的map
	public static void movieLenCode(Map<Integer, String> map)
			throws NumberFormatException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"files/data/movielencodeMap"));

		String line = null;
		while ((line = br.readLine()) != null) {
			String name = line.split(",")[1];
			int num = Integer.parseInt(line.split(",")[0]);
			map.put(num, name);
		}
		br.close();
	}

	public static void SortPrint(Map<String, Double> objectValue) {
		List<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(
				objectValue.entrySet());

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				if (o2.getValue() > o1.getValue())
					return 1;
				else if (o2.getValue() < o1.getValue())
					return -1;
				else {
					return 0;
				}

			}
		});

		for (int i = 0; i < infoIds.size(); i++) {
			String id = infoIds.get(i).getKey();
			double fre = infoIds.get(i).getValue();
			System.out.println(id + "," + fre);

		}
	}

	/**
	 * Map按照值排序
	 * 
	 * @param oriMap
	 * @return
	 */
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
							if (o2.getValue() > o1.getValue())
								return 1;
							else if (o2.getValue() < o1.getValue())
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
	
	
	
	
	public static Map<Integer, Integer> sortMapByIntegerValue(Map<Integer, Integer> oriMap) {
		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Map.Entry<Integer, Integer>> entryList = new ArrayList<Map.Entry<Integer, Integer>>(
					oriMap.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<Integer, Integer>>() {
						@Override
						public int compare(Entry<Integer, Integer> o1,
								Entry<Integer, Integer> o2) {
							if (o2.getValue() > o1.getValue())
								return 1;
							else if (o2.getValue() < o1.getValue())
								return -1;
							else {
								return 0;
							}
						}
					});
			Iterator<Map.Entry<Integer, Integer>> iter = entryList.iterator();
			Map.Entry<Integer, Integer> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}
	
	public static Map<Integer, Double> sortMapByDoubleValue(Map<Integer, Double> oriMap) {
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
		if (oriMap != null && !oriMap.isEmpty()) {
			List<Map.Entry<Integer, Double>> entryList = new ArrayList<Map.Entry<Integer, Double>>(
					oriMap.entrySet());
			Collections.sort(entryList,
					new Comparator<Map.Entry<Integer, Double>>() {
						@Override
						public int compare(Entry<Integer, Double> o1,
								Entry<Integer, Double> o2) {
							if (o2.getValue() > o1.getValue())
								return 1;
							else if (o2.getValue() < o1.getValue())
								return -1;
							else {
								return 0;
							}
						}
					});
			Iterator<Map.Entry<Integer, Double>> iter = entryList.iterator();
			Map.Entry<Integer, Double> tmpEntry = null;
			while (iter.hasNext()) {
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}

//	public static Map<String, Double> getTopNMap(Map<String, Double> oriMap) {
//		Map<String, Double> newMap = new LinkedHashMap<String, Double>();
//		Iterator<String> it = oriMap.keySet().iterator();
//		int i = 0;
//		for (String s : oriMap.keySet()) {
//			String id = s;
//			double fre = oriMap.get(s);
//			newMap.put(id, fre);
//			i++;
//			if (i >= n)
//				break;
//
//		}
//		return newMap;
//	}

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
			filmCode.put(f.nameurl, i);
		}
	}

	/**
	 * 用户的打分分为两组，一组学习，一组测试
	 * 
	 * @param args
	 * @throws Exception
	 */

	public static void splitUserRating(UserRatingObject userRating,
			UserRatingObject userRatingLearning,
			UserRatingObject userRatingTesting) {
		int size = userRating.userRatings.size();
		int number = (int) (size /2) ;
		for (int i = 0; i < number; i++) {
			userRatingLearning.userRatings.add(userRating.userRatings.get(i));
		}
		for (int j = number; j < size; j++) {
			userRatingTesting.userRatings.add(userRating.userRatings.get(j));
		}
	}

	/**
	 * 将1-5 打分转化为用户浏览历史
	 * 
	 * @param userRating
	 */
	public static void meanRating(UserRatingObject userRating) {
		double meanRating = 0;
		for (int i = 0; i < userRating.userRatings.size(); i++) {
			meanRating += userRating.userRatings.get(i).rating;
		}
		meanRating = meanRating / userRating.userRatings.size();
		for (Iterator it = userRating.userRatings.iterator(); it.hasNext();) {
			double rating = ((Rating) it.next()).rating;
			if (rating < meanRating) {
				it.remove();
			}
		}
	}

	public static void loadLDSDMovieMap(Map<String, Integer> filmCode,Map<Integer, String> filmCode2) throws NumberFormatException, IOException{
	
		BufferedReader br = new BufferedReader(new FileReader(
				"files/ldsd/filmcode"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String name = line.split(" ")[0];
			int number = Integer.parseInt(line.split(" ")[1]);
			filmCode.put(name, number);
			filmCode2.put(number, name);
		}
		br.close();
	}
	
	
	public static Map<Integer, Double> loadMoviePopular() throws Exception{
		Map<Integer, Integer> pMap=new HashMap<Integer, Integer>();
		Map<Integer, Integer> numberMap=new HashMap<Integer, Integer>();
		Map<Integer, Double> scoreMap=new HashMap<Integer, Double>();
		BufferedReader br = new BufferedReader(new FileReader(
				"files/data/ratings_new_learning.dat"));
		String line = null;
		while ((line = br.readLine()) != null) {
			int movie =Integer.parseInt( line.split("::")[1]);
			int rating = Integer.parseInt(line.split("::")[2]);
			
			if(pMap.containsKey(movie)){
				pMap.put(movie, pMap.get(movie)+rating);
				numberMap.put(movie, numberMap.get(movie)+1);
			}else{
				pMap.put(movie, rating);
				numberMap.put(movie, 1);
			}
		}
		for(Integer movie:pMap.keySet())
		{
			double ave=1.0*pMap.get(movie)/numberMap.get(movie);
			scoreMap.put(movie, ave);
		}
		
		br.close();
		return scoreMap;
	}
	public static void main(String[] args) throws Exception {

		double totalMRR=0;
		int totalNum=0;
		Map<Integer, String> movieCode = new HashMap<Integer, String>();
		UserSequenceModel.movieLenCode(movieCode);

		//电影的编号与对应的popular值
		Map<Integer, Double> popularMap=loadMoviePopular();
		double precisionSum = 0;
		double recallSum=0;
		for(Integer i:popularMap.keySet()){
			System.out.println(i+","+popularMap.get(i));
		}
		
		for (int k = 1; k <= 50; k++) {
			int userid = k;

			UserRatingObject userRating = HybridModel.getUserRatingObject(userid,"rating");

			UserSequenceModel.meanRating(userRating);
			if (userRating.userRatings.size() < 20)
				continue;


			totalNum++;
//			System.out.println("用户id=" + userid);
//			UserRatingObject userRatingLearning = new UserRatingObject();
//			UserRatingObject userRatingTesting = new UserRatingObject();
			UserRatingObject userRatingTesting =HybridModel.getUserRatingObject(userid,
					"rating_test");
			// 将用户打分分为学习集合和测试集合
//			splitUserRating(userRating, userRatingLearning, userRatingTesting);
			
			Map<Integer, Double> sortResult = sortMapByDoubleValue(popularMap);
			Map<Integer, Integer> sortNum = new HashMap<Integer, Integer>();

			// System.out.println("全部电影的排序结果为：");
			int i = 0;
			for (Integer s : sortResult.keySet()) {
				// System.out.println(s+","+sortResult.get(s));
				i++;
				sortNum.put(s, i);
			}

			double precision = 0;
			double recall=0;
			
			int rightRecommendNumber=0;
			for (Rating r : userRatingTesting.userRatings) {
//				String name = movieCode.get(r.movieid);
				int raking=sortNum.get(r.movieid);
				if(raking<=CommomFunction.TOPN)
					rightRecommendNumber++;
			}
			precision=1.0*rightRecommendNumber/CommomFunction.TOPN;
			recall=1.0*rightRecommendNumber/userRatingTesting.userRatings.size();
			System.out.println("用户" + userid + "的recall=" + recall+",precision="+precision);
			precisionSum+=precision;
			recallSum+=recall;
		}
		
		System.out.println(" average recall=" + recallSum/totalNum+",precision="+precisionSum / totalNum);
	}

}
