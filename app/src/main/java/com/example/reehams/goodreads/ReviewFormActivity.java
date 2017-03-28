package com.example.reehams.goodreads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ReviewFormActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private String[] ratingSpinner;
    private double rating;
    private EditText reviewText;
    private Button submitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_form);

        reviewText = (EditText) findViewById(R.id.reviewEditText);

        // Saving user review text from form to Firebase
        submitBtn = (Button) findViewById(R.id.button2);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TO DO:
            }
        });


        this.ratingSpinner = new String[] {"0", "0.5", "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ratings_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        switch(pos) {
            case 0:
                rating = 0;
                break;
            case 1:
                rating = 0.5;
                break;
            case 2:
                rating = 1;
                break;
            case 3:
                rating = 1.5;
                break;
            case 4:
                rating = 2;
                break;
            case 5:
                rating = 2.5;
                break;
            case 6:
                rating = 3;
                break;
            case 7:
                rating = 3.5;
                break;
            case 8:
                rating = 4;
                break;
            case 9:
                rating = 4.5;
                break;
            case 10:
                rating = 5;
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another Interface callback
    }


}
