package com.riktamtech.android.sellabike.dao;

import java.util.ArrayList;
import android.location.Location;
import android.util.Log;

public class GetBikeDAO {
	private static final String TAG = "getbike";
	public int id, engineID, makeID, modelID, sellerID, year, total;
	boolean isMyAd, isFavourite, isPendingForUpload;
	public int engineSize, bikeId, sellerId, bikeYear, mileage;
	public String bikeName, gumtree, premium, regNo, features, description,
			firstName, lastName, email, telephone, url;
	public double lat;
	public double lon;
	public float price;
	public ArrayList<Photo> photos;
	public ArrayList<String> thumbs;

	public GetBikeDAO(int id, int engineID, int makeID, int modelID, int year,
			int total, boolean isMyAd, boolean isFavourite,
			boolean isPendingForUpload, int engineSize, int bikeId,
			int sellerId, int bikeYear, int mileage, String regNo,
			String features, String bikeName, String gumtree, String premium,
			String description, String firstName, String lastName,
			String email, String telephone, String url, float lat, float lon,
			float price) {
		super();
		this.id = id;
		this.engineID = engineID;
		this.makeID = makeID;
		this.modelID = modelID;
		this.isMyAd = isMyAd;
		this.total = total;
		this.year = year;
		this.gumtree = gumtree;
		this.premium = premium;
		this.isFavourite = isFavourite;
		this.isPendingForUpload = isPendingForUpload;
		this.engineSize = engineSize;
		this.bikeName = bikeName;
		this.bikeId = bikeId;
		this.sellerId = sellerId;
		this.bikeYear = bikeYear;
		this.mileage = mileage;
		this.regNo = regNo;
		this.features = features;
		this.description = description;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.telephone = telephone;
		this.url = url;
		this.lat = lat;
		this.lon = lon;
		this.price = price;
	}

	public GetBikeDAO(String id, String gumtree, String premium) {
		super();
		this.id = (Integer.parseInt(id));
		this.gumtree = gumtree;
		this.premium = premium;
	}

	public GetBikeDAO(String total) {
		super();
		this.total = (Integer.parseInt(total));
	}

	public void setLocation(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		// TODO remove log
		Log.d(TAG, "setting location " + lat + " , " + lon);
	}

	@Override
	public String toString() {
		super.toString();
		return bikeName;
	}

}
