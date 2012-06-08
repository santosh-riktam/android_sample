package com.riktamtech.android.sellabike.dao;

public class ReviewsDAO {

	public int id;
	public String thumb, title, summary, shareSummary, fullText;

	public ReviewsDAO(int id, String thumb, String title, String summary, String shareSummary, String fullText) {
		super();
		this.id = id;
		this.thumb = thumb;
		this.title = title;
		this.summary = summary;
		this.shareSummary = shareSummary;
		this.fullText = fullText;
	}

	public ReviewsDAO(String id) {
		super();
		this.id = Integer.parseInt(id);
	}

	@Override
	public String toString() {
		super.toString();
		return title;
	}
}
