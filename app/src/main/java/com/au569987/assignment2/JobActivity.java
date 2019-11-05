package com.au569987.assignment2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Locale;

public class JobActivity extends AppCompatActivity {

    TextView txtCompany;
    ImageView imgLogo;
    TextView txtLocation;
    TextView txtTitle;
    TextView txtDescription;
    TextView txtScore;
    TextView txtStatus;
    Button btnRemove;

    SeekBar SeekbarPicker;
    TextView txtNote;
    Button btnSave;
    Intent returnIntent;
    String returnstr;
    TextView txtApplied;
    int position;
    boolean applied;
    private BackgroundService BoundBackgroundService;
    private ServiceConnection ServiceConnection;
    private boolean bound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        this.setTitle("");
        Intent intent = new Intent(this, BackgroundService.class);

        Intent activityIntent = getIntent();
        position = activityIntent.getIntExtra(MainActivity.JOB_INDEX, -1);

        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        //get the current intent



        imgLogo = findViewById(R.id.imgLogo);
        txtCompany = findViewById(R.id.txtCompany);
        txtLocation = findViewById(R.id.txtLocation);
        txtTitle = findViewById(R.id.txtJobtitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtScore = findViewById(R.id.txtScore);
        txtStatus = findViewById(R.id.txtStatusStatic);
        btnSave = findViewById(R.id.btnSave);
        txtNote = findViewById(R.id.txtNote);
        txtApplied = findViewById(R.id.txtStatus);
        btnRemove = findViewById(R.id.btnRemove);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bound && BoundBackgroundService != null) {
                    BoundBackgroundService.delJob(BoundBackgroundService.getRawJobList().get(position));
                    BoundBackgroundService.getRawJobList().remove(position);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

    }



    public void setAppliedText(boolean applied){
        if(applied){
            txtApplied.setText(R.string.Applied);
            txtApplied.setTextColor(Color.rgb(0,200,0));
        }
        else{
            txtApplied.setText(R.string.AppliedNot);
            txtApplied.setTextColor(Color.rgb(200,0,0));
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) service;
            BoundBackgroundService = binder.getService();
            bound = true;
            //Toast.makeText(getApplicationContext(), "heelele"+Integer.toString(position), Toast.LENGTH_SHORT).show();
            txtCompany.setText(BoundBackgroundService.getRawJobList().get(position).getCompany());
            txtLocation.setText(BoundBackgroundService.getRawJobList().get(position).getLocation());
            txtTitle.setText(BoundBackgroundService.getRawJobList().get(position).getTitle());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtDescription.setText(Html.fromHtml(BoundBackgroundService.getRawJobList().get(position).getDescription(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                txtDescription.setText(Html.fromHtml(BoundBackgroundService.getRawJobList().get(position).getDescription()));
            }

            txtScore.setText(String.format(Locale.US,"%.1f",BoundBackgroundService.getRawJobList().get(position).getScore()));
            applied = BoundBackgroundService.getRawJobList().get(position).getApplied();
            setAppliedText(applied);
            txtNote.setText(BoundBackgroundService.getRawJobList().get(position).getNote());
            Glide.with(getApplicationContext())
                    .load(BoundBackgroundService.getRawJobList().get(position).getCompanyLogo()).into(imgLogo);

            PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(BoundBackgroundService.getRawJobList().get(position).getStatusColor()), PorterDuff.Mode.MULTIPLY);
            txtScore.getBackground().setColorFilter(colorfilter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
