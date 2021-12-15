package com.example.svc;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Semaphore;

import ReceiverPackage.Receiver;
import ReceiverPackage.Recorder;
import Utils.NumbersGenerator;

public class CommunicationNetwork extends Thread {
    String frame;
    Recorder recorder;
    Receiver reciever;
    NumbersGenerator numGen;
    AddVC addEncounter;
    ViewVisitCard ViewVisitCard;
    long MBWP, RBWP;

    Semaphore sem;
    String threadName;

    boolean canListen = true;
    boolean errorTimeOut = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public CommunicationNetwork() {
        super("Main");
        this.sem = new Semaphore(1);
        this.addEncounter = new AddVC(this);
        this.ViewVisitCard = new ViewVisitCard();
        this.reciever = new Receiver();
        this.recorder = new Recorder();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CommunicationNetwork(String threadName) {
        super(threadName);
        this.threadName = threadName;
        //this.sem = new Semaphore(1);
        this.addEncounter = new AddVC(this);
        this.ViewVisitCard = new ViewVisitCard();
        this.reciever = new Receiver();
        this.recorder = new Recorder();

        this.numGen = new NumbersGenerator();
        MBWP = numGen.calculateMBWP();
        RBWP = numGen.calculateRBWP();
    }


    final private String listeningSemaphore = "Semaphore";

    /**
     * Builds the frame according to the user's id
     *
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
            Log.d("Debug ", "composeFrame Succeeded");
            this.frame = frame;
            return true;
        }
        Log.d("Debug ", "composeFrame failed");
        return false;
    }

    /**
     * Calculates the checksum of the message (according to the data segment)
     *
     * @param str The data to calculate the checksum for.
     * @return The checksum calculated.
     */
    public static String calcChecksum(String str) {

        char[] ch = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            ch[i] = str.charAt(i);
        }
        int num = 0;
        int len = str.length();
        while (len != 0) {
            num += Character.getNumericValue(ch[len - 1]);
            len = len - 1;
        }
        String checkSum = Integer.toHexString(num);
        Log.d("Debug ", "calculate chackSumS");
        return checkSum;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startProcess() throws InterruptedException {
        Log.d("Debug ", "startProcess");

        CommunicationNetwork listeningThread = new CommunicationNetwork("Listen");
        listeningThread.setFrame(this.frame);
      //  CommunicationNetwork transmittingThread = new CommunicationNetwork("Transmit");
      //  transmittingThread.setFrame(this.frame);

        // stating threads A and B
      //  transmittingThread.start();
        listeningThread.start();

        // waiting for threads A and B
        listeningThread.join();
       // transmittingThread.join();
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {

        // run by thread A
        //if (this.getName().equals("Listen")) {
        while(true){
            try {
                boolean succedded = false;
                //sem.acquire();
                if (!waitingThread(MBWP))
                    waitingThread(RBWP);
                do {
                    succedded = waitingThread(MBWP);
                } while (!succedded);
            } catch (InterruptedException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        // run by thread B
        /*else {
            try {
                // acquiring the lock
                sem.acquire();

                Integer[] SettingsArr = Utils.SoundSettings.getSettings();
                if (frame == null) {
                    sem.release();
                    Log.d("Debug ", "Frame fail. Trying again");
                } else {

                    ViewVisitCard.Send(frame);
                    waitingThread(RBWP);
                }


            } catch (InterruptedException | UnsupportedEncodingException exc) {
                System.out.println(exc);
            }

        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected boolean waitingThread(long timeToWait) throws UnsupportedEncodingException, InterruptedException {

        if (!recorder.getIsIdle() || canListen) {

            Thread timer = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(timeToWait);
                        canListen = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            timer.start();

            while (canListen) {
                Log.d("Debug ", "Start listening");
                addEncounter.Listen();
            }
            timer.join();
            return false;
        }
        else
        {
            if (!canListen)
            {
                //sem.release();
                Log.d("Debug ", "Start transmitting");
                ViewVisitCard.Send(frame);

                Thread errorTime = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Log.d("Debug ", "Listen for errors");
                            errorTimeOut = true;
                            Thread.sleep(2000);
                            errorTimeOut = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                errorTime.start();
                while (errorTimeOut)
                {
                    if (reciever.receiveError())
                    {
                        ViewVisitCard.Send(frame);
                    }
                }
                errorTime.join();
                canListen = true;
                return true;
            }
            return false;
        }
    }


    public String getFrame() { return frame; }
    public void setFrame(String newFrame) { this.frame = newFrame; }

    public boolean isCanListen() {
        return canListen;
    }
}

