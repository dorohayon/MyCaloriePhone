package com.example.akiscaloriephone;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.akiscaloriephone.FavoriteFoods.AddFavoriteBreakfastService;
import com.example.akiscaloriephone.StepCounter.StopStepCounterService;


public class NotificationsUtils {

    public static final String CHANNEL_ID_FAVORITE_NOTIFICATIONS ="4567";
    public static final int PENDING_INTENT_FAVORITES_NOTIFICATION =1468;
    public static final int PENDING_INTENT_STOP_STEP_COUNTER =5455;
    public static final int BREAKFAST_NOTIFICATION_ID=5489;
    public static final int LUNCH_NOTIFICATION_ID=1313;
    public static final int DINNER_NOTIFICATION_ID=8475;
    public static final String NOTIFICATION_TYPE_TAG ="notificationtypetag";


    public static void createBreakfestNotification(Context context){
        Log.e("test", "creating notification");
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_FAVORITE_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Time for breakfast")
                .setContentText("Click to add your favorite breakfast")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(contentIntentFavFoodsNotifications(context,BREAKFAST_NOTIFICATION_ID));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(BREAKFAST_NOTIFICATION_ID, builder.build());
    }
    public static void createDinnerNotification(Context context){
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_FAVORITE_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Time for dinner")
                .setContentText("Click to add your favorite dinner")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(contentIntentFavFoodsNotifications(context,DINNER_NOTIFICATION_ID));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(DINNER_NOTIFICATION_ID, builder.build());
    }
    public static void createLunchNotification(Context context){
        Log.e("test", "creating notification");

        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_FAVORITE_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Time for lunch")
                .setContentText("Click to add your favorite lunch")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(contentIntentFavFoodsNotifications(context,LUNCH_NOTIFICATION_ID));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(LUNCH_NOTIFICATION_ID, builder.build());
    }

    private static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Primary";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_FAVORITE_NOTIFICATIONS, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    public static Notification createStepsCounterNotification(Context context){
        createNotificationChannel(context);

       Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID_FAVORITE_NOTIFICATIONS)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle("Step Counter is running")
                .setContentText("Click to to open MyCaloriePhone settings")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(PendingIntent.getService(context,PENDING_INTENT_STOP_STEP_COUNTER,new Intent(context, StopStepCounterService.class),PendingIntent.FLAG_CANCEL_CURRENT))
                .setAutoCancel(true)
                .build();
       return notification;

    }




    private static PendingIntent contentIntentFavFoodsNotifications (Context context,int notificationIDTag) {
        Intent addFavFoods = new Intent(context, AddFavoriteBreakfastService.class);
        addFavFoods.putExtra(NOTIFICATION_TYPE_TAG,notificationIDTag);
        return PendingIntent.getService(
                context,
                PENDING_INTENT_FAVORITES_NOTIFICATION,
                addFavFoods,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
