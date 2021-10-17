package models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import security.Auth;

/**
 * This class encapsulates the user data in an object.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class UserDTO {
    private String email;
    private String password;
    private String full_name;


    private UserDTO(Builder builder){
        this.email = builder.email;
        this.password = builder.password;
        this.full_name = builder.full_name;
    }

    /**
     * The Builder class for the <i>Builder</i> design pattern.
     */
    public static class Builder{
        private String email;
        private String password;
        private String full_name;

        /**
         * Email field setter.
         * @param email The email value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        /**
         * Password field setter.
         * @param password The password value to set
         * @param hashPassword A flag to indicate whether or not to hash the password upon setting.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setPassword(String password, boolean hashPassword) {
            if(hashPassword)
                this.password = Auth.hashPassword(password);
            else
                this.password = password;
            return this;
        }
        /**
         * Full_Name field setter.
         * @param full_name The full_name value to set.
         * @return Builder object as per the recipe of the <i>Builder</i> design pattern.
         */
        public Builder setFull_name(String full_name) {
            this.full_name = full_name;
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
     * @return Email of this object
     */
    public String getEmail(){
        return this.email;
    }

    /**
     *
     * @return Password of this object.
     */
    public String getPassword(){
        return this.password;
    }

    /**
     *
     * @return full_name of this object.
     */
    public String getFull_name(){
        return this.full_name;
    }

    //===================================================================================


    /**
     *
     * @return A String representation of this object
     */
    @Override
    public String toString() {
        return String.format("%s;%s",this.full_name,this.email);
    }


    /**
     * converts a string to a UserDTO object.
     * @param user The string representation of a user
     * @return The UserDTO object extracted from the string.
     */
    public static UserDTO stringToUser(String user){
        String[] info = user.split(";");
        return new UserDTO.Builder().
                            setFull_name(info[0]).
                            setEmail(info[1]).
                            build();
    }
}
