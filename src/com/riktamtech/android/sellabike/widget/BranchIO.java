package com.riktamtech.android.sellabike.widget;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class BranchIO extends ItemizedOverlay<OverlayItem> {
	Drawable marker;
	Context mContext;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

	public BranchIO(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		marker = defaultMarker;
	}

	public BranchIO(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		marker = defaultMarker;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();

	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

}
