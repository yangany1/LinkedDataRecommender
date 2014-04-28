package com.sjtu.recommend.user;

public class Rating {
	public int movieid;
	public int rating;
	public int time;
	public Rating(int movieid, int rating, int time) {
		super();
		this.movieid = movieid;
		this.rating = rating;
		this.time = time;
	}
	public Rating() {
		super();
	}
	
	 
}
