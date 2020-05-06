package com.miui.networkassistant.ui.base;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import b.b.c.c.b.f;
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

public abstract class TrafficRelatedPreFragment extends f {
    private ServiceConnection mConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            TrafficRelatedPreFragment.this.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
            try {
                TrafficRelatedPreFragment.this.mTrafficCornBinders[0] = TrafficRelatedPreFragment.this.mTrafficManageBinder.getTrafficCornBinder(0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            TrafficRelatedPreFragment trafficRelatedPreFragment = TrafficRelatedPreFragment.this;
            trafficRelatedPreFragment.mSimUserInfos[0] = SimUserInfo.getInstance(trafficRelatedPreFragment.mAppContext, 0);
            if (DeviceUtil.IS_DUAL_CARD) {
                try {
                    TrafficRelatedPreFragment.this.mTrafficCornBinders[1] = TrafficRelatedPreFragment.this.mTrafficManageBinder.getTrafficCornBinder(1);
                } catch (RemoteException e2) {
                    e2.printStackTrace();
                }
                TrafficRelatedPreFragment trafficRelatedPreFragment2 = TrafficRelatedPreFragment.this;
                trafficRelatedPreFragment2.mSimUserInfos[1] = SimUserInfo.getInstance(trafficRelatedPreFragment2.mAppContext, 1);
            }
            TrafficRelatedPreFragment trafficRelatedPreFragment3 = TrafficRelatedPreFragment.this;
            trafficRelatedPreFragment3.mServiceConnected = true;
            trafficRelatedPreFragment3.onTrafficManageServiceConnected();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            TrafficRelatedPreFragment trafficRelatedPreFragment = TrafficRelatedPreFragment.this;
            trafficRelatedPreFragment.mTrafficManageBinder = null;
            trafficRelatedPreFragment.mServiceConnected = false;
        }
    };
    private Handler mHandler = new Handler();
    protected boolean mServiceConnected;
    protected SimCardHelper mSimCardHelper;
    /* access modifiers changed from: protected */
    public SimUserInfo[] mSimUserInfos = new SimUserInfo[2];
    /* access modifiers changed from: protected */
    public int mSlotNum = 0;
    protected ITrafficCornBinder[] mTrafficCornBinders = new ITrafficCornBinder[2];
    /* access modifiers changed from: protected */
    public ITrafficManageBinder mTrafficManageBinder;

    private void bindTrafficManageService() {
        TmBinderCacher.getInstance().bindTmService(this.mConn);
    }

    private void setSlotNum() {
        this.mSimCardHelper = SimCardHelper.getInstance(this.mAppContext);
        if (DeviceUtil.IS_DUAL_CARD) {
            Bundle arguments = getArguments();
            this.mSlotNum = (arguments == null || !arguments.containsKey(Sim.SIM_SLOT_NUM_TAG)) ? this.mSimCardHelper.isDualSimInsertedOne() ? Sim.getCurrentActiveSlotNum() : Sim.getCurrentOptSlotNum() : arguments.getInt(Sim.SIM_SLOT_NUM_TAG, 0);
            if (this.mSimCardHelper.isDualSimInserted()) {
                resetTitle();
            }
        }
    }

    private void unbindTrafficManageService() {
        TmBinderCacher.getInstance().unbindTmService(this.mConn);
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        bindTrafficManageService();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setSlotNum();
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
        this.mHandler.post(new Runnable() {
            public void run() {
                if (TrafficRelatedPreFragment.this.isAttatched()) {
                    Object[] objArr = new Object[2];
                    objArr[0] = TrafficRelatedPreFragment.this.getTitle();
                    TrafficRelatedPreFragment trafficRelatedPreFragment = TrafficRelatedPreFragment.this;
                    objArr[1] = trafficRelatedPreFragment.getString(trafficRelatedPreFragment.mSlotNum == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2);
                    TrafficRelatedPreFragment.this.setTitle(String.format("%s-%s", objArr));
                }
            }
        });
    }
}
