package com.riktamtech.android.sellabike.dao;

import java.io.Serializable;

public class Photo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8194237991584927177L;
	public String url;
	public String caption;

	
	
	public Photo(String url, String caption) {
		super();
		this.url = url;
		this.caption = caption;
	}



	public Photo() {
	}



	@Override
	public String toString() {
		return url;
	}

}