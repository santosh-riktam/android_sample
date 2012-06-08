package com.riktamtech.android.sellabike.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtils {

	Context context;
	public static int dispayWidth = -1, displayHeight = -1, density = -1;

	public DisplayUtils(Context context) {
		super();
		this.context = context;
		if (dispayWidth == -1) {
			DisplayMetrics displayMetrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			dispayWidth = displayMetrics.widthPixels;
			displayHeight = displayMetrics.heightPixels;
			density = displayMetrics.densityDpi;
		}
	}

	public static int getDpiSpecificValue(int length) {
		return ((int) (length / 160.0 * density));
	}

}
