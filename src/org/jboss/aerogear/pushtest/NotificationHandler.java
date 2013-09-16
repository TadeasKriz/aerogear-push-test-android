package org.jboss.aerogear.pushtest;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;

public class NotificationHandler implements MessageHandler {

    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessage(Context context, Bundle message) {
        String alert = message.getString("alert");
        int badge = message.getInt("badge");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("GCM Notification")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(alert))
                .setContentText(alert)
                .setNumber(badge);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onDeleteMessage(Context context, Bundle message) {

    }

    @Override
    public void onError() {

    }
}
