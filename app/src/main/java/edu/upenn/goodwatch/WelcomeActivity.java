package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import edu.upenn.goodwatch.FileAccess.Messages;
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
    private static String email;
    private static String facebookName;
    static String gender;
    private static String profilePicId;
    private DatabaseReference myDatabase;
    private static String userId1;

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
                    Toast.makeText(getApplicationContext(), "Error logging you in, please check your connection and try again.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Getter method for email
    protected static String getEmail() {
        return email;
    }

    //Getter method for Facebook name
    protected static String getFacebookName() {
        return facebookName;
    }

    //Getter method for Profile pic Id
    protected static String getProfilePicId() {
        return profilePicId;
    }

    //Getter method for
    protected static String getUserId1() {
        return userId1;
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
            Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "welcome.noEmail"), e);
        }
        try {
            this.gender = jsonObject.getString("gender");
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "welcome.noGender"), e);
        }
        try {
            this.facebookName = jsonObject.getString("name");
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "welcome.noName"), e);
        }
        try {
            this.profilePicId = jsonObject.getString("id");
        } catch (JSONException e) {
            Log.e(DEBUG_TAG, Messages.getMessage(getBaseContext(), "welcome.noPic"), e);
        }
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void login(AccessToken accessToken) {
        userId1 = accessToken.getUserId();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                getRefreshDatabaseGraphRequest());
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private GraphRequest.GraphJSONObjectCallback getRefreshDatabaseGraphRequest() {
        return new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.v("Main", response.toString());
                    setProfileToView(object);
                    myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //If this is a new user, create a new entry for them in the database
                        if(!dataSnapshot.hasChild(userId1)) {
                            List<String> watchlist = new ArrayList<String>();
                            List<Review> reviews = new ArrayList<Review>();
                            List<String> following = new ArrayList<String>();
                            List<String> followers = new ArrayList<String>();
                            User user = new User(facebookName, email, userId1, watchlist, reviews, following, followers);
                            myDatabase.child(userId1).setValue(user);
                        }
                        else {
                            facebookName = dataSnapshot.child(userId1).child("name").toString();
                            email = dataSnapshot.child(userId1).child("email").toString();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                    });
                    Intent i = new Intent(WelcomeActivity.this, HomeActivity.class);
                    i.putExtra("user_id", userId1);
                    startActivity(i);
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Error logging you in, please check your connection and try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

}

