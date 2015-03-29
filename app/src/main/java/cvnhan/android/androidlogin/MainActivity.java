package cvnhan.android.androidlogin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;

public class MainActivity extends Activity {

    private LoginButton loginButton;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info = getPackageManager().getPackageInfo("cvnhan.android.androidlogin",
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

        if(hasEmailPermission()==false){
            LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email"));
        }else{
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
            loginButton.setText("Log in with FB");
            LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    ((TextView) findViewById(R.id.txtView)).setText(loginResult.getAccessToken().getUserId() + "-" + loginButton.getText());

                    Set<String> permissions=loginResult.getAccessToken().getPermissions();
                    Set<String> deniedpermissions=loginResult.getAccessToken().getDeclinedPermissions();
                    Set<String> grantedpermissions=loginResult.getRecentlyGrantedPermissions();
                    for(String s:permissions){
                        Log.e("permissions", s);
                    }
                    for(String s:deniedpermissions){
                        Log.e("deniedpermissions", s);
                    }
                    for(String s:grantedpermissions){
                        Log.e("grantedpermissions", s);
                    }
                    Log.e("onSuccess", loginResult.getAccessToken().toString());
                }

                @Override
                public void onCancel() {
                    Log.e("onCancel", "cancel");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.e("onError", e.toString());
                }
            });
        }

        ((Button) findViewById(R.id.shareBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://developers.facebook.com"))
                        .build();
                ShareDialog.show(MainActivity.this, content);
            }
        });
        findViewById(R.id.getMebtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(),"/me",null, HttpMethod.GET,new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        String s=graphResponse.getJSONObject().toString();
                        ((TextView)findViewById(R.id.infoTxt)).setText(s);
                        Log.e("info", s);
                    }
                });

                graphRequest.executeAsync();


            }
        });

        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

    }
    private boolean hasEmailPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains("email");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("callbackManager", requestCode + " " + resultCode + " " + data.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
