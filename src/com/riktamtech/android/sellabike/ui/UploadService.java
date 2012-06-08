package com.riktamtech.android.sellabike.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.util.Utils;

public class UploadService extends android.app.IntentService {

	public static String TAG = "UploadService";

	public UploadService(String name) {
		super(name);
	}

	public UploadService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "service started");
		BikeDAO bike = (BikeDAO) intent.getSerializableExtra("dao");
		String submitBikeUrl = ServiceURLBuilder.getSubmitBikeUrl();
		Log.d(TAG, "post url:" + submitBikeUrl);
		try {
			HttpPost post = new HttpPost(submitBikeUrl);
			//post.setEntity(new UrlEncodedFormEntity(getParams(bike)));
			post.setEntity(getTestEntity(bike));
			HttpClient httpClient = new DefaultHttpClient();
			Log.d(TAG, "executing");
			HttpResponse response = httpClient.execute(post);
			String responseString = EntityUtils.toString(response.getEntity());
			Log.d(TAG, "response:\n" + responseString);
			Intent finishIntent = new Intent(Utils.UPLOAD_FINISHED_INTENT);
			finishIntent.putExtra("data", responseString);
			finishIntent.putExtra("bikeId", bike.bikeId);
			LocalBroadcastManager.getInstance(this).sendBroadcast(finishIntent);
		} catch (Exception e) {
			e.printStackTrace();
			sendBroadcast(new Intent(Utils.UPLOAD_FAILED_INTENT));
		}
	}

	
	private List<? extends NameValuePair> getParams(BikeDAO bike) {

		String appVersion = "no_info";
		try {
			PackageInfo packageInfo;
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			appVersion = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("name", bike.bikeName));
		params.add(new BasicNameValuePair("make", "" + bike.makeID));
		params.add(new BasicNameValuePair("model", "" + bike.modelID));
		params.add(new BasicNameValuePair("engine", "" + bike.engineID));
		params.add(new BasicNameValuePair("color", "" + bike.colorId));
		params.add(new BasicNameValuePair("seller", "" + bike.sellerId));
		params.add(new BasicNameValuePair("year", "" + bike.year));
		params.add(new BasicNameValuePair("mileage", "" + bike.mileage));
		
		bike.regNo=bike.regNo.replace("\n", "");
		bike.regNo=bike.regNo.replace(" ", "");
		
		params.add(new BasicNameValuePair("regno", bike.regNo));
		params.add(new BasicNameValuePair("price", "" + bike.price));
		params.add(new BasicNameValuePair("features", bike.features));
		params.add(new BasicNameValuePair("description", bike.description));
		params.add(new BasicNameValuePair("lat", "" + bike.lat));
		params.add(new BasicNameValuePair("lon", "" + bike.lon));
		params.add(new BasicNameValuePair("firstname", bike.firstName));
		params.add(new BasicNameValuePair("lastname", bike.lastName));
		params.add(new BasicNameValuePair("email", bike.email));
		params.add(new BasicNameValuePair("tel", bike.telephone));
		for (int i = 0; i < bike.photos.size(); i++) {
			String location = bike.photos.get(i).url;
			if (location != null && !TextUtils.isEmpty(location)) {
				params.add(new BasicNameValuePair("image" + (i + 1), bike.getBase64EncodedImage(i)));
				params.add(new BasicNameValuePair("caption" + (i + 1), bike.photos.get(i).caption));
			}
		}
		params.add(new BasicNameValuePair("PremiumFlag", (bike.photos.size() > 1 ? "Yes" : "No")));
		params.add(new BasicNameValuePair("deviceid", new PrefsManager(this).getDeviceId()));
		params.add(new BasicNameValuePair("devicetype", Build.MODEL));
		params.add(new BasicNameValuePair("appversion", appVersion));

		return params;
	}

	// TODO for testing
	private HttpEntity getTestEntity(BikeDAO bike) {
		try {
			String appVersion = "no_info";
			try {
				PackageInfo packageInfo;
				packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				appVersion = packageInfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			MultipartEntity multipartEntity = new MultipartEntity();

			multipartEntity.addPart("name", new StringBody(bike.bikeName));
			multipartEntity.addPart("make", new StringBody("" + bike.makeID));
			multipartEntity.	addPart("model", new StringBody("" + bike.modelID));
			multipartEntity.addPart("engine", new StringBody("" + bike.engineID));
			multipartEntity.addPart("color", new StringBody("" + bike.colorId));
			multipartEntity.addPart("seller", new StringBody("" + bike.sellerId));
			multipartEntity.addPart("year", new StringBody("" + bike.year));
			multipartEntity.addPart("mileage", new StringBody("" + bike.mileage));
			multipartEntity.addPart("regno", new StringBody(bike.regNo));
			multipartEntity.addPart("price", new StringBody("" + bike.price));
			multipartEntity.addPart("features", new StringBody(bike.features + ""));
			multipartEntity.addPart("description", new StringBody(bike.description));
			multipartEntity.addPart("lat", new StringBody("" + bike.lat));
			multipartEntity.addPart("lon", new StringBody("" + bike.lon));
			multipartEntity.addPart("firstname", new StringBody(bike.firstName));
			multipartEntity.addPart("lastname", new StringBody(bike.lastName));
			multipartEntity.addPart("email", new StringBody(bike.email));
			multipartEntity.addPart("tel", new StringBody(bike.telephone));

			for (int i = 0; i < bike.photos.size(); i++) {
				String location = bike.photos.get(i).url;
				if (location != null && !TextUtils.isEmpty(location)) {
					multipartEntity.addPart("image" + (i + 1), new FileBody(new File(bike.photos.get(i).url)));
					multipartEntity.addPart("caption" + (i + 1), new StringBody(bike.photos.get(i).caption));
				}
			}
			
			multipartEntity.addPart("PremiumFlag", new StringBody((bike.photos.size() > 1 ? "Yes" : "No")));
			multipartEntity.addPart("deviceid", new StringBody(new PrefsManager(this).getDeviceId()));
			multipartEntity.addPart("devicetype", new StringBody(Build.MODEL));
			multipartEntity.addPart("appversion", new StringBody(appVersion));

			return multipartEntity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
