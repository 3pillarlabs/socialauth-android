/*
 ===========================================================================
 Copyright (c) 2012 Three Pillar Global Inc. http://threepillarglobal.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sub-license, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===========================================================================
 */

package org.brickred.socialauth.android;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.brickred.socialauth.Album;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Career;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Feed;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.exception.SocialAuthException;
import org.brickred.socialauth.plugin.AlbumsPlugin;
import org.brickred.socialauth.plugin.CareerPlugin;
import org.brickred.socialauth.plugin.FeedPlugin;
import org.brickred.socialauth.util.AccessGrant;
import org.brickred.socialauth.util.Constants;
import org.brickred.socialauth.util.MethodType;
import org.brickred.socialauth.util.OAuthConfig;
import org.brickred.socialauth.util.Response;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * Main class of the SocialAuth Android SDK. Wraps a user interface component
 * with the SocialAuth functionality of updating status, getting user profiles,
 * contacts, upload images, get user feeds, get user albums on Facebook,
 * Twitter, LinkedIn, MySpace, Yahoo, Google FourSquare, Runkeeper, SalesForce
 * and Yammer. <br>
 * 
 * Currently it can be used in three different ways. First, it can be attached
 * with a Button that user may click. Clicking will open a menu with various
 * social networks listed that the user can click on. Clicking on any network
 * opens a dialog for authentication with that social network. Once the user is
 * authenticated, you can use various methods from the AuthProvider interface to
 * update status, get profile, contacts, user feeds, album feeds and upload
 * images. <br>
 * 
 * Secondly, it can be attached to a LinearLayout for creating a Bar with
 * several buttons, one for each social network. Clicking on these icons will
 * open a dialog which will authenticate the user and one the user is
 * authenticated, you can use various methods from the AuthProvider interface to
 * update status, get profile, contacts, user feeds, album feeds and upload
 * images. <br>
 * 
 * Lastly, you can just launch the authentication dialog directly from any event
 * you prefer. Examples for all of these ways is provided in the examples
 * directory of the SocialAuth Android SDK
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * @author abhinav.maheswari@3pillarglobal.com
 * 
 */

public class SocialAuthAdapter {

	/**
	 * Enum of all supported providers containing provider name , callback url
	 * and cancel url
	 * 
	 */
	public enum Provider {
		FACEBOOK(Constants.FACEBOOK, "fbconnect://success", "fbconnect://success?error_reason"), TWITTER(
				Constants.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do",
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do?denied"), LINKEDIN(Constants.LINKEDIN,
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do",
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do?oauth_problem"), MYSPACE(
				Constants.MYSPACE, "http://socialauth.in", "http://socialauth.in/?oauth_problem"), RUNKEEPER(
				Constants.RUNKEEPER, "http://socialauth.in/socialauthdemo/socialauthSuccessAction.do",
				"http://socialauth.in/socialauthdemo/socialauthSuccessAction.do/?error"), YAHOO(Constants.YAHOO,
				"http://socialauth.in/socialauthdemo", "http://socialauth.in/socialauthdemo/?oauth_problem"), FOURSQUARE(
				Constants.FOURSQUARE, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do",
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do/?oauth_problem"), GOOGLE(
				Constants.GOOGLE, "http://socialauth.in/socialauthdemo",
				"http://socialauth.in/socialauthdemo/?oauth_problem"), YAMMER(Constants.YAMMER,
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do",
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do/?oauth_problem"), SALESFORCE(
				Constants.SALESFORCE, "https://socialauth.in:8443/socialauthdemo/socialAuthSuccessAction.do",
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do/?oauth_problem"), GOOGLEPLUS(
				Constants.GOOGLE_PLUS, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do",
				"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do/?oauth_problem"), EMAIL(SHARE_MAIL, "",
				""), MMS(SHARE_MMS, "", "");

		private String name;
		private String cancelUri;
		private String callbackUri;

		/**
		 * Constructor with unique string representing the provider
		 * 
		 * @param name
		 */
		Provider(String name, String callbackUri, String cancelUri) {
			this.name = name;
			this.cancelUri = cancelUri;
			this.callbackUri = callbackUri;
		}

		/**
		 * returns cancel URI
		 */
		String getCancelUri() {
			return this.cancelUri;
		}

		/**
		 * returns Callback URI
		 */
		String getCallBackUri() {
			return this.callbackUri;
		}

