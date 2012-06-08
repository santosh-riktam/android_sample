package com.riktamtech.android.sellabike.dao;

import java.util.ArrayList;

import android.location.Location;
import android.util.Log;
import com.riktamtech.android.sellabike.dao.Photo;

public class MyAdsDAO {
	private static final String TAG = "myads";
	public String bikeName, gumtree, premium, features, description, firstName,
			lastName, email, url, photoUrl, photoCaption, thumb, regNo,
			telephone;;
	public int id, engineID, makeID, modelID, sellerID, engineSize, year,
			mileage, price, colorID, sellerType;
	public double lat;
	public double lon;
	public ArrayList<Photo> photos;
	public ArrayList<String> thumbs;

	public ArrayList<Integer> featuresArrayList;

	public MyAdsDAO(String bikeName, String gumtree, String premium,
			String features, String description, String firstName,
			String lastName, String email, String url, String photoUrl,
			String photoCaption, String thumb, String regNo, int id,
			int engineID, int makeID, int modelID, int sellerID,
			int engineSize, int year, int mileage, int price, int colorID,
			int sellerType, String telephone, double lat, double lon,
			ArrayList<Photo> photos, ArrayList<String> thumbs,
			ArrayList<Integer> featuresArrayList) {
		super();
		this.bikeName = bikeName;
		this.gumtree = gumtree;
		this.premium = premium;
		this.features = features;
		this.description = description;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.url = url;
		this.photoUrl = photoUrl;
		this.photoCaption = photoCaption;
		this.thumb = thumb;
		this.regNo = regNo;
		this.id = id;
		this.engineID = engineID;
		this.makeID = makeID;
		this.modelID = modelID;
		this.sellerID = sellerID;
		this.engineSize = engineSize;
		this.year = year;
		this.mileage = mileage;
		this.price = price;
		this.colorID = colorID;
		this.sellerType = sellerType;
		this.telephone = telephone;
		this.lat = lat;
		this.lon = lon;
		this.photos = photos;
		this.thumbs = thumbs;
		this.featuresArrayList = featuresArrayList;
	}

	public String getFeaturesString(String[] featuresArray) {
		String features = "";
		for (int i = 0; i < featuresArrayList.size(); i++) {
			features = features + "," + featuresArray[featuresArrayList.get(i)];
		}
		return features.substring(1);
	}

	public MyAdsDAO(String id, String gumtree, String premium) {
		super();
		this.id = (Integer.parseInt(id));
		this.gumtree = gumtree;
		this.premium = premium;
	}

	public MyAdsDAO() {
		makeID = modelID = engineID = colorID = engineSize = -1;
		sellerType = 0;
	}

	@Override
	public String toString() {
		super.toString();
		return bikeName;
	}

	public void setLocation(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		// TODO remove log
		Log.d(TAG, "setting location " + lat + " , " + lon);
	}
}
