package com.riktamtech.android.sellabike.ui;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;

import com.riktamtech.android.sellabike.R;

public class PickerDialogFragment extends DialogFragment {

	public static final String TAG_ITEMS = "items";
	public static final String TAG_TITLE = "title";
	public static final String TAG_ALL_TEXT = "ALL";
	public OnPickerItemSelectionListener onPickerItemSelectionListener;
	private String allText = null;
	private ArrayList<String> items;
	private Hashtable<String, Integer> itemIndicesHashtable;
	private ArrayList<String> itemsIndices;
	private WheelView wheelView1, wheelView2;

	public static PickerDialogFragment newInstance(String title, String items[], String allText, OnPickerItemSelectionListener onPickerItemSelectionListener) {

		PickerDialogFragment pickerDialogFragment = new PickerDialogFragment();
		pickerDialogFragment.onPickerItemSelectionListener = onPickerItemSelectionListener;
		Bundle bundle = new Bundle();
		bundle.putCharSequenceArray(TAG_ITEMS, items);
		bundle.putString(TAG_TITLE, title);
		bundle.putString(TAG_ALL_TEXT, allText);
		pickerDialogFragment.setArguments(bundle);
		return pickerDialogFragment;
	}

	public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		
		int textSize=18;
		
		allText = getArguments().getString(TAG_ALL_TEXT);

		items = new ArrayList<String>();
		items.addAll(Arrays.asList(getArguments().getStringArray("items")));
		buildIndexForItems();

		Dialog dialog = new Dialog(getActivity(), R.style.cust_dialog);
		dialog.setTitle(getArguments().getString(TAG_TITLE));
		dialog.setContentView(R.layout.fragment_picker_dialog);

		wheelView1 = (WheelView) dialog.findViewById(R.id.wheel1);
		if (items.size() > 20) {
			wheelView1.addScrollingListener(scrollListener);
			ArrayWheelAdapter<String> arrayWheelAdapter1 = new ArrayWheelAdapter<String>(getActivity(), itemsIndices.toArray(new String[itemsIndices.size()]));
			arrayWheelAdapter1.setTextSize(textSize);
			wheelView1.setViewAdapter(arrayWheelAdapter1);
			wheelView1.setVisibleItems(7);
		} else {
			wheelView1.setVisibility(View.GONE);
		}

		wheelView2 = (WheelView) dialog.findViewById(R.id.wheel2);
		wheelView2.addScrollingListener(scrollListener);
		ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(getActivity(), items.toArray(new String[items.size()]));
		arrayWheelAdapter.setTextSize(textSize);
		wheelView2.setViewAdapter(arrayWheelAdapter);
		wheelView2.setVisibleItems(7);
		

		dialog.findViewById(R.id.ok_button).setOnClickListener(buttonClickListener);
		dialog.findViewById(R.id.cancel_button).setOnClickListener(buttonClickListener);

		return dialog;
	}

	OnClickListener buttonClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			getDialog().dismiss();
			if (arg0.getId() == R.id.ok_button && onPickerItemSelectionListener != null)
				onPickerItemSelectionListener.onPickerItemSelected(wheelView2.getCurrentItem());
		}
	};

	int tempInt = -1;

	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {

		@Override
		public void onScrollingStarted(WheelView wheel) {
		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			if (wheel == wheelView1)
				reorderSecondWheel();
			else
				reorderFirstWheel();
		}
	};

	private void buildIndexForItems() {

		itemIndicesHashtable = new Hashtable<String, Integer>();
		for (int i = 0; i < items.size(); i++)
			if (!itemIndicesHashtable.containsKey(items.get(i).substring(0, 1)))
				itemIndicesHashtable.put(items.get(i).substring(0, 1), i + 1);

		itemsIndices = new ArrayList<String>(itemIndicesHashtable.keySet());
		Collections.sort(itemsIndices);

		if (allText != null) {
			itemsIndices.add(0, allText);
			items.add(0, allText);
		}

	}

	protected void reorderSecondWheel() {
		if (wheelView1.getCurrentItem() == 0) {
			wheelView2.setCurrentItem(0, true);
			return;
		}
		String indexString = itemsIndices.get(wheelView1.getCurrentItem());
		if (!indexString.equals(items.get(wheelView2.getCurrentItem()).substring(0, 1)))
			wheelView2.setCurrentItem(itemIndicesHashtable.get(indexString), true);
	}

	protected void reorderFirstWheel() {
		if (wheelView2.getCurrentItem() == 0) {
			wheelView1.setCurrentItem(0, true);
			return;
		}
		String str = items.get(wheelView2.getCurrentItem()).substring(0, 1);
		int index = itemsIndices.indexOf(str);
		if (index > -1)
			wheelView1.setCurrentItem(index, true);
	}

	public interface OnPickerItemSelectionListener {
		public void onPickerItemSelected(int index);
	}

}
