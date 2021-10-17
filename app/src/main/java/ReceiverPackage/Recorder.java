package ReceiverPackage;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.util.Log;

/**********************************************************************************************
 * Class: Recorder
 * Description:
 * functions:
 *      public ArrayList<String> receiveMsg(Integer[] settingsArr)
 *      public double calculateFFT(byte[] signal)
 *      public void StopRecord()
 *      public void onBufferAvailable(byte[] buffer)
 *      public void setBufferSize(int size)
 **********************************************************************************************/
public class Recorder {

    //Recorder parameters
    private int audioSource = MediaRecorder.AudioSource.DEFAULT;
    //Mono=8b, Stereo=16b
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    //16b or 8b per sample
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //Number of samples in 1sec
    private int sampleRate = 44100;
    //Recording thread
    private Thread thread;
    //Callback used to set up filled buffer
    private CallBack callback;

    /**********************************************************************************************
     * function: Recorder
     * description: empty constructor
     * args:
     **********************************************************************************************/
    public Recorder() {}

    /**********************************************************************************************
     * function: setCallback
     * description: function to set callback
     * args: callback
     * return: void
     **********************************************************************************************/
    public void setCallback(CallBack callback) {
        this.callback = callback;
    }

    /**********************************************************************************************
     * function: start
     * description: function to start recording
     * args:
     * return: void
     **********************************************************************************************/
    public void start() {
        if (thread != null) return;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
                int optimalBufSize = 12000;

                //if(optimalBufSize < minBufferSize){
                    optimalBufSize = minBufferSize;
                //}
                callback.setBufferSize(optimalBufSize);
                AudioRecord recorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioEncoding, optimalBufSize);
                if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                    Thread.currentThread().interrupt();
                    return;
                } else {
                    Log.i(Recorder.class.getSimpleName(), "Started.");
                }
                byte[] buffer = new byte[optimalBufSize];
                recorder.startRecording();
                while (thread != null && !thread.isInterrupted() && (recorder.read(buffer, 0, optimalBufSize)) > 0) {
                    callback.onBufferAvailable(buffer);
                }
                recorder.stop();
                recorder.release();
            }
        }, Recorder.class.getName());
        thread.start();
    }

    /**********************************************************************************************
     * function: stop
     * description: function to stop recording
     * args:
     * return: void
     **********************************************************************************************/
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
