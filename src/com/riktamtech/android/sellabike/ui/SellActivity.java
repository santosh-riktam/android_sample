package com.riktamtech.android.sellabike.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.BikeTypes;
import com.riktamtech.android.sellabike.inapp.BillingService;
import com.riktamtech.android.sellabike.inapp.BillingService.RequestPurchase;
import com.riktamtech.android.sellabike.inapp.BillingService.RestoreTransactions;
import com.riktamtech.android.sellabike.inapp.Consts;
import com.riktamtech.android.sellabike.inapp.Consts.PurchaseState;
import com.riktamtech.android.sellabike.inapp.Consts.ResponseCode;
import com.riktamtech.android.sellabike.inapp.PurchaseObserver;
import com.riktamtech.android.sellabike.inapp.ResponseHandler;
import com.riktamtech.android.sellabike.ui.AddPicturesFragment.OnAddPicturesFragmentEventListener;
import com.riktamtech.android.sellabike.ui.ListDialogFragment.OnListDialogRowClickListener;
import com.riktamtech.android.sellabike.ui.SellFormFragment.OnSellFormFragmentEventListener;
import com.riktamtech.android.sellabike.util.Utils;
import com.riktamtech.android.sellabike.widget.AddPhotoView;
import com.riktamtech.android.sellabike.widget.PickerSpinner;

public class SellActivity extends BaseActivity implements OnSellFormFragmentEventListener {

	private static final int REQ_LIBRARY = 0;
	private static final int REQ_CAMERA = 1;
	private static final int REQ_LOCATION = 2;
	private View currentView;

	private AddPicturesFragment addPicturesFragment;

	private String singlePicBitmapLocation;

	private SellFormFragment sellFormFragment;

	ArrayList<Photo> premiumPicsArrayList;
	//private boolean isInPremiumMode;

	public BikeDAO dao;

	private String cameraPhotoOutputPath;

