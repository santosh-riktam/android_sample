package com.riktamtech.android.sellabike.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.ColourDAO;
import com.riktamtech.android.sellabike.dao.EngineDAO;
import com.riktamtech.android.sellabike.dao.MakeDAO;
import com.riktamtech.android.sellabike.dao.ModelDAO;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.dao.ReviewsDAO;
import com.riktamtech.android.sellabike.dao.SearchParamsDAO;

public class DBManager {

	public interface Tables {
		String TBL_ENGINES = "TBL_ENGINES";
		String TBL_PHOTOS = "TBL_PHOTOS";
		String TBL_BIKE = "tbl_bike";
		String TBL_MODELS = "TBL_MODELS";
		String TBL_SEARCH_PARAMS = "tbl_search_params";
		String TBL_THUMBS = "TBL_THUMBS";
		String TBL_MAKES = "TBL_MAKE";
		String TBL_COLORS = "TBL_COLORS";
		String TBL_REVIEWS = "tbl_reviews";

	}

	public interface MakesCloumns {
		String MAKE_NAME = "MAKE_NAME";
		String MAKE_ID = "MAKE_ID";
	}

	public interface ModelsCloumns {
		String MODEL_NAME = "MODEL_NAME";
		String MAKE_ID = "MAKE_ID";
		String MODEL_ID = "MODEL_ID";
		String ENGINE_ID = "ENGINE_ID";

	}

	public interface ColorsCloumns {
		String COLOR_NAME = "COLOR_NAME";
		String COLOUR_ID = "COLOR_ID";
	}

	public interface EnginesCloumns {
		String ENGINE_NAME = "ENGINE_NAME";
		String ENGINE_ID = "ENGINE_ID";
	}

	public interface BikesCloumns {
		String bike_id = "_id";
		String is_gum_tree = "is_gum_tree";
		String is_premium = "is_premium";
		String bike_name = "bike_name";
		String make_id = "make_id";
		String model_id = "model_id";
		String engine_size = "engine_size";
		String engine_id = "engine_id";
		String seller_id = "seller_id";
		String color_id = "color_id";
		String year = "year";
		String mileage = "mileage";
		String reg_no = "reg_no";
		String features = "features";
		String description = "description";
		String price = "price";
		String lat = "lat";
		String lon = "lon";
		String distance = "distance";
		String first_name = "first_name";
		String last_name = "last_name";
		String telephone = "telephone";
		String email = "email";
		String seller_url = "seller_url";
		String bike_type = "bike_type";
		String insert_time = "insert_time";
		String saved_search_id = "saved_search_id";
		String draft_name = "draft_name";
	}

	public interface BikeTypes {
		int FAVOURUTE = 0, PENDING_FOR_UPLOAD = 1;

	}

	public interface PhotosCloumns {
		String PHOTO_URL = "photo_url";
		String PHOTO_ID = "_id";
		String PHOTO_CAPTION = "photo_caption";
		String BIKE_ID = "bike_id";
	}

	public interface ThumbsCloumns {
		String THUMB_ID = "_id";
		String THUMB_URL = "thumb_url";
		String BIKE_ID = "bike_id";
	}

	public interface SearchParamsCloumns {
		String SEARCH_ID = "_id";
		String MAKE_ID = "make_id";
		String MODEL_ID = "model_id";
		String ENGINE_ID = "engine_id";
		String COLOUR_ID = "color_id";
		String PRICE_MIN = "price_min";
		String PRICE_MAX = "price_max";
		String AGE_ID = "age_id";
		String MILEAGE_ID = "mileage_id";
		String SELLER_ID = "seller_id";
		String DISTANCE_ID = "distance_id";
		String POSTCODE = "postcode";
		String KEYWORDS = "keywords";
		String FEATURES = "features";
		String SAVED_SEARCH_ID="saved_search_id";
		String INSERT_TIME = "insert_time";
	}

	public interface ReviewsColumns {
		String REVIEW_ID = "_id";
		String THUMB = "thumb";
		String TITLE = "title";
		String SUMMARY = "summary";
		String SHARE_SUMMARY = "share_summary";
		String FULL_TEXT = "full_text";
	}

	private static final String TAG = "dbmanager";

	Context context;
	SQLiteDatabase readableDatabase;

	public DBManager(Context context) {
		super();
		this.context = context;
	}

