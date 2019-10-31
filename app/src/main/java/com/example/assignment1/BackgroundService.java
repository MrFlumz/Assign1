package com.example.assignment1;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment1.model.JobModel;
import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BackgroundService extends Service {

    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT = "com.leafcastle.android.servicesdemo.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";
    public static final String EXTRA_TASK_TIME_MS = "task_time";
    private static final String LOG = "BG_SERVICE";
    private static final int NOTIFY_ID = 169;
    public static final String JOBLIST_UPDATED = "JOBLIST_UPDATED";
    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION = "notification";
    //The IBinder instance to return

    private com.example.assignment1.JobRepository mRepository;
    private boolean started = false;
    private long wait = 1000L;
    public List<JobModel> RawJobList = new ArrayList<>();
    private LiveData<List<JobModel>> mAllJobs;
    RequestQueue queue;

    private static final long DEFAULT_WAIT = 30*1000; //default = 30s

    //whether to run as a ForegroundService (with permanent notification, harder to kill)
    private boolean runAsForegroundService = true;


    //extend the Binder class - we will return and instance of this in the onBind()
    public class BackgroundServiceBinder extends Binder {
        //return ref to service (or at least an interface) that activity can call public methods on
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }
    private final IBinder binder = new BackgroundServiceBinder();
    @Override
    //very important! return your IBinder (your custom Binder)
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //simple method for returning current count
    public int getCount(){
        return 1;
    }

    //simple method for returning current count
    public List<JobModel> getRawJobList(){
        return RawJobList;
    }



    public void setRawJobList(List<JobModel> list ){
        RawJobList = list;
    }


    public BackgroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG, "Background service onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //in this case we only start the background running loop once
        if(!started && intent!=null) {
            //for debugging/viewing database
            enableStethos();
            wait = intent.getLongExtra(EXTRA_TASK_TIME_MS, DEFAULT_WAIT);
            Log.d(LOG, "Background service onStartCommand with wait: " + wait + "ms");
            started = true;
            try {
                mRepository = new JobRepository(getApplication());
            }
            catch (Exception e){}



            if(runAsForegroundService) {

                //Intent notificationIntent = new Intent(this, MainActivity.class);
                //PendingIntent pendingIntent =
                //        PendingIntent.getActivity(this, 0, notificationIntent, 0);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) { //needed because channels are not supported on older versions
                    NotificationChannel mChannel = new NotificationChannel("myChannel", "Visible myChannel", NotificationManager.IMPORTANCE_LOW);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.createNotificationChannel(mChannel);
                }

                Notification notification =
                        new NotificationCompat.Builder(this, "myChannel")

                                .setSmallIcon(R.drawable.ic_star_24dp)
                                //        .setContentIntent(pendingIntent)
                                .setChannelId("myChannel")
                                .build();

                //calling Android to
                startForeground(NOTIFY_ID, notification);
                scheduleNotification(this,3000,100);
            }

            //do background thing
            //startNoficationTask(5000L);
        } else {
            Log.d(LOG, "Background service onStartCommand - already started!");
        }
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private void startNoficationTask(Long waitTimeInMilis){
        //create asynch tasks that sleeps X ms and then sends notification
        BackgroundNotifications task = new BackgroundNotifications(this,getApplicationContext());
        task.execute(5000L); //L means long number format
    }


    //lets make an asynch task to use just in this activity...
    private static class BackgroundNotifications extends AsyncTask<Long, Boolean, Boolean> {
        private WeakReference<BackgroundService> serviceRef;
        private Context mContext;
        private long waitTimeInMilis;
        // only retain a weak reference to the activityReference
        BackgroundNotifications(BackgroundService service, Context context) {
            mContext = context;
            serviceRef = new WeakReference<>(service);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Long... time) {
            waitTimeInMilis = time[0];
            try {
                Thread.sleep(waitTimeInMilis);
            } catch (Exception e) { return false;}
            return true;
        }

        @Override
        protected void onPostExecute(Boolean stringResult) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "JobHunt")
                    .setSmallIcon(R.drawable.ic_star_24dp)
                    .setContentTitle("hej")
                    .setContentText("hej")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            // === Removed some obsoletes
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            {
                String channelId = "Your_channel_id";
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_LOW);
                mNotificationManager.createNotificationChannel(channel);
                builder.setChannelId(channelId);
            }
            mNotificationManager.notify(NOTIFY_ID,builder.build());

            Toast.makeText(mContext, "Application tried notification", Toast.LENGTH_LONG).show();

            BackgroundService service = serviceRef.get();
            if (service != null) {
                service.startNoficationTask(waitTimeInMilis);
            }
        }
    }

    //send local broadcast
    private void broadcastTaskResult(String result){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_BACKGROUND_SERVICE_RESULT);
        broadcastIntent.putExtra(EXTRA_TASK_RESULT, result);
        Log.d(LOG, "Broadcasting:" + result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        started = false;
        Log.d(LOG,"Background service destroyed");
        super.onDestroy();
    }

    void getJobList(String filter)throws ExecutionException, InterruptedException{
        RawJobList = mRepository.getAllJobs();
        if (filter == "") {
            sendRequest("https://jobs.github.com/positions.json");
        }
        else {
            String url = "https://jobs.github.com/positions.json?description=" + filter;
            sendRequest(url);
        }
    }

    void getRoomJobList() throws ExecutionException, InterruptedException{
        RawJobList.clear();
        RawJobList = mRepository.getAllJobs();
    }

    List<JobModel> getFavoriteJobs() throws ExecutionException, InterruptedException{
        return mRepository.getAllJobs();
    }


    private void sendRequest(String url){
        if(queue==null){
            queue = Volley.newRequestQueue(this);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("hihih","hel2");
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Application could not load data", Toast.LENGTH_LONG).show();
                Log.d("hihih", "That did not work!", error);
            }
        });

        queue.add(stringRequest);
    }

    private void parseJson(String json){
        Gson gson = new GsonBuilder().create();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(json);
        int sizeOfFavorite = RawJobList.size();

        for (int i = 0; i < jsonArray.size(); i++) {
            JobModel job =  gson.fromJson(jsonArray.get(i).toString(), JobModel.class);
            boolean same = false;
            for (int u = 0; u < sizeOfFavorite; u++) {
                if (job.getId().equals(RawJobList.get(u).getId())){
                    same = true;
                }
            }

            if (!same) {RawJobList.add(job);}
        }
        broadcastTaskResult(JOBLIST_UPDATED);
    }


    public void addJob(JobModel t){
        try {
            mRepository.insert(t);
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
        }
    }
    public void delJob(JobModel t){
        try {
            mRepository.remove(t);
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
        }
    }


    private void enableStethos(){

           /* Stetho initialization - allows for debugging features in Chrome browser
           See http://facebook.github.io/stetho/ for details
           1) Open chrome://inspect/ in a Chrome browse
           2) select 'inspect' on your app under the specific device/emulator
           3) select resources tab
           4) browse database tables under Web SQL
         */
        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(
                        Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(
                        Stetho.defaultInspectorModulesProvider(this))
                .build());
        /* end Stethos */
    }


    public void scheduleNotification(Context context, long delay, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
        Intent intent = new Intent(context, BackgroundService.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(),2*60*1000,pendingIntent);
    }
}
