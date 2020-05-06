package com.miui.earthquakewarning.service;

import android.content.Context;
import android.location.Address;
import android.util.Log;
import com.miui.earthquakewarning.utils.LocationUtils;
import com.miui.earthquakewarning.utils.Utils;

public class UpdateAreaCodeManager {
    public static final String TAG = "UpdateAreaCodeManager";
    public static final long UPLOAD_TOPIC_INTERVAL = 21600000;
    private static volatile UpdateAreaCodeManager instance;

    private UpdateAreaCodeManager() {
    }

    public static UpdateAreaCodeManager getInstance() {
        if (instance == null) {
            synchronized (UpdateAreaCodeManager.class) {
                if (instance == null) {
                    instance = new UpdateAreaCodeManager();
                }
            }
        }
        return instance;
    }

    public void uploadSettings(Context context) {
        try {
            if (Utils.isEarthquakeWarningOpen()) {
                LocationUtils.getAdminAreaLocation3(context, new LocationUtils.AreaResultListener() {
                    public void areaFail() {
                    }

                    public void areaSuccess(Address address) {
                        address.getLocality();
                        Utils.setPreviousDistrict(address.getLocality());
                        new RequestAreaCodeTask().execute(new String[]{String.valueOf(address.getLongitude()), String.valueOf(address.getLatitude())});
                    }
                });
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}
