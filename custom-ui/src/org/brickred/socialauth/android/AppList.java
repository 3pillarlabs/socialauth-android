package org.brickred.socialauth.android;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class AppList {

	public Drawable drawable;
	public String appName;
	Intent intent;

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}
}
