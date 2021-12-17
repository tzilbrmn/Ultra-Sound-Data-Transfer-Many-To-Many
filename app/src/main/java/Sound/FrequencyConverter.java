package Sound;

import android.util.Log;

import Utils.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**********************************************************************************************
 * Class: FrequencyConverter
 * Description:
 * functions:
 *      public FrequencyConverter(int startFrequency, int endFrequency, int numberOfBitsInOneTone)
 *      public ArrayList<Integer> calculateMessageFrequencies(String Msg2Send)
 *      public void calculateBits(double frequency)
 *
 **********************************************************************************************/
public class FrequencyConverter {

    private int padding;
    private ArrayList<Byte> readBytes;
    private byte currByte;
    private int currShift;
    private ArrayList<String> MsgArray;

    private ArrayList<String> MsgArrayChecksum;
    private ArrayList<String> MsgArrayNoChecksum;
    Map<Integer, String> frequenciesToFourBit;

    /**********************************************************************************************
     * function: FrequencyConverter
     * description: constructor
     * args: int startFrequency, int endFrequency, int numberOfBitsInOneTone
     **********************************************************************************************/
    // Tal & Ariela - Can change numberOfBitsInOneTone to hardcoded size of msg
    public FrequencyConverter(){
        this.padding= 100;
        readBytes=new ArrayList<Byte>();
        currByte=0x00;
        currShift=0;
        MsgArray = new ArrayList<String>();
        MsgArrayChecksum = new ArrayList<String>();
        MsgArrayNoChecksum = new ArrayList<String>();
        frequenciesToFourBit = new HashMap<Integer, String>();
        for(int i = 17600 ; i < 17700; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0000");
        }
        for(int i = 17700 ; i < 17800; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0001");
        }
        for(int i = 17800 ; i < 17900; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0010");
        }
        for(int i = 17900 ; i < 18000; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0011");
        }
        for(int i = 18000 ; i < 18100; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0100");
        }
        for(int i = 18100 ; i < 18200; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0101");
        }
        for(int i = 18200 ; i < 18300; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0110");
        }
        for(int i = 18300 ; i < 18400; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "0111");
        }
        for(int i = 18400 ; i < 18500; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1000");
        }
        for(int i = 18500 ; i < 18600; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1001");
        }
        for(int i = 18600 ; i < 18700; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1010");
        }
        for(int i = 18700 ; i < 18800; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1011");
        }
        for(int i = 18800 ; i < 18900; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1100");
        }
        for(int i = 18900 ; i < 19000; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1101");
        }
        for(int i = 19000 ; i < 19100; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1110");
        }
        for(int i = 19100 ; i < 19200; i++)
        {
            frequenciesToFourBit.put(new Integer(i), "1111");
        }
    }

    /**********************************************************************************************
     * function: calculateMessageFrequencies
     * description: function to calculate frequency for a specific message
     * args: Msg2Send
     * return: ArrayList<Integer>
     **********************************************************************************************/
    public ArrayList<Integer> calculateMessageFrequencies(String msg2Send){
        ArrayList<Integer> freqArr = new ArrayList<Integer>();
        ArrayList<Integer> testFreq = new ArrayList<Integer>();
        Log.d("msg to send = ", msg2Send);
        //this should not happen because er convert our bit from byte and byte size is 8.
        while(msg2Send.length() % 4 != 0)
        {
            msg2Send += '0';
        }
        for(int i=0; i<msg2Send.length(); i+=4){
            String valueStr = msg2Send.substring(i,i+4);
            if(valueStr.compareTo("0000") == 0)
            {
                freqArr.add(new Integer(17650));
            }
            if(valueStr.compareTo("0001") == 0)
            {
                freqArr.add(new Integer(17750));
            }
            if(valueStr.compareTo("0010") == 0)
            {
                freqArr.add(new Integer(17850));
            }
            if(valueStr.compareTo("0011") == 0)
            {
                freqArr.add(new Integer(17950));
            }
            if(valueStr.compareTo("0100") == 0)
            {
                freqArr.add(new Integer(18050));
            }
            if(valueStr.compareTo("0101") == 0)
            {
                freqArr.add(new Integer(18150));
            }
            if(valueStr.compareTo("0110") == 0)
            {
                freqArr.add(new Integer(18250));
            }
            if(valueStr.compareTo("0111") == 0)
            {
                freqArr.add(new Integer(18350));
            }
            if(valueStr.compareTo("1000") == 0)
            {
                freqArr.add(new Integer(18450));
            }
            if(valueStr.compareTo("1001") == 0)
            {
                freqArr.add(new Integer(18550));
            }
            if(valueStr.compareTo("1010") == 0)
            {
                freqArr.add(new Integer(18650));
            }
            if(valueStr.compareTo("1011") == 0)
            {
                freqArr.add(new Integer(18750));
            }
            if(valueStr.compareTo("1100") == 0)
            {
                freqArr.add(new Integer(18850));
            }
            if(valueStr.compareTo("1101") == 0)
            {
                freqArr.add(new Integer(18950));
            }
            if(valueStr.compareTo("1110") == 0)
            {
                freqArr.add(new Integer(19050));
            }
            if(valueStr.compareTo("1111") == 0)
            {
                freqArr.add(new Integer(19150));
            }
        }
        return freqArr;
    }


    /**********************************************************************************************
     * function: calculateBits
     * description: function to calculate bit from given frequency
     * args: frequency
     * return: void
     **********************************************************************************************/
    public void calculateBits(double frequency) {  /* compiled code */
    int frequencyInt = (int) Math.round(frequency);
        Log.d("we got = ", String.valueOf(frequencyInt));
        if(frequencyInt > 17600 && frequencyInt < 19200)
        {
            String charReceived = Utils.utils.binaryToText(frequenciesToFourBit.get(new Integer(frequencyInt)));
            charReceived = Integer.toHexString(Integer.valueOf(charReceived));
            Log.d("The char:", charReceived);
            MsgArray.add(charReceived);
            if (MsgArray.size() <= 25 && !charReceived.equals("f")) {
                MsgArrayNoChecksum.add(charReceived);
            }
            if (MsgArray.size() > 25 && MsgArray.size() < 28)
            {
                MsgArrayChecksum.add(charReceived);
            }
        }
    }

    /**********************************************************************************************
     * function: getPadding
     * description: getter function to send padding
     * args:
     * return: int
     **********************************************************************************************/
    public int getPadding() {
        return this.padding;
    }


    /**********************************************************************************************
     * function: getMsgArray
     * description: getter function send msgArray
     * args:
     * return: ArrayList<String>
     **********************************************************************************************/
    public ArrayList<String> getMsgArray() { return this.MsgArray; }


    public ArrayList<String> getMsgArrayNoChecksum() { return MsgArrayNoChecksum; }

    public void setMsgArrayNoChecksum(ArrayList<String> msgArrayNoChecksum) {
        MsgArrayNoChecksum = msgArrayNoChecksum;
    }

    public String getMsgChecksum() {
 /*       ArrayList<String> tmp = new ArrayList<String>((List<String>) MsgArray.subList(MsgArray.size()-2, MsgArray.size()));
        String dataString = Utils.utils.concatArrayList(tmp);

  */
        String dataString = Utils.utils.concatArrayList(MsgArrayChecksum);
        return dataString;
    }

    public void setMsgArrayChecksum(ArrayList<String> msgArrayChecksum) {
        MsgArrayChecksum = msgArrayChecksum;
    }

    public String getFullMsgString() {
        String dataString = Utils.utils.concatArrayList(MsgArray);
        return dataString;
    }

    public int getSizeOfMsg() {
        return MsgArray.size();
    }

    public void clearArrays() {
        MsgArray.clear();
        MsgArrayChecksum.clear();
        MsgArrayNoChecksum.clear();
    }
}
