
 ![TopImage](https://raw.github.com/wiki/3pillarlabs/socialauth-android/images/android.png)

 
SocialAuth Android is an Android version of popular SocialAuth Java library. Now you do not need to integrate multiple SDKs if you want to integrate your application with multiple social networks. You just need to add few lines of code after integrating the SocialAuth Android library in your app.
The API enables user authentication and sharing updates through different various social networks and hides all the intricacies of generating signatures & token, doing security handshakes and provide an easy mechanism to build cool social apps.
With this library, you can:
  * Quickly build share functionality for posting updates on facebook, twitter, linkedin and more
  * Easily create a Share button or a social bar containing various social networks
  *	Access profile of logged in user for easy user registration
  *	Import friend contacts of logged in user (Email, Profile URL and Name)
  *	Do much more using our flexible API like extend it for more network

###Whats new in Version 3.2 ?
   *	Bugs Solved :  Facebook issue solved. Now we are using native web view login. please check the wiki to create Facebook app using native flow.
   *    Bugs Solved :  Foursquare issue fixed
   *    Bugs Solved :  Signout bug fixed
   *    Bugs Solved :  Custom -UI example upload image bug fixed
   *    Documentation Guides for Facebook, Google Plus, Flickr , Share-Menu  and more 
    
###Whats new in Version 3.1 ?
   *	Bugs Solved : Twitter Recent API changes fixed

###Whats new in Version 3.0 ?
  *	New Providers Support : Instagram , Flickr
  *	New Example : Share- Menu - Now use provides in Android ShareAction Provider. Check wiki and example for use.
  *	Contacts : Support added for Google Plus, Flickr , Instagram
  *	Feeds : Support added for Google Plus, Instagram
  *	Albums : Support added for Google Plus. Download Picasa Albums
  *	Generic OAuth2 Provider : Users can create own oAuth2 Providers from sdk.
  *	Bugs Solved : Publish Story bug for Facebook Solved
  *	Bugs Solved : Get Profile Images for FourSquare
  *	Bugs Solved : UI issues for Yahoo , Yammer Solved

###Whats new in Version 2.6 ?
  *	Linkedin Career Plugin Added to show information for job , education , recommendations.
  *	Linkedin feed plugin added.
  *	Now get profile images for your contacts on Facebook , Twitter and !Linkedin.
  *	Now you can post message on all connected providers at once.
  *	Examples updated.
  *	Bug fixes

Check [Getting Started](https://github.com/3pillarlabs/socialauth-android/wiki/Getting-Started) to start.

###How does it Work?

Once SocialAuth Android is integrated into your application, following is the authentication process:

 * User opens the app and chooses the provider to request the authentication by using SocialAuth-android library.
 *  User is redirected to Facebook, Twitter or other provider's login site by library where they enter their credentials.
 *  Upon successful login, provider asks for user’s permission to share their basic data with your app.
 * Once user accepts it,On successful authentication the library redirects user to app.
 * Now user can call SocialAuth Android library to get information about user profile, gets contacts list or share status to friends.
 
![UserFlow](https://raw.github.com/wiki/3pillarlabs/socialauth-android/images/socialauthandroid-process.png)

SocialAuth Android is distrubuted under MIT License.

# About this project

![3Pillar Global] (http://www.3pillarglobal.com/wp-content/themes/base/library/images/logo_3pg.png)

**SocialAuth Android** is developed and maintained by [3Pillar Global](http://www.3pillarglobal.com/).

