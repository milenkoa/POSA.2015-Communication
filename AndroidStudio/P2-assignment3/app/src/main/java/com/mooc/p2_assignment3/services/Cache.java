package com.mooc.p2_assignment3.services;

import android.util.Log;

import com.mooc.p2_assignment3.aidl.WeatherData;

import java.util.List;

/**
 * This is rudimentary implementation of single key/value par used for cache
 * Reading is considered fresh if timestamp < 10minutes old
 */
public class Cache {
    protected final String TAG = getClass().getSimpleName();

    private final long TEN_SECONDS = 10 * 1000;
    private long timestamp;
    private String mLocation = null;
    private List<WeatherData> mWeather;

    public synchronized void setCache(String location, List<WeatherData> weather) {
        Log.d(TAG, "storing data into cache");
        timestamp = System.currentTimeMillis();
        mLocation = location;
        mWeather = weather;
    }

    public synchronized List<WeatherData> checkCache(String location) {
        Log.d(TAG, "checkCache() for " + location);
        if (mLocation != null) {
            if (location.equals(mLocation)) {
                Log.d(TAG, "checkCache(): " + location + " found in cache");
                if ((System.currentTimeMillis() - timestamp < TEN_SECONDS)) {
                    Log.d(TAG, "checkCache() fresh readings");
                    return mWeather;
                }
                else {
                    Log.d(TAG, "checkCheck() stale readings");
                    return null;
                }
            } else {
                Log.d(TAG, "checkCache(): " + location + " not found");
                return null;
            }
        }
        else {
            Log.d(TAG, "checkCache(): Cache is empty");
            return null;
        }
    }
}
