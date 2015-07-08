# README #

#Setup Facebook login:#
* Ref: https://developers.facebook.com/
* get keyhash: 

keytool -exportcert -alias YOUR_RELEASE_KEY_ALIAS -keystore YOUR_RELEASE_KEY_PATH | openssl sha1 -binary | openssl base64

or use method: 

```
#!java
public static void getHashforFBLogin(Context context, String packageName){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("namenotfound", e.toString());

        } catch (NoSuchAlgorithmException e) {
            Log.e("nosuchalgorithmex", e.toString());
        }
    }
```
* Note: create app follow instruction of fb. Then:

Settings tab: 

```
#!html
- Add key hashes (for app use keytool and use method)
- Contact Email
- Save changes
```

Status & Review tab: 

```
#!html
- In line "
Do you want to make this app and all its live features available to the general public?" -> select YES
- Submission (check item: user_about_me)-> edit notes (choose other and type -> save)
```

App Details tab: 

```
#!html
- fill in Tagline, Long description, explanation for permissions
- Privacy Policy URL, User support Email
- Icons
- Save changes
```

In AndroidManifest.xml

```
#!xml
<meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id" />

        <activity
            android:name="com.facebook.FacebookActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
            android:label="@string/app_name"
            android:theme="@android:style/Theme.Translucent.NoTitleBar" />
        <provider
            android:name="com.facebook.FacebookContentProvider"
            android:authorities="com.facebook.app.FacebookContentProvider887309281312741"
            android:exported="true" />
```


#Setup Google+ login: #
* Ref: http://www.androidhive.info/2014/02/android-login-with-google-plus-account-1/
* Step:

1. Installing / updating Google Play Services.
2. Enabling G+ API on google console.

 * keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
 * Copy SHA1
 * Open Google APIs console (https://code.google.com/apis/console)
 * On the left, under APIs & auth section, click on APIs and on the right enable Google+ API service.
 * Now again on the left, click on Credentials and on the right, click on CREATE NEW CLIENT ID button. It will open a popup to configure a new client id.

 Importing Play Services Library
Permissions:

```
#!xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<uses-permission android:name="android.permission.USE_CREDENTIALS" />
```
Meta-data:

```
#!xml
<meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />

```




* Note: with setlogin gg in fagment, must be declare onActivityResult method in MainActivity, then ref to ActivityResult method in fragment.

MainActivity
```
#!java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof SignInFragment) {
            ((SignInFragment)fragment).onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
```
In fragment
```
#!java
    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != getActivity().RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
        callbackManager.onActivityResult(requestCode, responseCode, intent);
    }
```