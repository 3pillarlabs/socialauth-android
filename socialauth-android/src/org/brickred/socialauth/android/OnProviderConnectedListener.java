package org.brickred.socialauth.android;

interface iOnProviderRestoreListener{
	public void onConnected();
	public void onError(SocialAuthError e);
	public void onNotAvailable();
}

public abstract class OnProviderConnectedListener implements iOnProviderRestoreListener{
	
	public abstract void onConnected();
	
	public abstract void onError(SocialAuthError e);
	
	public abstract void onNotAvailable();
}
