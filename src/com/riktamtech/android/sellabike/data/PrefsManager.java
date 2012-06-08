package com.riktamtech.android.sellabike.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;

public class PrefsManager {
	SharedPreferences preferences;
	Context ctx;

	interface PrefNames {
		String name = "com.riktamtech.android.sellabiker";
		String makes = "pref_make";
		String models = "pref_models";
		String engines = "pref_engines";
		String colors = "pref_colors";
		String fbAccesToken = "pref_fb_acces_token";
		String fbExpiresIn = "pref_fb_expires_in";
		String twToken = "pref_tw_token";
		String twTokenSecret = "pref_tw_token_secret";
		String deviceId = "deviceId";
		String regId = "regId";
	}

	public PrefsManager(Context prefscontext) {
		super();
		this.ctx = prefscontext;
		preferences = prefscontext.getSharedPreferences(PrefNames.name, Context.MODE_PRIVATE);
	}

	/*
	 * public void updateDatabaseVersionPrefs(int makeId, int modelId, int engineId, int colorId) { SharedPreferences.Editor editor = preferences.edit();
	 * editor.putInt(PrefNames.makes, makeId); editor.putInt(PrefNames.models, modelId); editor.putInt(PrefNames.engnies, engineId); editor.putInt(PrefNames.colors, colorId);
	 * editor.commit(); }
	 */

	public void updateMakeVersion(int makeVersion) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(PrefNames.makes, makeVersion);
		editor.commit();
	}

	public void updateModelVersion(int modelVersion) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(PrefNames.models, modelVersion);
		editor.commit();
	}

	public void updateEngineVersion(int engineVersion) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(PrefNames.engines, engineVersion);
		editor.commit();
	}

	public void updateColorVersion(int colorVersion) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt(PrefNames.colors, colorVersion);
		editor.commit();
	}

	public void updateRegId(String regId) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PrefNames.regId, regId);
		editor.commit();
	}

	public String getRegId() {
		return preferences.getString(PrefNames.regId, null);
	}

	public int getMakesVersion() {
		return preferences.getInt(PrefNames.makes, 0);
	}

	public int getModelsVersion() {
		return preferences.getInt(PrefNames.models, 0);
	}

	public int getEnginesVersion() {
		return preferences.getInt(PrefNames.engines, 0);
	}

	public int getColorVersion() {
		return preferences.getInt(PrefNames.colors, 0);
	}

	public String getFbAccessToken() {
		return preferences.getString(PrefNames.fbAccesToken, null);
	}

	public String getFbExpiresIn() {
		return preferences.getString(PrefNames.fbExpiresIn, null);
	}

	public String getTwToken() {
		return preferences.getString(PrefNames.twToken, null);
	}

	public String getTwTokenSecret() {
		return preferences.getString(PrefNames.twTokenSecret, null);
	}

	public void setTwitterDetails(String token, String tokenSecret) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PrefNames.twToken, token);
		editor.putString(PrefNames.twTokenSecret, tokenSecret);
		editor.commit();
	}

	public void setFbDetails(String authToken, String expiresIn) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PrefNames.fbAccesToken, authToken);
		editor.putString(PrefNames.fbExpiresIn, expiresIn);
		editor.commit();
	}

	public String getDeviceId() {
		String deviceId = preferences.getString(PrefNames.deviceId, null);
		if (deviceId == null) {
			deviceId = generateUniqueId();
			Editor editor = preferences.edit();
			editor.putString(PrefNames.deviceId, deviceId);
			editor.commit();
		}
		return deviceId;
	}

	private String generateUniqueId() {
		String deviceIdSecure = Secure.getString(ctx.getContentResolver(), Secure.ANDROID_ID);
		String deviceId = UUID.randomUUID() + "-" + deviceIdSecure + "-" + System.currentTimeMillis();
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(deviceId.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return deviceId;
	}
}