	private void openDatabase() {
		readableDatabase = new DBHelper(context).getReadableDatabase();
	}

	private void closeDatabase() {
		readableDatabase.close();
	}

	public ArrayList<MakeDAO> getMakes() {
		openDatabase();
		Cursor resultCursor = readableDatabase.query(Tables.TBL_MAKES, new String[] { MakesCloumns.MAKE_ID, MakesCloumns.MAKE_NAME }, null, null, null, null,
				MakesCloumns.MAKE_NAME);
		ArrayList<MakeDAO> list = new ArrayList<MakeDAO>();
		resultCursor.moveToFirst();
		while (!resultCursor.isAfterLast()) {
			list.add(new MakeDAO(resultCursor.getInt(0), resultCursor.getString(1)));
			resultCursor.moveToNext();
		}
		resultCursor.close();
		closeDatabase();
		return list;
	}

	public MakeDAO getMake(int makeId) {
		if (makeId > 0) {
			openDatabase();
			Cursor resultCursor = readableDatabase.query(Tables.TBL_MAKES, new String[] { MakesCloumns.MAKE_ID, MakesCloumns.MAKE_NAME }, MakesCloumns.MAKE_ID + "=?",
					new String[] { makeId + "" }, null, null, MakesCloumns.MAKE_NAME);
			resultCursor.moveToFirst();
			MakeDAO makeDAO = new MakeDAO(resultCursor.getInt(0), resultCursor.getString(1));
			resultCursor.close();
			closeDatabase();
			return makeDAO;
		}
		return new MakeDAO(-1, "");
	}

	public ModelDAO getModel(int modelId) {
		if (modelId > 0) {
			openDatabase();
			Cursor resultCursor = readableDatabase.query(Tables.TBL_MODELS, new String[] { ModelsCloumns.MODEL_ID, ModelsCloumns.MAKE_ID, ModelsCloumns.ENGINE_ID,
					ModelsCloumns.MODEL_NAME }, ModelsCloumns.MODEL_ID + "=?", new String[] { modelId + "" }, null, null, ModelsCloumns.MODEL_NAME);
			resultCursor.moveToFirst();
			ModelDAO modelDAO = new ModelDAO(resultCursor.getInt(0), resultCursor.getInt(1), resultCursor.getString(2), resultCursor.getString(3));
			resultCursor.close();
			closeDatabase();
			return modelDAO;
		}
		return new ModelDAO(-1, -1, "", "");
	}

	public ArrayList<ModelDAO> getModels() {
		openDatabase();
		Cursor resultCursor = readableDatabase.query(Tables.TBL_MODELS, new String[] { ModelsCloumns.MODEL_ID, ModelsCloumns.MAKE_ID, ModelsCloumns.ENGINE_ID,
				ModelsCloumns.MODEL_NAME }, null, null, null, null, ModelsCloumns.MODEL_NAME);
		ArrayList<ModelDAO> list = new ArrayList<ModelDAO>();
		resultCursor.moveToFirst();
		while (!resultCursor.isAfterLast()) {
			list.add(new ModelDAO(resultCursor.getInt(0), resultCursor.getInt(1), resultCursor.getString(2) + "", resultCursor.getString(3)));
			resultCursor.moveToNext();
		}
		resultCursor.close();
		closeDatabase();
		return list;
	}

	public ArrayList<ModelDAO> getModels(int makeId) {
		ArrayList<ModelDAO> models = new ArrayList<ModelDAO>();
		ArrayList<ModelDAO> allModelDAOs = getModels();
		for (ModelDAO model : allModelDAOs) {
			if (model.makeid == makeId)
				models.add(model);
			if (model.id == 0)
				models.add(0, model);
		}
		return models;
	}

	public ArrayList<EngineDAO> getEngines() {
		openDatabase();
		Cursor resultCursor = readableDatabase.query(Tables.TBL_ENGINES, new String[] { EnginesCloumns.ENGINE_ID, EnginesCloumns.ENGINE_NAME }, null, null, null, null, null);
		ArrayList<EngineDAO> list = new ArrayList<EngineDAO>();
		resultCursor.moveToFirst();
		while (!resultCursor.isAfterLast()) {
			list.add(new EngineDAO(resultCursor.getInt(0), resultCursor.getString(1)));
			resultCursor.moveToNext();
		}
		resultCursor.close();
		closeDatabase();
		return list;

	}

