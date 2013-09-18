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

import java.util.List;

import org.brickred.customui.R;
import org.brickred.socialauth.Photo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

/**
 * 
 * Adapter for loading Photos of particular Album
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

public class PhotoAdapter extends ArrayAdapter<Photo> {

	// SocialAuth Components
	List<Photo> photos;

	// Android Components
	LayoutInflater mInflater;
	Context context;

	// Other Components
	PhotoHolder photoHolder;
	ImageLoader imageLoader;

	public PhotoAdapter(Context context, int textViewResourceId, List<Photo> photos) {
		super(context, textViewResourceId);
		this.photos = photos;
		this.context = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return photos.size();
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {

		final Photo bean = photos.get(position);

		if (row == null) {
			row = mInflater.inflate(R.layout.photoitem, null);

			photoHolder = new PhotoHolder();

			photoHolder.photoThumbnail = (ImageView) row.findViewById(R.id.photoThumbnail);
			row.setTag(photoHolder);
		} else {
			photoHolder = (PhotoHolder) row.getTag();
		}

		if (bean.getTitle() != null)
			Log.d("LifeView", "Photo Title = " + bean.getTitle());

		imageLoader.DisplayImage(bean.getSmallImage(), photoHolder.photoThumbnail);

		return row;
	}

	static class PhotoHolder {
		ImageView photoThumbnail;
	}
}
