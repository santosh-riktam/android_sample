package com.riktamtech.android.sellabike.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

/**
 * light version - doesnot maintain in-memory cache. Doesnot hold references to
 * context
 * 
 * @author Santosh Kumar D
 * 
 */
public class ImagesCacheSD {
	private static final String TAG = "ImagesCacheSD";
	String externalCacheDirectoryPathString;
	public static String path = "";

	public ImagesCacheSD() {
		super();
		this.externalCacheDirectoryPathString = path + "/thumbs/";
		File file = new File(externalCacheDirectoryPathString);
		if (!file.exists())
			file.mkdirs();

	}

	/**
	 * downloads bitmap from str and saves in sd card
	 * 
	 * @param str
	 * @return true if downloaded successfully
	 */
	public boolean downloadBitmap(String str) {
		Bitmap bmp = null;
		String location = getLocalFileLocation(str);
		File file = new File(location);
		if (!file.exists()) {
			try {
				// if (str.contains("ytimg.com")) {
				URL url = new URL(str);
				HttpGet httpRequest = null;
				httpRequest = new HttpGet(url.toURI());
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse response = (HttpResponse) httpclient
						.execute(httpRequest);

				HttpEntity entity = response.getEntity();
				BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(
						entity);
				InputStream input = bufHttpEntity.getContent();
				bmp = BitmapFactory.decodeStream(input);
				// } else {
				// URL url = new URL(str);
				// bmp = BitmapFactory.decodeStream(url.openStream());
				// }

				if (bmp != null) {
					if (!writeFile(location, bmp)) {
						Log.e(">>>>>>>>>><<<<<<<<", "write unsuccessful");
					}
					bmp.recycle();
					bmp = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}

	public boolean containsImage(String imageURL) {
		String fileString = getLocalFileLocation(imageURL);
		File file = new File(fileString);
		return file.exists();
	}

	public Bitmap getBitmapFromCache(String str, int maxSize) {
		Bitmap bmp = null;
		String file = getLocalFileLocation(str);
		bmp = readFile(file, maxSize);
		return bmp;
	}

	public Bitmap getBitmapFromCache(String str) {
		return getBitmap(str, 0);
	}

	public Bitmap getBitmap(String str, int maxSize) {
		if (!containsImage(str))
			if (!downloadBitmap(str))
				return null;
		return getBitmapFromCache(str, maxSize);
	}

	/**
	 * Returns the local file location corresponding to url. Downloads the file
	 * if it does not exist
	 * 
	 * @param url
	 *            location of file on the web
	 * @return url of the local file
	 */
	public String getLocalFileLocation(String urlString) {
		try {
			Log.d(TAG, "urlString : \"" + urlString + "\"");
			URL url = new URL(urlString);
			String file = externalCacheDirectoryPathString
					+ url.getFile().replace('/', '_');
			return file;
		} catch (MalformedURLException e) {
			Log.d(TAG, e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}

	public boolean writeFile(String str, Bitmap bmp) {
		// File file = new File(str);
		try {
			return bmp.compress(CompressFormat.PNG, 100, new FileOutputStream(
					str));
		} catch (Exception e) {
			// e.printStackTrace();
			Log.d(TAG, "error writing bitmap to " + str + " " + e.getMessage());
			return false;
		}
	}

	private Bitmap readFile(String str, int maxSize) {
		// Log.d("IIIIIIIII", "reading "+str);
		Options defaultBitmapOptions = ImageTools.getDefaultBitmapOptions();
		File file = new File(str);
		if (maxSize > 0)
			return ImageTools.decodeFile(str, maxSize);
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}
}
