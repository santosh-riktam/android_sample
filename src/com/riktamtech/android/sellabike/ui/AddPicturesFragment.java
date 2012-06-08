package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.widget.AddPhotoView;

/**
 * 
 * @author Santosh Kumar D
 * 
 *         For adding pictures for premium bike ads
 * 
 */
public class AddPicturesFragment extends Fragment {
	private static final String TAG = "AddPicturesFragment";
	ArrayList<AddPhotoView> photoViews;
	private OnAddPicturesFragmentEventListener onAddPicturesFragmentEventListener;
	ScrollView inflate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, "oncreateview");
		inflate = (ScrollView) inflater.inflate(R.layout.fragment_add_pictures, container, false);
		ViewTreeObserver viewTreeObserver = inflate.getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);

		LinearLayout layout = (LinearLayout) inflate.findViewById(R.id.addPicturesLayout);
		photoViews = new ArrayList<AddPhotoView>();

		for (int i = 0; i < 9; i++) {
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 16);
			AddPhotoView addPhotoView = new AddPhotoView(getActivity(), i, listener);

			photoViews.add(addPhotoView);
			layout.addView(addPhotoView, layoutParams);
		}
		return inflate;
	}

	OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (onAddPicturesFragmentEventListener != null) {
				AddPhotoView view = ((AddPhotoView) v);
				onAddPicturesFragmentEventListener.onAddPhotoViewClicked(view);
			}
		}
	};

	OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			ArrayList<Photo> bitmaps = onAddPicturesFragmentEventListener != null ? onAddPicturesFragmentEventListener.getSelectedPics() : null;
			for (int i = 0; i < photoViews.size(); i++) {
				if (bitmaps != null)
					photoViews.get(i).setPicData(bitmaps.get(i));
			}
			inflate.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		}
	};

	public void setImage(int index, String bitmapLocation) {
		photoViews.get(index).setImage(bitmapLocation);
	}

	@Override
	public void onStart() {
		super.onStart();
		((BaseActivity) getActivity()).setupNavigationButton(R.string.done, new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveData();
				getActivity().getSupportFragmentManager().popBackStack();
			}
		}, false);
		((BaseActivity) getActivity()).setupNavigationButton(R.string.sell, new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		}, true);

	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(TAG, "stopping");
		((BaseActivity) getActivity()).removeNavigationBarButtons();

	}

	private void saveData() {
		Log.d(TAG, "saving pics");
		if (onAddPicturesFragmentEventListener != null) {
			ArrayList<Photo> bitmaps = new ArrayList<Photo>();
			for (AddPhotoView addPhotoView : photoViews)
				bitmaps.add(addPhotoView.getPicData());
			onAddPicturesFragmentEventListener.onPicturesSelected(bitmaps);
		}
	}

	public interface OnAddPicturesFragmentEventListener {
		void onPicturesSelected(ArrayList<Photo> pictures);

		void onAddPhotoViewClicked(AddPhotoView photoView);

		ArrayList<Photo> getSelectedPics();
	}

	public OnAddPicturesFragmentEventListener getOnAddPicturesListener() {
		return onAddPicturesFragmentEventListener;
	}

	public void setOnAddPicturesListener(OnAddPicturesFragmentEventListener onAddPicturesFragmentEventListener) {
		this.onAddPicturesFragmentEventListener = onAddPicturesFragmentEventListener;
	}
}
