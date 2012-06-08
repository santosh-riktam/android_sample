package com.riktamtech.android.sellabike.widget;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.riktamtech.android.sellabike.ui.PickerDialogFragment;
import com.riktamtech.android.sellabike.ui.PickerDialogFragment.OnPickerItemSelectionListener;

public class PickerSpinner extends TextView implements OnPickerItemSelectionListener {
	private static final String TAG = "PickerSpinner";
	private String title, allText;
	private ArrayList<String> items;
	private int currentIndex;
	private boolean showPickerWhenSelected;
	public OnPickerItemSelectionListener onPickerItemSelectionListener;
	public ListAdapter adapter;

	/**
	 * {@inheritDoc}
	 */
	public PickerSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	/**
	 * {@inheritDoc}
	 */
	public PickerSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * {@inheritDoc}
	 */
	public PickerSpinner(Context context) {
		super(context);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;

	}

	/**
	 * 
	 * @param titleResource
	 *            - picker dialog title resource
	 * @param objects
	 *            - objects to be shown in picker
	 * @param index
	 *            - index of current item in the list
	 * @param showPickerWhenSelected
	 *            - if false, user click will not show a picker
	 */
	public void setData(int titleResource, ListAdapter objects, int index, String allText, boolean showPickerWhenSelected) {
		adapter = objects;
		items = new ArrayList<String>();

		for (int i = 0; i < objects.getCount(); i++) {
			items.add(objects.getItem(i).toString());
		}

		this.showPickerWhenSelected = showPickerWhenSelected;
		this.allText = allText;
		if (showPickerWhenSelected) {
			setOnClickListener(onClickListener);
		}
		title = getResources().getString(titleResource);

		setCurrentItem(index);
	}

	public void setAdapter(ListAdapter adapter, int index) {
		items = new ArrayList<String>();
		this.adapter = adapter;
		for (int i = 0; i < adapter.getCount(); i++) {
			items.add(adapter.getItem(i).toString());
		}

		setCurrentItem(index);
	}

	/**
	 * will be invoked when user selects an picker item
	 * 
	 * @param index
	 */
	public void setCurrentItem(int index) {
		Log.d(TAG, "index = " + index + " items " + items);
		if (index < items.size()) {
			currentIndex = index;
			String text = index > -1 ? items.get(currentIndex) : "All";
			setText(text);
			if (onPickerItemSelectionListener != null)
				onPickerItemSelectionListener.onPickerItemSelected(adapter, currentIndex);
		}

	}

	/**
	 * will be invoked with string returned from web service
	 * 
	 * @param s
	 */
	public void setCurrentItem(String s) {
		if (s == null) {
			setCurrentItem(0);
		} else {
			s = s.trim();
			for (int i = 0; i < items.size(); i++) {
				if (s.equalsIgnoreCase(items.get(i))) {
					setCurrentItem(i);
					break;
				}
			}
		}
	}

	@Override
	public void onPickerItemSelected(int index) {
		int correctedIndex = allText == null ? index : index - 1;
		setCurrentItem(correctedIndex);
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (showPickerWhenSelected) {
				PickerDialogFragment pickerDialogFragment = PickerDialogFragment.newInstance(title, items.toArray(new String[items.size()]), allText, PickerSpinner.this);
				pickerDialogFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "sample");
			}
		}
	};

	public interface OnPickerItemSelectionListener {
		public void onPickerItemSelected(ListAdapter adapter, int index);
	}
}
