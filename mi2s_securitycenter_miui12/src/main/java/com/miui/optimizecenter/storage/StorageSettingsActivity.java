package com.miui.optimizecenter.storage;

import android.os.Bundle;
import androidx.annotation.Nullable;
import b.b.c.c.a;
import com.miui.optimizecenter.storage.b.b;
import com.miui.securitycenter.R;

public class StorageSettingsActivity extends a {
    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        if (getIntent() == null) {
            finish();
            return;
        }
        setTitle(getResources().getString(R.string.priority_storage_title));
        getFragmentManager().beginTransaction().add(16908290, new b()).commitAllowingStateLoss();
    }
}
