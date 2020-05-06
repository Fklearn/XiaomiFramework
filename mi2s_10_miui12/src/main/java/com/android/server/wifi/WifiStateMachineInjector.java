package com.android.server.wifi;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.UserHandle;
import android.view.MiuiWindowManager;

final class WifiStateMachineInjector {
    WifiStateMachineInjector() {
    }

    static void sendScanResultsAvailableBroadcast(Context context, boolean screenOn, boolean scanSucceeded) {
        Resources resources;
        String nlpName;
        if (screenOn && (resources = context.getResources()) != null && (nlpName = resources.getString(17039783)) != null && nlpName.indexOf("xiaomi") >= 0) {
            Intent intent = new Intent("android.net.wifi.SCAN_RESULTS");
            intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
            intent.putExtra("resultsUpdated", scanSucceeded);
            intent.setPackage(nlpName);
            context.sendBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    static int convetFrequencyToOperatingChannel(int freq, int sec, int vht) {
        if (sec > 1 || sec < -1) {
            return 0;
        }
        if (freq >= 2412 && freq <= 2472) {
            return 81;
        }
        if (freq == 2484) {
            return 82;
        }
        if (freq < 5180 || freq > 5240) {
            if (freq < 5745 || freq > 5845) {
                if (freq < 5000 || freq > 5700) {
                    if (freq < 58320 || freq > 64800) {
                        return 0;
                    }
                    return 180;
                } else if (sec == 1) {
                    return 122;
                } else {
                    if (sec == -1) {
                        return 123;
                    }
                    return 125;
                }
            } else if (sec == 1) {
                return 126;
            } else {
                if (sec == -1) {
                    return 127;
                }
                if (freq <= 5805) {
                    return 124;
                }
                return 125;
            }
        } else if (sec == 1) {
            return 116;
        } else {
            if (sec == -1) {
                return 117;
            }
            return 115;
        }
    }
}
