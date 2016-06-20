package com.gabrielezanelli.schoolendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.gabrielezanelli.schoolendar.fragments.FragmentEvent;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.ALARM_SERVICE;

public class NotificationPusher extends BroadcastReceiver {
    private static String EXTRA_LONG_EVENT_ID;

    @Override
    public void onReceive(Context context, Intent workIntent) {
        EXTRA_LONG_EVENT_ID = context.getString(R.string.EXTRA_LONG_EVENT_ID);

        // Gets the event from the database using the ID received in the intent
        long ID = workIntent.getLongExtra(EXTRA_LONG_EVENT_ID, -1);
        Log.d("ID Pusher", "" + ID);


        EventManager eventManager = EventManager.getInstance(context);
        Event event = null;
        try {
            event = eventManager.getEvent(ID);
        } catch (SQLException e) {
            Toast.makeText(context,context.getString(R.string.error_event_not_found),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if (event != null) {
            String notificationTitle;
            String notificationText;
            // Sets the notification
            if (event.hasSubject()) {
                notificationTitle = event.getSubject() + "'s " + event.getType().toString();
                notificationText = context.getString(R.string.event_notification_default_text);
            } else {
                notificationTitle = event.getType().toString();
                notificationText = event.getTitle();
            }

            // Create the intent that is gonna be triggered when the notification is clicked and add to the stack
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.putExtra(EXTRA_LONG_EVENT_ID, ID);

            /** Stack Method used for Regular Activities
             TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
             stackBuilder.addParentStack(FragmentEvent.class);
             stackBuilder.addNextIntent(notificationIntent);

             PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
             */

            /** Sexy Method used for Special Activities */
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, UniqueID.getUniqueID(),
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            /** Sexy ends here */


            // Gets the default sound for notifications
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // Create the notification with a title,icon,text,sound and vibration
            NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_alarm_white_24dp)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setContentIntent(pendingIntent)
                    // Notification auto cancel itself when clicked
                    .setAutoCancel(true)
                    .setSound(uri)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setLights(Color.BLUE, 3000, 3000);

            // Build the notification and issue it
            Notification notification = nBuilder.build();
            NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nManager.notify((int)ID, notification);
        }
        else {
            Log.d("Error","Empty Extras");
        }

    }

    public static void scheduleNotification(Context context, long ID, long notificationTime) {
        // Creates the intent that is gonna be triggered when the notification is clicked
        Intent intentPusher = new Intent(context, NotificationPusher.class);
        // Puts the ID of the Event to show
        intentPusher.putExtra(context.getString(R.string.EXTRA_LONG_EVENT_ID), ID);
        // Gets the alarm manager and set the alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intentPusher, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);

        Log.d("Notification Pusher","Scheduled");
    }

    public static class UniqueID {
        private final static AtomicInteger uniqueID = new AtomicInteger(0);
        public static int getUniqueID() {
            return uniqueID.incrementAndGet();
        }
    }
}
