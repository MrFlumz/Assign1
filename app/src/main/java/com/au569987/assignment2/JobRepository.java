package com.au569987.assignment2;

import android.app.Application;
import android.os.AsyncTask;

import com.au569987.assignment2.model.JobModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
/**
 Room implementering er lavet med inspiration fra https://codelabs.developers.google.com/codelabs/android-room-with-a-view/?fbclid=IwAR0Ralb-PwYVCpSXFN54-E8oXwPjMV95sTbynuQhwezPdTMgZLKZS5hCkic#0
 */
public class JobRepository {

    private JobDAO mJobDao;
    private List<JobModel> mAllJobs;
    private BackgroundService BoundBackgroundService;
    private boolean bound = false;

    JobRepository(Application application) throws ExecutionException, InterruptedException {
        JobDatabase db = JobDatabase.getDatabase(application);
        mJobDao = db.JobDao();
        mAllJobs = new GetAllJobsAsync(mJobDao).execute().get();
        //Intent intent = new Intent(application.getApplicationContext(), BackgroundService.class);
        //application.getApplicationContext().bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    List<JobModel> getAllJobs() throws ExecutionException, InterruptedException {
        return new GetAllJobsAsync(mJobDao).execute().get();
    }

    private static class GetAllJobsAsync extends AsyncTask<Void,Void,ArrayList<JobModel>>{
        private JobDAO jobItemDao;

        private GetAllJobsAsync(JobDAO jobItemDao) {this.jobItemDao = jobItemDao;}

        @Override
        protected ArrayList<JobModel> doInBackground(Void... voids){
            return (ArrayList<JobModel>) jobItemDao.getAll();
        }
    }

    public void insert (JobModel job) {
        new insertAsyncTask(mJobDao).execute(job);
    }

    private static class insertAsyncTask extends AsyncTask<JobModel, Void, Void> {
        private JobDAO mAsyncTaskDao;
        insertAsyncTask(JobDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final JobModel... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    public void remove (JobModel job) {
        new RemoveAsyncTask(mJobDao).execute(job);
    }

    private static class RemoveAsyncTask extends AsyncTask<JobModel, Void, Void> {
        private JobDAO mAsyncTaskDao;
        RemoveAsyncTask(JobDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final JobModel... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }
}