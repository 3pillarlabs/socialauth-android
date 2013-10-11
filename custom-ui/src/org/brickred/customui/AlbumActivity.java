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

import org.brickred.customadapter.AlbumsAdapter;
import org.brickred.customadapter.ImageLoader;
import org.brickred.customadapter.PhotoAdapter;
import org.brickred.socialauth.Album;
import org.brickred.socialauth.Photo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * Activity to show Albums and Photos of Provider
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * 
 */

public class AlbumActivity extends Activity {

	// SocialAuth Component
	List<Album> albumList;

	// Android Components
	private LinearLayout dataSectionView, frameView;
	TextView textView;

	// Other Components
	String providerName;
	boolean photoListFlag = false;
	boolean photoFlag = false;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_grid);
		textView = (TextView) findViewById(R.id.albumTitle);
		dataSectionView = (LinearLayout) this.findViewById(R.id.dataSection);
		frameView = (LinearLayout) AlbumActivity.this.findViewById(R.id.frame);
		albumList = (ArrayList<Album>) getIntent().getSerializableExtra("album");
		getData(albumList);
	}

	// To set albums in gridview
	public void getData(final List<Album> albumList) {
		final GridView view = new GridView(this);
		getGridProperties(view);

		view.setAdapter(new AlbumsAdapter(AlbumActivity.this, 0, albumList));

		String[] urls = new String[albumList.size()];
		for (int i = 0; i < albumList.size(); i++) {
			urls[i] = albumList.get(i).getCoverPhoto();
		}

		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				showPhoto(view, albumList, position);
				photoListFlag = true;
			}
		});
		dataSectionView.addView(view);
	}

	/**
	 * To load photos in Gridview
	 * 
	 * @param gridview
	 *            gridview
	 * @param albumList
	 *            albums list of provider
	 * @param position
	 *            Clicking position of provider
	 */

	public void showPhoto(final GridView gridView, final List<Album> albumList, int position) {

		Album bean = albumList.get(position);
		Log.d("Custom-UI", "Album Clicked");

		final List<Photo> photoList = bean.getPhotos();
		PhotoAdapter photoAdapter = new PhotoAdapter(AlbumActivity.this, 0, photoList);
		gridView.setAdapter(photoAdapter);
		textView.setText(bean.getName());

		final ImageLoader imageLoader = new ImageLoader(AlbumActivity.this);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				Toast.makeText(AlbumActivity.this, "Loading Photo..... Please Wait", Toast.LENGTH_SHORT).show();
				Log.d("Custom-UI", "Photo Clicked");
				dataSectionView.setVisibility(View.GONE);
				photoListFlag = false;
				photoFlag = true;
				Photo photoBean = photoList.get(position);

				ImageView picture = (ImageView) AlbumActivity.this.findViewById(R.id.picture);
				TextView pictureTitle = (TextView) AlbumActivity.this.findViewById(R.id.pictureTitle);
				frameView.setVisibility(View.VISIBLE);
				imageLoader.DisplayImage(photoBean.getLargeImage(), picture);

				pictureTitle.setText(photoBean.getTitle());

			}
		});
	}

	/**
	 * collection of grid properties
	 */
	@SuppressWarnings("static-access")
	public void getGridProperties(GridView view) {
		view.setNumColumns(3);
		view.setVerticalSpacing(5);
		view.setScrollBarStyle(view.SCROLLBARS_OUTSIDE_OVERLAY);
		view.setScrollingCacheEnabled(false);
		view.setGravity(Gravity.TOP);
		view.setSelector(new ColorDrawable(Color.parseColor("#00000000")));
		view.setClipChildren(true);
		view.setPadding(5, 5, 5, 5);
	}

	public void clearView() {
		dataSectionView.removeAllViews();
		dataSectionView.invalidate();
	}

	@Override
	public void onBackPressed() {

		if (photoListFlag) {
			clearView();
			getData(albumList);
			photoListFlag = false;
		} else if (photoFlag) {
			getData(albumList);
			frameView.setVisibility(View.GONE);
			dataSectionView.setVisibility(View.VISIBLE);
			photoFlag = false;
			photoListFlag = true;
		} else
			finish();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}