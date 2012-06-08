package com.riktamtech.android.sellabike.ui;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.facebook.android.Facebook;
import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.ui.FeaturesDialogFragment.OnFeaturesSelectionListener;
import com.riktamtech.android.sellabike.util.ImageTools;
import com.riktamtech.android.sellabike.util.ImagesCacheSD;
import com.riktamtech.android.sellabike.util.Utils;

public class BaseActivity extends FragmentActivity {

	protected RequestToken oAuthRequestToken;
	protected Twitter twitter;
	protected Facebook facebook;

	private static ArrayList<Activity> activities = new ArrayList<Activity>();
	protected static final String TAG = "ui";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		activities.add(this);
		super.onCreate(savedInstanceState);
		BugSenseHandler.setup(this, "0a608a1e");
	}

	public void popStackTillRootFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++)
			fragmentManager.popBackStack();

	}

	public void doPositiveClick() {
		Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show();
	}

	public void doNegativeClick() {
		Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		activities.remove(this);
		finishAll();
	}

	public static void finishAll() {
		for (Activity activity : activities)
			activity.finish();
	}

	protected void attachFragment(Fragment fragment, boolean addToBackTask, String tag) {

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.tab_content, fragment);
		if (addToBackTask)
			transaction.addToBackStack(tag);
		transaction.commit();
	}

	protected void attachFragment(Fragment frg) {
		attachFragment(frg, true, frg.getTag());
	}

	public void setupNavigationButton(int textResource, OnClickListener listener, boolean isLeftButton) {
		Button button = (Button) (isLeftButton ? findViewById(R.id.navBarLeftButton) : findViewById(R.id.navBarRightButton));
		button.setText(textResource);
		if (listener == null)
			listener = defaultOnClickListener;
		button.setOnClickListener(listener);
		if (isLeftButton)
			findViewById(R.id.navBarLefSeperator).setVisibility(View.VISIBLE);
		else
			findViewById(R.id.navBarRightSeperator).setVisibility(View.VISIBLE);
	}

	public void removeNavigationBarButtons() {
		Button button1 = (Button) findViewById(R.id.navBarLeftButton);
		Button button2 = (Button) findViewById(R.id.navBarRightButton);
		button1.setText("");
		button2.setText("");
		button1.setOnClickListener(defaultOnClickListener);
		button2.setOnClickListener(defaultOnClickListener);
		findViewById(R.id.navBarLefSeperator).setVisibility(View.INVISIBLE);
		findViewById(R.id.navBarRightSeperator).setVisibility(View.INVISIBLE);
	}

	private OnClickListener defaultOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
		}
	};

	interface BackStackNames {
		String root = "root";
		String favourites = "favourites";
		String savedSearches = "savedsearches";
		String myAds = "myads";
		String myDrafts = "mydrafts";
	}

	public void showFeaturesDialog(ArrayList<Integer> indicesOfFeatures, OnFeaturesSelectionListener onFeaturesSelectionListener) {
		FeaturesDialogFragment featuresDialogFragment = FeaturesDialogFragment.newInstance(indicesOfFeatures, onFeaturesSelectionListener);
		featuresDialogFragment.show(getSupportFragmentManager(), "featuresDialog");
	}

	protected void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	// TODO when to clear cache?
	public void clearCache() {
		ArrayList<File> arrayList = new ArrayList<File>();
		arrayList.add(getCacheDir());
		do {
			File file = arrayList.remove(0);
			if (file.isFile())
				file.delete();
			else if (file.isDirectory()) {
				if (file.list().length > 0) {
					arrayList.addAll(Arrays.asList(file.listFiles()));
				} else
					file.delete();
			} else
				;
		} while (arrayList.size() > 0);
	}

	public void setImage(ListView listView, ImageView imageView, int position, String url, int targetWidth, int targetHeight) {
		if (url == null) {
			imageView.setImageBitmap(null);
			return;
		}

		ImagesCacheSD imagesCache = new ImagesCacheSD();
		if (imagesCache.containsImage(url)) {
			if (targetWidth > 0) {
				imageView.setImageBitmap(ImageTools.resizeBitmapToSize(imagesCache.getBitmapFromCache(url, targetWidth), targetWidth, targetHeight));
			} else {
				imageView.setImageBitmap(imagesCache.getBitmapFromCache(url));
			}
		} else {
			imageView.setTag(url);
			new ImageLoaderTask(listView, imageView, position, targetWidth, targetHeight).execute(url);
		}

	}

	public void setImage(ListView listView, ImageView imageView, int position, String url) {
		setImage(listView, imageView, position, url, -1, -1);
	}

	public void setImage(ImageView imageView, String url) {
		setImage(null, imageView, -1, url, -1, -1);
	}

	public void setImage(ImageView imageView, String url, int targetWidth, int targetHeight) {
		setImage(null, imageView, -1, url, targetWidth, targetHeight);
	}

	class ImageLoaderTask extends AsyncTask<String, Object, Bitmap> {
		WeakReference<ListView> listViewReference;
		WeakReference<ImageView> imageViewReference;
		int position;
		int targetWidth, targetHeight;

		public ImageLoaderTask(ListView listView, ImageView imageView, int pos, int targetWidth, int targetHeight) {
			super();
			this.listViewReference = new WeakReference<ListView>(listView);
			this.imageViewReference = new WeakReference<ImageView>(imageView);
			this.position = pos;
			this.targetWidth = targetWidth;
			this.targetHeight = targetHeight;
			if (targetWidth > 0)
				imageView.setImageBitmap(getDefaultBitmap());
		}

		public Bitmap getDefaultBitmap() {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.color.transparent);
			return bitmap;
		}
		
		private String url;

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				url=params[0];
				ImagesCacheSD imagesCacheSD = new ImagesCacheSD();
				if (imageViewReference.get() != null) {
					if (imagesCacheSD.downloadBitmap(params[0])) {
						Bitmap bitmap = imagesCacheSD.getBitmapFromCache(params[0], targetWidth);
						if (bitmap != null && targetWidth != -1) {
							Bitmap scaledBitmap = ImageTools.resizeBitmapToSize(bitmap, targetWidth, targetHeight);
							bitmap.recycle();
							bitmap = null;
							return scaledBitmap;
						}
						return bitmap;
					} else {
						return null;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null && imageViewReference != null && imageViewReference.get() != null && (imageViewReference.get().getTag()+"").equals(url)) {
				imageViewReference.get().setImageBitmap(result);
				Log.d(TAG, "gotcha ");
			} else {
				if (result != null)
					result.recycle();
				result = null;
				Log.d(TAG, "returned null");
			}
		}

		private boolean isValidView() {
			// either this is image not in a listview row or the user hasn't
			// scrolled listview much
			if (listViewReference.get() == null || (listViewReference.get().getFirstVisiblePosition() <= position && listViewReference.get().getLastVisiblePosition() >= position))
				return true;
			return false;
		}
	}

	private UploadReciever uploadReciever;

	@Override
	protected void onResume() {
		super.onResume();
		if (uploadReciever == null)
			uploadReciever = new UploadReciever();
		IntentFilter intentFilter = new IntentFilter(Utils.UPLOAD_FINISHED_INTENT);
		LocalBroadcastManager.getInstance(this).registerReceiver(uploadReciever, intentFilter);
		intentFilter = new IntentFilter(Utils.UPLOAD_FAILED_INTENT);
		LocalBroadcastManager.getInstance(this).registerReceiver(uploadReciever, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (uploadReciever != null)
			LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadReciever);
	}

	private class UploadReciever extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Utils.UPLOAD_FINISHED_INTENT)) {
				Log.d(TAG, "received broadcast");
				new DBManager(context).deleteBike(intent.getIntExtra("bikeId", -1));
				Toast.makeText(context, "Upload successful", 0).show();

			} else if (intent.getAction().equals(Utils.UPLOAD_FAILED_INTENT))
				Toast.makeText(context, "Upload failed", 0).show();
		}
	}

	public void contactSellerGumtree(BikeDAO bikeDAO) {
		attachFragment(new WebviewFragment(bikeDAO.url));
	}

	public void contactSellerClick(BikeDAO bikeDAO) {
		ContactSellerFragment contactSellerFragment = new ContactSellerFragment(bikeDAO);
		contactSellerFragment.show(getSupportFragmentManager(), "contactSellerDilaog");
	}

	class ContactSellerFragment extends DialogFragment {
		BikeDAO bikeDAO;

		ContactSellerFragment(BikeDAO bikeDAO) {
			this.bikeDAO = bikeDAO;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select an option");
			builder.setItems(R.array.contact_seller_items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					String telephoneNumString = "tel:" + bikeDAO.telephone.toString(), emailAddr = bikeDAO.email.toString() + "";
					if (item == 0) {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse(telephoneNumString + ""));
						callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(Intent.createChooser(callIntent, "Select"));
						// intent = new Intent(Intent.ACTION_CALL, Uri
						// .parse(telephoneNumString));
						// startActivity(Intent.createChooser(intent,
						// "call using"));
					} else if (item == 1) {
						final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
						emailIntent.setType("plain/text");
						emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { emailAddr + "" });
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback about Sell A Bike");

						String emailBody = "Hello," + "\n \n";

						emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + emailBody);

						getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
						// intent = new Intent(Intent.ACTION_SEND);
						// intent.putExtra(
						// android.content.Intent.EXTRA_EMAIL,
						// new String[] { emailAddr });
						// intent.putExtra(
						// android.content.Intent.EXTRA_SUBJECT,
						// "sell a bike");
						// startActivity(Intent.createChooser(intent,
						// "Send using"));
					}
				}
			});
			AlertDialog alert = builder.create();
			return alert;
		}
	}

}
