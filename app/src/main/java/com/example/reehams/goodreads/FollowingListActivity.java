package com.example.reehams.goodreads;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.example.reehams.goodreads.WelcomeActivity.facebookName;
import static com.example.reehams.goodreads.WelcomeActivity.userId1;

/**
 * Created by reehams on 3/27/17.
 */

public class FollowingListActivity extends SideBar {
    DatabaseReference reference;
    private ListView mListView;
    private ArrayList<String> whoIAmFollowing = new ArrayList<>();
    String[] searchResults; // Options to be shown in list view
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.following_list);
        super.onCreateDrawer();
        mListView = (ListView) findViewById(R.id.followingListView);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, whoIAmFollowing);
        mListView.setAdapter(arrayAdapter2);
        userId = getIntent().getStringExtra("user_id");
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    //set.add(childSnapshot.getKey());
                    if (childSnapshot.child("id").getValue(String.class).equals(userId1)) {
                        if (!childSnapshot.child("personName").getValue(String.class).equals(" ")) {
                            set.add(childSnapshot.child("personName").getValue(String.class));
                        }
                    }
                }
                whoIAmFollowing.clear();
                whoIAmFollowing.addAll(set);
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