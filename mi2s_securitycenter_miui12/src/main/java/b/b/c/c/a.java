package b.b.c.c;

import android.app.UiModeManager;
import android.os.Bundle;
import miui.app.Activity;

public class a extends Activity {
    private boolean mDarkModeEnable = false;

    public boolean isDarkModeEnable() {
        return this.mDarkModeEnable;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        a.super.onCreate(bundle);
        UiModeManager uiModeManager = (UiModeManager) getSystemService("uimode");
        if (uiModeManager != null) {
            this.mDarkModeEnable = uiModeManager.getNightMode() == 2;
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        a.super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        a.super.onResume();
    }
}
