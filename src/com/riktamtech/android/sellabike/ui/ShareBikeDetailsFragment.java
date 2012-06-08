package com.riktamtech.android.sellabike.ui;

import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.Facebook;
import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.data.PrefsManager;
import com.riktamtech.android.sellabike.util.BitlyAndroid;
import com.riktamtech.android.sellabike.util.DisplayUtils;
import com.riktamtech.android.sellabike.util.Utils;

public class ShareBikeDetailsFragment extends Fragment {
	BikeDAO bikeDAO;
	EditText editText;
	TextView bikeName_Price_TextView, others_TextView;
	ImageView imageView;
	RadioGroup radioGroup;
	RadioButton rb0, rb1, rb2;
	Button publish_share;
	Facebook facebook;
	Twitter twitter;
	PrefsManager prefsManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	ShareBikeDetailsFragment(BikeDAO bikeDAO) {
		this.bikeDAO = bikeDAO;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View inflate = inflater.inflate(R.layout.fragment_search_share,
				container, false);
		prefsManager = new PrefsManager(getActivity());
		facebook = new Facebook(getResources().getString(R.string.FBAppId));
		facebook.setAccessToken(prefsManager.getFbAccessToken());
		facebook.setAccessExpiresIn(prefsManager.getFbExpiresIn());

		twitter = new TwitterFactory().getInstance();
		radioGroup = (RadioGroup) inflate.findViewById(R.id.radioGroup1);
		rb0 = (RadioButton) inflate.findViewById(R.id.radio0);
		rb1 = (RadioButton) inflate.findViewById(R.id.radio1);
		rb2 = (RadioButton) inflate.findViewById(R.id.radio2);
		editText = (EditText) inflate.findViewById(R.id.editText_share);
		bikeName_Price_TextView = (TextView) inflate
				.findViewById(R.id.bikename_price_share);
		others_TextView = (TextView) inflate.findViewById(R.id.others_share);
		imageView = (ImageView) inflate.findViewById(R.id.imageView_share);
		publish_share = (Button) inflate.findViewById(R.id.publish_share);

		bikeName_Price_TextView.setText(bikeDAO.bikeName + " - "
				+ Utils.formatCurrency(bikeDAO.price));
		others_TextView.setText(bikeDAO.year + ", " + bikeDAO.engineSize + ", "
				+ bikeDAO.getDistance() + " miles, " + bikeDAO.features);
		if (bikeDAO.thumbs.size() > 0) {
			String thumb = bikeDAO.thumbs.get(0);
			((BaseActivity) getActivity()).setImage(imageView, thumb,
					DisplayUtils.dispayWidth / 4, DisplayUtils.dispayWidth / 6);
		}
		publish_share.setOnClickListener(publishClickListener);

		return inflate;
	}

	OnClickListener publishClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (rb0.isChecked()) {
				if (isLoggedIntoFb()) {
					if (postToImageToFacebook(bikeDAO.bikeName, editText
							.getText().toString(), bikeDAO.url,
							bikeDAO.photos.get(0).url))
						Toast.makeText(getActivity(), "Posted successfully",
								Toast.LENGTH_SHORT).show();
					else {
						Toast.makeText(getActivity(), "Internal error",
								Toast.LENGTH_SHORT).show();

					}

				} else
					Toast.makeText(getActivity(),
							"Sign in with Facebook in the settings screen",
							Toast.LENGTH_SHORT).show();
			} else if (rb1.isChecked()) {
				if (isLoggedIntoTw()) {
					boolean success = false;
					// TODO confirm this
					// tweet(editText.getText().toString() + "\n" +
					// bikeDAO.bikeName + " " + bikeDAO.url);
					BitlyAndroid bitlyAndroid = new BitlyAndroid(
							"santoshriktam",
							"R_bf672361276890b2ec6eb0bc8fcb12fb");
					try {
						tweet("Should I buy this bike? "
								+ bitlyAndroid.getShortUrl(bikeDAO
										.getBikePicForPosting())
								+ " You can buy & sell motorbikes for free on @sellabike");
						success = true;
					} catch (Exception e) {
						success = false;
						e.printStackTrace();
					}
					if (success)
						Toast.makeText(getActivity(), "Posted successfully",
								Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(getActivity(), "Internal error",
								Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getActivity(),
							"Sign in with Twitter in the settings screen",
							Toast.LENGTH_SHORT).show();
			} else if (rb2.isChecked()) {
				sendEmail();
			}
		}
	};

	private boolean isLoggedIntoFb() {
		return prefsManager.getFbAccessToken() != null
				&& prefsManager.getFbExpiresIn() != null;
	}

	private boolean isLoggedIntoTw() {
		return prefsManager.getTwToken() != null
				&& prefsManager.getTwTokenSecret() != null;
	}

	private boolean postToImageToFacebook(final String title,
			final String description, final String link, final String imageUrl) {
		try {
			Bundle param = new Bundle();
			param.putString("link", link);
			param.putString("name", title);
			// param.putString("description", description);
			// TODO confirm this
			param.putString(
					"description",
					"\'Like\' this image if you think I should buy this bike. Buy & sell motorbikes for free with Sell A Bike apps.");
			param.putString("picture", imageUrl);
			param.putString("icon", imageUrl);
			String response = facebook.request("me/feed", param, "POST");
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has("id"))
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	protected boolean tweet(String status) {
		try {
			if (status.length() > 140)
				throw new Exception("text longer thatn 140 chars");
			twitter = new TwitterFactory(getTwitterConf()).getInstance();
			twitter.updateStatus(status);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private twitter4j.conf.Configuration getTwitterConf() {
		String[] twitterDetails = new String[] { prefsManager.getTwToken(),
				prefsManager.getTwTokenSecret() };
		ConfigurationBuilder configurationBuilder = null;
		if (twitterDetails[0] != null)
			configurationBuilder = new ConfigurationBuilder()
					.setOAuthConsumerKey(
							getResources().getString(
									R.string.twitter_consumer_key))
					.setOAuthConsumerSecret(
							getResources().getString(
									R.string.twitter_consumer_secret))
					.setOAuthAccessToken(twitterDetails[0])
					.setOAuthAccessTokenSecret(twitterDetails[1])
					.setMediaProviderAPIKey(
							getResources().getString(R.string.twitpic_api_key));
		if (configurationBuilder != null)
			return configurationBuilder.build();
		return null;
	}

	public void sendEmail() {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SENDTO);
		emailIntent.setType("text/html");
		// emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
		// new String[] { "feedback@sellabike.me" });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Feedback about Sell A Bike");

		String emailBody = "Hello,"
				+ "\n \n"
				+ "I just tried an app and I found it really interesting I think you should try it some time."
				+ "\n \n"
				+ "\n and also challenge your friends.<a href=\"http://www.sellabike.me\">website</a> Here comes the best part of it… its FREE!! :)"
				+ "\n\n" + "Enjoy!! Have a Great Day!!";

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + emailBody);

		getActivity().startActivity(
				Intent.createChooser(emailIntent, "Send mail..."));

	}
 
}
