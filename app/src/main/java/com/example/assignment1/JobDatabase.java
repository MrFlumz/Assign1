package com.example.assignment1;

        import androidx.room.Database;
        import androidx.room.RoomDatabase;
        import androidx.room.TypeConverters;

        import com.example.assignment1.model.JobModel;


@Database(entities = {JobModel.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class JobDatabase extends RoomDatabase {
    public abstract JobDAO JobDao();
}
