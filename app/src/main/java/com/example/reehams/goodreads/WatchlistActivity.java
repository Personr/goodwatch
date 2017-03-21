package com.example.reehams.goodreads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.reehams.goodreads.WelcomeActivity.facebookName;
import static com.example.reehams.goodreads.WelcomeActivity.userId1;

public class WatchlistActivity extends AppCompatActivity {
    DatabaseReference reference;
    private ListView mListView;
    private ArrayList<String> myMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        mListView = (ListView) findViewById(R.id.listView);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myMovies);
        mListView.setAdapter(arrayAdapter);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    //set.add(childSnapshot.getKey());
                    if(childSnapshot.child("id").getValue(String.class).equals(userId1)) {
                        if (!childSnapshot.child("name").getValue(String.class).equals(facebookName)) {
                            set.add(childSnapshot.child("name").getValue(String.class));
                        }
                    }
                }
                myMovies.clear();
                myMovies.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
