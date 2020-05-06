package com.android.server.pm;

import android.content.IntentFilter;
import android.content.pm.PackageParser;
import android.content.pm.ProviderInfo;
import java.util.List;
import miui.security.WakePathComponent;

public class PackageManagerServiceCompat {
    public static int getPackageUid(PackageManagerService service, String packageName, int userId) {
        return service.getPackageUid(packageName, 8192, userId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0044, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<miui.security.WakePathComponent> getWakePathComponents(com.android.server.pm.PackageManagerService r5, java.lang.String r6) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            boolean r1 = android.text.TextUtils.isEmpty(r6)
            if (r1 == 0) goto L_0x000c
            return r0
        L_0x000c:
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r1 = r5.mPackages
            monitor-enter(r1)
            android.util.ArrayMap<java.lang.String, android.content.pm.PackageParser$Package> r2 = r5.mPackages     // Catch:{ all -> 0x0045 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ all -> 0x0045 }
            android.content.pm.PackageParser$Package r2 = (android.content.pm.PackageParser.Package) r2     // Catch:{ all -> 0x0045 }
            if (r2 != 0) goto L_0x001b
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            return r0
        L_0x001b:
            java.util.ArrayList r3 = r2.activities     // Catch:{ all -> 0x0045 }
            if (r3 == 0) goto L_0x0025
            java.util.ArrayList r3 = r2.activities     // Catch:{ all -> 0x0045 }
            r4 = 3
            parsePkgCompentLock(r0, r3, r4)     // Catch:{ all -> 0x0045 }
        L_0x0025:
            java.util.ArrayList r3 = r2.receivers     // Catch:{ all -> 0x0045 }
            if (r3 == 0) goto L_0x002f
            java.util.ArrayList r3 = r2.receivers     // Catch:{ all -> 0x0045 }
            r4 = 1
            parsePkgCompentLock(r0, r3, r4)     // Catch:{ all -> 0x0045 }
        L_0x002f:
            java.util.ArrayList r3 = r2.providers     // Catch:{ all -> 0x0045 }
            if (r3 == 0) goto L_0x0039
            java.util.ArrayList r3 = r2.providers     // Catch:{ all -> 0x0045 }
            r4 = 4
            parsePkgCompentLock(r0, r3, r4)     // Catch:{ all -> 0x0045 }
        L_0x0039:
            java.util.ArrayList r3 = r2.services     // Catch:{ all -> 0x0045 }
            if (r3 == 0) goto L_0x0043
            java.util.ArrayList r3 = r2.services     // Catch:{ all -> 0x0045 }
            r4 = 2
            parsePkgCompentLock(r0, r3, r4)     // Catch:{ all -> 0x0045 }
        L_0x0043:
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            return r0
        L_0x0045:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0045 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageManagerServiceCompat.getWakePathComponents(com.android.server.pm.PackageManagerService, java.lang.String):java.util.List");
    }

    private static void parsePkgCompentLock(List<WakePathComponent> wakePathComponents, List<? extends PackageParser.Component> components, int componentType) {
        boolean isExported;
        if (wakePathComponents != null && components != null) {
            for (int i = components.size() - 1; i >= 0; i--) {
                if (componentType == 1) {
                    isExported = components.get(i).info.exported;
                } else if (componentType == 2) {
                    isExported = components.get(i).info.exported;
                } else if (componentType == 3) {
                    isExported = components.get(i).info.exported;
                } else if (componentType != 4) {
                    isExported = false;
                } else {
                    isExported = components.get(i).info.exported;
                }
                if (isExported) {
                    WakePathComponent wakePathComponent = new WakePathComponent();
                    wakePathComponent.setType(componentType);
                    wakePathComponent.setClassname(components.get(i).className);
                    if (components.get(i).intents != null) {
                        for (int j = components.get(i).intents.size() - 1; j >= 0; j--) {
                            IntentFilter intentFilter = (IntentFilter) components.get(i).intents.get(j);
                            for (int k = intentFilter.countActions() - 1; k >= 0; k--) {
                                wakePathComponent.addIntentAction(intentFilter.getAction(k));
                            }
                        }
                    }
                    wakePathComponents.add(wakePathComponent);
                }
            }
        }
    }

    public static boolean isEnabledAndMatchLPr(Settings mSettings, ProviderInfo info, int flags, int userId) {
        return mSettings.isEnabledAndMatchLPr(info, flags, userId);
    }
}
