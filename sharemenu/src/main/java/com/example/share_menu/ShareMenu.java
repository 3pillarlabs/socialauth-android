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

package com.example.share_menu;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Main class of the ShareMenu Example for SocialAuth Android SDK. <br>
 * 
 * The main objective of this example is to access social media providers
 * Facebook, Twitter and others by using ShareActionProvider". User can use the
 * socialauth providers with native apps installed in the device.
 * 
 * The class first creates SocialAuthAdapter object and add providers<br>
 * 
 * Then it adds menu button and finally enables the providers by calling enable
 * method<br>
 * 
 * After successful authentication of provider, it receives the response in
 * responseListener and then update status by updatestatus() method
 * 
 * <br>
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

public class ShareMenu extends Activity {

	SocialAuthAdapter adapter;

	// Android Components
	Button update;
	EditText edit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Welcome Message
		TextView textview = (TextView) findViewById(R.id.text);
		textview.setText("Welcome to SocialAuth Demo. Connect any provider by clicking share icon in actionbar. Updatus status using textbox for socialauth providers,");

		adapter = new SocialAuthAdapter(new ResponseListener());
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
		adapter.addProvider(Provider.GOOGLE, R.drawable.google);
		adapter.addProvider(Provider.GOOGLEPLUS, R.drawable.googleplus);
		adapter.addProvider(Provider.MYSPACE, R.drawable.myspace);
		adapter.addProvider(Provider.RUNKEEPER, R.drawable.runkeeper);
		adapter.addProvider(Provider.YAHOO, R.drawable.yahoo);
		adapter.addProvider(Provider.YAMMER, R.drawable.yammer);
		adapter.addProvider(Provider.FOURSQUARE, R.drawable.foursquare);
		adapter.addProvider(Provider.FLICKR, R.drawable.flickr);
		adapter.addProvider(Provider.INSTAGRAM, R.drawable.instagram);

		// For twitter use add callback method. Put your own callback url here.
		adapter.addCallBack(Provider.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
		adapter.addCallBack(Provider.YAMMER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.share_menu, menu);
		final MenuItem item = menu.findItem(R.id.share_action);
		View actionView = item.getActionView().findViewById(R.id.imgbtnShare);
		adapter.enable(actionView);
		return true;
	}

	/**
	 * Listens Response from Library
	 * 
	 */

	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
			// Variable to receive message status
			Log.d("Share-Menu", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(ShareMenu.this, providerName + " connected", Toast.LENGTH_SHORT).show();

			update = (Button) findViewById(R.id.update);
			edit = (EditText) findViewById(R.id.editTxt);

			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.

			update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Call updateStatus to share message via oAuth providers
					adapter.updateStatus(edit.getText().toString(), new MessageListener(), false);
				}
			});
		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Menu", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Menu", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Menu", "Dialog Closed by pressing Back Key");

		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
				Toast.makeText(ShareMenu.this, "Message posted on " + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(ShareMenu.this, "Message not posted on" + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}
}
