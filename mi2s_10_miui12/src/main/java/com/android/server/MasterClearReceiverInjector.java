package com.android.server;

import android.os.SystemProperties;

class MasterClearReceiverInjector {
    MasterClearReceiverInjector() {
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void rebootFactoryReset(android.content.Context r12, boolean r13) throws java.io.IOException {
        /*
            android.os.ConditionVariable r0 = new android.os.ConditionVariable
            r0.<init>()
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r2 = "android.intent.action.MASTER_CLEAR_NOTIFICATION"
            r1.<init>(r2)
            r2 = 268435456(0x10000000, float:2.5243549E-29)
            r1.addFlags(r2)
            android.os.UserHandle r5 = android.os.UserHandle.OWNER
            com.android.server.MasterClearReceiverInjector$1 r7 = new com.android.server.MasterClearReceiverInjector$1
            r7.<init>(r0)
            java.lang.String r6 = "android.permission.MASTER_CLEAR"
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r3 = r12
            r4 = r1
            r3.sendOrderedBroadcastAsUser(r4, r5, r6, r7, r8, r9, r10, r11)
            r0.block()
            boolean r2 = isFormatData()
            r3 = 2
            java.lang.String r4 = "bootCommand"
            r5 = 1
            r6 = 0
            if (r2 == 0) goto L_0x005c
            java.lang.Class<android.os.RecoverySystem> r2 = android.os.RecoverySystem.class
            java.lang.Class<java.lang.Void> r7 = java.lang.Void.class
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r6] = r12
            java.lang.String[] r8 = new java.lang.String[r5]
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "--format_data\n--locale="
            r9.append(r10)
            java.util.Locale r10 = java.util.Locale.getDefault()
            java.lang.String r10 = r10.toString()
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8[r6] = r9
            r3[r5] = r8
            miui.util.ReflectionUtils.tryCallStaticMethod(r2, r4, r7, r3)
            goto L_0x00b3
        L_0x005c:
            if (r13 == 0) goto L_0x0089
            java.lang.Class<android.os.RecoverySystem> r2 = android.os.RecoverySystem.class
            java.lang.Class<java.lang.Void> r7 = java.lang.Void.class
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r6] = r12
            java.lang.String[] r8 = new java.lang.String[r5]
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "--wipe_data_and_storage\n--locale="
            r9.append(r10)
            java.util.Locale r10 = java.util.Locale.getDefault()
            java.lang.String r10 = r10.toString()
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8[r6] = r9
            r3[r5] = r8
            miui.util.ReflectionUtils.tryCallStaticMethod(r2, r4, r7, r3)
            goto L_0x00b3
        L_0x0089:
            java.lang.Class<android.os.RecoverySystem> r2 = android.os.RecoverySystem.class
            java.lang.Class<java.lang.Void> r7 = java.lang.Void.class
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r6] = r12
            java.lang.String[] r8 = new java.lang.String[r5]
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "--wipe_data\n--locale="
            r9.append(r10)
            java.util.Locale r10 = java.util.Locale.getDefault()
            java.lang.String r10 = r10.toString()
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8[r6] = r9
            r3[r5] = r8
            miui.util.ReflectionUtils.tryCallStaticMethod(r2, r4, r7, r3)
        L_0x00b3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.MasterClearReceiverInjector.rebootFactoryReset(android.content.Context, boolean):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void rebootFactoryReset(android.content.Context r12, boolean r13, java.lang.String r14) throws java.io.IOException {
        /*
            android.os.ConditionVariable r0 = new android.os.ConditionVariable
            r0.<init>()
            android.content.Intent r1 = new android.content.Intent
            java.lang.String r2 = "android.intent.action.MASTER_CLEAR_NOTIFICATION"
            r1.<init>(r2)
            r2 = 268435456(0x10000000, float:2.5243549E-29)
            r1.addFlags(r2)
            android.os.UserHandle r5 = android.os.UserHandle.OWNER
            com.android.server.MasterClearReceiverInjector$2 r7 = new com.android.server.MasterClearReceiverInjector$2
            r7.<init>(r0)
            java.lang.String r6 = "android.permission.MASTER_CLEAR"
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r3 = r12
            r4 = r1
            r3.sendOrderedBroadcastAsUser(r4, r5, r6, r7, r8, r9, r10, r11)
            r0.block()
            if (r13 == 0) goto L_0x0042
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "--wipe_data_and_storage\n--locale="
            r2.append(r3)
            java.util.Locale r3 = java.util.Locale.getDefault()
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            goto L_0x005b
        L_0x0042:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "--wipe_data\n--locale="
            r2.append(r3)
            java.util.Locale r3 = java.util.Locale.getDefault()
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
        L_0x005b:
            boolean r3 = android.text.TextUtils.isEmpty(r14)
            if (r3 != 0) goto L_0x0075
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            java.lang.String r4 = "\n--secure_pw="
            r3.append(r4)
            r3.append(r14)
            java.lang.String r2 = r3.toString()
        L_0x0075:
            java.lang.Class<android.os.RecoverySystem> r3 = android.os.RecoverySystem.class
            java.lang.Class<java.lang.Void> r4 = java.lang.Void.class
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r12
            r7 = 1
            java.lang.String[] r8 = new java.lang.String[r7]
            r8[r6] = r2
            r5[r7] = r8
            java.lang.String r6 = "bootCommand"
            miui.util.ReflectionUtils.tryCallStaticMethod(r3, r6, r4, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.MasterClearReceiverInjector.rebootFactoryReset(android.content.Context, boolean, java.lang.String):void");
    }

    static boolean isFormatData() {
        return "encrypted".equals(SystemProperties.get("ro.crypto.state")) && "true".equals(SystemProperties.get("ro.miui.has_cust_partition"));
    }
}
