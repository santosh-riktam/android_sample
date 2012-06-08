package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.data.AppSession;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.BikeTypes;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.NominatimParser;
import com.riktamtech.android.sellabike.io.SavedSearchParser;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.Utils;
import com.riktamtech.android.sellabike.widget.CircleIndicator;
import com.riktamtech.android.sellabike.widget.MyGallery;

public class BikeDetailsFragment extends Fragment {
	private BikeDAO bikeDAO;
	private TextView bikeNameTextView, priceTextView, yearTextView, featureTextView, milesTextView, engineTextView, absTextView, ownerTextView, distanceTextView,
			descriptionTextView, fromTextView, viewOnMapTextView;
	private ImageView saveForLaterImageView, contactSellerImageView;
	private MyGallery gallery;
	private DBManager dbManager;
	private double width = DisplayUtils.dispayWidth / 1;
	private double height = width * 2 / 3.6;
	private CircleIndicator circleIndicator;

	BikeDetailsFragment(BikeDAO bikeDAO) {
		this.bikeDAO = bikeDAO;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_bike_details, container, false);
		dbManager = new DBManager(getActivity());
		circleIndicator = (CircleIndicator) inflate.findViewById(R.id.circleIndicator1);
		circleIndicator.init(0, 0, R.drawable.dot_select_page7, R.drawable.dot_unselect_page7);

		gallery = (MyGallery) inflate.findViewById(R.id.gallery);
		gallery.setSpacing(2);
		gallery.setOnItemClickListener(galleryItemClickListener);
		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				circleIndicator.setCurrentIndex(arg2);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		bikeNameTextView = (TextView) inflate.findViewById(R.id.bikeNameTextView);
		priceTextView = (TextView) inflate.findViewById(R.id.priceTextView);
		yearTextView = (TextView) inflate.findViewById(R.id.year_bike_details);
		distanceTextView = (TextView) inflate.findViewById(R.id.distance_text_bikedetails);
		fromTextView = (TextView) inflate.findViewById(R.id.text_from_bikedetails);
		descriptionTextView = (TextView) inflate.findViewById(R.id.description_bike_details);
		milesTextView = (TextView) inflate.findViewById(R.id.miles_bike_details);
		engineTextView = (TextView) inflate.findViewById(R.id.engine_bike_details);
		absTextView = (TextView) inflate.findViewById(R.id.abs_bike_details);
		ownerTextView = (TextView) inflate.findViewById(R.id.owner_bike_details);
		featureTextView = (TextView) inflate.findViewById(R.id.feature_bike_details);
		viewOnMapTextView = (TextView) inflate.findViewById(R.id.viewonmap_textview);
		viewOnMapTextView.setOnClickListener(viewOnMapClickListener);

		contactSellerImageView = (ImageView) inflate.findViewById(R.id.contactSellerView);
		contactSellerImageView.setOnClickListener(contactSellerClkListener);

		inflate.findViewById(R.id.share_bike_details).setOnClickListener(shareClickListener);
		saveForLaterImageView = (ImageView) inflate.findViewById(R.id.saveforlater_bike_details);
		saveForLaterImageView.setOnClickListener(saveForLaterClickListener);
		saveForLaterImageView.setEnabled(!dbManager.isBikeInDB(bikeDAO.bikeId));

		// set photos

		if (bikeDAO.photos != null && bikeDAO.photos.size() > 0) {
			if (bikeDAO.photos.size() == 1)
				circleIndicator.setVisibility(View.GONE);
			gallery.setAdapter(new ImageAdapter(getActivity(), bikeDAO.photos));
			circleIndicator.setCount(bikeDAO.photos.size());
		} else if (bikeDAO.thumbs != null && bikeDAO.thumbs.size() > 0) {
			gallery.setAdapter(new GalleryAdapter(getActivity(), bikeDAO.thumbs));
			circleIndicator.setCount(bikeDAO.thumbs.size());
		} else {
			Toast.makeText(getActivity(), "No Images found", Toast.LENGTH_SHORT).show();
			circleIndicator.setVisibility(View.GONE);
		}

