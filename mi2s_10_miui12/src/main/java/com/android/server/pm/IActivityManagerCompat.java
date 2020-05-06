package com.android.server.pm;

import android.app.ActivityManagerNative;
import android.app.IApplicationThread;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;

public class IActivityManagerCompat {
    public static void sendBroadcast(Intent intent, String requiredPermission) throws RemoteException {
        ActivityManagerNative.getDefault().broadcastIntent((IApplicationThread) null, intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, new String[]{requiredPermission}, -1, (Bundle) null, false, false, 0);
    }
}
