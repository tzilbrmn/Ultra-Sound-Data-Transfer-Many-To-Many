package SenderPackage;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

import Sound.FrequencyConverter;

/**********************************************************************************************
 * Class: Sender
 * Description:
 * functions:
 *      public void sendMsg(Integer[] settingsArr)
 *      void PlayTone(double freqOfTone, double duration)
 *      public void setMsg2Send(String sMsg2Send)
 **********************************************************************************************/
public class Sender {

    private String Msg2Send;
    private double durationTime = 0.27; // time to play tone -> play on 0.18, optimal on 0.20, best on 0.27
    private int sampleRate = 44100; // Number of samples in 1 second
    private AudioTrack MyAudioTrack = null;
    private int StartFrequency;
    private int EndFrequency;
    private int BitsPerTone;

    /**********************************************************************************************
     * function: sendMsg
     * description: function to send msg via sound
     * args: settingsArr
     * return: void
     **********************************************************************************************/
    public void sendMsg(Integer[] settingsArr) {
        StartFrequency = settingsArr[0];
        EndFrequency = settingsArr[1];
        BitsPerTone = settingsArr[2];

        FrequencyConverter cFrequencyConverter = new FrequencyConverter(StartFrequency, EndFrequency, BitsPerTone);
        ArrayList<Integer> MsgFrequencies = cFrequencyConverter.calculateMessageFrequencies(this.Msg2Send);

        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        MyAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MyAudioTrack.setVolume(MyAudioTrack.getMaxVolume());
        }

        MyAudioTrack.play();

        int NumOfHandShakes = 4;
        int NumOfTones = MsgFrequencies.size() + NumOfHandShakes;

        // start handshake
        this.PlayTone((double) cFrequencyConverter.getStartHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getStartHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getStartHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getStartHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getStartHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getStartHandShakeFrequency(), this.durationTime);
        Log.d("sending = ", String.valueOf(MsgFrequencies));
//         start playing msg
        for (int freq : MsgFrequencies) {
            Log.d("sending = ", String.valueOf(freq));
            this.PlayTone((double) freq, 0.04);
        }

        // end handshake TODO taimor: check how many times do we really need
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayTone((double) cFrequencyConverter.getEndHandShakeFrequency(), this.durationTime);
        this.PlayMinimalAmplitude((double) cFrequencyConverter.getEndHandShakeFrequency(), 0.3);
        MyAudioTrack.release();
    }

    /**********************************************************************************************
     * function: PlayTone
     * description: function to play a specific frequency for a specific duration
     * args: freqOfTone, duration
     * return: void
    **********************************************************************************************/
    private void PlayTone(double freqOfTone, double duration) {
        int NumOfSamplesPerDuration = (int) Math.ceil(duration * sampleRate);// number of samples in give duration
        double sample[] = new double[(int) NumOfSamplesPerDuration];
        byte generatedSnd[] = new byte[2 * (int) NumOfSamplesPerDuration]; // each sample is a 16bits = 2bytes
        double anglePadding = (freqOfTone * 2 * Math.PI) / (sampleRate);
        double angleCurrent = 0;
        for (int i = 0; i < NumOfSamplesPerDuration; ++i) {
            sample[i] = Math.sin(angleCurrent);
            angleCurrent += anglePadding;
        }

        // TODO taimor: re-arrange all these loops
        //Convert to 16 bit pcm (pulse code modulation) sound array
        //assumes the sample buffer is normalized.
        int idx = 0;
        int i = 0;
        //Amplitude ramp as a percent of sample count
        int ramp = NumOfSamplesPerDuration / 30;
        //Ramp amplitude up (to avoid clicks)
        for (i = 0; i < ramp; ++i) {
            double dVal = sample[i];
            //Ramp up to maximum
            final short val = (short) ((dVal * 32767 * i / ramp));
            //In 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        // Max amplitude for most of the samples
        for (i = i; i < NumOfSamplesPerDuration - ramp; ++i) {
            double dVal = sample[i];
            //Scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            //In 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        //Ramp amplitude down
        for (i = i; i < NumOfSamplesPerDuration; ++i) {
            double dVal = sample[i];
            //Ramp down to zero
            final short val = (short) ((dVal * 32767 * (NumOfSamplesPerDuration - i) / ramp));
            //In 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        try {
            MyAudioTrack.write(generatedSnd, 0, generatedSnd.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**********************************************************************************************
     * function: PlayMinimalAmplitude
     * description: function to play a specific frequency for a specific duration to remove clicks
     * args: freqOfTone, duration
     * return: void
     **********************************************************************************************/
    private void PlayMinimalAmplitude(double freqOfTone, double duration) {
        int NumOfSamplesPerDuration = (int) Math.ceil(duration * sampleRate);// number of samples in give duration
        double sample[] = new double[(int) NumOfSamplesPerDuration];
        byte generatedSnd[] = new byte[2 * (int) NumOfSamplesPerDuration]; // each sample is a 16bits = 2bytes
        double anglePadding = (freqOfTone * 2 * Math.PI) / (sampleRate);
        double angleCurrent = 0;
        for (int i = 0; i < NumOfSamplesPerDuration; ++i) {
            sample[i] = Math.sin(angleCurrent);
            angleCurrent += anglePadding;
        }
        // TODO taimor: re-arrange all these loops
        //Convert to 16 bit pcm (pulse code modulation) sound array
        //assumes the sample buffer is normalized.
        int idx = 0;
        int i = 0;
        //Amplitude ramp as a percent of sample count
        for (i = i; i < NumOfSamplesPerDuration; ++i) {
            double dVal = sample[i];
            //Ramp down to zero
            final short val = (short) ((dVal * 32767 * 0.0001));
            //In 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        try {
            MyAudioTrack.write(generatedSnd, 0, generatedSnd.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**********************************************************************************************
     * function: setMsg2Send
     * description: setter function
     * args: sMsg2Send
     * return: void
     **********************************************************************************************/
    public void setMsg2Send(String sMsg2Send) {
        this.Msg2Send = sMsg2Send;
    }
}
