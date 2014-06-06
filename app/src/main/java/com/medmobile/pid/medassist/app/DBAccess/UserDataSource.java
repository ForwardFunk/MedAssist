package com.medmobile.pid.medassist.app.DBAccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.medmobile.pid.medassist.app.DataModels.User;
/**
 * Created by Pavle on 30.4.14..
 */
public class UserDataSource
{

    private SQLiteDatabase database;
    private MedAssistSQLiteHelper dbHelper;
    private String[] allColumns = {
            MedAssistSQLiteHelper.US_ID,
            MedAssistSQLiteHelper.US_NAME,
            MedAssistSQLiteHelper.US_LASTNAME,
            MedAssistSQLiteHelper.US_DOB,
            MedAssistSQLiteHelper.US_ALT_PHONE_NO,
            MedAssistSQLiteHelper.US_STATES,
            MedAssistSQLiteHelper.US_ALLERGIES,
            MedAssistSQLiteHelper.US_BLOODTYPE,
            MedAssistSQLiteHelper.US_CONTACT,
            MedAssistSQLiteHelper.US_LOCATION};

    public UserDataSource(Context context)
    {
        dbHelper = new MedAssistSQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public User insertUser(User user)
    {
        removeUsers();
        ContentValues values = new ContentValues();
        values.put(MedAssistSQLiteHelper.US_NAME, user.getName());
        values.put(MedAssistSQLiteHelper.US_LASTNAME, user.getLastname());
        values.put(MedAssistSQLiteHelper.US_DOB, user.getdOB());
        values.put(MedAssistSQLiteHelper.US_ALT_PHONE_NO, user.getAlternativePhoneNo());
        values.put(MedAssistSQLiteHelper.US_STATES, user.getChronicalStates());
        values.put(MedAssistSQLiteHelper.US_ALLERGIES, user.getAllergies());
        values.put(MedAssistSQLiteHelper.US_BLOODTYPE, user.getBloodType());
        values.put(MedAssistSQLiteHelper.US_CONTACT, user.getContact());
        values.put(MedAssistSQLiteHelper.US_LOCATION, String.valueOf(user.isLocationShared()));

        long insertId = database.insert(MedAssistSQLiteHelper.TABLE_USER, null, values);
        Cursor cursor = database.query(MedAssistSQLiteHelper.TABLE_USER,
                allColumns, MedAssistSQLiteHelper.US_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        User newUser = cursorToUser(cursor);
        cursor.close();

        return newUser;
    }

    public void removeUsers()
    {
        database.execSQL("delete from "+ MedAssistSQLiteHelper.TABLE_USER);
    }

    public User getUserData()
    {

        Cursor cursor = database.query(MedAssistSQLiteHelper.TABLE_USER, allColumns,
                null, null, null, null, null);

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            User user = cursorToUser(cursor);
            cursor.close();

            return user;
        }
        else
        {
            cursor.close();
            return null;
        }
    }

    private User cursorToUser(Cursor cursor)
    {

       /* */

        User user = new User();
        user.setId(cursor.getInt(0));
        user.setName(cursor.getString(1));
        user.setLastname(cursor.getString(2));
        user.setdOB(cursor.getString(3));
        user.setAlternativePhoneNo(cursor.getString(4));
        user.setChronicalStates(cursor.getString(5));
        user.setAllergies(cursor.getString(6));
        user.setBloodType(cursor.getInt(7));
        user.setContact(cursor.getString(8));
        user.setLocationSharing(Boolean.parseBoolean(cursor.getString(9)));

        return user;
    }
}
