package models;

import android.database.sqlite.SQLiteException;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.util.ArrayList;
import java.time.*;
import java.text.SimpleDateFormat;
import java.util.*;

import Utils.utils;

/**
 * This class encapsulates the visit card data in an object.
 */
public class Encounter {
    private String id;
    private String encounterStartDate;
    private String encounterStartTime;
    private String encounterEndDate;
    private String encounterEndTime;


    public Encounter(String id){
        this.id = id;

        String encounterTime = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date());
        String encounterDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        this.encounterStartDate = encounterDate;
        this.encounterStartTime = encounterTime;
        this.encounterEndDate = encounterStartDate;
        this.encounterEndTime = encounterStartTime;
    }

    public Encounter(String id, String encounterDate, String encounterTime){
        this.id = id;
        this.encounterStartDate = encounterDate;
        this.encounterStartTime = encounterTime;
        this.encounterEndDate = encounterStartDate;
        this.encounterEndTime = encounterStartTime;
    }

    /**
     * encounter time field setter.
     * @param encounterTime time value to set.
     * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
     */
    public Encounter setEncounterTime(String encounterTime) {
        this.encounterEndTime = encounterTime;
        return this;
    }

    /**
     *encounter date field setter.
     * @param encounterDate The encounter date value to set.
     * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
     */
    public Encounter setEncounterDate(String encounterDate) {
        this.encounterEndDate = encounterDate;
        return this;
    }

    /**
     *
     * @return The ID of this object.
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return encounter date of this object
     */
    public String getEncounterStartDate(){
        return this.encounterStartDate;
    }

    /**
     *
     * @return encounter date of this object
     */
    public String getEncounterEndDate(){
        return this.encounterEndDate;
    }


    /**
     *
     * @return encounter date of this object
     */
    public String getEncounterStartTime(){
        return this.encounterStartTime;
    }

    /**
     *
     * @return encounter date of this object
     */
    public String getEncounterEndTime(){
        return this.encounterEndTime;
    }

    //=======================================================================================

    /**
     *
     * @return A string representation of this object.
     */
    //Add end date and time? -Ariela
    @Override
    public String toString() {
        return String.format("%s;%s;%s",this.id,this.encounterStartDate, this.encounterStartTime);
    }


    /**
     * converts the string encoding received via sound to a Encounter object
     * @param enc The string encoding received via sound
     * @return The Encounter object extracted from the received string.
     */
    public static Encounter receiveVisitCard(String enc) throws IndexOutOfBoundsException,IllegalArgumentException{
        //fill empty fields in the end with empty strings
        return new Encounter(enc);
    }

    /**
     * Deletes a visit card from the DB.
     * @param id The id of the visit card to delete
     * @param db The DB instance.
     * @return success/failure of the operation
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean deleteVC (String id, SVCDB db){
        try{
            return db.deleteVC(id);
        } catch (SQLiteException e){
            return false;
        }

    }

    /**
     * gets the list of visit cards owned by the user.
     * @param db The DB instance
     * @return A list of visit cards (empty if user owns none).
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ArrayList<Encounter> getUserVisitCards(SVCDB db){
        try{
            return db.getUserVisitCards();
        } catch (SQLiteException e){
            return null;
        }

    }

    /**
     * Adds a visit card to the DB.
     * @param en The encounter to add.
     * @param db The DB instance.
     * @return success/failure of the operation
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean addVC (Encounter en, SVCDB db){

        //Change time from String to time format- Ariela !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
        //do input validation!!
        //get the vc from the database
        SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try{
            System.out.println(en.getId());
            boolean is_exist = db.VCexists(en.getId());
            if(is_exist)
            {
                Encounter original_encounter = db.getVC(en.getId());
                //get the time from DB
                try {
                    //get the date and time from DB
                    String originalEnd = new String(original_encounter.getEncounterEndDate() + " "+ original_encounter.getEncounterEndTime());
                    String newEnd = new String(en.getEncounterEndDate() + " " +en.getEncounterEndTime());

                    //combine date and time to calculate the differance
                    Date originalEncounter = dateAndTimeFormat.parse(originalEnd);
                    Date newEncounter = dateAndTimeFormat.parse(newEnd);

                    //calculate the duration between the two dates.
                    long duration = newEncounter.getTime() - originalEncounter.getTime();

                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    long daysInMilli = hoursInMilli * 24;

                    long elapsedDays = duration / daysInMilli;
                    duration = duration % daysInMilli;

                    long elapsedHours = duration / hoursInMilli;
                    duration = duration % hoursInMilli;

                    long elapsedMinutes = duration / minutesInMilli;
                    duration = duration % minutesInMilli;

                    long elapsedSeconds = duration / secondsInMilli;

                    if((elapsedDays == 0)&&(elapsedHours == 0)&&(((elapsedMinutes == 14)&&(elapsedSeconds <= 59)) ||((elapsedMinutes == 15)&&(elapsedSeconds == 0))||(elapsedMinutes <= 13))){
                        //update encounter
                        return db.editVC(en);
                    }
                    else {
                        //add new encounter
                        return db.addVC(en);
                    }

                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                    return false;
                    } catch (ParseException e) {
                    return false;
                 }
            }

            else{
                //add new encounter
               return db.addVC(en);
            }
        } catch (SQLiteException e){
            return false;
        }
    }


}
