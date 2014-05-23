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

import android.content.Context;
import org.brickred.socialauth.SocialAuthManager;

/**
 * Factorial class for SocialAuth's components. Enables users to provide their
 * own implementation for various UI components which are used in the library.
 *
 * @author Noor Dawod <github@fineswap.com>
 */
public class SocialAuthFactory {

	/**
	 * Implementation for presenting Provider's pages.
	 */
	abstract public static class InteractivePage {

		private String url;
		private SocialAuthAdapter.Provider provider;
		private DialogListener listener;
		private SocialAuthManager manager;
		private boolean titleStatus;

		abstract public Context getContext();
		abstract public void show();
		abstract public void dismiss();

		void setURL(String url) {
			this.url = url;
		}

		/**
		 * Gets the URL of the page.
		 *
		 * @return URL of current page
		 */
		public String getURL() {
			return url;
		}

		void setProvider(SocialAuthAdapter.Provider provider) {
			this.provider = provider;
		}

		/**
		 * Gets the provider of this page.
		 *
		 * @return Provider of current page
		 */
		public SocialAuthAdapter.Provider getProvider() {
			return provider;
		}

		void setListener(DialogListener listener) {
			this.listener = listener;
		}

		/**
		 * Gets the configured listener for this page.
		 *
		 * @return Listener for this page.
		 */
		public DialogListener getListener() {
			return listener;
		}

		void setAuthManager(SocialAuthManager manager) {
			this.manager = manager;
		}

		/**
		 * Gets the configured authentication manager for this page.
		 *
		 * @return Authentication manager for current page
		 */
		public SocialAuthManager getAuthManager() {
			return manager;
		}

		public void setTitleVisibility(boolean titleStatus) {
			this.titleStatus = titleStatus;
		}

		public boolean isTitleVisible() {
			return titleStatus;
		}

	}

}
