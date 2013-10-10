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

import java.util.ArrayList;
import java.util.List;

import org.brickred.socialauth.Feed;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.SocialAuthAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * Activity to show Feed list of Provider
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

public class FeedActivity extends Activity {

	// SocialAuth Components
	SocialAuthAdapter adapter;
	Profile profileMap;
	List<Feed> feedList;

	// Android Components
	ListView listview;
	AlertDialog dialog;
	TextView name;
	TextView displayName;
	TextView email;
	TextView location;
	TextView gender;
	TextView language;
	TextView birthday;
	TextView country;
	ImageView image;

	// Variables
	boolean status;
	public static int pos;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed);

		feedList = (ArrayList<Feed>) getIntent().getSerializableExtra("feed");

		ListView list = (ListView) findViewById(R.id.feedList);
		list.setAdapter(new MyCustomAdapter(this, R.layout.feed_list, feedList));

	}

	public class MyCustomAdapter extends ArrayAdapter<Feed> {
		private final LayoutInflater mInflater;
		List<Feed> feeds;

		public MyCustomAdapter(Context context, int textViewResourceId, List<Feed> feeds) {
			super(context, textViewResourceId);
			mInflater = LayoutInflater.from(context);
			this.feeds = feeds;
		}

		@Override
		public int getCount() {
			return feeds.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// return super.getView(position, convertView, parent);
			final Feed bean = feeds.get(position);

			View row = mInflater.inflate(R.layout.feed_list, parent, false);

			TextView label = (TextView) row.findViewById(R.id.fName);
			TextView email = (TextView) row.findViewById(R.id.fMsg);
			TextView date = (TextView) row.findViewById(R.id.fDate);

			Log.d("Custom-UI", "From = " + bean.getFrom());
			Log.d("Custom-UI", "Message = " + bean.getMessage());
			Log.d("Custom-UI", "Screen Name = " + bean.getScreenName());
			Log.d("Custom-UI", "Feed Id = " + bean.getId());
			Log.d("Custom-UI", "Created At = " + bean.getCreatedAt());

			label.setText("From : " + bean.getFrom());
			email.setText(bean.getMessage());
			date.setText(" , Created At : " + bean.getCreatedAt().toString());

			return row;
		}

	}
}