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

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * Adapter for creating menu of social networks for Share Button
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

public class PopUpListAdapter extends BaseAdapter {
	private final Context ctx;
	ArrayList<AppList> data;
	int[] imagesdata;

	public PopUpListAdapter(Context context, ArrayList<AppList> appList) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		ctx = context;
		data = appList;
	}

	/**
	 * The number of items in the list is determined by the number of speeches
	 * in our array.
	 */
	@Override
	public int getCount() {
		return data.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView text;

		if (convertView == null) {
			text = new TextView(ctx);
		} else {
			text = (TextView) convertView;
		}

		AppList bean = data.get(position);
		text.setText(bean.appName);
		bean.drawable.setBounds(0, 0, 60, 60);

		text.setCompoundDrawables(bean.drawable, null, null, null);
		text.setTextSize(18);
		text.setBackgroundColor(Color.parseColor("#303030"));
		text.setPadding(18, 18, 18, 18);
		text.setTextColor(Color.WHITE);
		text.setCompoundDrawablePadding(20);
		text.setGravity(Gravity.CENTER_VERTICAL);

		return text;
	}
}