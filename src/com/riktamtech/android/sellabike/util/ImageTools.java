package com.riktamtech.android.sellabike.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;

public class ImageTools {
	/**
	 * Resizes the bitmap to fit in the window of given width and height
	 * 
	 * @param bitmap
	 *            - bitmap to resize
	 * @param width
	 *            - width in pixels
	 * @param height
	 *            - height in pixels
	 * @return - resized bitmap
	 */
	public static Bitmap resizeBitmapToFit(Bitmap bitmap, int width, int height) {
		if (bitmap == null)
			return null;
		int originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();
		float ratioW = width / (float) originalWidth;
		float rationH = height / (float) originalHeight;
		float ratio = Math.min(ratioW, rationH);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (originalWidth * ratio), (int) (originalHeight * ratio), false);
		return scaledBitmap;
	}

	/**
	 * Resizes bitmap to exact size given by width and height. If the bitmap does not fit the given exactly, it creates a transparent bitmap of given size which includes the given
	 * resized bitmap
	 * 
	 * @param bitmap1
	 *            - bitmap to reisze
	 * @param width
	 *            - target width in pixels
	 * @param hieght
	 *            - target height in pixels
	 * @param useTransparentRegion
	 *            - true if
	 * @return resized bitmap
	 */
	public static Bitmap resizeBitmapToSize(Bitmap bitmap, int width, int height) {
		return resizeBitmapToSize(bitmap, width, height, Color.TRANSPARENT);
	}

	public static Bitmap resizeBitmapToSize(Bitmap bitmap, int width, int height, int color) {
		if (bitmap == null)
			return null;
		Bitmap bitmap1 = resizeBitmapToFit(bitmap, width, height);
		Bitmap wideBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(wideBitmap);
		canvas.drawColor(color);
		canvas.drawBitmap(bitmap1, (width - bitmap1.getWidth()) / 2, (height - bitmap1.getHeight()) / 2, null);
		return wideBitmap;
	}

	public static Bitmap resizeBitmapToFitExactlyInLandScape(Bitmap bitmap, int width, int height) {
		if (bitmap == null)
			return null;
		int originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();
		float ratioW = width / (float) originalWidth;
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (originalWidth * ratioW), (int) (originalHeight * ratioW), false);
		int top = 0;
		if (scaledBitmap.getHeight() > height)
			top = (scaledBitmap.getHeight() - height) / 2;
		scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, top, Math.min(scaledBitmap.getWidth(), width), Math.min(scaledBitmap.getHeight(), height));
		return getRoundedCornerBitmap(scaledBitmap);
	}

	public static Bitmap resizeBitmapToFitExactlyInBox(Bitmap bitmap, int width, int height) {
		if (bitmap == null)
			return null;
		int originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();
		float ratioW = width / (float) originalWidth;
		float rationH = height / (float) originalHeight;
		float ratio = Math.max(ratioW, rationH);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (originalWidth * ratio), (int) (originalHeight * ratio), false);
		int top = 0, left = 0;

		if (scaledBitmap.getHeight() > height)
			top = (scaledBitmap.getHeight() - height) / 2;
		if (scaledBitmap.getWidth() > width)
			left = (scaledBitmap.getWidth() - width) / 2;
		scaledBitmap = Bitmap.createBitmap(scaledBitmap, left, top, Math.min(scaledBitmap.getWidth(), width), Math.min(scaledBitmap.getHeight(), height));
		return getRoundedCornerBitmap(scaledBitmap);
	}

	private static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 12;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	private static BitmapFactory.Options options;

	public static BitmapFactory.Options getDefaultBitmapOptions() {
		if (options == null) {
			options = new BitmapFactory.Options();
			options.inPurgeable = true;
		}
		return options;
	}

	public static Bitmap decodeFile(String fileName, int maxSize) {
		try {
			File f = new File(fileName);
			//Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			//Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= maxSize && o.outHeight / scale / 2 >= maxSize)
				scale *= 2;

			//Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			return getBitmapWithCorrectOrientation(fileName, BitmapFactory.decodeStream(new FileInputStream(f), null, o2));
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public static Bitmap getBitmapWithCorrectOrientation(String location) {
		return getBitmapWithCorrectOrientation(location, null);
	}

	public static Bitmap getBitmapWithCorrectOrientation(String location, Bitmap bitmap) {
		if (location != null) {
			if (bitmap == null)
				bitmap = BitmapFactory.decodeFile(location, getDefaultBitmapOptions());
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(new File(location).getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			int rotate = 0;
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate += 90;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate += 90;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate += 90;
			}

			if (rotate != 0) {
				Matrix mtx = new Matrix();
				// Rotating Bitmap
				mtx.postRotate(rotate);
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);
			}
		}
		return bitmap;
	}
}