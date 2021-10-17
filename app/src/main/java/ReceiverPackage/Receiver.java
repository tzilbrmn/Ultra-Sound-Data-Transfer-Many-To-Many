package ReceiverPackage;

import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import FastFourierPackage.Complex;
import FastFourierPackage.FFT;
import Sound.FrequencyConverter;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;

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
    private int StartFrequency;
    private int EndFrequency;
    private int Padding;
    private AudioTrack cAudioTrack = null;
    private Recorder cRecorder = null;
    private ArrayList<byte[]> recordedArray;
    final private String recordedArraySem = "Semaphore";
    private boolean bIsRecording = true;
    private ArrayList<String> ReceivedMsg;
    private int bufferSizeInBytes;

    /**********************************************************************************************
     * function: receiveMsg
     * description: function to receive msg via sound
     * args: settingsArr
     * return: ArrayList<String>
     **********************************************************************************************/
    public ArrayList<String> receiveMsg(Integer[] settingsArr) throws UnsupportedEncodingException {
        Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);

        this.StartFrequency = settingsArr[0];
        this.EndFrequency = settingsArr[1];
        int BitsPerTone = settingsArr[2];
        FrequencyConverter cFrequencyConverter = new FrequencyConverter(StartFrequency, EndFrequency, BitsPerTone);
        this.Padding = cFrequencyConverter.getPadding();
        int HandshakeStartFrequency = cFrequencyConverter.getStartHandShakeFrequency();
        int HandshakeEndFrequency = cFrequencyConverter.getEndHandShakeFrequency();

        recordedArray = new ArrayList<byte[]>();
        cRecorder = new Recorder();
        cRecorder.setCallback(this);
        cRecorder.start();
        boolean bIsListeningStarted = false;
        int startHandShakeCounter = 0;
        int endHandShakeCounter = 0;

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

            Log.d("Debug 2 new", String.valueOf(NewToneFrequency));

            if (!bIsListeningStarted) {
                if ((NewToneFrequency > (HandshakeStartFrequency - this.Padding)) && (NewToneFrequency < (HandshakeStartFrequency + this.Padding))) {
                    startHandShakeCounter++;
                    if (startHandShakeCounter >= 2) { // start listening after receiving 2 startHandShakeFrequency received
                        bIsListeningStarted = true;
                        Log.d("Debug ", "listening Started");
                        Log.d("Debug ", String.valueOf(NewToneFrequency));
                    }
                } else {
                    startHandShakeCounter = 0;
                }
            }
            else{ // bIsListeningStarted = true
                if ((NewToneFrequency > (HandshakeEndFrequency - this.Padding)) && (NewToneFrequency < (HandshakeEndFrequency + this.Padding))) {
                    endHandShakeCounter++;
                    if (endHandShakeCounter >= 2) { // stop listening after 2 endHandShakeFrequency received
                        Log.d("Debug ", "listening End");
                        Log.d("Debug ", String.valueOf(NewToneFrequency));
                        StopRecord();
                    }
                } else {
                    endHandShakeCounter = 0;
                    cFrequencyConverter.calculateBits(NewToneFrequency);
                }
            }
        }

        ReceivedMsg = cFrequencyConverter.getMsgArray();
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
}
