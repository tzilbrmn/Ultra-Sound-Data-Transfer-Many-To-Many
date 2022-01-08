package models;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
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

        // TODO Auto-generated method stub
        //SQLiteDatabase db = new SQLiteDatabase("db");

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS `encounterLog` (" +
                        "  `encounterId` INTEGER PRIMARY KEY, " +
                        "  `id` VARCHAR(255) , " +
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

    //Visit card related methods

    public Encounter getVC(String id) throws SQLiteException{

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM encounterLog WHERE id = ? ORDER BY encounterId DESC LIMIT 1";
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
     * @param idToCheck The id field of the encounter to be fetched.
     * @return true or false if the object is in the database
     */
    public boolean VCexists(String idToCheck) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        this.onCreate(db);
        //SQLiteDatabase db = myGetWritableDatabase();
        //SQLiteDatabase db =  SQLiteDatabase.openDatabase("SVC", null, SQLiteDatabase.OPEN_READWRITE);
        //.getDatabasePath("SVC");
        //String sql = "SELECT endDate, endTime FROM encounterLog WHERE id = ? AND MAX(encounterId)";
        String sql = "SELECT * FROM 'encounterLog' WHERE id = ? ORDER BY encounterId DESC LIMIT 1";
        //Cursor cursor = db.rawQuery(sql, new String[] { id });
        Cursor cursor = db.rawQuery(sql, new String[] { idToCheck });
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
        contentValues.put(VC_COLUMN_END_DATE, vc.getEncounterEndDate());
        contentValues.put(VC_COLUMN_END_TIME, vc.getEncounterEndTime());

//table name, name of values to change, where, the new valuse.
        long update_result= db.update(VC_TABLE_NAME, contentValues,"id = ?", new String[] { vc.getId() });
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
     * deletes a visit card.
     * @return success/failure of the operation.
     */
    /*
     * deletes a visit card.
     * @param id The id of the visit card to delete
     * @return success/failure of the operation.
     */
    public void dropTable() throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE `encounterLog`;");
    }


    /**
     * Gets a list of all the visit cards owned by the given user.
     * @return The visit cards owned by the user (empty list if user owns none).
     */
    public ArrayList<Encounter> getUserVisitCards() throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM encounterLog";
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<Encounter> visitCards = new ArrayList<>();
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            visitCards.add(new Encounter(cursor.getString(cursor.getColumnIndex(VC_COLUMN_ID)), cursor.getString(cursor.getColumnIndex(VC_COLUMN_START_DATE)),
                    cursor.getString(cursor.getColumnIndex(VC_COLUMN_START_TIME))).setEncounterTime(cursor.getString(cursor.getColumnIndex(VC_COLUMN_END_TIME)))
                    .setEncounterDate(cursor.getString(cursor.getColumnIndex(VC_COLUMN_END_DATE))));

            cursor.moveToNext();
        }
        return visitCards;

    }
}
