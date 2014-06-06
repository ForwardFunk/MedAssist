package com.medmobile.pid.medassist.app.DBAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.medmobile.pid.medassist.app.DataModels.Alarm;

/**
 * Created by Pavle on 30.4.14..
 */
public class AlarmsDataSource {

    private SQLiteDatabase database;
    private MedAssistSQLiteHelper dbHelper;
    private String[] allColumns = {
            MedAssistSQLiteHelper.COL_ID,
            MedAssistSQLiteHelper.COL_NAME,
            MedAssistSQLiteHelper.COL_TYPE,
            MedAssistSQLiteHelper.COL_START_TIME,
            MedAssistSQLiteHelper.COL_TIMES,
            MedAssistSQLiteHelper.COL_DOSAGE,
            MedAssistSQLiteHelper.COL_DOSE_TYPE,
            MedAssistSQLiteHelper.COL_CURRENT_AMOUNT,
            MedAssistSQLiteHelper.COL_BARCODE,
            MedAssistSQLiteHelper.COL_VIBRATE,
            MedAssistSQLiteHelper.COL_NEXT_INTAKE};

    public AlarmsDataSource(Context context)
    {
        dbHelper = new MedAssistSQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void openForRead() { database = dbHelper.getReadableDatabase(); }

    public void close()
    {
        dbHelper.close();
    }

    public Alarm insertAlarm(Alarm alarm)
    {
        ContentValues values = new ContentValues();
        values.put(MedAssistSQLiteHelper.COL_NAME, alarm.getMedicineName());
        values.put(MedAssistSQLiteHelper.COL_TYPE, alarm.getMedicineType());
        values.put(MedAssistSQLiteHelper.COL_START_TIME, alarm.getStartTime());
        values.put(MedAssistSQLiteHelper.COL_TIMES, alarm.getTimesADay());
        values.put(MedAssistSQLiteHelper.COL_DOSAGE, alarm.getDosage());
        values.put(MedAssistSQLiteHelper.COL_DOSE_TYPE, alarm.getDosageType());
        values.put(MedAssistSQLiteHelper.COL_CURRENT_AMOUNT, alarm.getCurrentAmount());
        values.put(MedAssistSQLiteHelper.COL_BARCODE, alarm.getBarcode());
        values.put(MedAssistSQLiteHelper.COL_VIBRATE, String.valueOf(alarm.isVibrate()));
        values.put(MedAssistSQLiteHelper.COL_NEXT_INTAKE, alarm.getNextIntake());

        long insertId = database.insert(MedAssistSQLiteHelper.TABLE_ALARMS, null, values);
        Cursor cursor = database.query(MedAssistSQLiteHelper.TABLE_ALARMS, allColumns, MedAssistSQLiteHelper.COL_ID + " = " + insertId,
                null, null, null, null);

        cursor.moveToFirst();
        Alarm newAlarm = cursorToAlarm(cursor);
        cursor.close();

        return newAlarm;
    }

    public void removeAlarm(Alarm alarm)
    {
        int id = alarm.getId();
        Log.w(AlarmsDataSource.class.getName(), "Alarm deleted with id: " + id);
        database.delete(MedAssistSQLiteHelper.TABLE_ALARMS, MedAssistSQLiteHelper.COL_ID + " = " + id, null);
    }

    public List<Alarm> getAllAlarms()
    {
        List<Alarm> alarms = new ArrayList<Alarm>();

        Cursor cursor = database.query(MedAssistSQLiteHelper.TABLE_ALARMS, allColumns,
                null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Alarm alarm = cursorToAlarm(cursor);
            alarms.add(alarm);
            cursor.moveToNext();
        }

        cursor.close();

        return alarms;
    }

    private Alarm cursorToAlarm(Cursor cursor)
    {
        Alarm alarm = new Alarm(cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getInt(4), cursor.getInt(5),
                cursor.getInt(6), cursor.getInt(7), cursor.getString(8), Boolean.parseBoolean(cursor.getString(9)), cursor.getLong(10));

        alarm.setId(cursor.getInt(0));
        return alarm;
    }

    public Alarm getAlarmById(String id)
    {
        Cursor cursor = database.query(MedAssistSQLiteHelper.TABLE_ALARMS, allColumns, MedAssistSQLiteHelper.COL_ID + " = " + id,
                null, null, null, null);

        cursor.moveToFirst();
        Alarm result = cursorToAlarm(cursor);
        cursor.close();

        return result;
    }

    public void updateWhenAlarmed(int id, int newAmount, long newNextIntake)
    {
        ContentValues values = new ContentValues();

        values.put(MedAssistSQLiteHelper.COL_CURRENT_AMOUNT, newAmount);
        values.put(MedAssistSQLiteHelper.COL_NEXT_INTAKE, newNextIntake);

        database.update(MedAssistSQLiteHelper.TABLE_ALARMS, values, MedAssistSQLiteHelper.COL_ID + " = " + id, null);
    }
}
