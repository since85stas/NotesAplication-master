package com.batura.stas.notesaplication.AlarmFuncs;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Toast;

import com.batura.stas.notesaplication.EditorActivity;
import com.batura.stas.notesaplication.R;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

/**
 * Created by seeyo on 03.07.2018.
 */



public class AlarmReceiver extends BroadcastReceiver {

    //TODO добавить звуковой сигнал на напоминание

    //ключ с текстом напоминания
    public final static String NOTIF_TEXT =  AlarmSetActivity.class.getPackage() + ".NOTIF_TEXT";

    //  текст напоминания
    private String notifText;

    @Override
    public void onReceive(Context context, Intent intent) {

        notifText = intent.getExtras().getString(NOTIF_TEXT);
        //().getExtras().getString(NOTE_BODY); // получаем текст для напоминания
        Toast.makeText(context, "Notification from " + R.string.app_name,
                Toast.LENGTH_LONG).show();
        buildNotification(context);
    }

    private void buildNotification(Context context) {

        //String notificationText = get().getExtras().getString(NOTE_BODY); // получаем текст для напоминания

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        String channelId = "default_channel_id";
        String channelDescription = "Default Channel";
//Check if notification channel exists and if not create one
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);
            if (notificationChannel == null) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                notificationChannel = new NotificationChannel(channelId, channelDescription, importance);
                notificationChannel.setLightColor(Color.GREEN);
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        Notification.Builder builder = new Notification.Builder(context);

//notificationManager.notify as usual


        Intent intent = new Intent(context, EditorActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notificTitle)).setContentText(notifText)
                .setContentInfo(context.getString(R.string.notificInfo)).setTicker(context.getString(R.string.notifTicker))
                .setLights(0xFFFF0000, 500, 500)
                //.setChannelId(id)
                .setContentIntent(pendingIntent).setAutoCancel(true);

        Notification notification = builder.build();
        //notification.so

        notificationManager.notify(2, notification);
    }
}
