package ReceiverPackage;

import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import FastFourierPackage.Complex;
import FastFourierPackage.FFT;
import SenderPackage.Sender;
import Sound.FrequencyConverter;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static com.example.svc.CommunicationNetwork.calcChecksum;

import com.example.svc.CommunicationNetwork;

/**********************************************************************************************
 * Class: Receiver
 * Description:
 * functions:
 *      public ArrayList<String> receiveMsg(Integer[] settingsArr)
 *      public double calculateFFT(byte[] signal)
 *      public void StopRecord()
 *      public void onBufferAvailable(byte[] buffer)
 *      public void setBufferSize(int size)
 **********************************************************************************************/
public class Receiver implements CallBack{

    private int SampleRate = 44100;
    private double durationTime = 0.27;
    private int Padding;
    private AudioTrack cAudioTrack = null;
    private Recorder cRecorder = null;
    private ArrayList<byte[]> recordedArray;
    final private String recordedArraySem = "Semaphore";
    private boolean bIsRecording = true;
    private ArrayList<String> ReceivedMsg;
    private int bufferSizeInBytes;
    private Sender sender;
    private CommunicationNetwork communicationNet;
    private boolean errorMsgAccured = false;
    private boolean errorTimeOut = false;
    private boolean isIdle = false;


    public Receiver() {
        this.sender = new Sender();
    }
    /**********************************************************************************************
     * function: receiveMsg
     * description: function to receive msg via sound
     * args: settingsArr
     * return: ArrayList<String>
     **********************************************************************************************/
    public ArrayList<String> receiveMsg(CommunicationNetwork cm) throws UnsupportedEncodingException {
        Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);

        FrequencyConverter cFrequencyConverter = new FrequencyConverter();
        this.Padding = cFrequencyConverter.getPadding();
        this.communicationNet = cm;

        recordedArray = new ArrayList<byte[]>();
        cRecorder = new Recorder();
        cRecorder.setCallback(this);
        cRecorder.start();
        boolean bIsListeningStarted = false;

        boolean msgReceived = false;

