package com.miui.permcenter;

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityNodeProvider;

class d extends View.AccessibilityDelegate {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DebugSettingsAcitivty f6096a;

    d(DebugSettingsAcitivty debugSettingsAcitivty) {
        this.f6096a = debugSettingsAcitivty;
    }

    public AccessibilityNodeProvider getAccessibilityNodeProvider(View view) {
        return new c(this);
    }

    public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
        return true;
    }
}
