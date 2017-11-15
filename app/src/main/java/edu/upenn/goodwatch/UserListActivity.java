package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.upenn.goodwatch.FileAccess.Messages;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by reehams on 3/27/17.
 */

public class UserListActivity extends SideBar {
    DatabaseReference reference;
    private ListView mListView;
    private TextView emptyTextView;
    private ArrayList<String> accountIds = new ArrayList<>();
    private ArrayList<String> accountNames = new ArrayList<>();
    private String accountID;
    private String dataName;
    private String errorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        setTitle("Users");
        super.onCreateDrawer();
        mListView = (ListView) findViewById(R.id.followingListView);
        final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, accountNames);
        mListView.setAdapter(arrayAdapter2);

        accountID = getIntent().getStringExtra("id");
        dataName = getIntent().getStringExtra("dataName");
        errorID = getIntent().getStringExtra("errorID");
        emptyTextView = (TextView) findViewById(R.id.emptyText);
        emptyTextView.setText(Messages.getMessage(getBaseContext(), errorID));
        mListView.setEmptyView(emptyTextView);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child(accountID).child(dataName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for each accountID listed
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String accountID = childSnapshot.getValue(String.class);
                    if (accountID.equals("null")) {
                        break;
                    }
                    //get the name of that accountID and add it to the list
                    reference.child(accountID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String accountName = dataSnapshot.child("name").getValue(String.class);
                            String accountID = dataSnapshot.child("id").getValue(String.class);
                            accountIds.add(accountID);
                            accountNames.add(accountName);
                            arrayAdapter2.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(UserListActivity.this, AccountInformationActivity.class);
                String accountId = accountIds.get(position);
                if(accountId != null) {
                    i.putExtra("id", accountId);
                    startActivity(i);
                }
            }
        });
    }
}