        while (bIsRecording) {
            //Wait and get recorded data
            byte[] NewTone;
            synchronized (recordedArraySem) {
                while (recordedArray.isEmpty()) {
                    try {
                        recordedArraySem.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                NewTone = recordedArray.remove(0);
                recordedArraySem.notifyAll();
            }
            double NewToneFrequency = calculateFFT(NewTone);
            Log.d("d", String.valueOf(NewToneFrequency));

            if ((NewToneFrequency > 17600) && (NewToneFrequency < 19200)) {
                Log.d("Debug ", "Note in our range");
                Log.d("Debug ", String.valueOf(NewToneFrequency));
                this.isIdle = false;
            } else {
                this.isIdle = true;
                Log.d("Debug ", "Note out of our range");
                Log.d("Debug ", String.valueOf(NewToneFrequency));
            }
            if (!bIsListeningStarted && !msgReceived) {
                if ((NewToneFrequency > 19100) && (NewToneFrequency < 19200)) { //If we hear 'F'
                    Log.d("Debug 2 new", String.valueOf(NewToneFrequency));
                    bIsListeningStarted = true;
                    Log.d("Debug ", "listening Started");
                    //                   Log.d("Debug ", String.valueOf(NewToneFrequency));
                    cFrequencyConverter = new FrequencyConverter();
                    cFrequencyConverter.calculateBits(NewToneFrequency);
                    msgReceived = true;
                }
            } else { // bIsListeningStarted = true
                if ((NewToneFrequency > 17600) && (NewToneFrequency < 19200)) {
                    Log.d("Debug 3 new", String.valueOf(NewToneFrequency));

                    int s = cFrequencyConverter.getSizeOfMsg();
                    if (s >= 29) {
                        int m = 0;
                    }
                    if ((NewToneFrequency > 19100) && (NewToneFrequency < 19200) &&
                            (cFrequencyConverter.getSizeOfMsg() <= 4)) {
                        Log.d("Restart!", String.valueOf(cFrequencyConverter.getSizeOfMsg()));
                        Log.d("Debug 2 new AGAIN", String.valueOf(NewToneFrequency));
                        Log.d("Debug ", "listening Started");
                        //                       Log.d("Debug ", String.valueOf(NewToneFrequency));
                        cFrequencyConverter.clearArrays();
                        cFrequencyConverter.calculateBits(NewToneFrequency);
                        msgReceived = true;
                    } else if (cFrequencyConverter.getSizeOfMsg() >= 27) {
                        Log.d("Debug ", "listening End");
                        Log.d("Debug ", String.valueOf(NewToneFrequency));
                        StopRecord();
                    } else {
                        cFrequencyConverter.calculateBits(NewToneFrequency);
                    }
                    // }
                }
                //              else
                //                   StopRecord();
            }
        }
        ArrayList<String> data = cFrequencyConverter.getMsgArrayNoChecksum();
        String dataString = Utils.utils.concatArrayList(data);

        String chksum = calcChecksum(dataString);
        if (msgReceived) {
            Log.d("Checksum Received ", cFrequencyConverter.getMsgChecksum());
            Log.d("Checksum Calculated ", chksum);
            Log.d("Full Message", cFrequencyConverter.getFullMsgString());
            if (chksum.equals(cFrequencyConverter.getMsgChecksum().toString()))
                ReceivedMsg = cFrequencyConverter.getMsgArrayNoChecksum();
            else {
                sender.sendErrorDetected();
                Log.d("Debug ", "Error receiving the frame.");

            }
        }
        return ReceivedMsg;
        }

    /**********************************************************************************************
     * function: calculateFFT
     * description: function to calculate FFT for a signal
     * args: signal
     * return: double
     **********************************************************************************************/
    public double calculateFFT(byte[] signal) {
        final int NumberOfFFTPoints = 1024;
        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[NumberOfFFTPoints];

        for (int i = 0; i < NumberOfFFTPoints; i++) {
            temp = (double) ((signal[2 * i] & 0xFF) | (signal[2 * i + 1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp, 0.0);
        }

        y = FFT.fft(complexSignal);

        double[] magnitude = new double[NumberOfFFTPoints / 2];

        // calculate power spectrum (magnitude) values from fft[]
        for (int i = 0; i < (NumberOfFFTPoints / 2) - 1; ++i) {
            double real = y[i].re();
            double imaginary = y[i].im();
            magnitude[i] = Math.sqrt(real * real + imaginary * imaginary);
        }

        // find largest peak in power spectrum
        double max_magnitude = -100;
        int max_index = 0;
        for (int i = 0; i < magnitude.length; ++i) {
            double frequency = SampleRate * i / NumberOfFFTPoints;
            if ((magnitude[i] > max_magnitude) && (frequency > (17300) && frequency < (19400))) {
                max_magnitude = (int) magnitude[i];
                max_index = i;
            }
        }

        double freq = SampleRate * max_index / NumberOfFFTPoints;
        return freq;

    }

    /**********************************************************************************************
     * function: StopRecord
     * description: function call to stop recording
     * args:
     * return: void
     **********************************************************************************************/
    public void StopRecord(){
        if(cRecorder !=null){
            cRecorder.stop();
            cRecorder =null;
        }
        this.bIsRecording =false;
    }

    /**********************************************************************************************
     * function: onBufferAvailable
     * description: // TODO fares: add description
     * args: buffer
     * return: void
     **********************************************************************************************/
    @Override
    public void onBufferAvailable(byte[] buffer) {
        synchronized (recordedArraySem){
            recordedArray.add(buffer);
            recordedArraySem.notifyAll();
            while(recordedArray.size()>100){ // TODO fares: why 100?
                try {
                    recordedArraySem.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**********************************************************************************************
     * function: setBufferSize
     * description: setter function to set buffer size
     * args: size
     * return: void
     **********************************************************************************************/
    @Override
    public void setBufferSize(int size) {
        bufferSizeInBytes=size;
    }


    public boolean receiveError() throws UnsupportedEncodingException, InterruptedException {
        Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);

        //ArrayList<String> Msg = new ArrayList();
        FrequencyConverter cFrequencyConverter = new FrequencyConverter();
        FrequencyConverter nFrequencyConverter = new FrequencyConverter();
        this.Padding = cFrequencyConverter.getPadding();

        recordedArray = new ArrayList<byte[]>();
        cRecorder = new Recorder();
        cRecorder.setCallback(this);
        cRecorder.start();
        boolean bIsListeningStarted = false;
        int countOutOfRangeTone = 0;
        bIsRecording = true;

        ArrayList<String> errorMsg = new ArrayList<String>();
        errorMsg.add("f");
        errorMsg.add("f");
        errorMsg.add("f");

        Thread errorTime = new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("Debug ", "Listen for errors");
                    //errorTimeOut = true;
                    Thread.sleep(2000);
                    errorTimeOut = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };


        while (bIsRecording) {
            //Wait and get recorded data
            byte[] NewTone;
            synchronized (recordedArraySem) {
                while (recordedArray.isEmpty()) {
                    try {
                        recordedArraySem.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                NewTone = recordedArray.remove(0);
                recordedArraySem.notifyAll();
            }
            try{
                errorTime.start();
                errorTimeOut = true;
                while(errorTimeOut){
                    double NewToneFrequency = calculateFFT(NewTone);
                    Log.d("d error", String.valueOf(NewToneFrequency));

                    if ((NewToneFrequency > 19100) && (NewToneFrequency < 19200)) {
                        Log.d("Debug error", String.valueOf(NewToneFrequency));
                        cFrequencyConverter.calculateBits(NewToneFrequency);

                        if(cFrequencyConverter.getSizeOfMsg()>=3){
                            Log.d("Debug ", "Error listening End");
                            errorTimeOut = false;
                            errorTime.stop();
                            StopRecord();
                        }
                    }
              /*  else{
                    Log.d("Debug ", "Not Error Tone");
                    countOutOfRangeTone++;
                    nFrequencyConverter.calculateBits(NewToneFrequency);

                    if(nFrequencyConverter.getSizeOfMsg() >=3 || countOutOfRangeTone >=3){
                        Log.d("Debug ", "No Error Msg");
                        StopRecord();
                    }
                }

               */
                }
                errorTime.join();
            }catch (InterruptedException | IllegalThreadStateException e) {
                e.printStackTrace();
                break;
            }
            }

            ReceivedMsg = cFrequencyConverter.getMsgArray();
        this.errorMsgAccured = ReceivedMsg.equals(errorMsg);
        //StopRecord();
            return this.errorMsgAccured;
        }

  /*  public boolean checkIsIdle() throws UnsupportedEncodingException {
        Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);

        byte[] NewTone;
        recordedArray = new ArrayList<byte[]>();
        synchronized (recordedArraySem) {
            while (recordedArray.isEmpty()) {
                try {
                    recordedArraySem.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            NewTone = recordedArray.remove(0);
            recordedArraySem.notifyAll();
        }
        double NewToneFrequency = calculateFFT(NewTone);

        Log.d("Debug 2 new", String.valueOf(NewToneFrequency));
        if ((NewToneFrequency > 17600) && (NewToneFrequency < 19200)) {
                Log.d("Debug ", "Note in our range");
                Log.d("Debug ", String.valueOf(NewToneFrequency));
                this.isIdle = false;
            }
        else this.isIdle = true;
        return this.isIdle;
    }
*/
        public boolean getIsIdle(){ return this.isIdle; }

        public void setIsIdle(boolean val){this.isIdle = val;}

        public boolean getErrorMsg(){ return this.errorMsgAccured; }
}
