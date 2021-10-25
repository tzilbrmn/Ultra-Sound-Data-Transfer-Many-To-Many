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

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "SVCDB.db";
    //Constants for the User table
    private static final String USER_TABLE_NAME = "user";
    private static final String USER_COLUMN_ID = "Id"; //this is the PK

    //Constants for the VisitCard table
    //TAL - decide what to do
    private static final String VC_TABLE_NAME = "visit_card";
    private static final String VC_COLUMN_ID = "id"; //this is the PK
    private static final String VC_COLUMN_OWNER = "owner";
    private static final String VC_COLUMN_EMAIL = "email";
    private static final String VC_COLUMN_PREFIX = "prefix";


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
                "CREATE TABLE `user` (`email` VARCHAR(255) PRIMARY KEY, `password` VARCHAR(255) NOT NULL, `full_name` VARCHAR(255));"
        );
        db.execSQL(
                "CREATE TABLE `visit_card` (" +
                        "  `id` INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                        "  `owner` VARCHAR(255), " +
                        "  `Id` VARCHAR(255), " +
                        "  UNIQUE(`Id`), " +
                        "  CONSTRAINT `owner` " +
                        "    FOREIGN KEY (`owner`) " +
                        "    REFERENCES `user` (`Id`) " +
                        "    ON DELETE CASCADE " +
                        "    ON UPDATE CASCADE);"
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
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS visit_card");
        onCreate(db);
    }

    //===============================================================================================================================================


    //user related methods

    /**
     * gets a user from the DB corresponding to the given email (PK)
     * @param email The email of the user to fetch
     * @return The user object retrieved (null if not found)
     */
    public UserDTO getUser(String email) throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM user WHERE email = ?";
        Cursor cursor = db.rawQuery(sql, new String[] {email});
        if(cursor.moveToFirst()){
            String userId = cursor.getString(cursor.getColumnIndex(USER_COLUMN_ID)); //TAL - get from phone
            return new UserDTO.Builder()
                    .setId(userId)
                    .build();
        }
        return null;
    }

    /**
     * Adds a user to the DB.
     * @param user The user object containing the data to be added.
     * @return success/failure of the operation.
     */
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
        String sql = "SELECT * FROM visit_card WHERE id = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { Integer.toString(id) });
        if(cursor.moveToFirst()){
            int vc_id = cursor.getInt(cursor.getColumnIndex(VC_COLUMN_ID));
            String owner = cursor.getString(cursor.getColumnIndex(VC_COLUMN_OWNER));
            return new VisitCardDTO.Builder().
                    setId(vc_id).
                    setOwner(owner).
                    setId(id).
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
        String sql = "SELECT * FROM visit_card WHERE email = ? AND first_name = ? AND last_name = ? ";
        Cursor cursor = db.rawQuery(sql, new String[] { id });

        if(cursor.moveToFirst()){
            String prefix = cursor.getString(cursor.getColumnIndex(VC_COLUMN_PREFIX));
            System.out.println(prefix);
            return true;
        }
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
        contentValues.put(VC_COLUMN_OWNER, vc.getOwner());
        long insert_result = db.insert(VC_TABLE_NAME, null, contentValues);
        return insert_result != -1;
    }

    /**
     * Updates one or more of the fields of the given visit card.
     * @param vc The visit card object containing the data to change.
     * @return success/failure of the operation.
     */
    public boolean editVC(VisitCardDTO vc) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VC_COLUMN_OWNER, vc.getOwner());

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
    public ArrayList<VisitCardDTO> getUserVisitCards(String userEmail) throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM visit_card WHERE owner = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { userEmail });
        ArrayList<VisitCardDTO> visitCards = new ArrayList<>();
        cursor.moveToFirst();

        while(cursor.isAfterLast() == false){
            int id = cursor.getInt(cursor.getColumnIndex(VC_COLUMN_ID));
            System.out.println(id);
            String email = cursor.getString(cursor.getColumnIndex(VC_COLUMN_EMAIL));



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
