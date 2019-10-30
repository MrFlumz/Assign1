
package com.example.assignment1.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class JobModel {

    /* AUTO GENERATED CODE */
    @PrimaryKey
    @SerializedName("id")
    @Expose
    @NonNull
    private String id;

    @ColumnInfo(name = "type")
    @SerializedName("type")
    @Expose
    private String type;

    @ColumnInfo(name = "url")
    @SerializedName("url")
    @Expose
    private String url;

    @ColumnInfo(name = "created_at")
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @ColumnInfo(name = "Company")
    @SerializedName("company")
    @Expose
    private String company;

    @ColumnInfo(name = "Company_url")
    @SerializedName("company_url")
    @Expose
    private String companyUrl;

    @ColumnInfo(name = "Location")
    @SerializedName("location")
    @Expose
    private String location;

    @ColumnInfo(name = "Title")
    @SerializedName("title")
    @Expose
    private String title;

    @ColumnInfo(name = "Discription")
    @SerializedName("description")
    @Expose
    private String description;

    @ColumnInfo(name = "howToApply")
    @SerializedName("how_to_apply")
    @Expose
    private String howToApply;

    @ColumnInfo(name = "Company_Logo_Url")
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
    @ColumnInfo(name = "Score")
    private double Score = 0;
    @ColumnInfo(name = "Applied")
    private Boolean Applied = false;
    @ColumnInfo(name = "Status_Color")
    private String StatusColor = "#bdbdbd";
    @ColumnInfo(name = "Note")
    private String Note = "Notes..";
    @ColumnInfo(name = "Favorited")
    private Boolean Favorited = false;


    public String getStatusColor() {
        return StatusColor;
    }

    public void setStatusColor(String statusColor) {
        StatusColor = statusColor;
    }

    public Boolean getFavorited() {
        return Favorited;
    }

    public void setFavorited(Boolean favorited) {
        Favorited = favorited;
    }
    public double getScore() {
        return Score;
    }

    public void setScore(double mScore) {
        this.Score = mScore;
        this.StatusColor = CalcStatusColor(mScore);
    }

    public Boolean getApplied() { return Applied; }

    public void setApplied(Boolean mApplied) { this.Applied = mApplied; }





    public String CalcStatusColor(double score) {



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
        StatusColor = "#" + String.format("%02X", r)+String.format("%02X", g)+String.format("%02X", b);

        return StatusColor;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String mNote) {
        this.Note = mNote;
    }

}