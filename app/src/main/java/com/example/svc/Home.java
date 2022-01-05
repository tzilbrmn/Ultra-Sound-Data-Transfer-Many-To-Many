package com.example.svc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import SenderPackage.Sender;
import Utils.Constants;
import Utils.utils;
import models.Encounter;
import models.SVCDB;
import models.User;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)/*change to oreo version of OS*/
public class Home extends AppCompatActivity implements View.OnClickListener {
    /**
     * Instance variables:
     * userVisitCards - all the visit cards the user owns.
     * db - an instance of the SQLite Helper class.
     * user - The currently logged in user.
     * visitCardsTable - the table element to view the visit cards in.
     */
    private ArrayList<Encounter> userVisitCards;
    private User user;
    private SVCDB db;
    private TableLayout visitCardsTable;
    Sender cSender;
    TextView txtShowInfo;
    AddVC addVC;
    String id = "";

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

        //get permissions from the user at startup..
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CONTACTS}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        db = new SVCDB(this);

        addVC = new AddVC(db);
        txtShowInfo = (TextView)findViewById(R.id.txtMyInfo);
        txtShowInfo.setText("in onCreate");


        //DO THIS ONLY ON DEBUG!!!
        //db.onUpgrade(db.getReadableDatabase(), 1, 1);
        //db.dropTable();

        Button btn = (Button)findViewById(R.id.signUpBtn);
        btn.setOnClickListener(this);

        // Get the Intent that started this activity and extract the user.
        Intent intent = getIntent();
        user = new User("0545939593"); //TAL - need to change to get the actual user id
        //get the visit cards owned by this user.
        userVisitCards = Encounter.getUserVisitCards(db);
        if(userVisitCards == null){
            new AlertDialog.Builder(this)
                    .setTitle("An Error!")
                    .setMessage("An Error Occurred, please try again.")
                    .setNeutralButton("Close", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }



        //initialize the table layout.
        visitCardsTable = (TableLayout) findViewById(R.id.visitCardsTable);

        //for each visit card the user owns, initialize a table row with its' data.
/*        for(int i=1; i <= userVisitCards.size(); i++){
            Encounter vc = userVisitCards.get(i-1);
            //initialize the row
            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 2, 16, 2);
            row.setLayoutParams(lp);

            //set the views:

            //set the full id view
            TextView full_name = new TextView(this);
            full_name.setText(vc.getId());
            lp.setMargins(utils.pxFromDp(this,8),0,utils.pxFromDp(this,16),0);
            row.addView(full_name,lp);

            //set the encounter time view
            TextView encounterTime = new TextView(this);
            encounterTime.setText(vc.getEncounterStartTime());
            lp.setMargins(utils.pxFromDp(this,8),0,utils.pxFromDp(this,16),0);
            row.addView(encounterTime,lp);

            //set the "View" button view.
            Button viewVC = new Button(this);
            viewVC.setText("View");
            viewVC.setMinHeight(0);
            viewVC.setMinimumHeight(0);
            viewVC.setHeight(utils.pxFromDp(this,40));
            lp.setMargins(utils.pxFromDp(this,8),0,utils.pxFromDp(this,16),0);


            Context context = this;
            viewVC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ViewVisitCard.class);
                    intent.putExtra(Constants.VC_DATA,vc.toString());
                    intent.putExtra(Constants.USER,user.toString());
                    startActivity(intent);
                }
            });
            row.addView(viewVC,lp);

            //add the row to the table
            visitCardsTable.addView(row,i);
        }

*/    }

    /**
     * the onCLick for the <i>View Profile</i> button.
     * starts a new activity.
     * @param v <code>Auto-generated by Android</code>
     */
    public void viewProfile(View v){
        Intent intent = new Intent(this,ViewProfile.class);
        intent.putExtra(Constants.USER,user.toString());
        startActivity(intent);
    }

    /**
     * the onCLick for the <i>Add Visit Card</i> button.
     * opens the AddVC activity.
     * @param v <code>Auto-generated by Android</code>
     */
    public void addVC(View v){
        Intent intent = new Intent(this,AddVC.class);
        intent.putExtra(Constants.USER,user.toString());
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void signUp(View v) throws InterruptedException {
        this.id = ((EditText)findViewById(R.id.TxtUserId)).getText().toString();  // TAL - need to get from phone
//        cSender = new Sender();
//        cSender.setUserId(id);
        //Sign up to the cloud???
        txtShowInfo.setText("in signUp");

        userVisitCards = Encounter.getUserVisitCards(db);
        addVC.ReceiveVC(id); //The call to start the process of transmitting and receiving the frames.
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        int i = 0;
        int j;
        if (i == 0)
            j = 8;
        txtShowInfo.setText("in onClick");
        try {
            this.id = ((EditText)findViewById(R.id.TxtUserId)).getText().toString();
            Cloud cloud = new Cloud(db, this, id);
            cloud.upload();
            this.signUp(v);
        } catch (InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }
}