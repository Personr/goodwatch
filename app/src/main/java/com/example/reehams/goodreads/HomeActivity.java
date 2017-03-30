package com.example.reehams.goodreads;

import android.os.Bundle;

/**
 * Created by rahulkooverjee on 3/30/17.
 */

public class HomeActivity extends SideBar {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        super.onCreateDrawer();
    }
}
