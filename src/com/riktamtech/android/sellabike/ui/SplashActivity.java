package com.riktamtech.android.sellabike.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.ColourDAO;
import com.riktamtech.android.sellabike.dao.EngineDAO;
import com.riktamtech.android.sellabike.dao.MakeDAO;
import com.riktamtech.android.sellabike.dao.ModelDAO;
import com.riktamtech.android.sellabike.data.AppSession;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.ColoursParser;
import com.riktamtech.android.sellabike.io.DatabaseVersionParser;
import com.riktamtech.android.sellabike.io.DatabaseVersionParser.DatabaseVersionTags;
import com.riktamtech.android.sellabike.io.EnginesParser;
import com.riktamtech.android.sellabike.io.MakesParser;
import com.riktamtech.android.sellabike.io.ModelsParser;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.ImagesCacheSD;
import com.riktamtech.android.sellabike.util.Utils;

public class SplashActivity extends Activity {
	Handler handler;
	PrefsManager prefsManager;
	DBManager dbManager;
	protected static final String TAG = "SplashActivity";
	TextView textView;
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dbManager = new DBManager(this);
		textView = (TextView) findViewById(R.id.textView1);
		textView.setVisibility(View.GONE);
		textView.setText("loading...");

		prefsManager = new PrefsManager(this);

		if (prefsManager.getRegId() == null) {
			getRegId();
		}

		//installCache();

		ImagesCacheSD.path = Utils.getAppCacheDir(this).getAbsolutePath();
		Log.d(TAG, ImagesCacheSD.path);

		//init display
		DisplayUtils displayUtils = new DisplayUtils(this);

		//startActivity(new Intent(this, SellABikeActivity.class));
		// TODO remove comm.
		new DBSyncTask(this).execute(1);

