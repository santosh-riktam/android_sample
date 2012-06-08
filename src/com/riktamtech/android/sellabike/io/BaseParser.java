package com.riktamtech.android.sellabike.io;

import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public abstract class BaseParser {
	Context ctx;

	public BaseParser(Context ctx) {
		super();
		this.ctx = ctx;
	}

	private String getContentFromUrl(String url) throws IOException, ClientProtocolException {
		Log.d(TAG, "executing " + url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);
		HttpResponse response = httpClient.execute(getRequest);
		String str = EntityUtils.toString(response.getEntity());
		Log.d(TAG, url + "\n : result " + str);
		return str;
	}

	String url = "sellabike.me/api/getModels";
	ParseListener parseListener;
	private final static String TAG = "sab";

	public interface ParseListener {
		String ERROR = "error";

		public void onParseComplete(Context context, Object object);

		public void onError(Context context, Bundle bundle);
	}

	public abstract Object parseXml(String xmlString) throws Exception;

	public void parseXmlFromUrl(String url, ParseListener parseListener) {
		this.url = url;
		this.parseListener = parseListener;
		new ServiceTask(this).execute(url);
	}

	public Object parseXmlFromUrl(String url) {
		try {
			return parseXml(getContentFromUrl(url));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void onServiceResponse(Object result) {

		if (result != null && result instanceof String)
			try {
				parseListener.onParseComplete(ctx, parseXml((String) result));
			} catch (Exception e) {
				e.printStackTrace();
				Bundle bundle = new Bundle();
				bundle.putSerializable(ParseListener.ERROR, e);
				parseListener.onError(ctx, bundle);
			}
		else {
			Bundle bundle = new Bundle();
			bundle.putSerializable(ParseListener.ERROR, (Exception) result);
			parseListener.onError(ctx, bundle);
		}

	}

	static class ServiceTask extends AsyncTask<String, Object, Object> {
		WeakReference<BaseParser> parserReference;

		public ServiceTask(BaseParser parser) {
			super();
			this.parserReference = new WeakReference<BaseParser>(parser);
		}

		@Override
		protected Object doInBackground(String... params) {
			try {
				if (parserReference != null && parserReference.get() != null) {
					return parserReference.get().getContentFromUrl(params[0]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return e;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			if (parserReference != null && parserReference.get() != null) {
				parserReference.get().onServiceResponse(result);
			}

		}
	}
}
