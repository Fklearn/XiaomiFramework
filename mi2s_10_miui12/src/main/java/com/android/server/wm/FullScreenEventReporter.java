package com.android.server.wm;

import android.hardware.display.DisplayManagerGlobal;
import android.util.Slog;
import com.android.server.slice.SliceClientPermissions;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class FullScreenEventReporter {
    private static final float ACTION_DOWN_JANKY_THRESHOLD = 40.0f;
    private static final float ACTION_MOVE_JANKY_THRESHOLD = 17.1f;
    private static final float ACTION_UP_JANKY_THRESHOLD = 40.0f;
    private static final float ANIMATION_JANKY_THRESHOLD = 17.1f;
    private static final int CACHE_LIST_SIZE = 10;
    static final String FS_ACTION_CANCEL = "CANCEL";
    static final String FS_ACTION_HOME = "HOME";
    private static final String FS_ACTION_KEY = "action";
    static final String FS_ACTION_RECENTS = "RECENTS";
    private static final String FS_DO_FRAMES_COUNT = "doFramesCount";
    private static final String FS_EXTRA1 = "extraKey1";
    private static final String FS_EXTRA2 = "extraKey2";
    private static final String FS_FUNCTION_VERSION = "fullScreenVersion";
    private static final String FS_JANKY_FRAMES_COUNT = "jankyFramesCount";
    private static final String FS_MODULE = "fsJankyFrames";
    private static final String FS_PACKAGE_NAME = "package";
    private static final String FS_RECORD_TIME = "recordTime";
    private static final String FS_VERSION_VALUE = "2";
    private static final int JANKY_FRAME_INTERVAL = 20;
    private static final String JSON_PREFIX_FORMAT = "{\"fullScreenVersion\":\"\",\"action\":\"\",\"jankyFramesCount\":\"\",\"extraKey1\":\"\",\"extraKey2\":\" %s\"}";
    private static final String TAG = "MiuiGesture";
    private static long[] actionStartTime = new long[3];
    private static long mFrameIntervalMsec = 0;
    private static double sActionJankyTimeOnce;
    private static long sCancelFrameCount;
    private static long sCancelFrameCountOnce;
    private static long sCancelJankyFrameCount;
    private static long sCancelJankyFrameCountOnce;
    private static long sCurrentTime = -1;
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static long sDoFrameCount = 0;
    private static long sDownActionCount;
    private static long sDownActionJankyCount;
    private static long sDownActionJankyCountOnce;
    private static float sDownX;
    private static float sDownY;
    private static double sFrameJankyTimeOnce;
    private static long sHomeFrameCount;
    private static long sHomeFrameCountOnce;
    private static long sHomeJankyFrameCount;
    private static long sHomeJankyFrameCountOnce;
    private static long sJankyFrameCount;
    private static final List<String> sJankyFramesInfoList = new ArrayList();
    private static long sMoveActionCount;
    private static long sMoveActionCountOnce;
    private static long sMoveActionJankyCount;
    private static long sMoveActionJankyCountOnce;
    private static long sRecentsFrameCount;
    private static long sRecentsFrameCountOnce;
    private static long sRecentsJankyFrameCount;
    private static long sRecentsJankyFrameCountOnce;
    private static long sTotalActionCount;
    private static long sTotalActionJankyCount;
    private static double sTotalActionJankyTime;
    private static long sTotalFrameCount;
    private static long sTotalFrameJankyCount;
    private static double sTotalFrameJankyTime;
    private static long sUpActionCount;
    private static long sUpActionJankyCount;
    private static long sUpActionJankyCountOnce;
    private static float sUpX;
    private static float sUpY;

    static void startActionEventTrace(int action, float x, float y) {
        if (action == 0) {
            sDownX = x;
            sDownY = y;
        }
        actionStartTime[action] = System.currentTimeMillis();
    }

    static void endActionEventTrace(int action, float x, float y) {
        int i = action;
        long took = System.currentTimeMillis() - actionStartTime[i];
        sTotalActionCount++;
        if (i == 0) {
            if (sMoveActionCountOnce > 0) {
                Slog.wtf("MiuiGesture", "action up lost???");
                resetActionOnceCount();
            }
            sDownActionCount++;
            if (((float) took) > 40.0f) {
                sTotalActionJankyCount++;
                sTotalActionJankyTime += (double) (((float) took) - 40.0f);
                sActionJankyTimeOnce += (double) (((float) took) - 40.0f);
                sDownActionJankyCount++;
                sDownActionJankyCountOnce++;
                if (MiuiGestureController.DEBUG_PERFORMANCE) {
                    Slog.d("MiuiGesture", "jank! ACTION_DOWN  took " + took + " ms");
                }
            }
        } else if (i == 1) {
            sMoveActionCount++;
            sMoveActionCountOnce++;
            if (((float) took) > 17.1f) {
                sTotalActionJankyCount++;
                sTotalActionJankyTime += (double) (((float) took) - 17.1f);
                sActionJankyTimeOnce += (double) (((float) took) - 17.1f);
                sMoveActionJankyCount++;
                sMoveActionJankyCountOnce++;
                if (MiuiGestureController.DEBUG_PERFORMANCE) {
                    Slog.d("MiuiGesture", "jank! ACTION_MOVE  took " + took + " ms");
                }
            }
        } else if (i == 2) {
            sUpActionCount++;
            sUpX = x;
            sUpY = y;
            if (((float) took) > 40.0f) {
                sTotalActionJankyCount++;
                sTotalActionJankyTime += (double) (((float) took) - 40.0f);
                sActionJankyTimeOnce += (double) (((float) took) - 40.0f);
                sUpActionJankyCount++;
                sUpActionJankyCountOnce++;
                if (MiuiGestureController.DEBUG_PERFORMANCE) {
                    Slog.d("MiuiGesture", "jank! ACTION_UP  took " + took + " ms");
                }
            }
            if (sDownY - sUpY > 20.0f) {
                Slog.i("MiuiGesture", "PointerEventEnd:" + sDownActionJankyCountOnce + "," + sMoveActionJankyCountOnce + SliceClientPermissions.SliceAuthority.DELIMITER + sMoveActionCountOnce + "," + sUpActionJankyCountOnce + "," + sActionJankyTimeOnce + ",(" + sDownX + "," + sDownY + "),(" + sUpX + "," + sUpY + ")");
            }
            resetActionOnceCount();
        }
    }

    private static void resetActionOnceCount() {
        sActionJankyTimeOnce = 0.0d;
        sDownActionJankyCountOnce = 0;
        sMoveActionJankyCountOnce = 0;
        sMoveActionCountOnce = 0;
        sUpActionJankyCountOnce = 0;
    }

    private static void resetAnimationOnceCount() {
        sFrameJankyTimeOnce = 0.0d;
        sHomeJankyFrameCountOnce = 0;
        sHomeFrameCountOnce = 0;
        sRecentsJankyFrameCountOnce = 0;
        sRecentsFrameCountOnce = 0;
        sCancelJankyFrameCountOnce = 0;
        sCancelFrameCountOnce = 0;
    }

    static void resetAnimationFrameIntervalParams() {
        if (sHomeFrameCountOnce > 0) {
            Slog.e("MiuiGesture", "home animation not reset");
            resetAnimationOnceCount();
        }
        if (sRecentsFrameCountOnce > 0) {
            Slog.e("MiuiGesture", "recents animation not reset");
            resetAnimationOnceCount();
        }
        if (sCancelFrameCountOnce > 0) {
            Slog.e("MiuiGesture", "cancel animation not reset");
            resetAnimationOnceCount();
        }
        sCurrentTime = -1;
        sJankyFrameCount = 0;
        sDoFrameCount = 0;
        if (mFrameIntervalMsec == 0) {
            mFrameIntervalMsec = (long) (1000.0f / getRefreshRate());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x006c  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:49:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void caculateAnimationFrameInterval(java.lang.String r22) {
        /*
            r0 = r22
            long r1 = sDoFrameCount
            r3 = 1
            long r1 = r1 + r3
            sDoFrameCount = r1
            long r1 = sTotalFrameCount
            long r1 = r1 + r3
            sTotalFrameCount = r1
            long r1 = sCurrentTime
            long r5 = java.lang.System.currentTimeMillis()
            sCurrentTime = r5
            int r5 = r22.hashCode()
            java.lang.String r7 = "CANCEL"
            java.lang.String r8 = "RECENTS"
            java.lang.String r9 = "HOME"
            r10 = 1980572282(0x760d227a, float:7.156378E32)
            r11 = 1800278360(0x6b4e1158, float:2.4912062E26)
            r12 = 2223327(0x21ecdf, float:3.115545E-39)
            r14 = 2
            r15 = 1
            if (r5 == r12) goto L_0x0042
            if (r5 == r11) goto L_0x003a
            if (r5 == r10) goto L_0x0032
        L_0x0031:
            goto L_0x004a
        L_0x0032:
            boolean r5 = r0.equals(r7)
            if (r5 == 0) goto L_0x0031
            r5 = r14
            goto L_0x004b
        L_0x003a:
            boolean r5 = r0.equals(r8)
            if (r5 == 0) goto L_0x0031
            r5 = r15
            goto L_0x004b
        L_0x0042:
            boolean r5 = r0.equals(r9)
            if (r5 == 0) goto L_0x0031
            r5 = 0
            goto L_0x004b
        L_0x004a:
            r5 = -1
        L_0x004b:
            if (r5 == 0) goto L_0x006c
            if (r5 == r15) goto L_0x005f
            if (r5 == r14) goto L_0x0052
            goto L_0x0079
        L_0x0052:
            long r16 = sCancelFrameCount
            long r16 = r16 + r3
            sCancelFrameCount = r16
            long r16 = sCancelFrameCountOnce
            long r16 = r16 + r3
            sCancelFrameCountOnce = r16
            goto L_0x0079
        L_0x005f:
            long r16 = sRecentsFrameCount
            long r16 = r16 + r3
            sRecentsFrameCount = r16
            long r16 = sRecentsFrameCountOnce
            long r16 = r16 + r3
            sRecentsFrameCountOnce = r16
            goto L_0x0079
        L_0x006c:
            long r16 = sHomeFrameCount
            long r16 = r16 + r3
            sHomeFrameCount = r16
            long r16 = sHomeFrameCountOnce
            long r16 = r16 + r3
            sHomeFrameCountOnce = r16
        L_0x0079:
            r16 = 0
            int r5 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r5 <= 0) goto L_0x0125
            long r16 = sCurrentTime
            long r13 = r16 - r1
            float r5 = (float) r13
            r17 = 1099484365(0x4188cccd, float:17.1)
            int r5 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r5 <= 0) goto L_0x0125
            long r18 = mFrameIntervalMsec
            long r18 = r13 / r18
            long r18 = r18 - r3
            long r20 = sJankyFrameCount
            long r20 = r20 + r18
            sJankyFrameCount = r20
            long r20 = sTotalFrameJankyCount
            long r20 = r20 + r3
            sTotalFrameJankyCount = r20
            double r20 = sTotalFrameJankyTime
            float r5 = (float) r13
            float r5 = r5 - r17
            double r3 = (double) r5
            double r20 = r20 + r3
            sTotalFrameJankyTime = r20
            double r3 = sFrameJankyTimeOnce
            float r5 = (float) r13
            float r5 = r5 - r17
            r20 = r7
            double r6 = (double) r5
            double r3 = r3 + r6
            sFrameJankyTimeOnce = r3
            boolean r3 = com.android.server.wm.MiuiGestureController.DEBUG_PERFORMANCE
            if (r3 == 0) goto L_0x00d1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "jank!frame took "
            r3.append(r4)
            r3.append(r13)
            java.lang.String r4 = "ms"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "MiuiGesture"
            android.util.Slog.d(r4, r3)
        L_0x00d1:
            int r3 = r22.hashCode()
            if (r3 == r12) goto L_0x00ee
            if (r3 == r11) goto L_0x00e6
            if (r3 == r10) goto L_0x00dc
        L_0x00db:
            goto L_0x00f6
        L_0x00dc:
            r3 = r20
            boolean r3 = r0.equals(r3)
            if (r3 == 0) goto L_0x00db
            r3 = 2
            goto L_0x00f7
        L_0x00e6:
            boolean r3 = r0.equals(r8)
            if (r3 == 0) goto L_0x00db
            r3 = r15
            goto L_0x00f7
        L_0x00ee:
            boolean r3 = r0.equals(r9)
            if (r3 == 0) goto L_0x00db
            r3 = 0
            goto L_0x00f7
        L_0x00f6:
            r3 = -1
        L_0x00f7:
            if (r3 == 0) goto L_0x0119
            if (r3 == r15) goto L_0x010c
            r4 = 2
            if (r3 == r4) goto L_0x00ff
            goto L_0x0125
        L_0x00ff:
            long r3 = sCancelJankyFrameCount
            r5 = 1
            long r3 = r3 + r5
            sCancelJankyFrameCount = r3
            long r3 = sCancelJankyFrameCountOnce
            long r3 = r3 + r5
            sCancelJankyFrameCountOnce = r3
            goto L_0x0125
        L_0x010c:
            r5 = 1
            long r3 = sRecentsJankyFrameCount
            long r3 = r3 + r5
            sRecentsJankyFrameCount = r3
            long r3 = sRecentsJankyFrameCountOnce
            long r3 = r3 + r5
            sRecentsJankyFrameCountOnce = r3
            goto L_0x0125
        L_0x0119:
            r5 = 1
            long r3 = sHomeJankyFrameCount
            long r3 = r3 + r5
            sHomeJankyFrameCount = r3
            long r3 = sHomeJankyFrameCountOnce
            long r3 = r3 + r5
            sHomeJankyFrameCountOnce = r3
        L_0x0125:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.FullScreenEventReporter.caculateAnimationFrameInterval(java.lang.String):void");
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x009e  */
    static void recordJankyFrames(java.lang.String r7, java.lang.String r8) {
        /*
            int r0 = r7.hashCode()
            r1 = 2223327(0x21ecdf, float:3.115545E-39)
            r2 = 0
            r3 = 2
            r4 = 1
            if (r0 == r1) goto L_0x002b
            r1 = 1800278360(0x6b4e1158, float:2.4912062E26)
            if (r0 == r1) goto L_0x0021
            r1 = 1980572282(0x760d227a, float:7.156378E32)
            if (r0 == r1) goto L_0x0017
        L_0x0016:
            goto L_0x0035
        L_0x0017:
            java.lang.String r0 = "CANCEL"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r3
            goto L_0x0036
        L_0x0021:
            java.lang.String r0 = "RECENTS"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r4
            goto L_0x0036
        L_0x002b:
            java.lang.String r0 = "HOME"
            boolean r0 = r7.equals(r0)
            if (r0 == 0) goto L_0x0016
            r0 = r2
            goto L_0x0036
        L_0x0035:
            r0 = -1
        L_0x0036:
            if (r0 == 0) goto L_0x009e
            if (r0 == r4) goto L_0x006e
            if (r0 == r3) goto L_0x003e
            goto L_0x00ce
        L_0x003e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            java.lang.String r1 = " animation end:"
            r0.append(r1)
            long r5 = sCancelJankyFrameCountOnce
            r0.append(r5)
            java.lang.String r1 = "/"
            r0.append(r1)
            long r5 = sCancelFrameCountOnce
            r0.append(r5)
            java.lang.String r1 = ","
            r0.append(r1)
            double r5 = sFrameJankyTimeOnce
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "MiuiGesture"
            android.util.Slog.i(r1, r0)
            goto L_0x00ce
        L_0x006e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            java.lang.String r1 = " animation end:"
            r0.append(r1)
            long r5 = sRecentsJankyFrameCountOnce
            r0.append(r5)
            java.lang.String r1 = "/"
            r0.append(r1)
            long r5 = sRecentsFrameCountOnce
            r0.append(r5)
            java.lang.String r1 = ","
            r0.append(r1)
            double r5 = sFrameJankyTimeOnce
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "MiuiGesture"
            android.util.Slog.i(r1, r0)
            goto L_0x00ce
        L_0x009e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            java.lang.String r1 = " animation end:"
            r0.append(r1)
            long r5 = sHomeJankyFrameCountOnce
            r0.append(r5)
            java.lang.String r1 = "/"
            r0.append(r1)
            long r5 = sHomeFrameCountOnce
            r0.append(r5)
            java.lang.String r1 = ","
            r0.append(r1)
            double r5 = sFrameJankyTimeOnce
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "MiuiGesture"
            android.util.Slog.i(r1, r0)
        L_0x00ce:
            resetAnimationOnceCount()
            java.lang.Class<com.android.server.wm.FullScreenEventReporter> r0 = com.android.server.wm.FullScreenEventReporter.class
            monitor-enter(r0)
            org.json.JSONObject r1 = frameInfoToJson(r7, r8)     // Catch:{ JSONException -> 0x00e5 }
            java.util.List<java.lang.String> r3 = sJankyFramesInfoList     // Catch:{ JSONException -> 0x00e5 }
            java.lang.String r5 = r1.toString()     // Catch:{ JSONException -> 0x00e5 }
            r3.add(r5)     // Catch:{ JSONException -> 0x00e5 }
            goto L_0x00fc
        L_0x00e3:
            r1 = move-exception
            goto L_0x0120
        L_0x00e5:
            r1 = move-exception
            r1.printStackTrace()     // Catch:{ all -> 0x00e3 }
            java.util.List<java.lang.String> r3 = sJankyFramesInfoList     // Catch:{ all -> 0x00e3 }
            java.lang.String r5 = "{\"fullScreenVersion\":\"\",\"action\":\"\",\"jankyFramesCount\":\"\",\"extraKey1\":\"\",\"extraKey2\":\" %s\"}"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x00e3 }
            java.lang.String r6 = r1.toString()     // Catch:{ all -> 0x00e3 }
            r4[r2] = r6     // Catch:{ all -> 0x00e3 }
            java.lang.String r2 = java.lang.String.format(r5, r4)     // Catch:{ all -> 0x00e3 }
            r3.add(r2)     // Catch:{ all -> 0x00e3 }
        L_0x00fc:
            java.util.List<java.lang.String> r1 = sJankyFramesInfoList     // Catch:{ all -> 0x00e3 }
            int r1 = r1.size()     // Catch:{ all -> 0x00e3 }
            r2 = 10
            if (r1 < r2) goto L_0x011e
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x00e3 }
            java.util.List<java.lang.String> r2 = sJankyFramesInfoList     // Catch:{ all -> 0x00e3 }
            r1.<init>(r2)     // Catch:{ all -> 0x00e3 }
            java.util.List<java.lang.String> r2 = sJankyFramesInfoList     // Catch:{ all -> 0x00e3 }
            r2.clear()     // Catch:{ all -> 0x00e3 }
            android.os.Handler r2 = com.android.internal.os.BackgroundThread.getHandler()     // Catch:{ all -> 0x00e3 }
            com.android.server.wm.-$$Lambda$FullScreenEventReporter$Bk586HS9H6gobNfp0oWjTREouWs r3 = new com.android.server.wm.-$$Lambda$FullScreenEventReporter$Bk586HS9H6gobNfp0oWjTREouWs     // Catch:{ all -> 0x00e3 }
            r3.<init>(r1)     // Catch:{ all -> 0x00e3 }
            r2.post(r3)     // Catch:{ all -> 0x00e3 }
        L_0x011e:
            monitor-exit(r0)     // Catch:{ all -> 0x00e3 }
            return
        L_0x0120:
            monitor-exit(r0)     // Catch:{ all -> 0x00e3 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.FullScreenEventReporter.recordJankyFrames(java.lang.String, java.lang.String):void");
    }

    static void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("gesture performance statistics:");
        pw.println();
        pw.print(prefix + prefix);
        pw.print("Total Action: " + sTotalActionCount);
        pw.println();
        pw.print(prefix + prefix);
        pw.print("Total Action Janky Count: " + sTotalActionJankyCount);
        pw.println();
        pw.print(prefix + prefix);
        pw.print("Total Action Janky Time: " + sTotalActionJankyTime);
        pw.println();
        pw.print(prefix + prefix);
        pw.print("Action Down: " + sDownActionJankyCount + SliceClientPermissions.SliceAuthority.DELIMITER + sDownActionCount);
        pw.println();
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append(prefix);
        pw.print(sb.toString());
        pw.print("Action Move: " + sMoveActionJankyCount + SliceClientPermissions.SliceAuthority.DELIMITER + sMoveActionCount);
        pw.println();
        StringBuilder sb2 = new StringBuilder();
        sb2.append(prefix);
        sb2.append(prefix);
        pw.print(sb2.toString());
        pw.print("Action Up: " + sUpActionJankyCount + SliceClientPermissions.SliceAuthority.DELIMITER + sUpActionCount);
        pw.println();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(prefix);
        sb3.append(prefix);
        pw.print(sb3.toString());
        pw.print("Total Frame: " + sTotalFrameCount);
        pw.println();
        pw.print(prefix + prefix);
        pw.print("Total Frame Janky Count: " + sTotalFrameJankyCount);
        pw.println();
        pw.print(prefix + prefix);
        pw.print("Total Frame Janky Time: " + sTotalFrameJankyTime);
        pw.println();
        pw.print(prefix + prefix);
        pw.print("Home: " + sHomeJankyFrameCount + SliceClientPermissions.SliceAuthority.DELIMITER + sHomeFrameCount);
        pw.println();
        StringBuilder sb4 = new StringBuilder();
        sb4.append(prefix);
        sb4.append(prefix);
        pw.print(sb4.toString());
        pw.print("Recent: " + sRecentsJankyFrameCount + SliceClientPermissions.SliceAuthority.DELIMITER + sRecentsFrameCount);
        pw.println();
        StringBuilder sb5 = new StringBuilder();
        sb5.append(prefix);
        sb5.append(prefix);
        pw.print(sb5.toString());
        pw.print("Cancel: " + sCancelJankyFrameCount + SliceClientPermissions.SliceAuthority.DELIMITER + sCancelFrameCount);
        pw.println();
    }

    private static float getRefreshRate() {
        return DisplayManagerGlobal.getInstance().getDisplayInfo(0).getMode().getRefreshRate();
    }

    private static JSONObject frameInfoToJson(String action, String packageName) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FS_FUNCTION_VERSION, "2");
        jsonObject.put("action", action);
        jsonObject.put(FS_JANKY_FRAMES_COUNT, String.valueOf(sJankyFrameCount));
        JSONObject jsonSub = new JSONObject();
        jsonSub.put(FS_DO_FRAMES_COUNT, String.valueOf(sDoFrameCount));
        jsonSub.put("package", packageName);
        jsonSub.put(FS_RECORD_TIME, sDateFormat.format(Long.valueOf(System.currentTimeMillis())));
        jsonObject.put(FS_EXTRA1, jsonSub.toString());
        jsonObject.put(FS_EXTRA2, "");
        return jsonObject;
    }
}
