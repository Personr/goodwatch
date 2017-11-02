package edu.upenn.goodwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import edu.upenn.goodwatch.FileAccess.Messages;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

/**
 * Created by reehams on 2/20/17.
 */

public class LogOutActivity extends SideBar {
    private CallbackManager callbackManager;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.logout_activity);
        super.onCreateDrawer();
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void userLoggedOut(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(Messages.getMessage(getBaseContext(), "logout.logout"));

        // set dialog message
        alertDialogBuilder
                .setMessage(Messages.getMessage(getBaseContext(), "logout.confirm"))
                .setCancelable(false)
                .setPositiveButton(Messages.getMessage(getBaseContext(), "follow.yes"),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        LoginManager.getInstance().logOut();
                        Intent i = new Intent(LogOutActivity.this, WelcomeActivity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton(Messages.getMessage(getBaseContext(), "follow.no"),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
