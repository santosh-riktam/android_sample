package com.riktamtech.android.sellabike.ui;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.riktamtech.android.sellabike.R;
import com.riktamtech.android.sellabike.data.PrefsManager;

public class SettingsActivity extends BaseActivity {

	PrefsManager prefsManager;
	private static final int REQ_TWITTER = 0;
	SettingsFragment settingsFragment;
	Handler handler;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_settings);
		handler = new Handler();
		prefsManager = new PrefsManager(this);
		facebook = new Facebook(getResources().getString(R.string.FBAppId));
		twitter = new TwitterFactory().getInstance();
		settingsFragment = new SettingsFragment();
		attachFragment(settingsFragment, false, BackStackNames.root);

	}

	private boolean isLoggedIntoFb() {
		return prefsManager.getFbAccessToken() != null
				&& prefsManager.getFbExpiresIn() != null;
	}

	private boolean isLoggedIntoTw() {
		return prefsManager.getTwToken() != null
				&& prefsManager.getTwTokenSecret() != null;
	}

	public void showMyAdsFragment() {
		attachFragment(new MyAdsFragment());
	}

	public void onTblRowClick(View v) {
		switch (v.getId()) {
		case R.id.facebookTextView:
			if (!isLoggedIntoFb())
				setUpFacebook();
			else {
				toast("Signing out...");
				AsyncFacebookRunner asyncFacebookRunner = new AsyncFacebookRunner(
						facebook);
				asyncFacebookRunner.logout(this, fbLogoutRequestListener);
			}
			break;
		case R.id.twitterTextView:
			if (!isLoggedIntoTw())
				setUpTwitter();
			else {
				prefsManager.setTwitterDetails(null, null);
				settingsFragment.updateTwTextviewText(false);
			}
			break;
		default:
			break;
		}
	}

	// TODO check FACEBOOK STUFF

	private RequestListener fbLogoutRequestListener = new RequestListener() {

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
		}

		@Override
		public void onIOException(IOException e, Object state) {
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
		}

		@Override
		public void onComplete(String response, Object state) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					prefsManager.setFbDetails(null, null);
					settingsFragment.updateFbTextViewText(false);
				}
			});
		}
	};

	protected void setUpFacebook() {

		String[] facebookDetails = new String[] {
				prefsManager.getFbAccessToken(), prefsManager.getFbExpiresIn() };

		if (facebookDetails[0] != null) {
			facebook.setAccessToken(facebookDetails[0]);
			facebook.setAccessExpiresIn(facebookDetails[1]);
		}
		if (!facebook.isSessionValid()) {
			facebook.authorize(this, new String[] { "publish_stream",
					"read_stream", "offline_access", "user_videos" },
					new DialogListener() {

						@Override
						public void onFacebookError(FacebookError e) {
							facebookSetupFailure(e);
						}

						@Override
						public void onError(DialogError e) {
							facebookSetupFailure(e);
						}

						@Override
						public void onComplete(Bundle values) {
							facebookSetupSuccess(values);
						}

						@Override
						public void onCancel() {
							facebookSetupFailure(null);
						}

					});
		}
	}

	protected void facebookSetupSuccess(Bundle values) {

		String ACCESS_TOKEN = values.getString("access_token");
		String EXP_TIME = values.getString("expires_in");
		prefsManager.setFbDetails(ACCESS_TOKEN, EXP_TIME);
		settingsFragment.updateFbTextViewText(true);
	}

	protected void facebookSetupFailure(Object error) {

	}

	protected Bundle postToFacebook(String status) {
		try {
			Bundle param = new Bundle();
			param.putString("message", status);
			facebook.request("me/feed", param, "POST");
			Bundle bundle = new Bundle();
			// bundle.putBoolean(AppConstants.SUCCESS, true);
			// bundle.putBoolean(AppConstants.FB_POST_STATUS, true);
			return bundle;
		} catch (Exception e) {
			return null;
			// getExceptionWrappedInBundle(e,
			// AppConstants.FB_POST_STATUS,
			// AppConstants.FB_POST_MSG);
		}
	}

	protected Bundle postToImageToFacebook(String title, String description,
			String link, byte[] imageBytes) {
		try {
			Bundle param = new Bundle();
			param.putByteArray("picture", imageBytes);
			param.putString("caption", title);
			param.putString("link", link);
			param.putString("message", description);
			facebook.request("me/photos", param, "POST");

			Bundle bundle = new Bundle();
			// bundle.putBoolean(AppConstants.SUCCESS, true);
			// bundle.putBoolean(AppConstants.FB_POST_STATUS, true);
			return bundle;
		} catch (Exception e) {
			return null;// getExceptionWrappedInBundle(e,
						// AppConstants.FB_POST_STATUS,
						// AppConstants.FB_POST_MSG);
		}
	}

	protected Bundle postToVideoFacebook(String title, String description,
			String thumbnail, String link) {
		try {
			Bundle param = new Bundle();
			// param.putString("source", link);
			param.putString("name", title);
			param.putString("description", description);
			param.putString("link", link);
			String str = facebook.request("me/feed", param, "POST");
			JSONObject jsonObject = new JSONObject(str);
			JSONObject errorObejct = jsonObject.getJSONObject("error");
			if (errorObejct != null)
				throw new Exception(errorObejct.getString("message"));

			Bundle bundle = new Bundle();
			// bundle.putBoolean(AppConstants.SUCCESS, true);
			// bundle.putBoolean(AppConstants.FB_POST_STATUS, true);
			return bundle;
		} catch (Exception e) {
			return null;// getExceptionWrappedInBundle(e,
						// AppConstants.FB_POST_STATUS,
						// AppConstants.FB_POST_MSG);
		}
	}

	// TODO check TWITTER STUFF

	protected void setUpTwitter() {
		try {
			String[] twitterDetails = new String[] { prefsManager.getTwToken(),
					prefsManager.getTwTokenSecret() };
			if (twitterDetails[0] == null) {
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(
						getResources().getString(R.string.twitter_consumer_key),
						getResources().getString(
								R.string.twitter_consumer_secret));
				oAuthRequestToken = twitter.getOAuthRequestToken(getResources()
						.getString(R.string.twitter_callback));
				Intent i = new Intent(getApplicationContext(),
						TwitterWebviewActivity.class);
				i.putExtra("URL", oAuthRequestToken.getAuthenticationURL()
						+ "&force_login=true");
				startActivityForResult(i, REQ_TWITTER);
			} else {
				AccessToken accessToken = new AccessToken(twitterDetails[0],
						twitterDetails[1]);
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(
						getResources().getString(R.string.twitter_consumer_key),
						getResources().getString(
								R.string.twitter_consumer_secret));
				twitter.setOAuthAccessToken(accessToken);
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

	protected void twitterSetupSuccess(AccessToken token) {
		prefsManager
				.setTwitterDetails(token.getToken(), token.getTokenSecret());
		settingsFragment.updateTwTextviewText(true);
	}

	protected void twitterSetUpFailure(Object object) {

	}

	protected Bundle tweet(String status) {
		try {
			twitter = new TwitterFactory(getTwitterConf()).getInstance();
			twitter.updateStatus(status);
			Bundle bundle = new Bundle();
			// bundle.putBoolean(AppConstants.SUCCESS, true);
			// bundle.putBoolean(AppConstants.TW_POST_STATUS, true);
			return bundle;
		} catch (Exception e) {
			return null;// getExceptionWrappedInBundle(e,
						// AppConstants.TW_POST_STATUS,
						// AppConstants.FB_POST_MSG);
		}
	}

	protected Bundle tweet(String title, String desciption,
			ByteArrayInputStream inputStream) {
		try {

			twitter = new TwitterFactory(getTwitterConf()).getInstance();
			ImageUpload imageUpload = new ImageUploadFactory(getTwitterConf())
					.getInstance(MediaProvider.TWITPIC);
			String str = imageUpload.upload(title, inputStream, title);
			String desc = desciption;
			if ((desc.length() + str.length()) >= 140)
				desc = desc.substring(0, (138 - str.length()));
			twitter.updateStatus(desc + "  " + str);
			Bundle bundle = new Bundle();
			// bundle.putBoolean(AppConstants.SUCCESS, true);
			// bundle.putBoolean(AppConstants.TW_POST_STATUS, true);
			return bundle;
		} catch (Exception e) {
			e.printStackTrace();
			return null;// getExceptionWrappedInBundle(e,
						// AppConstants.TW_POST_STATUS,
						// AppConstants.TW_POST_MSG);
		}
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_TWITTER) {
			if (resultCode == RESULT_OK
					&& data.getStringExtra("oauth_verifier") != null) {
				String verifier = data.getStringExtra("oauth_verifier");
				try {
					AccessToken token = twitter.getOAuthAccessToken(
							oAuthRequestToken, verifier);
					if (token != null && token.getToken() != null
							&& token.getTokenSecret() != null)
						twitterSetupSuccess(token);
					else
						twitterSetUpFailure(null);

				} catch (TwitterException e) {
					e.printStackTrace();
					Toast.makeText(this, "" + e.getMessage(), 0).show();
					twitterSetUpFailure(e);
				}

			} else {
				twitterSetUpFailure(null);
			}
		}
	}

}
