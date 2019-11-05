package com.au569987.assignment2;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.au569987.assignment2.model.JobModel;
/**
 Room implementering er lavet med inspiration fra https://codelabs.developers.google.com/codelabs/android-room-with-a-view/?fbclid=IwAR0Ralb-PwYVCpSXFN54-E8oXwPjMV95sTbynuQhwezPdTMgZLKZS5hCkic#0
 */
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
