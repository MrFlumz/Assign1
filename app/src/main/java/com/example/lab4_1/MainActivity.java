package com.example.lab4_1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.IOException;
import java.io.FileReader;
import java.util.List;


public class MainActivity extends AppCompatActivity implements JobAdapter.OnJobListener, JobAdapter.OnJobLongListener{

    public static final String JOB_COMPANY = "JOB_COMPANY";
    public static final String JOB_LOCATION = "JOB_LOCATION";
    public static final String JOB_TITLE = "JOB_TITLE";
    public static final String JOB_DESCRIPTION = "JOB_DESCRIPTION";
    public static final String JOB_STATUS = "JOB_STATUS";
    public static final String JOB_SCORE = "JOB_SCORE";
    public static final String JOB_IMAGE = "JOB_IMAGE";
    public static final String JOB_POSITION = "JOB_IMAGE";

    public static final String TEXTFIELD = "textfield";
    public static final int PICKER_INPUT = 100;
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

        joblist = Jobs.parseJobList(list);
        Log.d("THOMAS", joblist.get(0).getmCompany());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchAlert = new AlertDialog.Builder(this);
        rvJobs = (RecyclerView) findViewById(R.id.rvDemos);

        adapter = new JobAdapter(this, joblist,this, this);
        rvJobs.setAdapter(adapter);
        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        rvJobs.setItemAnimator(new SlideInUpAnimator());

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
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.search_item:
                searchAlert.show();
                return true;
            case R.id.upload_item:
                // do your code
                return true;
            case R.id.copy_item:
                // do your code
                return true;
            case R.id.print_item:
                // do your code
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKER_INPUT) {
            if (resultCode == RESULT_OK) {
                TxtMain.setText("number from picker: " + data.getStringExtra(TEXTFIELD));
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

        // Set up the buttons
        searchAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchText = input.getText().toString();
                Toast.makeText(MainActivity.this, "Searching for Item: "+searchText, Toast.LENGTH_SHORT).show();
            }
        });
        searchAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }


    @Override
    public void onJobClick(int position) {
        Toast.makeText(MainActivity.this, "Clicked on pos "+position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PickerActivity.class);
        intent.putExtra(JOB_COMPANY, joblist.get(position).getmCompany());
        intent.putExtra(JOB_LOCATION, joblist.get(position).getmLocation());
        intent.putExtra(JOB_TITLE, joblist.get(position).getmTitle());
        intent.putExtra(JOB_DESCRIPTION, joblist.get(position).getmDescription());
        intent.putExtra(JOB_STATUS, joblist.get(position).getmApplied());
        intent.putExtra(JOB_SCORE, Double.toString(joblist.get(position).getmScore()));
        final String nameOfImage = "img_"+position;
        intent.putExtra(JOB_IMAGE, nameOfImage);
        ImageView imgView = (ImageView) rvJobs.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.imgLogo);
        TextView txtcompany = (TextView) rvJobs.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.txtCompany);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View,String>(imgView,"imageTransition");
        pairs[1] = new Pair<View,String>(txtcompany,"companyTransition");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
        this.startActivity(intent, options.toBundle());
    }

    @Override
    public boolean onJobLongClick(int position) {
        Intent intent = new Intent(this, SliderActivity.class);
        intent.putExtra(JOB_COMPANY, joblist.get(position).getmCompany());
        intent.putExtra(JOB_LOCATION, joblist.get(position).getmLocation());
        intent.putExtra(JOB_TITLE, joblist.get(position).getmTitle());
        intent.putExtra(JOB_DESCRIPTION, joblist.get(position).getmDescription());
        intent.putExtra(JOB_STATUS, joblist.get(position).getmApplied());
        intent.putExtra(JOB_SCORE, Double.toString(joblist.get(position).getmScore()));
        intent.putExtra(JOB_POSITION,position);
        this.startActivity(intent);
        return true;
    }
}
