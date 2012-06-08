package com.riktamtech.android.sellabike.util;

import android.text.InputFilter;
import android.text.Spanned;

public class TextTools {
	public static InputFilter maxLengthFilter = new InputFilter.LengthFilter(50);
	public static InputFilter usernameFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			String invalidChars = "<>";
			if (source.length() > 0 && (Character.isWhitespace(source.charAt(start)) || invalidChars.indexOf(source.charAt(start)) != -1))
				return "";
			return null;
		}
	};
	public static InputFilter firstNameFilter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.length() > 0 && !Character.isLetter(source.charAt(start)))
				return "";
			return null;
		}
	};

	public static InputFilter noWhiteSpacesFilter = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.length() > 0 && Character.isWhitespace(source.charAt(start)))
				return "";

			return null;
		}
	};

	public static InputFilter integerFilter = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (source.length() > 0 && Character.isDigit(source.charAt(start)))
				return null;
			return "";
		}
	};

	public static InputFilter capitalizeFirstLetterFilter = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			if (dest.length() == 0 && source.subSequence(start, end).length() > 0) {
				return Character.toUpperCase(source.charAt(start)) + "";
			}
			return null;
		}
	};

	public static InputFilter[] usernameFilters = new InputFilter[] { usernameFilter, maxLengthFilter};
	public static InputFilter[] firstNameFilters = new InputFilter[] { firstNameFilter, maxLengthFilter, capitalizeFirstLetterFilter  };

}
