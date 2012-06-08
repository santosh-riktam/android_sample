package com.riktamtech.android.sellabike.ui;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.data.DBHelper;
import com.riktamtech.android.sellabike.data.DBManager.SearchParamsCloumns;
import com.riktamtech.android.sellabike.data.DBManager.Tables;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class SellABikeActivity extends FragmentActivity {
	/** Called when the activity is first created. */
	ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		listView=(ListView) findViewById(R.id.listView1);
		SQLiteDatabase writableDatabase = new DBHelper(this).getWritableDatabase();
		Cursor cursor = writableDatabase.query(Tables.TBL_SEARCH_PARAMS, new String[] {SearchParamsCloumns.ENGINE_ID}, null, null, null, null, null);
		listView.setAdapter(new MyAdapter(this, cursor, true));
	}

	class MyAdapter extends CursorAdapter{

		public MyAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			
		}

		public MyAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
			
		}

		@Override
		public void bindView(View arg0, Context arg1, Cursor arg2) {
			TextView textView=(TextView) arg0.findViewById(R.id.textView1);
			textView.setText(arg2.getInt(0)+"");
			
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			LayoutInflater inflater=(LayoutInflater) arg0.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View inflate = inflater.inflate(R.layout.list_row_savedsearches, arg2,false);
			return inflate;
		}
		
		
	}
}