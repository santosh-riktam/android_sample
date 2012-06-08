package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.riktamtech.android.sellabike.R;

public class FeaturesDialogFragment extends DialogFragment {

	private static final String KEY_CHECKED_INDICES = "indicesOfFeatures";

	private OnFeaturesSelectionListener onFeaturesSelectionListener;

	public static FeaturesDialogFragment newInstance(
			ArrayList<Integer> selectedIndices,
			OnFeaturesSelectionListener onFeaturesSelectionListener) {

		Bundle bundle = new Bundle();
		bundle.putIntegerArrayList(KEY_CHECKED_INDICES, selectedIndices);
		FeaturesDialogFragment featuresDialogFragment = new FeaturesDialogFragment();
		featuresDialogFragment
				.setOnFeaturesSelectionListener(onFeaturesSelectionListener);
		featuresDialogFragment.setArguments(bundle);
		return featuresDialogFragment;
	}

	public OnFeaturesSelectionListener getOnFeaturesSelectionListener() {
		return onFeaturesSelectionListener;
	}

	public void setOnFeaturesSelectionListener(
			OnFeaturesSelectionListener onFeaturesSelectionListener) {
		this.onFeaturesSelectionListener = onFeaturesSelectionListener;
	}

	ListView listView;
	Dialog dialog;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		dialog = new Dialog(getActivity(), R.style.cust_dialog);
		dialog.setTitle(R.string.features);
		dialog.setContentView(R.layout.fragment_features_dialog);
		listView = (ListView) dialog.findViewById(R.id.listView1);
		String items[] = getResources().getStringArray(
				R.array.static_features_array);
		listView.setAdapter(new ArrayAdapter<String>(getActivity(),
				R.layout.list_row_features, items));
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		ArrayList<Integer> checkedIndices = getArguments().getIntegerArrayList(
				KEY_CHECKED_INDICES);
		for (int i = 0; i < items.length; i++) {
			if (checkedIndices.contains(i))
				listView.setItemChecked(i, true);
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				dialog.findViewById(R.id.ok_button).setEnabled(true);
			}
		});

		dialog.findViewById(R.id.ok_button).setOnClickListener(
				buttOnClickListener);
		dialog.findViewById(R.id.cancel_button).setOnClickListener(
				buttOnClickListener);
		return dialog;
	}

	OnClickListener buttOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.ok_button) {
				if (onFeaturesSelectionListener != null) {
					ArrayList<Integer> selectedIndices = new ArrayList<Integer>();
					int len = listView.getCount();
					SparseBooleanArray checked = listView
							.getCheckedItemPositions();
					for (int i = 0; i < len; i++)
						if (checked.get(i))
							selectedIndices.add(i);
					onFeaturesSelectionListener
							.onFeaturesSelected(selectedIndices);

				}
			}
			getDialog().dismiss();
		}
	};

	interface OnFeaturesSelectionListener {
		public void onFeaturesSelected(ArrayList<Integer> selectedIndices);
	}
}
