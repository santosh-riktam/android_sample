package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.SearchParamsDAO;
import com.riktamtech.android.sellabike.data.DBHelper;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.BikesCloumns;
import com.riktamtech.android.sellabike.data.DBManager.SearchParamsCloumns;
import com.riktamtech.android.sellabike.data.DBManager.Tables;

public class SavedSearchesFragment extends Fragment {
	protected static final String TAG = "SavedSearchesFragment";
	DBManager dbManager;
	DBHelper dbHelper;
	Cursor cursor;
	public String enginecc;
	TextView bikeNameTextView, colorTextView, engineTextView, keywordsTextView,
			featuresTextView, distanceTextView, ageTextView, priceTextView,
			itemsCountTextView;
	ImageView editImageView, removeImageView;
	ListView listView;
	SQLiteDatabase readableDatabase;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		dbManager = new DBManager(getActivity().getApplicationContext());
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_savedsearches,
				container, false);
		listView = (ListView) inflate
				.findViewById(R.id.saved_searches_listView);
		listView.setAdapter(new savedSearchesAdapter(getActivity(), cursor,
				false));
		listView.setOnItemClickListener(onlistItemClickListener);
		itemsCountTextView = (TextView) inflate
				.findViewById(R.id.itemsCountTextView);
		return inflate;
	}

	@Override
	public void onResume() {
		super.onResume();
		readableDatabase = new DBHelper(getActivity().getApplicationContext())
				.getReadableDatabase();
		cursor = readableDatabase.query(Tables.TBL_SEARCH_PARAMS,
				SavedSearchesQuery.columns, null, null, null, null,
				SearchParamsCloumns.INSERT_TIME + " DESC");
		setItemsCountText(cursor.getCount());
		listView.setAdapter(new savedSearchesAdapter(getActivity(), cursor,
				false));

	}

	private void setItemsCountText(int count) {
		String countString = "";
		if (count <= 0)
			countString = getResources().getString(R.string.no_saved_searches);
		if (count > 0)
			countString = count + " item";
		if (count > 1)
			countString += "s";
		itemsCountTextView.setText(countString);
	}

	private interface SavedSearchesQuery {
		String columns[] = { SearchParamsCloumns.AGE_ID,
				SearchParamsCloumns.COLOUR_ID, SearchParamsCloumns.DISTANCE_ID,
				SearchParamsCloumns.ENGINE_ID, SearchParamsCloumns.FEATURES,
				SearchParamsCloumns.KEYWORDS, SearchParamsCloumns.MAKE_ID,
				SearchParamsCloumns.MILEAGE_ID, SearchParamsCloumns.MODEL_ID,
				SearchParamsCloumns.POSTCODE, SearchParamsCloumns.PRICE_MAX,
				SearchParamsCloumns.PRICE_MIN, SearchParamsCloumns.SEARCH_ID,
				SearchParamsCloumns.SELLER_ID };
		int AGE_ID = 0;
		int COLOUR_ID = 1;
		int DISTANCE_ID = 2;
		int ENGINE_ID = 3;
		int FEATURES = 4;
		int KEYWORDS = 5;
		int MAKE_ID = 6;
		int MILEAGE_ID = 7;
		int MODEL_ID = 8;
		int POSTCODE = 9;
		int PRICE_MAX = 10;
		int PRICE_MIN = 11;
		int SEARCH_ID = 12;
		int SELLER_ID = 13;
	}

	class savedSearchesAdapter extends CursorAdapter {

		public savedSearchesAdapter(Context context, Cursor c,
				boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			if (arg0 == null) {
				arg0 = getActivity().getLayoutInflater().inflate(
						R.layout.list_row_savedsearches, null, false);
			}

			bikeNameTextView = (TextView) arg0
					.findViewById(R.id.textView1_saved_searches);
			engineTextView = (TextView) arg0
					.findViewById(R.id.textView2_saved_searches);
			colorTextView = (TextView) arg0
					.findViewById(R.id.textView3_saved_searches);
			priceTextView = (TextView) arg0
					.findViewById(R.id.textView4_saved_searches);
			keywordsTextView = (TextView) arg0
					.findViewById(R.id.textView5_saved_searches);
			featuresTextView = (TextView) arg0
					.findViewById(R.id.textView6_saved_searches);
			distanceTextView = (TextView) arg0
					.findViewById(R.id.textView7_saved_searches);
			ageTextView = (TextView) arg0
					.findViewById(R.id.textView8_saved_searches);
			editImageView = (ImageView) arg0
					.findViewById(R.id.edit_saved_searches);
			removeImageView = (ImageView) arg0
					.findViewById(R.id.remove_saved_searches);

			bikeNameTextView.setText(""
					+ dbManager.getMake(cursor
							.getInt(SavedSearchesQuery.MAKE_ID)).name
					+ " "
					+ dbManager.getModel(cursor
							.getInt(SavedSearchesQuery.MODEL_ID)).name);
			engineTextView.setText("Engine size: "
					+ dbManager.getEngine(cursor
							.getInt(SavedSearchesQuery.ENGINE_ID)).name);
			ageTextView.setText("Age: "
					+ dbManager.getEngine(cursor
							.getInt(SavedSearchesQuery.AGE_ID)).name);
			colorTextView.setText("Colour: "
					+ dbManager.getColour(cursor
							.getInt(SavedSearchesQuery.COLOUR_ID)).name);
			priceTextView.setText("Price: \u00A3 "
					+ cursor.getString(SavedSearchesQuery.PRICE_MIN)
					+ " - \u00A3 "
					+ cursor.getString(SavedSearchesQuery.PRICE_MAX));
			keywordsTextView.setText("Keywords: "
					+ cursor.getString(SavedSearchesQuery.KEYWORDS));
			if (cursor.getString(SavedSearchesQuery.FEATURES) == null)
				featuresTextView.setVisibility(View.GONE);
			else
				featuresTextView.setText("Features: "
						+ cursor.getString(SavedSearchesQuery.FEATURES));
			// // TODO set distance
			editImageView.setOnClickListener(editImageClkListener);
			editImageView.setTag(cursor.getPosition());
			removeImageView.setOnClickListener(removeImageClkListener);
			removeImageView.setTag(cursor.getInt(SavedSearchesQuery.SEARCH_ID));

			distanceTextView.setText("Distance: " + "miles form ");

		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			LayoutInflater inflater = LayoutInflater.from(getActivity()
					.getApplicationContext());
			View view = inflater.inflate(R.layout.list_row_savedsearches, arg2,
					false);
			return view;
		}

	}

	OnClickListener editImageClkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO return the same details to back screen
			int position = (Integer) v.getTag();
			saveCursorRowToDao(position);
			((SearchActivity) getActivity()).showSearchForm(true);
		}

	};

	private void saveCursorRowToDao(int position) {
		cursor.moveToPosition(position);
		SearchParamsDAO dao = ((SearchActivity) getActivity()).editSearchDao;
		dao.ageId = cursor.getInt(SavedSearchesQuery.AGE_ID);
		int colorid = cursor.getInt(SavedSearchesQuery.COLOUR_ID);
		dao.setColorId(colorid, dbManager.getColour(colorid).name);
		dao.distanceId = cursor.getInt(SavedSearchesQuery.DISTANCE_ID);
		dao.engineSizeId = cursor.getInt(SavedSearchesQuery.ENGINE_ID);
		String features = cursor.getString(SavedSearchesQuery.FEATURES);
		// making list of indices from features string
		List<String> featuresList = Arrays.asList(getResources()
				.getStringArray(R.array.static_features_array));
		StringTokenizer tokenizer = new StringTokenizer(features, ",", false);
		ArrayList<Integer> featuresIndices = new ArrayList<Integer>();
		while (tokenizer.hasMoreTokens()) {
			featuresIndices.add(featuresList.indexOf(tokenizer.nextToken()));
		}
		dao.setSelectedFeatures(featuresIndices);
		dao.id = cursor.getInt(SavedSearchesQuery.SEARCH_ID);
		dao.keywords = cursor.getString(SavedSearchesQuery.KEYWORDS);
		dao.makeId = cursor.getInt(SavedSearchesQuery.MAKE_ID);
		dao.modelId = cursor.getInt(SavedSearchesQuery.MODEL_ID);
		dao.postCode = cursor.getString(SavedSearchesQuery.POSTCODE);
		dao.priceMax = cursor.getInt(SavedSearchesQuery.PRICE_MAX);
		dao.priceMin = cursor.getInt(SavedSearchesQuery.PRICE_MIN);
		dao.sellerTypeId = cursor.getInt(SavedSearchesQuery.SELLER_ID);
	}

	OnClickListener removeImageClkListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int searchId = (Integer) v.getTag();
			int affectedRowCount = readableDatabase.delete(
					Tables.TBL_SEARCH_PARAMS, BikesCloumns.bike_id + "=?",
					new String[] { searchId + "" });
			Log.d(TAG, affectedRowCount + " rows deleted");
			((CursorAdapter) listView.getAdapter()).getCursor().requery();
			setItemsCountText(cursor.getCount());
		}
	};

	public void goBack() {
		((FragmentActivity) getActivity()).getSupportFragmentManager()
				.popBackStack();
	}

	@Override
	public void onPause() {
		super.onPause();
		cursor.close();
		readableDatabase.close();
	}

	public String getEngine(int id) {
		switch (id) {
		case 1:
			enginecc = "0 - 125cc";
			break;
		case 2:
			enginecc = "126 - 250cc";
			break;
		case 3:
			enginecc = "251 - 500cc";
			break;
		case 4:
			enginecc = "501 - 750cc";
			break;
		case 5:
			enginecc = "751 - 1000cc";
			break;
		case 6:
			enginecc = "Over 1000c";
			break;
		default:
			break;
		}
		return enginecc;
	}

	@Override
	public void onStart() {
		super.onStart();
		((BaseActivity) getActivity()).setupNavigationButton(R.string.clear,
				onclearClickListener, false);
	}

	private OnClickListener onclearClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO get and delete only favourite bikes
			readableDatabase.delete(Tables.TBL_SEARCH_PARAMS, null, null);
			cursor.requery();
			setItemsCountText(cursor.getCount());
		}
	};

	private OnItemClickListener onlistItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			saveCursorRowToDao(arg2);
			SearchActivity searchActivity = (SearchActivity) getActivity();
			searchActivity.onSearchClk(searchActivity.editSearchDao);
		}
	};

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
	}
}
