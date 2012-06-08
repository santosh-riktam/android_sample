package com.riktamtech.android.sellabike.ui;

import com.riktamtech.android.sellabike.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class SearchLocationFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_search_location,
				container, false);
		Toast.makeText(getActivity().getApplicationContext(),
				"Drag point to adjust location", Toast.LENGTH_SHORT).show();
		return inflate;
	}
}