	public EngineDAO getEngine(int engineId) {
		EngineDAO engineDAO = new EngineDAO(-1, "");

		if (engineId > 0) {
			openDatabase();
			Cursor resultCursor = readableDatabase.query(Tables.TBL_ENGINES, new String[] { EnginesCloumns.ENGINE_ID, EnginesCloumns.ENGINE_NAME },
					EnginesCloumns.ENGINE_ID + "=?", new String[] { engineId + "" }, null, null, EnginesCloumns.ENGINE_NAME);
			resultCursor.moveToFirst();

			if (!resultCursor.isAfterLast()) {
				engineDAO = new EngineDAO(resultCursor.getInt(0), resultCursor.getString(1));
			}

			resultCursor.close();
			closeDatabase();
		}
		return engineDAO;

	}

	public ArrayList<EngineDAO> getEngines(String engineIds) {
		StringTokenizer tokenizer = new StringTokenizer(engineIds, ",", false);
		Log.d(TAG, "engines " + engineIds);
		ArrayList<Integer> idsArrayList = new ArrayList<Integer>();
		while (tokenizer.hasMoreTokens())
			idsArrayList.add(Integer.parseInt(tokenizer.nextToken()));
		ArrayList<EngineDAO> filteredEngines = new ArrayList<EngineDAO>();
		for (EngineDAO engine : getEngines()) {
			{
				if (idsArrayList.contains(engine.id))
					filteredEngines.add(engine);
			}
		}

		return filteredEngines;
	}

	public ArrayList<ColourDAO> getColours() {
		openDatabase();
		Cursor resultCursor = readableDatabase.query(Tables.TBL_COLORS, new String[] { ColorsCloumns.COLOUR_ID, ColorsCloumns.COLOR_NAME }, null, null, null, null,
				ColorsCloumns.COLOR_NAME);
		ArrayList<ColourDAO> list = new ArrayList<ColourDAO>();
		resultCursor.moveToFirst();
		while (!resultCursor.isAfterLast()) {
			list.add(new ColourDAO(resultCursor.getInt(0), resultCursor.getString(1)));
			resultCursor.moveToNext();
		}
		resultCursor.close();
		closeDatabase();
		return list;
	}

	public ColourDAO getColour(int colourId) {
		if (colourId > 0) {
			openDatabase();
			Cursor resultCursor = readableDatabase.query(Tables.TBL_COLORS, new String[] { ColorsCloumns.COLOUR_ID, ColorsCloumns.COLOR_NAME }, ColorsCloumns.COLOUR_ID + "=?",
					new String[] { colourId + "" }, null, null, ColorsCloumns.COLOR_NAME);
			resultCursor.moveToFirst();
			ColourDAO colourDAO = new ColourDAO(resultCursor.getInt(0), resultCursor.getString(1));
			resultCursor.close();
			closeDatabase();
			return colourDAO;
		}
		return new ColourDAO(-1, "");
	}

	public ArrayList<String> getSellerTypes() {
		ArrayList<String> sellerTypesArrayList = new ArrayList<String>();
		sellerTypesArrayList.add("Private");
		sellerTypesArrayList.add("Personal");
		return sellerTypesArrayList;
	}

	public void upgradeMakeData(ArrayList<MakeDAO> items) {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();

		DatabaseUtils.InsertHelper insertHelper = new InsertHelper(writeableDatabase, Tables.TBL_MAKES);
		writeableDatabase.delete(Tables.TBL_MAKES, null, null);
		try {
			writeableDatabase.beginTransaction();
			for (MakeDAO item : items) {
				insertHelper.prepareForInsert();
				insertHelper.bind(insertHelper.getColumnIndex(MakesCloumns.MAKE_ID), item.id);
				insertHelper.bind(insertHelper.getColumnIndex(MakesCloumns.MAKE_NAME), item.name);
				insertHelper.execute();
			}
			writeableDatabase.setTransactionSuccessful();
			insertHelper.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeableDatabase.endTransaction();
			writeableDatabase.close();
		}
	}

