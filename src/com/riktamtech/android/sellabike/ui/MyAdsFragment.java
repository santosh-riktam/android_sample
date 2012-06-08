package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.InfoParser;
import com.riktamtech.android.sellabike.io.MyAdsParser;
import com.riktamtech.android.sellabike.util.DisplayUtils;

public class MyAdsFragment extends Fragment {
	private ArrayList<BikeDAO> myAdsArrayList;
	private MyAdsParser myAdsParser;
	private ListView listView;
	private DBManager dbManager;
	private TextView itemsCountTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myAdsParser = new MyAdsParser(getActivity());
		dbManager = new DBManager(getActivity());
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		PrefsManager prefsManager = new PrefsManager(getActivity());
		if (myAdsArrayList == null || myAdsArrayList.size() == 0) {
			myAdsArrayList = new ArrayList<BikeDAO>();
			myAdsParser.parseXmlFromUrl(ServiceURLBuilder.getMyAdsViewUrl(prefsManager.getDeviceId()), myAdsParseListener);
		}
		View inflate = inflater.inflate(R.layout.fragment_myads, container, false);
		itemsCountTextView = (TextView) inflate.findViewById(R.id.itemsCountTextView);
		listView = (ListView) inflate.findViewById(R.id.myads_listView);
		listView.setAdapter(new MyAdsAdapter());
		itemsCountTextView.setText(R.string.fetching_bikes);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				((BaseActivity) getActivity()).attachFragment(new BikeDetailsFragment(myAdsArrayList.get(arg2)));
			}
		});
		return inflate;
	}

	private void setItemsCountText() {
		int count = myAdsArrayList.size();
		String countString = "";
		if (count <= 0)
			countString = getResources().getString(R.string.no_my_ads);
		if (count > 0)
			countString = count + " item";
		if (count > 1)
			countString += "s";
		itemsCountTextView.setText(countString);
	}

	private ParseListener myAdsParseListener = new ParseListener() {
		@Override
		public void onParseComplete(Context context, Object object) {
			myAdsArrayList = (ArrayList<BikeDAO>) object;
			listView.setAdapter(new MyAdsAdapter());
			setItemsCountText();
		}

		@Override
		public void onError(Context context, Bundle bundle) {

		}
	};

	View.OnClickListener onRemoveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final int position = (Integer) v.getTag();
			int bikeId = myAdsArrayList.get(position).bikeId;
			new InfoParser(getActivity()).parseXmlFromUrl(ServiceURLBuilder.getMyadsDeleteUrl(bikeId), new ParseListener() {

				@Override
				public void onParseComplete(Context context, Object object) {
					myAdsArrayList.remove(position);
					((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
					setItemsCountText();
				}

				@Override
				public void onError(Context context, Bundle bundle) {
					Toast.makeText(context, "Error", 0).show();
				}
			});

		}
	};

	class MyAdsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return myAdsArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return super.getItemViewType(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_row_myads, parent, false);
				MyAdsHolder holder = new MyAdsHolder(convertView);
				convertView.setTag(holder);
			}

			BikeDAO myAdsItem = myAdsArrayList.get(position);
			MyAdsHolder holder = (MyAdsHolder) convertView.getTag();
			holder.titleTextView.setText(myAdsItem.bikeName);
			holder.discTextView.setText(myAdsItem.year + ", " + dbManager.getEngine(myAdsItem.engineID) + ", " + myAdsItem.getDistance() + "miles, " + myAdsItem.features);
			if (myAdsItem.thumbs != null && myAdsItem.thumbs.size() > 0)
				((BaseActivity) getActivity()).setImage(listView, holder.imageView, position, myAdsItem.photos.get(0).url, DisplayUtils.dispayWidth / 4,
						DisplayUtils.dispayWidth / 6);
			holder.deleteImageView.setTag(position);
			holder.distanceTextView.setText(myAdsItem.getDistance() + " miles");
			return convertView;
		}

	}

	class MyAdsHolder {
		TextView titleTextView, discTextView, distanceTextView;
		ImageView imageView, deleteImageView;

		public MyAdsHolder(View v) {
			titleTextView = (TextView) v.findViewById(R.id.pricevalue_favorites_textview);
			discTextView = (TextView) v.findViewById(R.id.TextView01);
			imageView = (ImageView) v.findViewById(R.id.ImageView01);
			deleteImageView = (ImageView) v.findViewById(R.id.deleteImageView);
			deleteImageView.setOnClickListener(onRemoveClickListener);
			distanceTextView = (TextView) v.findViewById(R.id.distanceTextView);
		}
	}
}