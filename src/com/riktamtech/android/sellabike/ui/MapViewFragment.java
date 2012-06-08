package com.riktamtech.android.sellabike.ui;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.data.AppSession;

public class MapViewFragment extends Fragment {
	MapView mapView;
	ImageView imageView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View inflate = inflater.inflate(R.layout.fragment_map_layout, container, false);
		mapView = (MapView) inflate.findViewById(R.id.map);
		imageView = (ImageView) inflate.findViewById(R.id.drag);
		return inflate;
	}

	LocationManager locationManager;
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			AppSession.currentUserLocation = location;
			Overlay overlay = mapView.getOverlays().get(0);
			GeoPoint geoPoint = new GeoPoint((int) (AppSession.currentUserLocation.getLatitude() * 1E6), (int) (AppSession.currentUserLocation.getLongitude() * 1e6));
			mapView.getOverlays().clear();
			OverlayItem overlayItem = new OverlayItem(geoPoint, "location", "location");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	void handleLocation() {
		locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		AppSession.currentUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getActivity(), "Please turn on GPS", 0).show();
		} else {

		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 300, locationListener);
	}

}
