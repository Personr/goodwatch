package com.example.reehams.goodreads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;

/**
 * Created by reehams on 3/26/17.
 */

public class FollowActivity extends AppCompatActivity {
    TextView email;
    TextView userName;
    ProfilePictureView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.follow_activity);
        String userName2 = getIntent().getStringExtra("name");
        String userEmail = getIntent().getStringExtra("email");
        email = (TextView) findViewById(R.id.email2);
        email.setText("Email:" + " " + userEmail);
        userName = (TextView) findViewById(R.id.userName2);
        userName.setText("Name:" + " " + userName2);
        image = (ProfilePictureView) findViewById(R.id.image2);
        image.setPresetSize(ProfilePictureView.NORMAL);
        String imageUsed = getIntent().getStringExtra("id");
        image.setProfileId(imageUsed);
    }
}
