package com.riktamtech.android.sellabike.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.data.PrefsManager;

public class SettingsFragment extends Fragment {

	private TextView fbTextView, twTextView, myAdsTextView, mailThoughtsTextView;
	private PrefsManager prefsManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_settings, container, false);
		prefsManager = new PrefsManager(getActivity());
		fbTextView = (TextView) inflate.findViewById(R.id.facebookTextView);
		twTextView = (TextView) inflate.findViewById(R.id.twitterTextView);
		myAdsTextView = (TextView) inflate.findViewById(R.id.manageAdsTextView);
		mailThoughtsTextView = (TextView) inflate.findViewById(R.id.mailThoughtsTextView);
		myAdsTextView.setOnClickListener(myAdsClickListener);
		mailThoughtsTextView.setOnClickListener(mailThoughtsClickListener);
		updateFbTextViewText(isLoggedIntoFb());
		updateTwTextviewText(isLoggedIntoTw());
		return inflate;
	}

	private boolean isLoggedIntoFb() {
		return prefsManager.getFbAccessToken() != null && prefsManager.getFbExpiresIn() != null;
	}

	private boolean isLoggedIntoTw() {
		return prefsManager.getTwToken() != null && prefsManager.getTwTokenSecret() != null;
	}

	OnClickListener myAdsClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((SettingsActivity) getActivity()).showMyAdsFragment();

		}
	};
	OnClickListener mailThoughtsClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			sendEmail();
		}
	};

	public void updateFbTextViewText(boolean loggedIn) {

		fbTextView.setText(loggedIn ? R.string.signout_fb : R.string.signin_fb);
	}

	public void updateTwTextviewText(boolean loggedIn) {
		twTextView.setText(loggedIn ? R.string.signout_tw : R.string.signin_tw);
	}

	public void sendEmail() {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { "feedback@sellabike.me" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback about Sell A Bike");

		String emailBody = "Hello," + "\n \n" + "I just tried an app and I found it really interesting I think you should try it some time." + "\n \n"
				+ "\n and also challenge your friends. Here comes the best part of it… its FREE!! :)" + "\n\n" + "Enjoy!! Have a Great Day!!";

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + emailBody);

		getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));

	}

}
