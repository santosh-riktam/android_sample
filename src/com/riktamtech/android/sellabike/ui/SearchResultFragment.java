package com.riktamtech.android.sellabike.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.os.Build;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.SearchParamsDAO;
import com.riktamtech.android.sellabike.data.AppSession;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.BikeAdsParser;
import com.riktamtech.android.sellabike.io.BikeAdsParser.BikesResult;
import com.riktamtech.android.sellabike.io.NominatimParser;
import com.riktamtech.android.sellabike.io.SavedSearchParser;
import com.riktamtech.android.sellabike.ui.FeaturesDialogFragment.OnFeaturesSelectionListener;
import com.riktamtech.android.sellabike.util.DisplayUtils;

public class SearchResultFragment extends Fragment {
	private SearchParamsDAO searchDao;
	private ArrayList<BikeDAO> bikes;
	private ListView listView;
	private TextView searchSummaryText;
	private DBManager dbManager;
	boolean isSaved;
	BikesResult result;
	private int resultsTotal = -1;

	public SearchResultFragment(SearchParamsDAO searchDao) {
		super();
		this.searchDao = searchDao;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		bikes = new ArrayList<BikeDAO>();
		dbManager = new DBManager(getActivity());
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_search_result, container, false);
		searchSummaryText = (TextView) layout.findViewById(R.id.search_result_text1);
		searchSummaryText.setText(getSearchSummary());
		isSaved = false;
		layout.findViewById(R.id.refineButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((SearchActivity) getActivity()).showFeaturesDialog(searchDao.getSelectedFeatures(), onFeaturesSelectionListener);
			}
		});
		listView = (ListView) layout.findViewById(R.id.listView1);
		listView.setOnItemClickListener(listItemClickListener);
		listView.setAdapter(new EndlessSearchResultAdapter(getActivity(),this, new SearchResultAdapter(getActivity())));
		// new
		// BikeAdsParser(BikeAdsParser.SEARCH_BIKES).parseXmlFromUrl(ServiceURLBuilder.getSearchUrl(getActivity(),
		// searchDao), parseListener);

		return layout;
	}

	private OnFeaturesSelectionListener onFeaturesSelectionListener = new OnFeaturesSelectionListener() {
		@Override
		public void onFeaturesSelected(ArrayList<Integer> selectedIndices) {
			searchDao.setSelectedFeatures(selectedIndices);
			searchSummaryText.setText(getSearchSummary());
			// new
			// BikeAdsParser(BikeAdsParser.SEARCH_BIKES).parseXmlFromUrl(ServiceURLBuilder.getSearchUrl(getActivity(),
			// searchDao), parseListener);
			bikes = new ArrayList<BikeDAO>();
			listView.setAdapter(new EndlessSearchResultAdapter(getActivity(), SearchResultFragment.this,new SearchResultAdapter(getActivity())));
		}
	};

	private String getSearchSummary() {
		String text = "";
		try {

			if (searchDao.makeId > -1) {
				text += dbManager.getMake(searchDao.makeId).name + " ";
			}
			if (searchDao.modelId > -1)
				text += dbManager.getModel(searchDao.modelId).name + " ";
			if (searchDao.engineSizeId > -1)
				text += dbManager.getEngine(searchDao.engineSizeId).name + " ";
			if (resultsTotal == -1)
				;
			else
				text += " " + resultsTotal + " results found";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}

	OnItemClickListener listItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (bikes != null && bikes.size() > arg2)
				((SearchActivity) getActivity()).showBikeDetails(bikes.get(arg2));
		}
	};

	OnClickListener onSaveClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (CheckSearchParams()) {
				Toast.makeText(SearchResultFragment.this.getActivity(), "Nothing to save", 0).show();
			} else {
				if (isSaved) {
					Toast.makeText(SearchResultFragment.this.getActivity(), "Saved Already", 0).show();
				} else {
					Toast.makeText(getActivity(), "Saving...", 0).show();
					new SavedSearchParser(getActivity()).parseXmlFromUrl(
							ServiceURLBuilder.getSavedSearchUrl(searchDao.makeId, searchDao.modelId, -1 , new PrefsManager(getActivity()).getDeviceId(), Build.MODEL),
							savedSearchParseListener);
					
				}
			}
		}
	};

	private ParseListener savedSearchParseListener = new ParseListener() {

		@Override
		public void onParseComplete(Context context, Object object) {
			searchDao.savedSearchId = Integer.parseInt(object.toString());			
			dbManager.insertOrUpdateSavedSeach(searchDao);
			Toast.makeText(SearchResultFragment.this.getActivity(), "Search Saved", 0).show();
			isSaved = true;
		}

		@Override
		public void onError(Context context, Bundle bundle) {
			Toast.makeText(context, "Error", 0).show();
			//Exception exception=(Exception) bundle.getSerializable(ERROR);
			//Utils.sendEmail(getActivity(), "crash log", Log.getStackTraceString(exception));
		}
	};
	
	private boolean CheckSearchParams() {
		if (searchDao.makeId == -1 && searchDao.modelId == -1 && searchDao.engineSizeId == -1 && searchDao.ageId == 0 && searchDao.color == null
				&& searchDao.getSelectedFeatures().size() == 0 && searchDao.mileageId == 0 && searchDao.priceMin == 1000 && searchDao.priceMax == 17500
				&& searchDao.sellerTypeId == 0)
			return true;
		else
			return false;

	}

	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
	};

	public void onDetach() {
		super.onDetach();

	};

	@Override
	public void onPause() {
		super.onPause();
		isSaved = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		isSaved = false;
	}

	@Override
	public void onStart() {
		super.onStart();
		((BaseActivity) getActivity()).setupNavigationButton(R.string.save, onSaveClickListener, false);

	}

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
	}

	class SearchResultAdapter extends BaseAdapter {
		Context context;
		
		public SearchResultAdapter(Context context) {
			super();
			this.context = context;
		}

		@Override
		public int getCount() {
			return bikes.size();
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
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return bikes.get(position).isPremium ? 1 : 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(getActivity()==null)
				return convertView;
			
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				if (getItemViewType(position) == 0)
					convertView = inflater.inflate(R.layout.list_row_search_result, parent, false);
				else
					convertView = inflater.inflate(R.layout.list_row_premium_ad, parent, false);

				ViewHolder holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			final ViewHolder holder = (ViewHolder) convertView.getTag();
			final BikeDAO bikeDAO = bikes.get(position);
			// TODO span title
			holder.titleTextView.setText(bikeDAO.bikeName + " -\u00A3 " + bikeDAO.price + " ono");
			holder.featuresTextView.setText(bikeDAO.year + ", " + dbManager.getEngine(bikeDAO.engineID) + ", " + bikeDAO.mileage + "miles, " + bikeDAO.features);
			if (bikeDAO.thumbs != null && bikeDAO.thumbs.size() > 0)
				((SearchActivity) getActivity()).setImage(listView, holder.bikeImageView, position, bikeDAO.photos.get(0).url, DisplayUtils.dispayWidth / 4,
						DisplayUtils.dispayWidth / 6);
			holder.distanceTextView.setText("Distance - " + bikeDAO.getDistance() + " miles");
			if (AppSession.currentUserLocation != null) {
				new NominatimParser(getActivity()).parseXmlFromUrl(
						ServiceURLBuilder.getReverseGeoCoderUrl(AppSession.currentUserLocation.getLatitude(), AppSession.currentUserLocation.getLongitude()), new ParseListener() {

							@Override
							public void onParseComplete(Context context, Object object) {
								holder.distanceTextView.setText("Distance - " + bikeDAO.getDistance() + " miles from " + object);
							}

							@Override
							public void onError(Context context, Bundle bundle) {
							}
						});
			} else {
				holder.distanceTextView.setText("");
			}
			return convertView;
		}
	}

	class ViewHolder {
		TextView titleTextView, featuresTextView, distanceTextView;
		ImageView bikeImageView;
		View rootView;

		public ViewHolder(View v) {
			rootView = v;
			titleTextView = (TextView) v.findViewById(R.id.textView1);
			featuresTextView = (TextView) v.findViewById(R.id.textView2);
			distanceTextView = (TextView) v.findViewById(R.id.textView3);
			bikeImageView = (ImageView) v.findViewById(R.id.imageView1);
		}
	}

	static class EndlessSearchResultAdapter extends EndlessAdapter {
		private int currentPage = 1;
		private WeakReference<SearchResultFragment> reference;
		private ArrayList<BikeDAO> newBikes;
		private Context context;

		public EndlessSearchResultAdapter(Context context, SearchResultFragment searchResultFragment, ListAdapter wrapped) {
			super(context, wrapped);
			this.context=context;
			reference=new WeakReference<SearchResultFragment>(searchResultFragment);
						
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if (reference != null && reference.get() != null) {
				BikesResult bikesResult = (BikesResult) new BikeAdsParser(getContext(), BikeAdsParser.SEARCH_BIKES).parseXmlFromUrl(ServiceURLBuilder.getSearchUrl(context,
						reference.get().searchDao, currentPage, currentPage + 24));

				newBikes = bikesResult.bikes;
				reference.get().resultsTotal = bikesResult.results;

				if (bikesResult != null) {
					currentPage += 25;
					return newBikes.size() == 25;
				}
			}
			return false;
		}

		@Override
		protected void appendCachedData() {
			if (reference != null && reference.get() != null) {
				if (newBikes != null && newBikes.size() > 0) {
					reference.get().bikes.addAll(newBikes);
					((BaseAdapter) getWrappedAdapter()).notifyDataSetChanged();
					reference.get().searchSummaryText.setText(reference.get().getSearchSummary());
				}
			}
		}

	}
}
