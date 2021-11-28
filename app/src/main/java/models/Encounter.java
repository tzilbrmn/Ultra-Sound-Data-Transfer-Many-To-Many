package models;

import android.database.sqlite.SQLiteException;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

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


    public Encounter(String id, String date, String time){
        this.id = id;
        this.encounterStartDate = date;
        this.encounterStartTime = time;
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
        String[] info = enc.split(";");
        //fill empty fields in the end with empty strings
        info = utils.fillArray(info,1);
        return new Encounter(info[0], info[1], info[2]);
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

// Add here the time calculation- Ariela
        try{
            System.out.println(en.getId());
            boolean is_exist = db.VCexists(en.toString());
            if(is_exist)
            {
                Encounter original_encounter = db.getVC(en.getId());

                try {
                    int endDateOriginal = Integer.parseInt(original_encounter.getEncounterEndDate());
                    int endTimeOriginal = Integer.parseInt(original_encounter.getEncounterEndTime());
                    int endDateNewEncounter =  Integer.parseInt(en.getEncounterEndDate());
                    int endTimeNewEncounter =  Integer.parseInt(en.getEncounterEndTime());

                } catch(NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
        //Calculate time differance- Ariela!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
                return false;
            }
            return db.addVC(en);
        } catch (SQLiteException e){
            return false;
        }
        //return true; //change later- Ariela
    }
}
