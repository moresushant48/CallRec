package io.moresushant48.callrec;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_ID = "RunningNotificationChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Running Notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

        }

    }
}
