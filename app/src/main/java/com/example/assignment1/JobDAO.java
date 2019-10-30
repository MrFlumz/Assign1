package com.example.assignment1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.assignment1.model.JobModel;

import java.util.List;

/**
 * Created by kasper on 04/10/17.
 */

@Dao
public interface JobDAO {

    @Query("SELECT * FROM jobmodel")
    List<JobModel> getAll();

    @Query("SELECT * FROM jobmodel WHERE id IN (:ids)")
    List<JobModel> loadAllByIds(int[] ids);

    @Query("SELECT * FROM jobmodel WHERE Company LIKE :name LIMIT 1")
    JobModel findByCompany(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(JobModel... jobs);

    @Delete
    void delete(JobModel job);
}
