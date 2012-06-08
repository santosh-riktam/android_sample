package com.riktamtech.android.sellabike.ui;

import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.data.DBManager;

public class ContactGumtreeFragment extends Fragment {
	DBManager dbManager;
	BikeDAO bikeDAO;
	EditText nameEditText, emailEditText, messageEditText;
	Button clearFormButton, submitButton;
	String email, name, message;
	boolean isDataValid;
	private final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	ContactGumtreeFragment(BikeDAO bikeDAO) {
		this.bikeDAO = bikeDAO;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_contact_gumtree,
				container, false);
		dbManager = new DBManager(getActivity());
		nameEditText = (EditText) inflate.findViewById(R.id.your_name_editText);
		emailEditText = (EditText) inflate
				.findViewById(R.id.your_email_editText);
		messageEditText = (EditText) inflate
				.findViewById(R.id.message_editText);
		clearFormButton = (Button) inflate.findViewById(R.id.clear_form_button);
		submitButton = (Button) inflate.findViewById(R.id.submit_button);
		clearFormButton.setOnClickListener(clearFormButtonClickListener);
		submitButton.setOnClickListener(submitButtonClickListener);
		emailEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
		return inflate;
	}

	public boolean onSubmitButtonClicked() {

		isDataValid = true;
		name = nameEditText.getText().toString();
		email = emailEditText.getText().toString();
		message = messageEditText.getText().toString();
		if (!TextUtils.isEmpty(email) && !checkEmail(email)) {
			isDataValid = false;
			emailEditText.setError(Messages.INVALID_EMAIL);
		}
		for (EditText editText : new EditText[] { nameEditText,
				messageEditText, emailEditText }) {
			if (TextUtils.isEmpty(editText.getText().toString())) {
				editText.setError(Messages.VALUE_REQUIRED);
				isDataValid = false;
			}
		}
		return isDataValid;
	}

	public void sendEmail() {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { email + "" });
		emailIntent.putExtra(
				android.content.Intent.EXTRA_SUBJECT,
				"" + dbManager.getMake(bikeDAO.makeID).name + ", "
						+ dbManager.getModel(bikeDAO.modelID).name + ", "
						+ bikeDAO.engineSize + " cc");

		String emailBody = "Hello,"
				+ "\n I'm interested in the motor bike i've seen advertised"
				+ " on Sell A Bike and wanted to know if it still for sale \n my name is"
				+ name + " \n" + message;

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + emailBody);

		getActivity().startActivity(
				Intent.createChooser(emailIntent, "Send mail..."));
	}

	OnClickListener clearFormButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			emailEditText.setText("");
			messageEditText.setText("");
			nameEditText.setText("");
		}
	};
	OnClickListener submitButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (onSubmitButtonClicked()) {
				sendEmail();
				goBack();
			}

		}
	};

	private interface Messages {
		String INVALID_EMAIL = "Invalid email";
		String VALUE_REQUIRED = "Value required";
	}

	public void goBack() {
		((FragmentActivity) getActivity()).getSupportFragmentManager()
				.popBackStack();
	}
}
