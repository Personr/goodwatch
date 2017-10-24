package com.example.reehams.goodreads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WelcomeActivity extends AppCompatActivity {

    private final String DEBUG_TAG = getClass().getSimpleName();
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
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            login(accessToken);
        }
        else {
            btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken accessToken = loginResult.getAccessToken();
                    login(accessToken);
                }

                @Override
                public void onCancel() {
                }

                @Override
                public void onError(FacebookException exception) {
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
            this.email = jsonObject.getString("email");
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "User doesn't have an email.\n", e);
        }
        try {
            this.gender = jsonObject.getString("gender");
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "User doesn't have a gender.\n", e);
        }
        try {
            this.facebookName = jsonObject.getString("name");
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "User doesn't have an name.\n", e);
        }
        try {
            this.profilePicId = jsonObject.getString("id");
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, "User doesn't have a profile pic.\n", e);
        }
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void login(AccessToken accessToken) {
        Intent i = new Intent(WelcomeActivity.this, HomeActivity.class);
        userId1 = accessToken.getUserId();
        i.putExtra("user_id", userId1);
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                getRefreshDatabaseGraphRequest());
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
        startActivity(i);
    }

    private GraphRequest.GraphJSONObjectCallback getRefreshDatabaseGraphRequest() {
        return new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.v("Main", response.toString());
                setProfileToView(object);

                myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> watchlist = (ArrayList<String>) dataSnapshot.child(userId1).child("watchlist").getValue();
                        List<Review> reviews = (ArrayList<Review>) dataSnapshot.child(userId1).child("reviews").getValue();
                        List<String> following = (ArrayList<String>) dataSnapshot.child(userId1).child("followingIds").getValue();
                        List<String> followers = (ArrayList<String>) dataSnapshot.child(userId1).child("followerIds").getValue();
                        if (watchlist == null) {
                            watchlist = new ArrayList<String>();
                            watchlist.add("null");
                        }
                        if (reviews == null) {
                            reviews = new ArrayList<Review>();
                            // reviews.add("null");
                            reviews.add(new Review(("null")));

                        }
                        if (following == null) {
                            following = new ArrayList<String>();
                            following.add("null");
                        }
                        if (followers == null) {
                            followers = new ArrayList<String>();
                            followers.add("null");
                        }
                        User user = new User(facebookName, email, userId1, watchlist, reviews, following, followers);
                        myDatabase.child(userId1).setValue(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        };
    }

}

