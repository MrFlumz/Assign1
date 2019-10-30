package com.example.assignment1;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment1.model.JobModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import com.facebook.stetho.Stetho;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class BackgroundService extends Service {

    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT = "com.leafcastle.android.servicesdemo.BROADCAST_BACKGROUND_SERVICE_RESULT";
    public static final String EXTRA_TASK_RESULT = "task_result";
    public static final String EXTRA_TASK_TIME_MS = "task_time";
    private static final String LOG = "BG_SERVICE";
    private static final int NOTIFY_ID = 142;
    public static final String JOBLIST_UPDATED = "JOBLIST_UPDATED";
    //The IBinder instance to return

    private boolean started = false;
    private long wait = 1000L;
    public List<JobModel> RawJobList = new ArrayList<>();
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

                                .setSmallIcon(R.mipmap.ic_launcher)
                                //        .setContentIntent(pendingIntent)
                                .setChannelId("myChannel")
                                .build();

                //calling Android to
                startForeground(NOTIFY_ID, notification);
            }

            //do background thing
            doBackgroundSave(wait);
        } else {
            Log.d(LOG, "Background service onStartCommand - already started!");
        }
        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }



    //using recursion for running this as a loop
    public void doBackgroundSave(long time){

        //create asynch tasks that sleeps X ms and then sends broadcast

        BackgroundThingTask task = new BackgroundThingTask(this);
        task.execute(time); //L means long number format

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

    //lets make an asynch task to use just in this activity...
    private static class BackgroundThingTask extends AsyncTask<Long, String, String> {

        //WeakReference is Java's way of indicating that the referenced object can be garbage collected if needed
        //we need this to avoid holding onto the service if the asynch task goes on (causing memory leak)
        private WeakReference<BackgroundService> serviceRef;
        private long waitTimeInMilis;
        // only retain a weak reference to the activityReference
        BackgroundThingTask(BackgroundService service) {
            serviceRef = new WeakReference<>(service);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Long... time) {


            return "saved";
        }

        @Override
        protected void onPostExecute(String stringResult) {

            BackgroundService service = serviceRef.get();
            if (service != null) {

                service.broadcastTaskResult(stringResult);

                //if Service is still running, keep doing this recursively
                //if(service.started){
                    //service.doBackgroundSave(waitTimeInMilis);
                //}
            }
        }
    }

    void getGithubJobList(String filter){
        if (filter == "") {
            sendRequest("https://jobs.github.com/positions.json");
        }
        else {
            String url = "https://jobs.github.com/positions.json?description=" + filter;
            sendRequest(url);
        }
    }

    void getRoomJobList(){
        try {
            List<JobModel> temp = loadTasks();
            if (temp != null){
                RawJobList = temp;
                broadcastTaskResult(JOBLIST_UPDATED);}
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        RawJobList.clear();
        Gson gson = new GsonBuilder().create();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            JobModel job =  gson.fromJson(jsonArray.get(i).toString(), JobModel.class);
            RawJobList.add(job);
        }
        broadcastTaskResult(JOBLIST_UPDATED);
    }


    public void addJob(JobModel t){
        t = RawJobList.get(1);
        Log.d("logig",t.toString());
        try {
            ((JobApplication)getApplicationContext()).getJobDatabase().JobDao().insertAll(t);
        } catch (Exception e) {
            Log.e("MYAPP", "exception", e);
        }
    }
    public void deleteTask(JobModel t){
        ((JobApplication)getApplicationContext()).getJobDatabase().JobDao().delete(t);;
    }

    public List<JobModel> loadTasks(){
        return ((JobApplication)getApplicationContext()).getJobDatabase().JobDao().getAll();
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
}
