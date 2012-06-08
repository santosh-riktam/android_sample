package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.data.AppSession;
import com.riktamtech.android.sellabike.data.DBHelper;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.BikeTypes;
import com.riktamtech.android.sellabike.data.DBManager.BikesCloumns;
import com.riktamtech.android.sellabike.data.DBManager.PhotosCloumns;
import com.riktamtech.android.sellabike.data.DBManager.Tables;
import com.riktamtech.android.sellabike.data.DBManager.ThumbsCloumns;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.BikeAdsParser;
import com.riktamtech.android.sellabike.io.InfoParser;
import com.riktamtech.android.sellabike.io.NominatimParser;
import com.riktamtech.android.sellabike.ui.ConfirmDialogFragment.OnDialogButtonClickListener;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.Utils;

public class FavoritesFragment extends Fragment {
	private static final String TAG = "FavoritesFragment";
	private BikeAdsParser bikeAdsParser;
	private DBManager dbManager;
	private DBHelper dbHelper;
	private final int orangeColor = Color.parseColor("#d83526");
	private Cursor cursor;
	private TextView itemsCountTextView;
	private SQLiteDatabase readableDatabase;
	private ListView listView;
	private PrefsManager prefsManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		dbManager = new DBManager(getActivity().getApplicationContext());
		prefsManager = new PrefsManager(getActivity());

		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_favorites, container,
				false);
		listView = (ListView) inflate.findViewById(R.id.favorites_listView);

		listView.setOnItemClickListener(favoriteClickListener);
		itemsCountTextView = (TextView) inflate
				.findViewById(R.id.itemsCountTextView);

		return inflate;
	}

	public void onPause() {
		super.onPause();
		cursor.close();
		readableDatabase.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		readableDatabase = new DBHelper(getActivity().getApplicationContext())
				.getReadableDatabase();
		cursor = readableDatabase.query(Tables.TBL_BIKE,
				FavoritesQuery.columns, BikesCloumns.bike_type + "=?",
				new String[] { BikeTypes.FAVOURUTE + "" }, null, null,
				BikesCloumns.insert_time + " desc");
		listView.setAdapter(new FavoritesAdapter(getActivity()
				.getApplicationContext(), cursor, false));
		setItemsCountText(cursor.getCount());
	}

	private void setItemsCountText(int count) {
		String countString = "";
		if (count <= 0)
			countString = getResources().getString(R.string.no_fav_bikes);
		if (count > 0)
			countString = count + " bike";
		if (count > 1)
			countString += "s";
		itemsCountTextView.setText(countString);
	}

	private interface FavoritesThumbsQuery {
		String columns[] = { ThumbsCloumns.BIKE_ID, ThumbsCloumns.THUMB_URL };
		int BIKE_ID = 0;
		int THUMB_URL = 1;
	}

	private interface FavouritePhotosQuery {
		String columns[] = { PhotosCloumns.BIKE_ID,
				PhotosCloumns.PHOTO_CAPTION, PhotosCloumns.PHOTO_ID,
				PhotosCloumns.PHOTO_URL };
		int BIKE_ID = 0, PHOTO_CAPTION = 1, PHOTO_ID = 2, PHOTO_URL = 3;
	}

	private interface FavoritesQuery {
		String columns[] = { BikesCloumns.bike_id, BikesCloumns.bike_name,
				BikesCloumns.color_id, BikesCloumns.description,
				BikesCloumns.distance, BikesCloumns.email,
				BikesCloumns.engine_id, BikesCloumns.engine_size,
				BikesCloumns.features, BikesCloumns.first_name,
				BikesCloumns.is_gum_tree, BikesCloumns.is_premium,
				BikesCloumns.last_name, BikesCloumns.lat, BikesCloumns.lon,
				BikesCloumns.make_id, BikesCloumns.mileage,
				BikesCloumns.model_id, BikesCloumns.price, BikesCloumns.reg_no,
				BikesCloumns.seller_id, BikesCloumns.seller_url,
				BikesCloumns.telephone, BikesCloumns.year,
				BikesCloumns.saved_search_id };
		int bike_id = 0;
		int bike_name = 1;
		int color_id = 2;
		int description = 3;
		int distance = 4;
		int email = 5;
		int engine_id = 6;
		int engine_size = 7;
		int features = 8;
		int first_name = 9;
		int is_gum_tree = 10;
		int is_premium = 11;
		int last_name = 12;
		int lat = 13;
		int lon = 14;
		int make_id = 15;
		int mileage = 16;
		int model_id = 17;
		int price = 18;
		int reg_no = 19;
		int seller_id = 20;
		int seller_url = 21;
		int telephone = 22;
		int year = 23;
		int saved_search_id = 24;
	}

	class FavoritesAdapter extends CursorAdapter {

		public FavoritesAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public void bindView(View arg0, Context arg1, final Cursor arg2) {
			final ViewHolder holder = (ViewHolder) arg0.getTag();

			holder.bikeName_Price_TextView.setText(cursor
					.getString(FavoritesQuery.bike_name)
					+ " - \u00A3 "
					+ cursor.getString(FavoritesQuery.price));
			holder.otherDetailsTextView.setText(cursor
					.getString(FavoritesQuery.year)
					+ ", "
					+ cursor.getInt(FavoritesQuery.engine_size)
					+ " cc, "
					+ cursor.getString(FavoritesQuery.mileage)
					+ " miles, "
					+ cursor.getString(FavoritesQuery.features));

			if (AppSession.currentUserLocation != null) {
				holder.distanceTextView.setText("Distance - "
						+ Utils.distanceTo(arg2.getDouble(FavoritesQuery.lat),
								arg2.getDouble(FavoritesQuery.lon)) + " miles");
				new NominatimParser(getActivity()).parseXmlFromUrl(
						ServiceURLBuilder.getReverseGeoCoderUrl(
								AppSession.currentUserLocation.getLatitude(),
								AppSession.currentUserLocation.getLongitude()),
						new ParseListener() {

							@Override
							public void onParseComplete(Context context,
									Object object) {
								String text = holder.distanceTextView.getText().toString();

								if (object != null)
									text += " (from " + object + ")";

								holder.distanceTextView.setText(text);
								text = holder.distanceTextView.getText()
										.toString();
								SpannableString spannable = new SpannableString(
										text);
								try {
								spannable.setSpan(
										new ForegroundColorSpan(getActivity()
												.getResources().getColor(
														R.color.light_gray)),
										text.indexOf("("),
										text.length(),
										SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
								holder.distanceTextView.setText(spannable);
								}catch (Exception e) {
									e.printStackTrace();
									holder.distanceTextView.setText(text);
								}
							}

							@Override
							public void onError(Context context, Bundle bundle) {
							}
						});
			} else {
				holder.distanceTextView.setText("");
			}
			int bike_id = cursor.getInt(FavoritesQuery.bike_id);
			holder.removeImageView.setOnClickListener(removeImageClkListener);

			holder.removeImageView.setTag(arg2.getPosition());

			String text = holder.bikeName_Price_TextView.getText().toString();
			SpannableString spannableString = new SpannableString(text);
			spannableString.setSpan(new ForegroundColorSpan(getActivity()
					.getResources().getColor(R.color.orange)), text
					.indexOf("-") + 1, text.length(),
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			holder.bikeName_Price_TextView.setText(spannableString);

			Cursor thumbsCursor = readableDatabase.query(Tables.TBL_THUMBS,
					FavoritesThumbsQuery.columns, ThumbsCloumns.BIKE_ID + "=?",
					new String[] { bike_id + "" }, null, null, null);

			if (thumbsCursor.getCount() > 0) {
				thumbsCursor.moveToFirst();
				String thumbUrl = thumbsCursor
						.getString(FavoritesThumbsQuery.THUMB_URL);
				if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl)) {
					((BaseActivity) getActivity()).setImage(listView,
							holder.bikeImageView, cursor.getPosition(),
							thumbUrl, DisplayUtils.dispayWidth / 4,
							DisplayUtils.dispayWidth / 6);

				}
			}
			thumbsCursor.close();

			// bikeImageView.setImageURI(Uri.parse(cursor2
			// .getString(FavoritesThumbsQuery.THUMB_URL)));
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			LayoutInflater inflater = LayoutInflater.from(getActivity()
					.getApplicationContext());
			View view = inflater.inflate(R.layout.list_row_favorites, arg2,
					false);
			ViewHolder holder = new ViewHolder(view);
			view.setTag(holder);
			return view;
		}

	}

	private class ViewHolder {
		TextView bikeName_Price_TextView, otherDetailsTextView,
				distanceTextView;
		ImageView bikeImageView, removeImageView;

		public ViewHolder(View arg0) {
			bikeName_Price_TextView = (TextView) arg0
					.findViewById(R.id.TextView1_favorites);
			distanceTextView = (TextView) arg0
					.findViewById(R.id.TextView3_favorites);
			otherDetailsTextView = (TextView) arg0
					.findViewById(R.id.TextView2_favorites);
			bikeImageView = (ImageView) arg0
					.findViewById(R.id.favouriteBikeImageView);
			removeImageView = (ImageView) arg0
					.findViewById(R.id.remove_favorites);

		}
	}

	OnClickListener removeImageClkListener = new OnClickListener() {

		@Override
		public void onClick(final View v) {
			// TODO remove
			// db manager.
			ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment
					.newInstance(R.string.alert, R.string.confirm_del_fav,
							R.string.yes, R.string.no);
			confirmDialogFragment
					.setOnDialogButtonClickListener(new OnDialogButtonClickListener() {

						@Override
						public void doPositiveClick() {

							int position = (Integer) v.getTag();
							cursor.moveToPosition(position);
							int bikeId = cursor.getInt(FavoritesQuery.bike_id);
							int savedSearchId = cursor
									.getInt(FavoritesQuery.saved_search_id);

							SQLiteDatabase database = new DBHelper(
									getActivity()).getWritableDatabase();
							int affectedRowCount = database.delete(
									Tables.TBL_BIKE, BikesCloumns.bike_id
											+ "=?",
									new String[] { bikeId + "" });
							database.delete(Tables.TBL_THUMBS,
									ThumbsCloumns.BIKE_ID + "=?",
									new String[] { bikeId + "" });
							database.delete(Tables.TBL_PHOTOS,
									PhotosCloumns.BIKE_ID + "=?",
									new String[] { bikeId + "" });
							database.close();
							Log.d(TAG, affectedRowCount + " rows deleted");
							refreshWidgets();
							new InfoParser(getActivity()).parseXmlFromUrl(
									ServiceURLBuilder.getRemoveSearchUrl(
											savedSearchId,
											prefsManager.getDeviceId()),
									deleteParseListener);
						}

						@Override
						public void doNegativeClick() {
						}
					});
			confirmDialogFragment.show(getActivity()
					.getSupportFragmentManager(), "dialog");
		}
	};

	ParseListener deleteParseListener = new ParseListener() {

		@Override
		public void onParseComplete(Context context, Object object) {

		}

		@Override
		public void onError(Context context, Bundle bundle) {
		}
	};

	OnClickListener clearClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (cursor.getCount() > 0) {
				ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment
						.newInstance(R.string.alert,
								R.string.confirm_clear_favs, R.string.yes,
								R.string.no);
				confirmDialogFragment
						.setOnDialogButtonClickListener(new OnDialogButtonClickListener() {

							@Override
							public void doPositiveClick() {

								cursor.moveToFirst();
								do {
									new InfoParser(getActivity()).parseXmlFromUrl(
											ServiceURLBuilder
													.getRemoveSearchUrl(
															cursor.getInt(FavoritesQuery.saved_search_id),
															prefsManager
																	.getDeviceId()),
											deleteParseListener);
								} while (cursor.moveToNext());

								String selectBikesQueryString = "select "
										+ BikesCloumns.bike_id + " from "
										+ Tables.TBL_BIKE + " where "
										+ BikesCloumns.bike_type + "="
										+ BikeTypes.FAVOURUTE;
								String deleteBikesQueryString = "delete from "
										+ Tables.TBL_BIKE + " where "
										+ BikesCloumns.bike_type + "="
										+ BikeTypes.FAVOURUTE;
								String deleteThumbsQueryString = "delete from "
										+ Tables.TBL_THUMBS + " where "
										+ ThumbsCloumns.BIKE_ID + " in ("
										+ selectBikesQueryString + ")";
								String deletePhotosQueryString = "delete from "
										+ Tables.TBL_PHOTOS + " where "
										+ PhotosCloumns.BIKE_ID + " in ("
										+ selectBikesQueryString + ")";

								SQLiteDatabase database = new DBHelper(
										getActivity()).getWritableDatabase();
								database.execSQL(deleteThumbsQueryString);
								database.execSQL(deletePhotosQueryString);
								database.execSQL(deleteBikesQueryString);

								database.close();

								Log.d(TAG, "All rows deleted");
								refreshWidgets();
							}

							@Override
							public void doNegativeClick() {
							}
						});
				confirmDialogFragment.show(getActivity()
						.getSupportFragmentManager(), "dialog");
			}
		}
	};

	OnItemClickListener favoriteClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// ((SearchActivity)
			// getActivity()).showFavoriteDetails(cursor.getInt(FavoritesQuery.bike_id));
			cursor.moveToPosition(arg2);
			int bike_id = cursor.getInt(FavoritesQuery.bike_id);

			ArrayList<Photo> photos = new ArrayList<Photo>();
			Cursor photosCursor = readableDatabase.query(Tables.TBL_PHOTOS,
					FavouritePhotosQuery.columns, PhotosCloumns.BIKE_ID + "=?",
					new String[] { bike_id + "" }, null, null, null);
			if (photosCursor.getCount() > 0) {
				photosCursor.moveToFirst();
				do {
					photos.add(new Photo(
							photosCursor
									.getString(FavouritePhotosQuery.PHOTO_URL),
							photosCursor
									.getString(FavouritePhotosQuery.PHOTO_CAPTION)));
				} while (photosCursor.moveToNext());
			}
			photosCursor.close();
			Cursor thumbsCursor = readableDatabase.query(Tables.TBL_THUMBS,
					FavoritesThumbsQuery.columns, ThumbsCloumns.BIKE_ID + "=?",
					new String[] { bike_id + "" }, null, null, null);
			ArrayList<String> thumbs = new ArrayList<String>();
			if (thumbsCursor.getCount() > 0) {
				thumbsCursor.moveToFirst();
				do {
					String thumbUrl = thumbsCursor
							.getString(FavoritesThumbsQuery.THUMB_URL);
					if (thumbUrl != null && !TextUtils.isEmpty(thumbUrl))
						thumbs.add(thumbUrl);
				} while (thumbsCursor.moveToNext());
			}
			thumbsCursor.close();
			BikeDAO bikeDAO = new BikeDAO(
					cursor.getInt(FavoritesQuery.engine_id),
					cursor.getInt(FavoritesQuery.make_id),
					cursor.getInt(FavoritesQuery.model_id),
					cursor.getInt(FavoritesQuery.year), BikeTypes.FAVOURUTE,
					cursor.getInt(FavoritesQuery.engine_size), bike_id,
					cursor.getInt(FavoritesQuery.seller_id),
					cursor.getInt(FavoritesQuery.mileage),
					cursor.getInt(FavoritesQuery.color_id),
					cursor.getString(FavoritesQuery.bike_name),
					cursor.getString(FavoritesQuery.reg_no),
					cursor.getString(FavoritesQuery.features),
					cursor.getString(FavoritesQuery.description),
					cursor.getString(FavoritesQuery.first_name),
					cursor.getString(FavoritesQuery.last_name),
					cursor.getString(FavoritesQuery.email),
					cursor.getString(FavoritesQuery.telephone),
					cursor.getString(FavoritesQuery.seller_url),
					cursor.getInt(FavoritesQuery.is_gum_tree) > 0,
					cursor.getInt(FavoritesQuery.is_premium) > 0,
					cursor.getFloat(FavoritesQuery.lat),
					cursor.getFloat(FavoritesQuery.lon),
					cursor.getFloat(FavoritesQuery.price), photos, thumbs);
			((SearchActivity) getActivity()).showBikeDetails(bikeDAO);
		}
	};

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		((BaseActivity) getActivity()).setupNavigationButton(R.string.clear,
				clearClickListener, false);
	}

	protected void refreshWidgets() {
		((CursorAdapter) listView.getAdapter()).getCursor().requery();
		int count = cursor.getCount();
		setItemsCountText(count);
		// TODO diaable clear button if count is 0, possible?

	}

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
	}

}
