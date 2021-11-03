package models;

import android.database.sqlite.SQLiteException;
import android.os.Build;

import androidx.annotation.RequiresApi;

import security.Auth;

/**
 * This class connects the <i>User</i> module to the DB. i.e., contains all the CRUD operations for the user.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class User {
    String id;


    private void setId(String id) {
        this.id = id;
    }

    /**
     * converts a string to a UserDTO object.
     * @param user The string representation of a user
     * @return The UserDTO object extracted from the string.
     */
    public User(String user){
        setId(user);
    }

    /**
     *
     * @return id of this object
     */
    public String getId(){
        return this.id;
    }

}

