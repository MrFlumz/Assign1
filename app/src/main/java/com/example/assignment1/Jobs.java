package com.example.assignment1;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.assignment1.model.JobModel;

import java.util.ArrayList;
import java.util.Random;

public class Jobs implements Parcelable {
    private double mScore = 0;
    private Boolean mApplied = false;
    private String mStatusColor;
    private String mNote = "Notes..";
    private Boolean Favorited = false;

    public Boolean getFavorited() {
        return Favorited;
    }

    public void setFavorited(Boolean favorited) {
        Favorited = favorited;
    }




    Random rand = new Random();








    public Jobs(Context mthis) {
        mNote = mthis.getResources().getString(R.string.txtNotes);
        mScore = 0.0;
        mStatusColor = "#bdbdbd";
    }

    public String getmStatusColor() {
        return mStatusColor;
    }

    public String setmStatusColor(double score) {



        if(score<0 || score>10) {
            Log.d("colorwas", "heeee");
            return "N/A";   // outside of range
        }
        double lightness = 0.7;
        // transform score from red to yellow to green
        int r,g,b = 0;
        if (score < 5){
            r = 255;
            g = (int)((score/5)*255);}
        else{
            g = 255;
            r = (int)((10/score)*255)-255; }
        r = (int)((double)r*lightness);
        g = (int)((double)g*lightness);

        // turns down red from 255 to 0 as score goes from 5 to 10
        Log.d("heee", String.format("%f",score)+"   "+String.format("%02X", r)+"  "+String.format("%02X", g)+"  "+String.format("%02X", b));
        mStatusColor = "#" + String.format("%02X", r)+String.format("%02X", g)+String.format("%02X", b);

        return mStatusColor;
    }

    public Boolean getmApplied() {
        return mApplied;
    }

    public void setmApplied(Boolean mApplied) {
        this.mApplied = mApplied;
    }

    public double getmScore() {
        return mScore;
    }

    public void setmScore(float mScore) {
        this.mScore = mScore;
        setmStatusColor(mScore);
    }


    public static ArrayList<Jobs> parseJobList(ArrayList<JobModel> list, Context mthis) {
        ArrayList<Jobs> joblist = new ArrayList<Jobs>();
        int length = list.size(); // -1 because first is description

        for (int i = 0; i < length; i++) {
            // (String mCom, String mLoc, String mTit, String mDes,
            joblist.add(new Jobs(mthis));
        }
        return joblist;
    }

    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }


    protected Jobs(Parcel in) {

        mScore = in.readDouble();
        byte mAppliedVal = in.readByte();
        mApplied = mAppliedVal == 0x02 ? null : mAppliedVal != 0x00;
        mStatusColor = in.readString();
        mNote = in.readString();
        rand = (Random) in.readValue(Random.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mScore);
        if (mApplied == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (mApplied ? 0x01 : 0x00));
        }
        dest.writeString(mStatusColor);
        dest.writeString(mNote);
        dest.writeValue(rand);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Jobs> CREATOR = new Parcelable.Creator<Jobs>() {
        @Override
        public Jobs createFromParcel(Parcel in) {
            return new Jobs(in);
        }

        @Override
        public Jobs[] newArray(int size) {
            return new Jobs[size];
        }
    };
}