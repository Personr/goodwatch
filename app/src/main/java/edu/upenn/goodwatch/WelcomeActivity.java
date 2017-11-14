package edu.upenn.goodwatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import edu.upenn.goodwatch.FileAccess.Messages;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WelcomeActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private final String DEBUG_TAG = getClass().getSimpleName();
    private LoginButton facebookButton;
    private SignInButton googleButton;
    private CallbackManager callbackManager;

    private static String email;
    private static String name;
    private static String photoUrl;
    private static String userId;

    private DatabaseReference myDatabase;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_welcome);
        callbackManager = CallbackManager.Factory.create();

        facebookButton = (LoginButton)findViewById(R.id.login_button2);
        facebookButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));

        googleButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleButton.setSize(SignInButton.SIZE_STANDARD);

        myDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            login(currentUser);
        }
        else {
            facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken accessToken = loginResult.getAccessToken();
                    handleFacebookAccessToken(accessToken);
                }

                @Override
                public void onCancel() {
                    Toast.makeText(WelcomeActivity.this, Messages.getMessage(getBaseContext(), "login.cancel"),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(WelcomeActivity.this, Messages.getMessage(getBaseContext(), "login.error"),
                            Toast.LENGTH_LONG).show();
                }
            });
            googleButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.google_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // If sign in fails, display a message to the user.
                Log.w(DEBUG_TAG, "Google sign in failed", e);
                Toast.makeText(WelcomeActivity.this, "Google sign in failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(DEBUG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(DEBUG_TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            login(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(DEBUG_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();}
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(DEBUG_TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(DEBUG_TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            login(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(DEBUG_TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(WelcomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Getter method for email
    protected static String getEmail() {
        return email;
    }

    //Getter method for Facebook name
    protected static String getName() {
        return name;
    }

    //Getter method for Profile pic Id
    protected static String getPhotoUrl() {
        return photoUrl;
    }

    //Getter method for
    protected static String getUserId() {
        return userId;
    }

    private void login(FirebaseUser user) {
        userId = user.getUid();
        email = user.getEmail();
        name = user.getDisplayName();
        Uri photo = user.getPhotoUrl();
        if(photo == null)
            photoUrl = null;
        else
            photoUrl = photo.toString();

        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //If this is a new user, create a new entry for them in the database
                if(!dataSnapshot.hasChild(userId)) {
                    List<String> watchlist = new ArrayList<>();
                    watchlist.add("null");
                    List<Review> reviews = new ArrayList<>();
                    reviews.add(new Review("null"));
                    List<String> following = new ArrayList<>();
                    following.add("null");
                    List<String> followers = new ArrayList<>();
                    followers.add("null");

                    User user = new User(name, email, userId, photoUrl, watchlist, reviews, following, followers);
                    myDatabase.child(userId).setValue(user);
                }
                else {
                    name = dataSnapshot.child(userId).child("name").toString();
                    email = dataSnapshot.child(userId).child("email").toString();
                }
                Intent i = new Intent(WelcomeActivity.this, HomeActivity.class);
                i.putExtra("user_id", userId);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(WelcomeActivity.this, Messages.getMessage(getBaseContext(), "login.cancel"),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}

