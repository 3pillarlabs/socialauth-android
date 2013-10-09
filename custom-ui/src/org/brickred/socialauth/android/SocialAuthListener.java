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

/**
 * Callback interface for dialog requests.
 * 
 * @author vineet.aggarwal@3pillarglobal.com
 * @author abhinav.maheswari@3pillarglobal.com
 */

public interface SocialAuthListener<T> {

	/**
	 * Called when data recived from server. Executed by the thread that
	 * initiated the dialog.
	 * 
	 * @param Bundle
	 *            containing response.
	 */
	public void onExecute(String provider, T t);

	/**
	 * Called when a dialog has an error. Executed by the thread that initiated
	 * the dialog.
	 */
	public void onError(SocialAuthError e);

}
