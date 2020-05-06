package com.miui.earthquakewarning.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.earthquakewarning.model.UserQuakeItem;
import com.miui.earthquakewarning.model.WarningModel;

public class ManageDataTask extends AsyncTask<String, Void, Boolean> {
    private static final String AUTHORITY = "com.miui.earthquakewarning.EarthquakeContentProvider";
    private static final Uri EARTHQUAKE_URI = Uri.parse("content://com.miui.earthquakewarning.EarthquakeContentProvider/earthquake");
    private static final String TAG = "ManageDataTask";
    private Context context;
    private UserQuakeItem userQuakeItem;

    public ManageDataTask(Context context2, UserQuakeItem userQuakeItem2) {
        this.context = context2;
        this.userQuakeItem = userQuakeItem2;
    }

    private void insertEarthquake(Context context2, UserQuakeItem userQuakeItem2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WarningModel.Columns.EVENTID, Long.valueOf(userQuakeItem2.getEventID()));
        contentValues.put(WarningModel.Columns.INDEX_EW, Integer.valueOf(userQuakeItem2.getIndex()));
        contentValues.put(WarningModel.Columns.MAGNITUDE, Float.valueOf(userQuakeItem2.getMagnitude()));
        contentValues.put(WarningModel.Columns.LONGITUDE, Double.valueOf(userQuakeItem2.getEpiLocation().getLongitude()));
        contentValues.put(WarningModel.Columns.LATITUDE, Double.valueOf(userQuakeItem2.getEpiLocation().getLatitude()));
        contentValues.put(WarningModel.Columns.MYLONGITUDE, Double.valueOf(userQuakeItem2.getLocation().getLongitude()));
        contentValues.put(WarningModel.Columns.MYLATITUDE, Double.valueOf(userQuakeItem2.getLocation().getLatitude()));
        contentValues.put(WarningModel.Columns.DISTANCE, String.valueOf(userQuakeItem2.getDistance()));
        contentValues.put(WarningModel.Columns.INTENSITY, Float.valueOf(userQuakeItem2.getIntensity()));
        contentValues.put(WarningModel.Columns.EPICENTER, userQuakeItem2.getEpiLocation().getPlace());
        contentValues.put("startTime", Long.valueOf(userQuakeItem2.getStartTime()));
        contentValues.put(WarningModel.Columns.SIGNATURE, userQuakeItem2.getSignatureText());
        contentValues.put("updateTime", Long.valueOf(userQuakeItem2.getUpdateTime()));
        contentValues.put("type", Integer.valueOf(userQuakeItem2.getType()));
        contentValues.put(WarningModel.Columns.WARNTIME, Integer.valueOf(userQuakeItem2.getCountdown()));
        context2.getContentResolver().insert(EARTHQUAKE_URI, contentValues);
    }

    private long queryData(Context context2, long j) {
        Cursor query = context2.getContentResolver().query(EARTHQUAKE_URI, new String[]{"_id", WarningModel.Columns.EVENTID}, (String) null, (String[]) null, "startTime desc");
        long j2 = -1;
        while (query.moveToNext()) {
            j2 = query.getLong(query.getColumnIndex(WarningModel.Columns.EVENTID));
            if (j2 == j) {
                break;
            }
        }
        return j2;
    }

    private void updateEarthquake(Context context2, UserQuakeItem userQuakeItem2) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WarningModel.Columns.INDEX_EW, Integer.valueOf(userQuakeItem2.getIndex()));
        contentValues.put(WarningModel.Columns.MAGNITUDE, Float.valueOf(userQuakeItem2.getMagnitude()));
        contentValues.put(WarningModel.Columns.LONGITUDE, Double.valueOf(userQuakeItem2.getEpiLocation().getLongitude()));
        contentValues.put(WarningModel.Columns.LATITUDE, Double.valueOf(userQuakeItem2.getEpiLocation().getLatitude()));
        contentValues.put(WarningModel.Columns.MYLONGITUDE, Double.valueOf(userQuakeItem2.getLocation().getLongitude()));
        contentValues.put(WarningModel.Columns.MYLATITUDE, Double.valueOf(userQuakeItem2.getLocation().getLatitude()));
        contentValues.put(WarningModel.Columns.DISTANCE, String.valueOf(userQuakeItem2.getDistance()));
        contentValues.put(WarningModel.Columns.INTENSITY, Float.valueOf(userQuakeItem2.getIntensity()));
        contentValues.put(WarningModel.Columns.EPICENTER, userQuakeItem2.getEpiLocation().getPlace());
        contentValues.put("startTime", Long.valueOf(userQuakeItem2.getStartTime()));
        contentValues.put(WarningModel.Columns.SIGNATURE, userQuakeItem2.getSignatureText());
        contentValues.put("updateTime", Long.valueOf(userQuakeItem2.getUpdateTime()));
        contentValues.put("type", Integer.valueOf(userQuakeItem2.getType()));
        context2.getContentResolver().update(EARTHQUAKE_URI, contentValues, "eventID = ?", new String[]{String.valueOf(userQuakeItem2.getEventID())});
    }

    /* access modifiers changed from: protected */
    public Boolean doInBackground(String... strArr) {
        try {
            if (this.userQuakeItem.getEventID() == queryData(this.context, this.userQuakeItem.getEventID())) {
                updateEarthquake(this.context, this.userQuakeItem);
            } else {
                insertEarthquake(this.context, this.userQuakeItem);
            }
        } catch (Exception e) {
            Log.e(TAG, "insert data error", e);
        }
        return true;
    }
}
