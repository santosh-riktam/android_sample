package com.riktamtech.android.sellabike.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.util.Utils;
import com.riktamtech.android.sellabike.widget.CircleIndicator;

public class BikePhotoGalleryFragment extends Fragment {
	BikeDAO bikeDAO;
	TextView captionTextView;
	ViewPager pager;
	CircleIndicator circleIndicator;

	public BikePhotoGalleryFragment(BikeDAO bikeDAO) {
		this.bikeDAO = bikeDAO;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_bike_photo_gallery,
				container, false);
		LinearLayout linearLayout = (LinearLayout) inflate
				.findViewById(R.id.linearLayout1);
		captionTextView = (TextView) linearLayout
				.findViewById(R.id.caption_Bike_photos);
		pager = (ViewPager) inflate.findViewById(R.id.pager);
		pager.setAdapter(new MyPagerAdapter());

		circleIndicator = (CircleIndicator) inflate
				.findViewById(R.id.circleIndicator1);
		circleIndicator.init(0, 0, R.drawable.dot_sel_page8,
				R.drawable.dot_unselect_page8);

		if (bikeDAO.photos.size() <= 1)
			circleIndicator.setVisibility(View.GONE);
		if (bikeDAO.photos.size() >= 1) {
			captionTextView.setText(bikeDAO.photos.get(0).caption + "\n"
					+ Utils.formatCurrency(bikeDAO.price));
			circleIndicator.setCount(bikeDAO.photos.size());
		}

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				captionTextView.setText(bikeDAO.photos.get(arg0).caption + "\n"
						+ Utils.formatCurrency(bikeDAO.price));
				circleIndicator.setCurrentIndex(arg0);
				// TODO update radio groups
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		return inflate;
	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewGroup)arg0).removeView(((ImageView)arg2));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return bikeDAO.photos.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			ImageView imageView = new ImageView(getActivity());
			((BaseActivity) getActivity()).setImage(imageView,
					bikeDAO.photos.get(arg1).url);
			((ViewPager) arg0).addView(imageView);
			return imageView;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
	}
}
