package com.riktamtech.android.sellabike.ui;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.riktamtech.android.sellabike.R;

public class UniversalAdapter extends BaseAdapter {
	int resource;
	Context ctx;
	int rowType;

	public UniversalAdapter(int resource, Context ctx) {
		super();
		this.resource = resource;
		this.ctx = ctx;
		rowType = UniversalRowTypes.none;
	}

	public UniversalAdapter(int resource, Context ctx, int universalRowType) {
		super();
		this.resource = resource;
		this.ctx = ctx;
		this.rowType = universalRowType;
	}

	@Override
	public int getCount() {
		return 9;
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO this is shortcut: for premium ads
		if ((rowType == UniversalRowTypes.searchResult)) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (arg0 == 0 || arg0 == 5) {
				arg1 = inflater.inflate(R.layout.list_row_premium_ad, arg2,
						false);
			} else {
				arg1 = inflater.inflate(resource, arg2, false);
			}
			fontsizeSearchResult(arg1);
			return arg1;
		}
		if (arg1 == null) {
			LayoutInflater inflater = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(resource, arg2, false);
		}
		return arg1;
	}

	public void fontsizeSearchResult(View view) {

		TextView textView = (TextView) view.findViewById(R.id.textView1);
		String text = textView.getText().toString();
		SpannableString spannableString = new SpannableString(text);
		spannableString.setSpan(new ForegroundColorSpan(ctx.getResources()
				.getColor(R.color.orange)), text.indexOf("-") + 1, text
				.length(), SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
		textView.setText(spannableString);
	}

	interface UniversalRowTypes {
		int none = -1;
		int favourites = 0;
		int savedSearches = 1;
		int searchResult = 2;
		int bikeDetails = 3;
	}

}
