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

package org.brickred.customadapter;

import org.brickred.customui.CustomUI;
import org.brickred.customui.R;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * CustomAdapter for showing List. On clicking any item , it calls authorize()
 * method to authenticate provider
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 */

public class CustomAdapter extends BaseAdapter {
	// Android Components
	private final LayoutInflater mInflater;
	private final Context ctx;
	private Bitmap mIcon;

	// SocialAuth Components
	SocialAuthAdapter adapter;
	private final Provider[] providers = new Provider[] { Provider.FACEBOOK, Provider.TWITTER, Provider.LINKEDIN,
			Provider.MYSPACE, Provider.YAHOO, Provider.GOOGLE, Provider.SALESFORCE, Provider.FOURSQUARE,
			Provider.GOOGLEPLUS, Provider.YAMMER, Provider.RUNKEEPER, Provider.FLICKR, Provider.INSTAGRAM };
	private final int[] images = new int[] { R.drawable.facebook, R.drawable.twitter, R.drawable.linkedin,
			R.drawable.myspace, R.drawable.yahoo, R.drawable.google, R.drawable.salesforce, R.drawable.foursquare,
			R.drawable.googleplus, R.drawable.yammer, R.drawable.runkeeper, R.drawable.flickr, R.drawable.instagram };

	public CustomAdapter(Context context, SocialAuthAdapter mAdapter) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		ctx = context;
		mInflater = LayoutInflater.from(ctx);
		adapter = mAdapter;
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 */
	@Override
	public int getCount() {
		return providers.length;
	}

	/**
	 * Since the data comes from an array, just returning the index is sufficent
	 * to get at the data. If we were using a more complex data structure, we
	 * would return whatever object represents one row in the list.
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
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need to reinflate it. We only inflate a new View when the convertView
		// supplied by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.providers_list, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.providerText);
			holder.icon = (ImageView) convertView.findViewById(R.id.provider);
			holder.signText = (TextView) convertView.findViewById(R.id.signstatus);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		mIcon = BitmapFactory.decodeResource(ctx.getResources(), images[position]);

		// Bind the data efficiently with the holder.

		String textCase = providers[position].toString();
		textCase = String.valueOf(textCase.charAt(0)).toUpperCase() + textCase.substring(1, textCase.length());

		holder.text.setText(textCase);
		holder.icon.setImageBitmap(mIcon);

		holder.text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				CustomUI.pos = position;

				if (providers[position].equals(Provider.GOOGLE))
					adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
				else if (providers[position].equals(Provider.FOURSQUARE))
					adapter.addCallBack(Provider.FOURSQUARE,
							"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.SALESFORCE))
					adapter.addCallBack(Provider.SALESFORCE,
							"https://socialauth.in:8443/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.YAMMER))
					adapter.addCallBack(Provider.YAMMER,
							"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.FLICKR))
					adapter.addCallBack(Provider.FLICKR,
							"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
				else if (providers[position].equals(Provider.INSTAGRAM))
					adapter.addCallBack(Provider.INSTAGRAM,
							"http://opensource.brickred.com/socialauthdemo/socialAuthSuccessAction.do");

				// This method will enable the selected provider
				adapter.authorize(ctx, providers[position]);

			}
		});

		holder.signText.setText("Sign In");
		holder.signText.setTag(1);

		holder.signText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String text = (String) ((TextView) v).getText();

				if (text.equalsIgnoreCase("sign in")) {
					CustomUI.pos = position;

					// Use addCallback method if using
					// your own keys for FOURSQUARE , GOOGLE , SALESFORCE ,
					// YAMMER

					if (providers[position].equals(Provider.GOOGLE))
						adapter.addCallBack(Provider.GOOGLE, "http://socialauth.in/socialauthdemo");
					else if (providers[position].equals(Provider.FOURSQUARE))
						adapter.addCallBack(Provider.FOURSQUARE,
								"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");
					else if (providers[position].equals(Provider.SALESFORCE))
						adapter.addCallBack(Provider.SALESFORCE,
								"https://socialauth.in:8443/socialauthdemo/socialAuthSuccessAction.do");
					else if (providers[position].equals(Provider.YAMMER))
						adapter.addCallBack(Provider.YAMMER,
								"http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

					// This method will enable the selected provider
					adapter.authorize(ctx, providers[position]);
				} else if (text.equalsIgnoreCase("sign out")) {
					// Sign Out
					boolean status = adapter.signOut(ctx, providers[position].toString());
					Log.d("status", String.valueOf(status));
					if (status) {
						((TextView) v).setText("Sign In");
					}
				}
			}
		});

		return convertView;
	}

	class ViewHolder {
		TextView text;
		ImageView icon;
		TextView signText;
	}
} // End of customAdapter
