package models;

import android.database.sqlite.SQLiteException;
import android.os.Build;

import androidx.annotation.RequiresApi;

import security.Auth;

/**
 * This class connects the <i>User</i> module to the DB. i.e., contains all the CRUD operations for the user.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class UserDAO {

    /**
     * checks the login credentials of the user.
     * @param user The login credentials (email + password)
     * @param db The db instance.
     * @return All the user data fetched from the DB, null if wrong credentials.
     */
    public static UserDTO login(UserDTO user,SVCDB db){
        try{
            //do input validation!!
            //get the user from the database
            UserDTO dbUser = db.getUser(user.getEmail());
            //if the user doesn't exist in db return false
            if (dbUser == null)
                return null;
            //check credentials and send response
            if (Auth.checkPassword(dbUser.getPassword(), user.getPassword()))
                return dbUser;
            return null;
        } catch(SQLiteException e){
            return null;
        }
    }

    /**
     * Adds a user to the DB after registration.
     * @param user The registration info.
     * @param db The DB instance
     * @return success/failure of the operation
     */
    public static boolean signUp(UserDTO user,SVCDB db){
        try{
            //do input validation!!!
            return db.addUser(user);
        } catch(SQLiteException e){
            return false;
        }
    }

    /**
     * Resets the password of the user.
     * @param user The user info.
     * @param db The DB instance
     * @return success/failure of the operation
     */
    public static boolean resetPassword (UserDTO user, SVCDB db){
        try{
            //do input validation!!
            //get the vc from the database
            return db.editPassword(user);
        } catch(SQLiteException e){
            return false;
        }
    }
}

