package com.example.lab4_1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.Random;

public class SliderActivity extends AppCompatActivity {

    Jobs job;
    TextView txtScore;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        this.setTitle("Slider");

        TextView txtCompany = findViewById(R.id.txtCompany);
        TextView txtLocation = findViewById(R.id.txtLocation);
        txtScore = findViewById(R.id.txtScore);
        TextView txtStatus = findViewById(R.id.txtStatus);
        intent = getIntent();
        txtCompany.setText(intent.getStringExtra(MainActivity.JOB_COMPANY));
        txtLocation.setText(intent.getStringExtra(MainActivity.JOB_LOCATION));
        String score = intent.getStringExtra(MainActivity.JOB_SCORE);
        txtScore.setText(score);
        job = new Jobs("","","",""); // is only used to get color conversion
        String color = job.setmStatusColor(Double.parseDouble(intent.getStringExtra(MainActivity.JOB_SCORE)));
        PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
        txtScore.getBackground().setColorFilter(colorfilter);

        txtStatus.setText(intent.getStringExtra(MainActivity.JOB_STATUS));

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setProgress((int)(Double.parseDouble(score)*10));
        seekBar.getThumb().setColorFilter(colorfilter);
        seekBar.getProgressDrawable().setColorFilter(colorfilter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                String color = job.setmStatusColor((double)i/10);
                PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
                txtScore.getBackground().setColorFilter(colorfilter);
                seekBar.getThumb().setColorFilter(colorfilter);
                seekBar.getProgressDrawable().setColorFilter(colorfilter);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



    }


}
