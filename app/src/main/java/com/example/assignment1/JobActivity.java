package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class JobActivity extends AppCompatActivity {

    SeekBar SeekbarPicker;
    TextView txtNote;
    Button btnSave;
    Intent returnIntent;
    String returnstr;
    Jobs job;
    TextView txtApplied;
    int position;
    boolean applied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        this.setTitle("");
        //get the current intent
        Intent intent = getIntent();
        ImageView imgLogo = findViewById(R.id.imgLogo);
        TextView txtCompany = findViewById(R.id.txtCompany);
        TextView txtLocation = findViewById(R.id.txtLocation);
        TextView txtTitle = findViewById(R.id.txtJobtitle);
        TextView txtDescription = findViewById(R.id.txtDescription);
        TextView txtScore = findViewById(R.id.txtScore);
        TextView txtStatus = findViewById(R.id.txtStatusStatic);
        btnSave = findViewById(R.id.btnSave);
        txtNote = findViewById(R.id.txtNote);
        txtApplied = findViewById(R.id.txtStatus);
        txtCompany.setText(intent.getStringExtra(MainActivity.JOB_COMPANY));
        txtLocation.setText(intent.getStringExtra(MainActivity.JOB_LOCATION));
        txtTitle.setText(intent.getStringExtra(MainActivity.JOB_TITLE));
        txtDescription.setText(intent.getStringExtra(MainActivity.JOB_DESCRIPTION));

        txtScore.setText(intent.getStringExtra(MainActivity.JOB_SCORE));
        applied = intent.getBooleanExtra(MainActivity.JOB_STATUS,false);
        setAppliedText(applied);
        txtNote.setText(intent.getStringExtra(MainActivity.JOB_NOTE));
        String nameOfImage = intent.getStringExtra(MainActivity.JOB_IMAGE);
        position = intent.getIntExtra(MainActivity.JOB_INDEX, -1);
        int resId = this.getResources().getIdentifier(nameOfImage, "drawable", this.getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), resId);
        imgLogo.setImageBitmap(bitmap);
        job = new Jobs("","","","",this); // is only used to get color conversion
        String color = job.setmStatusColor(Double.parseDouble(intent.getStringExtra(MainActivity.JOB_SCORE)));
        PorterDuffColorFilter colorfilter = new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
        txtScore.getBackground().setColorFilter(colorfilter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(MainActivity.JOB_NOTE,txtNote.getText().toString());
                Log.d("helolo", Integer.toString(position));
                returnIntent.putExtra(MainActivity.JOB_INDEX, position);
                setResult(RESULT_OK,returnIntent);
                finish();
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
