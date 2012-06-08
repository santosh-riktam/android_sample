package com.riktamtech.android.sellabike.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.data.AppSession;
import com.riktamtech.android.sellabike.data.DBHelper;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.BikesCloumns;
import com.riktamtech.android.sellabike.data.DBManager.Tables;
import com.riktamtech.android.sellabike.data.DBManager.ThumbsCloumns;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.GetBikeParser;
import com.riktamtech.android.sellabike.io.NominatimParser;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.Utils;

public class FavoritesDetailsFragment extends Fragment {
	BikeDAO bikeDAO;
	GetBikeParser getBikeParser;
	RadioGroup radioGroup;
	Cursor cursor;
	private int points;
	RadioButton rb[] = new RadioButton[9];
	TextView bikeNameTextView, priceTextView, yearTextView, featureTextView,
			milesTextView, engineTextView, absTextView, ownerTextView,
			distanceTextView, descriptionTextView, fromTextView,
			viewOnMapTextView;
	ImageView imageView[] = new ImageView[9];
	DBManager dbManager;
	SQLiteDatabase readableDatabase;
	LinearLayout bikePhotosLinearLayout;
	private int bike_id;

	FavoritesDetailsFragment(int bike_id) {
		this.bike_id = bike_id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getBikeParser = new GetBikeParser(getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getBikeParser.parseXmlFromUrl(ServiceURLBuilder.getBikeUrl(bike_id),
				getBikeParseListener);
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_favorites_details,
				container, false);
		dbManager = new DBManager(getActivity().getApplicationContext());
		radioGroup = (RadioGroup) inflate
				.findViewById(R.id.radioGroup_favorite_details);
		rb[0] = (RadioButton) inflate
				.findViewById(R.id.radio01_favorite_details);
		rb[1] = (RadioButton) inflate
				.findViewById(R.id.radio02_favorite_details);
		rb[2] = (RadioButton) inflate
				.findViewById(R.id.radio03_favorite_details);
		rb[3] = (RadioButton) inflate
				.findViewById(R.id.radio04_favorite_details);
		rb[4] = (RadioButton) inflate
				.findViewById(R.id.radio05_favorite_details);
		rb[5] = (RadioButton) inflate
				.findViewById(R.id.radio06_favorite_details);
		rb[6] = (RadioButton) inflate
				.findViewById(R.id.radio07_favorite_details);
		rb[7] = (RadioButton) inflate
				.findViewById(R.id.radio08_favorite_details);
		rb[8] = (RadioButton) inflate
				.findViewById(R.id.radio09_favorite_details);
		bikePhotosLinearLayout = (LinearLayout) inflate
				.findViewById(R.id.bikephotosLinearLayout);
		bikeNameTextView = (TextView) inflate
				.findViewById(R.id.bikenametextView);

		priceTextView = (TextView) inflate.findViewById(R.id.pricetextView);

		yearTextView = (TextView) inflate
				.findViewById(R.id.year_favorite_details);

		distanceTextView = (TextView) inflate
				.findViewById(R.id.distance_text_favoritedetails);

		fromTextView = (TextView) inflate
				.findViewById(R.id.text_from_favoritedetails);

		descriptionTextView = (TextView) inflate
				.findViewById(R.id.description_favorite_details);

		milesTextView = (TextView) inflate
				.findViewById(R.id.miles_favorite_details);

		engineTextView = (TextView) inflate
				.findViewById(R.id.engine_favorite_details);

		absTextView = (TextView) inflate
				.findViewById(R.id.abs_favorite_details);
		ownerTextView = (TextView) inflate
				.findViewById(R.id.owner_favorite_details);
		featureTextView = (TextView) inflate
				.findViewById(R.id.feature_favorite_details);

		imageView[0] = (ImageView) inflate
				.findViewById(R.id.imageView1_favorite_details);
		imageView[1] = (ImageView) inflate
				.findViewById(R.id.imageView2_favorite_details);
		imageView[2] = (ImageView) inflate
				.findViewById(R.id.imageView3_favorite_details);
		imageView[3] = (ImageView) inflate
				.findViewById(R.id.imageView4_favorite_details);
		imageView[4] = (ImageView) inflate
				.findViewById(R.id.imageView5_favorite_details);
		imageView[5] = (ImageView) inflate
				.findViewById(R.id.imageView6_favorite_details);
		imageView[6] = (ImageView) inflate
				.findViewById(R.id.ImageView7_favorite_details);
		imageView[7] = (ImageView) inflate
				.findViewById(R.id.ImageView8_favorite_details);
		imageView[8] = (ImageView) inflate
				.findViewById(R.id.ImageView9_favorite_details);

		inflate.findViewById(R.id.bikephotosLinearLayout).setOnClickListener(
				bikeOnClickListener);
		inflate.findViewById(R.id.contactSellerView).setOnClickListener(
				contactSellerClkListener);
		inflate.findViewById(R.id.share_favorite_details).setOnClickListener(
				shareClickListener);
		inflate.findViewById(R.id.saveforlater_favorite_details)
				.setOnClickListener(saveForLaterClickListener);
		 rfsh();

		return inflate;
	}