	public void insertReviews(ArrayList<ReviewsDAO> items) {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();

		DatabaseUtils.InsertHelper insertHelper = new InsertHelper(writeableDatabase, Tables.TBL_REVIEWS);
		writeableDatabase.delete(Tables.TBL_REVIEWS, null, null);
		try {
			writeableDatabase.beginTransaction();
			for (ReviewsDAO item : items) {
				insertHelper.prepareForInsert();
				insertHelper.bind(insertHelper.getColumnIndex(ReviewsColumns.REVIEW_ID), item.id);
				insertHelper.bind(insertHelper.getColumnIndex(ReviewsColumns.TITLE), item.title);
				insertHelper.bind(insertHelper.getColumnIndex(ReviewsColumns.THUMB), item.thumb);
				insertHelper.bind(insertHelper.getColumnIndex(ReviewsColumns.SUMMARY), item.summary);
				insertHelper.bind(insertHelper.getColumnIndex(ReviewsColumns.SHARE_SUMMARY), item.shareSummary);
				insertHelper.bind(insertHelper.getColumnIndex(ReviewsColumns.FULL_TEXT), item.fullText);

				insertHelper.execute();
			}
			writeableDatabase.setTransactionSuccessful();
			insertHelper.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeableDatabase.endTransaction();
			writeableDatabase.close();
		}
	}

	public void upgradeEngineData(ArrayList<EngineDAO> items) {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		writeableDatabase.delete(Tables.TBL_ENGINES, null, null);
		InsertHelper insertHelper = new InsertHelper(writeableDatabase, Tables.TBL_ENGINES);
		try {
			writeableDatabase.beginTransaction();
			for (EngineDAO item : items) {
				insertHelper.prepareForInsert();
				insertHelper.bind(insertHelper.getColumnIndex(EnginesCloumns.ENGINE_ID), item.id);
				insertHelper.bind(insertHelper.getColumnIndex(EnginesCloumns.ENGINE_NAME), item.name);
				insertHelper.execute();
			}
			writeableDatabase.setTransactionSuccessful();
			insertHelper.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeableDatabase.endTransaction();
			writeableDatabase.close();
		}
	}

	public void upgradeModelData(ArrayList<ModelDAO> items) {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		writeableDatabase.delete(Tables.TBL_MODELS, null, null);
		InsertHelper insertHelper = new InsertHelper(writeableDatabase, Tables.TBL_MODELS);
		try {
			writeableDatabase.beginTransaction();
			for (ModelDAO item : items) {

				insertHelper.prepareForInsert();
				insertHelper.bind(insertHelper.getColumnIndex(ModelsCloumns.MODEL_ID), item.id);
				insertHelper.bind(insertHelper.getColumnIndex(ModelsCloumns.ENGINE_ID), item.engineid);
				insertHelper.bind(insertHelper.getColumnIndex(ModelsCloumns.MAKE_ID), item.makeid);
				insertHelper.bind(insertHelper.getColumnIndex(ModelsCloumns.MODEL_NAME), item.name);
				insertHelper.execute();
			}
			writeableDatabase.setTransactionSuccessful();
			insertHelper.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeableDatabase.endTransaction();
			writeableDatabase.close();
		}
	}

	public void upgradeColourData(ArrayList<ColourDAO> items) {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		writeableDatabase.delete(Tables.TBL_COLORS, null, null);
		InsertHelper insertHelper = new InsertHelper(writeableDatabase, Tables.TBL_COLORS);
		try {
			writeableDatabase.beginTransaction();

			for (ColourDAO item : items) {
				insertHelper.prepareForInsert();
				insertHelper.bind(insertHelper.getColumnIndex(ColorsCloumns.COLOUR_ID), item.id);
				insertHelper.bind(insertHelper.getColumnIndex(ColorsCloumns.COLOR_NAME), item.name);
				insertHelper.execute();
			}
			writeableDatabase.setTransactionSuccessful();
			insertHelper.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writeableDatabase.endTransaction();
			writeableDatabase.close();
		}
	}

