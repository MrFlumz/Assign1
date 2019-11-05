package com.au569987.assignment2;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.au569987.assignment2.model.JobModel;

import java.util.List;

/**
 Room implementering er lavet med inspiration fra https://codelabs.developers.google.com/codelabs/android-room-with-a-view/?fbclid=IwAR0Ralb-PwYVCpSXFN54-E8oXwPjMV95sTbynuQhwezPdTMgZLKZS5hCkic#0
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
