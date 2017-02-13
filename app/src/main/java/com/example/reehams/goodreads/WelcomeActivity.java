package com.example.reehams.goodreads;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {
    WelcomeView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        v = new WelcomeView(this);
        setContentView(v);
    }

    private void test() {
        System.out.println("yooo");
        System.out.println("Rahul");
    }
}
