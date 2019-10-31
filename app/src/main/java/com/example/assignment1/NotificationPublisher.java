package com.example.assignment1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideType;
import com.example.assignment1.model.JobModel;

import java.util.List;
import java.util.Random;

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
                JobModel job = service.getRawJobList().get(new Random().nextInt(service.getRawJobList().size()));
                builder.setSmallIcon(R.drawable.ic_star_24dp);
                builder.setContentTitle("Become a " + job.getTitle() + " at " + job.getCompany());
                builder.setContentText("Apply now!");


            }
            else{
                builder.setSmallIcon(R.drawable.ic_star_24dp);
                builder.setContentTitle("Start looking now!");
                builder.setContentText("You haven't got any favorite jobs at the moment.");

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


