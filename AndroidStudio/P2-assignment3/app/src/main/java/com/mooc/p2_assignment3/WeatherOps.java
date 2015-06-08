package com.mooc.p2_assignment3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.mooc.p2_assignment3.aidl.WeatherCall;
import com.mooc.p2_assignment3.aidl.WeatherData;
import com.mooc.p2_assignment3.aidl.WeatherRequest;
import com.mooc.p2_assignment3.aidl.WeatherResults;
import com.mooc.p2_assignment3.services.WeatherServiceAsync;
import com.mooc.p2_assignment3.services.WeatherServiceSync;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.PriorityQueue;


public class WeatherOps {

    protected final String TAG = getClass().getSimpleName();

    protected WeakReference<MainActivity> mActivity;

    private WeatherCall mWeatherSync;
    private ServiceConnection svcnSync = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mWeatherSync = WeatherCall.Stub.asInterface(iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mWeatherSync = null;

        }
    };

    private WeatherRequest mWeatherRequest;
    private ServiceConnection svcnAsync = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mWeatherRequest = WeatherRequest.Stub.asInterface(iBinder);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mWeatherRequest = null;

        }
    };

    private WeatherResults.Stub mWeatherResults = new WeatherResults.Stub() {
        @Override
        public void sendResults(List<WeatherData> results) throws RemoteException {

            final MainActivity activity = mActivity.get();

            Runnable r = new DisplayRunnable(activity, results);
            activity.runOnUiThread(r);
        }
    };


    public WeatherOps(MainActivity activity) {
        Log.d(TAG,
                "WeatherOps() ctor");

        mActivity = new WeakReference<>(activity);
//        Intent iAsync = new Intent(activity, WeatherServiceAsync.class);
//        activity.bindService(iAsync, svcnAsync, Context.BIND_AUTO_CREATE);
//
//        Intent iSync = new Intent(activity, WeatherServiceSync.class);
//        activity.bindService(iSync, svcnSync, Context.BIND_AUTO_CREATE);

    }

    public void getWeather(String location, boolean bImperial, boolean bAsync) {

        String weatherFor;

        if (bImperial) {
            weatherFor = location.concat("&units=Imperial");
        }
        else {
            weatherFor = location.concat("&units=Metric");
        }

        if (bAsync) {
            try {
                mWeatherRequest.getCurrentWeather(weatherFor, mWeatherResults);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        else {
            AsyncTaskForSyncSvc asyncTaskforSyncSvc = new AsyncTaskForSyncSvc();
            asyncTaskforSyncSvc.execute(weatherFor);
        }
    }

    public void bindService() {
        Log.d(TAG,
                "WeatherOps.bindService");
        MainActivity activity = mActivity.get();
        if (activity != null) {
            Log.d(TAG,
                    "WeatherOps.bindService async");
            Intent iASync = new Intent(activity, WeatherServiceAsync.class);
            activity.getApplicationContext().bindService(iASync, svcnAsync, Context.BIND_AUTO_CREATE);

            Log.d(TAG,
                    "WeatherOps.bindService sync");
            Intent iSync = new Intent(activity, WeatherServiceSync.class);
            activity.getApplicationContext().bindService(iSync, svcnSync, Context.BIND_AUTO_CREATE);
        }
    }

    public void unbindService() {
        Log.d(TAG, "unbindService() in");
        MainActivity activity = mActivity.get();
        if (activity != null) {
            if (activity.isChangingConfigurations()) {
                Log.d(TAG, "unbindService(): configuration change, dont do anything");

            }
            else {
                Log.d(TAG, "unbindService(): unbind from sync and async service");
                activity.getApplicationContext().unbindService(svcnAsync);
                activity.getApplicationContext().unbindService(svcnSync);

            }
        }
    }

    public void onConfigurationChange(MainActivity activity) {
        mActivity = new WeakReference<>(activity);

    }

    private class DisplayRunnable implements Runnable {

        private List<WeatherData> mResults;
        private MainActivity mActivity;
        public DisplayRunnable(MainActivity activity, List<WeatherData> results) {
            mActivity = activity;
            mResults = results;
        }


        @Override
        public void run() {
            if (mResults.size() != 0) {

                WeatherData d = mResults.get(0);

                String temp = String.format("WeatherResult() Async sendResults(), mCod = %d", d.mCod);
                Log.d(TAG, temp);

                if (d.mCod != 200) {
                    if (d.mMessage != null) {
                        Toast.makeText(mActivity, d.mMessage, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(mActivity, "Error code: " + Integer.toString((int)d.mCod), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    //weatherText.setText(d.toString());
                    mActivity.weatherText.setText(d.toString());
                }

            } else {
                Toast.makeText(mActivity, "Unknown error", Toast.LENGTH_LONG).show();
            }
            //activity.weatherText.setText(d.toString());

        }
    }


    //private AsyncTask<String, Void, List<WeatherData>> asyncTaskforSyncSvc = new AsyncTask<String, Void, List<WeatherData>>() {
    private class AsyncTaskForSyncSvc extends AsyncTask<String, Void, List<WeatherData>> {
        /**
         * Runs in a background thread.
         */
        @Override
        protected List<WeatherData> doInBackground(String... params) {
            try {
                return mWeatherSync.getCurrentWeather(params[0]);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground() error" + e.toString());
            }
            return null;
        }

        /**
         * Runs in the UI Thread.
         */
        @Override
        protected void onPostExecute(List<WeatherData> result) {
            Log.d(TAG, "onPostExecute() in");
            MainActivity activity = mActivity.get();
            //if (result != null) {
                //MainActivity activity = mActivity.get();
                //if (activity != null) {
            if (activity != null) {
                if (result != null) {

                    if (result.size() != 0) {

                        WeatherData d = result.get(0);

                        String temp = String.format("onPostExecute(), mCod = %d", d.mCod);
                        Log.d(TAG, temp);

                        if (d.mCod != 200) {
                            if (d.mMessage != null) {
                                Toast.makeText(activity, d.mMessage, Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(activity, "Error code: " + Integer.toString((int)d.mCod), Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            //weatherText.setText(d.toString());
                            activity.weatherText.setText(d.toString());
                        }

                    } else {
                        Toast.makeText(activity, "Unknown error", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(activity, "Unknown error", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