		listenForLocation();

	}

	private interface Messages {
		String FAILURE = "failure";
		String SUCCESS = "success";
		String DB_VERSION = "database version";
		String MODELS = "models";
		String MAKES = "makes";
		String ENGINES = "engines";
		String COLORS = "colors";
		CharSequence DB_SYNC_FAILED = "Database sync failed";
	}

	static class DBSyncTask extends AsyncTask<Object, Object, Object> {

		WeakReference<SplashActivity> activityReference;

		public DBSyncTask(SplashActivity activity) {
			super();
			this.activityReference = new WeakReference<SplashActivity>(activity);
		}

		@Override
		protected Object doInBackground(Object... params) {
			try {
				publishProgress(Messages.DB_VERSION);
				Object object = new DatabaseVersionParser(activityReference.get()).parseXmlFromUrl(ServiceURLBuilder.getDatabaseVersionsUrl());
				if (object == null) {
					publishProgress(Messages.FAILURE);
					return null;
				}
				publishProgress(Messages.SUCCESS);
				Bundle bundle = (Bundle) object;
				int makesVersion = bundle.getInt(DatabaseVersionTags.MAKEVERSION);
				int modelVersion = bundle.getInt(DatabaseVersionTags.MODELVERSION);
				int enginesVersion = bundle.getInt(DatabaseVersionTags.ENGINEVERSION);
				int colorsVersion = bundle.getInt(DatabaseVersionTags.COLOURVERSION);
				if (activityReference.get().prefsManager.getMakesVersion() < makesVersion) {
					publishProgress(Messages.MAKES);
					ArrayList<MakeDAO> makesObject = (ArrayList<MakeDAO>) new MakesParser(activityReference.get()).parseXmlFromUrl(ServiceURLBuilder.getMakesUrl());
					if (makesObject != null) {
						publishProgress(Messages.SUCCESS);
						activityReference.get().dbManager.upgradeMakeData(makesObject);
						activityReference.get().prefsManager.updateMakeVersion(makesVersion);
					} else {
						publishProgress(Messages.FAILURE);
					}
				}

				if (activityReference.get().prefsManager.getModelsVersion() < modelVersion) {
					publishProgress(Messages.MODELS);
					ArrayList<ModelDAO> modelsArrayList = (ArrayList<ModelDAO>) new ModelsParser(activityReference.get()).parseXmlFromUrl(ServiceURLBuilder.getModelsUrl());
					if (modelsArrayList != null) {
						publishProgress(Messages.SUCCESS);
						activityReference.get().dbManager.upgradeModelData(modelsArrayList);
						activityReference.get().prefsManager.updateModelVersion(modelVersion);
					} else {
						publishProgress(Messages.FAILURE);
					}
				}

				if (activityReference.get().prefsManager.getEnginesVersion() < enginesVersion) {
					publishProgress(Messages.ENGINES);
					ArrayList<EngineDAO> arrayList = (ArrayList<EngineDAO>) new EnginesParser(activityReference.get()).parseXmlFromUrl(ServiceURLBuilder.getEnginesUrl());
					if (arrayList != null) {
						publishProgress(Messages.SUCCESS);
						activityReference.get().dbManager.upgradeEngineData(arrayList);
						activityReference.get().prefsManager.updateEngineVersion(enginesVersion);
					} else {
						publishProgress(Messages.FAILURE);
					}
				}
				if (activityReference.get().prefsManager.getColorVersion() < colorsVersion) {
					publishProgress(Messages.COLORS);
					ArrayList<ColourDAO> items = (ArrayList<ColourDAO>) new ColoursParser(activityReference.get()).parseXmlFromUrl(ServiceURLBuilder.getColorsUrl());
					if (items != null) {
						publishProgress(Messages.SUCCESS);
						activityReference.get().dbManager.upgradeColourData(items);
						activityReference.get().prefsManager.updateColorVersion(colorsVersion);
					} else {
						publishProgress(Messages.FAILURE);
					}
				}

				return 1;
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (result instanceof Integer && (Integer.parseInt(result.toString())) == -1)
				return;
			if (activityReference.get() != null) {
				if (activityReference.get().textView.getText().toString().contains("failure"))
					Toast.makeText(activityReference.get(), Messages.DB_SYNC_FAILED, 0).show();

				activityReference.get().startSearchActivity();
			}
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
			if (activityReference.get() != null) {
				String seperator = "\n";
				if (values[0].equals(Messages.SUCCESS) || values[0].equals(Messages.FAILURE))
					seperator = " - ";
				activityReference.get().textView.setText(activityReference.get().textView.getText().toString() + seperator + values[0]);
			}
		}
	}

	public void startSearchActivity() {
		if (!EXIT) {
			// c2dm handling - just forward extras to search activity
			Bundle bundle = getIntent().getExtras();
			Intent intent = new Intent(SplashActivity.this, SearchActivity.class);
			if (bundle != null && bundle.getString("title") != null)
				intent.putExtras(bundle);

			startActivity(intent);
		}
		finish();
	}

	private boolean EXIT = false;

	@Override
	public void onBackPressed() {
		EXIT = true;
		super.onBackPressed();
	}

	private void listenForLocation() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria crta = new Criteria();
		crta.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locationManager.getBestProvider(crta, true);

		if (provider != null)
			AppSession.currentUserLocation = locationManager.getLastKnownLocation(provider);

		if (provider == null || !locationManager.isProviderEnabled(provider)) {
			Toast.makeText(this, "Failed to get your location", 0).show();
			AppSession.currentUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (AppSession.currentUserLocation == null)
				AppSession.currentUserLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		} else {
			AppSession.currentUserLocation = locationManager.getLastKnownLocation(provider);
		}

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}

	private void getRegId() {
		Log.d(TAG, "trying to register");
		Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0)); // boilerplate
		registrationIntent.putExtra("sender", "sellabikenotification@gmail.com");
		startService(registrationIntent);
	}

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
			locationManager.removeUpdates(this);
		}
	};
}
