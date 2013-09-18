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

package org.brickred.socialbar;

import java.io.File;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Main class of the ShareBar Example for SocialAuth Android SDK. <br>
 * 
 * The main objective of this example is to create a bar of social media
 * providers Facebook, Twitter and others. It enables user to access the
 * respective provider on single click and update the status.
 * 
 * The class first creates a bar in main.xml. It then adds bar to SocialAuth
 * Android Library <br>
 * 
 * Then it adds providers Facebook, Twitter and others to library object by
 * addProvider method and finally enables the providers by calling enable method<br>
 * 
 * After successful authentication of provider, it receives the response in
 * responseListener and then automatically update status by updatestatus()
 * method.
 * 
 * It's Primarly use is to share message but developers can use to access other
 * functionalites like getting profile , contacts , sharing images etc.
 * 
 * Now you can use share -bar to share message via email and mms.See example
 * below <br>
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

public class ShareBarActivity extends Activity {

	// SocialAuth Component
	SocialAuthAdapter adapter;
	boolean status;

	// Android Components
	Button update;
	EditText edit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Welcome Message
		TextView textview = (TextView) findViewById(R.id.text);
		textview.setText("Welcome to SocialAuth Demo. Connect any provider and then press Update button to Share Update.");

		LinearLayout bar = (LinearLayout) findViewById(R.id.linearbar);
		bar.setBackgroundResource(R.drawable.bar_gradient);

		// Add Bar to library
		adapter = new SocialAuthAdapter(new ResponseListener());

		// Please note : Update status functionality is only supported by
		// Facebook, Twitter, Linkedin, MySpace, Yahoo and Yammer.

		// Add providers
		adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
		adapter.addProvider(Provider.TWITTER, R.drawable.twitter);
		adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);
		adapter.addProvider(Provider.MYSPACE, R.drawable.myspace);

		// Add email and mms providers
		adapter.addProvider(Provider.EMAIL, R.drawable.email);
		adapter.addProvider(Provider.MMS, R.drawable.mms);

		// For twitter use add callback method. Put your own callback url here.
		adapter.addCallBack(Provider.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

		adapter.enable(bar);

	}

	/**
	 * Listens Response from Library
	 * 
	 */

	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {

			// Variable to receive message status
			Log.d("Share-Bar", "Authentication Successful");

			// Get name of provider after authentication
			final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			Toast.makeText(ShareBarActivity.this, providerName + " connected", Toast.LENGTH_SHORT).show();

			update = (Button) findViewById(R.id.update);
			edit = (EditText) findViewById(R.id.editTxt);

			// Please avoid sending duplicate message. Social Media Providers
			// block duplicate messages.

			update.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Call updateStatus to share message via oAuth providers
					// adapter.updateStatus(edit.getText().toString(), new
					// MessageListener(), false);

					// call to update on all connected providers at once
					adapter.updateStatus(edit.getText().toString(), new MessageListener(), true);
				}
			});

			// Share via Email Intent
			if (providerName.equalsIgnoreCase("share_mail")) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
						"vineet.aggarwal@3pillarglobal.com", null));
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Test");
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
						"image5964402.png");
				Uri uri = Uri.fromFile(file);
				emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
				startActivity(Intent.createChooser(emailIntent, "Test"));
			}

			// Share via mms intent
			if (providerName.equalsIgnoreCase("share_mms")) {
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
						"image5964402.png");
				Uri uri = Uri.fromFile(file);

				Intent mmsIntent = new Intent(Intent.ACTION_SEND, uri);
				mmsIntent.putExtra("sms_body", "Test");
				mmsIntent.putExtra(Intent.EXTRA_STREAM, uri);
				mmsIntent.setType("image/png");
				startActivity(mmsIntent);
			}
		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
			Log.d("Share-Bar", error.getMessage());
		}

		@Override
		public void onCancel() {
			Log.d("Share-Bar", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
				Toast.makeText(ShareBarActivity.this, "Message posted on " + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(ShareBarActivity.this, "Message not posted on" + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}
}