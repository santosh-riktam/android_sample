package com.riktamtech.android.sellabike.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.ColourDAO;
import com.riktamtech.android.sellabike.dao.EngineDAO;
import com.riktamtech.android.sellabike.dao.MakeDAO;
import com.riktamtech.android.sellabike.dao.ModelDAO;
import com.riktamtech.android.sellabike.dao.Photo;
import com.riktamtech.android.sellabike.dao.RegistrationResultDAO;
import com.riktamtech.android.sellabike.data.DBManager;
import com.riktamtech.android.sellabike.data.DBManager.BikeTypes;
import com.riktamtech.android.sellabike.data.ServiceURLBuilder;
import com.riktamtech.android.sellabike.io.BaseParser.ParseListener;
import com.riktamtech.android.sellabike.io.RegistrationResultParser;
import com.riktamtech.android.sellabike.ui.FeaturesDialogFragment.OnFeaturesSelectionListener;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.ImageTools;
import com.riktamtech.android.sellabike.util.TextTools;
import com.riktamtech.android.sellabike.util.Utils;
import com.riktamtech.android.sellabike.widget.PhotosGrid;
import com.riktamtech.android.sellabike.widget.PickerSpinner;

public class SellFormFragment extends Fragment {
	protected static final String TAG = "SellFormFragment";
	private BikeDAO dao;
	private DBManager dbManager;
	private ArrayList<Integer> checkedFeatures;
	private OnSellFormFragmentEventListener onSellFormFragmentEventListener;
	// UI
	private View emptyPicsLayout, premiumPicsLayout;
	private TextView singlePicTextView;
	private TextView bikeFeaturesTextView, contactDetailsTextView,
			locationTextView;
	private EditText regEditText, mileageEditText, headlineEditText,
			descriptionEditText, priceEditText;
	private PickerSpinner makesPickerSpinner, modelsPickerSpinner,
			engineSizePickerSpinner, colorPickerSpinner;
	private PickerSpinner sellerTypePickerSpinner;
	private TextView yearTextView;
	private Button saveButton, sendButton;
	private View inflate;
	private PhotosGrid photosGrid;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d(TAG, "oncreateiew");
		setHasOptionsMenu(true);
		dbManager = new DBManager(getActivity().getApplicationContext());
		inflate = inflater.inflate(R.layout.fragment_sell_form, container,
				false);

		locationTextView = (TextView) inflate
				.findViewById(R.id.locationTextView);

		singlePicTextView = (TextView) inflate
				.findViewById(R.id.add1PictureButton);
		inflate.postDelayed(new Runnable() {

			@Override
			public void run() {
				String singleBitmapLocation = null;
				if (onSellFormFragmentEventListener != null) {
					singleBitmapLocation = onSellFormFragmentEventListener
							.getPhotosForUplaod().get(0).url;
				}
				if (singleBitmapLocation != null
						&& !TextUtils.isEmpty(singleBitmapLocation))
					updateSinglePicLayout(singleBitmapLocation);
			}
		}, 10);

		sendButton = (Button) inflate.findViewById(R.id.sendButton);
		sendButton.setOnClickListener(sendButtonClickListener);
		saveButton = (Button) inflate.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(saveButtonClickListener);
		photosGrid = (PhotosGrid) inflate.findViewById(R.id.photosGrid1);
		ArrayList<String> bitmaps = new ArrayList<String>();
		ArrayList<Photo> premiumPicsArrayList = null;
		if (onSellFormFragmentEventListener != null) {
			premiumPicsArrayList = onSellFormFragmentEventListener
					.getPremiumPicsArrayList();
		}
		if (premiumPicsArrayList != null) {
			for (Photo Photo : premiumPicsArrayList) {
				bitmaps.add(Photo.url);
			}
		}
		photosGrid.setImages(bitmaps);

		emptyPicsLayout = inflate.findViewById(R.id.standardPremiumLayout);
		premiumPicsLayout = inflate.findViewById(R.id.premiumPicsLayout);

