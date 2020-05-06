package com.android.server.wm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IMiuiActivityObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.content.MiSyncConstants;
import java.util.ArrayList;

public class AccountHelper {
    /* access modifiers changed from: private */
    public static boolean DEBUG = true;
    private static final int LISTEN_MODE_ACCOUNT = 1;
    private static final int LISTEN_MODE_NONE = 0;
    private static final int LISTEN_MODE_WIFI = 2;
    private static final String TAG = "MiuiPermision";
    private static final String mAccountLoginActivity = new String("com.xiaomi.account");
    /* access modifiers changed from: private */
    public static AccountCallback mCallBack;
    /* access modifiers changed from: private */
    public static Context mContext;
    private static boolean mInIMEIWhiteList = false;
    private static boolean mListeningActivity = false;
    private static final String mNotificationActivity = new String("com.xiaomi.passport");
    private static final String mWifiSettingActivity = new String("com.android.settings");
    /* access modifiers changed from: private */
    public static ArrayList<String> sAccessActiviteis = new ArrayList<>();
    private static volatile AccountHelper sAccountHelper = null;
    IMiuiActivityObserver mActivityStateObserver = new IMiuiActivityObserver.Stub() {
        public void activityIdle(Intent intent) throws RemoteException {
        }

        public void activityResumed(Intent intent) throws RemoteException {
            String className = intent.getComponent().getClassName();
            String packageName = intent.getComponent().getPackageName();
            if (AccountHelper.DEBUG) {
                Log.i(AccountHelper.TAG, "resume packageName:" + packageName + "mListenMode :" + AccountHelper.this.mListenMode);
            }
            Account[] accountsByType = AccountManager.get(AccountHelper.mContext).getAccountsByType(MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE);
            if (AccountHelper.sAccessActiviteis.contains(packageName)) {
                return;
            }
            if ((AccountHelper.this.mListenMode & 2) != 0) {
                AccountHelper.mCallBack.onWifiSettingFinish();
            } else if ((AccountHelper.this.mListenMode & 1) != 0) {
                AccountHelper.this.addAccount(AccountHelper.mContext);
            }
        }

        public void activityPaused(Intent intent) throws RemoteException {
        }

        public void activityStopped(Intent intent) throws RemoteException {
        }

        public void activityDestroyed(Intent intent) throws RemoteException {
        }

        /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.wm.AccountHelper$2, android.os.IBinder] */
        public IBinder asBinder() {
            return this;
        }
    };
    /* access modifiers changed from: private */
    public int mListenMode = 0;

    public interface AccountCallback {
        void onWifiSettingFinish();

        void onXiaomiAccountLogin();

        void onXiaomiAccountLogout();
    }

    static {
        sAccessActiviteis.add(mAccountLoginActivity);
        sAccessActiviteis.add(mNotificationActivity);
        sAccessActiviteis.add(mWifiSettingActivity);
    }

    private AccountHelper() {
    }

    public static AccountHelper getInstance() {
        if (sAccountHelper == null) {
            synchronized (AccountHelper.class) {
                if (sAccountHelper == null) {
                    sAccountHelper = new AccountHelper();
                }
            }
        }
        return sAccountHelper;
    }

    public void registerAccountListener(Context context, AccountCallback callBack) {
        mContext = context;
        mCallBack = callBack;
        context.registerReceiver(new AccountBroadcastReceiver(), new IntentFilter("android.accounts.LOGIN_ACCOUNTS_POST_CHANGED"));
    }

    public Account getXiaomiAccount(Context context) {
        Account account = null;
        Account[] accounts = AccountManager.get(context).getAccountsByType(MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE);
        if (accounts.length > 0) {
            account = accounts[0];
        }
        if (account == null) {
            Log.i(TAG, "xiaomi account is null");
        }
        return account;
    }

    public void onXiaomiAccountLogin(Context context, Account account) {
        mCallBack.onXiaomiAccountLogin();
    }

    public void onXiaomiAccountLogout(Context context, Account account) {
        mCallBack.onXiaomiAccountLogout();
    }

    private class AccountBroadcastReceiver extends BroadcastReceiver {
        private AccountBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (intent != null && TextUtils.equals(intent.getAction(), "android.accounts.LOGIN_ACCOUNTS_POST_CHANGED")) {
                int type = intent.getIntExtra("extra_update_type", -1);
                Account account = (Account) intent.getParcelableExtra("extra_account");
                if (account == null || !TextUtils.equals(account.type, MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE)) {
                    Log.i(AccountHelper.TAG, "It isn't a xiaomi account changed.");
                    return;
                }
                Context appContext = context.getApplicationContext();
                if (type == 1) {
                    AccountHelper.this.onXiaomiAccountLogout(appContext, account);
                } else if (type == 2) {
                    AccountHelper.this.onXiaomiAccountLogin(appContext, account);
                } else {
                    Log.w(AccountHelper.TAG, String.format("Xiaomi account changed, but unknown type: %s.", new Object[]{Integer.valueOf(type)}));
                }
            }
        }
    }

    public void ListenAccount(int mode) {
        registerAccountActivityObserver();
        this.mListenMode |= mode;
        if (DEBUG) {
            Log.i(TAG, "ListenAccount mode: " + mode + " mListenMode: " + this.mListenMode);
        }
    }

    public void UnListenAccount(int mode) {
        this.mListenMode ^= mode;
        if (this.mListenMode == 0) {
            unRegisterAccountActivityObserver();
        }
        if (DEBUG) {
            Log.i(TAG, "UnListenAccount mode: " + mode + " mListenMode: " + this.mListenMode);
        }
    }

    public void registerAccountActivityObserver() {
        if (!mListeningActivity) {
            mListeningActivity = true;
            Intent intent = new Intent();
            IActivityManager activityManager = ActivityManager.getService();
            if (activityManager != null) {
                try {
                    activityManager.registerActivityObserver(this.mActivityStateObserver, intent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void unRegisterAccountActivityObserver() {
        if (mListeningActivity) {
            mListeningActivity = false;
            new Intent();
            IActivityManager activityManager = ActivityManager.getService();
            if (activityManager != null) {
                try {
                    activityManager.unregisterActivityObserver(this.mActivityStateObserver);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addAccount(final Context context) {
        if (DEBUG) {
            Log.i(TAG, "addAccount");
        }
        new Bundle();
        AccountManager.get(context).addAccount(MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE, "passportapi", (String[]) null, (Bundle) null, (Activity) null, new AccountManagerCallback<Bundle>() {
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Intent intent = (Intent) future.getResult().getParcelable("intent");
                    intent.addFlags(268435456);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.i(AccountHelper.TAG, "addAccount");
                    e.printStackTrace();
                }
            }
        }, (Handler) null);
    }
}
