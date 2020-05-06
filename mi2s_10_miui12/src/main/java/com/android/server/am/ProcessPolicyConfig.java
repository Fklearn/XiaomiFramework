package com.android.server.am;

import java.util.ArrayList;

public class ProcessPolicyConfig {
    static final ArrayList<String> sDelayBootPersistentAppList = new ArrayList<>();
    static final ArrayList<String> sImportantProcessList = new ArrayList<>();
    static final ArrayList<String> sNeedTraceProcessList = new ArrayList<>();
    static final ArrayList<String> sProcessCleanProtectedList = new ArrayList<>();

    static {
        sNeedTraceProcessList.add("com.android.phone");
        sNeedTraceProcessList.add("com.miui.whetstone");
        sNeedTraceProcessList.add("com.android.nfc");
        sNeedTraceProcessList.add("com.fingerprints.serviceext");
        sDelayBootPersistentAppList.add("com.securespaces.android.ssm.service");
        sImportantProcessList.add("com.mobiletools.systemhelper:register");
        sProcessCleanProtectedList.add("com.miui.screenrecorder");
    }
}
