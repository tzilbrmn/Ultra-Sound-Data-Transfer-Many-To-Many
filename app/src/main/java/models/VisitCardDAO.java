package models;

import android.database.sqlite.SQLiteException;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

/**
 * This class connects the <i>Visit Card</i> module to the DB. i.e., contains all the CRUD operations for the visit card.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class VisitCardDAO {

    /**
     * gets the list of visit cards owned by the user.
     * @param email The user email
     * @param db The DB instance
     * @return A list of visit cards (empty if user owns none).
     */
    public static ArrayList<VisitCardDTO> getUserVisitCards(String email,SVCDB db){
        try{
            return db.getUserVisitCards(email);
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
    public static boolean addVC (VisitCardDTO vc, SVCDB db){
        //do input validation!!
        //get the vc from the database

        try{
            System.out.println(vc.getPosition_title());
            boolean is_exist = db.VCexists(vc.getEmail(),vc.getFirst_name(),vc.getLast_name());
            if(is_exist)
                return false;
            return db.addVC(vc);
        } catch (SQLiteException e){
            return false;
        }
    }

    /**
     * Modifies an existing visit card in the DB
     * @param vc The changes to apply to the visit card.
     * @param db The DB instance.
     * @return success/failure of the operation
     */
    public static boolean editVC (VisitCardDTO vc, SVCDB db){
        //do input validation!!
        //get the vc from the database
        try{
            return db.editVC(vc);
        } catch (SQLiteException e){
            return false;
        }
    }

    /**
     * Deletes a visit card from the DB.
     * @param id The id of the visit card to delete
     * @param db The DB instance.
     * @return success/failure of the operation
     */
    public static boolean deleteVC (int id, SVCDB db){
        try{
            return db.deleteVC(id);
        } catch (SQLiteException e){
            return false;
        }

    }
}
