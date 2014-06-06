package com.medmobile.pid.medassist.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.medmobile.pid.medassist.app.DBAccess.UserDataSource;
import com.medmobile.pid.medassist.app.DataModels.User;


public class EditUserInfo extends Activity implements View.OnClickListener
{

    /*//*//*/*//*/*//*/*/
    /* CLASS ATTRIBUTES */
    /*//*//*/*//*/*//*/*/


    // In-class constants and attributes
    private static final int DATE_DIALOG_ID = 999;
    private static final int LOCATION_DIALOG_ID=1010;
    private static final int DELETE_CONFIRM_DIALOG_ID = 2020;
    private static final int SAVE_PROMPT_DIALOG_ID = 3030;

    private int year;
    private int month;
    private int day;

    // views
    private EditText et_name;
    private EditText et_lastname;
    private EditText et_dob;
    private EditText et_chrStates;
    private EditText et_allergies;
    private EditText et_altPhone;
    private EditText et_emContact;
    private Spinner s_bloodtype;
    private ImageButton btn_accessContacts;
    private ToggleButton tgl_locationSharing;


    // DataSource for accessing the database and userData entity for modelling of the data
    private UserDataSource datasource;
    private User userDataDatabase; // from database
    private User userDataRetrieved; // from current user input

    // Data from user input
    private String name;
    private String lastname;
    private String dob;
    private String chrStates;
    private String allergies;
    private int bloodType;
    private String altPhoneNo;
    private String emContact;

