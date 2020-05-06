package com.miui.server;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.miui.Manifest;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.SmsApplication;
import miui.provider.ExtraTelephony;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;

class SecuritySmsHandler {
    private Context mContext;
    private Handler mHandler;
    private String mInterceptSmsCallerPkgName = null;
    private int mInterceptSmsCallerUid = 0;
    private int mInterceptSmsCount = 0;
    private Object mInterceptSmsLock = new Object();
    private String mInterceptSmsSenderNum = null;
    private BroadcastReceiver mInterceptedSmsResultReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int slotId = SecuritySmsHandler.getSlotIdFromIntent(intent);
            Log.i("SecurityManagerService", "mInterceptedSmsResultReceiver sms dispatched, action:" + action);
            if ("android.provider.Telephony.SMS_RECEIVED".equals(action) && getResultCode() == -1) {
                Log.i("SecurityManagerService", "mInterceptedSmsResultReceiver SMS_RECEIVED_ACTION not aborted");
                SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                StringBuilder sb = new StringBuilder();
                for (SmsMessage displayMessageBody : msgs) {
                    sb.append(displayMessageBody.getDisplayMessageBody());
                }
                int blockType = SecuritySmsHandler.this.checkByAntiSpam(msgs[0].getOriginatingAddress(), sb.toString(), slotId);
                if (blockType != 0) {
                    intent.putExtra("blockType", blockType);
                    if (ExtraTelephony.getRealBlockType(blockType) >= 3) {
                        Log.i("SecurityManagerService", "mInterceptedSmsResultReceiver: This sms is intercepted by AntiSpam");
                        SecuritySmsHandler.this.dispatchSmsToAntiSpam(intent);
                        return;
                    }
                    SecuritySmsHandler.this.dispatchNormalSms(intent);
                }
            }
        }
    };
    private BroadcastReceiver mNormalMsgResultReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("SecurityManagerService", "mNormalMsgResultReceiver sms dispatched, action:" + action);
            if ("android.provider.Telephony.SMS_DELIVER".equals(action)) {
                intent.setComponent((ComponentName) null);
                intent.setFlags(134217728);
                intent.setAction("android.provider.Telephony.SMS_RECEIVED");
                SecuritySmsHandler.this.dispatchIntent(intent, "android.permission.RECEIVE_SMS", 16, (BroadcastReceiver) null);
                Log.i("SecurityManagerService", "mNormalMsgResultReceiver dispatch SMS_RECEIVED_ACTION");
            } else if ("android.provider.Telephony.WAP_PUSH_DELIVER".equals(action)) {
                intent.setComponent((ComponentName) null);
                intent.setFlags(134217728);
                intent.setAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
                SecuritySmsHandler.this.dispatchIntent(intent, "android.permission.RECEIVE_SMS", 16, (BroadcastReceiver) null);
                Log.i("SecurityManagerService", "mNormalMsgResultReceiver dispatch WAP_PUSH_RECEIVED_ACTION");
            }
        }
    };

    SecuritySmsHandler(Context context, Handler handler) {
        this.mHandler = handler;
        this.mContext = context;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x016a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkSmsBlocked(android.content.Intent r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            java.lang.String r3 = "SecurityManagerService"
            java.lang.String r0 = "enter checkSmsBlocked"
            android.util.Log.i(r3, r0)
            r4 = 0
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r0 >= r5) goto L_0x0013
            return r4
        L_0x0013:
            java.lang.String r5 = r25.getAction()
            int r6 = getSlotIdFromIntent(r25)
            java.lang.String r0 = "android.provider.Telephony.SMS_DELIVER"
            boolean r0 = r5.equals(r0)
            r7 = 3
            java.lang.String r8 = "blockType"
            r9 = 0
            if (r0 == 0) goto L_0x0077
            android.telephony.SmsMessage[] r0 = android.provider.Telephony.Sms.Intents.getMessagesFromIntent(r25)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r11 = 0
        L_0x0031:
            int r12 = r0.length
            if (r11 >= r12) goto L_0x0040
            r12 = r0[r11]
            java.lang.String r12 = r12.getDisplayMessageBody()
            r10.append(r12)
            int r11 = r11 + 1
            goto L_0x0031
        L_0x0040:
            r9 = r0[r9]
            java.lang.String r9 = r9.getOriginatingAddress()
            java.lang.String r11 = r10.toString()
            boolean r12 = r1.checkWithInterceptedSender(r9)
            if (r12 == 0) goto L_0x0059
            java.lang.String r12 = "Intercepted by sender address"
            android.util.Log.i(r3, r12)
            r24.dispatchToInterceptApp(r25)
            r4 = 1
        L_0x0059:
            if (r4 != 0) goto L_0x0073
            int r12 = r1.checkByAntiSpam(r9, r11, r6)
            if (r12 == 0) goto L_0x0073
            r2.putExtra(r8, r12)
            int r8 = miui.provider.ExtraTelephony.getRealBlockType(r12)
            if (r8 < r7) goto L_0x0073
            java.lang.String r7 = "This sms is intercepted by AntiSpam"
            android.util.Log.i(r3, r7)
            r24.dispatchSmsToAntiSpam(r25)
            r4 = 1
        L_0x0073:
            r21 = r5
            goto L_0x0184
        L_0x0077:
            java.lang.String r0 = "android.provider.Telephony.WAP_PUSH_DELIVER"
            boolean r0 = r5.equals(r0)
            if (r0 == 0) goto L_0x017e
            java.lang.String r0 = "data"
            byte[] r10 = r2.getByteArrayExtra(r0)
            android.content.res.Resources r0 = android.content.res.Resources.getSystem()
            r11 = 17891482(0x111009a, float:2.6632725E-38)
            boolean r11 = r0.getBoolean(r11)
            java.lang.String r12 = ""
            java.lang.String r0 = "com.google.android.mms.pdu.PduParser"
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.String r14 = "com.google.android.mms.pdu.GenericPdu"
            java.lang.Class r14 = java.lang.Class.forName(r14)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.String r15 = "com.google.android.mms.pdu.EncodedStringValue"
            java.lang.Class r15 = java.lang.Class.forName(r15)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.String r16 = "com.google.android.mms.pdu.PduPersister"
            java.lang.Class r16 = java.lang.Class.forName(r16)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            r17 = r16
            r7 = 2
            java.lang.Class[] r13 = new java.lang.Class[r7]     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.Class<byte[]> r18 = byte[].class
            r13[r9] = r18     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.Class r18 = java.lang.Boolean.TYPE     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            r9 = 1
            r13[r9] = r18     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.reflect.Constructor r13 = r0.getConstructor(r13)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            r18 = 0
            r7[r18] = r10     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.Boolean r18 = java.lang.Boolean.valueOf(r11)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            r7[r9] = r18     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.Object r7 = r13.newInstance(r7)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.String r13 = "parse"
            r9 = 0
            java.lang.reflect.Method r13 = r0.getMethod(r13, r9)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.Object r13 = r13.invoke(r7, r9)     // Catch:{ InstantiationException -> 0x015a, IllegalAccessException -> 0x0151, InvocationTargetException -> 0x0148, NoSuchMethodException -> 0x013f, ClassNotFoundException -> 0x0136 }
            java.lang.String r9 = "getFrom"
            r20 = r4
            r4 = 0
            java.lang.reflect.Method r9 = r14.getMethod(r9, r4)     // Catch:{ InstantiationException -> 0x0132, IllegalAccessException -> 0x012e, InvocationTargetException -> 0x012a, NoSuchMethodException -> 0x0126, ClassNotFoundException -> 0x0122 }
            java.lang.Object r9 = r9.invoke(r13, r4)     // Catch:{ InstantiationException -> 0x0132, IllegalAccessException -> 0x012e, InvocationTargetException -> 0x012a, NoSuchMethodException -> 0x0126, ClassNotFoundException -> 0x0122 }
            java.lang.String r4 = "getTextString"
            r21 = r5
            r5 = 0
            java.lang.reflect.Method r4 = r15.getMethod(r4, r5)     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            java.lang.Object r4 = r4.invoke(r9, r5)     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            byte[] r4 = (byte[]) r4     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            java.lang.String r5 = "toIsoString"
            r22 = r0
            r23 = r7
            r0 = 1
            java.lang.Class[] r7 = new java.lang.Class[r0]     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            java.lang.Class<byte[]> r0 = byte[].class
            r19 = 0
            r7[r19] = r0     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            r0 = r17
            java.lang.reflect.Method r5 = r0.getMethod(r5, r7)     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            r7 = 1
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            r7[r19] = r4     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            r17 = r4
            r4 = 0
            java.lang.Object r5 = r5.invoke(r4, r7)     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ InstantiationException -> 0x0120, IllegalAccessException -> 0x011e, InvocationTargetException -> 0x011c, NoSuchMethodException -> 0x011a, ClassNotFoundException -> 0x0118 }
            r12 = r5
        L_0x0117:
            goto L_0x0163
        L_0x0118:
            r0 = move-exception
            goto L_0x013b
        L_0x011a:
            r0 = move-exception
            goto L_0x0144
        L_0x011c:
            r0 = move-exception
            goto L_0x014d
        L_0x011e:
            r0 = move-exception
            goto L_0x0156
        L_0x0120:
            r0 = move-exception
            goto L_0x015f
        L_0x0122:
            r0 = move-exception
            r21 = r5
            goto L_0x013b
        L_0x0126:
            r0 = move-exception
            r21 = r5
            goto L_0x0144
        L_0x012a:
            r0 = move-exception
            r21 = r5
            goto L_0x014d
        L_0x012e:
            r0 = move-exception
            r21 = r5
            goto L_0x0156
        L_0x0132:
            r0 = move-exception
            r21 = r5
            goto L_0x015f
        L_0x0136:
            r0 = move-exception
            r20 = r4
            r21 = r5
        L_0x013b:
            r0.printStackTrace()
            goto L_0x0163
        L_0x013f:
            r0 = move-exception
            r20 = r4
            r21 = r5
        L_0x0144:
            r0.printStackTrace()
            goto L_0x0117
        L_0x0148:
            r0 = move-exception
            r20 = r4
            r21 = r5
        L_0x014d:
            r0.printStackTrace()
            goto L_0x0117
        L_0x0151:
            r0 = move-exception
            r20 = r4
            r21 = r5
        L_0x0156:
            r0.printStackTrace()
            goto L_0x0117
        L_0x015a:
            r0 = move-exception
            r20 = r4
            r21 = r5
        L_0x015f:
            r0.printStackTrace()
            goto L_0x0117
        L_0x0163:
            r4 = 0
            int r0 = r1.checkByAntiSpam(r12, r4, r6)
            if (r0 == 0) goto L_0x0182
            r2.putExtra(r8, r0)
            int r4 = miui.provider.ExtraTelephony.getRealBlockType(r0)
            r5 = 3
            if (r4 < r5) goto L_0x0182
            java.lang.String r4 = "This mms is intercepted by AntiSpam"
            android.util.Log.i(r3, r4)
            r24.dispatchMmsToAntiSpam(r25)
            r4 = 1
            goto L_0x0184
        L_0x017e:
            r20 = r4
            r21 = r5
        L_0x0182:
            r4 = r20
        L_0x0184:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "leave checkSmsBlocked, blocked:"
            r0.append(r5)
            java.lang.String r5 = java.lang.String.valueOf(r4)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r3, r0)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.SecuritySmsHandler.checkSmsBlocked(android.content.Intent):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean startInterceptSmsBySender(String pkgName, String sender, int count) {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_SMS_INTERCEPT, "SecurityManagerService");
        int callerUid = Binder.getCallingUid();
        synchronized (this.mInterceptSmsLock) {
            if (this.mInterceptSmsCallerUid != 0) {
                return false;
            }
            this.mInterceptSmsCallerUid = callerUid;
            this.mInterceptSmsCallerPkgName = pkgName;
            this.mInterceptSmsSenderNum = sender;
            this.mInterceptSmsCount = count;
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean stopInterceptSmsBySender() {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.MANAGE_SMS_INTERCEPT, "SecurityManagerService");
        int callerUid = Binder.getCallingUid();
        synchronized (this.mInterceptSmsLock) {
            if (this.mInterceptSmsCallerUid == 0) {
                return true;
            }
            if (this.mInterceptSmsCallerUid != callerUid) {
                return false;
            }
            releaseSmsIntercept();
            return true;
        }
    }

    private boolean checkWithInterceptedSender(String sender) {
        boolean result = false;
        synchronized (this.mInterceptSmsLock) {
            Log.i("SecurityManagerService", String.format("checkWithInterceptedSender: callerUid:%d, senderNum:%s, count:%d", new Object[]{Integer.valueOf(this.mInterceptSmsCallerUid), this.mInterceptSmsSenderNum, Integer.valueOf(this.mInterceptSmsCount)}));
            if (this.mInterceptSmsCallerUid != 0 && TextUtils.equals(this.mInterceptSmsSenderNum, sender)) {
                if (this.mInterceptSmsCount > 0) {
                    this.mInterceptSmsCount--;
                    result = true;
                }
                if (this.mInterceptSmsCount == 0) {
                    releaseSmsIntercept();
                }
            }
        }
        return result;
    }

    /* access modifiers changed from: private */
    public int checkByAntiSpam(String address, String content, int slotId) {
        long token = Binder.clearCallingIdentity();
        int blockType = ExtraTelephony.getSmsBlockType(this.mContext, address, content, slotId);
        Binder.restoreCallingIdentity(token);
        Log.i("SecurityManagerService", "checkByAntiSpam : blockType = " + blockType);
        return blockType;
    }

    private void releaseSmsIntercept() {
        this.mInterceptSmsCallerUid = 0;
        this.mInterceptSmsCallerPkgName = null;
        this.mInterceptSmsSenderNum = null;
        this.mInterceptSmsCount = 0;
    }

    private void dispatchToInterceptApp(Intent intent) {
        Log.i("SecurityManagerService", "dispatchToInterceptApp");
        intent.setFlags(0);
        intent.setComponent((ComponentName) null);
        intent.setPackage(this.mInterceptSmsCallerPkgName);
        intent.setAction("android.provider.Telephony.SMS_RECEIVED");
        dispatchIntent(intent, "android.permission.RECEIVE_SMS", 16, this.mInterceptedSmsResultReceiver);
    }

    /* access modifiers changed from: private */
    public void dispatchSmsToAntiSpam(Intent intent) {
        Log.i("SecurityManagerService", "dispatchSmsToAntiSpam");
        intent.setComponent((ComponentName) null);
        intent.setPackage("com.android.mms");
        intent.setAction("android.provider.Telephony.SMS_DELIVER");
        dispatchIntent(intent, "android.permission.RECEIVE_SMS", 16, (BroadcastReceiver) null);
    }

    private void dispatchMmsToAntiSpam(Intent intent) {
        Log.i("SecurityManagerService", "dispatchMmsToAntiSpam");
        intent.setComponent((ComponentName) null);
        intent.setPackage("com.android.mms");
        intent.setAction("android.provider.Telephony.WAP_PUSH_DELIVER");
        dispatchIntent(intent, "android.permission.RECEIVE_SMS", 16, (BroadcastReceiver) null);
    }

    /* access modifiers changed from: private */
    public void dispatchNormalSms(Intent intent) {
        Log.i("SecurityManagerService", "dispatchNormalSms");
        intent.setPackage((String) null);
        ComponentName componentName = SmsApplication.getDefaultSmsApplication(this.mContext, true);
        if (componentName != null) {
            intent.setComponent(componentName);
            Log.i("SecurityManagerService", String.format("Delivering SMS to: %s", new Object[]{componentName.getPackageName()}));
        }
        intent.addFlags(134217728);
        intent.setAction("android.provider.Telephony.SMS_DELIVER");
        dispatchIntent(intent, "android.permission.RECEIVE_SMS", 16, this.mNormalMsgResultReceiver);
    }

    private void dispatchNormalMms(Intent intent) {
        Log.i("SecurityManagerService", "dispatchNormalMms");
        intent.setPackage((String) null);
        ComponentName componentName = SmsApplication.getDefaultMmsApplication(this.mContext, true);
        if (componentName != null) {
            intent.setComponent(componentName);
            Log.i("SecurityManagerService", String.format("Delivering MMS to: %s", new Object[]{componentName.getPackageName()}));
        }
        intent.addFlags(134217728);
        intent.setAction("android.provider.Telephony.WAP_PUSH_DELIVER");
        dispatchIntent(intent, "android.permission.RECEIVE_SMS", 16, this.mNormalMsgResultReceiver);
    }

    /* access modifiers changed from: private */
    public void dispatchIntent(Intent intent, String permission, int appOp, BroadcastReceiver resultReceiver) {
        this.mContext.sendOrderedBroadcast(intent, permission, appOp, resultReceiver, this.mHandler, -1, (String) null, (Bundle) null);
    }

    public static int getSlotIdFromIntent(Intent intent) {
        int slotId = 0;
        if (TelephonyManager.getDefault().getPhoneCount() > 1 && (slotId = intent.getIntExtra(SubscriptionManager.SLOT_KEY, 0)) < 0) {
            Log.e("SecurityManagerService", "getSlotIdFromIntent slotId < 0");
        }
        return slotId;
    }
}
