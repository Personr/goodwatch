package com.example.reehams.goodreads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.example.reehams.goodreads.WelcomeActivity.userId1;

/**
 * Created by reehams on 3/27/17.
 */

public class FollowerListActivity extends SideBar {
    DatabaseReference reference;
    private ListView mListView;
    private ArrayList<String> myFollowers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follower_list);
        super.onCreateDrawer();
        mListView = (ListView) findViewById(R.id.followerListView);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myFollowers);
        mListView.setAdapter(arrayAdapter2);
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    //set.add(childSnapshot.getKey());
                    if(childSnapshot.child("personId").getValue(String.class).equals(userId1)) {
                        if (!childSnapshot.child("personName").getValue(String.class).equals(" ")) {
                            set.add(childSnapshot.child("name").getValue(String.class));
                        }
                    }
                }
                myFollowers.clear();
                myFollowers.addAll(set);
                arrayAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
