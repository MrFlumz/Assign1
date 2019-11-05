package com.au569987.assignment2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.au569987.assignment2.model.JobModel;

import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    JobModel job;
    TextView txtScore;
    Intent intent;
    String newScore;
    boolean applied = false;
    boolean favorited = false;
    TextView txtApplied;
    Button btnSave;
    Button btnCancel;
    TextView txtCompany;
    TextView txtLocation;
    TextView txtStatus;
    EditText txtNote;
    CheckBox switchApplied;
    SeekBar seekBar;
    String score = "0.0";
    ImageView imgFavorite;
    Bundle msavedInstanceState;
    int position = 0;
    private BackgroundService BoundBackgroundService;
    private ServiceConnection ServiceConnection;
    private boolean bound = false;
    JobModel TempJob = new JobModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        this.setTitle("");
        Intent intent = new Intent(this, BackgroundService.class);
        Intent activityIntent = getIntent();
        position = activityIntent.getIntExtra(MainActivity.JOB_INDEX, -1);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        msavedInstanceState = savedInstanceState;



        txtCompany = findViewById(R.id.txtCompany);
        txtLocation = findViewById(R.id.txtLocation);
        switchApplied = findViewById(R.id.SwitchApplied);
        txtStatus = findViewById(R.id.txtStatusStatic);
        seekBar = findViewById(R.id.seekBar);
        txtScore = findViewById(R.id.txtScore);
        btnCancel = findViewById(R.id.btnExit);
        btnSave = findViewById(R.id.btnSave);
        txtNote = findViewById(R.id.txtNote);
        imgFavorite = findViewById(R.id.imgFavorite);
        job = new JobModel(); // is only used to get color conversion



        //txtStatus.setText(intent.getStringExtra(MainActivity.JOB_STATUS));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(bound && BoundBackgroundService!=null) {
                    String color = job.CalcStatusColor((double) i / 10);
                    PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
                    txtScore.getBackground().setColorFilter(colorfilter);
                    seekBar.getThumb().setColorFilter(colorfilter);
                    seekBar.getProgressDrawable().setColorFilter(colorfilter);
                    score = Double.toString((double) i / 10);

                    TempJob.setScore(Float.parseFloat(score));
                    txtScore.setText(score);

                    }
                    if (i == 100) {
                        txtScore.setText("10 ");
                        final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                        txtScore.startAnimation(animShake);
                    }else{
                        txtScore.setText(score);
                    }
                }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_OK,returnIntent);
                if (TempJob.getFavorited()) {
                    BoundBackgroundService.addJob(TempJob);}
                else {
                    BoundBackgroundService.delJob(TempJob);}
                finish();
            }
        });

        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favorited = favorited ? false : true;
                TempJob.setFavorited(favorited);
                setFavorite(favorited);
            }
        });

        switchApplied.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(bound && BoundBackgroundService!=null) {
                    if (switchApplied.isChecked()) {
                        TempJob.setApplied(true);
                        applied = true;
                        setAppliedText(applied);

                    } else {
                        TempJob.setApplied(false);
                        applied = false;
                        setAppliedText(applied);
                    }
                }
            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.BackgroundServiceBinder binder = (BackgroundService.BackgroundServiceBinder) service;
            BoundBackgroundService = binder.getService();
            bound = true;
            if (msavedInstanceState != null){
                TempJob =  msavedInstanceState.getParcelable("Job");
            }else{
            TempJob = BoundBackgroundService.RawJobList.get(position);
            }
            txtCompany.setText(TempJob.getCompany());
            txtLocation.setText(TempJob.getLocation());
            txtNote.setText(TempJob.getNote());

            txtScore.setText(String.format(Locale.US,"%.1f",TempJob.getScore()));
            applied = TempJob.getApplied();
            setAppliedText(applied);
            txtNote.setText(TempJob.getNote());

            PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(TempJob.getStatusColor()), PorterDuff.Mode.MULTIPLY);
            txtScore.getBackground().setColorFilter(colorfilter);

            switchApplied.setChecked(applied);

            seekBar.setMax(100);
            seekBar.setProgress((int) (TempJob.getScore() * 10));
            seekBar.getThumb().setColorFilter(colorfilter);
            seekBar.getProgressDrawable().setColorFilter(colorfilter);

            favorited = TempJob.getFavorited();
            setFavorite(favorited); // change star on note page
            }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            BoundBackgroundService = null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setAppliedText(boolean applied){
        if(applied){
            switchApplied.setText(R.string.Applied);
            switchApplied.setTextColor(Color.rgb(0,200,0));
        }
        else{
            switchApplied.setText(R.string.AppliedNot);
            switchApplied.setTextColor(Color.rgb(200,0,0));
        }
    }

    void setFavorite(boolean fav){
        if(bound && BoundBackgroundService!=null){
            if (fav){
                imgFavorite.setImageResource(R.drawable.ic_star_24dp);
            }
            else{
                imgFavorite.setImageResource(R.drawable.ic_star_border_24dp);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        TempJob.setNote(txtNote.getText().toString());
        savedInstanceState.putParcelable("Job",TempJob);
        //declare values before saving the state
        super.onSaveInstanceState(savedInstanceState);
    }

}