    /*//*//*/*//*/*//*/*/
    /* CLASS METHODS */
    /*//*//*/*//*/*//*/*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);


        /***************************/
        /*setting up the action bar*/
        /***************************/
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(232323));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Edit your information");


        /***************************/
        /*setting the Blood type choice spinner dialog */
        /***************************/
        s_bloodtype = (Spinner) this.findViewById(R.id.s_bloodtype);
        List<String> list = Arrays.asList(getResources().getStringArray(R.array.blood_types));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.eui_spinner_item, list);
        s_bloodtype.setAdapter(adapter);


        // registering all view elements (EditTexts, Buttons and Toggles) from XML to JAVA */
        et_name = (EditText) findViewById(R.id.et_name);
        et_lastname = (EditText) findViewById(R.id.et_lastname);
        et_dob = (EditText) findViewById(R.id.et_dateofbirth);
        et_chrStates = (EditText) findViewById(R.id.et_chronicStates);
        et_allergies = (EditText) findViewById(R.id.et_allergies);
        et_altPhone = (EditText) findViewById(R.id.et_altPhoneNo);
        et_emContact = (EditText) findViewById(R.id.et_emContact);

        btn_accessContacts = (ImageButton) findViewById(R.id.btn_access_contacts);

        tgl_locationSharing = (ToggleButton) findViewById(R.id.ei_tb_LocationSharing);

        // open User DataSource class for accessing the User table in the database
        datasource = new UserDataSource(this);
        datasource.open();

        // retrieve User data (if existent) and fill EditTexts and location toggle button with it
        userDataDatabase = datasource.getUserData();

        if (userDataDatabase != null)
        {
            et_name.setText(userDataDatabase.getName());
            et_lastname.setText(userDataDatabase.getLastname());
            et_dob.setText(userDataDatabase.getdOB());
            et_chrStates.setText(userDataDatabase.getChronicalStates());
            et_allergies.setText(userDataDatabase.getAllergies());
            s_bloodtype.setSelection(userDataDatabase.getBloodType());
            et_altPhone.setText(String.valueOf(userDataDatabase.getAlternativePhoneNo()));
            et_emContact.setText(String.valueOf(userDataDatabase.getContact()));
            tgl_locationSharing.setChecked(userDataDatabase.isLocationShared());
        }

    }


    @Override
    public void onResume()
    {
        //calling superclass's onResume method, before opening the DataSource session again
        super.onResume();
        datasource.open();
    }

    @Override
    public void onPause()
    {
        //calling superclass's onPause method, before closing the DataSource session
        super.onPause();
        datasource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_user_info, menu);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        //get the id of the view that has been clicked
        int id = v.getId();

        switch (id)
        {
            //if clicked on Date of birth EditText, show the dialog for date choice
            case R.id.et_dateofbirth:
                showDialog(DATE_DIALOG_ID);
                break;

            //if clicked on button for accessing contacts, start the intent for getting contact from PhoneBook, opening up
            //contacts pane, to choose a contact
            case R.id.btn_access_contacts:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
                break;

            //if clicked on button for toggling location sharing, and GPS service on the mobile device is off, show the dialog for the
            //user to choose if he/she wants to turn GPS on
            case R.id.ei_tb_LocationSharing:

               if(tgl_locationSharing.isChecked())
               {
                    final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    {
                        showDialog(LOCATION_DIALOG_ID);
                    }
                }
                break;



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //after returning from the activity for getting content from PhoneBook, if result was ok (contact was chosen properly)
        //get phone number from contacts database of the selected contact
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                Uri contactData = data.getData();
                Cursor cursor =  managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                et_emContact.setText(number);

            }
        }
    }


    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch(id)
        {
            case DATE_DIALOG_ID:
                Calendar currentTime = Calendar.getInstance();
                int day = currentTime.get(Calendar.DAY_OF_MONTH);
                int month = currentTime.get(Calendar.MONTH);
                int year = currentTime.get(Calendar.YEAR);

                return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
                {

                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay)
                    {
                        et_dob.setText(selectedDay +"/" + selectedMonth + "/" + selectedYear);
                    }
                }, year, month, day);

            case LOCATION_DIALOG_ID:
                final AlertDialog.Builder builder_location = new AlertDialog.Builder(this);
                builder_location.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                             }
                         });

                final AlertDialog alert_location = builder_location.create();
                return alert_location;

            case DELETE_CONFIRM_DIALOG_ID:
                final AlertDialog.Builder builder_clear = new AlertDialog.Builder(this);
                builder_clear.setMessage("Are you sure you want to clear all fields?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                clearInputFields();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });

                final AlertDialog alert_clear = builder_clear.create();
                return alert_clear;

            case SAVE_PROMPT_DIALOG_ID:
                final AlertDialog.Builder builder_save_prompt = new AlertDialog.Builder(this);
                builder_save_prompt.setMessage("It seems you haven't saved your data. Want to save it before going back?")
                        .setCancelable(true)
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                onBackPressed();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                saveUserInfo();
                                onBackPressed();
                            }
                        });
        }

        return null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_clear_fields:
                showDialog(DELETE_CONFIRM_DIALOG_ID);
                break;

            case R.id.menu_save_changes:
                saveUserInfo();
                String toastText;

                if (userDataDatabase.getName() == "")
                    toastText = "Your data was entered successfully";
                else
                    toastText = userDataDatabase.getName() + "'s data saved successfully";

                Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();

            case R.id.home:
                userDataRetrieved = retrieveUserFromInput();
                if (userDataRetrieved != userDataDatabase)
                    showDialog(SAVE_PROMPT_DIALOG_ID);
                else
                   onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    /* PRIVATE METHODS */
    private User retrieveUserFromInput()
    {
        name = et_name.getText().toString().matches("") ? null : et_name.getText().toString() ;
        lastname = et_lastname.getText().toString().matches("") ? null : et_lastname.getText().toString() ;
        dob = et_dob.getText().toString().matches("") ? null : et_dob.getText().toString() ;
        chrStates = et_chrStates.getText().toString().matches("") ? null : et_chrStates.getText().toString() ;
        allergies = et_allergies.getText().toString().matches("") ? null : et_allergies.getText().toString() ;
        bloodType = s_bloodtype.getSelectedItem().toString().matches("") || s_bloodtype.getSelectedItem().toString().matches("Blood type - none")
                ? null : s_bloodtype.getSelectedItemPosition();
        altPhoneNo = et_altPhone.getText().toString().matches("") ? null : et_altPhone.getText().toString() ;
        emContact = et_emContact.getText().toString().matches("") ? null : et_emContact.getText().toString() ;

        return new User(name, lastname, dob, chrStates, allergies, bloodType, altPhoneNo, emContact, tgl_locationSharing.isChecked());
    }

    private void saveUserInfo()
    {
        userDataRetrieved = retrieveUserFromInput();
        userDataDatabase = datasource.insertUser(userDataRetrieved);
    }

    private void clearInputFields()
    {
        et_name.setText("");
        et_lastname.setText("");
        et_dob.setText("");
        et_chrStates.setText("");
        et_allergies.setText("");
        s_bloodtype.setSelection(0);
        et_altPhone.setText("");
        et_emContact.setText("");
    }
}
