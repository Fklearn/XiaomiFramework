package com.android.server;

import android.content.Context;
import com.android.internal.os.RoSystemProperties;

class BluetoothService extends SystemService {
    private BluetoothManagerService mBluetoothManagerService;
    private boolean mInitialized = false;

    public BluetoothService(Context context) {
        super(context);
        this.mBluetoothManagerService = new BluetoothManagerService(context);
    }

    private void initialize() {
        if (!this.mInitialized) {
            this.mBluetoothManagerService.handleOnBootPhase();
            this.mInitialized = true;
        }
    }

    public void onStart() {
    }

    /* JADX WARNING: type inference failed for: r0v3, types: [android.os.IBinder, com.android.server.BluetoothManagerService] */
    public void onBootPhase(int phase) {
        if (phase == 500) {
            publishBinderService("bluetooth_manager", this.mBluetoothManagerService);
        } else if (phase == 550 && !RoSystemProperties.MULTIUSER_HEADLESS_SYSTEM_USER) {
            initialize();
        }
    }

    public void onSwitchUser(int userHandle) {
        initialize();
        this.mBluetoothManagerService.handleOnSwitchUser(userHandle);
    }

    public void onUnlockUser(int userHandle) {
        this.mBluetoothManagerService.handleOnUnlockUser(userHandle);
    }
}
