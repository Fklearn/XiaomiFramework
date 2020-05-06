package com.miui.server;

import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteFullException;
import android.icu.util.Calendar;
import android.miui.R;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.Log;

public class AudioQueryWeatherService {
    private static final String CITY_CONTENT_URI = "content://weather/selected_city";
    private static final String[] CITY_PROJECTION = {"posID", "position", "flag", "belongings"};
    private static final int CITY_QUERY_TOKEN = 1001;
    private static final boolean DEBUG = true;
    private static final int FLAG_LOCATION_TRUE = 1;
    private static final int MAX_SUNRISE_TIME = 10;
    private static final int MAX_SUNSET_TIME = 22;
    private static final int MIN_SUNRISE_TIME = 3;
    private static final int MIN_SUNSET_TIME = 15;
    private static final int NO_FIND_OUT_RESULT = -1;
    private static final String TAG = "AudioQueryWeatherService";
    private static final String WEATHER_CONTENT_URI = "content://weather/actualWeatherData";
    private static final String[] WEATHER_PROJECTION = {"city_id", "sunrise", "sunset"};
    private static final int WEATHER_QUERY_TOKEN = 1000;
    private BroadcastReceiver mBootCompleteReceiver;
    /* access modifiers changed from: private */
    public final ContentResolver mContentResolver;
    private Context mContext;
    private boolean mInternationalLocation;
    private LocationObserver mLocationObserver;
    /* access modifiers changed from: private */
    public boolean mNextSunriseSunsetTime = false;
    private QueryHandler mQueryHandler;
    private int mSunriseTimeHours;
    private int mSunriseTimeMins;
    private int mSunsetTimeHours;
    private int mSunsetTimeMins;
    private BroadcastReceiver mUpdateTimeReceiver;

    public AudioQueryWeatherService(Context context) {
        Log.d(TAG, "construct!!!");
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        if (this.mQueryHandler == null) {
            this.mQueryHandler = new QueryHandler(this.mContext);
        }
        this.mSunriseTimeHours = 0;
        this.mSunsetTimeHours = 0;
        this.mSunriseTimeMins = 0;
        this.mSunsetTimeMins = 0;
        this.mInternationalLocation = true;
    }

