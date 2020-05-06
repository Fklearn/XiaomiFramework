package com.android.server.am;

import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.TimeUtils;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.wm.SafeActivityOptions;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Objects;

public final class PendingIntentRecord extends IIntentSender.Stub {
    public static final int FLAG_ACTIVITY_SENDER = 1;
    public static final int FLAG_BROADCAST_SENDER = 2;
    public static final int FLAG_SERVICE_SENDER = 4;
    private static final String TAG = "ActivityManager";
    boolean canceled = false;
    final PendingIntentController controller;
    final Key key;
    String lastTag;
    String lastTagPrefix;
    private ArraySet<IBinder> mAllowBgActivityStartsForActivitySender = new ArraySet<>();
    private ArraySet<IBinder> mAllowBgActivityStartsForBroadcastSender = new ArraySet<>();
    private ArraySet<IBinder> mAllowBgActivityStartsForServiceSender = new ArraySet<>();
    private RemoteCallbackList<IResultReceiver> mCancelCallbacks;
    public final WeakReference<PendingIntentRecord> ref;
    boolean sent = false;
    String stringName;
    final int uid;
    private ArrayMap<IBinder, Long> whitelistDuration;

    static final class Key {
        private static final int ODD_PRIME_NUMBER = 37;
        final IBinder activity;
        Intent[] allIntents;
        String[] allResolvedTypes;
        final int flags;
        final int hashCode;
        final SafeActivityOptions options;
        final String packageName;
        final int requestCode;
        final Intent requestIntent;
        final String requestResolvedType;
        final int type;
        final int userId;
        final String who;

        Key(int _t, String _p, IBinder _a, String _w, int _r, Intent[] _i, String[] _it, int _f, SafeActivityOptions _o, int _userId) {
            this.type = _t;
            this.packageName = _p;
            this.activity = _a;
            this.who = _w;
            this.requestCode = _r;
            String str = null;
            this.requestIntent = _i != null ? _i[_i.length - 1] : null;
            this.requestResolvedType = _it != null ? _it[_it.length - 1] : str;
            this.allIntents = _i;
            this.allResolvedTypes = _it;
            this.flags = _f;
            this.options = _o;
            this.userId = _userId;
            int hash = (((((23 * 37) + _f) * 37) + _r) * 37) + _userId;
            hash = _w != null ? (hash * 37) + _w.hashCode() : hash;
            hash = _a != null ? (hash * 37) + _a.hashCode() : hash;
            Intent intent = this.requestIntent;
            hash = intent != null ? (hash * 37) + intent.filterHashCode() : hash;
            String str2 = this.requestResolvedType;
            this.hashCode = ((((str2 != null ? (hash * 37) + str2.hashCode() : hash) * 37) + (_p != null ? _p.hashCode() : 0)) * 37) + _t;
        }

        public boolean equals(Object otherObj) {
            if (otherObj == null) {
                return false;
            }
            try {
                Key other = (Key) otherObj;
                if (this.type != other.type || this.userId != other.userId || !Objects.equals(this.packageName, other.packageName) || this.activity != other.activity || !Objects.equals(this.who, other.who) || this.requestCode != other.requestCode) {
                    return false;
                }
                Intent intent = this.requestIntent;
                Intent intent2 = other.requestIntent;
                if (intent != intent2) {
                    if (this.requestIntent != null) {
                        if (!this.requestIntent.filterEquals(intent2)) {
                            return false;
                        }
                    } else if (intent2 != null) {
                        return false;
                    }
                }
                if (Objects.equals(this.requestResolvedType, other.requestResolvedType) && this.flags == other.flags) {
                    return true;
                }
                return false;
            } catch (ClassCastException e) {
                return false;
            }
        }

