package Utils;

import android.content.Context;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class utils {
    /**
     * converts px to dp units
     * @param context The activity context.
     * @param px the px units to convert
     * @return the corresponding dp units.
     */
    public static int dpFromPx(final Context context, final float px) {
        return (int) (px / context.getResources().getDisplayMetrics().density);
    }

    /**
     * converts dp to px units.
     * @param context The activity context.
     * @param dp the dp units to convert
     * @return the corresponding px units.
     */
    public static int pxFromDp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    /**
     * fills the string array with trailing empty strings. <br/>
     * <em style="color: red" >used only with converting a string to a visit card object!!</em>
     * @param arr The array to fill
     * @return A new array of size 14 with trailing empty strings
     */
    public static String[] fillArray(String arr[],int length) {
        String newArr[] = new String[length];
        int i = 0;
        for (; i < arr.length; i++)
            newArr[i] = arr[i];
        for(;i<length;i++)
            newArr[i] = "";

        return newArr;
    }

    /**
     * converts a string to its' binary representation.
     * @param s The string to convert
     * @return The binary representation of <i>s</i>.
     */
    public static String strToBinary(String s) {
        int n = s.length();
        StringBuilder binRes = new StringBuilder("");
        for (int i = 0; i < n; i++) {
            // convert each char to
            // ASCII value
            int val = Integer.valueOf(s.charAt(i));
            // Convert ASCII value to binary
            String bin = "";
            while (val > 0) {
                if (val % 2 == 1)
                    bin += '1';
                else
                    bin += '0';
                val /= 2;
            }
            while(bin.length() < 16)
                bin += '0';
            bin = reverse(bin);
            binRes.append(bin);
        }
        return binRes.toString();
    }

    /**
     * converts a byte array to its' binary representation
     * @param input The byte array to convert
     * @return The binary representation of <i>input</i>.
     */
    public static String byteArrayToBinary(byte[] input) {
        int n = input.length;
        StringBuilder binRes = new StringBuilder("");
        for (int i = 0; i < n; i++) {
            int val = input[i];
            // Convert byte to binary
            String bin = String.format("%8s", Integer.toBinaryString(val & 0xFF)).replace(' ', '0');
            binRes.append(bin);
        }
        return binRes.toString();
    }

    /**
     * Reverses a string.
     * @param input The input String.
     * @return The reverse of <i>input</i>.
     */
    private static String reverse(String input) {
        char[] a = input.toCharArray();
        int l, r = 0;
        r = a.length - 1;

        for (l = 0; l < r; l++, r--)
        {
            // Swap values of l and r
            char temp = a[l];
            a[l] = a[r];
            a[r] = temp;
        }
        return String.valueOf(a);
    }

    /**
     * converts an ArrayList of Strings to one String.
     * @param arrayList The ArrayList input
     * @return A string containing all the elements of the input.
     */
    public static String concatArrayList(ArrayList<String> arrayList){
        StringBuilder sb = new StringBuilder();
        for(String st: arrayList){
            sb.append(st);
        }

        return sb.toString();
    }

    /**
     * converts a binary string back to string.
     * @param binaryRep The binary string
     * @return The original string.
     */
    public static String binaryToText(String binaryRep){
        StringBuilder sb = new StringBuilder();
        int n = binaryRep.length();
        for(int i = 0; (i+16) <= n ; i += 16){
            String binCharCode = binaryRep.substring(i,i+16);
            int charCode = Integer.parseInt(binCharCode, 2);
            String str = Character.valueOf((char)charCode).toString();
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * converts a binary string back to a byte array
     * @param binaryRep The binary string
     * @return The original byte array.
     */
    public static byte[] binaryToByteArray(String binaryRep){
        int n = binaryRep.length();
        ByteBuffer bytes = ByteBuffer.allocate(n/8);
        for(int i = 0; (i+8) <= n ; i += 8){
            String substring = binaryRep.substring(i,i+8);
            Short val = Short.valueOf(substring, 2);
            bytes.put(val.byteValue());
        }
        return bytes.array();
    }




}
