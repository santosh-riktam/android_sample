package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;

import com.riktamtech.android.sellabike.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Contains list dialog. Activity showing this fragment should implement {@link OnListDialogRowClickListener}
 * 
 * @author Santosh Kumar D
 * 
 */
public class ListDialogFragment extends DialogFragment {

	private static final String KEY_LIST = "items";
	private OnListDialogRowClickListener onListDialogRowClickListener;

	public static ListDialogFragment newInstance(ArrayList<String> items) {
		ListDialogFragment listDialogFragment = new ListDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(KEY_LIST, items);
		listDialogFragment.setArguments(bundle);
		return listDialogFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);
		ArrayList<String> itemsArrayList = getArguments().getStringArrayList(KEY_LIST);
		String[] items = itemsArrayList.toArray(new String[itemsArrayList.size()]);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.select_option);
		builder.setItems(items, listener);
		return builder.create();
	}

	interface OnListDialogRowClickListener {
		void onListDialogItemClicked(int index);
	}

	public void setOnListDialogRowClickListener(OnListDialogRowClickListener onListDialogRowClickListener) {
		this.onListDialogRowClickListener = onListDialogRowClickListener;
	}

	public OnListDialogRowClickListener getOnListDialogRowClickListener() {
		return onListDialogRowClickListener;
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			if (onListDialogRowClickListener != null)
				onListDialogRowClickListener.onListDialogItemClicked(which);
		}
	};
}
