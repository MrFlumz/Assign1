package com.example.assignment1;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements JobAdapter.OnJobListener, JobAdapter.OnJobLongListener, JobAdapter.OnJobFavoriteListener{

    public static final String JOB_COMPANY = "JOB_COMPANY";
    public static final String JOB_LOCATION = "JOB_LOCATION";
    public static final String JOB_TITLE = "JOB_TITLE";
    public static final String JOB_DESCRIPTION = "JOB_DESCRIPTION";
    public static final String JOB_STATUS = "JOB_STATUS";
    public static final String JOB_SCORE = "JOB_SCORE";
    public static final String JOB_IMAGE = "JOB_IMAGE";
    public static final String JOB_NOTE = "JOB_NOTE";
    public static final String JOB_INDEX = "JOB_INDEX";
    public static final int REQUEST_NOTE = 102;
    public static final int REQUEST_NOTEACTIVITY = 100;
    public static final String JOBLIST = "JOBLIST";
    public static final String BG = "BG Service";
    private BackgroundService BoundBackgroundService;
    private ServiceConnection ServiceConnection;
    private boolean bound = false;
    AlertDialog.Builder searchAlert;
    RecyclerView rvJobs;
    JobAdapter adapter;
    EditText txtSearch;
    Button btnSearch;
    //for background service
    private long task_time = 4*1000; //4 ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startBackgroundService(task_time);
        super.onCreate(savedInstanceState);
        // Bind to LocalService
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);






        setContentView(R.layout.activity_main);
        searchAlert = new AlertDialog.Builder(this);
        rvJobs = (RecyclerView) findViewById(R.id.rvDemos);


        builderInit();

        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);

        txtSearch = findViewById(R.id.txtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        //bindToBackgroundService();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bound && BoundBackgroundService!=null){
                    try {
                        BoundBackgroundService.getJobList(txtSearch.getText().toString());
                    }catch (Exception e){
                        Log.e("h","hhhh",e);
                    }
                }
            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("bg", "registering receivers");

        IntentFilter filter = new IntentFilter();
        filter.addAction(BackgroundService.BROADCAST_BACKGROUND_SERVICE_RESULT);

        //can use registerReceiver(...)
        //but using local broadcasts for this service:
        LocalBroadcastManager.getInstance(this).registerReceiver(onBackgroundServiceResult, filter);


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("bg", "unregistering receivers");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onBackgroundServiceResult);


    }

    //define our broadcast receiver for (local) broadcasts.
    // Registered and unregistered in onStart() and onStop() methods
    private BroadcastReceiver onBackgroundServiceResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(BG, "Broadcast reveiced from bg service");
            String result = intent.getStringExtra(BackgroundService.EXTRA_TASK_RESULT);
            if(result==null){
                //result = getString(R.string.err_bg_service_result);
                result = "Error";
            }
            if (result==BackgroundService.JOBLIST_UPDATED){
                handleBackgroundResult(result);
            }

        }
    };

    private void handleBackgroundResult(String result){
        Toast.makeText(this, "Application closed", Toast.LENGTH_SHORT).show();
            adapter = new JobAdapter(getApplicationContext(), BoundBackgroundService.getRawJobList(),MainActivity.this, MainActivity.this, MainActivity.this);
            rvJobs.setAdapter(adapter);

        //runLayoutAnimation(rvJobs);
    }

    //starts background service, taskTime indicates desired sleep period in ms for broadcasts
    private void startBackgroundService(long taskTime){
        Intent backgroundServiceIntent = new Intent(MainActivity.this, BackgroundService.class);
        backgroundServiceIntent.putExtra(BackgroundService.EXTRA_TASK_TIME_MS, taskTime);
        startService(backgroundServiceIntent);
    }




    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) service;
            BoundBackgroundService = binder.getService();
            bound = true;

            try {
                BoundBackgroundService.getRoomJobList();
            }catch (Exception e){}


            adapter = new JobAdapter(getApplicationContext(), BoundBackgroundService.getRawJobList(),MainActivity.this, MainActivity.this,MainActivity.this);
            rvJobs.setAdapter(adapter);
            rvJobs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            //rvJobs.setItemAnimator(new SlideInUpAnimator());
            Decoration itemDecoration = new Decoration(getApplicationContext(),R.dimen.card_offset_R,R.dimen.card_offset_T,R.dimen.card_offset_L,R.dimen.card_offset_B);
            rvJobs.addItemDecoration(itemDecoration);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.quit_application:
                finish();
                Toast.makeText(this, "Application closed", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NOTEACTIVITY) {
            if (resultCode == RESULT_OK) {
                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == REQUEST_NOTE){
            if (resultCode == RESULT_OK) {
                adapter.notifyDataSetChanged();
            }
        }

    }

    void builderInit(){
        searchAlert.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        searchAlert.setView(input);



    }



    @Override
    public void onJobClick(int position) {


        Toast.makeText(MainActivity.this, "Clicked on pos "+position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, JobActivity.class);
        intent.putExtra(JOB_INDEX, position);
        ImageView imgView = (ImageView) rvJobs.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.imgLogo);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(imgView,"imageTransition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
        this.startActivityForResult(intent, REQUEST_NOTE , options.toBundle());
    }

    @Override
    public boolean onJobLongClick(int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(JOB_INDEX, position);
        this.startActivityForResult(intent, REQUEST_NOTEACTIVITY);
        return true;
    }

    @Override
    public void favoriteItem(int position) {
        if (bound && BoundBackgroundService != null) {
            if (BoundBackgroundService.getRawJobList().get(position).getFavorited()) { // hvis allerede fav, så slet
                BoundBackgroundService.delJob(BoundBackgroundService.getRawJobList().get(position));
                BoundBackgroundService.getRawJobList().get(position).setFavorited(false);
                Toast.makeText(this, "Non Fav", Toast.LENGTH_SHORT).show();}
            else {
            BoundBackgroundService.addJob(BoundBackgroundService.getRawJobList().get(position));
            BoundBackgroundService.getRawJobList().get(position).setFavorited(true);
            Toast.makeText(this, "Fav", Toast.LENGTH_SHORT).show();}
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        String someString = "this is a string";
        //savedInstanceState.putSerializable(JOBLIST, BoundBackgroundService.getjoblist());
        //declare values before saving the state
        super.onSaveInstanceState(savedInstanceState);
    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fadein);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
