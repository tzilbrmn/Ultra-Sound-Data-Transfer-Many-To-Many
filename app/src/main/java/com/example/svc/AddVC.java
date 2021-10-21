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
import java.util.ArrayList;

import ReceiverPackage.Receiver;
import Utils.Constants;
import Utils.Smaz;
import models.SVCDB;
import models.UserDTO;
import models.VisitCardDAO;
import models.VisitCardDTO;
import security.InputValidators;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class AddVC extends AppCompatActivity {
    /**
     * Instance variables:
     * db - an instance of the SQLite Helper class.
     * user - The currently logged in user.
     */
    private SVCDB db;
    private UserDTO user;

    /**
     * {@inheritDoc}
     * Initializes the db instance, gets the currently logged in user from the intent <i>Extra</i> dictionary.
     * @param savedInstanceState {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vc);
        db = new SVCDB(this);
        //get the user from intent
        Intent intent = getIntent();
        user = UserDTO.stringToUser(intent.getStringExtra(Constants.USER));

    }

    /**
     * the onClick method of the <i>Add</i> button.
     * gets all the values from the textFields, validates the input (displays alert if there's an error), adds the VC to the DB, and saves it in the phone book.
     * @param v <code>Auto-generated by Android</code>
     */
    public void addVc(View v){
        String id = ((EditText) findViewById(R.id.idlET)).getText().toString();

        if(!id.isEmpty() && !InputValidators.validate(InputValidators.ID,id))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Invalid input")
                    .setMessage("Id field is invalid")
                    .setNeutralButton("Close", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }
        //try to add the VC, catch possible IllegalArgumentException if any of the mandatory fields is missing. save the VC in the phone book afterwards.
        try {
            VisitCardDTO newVC = new VisitCardDTO.Builder()
                    .setId(id);
            if(VisitCardDAO.addVC(newVC, db)){
                //save contact in phone book..
                saveInPhoneBook(newVC);
                Intent intent = new Intent(this,Home.class);
                //putExtra...
                intent.putExtra(Constants.USER,user.toString());
                startActivity(intent);
            }else{
                //add a condition in the DB that checks if a visit card for this user with THE SAME VALUES FOR ALL FIELDS
                new AlertDialog.Builder(this)
                        .setTitle("You already have this visit card!")
                        .setMessage("This visit card already exists")
                        .setNeutralButton("Close", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        } catch (IllegalArgumentException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Mandatory fields missing")
                    .setMessage("Id is a mandatory field.")
                    .setNeutralButton("Close", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    /**
     * The onClick for the <i>Receive via sound</i> button.
     * Gets the required permission, listens for ultrasound data from the sending phone nearby.
     * After it receives the data, it saves the received VC to the DB and to the phone book
     * @param v <code>Auto-generated by Android</code>
     */
    public void ReceiveVC(View v) {
        System.out.println("Receiving...");
        //get recording permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
        else{
            Listen();//listen for ultrasound
            //TODO: add progress bar
        }

    }

    /**
     *
     * Will start when listen/Record starts. <br/>
     * Will convert the received binary string to a byte array, then decompress it back to the original string. <br/>
     * Lastly, it will fill the textFields with the received data.
     * @param
     * @return void
     */
    private void Listen() {
        Receiver cReceiver = new Receiver();
        Integer[] SettingsArr = Utils.SoundSettings.getSettings();
        try {

            ArrayList<String> ReceivedMsg = cReceiver.receiveMsg(SettingsArr);
            String binaryRep = Utils.utils.concatArrayList(ReceivedMsg);
            //If Algorithm is Smaz
            byte[] compressed = Utils.utils.binaryToByteArray(binaryRep);
            Smaz smaz = new Smaz();
            String decompressed = smaz.decompress(compressed);
            //If Algorithm is LZString
//            String compressed = Utils.utils.binaryToText(binaryRep);
//            String decompressed = Utils.LZString.decompress(compressed);
            //do whatever...
            //fill in the text fields...
            //new AlertDialog.Builder(this).setTitle("Received String").setMessage(decompressed).setNeutralButton("OK",null).show();
            VisitCardDTO receivedVC = VisitCardDTO.receiveVisitCard(decompressed);
            //TODO Rani: do the same for the 4 name TF's after you add them..

            ((EditText) findViewById(R.id.IdlET)).setText(receivedVC.getId());


        } catch (UnsupportedEncodingException | IllegalArgumentException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("Uh Oh! Something went wrong...")
                    .setMessage("An error occurred, maybe mandatory fields are missing.")
                    .setNeutralButton("OK",null)
                    .show();
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
    private void saveInPhoneBook(VisitCardDTO vc){
        //initialize the contact first.
        ArrayList<ContentProviderOperation> contact = new ArrayList<ContentProviderOperation>();
        contact.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());




        // Add email
        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Id.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Id.DATA, vc.getId())
                .withValue(ContactsContract.CommonDataKinds.Id.TYPE, ContactsContract.CommonDataKinds.Id.TYPE_WORK)
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