		updateViews();
		return inflate;
	}

	public void updateViews() {
		bikeNameTextView.setText(bikeDAO.bikeName + "");

		if (bikeDAO.price == 0)
			priceTextView.setVisibility(View.GONE);
		else
			priceTextView.setText("\u00A3 " + bikeDAO.price);
		if (bikeDAO.year == 0)
			yearTextView.setVisibility(View.GONE);
		else
			yearTextView.setText(bikeDAO.year + "");

		if (bikeDAO.isGumtree)
			contactSellerImageView.setImageResource(R.drawable.btn_contactseller_gumtree);

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
		if (AppSession.currentUserLocation != null) {
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
		distanceTextView.setText("");
		fromTextView.setText("");
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		ArrayList<Photo> images = new ArrayList<Photo>();

		public ImageAdapter(Context c, ArrayList<Photo> images) {
			mContext = c;
			this.images = images;
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null)
				imageView = new ImageView(mContext);

			((BaseActivity) getActivity()).setImage(imageView, bikeDAO.photos.get(position).url, (int) width, (int) height);
			return imageView;
		}

	}

	private class GalleryAdapter extends BaseAdapter {
		private Context mContext;
		ArrayList<String> images = new ArrayList<String>();

		public GalleryAdapter(Context c, ArrayList<String> images) {
			mContext = c;
			this.images = images;
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null)
				imageView = new ImageView(mContext);

			((BaseActivity) getActivity()).setImage(imageView, bikeDAO.thumbs.get(position), (int) width, (int) height);

			return imageView;
		}

	}

	OnItemClickListener galleryItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			((BaseActivity) getActivity()).attachFragment(new BikePhotoGalleryFragment(bikeDAO));
		}
	};

	android.view.View.OnClickListener viewOnMapClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), MapLocationActivity.class);
			intent.putExtra("lat", bikeDAO.lat);
			intent.putExtra("lon", bikeDAO.lon);
			startActivity(intent);

		}
	};
	OnClickListener contactSellerClkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (bikeDAO.isGumtree)
				((BaseActivity) getActivity()).contactSellerGumtree(bikeDAO);
			else
				((BaseActivity) getActivity()).contactSellerClick(bikeDAO);
		}
	};

	OnClickListener shareClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			((BaseActivity) getActivity()).attachFragment(new ShareBikeDetailsFragment(bikeDAO));
		}
	};

	OnClickListener saveForLaterClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			v.setEnabled(false);
			Toast.makeText(BikeDetailsFragment.this.getActivity(), "Saving...", 0).show();
			new SavedSearchParser(getActivity()).parseXmlFromUrl(
					ServiceURLBuilder.getSavedSearchUrl(bikeDAO.makeID, bikeDAO.modelID, bikeDAO.engineID, new PrefsManager(getActivity()).getDeviceId(), Build.MODEL),
					savedSearchParseListener);
		}
	};

	private ParseListener savedSearchParseListener = new ParseListener() {

		@Override
		public void onParseComplete(Context context, Object object) {
			bikeDAO.savedSearchId = Integer.parseInt(object.toString());
			dbManager.insertBike(bikeDAO, BikeTypes.FAVOURUTE);
			Toast.makeText(context, "Bike Saved", 0).show();
		}

		@Override
		public void onError(Context context, Bundle bundle) {
			Toast.makeText(context, "Error", 0).show();
			//Exception exception=(Exception) bundle.getSerializable(ERROR);
			//Utils.sendEmail(getActivity(), "crash log", Log.getStackTraceString(exception));
		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();

	}

	public void goBack() {
		((FragmentActivity) getActivity()).getSupportFragmentManager().popBackStack();
	}
}
