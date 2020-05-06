package com.android.server.pm;

import android.util.Slog;
import java.util.Locale;

class InstallationStatistic {
    String installerPkg = "";
    String pkg = "";
    long timeAospSecurityCheck = 0;
    long timeBeginAospVerify = 0;
    long timeBeginInstall = 0;
    long timeCollectingCerts = 0;
    long timeCopyApkConsumed = 0;
    long timeDexopt = 0;
    long timeEndInstall = 0;
    long timeMiuiSecurityCheck = 0;

    InstallationStatistic() {
    }

    /* access modifiers changed from: package-private */
    public void dump() {
        Slog.i("InstallationStatistic", toString());
    }

    public String toString() {
        return String.format(Locale.ENGLISH, "%s|%s|%d|%d|%d|%d|%d|%d", new Object[]{this.pkg, this.installerPkg, Long.valueOf(this.timeEndInstall - this.timeBeginInstall), Long.valueOf(this.timeCopyApkConsumed), Long.valueOf(this.timeCollectingCerts), Long.valueOf(this.timeMiuiSecurityCheck), Long.valueOf(this.timeAospSecurityCheck), Long.valueOf(this.timeDexopt)});
    }
}
