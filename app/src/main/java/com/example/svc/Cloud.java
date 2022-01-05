package com.example.svc;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Encounter;
import models.SVCDB;

public class Cloud {

    FirebaseDatabase database;
    DatabaseReference myRef;

    SVCDB db;
    Context context;
    String id;
    String key;
    File dir;
    File gpxfile;

    public Cloud (SVCDB db, Context context, String id)
    {
        this.db = db;
        this.context = context;
        this.id = id;

        database= FirebaseDatabase.getInstance("https://big-brother-ultra-sound-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("big-brother-ultra-sound-default-rtdb").child("users");
        addUserToFirebase();
    }

    protected void addUserToFirebase() {
        key = myRef.push().getKey();
        myRef.child(key).setValue(id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void upload() throws ParseException {
        //For debug
        ArrayList<Encounter> finalList = new ArrayList<Encounter>();
        Encounter enc = new Encounter(id).setEncounterDate("2022-01-05").setEncounterTime("18:00:00");
        finalList.add(enc);
        myRef.child(key).child(id).setValue(generateListToUpload(/*db.getUserVisitCards()*/ finalList));
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
