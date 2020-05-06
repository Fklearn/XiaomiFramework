package com.miui.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MiuiSettings;
import android.util.Slog;
import java.util.ArrayList;
import java.util.List;

public class WMServiceConnection implements ServiceConnection {
    private static final String ACTION = "com.miui.wmsvc.LINK";
    private static final int BIND_DELAY = 60000;
    private static final int MAX_DEATH_COUNT_IN_ONE_DAY = 3;
    private static final int MAX_DEATH_COUNT_IN_TOTAL = 10;
    private static final int ONE_DAY_IN_MILLISECONDS = 86400000;
    private static final String PACKAGE_NAME = "com.miui.wmsvc";
    private static final String TAG = "WMServiceConnection";
    private Runnable mBindRunnable = new Runnable() {
        public void run() {
            if (WMServiceConnection.this.shouldBind()) {
                WMServiceConnection.this.bind();
                WMServiceConnection.this.mDeathTimes.add(Long.valueOf(SystemClock.elapsedRealtime()));
                WMServiceConnection.this.mHandler.removeCallbacks(this);
                WMServiceConnection.this.mHandler.postDelayed(this, 60000);
            }
        }
    };
    private Context mContext;
    IBinder.DeathRecipient mDeathHandler = new IBinder.DeathRecipient() {
        public void binderDied() {
            Slog.v(WMServiceConnection.TAG, "Inspector service binderDied!");
            WMServiceConnection.this.bindDelay();
        }
    };
    /* access modifiers changed from: private */
    public List<Long> mDeathTimes;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private IBinder mRemote;

    public WMServiceConnection(Context context) {
        this.mContext = context;
        this.mHandler = new Handler();
        this.mDeathTimes = new ArrayList(3);
        bindDelay();
    }

    /* access modifiers changed from: private */
    public void bindDelay() {
        Slog.d(TAG, "schedule bind in 60000ms");
        this.mHandler.removeCallbacks(this.mBindRunnable);
        this.mHandler.postDelayed(this.mBindRunnable, 60000);
    }

    /* access modifiers changed from: private */
    public void bind() {
        try {
            Intent intent = new Intent(ACTION);
            intent.setPackage(PACKAGE_NAME);
            if (this.mContext.bindService(intent, this, 1)) {
                Slog.d(TAG, "Bind Inspector success!");
            } else {
                Slog.e(TAG, "Bind Inspector failed!");
            }
        } catch (Exception e) {
            Slog.e(TAG, "Bind Inspector failed");
        }
    }

    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mRemote = service;
        this.mHandler.removeCallbacks(this.mBindRunnable);
        try {
            this.mRemote.linkToDeath(this.mDeathHandler, 0);
        } catch (Exception e) {
            Slog.e(TAG, "linkToDeath failed");
        }
        Slog.d(TAG, "onServiceConnected");
    }

    public void onServiceDisconnected(ComponentName name) {
        this.mRemote = null;
        Slog.d(TAG, "onServiceDisconnected");
        Context context = this.mContext;
        if (context != null) {
            context.unbindService(this);
        }
    }

    /* access modifiers changed from: private */
    public boolean shouldBind() {
        if (!MiuiSettings.Secure.isHttpInvokeAppEnable(this.mContext.getContentResolver())) {
            Slog.d(TAG, "Cancel bind for http invoke disabled");
            return false;
        } else if (this.mRemote != null) {
            Slog.d(TAG, "Cancel bind for connected");
            return false;
        } else if (this.mDeathTimes.size() >= 10) {
            Slog.w(TAG, "Cancel bind for MAX_DEATH_COUNT_IN_TOTAL reached");
            return false;
        } else if (this.mDeathTimes.size() < 3) {
            return true;
        } else {
            List<Long> list = this.mDeathTimes;
            long delay = (86400000 + list.get(list.size() - 3).longValue()) - SystemClock.elapsedRealtime();
            if (delay <= 0) {
                return true;
            }
            Slog.w(TAG, "Cancel bind for MAX_DEATH_COUNT_IN_ONE_DAY reached");
            this.mHandler.removeCallbacks(this.mBindRunnable);
            this.mHandler.postDelayed(this.mBindRunnable, delay);
            return false;
        }
    }
}
