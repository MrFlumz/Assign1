package com.example.assignment1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.assignment1.model.JobModel;

@Database(entities = {JobModel.class}, version = 1)
public abstract class JobDatabase extends RoomDatabase {

    public abstract JobDAO JobDao();

    private static volatile JobDatabase INSTANCE;

    static JobDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (JobDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            JobDatabase.class, "job_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
