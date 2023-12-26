package com.example.project2102020;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class callNotification extends Service {

    private static final String CHANNEL_ID = "callNotificationChannel";
    private static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String userId = intent.getStringExtra("userId");
            String userName = intent.getStringExtra("userName");
            Toast.makeText(this, "Start Service" + userId + userName, Toast.LENGTH_SHORT).show();
            startCallInvitationService(userId, userName);
        }
        return START_STICKY;
    }

    private void startCallInvitationService(String userId, String userName) {
        // Initialize the Zego Cloud SDK service here

        // Create and show the notification
        createAndShowNotification();
    }

    private void createAndShowNotification() {
        // Create a notification channel if API level is >= 26 (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Call Invitations",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Call Invitation")
                .setContentText("You have a new call invitation.")
                .setSmallIcon(R.drawable.logo_croped)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Show the notification
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
