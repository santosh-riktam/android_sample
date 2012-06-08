package com.riktamtech.android.sellabike.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.Utils;

public class CircleIndicator extends View {

	private int space = 5, count, currentIndex, selectedResource, unselecteedResource;

	public CircleIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CircleIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public CircleIndicator(Context context) {
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
			result = 24;
			// result = 160;
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Resources resources = getContext().getResources();
		BitmapDrawable unselectedBitmapDrawable = (BitmapDrawable) resources.getDrawable(unselecteedResource), selectedBitmapDrawable = (BitmapDrawable) resources
				.getDrawable(selectedResource);
		int bitmapWidth = unselectedBitmapDrawable.getBitmap().getWidth();
		int x = (DisplayUtils.dispayWidth - bitmapWidth * count - space * (count - 1)) / 2;
		Paint paint = new Paint();

		for (int i = 0; i < count; i++) {
			Bitmap bitmap = i == currentIndex ? selectedBitmapDrawable.getBitmap() : unselectedBitmapDrawable.getBitmap();
			canvas.drawBitmap(bitmap, x, 8, paint);
			x = x + bitmapWidth + space;
			//Utils.recycle(bitmap);
		}
	}

	public void setCount(int count) {
		this.count = count;
		invalidate();
	}

	public int getCount() {
		return count;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
		invalidate();
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public void init(int count, int currentIndex, int selectedResource, int unselecteedResource) {
		this.count = count;
		this.currentIndex = currentIndex;
		this.selectedResource = selectedResource;
		this.unselecteedResource = unselecteedResource;
	}

}