	OnClickListener bikeOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((SearchActivity) getActivity()).showBikePhotos(bikeDAO);
		}
	};
	OnClickListener contactSellerClkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((SearchActivity) getActivity()).contactSellerClick(bikeDAO);
		}
	};
	OnClickListener shareClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((SearchActivity) getActivity()).showShareOptions(bikeDAO);
		}
	};
	OnClickListener saveForLaterClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Toast.makeText(FavoritesDetailsFragment.this.getActivity(),
					"Saved Already", 0).show();

		}
	};

	@Override
	public void onResume() {
		super.onResume();
		readableDatabase = new DBHelper(getActivity().getApplicationContext())
				.getReadableDatabase();
		cursor = readableDatabase.query(Tables.TBL_BIKE,
				FavoritesQuery.columns, FavoritesQuery.bike_id + "=?",
				new String[] { bike_id + "" }, null, null, null);
		cursor.moveToFirst();
	}

	@Override
	public void onStart() {
		super.onStart();
		((BaseActivity) getActivity()).setupNavigationButton(R.string.back,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						goBack();
					}
				}, true);
	}

	public void onPause() {
		super.onPause();
		cursor.close();
		readableDatabase.close();
	}

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();

	}

	public void goBack() {
		((FragmentActivity) getActivity()).getSupportFragmentManager()
				.popBackStack();
	}

	protected void rfsh() {
		bikeNameTextView.setText(""
				+ cursor.getString(FavoritesQuery.bike_name));
		priceTextView.setText("\u00A3 "
				+ cursor.getString(FavoritesQuery.price));
		yearTextView.setText("" + getString(FavoritesQuery.year));
		// TODO set distance and "from...."
		if (AppSession.currentUserLocation != null) {
			distanceTextView.setText("");
			new NominatimParser(getActivity()).parseXmlFromUrl(
					ServiceURLBuilder.getReverseGeoCoderUrl(AppSession.currentUserLocation.getLatitude(), AppSession.currentUserLocation.getLongitude()), new ParseListener() {

						@Override
						public void onParseComplete(Context context, Object object) {
							distanceTextView.setText("Distance - " + Utils.distanceTo(bikeDAO.lat, bikeDAO.lon) + " miles ");
							if(object!=null)
								fromTextView.setText("from " + object);
								
						}

						@Override
						public void onError(Context context, Bundle bundle) {
						}
					});
		}
		descriptionTextView.setText(""
				+ cursor.getString(FavoritesQuery.description));
		milesTextView.setText("" + cursor.getString(FavoritesQuery.mileage)
				+ " miles");
		engineTextView.setText(cursor.getString(FavoritesQuery.engine_size)
				+ " cc");
		absTextView.setVisibility(View.GONE);
		featureTextView.setVisibility(View.GONE);
		ownerTextView.setVisibility(View.GONE);
	}

	private ParseListener getBikeParseListener = new ParseListener() {
		@Override
		public void onParseComplete(Context context, Object object) {

			bikeDAO = (BikeDAO) object;
			bikeNameTextView.setText(bikeDAO.bikeName + "");

			if (bikeDAO.price == 0)
				priceTextView.setVisibility(View.GONE);
			else
				priceTextView.setText("\u00A3 " + bikeDAO.price);
			if (bikeDAO.year == 0)
				yearTextView.setVisibility(View.GONE);
			else
				yearTextView.setText(bikeDAO.year + "");
			if (bikeDAO.engineSize == 0)
				engineTextView.setVisibility(View.GONE);
			else
				engineTextView.setText(bikeDAO.engineSize + "cc");
			if (bikeDAO.mileage == 0)
				milesTextView.setVisibility(View.GONE);
			else
				milesTextView.setText(bikeDAO.mileage + "miles");
			if (bikeDAO.features == "") {
				featureTextView.setVisibility(View.GONE);
				absTextView.setVisibility(View.GONE);
				ownerTextView.setVisibility(View.GONE);
			} else
				featureTextView.setText(bikeDAO.features);
			if (bikeDAO.description == null)
				descriptionTextView.setVisibility(View.GONE);
			else
				descriptionTextView.setText(bikeDAO.description);

			// TODO set distance and "from...."

			// set photos
			DisplayUtils displayUtils = new DisplayUtils(getActivity());
			double width = displayUtils.dispayWidth / 1;
			double height = width * 2 / 3.4;
			if (bikeDAO.photos.size() > 0) {

				for (int i = 0; i < bikeDAO.photos.size(); i++) {
					((BaseActivity) getActivity()).setImage(imageView[i],
							bikeDAO.photos.get(i).toString(), (int) width,
							(int) height);
				}
				points = bikeDAO.photos.size();
				for (int i = bikeDAO.photos.size(); i < 9; i++)
					imageView[i].setVisibility(View.GONE);
			} else if (bikeDAO.thumbs.size() > 0) {

				((BaseActivity) getActivity()).setImage(imageView[0],
						bikeDAO.thumbs.get(0), (int) width, (int) height);
				for (int i = 1; i < 9; i++)
					imageView[i].setVisibility(View.GONE);
				points = 1;
			} else {
				bikePhotosLinearLayout.removeAllViews();
				points = 0;
				Toast.makeText(getActivity(), "No Images for this bike",
						Toast.LENGTH_SHORT).show();
			}
			if (points == 0) {
				radioGroup.setVisibility(View.GONE);
			} else if (points == 1) {
				radioGroup.setVisibility(View.GONE);
			} else {

			}
		}

		@Override
		public void onError(Context context, Bundle bundle) {
			Toast.makeText(
					context,
					"error loading :  "
							+ bundle.getSerializable(ERROR).toString(),
					Toast.LENGTH_SHORT).show();
			Log.d("sab", "error loading :  "
					+ bundle.getSerializable(ERROR).toString());
		}
	};

	private interface FavoritesThumbsQuery {
		String columns[] = { ThumbsCloumns.BIKE_ID, ThumbsCloumns.THUMB_URL };
		int BIKE_ID = 0;
		int THUMB_URL = 1;
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
				BikesCloumns.telephone, BikesCloumns.year };
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

	}

}