		regEditText = (EditText) inflate.findViewById(R.id.regEditText);
		regEditText.setOnKeyListener(regKeyListener);
		Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/UKNumberPlate.ttf");
		regEditText.setTypeface(face);
		regEditText.setFilters(new InputFilter[] { new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				if (source.length() == 0)
					return null;
				if (dest.length() == 4)
					return "\n" + source.subSequence(start, end);
				if (source.equals(" ") || dest.length() == 9)
					return "";
				return null;
			}
		} });

		makesPickerSpinner = (PickerSpinner) inflate
				.findViewById(R.id.makePickerSpinner);

		modelsPickerSpinner = (PickerSpinner) inflate
				.findViewById(R.id.modelPickerSpinner);

		// fontize cc
		engineSizePickerSpinner = (PickerSpinner) inflate
				.findViewById(R.id.EngineSizePickerSpinner);

		colorPickerSpinner = (PickerSpinner) inflate
				.findViewById(R.id.colorPickerSpinner);

		yearTextView = (TextView) inflate.findViewById(R.id.yearTextView);

		mileageEditText = (EditText) inflate.findViewById(R.id.mileageEditText);
		mileageEditText.setFilters(new InputFilter[] { TextTools.integerFilter,
				new InputFilter.LengthFilter(6) });

		bikeFeaturesTextView = (TextView) inflate
				.findViewById(R.id.bikeFeaturesTextView);

		headlineEditText = (EditText) inflate
				.findViewById(R.id.headlineEditText1);
		// headlineEditText
		// .setFilters(new InputFilter[] { new InputFilter.LengthFilter(50) });

		descriptionEditText = (EditText) inflate
				.findViewById(R.id.descriptionEditText);
		descriptionEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
						250) });

		priceEditText = (EditText) inflate.findViewById(R.id.priceEditText);

		mileageEditText.setFilters(new InputFilter[] { TextTools.integerFilter,
				new InputFilter.LengthFilter(5) });

		priceEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });

		sellerTypePickerSpinner = (PickerSpinner) inflate
				.findViewById(R.id.sellerTypePickerSpinner);

		contactDetailsTextView = (TextView) inflate
				.findViewById(R.id.contactDetailsTextView);
		contactDetailsTextView
				.setOnClickListener(onContactDetailsClickListener);

		for (int resource : new int[] { R.id.bikeFeaturesLayout,
				R.id.makeLayout, R.id.modelLayout, R.id.colorLayout,
				R.id.engineSizeLayout, R.id.yearLayout })
			inflate.findViewById(resource).setOnClickListener(
					tableCellClickListener);

		refreshData();

		return inflate;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		menu.add("Drafts");
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Drafts")) {
			hideKeyboard();
			((SellActivity) getActivity()).showDraftsFragment();
		}
		return super.onOptionsItemSelected(item);
		
	}
	
	public void refreshData() {

		if (onSellFormFragmentEventListener != null) {
			dao = onSellFormFragmentEventListener.getDao();
		}
		if (dao != null) {
			if (dao.regNo != null) {
				regEditText.setText(dao.regNo + "");
				validateRegId(regEditText, false);
			}

			// makesPickerSpinner.setText(dbManager.getMake((dao.makeID)).name);
			// modelsPickerSpinner.setText(dbManager.getModel(dao.modelID).name);
			// engineSizePickerSpinner.setText(dbManager.getEngine(dao.engineID).name);
			// colorPickerSpinner.setText(dbManager.getColour(dao.colorId).name);

			if (dao.year > 0)
				yearTextView.setText(dao.year + "");
			if (dao.mileage > 0)
				mileageEditText.setText(dao.mileage + "");
			checkedFeatures = dao.featuresArrayList;
			if (checkedFeatures == null)
				checkedFeatures = new ArrayList<Integer>();
			bikeFeaturesTextView.setText("(" + checkedFeatures.size() + ")");
			
			Log.d(TAG, "refresh data - setting headline to  "+dao.bikeName);
			headlineEditText.setText(dao.bikeName);
			descriptionEditText.setText(dao.description);
			if (dao.price > 0)
				priceEditText.setText((int) dao.price + "");
			sellerTypePickerSpinner.setData(R.string.seller_type,
					new ArrayAdapter<String>(getActivity(), -1, getResources()
							.getStringArray(R.array.seller_types)),
					dao.sellerId, null, true);

			for (String text : new String[] { dao.firstName, dao.lastName,
					dao.email, dao.telephone }) {
				if (text != null && !TextUtils.isEmpty(text.trim())) {
					contactDetailsTextView
							.setText(R.string.adjust_contact_details);
					contactDetailsTextView.setTextColor(getResources()
							.getColor(R.color.orange));
					break;
				}
			}
		} else {
			Log.e(TAG, "dao shouldn't be null");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "on resume");
		if (onSellFormFragmentEventListener != null && dao != null) {
			if (dao.isPremium
					&& Utils.containsNonNullItem(onSellFormFragmentEventListener
							.getPremiumPicsArrayList()))
				addPremiumPicsView(true);
			else
				addPremiumPicsView(false);
		}
		if (dao.lat != 0) {
			locationTextView.setText(R.string.adjust_location);
			locationTextView.setTextColor(getResources().getColor(
					R.color.orange));
		}
		regEditText.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (getActivity() != null)
					hideKeyboard();
			}
		}, 99);
	}

	

	private OnKeyListener regKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			validateRegId((EditText) v, keyCode == KeyEvent.KEYCODE_DEL);
			return false;
		}
	};

	private void validateRegId(TextView v, boolean isDelKey) {
		try {
			if (v.getId() == R.id.regEditText) {
				String text = v.getText().toString();
				if (isDelKey)
					text = text.substring(0, text.length() - 1);
				// Log.d(TAG, "text " + text);
				if (TextUtils.isEmpty(text)) {
					// toast(Messages.INVALID_REG_NUMBER);
					// Log.d(TAG, "empty text");
				} else {
					text = text.replace(" ", "");
					text = text.replace("\n", "");
					if (text.length() == 7) {
						new RegistrationResultParser(getActivity())
								.parseXmlFromUrl(ServiceURLBuilder
										.getRegistrationResultUrl(text),
										parseListener);
						hideKeyboard();
					} else {
						clearSpinners();
						regEditText.setError(Messages.INVALID_REG_NUMBER);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hideKeyboard() {
		Log.i(TAG, "hiding keyboard");
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		for (EditText editText : new EditText[] { regEditText, mileageEditText,
				headlineEditText, descriptionEditText, priceEditText })
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	private void clearSpinners() {
		makesPickerSpinner.setText("");
		modelsPickerSpinner.setText("");
		engineSizePickerSpinner.setText("");
		colorPickerSpinner.setText("");
		yearTextView.setText("");

	}

	ParseListener parseListener = new ParseListener() {

		@Override
		public void onParseComplete(Context context, Object object) {
			RegistrationResultDAO registrationResultDAO = (RegistrationResultDAO) object;
			if (registrationResultDAO != null) {
				regEditText.setError(null);
				makesPickerSpinner.setText(registrationResultDAO.makeName);
				modelsPickerSpinner.setText(registrationResultDAO.modelName);
				engineSizePickerSpinner.setText(registrationResultDAO
						.getEngineSizeString());
				if (onSellFormFragmentEventListener != null)
					onSellFormFragmentEventListener.getDao().engineSize = registrationResultDAO.engineSize;
				colorPickerSpinner.setText(registrationResultDAO.colour);
				yearTextView.setText(registrationResultDAO.regDate
						.substring(registrationResultDAO.regDate.length() - 4));
			} else {
				Log.d(TAG, "registration dao is null");
				if (getActivity() != null) {
					// toast(Messages.INVALID_REG_NUMBER);
					regEditText.setError(Messages.INVALID_REG_NUMBER);
					clearSpinners();
				}

			}
		}

		@Override
		public void onError(Context context, Bundle bundle) {
			Toast.makeText(context, Messages.SERVICE_ERROR, 0).show();

		}
	};

	protected void toast(String text) {
		Toast.makeText(getActivity(), text, 0).show();
	}

	private OnClickListener onContactDetailsClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			((SellActivity) getActivity()).showContactDetailsFragment();
		}
	};

	private OnClickListener sendButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (captureData(true)) {
				if (onSellFormFragmentEventListener != null) {
					onSellFormFragmentEventListener.submitClicked();
				}

			}

		}
	};

	private OnClickListener saveButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			captureData(false);
			hideKeyboard();
			if (dao.draftName == null || TextUtils.isEmpty(dao.draftName)) {
				InputDialogFrament dialogFrament = new InputDialogFrament(
						"Input", new OnClickListener() {

							@Override
							public void onClick(View v) {
								dao.draftName = ((EditText) v).getText()
										.toString();
								new DBManager(getActivity())
										.insertOrUpdateBike(dao,
												BikeTypes.PENDING_FOR_UPLOAD);
								toast(Messages.SAVED);

							}
						});
				dialogFrament.show(getActivity().getSupportFragmentManager(),
						"dialog");
			} else {
				new DBManager(getActivity()).insertOrUpdateBike(dao,
						BikeTypes.PENDING_FOR_UPLOAD);
				toast(Messages.SAVED);
			}
		}
	};

	private class InputDialogFrament extends DialogFragment {
		String title;
		OnClickListener onClickListener;

		public InputDialogFrament(String title, OnClickListener onClickListener) {
			super();
			this.title = title;
			this.onClickListener = onClickListener;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			super.onCreateDialog(savedInstanceState);
			final Dialog dialog = new Dialog(getActivity());

			dialog.setTitle(title);
			dialog.setContentView(R.layout.fragment_input_dialog);
			final EditText editText = (EditText) dialog
					.findViewById(R.id.editText1);
			dialog.findViewById(R.id.button1).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (TextUtils
									.isEmpty(editText.getText().toString())) {
								((EditText) v).setError("Value required");
								return;
							}
							dialog.dismiss();
							onClickListener.onClick(editText);
						}
					});
			return dialog;
		}
	}

	private OnClickListener tableCellClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.bikeFeaturesLayout:
				if (onSellFormFragmentEventListener != null)
					onSellFormFragmentEventListener.showFeaturesDialog(
							checkedFeatures, onFeaturesSelectionListener);
				break;
			case R.id.makeLayout:
				// ((SellActivity)
				// getActivity()).showPickerDialog(R.string.make);
				break;
			case R.id.modelLayout:
				// ((SellActivity)
				// getActivity()).showPickerDialog(R.string.model);
				break;
			case R.id.colorLayout:
				// ((SellActivity)
				// getActivity()).showPickerDialog(R.string.color);
				break;
			case R.id.engineSizeLayout:
				// ((SellActivity) getActivity())
				// .showPickerDialog(R.string.engine_size);
				break;
			default:
				break;
			}
		}
	};

	private OnFeaturesSelectionListener onFeaturesSelectionListener = new OnFeaturesSelectionListener() {
		@Override
		public void onFeaturesSelected(ArrayList<Integer> selectedIndices) {
			checkedFeatures = selectedIndices;
			bikeFeaturesTextView.setText("(" + checkedFeatures.size() + ")");
		}
	};

	public void addPremiumPicsView(boolean show) {
		emptyPicsLayout.setVisibility(show ? View.GONE : View.VISIBLE);
		premiumPicsLayout.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void updateSinglePicLayout(String singlePicBitmapLocation) {
		if (singlePicTextView.getWidth() <= 0)
			return;
		Bitmap singlePicBitmap = ImageTools
				.getBitmapWithCorrectOrientation(singlePicBitmapLocation);
		if (singlePicBitmap == null) {
			singlePicTextView.setBackgroundResource(R.drawable.ad_img1);
			singlePicTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
					R.drawable.btn_add1picture);
		} else {
			// BitmapDrawable bitmapDrawable = new
			// BitmapDrawable(ImageTools.resizeBitmapToSize(singlePicBitmap,
			// singlePicTextView.getWidth(), singlePicTextView.getHeight()));
			int w = DisplayUtils.getDpiSpecificValue(singlePicTextView
					.getWidth() - 8);
			Bitmap resizeBitmapToFitExactlyInLandScape = ImageTools
					.resizeBitmapToFitExactlyInLandScape(singlePicBitmap, w,
							w * 2 / 3);
			Utils.recycle(singlePicBitmap);
			resizeBitmapToFitExactlyInLandScape
					.setDensity(DisplayUtils.density);
			BitmapDrawable bitmapDrawable = new BitmapDrawable(
					resizeBitmapToFitExactlyInLandScape);

			// singlePicTextView
			// .setBackgroundDrawable(new
			// BitmapDrawable(ImageTools.resizeBitmapToSize(singlePicBitmap,
			// singlePicTextView.getWidth(), singlePicTextView.getHeight())));

			// singlePicTextView.setBackgroundDrawable(bitmapDrawable);
			singlePicTextView.setCompoundDrawablesWithIntrinsicBounds(null,
					null, null, bitmapDrawable);

		}
	}

	public OnSellFormFragmentEventListener getOnSellFormFragmentEventListener() {
		return onSellFormFragmentEventListener;
	}

	public void setOnSellFormFragmentEventListener(
			OnSellFormFragmentEventListener onSellFormFragmentEventListener) {
		this.onSellFormFragmentEventListener = onSellFormFragmentEventListener;
	}

	interface OnSellFormFragmentEventListener {
		ArrayList<Photo> getPremiumPicsArrayList();

		void showFeaturesDialog(ArrayList<Integer> indicesOfFeatures,
				OnFeaturesSelectionListener onFeaturesSelectionListener);

		void reset();

		BikeDAO getDao();

		ArrayList<Photo> getPhotosForUplaod();

		void submitClicked();
	}

	public void validate() {

		if (regEditText.getText().toString().length() == 0)
			regEditText.setError("First name is required!");
	}

	@Override
	public void onStart() {
		super.onStart();
		((BaseActivity) getActivity()).setupNavigationButton(R.string.reset,
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						hideKeyboard();
						if (onSellFormFragmentEventListener != null)
							onSellFormFragmentEventListener.reset();
					}
				}, true);
