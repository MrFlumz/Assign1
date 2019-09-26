package com.example.assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class PickerActivity extends AppCompatActivity {

    SeekBar SeekbarPicker;
    TextView txtPicker;
    Button btnSave;
    Intent returnIntent;
    String returnstr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        this.setTitle("Picker");
        //get the current intent
        Intent intent = getIntent();
        ImageView imgLogo = findViewById(R.id.imgLogo);
        TextView txtCompany = findViewById(R.id.txtCompany);
        TextView txtLocation = findViewById(R.id.txtLocation);
        TextView txtTitle = findViewById(R.id.txtJobtitle);
        TextView txtDescription = findViewById(R.id.txtDescription);
        TextView txtScore = findViewById(R.id.txtScore);
        TextView txtStatus = findViewById(R.id.txtStatus);

        txtCompany.setText(intent.getStringExtra(MainActivity.JOB_COMPANY));
        txtLocation.setText(intent.getStringExtra(MainActivity.JOB_LOCATION));
        txtTitle.setText(intent.getStringExtra(MainActivity.JOB_TITLE));
        txtDescription.setText(intent.getStringExtra(MainActivity.JOB_DESCRIPTION));
        txtScore.setText(intent.getStringExtra(MainActivity.JOB_SCORE));
        txtStatus.setText(intent.getStringExtra(MainActivity.JOB_STATUS));
        String nameOfImage = intent.getStringExtra(MainActivity.JOB_IMAGE);
        int resId = this.getResources().getIdentifier(nameOfImage, "drawable", this.getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), resId);
        imgLogo.setImageBitmap(bitmap);
    }
}
