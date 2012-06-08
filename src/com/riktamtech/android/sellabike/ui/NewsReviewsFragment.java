package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.NewsDAO;
import com.riktamtech.android.sellabike.dao.ReviewsDAO;
import com.riktamtech.android.sellabike.data.DBHelper;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.ReviewsColumns;
import com.riktamtech.android.sellabike.data.DBManager.Tables;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.NewsParser;
import com.riktamtech.android.sellabike.io.ReviewsParser;
import com.riktamtech.android.sellabike.util.DisplayUtils;

public class NewsReviewsFragment extends Fragment {
	ListView listView;
	RadioButton newsButton, reviewsButton;

	private NewsParser newsParser;
	private ReviewsParser reviewsParser;
	private ArrayList<NewsDAO> newsArrayList;
	private Cursor reviewsCursor;
	private SQLiteDatabase database;

	private static boolean isNews = true;

	public static NewsReviewsFragment newInstance(boolean showNews) {
		NewsReviewsFragment newsReviewsFragment = new NewsReviewsFragment();
		Bundle bundle = new Bundle();
		isNews = showNews;
		newsReviewsFragment.setArguments(bundle);
		return newsReviewsFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		newsParser = new NewsParser(getActivity());
		reviewsParser = new ReviewsParser(getActivity());

		newsArrayList = new ArrayList<NewsDAO>();
		newsParser.parseXmlFromUrl(ServiceURLBuilder.getNewsUrl(), newsParseListener);

		reviewsParser.parseXmlFromUrl(ServiceURLBuilder.getReviewsUrl(), reviewsParseListener);

		View inflate = inflater.inflate(R.layout.fragment_news_reviews, container, false);
		listView = (ListView) inflate.findViewById(R.id.listView1);
		listView.setOnItemClickListener(listItemClickListener);
		newsButton = (RadioButton) inflate.findViewById(R.id.radio0);
		reviewsButton = (RadioButton) inflate.findViewById(R.id.radio1);
		newsButton.setOnClickListener(newsAndReviewsOnClickListener);
		reviewsButton.setOnClickListener(newsAndReviewsOnClickListener);
		// NewsReviewsFragment.isNews =
		// getArguments().getBoolean(BundleKeys.IS_NEWS);

		if (NewsReviewsFragment.isNews)
			newsButton.performClick();
		else
			reviewsButton.performClick();
		return inflate;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (newsArrayList == null || newsArrayList.size() == 0) {
			newsArrayList = new ArrayList<NewsDAO>();
			newsParser.parseXmlFromUrl(ServiceURLBuilder.getNewsUrl(), newsParseListener);
		}
		if (reviewsCursor == null) {
			reviewsParser.parseXmlFromUrl(ServiceURLBuilder.getReviewsUrl(), reviewsParseListener);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		NewsReviewsFragment.isNews = newsButton.isChecked();
	}

	private ParseListener newsParseListener = new ParseListener() {
		@Override
		public void onParseComplete(Context context, Object object) {
			newsArrayList = (ArrayList<NewsDAO>) object;
			if (newsButton.isChecked()) {
				((NewsAdapter) listView.getAdapter()).setNews(newsArrayList);
			}
		}

		@Override
		public void onError(Context context, Bundle bundle) {
			toast(context, Messages.SERVICE_ERROR);
		}
	};

	protected void toast(Context context, String text) {
		Toast.makeText(context, text, 0).show();
	}

	private ParseListener reviewsParseListener = new ParseListener() {
		@Override
		public void onParseComplete(Context context, Object object) {
			ArrayList<ReviewsDAO> reviewsArrayList = (ArrayList<ReviewsDAO>) object;
			new DBManager(getActivity().getApplicationContext()).insertReviews(reviewsArrayList);
			database = new DBHelper(getActivity()).getWritableDatabase();
			reviewsCursor = database.query(Tables.TBL_REVIEWS, ReviewsQuery.columns, null, null, null, null, null);
			if (reviewsButton.isChecked()) {
				listView.setAdapter(new ReviewsCursorAdapter(getActivity()));
			}
		}

		@Override
		public void onError(Context context, Bundle bundle) {
			toast(context, Messages.SERVICE_ERROR);
		}
	};

	private interface ReviewsQuery {
		String columns[] = { ReviewsColumns.REVIEW_ID, ReviewsColumns.TITLE, ReviewsColumns.THUMB, ReviewsColumns.SUMMARY, ReviewsColumns.SHARE_SUMMARY, ReviewsColumns.FULL_TEXT };
		int REVIEW_ID = 0;
		int TITLE = 1;
		int THUMB = 2;
		int SUMMARY = 3;
		int SHARE_SUMMARY = 4;
		int FULL_TEXT = 5;

	}

	public void onDestroy() {
		super.onDestroy();
		if (reviewsCursor != null)
			reviewsCursor.close();
		if (database != null)
			database.close();
	};

	View.OnClickListener newsAndReviewsOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.radio0)
				listView.setAdapter(new NewsAdapter(newsArrayList));
			else if (v.getId() == R.id.radio1)
				listView.setAdapter(new ReviewsCursorAdapter(getActivity()));
		}
	};

	OnItemClickListener listItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if (newsButton.isChecked()) {// TODO change to full text
				((ArticlesActivity) getActivity()).showNewsDetails(newsArrayList.get(arg2));
			} else {
				reviewsCursor.moveToPosition(arg2);
				ReviewsDAO reviewsItem = new ReviewsDAO(reviewsCursor.getInt(ReviewsQuery.REVIEW_ID), reviewsCursor.getString(ReviewsQuery.THUMB),
						reviewsCursor.getString(ReviewsQuery.TITLE), reviewsCursor.getString(ReviewsQuery.SUMMARY), reviewsCursor.getString(ReviewsQuery.SHARE_SUMMARY),
						reviewsCursor.getString(ReviewsQuery.FULL_TEXT));
				((ArticlesActivity) getActivity()).showReviewsDetails(reviewsItem);
			}
		}
	};

	private class ReviewsCursorAdapter extends CursorAdapter {

		public ReviewsCursorAdapter(Context context) {
			super(context, reviewsCursor, true);
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			ReviewsDAO reviewsItem = new ReviewsDAO(arg2.getInt(ReviewsQuery.REVIEW_ID), arg2.getString(ReviewsQuery.THUMB), arg2.getString(ReviewsQuery.TITLE),
					arg2.getString(ReviewsQuery.SUMMARY), arg2.getString(ReviewsQuery.SHARE_SUMMARY), arg2.getString(ReviewsQuery.FULL_TEXT));
			ReviewsHolder holder = (ReviewsHolder) arg0.getTag();
			holder.titleTextView.setText(reviewsItem.title);
			holder.discTextView.setText(Html.fromHtml(reviewsItem.summary));
			// TODO set image too
			((BaseActivity) getActivity())
					.setImage(listView, holder.imageView, arg2.getPosition(), reviewsItem.thumb, DisplayUtils.dispayWidth / 4, DisplayUtils.displayHeight / 6);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			View view = getActivity().getLayoutInflater().inflate(R.layout.list_row_reviews, arg2, false);
			ReviewsHolder holder = new ReviewsHolder(view);
			view.setTag(holder);
			return view;
		}

	}

	private class NewsAdapter extends BaseAdapter {

		ArrayList<NewsDAO> newsArrayList;

		public NewsAdapter(ArrayList<NewsDAO> newsArrayList) {
			super();
			setNews(newsArrayList);
		}

		public void setNews(ArrayList<NewsDAO> newsArrayList) {
			if (newsArrayList == null)
				newsArrayList = new ArrayList<NewsDAO>();
			this.newsArrayList = newsArrayList;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return newsArrayList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_row_news, parent, false);
				NewsHolder holder = new NewsHolder(convertView);
				convertView.setTag(holder);
			}
			NewsDAO newsItem = newsArrayList.get(position);
			NewsHolder holder = (NewsHolder) convertView.getTag();
			holder.titleTextView.setText(newsItem.title);
			holder.descTextView.setText(Html.fromHtml(newsItem.summary));
			// TODO set image too
			((BaseActivity) getActivity()).setImage(listView, holder.imageView, position, newsItem.thumb, DisplayUtils.dispayWidth / 4, DisplayUtils.dispayWidth / 6);
			return convertView;
		}

	}

	private class NewsHolder {
		TextView titleTextView, descTextView;
		ImageView imageView;

		public NewsHolder(View v) {
			titleTextView = (TextView) v.findViewById(R.id.textView1);
			descTextView = (TextView) v.findViewById(R.id.textView2);
			imageView = (ImageView) v.findViewById(R.id.imageView2);
		}
	}

	private class ReviewsHolder {
		TextView titleTextView, discTextView;
		ImageView imageView;

		public ReviewsHolder(View v) {
			titleTextView = (TextView) v.findViewById(R.id.textView1);
			discTextView = (TextView) v.findViewById(R.id.textView2);
			imageView = (ImageView) v.findViewById(R.id.imageView1);
		}
	}

	private interface Messages {
		String INVALID_EMAIL = "Invalid email";
		String VALUE_REQUIRED = "Value required";
		String SERVICE_ERROR = "Internal error";
	}
}
