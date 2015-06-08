package  com.mooc.p2_assignment3.jsonweather;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
            this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonStream(InputStream inputStream)
            throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG,
                "parseJsonStream(InputStream inputStream) in");
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
        //return parseJsonWeatherArray(reader);
        List<JsonWeather> result = new ArrayList<>();
        result.add(parseJsonWeather(reader));
        return result;
        //return null;
    }

    /**
     * Parse a single Json stream and convert it into a JsonWeather
     * object.
     */
    public JsonWeather parseJsonStreamSingle(JsonReader reader)
            throws IOException {
        // TODO -- you fill in here.
        return null;
    }

    /**
     * Parse a Json stream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonWeatherArray(JsonReader reader)
            throws IOException {

        // TODO -- you fill in here.
        Log.d(TAG,
                "parseJsonWeatherArray(JsonReader reader) in");
        List<JsonWeather> w = new ArrayList<JsonWeather>();

        reader.beginArray();
        while(reader.hasNext()) {
            w.add(parseJsonWeather(reader));
        }
        reader.endArray();
        return w;


        //return null;
    }

    /**
     * Parse a Json stream and return a JsonWeather object.
     */
    public JsonWeather parseJsonWeather(JsonReader reader)
            throws IOException {

        // TODO -- you fill in here.
        Log.d(TAG,
                "parseJsonWeather(JsonReader reader) in");

        String mMessage = null;

        Sys mSys = null;
        String mBase = null;
        Main mMain = null;
        List<Weather> mWeather = new ArrayList<Weather>();
        Wind mWind = null;
        long mDt = 0;
        long mId = 0;
        String mName = null;
        long mCod = 0;

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("sys")) {
                mSys = parseSys(reader);
            }
            else if (name.equals("base")) {
                mBase = reader.nextString();
            }
            else if (name.equals("main")) {
                mMain = parseMain(reader);
            }
            else if (name.equals("weather")) {
                mWeather = parseWeathers(reader);
            }
            else if (name.equals("wind")) {
                mWind = parseWind(reader);
            }
            else if (name.equals("dt")) {
                mDt = reader.nextLong();
            }
            else if (name.equals("id")) {
                mId = reader.nextLong();
            }
            else if (name.equals("name")) {
                mName = reader.nextString();
            }
            else if (name.equals("cod")) {
                mCod = reader.nextLong();
            }
            else if (name.equals("message")) {
                mMessage = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }

        if (mCod != 200) {
            if (mMessage != null) {
                Log.d(TAG, "parseJsonWeather(JsonReader reader) " + mMessage);
            }
            else {
                Log.d(TAG, "parseJsonWeather(JsonReader reader) Unknown error");
            }
        }
        return new JsonWeather(mSys, mBase, mMain, mWeather, mWind, mDt, mId, mName, mCod, mMessage);
        //return null;
    }

    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG,
                "parseWeathers(JsonReader reader) in");

        List<Weather> weathers = new ArrayList<Weather>();

        reader.beginArray();
        while(reader.hasNext()) {
            weathers.add(parseWeather(reader));
        }
        reader.endArray();
        return weathers;

        //return null;
    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG,
                "parseWeather(JsonReader reader) in");

        long id = 0;
        String main = null;
        String description = null;
        String icon = null;

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextLong();
            }
            else if (name.equals("main")) {
                main = reader.nextString();
            }
            else if (name.equals("description")) {
                description = reader.nextString();
            }
            else if (name.equals("icon")) {
                icon = reader.nextString();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Weather(id, main, description, icon);
        //return null;
    }

    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader)
            throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG,
                "parseMain(JsonReader reader) in");

        double mTemp = 0;
        double mTempMin = 0;
        double mTempMax = 0;
        double mPressure = 0;
        double mSeaLevel = 0;
        double mGrndLevel = 0;
        long mHumidity = 0;

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("temp")) {
                mTemp = reader.nextDouble();
            }
            else if (name.equals("temp_min")) {
                mTempMin = reader.nextDouble();
            }
            else if (name.equals("temp_max")) {
                mTempMax = reader.nextDouble();
            }
            else if (name.equals("pressure")) {
                mPressure = reader.nextDouble();
            }
            else if (name.equals("sea_level")) {
                mSeaLevel = reader.nextDouble();
            }
            else if (name.equals("grnd_level")) {
                mGrndLevel = reader.nextDouble();
            }
            else if (name.equals("humidity")) {
                mHumidity = reader.nextLong();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Main(mTemp, mTempMin, mTempMax, mPressure, mSeaLevel, mGrndLevel, mHumidity);
        //return null;
    }

    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Log.d(TAG,
                "parseWind(JsonReader reader) in");

        double speed = 0;
        double deg = 0;

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("speed")) {
                speed = reader.nextDouble();
            }
            else if (name.equals("deg")) {
                deg = reader.nextDouble();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();

        return new Wind(speed, deg);
        //return null;
    }

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader) throws IOException {
    // TODO -- you fill in here.
        Log.d(TAG,
                "parseSys(JsonReader reader) in");

        double message = 0;
        String country = null;
        long sunrise = 0;
        long sunset = 0;

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("message")) {
                message = reader.nextDouble();

            }
            else if (name.equals("country")) {
                country = reader.nextString();
            }
            else if (name.equals("sunrise")) {
                sunrise = reader.nextLong();
            }
            else if (name.equals("sunset")) {
                sunset = reader.nextLong();
            }
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        Sys s = new Sys();
        s.setCountry(country);
        s.setMessage(message);
        s.setSunrise(sunrise);
        s.setSunset(sunset);
        return s;
    //return null;
    }
}
