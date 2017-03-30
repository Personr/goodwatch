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

import java.util.HashSet;
import java.util.Set;

import static com.example.reehams.goodreads.WelcomeActivity.userId1;

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
        if(imageUsed.equals(WelcomeActivity.userId1)) {
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

        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if(childSnapshot.child("id").getValue(String.class).equals(userId1)) {
                        if (!childSnapshot.child("personName").getValue(String.class).equals(" ")) {
                            set.add(childSnapshot.child("personName").getValue(String.class));
                        }
                    }
                }

                followButton = (Button) findViewById(R.id.followbotton);

                if(set.contains(userName.getText().subSequence(6, userName.getText().length()))) {
                    followButton.setText("Unfollow");
                }
                else {
                    followButton.setText("Follow");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void followThisUser(View view) {
        userName = (TextView) findViewById(R.id.userName2);
        final String personId = getIntent().getStringExtra("id");

        myDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               Set<String> set = new HashSet<String>();
               for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                   if(childSnapshot.child("id").getValue(String.class).equals(userId1)) {
                       if (!childSnapshot.child("personName").getValue(String.class).equals(" ")) {
                           set.add(childSnapshot.child("personName").getValue(String.class));
                       }
                   }
               }
               Button follow_button = (Button) findViewById(R.id.followbotton);
               if (set.contains(userName.getText().subSequence(6, userName.getText().length()))) {
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
                                   myDatabase.child(userId1 + " " + getIntent().getStringExtra("id")).removeValue();
                                  Intent i = new Intent(FollowActivity.this,FollowingListActivity.class);
                                  Bundle extras = new Bundle();
                                   extras.putString("user_id", userId1);
                                  extras.putString("person_id", getIntent().getStringExtra("id"));
                                  i.putExtras(extras);
                                   startActivity(i);
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
                   Following follower = new Following(userId1, getIntent().getStringExtra("id"),
                           WelcomeActivity.facebookName, getIntent().getStringExtra("name"));
                   myDatabase.child(userId1 + " " + getIntent().getStringExtra("id")).setValue(follower);
                   Intent i = new Intent(FollowActivity.this,FollowingListActivity.class);
                   Bundle extras = new Bundle();
                   extras.putString("user_id", userId1);
                   extras.putString("person_id", getIntent().getStringExtra("id"));
                   i.putExtras(extras);
                   startActivity(i);
               }
           }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
