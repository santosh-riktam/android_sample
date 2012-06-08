package com.riktamtech.android.sellabike.util;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.Location;
import android.media.ExifInterface;
import android.widget.AbsListView.RecyclerListener;

import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.data.AppSession;

public class Utils {

	public static String UPLOAD_FINISHED_INTENT = "upload finish";
	public static String UPLOAD_FAILED_INTENT = "upload failed";

	public static int distanceTo(double lat, double lon) {
		float[] results = new float[5];
		if (AppSession.currentUserLocation == null)
			return -1;
		Location.distanceBetween(AppSession.currentUserLocation.getLatitude(),
				AppSession.currentUserLocation.getLongitude(), lat, lon,
				results);
		int miles = (int) (results[0] * 0.000621371192);
		return miles;
	}

	public static String formatCurrency(int price) {
		NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.UK);
		numberFormat.setMaximumFractionDigits(0);
		return numberFormat.format(price);
	}

	public static String formatCurrency(double price) {
		NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.UK);
		numberFormat.setMaximumFractionDigits(0);
		return numberFormat.format(price);
	}

	public static void recycle(Bitmap bmp) {
		if (bmp != null) {
			bmp.recycle();
			bmp = null;
			System.gc();
		}
	}

	/**
	 * returns external cache if avaialble else return app cache
	 * 
	 * @param ctx
	 * @return
	 */
	public static File getAppCacheDir(Context ctx) {
		File cacheDir = ctx.getExternalCacheDir();
		if (cacheDir == null || !cacheDir.exists())
			cacheDir = ctx.getCacheDir();
		return cacheDir;
	}

	
	public static void sendEmail(Context context,String sub,String body) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		// emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
		// new String[] { "feedback@sellabike.me" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sub);

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + body);
		
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String [] {"santosh@riktamtech.com"});

		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

	}
	
	public static boolean containsNonNullItem(ArrayList<Photo> list) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).url != null)
				return true;
		return false;
	}
}
