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
    public String getEncounterStartTime(){
        return this.encounterStartTime;
    }

    //=======================================================================================

    /**
     *
     * @return A string representation of this object.
     */
    @Override
    public String toString() {
        return String.format("%d;%s;%s",this.id,this.encounterStartDate, this.encounterStartTime);
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
     * @param email The user email
     * @param db The DB instance
     * @return A list of visit cards (empty if user owns none).
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ArrayList<Encounter> getUserVisitCards(String email, SVCDB db){
        try{
            return db.getUserVisitCards();
        } catch (SQLiteException e){
            return null;
        }

    }

    /**
     * Adds a visit card to the DB.
     * @param vc The visit card to add.
     * @param db The DB instance.
     * @return success/failure of the operation
     */
    public static boolean addVC (Encounter vc, SVCDB db){
        //do input validation!!
        //get the vc from the database

// Add here the time calculation- Ariela
       /* try{
            System.out.println(vc.getId());
            boolean is_exist = db.VCexists(vc.toString());
            if(is_exist)
                return false;
            return db.addVC(vc);
        } catch (SQLiteException e){
            return false;
        }   */
        return true; //change later- Ariela
    }
}
