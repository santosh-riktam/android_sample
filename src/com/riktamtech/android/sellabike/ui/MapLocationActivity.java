package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.data.AppSession;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.GeoCodeParser;
import com.riktamtech.android.sellabike.widget.BranchIO;
import com.riktamtech.android.sellabike.widget.SitesOverlay;

public class MapLocationActivity extends MapActivity {

	protected static final String TAG = "maps";
	private MapView mapView;
	private MapController mapController;
	private ImageView imageView;
	private EditText postCodeEditText;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.fragment_map_layout);
		mapView = (MapView) findViewById(R.id.map);
		mapController = mapView.getController();
		mapController.setZoom(15);
		mapView.setBuiltInZoomControls(true);
		imageView = (ImageView) findViewById(R.id.drag);

		if (getIntent().getDoubleExtra("lat", -1) != -1) {
			findViewById(R.id.postcodeEntryLayout).setVisibility(View.GONE);
			findViewById(R.id.navBarRightButton).setVisibility(View.GONE);
			int lat = (int) (getIntent().getDoubleExtra("lat", -1) * 1e6);
			int lon = (int) (getIntent().getDoubleExtra("lon", -1) * 1e6);
			GeoPoint geoPoint = new GeoPoint(lat, lon);
			OverlayItem item = new OverlayItem(geoPoint, null, null);
			BranchIO branchIO = new BranchIO(getResources().getDrawable(
					R.drawable.map_pin));
			branchIO.addOverlay(item);
			mapView.getOverlays().clear();
			mapView.getOverlays().add(branchIO);
			mapView.invalidate();
			mapController.animateTo(geoPoint);

		} else {
			locationListener = new LocationListener() {
				public void onLocationChanged(Location location) {
					Log.d(TAG, "location updated");
					// Toast.makeText(MapLocationActivity.this, "lat " +
					// location.getLatitude(), 0).show();
					AppSession.currentUserLocation = location;
					redrawPinAt(location.getLatitude(), location.getLongitude());
				}

				public void onStatusChanged(String provider, int status,
						Bundle extras) {
					// Toast.makeText(MapLocationActivity.this,
					// "onStatusChanged", 0).show();
				}

				public void onProviderEnabled(String provider) {
					// Toast.makeText(MapLocationActivity.this,
					// "onProviderEnabled", 0).show();
				}

				public void onProviderDisabled(String provider) {
					// Toast.makeText(MapLocationActivity.this,
					// "onProviderDisabled", 0).show();
				}
			};

			handleLocation();
			postCodeEditText = (EditText) findViewById(R.id.editText1);
			postCodeEditText
					.setOnEditorActionListener(onPostcodeEnteredListener);
			Toast.makeText(this, R.string.drag_help, 0).show();
		}
	}

	public void myLoc(View v) {
		handleLocation();
	}

	public void onBackClk(View v) {
		setResult(RESULT_CANCELED);
		finish();
	}

	public void onDoneClk(View v) {
		try {
			Intent intent = new Intent();
			SitesOverlay sitesOverlay = (SitesOverlay) mapView.getOverlays()
					.get(0);
			intent.putExtra("lat", sitesOverlay.getLocationofTheOverlay()
					.getLatitudeE6());
			intent.putExtra("lon", sitesOverlay.getLocationofTheOverlay()
					.getLongitudeE6());
			setResult(RESULT_OK, intent);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnEditorActionListener onPostcodeEnteredListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

			String postcode = v.getText().toString();
			postcode = postcode.replace(" ", "");
			// Geocoder geocoder = new Geocoder(MapLocationActivity.this);
			// try {
			// List<Address> addresses = geocoder.getFromLocationName(postcode,
			// 1);
			// if (addresses.size() > 0)
			// redrawPinAt(addresses.get(0).getLatitude(),
			// addresses.get(0).getLatitude());
			// } catch (IOException e) {
			// Toast.makeText(MapLocationActivity.this, "Service not available",
			// 0).show();
			// e.printStackTrace();
			// }
			new GeoCodeParser(MapLocationActivity.this).parseXmlFromUrl(
					ServiceURLBuilder.getGoecoderUrl(postcode),
					new ParseListener() {

						@Override
						public void onParseComplete(Context context,
								Object object) {
							ArrayList<GeoPoint> addresses = (ArrayList<GeoPoint>) object;
							if (addresses.size() > 0)
								redrawPinAt(
										addresses.get(0).getLatitudeE6() / 1.0e6,
										addresses.get(0).getLongitudeE6() / 1.0e6);
						}

						@Override
						public void onError(Context context, Bundle bundle) {
							Toast.makeText(context, "Service not available", 0)
									.show();
						}
					});
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(postCodeEditText.getWindowToken(), 0);
			return true;
		}
	};

	protected void redrawPinAt(double latitude, double longitude) {
		GeoPoint geoPoint = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1e6));
		mapController.animateTo(geoPoint);
		OverlayItem overlayItem = new OverlayItem(geoPoint, "location",
				"location");
		SitesOverlay overlay = new SitesOverlay(getResources().getDrawable(
				R.drawable.map_pin), imageView, mapView);
		overlay.clearAndAddOverlay(overlayItem);
		List<Overlay> overlays = mapView.getOverlays();
		overlays.clear();
		overlays.add(overlay);
		mapView.invalidate();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (locationManager != null && locationListener != null)
			locationManager.removeUpdates(locationListener);
	}

	LocationManager locationManager;
	LocationListener locationListener;

	void handleLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(crta, true);

		if (provider != null && AppSession.currentUserLocation == null)
			AppSession.currentUserLocation = locationManager
					.getLastKnownLocation(provider);

		if (provider == null || !locationManager.isProviderEnabled(provider)) {
			Toast.makeText(this, "Failed to get your location", 0).show();
			AppSession.currentUserLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (AppSession.currentUserLocation == null)
				AppSession.currentUserLocation = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		} else {
			AppSession.currentUserLocation = locationManager
					.getLastKnownLocation(provider);
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
