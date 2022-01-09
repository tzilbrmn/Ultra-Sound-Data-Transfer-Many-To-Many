package com.example.svc;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import models.Encounter;
import models.SVCDB;

public class Cloud {

    FirebaseDatabase database;
    DatabaseReference myRef;

    Thread uploadTime;

    SVCDB db;
    Context context;
    String id;
    String key;


    public Cloud (SVCDB db, Context context, String id) throws InterruptedException {
        this.db = db;
        this.context = context;
        this.id = id;

        database= FirebaseDatabase.getInstance("https://big-brother-ultra-sound-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("big-brother-ultra-sound-default-rtdb").child("users");

        addUserToFirebase();

        this.uploadTime = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    Log.d("Debug ", "Wait to upload");
                    setPriority(Thread.MIN_PRIORITY);
                    long sleepTime = 14 * 24 * 60 * 60000;
                    while (true) {
                        Thread.sleep(sleepTime);
                        upload();
                    }
                } catch (InterruptedException | ParseException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    protected void addUserToFirebase() throws InterruptedException {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            key = myRef.push().getKey();
            myRef.child(key).setValue(id);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void upload() throws ParseException {
        myRef.child(key).child(id).setValue(generateListToUpload(db.getUserVisitCards()));
    }

    protected ArrayList<Encounter> generateListToUpload(ArrayList<Encounter> encList) throws ParseException {
        ArrayList<Encounter> finalList = new ArrayList<Encounter>();
        SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        int i;
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        for(i = 0; i < encList.size(); i++)
        {
            String start = new String(encList.get(i).getEncounterStartDate() + " "+ encList.get(i).getEncounterStartTime());
            String finish = new String(encList.get(i).getEncounterEndDate() + " " +encList.get(i).getEncounterEndTime());

            //combine date and time to calculate the differance
            Date startDateFormat = dateAndTimeFormat.parse(start);
            Date finishDateFormat = dateAndTimeFormat.parse(finish);

            //calculate the duration between the two dates.
            long duration = finishDateFormat.getTime() - startDateFormat.getTime();

            long elapsedDays = duration / daysInMilli;
            duration = duration % daysInMilli;

            long elapsedHours = duration / hoursInMilli;
            duration = duration % hoursInMilli;

            long elapsedMinutes = duration / minutesInMilli;
            duration = duration % minutesInMilli;

            long elapsedSeconds = duration / secondsInMilli;

            if((elapsedDays == 0)&&(elapsedHours == 0)&&(((elapsedMinutes == 14)&&(elapsedSeconds <= 59)) ||((elapsedMinutes == 15)&&(elapsedSeconds == 0))||(elapsedMinutes <= 13))){
            }
            else {
                finalList.add(encList.get(i));
            }
        }
        return finalList;
    }
}
