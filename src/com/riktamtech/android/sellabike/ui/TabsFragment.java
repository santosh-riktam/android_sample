package com.riktamtech.android.sellabike.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.ui.BaseActivity.BackStackNames;

public class TabsFragment extends Fragment {
	RadioButton selectedRadioButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_tabs, null);
		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.tabs);
		for (int i = 0; i < radioGroup.getChildCount(); i++)
			radioGroup.getChildAt(i).setOnClickListener(tabClickListener);

		Activity activity = getActivity();
		if (activity instanceof SearchActivity) {
			selectedRadioButton = (RadioButton) radioGroup.findViewById(R.id.tab_search);
		} else if (activity instanceof SellActivity) {
			selectedRadioButton = (RadioButton) radioGroup.findViewById(R.id.tab_sell);
		} else if (activity instanceof ArticlesActivity) {
			selectedRadioButton = (RadioButton) radioGroup.findViewById(R.id.tab_articles);
		} else if (activity instanceof SettingsActivity) {
			selectedRadioButton = (RadioButton) radioGroup.findViewById(R.id.tab_settings);
		} else
			;
		selectedRadioButton.setChecked(true);
		selectedRadioButton.setOnClickListener(selectedTabClickListener);
		return view;
	}

	android.view.View.OnClickListener selectedTabClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.tab_search) {
				if (!((SearchActivity) getActivity()).isShowingSearchForm())
					((SearchActivity) getActivity()).showSearchForm(false);
			} else
				((BaseActivity) getActivity()).popStackTillRootFragment();
		}
	};

	OnClickListener tabClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);

			switch (v.getId()) {
			case R.id.tab_search:
				intent.setClass(getActivity(), SearchActivity.class);
				break;
			case R.id.tab_sell:
				intent.setClass(getActivity(), SellActivity.class);
				break;
			case R.id.tab_articles:
				intent.setClass(getActivity(), ArticlesActivity.class);
				break;
			case R.id.tab_settings:
				intent.setClass(getActivity(), SettingsActivity.class);
				break;
			default:
				break;
			}
			selectedRadioButton.setChecked(true);
			getActivity().startActivity(intent);
			getActivity().overridePendingTransition(0, 0);
		}
	};
}
