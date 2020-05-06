package com.android.server.utils;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PriorityDump {
    public static final String PRIORITY_ARG = "--dump-priority";
    public static final String PRIORITY_ARG_CRITICAL = "CRITICAL";
    public static final String PRIORITY_ARG_HIGH = "HIGH";
    public static final String PRIORITY_ARG_NORMAL = "NORMAL";
    private static final int PRIORITY_TYPE_CRITICAL = 1;
    private static final int PRIORITY_TYPE_HIGH = 2;
    private static final int PRIORITY_TYPE_INVALID = 0;
    private static final int PRIORITY_TYPE_NORMAL = 3;
    public static final String PROTO_ARG = "--proto";

    @Retention(RetentionPolicy.SOURCE)
    private @interface PriorityType {
    }

    private PriorityDump() {
        throw new UnsupportedOperationException();
    }

    /* JADX WARNING: type inference failed for: r4v5, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void dump(com.android.server.utils.PriorityDump.PriorityDumper r8, java.io.FileDescriptor r9, java.io.PrintWriter r10, java.lang.String[] r11) {
        /*
            r0 = 0
            r1 = 0
            if (r11 != 0) goto L_0x0008
            r8.dump(r9, r10, r11, r0)
            return
        L_0x0008:
            int r2 = r11.length
            java.lang.String[] r2 = new java.lang.String[r2]
            r3 = 0
            r4 = 0
        L_0x000d:
            int r5 = r11.length
            r6 = 1
            if (r4 >= r5) goto L_0x003e
            r5 = r11[r4]
            java.lang.String r7 = "--proto"
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L_0x001d
            r0 = 1
            goto L_0x003c
        L_0x001d:
            r5 = r11[r4]
            java.lang.String r7 = "--dump-priority"
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L_0x0035
            int r5 = r4 + 1
            int r7 = r11.length
            if (r5 >= r7) goto L_0x003c
            int r4 = r4 + 1
            r5 = r11[r4]
            int r1 = getPriorityType(r5)
            goto L_0x003c
        L_0x0035:
            int r5 = r3 + 1
            r7 = r11[r4]
            r2[r3] = r7
            r3 = r5
        L_0x003c:
            int r4 = r4 + r6
            goto L_0x000d
        L_0x003e:
            int r4 = r11.length
            if (r3 >= r4) goto L_0x0048
            java.lang.Object[] r4 = java.util.Arrays.copyOf(r2, r3)
            r2 = r4
            java.lang.String[] r2 = (java.lang.String[]) r2
        L_0x0048:
            if (r1 == r6) goto L_0x005c
            r4 = 2
            if (r1 == r4) goto L_0x0058
            r4 = 3
            if (r1 == r4) goto L_0x0054
            r8.dump(r9, r10, r2, r0)
            return
        L_0x0054:
            r8.dumpNormal(r9, r10, r2, r0)
            return
        L_0x0058:
            r8.dumpHigh(r9, r10, r2, r0)
            return
        L_0x005c:
            r8.dumpCritical(r9, r10, r2, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.utils.PriorityDump.dump(com.android.server.utils.PriorityDump$PriorityDumper, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0040 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int getPriorityType(java.lang.String r5) {
        /*
            int r0 = r5.hashCode()
            r1 = -1986416409(0xffffffff8999b0e7, float:-3.699977E-33)
            r2 = 0
            r3 = 2
            r4 = 1
            if (r0 == r1) goto L_0x002b
            r1 = -1560189025(0xffffffffa301679f, float:-7.015047E-18)
            if (r0 == r1) goto L_0x0021
            r1 = 2217378(0x21d5a2, float:3.107208E-39)
            if (r0 == r1) goto L_0x0017
        L_0x0016:
            goto L_0x0035
        L_0x0017:
            java.lang.String r0 = "HIGH"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r4
            goto L_0x0036
        L_0x0021:
            java.lang.String r0 = "CRITICAL"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r2
            goto L_0x0036
        L_0x002b:
            java.lang.String r0 = "NORMAL"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r3
            goto L_0x0036
        L_0x0035:
            r0 = -1
        L_0x0036:
            if (r0 == 0) goto L_0x0040
            if (r0 == r4) goto L_0x003f
            if (r0 == r3) goto L_0x003d
            return r2
        L_0x003d:
            r0 = 3
            return r0
        L_0x003f:
            return r3
        L_0x0040:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.utils.PriorityDump.getPriorityType(java.lang.String):int");
    }

    public interface PriorityDumper {
        void dumpCritical(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
        }

        void dumpHigh(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
        }

        void dumpNormal(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
        }

        void dump(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
            dumpCritical(fd, pw, args, asProto);
            dumpHigh(fd, pw, args, asProto);
            dumpNormal(fd, pw, args, asProto);
        }
    }
}
