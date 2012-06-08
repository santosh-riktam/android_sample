package com.riktamtech.android.sellabike.dao;


public class PicData {
	public String bitmapLocation;
	public String caption;

	public PicData(String bitmapLocation, String caption) {
		super();
		this.bitmapLocation = bitmapLocation;
		this.caption = caption;
	}

	public PicData() {
		bitmapLocation = null;
		caption = "";
	}

}