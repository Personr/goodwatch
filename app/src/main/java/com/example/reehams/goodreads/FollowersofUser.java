package com.example.reehams.goodreads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

/**
 * Created by reehams on 3/29/17.
 */

public class FollowersofUser extends SideBar {
    DatabaseReference reference;
    private ListView mListView;
    private ArrayList<String> whouserIsFollowing = new ArrayList<>();
    String[] searchResults; // Options to be shown in list view
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followersofuser);
        super.onCreateDrawer();
        mListView = (ListView) findViewById(R.id.FollowersofUserList);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, whouserIsFollowing);
        mListView.setAdapter(arrayAdapter2);
        userId = getIntent().getStringExtra("idOfCurrentPage");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.child(userId).child("followerIds").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String following = childSnapshot.getValue(String.class);
                    if (following.equals("null")) {
                        set.add("No followers");
                        break;
                    }
                    int idx = following.indexOf(",");
                    following = following.substring(idx + 1, following.length());
                    set.add(following);
                }
                whouserIsFollowing.clear();
                whouserIsFollowing.addAll(set);
                arrayAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
