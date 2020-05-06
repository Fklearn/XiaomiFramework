package com.miui.gamebooster.videobox.settings;

import android.os.Bundle;
import android.util.Log;
import b.b.c.c.a;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.securitycenter.R;

public class VideoBoxSettingsActivity extends a {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.gb_video_settings_activity);
        if (!e.a()) {
            Log.e("VideoBoxSettings", "Device not support vtb!!!");
            finish();
        }
    }
}
