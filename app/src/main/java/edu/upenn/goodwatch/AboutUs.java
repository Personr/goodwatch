package edu.upenn.goodwatch;

import android.os.Bundle;

public class AboutUs extends SideBar {
    //About us activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        super.onCreateDrawer();
    }
}
