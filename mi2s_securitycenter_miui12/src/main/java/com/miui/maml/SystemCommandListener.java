package com.miui.maml;

import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;

public class SystemCommandListener implements ScreenElementRoot.OnExternCommandListener {
    private static final String CLEAR_RESOURCE = "__clearResource";
    private static final String REQUEST_UPDATE = "__requestUpdate";
    private ScreenElementRoot mRoot;

    public SystemCommandListener(ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
    }

    public void onCommand(String str, Double d2, String str2) {
        if (CLEAR_RESOURCE.equals(str)) {
            if (TextUtils.isEmpty(str2)) {
                this.mRoot.getContext().mResourceManager.clear();
            } else {
                this.mRoot.getContext().mResourceManager.clear(str2);
            }
        } else if (REQUEST_UPDATE.equals(str)) {
            this.mRoot.requestUpdate();
        }
    }
}
