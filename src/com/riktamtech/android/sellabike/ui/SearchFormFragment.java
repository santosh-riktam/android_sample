package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.ColourDAO;
import com.riktamtech.android.sellabike.dao.EngineDAO;
import com.riktamtech.android.sellabike.dao.MakeDAO;
import com.riktamtech.android.sellabike.dao.ModelDAO;
import com.riktamtech.android.sellabike.dao.SearchParamsDAO;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.BikeAdsParser;
import com.riktamtech.android.sellabike.io.BikeAdsParser.BikesResult;
import com.riktamtech.android.sellabike.ui.FeaturesDialogFragment.OnFeaturesSelectionListener;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.Utils;
import com.riktamtech.android.sellabike.widget.PickerSpinner;
import com.riktamtech.android.sellabike.widget.PickerSpinner.OnPickerItemSelectionListener;
import com.riktamtech.android.sellabike.widget.PremiumAdsView;
import com.riktamtech.android.sellabike.widget.RangeSeekBar;
import com.riktamtech.android.sellabike.widget.RangeSeekBar.OnRangeSeekBarChangeListener;

public class SearchFormFragment extends Fragment {
	private static final String TAG = "SearchFormFragment";
	private TextView advancedSectionTextView, priceTextView, bikeFeaturesTextView;
	private PickerSpinner makesPickerSpinner, modelsPickerSpinner, colorPickerSpinner, engineSizePickerSpinner, agePickerSpinner, mileagePickerSpinner, sellerTypePickerSpinner;
	private EditText postCodeEditText, keywordsEditText;
	private RadioGroup distanceGroup;
	private RangeSeekBar<Integer> seekBar;
	private TableLayout advancedSectionLayout;
	private Button advancedSearchButton;
	private LinearLayout bikeAdsLinearLayout;
	private boolean isAdvancedSectionVisible = false;
	private ArrayList<Integer> checkedFeatures;
	private SearchParamsDAO searchParamsDAO;
	private ArrayList<BikeDAO> premiumBikes;
	private DBManager dbManager;
	private List<Integer> radioButtonIds;
	private String allText = "All";
	private boolean isInEditMode;

	public SearchFormFragment() {
	}

	public SearchFormFragment(boolean openInEditMode) {
		isInEditMode = openInEditMode;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_search_form, container, false);
		SearchActivity searchActivity = ((SearchActivity) getActivity());
		searchParamsDAO = isInEditMode ? searchActivity.editSearchDao : searchActivity.searchParamsDAO;
		premiumBikes = searchActivity.premiumBikes;
		dbManager = new DBManager(getActivity().getApplicationContext());

		radioButtonIds = Arrays.asList(R.id.radio0, R.id.radio1, R.id.radio2, R.id.radio3, R.id.radio4, R.id.radio5);

