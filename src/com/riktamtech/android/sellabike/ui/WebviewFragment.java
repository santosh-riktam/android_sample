package com.riktamtech.android.sellabike.ui;

import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.riktamtech.android.sellabike.R;

public class WebviewFragment extends Fragment {
	private static final String TAG = "ww";
	private WebView webView;
	private String url;

	public WebviewFragment(String url) {
		super();
		this.url = url;
		Log.d(TAG, "url :" + url);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_webview, container, false);
		webView = (WebView) inflate.findViewById(R.id.webView1);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
					startActivity(Intent.createChooser(intent, "Pick"));
					return true;
				}

				else if (url.startsWith("mailto:")) {
					MailTo mailTo = MailTo.parse(url);
					Intent i = newEmailIntent(mailTo.getTo(), mailTo.getSubject(), mailTo.getBody(), mailTo.getCc());
					startActivity(Intent.createChooser(i, "Pick"));
					return true;
				} else {
					view.loadUrl(url);
				}
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		webView.loadUrl(url);

		return inflate;
	}

	public static Intent newEmailIntent(String address, String subject, String body, String cc) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_CC, cc);
		intent.setType("message/rfc822");
		return intent;
	}

	@Override
	public void onStart() {
		((BaseActivity) getActivity()).setupNavigationButton(R.string.back, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		}, true);
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
	}
}
