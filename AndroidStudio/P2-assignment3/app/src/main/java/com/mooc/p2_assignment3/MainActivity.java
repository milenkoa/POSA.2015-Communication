package com.mooc.p2_assignment3;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mooc.p2_assignment3.aidl.WeatherCall;
import com.mooc.p2_assignment3.aidl.WeatherData;
import com.mooc.p2_assignment3.services.RetainedFragmentManager;
import com.mooc.p2_assignment3.services.WeatherServiceSync;

import java.util.List;

/**
 * Main Activity
 *  - Displays very basic UI with with EditText and two RadioGroups (for selecting between
 *    synchronous and asynchronous service; between metric and imperial units)
 *  - All service connection is implemented in WeatherOps class (bridge pattern)
 *  - Implements retention fragment to preservice instance of WeatherOps across configuration
 *    changes (Memento pattern)
 */

public class MainActivity extends LifecycleLoggingActivity {

    protected final String TAG = getClass().getSimpleName();

    private final String WEATHER_OPS = "WEATHER_OPS";

    private WeatherCall weatherSync;
    private EditText inputText;
    public TextView weatherText;
    private RadioGroup unitsRadioGroup;
    private RadioGroup serviceTypeRadioGroup;

    private WeatherOps mWeatherOps;

    protected final RetainedFragmentManager mRetainedFragmentManager =
            new RetainedFragmentManager(this.getFragmentManager(), TAG);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = (TextView) findViewById(R.id.weather);
        inputText = (EditText) findViewById(R.id.place);
        unitsRadioGroup = (RadioGroup) findViewById(R.id.units);
        serviceTypeRadioGroup = (RadioGroup) findViewById(R.id.servicetype);

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,1);
    if (mRetainedFragmentManager.firstTimeIn()) {
            Log.d(TAG, "First time in MainActivity.onCreate()");
            mWeatherOps = new WeatherOps(this);
            mRetainedFragmentManager.put(WEATHER_OPS, mWeatherOps);
            mWeatherOps.bindService();

        }
        else {
            Log.d(TAG, "Subsequent MainActivity.onCreate() call");
            mWeatherOps = (WeatherOps) mRetainedFragmentManager.get(WEATHER_OPS);

            if (mWeatherOps == null) {
                mWeatherOps = new WeatherOps(this);
                mRetainedFragmentManager.put(WEATHER_OPS, mWeatherOps);
                mWeatherOps.bindService();
            }
            else {
                mWeatherOps.onConfigurationChange(this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mWeatherOps.unbindService();
    }


    public void getWeather(View v) {
        Log.d(TAG, "MainActivity.getWeather()");
        final String location = inputText.getText().toString();
        if (location.length() == 0) {
            Toast.makeText(this, "Provide location", Toast.LENGTH_LONG).show();
            return;
        }
        final int units = unitsRadioGroup.getCheckedRadioButtonId();
        boolean bImperial = false;
        switch (units) {
            case R.id.units_imperial:
                bImperial = true;
                break;

            case R.id.units_metric:
                bImperial = false;
                break;
        }

        final int serviceType = serviceTypeRadioGroup.getCheckedRadioButtonId();
        boolean bAsync = false;
        switch (serviceType) {
            case R.id.servicetype_async:
                bAsync = true;
                break;

            case R.id.servicetype_sync:
                bAsync = false;
                break;
        }

        Log.d(TAG,
                "getWeather(): Units are " + (bImperial?("Imperial"):("Metric")));
        Log.d(TAG,
                "getWeather(): Service is " + (bAsync?("Async"):("Sync")));

        // hide keyboard:
        InputMethodManager mgr =
                    (InputMethodManager) this.getSystemService
                            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(inputText.getWindowToken(),
                    0);

        mWeatherOps.getWeather(location, bImperial, bAsync);
    }
}
