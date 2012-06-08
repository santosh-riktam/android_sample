package com.riktamtech.android.sellabike.ui;

import com.riktamtech.android.sellabike.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ConfirmDialogFragment extends DialogFragment {

	private OnDialogButtonClickListener onDialogButtonClickListener;

	public static ConfirmDialogFragment newInstance(int title, int message,
			int ok, int cancel) {
		ConfirmDialogFragment fragment = new ConfirmDialogFragment();
		Bundle args = new Bundle();
		args.putInt(Args.title, title);
		args.putInt(Args.message, message);
		args.putInt(Args.ok, ok);
		args.putInt(Args.cancel, cancel);
		fragment.setArguments(args);
		return fragment;
	}

	public static ConfirmDialogFragment newInstance(int title, int message) {
		return newInstance(title, message, R.string.ok, R.string.cancel);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(getArguments().getInt(Args.title))
				.setMessage(getArguments().getInt(Args.message))
				.setPositiveButton(getArguments().getInt(Args.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (onDialogButtonClickListener != null)
									onDialogButtonClickListener
											.doPositiveClick();
							}
						})
				.setNegativeButton(getArguments().getInt(Args.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (onDialogButtonClickListener != null)
									onDialogButtonClickListener
											.doNegativeClick();
							}
						}).create();
	}

	public OnDialogButtonClickListener getOnDialogButtonClickListener() {
		return onDialogButtonClickListener;
	}

	public void setOnDialogButtonClickListener(
			OnDialogButtonClickListener onDialogButtonClickListener) {
		this.onDialogButtonClickListener = onDialogButtonClickListener;
	}

	private interface Args {
		String title = "title";
		String message = "message";
		String ok = "ok";
		String cancel = "cancel";

	}

	interface OnDialogButtonClickListener {
		void doPositiveClick();

		void doNegativeClick();
	}
}
