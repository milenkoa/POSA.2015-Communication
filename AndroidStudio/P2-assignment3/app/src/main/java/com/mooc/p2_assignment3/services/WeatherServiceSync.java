package com.mooc.p2_assignment3.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.mooc.p2_assignment3.aidl.WeatherCall;
import com.mooc.p2_assignment3.aidl.WeatherData;
import com.mooc.p2_assignment3.jsonweather.JsonWeather;
import com.mooc.p2_assignment3.jsonweather.WeatherJSONParser;

/**
 * Class that implements synchronous weather service
 */
public class WeatherServiceSync extends LifecycleLoggingService {
    protected final String TAG = getClass().getSimpleName();
    private Cache mCache = new Cache();

    public WeatherServiceSync() {
    }

    WeatherCall.Stub mWeatherCallImpl = new WeatherCall.Stub() {

        @Override
        public List<WeatherData> getCurrentWeather(String Weather) {

            Log.d(TAG, "Sync getCurrentWeather(): " + Weather);
            List<WeatherData> result = mCache.checkCache(Weather);
            if (result == null) {

                WeatherUtil util = new WeatherUtil();
                result = util.getWeather(Weather);
                if (result != null && result.get(0).mCod == 200) {
                    mCache.setCache(Weather, result);
                }

            }
            return result;
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mWeatherCallImpl;
    }
}
