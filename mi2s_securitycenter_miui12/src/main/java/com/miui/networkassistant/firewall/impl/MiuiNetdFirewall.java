package com.miui.networkassistant.firewall.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.h.f;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.dual.DualSimInfoManager;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.firewall.IFirewall;
import com.miui.networkassistant.firewall.IFirewallListener;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.TempFirewallRule;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.support.provider.e;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import miui.security.SecurityManager;
import miui.util.ArrayMap;

public class MiuiNetdFirewall implements IFirewall {
    private static final String TAG = "NetdFirewall";
    private static final int TYPE_ALL = 3;
    private static final int TYPE_MOBILE = 0;
    private static final int TYPE_OTHER = 4;
    private static final int TYPE_ROAMING = 2;
    private static final int TYPE_WIFI = 1;
    private CommonConfig mCommonConfig;
    private Context mContext;
    /* access modifiers changed from: private */
    public f.a mCurrentNetworkState = f.a.Inited;
    private boolean mCurrentRoamingState = false;
    private BroadcastReceiver mFirewallReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(MiuiNetdFirewall.TAG, "mFirewallReceiver");
            MiuiNetdFirewall.this.mListener.onAppNetworkRestrict(intent.getStringExtra("pkg"), MiuiNetdFirewall.this.mCurrentNetworkState);
        }
    };
    private HandlerThread mHandlerThread;
    private boolean mIsDataRoamingWhiteListEnable;
    private volatile boolean mIsStarted;
    /* access modifiers changed from: private */
    public IFirewallListener mListener;
    private Object mLock = new Object();
    private SparseArray<ArrayMap<String, FirewallRule>> mMobileRuleArrMap = new SparseArray<>(2);
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            MiuiNetdFirewall.this.refreshNetworkType();
        }
    };
    private ArrayMap<String, FirewallRule> mRoamingRuleMap = new ArrayMap<>();
    private Object mRuleMapLock = new Object();
    /* access modifiers changed from: private */
    public SecurityManager mSecurityManager;
    private DualSimInfoManager.ISimInfoChangeListener mSimInfoChangeListener = new DualSimInfoManager.ISimInfoChangeListener() {
        public void onSubscriptionsChanged() {
            MiuiNetdFirewall.this.refreshNetworkType();
        }
    };
    private SparseArray<ArrayMap<String, TempFirewallRule>> mTempMobileRule = new SparseArray<>(2);
    private ArrayMap<String, TempFirewallRule> mTempWifiRule = new ArrayMap<>();
    private ArrayMap<String, FirewallRule> mWifiRuleMap = new ArrayMap<>();
    private WorkHandler mWorkHandler;

    /* renamed from: com.miui.networkassistant.firewall.impl.MiuiNetdFirewall$4  reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState = new int[f.a.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                b.b.c.h.f$a[] r0 = b.b.c.h.f.a.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState = r0
                int[] r0 = $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0014 }
                b.b.c.h.f$a r1 = b.b.c.h.f.a.MobileConnected     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x001f }
                b.b.c.h.f$a r1 = b.b.c.h.f.a.WifiConnected     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x002a }
                b.b.c.h.f$a r1 = b.b.c.h.f.a.EthernetConnected     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0035 }
                b.b.c.h.f$a r1 = b.b.c.h.f.a.BluetoothConnected     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0040 }
                b.b.c.h.f$a r1 = b.b.c.h.f.a.OtherConnected     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x004b }
                b.b.c.h.f$a r1 = b.b.c.h.f.a.WifiApConnected     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                int[] r0 = $SwitchMap$com$miui$common$network$NetworkUtils$NetworkState     // Catch:{ NoSuchFieldError -> 0x0056 }
                b.b.c.h.f$a r1 = b.b.c.h.f.a.Diconnected     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.firewall.impl.MiuiNetdFirewall.AnonymousClass4.<clinit>():void");
        }
    }

    public class WorkHandler extends Handler {
        private static final int DEFAULT_STATE = -10;
        static final String EXTRA_PACKAGE_NAME = "package_name";
        static final String EXTRA_RULE = "rule";
        static final String EXTRA_TYPE = "type";
        static final int WHAT_SET_CURRENT_NETWORK_STATE = 11;
        static final int WHAT_SET_MIUI_FIREWALL_RULE = 12;
        private boolean mIsOldDevice = false;
        private boolean mIsRecord = false;

        WorkHandler(Looper looper) {
            super(looper);
        }

        private void doSetFirewallRule(Bundle bundle) {
            if (bundle != null) {
                String string = bundle.getString("package_name");
                int i = bundle.getInt(EXTRA_RULE, -10);
                int i2 = bundle.getInt("type", -10);
                if (TextUtils.isEmpty(string) || i == -10 || i2 == -10) {
                    Log.e(MiuiNetdFirewall.TAG, String.format("setMiuiFirewallRule failed！pkgName: %s , rule: %s , type: %s", new Object[]{string, Integer.valueOf(i), Integer.valueOf(i2)}));
                    return;
                }
                trySetMiuiFirewallRule(MiuiNetdFirewall.this.mSecurityManager, string, MiuiNetdFirewall.this.getUid(string), i, i2);
            }
        }

        private void trySetMiuiFirewallRule(SecurityManager securityManager, String str, int i, int i2, int i3) {
            Class<?> cls = securityManager.getClass();
            if (!this.mIsOldDevice) {
                try {
                    cls.getMethod("setMiuiFirewallRule", new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE}).invoke(securityManager, new Object[]{str, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)});
                    return;
                } catch (Exception e) {
                    Log.w(MiuiNetdFirewall.TAG, "setMiuiFirewallRule Exception OldDevice:false");
                    AnalyticsHelper.recordThrowable(e, "new setMiuiFirewallRule");
                }
            }
            try {
                cls.getMethod("setMiuiFirewallRule", new Class[]{String.class, Integer.TYPE, Integer.TYPE}).invoke(securityManager, new Object[]{str, Integer.valueOf(i2), Integer.valueOf(i3)});
                this.mIsOldDevice = true;
            } catch (Exception e2) {
                Log.w(MiuiNetdFirewall.TAG, "setMiuiFirewallRule Exception OldDevice:true");
                if (!this.mIsRecord) {
                    AnalyticsHelper.recordThrowable(e2, "old setMiuiFirewallRule");
                    this.mIsRecord = true;
                }
            }
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == 11) {
                MiuiNetdFirewall.this.mSecurityManager.setCurrentNetworkState(((Integer) message.obj).intValue());
            } else if (i == 12) {
                doSetFirewallRule(message.getData());
            }
        }
    }

    public MiuiNetdFirewall(Context context) {
        this.mContext = context;
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        this.mSecurityManager = (SecurityManager) this.mContext.getSystemService("security");
        this.mMobileRuleArrMap.put(0, new ArrayMap());
        this.mTempMobileRule.put(0, new ArrayMap());
        if (DeviceUtil.IS_DUAL_CARD) {
            this.mMobileRuleArrMap.put(1, new ArrayMap());
            this.mTempMobileRule.put(1, new ArrayMap());
        }
    }

    private boolean getCurrentRoamingState() {
        int currentActiveSlotNum = Sim.getCurrentActiveSlotNum();
        boolean d2 = e.d(this.mContext);
        boolean z = false;
        boolean z2 = e.c(this.mContext) != 1;
        if (d2 && z2) {
            int b2 = e.b(this.mContext);
        } else if (TelephonyUtil.isNetworkRoaming(this.mContext, currentActiveSlotNum) && this.mIsDataRoamingWhiteListEnable) {
            z = true;
        }
        Log.i(TAG, "virtual sim disabled : " + z);
        return z;
    }

    private List<String> getPackagesByRules(ArrayMap<String, FirewallRule> arrayMap, FirewallRule firewallRule) {
        ArrayList arrayList = new ArrayList();
        for (String str : arrayMap.keySet()) {
            if (((FirewallRule) arrayMap.get(str)) == firewallRule) {
                arrayList.add(str);
            }
        }
        return arrayList;
    }

    private List<String> getPackagesByTempRules(ArrayMap<String, TempFirewallRule> arrayMap, FirewallRule firewallRule) {
        ArrayList arrayList = new ArrayList();
        for (String str : arrayMap.keySet()) {
            TempFirewallRule tempFirewallRule = (TempFirewallRule) arrayMap.get(str);
            if (tempFirewallRule != null && tempFirewallRule.rule == firewallRule) {
                arrayList.add(str);
            }
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public int getUid(String str) {
        int parseUidByPackageName = PackageUtil.parseUidByPackageName(str);
        return parseUidByPackageName == 0 ? PackageUtil.getUidByPackageName(this.mContext, str) : parseUidByPackageName;
    }

    /* access modifiers changed from: private */
    public void refreshNetworkType() {
        synchronized (this.mLock) {
            if (shouldUpdateNetworkType()) {
                int i = 0;
                switch (AnonymousClass4.$SwitchMap$com$miui$common$network$NetworkUtils$NetworkState[this.mCurrentNetworkState.ordinal()]) {
                    case 1:
                        showRoamingStateNotification();
                        if (this.mCurrentRoamingState) {
                            i = 2;
                            break;
                        }
                        break;
                    case 2:
                        setCurrentNetworkState(1);
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        setCurrentNetworkState(4);
                        break;
                    case 7:
                        break;
                }
                setCurrentNetworkState(i);
            }
        }
    }

    private void registerFirewallReceiver() {
        g.a(this.mContext, this.mFirewallReceiver, B.k(), new IntentFilter("miui.intent.action.FIREWALL"));
    }

    private void registerNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.CONNECTIVITY_ACTION_IMMEDIATE);
        intentFilter.addAction(Constants.System.ACTION_TETHER_STATE_CHANGED);
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    private void registerSimStateReceiver() {
        DualSimInfoManager.registerChangeListener(this.mContext, this.mSimInfoChangeListener);
    }

    private void setCurrentNetworkState(int i) {
        if (this.mWorkHandler != null) {
            Message obtain = Message.obtain();
            obtain.what = 11;
            obtain.obj = Integer.valueOf(i);
            this.mWorkHandler.sendMessage(obtain);
        }
    }

    private void setMiuiFirewallRule(String str, int i, int i2) {
        if (this.mWorkHandler != null) {
            Message obtain = Message.obtain();
            obtain.what = 12;
            Bundle bundle = new Bundle();
            bundle.putString("package_name", str);
            bundle.putInt("rule", i);
            bundle.putInt("type", i2);
            obtain.setData(bundle);
            this.mWorkHandler.sendMessage(obtain);
        }
    }

    private boolean shouldUpdateNetworkType() {
        f.a c2 = f.c(this.mContext);
        boolean currentRoamingState = getCurrentRoamingState();
        if (this.mCurrentNetworkState == c2 && currentRoamingState == this.mCurrentRoamingState) {
            Log.i(TAG, String.format("network: %s, roaming:%b", new Object[]{c2.toString(), Boolean.valueOf(currentRoamingState)}));
            return false;
        }
        this.mCurrentNetworkState = c2;
        this.mCurrentRoamingState = currentRoamingState;
        return true;
    }

    private void showRoamingStateNotification() {
        if (this.mCommonConfig.isRoamingWhiteListNotifyEnable() && this.mCurrentRoamingState) {
            NotificationUtil.sendOpenRoamingWhiteListNotify(this.mContext);
            this.mCommonConfig.setRoamingWhiteListNotifyEnable(false);
        }
    }

    private void startWorkHandler() {
        this.mHandlerThread = new HandlerThread(TAG);
        this.mHandlerThread.start();
        this.mWorkHandler = new WorkHandler(this.mHandlerThread.getLooper());
    }

    private void stopWorkHandler() {
        WorkHandler workHandler = this.mWorkHandler;
        if (workHandler != null && this.mHandlerThread != null) {
            workHandler.removeCallbacksAndMessages((Object) null);
            this.mHandlerThread.quit();
            this.mWorkHandler = null;
            this.mHandlerThread = null;
        }
    }

    private void unRegisterFirewallReceiver() {
        this.mContext.unregisterReceiver(this.mFirewallReceiver);
    }

    private void unRegisterNetworkReceiver() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }

    private void unRegisterSimStateReceiver() {
        DualSimInfoManager.unRegisterChangeListener(this.mContext, this.mSimInfoChangeListener);
    }

    public List<String> getMobileRestrictPackages(int i) {
        HashSet hashSet = new HashSet();
        hashSet.addAll(getPackagesByRules((ArrayMap) this.mMobileRuleArrMap.get(i), FirewallRule.Restrict));
        hashSet.addAll(getPackagesByTempRules((ArrayMap) this.mTempMobileRule.get(i), FirewallRule.Restrict));
        return new ArrayList(hashSet);
    }

    public FirewallRule getMobileRule(String str, int i) {
        synchronized (this.mRuleMapLock) {
            FirewallRule tempMobileRule = getTempMobileRule(str, i);
            if (tempMobileRule == FirewallRule.Restrict) {
                return tempMobileRule;
            }
            FirewallRule firewallRule = (FirewallRule) ((ArrayMap) this.mMobileRuleArrMap.get(i)).get(str);
            if (firewallRule != null) {
                return firewallRule;
            }
            FirewallRule firewallRule2 = FirewallRule.Allow;
            return firewallRule2;
        }
    }

    public FirewallRule getRoamingRule(String str) {
        synchronized (this.mRuleMapLock) {
            FirewallRule firewallRule = (FirewallRule) this.mRoamingRuleMap.get(str);
            if (firewallRule != null) {
                return firewallRule;
            }
            FirewallRule firewallRule2 = FirewallRule.Restrict;
            return firewallRule2;
        }
    }

    public boolean getRoamingWhiteListEnable() {
        return this.mIsDataRoamingWhiteListEnable;
    }

    public FirewallRule getTempMobileRule(String str, int i) {
        TempFirewallRule tempFirewallRule;
        synchronized (this.mRuleMapLock) {
            tempFirewallRule = (TempFirewallRule) ((ArrayMap) this.mTempMobileRule.get(i)).get(str);
        }
        return tempFirewallRule == null ? FirewallRule.Allow : tempFirewallRule.rule;
    }

    public String getTempMobileRuleSrcPkgName(String str, int i) {
        TempFirewallRule tempFirewallRule;
        synchronized (this.mRuleMapLock) {
            tempFirewallRule = (TempFirewallRule) ((ArrayMap) this.mTempMobileRule.get(i)).get(str);
        }
        if (tempFirewallRule == null) {
            return null;
        }
        return tempFirewallRule.srcPkgName;
    }

    public FirewallRule getTempWifiRule(String str) {
        TempFirewallRule tempFirewallRule;
        synchronized (this.mRuleMapLock) {
            tempFirewallRule = (TempFirewallRule) this.mTempWifiRule.get(str);
        }
        return tempFirewallRule == null ? FirewallRule.Allow : tempFirewallRule.rule;
    }

    public String getTempWifiRuleSrcPkgName(String str) {
        TempFirewallRule tempFirewallRule;
        synchronized (this.mRuleMapLock) {
            tempFirewallRule = (TempFirewallRule) this.mTempWifiRule.get(str);
        }
        if (tempFirewallRule == null) {
            return null;
        }
        return tempFirewallRule.srcPkgName;
    }

    public List<String> getWifiRestrictPackages() {
        HashSet hashSet = new HashSet();
        hashSet.addAll(getPackagesByRules(this.mWifiRuleMap, FirewallRule.Restrict));
        hashSet.addAll(getPackagesByTempRules(this.mTempWifiRule, FirewallRule.Restrict));
        return new ArrayList(hashSet);
    }

    public FirewallRule getWifiRule(String str) {
        synchronized (this.mRuleMapLock) {
            FirewallRule tempWifiRule = getTempWifiRule(str);
            if (tempWifiRule == FirewallRule.Restrict) {
                return tempWifiRule;
            }
            FirewallRule firewallRule = (FirewallRule) this.mWifiRuleMap.get(str);
            if (firewallRule != null) {
                return firewallRule;
            }
            FirewallRule firewallRule2 = FirewallRule.Allow;
            return firewallRule2;
        }
    }

    public boolean isStarted() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mIsStarted;
        }
        return z;
    }

    public void loadMobileRules(ArrayMap<String, FirewallRule> arrayMap, int i, boolean z, boolean z2) {
        int value;
        synchronized (this.mRuleMapLock) {
            Log.i(TAG, String.format("slotNum:%d, update: %b, revert:%b", new Object[]{Integer.valueOf(i), Boolean.valueOf(z), Boolean.valueOf(z2)}));
            ArrayMap arrayMap2 = (ArrayMap) this.mMobileRuleArrMap.get(i);
            for (int i2 = 0; i2 < arrayMap.size(); i2++) {
                String str = (String) arrayMap.keyAt(i2);
                FirewallRule firewallRule = (FirewallRule) arrayMap.valueAt(i2);
                arrayMap2.put(str, firewallRule);
                if (z) {
                    if (z2 && firewallRule == FirewallRule.Restrict) {
                        value = FirewallRule.Allow.value();
                    } else if (firewallRule == FirewallRule.Restrict) {
                        value = firewallRule.value();
                    }
                    setMiuiFirewallRule(str, value, 0);
                }
            }
            for (String str2 : ((ArrayMap) this.mTempMobileRule.get(i)).keySet()) {
                FirewallRule firewallRule2 = ((TempFirewallRule) ((ArrayMap) this.mTempMobileRule.get(i)).get(str2)).rule;
                if (firewallRule2 == FirewallRule.Restrict) {
                    setMiuiFirewallRule(str2, z2 ? FirewallRule.Allow.value() : firewallRule2.value(), 0);
                }
            }
            this.mMobileRuleArrMap.put(i, arrayMap2);
        }
    }

    public void loadRoamingRules(ArrayMap<String, FirewallRule> arrayMap) {
        synchronized (this.mRuleMapLock) {
            for (int i = 0; i < arrayMap.size(); i++) {
                String str = (String) arrayMap.keyAt(i);
                FirewallRule firewallRule = (FirewallRule) arrayMap.valueAt(i);
                this.mRoamingRuleMap.put(str, firewallRule);
                if (firewallRule == FirewallRule.Allow) {
                    setMiuiFirewallRule(str, firewallRule.value(), 2);
                }
            }
        }
    }

    public void loadWifiRules(ArrayMap<String, FirewallRule> arrayMap) {
        synchronized (this.mRuleMapLock) {
            for (int i = 0; i < arrayMap.size(); i++) {
                String str = (String) arrayMap.keyAt(i);
                FirewallRule firewallRule = (FirewallRule) arrayMap.valueAt(i);
                this.mWifiRuleMap.put(str, firewallRule);
                if (firewallRule == FirewallRule.Restrict) {
                    setMiuiFirewallRule(str, firewallRule.value(), 1);
                }
            }
        }
    }

    public void removePackage(String str, int i) {
        synchronized (this.mRuleMapLock) {
            ((ArrayMap) this.mMobileRuleArrMap.get(i)).remove(str);
            this.mWifiRuleMap.remove(str);
            this.mRoamingRuleMap.remove(str);
        }
    }

    public void setListener(IFirewallListener iFirewallListener) {
        this.mListener = iFirewallListener;
    }

    public void setMobileRule(String str, FirewallRule firewallRule, int i, boolean z) {
        synchronized (this.mRuleMapLock) {
            if (firewallRule == FirewallRule.Allow) {
                ((ArrayMap) this.mMobileRuleArrMap.get(i)).remove(str);
            } else {
                ((ArrayMap) this.mMobileRuleArrMap.get(i)).put(str, firewallRule);
            }
            if (z) {
                setMiuiFirewallRule(str, firewallRule.value(), 0);
            }
        }
    }

    public void setRoamingRule(String str, FirewallRule firewallRule) {
        synchronized (this.mRuleMapLock) {
            if (firewallRule == FirewallRule.Restrict) {
                this.mRoamingRuleMap.remove(str);
            } else {
                this.mRoamingRuleMap.put(str, firewallRule);
            }
            setMiuiFirewallRule(str, firewallRule.value(), 2);
        }
    }

    public void setRoamingWhiteListEnable(boolean z) {
        this.mIsDataRoamingWhiteListEnable = z;
        refreshNetworkType();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x005b, code lost:
        if (r5 != com.miui.networkassistant.model.FirewallRule.Restrict) goto L_0x0077;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x005d, code lost:
        r0 = r3.mRuleMapLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005f, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        ((miui.util.ArrayMap) r3.mTempMobileRule.get(r7)).put(r4, new com.miui.networkassistant.model.TempFirewallRule(r5, r6));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0070, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0071, code lost:
        if (r8 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0077, code lost:
        r5 = r3.mRuleMapLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0079, code lost:
        monitor-enter(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        ((miui.util.ArrayMap) r3.mTempMobileRule.get(r7)).remove(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0085, code lost:
        monitor-exit(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0086, code lost:
        if (r8 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0088, code lost:
        r5 = getMobileRule(r4, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x008c, code lost:
        setMiuiFirewallRule(r4, r5.value(), 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setTempMobileRule(java.lang.String r4, com.miui.networkassistant.model.FirewallRule r5, java.lang.String r6, int r7, boolean r8) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mRuleMapLock
            monitor-enter(r0)
            android.util.SparseArray<miui.util.ArrayMap<java.lang.String, com.miui.networkassistant.model.TempFirewallRule>> r1 = r3.mTempMobileRule     // Catch:{ all -> 0x0098 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ all -> 0x0098 }
            miui.util.ArrayMap r1 = (miui.util.ArrayMap) r1     // Catch:{ all -> 0x0098 }
            boolean r1 = r1.containsKey(r4)     // Catch:{ all -> 0x0098 }
            r2 = 0
            if (r1 == 0) goto L_0x0058
            android.util.SparseArray<miui.util.ArrayMap<java.lang.String, com.miui.networkassistant.model.TempFirewallRule>> r1 = r3.mTempMobileRule     // Catch:{ all -> 0x0098 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ all -> 0x0098 }
            miui.util.ArrayMap r1 = (miui.util.ArrayMap) r1     // Catch:{ all -> 0x0098 }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ all -> 0x0098 }
            com.miui.networkassistant.model.TempFirewallRule r1 = (com.miui.networkassistant.model.TempFirewallRule) r1     // Catch:{ all -> 0x0098 }
            java.lang.String r1 = r1.srcPkgName     // Catch:{ all -> 0x0098 }
            boolean r1 = r1.equals(r6)     // Catch:{ all -> 0x0098 }
            if (r1 != 0) goto L_0x0058
            java.lang.String r5 = "NetdFirewall"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0098 }
            r6.<init>()     // Catch:{ all -> 0x0098 }
            java.lang.String r8 = "Package "
            r6.append(r8)     // Catch:{ all -> 0x0098 }
            r6.append(r4)     // Catch:{ all -> 0x0098 }
            java.lang.String r8 = " cannot set temp mobile rule, because it exists another rule. Set by package:"
            r6.append(r8)     // Catch:{ all -> 0x0098 }
            android.util.SparseArray<miui.util.ArrayMap<java.lang.String, com.miui.networkassistant.model.TempFirewallRule>> r8 = r3.mTempMobileRule     // Catch:{ all -> 0x0098 }
            java.lang.Object r7 = r8.get(r7)     // Catch:{ all -> 0x0098 }
            miui.util.ArrayMap r7 = (miui.util.ArrayMap) r7     // Catch:{ all -> 0x0098 }
            java.lang.Object r4 = r7.get(r4)     // Catch:{ all -> 0x0098 }
            com.miui.networkassistant.model.TempFirewallRule r4 = (com.miui.networkassistant.model.TempFirewallRule) r4     // Catch:{ all -> 0x0098 }
            java.lang.String r4 = r4.srcPkgName     // Catch:{ all -> 0x0098 }
            r6.append(r4)     // Catch:{ all -> 0x0098 }
            java.lang.String r4 = r6.toString()     // Catch:{ all -> 0x0098 }
            android.util.Log.i(r5, r4)     // Catch:{ all -> 0x0098 }
            monitor-exit(r0)     // Catch:{ all -> 0x0098 }
            return r2
        L_0x0058:
            monitor-exit(r0)     // Catch:{ all -> 0x0098 }
            com.miui.networkassistant.model.FirewallRule r0 = com.miui.networkassistant.model.FirewallRule.Restrict
            if (r5 != r0) goto L_0x0077
            java.lang.Object r0 = r3.mRuleMapLock
            monitor-enter(r0)
            android.util.SparseArray<miui.util.ArrayMap<java.lang.String, com.miui.networkassistant.model.TempFirewallRule>> r1 = r3.mTempMobileRule     // Catch:{ all -> 0x0074 }
            java.lang.Object r7 = r1.get(r7)     // Catch:{ all -> 0x0074 }
            miui.util.ArrayMap r7 = (miui.util.ArrayMap) r7     // Catch:{ all -> 0x0074 }
            com.miui.networkassistant.model.TempFirewallRule r1 = new com.miui.networkassistant.model.TempFirewallRule     // Catch:{ all -> 0x0074 }
            r1.<init>(r5, r6)     // Catch:{ all -> 0x0074 }
            r7.put(r4, r1)     // Catch:{ all -> 0x0074 }
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            if (r8 == 0) goto L_0x0093
            goto L_0x008c
        L_0x0074:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0074 }
            throw r4
        L_0x0077:
            java.lang.Object r5 = r3.mRuleMapLock
            monitor-enter(r5)
            android.util.SparseArray<miui.util.ArrayMap<java.lang.String, com.miui.networkassistant.model.TempFirewallRule>> r6 = r3.mTempMobileRule     // Catch:{ all -> 0x0095 }
            java.lang.Object r6 = r6.get(r7)     // Catch:{ all -> 0x0095 }
            miui.util.ArrayMap r6 = (miui.util.ArrayMap) r6     // Catch:{ all -> 0x0095 }
            r6.remove(r4)     // Catch:{ all -> 0x0095 }
            monitor-exit(r5)     // Catch:{ all -> 0x0095 }
            if (r8 == 0) goto L_0x0093
            com.miui.networkassistant.model.FirewallRule r5 = r3.getMobileRule(r4, r7)
        L_0x008c:
            int r5 = r5.value()
            r3.setMiuiFirewallRule(r4, r5, r2)
        L_0x0093:
            r4 = 1
            return r4
        L_0x0095:
            r4 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0095 }
            throw r4
        L_0x0098:
            r4 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0098 }
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.firewall.impl.MiuiNetdFirewall.setTempMobileRule(java.lang.String, com.miui.networkassistant.model.FirewallRule, java.lang.String, int, boolean):boolean");
    }

    public boolean setTempWifiRule(String str, FirewallRule firewallRule, String str2) {
        int value;
        synchronized (this.mRuleMapLock) {
            if (!this.mTempWifiRule.containsKey(str) || ((TempFirewallRule) this.mTempWifiRule.get(str)).srcPkgName.equals(str2)) {
                if (firewallRule == FirewallRule.Allow) {
                    this.mTempWifiRule.remove(str);
                    value = getWifiRule(str).value();
                } else {
                    this.mTempWifiRule.put(str, new TempFirewallRule(firewallRule, str2));
                    value = firewallRule.value();
                }
                setMiuiFirewallRule(str, value, 1);
                return true;
            }
            Log.i(TAG, "Package " + str + " cannot set temp wifi rule, because it exists another rule. Set by package:" + ((TempFirewallRule) this.mTempWifiRule.get(str)).srcPkgName);
            return false;
        }
    }

    public void setWifiRule(String str, FirewallRule firewallRule) {
        synchronized (this.mRuleMapLock) {
            if (firewallRule == FirewallRule.Allow) {
                this.mWifiRuleMap.remove(str);
            } else {
                this.mWifiRuleMap.put(str, firewallRule);
            }
            setMiuiFirewallRule(str, firewallRule.value(), 1);
        }
    }

    public boolean start() {
        synchronized (this.mLock) {
            if (!this.mIsStarted) {
                this.mIsStarted = true;
                registerNetworkReceiver();
                registerSimStateReceiver();
                registerFirewallReceiver();
                startWorkHandler();
                Log.i(TAG, "netd firewall start");
            }
        }
        return true;
    }

    public boolean stop() {
        synchronized (this.mLock) {
            if (this.mIsStarted) {
                this.mIsStarted = false;
                unRegisterNetworkReceiver();
                unRegisterSimStateReceiver();
                unRegisterFirewallReceiver();
                stopWorkHandler();
                Log.i(TAG, "netd firewall stop");
            }
        }
        return true;
    }
}
