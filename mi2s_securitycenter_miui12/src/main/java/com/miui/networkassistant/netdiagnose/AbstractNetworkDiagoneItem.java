package com.miui.networkassistant.netdiagnose;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import b.b.c.h.f;
import com.miui.securitycenter.R;

public abstract class AbstractNetworkDiagoneItem {
    protected ConnectivityManager mCm;
    protected Context mContext = null;
    protected NetworkDiagnosticsManager mDiagnosticsManager = null;
    protected boolean mIsStatusNormal = true;

    public enum FixedResult {
        SUCCESS,
        FAILED,
        NETWORKCHANGED
    }

    public AbstractNetworkDiagoneItem(Context context) {
        this.mContext = context;
        this.mDiagnosticsManager = NetworkDiagnosticsManager.getInstance(this.mContext);
        this.mCm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
    }

    public abstract void check();

    public abstract FixedResult fix();

    public String getFixingWaitProgressDlgMsg() {
        return this.mContext.getResources().getString(R.string.usage_sorted_loading_text);
    }

    public boolean getIsContinueDiagnose() {
        return this.mIsStatusNormal;
    }

    public boolean getIsStatusNormal() {
        return this.mIsStatusNormal;
    }

    public abstract String getItemName();

    public abstract String getItemSolution();

    public abstract String getItemSummary();

    public boolean networkChanged() {
        int activeNetworkType = this.mDiagnosticsManager.getActiveNetworkType();
        return this.mDiagnosticsManager.getDiagnosingNetworkType() != activeNetworkType || !TextUtils.equals(this.mDiagnosticsManager.getDiagnosingNetworkInterface(), activeNetworkType != 1 ? f.b(this.mContext) : f.g(this.mContext));
    }

    public void reset() {
        this.mIsStatusNormal = true;
    }
}
