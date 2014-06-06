package com.medmobile.pid.medassist.app.DBAccess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pavle on 30.4.14..
 */
public class MedAssistSQLiteHelper extends SQLiteOpenHelper {




    /* Table and columns info for ALARMS table */
    public static final String TABLE_ALARMS = "ALARMS";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "MedicineName";
    public static final String COL_TYPE = "MedicineType";
    public static final String COL_START_TIME = "StartTime";
    public static final String COL_TIMES = "TimesADay";
    public static final String COL_DOSAGE = "Dosage";
    public static final String COL_DOSE_TYPE = "DosageType";
    public static final String COL_CURRENT_AMOUNT = "CurrentAmount";
    public static final String COL_BARCODE = "Barcode";
    public static final String COL_VIBRATE = "Vibrate";
    public static final String COL_NEXT_INTAKE = "NextIntake";

    /* Table and column info for USER table */
    public static final String TABLE_USER = "USER";
    public static final String US_ID = "_id";
    public static final String US_NAME = "Name";
    public static final String US_LASTNAME = "LastName";
    public static final String US_DOB = "DateOfBirth";
    public static final String US_ALT_PHONE_NO = "AlternativePhoneNo";
    public static final String US_STATES = "ChronicalStates";
    public static final String US_ALLERGIES = "Allergies";
    public static final String US_BLOODTYPE = "BloodType";
    public static final String US_CONTACT = "Contact";
    public static final String US_LOCATION = "LocationSharing";


    /* Database info */
    private static final String DATABASE_NAME = "medassist.db";
    private static final int DATABASE_VERSION = 7;

    //SQL statement for ALARMS table creation
    private static final String TABLE_ALARMS_CREATE =
            "create table " + TABLE_ALARMS + "("
            + COL_ID + " integer primary key autoincrement, "
            + COL_NAME + " text not null, "
            + COL_TYPE + " text not null, "
            + COL_START_TIME + " integer not null, "
            + COL_TIMES + " integer not null, "
            + COL_DOSAGE + " integer, "
            + COL_DOSE_TYPE + " integer, "
            + COL_CURRENT_AMOUNT + " integer, "
            + COL_BARCODE  +  " text, "
            + COL_VIBRATE + " text, "
            + COL_NEXT_INTAKE + " integer);";

    //SQL statement for USER table creation
    private static final String TABLE_USER_CREATE =
            "create table " + TABLE_USER + "("
            + US_ID + " integer primary key autoincrement, "
            + US_NAME + " text, "
            + US_LASTNAME + " text, "
            + US_DOB + " text, "
            + US_ALT_PHONE_NO + " text, "
            + US_STATES + " text, "
            + US_ALLERGIES + " text, "
            + US_BLOODTYPE + " integer, "
            + US_CONTACT + " text, "
            + US_LOCATION + " text);";


    public MedAssistSQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(TABLE_ALARMS_CREATE);
        database.execSQL(TABLE_USER_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVer, int newVer)
    {
        Log.w(MedAssistSQLiteHelper.class.getName(),
                "Upgrading database from " + oldVer + " to " + newVer + ". DESTROYING ALL DATA...");
        database.execSQL("drop table if exists " + TABLE_ALARMS);
        database.execSQL("drop table if exists " + TABLE_USER);

        onCreate(database);
    }
}
