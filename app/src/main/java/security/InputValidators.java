package security;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains all the methods for input validation using REGEX.<br/>
 * How to Use:<br/>
 * Public use consists of only the public constants and the public method <i>validate</i>.<br/>
 * Call the <i>validate</i> method and pass the <i>type</i> of the input and the <i>value</i>.<br/>
 * <code>type</code> can be any of the public Constants (EMAIL, NAME, etc.), it determines which regex to use.<br/>
 * <code>value</code> is the string to match against the regex pattern.<br/>
 */
public class InputValidators {
    /**
     * Public Constant:
     *  EMAIL - used to indicate that the type of the input is <i>email</i>.
     */
    //constants for less errors when used.
    public static final String EMAIL = "EMAIL";
    /**
     * Public Constant:
     *  NAME - used to indicate that the type of the input is <i>name</i>.
     */
    public static final String NAME = "NAME";
    /**
     * Public Constant:
     *  FULL_NAME - used to indicate that the type of the input is <i>full_name</i>.
     */
    public static final String FULL_NAME = "FULL_NAME";
    /**
     * Public Constants:
     *  PASSWORD - used to indicate that the type of the input is <i>password</i>.
     */
    public static final String PASSWORD = "PASSWORD";
    /**
     * Public Constant:
     *  MOBILE - used to indicate that the type of the input is <i>mobile</i>.
     */
    public static final String MOBILE = "MOBILE";
    /**
     * Public Constant:
     *  TELEPHONE - used to indicate that the type of the input is <i>telephone</i>.
     */
    public static final String TELEPHONE = "TELEPHONE";
    /**
     * Public Constant:
     *  FAX - used to indicate that the type of the input is <i>fax</i>.
     */
    public static final String FAX = "FAX";
    /**
     * Public Constant:
     *  WEBSITE - used to indicate that the type of the input is <i>website</i>.
     */
    public static final String WEBSITE = "WEBSITE";

    /**
     * private Constants:
     * nameRegex, passwordRegex, emailRegex, mobileRegex, telephone_faxRegex, websiteRegex - The REGEX patterns for different input types.
     */
    //for user model
    private static final Pattern nameRegex = Pattern.compile("^[a-z]+[^0-9]$",Pattern.CASE_INSENSITIVE); // name must contain only english characters
    private static final Pattern full_nameRegex = Pattern.compile("^[a-z ]+[^0-9]$",Pattern.CASE_INSENSITIVE); // name must contain only english characters and space
    private static final Pattern passwordRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"); // Password must be at least 8 characters long and contain at least 1 digit, 1 small case letter, and 1 upper case letter.
    private static final Pattern emailRegex = Pattern.compile("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$",Pattern.CASE_INSENSITIVE);//works
    //email is invalid
    //=====================================================================================
    //for visit card model
    private static final Pattern mobileRegex = Pattern.compile("^([05].)(\\d{1})(\\d{7})$"); //mobile phone number must start with 05 and contain 10 digits in total.
    private static final Pattern telephone_faxRegex = Pattern.compile("^([02,03,04,08,09].)(\\d{7})$"); //telephone number must start with 02,03,04,08 or 09 and contain 9 digits in total.
    private static final Pattern websiteRegex = Pattern.compile("^(https?:\\/\\/)?(www\\.)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)|(https?:\\/\\/)?(www\\.)?(?!ww)[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,4}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)$",Pattern.CASE_INSENSITIVE);
    //website is invalid

    /**
     * checks if the given value matches the regex of the given type.
     * @param type - type of input (one of the public constants EMAIL, NAME, etc.)
     * @param value - the value to check against the regex.
     * @return boolean - the value matches/does not match the regex.
     */
    public static boolean validate(String type, String value){
        Matcher matcher;
        if(type == EMAIL){
            matcher = emailRegex.matcher(value);
            return matcher.find();
        } else if(type == NAME){
            matcher = nameRegex.matcher(value);
            return matcher.find();
        } else if(type == FULL_NAME){
            matcher = full_nameRegex.matcher(value);
            return matcher.find();
        } else if(type == PASSWORD){
            matcher = passwordRegex.matcher(value);
            return matcher.find();
        }else if(type == MOBILE){
            matcher = mobileRegex.matcher(value);
            return matcher.find();
        }else if(type == TELEPHONE || type == FAX) {
            matcher = telephone_faxRegex.matcher(value);
            return matcher.find();
        }else if(type == WEBSITE){
            matcher = websiteRegex.matcher(value);
            return matcher.find();
        }
        return false;

    }


}
