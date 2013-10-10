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

package org.brickred.customui;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.brickred.customadapter.CustomAdapter;
import org.brickred.socialauth.Album;
import org.brickred.socialauth.Career;
import org.brickred.socialauth.Contact;
import org.brickred.socialauth.Feed;
import org.brickred.socialauth.Photo;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Main class of the CustomUI Example for SocialAuth Android SDK. <br>
 * 
 * The main objective of this example is to access social media providers
 * Facebook, Twitter and others by creating your own UI.
 * 
 * Here we are creating a ListView. The ListView contains list of all providers
 * On clicking any provider, it authorizes the provider by calling authorize
 * method.
 * 
 * After successful authentication of provider, it receives the response in
 * responseListener and then shows a dialog containing options for getting user
 * profile, for updating status, to get contact list, to get feeds , to upload
 * image and to get albums <br>
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

// Please see strings.xml for list values

public class CustomUI extends Activity {

	// SocialAuth Components
	private static SocialAuthAdapter adapter;
	Profile profileMap;
	List<Photo> photosList;

	// Android Components
	ListView listview;
	AlertDialog dialog;
	TextView title;
	ProgressDialog mDialog;

	// Variables
	boolean status;
	String providerName;
	public static int pos;
	private static final int SELECT_PHOTO = 100;
	public static Bitmap bitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Adapter initialization
		adapter = new SocialAuthAdapter(new ResponseListener());

		// Set title
		title = (TextView) findViewById(R.id.textview);
		title.setText(R.string.app_name);

