package com.medmobile.pid.medassist.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.medmobile.pid.medassist.app.DBAccess.AlarmsDataSource;
import com.medmobile.pid.medassist.app.DataModels.Alarm;
import com.medmobile.pid.medassist.app.InputFilters.InputFilterMinMax;


public class AlarmSettings extends Activity implements View.OnClickListener {

    // Constants for use in activity
    private static final int TIME_PICKER_DIALOG = 1111;
    private static final int OBLIGATORY_NOT_FILLED_DIALOG = 2121;

    private ImageButton ib_capsule;
    private ImageButton ib_syringe;
    private ImageButton ib_syrup;
    private ImageButton ib_barcodeScanner;

    private  EditText et_medicineName;
    private EditText et_startingTime;
    private EditText et_intakesPerDay;
    private EditText et_numPerDosage;
    private EditText et_currentAmount;

    private TextView tv_pills_dose;
    private TextView tv_ampoules_dose;
    private Spinner s_syrup_dose;

    private TextView tv_pills_amount;
    private TextView tv_ampoules_amount;
    private TextView tv_syrup_amount;
    private TextView tv_currAmount;

    private TextView tv_barcodeStatus;

    private ToggleButton tb_vibrate;

    private CheckBox cb_doseControl;

    private Alarm retrievedAlarm, databaseAlarm;
    private AlarmsDataSource datasource;

    // Tells if we are editing an existing alarm, or creating a new one
    private boolean newAlarm;

