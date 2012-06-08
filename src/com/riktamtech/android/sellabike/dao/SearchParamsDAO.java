package com.riktamtech.android.sellabike.dao;

import java.util.ArrayList;

public class SearchParamsDAO {
	public int id;
	public int makeId, modelId, engineSizeId, distanceId, priceMin, priceMax, ageId, mileageId, sellerTypeId,savedSearchId;
	public String keywords, postCode;
	public double lat, lon;
	public String color;
	private int colorId;
	// helpers
	private ArrayList<Integer> selectedFeatures;

	public SearchParamsDAO() {
		ageId = mileageId = sellerTypeId = 0;
		// TODO correct?
		makeId = modelId = engineSizeId = colorId = -1;
		selectedFeatures = new ArrayList<Integer>();
		postCode = null;
		priceMin = 20;
		priceMax = 20000;
		distanceId = 5;
		lat=lon=-1;
	}

	public SearchParamsDAO(int id, int makeId, int modelId, int engineSizeId, int colorId, int distanceId, int priceMin, int priceMax, int ageId, int mileageId, int sellerTypeId,
			String keywords, String postCode, String features) {
		super();
		this.id = id;
		this.makeId = makeId;
		this.modelId = modelId;
		this.engineSizeId = engineSizeId;
		this.colorId = colorId;
		this.distanceId = distanceId;
		this.priceMin = priceMin;
		this.priceMax = priceMax;
		this.ageId = ageId;
		this.mileageId = mileageId;
		this.sellerTypeId = sellerTypeId;
		this.keywords = keywords;
		this.postCode = postCode;
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

	public int getAge() {
		int ages[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		return ages[ageId];
	}

	public int getMileage() {
		int mileages[] = { 5000, 1000, 20000, 40000, 60000, 80000, 100000, 100001 };
		return mileages[mileageId];
	}

	public String getDistance() {
		String[] distances = { "5", "10", "20", "30", "40", "50+" };
		return distances[distanceId];
	}
}
