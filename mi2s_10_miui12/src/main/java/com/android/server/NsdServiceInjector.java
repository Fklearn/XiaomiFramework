package com.android.server;

import android.net.nsd.NsdServiceInfo;
import android.net.nsd.NsdServiceInfoInjector;
import android.util.Base64;

public class NsdServiceInjector {
    static void resolveTextRecored(NsdServiceInfo nsdServiceInfo, String[] cooked) {
        if (cooked.length >= 7) {
            int parseInt = Integer.parseInt(cooked[5]);
            byte[] txtRecord = Base64.decode(cooked[6].getBytes(), 0);
            NsdServiceInfoInjector.setTxtRecord(txtRecord.length, txtRecord, nsdServiceInfo);
        }
    }
}
