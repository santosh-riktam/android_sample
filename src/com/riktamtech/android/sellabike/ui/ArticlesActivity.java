package com.riktamtech.android.sellabike.ui;

import android.os.Bundle;

import com.mobfox.sdk.MobFoxView;
import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.NewsDAO;
import com.riktamtech.android.sellabike.dao.ReviewsDAO;

public class ArticlesActivity extends BaseActivity {

	public boolean isShowingNews = true;
	public int currentListRowIndex = 0;
	MobFoxView mobFoxView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_articles);
		mobFoxView = (MobFoxView) findViewById(R.id.mobFoxView);
		switchToTab(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mobFoxView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mobFoxView.resume();
	}

	public void showNewsDetails(NewsDAO newsDAO) {
		attachFragment(new NewsDetailsFragment(newsDAO));
	}

	public void showShareOptions(NewsDAO newsDAO, int i) {
		attachFragment(new ShareArticlesFragment(newsDAO, i));
	}

	public void showReviewsDetails(ReviewsDAO reviewsDAO) {
		attachFragment(new ReviewsDetailsFragment(reviewsDAO));
	}

	public void showShareOptions(ReviewsDAO reviewsDAO, int i) {
		attachFragment(new ShareArticlesFragment(reviewsDAO, i));
	}

	public void switchToTab(boolean isNews) {
		attachFragment(NewsReviewsFragment.newInstance(isNews), false, BackStackNames.root);
	}
}
