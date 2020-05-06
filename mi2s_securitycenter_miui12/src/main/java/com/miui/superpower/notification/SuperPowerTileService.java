package com.miui.superpower.notification;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import com.miui.superpower.b.k;

@TargetApi(24)
public class SuperPowerTileService extends TileService {

    /* renamed from: a  reason: collision with root package name */
    private Handler f8120a = new Handler();

    /* access modifiers changed from: private */
    public void a() {
        if (k.o(this) && !o.m(this)) {
            this.f8120a.postDelayed(new h(this), 500);
        }
    }

    private void a(Context context) {
        try {
            Object systemService = context.getSystemService("statusbar");
            systemService.getClass().getMethod("collapsePanels", new Class[0]).invoke(systemService, new Object[0]);
        } catch (Exception e) {
            Log.w("SuperPowerSaveManager", "collapseStatusBar failed. " + e);
        }
    }

    private void a(boolean z) {
        Tile qsTile = getQsTile();
        if (qsTile != null) {
            qsTile.setState(z ? 1 : 0);
            qsTile.setLabel(getString(R.string.superpower_settings));
            qsTile.updateTile();
        }
    }

    public void onClick() {
        Log.d("SuperPowerSaveManager", "onTileClick");
        if (isLocked()) {
            unlockAndRun(new g(this));
            return;
        }
        a((Context) this);
        a();
    }

    public void onStartListening() {
        super.onStartListening();
        Log.d("SuperPowerSaveManager", "onTileStartListening");
        a(k.o(this));
    }

    public void onTileAdded() {
        super.onTileAdded();
        Log.d("SuperPowerSaveManager", "onTileAdded");
        a(k.o(this));
    }
}