		makesPickerSpinner = (PickerSpinner) layout.findViewById(R.id.makePickerSpinner);
		ArrayList<MakeDAO> makes = dbManager.getMakes();
		ArrayAdapter<MakeDAO> makesAdapter = new ArrayAdapter<MakeDAO>(getActivity(), -1, makes);
		onMakePickerItemSelectionListener = new OnPickerItemSelectionListener() {
			@Override
			public void onPickerItemSelected(ListAdapter adapter, int index) {
				if (index < 0) {
					modelsPickerSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), -1, getDefaultArrayList()), -1);
				} else {
					ArrayAdapter<MakeDAO> makesAdapter = (ArrayAdapter<MakeDAO>) adapter;
					int makeid = makesAdapter.getItem(index).id;
					modelsPickerSpinner.setAdapter(new ArrayAdapter<ModelDAO>(getActivity(), -1, dbManager.getModels(makeid)), -1);
				}
			}
		};
		makesPickerSpinner.setData(R.string.make, makesAdapter, makes.indexOf(new MakeDAO(searchParamsDAO.makeId, "")), allText, true);
		makesPickerSpinner.onPickerItemSelectionListener = onMakePickerItemSelectionListener;

		modelsPickerSpinner = (PickerSpinner) layout.findViewById(R.id.modelPickerSpinner);
		onModelPickerItemSelectionListener = new OnPickerItemSelectionListener() {
			@Override
			public void onPickerItemSelected(ListAdapter adapter, int index) {
				if (index < 0) {
					engineSizePickerSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), -1, getDefaultArrayList()), -1);
				} else {
					ArrayAdapter<ModelDAO> modelsAdapter = (ArrayAdapter<ModelDAO>) adapter;
					String engineidsString = modelsAdapter.getItem(index).engineid;
					engineSizePickerSpinner.setAdapter(new ArrayAdapter<EngineDAO>(getActivity(), -1, dbManager.getEngines(engineidsString)), -1);
				}
			}
		};
		if (searchParamsDAO.makeId < 0)
			modelsPickerSpinner.setData(R.string.model, new ArrayAdapter<String>(getActivity(), -1, getDefaultArrayList()), searchParamsDAO.modelId, allText, true);
		else {
			ArrayList<ModelDAO> models = dbManager.getModels(searchParamsDAO.makeId);
			ListAdapter modelsAdapter = new ArrayAdapter<ModelDAO>(getActivity(), -1, models);
			modelsPickerSpinner.setData(R.string.model, modelsAdapter, models.indexOf(new ModelDAO(searchParamsDAO.modelId, 78, "", "")), allText, true);
		}
		modelsPickerSpinner.onPickerItemSelectionListener = onModelPickerItemSelectionListener;

		// fontize cc
		engineSizePickerSpinner = (PickerSpinner) layout.findViewById(R.id.EngineSizePickerSpinner);
		ArrayList<EngineDAO> engines = dbManager.getEngines();
		if (searchParamsDAO.engineSizeId < 0) {
			engineSizePickerSpinner.setData(R.string.engine_size, new ArrayAdapter<String>(getActivity(), -1, getDefaultArrayList()), searchParamsDAO.engineSizeId, allText, true);
		} else {
			engineSizePickerSpinner.setData(R.string.engine_size, new ArrayAdapter<EngineDAO>(getActivity(), -1, engines),
					engines.indexOf(new EngineDAO(searchParamsDAO.engineSizeId, "")), allText, true);
		}
		engineSizePickerSpinner.onPickerItemSelectionListener = new OnPickerItemSelectionListener() {
			@Override
			public void onPickerItemSelected(ListAdapter adapter, int index) {
				fontizeText(engineSizePickerSpinner);
			}
		};

		ArrayList<ColourDAO> colours = dbManager.getColours();
		colorPickerSpinner = (PickerSpinner) layout.findViewById(R.id.colorPickerSpinner);
		colorPickerSpinner.setData(R.string.colour, new ArrayAdapter<ColourDAO>(getActivity(), -1, colours), colours.indexOf(new ColourDAO(searchParamsDAO.getColorId(), "")),
				allText, true);

		for (PickerSpinner pickerSpinner : new PickerSpinner[] { makesPickerSpinner, modelsPickerSpinner, engineSizePickerSpinner, colorPickerSpinner })
			if (pickerSpinner.getCurrentIndex() == -1)
				pickerSpinner.setText("Any");

		checkedFeatures = searchParamsDAO.getSelectedFeatures();
		if (checkedFeatures == null)
			checkedFeatures = new ArrayList<Integer>();
		bikeFeaturesTextView = (TextView) layout.findViewById(R.id.bikeFeaturesTextView);
		bikeFeaturesTextView.setText("(" + checkedFeatures.size() + ")");
		((View) bikeFeaturesTextView.getParent()).setOnClickListener(bikeFeaturesClickListener);

		agePickerSpinner = (PickerSpinner) layout.findViewById(R.id.agePickerSpinner);
		agePickerSpinner.setData(R.string.age, new ArrayAdapter<String>(getActivity(), -1, getResources().getStringArray(R.array.ages)), searchParamsDAO.ageId, null, true);

		mileagePickerSpinner = (PickerSpinner) layout.findViewById(R.id.mileagePickerSpinner);
		mileagePickerSpinner.setData(R.string.mileage, new ArrayAdapter<String>(getActivity(), -1, getResources().getStringArray(R.array.mileages)), searchParamsDAO.mileageId,
				null, true);

		sellerTypePickerSpinner = (PickerSpinner) layout.findViewById(R.id.sellerTypePickerSpinner);
		sellerTypePickerSpinner.setData(R.string.sellertype, new ArrayAdapter<String>(getActivity(), -1, getResources().getStringArray(R.array.seller_types)),
				searchParamsDAO.sellerTypeId, null, true);

		postCodeEditText = (EditText) layout.findViewById(R.id.postcode_edittext);
		postCodeEditText.setText(searchParamsDAO.postCode);

		distanceGroup = (RadioGroup) layout.findViewById(R.id.radioGroup1);
		distanceGroup.check(radioButtonIds.get(searchParamsDAO.distanceId));

		TextView distanceTextView = (TextView) layout.findViewById(R.id.distance_search_form_textview);
		Spannable spannable = new SpannableString(distanceTextView.getText());
		spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(new ForegroundColorSpan(Color.GRAY), 15, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		distanceTextView.setText(spannable);

		LinearLayout MinLinearLayout = (LinearLayout) layout.findViewById(R.id.MinLinearLayout);
		priceTextView = (TextView) layout.findViewById(R.id.price_textview);
		seekBar = new RangeSeekBar<Integer>(20, 20000, getActivity().getBaseContext());
		seekBar.setNotifyWhileDragging(true);
		seekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
			@Override
			public void rangeSeekBarValuesChanged(Integer minValue, Integer maxValue) {
				// handle changed range values
				String priceString = Utils.formatCurrency(minValue) + " to " + Utils.formatCurrency(maxValue);
				priceTextView.setText(priceString);
			}
		});
		seekBar.setSelectedMinValue(searchParamsDAO.priceMin);
		seekBar.setSelectedMaxValue(searchParamsDAO.priceMax);

		// add RangeSeekBar to pre-defined layout
		seekBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		/*
		 * seekBar.setBackgroundDrawable(getResources().getDrawable( R.drawable.seekbar_bg));
		 */

		TextView valueTV = new TextView(getActivity());
		valueTV.setText("Max");
		valueTV.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		MinLinearLayout.addView(valueTV);
		MinLinearLayout.addView(seekBar, 1, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		keywordsEditText = (EditText) layout.findViewById(R.id.keywors_edittext);
		keywordsEditText.setText(searchParamsDAO.keywords);

		layout.findViewById(R.id.search_button).setOnClickListener(searchClickListener);

		advancedSearchButton = (Button) layout.findViewById(R.id.advanced_button);
		advancedSectionLayout = (TableLayout) layout.findViewById(R.id.advanced_search_tableLayout);
		advancedSectionTextView = (TextView) layout.findViewById(R.id.advanced_search_tv);
		bikeAdsLinearLayout = (LinearLayout) layout.findViewById(R.id.bikeAdsLinearLayout);
		advancedSearchButton.setOnClickListener(advancedClickListener);

		// layout.findViewById(R.id.search_button).setOnClickListener(searchClickListener);
		if (premiumBikes == null || premiumBikes.size() < 1) {
			new BikeAdsParser(getActivity(), BikeAdsParser.FEATURED_BIKES).parseXmlFromUrl(ServiceURLBuilder.getFeaturedBikesUrl(), parseListener);
		} else {
			refreshAdsLayout();
		}
		return layout;

	}

	private ArrayList<String> getDefaultArrayList() {
		ArrayList<String> arrayList = new ArrayList<String>();
		return arrayList;
	}

	OnPickerItemSelectionListener onMakePickerItemSelectionListener, onModelPickerItemSelectionListener;

	private ParseListener parseListener = new ParseListener() {

		@Override
		public void onParseComplete(Context context, Object object) {
			premiumBikes.clear();
			premiumBikes.addAll(((BikesResult) object).bikes);
			refreshAdsLayout();
		}

		@Override
		public void onError(Context context, Bundle bundle) {
			Log.d(TAG, "failed to fetch premium ads");
		}
	};

	private void refreshAdsLayout() {
		bikeAdsLinearLayout.removeAllViews();
		for (BikeDAO bike : premiumBikes) {
			PremiumAdsView premiumAdsView = new PremiumAdsView(SearchFormFragment.this.getActivity(), bike);
			premiumAdsView.setLayoutParams(new LinearLayout.LayoutParams(DisplayUtils.getDpiSpecificValue(180),LayoutParams.WRAP_CONTENT));
			premiumAdsView.setClickable(true);
			bikeAdsLinearLayout.addView(premiumAdsView);
		}
		bikeAdsLinearLayout.invalidate();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isNetworkAvailable()) {
			if (premiumBikes == null || premiumBikes.size() < 1) {
				new BikeAdsParser(getActivity(), BikeAdsParser.FEATURED_BIKES).parseXmlFromUrl(ServiceURLBuilder.getFeaturedBikesUrl(), parseListener);
			} else {
				refreshAdsLayout();
			}
		} else
			Toast.makeText(getActivity(), "No Internet Connectivity", 0).show();

	}

	public void setPostCodeText(String postCode, boolean enabled) {
		if (enabled)
			{
				postCodeEditText.setText(postCode);
				postCodeEditText.setTextColor(Color.BLACK);
			}
		else {
			postCodeEditText.setText(null);
			postCodeEditText.setText("Using current location");
			postCodeEditText.setTextColor(getResources().getColor(R.color.orange));
		}
		postCodeEditText.setEnabled(enabled);
	}	

	private void fontizeText(TextView textView) {
		try {
			String text = textView.getText().toString();
			SpannableString spannableString = new SpannableString(text);
			spannableString.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.gray)), text.indexOf("cc"), text.indexOf("cc") + 2,
					SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
			textView.setText(spannableString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener searchClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (isNetworkAvailable()) {
				searchParamsDAO.makeId = makesPickerSpinner.getCurrentIndex() < 0 ? -1 : ((ArrayAdapter<MakeDAO>) makesPickerSpinner.adapter).getItem(makesPickerSpinner
						.getCurrentIndex()).id;
				searchParamsDAO.modelId = modelsPickerSpinner.getCurrentIndex() < 0 ? -1 : ((ArrayAdapter<ModelDAO>) modelsPickerSpinner.adapter).getItem(modelsPickerSpinner
						.getCurrentIndex()).id;
				searchParamsDAO.engineSizeId = engineSizePickerSpinner.getCurrentIndex() < 0 ? -1 : ((ArrayAdapter<EngineDAO>) engineSizePickerSpinner.adapter)
						.getItem(engineSizePickerSpinner.getCurrentIndex()).id;

				if (colorPickerSpinner.getCurrentIndex() > 0) {
					ColourDAO colourDAO = ((ArrayAdapter<ColourDAO>) colorPickerSpinner.adapter).getItem(colorPickerSpinner.getCurrentIndex());
					searchParamsDAO.setColorId(colourDAO.id, colourDAO.name);
				}
				searchParamsDAO.postCode = postCodeEditText.getText().toString();
				searchParamsDAO.distanceId = radioButtonIds.indexOf(distanceGroup.getCheckedRadioButtonId());
				searchParamsDAO.priceMin = seekBar.getSelectedMinValue();
				searchParamsDAO.priceMax = seekBar.getSelectedMaxValue();
				searchParamsDAO.ageId = agePickerSpinner.getCurrentIndex();
				searchParamsDAO.mileageId = mileagePickerSpinner.getCurrentIndex();
				searchParamsDAO.sellerTypeId = sellerTypePickerSpinner.getCurrentIndex();
				searchParamsDAO.setSelectedFeatures(checkedFeatures);
				searchParamsDAO.keywords = keywordsEditText.getText().toString();
				((SearchActivity) getActivity()).onSearchClk(searchParamsDAO);
			} else {
				Toast.makeText(getActivity(), "No Internet Connectivity", 0).show();
			}
		}
	};

	private OnClickListener advancedClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			isAdvancedSectionVisible = !isAdvancedSectionVisible;

			int visibility = isAdvancedSectionVisible ? View.VISIBLE : View.GONE;
			advancedSectionTextView.setVisibility(visibility);
			advancedSectionLayout.setVisibility(visibility);

			int searchButtonResource = isAdvancedSectionVisible ? R.drawable.btn_basicsearch : R.drawable.btn_advancedsearch;
			advancedSearchButton.setBackgroundResource(searchButtonResource);
		}
	};

	private OnClickListener bikeFeaturesClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((SearchActivity) getActivity()).showFeaturesDialog(checkedFeatures, onFeaturesSelectionListener);
		}
	};

	private OnFeaturesSelectionListener onFeaturesSelectionListener = new OnFeaturesSelectionListener() {
		@Override
		public void onFeaturesSelected(ArrayList<Integer> selectedIndices) {
			checkedFeatures = selectedIndices;
			bikeFeaturesTextView.setText("(" + checkedFeatures.size() + ")");
		}
	};

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}
}
