package com.miui.networkassistant.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import b.b.c.h.f;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.o.g.e;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.miui.appmanager.AppManageUtils;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.FireWallConfig;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.firewall.FirewallConstants;
import com.miui.networkassistant.firewall.IFirewall;
import com.miui.networkassistant.firewall.IFirewallListener;
import com.miui.networkassistant.firewall.impl.MiuiNetdFirewall;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.FirewallRuleSet;
import com.miui.networkassistant.provider.NetworkAssistantProvider;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.ui.activity.RoamingStateAlertActivity;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.NotificationUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.VirtualSimUtil;
import com.miui.securitycenter.R;
import java.util.List;
import java.util.Map;
import miui.util.ArrayMap;

public class FirewallService extends Service {
    private static final String MAP_KEY_TAG_MOBILE = "mobile";
    private static final String MAP_KEY_TAG_WIFI = "wifi";
    private static final int MSG_HANDLER_SEED_SIM = 64;
    private static final String MSG_KEY_SEND_NETWORK_BLOCKED_NETWORK_TYPE = "network_blocked_network_type";
    private static final String MSG_KEY_SEND_NETWORK_BLOCKED_PKGNAME = "network_blocked_pkgname";
    private static final String MSG_KEY_TEMP_RULE_PACKAGE = "temp_rule_package";
    private static final String MSG_KEY_TEMP_RULE_REASON = "temp_rule_reason";
    private static final int MSG_SAVE_RULE_DATA = 2;
    private static final int MSG_SAVE_RULE_DATA_SLOT_0 = 1;
    private static final int MSG_SAVE_RULE_DATA_SLOT_1 = 17;
    private static final int MSG_SEND_NETWORK_BLOCKED = 256;
    private static final int MSG_SHOWING_TEMP_RULE_REASON = 33;
    private static final int MSG_SHOW_TEMP_RULE_REASON = 32;
    private static final int MSG_UPDATE_FIREWALL_CHANGED = 48;
    private static final String TAG = "FireWallService";
    /* access modifiers changed from: private */
    public int mActiveSlotNum;
    private BroadcastReceiver mAllowFirewallReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String str;
            FirewallService firewallService;
            int i;
            Log.i(FirewallService.TAG, "mAllowFirewallReceiver : " + intent.toString());
            NotificationUtil.cancelFirewallRestrictionNotification(context);
            String stringExtra = intent.getStringExtra("packageName");
            int intExtra = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, -1);
            if (intExtra == f.a.MobileConnected.ordinal()) {
                FirewallService.this.mFirewallBinder.setMobileRule(stringExtra, FirewallRule.Allow, FirewallService.this.mActiveSlotNum);
                firewallService = FirewallService.this;
                i = R.string.firewall_network_restrict_notify_mobile;
            } else if (intExtra == f.a.WifiConnected.ordinal()) {
                FirewallService.this.mFirewallBinder.setWifiRule(stringExtra, FirewallRule.Allow);
                firewallService = FirewallService.this;
                i = R.string.firewall_wifi;
            } else {
                str = null;
                FirewallService.this.showAllowFirewallToast(stringExtra, str);
            }
            str = firewallService.getString(i);
            FirewallService.this.showAllowFirewallToast(stringExtra, str);
        }
    };
    private BroadcastReceiver mCancelFloatNotificationReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int intExtra = intent.getIntExtra(NotificationUtil.CANCEL_FLOAT_NOTIFICATION, 0);
            Log.i(FirewallService.TAG, "cancel float notification id : " + intExtra);
            NotificationUtil.cancelNotification(context, intExtra);
        }
    };
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public IFirewall mFirewall;
    /* access modifiers changed from: private */
    public FirewallBinder mFirewallBinder;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                FirewallService.this.saveRuleData(0);
            } else if (i == 2) {
                FirewallService.this.saveRuleData();
            } else if (i == 17) {
                FirewallService.this.saveRuleData(1);
            } else if (i != 32) {
                if (i == 48) {
                    FirewallService.this.getContentResolver().notifyChange(Uri.parse("content://" + String.format("%s/%s", new Object[]{ProviderConstant.AUTHORITY, NetworkAssistantProvider.FIREWALL_PACKAGENAME_STR})), (ContentObserver) null);
                } else if (i == 64) {
                    FirewallService.this.handleSeedSim();
                } else if (i == FirewallService.MSG_SEND_NETWORK_BLOCKED) {
                    FirewallService.this.resolveAppBlocked(message.getData().getString(FirewallService.MSG_KEY_SEND_NETWORK_BLOCKED_PKGNAME), message.getData().getInt(FirewallService.MSG_KEY_SEND_NETWORK_BLOCKED_NETWORK_TYPE));
                }
            } else if (!FirewallService.this.mHandler.hasMessages(33)) {
                String string = message.getData().getString(FirewallService.MSG_KEY_TEMP_RULE_PACKAGE);
                String string2 = message.getData().getString(FirewallService.MSG_KEY_TEMP_RULE_REASON);
                String charSequence = PackageUtil.getLableByPackageName(FirewallService.this.mContext, string).toString();
                String charSequence2 = PackageUtil.getLableByPackageName(FirewallService.this.mContext, string2).toString();
                Toast.makeText(FirewallService.this, FirewallService.this.mContext.getString(R.string.firewall_temp_rule_reason, new Object[]{charSequence, charSequence2}), 1).show();
                FirewallService.this.mHandler.sendMessageDelayed(FirewallService.this.mHandler.obtainMessage(33), DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            }
        }
    };
    private boolean mHaveBackupData = false;
    private IFirewallListener mIFirewallListener = new IFirewallListener() {
        public void onAppNetworkRestrict(String str, f.a aVar) {
            Message obtainMessage = FirewallService.this.mHandler.obtainMessage(FirewallService.MSG_SEND_NETWORK_BLOCKED);
            Bundle bundle = new Bundle();
            bundle.putString(FirewallService.MSG_KEY_SEND_NETWORK_BLOCKED_PKGNAME, str);
            bundle.putInt(FirewallService.MSG_KEY_SEND_NETWORK_BLOCKED_NETWORK_TYPE, aVar.ordinal());
            obtainMessage.setData(bundle);
            FirewallService.this.mHandler.sendMessage(obtainMessage);
        }
    };
    /* access modifiers changed from: private */
    public Object mIPm;
    /* access modifiers changed from: private */
    public FireWallConfig[] mMobileFireWallConfig;
    /* access modifiers changed from: private */
    public PackageManager mPackageManager;
    private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(FirewallService.TAG, "mina mPackageReceiver onReceive");
            Uri data = intent.getData();
            if (data != null) {
                String schemeSpecificPart = data.getSchemeSpecificPart();
                int intExtra = intent.getIntExtra("android.intent.extra.UID", 0);
                if (!TextUtils.isEmpty(schemeSpecificPart) && intExtra != 0) {
                    if (Build.VERSION.SDK_INT > 22 && Constants.System.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
                        String installer = PackageUtil.getInstaller(context, schemeSpecificPart);
                        if (TextUtils.isEmpty(installer) || TextUtils.equals(installer, "null")) {
                            Log.w(FirewallService.TAG, "is Pre-installed application");
                            return;
                        }
                    }
                    String packageNameFormat = PackageUtil.getPackageNameFormat(schemeSpecificPart, intExtra);
                    String action = intent.getAction();
                    boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
                    if (!booleanExtra && Constants.System.ACTION_PACKAGE_REMOVED.equals(action)) {
                        FirewallService.this.removePackage(packageNameFormat);
                    } else if (!booleanExtra && Constants.System.ACTION_PACKAGE_ADDED.equals(action) && PackageUtil.hasInternetPermission(FirewallService.this.mPackageManager, packageNameFormat)) {
                        FirewallService.this.setNewInstallAppMobileRule(schemeSpecificPart, 0);
                        if (DeviceUtil.IS_DUAL_CARD) {
                            FirewallService.this.setNewInstallAppMobileRule(schemeSpecificPart, 1);
                        }
                        FirewallService.this.setNewInstallAppWifiRule(schemeSpecificPart);
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public FireWallConfig mRoamingFirewallConfig;
    private boolean mRoamingWhiteListEnable = false;
    private BroadcastReceiver mSimCurrentMobileChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            FirewallService.this.mHandler.removeMessages(64);
            FirewallService.this.mHandler.sendEmptyMessageDelayed(64, 200);
        }
    };
    private BroadcastReceiver mSimStateDataSlotReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(FirewallService.TAG, "mSimStateDataSlotReceiver");
            FirewallService.this.loadActiveFirewallConfig();
        }
    };
    private BroadcastReceiver mSimStateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra("ss");
            Log.i(FirewallService.TAG, "mSimStateReceiver : " + stringExtra);
            if ("LOADED".equals(stringExtra) || "ABSENT".equals(stringExtra)) {
                FirewallService.this.loadMobileFirewall();
                FirewallService.this.setWhiteListAppMobileAllow();
                FirewallService.this.mHandler.removeMessages(64);
                FirewallService.this.mHandler.sendEmptyMessageDelayed(64, 200);
            }
        }
    };
    private SimUserInfo[] mSimUserInfos;
    private ArrayMap<String, Long> mToastedAppMap;
    /* access modifiers changed from: private */
    public FireWallConfig mWifiFirewallConfig;

    private class FirewallBinder extends IFirewallBinder.Stub {
        private FirewallBinder() {
        }

        private boolean checkMobileRuleIsTempRestricted(String str, int i) {
            if (FirewallService.this.mFirewall.getTempMobileRule(str, i) != FirewallRule.Restrict) {
                return false;
            }
            String tempMobileRuleSrcPkgName = FirewallService.this.mFirewall.getTempMobileRuleSrcPkgName(str, i);
            Message obtainMessage = FirewallService.this.mHandler.obtainMessage(32);
            Bundle bundle = new Bundle();
            bundle.putString(FirewallService.MSG_KEY_TEMP_RULE_PACKAGE, str);
            bundle.putString(FirewallService.MSG_KEY_TEMP_RULE_REASON, tempMobileRuleSrcPkgName);
            obtainMessage.setData(bundle);
            FirewallService.this.mHandler.sendMessage(obtainMessage);
            return true;
        }

        private boolean checkWifiRuleIsTempRestricted(String str) {
            if (FirewallService.this.mFirewall.getTempWifiRule(str) != FirewallRule.Restrict) {
                return false;
            }
            String tempWifiRuleSrcPkgName = FirewallService.this.mFirewall.getTempWifiRuleSrcPkgName(str);
            Message obtainMessage = FirewallService.this.mHandler.obtainMessage(32);
            Bundle bundle = new Bundle();
            bundle.putString(FirewallService.MSG_KEY_TEMP_RULE_PACKAGE, str);
            bundle.putString(FirewallService.MSG_KEY_TEMP_RULE_REASON, tempWifiRuleSrcPkgName);
            obtainMessage.setData(bundle);
            FirewallService.this.mHandler.sendMessage(obtainMessage);
            return true;
        }

        private ApplicationInfo getApplicationInfo(String str) {
            try {
                if (PackageUtil.isXSpaceApp(str)) {
                    return null;
                }
                if (FirewallService.this.mIPm == null) {
                    IBinder iBinder = (IBinder) e.a(Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "package");
                    FirewallService firewallService = FirewallService.this;
                    Object unused = firewallService.mIPm = e.a(Class.forName("android.content.pm.IPackageManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
                }
                return AppManageUtils.a(FirewallService.this.mIPm, str, 0, PackageUtil.getCurrentUserId());
            } catch (Exception e) {
                Log.e(FirewallService.TAG, "setRule : package not found", e);
                return null;
            }
        }

        private boolean setMobileRuleNoCheckSys(String str, FirewallRule firewallRule, int i) {
            if (PreSetGroup.isPreFirewallWhiteListPackage(str)) {
                return false;
            }
            if (checkMobileRuleIsTempRestricted(str, i)) {
                Log.i(FirewallService.TAG, "cannot set mobile rule for " + str + " because the package name exists in tempMobileRules.");
                return false;
            }
            boolean z = FirewallService.this.mActiveSlotNum == i;
            FirewallService.this.mMobileFireWallConfig[i].set(str, firewallRule);
            int i2 = 1 - i;
            FireWallConfig fireWallConfig = FirewallService.this.mMobileFireWallConfig[i2];
            if (fireWallConfig != null && fireWallConfig.get(str) == null) {
                fireWallConfig.set(str, FirewallRule.Allow);
                FirewallService.this.saveRuleDataDelay(i2);
            }
            FirewallService.this.mFirewall.setMobileRule(str, firewallRule, i, z);
            FirewallService.this.saveRuleDataDelay(i);
            FirewallService.this.removePkgFromAppBlockedMap(str, FirewallService.MAP_KEY_TAG_MOBILE);
            Log.i(FirewallService.TAG, String.format("set mobile rule for %s:%s:%b", new Object[]{str, firewallRule.toString(), Boolean.valueOf(z)}));
            return true;
        }

        private boolean setWifiRuleNoCheckSys(String str, FirewallRule firewallRule) {
            if (PreSetGroup.isPreFirewallWhiteListPackage(str)) {
                return false;
            }
            if (checkWifiRuleIsTempRestricted(str)) {
                Log.i(FirewallService.TAG, "Cannot set wifi rule for " + str + " because the package name exists in tempWifiRules.");
                return false;
            }
            FirewallService.this.mWifiFirewallConfig.set(str, firewallRule);
            FirewallService.this.mFirewall.setWifiRule(str, firewallRule);
            FirewallService.this.saveRuleDataDelay();
            FirewallService.this.removePkgFromAppBlockedMap(str, FirewallService.MAP_KEY_TAG_WIFI);
            Log.i(FirewallService.TAG, String.format("set wifi rule for %s:%s", new Object[]{str, firewallRule.toString()}));
            return true;
        }

        public List<String> getMobileRestrictPackages(int i) {
            return FirewallService.this.mFirewall.getMobileRestrictPackages(i);
        }

        public synchronized FirewallRule getMobileRule(String str, int i) {
            return FirewallService.this.mFirewall.getMobileRule(str, i);
        }

        public int getRoamingAppCountByRule(FirewallRule firewallRule) {
            ArrayMap<String, FirewallRule> pairMap = FirewallService.this.mRoamingFirewallConfig.getPairMap();
            int i = 0;
            for (int i2 = 0; i2 < pairMap.size(); i2++) {
                if (firewallRule != null && firewallRule == pairMap.valueAt(i2)) {
                    i++;
                }
            }
            return i;
        }

        public FirewallRule getRoamingRule(String str) {
            return FirewallService.this.mFirewall.getRoamingRule(str);
        }

        public boolean getRoamingWhiteListEnable() {
            return FirewallService.this.mFirewall.getRoamingWhiteListEnable();
        }

        public FirewallRuleSet getRule(String str) {
            FirewallRuleSet defaultValue = FirewallRuleSet.defaultValue();
            defaultValue.mobileRule = getMobileRule(str, 0);
            defaultValue.wifiRule = getWifiRule(str);
            if (DeviceUtil.IS_DUAL_CARD) {
                defaultValue.mobileRule2 = getMobileRule(str, 1);
            }
            return defaultValue;
        }

        public synchronized FirewallRule getTempMobileRule(String str, int i) {
            return getMobileRule(str, i);
        }

        public FirewallRule getTempWifiRule(String str) {
            return getWifiRule(str);
        }

        public synchronized List<String> getWifiRestrictPackages() {
            return FirewallService.this.mFirewall.getWifiRestrictPackages();
        }

        public synchronized FirewallRule getWifiRule(String str) {
            return FirewallService.this.mFirewall.getWifiRule(str);
        }

        public boolean isStarted() {
            return FirewallService.this.mFirewall.isStarted();
        }

        public synchronized boolean setMobileRule(String str, FirewallRule firewallRule, int i) {
            boolean z;
            ApplicationInfo applicationInfo = getApplicationInfo(str);
            if (applicationInfo == null || !PackageUtil.isSystemApp(applicationInfo)) {
                z = setMobileRuleNoCheckSys(str, firewallRule, i);
            } else {
                z = true;
                for (String mobileRuleNoCheckSys : FirewallService.this.mPackageManager.getPackagesForUid(applicationInfo.uid)) {
                    z &= setMobileRuleNoCheckSys(mobileRuleNoCheckSys, firewallRule, i);
                }
            }
            FirewallService.this.checkFirewallRuleChangedDelay(z);
            return z;
        }

        public void setMobileRuleForPackages(Map map, int i) {
            for (String str : map.keySet()) {
                setMobileRule(str, (FirewallRule) map.get(str), i);
            }
        }

        public void setRoamingRule(String str, FirewallRule firewallRule) {
            if (!PreSetGroup.isPreFirewallWhiteListPackage(str)) {
                FirewallService.this.mRoamingFirewallConfig.set(str, firewallRule);
                FirewallService.this.mFirewall.setRoamingRule(str, firewallRule);
                FirewallService.this.saveRuleDataDelay();
                FirewallService.this.removePkgFromAppBlockedMap(str, FirewallService.MAP_KEY_TAG_MOBILE);
                Log.i(FirewallService.TAG, String.format("set roaming rule for %s:%s", new Object[]{str, firewallRule.toString()}));
            }
        }

        public void setRoamingWhiteListEnable(boolean z) {
            FirewallService.this.mFirewall.setRoamingWhiteListEnable(z);
            FirewallService.this.mCommonConfig.setDataRoamingWhiteListEnable(z);
        }

        public synchronized boolean setTempMobileRule(String str, FirewallRule firewallRule, String str2, int i) {
            boolean tempMobileRule;
            synchronized (this) {
                if (str2 == null) {
                    return false;
                }
                boolean z = FirewallService.this.mActiveSlotNum == i;
                ApplicationInfo applicationInfo = getApplicationInfo(str);
                if (applicationInfo != null && PackageUtil.isSystemApp(applicationInfo)) {
                    tempMobileRule = true;
                    for (String str3 : FirewallService.this.mPackageManager.getPackagesForUid(applicationInfo.uid)) {
                        if (PreSetGroup.isPreFirewallWhiteListPackage(str3)) {
                            tempMobileRule = false;
                        } else {
                            boolean tempMobileRule2 = FirewallService.this.mFirewall.setTempMobileRule(str3, firewallRule, str2, i, z);
                            Log.i(FirewallService.TAG, String.format("set temp mobile rule for %s:%s:%b", new Object[]{str3, firewallRule.toString(), Boolean.valueOf(tempMobileRule2)}));
                            tempMobileRule &= tempMobileRule2;
                        }
                    }
                } else if (PreSetGroup.isPreFirewallWhiteListPackage(str)) {
                    return false;
                } else {
                    tempMobileRule = FirewallService.this.mFirewall.setTempMobileRule(str, firewallRule, str2, i, z);
                    Log.i(FirewallService.TAG, String.format("set temp mobile rule for %s:%s:%b", new Object[]{str, firewallRule.toString(), Boolean.valueOf(tempMobileRule)}));
                }
                boolean z2 = tempMobileRule;
                FirewallService.this.checkFirewallRuleChangedDelay(z2);
                return z2;
            }
        }

        public boolean setTempWifiRule(String str, FirewallRule firewallRule, String str2) {
            boolean z;
            if (str2 == null) {
                return false;
            }
            ApplicationInfo applicationInfo = getApplicationInfo(str);
            if (applicationInfo != null && PackageUtil.isSystemApp(applicationInfo)) {
                z = true;
                for (String str3 : FirewallService.this.mPackageManager.getPackagesForUid(applicationInfo.uid)) {
                    if (PreSetGroup.isPreFirewallWhiteListPackage(str3)) {
                        z = false;
                    } else {
                        boolean tempWifiRule = FirewallService.this.mFirewall.setTempWifiRule(str3, firewallRule, str2);
                        z &= tempWifiRule;
                        Log.i(FirewallService.TAG, String.format("Set temp wifi rule for %s:%s:%b", new Object[]{str3, firewallRule.toString(), Boolean.valueOf(tempWifiRule)}));
                    }
                }
            } else if (PreSetGroup.isPreFirewallWhiteListPackage(str)) {
                return false;
            } else {
                z = FirewallService.this.mFirewall.setTempWifiRule(str, firewallRule, str2);
                Log.i(FirewallService.TAG, String.format("Set temp wifi rule for %s:%s:%b", new Object[]{str, firewallRule.toString(), Boolean.valueOf(z)}));
            }
            FirewallService.this.checkFirewallRuleChangedDelay(z);
            return z;
        }

        public synchronized boolean setWifiRule(String str, FirewallRule firewallRule) {
            boolean z;
            ApplicationInfo applicationInfo = getApplicationInfo(str);
            if (applicationInfo == null || !PackageUtil.isSystemApp(applicationInfo)) {
                z = setWifiRuleNoCheckSys(str, firewallRule);
            } else {
                z = true;
                for (String wifiRuleNoCheckSys : FirewallService.this.mPackageManager.getPackagesForUid(applicationInfo.uid)) {
                    z &= setWifiRuleNoCheckSys(wifiRuleNoCheckSys, firewallRule);
                }
            }
            FirewallService.this.checkFirewallRuleChangedDelay(z);
            return z;
        }

        public void setWifiRuleForPackages(Map map) {
            for (String str : map.keySet()) {
                setWifiRule(str, (FirewallRule) map.get(str));
            }
        }
    }

    private void backupRoamingState(boolean z) {
        if (!this.mHaveBackupData) {
            this.mHaveBackupData = true;
            this.mRoamingWhiteListEnable = z;
        }
    }

    /* access modifiers changed from: private */
    public void checkFirewallRuleChangedDelay(boolean z) {
        if (z) {
            if (this.mHandler.hasMessages(48)) {
                this.mHandler.removeMessages(48);
            }
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(48), 100);
        }
    }

    private void createFireWall() {
        this.mMobileFireWallConfig = new FireWallConfig[2];
        this.mFirewall = new MiuiNetdFirewall(getApplicationContext());
        this.mFirewall.start();
        this.mFirewall.setListener(this.mIFirewallListener);
        loadWifiAndRoamingFirewall();
        loadMobileFirewall();
        setWhiteListAppMobileAllow();
        setWhiteListAppWifiAllow();
    }

    /* access modifiers changed from: private */
    public void handleSeedSim() {
        try {
            if (this.mFirewallBinder == null) {
                Log.e(TAG, "mFirewallBinder is null");
                return;
            }
            Log.i(TAG, "handleSeedSim");
            if (VirtualSimUtil.isSeedSimEnable(this.mContext, this.mSimUserInfos[TelephonyUtil.getCurrentMobileSlotNum()].getIccid())) {
                backupRoamingState(this.mFirewallBinder.getRoamingWhiteListEnable());
                this.mFirewallBinder.setRoamingWhiteListEnable(true);
                for (String roamingRule : VirtualSimUtil.getSeedSimList(this.mContext)) {
                    this.mFirewallBinder.setRoamingRule(roamingRule, FirewallRule.Allow);
                }
                return;
            }
            Log.i(TAG, "iccid don't found");
            recoveryRoamingState();
        } catch (Exception e) {
            Log.i(TAG, "handleSeedSim", e);
        }
    }

    /* access modifiers changed from: private */
    public void loadActiveFirewallConfig() {
        int currentMobileSlotNum = TelephonyUtil.getCurrentMobileSlotNum();
        if (currentMobileSlotNum != this.mActiveSlotNum) {
            this.mActiveSlotNum = currentMobileSlotNum;
            Log.i(TAG, String.format("ActiveSlot %d", new Object[]{Integer.valueOf(this.mActiveSlotNum)}));
            int i = this.mActiveSlotNum;
            if (i == 0) {
                makeActiveSlotFirewall(0, 1);
            } else if (i == 1) {
                makeActiveSlotFirewall(1, 0);
            }
            this.mToastedAppMap.clear();
        }
    }

    /* access modifiers changed from: private */
    public void loadMobileFirewall() {
        loadMobileFirewallBySlot(0);
        if (DeviceUtil.IS_DUAL_CARD) {
            loadMobileFirewallBySlot(1);
        }
    }

    private void loadMobileFirewallBySlot(int i) {
        this.mActiveSlotNum = TelephonyUtil.getCurrentMobileSlotNum();
        String subscriberId = TelephonyUtil.getSubscriberId(this.mContext, i, "default");
        boolean z = this.mActiveSlotNum == i;
        Log.i(TAG, String.format("firewall config %d, %b", new Object[]{Integer.valueOf(i), Boolean.valueOf(z)}));
        this.mSimUserInfos[i] = SimUserInfo.getInstance(this.mContext, i);
        this.mMobileFireWallConfig[i] = FireWallConfig.getInstance(this.mContext, subscriberId + FirewallConstants.FILE_MOBILE);
        updateMobileRuleMap(i, z, false);
    }

    private void loadWifiAndRoamingFirewall() {
        ArrayMap<String, FirewallRule> pairMap = this.mWifiFirewallConfig.getPairMap();
        this.mFirewall.loadWifiRules(pairMap);
        this.mFirewall.loadRoamingRules(this.mRoamingFirewallConfig.getPairMap());
        this.mFirewall.setRoamingWhiteListEnable(this.mCommonConfig.getDataRoamingWhiteListEnable());
        setSystemAppWifiRuleAllow(pairMap);
        Log.i(TAG, "loadWifiAndRoamingFirewall over");
    }

    private void makeActiveSlotFirewall(int i, int i2) {
        updateMobileRuleMap(i2, true, true);
        updateMobileRuleMap(i, true, false);
    }

    private void recoveryRoamingState() {
        FirewallBinder firewallBinder;
        if (this.mHaveBackupData && (firewallBinder = this.mFirewallBinder) != null) {
            firewallBinder.setRoamingWhiteListEnable(this.mRoamingWhiteListEnable);
            for (String roamingRule : VirtualSimUtil.getSeedSimList(getApplicationContext())) {
                this.mFirewallBinder.setRoamingRule(roamingRule, FirewallRule.Restrict);
            }
            this.mHaveBackupData = false;
        }
    }

    private void registerAllowFirewallReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.App.ACTION_BROADCAST_ALLOW_APP_FIREWALL);
        g.a((Context) this, this.mAllowFirewallReceiver, B.d(), intentFilter, Constants.App.PERMISSION_EXTRA_NETWORK, (Handler) null);
    }

    private void registerCancelFloatNotificationReceiver() {
        g.a((Context) this, this.mCancelFloatNotificationReceiver, B.d(), new IntentFilter(Constants.App.ACTION_BROADCAST_CANCEL_NOTIFICATION));
    }

    private void registerCurrentMobileChangeReceiver() {
        registerReceiver(this.mSimCurrentMobileChangeReceiver, new IntentFilter(Constants.System.ACTION_DEFAULT_DATA_SLOT_CHANGED));
    }

    private void registerPackageReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Constants.System.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        g.a((Context) this, this.mPackageReceiver, B.d(), intentFilter);
    }

    private void registerSimDataSlotStateReceiver() {
        registerReceiver(this.mSimStateDataSlotReceiver, new IntentFilter(Constants.System.ACTION_DEFAULT_DATA_SLOT_CHANGED));
    }

    private void registerSimStateReceiver() {
        registerReceiver(this.mSimStateReceiver, new IntentFilter(Constants.System.ACTION_SIM_STATE_CHANGED));
    }

    private void removeMobileData(String str, int i) {
        this.mFirewall.removePackage(str, i);
        FireWallConfig[] fireWallConfigArr = this.mMobileFireWallConfig;
        if (fireWallConfigArr[i] != null) {
            fireWallConfigArr[i].remove(str);
            saveRuleDataDelay(i);
        }
    }

    /* access modifiers changed from: private */
    public void removePackage(String str) {
        removeMobileData(str, 0);
        if (DeviceUtil.IS_DUAL_CARD) {
            removeMobileData(str, 1);
        }
        this.mRoamingFirewallConfig.remove(str);
        this.mWifiFirewallConfig.remove(str);
        saveRuleDataDelay();
    }

    /* access modifiers changed from: private */
    public void removePkgFromAppBlockedMap(String str, String str2) {
        this.mToastedAppMap.remove(String.format("%s_%s", new Object[]{str, str2}));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0084, code lost:
        if (r14 == b.b.c.h.f.a.WifiConnected.ordinal()) goto L_0x0086;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void resolveAppBlocked(java.lang.String r13, int r14) {
        /*
            r12 = this;
            if (r13 != 0) goto L_0x0003
            return
        L_0x0003:
            r0 = 2
            java.lang.Object[] r1 = new java.lang.Object[r0]
            r2 = 0
            r1[r2] = r13
            b.b.c.h.f$a r3 = b.b.c.h.f.a.MobileConnected
            int r3 = r3.ordinal()
            if (r14 != r3) goto L_0x0014
            java.lang.String r3 = MAP_KEY_TAG_MOBILE
            goto L_0x0021
        L_0x0014:
            b.b.c.h.f$a r3 = b.b.c.h.f.a.WifiConnected
            int r3 = r3.ordinal()
            if (r14 != r3) goto L_0x001f
            java.lang.String r3 = MAP_KEY_TAG_WIFI
            goto L_0x0021
        L_0x001f:
            java.lang.String r3 = ""
        L_0x0021:
            r4 = 1
            r1[r4] = r3
            java.lang.String r3 = "%s_%s"
            java.lang.String r1 = java.lang.String.format(r3, r1)
            long r5 = java.lang.System.currentTimeMillis()
            miui.util.ArrayMap<java.lang.String, java.lang.Long> r3 = r12.mToastedAppMap
            java.lang.Object r3 = r3.get(r1)
            java.lang.Long r3 = (java.lang.Long) r3
            java.lang.String r7 = com.miui.networkassistant.utils.PackageUtil.getRealPackageName(r13)
            if (r3 == 0) goto L_0x0049
            long r8 = r3.longValue()
            long r8 = r5 - r8
            r10 = 86400000(0x5265c00, double:4.2687272E-316)
            int r3 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x00a7
        L_0x0049:
            java.lang.String r3 = "android"
            boolean r3 = r3.equals(r7)
            if (r3 != 0) goto L_0x005a
            android.content.Context r3 = r12.mContext
            boolean r3 = com.miui.networkassistant.utils.PackageUtil.isRunningForeground(r3, r7)
            if (r3 != 0) goto L_0x005a
            return
        L_0x005a:
            java.lang.CharSequence r3 = com.miui.networkassistant.utils.PackageUtil.getLableByPackageName(r12, r7)
            b.b.c.h.f$a r7 = b.b.c.h.f.a.MobileConnected
            int r7 = r7.ordinal()
            if (r14 != r7) goto L_0x007e
            android.content.Context r7 = r12.getApplicationContext()
            int r8 = r12.mActiveSlotNum
            boolean r7 = com.miui.networkassistant.utils.TelephonyUtil.isNetworkRoaming(r7, r8)
            if (r7 == 0) goto L_0x0086
            com.miui.networkassistant.config.CommonConfig r7 = r12.mCommonConfig
            boolean r7 = r7.getDataRoamingWhiteListEnable()
            if (r7 == 0) goto L_0x0086
            r12.startRoamingStateAlertActivity(r3)
            goto L_0x0089
        L_0x007e:
            b.b.c.h.f$a r7 = b.b.c.h.f.a.WifiConnected
            int r7 = r7.ordinal()
            if (r14 != r7) goto L_0x0089
        L_0x0086:
            r12.showNetworkRestrictNotify(r3, r13, r14)
        L_0x0089:
            miui.util.ArrayMap<java.lang.String, java.lang.Long> r13 = r12.mToastedAppMap
            java.lang.Long r14 = java.lang.Long.valueOf(r5)
            r13.put(r1, r14)
            java.lang.Object[] r13 = new java.lang.Object[r0]
            r13[r2] = r1
            java.lang.Long r14 = java.lang.Long.valueOf(r5)
            r13[r4] = r14
            java.lang.String r14 = "ToastedAppMap put, key:%s, value:%d"
            java.lang.String r13 = java.lang.String.format(r14, r13)
            java.lang.String r14 = "FireWallService"
            android.util.Log.i(r14, r13)
        L_0x00a7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.FirewallService.resolveAppBlocked(java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    public void saveRuleData() {
        this.mWifiFirewallConfig.saveNow();
        this.mRoamingFirewallConfig.saveNow();
    }

    /* access modifiers changed from: private */
    public void saveRuleData(int i) {
        this.mMobileFireWallConfig[i].saveNow();
    }

    /* access modifiers changed from: private */
    public void saveRuleDataDelay() {
        if (this.mHandler.hasMessages(2)) {
            this.mHandler.removeMessages(2);
        }
        this.mHandler.sendEmptyMessageDelayed(2, 100);
    }

    /* access modifiers changed from: private */
    public void saveRuleDataDelay(int i) {
        int i2 = i == 0 ? 1 : 17;
        if (this.mHandler.hasMessages(i2)) {
            this.mHandler.removeMessages(i2);
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i2), 100);
    }

    /* access modifiers changed from: private */
    public void setNewInstallAppMobileRule(String str, int i) {
        SimUserInfo[] simUserInfoArr = this.mSimUserInfos;
        if (simUserInfoArr[i] != null && simUserInfoArr[i].isSimInserted()) {
            this.mFirewallBinder.setMobileRule(str, FirewallRule.parse(this.mSimUserInfos[i].getFirewallMobilePreConfig()), i);
        }
    }

    /* access modifiers changed from: private */
    public void setNewInstallAppWifiRule(String str) {
        this.mFirewallBinder.setWifiRule(str, FirewallRule.parse(this.mCommonConfig.getFirewallWifiPreConfig()));
    }

    private void setSystemAppWifiRuleAllow(ArrayMap<String, FirewallRule> arrayMap) {
        FirewallRule firewallRule;
        for (int i = 0; i < arrayMap.size(); i++) {
            String realPackageName = PackageUtil.getRealPackageName((String) arrayMap.keyAt(i));
            if (realPackageName != null && PackageUtil.isSystemApp(this.mContext, realPackageName) && (firewallRule = (FirewallRule) arrayMap.valueAt(i)) != null && firewallRule == FirewallRule.Restrict) {
                this.mFirewall.setWifiRule(realPackageName, FirewallRule.Allow);
            }
        }
        saveRuleDataDelay();
    }

    /* access modifiers changed from: private */
    public void setWhiteListAppMobileAllow() {
        for (String next : PreSetGroup.getPreFirewallWhiteList()) {
            boolean z = false;
            this.mFirewall.setMobileRule(next, FirewallRule.Allow, 0, this.mActiveSlotNum == 0);
            if (DeviceUtil.IS_DUAL_CARD) {
                IFirewall iFirewall = this.mFirewall;
                FirewallRule firewallRule = FirewallRule.Allow;
                if (this.mActiveSlotNum == 1) {
                    z = true;
                }
                iFirewall.setMobileRule(next, firewallRule, 1, z);
            }
        }
    }

    private void setWhiteListAppWifiAllow() {
        for (String next : PreSetGroup.getPreFirewallWhiteList()) {
            this.mFirewall.setWifiRule(next, FirewallRule.Allow);
            this.mFirewall.setRoamingRule(next, FirewallRule.Allow);
        }
    }

    /* access modifiers changed from: private */
    public void showAllowFirewallToast(String str, String str2) {
        if (str2 != null) {
            CharSequence lableByPackageName = PackageUtil.getLableByPackageName(getApplicationContext(), PackageUtil.getRealPackageName(str));
            Toast.makeText(getApplicationContext(), String.format(getString(R.string.firewall_network_allow), new Object[]{lableByPackageName, str2}), 0).show();
        }
    }

    private void showNetworkRestrictNotify(CharSequence charSequence, String str, int i) {
        NotificationUtil.sendNetworkRestrictNotify(this.mContext, String.format(getString(R.string.firewall_restrict_notify_title), new Object[]{charSequence}), getString(R.string.firewall_network_restrict_notify_message), str, i);
    }

    private void startRoamingStateAlertActivity(CharSequence charSequence) {
        String format = String.format(getResources().getString(R.string.dialog_app_roaming_title), new Object[]{charSequence});
        String format2 = String.format(getString(R.string.dialog_app_roaming_message), new Object[]{charSequence});
        Intent intent = new Intent(this, RoamingStateAlertActivity.class);
        intent.putExtra(RoamingStateAlertActivity.DIALOG_TITLE, format);
        intent.putExtra(RoamingStateAlertActivity.DIALOG_MESSAGE, format2);
        intent.addFlags(268435456);
        startActivity(intent);
    }

    private void unRegisterAllowFirewallReceiver() {
        unregisterReceiver(this.mAllowFirewallReceiver);
    }

    private void unRegisterCancelFloatNotificationReceiver() {
        unregisterReceiver(this.mCancelFloatNotificationReceiver);
    }

    private void unRegisterCurrentMobileChangeReceiver() {
        unregisterReceiver(this.mSimCurrentMobileChangeReceiver);
    }

    private void unRegisterPackageReceiver() {
        unregisterReceiver(this.mPackageReceiver);
    }

    private void unRegisterSimDataSlotStateReceiver() {
        unregisterReceiver(this.mSimStateDataSlotReceiver);
    }

    private void unRegisterSimStateReceiver() {
        unregisterReceiver(this.mSimStateReceiver);
    }

    private void updateMobileRuleMap(int i, boolean z, boolean z2) {
        FireWallConfig[] fireWallConfigArr = this.mMobileFireWallConfig;
        if (fireWallConfigArr[i] != null && this.mFirewall != null) {
            this.mFirewall.loadMobileRules(fireWallConfigArr[i].getPairMap(), i, z, z2);
        }
    }

    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return this.mFirewallBinder.asBinder();
    }

    public void onCreate() {
        this.mContext = getApplicationContext();
        this.mSimUserInfos = new SimUserInfo[2];
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        this.mWifiFirewallConfig = FireWallConfig.getInstance(this.mContext, FirewallConstants.FILE_WIFI);
        this.mRoamingFirewallConfig = FireWallConfig.getInstance(this.mContext, FirewallConstants.FILE_ROAMING);
        this.mToastedAppMap = new ArrayMap<>();
        this.mPackageManager = getPackageManager();
        this.mFirewallBinder = new FirewallBinder();
        createFireWall();
        registerPackageReceiver();
        registerAllowFirewallReceiver();
        registerSimDataSlotStateReceiver();
        registerSimStateReceiver();
        registerCurrentMobileChangeReceiver();
        registerCancelFloatNotificationReceiver();
        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
        unRegisterPackageReceiver();
        unRegisterAllowFirewallReceiver();
        unRegisterSimDataSlotStateReceiver();
        unRegisterSimStateReceiver();
        unRegisterCurrentMobileChangeReceiver();
        unRegisterCancelFloatNotificationReceiver();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return super.onStartCommand(intent, 1, i2);
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
