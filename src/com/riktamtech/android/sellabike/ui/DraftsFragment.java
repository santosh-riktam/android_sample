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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.data.DBHelper;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.BikeTypes;
import com.riktamtech.android.sellabike.data.DBManager.BikesCloumns;
import com.riktamtech.android.sellabike.data.DBManager.PhotosCloumns;
import com.riktamtech.android.sellabike.data.DBManager.Tables;
import com.riktamtech.android.sellabike.data.DBManager.ThumbsCloumns;

public class DraftsFragment extends Fragment {
	protected static final String TAG = "DraftsFragment";
	private DBManager dbManager;
	private Cursor cursor;
	private ListView listView;
	private SQLiteDatabase readableDatabase;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		dbManager = new DBManager(getActivity().getApplicationContext());
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_drafts, container, false);
		listView = (ListView) inflate.findViewById(R.id.listView_drafts);
		listView.setOnItemClickListener(onlistItemClickListener);
		return inflate;
	}

	@Override
	public void onResume() {
		super.onResume();
		readableDatabase = new DBHelper(getActivity().getApplicationContext()).getReadableDatabase();
		cursor = readableDatabase.query(Tables.TBL_BIKE, DraftsQuery.columns, BikesCloumns.bike_type + "=?", new String[] { BikeTypes.PENDING_FOR_UPLOAD + "" }, null, null, null);
		listView.setAdapter(new DraftsAdapter(getActivity()));
	}

	class DraftsAdapter extends CursorAdapter {

		public DraftsAdapter(Context context) {
			super(context, cursor, true);
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			if (arg0 == null) {
				arg0 = getActivity().getLayoutInflater().inflate(R.layout.list_drats, null, false);
			}
			TextView titleTextView, detailsTextView;
			titleTextView = (TextView) arg0.findViewById(R.id.title_dratfs);
			detailsTextView = (TextView) arg0.findViewById(R.id.details_dratfs);
			// TODO edit text views
			titleTextView.setText(arg2.getString(DraftsQuery.draft_name));
			String make, model, engine;
			make = dbManager.getMake(arg2.getInt(DraftsQuery.make_id)).name;
			model = dbManager.getModel(arg2.getInt(DraftsQuery.model_id)).name;
			engine = dbManager.getEngine(arg2.getInt(DraftsQuery.engine_id)).name;
			String text = "";
			if (make != null && !TextUtils.isEmpty(make)) {
				text += make + ", ";
			}
			if (model != null && !TextUtils.isEmpty(model))
				text += model + ", ";
			text += engine + "";
			detailsTextView.setText(text);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
			View view = inflater.inflate(R.layout.list_drats, arg2, false);
			return view;
		}

	}

	private OnItemClickListener onlistItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			saveCursorRowToDao(arg2);
			((SellActivity) getActivity()).showSellForm();
			//getActivity().getSupportFragmentManager().popBackStack();
		}
	};

	private void saveCursorRowToDao(int position) {
		cursor.moveToPosition(position);
		BikeDAO dao = new BikeDAO();
		dao.bikeId = cursor.getInt(DraftsQuery.bike_id);
		dao.regNo = cursor.getString(DraftsQuery.reg_no);
		int colorid = cursor.getInt(DraftsQuery.color_id);
		dao.setColorId(colorid, dbManager.getColour(colorid).name);
		dao.year = cursor.getInt(DraftsQuery.color_id);
		dao.mileage = cursor.getInt(DraftsQuery.mileage);
		dao.engineID = cursor.getInt(DraftsQuery.engine_id);
		String features = cursor.getString(DraftsQuery.features);
		if (features != null && !TextUtils.isEmpty(features)) {
			// making list of indices from features string
			List<String> featuresList = Arrays.asList(getResources().getStringArray(R.array.static_features_array));
			StringTokenizer tokenizer = new StringTokenizer(features, ",", false);
			ArrayList<Integer> featuresIndices = new ArrayList<Integer>();
			while (tokenizer.hasMoreTokens()) {
				featuresIndices.add(featuresList.indexOf(tokenizer.nextToken()));
			}
			dao.setSelectedFeatures(featuresIndices);
		} else {
			dao.setSelectedFeatures(new ArrayList<Integer>());
		}
		dao.makeID = cursor.getInt(DraftsQuery.make_id);
		dao.modelID = cursor.getInt(DraftsQuery.model_id);
		dao.bikeName = cursor.getString(DraftsQuery.bike_name);
		dao.description = cursor.getString(DraftsQuery.description);
		dao.price = cursor.getInt(DraftsQuery.price);
		dao.sellerId = cursor.getInt(DraftsQuery.seller_id);
		dao.lat = cursor.getDouble(DraftsQuery.lat);
		dao.lon = cursor.getDouble(DraftsQuery.lon);
		dao.draftName = cursor.getString(DraftsQuery.draft_name);

		dao.firstName = cursor.getString(DraftsQuery.first_name);
		dao.lastName = cursor.getString(DraftsQuery.last_name);
		dao.email = cursor.getString(DraftsQuery.email);
		dao.telephone = cursor.getString(DraftsQuery.telephone);

		int bike_id = cursor.getInt(DraftsQuery.bike_id);

		ArrayList<Photo> photos = new ArrayList<Photo>();
		Cursor photosCursor = readableDatabase.query(Tables.TBL_PHOTOS, BikePhotosQuery.columns, PhotosCloumns.BIKE_ID + "=?", new String[] { bike_id + "" }, null, null, null);
		if (photosCursor.getCount() > 0) {
			photosCursor.moveToFirst();
			do {
				photos.add(new Photo(photosCursor.getString(BikePhotosQuery.PHOTO_URL), photosCursor.getString(BikePhotosQuery.PHOTO_CAPTION)));
			} while (photosCursor.moveToNext());
		}
		photosCursor.close();
		Cursor thumbsCursor = readableDatabase.query(Tables.TBL_THUMBS, BikeThumbsQuery.columns, ThumbsCloumns.BIKE_ID + "=?", new String[] { bike_id + "" }, null, null, null);
		ArrayList<String> thumbs = new ArrayList<String>();
		if (thumbsCursor.getCount() > 0) {
			thumbsCursor.moveToFirst();
			do {
				String thumbUrl = thumbsCursor.getString(BikeThumbsQuery.THUMB_URL);
				if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl))
					thumbs.add(thumbUrl);
			} while (thumbsCursor.moveToNext());
		}
		thumbsCursor.close();
		dao.thumbs = thumbs;
		dao.photos = photos;
		((SellActivity) getActivity()).setDao(dao);
	}

	public void goBack() {
		((FragmentActivity) getActivity()).getSupportFragmentManager().popBackStack();
	}

	@Override
	public void onPause() {
		super.onPause();
		cursor.close();
		readableDatabase.close();
	}

	private interface DraftsQuery {
		String columns[] = { BikesCloumns.bike_id, BikesCloumns.bike_name, BikesCloumns.bike_type, BikesCloumns.color_id, BikesCloumns.description, BikesCloumns.distance,
				BikesCloumns.draft_name, BikesCloumns.email, BikesCloumns.engine_id, BikesCloumns.engine_size, BikesCloumns.features, BikesCloumns.first_name,
				BikesCloumns.insert_time, BikesCloumns.is_gum_tree, BikesCloumns.is_premium, BikesCloumns.last_name, BikesCloumns.lat, BikesCloumns.lon, BikesCloumns.make_id,
				BikesCloumns.mileage, BikesCloumns.model_id, BikesCloumns.price, BikesCloumns.reg_no, BikesCloumns.saved_search_id, BikesCloumns.seller_id,
				BikesCloumns.seller_url, BikesCloumns.telephone, BikesCloumns.year };
		int bike_id = 0;
		int bike_name = 1;
		int bike_type = 2;
		int color_id = 3;
		int description = 4;
		int distance = 5;
		int draft_name = 6;
		int email = 7;
		int engine_id = 8;
		int engine_size = 9;
		int features = 10;
		int first_name = 11;
		int insert_time = 12;
		int is_gum_tree = 13;
		int is_premium = 14;
		int last_name = 15;
		int lat = 16;
		int lon = 17;
		int make_id = 18;
		int mileage = 19;
		int model_id = 20;
		int price = 21;
		int reg_no = 22;
		int saved_search_id = 23;
		int seller_id = 24;
		int seller_url = 25;
		int telephone = 26;
		int year = 27;
	}

	private interface BikeThumbsQuery {
		String columns[] = { ThumbsCloumns.BIKE_ID, ThumbsCloumns.THUMB_URL };
		int BIKE_ID = 0;
		int THUMB_URL = 1;
	}

	private interface BikePhotosQuery {
		String columns[] = { PhotosCloumns.BIKE_ID, PhotosCloumns.PHOTO_CAPTION, PhotosCloumns.PHOTO_ID, PhotosCloumns.PHOTO_URL };
		int BIKE_ID = 0, PHOTO_CAPTION = 1, PHOTO_ID = 2, PHOTO_URL = 3;
	}

}