        public int hashCode() {
            return this.hashCode;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Key{");
            sb.append(typeName());
            sb.append(" pkg=");
            sb.append(this.packageName);
            sb.append(" intent=");
            Intent intent = this.requestIntent;
            sb.append(intent != null ? intent.toShortString(false, true, false, false) : "<null>");
            sb.append(" flags=0x");
            sb.append(Integer.toHexString(this.flags));
            sb.append(" u=");
            sb.append(this.userId);
            sb.append("}");
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public String typeName() {
            int i = this.type;
            if (i == 1) {
                return "broadcastIntent";
            }
            if (i == 2) {
                return "startActivity";
            }
            if (i == 3) {
                return "activityResult";
            }
            if (i == 4) {
                return "startService";
            }
            if (i != 5) {
                return Integer.toString(i);
            }
            return "startForegroundService";
        }
    }

    PendingIntentRecord(PendingIntentController _controller, Key _k, int _u) {
        this.controller = _controller;
        this.key = _k;
        this.uid = _u;
        this.ref = new WeakReference<>(this);
    }

    /* access modifiers changed from: package-private */
    public void setWhitelistDurationLocked(IBinder whitelistToken, long duration) {
        if (duration > 0) {
            if (this.whitelistDuration == null) {
                this.whitelistDuration = new ArrayMap<>();
            }
            this.whitelistDuration.put(whitelistToken, Long.valueOf(duration));
        } else {
            ArrayMap<IBinder, Long> arrayMap = this.whitelistDuration;
            if (arrayMap != null) {
                arrayMap.remove(whitelistToken);
                if (this.whitelistDuration.size() <= 0) {
                    this.whitelistDuration = null;
                }
            }
        }
        this.stringName = null;
    }

    /* access modifiers changed from: package-private */
    public void setAllowBgActivityStarts(IBinder token, int flags) {
        if (token != null) {
            if ((flags & 1) != 0) {
                this.mAllowBgActivityStartsForActivitySender.add(token);
            }
            if ((flags & 2) != 0) {
                this.mAllowBgActivityStartsForBroadcastSender.add(token);
            }
            if ((flags & 4) != 0) {
                this.mAllowBgActivityStartsForServiceSender.add(token);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void clearAllowBgActivityStarts(IBinder token) {
        if (token != null) {
            this.mAllowBgActivityStartsForActivitySender.remove(token);
            this.mAllowBgActivityStartsForBroadcastSender.remove(token);
            this.mAllowBgActivityStartsForServiceSender.remove(token);
        }
    }

    public void registerCancelListenerLocked(IResultReceiver receiver) {
        if (this.mCancelCallbacks == null) {
            this.mCancelCallbacks = new RemoteCallbackList<>();
        }
        this.mCancelCallbacks.register(receiver);
    }

    public void unregisterCancelListenerLocked(IResultReceiver receiver) {
        RemoteCallbackList<IResultReceiver> remoteCallbackList = this.mCancelCallbacks;
        if (remoteCallbackList != null) {
            remoteCallbackList.unregister(receiver);
            if (this.mCancelCallbacks.getRegisteredCallbackCount() <= 0) {
                this.mCancelCallbacks = null;
            }
        }
    }

    public RemoteCallbackList<IResultReceiver> detachCancelListenersLocked() {
        RemoteCallbackList<IResultReceiver> listeners = this.mCancelCallbacks;
        this.mCancelCallbacks = null;
        return listeners;
    }

    public void send(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
        sendInner(code, intent, resolvedType, whitelistToken, finishedReceiver, requiredPermission, (IBinder) null, (String) null, 0, 0, 0, options);
    }

    public int sendWithResult(int code, Intent intent, String resolvedType, IBinder whitelistToken, IIntentReceiver finishedReceiver, String requiredPermission, Bundle options) {
        return sendInner(code, intent, resolvedType, whitelistToken, finishedReceiver, requiredPermission, (IBinder) null, (String) null, 0, 0, 0, options);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:105:0x0164, code lost:
        r11 = android.os.Binder.getCallingUid();
        r31 = android.os.Binder.getCallingPid();
        r32 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0172, code lost:
        if (r28 == null) goto L_0x0203;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:?, code lost:
        r1 = r15.controller.mAmInternal.getUidProcessState(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x0180, code lost:
        if (android.app.ActivityManager.isProcStateBackground(r1) != false) goto L_0x01de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x0182, code lost:
        r2 = new java.lang.StringBuilder(64);
        r2.append("pendingintent:");
        android.os.UserHandle.formatUid(r2, r11);
        r2.append(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x019b, code lost:
        if (r10.getAction() == null) goto L_0x01a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x019d, code lost:
        r2.append(r10.getAction());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x01a9, code lost:
        if (r10.getComponent() == null) goto L_0x01b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x01ab, code lost:
        r10.getComponent().appendShortString(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x01b7, code lost:
        if (r10.getData() == null) goto L_0x01c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x01b9, code lost:
        r2.append(r10.getData().toSafeString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x01c4, code lost:
        r15.controller.mAmInternal.tempWhitelistForPendingIntent(r31, r11, r15.uid, r28.longValue(), r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01de, code lost:
        android.util.Slog.w(TAG, "Not doing whitelist " + r15 + ": caller state=" + r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01fd, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01fe, code lost:
        r20 = r11;
        r11 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0203, code lost:
        if (r43 == null) goto L_0x0207;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0205, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x0207, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x0208, code lost:
        r35 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:?, code lost:
        r1 = r15.key.userId;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x020f, code lost:
        if (r1 != -2) goto L_0x021d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0219, code lost:
        r36 = r15.controller.mUserController.getCurrentOrTargetUserId();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x021d, code lost:
        r36 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0221, code lost:
        if (r15.uid == r11) goto L_0x022f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x022b, code lost:
        if (r15.controller.mAtmInternal.isUidForeground(r11) == false) goto L_0x022f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x022d, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x022f, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0230, code lost:
        r37 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:?, code lost:
        r1 = r15.key.type;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0236, code lost:
        if (r1 == 1) goto L_0x0366;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x0238, code lost:
        if (r1 == 2) goto L_0x02c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x023b, code lost:
        if (r1 == 3) goto L_0x029c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x023f, code lost:
        if (r1 == 4) goto L_0x0249;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x0241, code lost:
        if (r1 == 5) goto L_0x0249;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0243, code lost:
        r19 = r10;
        r20 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:?, code lost:
        r1 = r15.controller.mAmInternal;
        r2 = r15.uid;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0253, code lost:
        if (r15.key.type != 5) goto L_0x0258;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x0255, code lost:
        r20 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0258, code lost:
        r20 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x025a, code lost:
        r3 = r15.key.packageName;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x0264, code lost:
        if (r15.mAllowBgActivityStartsForServiceSender.contains(r13) != false) goto L_0x026c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x0266, code lost:
        if (r37 == false) goto L_0x0269;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x0269, code lost:
        r23 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x026c, code lost:
        r23 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x026e, code lost:
        r1.startServiceInPackage(r2, r10, r24, r20, r3, r36, r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x027d, code lost:
        r19 = r10;
        r20 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0284, code lost:
        r19 = r10;
        r20 = r11;
        r9 = -96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x028e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:?, code lost:
        android.util.Slog.w(TAG, "Unable to send startService intent", r0);
        r19 = r10;
        r20 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x029c, code lost:
        r15.controller.mAtmInternal.sendActivityResult(-1, r15.key.activity, r15.key.who, r15.key.requestCode, r39, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02bd, code lost:
        r19 = r10;
        r20 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x02c7, code lost:
        if (r15.key.allIntents == null) goto L_0x0310;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02ce, code lost:
        if (r15.key.allIntents.length <= 1) goto L_0x0310;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x02e1, code lost:
        r19 = r10;
        r20 = r11;
        r14 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x02fc, code lost:
        r34 = r15.controller.mAtmInternal.startActivitiesInPackage(r15.uid, r31, r11, r15.key.packageName, r29, r30, r45, r27, r36, false, r38, r15.mAllowBgActivityStartsForActivitySender.contains(r13));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x02ff, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0300, code lost:
        r11 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0303, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0304, code lost:
        r20 = r11;
        r14 = r13;
        r11 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0309, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x030a, code lost:
        r19 = r10;
        r20 = r11;
        r14 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0310, code lost:
        r19 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0348, code lost:
        r34 = r15.controller.mAtmInternal.startActivityInPackage(r15.uid, r31, r11, r15.key.packageName, r19, r24, r45, r46, r47, 0, r27, r36, (com.android.server.wm.TaskRecord) null, "PendingIntentRecord", false, r38, r15.mAllowBgActivityStartsForActivitySender.contains(r13));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x034a, code lost:
        r9 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x034e, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0350, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0351, code lost:
        r20 = r11;
        r11 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0357, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0358, code lost:
        r19 = r10;
        r20 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:?, code lost:
        android.util.Slog.w(TAG, "Unable to send startActivity intent", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0366, code lost:
        r19 = r10;
        r20 = r11;
        r15 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:?, code lost:
        r1 = r15.controller.mAmInternal;
        r2 = r15.key.packageName;
        r3 = r15.uid;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0378, code lost:
        if (r43 == null) goto L_0x037c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x037a, code lost:
        r14 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x037c, code lost:
        r14 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0387, code lost:
        if (r15.mAllowBgActivityStartsForBroadcastSender.contains(r42) != false) goto L_0x038f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0389, code lost:
        if (r37 == false) goto L_0x038c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x038c, code lost:
        r17 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x038f, code lost:
        r17 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x03a9, code lost:
        if (r1.broadcastIntentInPackage(r2, r3, r20, r31, r19, r24, r43, r39, (java.lang.String) null, (android.os.Bundle) null, r44, r50, r14, false, r36, r17) != 0) goto L_0x03ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03ab, code lost:
        r35 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03ad, code lost:
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03b0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03b1, code lost:
        r11 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03b3, code lost:
        r10 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x03b7, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:?, code lost:
        android.util.Slog.w(TAG, "Unable to send startActivity intent", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x03c0, code lost:
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x03c2, code lost:
        if (r35 == false) goto L_0x03fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:?, code lost:
        r43.performReceive(new android.content.Intent(r19), 0, (java.lang.String) null, (android.os.Bundle) null, false, false, r38.key.userId);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x03e0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x03e4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03e5, code lost:
        r11 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03e7, code lost:
        r34 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x03eb, code lost:
        r11 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x03ee, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x03ef, code lost:
        r11 = r38;
        r10 = r19;
        r34 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03f7, code lost:
        r11 = r38;
        r10 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x03fc, code lost:
        r11 = r38;
        r10 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x0400, code lost:
        android.os.Binder.restoreCallingIdentity(r32);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x0404, code lost:
        return r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x0405, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x0406, code lost:
        r11 = r38;
        r10 = r19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x040b, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x040c, code lost:
        r20 = r11;
        r11 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x040f, code lost:
        android.os.Binder.restoreCallingIdentity(r32);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x0412, code lost:
        throw r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x03c8 A[SYNTHETIC, Splitter:B:232:0x03c8] */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x03fc  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int sendInner(int r39, android.content.Intent r40, java.lang.String r41, android.os.IBinder r42, android.content.IIntentReceiver r43, java.lang.String r44, android.os.IBinder r45, java.lang.String r46, int r47, int r48, int r49, android.os.Bundle r50) {
        /*
            r38 = this;
            r15 = r38
            r14 = r40
            r13 = r42
            r12 = r50
            r0 = 1
            if (r14 == 0) goto L_0x000e
            r14.setDefusable(r0)
        L_0x000e:
            if (r12 == 0) goto L_0x0013
            r12.setDefusable(r0)
        L_0x0013:
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            com.android.server.am.PendingIntentController r6 = r15.controller
            java.lang.Object r6 = r6.mLock
            monitor-enter(r6)
            boolean r7 = r15.canceled     // Catch:{ all -> 0x0447 }
            r11 = -96
            if (r7 == 0) goto L_0x002f
            monitor-exit(r6)     // Catch:{ all -> 0x0025 }
            return r11
        L_0x0025:
            r0 = move-exception
            r24 = r41
            r25 = r48
            r26 = r49
            r11 = r15
            goto L_0x0451
        L_0x002f:
            r15.sent = r0     // Catch:{ all -> 0x0447 }
            com.android.server.am.PendingIntentRecord$Key r7 = r15.key     // Catch:{ all -> 0x0447 }
            int r7 = r7.flags     // Catch:{ all -> 0x0447 }
            r8 = 1073741824(0x40000000, float:2.0)
            r7 = r7 & r8
            if (r7 == 0) goto L_0x003f
            com.android.server.am.PendingIntentController r7 = r15.controller     // Catch:{ all -> 0x0025 }
            r7.cancelIntentSender(r15, r0)     // Catch:{ all -> 0x0025 }
        L_0x003f:
            com.android.server.am.PendingIntentRecord$Key r7 = r15.key     // Catch:{ all -> 0x0447 }
            android.content.Intent r7 = r7.requestIntent     // Catch:{ all -> 0x0447 }
            if (r7 == 0) goto L_0x004f
            android.content.Intent r7 = new android.content.Intent     // Catch:{ all -> 0x0025 }
            com.android.server.am.PendingIntentRecord$Key r8 = r15.key     // Catch:{ all -> 0x0025 }
            android.content.Intent r8 = r8.requestIntent     // Catch:{ all -> 0x0025 }
            r7.<init>(r8)     // Catch:{ all -> 0x0025 }
            goto L_0x0054
        L_0x004f:
            android.content.Intent r7 = new android.content.Intent     // Catch:{ all -> 0x0447 }
            r7.<init>()     // Catch:{ all -> 0x0447 }
        L_0x0054:
            r10 = r7
            com.android.server.am.PendingIntentRecord$Key r2 = r15.key     // Catch:{ all -> 0x043b }
            int r2 = r2.flags     // Catch:{ all -> 0x043b }
            r7 = 67108864(0x4000000, float:1.5046328E-36)
            r2 = r2 & r7
            r7 = 0
            if (r2 == 0) goto L_0x0061
            r2 = r0
            goto L_0x0062
        L_0x0061:
            r2 = r7
        L_0x0062:
            if (r2 != 0) goto L_0x00bb
            if (r14 == 0) goto L_0x0086
            com.android.server.am.PendingIntentRecord$Key r8 = r15.key     // Catch:{ all -> 0x007b }
            int r8 = r8.flags     // Catch:{ all -> 0x007b }
            int r8 = r10.fillIn(r14, r8)     // Catch:{ all -> 0x007b }
            r9 = r8 & 2
            if (r9 != 0) goto L_0x0077
            com.android.server.am.PendingIntentRecord$Key r9 = r15.key     // Catch:{ all -> 0x007b }
            java.lang.String r9 = r9.requestResolvedType     // Catch:{ all -> 0x007b }
            goto L_0x0079
        L_0x0077:
            r9 = r41
        L_0x0079:
            r8 = r9
            goto L_0x008a
        L_0x007b:
            r0 = move-exception
            r24 = r41
            r25 = r48
            r26 = r49
            r2 = r10
            r11 = r15
            goto L_0x0451
        L_0x0086:
            com.android.server.am.PendingIntentRecord$Key r8 = r15.key     // Catch:{ all -> 0x00ae }
            java.lang.String r8 = r8.requestResolvedType     // Catch:{ all -> 0x00ae }
        L_0x008a:
            r9 = r48
            r9 = r9 & -196(0xffffffffffffff3c, float:NaN)
            r16 = r49 & r9
            int r17 = r10.getFlags()     // Catch:{ all -> 0x00a3 }
            int r11 = ~r9     // Catch:{ all -> 0x00a3 }
            r11 = r17 & r11
            r11 = r11 | r16
            r10.setFlags(r11)     // Catch:{ all -> 0x00a3 }
            r24 = r8
            r25 = r9
            r26 = r16
            goto L_0x00c7
        L_0x00a3:
            r0 = move-exception
            r24 = r8
            r25 = r9
            r2 = r10
            r11 = r15
            r26 = r16
            goto L_0x0451
        L_0x00ae:
            r0 = move-exception
            r9 = r48
            r24 = r41
            r26 = r49
            r25 = r9
            r2 = r10
            r11 = r15
            goto L_0x0451
        L_0x00bb:
            r9 = r48
            com.android.server.am.PendingIntentRecord$Key r8 = r15.key     // Catch:{ all -> 0x0431 }
            java.lang.String r8 = r8.requestResolvedType     // Catch:{ all -> 0x0431 }
            r26 = r49
            r24 = r8
            r25 = r9
        L_0x00c7:
            android.app.ActivityOptions r8 = android.app.ActivityOptions.fromBundle(r50)     // Catch:{ all -> 0x042d }
            if (r8 == 0) goto L_0x00da
            int r9 = r8.getPendingIntentLaunchFlags()     // Catch:{ all -> 0x00d5 }
            r10.addFlags(r9)     // Catch:{ all -> 0x00d5 }
            goto L_0x00da
        L_0x00d5:
            r0 = move-exception
            r2 = r10
            r11 = r15
            goto L_0x0451
        L_0x00da:
            com.android.server.am.PendingIntentRecord$Key r9 = r15.key     // Catch:{ all -> 0x042d }
            com.android.server.am.PendingIntentRecordInjector.preSendInner(r9, r10)     // Catch:{ all -> 0x042d }
            com.android.server.am.PendingIntentRecord$Key r9 = r15.key     // Catch:{ all -> 0x042d }
            com.android.server.wm.SafeActivityOptions r9 = r9.options     // Catch:{ all -> 0x042d }
            r5 = r9
            if (r5 != 0) goto L_0x00ef
            com.android.server.wm.SafeActivityOptions r9 = new com.android.server.wm.SafeActivityOptions     // Catch:{ all -> 0x00d5 }
            r9.<init>(r8)     // Catch:{ all -> 0x00d5 }
            r5 = r9
            r27 = r5
            goto L_0x00f4
        L_0x00ef:
            r5.setCallerOptions(r8)     // Catch:{ all -> 0x042d }
            r27 = r5
        L_0x00f4:
            android.util.ArrayMap<android.os.IBinder, java.lang.Long> r5 = r15.whitelistDuration     // Catch:{ all -> 0x0427 }
            if (r5 == 0) goto L_0x0109
            android.util.ArrayMap<android.os.IBinder, java.lang.Long> r5 = r15.whitelistDuration     // Catch:{ all -> 0x0104 }
            java.lang.Object r5 = r5.get(r13)     // Catch:{ all -> 0x0104 }
            java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ all -> 0x0104 }
            r1 = r5
            r28 = r1
            goto L_0x010b
        L_0x0104:
            r0 = move-exception
            r2 = r10
            r11 = r15
            goto L_0x042a
        L_0x0109:
            r28 = r1
        L_0x010b:
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x041f }
            int r1 = r1.type     // Catch:{ all -> 0x041f }
            r5 = 2
            if (r1 != r5) goto L_0x015f
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x015a }
            android.content.Intent[] r1 = r1.allIntents     // Catch:{ all -> 0x015a }
            if (r1 == 0) goto L_0x015f
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x015a }
            android.content.Intent[] r1 = r1.allIntents     // Catch:{ all -> 0x015a }
            int r1 = r1.length     // Catch:{ all -> 0x015a }
            if (r1 <= r0) goto L_0x015f
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x015a }
            android.content.Intent[] r1 = r1.allIntents     // Catch:{ all -> 0x015a }
            int r1 = r1.length     // Catch:{ all -> 0x015a }
            android.content.Intent[] r1 = new android.content.Intent[r1]     // Catch:{ all -> 0x015a }
            r3 = r1
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x015a }
            android.content.Intent[] r1 = r1.allIntents     // Catch:{ all -> 0x015a }
            int r1 = r1.length     // Catch:{ all -> 0x015a }
            java.lang.String[] r1 = new java.lang.String[r1]     // Catch:{ all -> 0x015a }
            r4 = r1
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x015a }
            android.content.Intent[] r1 = r1.allIntents     // Catch:{ all -> 0x015a }
            com.android.server.am.PendingIntentRecord$Key r9 = r15.key     // Catch:{ all -> 0x015a }
            android.content.Intent[] r9 = r9.allIntents     // Catch:{ all -> 0x015a }
            int r9 = r9.length     // Catch:{ all -> 0x015a }
            java.lang.System.arraycopy(r1, r7, r3, r7, r9)     // Catch:{ all -> 0x015a }
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x015a }
            java.lang.String[] r1 = r1.allResolvedTypes     // Catch:{ all -> 0x015a }
            if (r1 == 0) goto L_0x014d
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x015a }
            java.lang.String[] r1 = r1.allResolvedTypes     // Catch:{ all -> 0x015a }
            com.android.server.am.PendingIntentRecord$Key r9 = r15.key     // Catch:{ all -> 0x015a }
            java.lang.String[] r9 = r9.allResolvedTypes     // Catch:{ all -> 0x015a }
            int r9 = r9.length     // Catch:{ all -> 0x015a }
            java.lang.System.arraycopy(r1, r7, r4, r7, r9)     // Catch:{ all -> 0x015a }
        L_0x014d:
            int r1 = r3.length     // Catch:{ all -> 0x015a }
            int r1 = r1 - r0
            r3[r1] = r10     // Catch:{ all -> 0x015a }
            int r1 = r4.length     // Catch:{ all -> 0x015a }
            int r1 = r1 - r0
            r4[r1] = r24     // Catch:{ all -> 0x015a }
            r29 = r3
            r30 = r4
            goto L_0x0163
        L_0x015a:
            r0 = move-exception
            r2 = r10
            r11 = r15
            goto L_0x0422
        L_0x015f:
            r29 = r3
            r30 = r4
        L_0x0163:
            monitor-exit(r6)     // Catch:{ all -> 0x0413 }
            int r11 = android.os.Binder.getCallingUid()
            int r31 = android.os.Binder.getCallingPid()
            long r32 = android.os.Binder.clearCallingIdentity()
            r34 = 0
            if (r28 == 0) goto L_0x0203
            com.android.server.am.PendingIntentController r1 = r15.controller     // Catch:{ all -> 0x01fd }
            android.app.ActivityManagerInternal r1 = r1.mAmInternal     // Catch:{ all -> 0x01fd }
            int r1 = r1.getUidProcessState(r11)     // Catch:{ all -> 0x01fd }
            boolean r2 = android.app.ActivityManager.isProcStateBackground(r1)     // Catch:{ all -> 0x01fd }
            if (r2 != 0) goto L_0x01de
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01fd }
            r3 = 64
            r2.<init>(r3)     // Catch:{ all -> 0x01fd }
            java.lang.String r3 = "pendingintent:"
            r2.append(r3)     // Catch:{ all -> 0x01fd }
            android.os.UserHandle.formatUid(r2, r11)     // Catch:{ all -> 0x01fd }
            java.lang.String r3 = ":"
            r2.append(r3)     // Catch:{ all -> 0x01fd }
            java.lang.String r3 = r10.getAction()     // Catch:{ all -> 0x01fd }
            if (r3 == 0) goto L_0x01a5
            java.lang.String r3 = r10.getAction()     // Catch:{ all -> 0x01fd }
            r2.append(r3)     // Catch:{ all -> 0x01fd }
            goto L_0x01c4
        L_0x01a5:
            android.content.ComponentName r3 = r10.getComponent()     // Catch:{ all -> 0x01fd }
            if (r3 == 0) goto L_0x01b3
            android.content.ComponentName r3 = r10.getComponent()     // Catch:{ all -> 0x01fd }
            r3.appendShortString(r2)     // Catch:{ all -> 0x01fd }
            goto L_0x01c4
        L_0x01b3:
            android.net.Uri r3 = r10.getData()     // Catch:{ all -> 0x01fd }
            if (r3 == 0) goto L_0x01c4
            android.net.Uri r3 = r10.getData()     // Catch:{ all -> 0x01fd }
            java.lang.String r3 = r3.toSafeString()     // Catch:{ all -> 0x01fd }
            r2.append(r3)     // Catch:{ all -> 0x01fd }
        L_0x01c4:
            com.android.server.am.PendingIntentController r3 = r15.controller     // Catch:{ all -> 0x01fd }
            android.app.ActivityManagerInternal r3 = r3.mAmInternal     // Catch:{ all -> 0x01fd }
            int r4 = r15.uid     // Catch:{ all -> 0x01fd }
            long r20 = r28.longValue()     // Catch:{ all -> 0x01fd }
            java.lang.String r22 = r2.toString()     // Catch:{ all -> 0x01fd }
            r16 = r3
            r17 = r31
            r18 = r11
            r19 = r4
            r16.tempWhitelistForPendingIntent(r17, r18, r19, r20, r22)     // Catch:{ all -> 0x01fd }
            goto L_0x0203
        L_0x01de:
            java.lang.String r2 = "ActivityManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x01fd }
            r3.<init>()     // Catch:{ all -> 0x01fd }
            java.lang.String r4 = "Not doing whitelist "
            r3.append(r4)     // Catch:{ all -> 0x01fd }
            r3.append(r15)     // Catch:{ all -> 0x01fd }
            java.lang.String r4 = ": caller state="
            r3.append(r4)     // Catch:{ all -> 0x01fd }
            r3.append(r1)     // Catch:{ all -> 0x01fd }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01fd }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x01fd }
            goto L_0x0203
        L_0x01fd:
            r0 = move-exception
            r20 = r11
            r11 = r15
            goto L_0x040f
        L_0x0203:
            if (r43 == 0) goto L_0x0207
            r1 = r0
            goto L_0x0208
        L_0x0207:
            r1 = r7
        L_0x0208:
            r35 = r1
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x040b }
            int r1 = r1.userId     // Catch:{ all -> 0x040b }
            r2 = -2
            if (r1 != r2) goto L_0x021d
            com.android.server.am.PendingIntentController r2 = r15.controller     // Catch:{ all -> 0x01fd }
            com.android.server.am.UserController r2 = r2.mUserController     // Catch:{ all -> 0x01fd }
            int r2 = r2.getCurrentOrTargetUserId()     // Catch:{ all -> 0x01fd }
            r1 = r2
            r36 = r1
            goto L_0x021f
        L_0x021d:
            r36 = r1
        L_0x021f:
            int r1 = r15.uid     // Catch:{ all -> 0x040b }
            if (r1 == r11) goto L_0x022f
            com.android.server.am.PendingIntentController r1 = r15.controller     // Catch:{ all -> 0x01fd }
            com.android.server.wm.ActivityTaskManagerInternal r1 = r1.mAtmInternal     // Catch:{ all -> 0x01fd }
            boolean r1 = r1.isUidForeground(r11)     // Catch:{ all -> 0x01fd }
            if (r1 == 0) goto L_0x022f
            r1 = r0
            goto L_0x0230
        L_0x022f:
            r1 = r7
        L_0x0230:
            r37 = r1
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x040b }
            int r1 = r1.type     // Catch:{ all -> 0x040b }
            if (r1 == r0) goto L_0x0366
            if (r1 == r5) goto L_0x02c3
            r2 = 3
            if (r1 == r2) goto L_0x029c
            r2 = 4
            r3 = 5
            if (r1 == r2) goto L_0x0249
            if (r1 == r3) goto L_0x0249
            r19 = r10
            r20 = r11
            goto L_0x03c0
        L_0x0249:
            com.android.server.am.PendingIntentController r1 = r15.controller     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            android.app.ActivityManagerInternal r1 = r1.mAmInternal     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            int r2 = r15.uid     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            com.android.server.am.PendingIntentRecord$Key r4 = r15.key     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            int r4 = r4.type     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            if (r4 != r3) goto L_0x0258
            r20 = r0
            goto L_0x025a
        L_0x0258:
            r20 = r7
        L_0x025a:
            com.android.server.am.PendingIntentRecord$Key r3 = r15.key     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            java.lang.String r3 = r3.packageName     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            android.util.ArraySet<android.os.IBinder> r4 = r15.mAllowBgActivityStartsForServiceSender     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            boolean r4 = r4.contains(r13)     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            if (r4 != 0) goto L_0x026c
            if (r37 == 0) goto L_0x0269
            goto L_0x026c
        L_0x0269:
            r23 = r7
            goto L_0x026e
        L_0x026c:
            r23 = r0
        L_0x026e:
            r16 = r1
            r17 = r2
            r18 = r10
            r19 = r24
            r21 = r3
            r22 = r36
            r16.startServiceInPackage(r17, r18, r19, r20, r21, r22, r23)     // Catch:{ RuntimeException -> 0x028e, TransactionTooLargeException -> 0x0283 }
            r19 = r10
            r20 = r11
            goto L_0x03c0
        L_0x0283:
            r0 = move-exception
            r34 = -96
            r19 = r10
            r20 = r11
            r9 = r34
            goto L_0x03c2
        L_0x028e:
            r0 = move-exception
            java.lang.String r1 = "ActivityManager"
            java.lang.String r2 = "Unable to send startService intent"
            android.util.Slog.w(r1, r2, r0)     // Catch:{ all -> 0x01fd }
            r19 = r10
            r20 = r11
            goto L_0x03c0
        L_0x029c:
            com.android.server.am.PendingIntentController r0 = r15.controller     // Catch:{ all -> 0x01fd }
            com.android.server.wm.ActivityTaskManagerInternal r0 = r0.mAtmInternal     // Catch:{ all -> 0x01fd }
            r17 = -1
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ all -> 0x01fd }
            android.os.IBinder r1 = r1.activity     // Catch:{ all -> 0x01fd }
            com.android.server.am.PendingIntentRecord$Key r2 = r15.key     // Catch:{ all -> 0x01fd }
            java.lang.String r2 = r2.who     // Catch:{ all -> 0x01fd }
            com.android.server.am.PendingIntentRecord$Key r3 = r15.key     // Catch:{ all -> 0x01fd }
            int r3 = r3.requestCode     // Catch:{ all -> 0x01fd }
            r16 = r0
            r18 = r1
            r19 = r2
            r20 = r3
            r21 = r39
            r22 = r10
            r16.sendActivityResult(r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x01fd }
            r19 = r10
            r20 = r11
            goto L_0x03c0
        L_0x02c3:
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ RuntimeException -> 0x0357, all -> 0x0350 }
            android.content.Intent[] r1 = r1.allIntents     // Catch:{ RuntimeException -> 0x0357, all -> 0x0350 }
            if (r1 == 0) goto L_0x0310
            com.android.server.am.PendingIntentRecord$Key r1 = r15.key     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            android.content.Intent[] r1 = r1.allIntents     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            int r1 = r1.length     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            if (r1 <= r0) goto L_0x0310
            com.android.server.am.PendingIntentController r0 = r15.controller     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            com.android.server.wm.ActivityTaskManagerInternal r1 = r0.mAtmInternal     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            int r2 = r15.uid     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            com.android.server.am.PendingIntentRecord$Key r0 = r15.key     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            java.lang.String r5 = r0.packageName     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            r0 = 0
            android.util.ArraySet<android.os.IBinder> r3 = r15.mAllowBgActivityStartsForActivitySender     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            boolean r16 = r3.contains(r13)     // Catch:{ RuntimeException -> 0x0309, all -> 0x0303 }
            r3 = r31
            r4 = r11
            r6 = r29
            r7 = r30
            r8 = r45
            r9 = r27
            r19 = r10
            r10 = r36
            r20 = r11
            r11 = r0
            r12 = r38
            r14 = r13
            r13 = r16
            int r0 = r1.startActivitiesInPackage(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ RuntimeException -> 0x034e, all -> 0x02ff }
            r34 = r0
            goto L_0x034a
        L_0x02ff:
            r0 = move-exception
            r11 = r15
            goto L_0x03b3
        L_0x0303:
            r0 = move-exception
            r20 = r11
            r14 = r13
            r11 = r15
            goto L_0x0355
        L_0x0309:
            r0 = move-exception
            r19 = r10
            r20 = r11
            r14 = r13
            goto L_0x035c
        L_0x0310:
            r19 = r10
            r20 = r11
            r14 = r13
            com.android.server.am.PendingIntentController r0 = r15.controller     // Catch:{ RuntimeException -> 0x034e }
            com.android.server.wm.ActivityTaskManagerInternal r1 = r0.mAtmInternal     // Catch:{ RuntimeException -> 0x034e }
            int r2 = r15.uid     // Catch:{ RuntimeException -> 0x034e }
            com.android.server.am.PendingIntentRecord$Key r0 = r15.key     // Catch:{ RuntimeException -> 0x034e }
            java.lang.String r5 = r0.packageName     // Catch:{ RuntimeException -> 0x034e }
            r11 = 0
            r0 = 0
            java.lang.String r16 = "PendingIntentRecord"
            r17 = 0
            android.util.ArraySet<android.os.IBinder> r3 = r15.mAllowBgActivityStartsForActivitySender     // Catch:{ RuntimeException -> 0x034e }
            boolean r18 = r3.contains(r14)     // Catch:{ RuntimeException -> 0x034e }
            r3 = r31
            r4 = r20
            r6 = r19
            r7 = r24
            r8 = r45
            r9 = r46
            r10 = r47
            r12 = r27
            r13 = r36
            r14 = r0
            r15 = r16
            r16 = r17
            r17 = r38
            int r0 = r1.startActivityInPackage(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)     // Catch:{ RuntimeException -> 0x034e }
            r34 = r0
        L_0x034a:
            r9 = r34
            goto L_0x03c2
        L_0x034e:
            r0 = move-exception
            goto L_0x035c
        L_0x0350:
            r0 = move-exception
            r20 = r11
            r11 = r38
        L_0x0355:
            goto L_0x040f
        L_0x0357:
            r0 = move-exception
            r19 = r10
            r20 = r11
        L_0x035c:
            java.lang.String r1 = "ActivityManager"
            java.lang.String r2 = "Unable to send startActivity intent"
            android.util.Slog.w(r1, r2, r0)     // Catch:{ all -> 0x03b0 }
            goto L_0x03c0
        L_0x0366:
            r19 = r10
            r20 = r11
            r15 = r38
            com.android.server.am.PendingIntentController r1 = r15.controller     // Catch:{ RuntimeException -> 0x03b7 }
            android.app.ActivityManagerInternal r1 = r1.mAmInternal     // Catch:{ RuntimeException -> 0x03b7 }
            com.android.server.am.PendingIntentRecord$Key r2 = r15.key     // Catch:{ RuntimeException -> 0x03b7 }
            java.lang.String r2 = r2.packageName     // Catch:{ RuntimeException -> 0x03b7 }
            int r3 = r15.uid     // Catch:{ RuntimeException -> 0x03b7 }
            r10 = 0
            r11 = 0
            if (r43 == 0) goto L_0x037c
            r14 = r0
            goto L_0x037d
        L_0x037c:
            r14 = r7
        L_0x037d:
            r16 = 0
            android.util.ArraySet<android.os.IBinder> r4 = r15.mAllowBgActivityStartsForBroadcastSender     // Catch:{ RuntimeException -> 0x03b7 }
            r13 = r42
            boolean r4 = r4.contains(r13)     // Catch:{ RuntimeException -> 0x03b7 }
            if (r4 != 0) goto L_0x038f
            if (r37 == 0) goto L_0x038c
            goto L_0x038f
        L_0x038c:
            r17 = r7
            goto L_0x0391
        L_0x038f:
            r17 = r0
        L_0x0391:
            r4 = r20
            r5 = r31
            r6 = r19
            r7 = r24
            r8 = r43
            r9 = r39
            r12 = r44
            r13 = r50
            r15 = r16
            r16 = r36
            int r0 = r1.broadcastIntentInPackage(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)     // Catch:{ RuntimeException -> 0x03b7 }
            if (r0 != 0) goto L_0x03ad
            r35 = 0
        L_0x03ad:
            r9 = r34
            goto L_0x03c2
        L_0x03b0:
            r0 = move-exception
            r11 = r38
        L_0x03b3:
            r10 = r19
            goto L_0x040f
        L_0x03b7:
            r0 = move-exception
            java.lang.String r1 = "ActivityManager"
            java.lang.String r2 = "Unable to send startActivity intent"
            android.util.Slog.w(r1, r2, r0)     // Catch:{ all -> 0x0405 }
        L_0x03c0:
            r9 = r34
        L_0x03c2:
            if (r35 == 0) goto L_0x03fc
            r1 = -96
            if (r9 == r1) goto L_0x03fc
            android.content.Intent r2 = new android.content.Intent     // Catch:{ RemoteException -> 0x03f6, all -> 0x03ee }
            r10 = r19
            r2.<init>(r10)     // Catch:{ RemoteException -> 0x03ea, all -> 0x03e4 }
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r11 = r38
            com.android.server.am.PendingIntentRecord$Key r0 = r11.key     // Catch:{ RemoteException -> 0x03e2, all -> 0x03e0 }
            int r8 = r0.userId     // Catch:{ RemoteException -> 0x03e2, all -> 0x03e0 }
            r1 = r43
            r1.performReceive(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ RemoteException -> 0x03e2, all -> 0x03e0 }
            goto L_0x0400
        L_0x03e0:
            r0 = move-exception
            goto L_0x03e7
        L_0x03e2:
            r0 = move-exception
            goto L_0x0400
        L_0x03e4:
            r0 = move-exception
            r11 = r38
        L_0x03e7:
            r34 = r9
            goto L_0x040f
        L_0x03ea:
            r0 = move-exception
            r11 = r38
            goto L_0x0400
        L_0x03ee:
            r0 = move-exception
            r11 = r38
            r10 = r19
            r34 = r9
            goto L_0x040f
        L_0x03f6:
            r0 = move-exception
            r11 = r38
            r10 = r19
            goto L_0x0400
        L_0x03fc:
            r11 = r38
            r10 = r19
        L_0x0400:
            android.os.Binder.restoreCallingIdentity(r32)
            return r9
        L_0x0405:
            r0 = move-exception
            r11 = r38
            r10 = r19
            goto L_0x040f
        L_0x040b:
            r0 = move-exception
            r20 = r11
            r11 = r15
        L_0x040f:
            android.os.Binder.restoreCallingIdentity(r32)
            throw r0
        L_0x0413:
            r0 = move-exception
            r11 = r15
            r2 = r10
            r5 = r27
            r1 = r28
            r3 = r29
            r4 = r30
            goto L_0x0451
        L_0x041f:
            r0 = move-exception
            r11 = r15
            r2 = r10
        L_0x0422:
            r5 = r27
            r1 = r28
            goto L_0x0451
        L_0x0427:
            r0 = move-exception
            r11 = r15
            r2 = r10
        L_0x042a:
            r5 = r27
            goto L_0x0451
        L_0x042d:
            r0 = move-exception
            r11 = r15
            r2 = r10
            goto L_0x0451
        L_0x0431:
            r0 = move-exception
            r11 = r15
            r24 = r41
            r26 = r49
            r25 = r9
            r2 = r10
            goto L_0x0451
        L_0x043b:
            r0 = move-exception
            r9 = r48
            r11 = r15
            r24 = r41
            r26 = r49
            r25 = r9
            r2 = r10
            goto L_0x0451
        L_0x0447:
            r0 = move-exception
            r9 = r48
            r11 = r15
            r24 = r41
            r26 = r49
            r25 = r9
        L_0x0451:
            monitor-exit(r6)     // Catch:{ all -> 0x0453 }
            throw r0
        L_0x0453:
            r0 = move-exception
            goto L_0x0451
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.PendingIntentRecord.sendInner(int, android.content.Intent, java.lang.String, android.os.IBinder, android.content.IIntentReceiver, java.lang.String, android.os.IBinder, java.lang.String, int, int, int, android.os.Bundle):int");
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            if (!this.canceled) {
                this.controller.mH.sendMessage(PooledLambda.obtainMessage($$Lambda$PendingIntentRecord$hlEHdgdG_SS5n3v7IRr7e6QZgLQ.INSTANCE, this));
            }
        } finally {
            PendingIntentRecord.super.finalize();
        }
    }

    /* access modifiers changed from: private */
    public void completeFinalize() {
        synchronized (this.controller.mLock) {
            if (this.controller.mIntentSenderRecords.get(this.key) == this.ref) {
                this.controller.mIntentSenderRecords.remove(this.key);
            }
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.print(prefix);
        pw.print("uid=");
        pw.print(this.uid);
        pw.print(" packageName=");
        pw.print(this.key.packageName);
        pw.print(" type=");
        pw.print(this.key.typeName());
        pw.print(" flags=0x");
        pw.println(Integer.toHexString(this.key.flags));
        if (!(this.key.activity == null && this.key.who == null)) {
            pw.print(prefix);
            pw.print("activity=");
            pw.print(this.key.activity);
            pw.print(" who=");
            pw.println(this.key.who);
        }
        if (!(this.key.requestCode == 0 && this.key.requestResolvedType == null)) {
            pw.print(prefix);
            pw.print("requestCode=");
            pw.print(this.key.requestCode);
            pw.print(" requestResolvedType=");
            pw.println(this.key.requestResolvedType);
        }
        if (this.key.requestIntent != null) {
            pw.print(prefix);
            pw.print("requestIntent=");
            pw.println(this.key.requestIntent.toShortString(false, true, true, true));
        }
        if (this.sent || this.canceled) {
            pw.print(prefix);
            pw.print("sent=");
            pw.print(this.sent);
            pw.print(" canceled=");
            pw.println(this.canceled);
        }
        if (this.whitelistDuration != null) {
            pw.print(prefix);
            pw.print("whitelistDuration=");
            for (int i = 0; i < this.whitelistDuration.size(); i++) {
                if (i != 0) {
                    pw.print(", ");
                }
                pw.print(Integer.toHexString(System.identityHashCode(this.whitelistDuration.keyAt(i))));
                pw.print(":");
                TimeUtils.formatDuration(this.whitelistDuration.valueAt(i).longValue(), pw);
            }
            pw.println();
        }
        if (this.mCancelCallbacks != null) {
            pw.print(prefix);
            pw.println("mCancelCallbacks:");
            for (int i2 = 0; i2 < this.mCancelCallbacks.getRegisteredCallbackCount(); i2++) {
                pw.print(prefix);
                pw.print("  #");
                pw.print(i2);
                pw.print(": ");
                pw.println(this.mCancelCallbacks.getRegisteredCallbackItem(i2));
            }
        }
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("PendingIntentRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.key.packageName);
        sb.append(' ');
        sb.append(this.key.typeName());
        if (this.whitelistDuration != null) {
            sb.append(" (whitelist: ");
            for (int i = 0; i < this.whitelistDuration.size(); i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(Integer.toHexString(System.identityHashCode(this.whitelistDuration.keyAt(i))));
                sb.append(":");
                TimeUtils.formatDuration(this.whitelistDuration.valueAt(i).longValue(), sb);
            }
            sb.append(")");
        }
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }
}
