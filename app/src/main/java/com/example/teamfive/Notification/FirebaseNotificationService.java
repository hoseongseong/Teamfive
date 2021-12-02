package com.example.teamfive.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.teamfive.MainActivity;
import com.example.teamfive.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseNotificationService extends FirebaseMessagingService {

    String info;
    String name;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public FirebaseNotificationService() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.e("Service", "pss");
        Map<String, String> data_notify = remoteMessage.getData();

        //알림 제어
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        if(data_notify != null){
            Log.e("FCMService","received");
            info = data_notify.get("info");
            name = data_notify.get("name");
            final String uid = mAuth.getCurrentUser().getUid();

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.putExtra("info", info);
            intent.putExtra("name", name);
            intent.putExtra("where", "2");
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            String channelId = "mychannel";

            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(name+"에 가까워짐")
                            .setAutoCancel(true)
                            .setSound(defaultUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelName = "channelName";
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManger.createNotificationChannel(channel);
            }
            notificationManger.notify(0, notificationBuilder.build());
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
