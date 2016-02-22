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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 
 * Utility methods
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * @author abhinav.maheswari@3pillarglobal.com
 * 
 */

public final class Util {

	public static int UI_DENSITY;
	public static int UI_SIZE;
	public static int UI_YAHOO_ALLOW;
	public static int UI_RESOLUTION;

	public static String providerList[] = { "facebook", "twitter", "runkeeper", "yammer", "foursquare", "salesforce",
			"linkedin", "myspace", "flickr", "instagram" };

	/**
	 * URL encoding of query parameters of a URL
	 * 
	 * @param parameters
	 * @return encoded URL
	 */
	@SuppressWarnings("deprecation")
	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}

	/**
	 * URL decoding of query parameters of a URL
	 * 
	 * @param s
	 *            URL to be decoded
	 * @return Map of parameter and values
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, String> decodeUrl(String s) {
		Map<String, String> params = new HashMap<String, String>();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				if (v.length > 1) {
					params.put(URLDecoder.decode(v[0]), v.length > 1 ? URLDecoder.decode(v[1]) : null);
				}
			}
		}
		return params;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 * 
	 * @param url
	 *            the URL to parse
	 * @return a dictionary bundle of keys and values
	 */
	public static Map<String, String> parseUrl(String url) {
		// hack to prevent MalformedURLException
		url = url.replace("fbconnect", "http");
		try {
			URL u = new URL(url);
			Map<String, String> params = decodeUrl(u.getQuery());
			params.putAll(decodeUrl(u.getRef()));
			return params;
		} catch (MalformedURLException e) {
			return new HashMap<String, String>();
		}
	}

	/**
	 * Display a simple alert dialog with the given text and title.
	 * 
	 * @param context
	 *            Android context in which the dialog should be displayed
	 * @param title
	 *            Alert dialog title
	 * @param text
	 *            Alert dialog message
	 */

	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	/**
	 * Function for check the network connectivity
	 * 
	 * @return true if network Available otherwise false
	 */
	public static boolean isNetworkAvailable(Context context) {
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			return false;
		}

		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnected();
	}

	public static ArrayList<AppList> queryIntentActivities(Context c, String providerNames[], int providerLogos[]) {

		int index = 0;
		AppList info = null;
		ArrayList<AppList> listOfImageSharableApp = new ArrayList<AppList>();

		for (String menuItem : providerNames) {
			info = new AppList();
			info.appName = toTitleCase(menuItem);
			// Log.e("App name", "appName1 == " + menuItem);
			info.drawable = c.getResources().getDrawable(providerLogos[index++]);
			listOfImageSharableApp.add(info);
		}

		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("image/*");

		List<ResolveInfo> resInfo = c.getPackageManager().queryIntentActivities(shareIntent, 0);

		if (!resInfo.isEmpty()) {
			for (ResolveInfo resolveInfo : resInfo) {
				String packageName = resolveInfo.activityInfo.packageName;
				Log.e("Package Name", packageName);
				Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
				targetedShareIntent.setType("image/*");
				AppList pInfo = null;
				if ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
					targetedShareIntent.setPackage(packageName);
					pInfo = new AppList();
				} else {
					if (!isInProvidersList(packageName)) {
						targetedShareIntent.setPackage(packageName);
						pInfo = new AppList();
					}
				}

				if (pInfo != null) {
					Drawable appIcon = resolveInfo.activityInfo.applicationInfo.loadIcon(c.getPackageManager());
					String appNAme = resolveInfo.activityInfo.applicationInfo.loadLabel(c.getPackageManager())
							.toString();
					pInfo.drawable = appIcon;
					pInfo.appName = appNAme;
					// Log.e("App name", "appName2 == " + appNAme);
					pInfo.intent = targetedShareIntent;
					listOfImageSharableApp.add(pInfo);
				}
			}
		}
		return listOfImageSharableApp;
	}

	public static boolean isInProvidersList(String packageName) {
		Log.e("Package Name remove", packageName);
		boolean temp = false;
		for (String providerKey : providerList) {
			if (packageName.contains(providerKey)) {
				Log.e("Provider Key", providerKey);
				return temp = true;
			}
		}
		return temp;
	}

	public static String toTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;

		for (char c : input.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}

			titleCase.append(c);
		}

		return titleCase.toString();
	}

	/**
	 * Function to print screen resolution, screen inches and density of android
	 * device.
	 * 
	 * @param ctx
	 *            Activity Context
	 */

	@SuppressWarnings("deprecation")
	public static void getDisplayDpi(Context ctx) {

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);

		double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		double screenInches = Math.sqrt(x + y);
		int screenInch = (int) Math.round(screenInches);
		int dapi = dm.densityDpi;

		Log.d("Resolution X", String.valueOf(width));
		Log.d("Resolution Y", String.valueOf(height));
		Log.d("screeninch", String.valueOf(screenInch));
		Log.d("dapi", String.valueOf(dapi));

		try {
			switch (dm.densityDpi) {

			case DisplayMetrics.DENSITY_LOW:

				UI_DENSITY = 120;

				if (screenInch <= 7) {
					UI_SIZE = 4;
					UI_YAHOO_ALLOW = 125;

				} else {
					UI_SIZE = 10;
				}

				break;
			case DisplayMetrics.DENSITY_MEDIUM:

				UI_DENSITY = 160;

				if (screenInch <= 7) {

					// For devices having width 320
					if (width == 320) {
						UI_YAHOO_ALLOW = 105;
						UI_SIZE = 3;
					} else if (width == 480) {
						UI_YAHOO_ALLOW = 200;
						UI_SIZE = 4;
					} else {
						UI_YAHOO_ALLOW = 1;
						UI_SIZE = 7;
					}
				} else {
					UI_SIZE = 10;
					UI_YAHOO_ALLOW = 1;
				}

				break;

			case DisplayMetrics.DENSITY_HIGH:

				UI_DENSITY = 240;
				UI_YAHOO_ALLOW = 375;

				break;
			case DisplayMetrics.DENSITY_XHIGH:
				UI_DENSITY = 320;

				if (screenInch < 7) {
					UI_YAHOO_ALLOW = 55;
				} else {
					if (width >= 720 && width < 1280) {
						UI_SIZE = 7;
						UI_YAHOO_ALLOW = 475;
					} else if (width >= 1280) {
						UI_SIZE = 10;
						UI_YAHOO_ALLOW = 1;
					} else {
						UI_YAHOO_ALLOW = 1;
					}
				}

				break;

			case 213:
				UI_DENSITY = 213;
				UI_YAHOO_ALLOW = 95;

			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