	public long insertOrUpdateSavedSeach(SearchParamsDAO searchParamsDAO) {

		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();

		// making features string from feature indices
		List<String> featuresList = Arrays.asList(context.getResources().getStringArray(R.array.static_features_array));
		String features = "";
		for (int i : searchParamsDAO.getSelectedFeatures()) {
			if (i > -1 && i < featuresList.size())
				features += ", " + featuresList.get(i);
		}
		if (features.length() > 0 && features.startsWith(","))
			features = features.substring(1);

		ContentValues cv = new ContentValues();
		cv.put(SearchParamsCloumns.COLOUR_ID, searchParamsDAO.getColorId());
		cv.put(SearchParamsCloumns.MAKE_ID, searchParamsDAO.makeId);
		cv.put(SearchParamsCloumns.MODEL_ID, searchParamsDAO.modelId);
		cv.put(SearchParamsCloumns.ENGINE_ID, searchParamsDAO.engineSizeId);
		cv.put(SearchParamsCloumns.PRICE_MIN, searchParamsDAO.priceMin);
		cv.put(SearchParamsCloumns.PRICE_MAX, searchParamsDAO.priceMax);
		cv.put(SearchParamsCloumns.AGE_ID, searchParamsDAO.ageId);
		cv.put(SearchParamsCloumns.MILEAGE_ID, searchParamsDAO.mileageId);
		cv.put(SearchParamsCloumns.SELLER_ID, searchParamsDAO.sellerTypeId);
		cv.put(SearchParamsCloumns.DISTANCE_ID, searchParamsDAO.distanceId);
		cv.put(SearchParamsCloumns.POSTCODE, searchParamsDAO.postCode + "");
		cv.put(SearchParamsCloumns.KEYWORDS, searchParamsDAO.keywords + "");
		cv.put(SearchParamsCloumns.FEATURES, features);
		cv.put(SearchParamsCloumns.SAVED_SEARCH_ID, searchParamsDAO.savedSearchId);
		cv.put(SearchParamsCloumns.INSERT_TIME, System.currentTimeMillis());

		long returnValue = 0;
		Cursor query = writeableDatabase.query(Tables.TBL_SEARCH_PARAMS, new String[] { SearchParamsCloumns.SEARCH_ID }, SearchParamsCloumns.SEARCH_ID + "=?",
				new String[] { searchParamsDAO.id + "" }, null, null, null);
		if (query.getCount() > 0) {

			returnValue = writeableDatabase.update(Tables.TBL_SEARCH_PARAMS, cv, SearchParamsCloumns.SEARCH_ID + "=?", new String[] { searchParamsDAO.id + "" });
			Log.d(TAG, "updated " + returnValue);
		} else {
			returnValue = writeableDatabase.insert(Tables.TBL_SEARCH_PARAMS, null, cv);
			Log.d(TAG, "inserted " + returnValue);
		}

		query.close();
		writeableDatabase.close();
		return returnValue;
	}

	// TODO remove this - for testing
	public void getSavedSearches() {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		Cursor cursor = writeableDatabase.query(Tables.TBL_SEARCH_PARAMS, SavedSearchesQuery.columns, null, null, null, null, null);
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			Log.d(TAG, "no data");

		} else {
			while (cursor.moveToNext()) {
				Log.d(TAG, "data " + cursor.getInt(0));
			}
		}

