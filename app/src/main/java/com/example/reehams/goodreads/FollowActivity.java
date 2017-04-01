package com.example.reehams.goodreads;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.reehams.goodreads.WelcomeActivity.userId1;

/**
 * Created by reehams on 3/26/17.
 */

public class FollowActivity extends SideBar {
    TextView email;
    TextView userName;
    Button followButton;
    ProfilePictureView image;

    private DatabaseReference myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_activity);
        super.onCreateDrawer();
        FacebookSdk.sdkInitialize(getApplicationContext());
        String imageUsed = getIntent().getStringExtra("id");
        final String userId1 = getIntent().getStringExtra("userId1");
        if (imageUsed.equals(WelcomeActivity.userId1)) {
            Intent i = new Intent(FollowActivity.this, MyAccountActivity.class);
            startActivity(i);
        }
        myDatabase = FirebaseDatabase.getInstance().getReference();
        final String userName2 = getIntent().getStringExtra("name");
        final String userId2 = getIntent().getStringExtra("id");

        String userEmail = getIntent().getStringExtra("email");
        email = (TextView) findViewById(R.id.email2);
        email.setText("Email:" + " " + userEmail);
        userName = (TextView) findViewById(R.id.userName2);
        userName.setText("Name:" + " " + userName2);
        image = (ProfilePictureView) findViewById(R.id.image2);
        image.setPresetSize(ProfilePictureView.NORMAL);
        image.setProfileId(imageUsed);
        myDatabase.child(userId1).child("followingIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                followButton = (Button) findViewById(R.id.followbotton);
                followButton.setText("+Follow");
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getValue(String.class).equals(userId2 + "," + userName2)) {
                        followButton.setText("-Unfollow");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void followThisUser(View view) {
        userName = (TextView) findViewById(R.id.userName2);
        final String userId1 = getIntent().getStringExtra("userId1");
        final String userName1 = getIntent().getStringExtra("userName1");

        final String userId2 = getIntent().getStringExtra("id");
        final String userName2 = getIntent().getStringExtra("name");

        // Add the person you followed to your list of followers
        myDatabase.child(userId1).child("followingIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followButton = (Button) findViewById(R.id.followbotton);
                boolean isFollowing = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getValue(String.class).equals(userId2 + "," + userName2)) {
                        isFollowing = true;

                    }
                }
                final List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                if (isFollowing) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            FollowActivity.this);

                    // set title
                    alertDialogBuilder.setTitle("Remove from Following List");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure you want to unfollow?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    String s = userId2 + "," + userName2;
                                    l.remove(s);
                                    if (l.isEmpty()) {
                                        l.add("null");
                                    }
                                    followButton.setText("+Follow");
                                    myDatabase.child(userId1).child("followingIds").setValue(l);
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
                else {
                    if (l.get(0).equals("null")) {
                        l.remove(0);
                    }
                    String s = userId2 + "," + userName2;
                    if (!l.contains(s)) {
                        l.add(s);
                    }
                    followButton.setText("-Unfollow");
                    myDatabase.child(userId1).child("followingIds").setValue(l);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // Add yourself as a follower to the person you just followed
        myDatabase.child(userId2).child("followerIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isFollowing = false;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if (childSnapshot.getValue(String.class).equals(userId1 + "," + userName1)) {
                        isFollowing = true;
                    }
                }
                final List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                if (isFollowing) {
                    String s = userId1 + "," + userName1;
                    l.remove(s);
                    if (l.isEmpty()) {
                        l.add("null");
                    }
                    myDatabase.child(userId2).child("followerIds").setValue(l);
                }
                else {
                    if (l.get(0).equals("null")) {
                        l.remove(0);
                    }
                    String s = userId1 + "," + userName1;
                    if (!l.contains(s)) {
                        l.add(s);
                    }
                    myDatabase.child(userId2).child("followerIds").setValue(l);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // TODO PROBABLY NEED TO NOT DO THIS
        Intent i = new Intent(FollowActivity.this,FollowingListActivity.class);
        Bundle extras = new Bundle();
        extras.putString("user_id", userId1);
        extras.putString("person_id", getIntent().getStringExtra("id"));
        i.putExtras(extras);
       // startActivity(i);

    }

    protected void followingOfTheUser(View view) {
        Intent i = new Intent(FollowActivity.this, WhoIsFollowingUser.class);
        i.putExtra("idOfCurrentPage",getIntent().getStringExtra("id"));
        startActivity(i);
    }
    protected void followersOfTheUser(View view) {
        Intent i = new Intent(FollowActivity.this, FollowersofUser.class);
        i.putExtra("idOfCurrentPage",getIntent().getStringExtra("id"));
        startActivity(i);
    }
}
