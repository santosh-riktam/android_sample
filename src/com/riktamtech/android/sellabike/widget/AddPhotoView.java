package com.riktamtech.android.sellabike.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.ImageTools;
import com.riktamtech.android.sellabike.util.Utils;

public class AddPhotoView extends RelativeLayout implements OnClickListener {
	private static final String TEXT_PREMIUM_PICTURE = "PREMIUM PICTURE ";
	private RelativeLayout topLayout;
	private Button cameraButton, addPicturesButton;
	private EditText editText;
	private TextView titleTextView, captionTextView;
	private LinearLayout captionLayout;

	public int num;

	public String bitmapLocation;
	private OnClickListener listener;

	private Photo picData;

	public AddPhotoView(Context context, int num, OnClickListener listener) {
		super(context);
		this.num = num;
		this.listener = listener;
		LayoutInflater.from(context).inflate(R.layout.add_photo_view, this, true);
		initVariables();
		registerListeners();
	}

	private void initVariables() {
		topLayout = (RelativeLayout) findViewById(R.id.add_photo_view_layout);
		cameraButton = (Button) findViewById(R.id.cameraButton);
		addPicturesButton = (Button) findViewById(R.id.addPicturesButton);
		editText = (EditText) findViewById(R.id.editText1);
		titleTextView = (TextView) findViewById(R.id.textView1);
		captionTextView = (TextView) findViewById(R.id.textView2);
		captionLayout = (LinearLayout) findViewById(R.id.captionLayout);
		titleTextView.setText(TEXT_PREMIUM_PICTURE + (num + 1));

		showWidgets();

	}

	private void registerListeners() {
		topLayout.setOnClickListener(this);
		cameraButton.setOnClickListener(this);
		addPicturesButton.setOnClickListener(this);
	}

	public void setSelectedImage(Bitmap bitmap) {
		topLayout.setBackgroundDrawable(new BitmapDrawable(bitmap));
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	private void showWidgets() {
		captionLayout.setBackgroundColor(Color.TRANSPARENT);
		titleTextView.setVisibility(View.VISIBLE);
		captionTextView.setVisibility(View.VISIBLE);
		cameraButton.setVisibility(View.VISIBLE);
		addPicturesButton.setVisibility(View.VISIBLE);
	}

	private void hideWidgets() {
		captionLayout.setBackgroundResource(R.color.transparent_black);
		titleTextView.setVisibility(View.INVISIBLE);
		captionTextView.setVisibility(View.GONE);
		cameraButton.setVisibility(View.GONE);
		addPicturesButton.setVisibility(View.GONE);
	}

	public void setImage(String bitmapLocation) {
		this.bitmapLocation = bitmapLocation;
		if (bitmapLocation == null || bitmapLocation.equals(""))
			return;
		Bitmap backgroundBitmap = ImageTools.decodeFile(bitmapLocation, DisplayUtils.dispayWidth);// BitmapFactory.decodeFile(bitmapLocation, ImageTools.getDefaultBitmapOptions());
		BitmapDrawable background = (BitmapDrawable) topLayout.getBackground();
		topLayout.setBackgroundDrawable(new BitmapDrawable(ImageTools.resizeBitmapToFitExactlyInBox(backgroundBitmap,
				DisplayUtils.getDpiSpecificValue(background.getMinimumWidth()), DisplayUtils.getDpiSpecificValue(background.getMinimumHeight()))));
		Utils.recycle(backgroundBitmap);
		hideWidgets();
	}

	public void clearImage() {
		bitmapLocation = null;
		topLayout.setBackgroundResource(R.drawable.premium_picture_bg);
		showWidgets();
	}

	public boolean hasImage() {
		return bitmapLocation != null;
	}

	@Override
	public void onClick(View v) {
		if (listener != null)
			listener.onClick(this);
	}

	public Photo getPicData() {
		picData = new Photo(bitmapLocation, editText.getText().toString());
		return picData;
	}

	public void setPicData(Photo picData) {
		if (picData != null) {
			setImage(picData.url);
			editText.setText(picData.caption);
		}
		this.picData = picData;
	}

}
