package com.example.assignment1;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class MainActivity extends AppCompatActivity implements JobAdapter.OnJobListener, JobAdapter.OnJobLongListener{

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
    AlertDialog.Builder searchAlert;
    ArrayList<Jobs> joblist = new ArrayList<Jobs>();
    RecyclerView rvJobs;
    JobAdapter adapter;
    ArrayList<JobModel> jobsList = new ArrayList<JobModel>();
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        loadData();
        /*
        if (savedInstanceState != null){
            //Do whatever you need with the string here, like assign it to variable.
            joblist = (ArrayList<Jobs>)savedInstanceState.getSerializable(JOBLIST);
        }
        */



        setContentView(R.layout.activity_main);
        searchAlert = new AlertDialog.Builder(this);
        rvJobs = (RecyclerView) findViewById(R.id.rvDemos);


        adapter = new JobAdapter(this, joblist,this, this);
        rvJobs.setAdapter(adapter);
        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        rvJobs.setItemAnimator(new SlideInUpAnimator());
        Decoration itemDecoration = new Decoration(this, R.dimen.item_offset);
        rvJobs.addItemDecoration(itemDecoration);
        builderInit();

        Transition fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);

    }


    private void loadData(){
        String base = "https://jobs.github.com/positions.json";
        sendRequest(base);
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
                Log.d("hihih", "That did not work!", error);
            }
        });

        queue.add(stringRequest);
    }

    private void parseJson(String json){
        Gson gson = new GsonBuilder().create();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonArray = (JsonArray) jsonParser.parse(json);
        for (int i = 0; i < jsonArray.size(); i++) {
            JobModel job =  gson.fromJson(jsonArray.get(i).toString(), JobModel.class);
            jobsList.add(job);

        }

        joblist = Jobs.parseJobList(jobsList,this);
        Log.d("hihih", joblist.get(7).getmCompany());
        adapter.notifyDataSetChanged();

    }

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
                int pos = data.getIntExtra(JOB_INDEX, -1);
                joblist.get(pos).setmScore((float)Double.parseDouble(data.getStringExtra(JOB_SCORE)));
                Log.d( "hejsa", data.getStringExtra(JOB_NOTE));
                joblist.get(pos).setmNote(data.getStringExtra(JOB_NOTE));
                joblist.get(pos).setmApplied(data.getBooleanExtra(JOB_STATUS,false));
                adapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == REQUEST_NOTE){
            if (resultCode == RESULT_OK) {
                int pos = data.getIntExtra(JOB_INDEX, -1);
                joblist.get(pos).setmNote(data.getStringExtra(JOB_NOTE));
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
        intent.putExtra(JOB_COMPANY, joblist.get(position).getmCompany());
        intent.putExtra(JOB_LOCATION, joblist.get(position).getmLocation());
        intent.putExtra(JOB_TITLE, joblist.get(position).getmTitle());
        intent.putExtra(JOB_DESCRIPTION, joblist.get(position).getmDescription());
        intent.putExtra(JOB_STATUS, joblist.get(position).getmApplied());

        // if score is 10, remove decimal as it cant fit
        if (joblist.get(position).getmScore()<9.9){
            intent.putExtra(JOB_SCORE,  String.format(Locale.US,"%.1f", joblist.get(position).getmScore()));}
        else {
            intent.putExtra(JOB_SCORE,  String.format(Locale.US,"%.0f", joblist.get(position).getmScore()));}

        intent.putExtra(JOB_NOTE, joblist.get(position).getmNote());
        intent.putExtra(JOB_INDEX, position);
        final String nameOfImage = "img_"+position;
        intent.putExtra(JOB_IMAGE, nameOfImage);
        ImageView imgView = (ImageView) rvJobs.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.imgLogo);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View,String>(imgView,"imageTransition");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);

        this.startActivityForResult(intent, REQUEST_NOTE , options.toBundle());
    }

    @Override
    public boolean onJobLongClick(int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra(JOB_COMPANY, joblist.get(position).getmCompany());
        intent.putExtra(JOB_LOCATION, joblist.get(position).getmLocation());
        intent.putExtra(JOB_TITLE, joblist.get(position).getmTitle());
        intent.putExtra(JOB_DESCRIPTION, joblist.get(position).getmDescription());
        intent.putExtra(JOB_STATUS, joblist.get(position).getmApplied());
        intent.putExtra(JOB_SCORE, String.format(Locale.US, "%.1f", joblist.get(position).getmScore()));
        intent.putExtra(JOB_INDEX, position);
        intent.putExtra(JOB_NOTE, joblist.get(position).getmNote());
        this.startActivityForResult(intent, REQUEST_NOTEACTIVITY);
        return true;
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        String someString = "this is a string";
        savedInstanceState.putSerializable(JOBLIST, joblist);
        //declare values before saving the state
        super.onSaveInstanceState(savedInstanceState);
    }

}
