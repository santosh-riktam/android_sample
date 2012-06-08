package com.riktamtech.android.sellabike.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.NewsDAO;

public class NewsDetailsFragment extends Fragment {
	private NewsDAO newsDAO;

	public NewsDetailsFragment(NewsDAO newsDAO) {
		super();
		this.newsDAO = newsDAO;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_news_details, null);
		inflate.findViewById(R.id.radio0)
				.setOnClickListener(radioClickListener);
		inflate.findViewById(R.id.radio1)
				.setOnClickListener(radioClickListener);
		WebView webView = (WebView) inflate.findViewById(R.id.webView1);
		webView.loadData(newsDAO.fullText, "text/html", "UTF-8");
		return inflate;
	}

	OnClickListener radioClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.radio0)
				((ArticlesActivity) getActivity()).switchToTab(true);
			else if (v.getId() == R.id.radio1)
				((ArticlesActivity) getActivity()).switchToTab(false);
		}
	};

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
	}

	public void goBack() {
		((FragmentActivity) getActivity()).getSupportFragmentManager()
				.popBackStack();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		((BaseActivity) getActivity()).setupNavigationButton(R.string.share,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						((ArticlesActivity) getActivity()).showShareOptions(
								newsDAO, 1);
					}
				}, false);

	}
}
