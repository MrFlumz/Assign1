package com.au569987.assignment2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.au569987.assignment2.model.JobModel;

import java.util.List;
import java.util.Random;

/*
* https://developer.android.com/guide/topics/ui/notifiers/notifications
* */

public class NotificationPublisher extends BroadcastReceiver {

    private BackgroundService BoundBackgroundService;
    private boolean bound = false;
    List<JobModel> favoriteJobs;
    @Override
    public void onReceive(final Context context, Intent intent) {
        IBinder binder = peekService(context, new Intent(context, BackgroundService.class));

        if (binder == null){}
        else {
            BackgroundService service = ((BackgroundService.BackgroundServiceBinder) binder).getService();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "JobHunt");
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            try {
                favoriteJobs = service.getFavoriteJobs();
            }catch (Exception e){}

            if (favoriteJobs.size()!= 0) {
                JobModel job = favoriteJobs.get(new Random().nextInt(favoriteJobs.size()));
                builder.setSmallIcon(R.drawable.ic_star_24dp);
                builder.setContentTitle(service.getString(R.string.Notification_become_pt1) + job.getTitle() + service.getString(R.string.Notification_become_pt2) + job.getCompany());
                builder.setContentText(service.getString(R.string.Notification_apply_now));
            }
            else{
                builder.setSmallIcon(R.drawable.ic_star_24dp);
                builder.setContentTitle(service.getString(R.string.Notification_start_looking));
                builder.setContentText(service.getString(R.string.Notification_no_favorites));

            }


            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                String channelId = "Your_channel_id";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_LOW);
                mNotificationManager.createNotificationChannel(channel);
                builder.setChannelId(channelId);
            }
            mNotificationManager.notify(100, builder.build());
        }

    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) service;
            BoundBackgroundService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}


