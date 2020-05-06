package com.android.server.am;

/* compiled from: ActiveServicesInjector */
class LowPriorityServiceInfo {
    long delay;
    boolean isRestart = false;
    ServiceRecord mR;
    boolean restartPerformed = true;

    public LowPriorityServiceInfo(ServiceRecord r, boolean restart) {
        this.mR = r;
        this.isRestart = restart;
    }
}
