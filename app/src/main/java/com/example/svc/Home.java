package com.example.svc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ReceiverPackage.Receiver;
import ReceiverPackage.Recorder;
import SenderPackage.Sender;
import models.Encounter;
import models.SVCDB;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)/*change to oreo version of OS*/
public class Home extends AppCompatActivity {
    /**
     * Instance variables:
     * db - an instance of the SQLite Helper class.
     * communicationNetwork - an instance of the communication network class.
     * id - the id of the user.
     */
    private SVCDB db;
    Cloud cloud;
    CommunicationNetwork communicationNetwork;
    String id = "9999999999999";

    /**
     * {@inheritDoc}
     * Asks for the needed permissions.
     * initializes the db and communication network and xalls the sign up function.
     * @param savedInstanceState {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView iv = (ImageView)findViewById(R.id.BigBrotherIcon);

        Resources resources = this.getResources();
        final int resourceId = resources.getIdentifier("bigbrothericon", "drawable", this.getPackageName());
        iv.setImageResource(resourceId);

        //get permissions from the user at startup..
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }



        db = new SVCDB(this);
        communicationNetwork = new CommunicationNetwork("", new Receiver(), new Recorder(), db);

        // Get the Intent that started this activity and extract the user.
        Intent intent = getIntent();

        //DO THIS ONLY ON DEBUG!!!
        //db.onUpgrade(db.getReadableDatabase(), 1, 1);
        //db.dropTable();

        try {
            this.signUp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * Checks the needed permissions.
     * Gets the id from the user's SIM card.
     * Initializes the cloud and cloud timer, and calls to start the communication process.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void signUp() throws InterruptedException {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        SubscriptionManager subsManager = (SubscriptionManager) this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

            if (subsList != null) {
                for (SubscriptionInfo subsInfo : subsList) {
                    if (subsInfo != null) {
                        String tmp = subsInfo.getNumber();
                        if (!tmp.equals(""))
                            id = tmp;
                        else {
                            String msg = "Couldn't get SIM information.\n Sadly, you're SIM card is not sutable for this application.";
                            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
                            toast.show();
                        }
                        Log.d("debug phone number", "phone number " + id);
                        id = id.substring(4);

                        Log.d("debug phone number", "updated phone number" + id);
                    }
                }
            }
        }

        id = "0" + id;
        communicationNetwork.composeFrame(id);
        this.cloud = new Cloud(db, this, id);
        cloud.uploadTime.start();
        communicationNetwork.startProcess();
        cloud.uploadTime.join();
    }
}