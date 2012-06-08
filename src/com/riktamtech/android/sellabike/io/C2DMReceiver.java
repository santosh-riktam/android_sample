package com.riktamtech.android.sellabike.io;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.ui.SplashActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Contacts.Intents.UI;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class C2DMReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Context c = context;
		final Intent i = intent;
		if (intent.getAction().equals("com.google.android.c2dm.intent.REGISTRATION")) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					handleRegistration(c, i);
				}
			}).start();

		} else if (intent.getAction().equals("com.google.android.c2dm.intent.RECEIVE")) {
			String jsonString = intent.getExtras().getString("payload");
			String title = "sell a bike - notification", message = "New bikes have been uploaded \n " + jsonString, bikeId = null;
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				title = jsonObject.getString("title");
				message = jsonObject.getString("msg");
				if (jsonObject.has("bikeId"))
					bikeId = jsonObject.getString("bikeId");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(500);
			sendNtfctn(context, title, message, bikeId);

		}
	}

	/**
	 * posts notification on notification bar
	 * 
	 * @param context
	 * @param message
	 * @param bikeId
	 * @param message2
	 */
	private void sendNtfctn(Context context, String title, String message, String bikeId) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notification = new Notification(android.R.drawable.ic_dialog_alert, "Mesaage - Sell a bike", System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.rev_1);
		notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;

		Intent intent2 = new Intent(context, SplashActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("msg", message);
		bundle.putString("title", title);
		bundle.putString("bikeId", bikeId);
		intent2.putExtras(bundle);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, title, message, pendingIntent);

		notificationManager.notify(0, notification);
	}

	private void handleRegistration(Context context, Intent intent) {

		String registration = intent.getStringExtra("registration_id");

		if (intent.getStringExtra("error") != null) {
			// Registration failed, should try again later.
			Log.d("&&&&&&", "Registration failed, should try again later.\n " + intent.getStringExtra("error"));
		} else if (intent.getStringExtra("unregistered") != null) {
			// unregistration done, new messages from the authorized sender
			// will be rejected
		} else if (registration != null) {
			// Send the registration ID to the 3rd party site that is
			// sending the messages.
			// This should be done in a separate thread.
			// When done, remember that all registration is done.
			log(registration);
			new PrefsManager(context).updateRegId(registration);
			postToServer(context, registration);
			//sendNtfctn(context, "registration id:" + registration);
		}
	}

	public void postToServer(Context context, String regId) {
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		HttpClient client = new DefaultHttpClient();
		String reqString = "http://api.sellabike.me/api/registerDevice?deviceid=" + new PrefsManager(context).getDeviceId() + "&regid=" + regId;
		HttpGet request = new HttpGet(reqString);
		log(reqString);
		try {
			HttpResponse response = client.execute(request);
			log(EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void log(String ss) {
		Log.d("c2dm", ss);
	}

}