		listview = (ListView) findViewById(R.id.listview);
		listview.setAdapter(new CustomAdapter(this, adapter));
	}

	public static SocialAuthAdapter getSocialAuthAdapter() {
		return adapter;
	}

	// To receive the response after authentication
	private final class ResponseListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {

			Log.d("Custom-UI", "Successful");

			// Changing Sign In Text to Sign Out
			View v = listview.getChildAt(pos - listview.getFirstVisiblePosition());
			TextView pText = (TextView) v.findViewById(R.id.signstatus);
			pText.setText("Sign Out");

			// Get the provider
			providerName = values.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Custom-UI", "providername = " + providerName);

			Toast.makeText(CustomUI.this, providerName + " connected", Toast.LENGTH_SHORT).show();

			int res = getResources().getIdentifier(providerName + "_array", "array", CustomUI.this.getPackageName());

			AlertDialog.Builder builder = new AlertDialog.Builder(CustomUI.this);
			builder.setTitle("Select Options");
			builder.setCancelable(true);
			builder.setIcon(android.R.drawable.ic_menu_more);

			mDialog = new ProgressDialog(CustomUI.this);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.setMessage("Loading...");

			builder.setSingleChoiceItems(new DialogAdapter(CustomUI.this, R.layout.provider_options, getResources()
					.getStringArray(res)), 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int item) {

					Events(item, providerName);
					dialog.dismiss();
				}
			});
			dialog = builder.create();
			dialog.show();

		}

		@Override
		public void onError(SocialAuthError error) {
			Log.d("Custom-UI", "Error");
			error.printStackTrace();
		}

		@Override
		public void onCancel() {
			Log.d("Custom-UI", "Cancelled");
		}

		@Override
		public void onBack() {
			Log.d("Custom-UI", "Dialog Closed by pressing Back Key");

		}
	}

	// Method to handle events of providers
	public void Events(int position, final String provider) {

		switch (position) {
		case 0: // Code to print user profile details for all providers
		{
			mDialog.show();
			adapter.getUserProfileAsync(new ProfileDataListener());
			break;
		}

		case 1: {
			// Share Update : Facebook, Twitter, Linkedin, Yahoo,
			// MySpace,Yammer

			// Get Contacts for FourSquare, Google, Google Plus,
			// Flickr, Instagram

			// Dismiss Dialog for Runkeeper and SalesForce

			if (provider.equalsIgnoreCase("foursquare") || provider.equalsIgnoreCase("google")
					|| provider.equalsIgnoreCase("flickr") || provider.equalsIgnoreCase("googleplus")
					|| provider.equalsIgnoreCase("instagram")) {
				mDialog.show();
				adapter.getContactListAsync(new ContactDataListener());

			} else if (provider.equalsIgnoreCase("runkeeper") || provider.equalsIgnoreCase("salesforce")) {
				dialog.dismiss();

			} else {

				// Code to Post Message for all providers
				final Dialog msgDialog = new Dialog(CustomUI.this);
				msgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				msgDialog.setContentView(R.layout.dialog);

				TextView dialogTitle = (TextView) msgDialog.findViewById(R.id.dialogTitle);
				dialogTitle.setText("Share Update");
				final EditText edit = (EditText) msgDialog.findViewById(R.id.editTxt);
				Button update = (Button) msgDialog.findViewById(R.id.update);

				msgDialog.show();

				update.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						msgDialog.dismiss();
						adapter.updateStatus(edit.getText().toString(), new MessageListener(), false);
					}
				});
			}
			break;
		}

		case 2: {

			// Get Contacts : Facebook, Twitter, Linkedin, Yahoo,
			// MySpace,Yammer

			// Get Feeds : Google Plus, Instagram
			// Dismiss Dialog for FourSquare , Google, Flickr

			if (provider.equalsIgnoreCase("foursquare") || provider.equalsIgnoreCase("google")
					|| provider.equalsIgnoreCase("flickr")) {
				// Close Dialog
				dialog.dismiss();
			} else if (provider.equalsIgnoreCase("instagram") || provider.equalsIgnoreCase("googleplus")) {
				mDialog.show();
				adapter.getFeedsAsync(new FeedDataListener());
			} else {
				// Get Contacts for Remaining Providers
				mDialog.show();
				adapter.getContactListAsync(new ContactDataListener());
			}
			break;
		}

		case 3: {
			// Get Feeds : For Facebook , Twitter, Linkedin
			// Get Albums : Google Plus
			// Dismiss Dialog: Rest

			if (provider.equalsIgnoreCase("facebook") || provider.equalsIgnoreCase("twitter")
					|| provider.equalsIgnoreCase("linkedin")) {
				mDialog.show();
				adapter.getFeedsAsync(new FeedDataListener());
			} else if (provider.equalsIgnoreCase("googleplus")) {
				mDialog.show();
				adapter.getAlbumsAsync(new AlbumDataListener());
			} else {
				dialog.dismiss();
			}
			break;
		}

		case 4: {
			// Upload Image for Facebook and Twitter

			if (provider.equalsIgnoreCase("facebook") || provider.equalsIgnoreCase("twitter")) {

				// Code to Post Message for all providers
				final Dialog imgDialog = new Dialog(CustomUI.this);
				imgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				imgDialog.setContentView(R.layout.dialog);
				imgDialog.setCancelable(true);

				TextView dialogTitle = (TextView) imgDialog.findViewById(R.id.dialogTitle);
				dialogTitle.setText("Share Image");
				final EditText edit = (EditText) imgDialog.findViewById(R.id.editTxt);
				Button update = (Button) imgDialog.findViewById(R.id.update);
				update.setVisibility(View.INVISIBLE);
				Button getImage = (Button) imgDialog.findViewById(R.id.loadImage);
				getImage.setVisibility(View.VISIBLE);
				imgDialog.show();

				getImage.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						// Taking image from phone gallery
						Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, SELECT_PHOTO);

						if (bitmap != null) {
							mDialog.show();
							try {
								adapter.uploadImageAsync(edit.getText().toString(), "icon.png", bitmap, 0,
										new UploadImageListener());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						imgDialog.dismiss();
					}
				});

			} else if (provider.equalsIgnoreCase("linkedin")) {

				// get Job and Education information
				mDialog.show();
				adapter.getCareerAsync(new CareerListener());

			} else {
				dialog.dismiss();
			}
			break;
		}

		case 5: {
			// Get Albums for Facebook and Twitter

			if (provider.equalsIgnoreCase("facebook") || provider.equalsIgnoreCase("twitter")) {
				mDialog.show();
				adapter.getAlbumsAsync(new AlbumDataListener());
			} else {
				dialog.dismiss();
			}
			break;
		}

		case 6: {
			// For share text with link preview
			if (provider.equalsIgnoreCase("facebook")) {
				try {
					adapter.updateStory(
							"Hello SocialAuth Android" + System.currentTimeMillis(),
							"Google SDK for Android",
							"Build great social apps and get more installs.",
							"The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.",
							"https://www.facebook.com", "http://carbonfreepress.gr/images/facebook.png",
							new MessageListener());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				dialog.dismiss();
			}
			break;
		}

		case 7: {
			// Dismiss Dialog
			dialog.dismiss();
			break;
		}

		}

	}

	// To receive the profile response after authentication
	private final class ProfileDataListener implements SocialAuthListener<Profile> {

		@Override
		public void onExecute(String provider, Profile t) {

			Log.d("Custom-UI", "Receiving Data");
			mDialog.dismiss();
			Profile profileMap = t;

			Intent intent = new Intent(CustomUI.this, ProfileActivity.class);
			intent.putExtra("provider", provider);
			intent.putExtra("profile", profileMap);
			startActivity(intent);
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	// To get status of message after authentication
	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
			Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
				Toast.makeText(CustomUI.this, "Message posted on" + provider, Toast.LENGTH_LONG).show();
			else
				Toast.makeText(CustomUI.this, "Message not posted" + provider, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	// To receive the album response after authentication
	private final class AlbumDataListener implements SocialAuthListener<List<Album>> {

		@Override
		public void onExecute(String provider, List<Album> t) {

			Log.d("Custom-UI", "Receiving Data");
			mDialog.dismiss();
			List<Album> albumList = t;

			if (albumList != null && albumList.size() > 0) {
				Intent intent = new Intent(CustomUI.this, AlbumActivity.class);
				intent.putExtra("album", (Serializable) albumList);
				startActivity(intent);
			} else {
				Log.d("Custom-UI", "Album List Empty");
			}
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	// To receive the contacts response after authentication
	private final class ContactDataListener implements SocialAuthListener<List<Contact>> {

		@Override
		public void onExecute(String provider, List<Contact> t) {

			Log.d("Custom-UI", "Receiving Data");
			mDialog.dismiss();
			List<Contact> contactsList = t;

			if (contactsList != null && contactsList.size() > 0) {
				Intent intent = new Intent(CustomUI.this, ContactActivity.class);
				intent.putExtra("provider", provider);
				intent.putExtra("contact", (Serializable) contactsList);
				startActivity(intent);
			} else {
				Log.d("Custom-UI", "Contact List Empty");
			}
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	// To get status of image upload after authentication
	private final class UploadImageListener implements SocialAuthListener<Integer> {

		@Override
		public void onExecute(String provider, Integer t) {
			mDialog.dismiss();
			Integer status = t;
			Log.d("Custom-UI", String.valueOf(status));
			if (status.intValue() == 200 || status.intValue() == 201 || status.intValue() == 204)
				Toast.makeText(CustomUI.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(CustomUI.this, "Image not Uploaded", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}

	// To receive the feed response after authentication
	private final class FeedDataListener implements SocialAuthListener<List<Feed>> {

		@Override
		public void onExecute(String provider, List<Feed> t) {

			Log.d("Custom-UI", "Receiving Data");
			mDialog.dismiss();
			List<Feed> feedList = t;

			if (feedList != null && feedList.size() > 0) {
				Intent intent = new Intent(CustomUI.this, FeedActivity.class);
				intent.putExtra("feed", (Serializable) feedList);
				startActivity(intent);
			} else {
				Log.d("Custom-UI", "Feed List Empty");
			}
		}

		@Override
		public void onError(SocialAuthError e) {
		}
	}

	// To receive the feed response after authentication
	private final class CareerListener implements SocialAuthListener<Career> {

		@Override
		public void onExecute(String provider, Career t) {

			Log.d("Custom-UI", "Receiving Data");
			mDialog.dismiss();
			Career careerMap = t;
			Intent intent = new Intent(CustomUI.this, CareerActivity.class);
			intent.putExtra("provider", provider);
			intent.putExtra("career", careerMap);
			startActivity(intent);
		}

		@Override
		public void onError(SocialAuthError e) {
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
					bitmap = BitmapFactory.decodeStream(imageStream);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * CustomAdapter for showing List. On clicking any item , it calls
	 * authorize() method to authenticate provider
	 */

	public class DialogAdapter extends BaseAdapter {
		// Android Components
		private final LayoutInflater mInflater;
		private final Context ctx;
		private Drawable mIcon;
		String[] drawables;
		String[] options;

		public DialogAdapter(Context context, int textViewResourceId, String[] providers) {
			// Cache the LayoutInflate to avoid asking for a new one each time.
			ctx = context;
			mInflater = LayoutInflater.from(ctx);
			options = providers;
		}

		/**
		 * The number of items in the list is determined by the number of
		 * speeches in our array.
		 */
		@Override
		public int getCount() {
			return options.length;
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 */
		@Override
		public Object getItem(int position) {
			return position;
		}

		/**
		 * Use the array index as a unique id.
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 * 
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// A ViewHolder keeps references to children views to avoid
			// unneccessary
			// calls to findViewById() on each row.
			ViewHolder holder;

			// When convertView is not null, we can reuse it directly, there is
			// no
			// need to reinflate it. We only inflate a new View when the
			// convertView
			// supplied by ListView is null.
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.provider_options, null);

				// Creates a ViewHolder and store references to the two children
				// views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.text = (TextView) convertView.findViewById(R.id.providerText);
				holder.icon = (ImageView) convertView.findViewById(R.id.provider);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			String drawables[] = ctx.getResources().getStringArray(R.array.drawable_array);

			mIcon = ctx.getResources().getDrawable(
					ctx.getResources().getIdentifier(drawables[position], "drawable", ctx.getPackageName()));

			// Bind the data efficiently with the holder
			holder.text.setText(options[position]);
			if (options[position].equalsIgnoreCase("career"))
				holder.icon.setImageResource(R.drawable.career);
			else
				holder.icon.setImageDrawable(mIcon);

			return convertView;
		}

		class ViewHolder {
			TextView text;
			ImageView icon;
		}
	}
}