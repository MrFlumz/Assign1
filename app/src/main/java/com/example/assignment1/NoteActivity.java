package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;

public class NoteActivity extends AppCompatActivity {

    Jobs job;
    TextView txtScore;
    Intent intent;
    String newScore;
    boolean applied;
    TextView txtApplied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        this.setTitle("Slider");

        TextView txtCompany = findViewById(R.id.txtCompany);
        TextView txtLocation = findViewById(R.id.txtLocation);
        final Switch switchApplied = findViewById(R.id.SwitchApplied);
        TextView txtStatus = findViewById(R.id.txtStatus);
        SeekBar seekBar = findViewById(R.id.seekBar);
        txtApplied = findViewById(R.id.txtApplied);
        txtScore = findViewById(R.id.txtScore);
        intent = getIntent();
        applied = Boolean.parseBoolean(intent.getStringExtra(MainActivity.JOB_STATUS));
        setAppliedText(applied);
        txtCompany.setText(intent.getStringExtra(MainActivity.JOB_COMPANY));
        txtLocation.setText(intent.getStringExtra(MainActivity.JOB_LOCATION));
        String score = intent.getStringExtra(MainActivity.JOB_SCORE);
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
                newScore = Double.toString((double) i / 10);
                txtScore.setText(newScore);

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
