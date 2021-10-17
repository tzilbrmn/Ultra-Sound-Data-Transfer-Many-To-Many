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
    private static final String USER_COLUMN_EMAIL = "email"; //this is the PK
    private static final String USER_COLUMN_FULL_NAME = "full_name";
    private static final String USER_COLUMN_PASSWORD = "password";

    //Constants for the VisitCard table
    private static final String VC_TABLE_NAME = "visit_card";
    private static final String VC_COLUMN_ID = "id"; //this is the PK
    private static final String VC_COLUMN_OWNER = "owner";
    private static final String VC_COLUMN_EMAIL = "email";
    private static final String VC_COLUMN_PREFIX = "prefix";
    private static final String VC_COLUMN_FIRST_NAME = "first_name";
    private static final String VC_COLUMN_MIDDLE_NAME = "middle_name";
    private static final String VC_COLUMN_LAST_NAME = "last_name";
    private static final String VC_COLUMN_POSITION_TITLE = "position_title";
    private static final String VC_COLUMN_COMPANY = "company";
    private static final String VC_COLUMN_ADDRESS = "address";
    private static final String VC_COLUMN_TELEPHONE = "telephone";
    private static final String VC_COLUMN_FAX = "fax";
    private static final String VC_COLUMN_MOBILE = "mobile";
    private static final String VC_COLUMN_WEBSITE = "website";


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
                        "  `email` VARCHAR(255), " +
                        "  `prefix` VARCHAR(255), " +
                        "  `first_name` VARCHAR(255), " +
                        "  `middle_name` VARCHAR(255), " +
                        "  `last_name` VARCHAR(255), " +
                        "  `position_title` VARCHAR(255) , " +
                        "  `company` VARCHAR(255), " +
                        "  `address` VARCHAR(255), " +
                        "  `telephone` VARCHAR(15), " +
                        "  `fax` VARCHAR(15), " +
                        "  `mobile` VARCHAR(15), " +
                        "  `website` VARCHAR(255), " +
                        "  UNIQUE(`email`,`first_name`,`last_name`), " +
                        "  CONSTRAINT `owner` " +
                        "    FOREIGN KEY (`owner`) " +
                        "    REFERENCES `user` (`email`) " +
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
            String userEmail = cursor.getString(cursor.getColumnIndex(USER_COLUMN_EMAIL));
            String password = cursor.getString(cursor.getColumnIndex(USER_COLUMN_PASSWORD));
            String full_name = cursor.getString(cursor.getColumnIndex(USER_COLUMN_FULL_NAME));
            return new UserDTO.Builder()
                    .setEmail(userEmail)
                    .setPassword(password,false)
                    .setFull_name(full_name)
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
        contentValues.put(USER_COLUMN_EMAIL, user.getEmail());
        contentValues.put(USER_COLUMN_PASSWORD, user.getPassword());
        contentValues.put(USER_COLUMN_FULL_NAME, user.getFull_name());

        long insert_result = db.insert(USER_TABLE_NAME, null, contentValues);
        return insert_result != -1;
    }

    /**
     * removes a user from the DB
     * @param email The email of the user to remove.
     * @return success/failure of the operation.
     */
    public boolean removeUser(String email) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(USER_TABLE_NAME, "email = ? ", new String[] { email}) == 1;
    }

    /**
     * Changes the password of a given user.
     * @param user The user object containing the relevant information.
     * @return success/failure of the operation.
     */
    public boolean editPassword(UserDTO user) throws SQLiteException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_COLUMN_PASSWORD, user.getPassword());


        long update_result= db.update(USER_TABLE_NAME, contentValues,"email = ? ", new String[] {user.getEmail()});
        return update_result != -1;
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
            String email = cursor.getString(cursor.getColumnIndex(VC_COLUMN_EMAIL));
            String prefix = cursor.getString(cursor.getColumnIndex(VC_COLUMN_PREFIX));
            String first_name = cursor.getString(cursor.getColumnIndex(VC_COLUMN_FIRST_NAME));
            String middle_name = cursor.getString(cursor.getColumnIndex(VC_COLUMN_MIDDLE_NAME));
            String last_name = cursor.getString(cursor.getColumnIndex(VC_COLUMN_LAST_NAME));
            String position_title = cursor.getString(cursor.getColumnIndex(VC_COLUMN_POSITION_TITLE));
            String company = cursor.getString(cursor.getColumnIndex(VC_COLUMN_COMPANY));
            String address = cursor.getString(cursor.getColumnIndex(VC_COLUMN_ADDRESS));
            String telephone = cursor.getString(cursor.getColumnIndex(VC_COLUMN_TELEPHONE));
            String fax = cursor.getString(cursor.getColumnIndex(VC_COLUMN_FAX));
            String mobile = cursor.getString(cursor.getColumnIndex(VC_COLUMN_MOBILE));
            String website = cursor.getString(cursor.getColumnIndex(VC_COLUMN_WEBSITE));

            return new VisitCardDTO.Builder().
                    setId(vc_id).
                    setOwner(owner).
                    setEmail(email).
                    setPrefix(prefix).
                    setFirst_name(first_name).
                    setMiddle_name(middle_name).
                    setLast_name(last_name).
                    setPosition_title(position_title).
                    setCompany(company).
                    setAddress(address).
                    setTelephone(telephone).
                    setFax(fax).
                    setMobile(mobile).
                    setWebsite(website).
                    build();
        }
        return null;
    }
    /**
     * Gets a visit card  from the database corresponding to the passed ID (PK).
     * @param email The email field of the visit card to be fetched.
     * @param first_name The first name field of the visit card to be fetched.
     * @param last_name The last name field of the visit card to be fetched.
     * @return A visit card object containing all the data (null if not found)
     */
    public boolean VCexists(String email, String first_name, String last_name) throws SQLiteException{
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM visit_card WHERE email = ? AND first_name = ? AND last_name = ? ";
        Cursor cursor = db.rawQuery(sql, new String[] { email,first_name,last_name });

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
        contentValues.put(VC_COLUMN_EMAIL, vc.getEmail());
        contentValues.put(VC_COLUMN_PREFIX, vc.getPrefix());
        contentValues.put(VC_COLUMN_FIRST_NAME, vc.getFirst_name());
        contentValues.put(VC_COLUMN_MIDDLE_NAME, vc.getMiddle_name());
        contentValues.put(VC_COLUMN_LAST_NAME, vc.getLast_name());
        contentValues.put(VC_COLUMN_POSITION_TITLE, vc.getPosition_title());
        contentValues.put(VC_COLUMN_COMPANY, vc.getCompany());
        contentValues.put(VC_COLUMN_ADDRESS, vc.getAddress());
        contentValues.put(VC_COLUMN_TELEPHONE, vc.getTelephone());
        contentValues.put(VC_COLUMN_FAX, vc.getFax());
        contentValues.put(VC_COLUMN_MOBILE, vc.getMobile());
        contentValues.put(VC_COLUMN_WEBSITE, vc.getWebsite());
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
        contentValues.put(VC_COLUMN_EMAIL, vc.getEmail());
        contentValues.put(VC_COLUMN_PREFIX, vc.getPrefix());
        contentValues.put(VC_COLUMN_FIRST_NAME, vc.getFirst_name());
        contentValues.put(VC_COLUMN_MIDDLE_NAME, vc.getMiddle_name());
        contentValues.put(VC_COLUMN_LAST_NAME, vc.getLast_name());
        contentValues.put(VC_COLUMN_POSITION_TITLE, vc.getPosition_title());
        contentValues.put(VC_COLUMN_COMPANY, vc.getCompany());
        contentValues.put(VC_COLUMN_ADDRESS, vc.getAddress());
        contentValues.put(VC_COLUMN_TELEPHONE, vc.getTelephone());
        contentValues.put(VC_COLUMN_FAX, vc.getFax());
        contentValues.put(VC_COLUMN_MOBILE, vc.getMobile());
        contentValues.put(VC_COLUMN_WEBSITE, vc.getWebsite());

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
            String prefix = cursor.getString(cursor.getColumnIndex(VC_COLUMN_PREFIX));
            String first_name = cursor.getString(cursor.getColumnIndex(VC_COLUMN_FIRST_NAME));
            String middle_name = cursor.getString(cursor.getColumnIndex(VC_COLUMN_MIDDLE_NAME));
            String last_name = cursor.getString(cursor.getColumnIndex(VC_COLUMN_LAST_NAME));
            String position_title = cursor.getString(cursor.getColumnIndex(VC_COLUMN_POSITION_TITLE));
            String company = cursor.getString(cursor.getColumnIndex(VC_COLUMN_COMPANY));
            String address = cursor.getString(cursor.getColumnIndex(VC_COLUMN_ADDRESS));
            String telephone = cursor.getString(cursor.getColumnIndex(VC_COLUMN_TELEPHONE));
            String fax = cursor.getString(cursor.getColumnIndex(VC_COLUMN_FAX));
            String mobile = cursor.getString(cursor.getColumnIndex(VC_COLUMN_MOBILE));
            String website = cursor.getString(cursor.getColumnIndex(VC_COLUMN_WEBSITE));


            visitCards.add(new VisitCardDTO.Builder().
                                            setId(id).
                                            setOwner(userEmail).
                                            setEmail(email).
                                            setPrefix(prefix).
                                            setFirst_name(first_name).
                                            setMiddle_name(middle_name).
                                            setLast_name(last_name).
                                            setPosition_title(position_title).
                                            setCompany(company).
                                            setAddress(address).
                                            setTelephone(telephone).
                                            setFax(fax).
                                            setMobile(mobile).
                                            setWebsite(website).
                                            build()
            );
            cursor.moveToNext();
        }
        return visitCards;

    }
}
