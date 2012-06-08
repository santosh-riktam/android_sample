package com.riktamtech.android.sellabike.data;

import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String TAG = "db";
	private static final String DATABASE_NAME = "sellabike.db";
	private static final int DATABASE_VERSION = 2;
	Context ctx;

	public DBHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			Scanner scanner = new Scanner(ctx.getAssets().open("db.sql"));
			String sqlStatements = "";
			while (scanner.hasNext())
				sqlStatements += scanner.nextLine();
			scanner.close();
			StringTokenizer stringTokenizer = new StringTokenizer(sqlStatements, ";", false);
			while (stringTokenizer.hasMoreTokens()) {
				try {
					db.execSQL(stringTokenizer.nextToken());
				} catch (SQLException e) {
					Log.d(TAG, e.getMessage());
				}

			}

		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
