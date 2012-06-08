package com.riktamtech.android.sellabike.ui;

import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.ui.SellFormFragment.OnSellFormFragmentEventListener;
import com.riktamtech.android.sellabike.util.TextTools;

public class ContactDetailsFragment extends Fragment {

	EditText firstNameEditText, lastNameEditText, emailEditText, contactNumberEditText;
	public String firstName, lastName, email, contactNumber;
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
			+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
	private OnSellFormFragmentEventListener onSellFormFragmentEventListener;

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_contact_details, container, false);
		firstNameEditText = (EditText) inflate.findViewById(R.id.firstName);
		lastNameEditText = (EditText) inflate.findViewById(R.id.lastName);
		emailEditText = (EditText) inflate.findViewById(R.id.email);
		// TODO Email Validation..
		contactNumberEditText = (EditText) inflate.findViewById(R.id.contactNumber);
		if (onSellFormFragmentEventListener != null) {
			BikeDAO dao = onSellFormFragmentEventListener.getDao();
			firstNameEditText.setText(dao.firstName);
			lastNameEditText.setText(dao.lastName);
			emailEditText.setText(dao.email);
			contactNumberEditText.setText(dao.telephone);
		}

		firstNameEditText.setFilters(TextTools.firstNameFilters);
		lastNameEditText.setFilters(TextTools.firstNameFilters);
		contactNumberEditText.setFilters(new InputFilter[] { TextTools.integerFilter, new InputFilter.LengthFilter(15) });
		// email validation
		emailEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });

		return inflate;
	}

	public OnSellFormFragmentEventListener getOnSellFormFragmentEventListener() {
		return onSellFormFragmentEventListener;
	}

	public void setOnSellFormFragmentEventListener(OnSellFormFragmentEventListener onSellFormFragmentEventListener) {
		this.onSellFormFragmentEventListener = onSellFormFragmentEventListener;
	}

	public boolean onSaveButtonClicked() {
		firstName = firstNameEditText.getText().toString();
		lastName = lastNameEditText.getText().toString();
		email = emailEditText.getText().toString();
		contactNumber = contactNumberEditText.getText().toString();

		boolean isDataValid = true;
		for (EditText editText : new EditText[] { firstNameEditText, lastNameEditText, contactNumberEditText, emailEditText }) {
			if (TextUtils.isEmpty(editText.getText().toString())) {
				editText.setError(Messages.VALUE_REQUIRED);
				isDataValid = false;
			}
		}

		if (!TextUtils.isEmpty(email) && !checkEmail(email)) {
			isDataValid = false;
			emailEditText.setError(Messages.INVALID_EMAIL);

		}
		if (onSellFormFragmentEventListener != null && isDataValid) {
			BikeDAO dao = onSellFormFragmentEventListener.getDao();
			dao.firstName = firstName;
			dao.lastName = lastName;
			dao.email = email;
			dao.telephone = contactNumber;
		}
		return isDataValid;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		((BaseActivity) getActivity()).setupNavigationButton(R.string.sell, new OnClickListener() {

			@Override
			public void onClick(View v) {
				goBack();
			}
		}, true);
		((BaseActivity) getActivity()).setupNavigationButton(R.string.save, new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onSaveButtonClicked())
					goBack();
			}
		}, false);
	}

	public void goBack() {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
		for (EditText editText : new EditText[] { firstNameEditText, lastNameEditText, emailEditText, contactNumberEditText })
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		((FragmentActivity) getActivity()).getSupportFragmentManager().popBackStack();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		((BaseActivity) getActivity()).removeNavigationBarButtons();
	}

	private interface Messages {
		String INVALID_EMAIL = "Invalid email";
		String VALUE_REQUIRED = "Value required";
	}
}
