package com.example.reehams.goodreads;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    WelcomeView v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        v = new WelcomeView(this);
//        setContentView(v);
        setContentView(R.layout.activity_welcome);
    }

    private void test() {
        System.out.println("Rahul");
        System.out.println("Reeham");
        System.out.println("Alex");
        System.out.println("Ameya");
    }
}
