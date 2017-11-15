package edu.upenn.goodwatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.upenn.goodwatch.FileAccess.Messages;


public class EditProfileActivity extends AppCompatActivity {
    private DatabaseReference myDatabase;
    private String userName;
    private String userEmail;
    private String userId;
    private String userBio;
    private String userPhotoUrl;

    private EditText nameEdit;
    private EditText emailEdit;
    private EditText bioEdit;
    private EditText photoUrlEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myDatabase = FirebaseDatabase.getInstance().getReference();

        nameEdit = (EditText) findViewById(R.id.nameEdit);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        bioEdit = (EditText) findViewById(R.id.bioEdit);
        photoUrlEdit = (EditText) findViewById(R.id.photoUrlEdit);

        userName = getIntent().getStringExtra("name");
        userEmail = getIntent().getStringExtra("email");
        userId = getIntent().getStringExtra("id");
        userBio = getIntent().getStringExtra("bio");
        userPhotoUrl = getIntent().getStringExtra("photoUrl");

        nameEdit.setText(userName);
        emailEdit.setText(userEmail);
        bioEdit.setText(userBio);
        photoUrlEdit.setText(userPhotoUrl);
    }

    protected void save(View view) {
        String new_email = emailEdit.getText().toString();
        String new_name = nameEdit.getText().toString();
        String new_bio = bioEdit.getText().toString();
        String new_photoUrl = photoUrlEdit.getText().toString();
        myDatabase.child(userId).child("email").setValue(new_email);
        myDatabase.child(userId).child("name").setValue(new_name);
        myDatabase.child(userId).child("bio").setValue(new_bio);
        myDatabase.child(userId).child("photoUrl").setValue(new_photoUrl);

        Toast.makeText(getApplicationContext(), "Profile saved.",  Toast.LENGTH_SHORT).show();
        finish();
    }
}
