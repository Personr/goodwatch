package com.example.reehams.goodreads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WelcomeActivity extends AppCompatActivity {


    private LoginButton btnLogin;
    private CallbackManager callbackManager;
    static String email;
    static String facebookName;
    static String gender;
    static String profilePicId;
    private DatabaseReference myDatabase;
    static String userId1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_welcome);
        btnLogin = (LoginButton)findViewById(R.id.login_button2);

        btnLogin.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        callbackManager = CallbackManager.Factory.create();

        myDatabase = FirebaseDatabase.getInstance().getReference();



        if (isLoggedIn()) {
            Intent i = new Intent(WelcomeActivity.this, SideBar.class);
            Intent i2 = new Intent(WelcomeActivity.this, FollowActivity.class);
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            userId1 = accessToken.getUserId();
            i.putExtra("user_id", userId1);
            i2.putExtra("user_id", userId1);
            startActivity(i);
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("Main", response.toString());
                            setProfileToView(object);
                            try {
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String profilePicId = object.getString("id");
                                User user = new User(name, email, userId1, " ", " ");
                                myDatabase.child(userId1).setValue(user);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();

        }




        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent i = new Intent(WelcomeActivity.this, SideBar.class);
                //Facebook userId(the numerical one)
                userId1 = loginResult.getAccessToken().getUserId();
                i.putExtra("user_id", userId1);
                startActivity(i);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("Main", response.toString());
                                setProfileToView(object);
                                //String userId = myDatabase.push().getKey();
                                try {
                                        String name = object.getString("name");
                                        String email = object.getString("email");
                                        User user = new User(name, email, userId1," ", " ");
                                        myDatabase.child(userId1).setValue(user);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {

            }
        });

    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }





    private void setProfileToView(JSONObject jsonObject) {
        try {
            this.email = (jsonObject.getString("email"));
            this.gender = (jsonObject.getString("gender"));
            this.facebookName = (jsonObject.getString("name"));
            this.profilePicId = (jsonObject.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

}