//		change in requirement
//		((BaseActivity) getActivity()).setupNavigationButton(R.string.drafts,
//				new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						hideKeyboard();
//						((SellActivity) getActivity()).showDraftsFragment();
//					}
//				}, false);

	}

	@Override
	public void onStop() {
		super.onStop();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
		// we dont capture data when resetting. Reset method of activity will
		// reset dao
	}

	@Override
	public void onDetach() {
		super.onDetach();
		photosGrid.releaseBitmaps();
	}

	public boolean captureData(boolean showErrorsOnFields) {
		Log.d(TAG, "capture data " + showErrorsOnFields);

		// TODO capture fields too
		dao = onSellFormFragmentEventListener == null ? new BikeDAO()
				: onSellFormFragmentEventListener.getDao();
		boolean valid = true;
		EditText[] editTexts = new EditText[] { regEditText, mileageEditText,
				headlineEditText, priceEditText };
		for (EditText editText : editTexts) {
			if (TextUtils.isEmpty(editText.getText().toString().trim())) {
				valid = false;
				editText.setText("");
				if (showErrorsOnFields)
					editText.setError(Messages.VALUE_REQUIRED);
			}
		}
		valid = regEditText.getError() == null;
		for (EditText editText : new EditText[] { priceEditText,
				mileageEditText }) {
			try {
				String text = editText.getText().toString();
				if (!TextUtils.isEmpty(text)) {
					int number = Integer.parseInt(text);
					if (number < 1) {
						throw new Exception();
					}
				}
			} catch (Exception e) {
				valid = false;
				editText.setError(Messages.INVALID_DATA);
			}
		}

		// TODO validating contact details and location correct?
		for (String text : new String[] { dao.firstName, dao.lastName,
				dao.email, dao.telephone }) {
			if (text == null || TextUtils.isEmpty(text.trim())) {
				if (showErrorsOnFields)
					contactDetailsTextView
							.setError(Messages.CONTACT_DETAILS_REQUIRED);
				valid = false;
				break;
			}
		}
		if (dao.lat == 0 || dao.lon == 0) {
			if (showErrorsOnFields)
				locationTextView.setError(Messages.LOCATION_REQUIRED);
			valid = false;
		}

		dao.regNo = regEditText.getText().toString();

		for (MakeDAO make : dbManager.getMakes())
			if (make.name.equalsIgnoreCase(makesPickerSpinner.getText()
					.toString().trim())) {
				Log.d(TAG, "found ya " + make.id + "-" + make.name);
				dao.makeID = make.id;
			}

		for (ModelDAO model : dbManager.getModels())
			if (model.name.equalsIgnoreCase(modelsPickerSpinner.getText()
					.toString().trim())) {
				Log.d(TAG, "found ya " + model.id + "-" + model.name);
				dao.modelID = model.id;
			}

		for (EngineDAO engine : dbManager.getEngines())
			if (engine.name.equalsIgnoreCase(engineSizePickerSpinner.getText()
					.toString().trim())) {
				Log.d(TAG, "found ya " + engine.id + "-" + engine.name);
				dao.engineID = engine.id;
			}

		for (ColourDAO color : dbManager.getColours())
			if (color.name.equalsIgnoreCase(colorPickerSpinner.getText()
					.toString().trim())) {
				Log.d(TAG, "found ya " + color.id + "-" + color.name);
				dao.colorId = color.id;
			}

		dao.engineSize = engineSizePickerSpinner.getCurrentIndex();
		dao.colorId = colorPickerSpinner.getCurrentIndex();
		dao.sellerId = sellerTypePickerSpinner.getCurrentIndex();
		dao.year = parseInt(yearTextView.getText().toString());
		dao.mileage = parseInt(mileageEditText.getText().toString());
		dao.featuresArrayList = checkedFeatures;
		dao.bikeName = headlineEditText.getText().toString();
		dao.description = descriptionEditText.getText().toString();
		dao.price = parseInt(priceEditText.getText().toString());

		dao.photos = onSellFormFragmentEventListener.getPhotosForUplaod();
		String singlePicLocation = dao.photos.get(0).url;
		if (singlePicLocation == null || TextUtils.isEmpty(singlePicLocation)) {
			valid = false;
			if (showErrorsOnFields)
				singlePicTextView.setError("Image required");
		}
		return valid;
	}

	public int parseInt(String s) {
		if (s != null && !TextUtils.isEmpty(s)) {
			try {
				return Integer.parseInt(s);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	private interface Messages {
		String SERVICE_ERROR = "Internal error";
		// String SUBMITTED = "Submitting Bike...";
		String SAVED = "Saved";
		String INVALID_REG_NUMBER = "Invalid registration number";
		// String FETCHING = "fetching...";
		String VALUE_REQUIRED = "Value is required";
		String CONTACT_DETAILS_REQUIRED = "Contact details required";
		String LOCATION_REQUIRED = "Location is required";
		String INVALID_DATA = "Invalid data";
	}

}
