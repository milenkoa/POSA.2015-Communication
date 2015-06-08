// WeatherResult.aidl
package com.mooc.p2_assignment3.aidl;

import com.mooc.p2_assignment3.aidl.WeatherData;
import java.util.List;

/**
 * Interface defining the method that receives callbacks from the
 * WeatherServiceAsync.  This method should be implemented by the
 * WeatherActivity.
 */
interface WeatherResults {
    /**
     * This one-way (non-blocking) method allows WeatherServiceAsync
     * to return the List of WeatherData results associated with a
     * one-way WeatherRequest.getCurrentWeather() call.
     */
    oneway void sendResults(in List<WeatherData> results);
}
