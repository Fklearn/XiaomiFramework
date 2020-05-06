package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.text.TextUtils;
import b.b.c.h.f;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.securitycenter.R;

public class NetworkChangedCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NetworkDiagnostics_ChangedCheck";

    public NetworkChangedCheck(Context context) {
        super(context);
    }

    public void check() {
        this.mIsStatusNormal = true;
        int activeNetworkType = this.mDiagnosticsManager.getActiveNetworkType();
        String b2 = activeNetworkType != 1 ? f.b(this.mContext) : f.g(this.mContext);
        if (this.mDiagnosticsManager.getDiagnosingNetworkType() != activeNetworkType || !TextUtils.equals(this.mDiagnosticsManager.getDiagnosingNetworkInterface(), b2)) {
            this.mIsStatusNormal = false;
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.networkchanged_exception_title);
    }

    public String getItemSolution() {
        return null;
    }

    public String getItemSummary() {
        return this.mContext.getResources().getString(R.string.networkchanged_exception_summary);
    }
}
