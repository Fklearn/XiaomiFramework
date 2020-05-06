package com.miui.server.enterprise;

import android.content.Context;
import android.net.ConnectivityManager;
import com.android.server.wm.WindowManagerService;

public class RestrictionManagerServiceProxy {
    static void setScreenCaptureDisabled(WindowManagerService service, Context context, int userId, boolean enabled) {
        service.refreshScreenCaptureDisabled(userId);
    }

    static void setWifiApEnabled(Context context, boolean enabled) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        connectivityManager.stopTethering(0);
        connectivityManager.stopTethering(1);
        connectivityManager.stopTethering(2);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, byte], vars: [r0v0 ?, r0v2 ?, r0v3 ?]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:102)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:78)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:69)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:51)
        	at jadx.core.dex.visitors.InitCodeVariables.visit(InitCodeVariables.java:32)
        */
    static void setDisableRecoveryClearData(android.service.persistentdata.IPersistentDataBlockService r3, boolean r4) {
        /*
            r0 = 1
            byte[] r1 = new byte[r0]     // Catch:{ RemoteException -> 0x000f }
            r2 = 0
            if (r4 == 0) goto L_0x0007
            goto L_0x0008
        L_0x0007:
            r0 = r2
        L_0x0008:
            r1[r2] = r0     // Catch:{ RemoteException -> 0x000f }
            r3.writeEnterprisePersistentData(r2, r1)     // Catch:{ RemoteException -> 0x000f }
            goto L_0x0013
        L_0x000f:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0013:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.enterprise.RestrictionManagerServiceProxy.setDisableRecoveryClearData(android.service.persistentdata.IPersistentDataBlockService, boolean):void");
    }
}
