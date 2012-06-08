package com.riktamtech.android.sellabike.dao;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.riktamtech.android.sellabike.util.Base64;
import com.riktamtech.android.sellabike.util.ImageTools;
import com.riktamtech.android.sellabike.util.Utils;

public class BikeDAO implements Serializable {
	private static final long serialVersionUID = -3909237433212069124L;
	public static int UN_ASSIGNED = -99999999;
	public static int UPLOAD_IN_PROGRESS = -99999998;

	public String color;
	private ArrayList<Integer> selectedFeatures;
	/**
	 * 
	 */
	private static final String TAG = "bikeads";
	public int engineID, makeID, modelID, year;
	public int bikeType;
	public int engineSize, bikeId, sellerId, mileage, colorId;
	public String bikeName, regNo, features, description, firstName, lastName, email, telephone, url, draftName;
	public boolean isGumtree, isPremium;
	public double lat;
	public double lon;
	public float price;
	public ArrayList<Photo> photos;
	public ArrayList<String> thumbs;
	public int savedSearchId;
	public ArrayList<Integer> featuresArrayList;

	public BikeDAO(int engineID, int makeID, int modelID, int year, int bikeType, int engineSize, int bikeId, int sellerId, int mileage, int colorId, String bikeName,
			String regNo, String features, String description, String firstName, String lastName, String email, String telephone, String url, boolean isGumtree, boolean isPremium,
			double lat, double lon, float price, ArrayList<Photo> photos, ArrayList<String> thumbs) {
		super();
		this.engineID = engineID;
		this.makeID = makeID;
		this.modelID = modelID;
		this.year = year;
		this.bikeType = bikeType;
		this.engineSize = engineSize;
		this.bikeId = bikeId;
		this.sellerId = sellerId;
		this.mileage = mileage;
		this.colorId = colorId;
		this.bikeName = bikeName;
		this.regNo = regNo;
		this.features = features;
		this.description = description;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.telephone = telephone;
		this.url = url;
		this.isGumtree = isGumtree;
		this.isPremium = isPremium;
		this.lat = lat;
		this.lon = lon;
		this.price = price;
		this.photos = photos;
		this.thumbs = thumbs;
	}

	public BikeDAO(String id, String gumtree, String premium) {
		super();
		this.bikeId = (Integer.parseInt(id));
		this.isGumtree = Boolean.parseBoolean(gumtree);
		this.isPremium = Boolean.parseBoolean(premium);

	}

	public BikeDAO() {
		bikeId = UN_ASSIGNED;
		isPremium = false;
	}

	public void setLocation(Location location) {
		lat = location.getLatitude();
		lon = location.getLongitude();
		// TODO remove log
		Log.d(TAG, "setting location " + lat + " , " + lon);
	}

	public int getDistance() {
		return Utils.distanceTo(lat, lon);
	}

	@Override
	public String toString() {
		super.toString();
		return bikeName;
	}

	public ArrayList<Integer> getSelectedFeatures() {
		return selectedFeatures;
	}

	public void setSelectedFeatures(ArrayList<Integer> selectedFeatures) {
		this.selectedFeatures = selectedFeatures;

	}

	public int getColorId() {
		return colorId;
	}

	public void setColorId(int colorId, String color) {
		this.colorId = colorId;
		this.color = color;
	}

	public int getFeatures() {
		return 0;
	}

	public String getBase64EncodedImage(int index) {
		Bitmap bm = ImageTools.decodeFile(photos.get(index).url, 800);
		Log.d(TAG, "bmp : " + bm.getWidth() + "," + bm.getHeight());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		bm.recycle();
		bm = null;
		byte[] byteArray = baos.toByteArray();
		Log.d(TAG, byteArray.length + "");
		return Base64.encodeBytes(byteArray);
	}

	public String getBikePicForPosting() {
		if (photos != null && photos.get(0) != null)
			return photos.get(0).url;
		else if (thumbs != null && thumbs.get(0) != null)
			return thumbs.get(0);

		else
			return url;
	}
}
