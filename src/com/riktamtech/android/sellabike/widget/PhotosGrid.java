package com.riktamtech.android.sellabike.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.ImageTools;

public class PhotosGrid extends View {

	private static final String TAG = "PhotosGrid";
	private List<BitmapWrapper> images;
	private int space = 5;

	public PhotosGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PhotosGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PhotosGrid(Context context) {
		super(context);
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) (DisplayUtils.dispayWidth * 0.75);
			// result = 240;
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (DisplayUtils.dispayWidth * 0.6);
			// result = 160;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(TAG, "onDraw called \t-o:" + images);

		if (images == null || images.size() < 9) {
			Log.d(TAG, "images null ? " + images);
			return;
		}

		int boxWidth = getMeasuredWidth() / 3, boxHeight = getMeasuredHeight() / 3;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		if (getMeasuredWidth() > displayMetrics.widthPixels) {
			double ratio = getMeasuredWidth() / (double) displayMetrics.widthPixels;
			double correctWidth = getMeasuredWidth() * ratio;
			double correctHeight = getMeasuredHeight() * ratio;
			boxWidth = (int) (correctWidth / 3);
			boxHeight = (int) (correctHeight / 3);
		}

		int imageWidth = boxWidth - 2 * space, imageHeight = boxHeight - 2 * space;
		Paint paint = new Paint();
		for (int i = 0; i < images.size(); i++) {
			if (images.get(i) != null) {
				if (!images.get(i).optimizedForPainting) {
					Bitmap bitmap = ImageTools.resizeBitmapToFitExactlyInBox(images.get(i).bitmap, imageWidth, imageHeight);
					images.get(i).bitmap.recycle();
					System.gc();
					images.get(i).bitmap = bitmap;
					images.get(i).optimizedForPainting = true;
				}
				//Log.d(TAG, "darwing at location- " + i);
				int xIndex = i / 3, yIndex = i % 3;
				int x = yIndex * boxWidth + space;
				int y = xIndex * boxHeight + space;
				canvas.drawBitmap(images.get(i).bitmap, x, y, paint);
			} else {
				//Log.d(TAG, "found null bitmap at location " + i);
			}
		}
	}

	public List<BitmapWrapper> getImages() {
		return images;
	}

	public void setImages(List<String> imagesUrls) {
		Log.d(TAG, "set image called - " + imagesUrls);
		int w = (int) (DisplayUtils.dispayWidth * 0.33);
		int h = (int) (DisplayUtils.dispayWidth * 0.6);
		images = new ArrayList<BitmapWrapper>();
		Log.d(TAG, "image changing");
		for (int i = 0; i < 9; i++)
			images.add(null);
		new BitmapProcessingTask(imagesUrls, this, w, h).execute("");

	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	private static class BitmapProcessingTask extends AsyncTask<Object, Object, Object> {

		private List<String> imageLocations;
		private WeakReference<PhotosGrid> reference;
		private int width, height;

		public BitmapProcessingTask(List<String> imageLocations, PhotosGrid photosGrid, int width, int height) {
			super();
			this.imageLocations = imageLocations;
			reference = new WeakReference<PhotosGrid>(photosGrid);
			this.width = width;
			this.height = height;
		}

		@Override
		protected Object doInBackground(Object... params) {
			for (int i = 0; i < imageLocations.size(); i++) {
				if (imageLocations.get(i) != null) {
					Bitmap bmp = null;
					bmp = ImageTools.decodeFile(imageLocations.get(i), width);

					//if (imagesUrls != null && !imagesUrls.equals(""))
					//	bmp = BitmapFactory.decodeFile(imagesUrls.get(i), defaultBitmapOptions);
					if (reference.get() != null) {
						Log.d(TAG, "bg setting bitmap at " + i);
						reference.get().images.set(i, new BitmapWrapper(bmp));
					}
				}
			}
			return true;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (reference.get() != null) {
				Log.d(TAG, "After loading... images - " + reference.get().images);
				reference.get().invalidate();

			}
		}
	}

	public void releaseBitmaps() {
		for (BitmapWrapper wrapper : images)
			if (wrapper!=null && wrapper.bitmap != null)
				wrapper.bitmap.recycle();
		System.gc();

	}

	private static class BitmapWrapper {
		public Bitmap bitmap;
		/* this indicates that the bitmap is optimized in ondraw mathod */
		public boolean optimizedForPainting;

		public BitmapWrapper(Bitmap bmp) {
			this(bmp, false);
		}

		public BitmapWrapper(Bitmap bitmap, boolean optimizedForPainting) {
			super();
			this.bitmap = bitmap;
			this.optimizedForPainting = optimizedForPainting;
		}

		@Override
		public String toString() {
			return  "bitmap(" + optimizedForPainting + ")";
		}
	}

}
