package com.example.reehams.goodreads;


import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import com.facebook.login.widget.ProfilePictureView;




/**
 * Created by reehams on 2/17/17.
 */

public class MyAccountActivity extends SideBar {
    TextView email;
    TextView gender;
    TextView userName;
    ProfilePictureView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.my_account);
        super.onCreateDrawer();
        email = (TextView) findViewById(R.id.email);
        email.setText("Email:" + " " + WelcomeActivity.email);
        gender = (TextView) findViewById(R.id.gender);
        gender.setText("Gender:" + " " +WelcomeActivity.gender);
        userName = (TextView) findViewById(R.id.userName);
        userName.setText("Name:" + " " + WelcomeActivity.facebookName);
        image = (ProfilePictureView) findViewById(R.id.image);
        image.setPresetSize(ProfilePictureView.NORMAL);
        image.setProfileId(WelcomeActivity.profilePicId);
    }

    protected void watchlistOnButtonPressed(View view) {
        Intent i = new Intent(this,WatchlistActivity.class);
        startActivity(i);
    }

    protected void whoIamFollowing(View view) {
        Intent i = new Intent(this, FollowingListActivity.class);
        i.putExtra("user_id", WelcomeActivity.userId1);
        startActivity(i);
    }
    protected void myFollowers(View view) {
        Intent i = new Intent(this, FollowerListActivity.class);
        i.putExtra("user_id", WelcomeActivity.userId1);
        startActivity(i);
    }
}
