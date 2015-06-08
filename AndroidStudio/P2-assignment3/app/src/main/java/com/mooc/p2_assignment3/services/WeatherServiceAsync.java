package com.mooc.p2_assignment3.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mooc.p2_assignment3.aidl.WeatherData;
import com.mooc.p2_assignment3.aidl.WeatherRequest;
import com.mooc.p2_assignment3.aidl.WeatherResults;
import com.mooc.p2_assignment3.jsonweather.JsonWeather;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Class that implements asynchronous weather service
 */
public class WeatherServiceAsync extends LifecycleLoggingService {

    protected final String TAG = getClass().getSimpleName();
    /* simple cache that keeps only last reading. Not shared with other entities */
    private Cache mCache = new Cache();

    private WeatherRequest.Stub mWeatherRequestImpl = new WeatherRequest.Stub() {
        @Override
        public void getCurrentWeather(String Weather, WeatherResults results) {
            Log.d(TAG,
                    "Async getCurrentWeather(): " + Weather);
            List<WeatherData> result = mCache.checkCache(Weather);
            if (result == null) {
                WeatherUtil util = new WeatherUtil();
                //List<WeatherData> result = util.getWeather(Weather);
                result = util.getWeather(Weather);
                if (result != null && result.get(0).mCod == 200) {
                    mCache.setCache(Weather, result);
                }
            }


            try {
                results.sendResults(result);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    };


    public WeatherServiceAsync() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mWeatherRequestImpl;
    }
}
