package com.riktamtech.android.sellabike.data;

import java.net.URLEncoder;
import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.riktamtech.android.sellabike.dao.SearchParamsDAO;

public class ServiceURLBuilder {

	private interface urls {

		String databaseVersions = "http://api.sellabike.me/api/getDatabaseVersion";
		String colors = "http://api.sellabike.me/api/getColours";
		String engines = "http://api.sellabike.me/api/getEngines";
		String makes = "http://api.sellabike.me/api/getMakes";
		String getBike = "http://api.sellabike.me/api/getBike/?id=";
		String models = "http://api.sellabike.me/api/getModels";
		String news = "http://api.sellabike.me/api/getNews";
		String reviews = "http://api.sellabike.me/api/getReviews";
		String myAdsView = "http://api.sellabike.me/api/myAdsView/?deviceID=";
		String myadsDelete = "http://api.sellabike.me/api/myAdsDelete?bikeid=";
		String regResult = "https://cdl-elvis.cdlis.co.uk/cdl-elvis/elvis?vehicle_type=PC&userid=SELLABIKE&test_flag=N&client_type=external&search_type=vrm&function_name=xml_SELLABIKE_fnc&search_string=";
		String featuredBikes = "http://api.sellabike.me/api/getfeaturedbikes";
		String searchBikes = "http://api.sellabike.me/api/searchBikes";
		String reverseGeocoder = "http://nominatim.openstreetmap.org/reverse?format=xml&zoom=18&addressdetails=1";
		String saveSearch = "http://api.sellabike.me/api/saveSearch/";
		String removeSearch = "http://api.sellabike.me/api/removeSearch";
		String submitBike = "http://api.sellabike.me/api/submitBike/";
		String geoCoderUrl = "http://maps.googleapis.com/maps/api/geocode/xml?sensor=true&address=";
	}

	public static String getDatabaseVersionsUrl() {
		return urls.databaseVersions;
	}

	public static String getMakesUrl() {
		return urls.makes;
	}

	public static String getBikeUrl(int bikeID) {
		return urls.getBike + bikeID;
	}

	public static String getModelsUrl() {
		return urls.models;
	}

	public static String getEnginesUrl() {
		return urls.engines;
	}

	public static String getColorsUrl() {
		return urls.colors;
	}

	public static String getNewsUrl() {
		return urls.news;
	}

	public static String getReviewsUrl() {
		return urls.reviews;
	}

	public static String getMyAdsViewUrl(String deviceId) {
		return urls.myAdsView + deviceId;
	}

	public static String getFeaturedBikesUrl() {
		return urls.featuredBikes;
	}

	public static String getRegistrationResultUrl(String regNo) {
		return urls.regResult + regNo;
	}

	public static String getSearchUrl(Context context, SearchParamsDAO searchParamsDAO, int start, int end) {
		String text = urls.searchBikes;
		ArrayList<String> params = new ArrayList<String>();

		if (!isEmpty(searchParamsDAO.keywords))
			params.add("keywords=" + URLEncoder.encode(searchParamsDAO.keywords));

		if (!isEmpty(searchParamsDAO.color))
			params.add("color=" + URLEncoder.encode(searchParamsDAO.color));

		if (searchParamsDAO.makeId > 0)
			params.add("make=" + searchParamsDAO.makeId);

		if (searchParamsDAO.modelId > 0)
			params.add("model=" + searchParamsDAO.modelId);

		if (searchParamsDAO.engineSizeId > 0)
			params.add("engine=" + searchParamsDAO.engineSizeId);

		params.add("pricerange=" + searchParamsDAO.priceMin + ";" + searchParamsDAO.priceMax);

		// TODO should discuss age and mileage for 'over' value 
		if (searchParamsDAO.ageId > 0)
			params.add("age=" + searchParamsDAO.getAge());

		if (searchParamsDAO.mileageId > 0)
			params.add("mileage=" + searchParamsDAO.getMileage());

		if (searchParamsDAO.sellerTypeId > 0)
			params.add("seller=" + searchParamsDAO.sellerTypeId);

		if (searchParamsDAO.distanceId >= 0)
			params.add("distance=" + searchParamsDAO.getDistance());

		if (searchParamsDAO.lat != 0) {
			params.add("lat=" + searchParamsDAO.lat);
			params.add("lon=" + searchParamsDAO.lon);
		}

		params.add("start=" + start);
		params.add("end=" + end);

		for (int i = 0; i < params.size(); i++) {
			String connector = i == 0 ? "?" : "&";
			text = text + connector + params.get(i);
		}
		return text;
	}

	private static boolean isEmpty(String str) {
		return str == null || TextUtils.isEmpty(str);
	}

	public static String getReverseGeoCoderUrl(double lat, double lon) {
		String link = urls.reverseGeocoder + "&lat=" + lat + "&lon=" + lon;
		return link;
	}

	public static String getMyadsDeleteUrl(int bikeid) {
		return urls.myadsDelete + bikeid;
	}

	public static String getSavedSearchUrl(int makeId, int modelId, int engineId, String appleid, String deviceId) {
		String text = urls.saveSearch;
		// TODO What to pass if the user doesn't select make, model etc
		text += "?make=" + makeId + "&model=" + modelId + "&engine=" + engineId + "&appleid=" + URLEncoder.encode(appleid) + "&deviceid=" + URLEncoder.encode(deviceId);
		return text;
	}

	public static String getSubmitBikeUrl() {
		return urls.submitBike;
	}

	public static String getRemoveSearchUrl(int savedsearchid, String appleid) {
		String text = urls.removeSearch;
		text += "?savedsearchid=" + savedsearchid + "&appleid=" + URLEncoder.encode(appleid);
		return text;
	}

	public static String getGoecoderUrl(String address) {
		return urls.geoCoderUrl + URLEncoder.encode(address);
	}
	
	public static String getNominatimSearchUrl(String postcode) {
		String url="http://nominatim.openstreetmap.org/search?&format=xml&polygon=1&addressdetails=1&q="+URLEncoder.encode(postcode)+",UK";
		return url;
	}
}
