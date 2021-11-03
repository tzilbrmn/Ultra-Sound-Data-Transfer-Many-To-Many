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

    private static final int DATABASE_VERSION = 2; //Check what does it mean- Ariela
    private static final String DATABASE_NAME = "SVCDB.db"; //Change later - Ariela

    //Constants for the VisitCard table
    //TAL - decide what to do
    private static final String VC_TABLE_NAME = "encounterLog";
    private static final String VC_COLUMN_ENCOUNTER_ID = "encounterId";
    private static final String VC_COLUMN_ID = "id"; //this is the PK
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
    }


    /**
     * {@inheritDoc}
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
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
    public boolean addUser(UserDTO user) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_ID, user.getId());

        long insert_result = db.insert(USER_TABLE_NAME, null, contentValues);
        return insert_result != -1;
    }


    //===============================================================================================================================================

    //Visit card related methods

    public VisitCardDTO getVC(int id) throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM encounterLog WHERE id = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { Integer.toString(id) });
        if(cursor.moveToFirst()){
            int vc_id = cursor.getInt(cursor.getColumnIndex(VC_COLUMN_ID));
            String endDate = cursor.getString(cursor.getColumnIndex(VC_COLUMN_END_DATE));
            String endTime = cursor.getString(cursor.getColumnIndex(VC_COLUMN_END_TIME));
            return new VisitCardDTO.Builder().
                    setId(vc_id).
                    setEndDate(endDate).
                    setEndTime(endTime).
                    build();
        }
        return null;
    }
    /**
     * Gets a visit card  from the database corresponding to the passed ID (PK).
     * @param id The id field of the visit card to be fetched.
     * @return A visit card object containing all the data (null if not found)
     */
    public boolean VCexists(String id) throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT endDate, endTime FROM encounterLog WHERE id = ? AND MAX(encounterId)";
        Cursor cursor = db.rawQuery(sql, new String[] { id });
        //Chack if cursor is not empty -Ariela ???????????????????????????????????
        //else:
       return false;
    }

    /**
     * Adds a visit card to the DB.
     * @param vc The visit card object containing the data to add.
     * @return success/failure of the operation.
     */
    public boolean addVC(VisitCardDTO vc) throws SQLiteException{

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VC_COLUMN_ID, vc.getId());
        long insert_result = db.insert(VC_TABLE_NAME, null, contentValues);
        return insert_result != -1;
    }

    /**
     * Updates one or more of the fields of the given visit card.
     * @param vc The visit card object containing the data to change.
     * @return success/failure of the operation.
     */
    public boolean editVC(VisitCardDTO vc) throws SQLiteException {
        //Change according to our project- Ariela
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VC_COLUMN_ID, vc.getId());

        long update_result= db.update(VC_TABLE_NAME, contentValues,"id = ?", new String[] { Integer.toString(vc.getId()) });
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
    public boolean deleteVC(int id) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        long delete_result= db.delete(VC_TABLE_NAME,"id = ? ", new String[] {Integer.toString(id)});
        return delete_result != -1;
    }


    /**
     * Gets a list of all the visit cards owned by the given user.
     * @param userEmail The email of the user owning the visit cards.
     * @return The visit cards owned by the user (empty list if user owns none).
     */
    //DELETE???- Ariela
    public ArrayList<VisitCardDTO> getUserVisitCards(String id) throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM encounterLog WHERE id = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { id });
        ArrayList<VisitCardDTO> visitCards = new ArrayList<>();
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            int id = cursor.getInt(cursor.getColumnIndex(VC_COLUMN_ENCOUNTER_ID));
            System.out.println(id);
            String id = cursor.getString(cursor.getColumnIndex(VC_COLUMN_ID));



            visitCards.add(new VisitCardDTO.Builder().
                                            setId(id).
                                            setEncounter(encounter_time).
                                            setEncounterDate(encounter_date).
                                            build()
            );
            cursor.moveToNext();
        }
        return visitCards;

    }
}
