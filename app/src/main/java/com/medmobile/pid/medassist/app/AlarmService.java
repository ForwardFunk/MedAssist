package com.medmobile.pid.medassist.app;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;

import com.medmobile.pid.medassist.app.DBAccess.AlarmsDataSource;
import com.medmobile.pid.medassist.app.DataModels.Alarm;


public class AlarmService extends IntentService
{

    public static final String CREATE = "CREATE";
    public static final String CANCEL = "CANCEL";
    public static final int DAY_IN_MILLIS = 24*3600*1000;

    private IntentFilter matcher;

    private AlarmsDataSource datasource;

    public AlarmService()
    {
        super("AlarmService");
        matcher = new IntentFilter();
        matcher.addAction(CREATE);
        matcher.addAction(CANCEL);

        datasource.openForRead();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        String action = intent.getAction();
        String alarmId = intent.getStringExtra("alarmId");

        if (matcher.matchAction(action))
            execute(action, alarmId);
    }

    private void execute(String action, String alarmId)
    {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Alarm alarmToSet = datasource.getAlarmById(alarmId);
        datasource.close();
        Intent intent = new Intent(this, AlarmSettings.class);

        long time = alarmToSet.getStartTime();
        long interval = DAY_IN_MILLIS / alarmToSet.getTimesADay();


        intent.putExtra("id", alarmToSet.getId());
        intent.putExtra("medicineName", alarmToSet.getMedicineName());
        intent.putExtra("medicineType", alarmToSet.getMedicineType());
        intent.putExtra("currentTime", alarmToSet.getStartTime());
        intent.putExtra("interval", interval);
        intent.putExtra("dosage", alarmToSet.getDosage());
        intent.putExtra("dosageType", alarmToSet.getDosageType());
        intent.putExtra("currentAmount", alarmToSet.getCurrentAmount());
        intent.putExtra("vibrate", alarmToSet.isVibrate());
        intent.putExtra("isDoseControlEnabled", alarmToSet.isDoseControlEnabled());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (CREATE.equals(action))
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, interval, pendingIntent);
        else if (CANCEL.equals(action))
            alarmManager.cancel(pendingIntent);
    }

}