    String barcodeResult; //string for saving the barcode scan result

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);

        /***************************/
        /*setting up the action bar*/
        /***************************/
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(new ColorDrawable(232323));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Reminder settings");


        // Registering views for use in Java

        // Image buttons for selecting medicine type
        ib_capsule = (ImageButton) findViewById(R.id.ib_capsuleTypeSelector);
        ib_syringe = (ImageButton) findViewById(R.id.ib_syringeTypeSelector);
        ib_syrup = (ImageButton) findViewById(R.id.ib_syrupTypeSelector);

        ib_barcodeScanner = (ImageButton) findViewById(R.id.ib_barcode_rs_barcodeScanner);


        // EditTexts
        et_medicineName = (EditText) findViewById(R.id.et_rs_medicineName);
        et_startingTime = (EditText) findViewById(R.id.et_time_rs_times);
        et_intakesPerDay = (EditText) findViewById(R.id.et_number_rs_times);
        et_numPerDosage = (EditText) findViewById(R.id.et_number_rs_dosage);
        et_currentAmount = (EditText) findViewById(R.id.et_currAmount_rs_startingDose);

        // Set input filter for intakes per day (only numbers from 1 to 24),
        et_intakesPerDay.setFilters(new InputFilter[] { new InputFilterMinMax(1, 24)});

        // TextViews
        tv_pills_dose = (TextView) findViewById(R.id.tv_pills_rs_dosage);
        tv_ampoules_dose = (TextView) findViewById(R.id.tv_ampoules_rs_dosage);
        tv_currAmount = (TextView) findViewById(R.id.tv_currAmount_rs_startingDose);
        s_syrup_dose = (Spinner) findViewById(R.id.s_syrup_rs_dosage);

        List<String> list = Arrays.asList(getResources().getStringArray(R.array.syrup_dosage_type));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.eui_spinner_item, list);
        s_syrup_dose.setAdapter(adapter);

        tv_pills_amount = (TextView) findViewById(R.id.tv_pills_rs_startingDose);
        tv_ampoules_amount = (TextView) findViewById(R.id.tv_ampoules_rs_startingDose);
        tv_syrup_amount = (TextView) findViewById(R.id.tv_syrup_rs_startingDose);

        tv_barcodeStatus = (TextView) findViewById(R.id.tv_barcodeStatus_rs_additionalFeatures);

        // ToggleButton for turning vibration on/off
        tb_vibrate = (ToggleButton) findViewById(R.id.tb_vibrate_rs_additionalFeatures);

        // Checkbox for enabling/disabling dose control feature
        cb_doseControl = (CheckBox) findViewById(R.id.cb_doseControl);


        disableDosageViews();

        // Set medtype-dependant views' visibility to false
        tv_pills_dose.setVisibility(View.INVISIBLE);
        tv_ampoules_dose.setVisibility(View.INVISIBLE);
        s_syrup_dose.setVisibility(View.INVISIBLE);
        tv_pills_amount.setVisibility(View.INVISIBLE);
        tv_ampoules_amount.setVisibility(View.INVISIBLE);
        tv_syrup_amount.setVisibility(View.INVISIBLE);

        // Set-up the datasource for DB access and open it
        datasource = new AlarmsDataSource(this);
        datasource.open();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_settings, menu);

        //by default, delete alarm is disabled (if alarm still hasn't been saved to the database)
        menu.getItem(1).setVisible(false);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case R.id.menu_save_alarm_changes:
                if (!areRequiredFieldsEntered())
                    showDialog(OBLIGATORY_NOT_FILLED_DIALOG);
                else
                    saveAlarmInfo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch(id)
        {
            case R.id.et_time_rs_times:
                showDialog(TIME_PICKER_DIALOG);
                break;
            case R.id.ib_barcode_rs_barcodeScanner:

                //do barcode scanning;

                break;
            case R.id.cb_doseControl:
                if (cb_doseControl.isChecked())
                    enableDosageViews();
                else
                    disableDosageViews();
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case TIME_PICKER_DIALOG:
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et_startingTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");

                return mTimePicker;

            case OBLIGATORY_NOT_FILLED_DIALOG:
                final AlertDialog.Builder builder_required = new AlertDialog.Builder(this);
                builder_required.setMessage(("Not all the required fields are entered (type, name, intake time, and t.p.d)" +
                        ". Enter those and then save"))
                        .setCancelable(true)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                final AlertDialog alert_required = builder_required.create();
                return alert_required;
        }

        return null;
    }

    public void onMedSelectorClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
            case R.id.ib_syrupTypeSelector:
                ib_capsule.setSelected(false);
                ib_syringe.setSelected(false);
                ib_syrup.setSelected(true);

                // Set syrup dependant views to visible
                et_numPerDosage.setVisibility(View.VISIBLE);
                et_currentAmount.setVisibility(View.VISIBLE);
                tv_currAmount.setVisibility(View.VISIBLE);
                tv_pills_dose.setVisibility(View.INVISIBLE);
                tv_ampoules_dose.setVisibility(View.INVISIBLE);
                s_syrup_dose.setVisibility(View.VISIBLE);
                tv_pills_amount.setVisibility(View.INVISIBLE);
                tv_ampoules_amount.setVisibility(View.INVISIBLE);
                tv_syrup_amount.setVisibility(View.VISIBLE);
            break;

            case R.id.ib_capsuleTypeSelector:
                ib_capsule.setSelected(true);
                ib_syringe.setSelected(false);
                ib_syrup.setSelected(false);

                // Set capsule dependant views to visible
                et_numPerDosage.setVisibility(View.VISIBLE);
                et_currentAmount.setVisibility(View.VISIBLE);
                tv_pills_dose.setVisibility(View.VISIBLE);
                tv_currAmount.setVisibility(View.VISIBLE);
                tv_ampoules_dose.setVisibility(View.INVISIBLE);
                s_syrup_dose.setVisibility(View.INVISIBLE);
                tv_pills_amount.setVisibility(View.VISIBLE);
                tv_ampoules_amount.setVisibility(View.INVISIBLE);
                tv_syrup_amount.setVisibility(View.INVISIBLE);
            break;

            case R.id.ib_syringeTypeSelector:
                ib_capsule.setSelected(false);
                ib_syringe.setSelected(true);
                ib_syrup.setSelected(false);

                // Set syringe dependant views to visible
                et_numPerDosage.setVisibility(View.VISIBLE);
                et_currentAmount.setVisibility(View.VISIBLE);
                tv_currAmount.setVisibility(View.VISIBLE);
                tv_pills_dose.setVisibility(View.INVISIBLE);
                tv_ampoules_dose.setVisibility(View.VISIBLE);
                s_syrup_dose.setVisibility(View.INVISIBLE);
                tv_pills_amount.setVisibility(View.INVISIBLE);
                tv_ampoules_amount.setVisibility(View.VISIBLE);
                tv_syrup_amount.setVisibility(View.INVISIBLE);
            break;

        }

    }

    private void enableDosageViews()
    {
        et_numPerDosage.setEnabled(true);
        et_currentAmount.setEnabled(true);
        tv_pills_dose.setTextColor(Color.BLACK);
        tv_ampoules_dose.setTextColor(Color.BLACK);
        s_syrup_dose.setEnabled(true);
        tv_pills_amount.setTextColor(Color.BLACK);
        tv_ampoules_amount.setTextColor(Color.BLACK);
        tv_syrup_amount.setTextColor(Color.BLACK);
        tv_currAmount.setTextColor(Color.BLACK);
    }

    private void disableDosageViews()
    {
        et_numPerDosage.setEnabled(false);
        et_currentAmount.setEnabled(false);
        tv_pills_dose.setTextColor(getResources().getColor(R.color.hintGrey));
        tv_ampoules_dose.setTextColor(getResources().getColor(R.color.hintGrey));
        s_syrup_dose.setEnabled(false);
        tv_pills_amount.setTextColor(getResources().getColor(R.color.hintGrey));
        tv_ampoules_amount.setTextColor(getResources().getColor(R.color.hintGrey));
        tv_syrup_amount.setTextColor(getResources().getColor(R.color.hintGrey));
        tv_currAmount.setTextColor(getResources().getColor(R.color.hintGrey));
    }

    private boolean areRequiredFieldsEntered()
    {
        boolean result = true;

        // If obligatory EditTexts aren't filled,
        if (et_medicineName.getText().toString().matches("") || et_startingTime.getText().toString().matches("") || et_intakesPerDay.getText().toString().matches(""))
            result = false;

        // If medicine type isn't selected
        if(!ib_capsule.isSelected() && !ib_syringe.isSelected() && !ib_syrup.isSelected())
            result = false;

        // If dosage control is enabled, then check if the necessary fields for it are entered
        if (cb_doseControl.isChecked())
            if (et_numPerDosage.getText().toString().matches("") || et_currentAmount.getText().toString().matches("") )
                result = false;

        return result;
    }

    private Alarm retrieveAlarmFromInput()
    {
        String medicineName = et_medicineName.getText().toString();
        String medicineType = ib_syrup.isSelected() ? "syrup" : ib_capsule.isSelected() ? "pills" : ib_syringe.isSelected() ? "injections" : "";
        long startTime = getMillisFromEditText(et_startingTime);
        int timesADay = Integer.parseInt(et_intakesPerDay.getText().toString());
        int dosage = et_numPerDosage.getText().toString().equals("") ? -1 : Integer.parseInt(et_numPerDosage.getText().toString());
        int dosageType = 0;
        int currentAmount = et_currentAmount.getText().toString().equals("") ? -1 : Integer.parseInt(et_currentAmount.getText().toString());
        boolean vibrate = tb_vibrate.isChecked();
        boolean isDoseControlEnabled = cb_doseControl.isChecked();

        if (ib_syrup.isSelected())
        {
            if (s_syrup_dose.getSelectedItemPosition() == 0)
                dosageType = 8;
            else
                dosageType = 15;
        }


        return new Alarm(medicineName, medicineType, startTime, timesADay, dosage, dosageType, currentAmount, barcodeResult, vibrate, startTime, isDoseControlEnabled);

    }

    private void saveAlarmInfo()
    {
        retrievedAlarm = retrieveAlarmFromInput();
        databaseAlarm = datasource.insertAlarm(retrievedAlarm);
    }

    private long getMillisFromEditText(EditText et)
    {
        long result;

        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
        Calendar cal = Calendar.getInstance();
        Date dateValue = new Date(0);

        try
        {
            dateValue = new Date(dateFormatter.parse(et.getText().toString()).getTime());
        } catch (ParseException ex)
        {
            Log.w(this.getClass().getName(), "User data: Date/Time parse exception!");
        }

        cal.set(Calendar.HOUR, dateValue.getHours());
        cal.set(Calendar.MINUTE, dateValue.getMinutes());

        result = cal.getTimeInMillis();

        return result;
    }
}