		cursor.close();
		writeableDatabase.close();
	}

	public void getDrafts() {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		Cursor cursor = writeableDatabase.query(Tables.TBL_BIKE, DraftsQuery.columns, null, null, null, null, null);
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			Log.d(TAG, "no data");

		} else {
			while (cursor.moveToNext()) {
				Log.d(TAG, "data " + cursor.getInt(0));
			}
		}

		cursor.close();
		writeableDatabase.close();
	}

	public long insertOrUpdateBike(BikeDAO bike, int bikeType) {
		// updating - delete bike if already exists
		if (isBikeInDB(bike.bikeId)) {
			deleteBike(bike.bikeId);
		}
		if (bike.bikeId == BikeDAO.UN_ASSIGNED && bikeType == BikeTypes.PENDING_FOR_UPLOAD) {
			int id = getMinBikeId() - 1;
			bike.bikeId = id;
		}
		return insertBike(bike, bikeType);
	}

	public void deleteBike(int bikeId) {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		writeableDatabase.delete(Tables.TBL_BIKE, BikesCloumns.bike_id + "=?", new String[] { "" + bikeId });
		writeableDatabase.delete(Tables.TBL_PHOTOS, PhotosCloumns.BIKE_ID + "=?", new String[] { bikeId + "" });
		writeableDatabase.delete(Tables.TBL_THUMBS, ThumbsCloumns.BIKE_ID + "=?", new String[] { bikeId + "" });
		writeableDatabase.close();
	}

	public int getMinBikeId() {
		openDatabase();
		Cursor cursor = readableDatabase.rawQuery("select min (" + BikesCloumns.bike_id + ") from " + Tables.TBL_BIKE, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			int i = cursor.getInt(0);
			cursor.close();
			closeDatabase();
			return i;
		}
		return -1;

	}

	public long insertBike(BikeDAO bikeDAO, int bikeType) {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		writeableDatabase.beginTransaction();
		ContentValues cv = new ContentValues();
		cv.put(BikesCloumns.bike_id, bikeDAO.bikeId);
		cv.put(BikesCloumns.bike_name, bikeDAO.bikeName);
		cv.put(BikesCloumns.color_id, bikeDAO.colorId);
		cv.put(BikesCloumns.description, bikeDAO.description);
		cv.put(BikesCloumns.distance, bikeDAO.getDistance());
		cv.put(BikesCloumns.is_gum_tree, bikeDAO.isGumtree);
		cv.put(BikesCloumns.is_premium, bikeDAO.isPremium);
		cv.put(BikesCloumns.email, bikeDAO.email);
		cv.put(BikesCloumns.engine_id, bikeDAO.engineID);
		cv.put(BikesCloumns.engine_size, bikeDAO.engineSize);
		cv.put(BikesCloumns.features, bikeDAO.features);
		cv.put(BikesCloumns.first_name, bikeDAO.firstName);
		cv.put(BikesCloumns.last_name, bikeDAO.lastName);
		cv.put(BikesCloumns.lat, bikeDAO.lat);
		cv.put(BikesCloumns.lon, bikeDAO.lon);
		cv.put(BikesCloumns.make_id, bikeDAO.makeID);
		cv.put(BikesCloumns.mileage, bikeDAO.mileage);
		cv.put(BikesCloumns.model_id, bikeDAO.modelID);
		cv.put(BikesCloumns.price, bikeDAO.price);
		cv.put(BikesCloumns.reg_no, bikeDAO.regNo);
		cv.put(BikesCloumns.seller_id, bikeDAO.sellerId);
		cv.put(BikesCloumns.seller_url, bikeDAO.url);
		cv.put(BikesCloumns.telephone, bikeDAO.telephone);
		cv.put(BikesCloumns.year, bikeDAO.year);
		cv.put(BikesCloumns.bike_type, bikeType);
		cv.put(BikesCloumns.draft_name, bikeDAO.draftName);
		cv.put(BikesCloumns.insert_time, System.currentTimeMillis());
		cv.put(BikesCloumns.saved_search_id, bikeDAO.savedSearchId);
		long returnValue = writeableDatabase.insert(Tables.TBL_BIKE, null, cv);

		if (bikeDAO.thumbs != null && bikeDAO.thumbs.size() > 0) {
			InsertHelper insertHelper = new InsertHelper(writeableDatabase, Tables.TBL_THUMBS);
			int bikeidColIndex = insertHelper.getColumnIndex(ThumbsCloumns.BIKE_ID), thumbUrlColIndex = insertHelper.getColumnIndex(ThumbsCloumns.THUMB_URL);
			for (String thumb : bikeDAO.thumbs) {
				insertHelper.prepareForInsert();
				insertHelper.bind(bikeidColIndex, bikeDAO.bikeId);
				insertHelper.bind(thumbUrlColIndex, thumb);
				insertHelper.execute();
			}
			insertHelper.close();
		}

		if (bikeDAO.photos != null && bikeDAO.photos.size() > 0) {
			InsertHelper insertHelper = new InsertHelper(writeableDatabase, Tables.TBL_PHOTOS);
			int bikeidColIndex = insertHelper.getColumnIndex(PhotosCloumns.BIKE_ID), photoUrlColIndex = insertHelper.getColumnIndex(PhotosCloumns.PHOTO_URL), photoCaptionColIndex = insertHelper
					.getColumnIndex(PhotosCloumns.PHOTO_CAPTION);
			for (Photo photo : bikeDAO.photos) {
				insertHelper.prepareForInsert();
				insertHelper.bind(bikeidColIndex, bikeDAO.bikeId);
				insertHelper.bind(photoCaptionColIndex, photo.caption);
				insertHelper.bind(photoUrlColIndex, photo.url);
				insertHelper.execute();
			}
			insertHelper.close();
		}
		writeableDatabase.setTransactionSuccessful();
		writeableDatabase.endTransaction();
		writeableDatabase.close();
		return returnValue;
	}

	// TODO remove - this is for testing
	public void getFavouriteBikes() {
		SQLiteDatabase writeableDatabase = new DBHelper(context).getWritableDatabase();
		Cursor cursor = writeableDatabase.query(Tables.TBL_BIKE, FavoritesQuery.columns, null, null, null, null, null);
		Cursor cursor2 = writeableDatabase.query(Tables.TBL_THUMBS, FavoritesThumbsQuery.columns, null, null, null, null, null);
		cursor2.moveToFirst();
		cursor.moveToFirst();
		if (cursor.isAfterLast()) {
			Log.d(TAG, "no data");

		} else {
			while (cursor.moveToNext()) {
				Log.d(TAG, "data " + cursor.getInt(0));
			}
		}
		if (cursor2.isAfterLast()) {
			Log.d(TAG, "no data");

		} else {
			while (cursor2.moveToNext()) {
				Log.d(TAG, "data " + cursor.getInt(0));
			}
		}
		cursor2.close();
		cursor.close();
		writeableDatabase.close();
	}

	public boolean isBikeInDB(int bikeId) {
		openDatabase();
		Cursor cursor = readableDatabase.query(Tables.TBL_BIKE, new String[] { BikesCloumns.bike_id, BikesCloumns.bike_name }, BikesCloumns.bike_id + "=?", new String[] { bikeId
				+ "" }, null, null, null);
		cursor.moveToFirst();
		boolean bikeExists = cursor.getCount() > 0;
		cursor.close();
		closeDatabase();
		return bikeExists;

	}

	private interface SavedSearchesQuery {
		String columns[] = { SearchParamsCloumns.AGE_ID, SearchParamsCloumns.COLOUR_ID, SearchParamsCloumns.DISTANCE_ID, SearchParamsCloumns.ENGINE_ID,
				SearchParamsCloumns.FEATURES, SearchParamsCloumns.KEYWORDS, SearchParamsCloumns.MAKE_ID, SearchParamsCloumns.MILEAGE_ID, SearchParamsCloumns.MODEL_ID,
				SearchParamsCloumns.POSTCODE, SearchParamsCloumns.PRICE_MAX, SearchParamsCloumns.PRICE_MIN, SearchParamsCloumns.SEARCH_ID, SearchParamsCloumns.SELLER_ID };
	}

	private interface FavoritesQuery {
		String columns[] = { BikesCloumns.bike_id, BikesCloumns.bike_name, BikesCloumns.color_id, BikesCloumns.description, BikesCloumns.distance, BikesCloumns.email,
				BikesCloumns.engine_id, BikesCloumns.engine_size, BikesCloumns.features, BikesCloumns.first_name, BikesCloumns.is_gum_tree, BikesCloumns.is_premium,
				BikesCloumns.last_name, BikesCloumns.lat, BikesCloumns.lon, BikesCloumns.make_id, BikesCloumns.mileage, BikesCloumns.model_id, BikesCloumns.price,
				BikesCloumns.reg_no, BikesCloumns.seller_id, BikesCloumns.seller_url, BikesCloumns.telephone, BikesCloumns.year };

	}

	private interface DraftsQuery {
		String columns[] = { BikesCloumns.bike_id, BikesCloumns.bike_name, BikesCloumns.bike_type, BikesCloumns.color_id, BikesCloumns.description, BikesCloumns.distance,
				BikesCloumns.draft_name, BikesCloumns.email, BikesCloumns.engine_id, BikesCloumns.engine_size, BikesCloumns.features, BikesCloumns.first_name,
				BikesCloumns.insert_time, BikesCloumns.is_gum_tree, BikesCloumns.is_premium, BikesCloumns.last_name, BikesCloumns.lat, BikesCloumns.lon, BikesCloumns.make_id,
				BikesCloumns.mileage, BikesCloumns.model_id, BikesCloumns.price, BikesCloumns.reg_no, BikesCloumns.saved_search_id, BikesCloumns.seller_id,
				BikesCloumns.seller_url, BikesCloumns.telephone, BikesCloumns.year };

	}

	private interface FavoritesThumbsQuery {
		String columns[] = { ThumbsCloumns.BIKE_ID, ThumbsCloumns.THUMB_ID, ThumbsCloumns.THUMB_URL };
	}
}
