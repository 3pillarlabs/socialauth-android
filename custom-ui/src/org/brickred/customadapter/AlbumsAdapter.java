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
import org.brickred.socialauth.Album;
import org.brickred.socialauth.Photo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * Adapter for creating Photo Albums
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

public class AlbumsAdapter extends ArrayAdapter<Album> {

	// SocialAuth Components
	List<Album> albums;
	List<Photo> photos;

	// Android Components
	Context context;
	LayoutInflater mInflater;

	// Other Components
	AlbumHolder albumHolder;
	ImageLoader imageLoader;

	public AlbumsAdapter(Context context, int textViewResourceId, List<Album> albums) {
		super(context, textViewResourceId);
		this.albums = albums;
		this.context = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}

	@Override
	public int getCount() {
		return albums.size();
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {

		final Album bean = albums.get(position);

		if (row == null) {
			row = mInflater.inflate(R.layout.albumitem, parent, false);

			albumHolder = new AlbumHolder();

			albumHolder.coverImage = (ImageView) row.findViewById(R.id.coveralbum);

			albumHolder.albumName = (TextView) row.findViewById(R.id.albumname);
			albumHolder.photoCount = (TextView) row.findViewById(R.id.photocount);

			row.setTag(albumHolder);
		} else {
			albumHolder = (AlbumHolder) row.getTag();
		}
		Log.d("LifeView ", "Cover Photo = " + bean.getCoverPhoto());

		imageLoader.DisplayImage(bean.getCoverPhoto(), albumHolder.coverImage);

		Log.d("LifeView ", "Album Name = " + bean.getName());
		albumHolder.albumName.setText(bean.getName());

		Log.d("LifeView ", "Photos Count = " + bean.getPhotosCount());
		albumHolder.photoCount.setText(String.valueOf(bean.getPhotosCount()) + " Photos");

		return row;
	}

	static class AlbumHolder {
		ImageView coverImage;
		TextView albumName;
		TextView photoCount;
	}
}
