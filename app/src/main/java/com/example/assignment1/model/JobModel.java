
package com.example.assignment1.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobModel {

    /* AUTO GENERATED CODE */
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("company_url")
    @Expose
    private String companyUrl;
    @SerializedName("location")
    @Expose
    private String location;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("how_to_apply")
    @Expose
    private String howToApply;
    @SerializedName("company_logo")
    @Expose
    private String companyLogo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyUrl() {
        return companyUrl;
    }

    public void setCompanyUrl(String companyUrl) {
        this.companyUrl = companyUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHowToApply() {
        return howToApply;
    }

    public void setHowToApply(String howToApply) {
        this.howToApply = howToApply;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    /* CUSTOM CODE*/
    private double mScore = 0;
    private Boolean mApplied = false;
    private String mStatusColor = "#bdbdbd";
    private String mNote = "Notes..";
    private Boolean Favorited = false;

    public Boolean getFavorited() {
        return Favorited;
    }

    public void setFavorited(Boolean favorited) {
        Favorited = favorited;
    }
    public double getmScore() {
        return mScore;
    }

    public void setmScore(double mScore) {
        this.mScore = mScore;
        this.mStatusColor = setmStatusColor(mScore);
    }

    public Boolean getmApplied() {
        return mApplied;
    }

    public void setmApplied(Boolean mApplied) {
        this.mApplied = mApplied;
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
    public String getmNote() {
        return mNote;
    }

    public void setmNote(String mNote) {
        this.mNote = mNote;
    }

}