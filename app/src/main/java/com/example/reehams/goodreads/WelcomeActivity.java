package com.example.reehams.goodreads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class WelcomeActivity extends AppCompatActivity {
    LoginButton loginButton;
    TextView textView;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_welcome);
        loginButton = (LoginButton) findViewById(R.id.fb_login_bn);
        textView = (TextView) findViewById(R.id.textView);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("Login success \n" +
                        loginResult.getAccessToken().getUserId() + "\n"
                        + loginResult.getAccessToken().getToken());

            }
            @Override
            public void onCancel() {
                textView.setText("Login cancelled");
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Intent i = new Intent(this, AboutUs.class);
        startActivityForResult(i, 1);
    }

    /**
     * Handle button presses by starting the game
     * @param view
     */
    public void onButtonPress(View view) {
        Intent i = new Intent(this, MovieSearch.class);
        // i.putExtra("numPoints", numPoints);
        startActivityForResult(i, 1);
    }
}
