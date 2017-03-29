package com.example.reehams.goodreads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by reehams on 3/26/17.
 */

public class FollowActivity extends AppCompatActivity {
    TextView email;
    TextView userName;
    Button followButton;
    ProfilePictureView image;
    private DatabaseReference myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        String imageUsed = getIntent().getStringExtra("id");
        if(imageUsed == WelcomeActivity.userId1) {
            Intent i = new Intent(FollowActivity.this, MyAccountActivity.class);
            startActivity(i);
        }
        myDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.follow_activity);
        String userName2 = getIntent().getStringExtra("name");
        String userEmail = getIntent().getStringExtra("email");
        email = (TextView) findViewById(R.id.email2);
        email.setText("Email:" + " " + userEmail);
        userName = (TextView) findViewById(R.id.userName2);
        userName.setText("Name:" + " " + userName2);
        image = (ProfilePictureView) findViewById(R.id.image2);
        image.setPresetSize(ProfilePictureView.NORMAL);
        image.setProfileId(imageUsed);
    }

    protected void followThisUser(View view) {
        String personId = getIntent().getStringExtra("id");
        String userName2 = getIntent().getStringExtra("name");
        Following nowFollowing = new Following(WelcomeActivity.userId1, personId,WelcomeActivity.facebookName, userName2);
        myDatabase.child(WelcomeActivity.userId1 + " " + personId).setValue(nowFollowing);
        followButton = (Button) findViewById(R.id.followbotton);
        followButton.setText("Unfollow");
        Intent i = new Intent(FollowActivity.this, FollowingListActivity.class);
        startActivity(i);
    }
}
