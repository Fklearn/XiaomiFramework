package com.miui.internal.vip.utils;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import miui.accounts.ExtraAccountManager;
import miui.telephony.phonenumber.Prefix;

public class VipDataPref {
    private static final String UpdatePostFix = "_update";
    private final Context mCtx;
    private final String mName;
    private volatile SharedPreferences mPref;
    private String mPrefName;

    public VipDataPref(Context ctx, String name) {
        this.mName = name;
        this.mCtx = ctx;
    }

    public SharedPreferences getPref() {
        Account account = getAccount();
        if (account == null) {
            return null;
        }
        String name = this.mName + Utils.md5(account.name);
        synchronized (this) {
            if (!name.equals(this.mPrefName)) {
                this.mPrefName = name;
                this.mPref = this.mCtx.getSharedPreferences(this.mPrefName, 0);
            }
        }
        return this.mPref;
    }

    public String getString(String name) {
        SharedPreferences pref = getPref();
        if (pref != null) {
            return pref.getString(name, Prefix.EMPTY);
        }
        return Prefix.EMPTY;
    }

    public void remove(String name) {
        SharedPreferences pref = getPref();
        if (pref != null) {
            pref.edit().remove(name).apply();
        }
    }

    public void setString(String name, String content) {
        SharedPreferences pref = getPref();
        if (pref != null) {
            pref.edit().putString(name, content).apply();
        }
    }

    public long getLong(String name) {
        SharedPreferences pref = getPref();
        if (pref == null) {
            return 0;
        }
        return pref.getLong(name + UpdatePostFix, 0);
    }

    public void setLong(String name, long data) {
        SharedPreferences pref = getPref();
        if (pref != null) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putLong(name + UpdatePostFix, data).apply();
        }
    }

    public int getInt(String name) {
        SharedPreferences pref = getPref();
        if (pref == null) {
            return 0;
        }
        return pref.getInt(name + UpdatePostFix, 0);
    }

    public void setInt(String name, int data) {
        SharedPreferences pref = getPref();
        if (pref != null) {
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt(name + UpdatePostFix, data).apply();
        }
    }

    public void clear() {
        SharedPreferences pref = getPref();
        if (pref != null) {
            pref.edit().clear().apply();
        }
    }

    public synchronized void reset() {
        this.mPrefName = null;
        this.mPref = null;
    }

    private Account getAccount() {
        return ExtraAccountManager.getXiaomiAccount(this.mCtx);
    }
}
