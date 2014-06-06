package com.medmobile.pid.medassist.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.medmobile.pid.medassist.app.DBAccess.AlarmsDataSource;

public class AlarmReceiver extends BroadcastReceiver
{
    private AlarmsDataSource datasource;
    int id;
    String medicineName;
    String medicineType;
    long currentTime;
    long interval;
    int dosage;
    int dosageType;
    int currentAmount;
    boolean vibrate;

    int remainingDoses;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        id = intent.getIntExtra("id", 0);
        medicineName = intent.getStringExtra("medicineName");
        medicineType = intent.getStringExtra("medicineType");
        currentTime = intent.getLongExtra("currentTime", 0);
        interval = intent.getLongExtra("timesADay", 0);
        dosage = intent.getIntExtra("dosage", 0);
        dosageType = intent.getIntExtra("dosageType", 0);
        currentAmount = intent.getIntExtra("currentAmount", 0);
        vibrate = Boolean.parseBoolean(intent.getStringExtra("vibrate"));
        int newAmount;


        if (isDoseFunctionalityEnabled())
            newAmount = getNewAmount(medicineType, dosage, dosageType, currentAmount);
        else
            newAmount = currentAmount;

        long newNextIntake = currentTime + interval;

        datasource.open();
        // Update the table of all Alarms (new amount and new next intake time)
        datasource.updateWhenAlarmed(id, newAmount, newNextIntake);

        if (isDoseFunctionalityEnabled())
        {
            remainingDoses = getRemainingDoses(medicineType, dosage, dosageType, newAmount);

            // if there are <= 3 remaining doses, then fire off a notification that user needs to refill
            if (remainingDoses <= 3) {
                String msg = "After current intake, you will have " + remainingDoses + " remaining doses of " + medicineName + ". " +
                        "You should probably consider a refill if your therapy is not ending.";

                NotificationCompat.Builder notifyBuilder =
                        new NotificationCompat.Builder(context)
                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                                .setContentTitle(medicineName + " is running out")
                                .setContentText(msg);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

                notifyBuilder.setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, notifyBuilder.build());
            }
        }
        // Invoke an activity (dialog) with information about the alarm
        // and the medicine that needs to be taken

        Intent dialog = new Intent();
        dialog.putExtra("currentTime", currentTime);
        dialog.putExtra("medicineName", medicineName);
        dialog.putExtra("medicineType", medicineType);
        dialog.putExtra("vibrate", vibrate);
        dialog.putExtra("dosage", dosage);
        dialog.putExtra("dosageType", dosageType);
        dialog.setClassName("com.medmobile.pid.medassist.app", "com.medmobile.pid.medassist.app.AlarmDialog");
        dialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialog);

        datasource.close();
    }

    private int getRemainingDoses(String type, int dose, int dType, int amount)
    {
        int result;

        if (type.equals("syrup"))
            result = amount / dose*dType;
        else
            result = amount / dose;

        return result;
    }

    private int getNewAmount(String type, int dose, int dType, int amount)
    {
        int result;

        if (type.equals("syrup"))
            result = amount - dose*dType;
        else
            result = amount - dose;

        return result;
    }

    private boolean isDoseFunctionalityEnabled()
    {
        if (dosage == -1 || currentAmount == -1)
            return false;
        else
            return true;
    }
}