		/**
		 * Set callback URI
		 */
		public void setCallBackUri(String callbackUri) {
			this.callbackUri = callbackUri;
		}

		/**
		 * Returns the unique string representing the provider
		 */
		@Override
		public String toString() {
			return name;
		}
	}

	// Share Mail & MMS providers
	public static final String SHARE_MAIL = "share_mail";
	public static final String SHARE_MMS = "share_mms";

	// Constants
	public static final String PROVIDER = "provider";
	public static final String ACCESS_GRANT = "access_grant";

	// Facebook feed url for updating story
	private final String UPDATE_STATUS_URL = "https://graph.facebook.com/me/feed";

	// socialAuthManager object
	private SocialAuthManager socialAuthManager;

	// dialogListener object
	private DialogListener dialogListener;

	// provides currentprovider information
	private Provider currentProvider;

	// contains array of providers
	private final Provider authProviders[];

	// Variables, Arrays and Maps
	private String url;
	private int providerCount = 0;
	private final int authProviderLogos[];
	private Map<String, Object> tokenMap;
	private final Map<String, OAuthConfig> authMap;

	// Android Components
	private Context context;
	private final Handler handler = new Handler();

	/**
	 * Constructor
	 * 
	 * @param listener
	 *            Listener for the adapter events
	 */

	public SocialAuthAdapter(DialogListener listener) {
		authProviders = new Provider[Provider.values().length];
		authProviderLogos = new int[Provider.values().length];
		this.dialogListener = listener;
		authMap = new HashMap<String, OAuthConfig>();
	}

	/**
	 * Attaches a new listener to the adapter. Define logos and providers.
	 * 
	 * @param listener
	 */
	public void setListener(DialogListener listener) {
		this.dialogListener = listener;
	}

	/**
	 * Enables a provider
	 * 
	 * @param provider
	 *            Provider to be enables
	 * @param logo
	 *            Image resource for the logo of the provider
	 */
	public void addProvider(Provider provider, int logo) {
		authProviders[providerCount] = provider;
		authProviderLogos[providerCount] = logo;
		providerCount++;
	}

	/**
	 * Adds callback URL
	 * 
	 * @param provider
	 *            Provider to be enables
	 * @param calBack
	 *            CallBack URL String
	 */
	public void addCallBack(Provider provider, String callBack) {
		if (provider.name() == Constants.FACEBOOK || provider.name() == Constants.LINKEDIN
				|| provider.name() == Constants.MYSPACE || provider.name() == Constants.YAHOO
				|| provider.name() == Constants.RUNKEEPER) {
			Log.d("SocialAuthAdapter", "Callback Url not require");
		} else
			provider.setCallBackUri(callBack);
	}

	/**
	 * Returns the last authenticated provider. Please use the SocialAuth API to
	 * find out about the methods available in this interface
	 * 
	 * @return Provider object
	 */
	public AuthProvider getCurrentProvider() {
		if (currentProvider != null) {
			return socialAuthManager.getProvider(currentProvider.toString());

		} else {
			return null;
		}
	}

