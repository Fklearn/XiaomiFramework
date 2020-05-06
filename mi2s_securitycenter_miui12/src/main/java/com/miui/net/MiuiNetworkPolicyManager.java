package com.miui.net;

import android.content.Context;

public class MiuiNetworkPolicyManager {
    private miui.securitycenter.net.MiuiNetworkPolicyManager mMiuiPolicyManager;

    public MiuiNetworkPolicyManager(Context context) {
        this.mMiuiPolicyManager = new miui.securitycenter.net.MiuiNetworkPolicyManager(context);
    }

    public int getAppRestrictBackground(int i) {
        return this.mMiuiPolicyManager.getAppRestrictBackground(i);
    }

    public boolean getRestrictBackground() {
        return this.mMiuiPolicyManager.getRestrictBackground();
    }

    public boolean isAppRestrictBackground(int i) {
        return this.mMiuiPolicyManager.isAppRestrictBackground(i);
    }

    public void setAppRestrictBackground(int i, boolean z) {
        this.mMiuiPolicyManager.setAppRestrictBackground(i, z);
    }

    public void setRestrictBackground(boolean z) {
        this.mMiuiPolicyManager.setRestrictBackground(z);
    }
}
