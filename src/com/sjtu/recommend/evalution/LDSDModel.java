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
import com.sjtu.recommend.utils.CommomFunction;
import com.sjtu.recommend.utils.FilmObject;
import com.sjtu.recommend.utils.Pair;
/**
 * 采用LDSD进行推荐的模型
 * @author luo
 *
 */
public class LDSDModel {
	public static double pfilter = 0.1;
	public static int n = 200;
	public static float[][] rele;
	public static int filmNumber=1577;
	
	public static Map<String, Double> pFrequency;
	// 从数据库获得用户的历史记录
	// 从数据库获得用户的历史记录
		public static List<Rating> getUserRatingsFromMysql(int userid,
				String tablename) {
			List<Rating> rList = new ArrayList<Rating>();
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager
						.getConnection(
								"jdbc:mysql://localhost/paper2?useUnicode=true&characterEncoding=utf-8",
								"root", "luo");
				String sql = "select * from " + tablename + " where userid="
						+ userid + " order by time asc"; // 查询数据的sql语句
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
		public static UserRatingObject getUserRatingObject(int userid) {
			UserRatingObject userRating = new UserRatingObject();
			userRating.userid = userid;
			userRating.userRatings = getUserRatingsFromMysql(userid, "rating");
			return userRating;

		}

		public static UserRatingObject getUserRatingLearningObject(int userid) {
			UserRatingObject userRating = new UserRatingObject();
			userRating.userid = userid;
			userRating.userRatings = getUserRatingsFromMysql(userid,
					"rating_learning");
			return userRating;

		}

		public static UserRatingObject getUserRatingTestingObject(int userid) {
			UserRatingObject userRating = new UserRatingObject();
			userRating.userid = userid;
			userRating.userRatings = getUserRatingsFromMysql(userid, "rating_test");
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

	public static Map<String, Double> getTopNMap(Map<String, Double> oriMap) {
		Map<String, Double> newMap = new LinkedHashMap<String, Double>();
		Iterator<String> it = oriMap.keySet().iterator();
		int i = 0;
		for (String s : oriMap.keySet()) {
			String id = s;
			double fre = oriMap.get(s);
			newMap.put(id, fre);
			i++;
			if (i >= n)
				break;

		}
		return newMap;
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

		System.out.println(filmList.size());
		// filmCode 用于根据资源名得到相应的资源类
		for (int i = 0; i < filmList.size(); i++) {
			FilmObject f = filmList.get(i);
			f.calculateLinkNum();
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
	
	public static void loadReleFromFile() throws Exception{
		rele=new float[filmNumber+1][filmNumber+1];
		BufferedReader br=new BufferedReader(new FileReader("files/ldsd/resultFile"));
		String line=null;
		while((line=br.readLine())!=null){
			int film1=Integer.parseInt(line.split(",")[0]);
			int film2=Integer.parseInt(line.split(",")[1]);
			float relevancy=Float.parseFloat(line.split(",")[2]);
			rele[film1][film2]=relevancy;
			rele[film2][film1]=relevancy;
		}
		br.close();
//		System.out.println("load rele end");
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
		
		
		double totalMRR=0;
		int totalNum=0;
//		loadReleFromFile();
		
		pFrequency = new HashMap<String, Double>();// 谓词的频率
		LoadPredicateFrequency(pFrequency);
		
		for (int k = CommomFunction.testStart; k <=CommomFunction.testEnd; k++) {
			int userid = k;
			UserRatingObject userRating = getUserRatingObject(userid,"rating");
			meanRating(userRating);
			if (userRating.userRatings.size() < 20)
				continue;
			totalNum++;

			UserRatingObject userRatingLearning = getUserRatingObject(userid,"rating_learning");
			UserRatingObject userRatingTesting = getUserRatingObject(userid,"rating_test");

			Map<Integer, String> movieCode = new HashMap<Integer, String>();
			movieLenCode(movieCode);

			Map<String, Integer> ldsdMovieMap=new HashMap<String, Integer>();
			Map<Integer, String> ldsdMovieMap2=new HashMap<Integer, String>();
			
			loadLDSDMovieMap(ldsdMovieMap, ldsdMovieMap2);

		
			
			List<Integer> learnList=new ArrayList<Integer>();
			for (Rating r : userRatingLearning.userRatings) {
				String name = movieCode.get(r.movieid);
				int id=ldsdMovieMap.get(name);
//			s	learnList.add(id);
				learnList.add(r.movieid);
			}

			Map<String, Double> allResult = new HashMap<String, Double>();
		
//			System.out.println("learn size="+learnList.size());
//			for(int i=1;i<=1577;i++){
//				double relevancy=0;
//				for(Integer l:learnList){
//					if(i!=l)
//						relevancy+=rele[l][i];
//				}
//				
//				allResult.put(ldsdMovieMap2.get(i), relevancy);
//			}
			
//			System.out.println(filmCode.size());
			for (int i = 0; i < filmList.size(); i++) {
				FilmObject f = filmList.get(i);
				double rele = 0;
				for (Integer movie : learnList) {
					String movieName=movieCode.get(movie);
					int mid=filmCode.get(movieName);
					FilmObject f2 = filmList.get(mid);
					if(mid!=i)
					rele+= calculateLDSD(f, f2);

				}
				allResult.put(f.nameurl, rele);
			}


		
			Map<String, Double> sortResult = sortMapByValue(allResult);
			Map<String, Integer> sortNum = new HashMap<String, Integer>();

			// System.out.println("全部电影的排序结果为：");
			int i = 0;
			for (String s : sortResult.keySet()) {
				// System.out.println(s+","+sortResult.get(s));
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
			totalMRR+=MRR;
			System.out.println();
		}
		
		System.out.println("average MRR="+totalMRR/totalNum);
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
					if (pFrequency.containsKey(p1.predicate))
						sum = (float) (sum + 1 / (1 + Math.log(linksNum)));
//						sum = (float) (sum + predicateWeight.get(p1.predicate)
//								* 1 / (1 + Math.log(linksNum)));
				}
			}
		}
		return sum;
	}
}
