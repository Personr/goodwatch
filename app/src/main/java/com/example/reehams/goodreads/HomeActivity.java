package com.example.reehams.goodreads;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.*;

import java.util.*;

/**
 * Created by rahulkooverjee on 3/30/17.
 */

public class HomeActivity extends SideBar {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        super.onCreateDrawer();
        final String userId = getIntent().getStringExtra("user_id");
        final DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference();
        myDatabase.child(userId).child("email").setValue("hi");
        /**
        myDatabase.child(userId).child("followingIds").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                       //List l = dataSnapshot.getValue(List.class);
                        List<String> l = (ArrayList<String>) dataSnapshot.getValue();
                        l.remove(0);
                        l.add("yo");
                        myDatabase.child(userId).child("followingIds").setValue(l);

                        //user.email now has your email value
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }); */
    }
}
