package com.mooc.p2_assignment3.services;


import android.net.Uri;
import android.util.Log;

import com.mooc.p2_assignment3.aidl.WeatherData;
import com.mooc.p2_assignment3.jsonweather.JsonWeather;
import com.mooc.p2_assignment3.jsonweather.WeatherJSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class WeatherUtil {

    private final String TAG =
            this.getClass().getCanonicalName();

    private final int CONN_TIMEOUT = 10 * 1000;
    private final int READ_TIMEOUT = 15 * 1000;




    boolean bTestLocation = false;

    private String weatherApiString = "http://api.openweathermap.org/data/2.5/weather?q=";
    private String testLocation = "Nashville,TN";
    private WeatherJSONParser parser = new WeatherJSONParser();

    public List<WeatherData> getWeather(String location) {
        Log.d(TAG, "getWeather() for location: " + location);

        InputStream is;
        try {
            is = openConnection(location);
        }
        catch (IOException ex) {
            Log.d(TAG, "getWeather() exception: Failed to open connection  " + ex.toString());
            WeatherData data = new WeatherData(null, 0, 0, 0, 0, 0, 0);
            data.mCod = 404;
            data.mMessage = new String("Failed to open connection");
            List<WeatherData> output = new ArrayList<>();
            output.add(data);
            return output;
        }
        if (is != null) {
            List<JsonWeather> list;
            try {
                list = parser.parseJsonStream(is);
            } catch (IOException e) {
                Log.d(TAG, "getWeather(): exception  " + e.toString());
                e.printStackTrace();
                WeatherData data = new WeatherData(null, 0, 0, 0, 0, 0, 0);
                data.mCod = 404;
                data.mMessage = new String("Error reading weather data");
                List<WeatherData> output = new ArrayList<>();
                output.add(data);
                return output;
            } catch (Exception ex) {
                Log.d(TAG, "getWeather(): exception  " + ex.toString());
                ex.printStackTrace();
                return null;
            }
            return getWeatherData(list);
        }
        else {
            return null;
        }

    }

    private List<WeatherData> getWeatherData(List<JsonWeather> list) {
        Log.d(TAG, "getWeatherData() in");

        List<WeatherData> out = new ArrayList<>();
        for (JsonWeather e : list) {
            if (e.getCod() == 200) {
                WeatherData data = new WeatherData(
                        e.getName(),
                        e.getWind().getSpeed(),
                        e.getWind().getDeg(),
                        e.getMain().getTemp(),
                        e.getMain().getHumidity(),
                        e.getSys().getSunrise(),
                        e.getSys().getSunset()
                );
                data.mCod = e.getCod();
                out.add(data);
            }
            else {
                WeatherData data = new WeatherData(null, 0, 0, 0, 0, 0, 0);
                data.mCod = e.getCod();
                if (e.mMessage != null) {
                    Log.d(TAG, "getWeatherData() " + e.mMessage);
                    data.mMessage = e.mMessage;
                }
                else {
                    Log.d(TAG, "getWeatherData() unknown error");
                }
                out.add(data);
            }
        }
        return out;
    }


    private InputStream openConnection(String location) throws IOException {
        Log.d(TAG, "openConnection() in");
        Uri api = Uri.parse(weatherApiString + location);
        InputStream is;
        /*try {
            is = (InputStream) new URL(api.toString()).getContent();
        } catch (IOException e) {
            Log.d(TAG, "openConnection() IOException: " + e.toString());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            Log.d(TAG, "openConnection(): " + e.toString());
            e.printStackTrace();
            return null;
        }*/
        //is = (InputStream) new URL(api.toString()).getContent();
        URL url = new URL(weatherApiString + location);
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(CONN_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        is = (InputStream) connection.getContent();
        return is;
    }
}
