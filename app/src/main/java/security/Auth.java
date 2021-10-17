package security;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class contains methods that are related to authentication.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class Auth {
    /**
     * Assumes both passwords are hashed.
     * Compares the hashing of both passwords and returns true/false if they're equal.
     * @param originalPassword - the original password
     * @param passwordToCheck - the password to check
     * @return result of compare: equal/not equal.
     */
    //assumes both passwords are hashed.
    public static boolean checkPassword(String originalPassword, String passwordToCheck){
        return originalPassword.equals(passwordToCheck);
    }

    /**
     * hashes the given password using the SHA256 hashing algorithm.
     * @param password - the password to hash.
     * @return the hex representation of the hashed password.
     */
    public static String hashPassword(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHexString(md.digest(password.getBytes(StandardCharsets.UTF_8)));
        }catch (NoSuchAlgorithmException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * converts a byte array to the hex representation of it.
     * @param hash - the byte array.
     * @return the hex representation of the argument.
     */
    private static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

}