	// In-app purchase fields
	private Handler handler;
	private BillingService billingService;
	private MyPurchaseObserver observer;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_sell);
		reset();
		billingService = new BillingService();
		billingService.setContext(this);
		handler = new Handler();
		observer = new MyPurchaseObserver(handler);
		ResponseHandler.register(observer);
	}

	@Override
	public void onDestroy() {
		ResponseHandler.unregister(observer);
		billingService.unbind();
		super.onDestroy();
	}

	private void makePurchase() {
		// TODO change package name
		billingService.requestPurchase("com.rt.android.sellabike.inapp.item1", "no payload");
	}

	public void reset() {
		dao = new BikeDAO();
		premiumPicsArrayList = new ArrayList<Photo>();
		singlePicBitmapLocation = null;
		for (int i = 0; i < 9; i++)
			premiumPicsArrayList.add(new Photo());
		sellFormFragment = new SellFormFragment();
		sellFormFragment.setOnSellFormFragmentEventListener(this);
		attachFragment(sellFormFragment, false, BackStackNames.root);
	}

	public void tblRowClk(View v) {
		ViewGroup tblRow = (ViewGroup) v;
		for (int i = 0; i < tblRow.getChildCount(); i++) {
			View child = tblRow.getChildAt(i);
			if (child instanceof PickerSpinner)
				child.performClick();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == RESULT_OK) {
			if (arg0 == REQ_LIBRARY) {
				String path = getPath(arg2.getData());
				setStandardOrPemiumImage(path);
			} else if (arg0 == REQ_CAMERA) {

				//				Bitmap bmp = (Bitmap) arg2.getExtras().get("data");
				//				try {
				//					String targetLocation = getExternalCacheDir() + "" + new Random().nextLong() + ".jpg";
				//					bmp.compress(CompressFormat.JPEG, 100, new FileOutputStream(new File(targetLocation)));
				//					setStandardOrPemiumImage(targetLocation);
				//				} catch (FileNotFoundException e) {
				//					e.printStackTrace();
				//				}
				//				bmp.recycle();
				//				bmp = null;

				setStandardOrPemiumImage(cameraPhotoOutputPath);

			} else if (arg0 == REQ_LOCATION) {
				dao.lat = arg2.getIntExtra("lat", 0) / 1.0e6;
				dao.lon = arg2.getIntExtra("lon", 0) / 1.0e6;
				Log.d(TAG, "location " + dao.lat + " , " + dao.lon);
			}
		}
	}

	private void setStandardOrPemiumImage(String bitmapLocation) {
		if (dao.isPremium && currentView instanceof AddPhotoView) {
			getCurrentAddPhotoView().setImage(bitmapLocation);

		} else {
			singlePicBitmapLocation = bitmapLocation;
			sellFormFragment.updateSinglePicLayout(bitmapLocation);
		}
	}

	public void showContactDetailsFragment() {
		sellFormFragment.captureData(false);
		ContactDetailsFragment contactDetailsFragment = new ContactDetailsFragment();
		contactDetailsFragment.setOnSellFormFragmentEventListener(this);
		attachFragment(contactDetailsFragment);
	}

	public void showDraftsFragment() {
		sellFormFragment.captureData(false);
		attachFragment(new DraftsFragment());
	}

	public void showMapFragment(View v) {
		sellFormFragment.captureData(false);
		startActivityForResult(new Intent(this, MapLocationActivity.class), REQ_LOCATION);
	}

	public void showSellForm() {
		popBackStackCompletely();
		sellFormFragment = new SellFormFragment();
		sellFormFragment.setOnSellFormFragmentEventListener(this);
		attachFragment(sellFormFragment, false, BackStackNames.root);
	}

	public void popBackStackCompletely() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
			int id = fragmentManager.getBackStackEntryAt(0).getId();
			fragmentManager.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}

	}

	// listeners
	public void onAddManyPicsBtnClk(View v) {
		sellFormFragment.captureData(false);
		if (sellFormFragment != null)
			sellFormFragment.hideKeyboard();

		if (singlePicBitmapLocation != null && !singlePicBitmapLocation.equals("")) {
			dao.isPremium = true;
			addPicturesFragment = new AddPicturesFragment();
			addPicturesFragment.setOnAddPicturesListener(onAddPicturesFragmentEventListener);
			attachFragment(addPicturesFragment);
		} else {
			Toast.makeText(this, "Please add standard bike image first", 0).show();
		}
	}

	public void onAddOnePicBtnClk(View v) {
		sellFormFragment.captureData(false);
		// TODO check actual texts
		if (sellFormFragment != null)
			sellFormFragment.hideKeyboard();
		currentView = v;
		showListOfOptions();

	}

	private OnAddPicturesFragmentEventListener onAddPicturesFragmentEventListener = new OnAddPicturesFragmentEventListener() {

		public void onPicturesSelected(java.util.ArrayList<Photo> pictures) {
			setPremiumPicsArrayList(pictures);
		};

		@Override
		public void onAddPhotoViewClicked(AddPhotoView photoView) {
			currentView = photoView;
			showListOfOptions();
		}

		@Override
		public ArrayList<Photo> getSelectedPics() {
			return getPremiumPicsArrayList();
		}
	};

	OnListDialogRowClickListener onListDialogRowClickListener = new OnListDialogRowClickListener() {

		@Override
		public void onListDialogItemClicked(int index) {

			if ((currentView instanceof TextView && singlePicBitmapLocation != null) || (currentView instanceof AddPhotoView && getCurrentAddPhotoView().hasImage()))
				index--;

			if (index == -1) {
				if (currentView instanceof AddPhotoView)
					getCurrentAddPhotoView().clearImage();
				else {
					singlePicBitmapLocation = null;
					sellFormFragment.updateSinglePicLayout(null);
				}
			} else if (index == 0) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Pick"), REQ_LIBRARY);
			} else if (index == 1) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				cameraPhotoOutputPath = Utils.getAppCacheDir(SellActivity.this).getAbsolutePath() + "/img" + System.currentTimeMillis() + ".jpg";
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(cameraPhotoOutputPath)));
				startActivityForResult(Intent.createChooser(intent, "Pick"), REQ_CAMERA);
			}
		}
	};

	private void showListOfOptions() {
		ArrayList<String> items = new ArrayList<String>();
		items.addAll(Arrays.asList(getResources().getStringArray(R.array.addPictureOptions)));

		if (currentView instanceof TextView && singlePicBitmapLocation == null)
			items.remove(0);

		if (currentView instanceof AddPhotoView && !((AddPhotoView) currentView).hasImage())
			items.remove(0);

		ListDialogFragment listDialogFragment = ListDialogFragment.newInstance(items);
		listDialogFragment.setOnListDialogRowClickListener(onListDialogRowClickListener);
		listDialogFragment.show(getSupportFragmentManager(), Tags.addPictureOptions);
	}

	private String getPath(Uri uri) {
		String selectedImagePath;
		// MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
		String[] projection = { MediaStore.Images.Media.DATA };

		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			selectedImagePath = cursor.getString(column_index);
		} else {
			selectedImagePath = null;
		}

		if (selectedImagePath == null) {
			// 2:OI FILE Manager --- call method: uri.getPath()
			selectedImagePath = uri.getPath();
		}
		return selectedImagePath;
	}

	public ArrayList<Photo> getPremiumPicsArrayList() {
		return premiumPicsArrayList;
	}

	public void setPremiumPicsArrayList(ArrayList<Photo> premiumPicsArrayList) {
		this.premiumPicsArrayList = premiumPicsArrayList;
	}

	public ArrayList<Photo> getPhotosForUplaod() {
		ArrayList<Photo> pics = new ArrayList<Photo>();
		pics.add(new Photo(singlePicBitmapLocation, ""));
		if (dao.isPremium)
			pics.addAll(premiumPicsArrayList);
		return pics;
	}

	private interface Tags {
		String addPictureOptions = "addPictureOptions";
	}

	public AddPhotoView getCurrentAddPhotoView() {
		return (AddPhotoView) currentView;
	}

	public BikeDAO getDao() {
		if (dao == null)
			dao = new BikeDAO();
		return dao;
	}

	public void setDao(BikeDAO dao) {
		this.dao = dao;
		singlePicBitmapLocation = dao.photos.get(0).url;

		premiumPicsArrayList = new ArrayList<Photo>();
		for (int i = 0; i < 9; i++)
			premiumPicsArrayList.add(new Photo());

		for (int i = 1; i < dao.photos.size(); i++)
			premiumPicsArrayList.set(i - 1, dao.photos.get(i));
		dao.isPremium = dao.photos.size() > 1;
	}

	private class MyPurchaseObserver extends PurchaseObserver {
		public MyPurchaseObserver(Handler handler) {
			super(SellActivity.this, handler);
		}

		@Override
		public void onBillingSupported(boolean supported) {
			if (Consts.DEBUG) {
				log(TAG, "supported: " + supported);
			}

		}

		@Override
		public void onPurchaseStateChange(PurchaseState purchaseState, String itemId, int quantity, long purchaseTime, String developerPayload) {
			if (Consts.DEBUG) {
				log(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
			}

			if (purchaseState == PurchaseState.PURCHASED) {
				startUpload();
				log(TAG, "purchased");
			}
		}

		@Override
		public void onRequestPurchaseResponse(RequestPurchase request, ResponseCode responseCode) {
			if (Consts.DEBUG) {
				log(TAG, request.mProductId + ": " + responseCode);
			}
			if (responseCode == ResponseCode.RESULT_OK) {
				if (Consts.DEBUG) {
					log(TAG, "purchase was successfully sent to server");
				}
			} else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
				if (Consts.DEBUG) {
					log(TAG, "user canceled purchase");
				}
			} else {
				if (Consts.DEBUG) {
					log(TAG, "purchase failed");
				}
			}
		}

		@Override
		public void onRestoreTransactionsResponse(RestoreTransactions request, ResponseCode responseCode) {
			if (responseCode == ResponseCode.RESULT_OK) {
				if (Consts.DEBUG) {
					log(TAG, "completed RestoreTransactions request");
				}
			} else {
				if (Consts.DEBUG) {
					log(TAG, "RestoreTransactions error: " + responseCode);
				}
			}
		}
	}

	private void log(String tag, String message) {
		Log.d(tag, message);
	}

	@Override
	public void submitClicked() {
		if (dao.isPremium && Utils.containsNonNullItem(getPremiumPicsArrayList())) {
			// TODO better to show confirm dialog 
			makePurchase();
		} else {
			startUpload();
		}

	}

	private void startUpload() {
		if (dao.bikeId == BikeDAO.UN_ASSIGNED)
			dao.bikeId = BikeDAO.UPLOAD_IN_PROGRESS;
		new DBManager(this).insertOrUpdateBike(dao, BikeTypes.PENDING_FOR_UPLOAD);
		Intent intent = new Intent(getApplicationContext(), UploadService.class);
		// TODO should check if it is runnig when app is not runnig
		intent.putExtra("dao", dao);
		startService(intent);
		reset();
		toast("Submiting...");
	}

}
