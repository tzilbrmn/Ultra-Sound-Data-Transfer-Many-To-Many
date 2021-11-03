package com.example.svc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import Utils.Constants;
import models.SVCDB;
import models.User;
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ViewProfile extends AppCompatActivity {
    /**
     * Instance variables:
     * db - an instance of the SQLite Helper class.
     * user - The currently logged in user.
     */
    private SVCDB db;
    private User user;

    /**
     * {@inheritDoc}
     * Initializes the db instance, gets the currently logged in user from the intent <i>Extra</i> dictionary.
     * sets the TextFields value to that stored in the current user.
     * @param savedInstanceState {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        db = new SVCDB(this);
        Intent intent = getIntent();
        //get the user from the intent dictionary
        user = new User(intent.getStringExtra(Constants.USER));
    }
}