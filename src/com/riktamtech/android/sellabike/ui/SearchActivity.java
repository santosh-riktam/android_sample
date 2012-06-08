package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.SearchParamsDAO;
import com.riktamtech.android.sellabike.data.AppSession;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.GetBikeParser;
import com.riktamtech.android.sellabike.io.NominatimParser;
import com.riktamtech.android.sellabike.io.NominatimSearchParser;
import com.riktamtech.android.sellabike.widget.PickerSpinner;

/**
 * @author Santosh Kumar D activity containing the functionality for search module
 * 
 */
public class SearchActivity extends BaseActivity {

	private SearchFormFragment searchFormFragment;
	public SearchParamsDAO searchParamsDAO, editSearchDao;
	public ArrayList<BikeDAO> premiumBikes;
	public boolean isSearchSaved = true;
	private ProgressDialog progressDialog;
	private static final String TAG = "SearchActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		searchParamsDAO = new SearchParamsDAO();
		editSearchDao = new SearchParamsDAO();
		premiumBikes = new ArrayList<BikeDAO>();
		showSearchForm(false);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Bundle bundle = getIntent().getExtras();

		String bikeIdString = null;
		int bikeId = -1;
		if (bundle != null && bundle.getString("bikeId") != null) {
			bikeIdString = bundle.getString("bikeId");
			if (bikeIdString != null && !bikeIdString.equals("")) {
				try {
					bikeId = Integer.parseInt(bikeIdString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (bikeId > -1) {
			progressDialog = ProgressDialog.show(this, null, "loading bike details....");
			progressDialog.setCanceledOnTouchOutside(true);

			new GetBikeParser(this).parseXmlFromUrl(ServiceURLBuilder.getBikeUrl(bikeId), new ParseListener() {

				@Override
				public void onParseComplete(Context context, Object object) {
					if (progressDialog != null)
						progressDialog.dismiss();
					BikeDAO bikeDAO = (BikeDAO) object;
					Log.d(TAG, bikeDAO.thumbs + "");
					Log.d(TAG, bikeDAO.photos + "");
					showBikeDetails(bikeDAO);
				}

				@Override
				public void onError(Context context, Bundle bundle) {
					if (progressDialog != null)
						progressDialog.dismiss();

				}
			});

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO disable / enable myads dynamically
		getMenuInflater().inflate(R.menu.search_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_myads:
			showMyAds();
			break;
		case R.id.item_savedBikes:
			showFavourites();
			break;
		case R.id.item_savedSearches:
			showSavedSearches();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean isShowingSearchForm() {
		return searchFormFragment.isAdded();
	}

	public void showSearchForm(boolean isEditing) {
		popBackStackCompletely();
		searchFormFragment = new SearchFormFragment(isEditing);
		attachFragment(searchFormFragment, false, BackStackNames.root);
	}

	public void onSearchClk(SearchParamsDAO searchParamsDAO) {
		this.searchParamsDAO = searchParamsDAO;
		popBackStackCompletely();

		if (searchParamsDAO.lat == -1) {
			if (searchParamsDAO.postCode != null && !searchParamsDAO.postCode.trim().equals("")) {
				new NominatimSearchParser(this).parseXmlFromUrl(ServiceURLBuilder.getNominatimSearchUrl(searchParamsDAO.postCode), new ParseListener() {

					@Override
					public void onParseComplete(Context context, Object object) {
						ArrayList<HashMap<String, Double>> points = (ArrayList<HashMap<String, Double>>) object;
						HashMap<String, Double> hashMap = points.get(0);
						SearchActivity.this.searchParamsDAO.lat = hashMap.get("lat");
						SearchActivity.this.searchParamsDAO.lon = hashMap.get("lon");
						attachFragment(new SearchResultFragment(SearchActivity.this.searchParamsDAO));
					}

					@Override
					public void onError(Context context, Bundle bundle) {
						Toast.makeText(SearchActivity.this, "Failed to get location from postcode. Pls try again later.", 0).show();
					}
				});

			} else {
				Toast.makeText(this, "Location is required", 0).show();
			}
		} else {
			attachFragment(new SearchResultFragment(SearchActivity.this.searchParamsDAO));
		}

	}

	public void bikeClicked(View v) {
		// PremiumAdsView premiumAdsView=(PremiumAdsView) v;
		try {
			BikeDAO bikeDAO = (BikeDAO) v.getTag();
			showBikeDetails(bikeDAO);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void tblRowClk(View v) {
		ViewGroup tblRow = (TableRow) v;
		for (int i = 0; i < tblRow.getChildCount(); i++) {
			View child = tblRow.getChildAt(i);
			if (child instanceof PickerSpinner)
				child.performClick();
		}
	}

	public void showBikeDetails(BikeDAO bike) {
		attachFragment(new BikeDetailsFragment(bike));

	}

	public void showBikePhotos(BikeDAO bikeDAO) {
		attachFragment(new BikePhotoGalleryFragment(bikeDAO));
	}

	public void showShareOptions(BikeDAO bikeDAO) {
		attachFragment(new ShareBikeDetailsFragment(bikeDAO));
	}

	private void popBackStackCompletely() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
			int id = fragmentManager.getBackStackEntryAt(0).getId();
			fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}

	}

	public void showFavourites() {
		// popTopMenuItemFragmentIfExists();
		popBackStackCompletely();
		attachFragment(new FavoritesFragment(), false, BackStackNames.favourites);
	}

	public void showMyAds() {
		// popTopMenuItemFragmentIfExists();
		popBackStackCompletely();
		attachFragment(new MyAdsFragment(), false, BackStackNames.myAds);
	}

	public void showSavedSearches() {
		// popTopMenuItemFragmentIfExists();
		popBackStackCompletely();
		attachFragment(new SavedSearchesFragment(), false, BackStackNames.savedSearches);
	}

	public void showFavoriteDetails(int bike_id) {
		attachFragment(new FavoritesDetailsFragment(bike_id));
	}

	private LocationManager locationManager;
	private LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			AppSession.currentUserLocation = location;
			searchParamsDAO.lat = location.getLatitude();
			searchParamsDAO.lon = location.getLongitude();
			/*
			 * change in requirement - ignore below
			 */
			//			String reverseGeoCoderUrl = ServiceURLBuilder.getReverseGeoCoderUrl(searchParamsDAO.lat, searchParamsDAO.lon);
			//
			//			Log.d(TAG, "received location " + location.getLatitude());
			//			// if (geocoder == null)
			//			// geocoder = new Geocoder(SearchActivity.this);
			//
			//			// List<Address> list =
			//			// geocoder.getFromLocation(location.getLatitude(),
			//			// location.getLongitude(), 1);
			//			// String postCode = list.get(0).getPostalCode();
			//
			//			new NominatimParser(SearchActivity.this).parseXmlFromUrl(reverseGeoCoderUrl, new ParseListener() {
			//
			//				@Override
			//				public void onParseComplete(Context context, Object object) {
			//					if (object != null)
			//						searchFormFragment.setPostCode(object.toString());
			//
			//				}
			//
			//				@Override
			//				public void onError(Context context, Bundle bundle) {
			//				}
			//			});
			//
			//			// if (count == 3 && postCode == null)
			//			// Toast.makeText(SearchActivity.this, "failed to get post code",
			//			// 0).show();

			locationManager.removeUpdates(this);
		}
	};

	public void onLocationButtonClick(View v) {
		ToggleButton locationButton = (ToggleButton) v;
		if (locationButton.isChecked()) {
			searchFormFragment.setPostCodeText("Using current location", false);
			listenForLocation();
		} else {
			searchParamsDAO.lat = -1;
			searchParamsDAO.lon = -1;
			searchFormFragment.setPostCodeText(null, true);
		}

	}

	void listenForLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(crta, true);
		Location location = null;

		if (provider != null)
			location = locationManager.getLastKnownLocation(provider);

		if (provider == null || !locationManager.isProviderEnabled(provider)) {
			Toast.makeText(this, "Failed to get your location", 0).show();
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null)
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		} else {
			location = locationManager.getLastKnownLocation(provider);
		}

		if (AppSession.currentUserLocation == null && location != null)
			AppSession.currentUserLocation = location;

		if (AppSession.currentUserLocation != null) {
			searchParamsDAO.lat = AppSession.currentUserLocation.getLatitude();
			searchParamsDAO.lon = AppSession.currentUserLocation.getLongitude();
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

}
