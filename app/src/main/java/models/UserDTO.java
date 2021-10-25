package models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import security.Auth;

/**
 * This class encapsulates the user data in an object.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class UserDTO {
    private String id;


    private UserDTO(Builder builder){
        this.id = builder.id;
    }

    /**
     * The Builder class for the <i>Builder</i> design pattern.
     */
    public static class Builder{
        private String id;

        /**
         * id field setter.
         * @param The id value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * builds the UserDTO object.
         * @return the newly built UserDTO object.
         */
        public UserDTO build(){
            return new UserDTO(this);
        }

    }
    //===================================================================================

    /**
     *
     * @return id of this object
     */
    public String getId(){
        return this.id;
    }


    //===================================================================================

    /**
     *
     * @return A String representation of this object
     */
    @Override
    public String toString() {
        return String.format("%s;%s",this.id);
    }

    /**
     * converts a string to a UserDTO object.
     * @param user The string representation of a user
     * @return The UserDTO object extracted from the string.
     */
    public static UserDTO stringToUser(String user){
        String[] info = user.split(";");
        return new UserDTO.Builder().
                            setId(info[1]).
                            build();
    }
}
