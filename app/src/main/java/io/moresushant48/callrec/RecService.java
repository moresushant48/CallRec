package io.moresushant48.callrec;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class RecService extends Service {

    public static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    public static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        setNotificationAndStartForeground();

        registerReceiversForCallRec();

        return START_NOT_STICKY;
    }

    /*
    *   SET NOTIFICATION AND START FOREGROUND SERVICE.
    * */

    private void setNotificationAndStartForeground() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("CallRec")
                .setContentText("Service is running.")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }

    /*
    *   REGISTER CALL BROADCAST (@CallBr),
    *   TO RECEIVE INCOMING (@ACTION_IN) AND OUTGOING (@ACTION_OUT) CALLS.
    * */

    private void registerReceiversForCallRec() {

        CallBr callBr = new CallBr();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_IN);
        intentFilter.addAction(ACTION_OUT);

        registerReceiver(callBr, intentFilter);
    }
}
