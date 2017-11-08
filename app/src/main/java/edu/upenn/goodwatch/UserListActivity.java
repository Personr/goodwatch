package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.upenn.goodwatch.FileAccess.Messages;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by reehams on 3/27/17.
 */

public class UserListActivity extends SideBar {
    DatabaseReference reference;
    private ListView mListView;
    private ArrayList<String> users = new ArrayList<>();
    private String accountID;
    private String dataName;
    private String errorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        super.onCreateDrawer();
        mListView = (ListView) findViewById(R.id.followingListView);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, users);
        mListView.setAdapter(arrayAdapter2);

        accountID = getIntent().getStringExtra("id");
        dataName = getIntent().getStringExtra("dataName");
        errorID = getIntent().getStringExtra("errorID");

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child(accountID).child(dataName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new TreeSet<String>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String person = childSnapshot.getValue(String.class);
                    if (person.equals("null")) {
                        set.add(Messages.getMessage(getBaseContext(), errorID));
                        break;
                    }
                    int idx = person.indexOf(",");
                    person = person.substring(idx + 1, person.length());
                    set.add(person);
                }
                users.clear();
                users.addAll(set);
                arrayAdapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent i = new Intent(UserListActivity.this, AccountInformationActivity.class);
                final String name = users.get(position);
                i.putExtra("name", name);

                reference.child(accountID).child(dataName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String user = childSnapshot.getValue(String.class);
                            if (user.equals("null")) {
                                break;
                            }
                            final String[] followingDetails = user.split(",");
                            i.putExtra("id", followingDetails[0]);
                            if(followingDetails[1].equals(name)) {
                                reference.child(followingDetails[0]).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String email = dataSnapshot.getValue(String.class);
                                        i.putExtra("email", email);
                                        startActivity(i);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            }

        });

    }
}