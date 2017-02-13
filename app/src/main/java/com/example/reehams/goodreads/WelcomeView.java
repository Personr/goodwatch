package com.example.reehams.goodreads;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by reehams on 2/12/17.
 */

public class WelcomeView extends View {

    public WelcomeView(Context context) {
        super(context);
        setBackgroundResource(R.drawable.theatrenice);
    }

    public WelcomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.theatrenice);
    }

    public WelcomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.theatrenice);
    }

}
