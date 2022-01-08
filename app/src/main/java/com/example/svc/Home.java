package com.example.svc;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

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
     * userVisitCards - all the visit cards the user owns.
     * db - an instance of the SQLite Helper class.
     * user - The currently logged in user.
     * visitCardsTable - the table element to view the visit cards in.
     */
    private ArrayList<Encounter> userVisitCards;
    private SVCDB db;
    Sender cSender;
    TextView txtShowInfo;
    CommunicationNetwork communicationNetwork;
    String id = "9999999999999";

    /**
     * {@inheritDoc}
     * Initializes the db instance, gets the currently logged in user from the intent <i>Extra</i> dictionary.
     * gets the visit cards owned by the currently logged in user and populates the table.
     * @param savedInstanceState {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView iv = (ImageView)findViewById(R.id.BigBrotherIcon);
        iv.setImageResource(R.drawable.bigbrothericon2);

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
                        Log.d("debug phone number", "phone number " + id);
                        id = id.substring(4);

                        Log.d("debug phone number", "updated phone number" + id);
                    }
                }
            }
        }


        userVisitCards = Encounter.getUserVisitCards(db);
        id = "0" + id;
        communicationNetwork.composeFrame(id);
        communicationNetwork.startProcess();
        try {
            int lastIndex = userVisitCards.size() - 1;

        if (lastIndex > 0) {
            txtShowInfo.setText("Info from DB: id- " + userVisitCards.get(lastIndex).getId() + " start date- " + userVisitCards.get(lastIndex).getEncounterStartDate() + " start time- " + userVisitCards.get(lastIndex).getEncounterStartTime());


            int i = 0;
            while (i < 100) {
                userVisitCards = Encounter.getUserVisitCards(db);
                lastIndex = userVisitCards.size() - 1;
                txtShowInfo.setText("Info from DB: id- " + userVisitCards.get(lastIndex).getId() + " start date- " + userVisitCards.get(lastIndex).getEncounterStartDate() + " start time- " + userVisitCards.get(lastIndex).getEncounterStartTime());
                i++;
            }

        }
        }
        catch (Exception e) {}
    }
}