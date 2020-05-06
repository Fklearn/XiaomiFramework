package com.miui.earthquakewarning.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.miui.earthquakewarning.utils.NotificationUtil;
import com.miui.earthquakewarning.utils.Utils;
import com.miui.networkassistant.config.Constants;

public class EarthquakeWarningService extends Service {
    private static final String TAG = "EwService";
    /* access modifiers changed from: private */
    public int mCurrentCallState;
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            int callState = ((TelephonyManager) EarthquakeWarningService.this.getSystemService("phone")).getCallState();
            if (EarthquakeWarningService.this.mCurrentCallState != callState) {
                if (EarthquakeWarningService.this.mPlaying) {
                    if (callState != 0) {
                        NotificationUtil.muteVolume(EarthquakeWarningService.this);
                    } else {
                        NotificationUtil.remuteVolume(EarthquakeWarningService.this);
                    }
                }
                int unused = EarthquakeWarningService.this.mCurrentCallState = callState;
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mPlaying;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Constants.System.ACTION_SCREEN_ON.equals(intent.getAction())) {
                if (!Utils.isEarthquakeWarningOpen()) {
                    EarthquakeWarningService.this.stopSelf();
                }
                long uploadTopicTime = Utils.getUploadTopicTime();
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - uploadTopicTime > 21600000) {
                    UpdateAreaCodeManager.getInstance().uploadSettings(context);
                    Utils.setUploadTopicTime(currentTimeMillis);
                }
            }
        }
    };
    private TelephonyManager mTelephonyManager;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_ON);
        registerReceiver(this.mReceiver, intentFilter);
        this.mTelephonyManager = (TelephonyManager) getSystemService("phone");
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
        Log.i(TAG, "EarthquakeWarningService created");
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mReceiver);
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
        Log.w(TAG, "EarthquakeWarningService destroyed");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Log.i(TAG, "EarthquakeWarningService onStartCommand");
        if (intent == null) {
            return super.onStartCommand(intent, i, i2);
        }
        if ("updatePlayingStatus".equals(intent.getAction())) {
            setPlaying(intent.getBooleanExtra("playing", false));
        }
        this.mCurrentCallState = this.mTelephonyManager.getCallState();
        return super.onStartCommand(intent, i, i2);
    }

    public void setPlaying(boolean z) {
        this.mPlaying = z;
    }
}
