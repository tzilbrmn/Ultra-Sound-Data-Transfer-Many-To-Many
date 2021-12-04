package com.example.svc;

import static Utils.utils.strToBinary;

import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class CommunicationNetwork {
    String frame;

    /**
     * Builds the frame according to the user's id
     * @param id The id of the user.
     * @return If the process succeeded.
     */
    public boolean composeFrame(String id) {
        String origBinaryRep = Utils.utils.convertStringToHex(id);
        String frame = 'F' + origBinaryRep + 'F';
        String checksum = calcChecksum(id);
        if (checksum.length() < 2)
            checksum = '0' + checksum;
        frame += checksum;

        if (frame.length() == 27) {
            frame = Utils.utils.strToBinary(frame);
            this.frame = frame;
            return true;
        }
        return false;
    }

    /**
     * Calculates the checksum of the message (according to the data segment)
     * @param str The data to calculate the checksum for.
     * @return The checksum calculated.
     */
    public static String calcChecksum(String str){

        char[] ch = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            ch[i] = str.charAt(i);
        }
        int num = 0;
        int len = str.length();
        while(len!=0){
            num+= Character.getNumericValue(ch[len-1]);
            len = len-1;
        }
        String checkSum = Integer.toHexString(num);

        return checkSum;
    }

}
