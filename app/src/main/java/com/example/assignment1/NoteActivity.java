package com.example.assignment1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class NoteActivity extends AppCompatActivity {

    Jobs job;
    TextView txtScore;
    Intent intent;
    String newScore;
    boolean applied;
    TextView txtApplied;
    Button btnSave;
    Button btnCancel;
    TextView txtCompany;
    TextView txtLocation;
    TextView txtStatus;
    EditText txtNote;
    Switch switchApplied;
    SeekBar seekBar;
    String score;
    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        this.setTitle("Slider");

        txtCompany = findViewById(R.id.txtCompany);
        txtLocation = findViewById(R.id.txtLocation);
        switchApplied = findViewById(R.id.SwitchApplied);
        txtStatus = findViewById(R.id.txtStatusStatic);
        seekBar = findViewById(R.id.seekBar);
        txtApplied = findViewById(R.id.txtApplied);
        txtScore = findViewById(R.id.txtScore);
        btnCancel = findViewById(R.id.btnSave);
        btnSave = findViewById(R.id.btnSave);
        txtNote = findViewById(R.id.txtNote);
        intent = getIntent();
        position = intent.getIntExtra(MainActivity.JOB_INDEX, -1);
        applied = intent.getBooleanExtra(MainActivity.JOB_STATUS,false);
        Log.d("hejhej", Boolean.toString(applied));
        setAppliedText(applied);
        txtCompany.setText(intent.getStringExtra(MainActivity.JOB_COMPANY));
        txtLocation.setText(intent.getStringExtra(MainActivity.JOB_LOCATION));
        txtNote.setText(intent.getStringExtra(MainActivity.JOB_NOTE));
        score = intent.getStringExtra(MainActivity.JOB_SCORE);
        txtScore.setText(score);
        job = new Jobs("", "", "", ""); // is only used to get color conversion
        String color = job.setmStatusColor(Double.parseDouble(intent.getStringExtra(MainActivity.JOB_SCORE)));
        PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
        txtScore.getBackground().setColorFilter(colorfilter);

        //txtStatus.setText(intent.getStringExtra(MainActivity.JOB_STATUS));
        switchApplied.setChecked(applied);

        seekBar.setMax(100);
        seekBar.setProgress((int) (Double.parseDouble(score) * 10));
        seekBar.getThumb().setColorFilter(colorfilter);
        seekBar.getProgressDrawable().setColorFilter(colorfilter);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String color = job.setmStatusColor((double) i / 10);
                PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
                txtScore.getBackground().setColorFilter(colorfilter);
                seekBar.getThumb().setColorFilter(colorfilter);
                seekBar.getProgressDrawable().setColorFilter(colorfilter);
                score = Double.toString((double) i / 10);
                txtScore.setText(score);

                if (i == 100) {
                    final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    txtScore.startAnimation(animShake);
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
                returnIntent.putExtra(MainActivity.JOB_SCORE,score);
                returnIntent.putExtra(MainActivity.JOB_STATUS, applied);
                returnIntent.putExtra(MainActivity.JOB_NOTE,txtNote.getText().toString());
                Log.d("helolo", txtNote.getText().toString());
                returnIntent.putExtra(MainActivity.JOB_INDEX, position);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        });



        switchApplied.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchApplied.isChecked()) {
                    applied = true;
                    setAppliedText(applied);
                } else {
                    applied = false;
                    setAppliedText(applied);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

}
