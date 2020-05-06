package miui.provider;

import android.net.Uri;

public class Weather {
    public static final String AQIAUTHROITY = "weatherinfo";
    public static final String AUTHORITY = "weather";
    public static final int BLIZZARD = 13;
    public static final int BLOWING_SAND = 21;
    public static final int CLOUDY = 1;
    public static final int DUST = 20;
    public static final int DUSTSTORM = 19;
    public static final int FLOATING_DUST = 23;
    public static final int FOG = 3;
    public static final int HAILSTONE = 22;
    public static final int HAZE = 24;
    public static final int HEAVY_RAIN = 9;
    public static final int HEAVY_RAINSTORM = 5;
    public static final int HEAVY_SNOW = 15;
    public static final int LIGHT_RAIN = 11;
    public static final int LIGHT_SNOW = 17;
    public static final int MODERATE_RAIN = 10;
    public static final int MODERATE_SNOW = 16;
    public static final int OVERCAST = 2;
    public static final int RAINSTORM = 6;
    public static final int RAIN_SHOWERS = 8;
    public static final int SLEET = 12;
    public static final int SNOW_SHOWERS = 14;
    public static final int STRONG_DUSTSTORM = 18;
    public static final int STRONG_RAINSTORM = 4;
    public static final int SUNNY = 0;
    public static final int T_STORMS = 7;

    public static class DLCityBase {
        public static final String PID = "pid";
    }

    public static final class DLCityMetaData extends DLCityBase {
        public static final String ALTITUDE = "altitude";
        public static final String AREACODE = "areaCode";
        public static final Uri CONTENT_URI = Uri.parse("content://weather/dlcitymetadata");
        public static final String LATITUDE = "latitude";
        public static final String LONGTITUDE = "longitude";
        public static final String PARAM = "dlcitymetadata";
        public static final String PHONECODE = "phoneCode";
    }

    public static final class DLCityName extends DLCityBase {
        public static final String BELONGINGS = "belongings";
        public static final String NAME = "name";
        public static final String PARAMPROFIX = "dlcityname_";
        public static final String SEARCHKEYS = "searchKeys";
    }

    public static final class DailyWeather extends WeatherBaseColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/weather_daily_weather";
        public static final Uri CONTENT_URI = Uri.parse("content://weather/daily_weather");
        public static final String PRARM = "daily_weather";
    }

    public static final class LocalWeather extends WeatherBaseColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/weather_weather";
        public static final Uri CONTENT_URI = Uri.parse("content://weather/weather");
        public static final String PRARM = "weather";
    }

    public static final class SelectedCity implements WeatherCityColumns {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/weather_selected_city";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/weather_selected_city";
        public static final Uri CONTENT_URI = Uri.parse("content://weather/selected_city");
        public static final String FLAG = "flag";
        public static final int FLAG_GPS = 1;
        public static final String POSITION = "position";
        public static final String PRARM = "selected_city";
    }

    interface WeatherCityColumns {
        public static final String ENNAME = "en_name";
        public static final String ID = "_id";
        public static final String NAME = "name";
        public static final String PID = "posID";
        public static final String TWNAME = "tw_name";
    }

    public static final class Widget {
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/weather_widget";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/weather_widget";
        public static final Uri CONTENT_URI = Uri.parse("content://weather/widget");
        public static final String ID = "_id";
        public static final String PID = "posID";
        public static final String PRARM = "widget";
        public static final String TYPE_ID = "type_id";
        public static final String WIDGET_ID = "widget_id";
    }

    private static class WeatherCommonColums {
        public static final String CITY_ID = "city_id";

        private WeatherCommonColums() {
        }
    }

    public static class WeatherBaseColumns extends WeatherCommonColums {
        public static final String AQILEVEL = "aqilevel";
        public static final String BEGINS = "begins";
        public static final String CITY_NAME = "city_name";
        public static final Uri CONTENT_URI = Uri.parse("content://weather/all_weather");
        public static final String DATA1 = "data1";
        public static final String DAY = "day";
        public static final String DESCRIPTION = "description";
        public static final String EBBTIDE = "ebbTide";
        public static final String ENDS = "ends";
        public static final String HUMIDITY = "humidity";
        public static final String ID = "_id";
        public static final String LOCALE = "locale";
        public static final String PRARM = "all_weather";
        public static final String PRESSURE = "pressure";
        public static final String PRESSURES = "pressures";
        public static final String PUBLISH_TIME = "publish_time";
        public static final String RISINGTIDE = "risingTide";
        public static final String SUNRISE = "sunrise";
        public static final String SUNSET = "sunset";
        public static final String TEMPERATURE = "temperature";
        public static final String TEMPERATURE_RANGE = "temperature_range";
        public static final String TIMESTAMP = "timestamp";
        public static final String TMPHIGHS = "tmphighs";
        public static final String TMPLOWS = "tmplows";
        public static final String WATER = "water";
        public static final String WEATHERNAMESFROM = "weathernamesfrom";
        public static final String WEATHERNAMESTO = "weathernamesto";
        public static final String WEATHER_TYPE = "weather_type";
        public static final String WIND = "wind";
        public static final String WINDS = "winds";

        public WeatherBaseColumns() {
            super();
        }
    }

    public static final class AQIInfo extends WeatherCommonColums {
        public static final String AQI = "aqi";
        public static final String CITY_NAME = "city";
        public static final String CO = "co";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/aqi_info";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/aqi_info";
        public static final Uri CONTENT_URI = Uri.parse("content://weatherinfo/aqi");
        public static final String DESC = "desc";
        public static final String ID = "_id";
        public static final String NO2 = "no2";
        public static final String O3 = "o3";
        public static final String PARAM = "aqi";
        public static final String PM10 = "pm10";
        public static final String PM25 = "pm25";
        public static final String PUBLISH_TIME = "pub_time";
        public static final String SO2 = "so2";
        public static final String SPOT = "spot";
        public static final String SRC = "src";
        public static final String TITLE = "title";

        public AQIInfo() {
            super();
        }
    }

    public static final class AlertInfo extends WeatherCommonColums {
        public static final String ABNORMAL = "abnormal";
        public static final String ALERT = "alert";
        public static final String CITY_NAME = "city";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/alert_info";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/alert_info";
        public static final Uri CONTENT_URI = Uri.parse("content://weatherinfo/alert");
        public static final String DETAIL = "detail";
        public static final String HOLIDAY = "holiday";
        public static final String ID = "_id";
        public static final String LEVEL = "level";
        public static final String PARAM = "alert";
        public static final String PUBLISH_TIME = "pub_time";
        public static final String TITLE = "title";

        public AlertInfo() {
            super();
        }
    }

    public static final class RawInfo extends WeatherCommonColums {
        public static final Uri CONTENT_URI = Uri.parse("content://weather/raw");
        public static final String DATA1 = "data1";
        public static final String INSERTTIME_TIME = "insert_time";
        public static final String LOCALE = "locale";
        public static final String PARAM = "raw";

        public RawInfo() {
            super();
        }
    }
}
