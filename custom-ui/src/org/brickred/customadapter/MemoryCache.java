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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Handles MemoryCache
 * 
 * @author Vineet Aggarwal
 * 
 */

public class MemoryCache {

	private static final String TAG = "MemoryCache";
	private final Map<String, Bitmap> cache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(10, 1.5f,
			true));// Last
					// argument
					// true
					// for
					// LRU
					// ordering
	private long size = 0;// current allocated size
	private long limit = 1000000;// max memory in bytes

	public MemoryCache() {
		// use 50% of available heap size
		setLimit(Runtime.getRuntime().maxMemory() / 4);
	}

	public void setLimit(long new_limit) {
		limit = new_limit;
		Log.i(TAG, "MemoryCache will use up to " + limit / 1024. / 1024. + "MB");
	}

	public Bitmap get(String id) {
		try {
			if (!cache.containsKey(id))
				return null;
			// NullPointerException sometimes happen here
			// http://code.google.com/p/osmdroid/issues/detail?id=78
			return cache.get(id);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public void put(String id, Bitmap bitmap) {
		try {
			if (cache.containsKey(id))
				size -= getSizeInBytes(cache.get(id));
			cache.put(id, bitmap);
			size += getSizeInBytes(bitmap);
			checkSize();
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	private void checkSize() {
		Log.i(TAG, "cache size=" + size + " length=" + cache.size());
		if (size > limit) {
			Iterator<Entry<String, Bitmap>> iter = cache.entrySet().iterator();// least
																				// recently
																				// accessed
																				// item
																				// will
																				// be
																				// the
																				// first
																				// one
																				// iterated
			while (iter.hasNext()) {
				Entry<String, Bitmap> entry = iter.next();
				size -= getSizeInBytes(entry.getValue());
				iter.remove();
				if (size <= limit)
					break;
			}
			Log.i(TAG, "Clean cache. New size " + cache.size());
		}
	}

	public void clear() {
		try {
			// NullPointerException sometimes happen here
			// http://code.google.com/p/osmdroid/issues/detail?id=78
			cache.clear();
			size = 0;
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	long getSizeInBytes(Bitmap bitmap) {
		if (bitmap == null)
			return 0;
		return bitmap.getRowBytes() * bitmap.getHeight();
	}
}