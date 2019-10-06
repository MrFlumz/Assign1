package com.example.assignment1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements JobAdapter.OnJobListener, JobAdapter.OnJobLongListener{

    public static final String JOB_COMPANY = "JOB_COMPANY";
    public static final String JOB_LOCATION = "JOB_LOCATION";
    public static final String JOB_TITLE = "JOB_TITLE";
    public static final String JOB_DESCRIPTION = "JOB_DESCRIPTION";
    public static final String JOB_STATUS = "JOB_STATUS";
    public static final String JOB_SCORE = "JOB_SCORE";
    public static final String JOB_IMAGE = "JOB_IMAGE";
    public static final String JOB_POSITION = "JOB_IMAGE";
    public static final String JOB_NOTE = "JOB_NOTE";
    public static final String JOB_INDEX = "JOB_INDEX";
    public static final String TEXTFIELD = "textfield";
    public static final int PICKER_INPUT = 101;
    public static final int REQUEST_NOTE = 102;
    public static final int REQUEST_NOTEACTIVITY = 100;
    public static final String JOBLIST = "JOBLIST";
    Button btnPicker;
    Button btnEditText;
    Button btnSlider;
    Intent intentPicker;
    Intent intentEditText;
    Intent intentSlider;
    TextView TxtMain;
    AlertDialog.Builder searchAlert;
    private String searchText = "Search";
    ArrayList<Jobs> joblist;
    private Class PickerActivity;
    RecyclerView rvJobs;
    JobAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            //Do whatever you need with the string here, like assign it to variable.
            joblist = (ArrayList<Jobs>)savedInstanceState.getSerializable(JOBLIST);
        }
        else{
            String next[] = {};
            List<String[]> list = new ArrayList<String[]>();
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            try {
                CSVReader reader = new CSVReaderBuilder(new InputStreamReader(getAssets().open("Jobs.csv"))).withCSVParser(parser).build();
                while(true) {
                    next = reader.readNext();
                    if(next != null) {
                        list.add(next);
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("JJJJ", list.get(0)[0]);

            joblist = Jobs.parseJobList(list,this);
            Log.d("THOMAS", joblist.get(0).getmCompany());
        }


        Log.d("THOMAS", joblist.get(0).getmCompany());

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
