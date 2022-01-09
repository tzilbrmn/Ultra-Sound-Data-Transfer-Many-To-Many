package com.example.svc;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import ReceiverPackage.Receiver;
import ReceiverPackage.Recorder;
import SenderPackage.Sender;
import Utils.NumbersGenerator;
import models.Encounter;
import models.SVCDB;

public class CommunicationNetwork extends Thread {
    String frame;
    Recorder recorder;
    Receiver reciever;
    NumbersGenerator numGen;
    SVCDB db;
    long MBWP, RBWP;

    Semaphore sem;
    String threadName;

    boolean canListen = true;
    private boolean errorTimeOut = false;
    private boolean receivedError = false;
    boolean succedded = false;

    /**
     * {@inheritDoc}
     * Initialize the variables according to the received params.
     * @param threadName {@inheritDoc}
     * @param receiver {@inheritDoc}
     * @param rec {@inheritDoc}
     * @param db {@inheritDoc}
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public CommunicationNetwork(String threadName, Receiver receiver, Recorder rec, SVCDB db) {
        super(threadName);
        this.threadName = threadName;
        this.reciever = receiver;
        this.recorder = rec;
        this.db = db;

        this.numGen = new NumbersGenerator();
        MBWP = numGen.calculateMBWP();
        RBWP = numGen.calculateRBWP();
    }

    /**
     * Builds the frame according to the user's id
     *
     * @param id The id of the user.
     * @return If the process succeeded.
     */
    public boolean composeFrame(String id) {
        String origBinaryRep = Utils.utils.convertStringToHex(id);
        String frame = "f" + origBinaryRep + "f";
        String checksum = calcChecksum(id);
        if (checksum.length() < 2)
            checksum = "0" + checksum;
        frame += checksum;

        if (frame.length() == 14) {
            frame = Utils.utils.strToBinary(frame);
            Log.d("Debug ", "composeFrame Succeeded");
            Log.d("Debug ", frame);
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
        Log.d("Debug ", "calculate checksums");
        return checkSum;
    }

    /**
     * {@inheritDoc}
     * Creates a thread with the frame that will execute the network protocol and start it.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void startProcess() throws InterruptedException {
        Log.d("Debug ", "startProcess");

        CommunicationNetwork listeningThread = new CommunicationNetwork("Listen", this.reciever, this.recorder, db);
        listeningThread.setFrame(this.frame);

        listeningThread.start();

        listeningThread.join();
    }



    /**
     * {@inheritDoc}
     * Wait the waiting periods according to the protocol.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {

        // run by thread A
        //if (this.getName().equals("Listen")) {
        while(true){
            try {

                reciever.setIsIdle(true);
                this.succedded = false;
                if (!waitingThread(MBWP))
                    waitingThread(RBWP);
                do {
                    this.succedded = waitingThread(MBWP);
                } while (!this.succedded);
            } catch (InterruptedException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     * Check if the channel is Idle.
     * Send the frame and wait for error.
     * @param timeToWait
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected boolean waitingThread(long timeToWait) throws UnsupportedEncodingException, InterruptedException {
        if (!reciever.getIsIdle() || canListen) {
            if (!reciever.getIsIdle())
                Log.d("Debug ", "is idle = false");
            else Log.d("Debug ", "is idle = true");

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
                Listen();
            }
            timer.join();
            return false;
        } else {

            Log.d("Debug ", "IsIdle = true");
            if (!canListen) {
                receivedError = true;
                while (receivedError) {
                    Log.d("Debug ", "Start transmitting");
                    Send(frame);

                    Thread getErrorMsg = new Thread() {
                        @Override
                        public void run() {
                            try {
                                receivedError = false;
                                if (reciever.receiveError()) {
                                    Log.d("Debug ", "receive error msg");
                                    Log.d("Debug ", "send again");
                                    receivedError = true;
                                }
                            } catch (UnsupportedEncodingException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    getErrorMsg.start();

                    getErrorMsg.join();

                }
                canListen = true;
                Log.d("Debug ", "end transmit");
                return true;
            }
            return false;
        }

    }

    /**
     * {@inheritDoc}
     * Call to send the frame.
     * @param binaryRep
     */
    public void Send(String binaryRep){

        if (!binaryRep.isEmpty() && !binaryRep.equals("")) {
            Sender cSender = new Sender();
            cSender.setMsg2Send(binaryRep);
            cSender.sendMsg();
        }
    }

    /**
     * {@inheritDoc}
     * Listen to the channel, and add to the DB the received msg
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Listen() {
        Receiver cReceiver = new Receiver();
        try {
            ArrayList<String> ReceivedMsg = cReceiver.receiveMsg(this);
            if (ReceivedMsg != null) {
                String binaryRep = Utils.utils.concatArrayList(ReceivedMsg);
                Encounter receivedVC = Encounter.receiveVisitCard(binaryRep);
                Encounter.addVC(receivedVC, db);
            }

        } catch (UnsupportedEncodingException | IllegalArgumentException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public String getFrame() { return frame; }
    public void setFrame(String newFrame) { this.frame = newFrame; }
}

