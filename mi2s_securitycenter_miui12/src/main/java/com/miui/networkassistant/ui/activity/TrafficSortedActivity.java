package com.miui.networkassistant.ui.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.MessageQueue;
import b.b.c.c.b.e;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.ui.fragment.TrafficSortedFragment;
import com.miui.networkassistant.utils.AnalyticsHelper;

public class TrafficSortedActivity extends e {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        postOnIdleUiThread(new MessageQueue.IdleHandler() {
            public boolean queueIdle() {
                AnalyticsHelper.trackActiveNetworkAssistant(TrafficSortedActivity.this.getApplicationContext());
                return false;
            }
        });
    }

    public Fragment onCreateFragment() {
        TrafficSortedFragment trafficSortedFragment = new TrafficSortedFragment();
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Sim.SIM_SLOT_NUM_TAG)) {
            bundle.putInt(Sim.SIM_SLOT_NUM_TAG, intent.getIntExtra(Sim.SIM_SLOT_NUM_TAG, 0));
        }
        trafficSortedFragment.setArguments(bundle);
        return trafficSortedFragment;
    }
}
