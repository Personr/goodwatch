package com.example.reehams.goodreads;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by rahulkooverjee on 4/11/17.
 */

public class TopChartsActivity extends SideBar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topcharts_layout);
        super.onCreateDrawer();
    }
}
