package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

/**
 * This is the helper class of the SQLite DB.<br/>
 * It contains CRUD operations on the DB tables, and everything related to the DB.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class SVCDB extends SQLiteOpenHelper {

    private Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SVCDB.db"; //Change later - Ariela

    //Constants for the VisitCard table
    //TAL - decide what to do

    //Change time from String to time format- Ariela !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
    private static final String VC_TABLE_NAME = "encounterLog";
    private static final String VC_COLUMN_ENCOUNTER_ID = "encounterId";
    private static final String VC_COLUMN_ID = "id"; //this is the PK, the Id of the user with whom the encounter occurred
    private static final String VC_COLUMN_START_DATE = "startDate";
    private static final String VC_COLUMN_END_DATE = "endDate";
    private static final String VC_COLUMN_START_TIME = "startTime";
    private static final String VC_COLUMN_END_TIME = "endTime";

    /**
     *
     * @param context the context of the activity calling the DB.
     */
    public SVCDB(Context context){
        super(context, DATABASE_NAME , null, 1);
        this.context = context;
    }


    /**
     * {@inheritDoc}
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Change time from String to time format- Ariela !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
        // TODO Auto-generated method stub
        db.execSQL(
                "CREATE TABLE `encounterLog` (" +
                        "   'encounterId' int NOT NULL AUTO_INCREMENT, " +
                        "  `id` VARCHAR(255)  PRIMARY KEY, " +
                        "  `startDate` VARCHAR(255), " +
                        "  `endDate` VARCHAR(255), " +
                        "  `startTime` VARCHAR(255), " +
                        "  `endTime` VARCHAR(255) );"
        );
    }

    /**
     * {@inheritDoc}
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS encounterLog");
        onCreate(db);
    }

    //===============================================================================================================================================


    //user related methods


    /**
     * Adds a user to the DB.
     * @param user The user object containing the data to be added.
     * @return success/failure of the operation.
     */
    //DELETE?? - Ariela (a structure for adding new entity to a table)
    public boolean addUser(User user) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();//store all the data from the application and pass to the db tabel
        contentValues.put(VC_COLUMN_ID, user.getId());

        long insert_result = db.insert(VC_TABLE_NAME, null, contentValues);
        return insert_result != -1;
    }


    //===============================================================================================================================================

    //Visit card related methods

    public Encounter getVC(String id) throws SQLiteException{

        //Change time from String to time format- Ariela !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM encounterLog WHERE id = ?";
        //Cursor cursor = db.rawQuery(sql, new String[] { Integer.toString(id) });
        Cursor cursor = db.rawQuery(sql, new String[] { id });
        if(cursor.moveToFirst()){
            String en_id = cursor.getString(cursor.getColumnIndex(VC_COLUMN_ID));
            String startDate = cursor.getString(cursor.getColumnIndex(VC_COLUMN_START_DATE));
            String startTime = cursor.getString(cursor.getColumnIndex(VC_COLUMN_START_TIME));
            String endDate = cursor.getString(cursor.getColumnIndex(VC_COLUMN_END_DATE));
            String endTime = cursor.getString(cursor.getColumnIndex(VC_COLUMN_END_TIME));
            return new Encounter(en_id, startDate, startTime).setEncounterDate(endDate).setEncounterTime(endTime);
        }
        return null;
    }
    /**
     * Gets an encounter end date and time  from the database corresponding to the passed ID (PK).
     * @param id The id field of the encounter to be fetched.
     * @return true or false if the object is in the database
     */
    public boolean VCexists(String id) throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        //String sql = "SELECT endDate, endTime FROM encounterLog WHERE id = ? AND MAX(encounterId)";
        String sql = "SELECT * FROM encounterLog WHERE id = ? AND MAX(encounterId)";
        //Cursor cursor = db.rawQuery(sql, new String[] { id });
        Cursor cursor = db.rawQuery(sql, id);
        if(cursor.getCount()<=0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * Adds a visit card to the DB.
     * @param vc The visit card object containing the data to add.
     * @return success/failure of the operation.
     */
    public boolean addVC(Encounter vc) throws SQLiteException{

        //Change time from String to time format- Ariela !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VC_COLUMN_ID, vc.getId());
        contentValues.put(VC_COLUMN_START_DATE, vc.getEncounterStartDate());
        contentValues.put(VC_COLUMN_END_DATE, vc.getEncounterEndDate());
        contentValues.put(VC_COLUMN_START_TIME, vc.getEncounterStartTime());
        contentValues.put(VC_COLUMN_END_TIME, vc.getEncounterEndTime());

        long insert_result = db.insert(VC_TABLE_NAME, null, contentValues);
        return insert_result != -1;
    }

    /**
     * Updates one or more of the fields of the given visit card.
     * @param vc The visit card object containing the data to change.
     * @return success/failure of the operation.
     */
    public boolean editVC(Encounter vc) throws SQLiteException {
        //Change according to our project- Ariela
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VC_COLUMN_ID, vc.getId());

        long update_result= db.update(VC_TABLE_NAME, contentValues,"id = ?", new String[] { vc.getId() });
        System.out.println("HI " + update_result);
        return update_result != -1;
    }

    /**
     * deletes a visit card.
     * @param id The id field of the visit card to delete.
     * @return success/failure of the operation.
     */
    /*
     * deletes a visit card.
     * @param id The id of the visit card to delete
     * @return success/failure of the operation.
     */
    public boolean deleteVC(String id) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        long delete_result= db.delete(VC_TABLE_NAME,"id = ? ", new String[Integer.parseInt(id)]);
        return delete_result != -1;
    }


    /**
     * Gets a list of all the visit cards owned by the given user.
     * @return The visit cards owned by the user (empty list if user owns none).
     */
    //DELETE???- Ariela
    public ArrayList<Encounter> getUserVisitCards() throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM encounterLog";
        Cursor cursor = db.rawQuery(sql, new String[] { "" });
        ArrayList<Encounter> visitCards = new ArrayList<>();
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            visitCards.add(new Encounter(cursor.getString(Integer.parseInt(VC_COLUMN_ID)), cursor.getString(Integer.parseInt(VC_COLUMN_START_DATE)),
                    cursor.getString(Integer.parseInt(VC_COLUMN_START_TIME))).setEncounterTime(cursor.getString(Integer.parseInt(VC_COLUMN_END_TIME)))
                    .setEncounterDate(cursor.getString(Integer.parseInt(VC_COLUMN_END_DATE))));

            cursor.moveToNext();
        }
        return visitCards;

    }
}