	/**
	 * Enables a button with the SocialAuth menu
	 * 
	 * @param sharebtn
	 *            The button that will be clicked by user to start sharing
	 */
	public void enable(Button sharebtn) {

		Log.d("SocialAuthAdapter", "Enabling button with SocialAuth");
		final Context ctx = sharebtn.getContext();
		context = ctx;

		// Click Listener For Share Button
		sharebtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// This dialog will show list of all providers
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				builder.setTitle("Share via");
				builder.setCancelable(true);
				builder.setIcon(android.R.drawable.ic_menu_more);

				// Handles Click Events
				String[] providerNames = new String[providerCount];
				int[] providerLogos = new int[providerCount];

				for (int i = 0; i < providerCount; i++) {
					providerNames[i] = authProviders[i].toString();
					providerLogos[i] = authProviderLogos[i];
				}

				builder.setSingleChoiceItems(new ShareButtonAdapter(ctx, providerNames, providerLogos), 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int item) {
								if (authProviders[item].toString().startsWith("share_mail")
										|| authProviders[item].toString().startsWith("share_mms")) {
									// Getting selected provider email or mms
									Bundle bundle = new Bundle();
									bundle.putString(SocialAuthAdapter.PROVIDER, authProviders[item].toString());
									dialogListener.onComplete(bundle);
								} else {
									// Getting selected provider and starting
									// authentication
									authorize(ctx, authProviders[item]);
								}
								dialog.dismiss();
							}
						});
				final AlertDialog dialog = builder.create();
				dialog.show();
			}
		});

		if (!Util.isNetworkAvailable(ctx)) {
			dialogListener.onError(new SocialAuthError("Please check your Internet connection", new Exception("")));
			return;
		}
	}

	/**
	 * Enables a LinearLayout with SocialAuth functionality
	 * 
	 * @param linearbar
	 *            The LinearLayout which is created as a bar
	 */
	public void enable(LinearLayout linearbar) {
		Log.d("SocialAuthAdapter", "Enabling bar with SocialAuth");
		final Context ctx = linearbar.getContext();

		context = ctx;
		// Handles Clicking Events for Buttons
		View.OnClickListener viewlistener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Getting selected provider and starting authentication
				if (authProviders[v.getId()].toString().startsWith("share_mail")
						|| authProviders[v.getId()].toString().startsWith("share_mms")) {
					Bundle bundle = new Bundle();
					bundle.putString(SocialAuthAdapter.PROVIDER, authProviders[v.getId()].toString());
					dialogListener.onComplete(bundle);
				} else {
					// Getting selected provider and starting authentication
					authorize(ctx, authProviders[v.getId()]);
				}
			}
		};

		// Adding Buttons to Bar
		for (int i = 0; i < providerCount; i++) {
			ImageView provider = new ImageView(ctx);
			provider.setId(i);
			provider.setImageResource(authProviderLogos[i]);
			provider.setPadding(5, 5, 5, 5);
			provider.setOnClickListener(viewlistener);
			linearbar.addView(provider);
		}

		if (!Util.isNetworkAvailable(ctx)) {
			dialogListener.onError(new SocialAuthError("Please check your Internet connection", new Exception("")));
			return;
		}
	}

	/**
	 * Method to handle configuration , Use directly for CustomUI
	 * 
	 * @param ctx
	 *            activity context
	 * @param provider
	 *            name of provider
	 */

	public void authorize(Context ctx, Provider provider) {
		if (!Util.isNetworkAvailable(ctx)) {
			dialogListener.onError(new SocialAuthError("Please check your Internet connection", new Exception("")));
			return;
		}
		context = ctx;
		currentProvider = provider;
		Log.d("SocialAuthAdapter", "Selected provider is " + currentProvider);

		// Initialize socialauth manager if not already done
		if (socialAuthManager != null) {
			// If SocialAuthManager is not null and contains Provider Id, send
			// response to listener
			if (socialAuthManager.getConnectedProvidersIds().contains(currentProvider.toString())) {
				Log.d("SocialAuthAdapter", "Provider already connected");
				Bundle bundle = new Bundle();
				bundle.putString(SocialAuthAdapter.PROVIDER, currentProvider.toString());
				dialogListener.onComplete(bundle);
			}

			// If SocialAuthManager is not null and not contains Provider Id
			else {
				connectProvider(ctx, provider);
			}

		}
		// If SocialAuthManager is null
		else {
			Log.d("SocialAuthAdapter", "Loading keys and secrets from configuration");

			socialAuthManager = new SocialAuthManager();
			try {
				loadConfig(ctx);

			} catch (Exception e) {
				Log.d("SocialAuthAdapter", "Could not load configuration");
			}
			connectProvider(ctx, provider);
		}
	}

	/**
	 * Method to add user defined configuration. Please delete
	 * oauth_consumers.properties before using this method.
	 * 
	 * @param provider
	 *            name of provider
	 * @param key
	 *            provider key
	 * @param secret
	 *            provider secret
	 * @param permissions
	 *            permissions for provider
	 * 
	 */

	public void addConfig(Provider provider, String key, String secret, String permissions) throws Exception {
		OAuthConfig authConfig = new OAuthConfig(key, secret);
		authConfig.setId(provider.toString());
		authConfig.setCustomPermissions(permissions);
		authMap.put(provider.toString(), authConfig);
	}

	/**
	 * Internal method to load config
	 * 
	 * @param context
	 *            The Android Activity context
	 * 
	 */

	private void loadConfig(Context ctx) throws Exception {

		SocialAuthConfig config = new SocialAuthConfig();
		Resources resources = ctx.getResources();
		AssetManager assetManager = resources.getAssets();
		InputStream inputStream;
		boolean fileExist = false;
		try {
			inputStream = assetManager.open("oauth_consumer.properties");
			fileExist = true;
		} catch (Exception e) {
			fileExist = false;
			throw new SocialAuthException("oauth_consumer.properties not found");
		}

		if (fileExist) {
			// Add keys from oauth_consumers file. loadConfig() method
			// is removed
			config.load(inputStream);
			socialAuthManager.setSocialAuthConfig(config);
		} else {
			// Add user keys if outh_consumers file not exists
			for (String key : authMap.keySet()) {
				config.addProviderConfig(key, authMap.get(key));
			}
			socialAuthManager.setSocialAuthConfig(config);
		}
	}

	/**
	 * Internal method to handle dialog-based authentication backend for
	 * authorize().
	 * 
	 * @param context
	 *            The Android Activity that will parent the auth dialog.
	 * @param provider
	 *            Provider being authenticated
	 * 
	 */
	private void startDialogAuth(final Context context, final Provider provider) {
		CookieSyncManager.createInstance(context);

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					// Get Callback url
					url = socialAuthManager.getAuthenticationUrl(provider.toString(), provider.getCallBackUri())
							+ "&type=user_agent&display=touch";

					handler.post(new Runnable() {
						@Override
						public void run() {
							Log.d("SocialAuthAdapter", "Loading URL : " + url);
							String callbackUri = provider.getCallBackUri();
							Log.d("SocialAuthAdapter", "Callback URI : " + callbackUri);
							// start webview dialog
							new SocialAuthDialog(context, url, provider, dialogListener, socialAuthManager).show();
						}
					});
				} catch (Exception e) {

					dialogListener.onError(new SocialAuthError("URL Authentication error", e));
				}
			}
		};

		new Thread(runnable).start();
	}

	/**
	 * Internal method to connect provider. The method check for access token If
	 * available it connects manager with AccessGrant else create new manager
	 * and open webview
	 * 
	 * @param context
	 *            The Android Activity that will parent the auth dialog.
	 * @param provider
	 *            Provider being authenticated
	 */

	private void connectProvider(final Context ctx, final Provider provider) {

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

		if (pref.contains(provider.toString() + " key")) {
			tokenMap = new HashMap<String, Object>();

			for (Map.Entry entry : pref.getAll().entrySet())
				tokenMap.put(entry.getKey().toString(), entry.getValue());

			// If Access Token is available , connect using Access Token
			try {

				HashMap<String, Object> attrMap = null;
				attrMap = new HashMap<String, Object>();

				String key = (String) tokenMap.get(provider.toString() + " key");

				String secret = (String) tokenMap.get(provider.toString() + " secret");

				String providerid = (String) tokenMap.get(provider.toString() + " providerid");

				String temp = provider.toString() + "attribute";
				for (String attr : tokenMap.keySet()) {
					if (attr.startsWith(temp)) {
						int startLocation = attr.indexOf(temp) + temp.length() + 1;
						attrMap.put(attr.substring(startLocation), tokenMap.get(attr));
					}

				}

				for (Map.Entry entry : attrMap.entrySet()) {
					System.out.println(entry.getKey() + ", " + entry.getValue());
				}

				final AccessGrant accessGrant = new AccessGrant(key, secret);
				accessGrant.setProviderId(providerid);
				accessGrant.setAttributes(attrMap);

				Log.d("SocialAuthAdapter", "Loading from AccessToken");

				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						try {

							socialAuthManager.connect(accessGrant);

							// To check validity of Access Token
							getCurrentProvider().getUserProfile().getValidatedId();

							handler.post(new Runnable() {
								@Override
								public void run() {

									Bundle bundle = new Bundle();
									bundle.putString(SocialAuthAdapter.PROVIDER, currentProvider.toString());
									dialogListener.onComplete(bundle);
								}
							});
						} catch (Exception e) {
							dialogListener.onError(new SocialAuthError("Token Error", e));
							Log.d("SocialAuthAdapter", "Starting webview for authentication for new Token");

							socialAuthManager = new SocialAuthManager();
							try {
								loadConfig(ctx);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							startDialogAuth(ctx, currentProvider);

						}
					}
				};

				new Thread(runnable).start();

			} catch (Exception e) {
				dialogListener.onError(new SocialAuthError("Unknown error", e));
				e.printStackTrace();
			}
		}
		// If Access Token is not available , Open Authentication Dialog
		else {
			Log.d("SocialAuthAdapter", "Starting webview for authentication");
			startDialogAuth(ctx, currentProvider);
		}

	}

	/**
	 * Sets Size of Dialog. Max value for Portrait and Landscape - 40,60
	 */
	public void setDialogSize(float width, float height) {
		if (width < 0 || width > 40)
			SocialAuthDialog.width = 40;
		else
			SocialAuthDialog.width = width;

		if (height < 0 || height > 60)
			SocialAuthDialog.height = 60;
		else
			SocialAuthDialog.height = height;
	}

	/**
	 * Signs out the user out of current provider
	 * 
	 * @return Status of signing out
	 */
	public boolean signOut(String providerName) {

		CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();

		if (providerName != null) {

			if (socialAuthManager.getConnectedProvidersIds().contains(providerName))
				socialAuthManager.disconnectProvider(providerName);

			Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
			edit.remove(providerName + " key");
			edit.commit();

			Log.d("SocialAuthAdapter", "Disconnecting Provider");

			return true;
		} else {
			Log.d("SocialAuthAdapter", "The provider name should be same");
			return false;
		}
	}

	/**
	 * Disable title of dialog.
	 * 
	 * @param titleStatus
	 *            default false , Set true to disable dialog titlebar
	 * 
	 */
	public void setTitleVisible(boolean titleStatus) {
		SocialAuthDialog.titleStatus = titleStatus;
	}

	/**
	 * Method to update status of user
	 * 
	 * @param message
	 *            The message to be send.
	 * @param listener
	 *            socialAuth listener to get status
	 * @param shareOption
	 *            true - share on all providers false - share on current
	 *            provider
	 */

	public void updateStatus(final String message, final SocialAuthListener<Integer> listener, final boolean shareOption) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					if (shareOption == true) {
						final List<String> activeProviders = socialAuthManager.getConnectedProvidersIds();
						for (int i = 0; i < activeProviders.size(); i++) {
							final String provider = activeProviders.get(i);
							final Response response = socialAuthManager.getProvider(provider).updateStatus(message);

							handler.post(new Runnable() {
								@Override
								public void run() {
									int status = response.getStatus();
									listener.onExecute(provider, Integer.valueOf(status));
								}
							});
						}
					} else {
						final Response response = getCurrentProvider().updateStatus(message);

						handler.post(new Runnable() {
							@Override
							public void run() {
								int status = response.getStatus();
								listener.onExecute(getCurrentProvider().getProviderId(), Integer.valueOf(status));
							}
						});
					}

				} catch (Exception e) {
					dialogListener.onError(new SocialAuthError("Message Not Posted", e));
				}
			}
		};

		new Thread(runnable).start();
	}

	/**
	 * Method to share message with link preview on facebook only
	 * 
	 * @param message
	 *            The message to be send.
	 * @param name
	 *            title
	 * @param caption
	 *            subtitle
	 * @param description
	 *            story description
	 * @param link
	 *            link to be share
	 * @param picture
	 *            picture for link
	 */

	public void updateStory(final String message, final String name, final String caption, final String description,
			final String link, final String picture, final SocialAuthListener<Integer> listener) {

		if (getCurrentProvider().getProviderId().equalsIgnoreCase("facebook")) {
			final Map<String, String> params = new HashMap<String, String>();
			params.put("name", name);
			params.put("caption", caption);
			params.put("description", description);
			params.put("link", link);
			params.put("picture", picture);

			final StringBuilder strb = new StringBuilder();

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						// Call using API method of socialauth
						strb.append("message=").append(URLEncoder.encode(message, Constants.ENCODING));
						strb.append("&access_token").append("=").append(getCurrentProvider().getAccessGrant().getKey());
						final Response response = getCurrentProvider().api(UPDATE_STATUS_URL,
								MethodType.POST.toString(), params, null, strb.toString());

						handler.post(new Runnable() {
							@Override
							public void run() {
								int status = response.getStatus();
								listener.onExecute(getCurrentProvider().getProviderId(), Integer.valueOf(status));
							}
						});
					} catch (Exception e) {
						dialogListener.onError(new SocialAuthError("Message Not Posted", e));
					}
				}
			};

			new Thread(runnable).start();
		} else {
			Log.d("SocialAuthAdapter", "Provider Not Supported");
		}
	}

	/**
	 * Makes HTTP request to a given URL.It attaches access token in URL.
	 * 
	 * @param url
	 *            URL to make HTTP request.
	 * @param methodType
	 *            Method type can be GET, POST or PUT
	 * @param params
	 * @param headerParams
	 *            Parameters need to pass as Header Parameters
	 * @param body
	 *            Request Body
	 * @return Response object
	 * @throws Exception
	 */
	public Response api(final String url, final String methodType, final Map<String, String> params,
			final Map<String, String> headerParams, final String body) throws Exception {
		Response response = null;
		try {
			response = getCurrentProvider().api(url, methodType, params, headerParams, body);
		} catch (Exception e) {
			throw new SocialAuthException("Error while making request to URL : " + url, e);
		}
		return response;
	}

	/**
	 * Synchronous Method to get Profile of User Use this method inside
	 * AsyncTask else you will get NetworkOSThreadException
	 */

	public Profile getUserProfile() {
		Profile profileList = null;
		try {
			profileList = getCurrentProvider().getUserProfile();
			Log.d("SocialAuthAdapter", "Received Profile Details");
			return profileList;

		} catch (Exception e) {
			Log.d("SocialAuthAdapter", "Profile Details not Received");
			return null;
		}
	}

	/**
	 * Asynchronous Method to get User Profile.Returns result in onExecute() of
	 * AsyncTaskListener.
	 */

	public void getUserProfileAsync(SocialAuthListener<Profile> listener) {
		new ProfileTask(listener).execute();
	}

	/**
	 * AsyncTask to get user profile
	 */

	private class ProfileTask extends AsyncTask<Void, Void, Profile> {

		SocialAuthListener<Profile> listener;

		private ProfileTask(SocialAuthListener<Profile> listener) {
			this.listener = listener;
		}

		@Override
		protected Profile doInBackground(Void... params) {

			Profile profileList = null;
			try {
				profileList = getCurrentProvider().getUserProfile();
				Log.d("SocialAuthAdapter", "Received Profile Details");
				return profileList;

			} catch (Exception e) {
				e.printStackTrace();
				listener.onError(new SocialAuthError("Profile Details not Received", e));
				return null;
			}
		}

		@Override
		protected void onPostExecute(Profile profile) {
			listener.onExecute(getCurrentProvider().getProviderId(), profile);
		}
	}

	/**
	 * Synchronous Method to User Contacts. Use this method inside AsyncTask
	 * else you will get NetworkOSThreadException
	 */

	public List<Contact> getContactList() {
		List<Contact> contactsMap = null;
		try {
			contactsMap = getCurrentProvider().getContactList();
			Log.d("SocialAuthAdapter", "Received Contact list");
			return contactsMap;
		} catch (Exception e) {
			Log.d("SocialAuthAdapter", "Contact list not Received");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Asynchronous Method to get User Contacts.Returns result in onExecute() of
	 * AsyncTaskListener.
	 */

	public void getContactListAsync(SocialAuthListener<List<Contact>> listener) {
		new ContactTask(listener).execute();
	}

	/**
	 * AsyncTask to retrieve contacts
	 */

	private class ContactTask extends AsyncTask<Void, Void, List<Contact>> {

		SocialAuthListener<List<Contact>> listener;

		private ContactTask(SocialAuthListener<List<Contact>> listener) {
			this.listener = listener;
		}

		@Override
		protected List<Contact> doInBackground(Void... params) {
			List<Contact> contactsMap = null;
			try {
				contactsMap = getCurrentProvider().getContactList();
				Log.d("SocialAuthAdapter", "Received Contact list");
				return contactsMap;
			} catch (Exception e) {
				e.printStackTrace();
				listener.onError(new SocialAuthError("Contact List not Received", e));
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Contact> contactsMap) {
			listener.onExecute(getCurrentProvider().getProviderId(), contactsMap);
		}
	}

	/**
	 * Synchronous Method to get Feeds of User Use this method inside AsyncTask
	 * else you will get NetworkOSThreadException
	 */

	public List<Feed> getFeeds() {
		try {
			List<Feed> feedMap = null;
			if (getCurrentProvider().isSupportedPlugin(org.brickred.socialauth.plugin.FeedPlugin.class)) {
				FeedPlugin p = getCurrentProvider().getPlugin(org.brickred.socialauth.plugin.FeedPlugin.class);
				feedMap = p.getFeeds();
				Log.d("SocialAuthAdapter", "Received Feeds");
			} else
				Log.d("SocialAuthAdapter", "Feeds not Supported from Provider");

			return feedMap;

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SocialAuthAdapter", "Feeds not Available from Provider");
			return null;
		}
	}

	/**
	 * Asynchronous Method to get User Feeds.Returns result in onExecute() of
	 * AsyncTaskListener.Currently supports Facebook and Twitter
	 */

	public void getFeedsAsync(SocialAuthListener<List<Feed>> listener) {
		new FeedTask(listener).execute();
	}

	/**
	 * AsyncTask to retrieve feeds
	 */
	private class FeedTask extends AsyncTask<Void, Void, List<Feed>> {

		SocialAuthListener<List<Feed>> listener;

		private FeedTask(SocialAuthListener<List<Feed>> listener) {
			this.listener = listener;
		}

		@Override
		protected List<Feed> doInBackground(Void... params) {

			try {
				List<Feed> feedMap = null;
				if (getCurrentProvider().isSupportedPlugin(org.brickred.socialauth.plugin.FeedPlugin.class)) {
					FeedPlugin p = getCurrentProvider().getPlugin(org.brickred.socialauth.plugin.FeedPlugin.class);
					feedMap = p.getFeeds();
					Log.d("SocialAuthAdapter", "Received Feeds");
				} else
					Log.d("SocialAuthAdapter", "Feeds not Supported from Provider");

				return feedMap;

			} catch (Exception e) {
				e.printStackTrace();
				listener.onError(new SocialAuthError("Feed not Available from Provider", e));
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Feed> feedMap) {
			listener.onExecute(getCurrentProvider().getProviderId(), feedMap);
		}
	}

	/**
	 * Synchronous Method to get Albums of User Use this method inside AsyncTask
	 * else you will get NetworkOSThreadException
	 */

	public List<Album> getAlbums() {

		try {
			List<Album> albumMap = null;

			if (getCurrentProvider().isSupportedPlugin(org.brickred.socialauth.plugin.AlbumsPlugin.class)) {
				AlbumsPlugin p = getCurrentProvider().getPlugin(org.brickred.socialauth.plugin.AlbumsPlugin.class);
				albumMap = p.getAlbums();

				Log.d("SocialAuthAdapter", "Received Albums");
			} else
				Log.d("SocialAuthAdapter", "Albums not Supported from Provider");

			return albumMap;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("SocialAuthAdapter", "Albums not Available from Provider");
			return null;
		}
	}

	/**
	 * Asynchronous Method to get User Albums.Returns result in onExecute() of
	 * AsyncTaskListener.Currently supports Facebook and Twitter
	 */

	public void getAlbumsAsync(SocialAuthListener<List<Album>> listener) {
		new AlbumTask(listener).execute();
	}

	/**
	 * AsyncTask to retrieve albums
	 */

	private class AlbumTask extends AsyncTask<Void, Void, List<Album>> {

		SocialAuthListener<List<Album>> listener;

		private AlbumTask(SocialAuthListener<List<Album>> listener) {
			this.listener = listener;
		}

		@Override
		protected List<Album> doInBackground(Void... params) {
			try {
				List<Album> albumMap = null;

				if (getCurrentProvider().isSupportedPlugin(org.brickred.socialauth.plugin.AlbumsPlugin.class)) {
					AlbumsPlugin p = getCurrentProvider().getPlugin(org.brickred.socialauth.plugin.AlbumsPlugin.class);
					albumMap = p.getAlbums();

					Log.d("SocialAuthAdapter", "Received Albums");
				} else
					Log.d("SocialAuthAdapter", "Albums not Supported from Provider");

				return albumMap;
			} catch (Exception e) {
				e.printStackTrace();
				listener.onError(new SocialAuthError("Albums not Available from Provider", e));
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Album> albumMap) {

			listener.onExecute(getCurrentProvider().getProviderId(), albumMap);
		}
	}

	/**
	 * Synchronous Method to upload image on provider
	 * 
	 * @param message
	 *            message to be attached with image
	 * @param fileName
	 *            image file name
	 * @param bitmap
	 *            image bitmap to be uploaded
	 * @param quality
	 *            image quality for jpeg , enter 0 for png
	 * 
	 *            Returns result in onReceive()
	 */
	public Integer uploadImage(String message, String fileName, Bitmap bitmap, int quality) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if (fileName.endsWith("PNG") || fileName.endsWith("png")) {
			bitmap.compress(CompressFormat.PNG, 0, bos);
		} else if (fileName.endsWith("JPEG") || fileName.endsWith("JPG") || fileName.endsWith("jpg")
				|| fileName.endsWith("jpeg")) {
			bitmap.compress(CompressFormat.JPEG, quality, bos);
		} else {
			throw new SocialAuthException("Image Format not supported");
		}

		InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());

		Response res = null;
		try {
			if (getCurrentProvider().getProviderId().equalsIgnoreCase("facebook")
					|| getCurrentProvider().getProviderId().equalsIgnoreCase("twitter")) {
				res = getCurrentProvider().uploadImage(message, fileName, inputStream);
				Log.d("SocialAuthAdapter", "Image Uploaded");
				return Integer.valueOf(res.getStatus());
			} else {
				throw new SocialAuthException("Provider not Supported");
			}
		} catch (Exception e) {
			throw new SocialAuthException("Image Upload Error");
		}
	}

	/**
	 * Asynchronous Method to upload image on provider.Returns result in
	 * onExecute() of AsyncTaskListener.Currently supports Facebook and Twitter
	 * 
	 * @param message
	 *            message to be attached with image
	 * @param fileName
	 *            image file name
	 * @param bitmap
	 *            image bitmap to be uploaded
	 * @param quality
	 *            image quality for jpeg , enter 0 for png
	 */

	public void uploadImageAsync(String message, String fileName, Bitmap bitmap, int quality,
			SocialAuthListener<Integer> listener) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		if (fileName.endsWith("PNG") || fileName.endsWith("png")) {
			bitmap.compress(CompressFormat.PNG, 0, bos);
		} else if (fileName.endsWith("JPEG") || fileName.endsWith("JPG") || fileName.endsWith("jpg")
				|| fileName.endsWith("jpeg")) {
			bitmap.compress(CompressFormat.JPEG, quality, bos);
		} else {
			throw new SocialAuthException("Image Format not supported");
		}

		InputStream inputStream = new ByteArrayInputStream(bos.toByteArray());
		if (getCurrentProvider().getProviderId().equalsIgnoreCase("facebook")
				|| getCurrentProvider().getProviderId().equalsIgnoreCase("twitter")) {
			new UploadImageTask(listener).execute(message, fileName, inputStream);
		} else {
			throw new SocialAuthException("Provider not Supported");
		}
	}

	/**
	 * AsyncTask to uploadImage
	 */

	private class UploadImageTask extends AsyncTask<Object, Void, Integer> {

		SocialAuthListener<Integer> listener;

		private UploadImageTask(SocialAuthListener<Integer> listener) {
			this.listener = listener;
		}

		@Override
		protected Integer doInBackground(Object... params) {
			Response res = null;
			try {
				res = getCurrentProvider().uploadImage((String) params[0], (String) params[1], (InputStream) params[2]);
				Log.d("SocialAuthAdapter", "Image Uploaded");
				return Integer.valueOf(res.getStatus());
			} catch (Exception e) {
				listener.onError(new SocialAuthError("Image Upload Error", e));
				return null;
			}
		}

		@Override
		protected void onPostExecute(Integer status) {

			listener.onExecute(getCurrentProvider().getProviderId(), status);
		}
	}

	public void getCareerAsync(SocialAuthListener<Career> listener) {
		new CareerTask(listener).execute();
	}

	/**
	 * AsyncTask to uploadImage
	 */

	private class CareerTask extends AsyncTask<Void, Void, Career> {

		SocialAuthListener<Career> listener;

		private CareerTask(SocialAuthListener<Career> listener) {
			this.listener = listener;
		}

		@Override
		protected Career doInBackground(Void... params) {
			try {
				Career careerList = null;

				if (getCurrentProvider().isSupportedPlugin(org.brickred.socialauth.plugin.CareerPlugin.class)) {
					CareerPlugin p = getCurrentProvider().getPlugin(org.brickred.socialauth.plugin.CareerPlugin.class);
					careerList = p.getCareerDetails();
					Log.d("SocialAuthAdapter", "Received Career Details");

				} else
					Log.d("SocialAuthAdapter", "Career Details only Supported from Linkedin");

				return careerList;
			} catch (Exception e) {
				e.printStackTrace();
				listener.onError(new SocialAuthError("Career Details not Available from Provider", e));
				return null;
			}
		}

		@Override
		protected void onPostExecute(Career careerList) {

			listener.onExecute(getCurrentProvider().getProviderId(), careerList);
		}
	}
}