    public void onCreate() {
        Log.d(TAG, "onCreate!!!");
        if (this.mBootCompleteReceiver == null) {
            this.mBootCompleteReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.d(AudioQueryWeatherService.TAG, "receive the boot complete : " + intent);
                    boolean unused = AudioQueryWeatherService.this.mNextSunriseSunsetTime = false;
                    AudioQueryWeatherService.this.startCityQuery();
                }
            };
            IntentFilter intentFilterBootComplete = new IntentFilter("android.intent.action.BOOT_COMPLETED");
            intentFilterBootComplete.setPriority(1000);
            this.mContext.registerReceiver(this.mBootCompleteReceiver, intentFilterBootComplete);
        }
        if (this.mUpdateTimeReceiver == null) {
            this.mUpdateTimeReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    Log.d(AudioQueryWeatherService.TAG, "receive sunrise.sunset.time broadcast : " + intent);
                    boolean unused = AudioQueryWeatherService.this.mNextSunriseSunsetTime = true;
                    AudioQueryWeatherService.this.startCityQuery();
                }
            };
            IntentFilter intentFilterUpdateTime = new IntentFilter("com.android.media.update.sunrise.sunset.time");
            intentFilterUpdateTime.setPriority(1000);
            this.mContext.registerReceiver(this.mUpdateTimeReceiver, intentFilterUpdateTime);
        }
        this.mLocationObserver = new LocationObserver();
        this.mLocationObserver.onCreate();
    }

    private class LocationObserver extends ContentObserver {
        LocationObserver() {
            super(new Handler());
            AudioQueryWeatherService.this.mContentResolver.registerContentObserver(Uri.parse(AudioQueryWeatherService.CITY_CONTENT_URI), false, this);
        }

        public void onCreate() {
            Log.d(AudioQueryWeatherService.TAG, "LocationObserver:onCreate!");
        }

        public void onChange(boolean selfChange) {
            Log.d(AudioQueryWeatherService.TAG, "location change:" + selfChange);
            boolean unused = AudioQueryWeatherService.this.mNextSunriseSunsetTime = false;
            AudioQueryWeatherService.this.startCityQuery();
        }
    }

    /* access modifiers changed from: private */
    public void startCityQuery() {
        synchronized (AudioQueryWeatherService.class) {
            if (this.mQueryHandler != null) {
                this.mQueryHandler.cancelOperation(1001);
                this.mQueryHandler.cancelOperation(1000);
                this.mQueryHandler.startQuery(1001, (Object) null, Uri.parse(CITY_CONTENT_URI).buildUpon().build(), CITY_PROJECTION, (String) null, (String[]) null, (String) null);
            }
        }
    }

    private void CalculateLocationAndQuery(int flag, String cityId, String belongings) {
        String LocationJudgment = this.mContext.getResources().getString(R.string.customer_location_judgment);
        if (flag == 1) {
            if (belongings == null || belongings.indexOf(LocationJudgment) == -1) {
                this.mInternationalLocation = true;
            } else {
                this.mInternationalLocation = false;
            }
            if (!this.mInternationalLocation && !TextUtils.isEmpty(cityId)) {
                startWeatherQuery(cityId);
                return;
            }
            return;
        }
        this.mInternationalLocation = true;
    }

    /* access modifiers changed from: private */
    public void updateCityInfo(Cursor cursor) {
        String cityId = null;
        String belongings = null;
        int flag = -1;
        if (cursor != null) {
            try {
                if (cursor.getCount() != 0 && cursor.moveToFirst()) {
                    cityId = cursor.getString(cursor.getColumnIndex("posID"));
                    belongings = cursor.getString(cursor.getColumnIndex("belongings"));
                    flag = cursor.getInt(cursor.getColumnIndex("flag"));
                    Log.d(TAG, "updateCityInfo flag:" + flag);
                }
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                cursor.close();
            } catch (Throwable th) {
                try {
                    cursor.close();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                throw th;
            }
            CalculateLocationAndQuery(flag, cityId, belongings);
            return;
        }
        Log.d(TAG, "location: weather maybe uninstalled");
        this.mInternationalLocation = true;
    }

    private void startWeatherQuery(String cityId) {
        Log.d(TAG, "startWeatherQuery start!");
        synchronized (AudioQueryWeatherService.class) {
            if (this.mQueryHandler != null) {
                this.mQueryHandler.cancelOperation(1000);
                this.mQueryHandler.startQuery(1000, (Object) null, Uri.parse(WEATHER_CONTENT_URI).buildUpon().appendPath(SplitScreenReporter.ACTION_EXIT_SPLIT).appendPath("0").build(), WEATHER_PROJECTION, (String) null, new String[]{cityId}, (String) null);
            }
        }
    }

    private void CalculateSunriseAndSunsetTime(long sunriseTime, long sunsetTime) {
        Calendar SunCalendar = Calendar.getInstance();
        if (sunriseTime == 0 || sunsetTime == 0) {
            this.mInternationalLocation = true;
            return;
        }
        SunCalendar.setTimeInMillis(sunriseTime);
        this.mSunriseTimeHours = SunCalendar.get(11);
        this.mSunriseTimeMins = SunCalendar.get(12);
        if (this.mSunriseTimeHours < 3) {
            this.mSunriseTimeHours = 3;
            this.mSunriseTimeMins = 0;
        }
        if (this.mSunriseTimeHours > 10) {
            this.mSunriseTimeHours = 11;
            this.mSunriseTimeMins = 0;
        }
        SunCalendar.setTimeInMillis(sunsetTime);
        this.mSunsetTimeHours = SunCalendar.get(11);
        this.mSunsetTimeMins = SunCalendar.get(12);
        if (this.mSunsetTimeHours < 15) {
            this.mSunsetTimeHours = 15;
            this.mSunsetTimeMins = 0;
        }
        if (this.mSunsetTimeHours > 22) {
            this.mSunsetTimeHours = 23;
            this.mSunsetTimeMins = 0;
        }
        Log.d(TAG, "updateWeatherInfo sunriseHour=" + this.mSunriseTimeHours + " sunriseMin=" + this.mSunriseTimeMins + " mNextSunriseSunsetTime=" + this.mNextSunriseSunsetTime);
        StringBuilder sb = new StringBuilder();
        sb.append("updateWeatherInfo sunsetHour=");
        sb.append(this.mSunsetTimeHours);
        sb.append(" sunsetMin=");
        sb.append(this.mSunsetTimeMins);
        Log.d(TAG, sb.toString());
    }

    /* access modifiers changed from: private */
    public void updateWeatherInfo(Cursor cursor) {
        Log.d(TAG, "updateWeatherInfo start!");
        if (cursor != null) {
            long sunriseTime = 0;
            long sunsetTime = 0;
            try {
                if (cursor.getCount() == 0 || !cursor.moveToFirst()) {
                    this.mInternationalLocation = true;
                } else {
                    if (this.mNextSunriseSunsetTime) {
                        cursor.moveToNext();
                    }
                    sunriseTime = cursor.getLong(cursor.getColumnIndex("sunrise"));
                    sunsetTime = cursor.getLong(cursor.getColumnIndex("sunset"));
                }
                CalculateSunriseAndSunsetTime(sunriseTime, sunsetTime);
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                this.mInternationalLocation = true;
                e2.printStackTrace();
                cursor.close();
            } catch (Throwable th) {
                try {
                    cursor.close();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                throw th;
            }
        } else {
            Log.d(TAG, "weather maybe uninstalled");
            this.mInternationalLocation = true;
        }
    }

    public boolean getDefaultTimeZoneStatus() {
        return this.mInternationalLocation;
    }

    public int getSunriseTimeHours() {
        return this.mSunriseTimeHours;
    }

    public int getSunsetTimeHours() {
        return this.mSunsetTimeHours;
    }

    public int getSunriseTimeMins() {
        return this.mSunriseTimeMins;
    }

    public int getSunsetTimeMins() {
        return this.mSunsetTimeMins;
    }

    private final class QueryHandler extends AsyncQueryHandler {

        protected class CatchingWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public CatchingWorkerHandler(Looper looper) {
                super(QueryHandler.this, looper);
            }

            public void handleMessage(Message msg) {
                try {
                    QueryHandler.super.handleMessage(msg);
                } catch (SQLiteDiskIOException e) {
                    Log.d(AudioQueryWeatherService.TAG, "Exception background worker thread", e);
                } catch (SQLiteFullException e2) {
                    Log.d(AudioQueryWeatherService.TAG, "Exception worker thread", e2);
                } catch (SQLiteDatabaseCorruptException e3) {
                    Log.d(AudioQueryWeatherService.TAG, "Exception on background", e3);
                }
            }
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [android.os.Handler, com.miui.server.AudioQueryWeatherService$QueryHandler$CatchingWorkerHandler] */
        /* access modifiers changed from: protected */
        public Handler createHandler(Looper looper) {
            return new CatchingWorkerHandler(looper);
        }

        public QueryHandler(Context context) {
            super(context.getContentResolver());
        }

        /* access modifiers changed from: protected */
        public void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (token == 1001) {
                AudioQueryWeatherService.this.updateCityInfo(cursor);
            } else {
                AudioQueryWeatherService.this.updateWeatherInfo(cursor);
            }
        }
    }
}
