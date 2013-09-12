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

import java.util.Map;

import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.util.AccessGrant;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Dialog that wraps a Web view for authenticating with the given social
 * network. All the OAuth redirection happens over here and the success and
 * failure are handed over to the listener
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * @author abhinav.maheswari@3pillarglobal.com
 * 
 */
public class SocialAuthDialog extends Dialog {

	// Variables
	public static final int BLUE = 0xFF6D84B4;
	public static final int MARGIN = 4;
	public static final int PADDING = 2;

	public static float width = 40;
	public static float height = 60;

	public static final float[] DIMENSIONS_DIFF_LANDSCAPE = { width, height };
	public static final float[] DIMENSIONS_DIFF_PORTRAIT = { width, height };

	public static boolean titleStatus = false;
	public static final String DISPLAY_STRING = "touch";

	private final String mUrl;
	private String newUrl;
	private int count;

	// Android Components
	private TextView mTitle;
	private final DialogListener mListener;
	private ProgressDialog mSpinner;
	private CustomWebView mWebView;
	private LinearLayout mContent;
	private Drawable icon;
	private Handler handler;
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);

	// SocialAuth Components
	private final SocialAuthManager mSocialAuthManager;
	private final Provider mProviderName;

	/**
	 * Constructor for the dialog
	 * 
	 * @param context
	 *            Parent component that opened this dialog
	 * @param url
	 *            URL that will be used for authenticating
	 * @param providerName
	 *            Name of provider that is being authenticated
	 * @param listener
	 *            Listener object to handle events
	 * @param socialAuthManager
	 *            Underlying SocialAuth framework for OAuth
	 */
	public SocialAuthDialog(Context context, String url, Provider providerName, DialogListener listener,
			SocialAuthManager socialAuthManager) {
		super(context);
		mProviderName = providerName;
		mUrl = url;
		mListener = listener;
		mSocialAuthManager = socialAuthManager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		Util.getDisplayDpi(getContext());

		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		mSpinner.setCancelable(true);

		mContent = new LinearLayout(getContext());
		mContent.setOrientation(LinearLayout.VERTICAL);
		setUpTitle();
		setUpWebView();

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		int orientation = getContext().getResources().getConfiguration().orientation;
		float[] dimensions = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? DIMENSIONS_DIFF_LANDSCAPE
				: DIMENSIONS_DIFF_PORTRAIT;

		addContentView(mContent, new LinearLayout.LayoutParams(display.getWidth()
				- ((int) (dimensions[0] * scale + 0.5f)), display.getHeight() - ((int) (dimensions[1] * scale + 0.5f))));

		mSpinner.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialogInterface) {
				mWebView.stopLoading();
				mListener.onBack();
				SocialAuthDialog.this.dismiss();
			}
		});

		this.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
					mWebView.stopLoading();
					dismiss();
					mListener.onBack();
					return true;
				}
				return false;
			}
		});

	}

	/**
	 * Sets title and icon of provider
	 * 
	 */

	private void setUpTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mTitle = new TextView(getContext());
		int res = getContext().getResources().getIdentifier(mProviderName.toString(), "drawable",
				getContext().getPackageName());
		icon = getContext().getResources().getDrawable(res);
		StringBuilder sb = new StringBuilder();
		sb.append(mProviderName.toString().substring(0, 1).toUpperCase());
		sb.append(mProviderName.toString().substring(1, mProviderName.toString().length()));
		mTitle.setText(sb.toString());
		mTitle.setGravity(Gravity.CENTER_VERTICAL);
		mTitle.setTextColor(Color.WHITE);
		mTitle.setTypeface(Typeface.DEFAULT_BOLD);
		mTitle.setBackgroundColor(BLUE);
		mTitle.setPadding(MARGIN + PADDING, MARGIN, MARGIN, MARGIN);
		mTitle.setCompoundDrawablePadding(MARGIN + PADDING);
		mTitle.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);

		if (!titleStatus)
			mContent.addView(mTitle);
	}

	/**
	 * Set up WebView to load the provider URL
	 * 
	 */
	private void setUpWebView() {
		mWebView = new CustomWebView(getContext());
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setWebViewClient(new SocialAuthDialog.SocialAuthWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.loadUrl(mUrl);
		mWebView.setLayoutParams(FILL);

		if (mProviderName.toString().equalsIgnoreCase("yahoo") || mProviderName.toString().equalsIgnoreCase("yammer"))
			mWebView.getSettings().setUseWideViewPort(true);

		mContent.addView(mWebView);
	}

	/**
	 * WebView Client
	 */

	private class SocialAuthWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("SocialAuth-WebView", "Override url: " + url);

			if (url.startsWith(mProviderName.getCallBackUri())
					&& (mProviderName.toString().equalsIgnoreCase("facebook") || mProviderName.toString()
							.equalsIgnoreCase("twitter"))) {
				if (url.startsWith(mProviderName.getCancelUri())) {
					// Handles Twitter and Facebook Cancel
					mListener.onCancel();
				} else { // for Facebook and Twitter
					final Map<String, String> params = Util.parseUrl(url);

					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							try {

								AuthProvider auth = mSocialAuthManager.connect(params);
								writeToken(auth);

								handler.post(new Runnable() {
									@Override
									public void run() {
										if (mSpinner != null && mSpinner.isShowing())
											mSpinner.dismiss();

										Bundle bundle = new Bundle();
										bundle.putString(SocialAuthAdapter.PROVIDER, mProviderName.toString());
										mListener.onComplete(bundle);
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
								mListener.onError(new SocialAuthError("Unknown Error", e));
							}
						}
					};
					new Thread(runnable).start();
				}
				SocialAuthDialog.this.dismiss();
				return true;
			}

			// ***Handling Runkeeper Facebook Start**************

			else if (url.startsWith("https://www.facebook.com/dialog/oauth")) {
				newUrl = url.replace("https://www.facebook.com/dialog/oauth", "https://m.facebook.com/dialog/oauth");

				mWebView.loadUrl(newUrl);
				return true;
			} else if (url.startsWith("http://runkeeper.com/jsp/widgets/streetTeamWidgetClose.jsp")) {
				mWebView.loadUrl("http://runkeeper.com/facebookSignIn");
				return true;
			} else if (url.startsWith("http://runkeeper.com/home")) {
				Log.d("Again Calling auth URL ", "SocialAuth");
				mWebView.loadUrl(mUrl);
				return false;
			}

			// ****Handling Runkeeper Facebook End*************

			else if (url.startsWith(mProviderName.getCancelUri())) {
				// Handles MySpace and Linkedin Cancel
				mListener.onCancel();
				SocialAuthDialog.this.dismiss();
				return true;
			} else if (url.contains(DISPLAY_STRING)) {
				return false;
			}

			return false;

		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.d("SocialAuth-WebView", "Inside OnReceived Error");
			Log.d("SocialAuth-WebView", String.valueOf(errorCode));
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new SocialAuthError(description, new Exception(failingUrl)));
			SocialAuthDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			// To set zoom density of runkeeper dialog for various densities
			if (url.startsWith("https://runkeeper.com/apps/authorize") & count < 1) {
				if (Util.UI_SIZE == 4 && Util.UI_DENSITY == 120) {
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
					mWebView.setInitialScale(60);
					count = 1;
				} else if (Util.UI_SIZE == 3 && Util.UI_DENSITY == 160) {
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
					mWebView.setInitialScale(70);
					count = 1;
				} else if (Util.UI_DENSITY == 240) {
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
					count = 1;
				} else {
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
					mWebView.setInitialScale(120);
					count = 1;
				}
			}

			// To set zoom density of yahoo dialog for mdpi and xhdpi
			if (mProviderName.toString().equalsIgnoreCase("yahoo")) {
				if (url.startsWith("https://login.yahoo.com/config/login")) {
					if (Util.UI_DENSITY == 160 && Util.UI_SIZE == 4) {
						mWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
						mWebView.setInitialScale(155);
					} else if (Util.UI_DENSITY == 320) {
						if (Util.UI_SIZE == 7)
							mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
						else if (Util.UI_SIZE == 10) {
							mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
							mWebView.setInitialScale(120);
						}
					} else
						mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
				}
			}

			// To set zoom density of linkedin dialog for ldpi and mdpi
			if (mProviderName.toString().equalsIgnoreCase("linkedin")) {
				if (Util.UI_DENSITY == 120)
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
				else if (Util.UI_DENSITY == 160) {
					if (Util.UI_SIZE == 3)
						mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
					else
						mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
				} else
					mWebView.setInitialScale(60);
			}

			// To set zoom density of yammer dialog
			if (mProviderName.toString().equalsIgnoreCase("yammer")) {
				if (Util.UI_DENSITY == 120) {
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
					mWebView.setInitialScale(55);
				} else if (Util.UI_DENSITY == 160) {
					if (Util.UI_SIZE == 3) {
						mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
						mWebView.setInitialScale(65);
					} else if (Util.UI_SIZE == 10) {
						mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
					}
				} else if (Util.UI_DENSITY == 240) {
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
				} else if (Util.UI_DENSITY == 320) {
					mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
				}
			}

			Log.d("SocialAuth-WebView", "onPageStart:" + url);
			mSpinner.show();

			// For Linkedin, MySpace, Runkeeper - Calls onPageStart to
			// authorize.
			if (url.startsWith(mProviderName.getCallBackUri())) {
				if (url.startsWith(mProviderName.getCancelUri())) {
					mListener.onCancel();
				} else {
					final Map<String, String> params = Util.parseUrl(url);
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							try {
								AuthProvider auth = mSocialAuthManager.connect(params);

								// Don't save token for yahoo, yammer,
								// salesforce
								if (!mProviderName.toString().equalsIgnoreCase("yahoo")
										|| !mProviderName.toString().equalsIgnoreCase("yammer")
										|| !mProviderName.toString().equalsIgnoreCase("salesforce"))
									writeToken(auth);

								handler.post(new Runnable() {
									@Override
									public void run() {

										if (mSpinner != null && mSpinner.isShowing())
											mSpinner.dismiss();

										Bundle bundle = new Bundle();
										bundle.putString(SocialAuthAdapter.PROVIDER, mProviderName.toString());
										mListener.onComplete(bundle);
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
								mListener.onError(new SocialAuthError("Could not connect using SocialAuth", e));
							}
						}
					};
					new Thread(runnable).start();
				}
				SocialAuthDialog.this.dismiss();
			}
		}

		@Override
		public void onPageFinished(WebView view, final String url) {

			super.onPageFinished(view, url);

			// workaround for yahoo and runkeeper
			mWebView.setPictureListener(new PictureListener() {
				@Override
				public void onNewPicture(WebView view, Picture arg1) {
					// To set zoom density of yahoo dialog
					if (mProviderName.toString().equalsIgnoreCase("yahoo")) {
						if (url.startsWith("https://login.yahoo.com/config/login"))
							mWebView.scrollTo(Util.UI_YAHOO_SCROLL, 0);
						else if (url.startsWith("https://api.login.yahoo.com//oauth/v2")) {
							if (Util.UI_DENSITY == 160 && Util.UI_SIZE == 3)
								mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
							else
								mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);

							mWebView.scrollTo(Util.UI_YAHOO_ALLOW, 0);
						}
						mSpinner.dismiss();
					}

					if (mProviderName.toString().equalsIgnoreCase("yammer")) {
						if (url.startsWith("https://www.yammer.com/dialog/authenticate")) {
							if (Util.UI_DENSITY == 240)
								mWebView.scrollTo(105, 0);
							else if (Util.UI_DENSITY == 320)
								mWebView.scrollTo(95, 0);
						}
					}

					if (mProviderName.toString().equalsIgnoreCase("runkeeper")
							&& (url.startsWith("http://m.facebook.com/login.php") || url
									.startsWith("https://m.facebook.com/dialog/oauth"))) {
						// Set Zoom Density of FaceBook Dialog
						mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
					}
				}
			});

			String title = mWebView.getTitle();
			if (title != null && title.length() > 0) {
				mTitle.setText(title);
			}

			if (!mProviderName.toString().equalsIgnoreCase("yahoo"))
				mSpinner.dismiss();
		}

	}

	/**
	 * Internal Method to create new File in internal memory for each provider
	 * and save accessGrant
	 * 
	 * @param auth
	 *            AuthProvider
	 */

	private void writeToken(AuthProvider auth) {

		AccessGrant accessGrant = auth.getAccessGrant();
		String key = accessGrant.getKey();
		String secret = accessGrant.getSecret();

		String providerid = accessGrant.getProviderId();

		Map<String, Object> attributes = accessGrant.getAttributes();

		Editor edit = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

		edit.putString(mProviderName.toString() + " key", key);
		edit.putString(mProviderName.toString() + " secret", secret);
		edit.putString(mProviderName.toString() + " providerid", providerid);

		if (attributes != null) {
			for (Map.Entry entry : attributes.entrySet()) {
				System.out.println(entry.getKey() + ", " + entry.getValue());
			}

			for (String s : attributes.keySet()) {
				edit.putString(mProviderName.toString() + "attribute " + s, String.valueOf(attributes.get(s)));
			}

		}

		edit.commit();

	}

	/**
	 * Workaround for Null pointer exception in WebView.onWindowFocusChanged in
	 * droid phones and emulator with android 2.2 os. It prevents first time
	 * WebView crash.
	 */

	public class CustomWebView extends WebView {

		public CustomWebView(Context context) {
			super(context);
		}

		public CustomWebView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public CustomWebView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void onWindowFocusChanged(boolean hasWindowFocus) {
			try {
				super.onWindowFocusChanged(hasWindowFocus);
			} catch (NullPointerException e) {
				// Catch null pointer exception
			}
		}
	}
}
