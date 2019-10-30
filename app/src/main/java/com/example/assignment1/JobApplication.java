package com.example.assignment1;

import android.app.Application;

import androidx.room.Room;

/**
 * Created by kasper on 04/10/17.
 */

public class JobApplication extends Application {

    private JobDatabase db;

    //singleton pattern used, for lazy loading + single instance since db object is expensive
    public JobDatabase getJobDatabase(){
        if(db == null){
            //this builder is for simplicity of the example and not good practise
            //- dangerous to allow queries on the main thread as it could block
            //- destructive migrations is dangerous as you might loose data with change in schema
            db = Room.databaseBuilder(this, JobDatabase.class, "my_tasks").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return db;
    }
}
