package com.miui.networkassistant.ui.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.c.a.b;
import b.b.c.c.b.h;
import com.miui.analytics.AnalyticsUtil;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.service.ITrafficCornBinder;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.wrapper.TmBinderCacher;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil;
import com.miui.securitycenter.R;

public abstract class TrafficRelatedFragment extends h {
    private static final String TAG = "TrafficRelatedFragment";
    private ServiceConnection mConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TrafficRelatedFragment.TAG, "onServiceConnected name=" + componentName);
            TrafficRelatedFragment.this.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            try {
                TrafficRelatedFragment.this.mTrafficCornBinders[0] = TrafficRelatedFragment.this.mTrafficManageBinder.getTrafficCornBinder(0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            TrafficRelatedFragment trafficRelatedFragment = TrafficRelatedFragment.this;
            trafficRelatedFragment.mSimUserInfos[0] = SimUserInfo.getInstance(trafficRelatedFragment.mAppContext, 0);
            if (DeviceUtil.IS_DUAL_CARD) {
                try {
                    TrafficRelatedFragment.this.mTrafficCornBinders[1] = TrafficRelatedFragment.this.mTrafficManageBinder.getTrafficCornBinder(1);
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                }
                TrafficRelatedFragment trafficRelatedFragment2 = TrafficRelatedFragment.this;
                trafficRelatedFragment2.mSimUserInfos[1] = SimUserInfo.getInstance(trafficRelatedFragment2.mAppContext, 1);
            }
            TrafficRelatedFragment trafficRelatedFragment3 = TrafficRelatedFragment.this;
            trafficRelatedFragment3.mServiceConnected = true;
            trafficRelatedFragment3.onTrafficManageServiceConnected();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TrafficRelatedFragment.TAG, "onServiceDisconnected name=" + componentName);
            TrafficRelatedFragment.this.mServiceConnected = false;
        }
    };
    protected boolean mServiceConnected;
    protected SimCardHelper mSimCardHelper;
    protected SimUserInfo[] mSimUserInfos = new SimUserInfo[2];
    protected int mSlotNum = 0;
    protected ITrafficCornBinder[] mTrafficCornBinders = new ITrafficCornBinder[2];
    protected ITrafficManageBinder mTrafficManageBinder;

    private void bindTrafficManageService() {
        TmBinderCacher.getInstance().bindTmService(this.mConn);
    }

    private void unbindTrafficManageService() {
        TmBinderCacher.getInstance().unbindTmService(this.mConn);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mSimCardHelper = SimCardHelper.getInstance(this.mAppContext);
        if (DeviceUtil.IS_DUAL_CARD) {
            Activity activity = this.mActivity;
            int i = -1;
            if (activity != null) {
                i = activity.getIntent().getIntExtra(Sim.SIM_SLOT_NUM_TAG, -1);
            }
            if (i >= 0) {
                this.mSlotNum = i;
            } else {
                this.mSlotNum = this.mSimCardHelper.isDualSimInsertedOne() ? Sim.getCurrentActiveSlotNum() : Sim.getCurrentOptSlotNum();
            }
            if (this.mSimCardHelper.isDualSimInserted()) {
                resetTitle();
            }
        }
        bindTrafficManageService();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        PrivacyDeclareAndAllowNetworkUtil.showSecurityCenterAllowNetwork(this.mActivity);
    }

    public void onDestroy() {
        super.onDestroy();
        unbindTrafficManageService();
    }

    public void onPause() {
        super.onPause();
        AnalyticsUtil.recordPageEnd(getClass().getName());
    }

    public void onResume() {
        super.onResume();
        AnalyticsUtil.recordPageStart(getClass().getName());
    }

    /* access modifiers changed from: protected */
    public abstract void onTrafficManageServiceConnected();

    /* access modifiers changed from: protected */
    public void resetTitle() {
        postOnUiThread(new b(this) {
            public void runOnUiThread() {
                if (TrafficRelatedFragment.this.isAttatched()) {
                    Object[] objArr = new Object[2];
                    objArr[0] = TrafficRelatedFragment.this.getTitle();
                    objArr[1] = getString(TrafficRelatedFragment.this.mSlotNum == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2);
                    TrafficRelatedFragment.this.setTitle(String.format("%s-%s", objArr));
                }
            }
        });
    }
}
