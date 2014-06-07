package com.medmobile.pid.medassist.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Calendar;


public class AlarmDialog extends Activity implements View.OnClickListener
{
    private static final int TEN_MINS_IN_MILLIS = 10*60*1000; // 10 mins by 60 secs by 1000 millisecs

    // Alarm attributes sent using putExtra(tag, content) by the calling broadcast receiver
    private String medicineName;
    private long currentTime;
    private String medicineType;
    private boolean vibrate;
    private int dosage;
    private int dosageType;
    private boolean isDoseControlEnabled;

    // Views used in the dialog to display data
    private TextView tv_dosage;
    private TextView tv_dosageType;
    private TextView tv_time;

    //MediaPlayer (for ringtone) and vibrator objects
    private MediaPlayer player;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_LEFT_ICON);
        setContentView(R.layout.activity_alarm_dialog);
        //getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);
        this.setFinishOnTouchOutside(false);

        // Get the intent which called the alarm dialog
        Intent intent = getIntent();

        // Get all the extras (data) which were sent through the intent to the dialog
        medicineName = intent.getStringExtra("medicineName");
        currentTime = intent.getLongExtra("currentTime", 0);
        medicineType = intent.getStringExtra("medicineType");
        vibrate = intent.getBooleanExtra("vibrate", false);
        dosage = intent.getIntExtra("dosage", 0);
        dosageType = intent.getIntExtra("dosageType", 0);
        isDoseControlEnabled = intent.getBooleanExtra("isDoseControlEnabled", false);

        // Register the data-displaying TextViews for use in Java
        tv_dosage = (TextView) findViewById(R.id.tv_dosage_msg_ad_main);
        tv_dosageType = (TextView) findViewById(R.id.tv_type_msg_ad_main);
        tv_time = (TextView) findViewById(R.id.tv_time_ad_main);

        // Register the font from resources, and apply it to the Time TextView
        Typeface zwodrei= Typeface.createFromAsset(getAssets(),"fonts/zwodrei.ttf");
        tv_time.setTypeface(zwodrei);
        tv_dosage.setTypeface(null, Typeface.BOLD);
        tv_dosageType.setTypeface(null, Typeface.BOLD);

        // ViewFlipper used to switch between DoseControl and NonDoseControl views
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper_dialog);

        if (!isDoseControlEnabled)
            viewFlipper.showNext();

        playRingtoneAndVibrate();

        int dividerId = this.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = findViewById(dividerId);
        divider.setBackgroundColor(getResources().getColor(R.color.darkRed));

        setTitle(medicineName);
        setTime();
        setDosageAndType();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_dialog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
            case R.id.btn_dismiss_ad_buttons:
                player.stop();
                finish();
                break;
            case R.id.btn_snooze_ad_buttons:
                activateSnooze();
                player.stop();
                finish();
                break;
        }
    }

    private void playRingtoneAndVibrate()
    {
        // Vibrate if vibration allowed by user
        if (vibrate)
        {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000);
        }

        // Play default alarm sound, or if user has never setup the alarm, play the ringtone
        // default sound
        if (RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM) == null)
            player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        else
            player = MediaPlayer.create(this, Settings.System.DEFAULT_ALARM_ALERT_URI);
        player.start();
    }

    private void activateSnooze()
    {
        // We need to schedule another alarm, to be fired off in 10 minutes
        // with the same dialog data (except for current time)

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent dialog = new Intent(this, this.getClass());
        dialog.putExtra("currentTime", currentTime + TEN_MINS_IN_MILLIS);
        dialog.putExtra("medicineName", medicineName);
        dialog.putExtra("medicineType", medicineType);
        dialog.putExtra("vibrate", vibrate);
        dialog.putExtra("dosage", dosage);
        dialog.putExtra("dosageType", dosageType);
        dialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, dialog, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime + TEN_MINS_IN_MILLIS, pendingIntent);
    }


    private void setTime()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);

        tv_time.setText(cal.getTime().getHours() + ":" + cal.getTime().getMinutes());
    }

    private void setDosageAndType()
    {
        if (isDoseControlEnabled)
        {
            tv_dosage.setText(String.valueOf(dosage));


            if (medicineType.equals("syrup")) {
                if (dosageType == 8)
                    tv_dosageType.setText("teaspoons");
                else if (dosageType == 15)
                    tv_dosageType.setText("tablespoons");
            } else
                tv_dosageType.setText(dosageType);
        }
    }
}
