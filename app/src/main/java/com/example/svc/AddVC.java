package com.example.svc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ReceiverPackage.Receiver;
import ReceiverPackage.Recorder;
import Utils.Constants;
import Utils.Smaz;
import models.Encounter;
import models.SVCDB;
import models.User;

import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)/*change to oreo version of OS*/
public class AddVC extends AppCompatActivity {

    /**
     * Instance variables:
     * db - an instance of the SQLite Helper class.
     * user - The currently logged in user.
     */
    private SVCDB db;
    CommunicationNetwork communicationNetwork;

    /**
     * {@inheritDoc}
     * Initializes the db instance, gets the currently logged in user from the intent <i>Extra</i> dictionary.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected AddVC(SVCDB db) {

        this.db = db;
        communicationNetwork = new CommunicationNetwork("", new ViewVisitCard(), new Receiver(), new Recorder(), this);
    }

    /**
     * the onClick method of the <i>Add</i> button.
     * gets all the values from the textFields, validates the input (displays alert if there's an error), adds the VC to the DB, and saves it in the phone book.
     */
    public void addVc(){
        //Change time from String to time format- Ariela !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
        String id = ((EditText) findViewById(R.id.ETUserId)).getText().toString();//will be accepted in the listening process.
        String encounterTime = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date());
        String encounterDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        //try to add the VC, catch possible IllegalArgumentException if any of the mandatory fields is missing. save the VC in the phone book afterwards.
        try {
            Encounter newVC = new Encounter(id);
            Encounter.addVC(newVC, db);
                //save contact in phone book..
                //saveInPhoneBook(newVC);

        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * The onClick for the <i>Receive via sound</i> button.
     * Gets the required permission, listens for ultrasound data from the sending phone nearby.
     * After it receives the data, it saves the received VC to the DB and to the phone book
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ReceiveVC(String id) throws InterruptedException {
        System.out.println("Receiving...");
        //get recording permission
            communicationNetwork.composeFrame(id);
            communicationNetwork.startProcess();//listen for ultrasound
    }

    /**
     *
     * Will start when listen/Record starts. <br/>
     * Will convert the received binary string to a byte array, then decompress it back to the original string. <br/>
     * Lastly, it will fill the textFields with the received data.
     * @param
     * @return void
     */
    public void Listen() {
        Receiver cReceiver = new Receiver();
        try {
            ArrayList<String> ReceivedMsg = cReceiver.receiveMsg(communicationNetwork);
            if (ReceivedMsg != null) {
                String binaryRep = Utils.utils.concatArrayList(ReceivedMsg);
                Encounter receivedVC = Encounter.receiveVisitCard(binaryRep);
                Encounter.addVC(receivedVC, db);
            }

        } catch (UnsupportedEncodingException | IllegalArgumentException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }



    /**
     * function: onRequestPermissionsResult
     * description: function to ask for a Record permissions
     * @param requestCode (int)
     * @param  permissions (String) - NOT NULL
     * @param  grantResults (int[]) - NOT NULL
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                //continue listening when user granted permission on mic
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Listen();
                }
                break;
            }
        }
    }

    /**
     * saves the received visit card to the phone book.
     * @param vc the visit card to add
     */
    //Change name to encounter log - Ariela
    private void saveInPhoneBook(Encounter vc){
        //initialize the contact first.
        ArrayList<ContentProviderOperation> contact = new ArrayList<ContentProviderOperation>();
        contact.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Add id ???????????????????????????????????????????????????????????????????????????
        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                //.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Id.CONTENT_ITEM_TYPE)
                //.withValue(ContactsContract.CommonDataKinds.Id.DATA, vc.getId())
                //.withValue(ContactsContract.CommonDataKinds.Id.TYPE, ContactsContract.CommonDataKinds.Id.TYPE_WORK)
                .build());

        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, contact);
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("Something went wrong...")
                    .setMessage("An error occured, please try again later.")
                    .setNeutralButton("Close", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
