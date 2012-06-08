package com.riktamtech.android.sellabike.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.ui.BaseActivity;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.Utils;

public class PremiumAdsView extends LinearLayout {
	public BikeDAO bike;

	public PremiumAdsView(Context context, BikeDAO bike) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View inflate = inflater.inflate(R.layout.fragment_premium_ads, this);
		inflate.findViewById(R.id.premiumBikeLayout).setTag(bike);

		TextView bikeNameTextView = (TextView) inflate
				.findViewById(R.id.textView1_premiumads);
		bikeNameTextView.setText(bike.bikeName);

		TextView yearDistanceTextView = (TextView) inflate
				.findViewById(R.id.textView2_premiumads);
		// TODO what about the distance in miles
		yearDistanceTextView
				.setText(bike.year + ", " + bike.mileage + " miles");

		
		TextView priceTextView = (TextView) inflate
				.findViewById(R.id.textView3_premiumads);
		priceTextView.setText(Utils.formatCurrency(bike.price));

		// TODO should set image too
		ImageView imageView = (ImageView) inflate.findViewById(R.id.imageView1);
		DisplayUtils displayUtils = new DisplayUtils(context);
		double width = displayUtils.dispayWidth / 2.5;
		double height = width * 2 / 3.4;
		if (bike.thumbs.size() > 0)
			((BaseActivity) getContext()).setImage(imageView,
					bike.thumbs.get(0), (int) width, (int) height);
	}

}
