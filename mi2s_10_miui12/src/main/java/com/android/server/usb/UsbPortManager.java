package com.android.server.usb;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.ParcelableUsbPort;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.hardware.usb.V1_0.IUsb;
import android.hardware.usb.V1_0.PortRole;
import android.hardware.usb.V1_0.PortStatus;
import android.hardware.usb.V1_1.PortStatus_1_1;
import android.hardware.usb.V1_2.IUsbCallback;
import android.hidl.manager.V1_0.IServiceManager;
import android.hidl.manager.V1_0.IServiceNotification;
import android.os.Bundle;
import android.os.Handler;
import android.os.IHwBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.StatsLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.usb.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.server.FgThread;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class UsbPortManager {
    private static final int COMBO_SINK_DEVICE = UsbPort.combineRolesAsBit(2, 2);
    private static final int COMBO_SINK_HOST = UsbPort.combineRolesAsBit(2, 1);
    private static final int COMBO_SOURCE_DEVICE = UsbPort.combineRolesAsBit(1, 2);
    private static final int COMBO_SOURCE_HOST = UsbPort.combineRolesAsBit(1, 1);
    private static final int MSG_SYSTEM_READY = 2;
    private static final int MSG_UPDATE_PORTS = 1;
    private static final String PORT_INFO = "port_info";
    private static final String TAG = "UsbPortManager";
    private static final int USB_HAL_DEATH_COOKIE = 1000;
    private final ArrayMap<String, Boolean> mConnected = new ArrayMap<>();
    private final ArrayMap<String, Integer> mContaminantStatus = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final Context mContext;
    private HALCallback mHALCallback = new HALCallback((IndentingPrintWriter) null, this);
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler(FgThread.get().getLooper()) {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                ArrayList<RawPortInfo> PortInfo = msg.getData().getParcelableArrayList(UsbPortManager.PORT_INFO);
                synchronized (UsbPortManager.this.mLock) {
                    UsbPortManager.this.updatePortsLocked((IndentingPrintWriter) null, PortInfo);
                }
            } else if (i == 2) {
                UsbPortManager usbPortManager = UsbPortManager.this;
                NotificationManager unused = usbPortManager.mNotificationManager = (NotificationManager) usbPortManager.mContext.getSystemService("notification");
            }
        }
    };
    private int mIsPortContaminatedNotificationId;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public NotificationManager mNotificationManager;
    private final ArrayMap<String, PortInfo> mPorts = new ArrayMap<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public IUsb mProxy = null;
    private final ArrayMap<String, RawPortInfo> mSimulatedPorts = new ArrayMap<>();
    /* access modifiers changed from: private */
    public boolean mSystemReady;

    public UsbPortManager(Context context) {
        this.mContext = context;
        try {
            if (!IServiceManager.getService().registerForNotifications(IUsb.kInterfaceName, "", new ServiceNotification())) {
                logAndPrint(6, (IndentingPrintWriter) null, "Failed to register service start notification");
            }
            connectToProxy((IndentingPrintWriter) null);
        } catch (RemoteException e) {
            logAndPrintException((IndentingPrintWriter) null, "Failed to register service start notification", e);
        }
    }

    public void systemReady() {
        this.mSystemReady = true;
        IUsb iUsb = this.mProxy;
        if (iUsb != null) {
            try {
                iUsb.queryPortStatus();
            } catch (RemoteException e) {
                logAndPrintException((IndentingPrintWriter) null, "ServiceStart: Failed to query port status", e);
            }
        }
        this.mHandler.sendEmptyMessage(2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x002e, code lost:
        r1 = r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateContaminantNotification() {
        /*
            r18 = this;
            r0 = r18
            r1 = 0
            android.content.Context r2 = r0.mContext
            android.content.res.Resources r2 = r2.getResources()
            r3 = 2
            android.util.ArrayMap<java.lang.String, com.android.server.usb.UsbPortManager$PortInfo> r4 = r0.mPorts
            java.util.Collection r4 = r4.values()
            java.util.Iterator r4 = r4.iterator()
        L_0x0014:
            boolean r5 = r4.hasNext()
            r6 = 3
            r7 = 1
            if (r5 == 0) goto L_0x0030
            java.lang.Object r5 = r4.next()
            com.android.server.usb.UsbPortManager$PortInfo r5 = (com.android.server.usb.UsbPortManager.PortInfo) r5
            android.hardware.usb.UsbPortStatus r8 = r5.mUsbPortStatus
            int r3 = r8.getContaminantDetectionStatus()
            if (r3 == r6) goto L_0x002e
            if (r3 != r7) goto L_0x002d
            goto L_0x002e
        L_0x002d:
            goto L_0x0014
        L_0x002e:
            r1 = r5
        L_0x0030:
            r4 = 17170460(0x106001c, float:2.4611991E-38)
            r5 = 53
            r8 = 52
            r9 = 0
            if (r3 != r6) goto L_0x00ce
            int r10 = r0.mIsPortContaminatedNotificationId
            if (r10 == r8) goto L_0x00ce
            if (r10 != r5) goto L_0x0047
            android.app.NotificationManager r5 = r0.mNotificationManager
            android.os.UserHandle r6 = android.os.UserHandle.ALL
            r5.cancelAsUser(r9, r10, r6)
        L_0x0047:
            r0.mIsPortContaminatedNotificationId = r8
            r5 = 17041266(0x1040772, float:2.4249913E-38)
            java.lang.CharSequence r6 = r2.getText(r5)
            java.lang.String r8 = com.android.internal.notification.SystemNotificationChannels.ALERTS
            r10 = 17041265(0x1040771, float:2.424991E-38)
            java.lang.CharSequence r10 = r2.getText(r10)
            android.content.Intent r11 = new android.content.Intent
            r11.<init>()
            r12 = 268435456(0x10000000, float:2.5243549E-29)
            r11.addFlags(r12)
            java.lang.String r12 = "com.android.systemui"
            java.lang.String r13 = "com.android.systemui.usb.UsbContaminantActivity"
            r11.setClassName(r12, r13)
            android.hardware.usb.UsbPort r12 = r1.mUsbPort
            android.hardware.usb.ParcelableUsbPort r12 = android.hardware.usb.ParcelableUsbPort.of(r12)
            java.lang.String r13 = "port"
            r11.putExtra(r13, r12)
            android.content.Context r12 = r0.mContext
            r13 = 0
            r15 = 0
            r16 = 0
            android.os.UserHandle r17 = android.os.UserHandle.CURRENT
            r14 = r11
            android.app.PendingIntent r12 = android.app.PendingIntent.getActivityAsUser(r12, r13, r14, r15, r16, r17)
            android.app.Notification$Builder r13 = new android.app.Notification$Builder
            android.content.Context r14 = r0.mContext
            r13.<init>(r14, r8)
            android.app.Notification$Builder r13 = r13.setOngoing(r7)
            android.app.Notification$Builder r13 = r13.setTicker(r6)
            android.content.Context r14 = r0.mContext
            int r4 = r14.getColor(r4)
            android.app.Notification$Builder r4 = r13.setColor(r4)
            android.app.Notification$Builder r4 = r4.setContentIntent(r12)
            android.app.Notification$Builder r4 = r4.setContentTitle(r6)
            android.app.Notification$Builder r4 = r4.setContentText(r10)
            android.app.Notification$Builder r4 = r4.setVisibility(r7)
            r7 = 17301642(0x108008a, float:2.4979642E-38)
            android.app.Notification$Builder r4 = r4.setSmallIcon(r7)
            android.app.Notification$BigTextStyle r7 = new android.app.Notification$BigTextStyle
            r7.<init>()
            android.app.Notification$BigTextStyle r7 = r7.bigText(r10)
            android.app.Notification$Builder r4 = r4.setStyle(r7)
            android.app.Notification r7 = r4.build()
            android.app.NotificationManager r13 = r0.mNotificationManager
            int r14 = r0.mIsPortContaminatedNotificationId
            android.os.UserHandle r15 = android.os.UserHandle.ALL
            r13.notifyAsUser(r9, r14, r7, r15)
            goto L_0x0136
        L_0x00ce:
            if (r3 == r6) goto L_0x0136
            int r6 = r0.mIsPortContaminatedNotificationId
            if (r6 != r8) goto L_0x0136
            android.app.NotificationManager r8 = r0.mNotificationManager
            android.os.UserHandle r10 = android.os.UserHandle.ALL
            r8.cancelAsUser(r9, r6, r10)
            r6 = 0
            r0.mIsPortContaminatedNotificationId = r6
            r6 = 2
            if (r3 != r6) goto L_0x0137
            r0.mIsPortContaminatedNotificationId = r5
            r5 = 17041268(0x1040774, float:2.4249918E-38)
            java.lang.CharSequence r6 = r2.getText(r5)
            java.lang.String r8 = com.android.internal.notification.SystemNotificationChannels.ALERTS
            r10 = 17041267(0x1040773, float:2.4249916E-38)
            java.lang.CharSequence r10 = r2.getText(r10)
            android.app.Notification$Builder r11 = new android.app.Notification$Builder
            android.content.Context r12 = r0.mContext
            r11.<init>(r12, r8)
            r12 = 17302845(0x108053d, float:2.4983013E-38)
            android.app.Notification$Builder r11 = r11.setSmallIcon(r12)
            android.app.Notification$Builder r11 = r11.setTicker(r6)
            android.content.Context r12 = r0.mContext
            int r4 = r12.getColor(r4)
            android.app.Notification$Builder r4 = r11.setColor(r4)
            android.app.Notification$Builder r4 = r4.setContentTitle(r6)
            android.app.Notification$Builder r4 = r4.setContentText(r10)
            android.app.Notification$Builder r4 = r4.setVisibility(r7)
            android.app.Notification$BigTextStyle r7 = new android.app.Notification$BigTextStyle
            r7.<init>()
            android.app.Notification$BigTextStyle r7 = r7.bigText(r10)
            android.app.Notification$Builder r4 = r4.setStyle(r7)
            android.app.Notification r7 = r4.build()
            android.app.NotificationManager r11 = r0.mNotificationManager
            int r12 = r0.mIsPortContaminatedNotificationId
            android.os.UserHandle r13 = android.os.UserHandle.ALL
            r11.notifyAsUser(r9, r12, r7, r13)
            goto L_0x0137
        L_0x0136:
        L_0x0137:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbPortManager.updateContaminantNotification():void");
    }

    public UsbPort[] getPorts() {
        UsbPort[] result;
        synchronized (this.mLock) {
            int count = this.mPorts.size();
            result = new UsbPort[count];
            for (int i = 0; i < count; i++) {
                result[i] = this.mPorts.valueAt(i).mUsbPort;
            }
        }
        return result;
    }

    public UsbPortStatus getPortStatus(String portId) {
        UsbPortStatus usbPortStatus;
        synchronized (this.mLock) {
            PortInfo portInfo = this.mPorts.get(portId);
            usbPortStatus = portInfo != null ? portInfo.mUsbPortStatus : null;
        }
        return usbPortStatus;
    }

    public void enableContaminantDetection(String portId, boolean enable, IndentingPrintWriter pw) {
        PortInfo portInfo = this.mPorts.get(portId);
        if (portInfo == null) {
            if (pw != null) {
                pw.println("No such USB port: " + portId);
            }
        } else if (portInfo.mUsbPort.supportsEnableContaminantPresenceDetection()) {
            if (enable && portInfo.mUsbPortStatus.getContaminantDetectionStatus() != 1) {
                return;
            }
            if ((enable || portInfo.mUsbPortStatus.getContaminantDetectionStatus() != 1) && portInfo.mUsbPortStatus.getContaminantDetectionStatus() != 0) {
                try {
                    android.hardware.usb.V1_2.IUsb.castFrom(this.mProxy).enableContaminantPresenceDetection(portId, enable);
                } catch (RemoteException e) {
                    logAndPrintException(pw, "Failed to set contaminant detection", e);
                } catch (ClassCastException e2) {
                    logAndPrintException(pw, "Method only applicable to V1.2 or above implementation", e2);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0081, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x002f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setPortRoles(java.lang.String r21, int r22, int r23, com.android.internal.util.IndentingPrintWriter r24) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r5 = r24
            java.lang.Object r6 = r1.mLock
            monitor-enter(r6)
            android.util.ArrayMap<java.lang.String, com.android.server.usb.UsbPortManager$PortInfo> r0 = r1.mPorts     // Catch:{ all -> 0x0240 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x0240 }
            com.android.server.usb.UsbPortManager$PortInfo r0 = (com.android.server.usb.UsbPortManager.PortInfo) r0     // Catch:{ all -> 0x0240 }
            r7 = r0
            if (r7 != 0) goto L_0x0030
            if (r5 == 0) goto L_0x002e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = "No such USB port: "
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            r0.append(r2)     // Catch:{ all -> 0x0240 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0240 }
            r5.println(r0)     // Catch:{ all -> 0x0240 }
        L_0x002e:
            monitor-exit(r6)     // Catch:{ all -> 0x0240 }
            return
        L_0x0030:
            android.hardware.usb.UsbPortStatus r0 = r7.mUsbPortStatus     // Catch:{ all -> 0x0240 }
            boolean r0 = r0.isRoleCombinationSupported(r3, r4)     // Catch:{ all -> 0x0240 }
            r8 = 6
            if (r0 != 0) goto L_0x0067
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r9 = "Attempted to set USB port into unsupported role combination: portId="
            r0.append(r9)     // Catch:{ all -> 0x0240 }
            r0.append(r2)     // Catch:{ all -> 0x0240 }
            java.lang.String r9 = ", newPowerRole="
            r0.append(r9)     // Catch:{ all -> 0x0240 }
            java.lang.String r9 = android.hardware.usb.UsbPort.powerRoleToString(r22)     // Catch:{ all -> 0x0240 }
            r0.append(r9)     // Catch:{ all -> 0x0240 }
            java.lang.String r9 = ", newDataRole="
            r0.append(r9)     // Catch:{ all -> 0x0240 }
            java.lang.String r9 = android.hardware.usb.UsbPort.dataRoleToString(r23)     // Catch:{ all -> 0x0240 }
            r0.append(r9)     // Catch:{ all -> 0x0240 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0240 }
            logAndPrint(r8, r5, r0)     // Catch:{ all -> 0x0240 }
            monitor-exit(r6)     // Catch:{ all -> 0x0240 }
            return
        L_0x0067:
            android.hardware.usb.UsbPortStatus r0 = r7.mUsbPortStatus     // Catch:{ all -> 0x0240 }
            int r0 = r0.getCurrentDataRole()     // Catch:{ all -> 0x0240 }
            r9 = r0
            android.hardware.usb.UsbPortStatus r0 = r7.mUsbPortStatus     // Catch:{ all -> 0x0240 }
            int r0 = r0.getCurrentPowerRole()     // Catch:{ all -> 0x0240 }
            r10 = r0
            if (r9 != r4) goto L_0x0082
            if (r10 != r3) goto L_0x0082
            if (r5 == 0) goto L_0x0080
            java.lang.String r0 = "No change."
            r5.println(r0)     // Catch:{ all -> 0x0240 }
        L_0x0080:
            monitor-exit(r6)     // Catch:{ all -> 0x0240 }
            return
        L_0x0082:
            boolean r0 = r7.mCanChangeMode     // Catch:{ all -> 0x0240 }
            r11 = r0
            boolean r0 = r7.mCanChangePowerRole     // Catch:{ all -> 0x0240 }
            r12 = r0
            boolean r0 = r7.mCanChangeDataRole     // Catch:{ all -> 0x0240 }
            r13 = r0
            android.hardware.usb.UsbPortStatus r0 = r7.mUsbPortStatus     // Catch:{ all -> 0x0240 }
            int r0 = r0.getCurrentMode()     // Catch:{ all -> 0x0240 }
            r14 = r0
            r0 = 2
            r15 = 1
            if (r12 != 0) goto L_0x0098
            if (r10 != r3) goto L_0x009c
        L_0x0098:
            if (r13 != 0) goto L_0x00e0
            if (r9 == r4) goto L_0x00e0
        L_0x009c:
            if (r11 == 0) goto L_0x00a7
            if (r3 != r15) goto L_0x00a7
            if (r4 != r15) goto L_0x00a7
            r16 = 2
            r17 = r16
            goto L_0x00e4
        L_0x00a7:
            if (r11 == 0) goto L_0x00b2
            if (r3 != r0) goto L_0x00b2
            if (r4 != r0) goto L_0x00b2
            r16 = 1
            r17 = r16
            goto L_0x00e4
        L_0x00b2:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r15 = "Found mismatch in supported USB role combinations while attempting to change role: "
            r0.append(r15)     // Catch:{ all -> 0x0240 }
            r0.append(r7)     // Catch:{ all -> 0x0240 }
            java.lang.String r15 = ", newPowerRole="
            r0.append(r15)     // Catch:{ all -> 0x0240 }
            java.lang.String r15 = android.hardware.usb.UsbPort.powerRoleToString(r22)     // Catch:{ all -> 0x0240 }
            r0.append(r15)     // Catch:{ all -> 0x0240 }
            java.lang.String r15 = ", newDataRole="
            r0.append(r15)     // Catch:{ all -> 0x0240 }
            java.lang.String r15 = android.hardware.usb.UsbPort.dataRoleToString(r23)     // Catch:{ all -> 0x0240 }
            r0.append(r15)     // Catch:{ all -> 0x0240 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0240 }
            logAndPrint(r8, r5, r0)     // Catch:{ all -> 0x0240 }
            monitor-exit(r6)     // Catch:{ all -> 0x0240 }
            return
        L_0x00e0:
            r16 = r14
            r17 = r16
        L_0x00e4:
            r15 = 4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = "Setting USB port mode and role: portId="
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            r0.append(r2)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = ", currentMode="
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = android.hardware.usb.UsbPort.modeToString(r14)     // Catch:{ all -> 0x0240 }
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = ", currentPowerRole="
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = android.hardware.usb.UsbPort.powerRoleToString(r10)     // Catch:{ all -> 0x0240 }
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = ", currentDataRole="
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = android.hardware.usb.UsbPort.dataRoleToString(r9)     // Catch:{ all -> 0x0240 }
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = ", newMode="
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = android.hardware.usb.UsbPort.modeToString(r17)     // Catch:{ all -> 0x0240 }
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = ", newPowerRole="
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = android.hardware.usb.UsbPort.powerRoleToString(r22)     // Catch:{ all -> 0x0240 }
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = ", newDataRole="
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = android.hardware.usb.UsbPort.dataRoleToString(r23)     // Catch:{ all -> 0x0240 }
            r0.append(r8)     // Catch:{ all -> 0x0240 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0240 }
            logAndPrint(r15, r5, r0)     // Catch:{ all -> 0x0240 }
            android.util.ArrayMap<java.lang.String, com.android.server.usb.UsbPortManager$RawPortInfo> r0 = r1.mSimulatedPorts     // Catch:{ all -> 0x0240 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x0240 }
            com.android.server.usb.UsbPortManager$RawPortInfo r0 = (com.android.server.usb.UsbPortManager.RawPortInfo) r0     // Catch:{ all -> 0x0240 }
            r8 = r0
            if (r8 == 0) goto L_0x015a
            r15 = r17
            r8.currentMode = r15     // Catch:{ all -> 0x0240 }
            r8.currentPowerRole = r3     // Catch:{ all -> 0x0240 }
            r8.currentDataRole = r4     // Catch:{ all -> 0x0240 }
            r0 = 0
            r1.updatePortsLocked(r5, r0)     // Catch:{ all -> 0x0240 }
            goto L_0x023e
        L_0x015a:
            r15 = r17
            android.hardware.usb.V1_0.IUsb r0 = r1.mProxy     // Catch:{ all -> 0x0240 }
            if (r0 == 0) goto L_0x0238
            if (r14 == r15) goto L_0x01c3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            r17 = r7
            java.lang.String r7 = "Trying to set the USB port mode: portId="
            r0.append(r7)     // Catch:{ all -> 0x0240 }
            r0.append(r2)     // Catch:{ all -> 0x0240 }
            java.lang.String r7 = ", newMode="
            r0.append(r7)     // Catch:{ all -> 0x0240 }
            java.lang.String r7 = android.hardware.usb.UsbPort.modeToString(r15)     // Catch:{ all -> 0x0240 }
            r0.append(r7)     // Catch:{ all -> 0x0240 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0240 }
            r7 = 6
            logAndPrint(r7, r5, r0)     // Catch:{ all -> 0x0240 }
            android.hardware.usb.V1_0.PortRole r0 = new android.hardware.usb.V1_0.PortRole     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            r7 = r0
            r0 = 2
            r7.type = r0     // Catch:{ all -> 0x0240 }
            r7.role = r15     // Catch:{ all -> 0x0240 }
            android.hardware.usb.V1_0.IUsb r0 = r1.mProxy     // Catch:{ RemoteException -> 0x019a }
            r0.switchRole(r2, r7)     // Catch:{ RemoteException -> 0x019a }
            r18 = r8
            r19 = r11
            goto L_0x01c1
        L_0x019a:
            r0 = move-exception
            r18 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r8.<init>()     // Catch:{ all -> 0x0240 }
            r19 = r11
            java.lang.String r11 = "Failed to set the USB port mode: portId="
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            r8.append(r2)     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = ", newMode="
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            int r11 = r7.role     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = android.hardware.usb.UsbPort.modeToString(r11)     // Catch:{ all -> 0x0240 }
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0240 }
            logAndPrintException(r5, r8, r0)     // Catch:{ all -> 0x0240 }
        L_0x01c1:
            goto L_0x023e
        L_0x01c3:
            r17 = r7
            r18 = r8
            r19 = r11
            if (r10 == r3) goto L_0x0201
            android.hardware.usb.V1_0.PortRole r0 = new android.hardware.usb.V1_0.PortRole     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            r7 = r0
            r0 = 1
            r7.type = r0     // Catch:{ all -> 0x0240 }
            r7.role = r3     // Catch:{ all -> 0x0240 }
            android.hardware.usb.V1_0.IUsb r0 = r1.mProxy     // Catch:{ RemoteException -> 0x01dc }
            r0.switchRole(r2, r7)     // Catch:{ RemoteException -> 0x01dc }
            goto L_0x0201
        L_0x01dc:
            r0 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r8.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = "Failed to set the USB port power role: portId="
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            r8.append(r2)     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = ", newPowerRole="
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            int r11 = r7.role     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = android.hardware.usb.UsbPort.powerRoleToString(r11)     // Catch:{ all -> 0x0240 }
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0240 }
            logAndPrintException(r5, r8, r0)     // Catch:{ all -> 0x0240 }
            monitor-exit(r6)     // Catch:{ all -> 0x0240 }
            return
        L_0x0201:
            if (r9 == r4) goto L_0x023e
            android.hardware.usb.V1_0.PortRole r0 = new android.hardware.usb.V1_0.PortRole     // Catch:{ all -> 0x0240 }
            r0.<init>()     // Catch:{ all -> 0x0240 }
            r7 = r0
            r0 = 0
            r7.type = r0     // Catch:{ all -> 0x0240 }
            r7.role = r4     // Catch:{ all -> 0x0240 }
            android.hardware.usb.V1_0.IUsb r0 = r1.mProxy     // Catch:{ RemoteException -> 0x0214 }
            r0.switchRole(r2, r7)     // Catch:{ RemoteException -> 0x0214 }
            goto L_0x023e
        L_0x0214:
            r0 = move-exception
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0240 }
            r8.<init>()     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = "Failed to set the USB port data role: portId="
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            r8.append(r2)     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = ", newDataRole="
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            int r11 = r7.role     // Catch:{ all -> 0x0240 }
            java.lang.String r11 = android.hardware.usb.UsbPort.dataRoleToString(r11)     // Catch:{ all -> 0x0240 }
            r8.append(r11)     // Catch:{ all -> 0x0240 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0240 }
            logAndPrintException(r5, r8, r0)     // Catch:{ all -> 0x0240 }
            goto L_0x023e
        L_0x0238:
            r17 = r7
            r18 = r8
            r19 = r11
        L_0x023e:
            monitor-exit(r6)     // Catch:{ all -> 0x0240 }
            return
        L_0x0240:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0240 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbPortManager.setPortRoles(java.lang.String, int, int, com.android.internal.util.IndentingPrintWriter):void");
    }

    public void addSimulatedPort(String portId, int supportedModes, IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            if (this.mSimulatedPorts.containsKey(portId)) {
                pw.println("Port with same name already exists.  Please remove it first.");
                return;
            }
            pw.println("Adding simulated port: portId=" + portId + ", supportedModes=" + UsbPort.modeToString(supportedModes));
            this.mSimulatedPorts.put(portId, new RawPortInfo(portId, supportedModes));
            updatePortsLocked(pw, (ArrayList<RawPortInfo>) null);
        }
    }

    public void connectSimulatedPort(String portId, int mode, boolean canChangeMode, int powerRole, boolean canChangePowerRole, int dataRole, boolean canChangeDataRole, IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            RawPortInfo portInfo = this.mSimulatedPorts.get(portId);
            if (portInfo == null) {
                pw.println("Cannot connect simulated port which does not exist.");
                return;
            }
            if (!(mode == 0 || powerRole == 0)) {
                if (dataRole != 0) {
                    if ((portInfo.supportedModes & mode) == 0) {
                        pw.println("Simulated port does not support mode: " + UsbPort.modeToString(mode));
                        return;
                    }
                    pw.println("Connecting simulated port: portId=" + portId + ", mode=" + UsbPort.modeToString(mode) + ", canChangeMode=" + canChangeMode + ", powerRole=" + UsbPort.powerRoleToString(powerRole) + ", canChangePowerRole=" + canChangePowerRole + ", dataRole=" + UsbPort.dataRoleToString(dataRole) + ", canChangeDataRole=" + canChangeDataRole);
                    portInfo.currentMode = mode;
                    portInfo.canChangeMode = canChangeMode;
                    portInfo.currentPowerRole = powerRole;
                    portInfo.canChangePowerRole = canChangePowerRole;
                    portInfo.currentDataRole = dataRole;
                    portInfo.canChangeDataRole = canChangeDataRole;
                    updatePortsLocked(pw, (ArrayList<RawPortInfo>) null);
                    return;
                }
            }
            pw.println("Cannot connect simulated port in null mode, power role, or data role.");
        }
    }

    public void simulateContaminantStatus(String portId, boolean detected, IndentingPrintWriter pw) {
        int i;
        synchronized (this.mLock) {
            RawPortInfo portInfo = this.mSimulatedPorts.get(portId);
            if (portInfo == null) {
                pw.println("Simulated port not found.");
                return;
            }
            pw.println("Simulating wet port: portId=" + portId + ", wet=" + detected);
            if (detected) {
                i = 3;
            } else {
                i = 2;
            }
            portInfo.contaminantDetectionStatus = i;
            updatePortsLocked(pw, (ArrayList<RawPortInfo>) null);
        }
    }

    public void disconnectSimulatedPort(String portId, IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            RawPortInfo portInfo = this.mSimulatedPorts.get(portId);
            if (portInfo == null) {
                pw.println("Cannot disconnect simulated port which does not exist.");
                return;
            }
            pw.println("Disconnecting simulated port: portId=" + portId);
            portInfo.currentMode = 0;
            portInfo.canChangeMode = false;
            portInfo.currentPowerRole = 0;
            portInfo.canChangePowerRole = false;
            portInfo.currentDataRole = 0;
            portInfo.canChangeDataRole = false;
            updatePortsLocked(pw, (ArrayList<RawPortInfo>) null);
        }
    }

    public void removeSimulatedPort(String portId, IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            int index = this.mSimulatedPorts.indexOfKey(portId);
            if (index < 0) {
                pw.println("Cannot remove simulated port which does not exist.");
                return;
            }
            pw.println("Disconnecting simulated port: portId=" + portId);
            this.mSimulatedPorts.removeAt(index);
            updatePortsLocked(pw, (ArrayList<RawPortInfo>) null);
        }
    }

    public void resetSimulation(IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            pw.println("Removing all simulated ports and ending simulation.");
            if (!this.mSimulatedPorts.isEmpty()) {
                this.mSimulatedPorts.clear();
                updatePortsLocked(pw, (ArrayList<RawPortInfo>) null);
            }
        }
    }

    public void dump(DualDumpOutputStream dump, String idName, long id) {
        long token = dump.start(idName, id);
        synchronized (this.mLock) {
            dump.write("is_simulation_active", 1133871366145L, !this.mSimulatedPorts.isEmpty());
            for (PortInfo portInfo : this.mPorts.values()) {
                portInfo.dump(dump, "usb_ports", 2246267895810L);
            }
        }
        dump.end(token);
    }

    private static class HALCallback extends IUsbCallback.Stub {
        public UsbPortManager portManager;
        public IndentingPrintWriter pw;

        HALCallback(IndentingPrintWriter pw2, UsbPortManager portManager2) {
            this.pw = pw2;
            this.portManager = portManager2;
        }

        public void notifyPortStatusChange(ArrayList<PortStatus> currentPortStatus, int retval) {
            if (this.portManager.mSystemReady) {
                if (retval != 0) {
                    UsbPortManager.logAndPrint(6, this.pw, "port status enquiry failed");
                    return;
                }
                ArrayList<RawPortInfo> newPortInfo = new ArrayList<>();
                Iterator<PortStatus> it = currentPortStatus.iterator();
                while (it.hasNext()) {
                    PortStatus current = it.next();
                    newPortInfo.add(new RawPortInfo(current.portName, current.supportedModes, 0, current.currentMode, current.canChangeMode, current.currentPowerRole, current.canChangePowerRole, current.currentDataRole, current.canChangeDataRole, false, 0, false, 0));
                    IndentingPrintWriter indentingPrintWriter = this.pw;
                    UsbPortManager.logAndPrint(4, indentingPrintWriter, "ClientCallback V1_0: " + current.portName);
                }
                Message message = this.portManager.mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(UsbPortManager.PORT_INFO, newPortInfo);
                message.what = 1;
                message.setData(bundle);
                this.portManager.mHandler.sendMessage(message);
            }
        }

        public void notifyPortStatusChange_1_1(ArrayList<PortStatus_1_1> currentPortStatus, int retval) {
            if (this.portManager.mSystemReady) {
                if (retval != 0) {
                    UsbPortManager.logAndPrint(6, this.pw, "port status enquiry failed");
                    return;
                }
                ArrayList<RawPortInfo> newPortInfo = new ArrayList<>();
                int numStatus = currentPortStatus.size();
                for (int i = 0; i < numStatus; i++) {
                    PortStatus_1_1 current = currentPortStatus.get(i);
                    newPortInfo.add(new RawPortInfo(current.status.portName, current.supportedModes, 0, current.currentMode, current.status.canChangeMode, current.status.currentPowerRole, current.status.canChangePowerRole, current.status.currentDataRole, current.status.canChangeDataRole, false, 0, false, 0));
                    IndentingPrintWriter indentingPrintWriter = this.pw;
                    UsbPortManager.logAndPrint(4, indentingPrintWriter, "ClientCallback V1_1: " + current.status.portName);
                }
                ArrayList<PortStatus_1_1> arrayList = currentPortStatus;
                Message message = this.portManager.mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(UsbPortManager.PORT_INFO, newPortInfo);
                message.what = 1;
                message.setData(bundle);
                this.portManager.mHandler.sendMessage(message);
            }
        }

        public void notifyPortStatusChange_1_2(ArrayList<android.hardware.usb.V1_2.PortStatus> currentPortStatus, int retval) {
            if (this.portManager.mSystemReady) {
                if (retval != 0) {
                    UsbPortManager.logAndPrint(6, this.pw, "port status enquiry failed");
                    return;
                }
                ArrayList<RawPortInfo> newPortInfo = new ArrayList<>();
                int numStatus = currentPortStatus.size();
                int i = 0;
                while (i < numStatus) {
                    android.hardware.usb.V1_2.PortStatus current = currentPortStatus.get(i);
                    int numStatus2 = numStatus;
                    newPortInfo.add(new RawPortInfo(current.status_1_1.status.portName, current.status_1_1.supportedModes, current.supportedContaminantProtectionModes, current.status_1_1.currentMode, current.status_1_1.status.canChangeMode, current.status_1_1.status.currentPowerRole, current.status_1_1.status.canChangePowerRole, current.status_1_1.status.currentDataRole, current.status_1_1.status.canChangeDataRole, current.supportsEnableContaminantPresenceProtection, current.contaminantProtectionStatus, current.supportsEnableContaminantPresenceDetection, current.contaminantDetectionStatus));
                    IndentingPrintWriter indentingPrintWriter = this.pw;
                    UsbPortManager.logAndPrint(4, indentingPrintWriter, "ClientCallback V1_2: " + current.status_1_1.status.portName);
                    i++;
                    numStatus = numStatus2;
                }
                int i2 = i;
                Message message = this.portManager.mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(UsbPortManager.PORT_INFO, newPortInfo);
                message.what = 1;
                message.setData(bundle);
                this.portManager.mHandler.sendMessage(message);
            }
        }

        public void notifyRoleSwitchStatus(String portName, PortRole role, int retval) {
            if (retval == 0) {
                IndentingPrintWriter indentingPrintWriter = this.pw;
                UsbPortManager.logAndPrint(4, indentingPrintWriter, portName + " role switch successful");
                return;
            }
            IndentingPrintWriter indentingPrintWriter2 = this.pw;
            UsbPortManager.logAndPrint(6, indentingPrintWriter2, portName + " role switch failed");
        }
    }

    final class DeathRecipient implements IHwBinder.DeathRecipient {
        public IndentingPrintWriter pw;

        DeathRecipient(IndentingPrintWriter pw2) {
            this.pw = pw2;
        }

        public void serviceDied(long cookie) {
            if (cookie == 1000) {
                IndentingPrintWriter indentingPrintWriter = this.pw;
                UsbPortManager.logAndPrint(6, indentingPrintWriter, "Usb hal service died cookie: " + cookie);
                synchronized (UsbPortManager.this.mLock) {
                    IUsb unused = UsbPortManager.this.mProxy = null;
                }
            }
        }
    }

    final class ServiceNotification extends IServiceNotification.Stub {
        ServiceNotification() {
        }

        public void onRegistration(String fqName, String name, boolean preexisting) {
            UsbPortManager.logAndPrint(4, (IndentingPrintWriter) null, "Usb hal service started " + fqName + " " + name);
            UsbPortManager.this.connectToProxy((IndentingPrintWriter) null);
        }
    }

    /* access modifiers changed from: private */
    public void connectToProxy(IndentingPrintWriter pw) {
        synchronized (this.mLock) {
            if (this.mProxy == null) {
                try {
                    this.mProxy = IUsb.getService();
                    this.mProxy.linkToDeath(new DeathRecipient(pw), 1000);
                    this.mProxy.setCallback(this.mHALCallback);
                    this.mProxy.queryPortStatus();
                } catch (NoSuchElementException e) {
                    logAndPrintException(pw, "connectToProxy: usb hal service not found. Did the service fail to start?", e);
                } catch (RemoteException e2) {
                    logAndPrintException(pw, "connectToProxy: usb hal service not responding", e2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updatePortsLocked(IndentingPrintWriter pw, ArrayList<RawPortInfo> newPortInfo) {
        IndentingPrintWriter indentingPrintWriter = pw;
        int i = this.mPorts.size();
        while (true) {
            int i2 = i - 1;
            if (i <= 0) {
                break;
            }
            this.mPorts.valueAt(i2).mDisposition = 3;
            i = i2;
        }
        if (!this.mSimulatedPorts.isEmpty()) {
            int count = this.mSimulatedPorts.size();
            int i3 = 0;
            while (i3 < count) {
                RawPortInfo portInfo = this.mSimulatedPorts.valueAt(i3);
                String str = portInfo.portId;
                int i4 = portInfo.supportedModes;
                int i5 = portInfo.supportedContaminantProtectionModes;
                int i6 = portInfo.currentMode;
                boolean z = portInfo.canChangeMode;
                int i7 = portInfo.currentPowerRole;
                boolean z2 = portInfo.canChangePowerRole;
                int i8 = portInfo.currentDataRole;
                boolean z3 = portInfo.canChangeDataRole;
                boolean z4 = portInfo.supportsEnableContaminantPresenceProtection;
                int i9 = portInfo.contaminantProtectionStatus;
                int count2 = count;
                boolean z5 = portInfo.supportsEnableContaminantPresenceDetection;
                int i10 = portInfo.contaminantDetectionStatus;
                RawPortInfo rawPortInfo = portInfo;
                addOrUpdatePortLocked(str, i4, i5, i6, z, i7, z2, i8, z3, z4, i9, z5, i10, pw);
                i3++;
                count = count2;
            }
            int i11 = i3;
            int i12 = count;
        } else {
            Iterator<RawPortInfo> it = newPortInfo.iterator();
            while (it.hasNext()) {
                RawPortInfo currentPortInfo = it.next();
                RawPortInfo rawPortInfo2 = currentPortInfo;
                addOrUpdatePortLocked(currentPortInfo.portId, currentPortInfo.supportedModes, currentPortInfo.supportedContaminantProtectionModes, currentPortInfo.currentMode, currentPortInfo.canChangeMode, currentPortInfo.currentPowerRole, currentPortInfo.canChangePowerRole, currentPortInfo.currentDataRole, currentPortInfo.canChangeDataRole, currentPortInfo.supportsEnableContaminantPresenceProtection, currentPortInfo.contaminantProtectionStatus, currentPortInfo.supportsEnableContaminantPresenceDetection, currentPortInfo.contaminantDetectionStatus, pw);
            }
        }
        int i13 = this.mPorts.size();
        while (true) {
            int i14 = i13 - 1;
            if (i13 > 0) {
                PortInfo portInfo2 = this.mPorts.valueAt(i14);
                int i15 = portInfo2.mDisposition;
                if (i15 == 0) {
                    handlePortAddedLocked(portInfo2, pw);
                    portInfo2.mDisposition = 2;
                } else if (i15 == 1) {
                    handlePortChangedLocked(portInfo2, pw);
                    portInfo2.mDisposition = 2;
                } else if (i15 != 3) {
                    IndentingPrintWriter indentingPrintWriter2 = pw;
                } else {
                    this.mPorts.removeAt(i14);
                    portInfo2.mUsbPortStatus = null;
                    handlePortRemovedLocked(portInfo2, pw);
                }
                i13 = i14;
            } else {
                IndentingPrintWriter indentingPrintWriter3 = pw;
                return;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00e1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addOrUpdatePortLocked(java.lang.String r24, int r25, int r26, int r27, boolean r28, int r29, boolean r30, int r31, boolean r32, boolean r33, int r34, boolean r35, int r36, com.android.internal.util.IndentingPrintWriter r37) {
        /*
            r23 = this;
            r0 = r23
            r8 = r24
            r9 = r25
            r1 = r27
            r15 = r29
            r14 = r31
            r13 = r33
            r12 = r35
            r11 = r37
            r2 = r9 & 3
            r3 = 3
            r4 = 5
            if (r2 == r3) goto L_0x004c
            r2 = 0
            if (r1 == 0) goto L_0x0047
            if (r1 == r9) goto L_0x0047
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "Ignoring inconsistent current mode from USB port driver: supportedModes="
            r3.append(r5)
            java.lang.String r5 = android.hardware.usb.UsbPort.modeToString(r25)
            r3.append(r5)
            java.lang.String r5 = ", currentMode="
            r3.append(r5)
            java.lang.String r5 = android.hardware.usb.UsbPort.modeToString(r27)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            logAndPrint(r4, r11, r3)
            r1 = 0
            r20 = r1
            r21 = r2
            goto L_0x0050
        L_0x0047:
            r20 = r1
            r21 = r2
            goto L_0x0050
        L_0x004c:
            r21 = r28
            r20 = r1
        L_0x0050:
            int r1 = android.hardware.usb.UsbPort.combineRolesAsBit(r15, r14)
            r2 = 2
            r3 = 1
            if (r20 == 0) goto L_0x0098
            if (r15 == 0) goto L_0x0098
            if (r14 == 0) goto L_0x0098
            if (r30 == 0) goto L_0x006f
            if (r32 == 0) goto L_0x006f
            int r5 = COMBO_SOURCE_HOST
            int r6 = COMBO_SOURCE_DEVICE
            r5 = r5 | r6
            int r6 = COMBO_SINK_HOST
            r5 = r5 | r6
            int r6 = COMBO_SINK_DEVICE
            r5 = r5 | r6
            r1 = r1 | r5
            r22 = r1
            goto L_0x009a
        L_0x006f:
            if (r30 == 0) goto L_0x007e
            int r5 = android.hardware.usb.UsbPort.combineRolesAsBit(r3, r14)
            r1 = r1 | r5
            int r5 = android.hardware.usb.UsbPort.combineRolesAsBit(r2, r14)
            r1 = r1 | r5
            r22 = r1
            goto L_0x009a
        L_0x007e:
            if (r32 == 0) goto L_0x008d
            int r5 = android.hardware.usb.UsbPort.combineRolesAsBit(r15, r3)
            r1 = r1 | r5
            int r5 = android.hardware.usb.UsbPort.combineRolesAsBit(r15, r2)
            r1 = r1 | r5
            r22 = r1
            goto L_0x009a
        L_0x008d:
            if (r21 == 0) goto L_0x0098
            int r5 = COMBO_SOURCE_HOST
            int r6 = COMBO_SINK_DEVICE
            r5 = r5 | r6
            r1 = r1 | r5
            r22 = r1
            goto L_0x009a
        L_0x0098:
            r22 = r1
        L_0x009a:
            android.util.ArrayMap<java.lang.String, com.android.server.usb.UsbPortManager$PortInfo> r1 = r0.mPorts
            java.lang.Object r1 = r1.get(r8)
            r10 = r1
            com.android.server.usb.UsbPortManager$PortInfo r10 = (com.android.server.usb.UsbPortManager.PortInfo) r10
            if (r10 != 0) goto L_0x00e1
            com.android.server.usb.UsbPortManager$PortInfo r16 = new com.android.server.usb.UsbPortManager$PortInfo
            android.content.Context r1 = r0.mContext
            java.lang.Class<android.hardware.usb.UsbManager> r2 = android.hardware.usb.UsbManager.class
            java.lang.Object r1 = r1.getSystemService(r2)
            r2 = r1
            android.hardware.usb.UsbManager r2 = (android.hardware.usb.UsbManager) r2
            r1 = r16
            r3 = r24
            r4 = r25
            r5 = r26
            r6 = r33
            r7 = r35
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r10 = r1
            r5 = r11
            r11 = r20
            r6 = r12
            r12 = r21
            r7 = r13
            r13 = r29
            r14 = r30
            r15 = r31
            r16 = r32
            r17 = r22
            r18 = r34
            r19 = r36
            r10.setStatus(r11, r12, r13, r14, r15, r16, r17, r18, r19)
            android.util.ArrayMap<java.lang.String, com.android.server.usb.UsbPortManager$PortInfo> r2 = r0.mPorts
            r2.put(r8, r1)
            goto L_0x0184
        L_0x00e1:
            r5 = r11
            r6 = r12
            r7 = r13
            android.hardware.usb.UsbPort r1 = r10.mUsbPort
            int r1 = r1.getSupportedModes()
            java.lang.String r11 = ", current="
            if (r9 == r1) goto L_0x0116
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r12 = "Ignoring inconsistent list of supported modes from USB port driver (should be immutable): previous="
            r1.append(r12)
            android.hardware.usb.UsbPort r12 = r10.mUsbPort
            int r12 = r12.getSupportedModes()
            java.lang.String r12 = android.hardware.usb.UsbPort.modeToString(r12)
            r1.append(r12)
            r1.append(r11)
            java.lang.String r12 = android.hardware.usb.UsbPort.modeToString(r25)
            r1.append(r12)
            java.lang.String r1 = r1.toString()
            logAndPrint(r4, r5, r1)
        L_0x0116:
            android.hardware.usb.UsbPort r1 = r10.mUsbPort
            boolean r1 = r1.supportsEnableContaminantPresenceProtection()
            if (r7 == r1) goto L_0x013e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r12 = "Ignoring inconsistent supportsEnableContaminantPresenceProtectionUSB port driver (should be immutable): previous="
            r1.append(r12)
            android.hardware.usb.UsbPort r12 = r10.mUsbPort
            boolean r12 = r12.supportsEnableContaminantPresenceProtection()
            r1.append(r12)
            r1.append(r11)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            logAndPrint(r4, r5, r1)
        L_0x013e:
            android.hardware.usb.UsbPort r1 = r10.mUsbPort
            boolean r1 = r1.supportsEnableContaminantPresenceDetection()
            if (r6 == r1) goto L_0x0166
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r12 = "Ignoring inconsistent supportsEnableContaminantPresenceDetection USB port driver (should be immutable): previous="
            r1.append(r12)
            android.hardware.usb.UsbPort r12 = r10.mUsbPort
            boolean r12 = r12.supportsEnableContaminantPresenceDetection()
            r1.append(r12)
            r1.append(r11)
            r1.append(r6)
            java.lang.String r1 = r1.toString()
            logAndPrint(r4, r5, r1)
        L_0x0166:
            r1 = r10
            r11 = r20
            r12 = r21
            r13 = r29
            r14 = r30
            r15 = r31
            r16 = r32
            r17 = r22
            r18 = r34
            r19 = r36
            boolean r4 = r10.setStatus(r11, r12, r13, r14, r15, r16, r17, r18, r19)
            if (r4 == 0) goto L_0x0182
            r1.mDisposition = r3
            goto L_0x0184
        L_0x0182:
            r1.mDisposition = r2
        L_0x0184:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbPortManager.addOrUpdatePortLocked(java.lang.String, int, int, int, boolean, int, boolean, int, boolean, boolean, int, boolean, int, com.android.internal.util.IndentingPrintWriter):void");
    }

    private void handlePortLocked(PortInfo portInfo, IndentingPrintWriter pw) {
        sendPortChangedBroadcastLocked(portInfo);
        logToStatsd(portInfo, pw);
        updateContaminantNotification();
    }

    private void handlePortAddedLocked(PortInfo portInfo, IndentingPrintWriter pw) {
        logAndPrint(4, pw, "USB port added: " + portInfo);
        handlePortLocked(portInfo, pw);
    }

    private void handlePortChangedLocked(PortInfo portInfo, IndentingPrintWriter pw) {
        logAndPrint(4, pw, "USB port changed: " + portInfo);
        enableContaminantDetectionIfNeeded(portInfo, pw);
        handlePortLocked(portInfo, pw);
    }

    private void handlePortRemovedLocked(PortInfo portInfo, IndentingPrintWriter pw) {
        logAndPrint(4, pw, "USB port removed: " + portInfo);
        handlePortLocked(portInfo, pw);
    }

    private static int convertContaminantDetectionStatusToProto(int contaminantDetectionStatus) {
        if (contaminantDetectionStatus == 0) {
            return 1;
        }
        if (contaminantDetectionStatus == 1) {
            return 2;
        }
        if (contaminantDetectionStatus == 2) {
            return 3;
        }
        if (contaminantDetectionStatus != 3) {
            return 0;
        }
        return 4;
    }

    private void sendPortChangedBroadcastLocked(PortInfo portInfo) {
        Intent intent = new Intent("android.hardware.usb.action.USB_PORT_CHANGED");
        intent.addFlags(285212672);
        intent.putExtra("port", ParcelableUsbPort.of(portInfo.mUsbPort));
        intent.putExtra("portStatus", portInfo.mUsbPortStatus);
        this.mHandler.post(new Runnable(intent) {
            private final /* synthetic */ Intent f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                UsbPortManager.this.lambda$sendPortChangedBroadcastLocked$0$UsbPortManager(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$sendPortChangedBroadcastLocked$0$UsbPortManager(Intent intent) {
        this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL, "android.permission.MANAGE_USB");
    }

    private void enableContaminantDetectionIfNeeded(PortInfo portInfo, IndentingPrintWriter pw) {
        if (this.mConnected.containsKey(portInfo.mUsbPort.getId()) && this.mConnected.get(portInfo.mUsbPort.getId()).booleanValue() && !portInfo.mUsbPortStatus.isConnected() && portInfo.mUsbPortStatus.getContaminantDetectionStatus() == 1) {
            enableContaminantDetection(portInfo.mUsbPort.getId(), true, pw);
        }
    }

    private void logToStatsd(PortInfo portInfo, IndentingPrintWriter pw) {
        int i = 0;
        if (portInfo.mUsbPortStatus == null) {
            if (this.mConnected.containsKey(portInfo.mUsbPort.getId())) {
                if (this.mConnected.get(portInfo.mUsbPort.getId()).booleanValue()) {
                    StatsLog.write(70, 0, portInfo.mUsbPort.getId(), portInfo.mLastConnectDurationMillis);
                }
                this.mConnected.remove(portInfo.mUsbPort.getId());
            }
            if (this.mContaminantStatus.containsKey(portInfo.mUsbPort.getId())) {
                if (this.mContaminantStatus.get(portInfo.mUsbPort.getId()).intValue() == 3) {
                    StatsLog.write(146, portInfo.mUsbPort.getId(), convertContaminantDetectionStatusToProto(2));
                }
                this.mContaminantStatus.remove(portInfo.mUsbPort.getId());
                return;
            }
            return;
        }
        if (!this.mConnected.containsKey(portInfo.mUsbPort.getId()) || this.mConnected.get(portInfo.mUsbPort.getId()).booleanValue() != portInfo.mUsbPortStatus.isConnected()) {
            this.mConnected.put(portInfo.mUsbPort.getId(), Boolean.valueOf(portInfo.mUsbPortStatus.isConnected()));
            if (portInfo.mUsbPortStatus.isConnected()) {
                i = 1;
            }
            StatsLog.write(70, i, portInfo.mUsbPort.getId(), portInfo.mLastConnectDurationMillis);
        }
        if (!this.mContaminantStatus.containsKey(portInfo.mUsbPort.getId()) || this.mContaminantStatus.get(portInfo.mUsbPort.getId()).intValue() != portInfo.mUsbPortStatus.getContaminantDetectionStatus()) {
            this.mContaminantStatus.put(portInfo.mUsbPort.getId(), Integer.valueOf(portInfo.mUsbPortStatus.getContaminantDetectionStatus()));
            StatsLog.write(146, portInfo.mUsbPort.getId(), convertContaminantDetectionStatusToProto(portInfo.mUsbPortStatus.getContaminantDetectionStatus()));
        }
    }

    /* access modifiers changed from: private */
    public static void logAndPrint(int priority, IndentingPrintWriter pw, String msg) {
        Slog.println(priority, TAG, msg);
        if (pw != null) {
            pw.println(msg);
        }
    }

    private static void logAndPrintException(IndentingPrintWriter pw, String msg, Exception e) {
        Slog.e(TAG, msg, e);
        if (pw != null) {
            pw.println(msg + e);
        }
    }

    private static final class PortInfo {
        public static final int DISPOSITION_ADDED = 0;
        public static final int DISPOSITION_CHANGED = 1;
        public static final int DISPOSITION_READY = 2;
        public static final int DISPOSITION_REMOVED = 3;
        public boolean mCanChangeDataRole;
        public boolean mCanChangeMode;
        public boolean mCanChangePowerRole;
        public long mConnectedAtMillis;
        public int mDisposition;
        public long mLastConnectDurationMillis;
        public final UsbPort mUsbPort;
        public UsbPortStatus mUsbPortStatus;

        PortInfo(UsbManager usbManager, String portId, int supportedModes, int supportedContaminantProtectionModes, boolean supportsEnableContaminantPresenceDetection, boolean supportsEnableContaminantPresenceProtection) {
            this.mUsbPort = new UsbPort(usbManager, portId, supportedModes, supportedContaminantProtectionModes, supportsEnableContaminantPresenceDetection, supportsEnableContaminantPresenceProtection);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0037, code lost:
            if (r0.mUsbPortStatus.getSupportedRoleCombinations() != r24) goto L_0x0051;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean setStatus(int r18, boolean r19, int r20, boolean r21, int r22, boolean r23, int r24) {
            /*
                r17 = this;
                r0 = r17
                r1 = 0
                r2 = r19
                r0.mCanChangeMode = r2
                r3 = r21
                r0.mCanChangePowerRole = r3
                r4 = r23
                r0.mCanChangeDataRole = r4
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                if (r5 == 0) goto L_0x0049
                int r5 = r5.getCurrentMode()
                r13 = r18
                if (r5 != r13) goto L_0x0042
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getCurrentPowerRole()
                r14 = r20
                if (r5 != r14) goto L_0x003d
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getCurrentDataRole()
                r15 = r22
                if (r5 != r15) goto L_0x003a
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getSupportedRoleCombinations()
                r12 = r24
                if (r5 == r12) goto L_0x0067
                goto L_0x0051
            L_0x003a:
                r12 = r24
                goto L_0x0051
            L_0x003d:
                r15 = r22
                r12 = r24
                goto L_0x0051
            L_0x0042:
                r14 = r20
                r15 = r22
                r12 = r24
                goto L_0x0051
            L_0x0049:
                r13 = r18
                r14 = r20
                r15 = r22
                r12 = r24
            L_0x0051:
                android.hardware.usb.UsbPortStatus r5 = new android.hardware.usb.UsbPortStatus
                r11 = 0
                r16 = 0
                r6 = r5
                r7 = r18
                r8 = r20
                r9 = r22
                r10 = r24
                r12 = r16
                r6.<init>(r7, r8, r9, r10, r11, r12)
                r0.mUsbPortStatus = r5
                r1 = 1
            L_0x0067:
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                boolean r5 = r5.isConnected()
                r6 = 0
                if (r5 == 0) goto L_0x0080
                long r8 = r0.mConnectedAtMillis
                int r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r5 != 0) goto L_0x0080
                long r8 = android.os.SystemClock.elapsedRealtime()
                r0.mConnectedAtMillis = r8
                r0.mLastConnectDurationMillis = r6
                goto L_0x0099
            L_0x0080:
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                boolean r5 = r5.isConnected()
                if (r5 != 0) goto L_0x0099
                long r8 = r0.mConnectedAtMillis
                int r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r5 == 0) goto L_0x0099
                long r8 = android.os.SystemClock.elapsedRealtime()
                long r10 = r0.mConnectedAtMillis
                long r8 = r8 - r10
                r0.mLastConnectDurationMillis = r8
                r0.mConnectedAtMillis = r6
            L_0x0099:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbPortManager.PortInfo.setStatus(int, boolean, int, boolean, int, boolean, int):boolean");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:13:0x004b, code lost:
            if (r0.mUsbPortStatus.getContaminantDetectionStatus() != r25) goto L_0x007d;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean setStatus(int r17, boolean r18, int r19, boolean r20, int r21, boolean r22, int r23, int r24, int r25) {
            /*
                r16 = this;
                r0 = r16
                r1 = 0
                r2 = r18
                r0.mCanChangeMode = r2
                r3 = r20
                r0.mCanChangePowerRole = r3
                r4 = r22
                r0.mCanChangeDataRole = r4
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                if (r5 == 0) goto L_0x0071
                int r5 = r5.getCurrentMode()
                r13 = r17
                if (r5 != r13) goto L_0x0066
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getCurrentPowerRole()
                r14 = r19
                if (r5 != r14) goto L_0x005d
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getCurrentDataRole()
                r15 = r21
                if (r5 != r15) goto L_0x0056
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getSupportedRoleCombinations()
                r12 = r23
                if (r5 != r12) goto L_0x0051
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getContaminantProtectionStatus()
                r11 = r24
                if (r5 != r11) goto L_0x004e
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                int r5 = r5.getContaminantDetectionStatus()
                r10 = r25
                if (r5 == r10) goto L_0x0092
                goto L_0x007d
            L_0x004e:
                r10 = r25
                goto L_0x007d
            L_0x0051:
                r11 = r24
                r10 = r25
                goto L_0x007d
            L_0x0056:
                r12 = r23
                r11 = r24
                r10 = r25
                goto L_0x007d
            L_0x005d:
                r15 = r21
                r12 = r23
                r11 = r24
                r10 = r25
                goto L_0x007d
            L_0x0066:
                r14 = r19
                r15 = r21
                r12 = r23
                r11 = r24
                r10 = r25
                goto L_0x007d
            L_0x0071:
                r13 = r17
                r14 = r19
                r15 = r21
                r12 = r23
                r11 = r24
                r10 = r25
            L_0x007d:
                android.hardware.usb.UsbPortStatus r5 = new android.hardware.usb.UsbPortStatus
                r6 = r5
                r7 = r17
                r8 = r19
                r9 = r21
                r10 = r23
                r11 = r24
                r12 = r25
                r6.<init>(r7, r8, r9, r10, r11, r12)
                r0.mUsbPortStatus = r5
                r1 = 1
            L_0x0092:
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                boolean r5 = r5.isConnected()
                r6 = 0
                if (r5 == 0) goto L_0x00ab
                long r8 = r0.mConnectedAtMillis
                int r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r5 != 0) goto L_0x00ab
                long r8 = android.os.SystemClock.elapsedRealtime()
                r0.mConnectedAtMillis = r8
                r0.mLastConnectDurationMillis = r6
                goto L_0x00c4
            L_0x00ab:
                android.hardware.usb.UsbPortStatus r5 = r0.mUsbPortStatus
                boolean r5 = r5.isConnected()
                if (r5 != 0) goto L_0x00c4
                long r8 = r0.mConnectedAtMillis
                int r5 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r5 == 0) goto L_0x00c4
                long r8 = android.os.SystemClock.elapsedRealtime()
                long r10 = r0.mConnectedAtMillis
                long r8 = r8 - r10
                r0.mLastConnectDurationMillis = r8
                r0.mConnectedAtMillis = r6
            L_0x00c4:
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbPortManager.PortInfo.setStatus(int, boolean, int, boolean, int, boolean, int, int, int):boolean");
        }

        /* access modifiers changed from: package-private */
        public void dump(DualDumpOutputStream dump, String idName, long id) {
            long token = dump.start(idName, id);
            DumpUtils.writePort(dump, "port", 1146756268033L, this.mUsbPort);
            DumpUtils.writePortStatus(dump, "status", 1146756268034L, this.mUsbPortStatus);
            dump.write("can_change_mode", 1133871366147L, this.mCanChangeMode);
            dump.write("can_change_power_role", 1133871366148L, this.mCanChangePowerRole);
            dump.write("can_change_data_role", 1133871366149L, this.mCanChangeDataRole);
            DualDumpOutputStream dualDumpOutputStream = dump;
            dualDumpOutputStream.write("connected_at_millis", 1112396529670L, this.mConnectedAtMillis);
            dualDumpOutputStream.write("last_connect_duration_millis", 1112396529671L, this.mLastConnectDurationMillis);
            dump.end(token);
        }

        public String toString() {
            return "port=" + this.mUsbPort + ", status=" + this.mUsbPortStatus + ", canChangeMode=" + this.mCanChangeMode + ", canChangePowerRole=" + this.mCanChangePowerRole + ", canChangeDataRole=" + this.mCanChangeDataRole + ", connectedAtMillis=" + this.mConnectedAtMillis + ", lastConnectDurationMillis=" + this.mLastConnectDurationMillis;
        }
    }

    private static final class RawPortInfo implements Parcelable {
        public static final Parcelable.Creator<RawPortInfo> CREATOR = new Parcelable.Creator<RawPortInfo>() {
            public RawPortInfo createFromParcel(Parcel in) {
                return new RawPortInfo(in.readString(), in.readInt(), in.readInt(), in.readInt(), in.readByte() != 0, in.readInt(), in.readByte() != 0, in.readInt(), in.readByte() != 0, in.readBoolean(), in.readInt(), in.readBoolean(), in.readInt());
            }

            public RawPortInfo[] newArray(int size) {
                return new RawPortInfo[size];
            }
        };
        public boolean canChangeDataRole;
        public boolean canChangeMode;
        public boolean canChangePowerRole;
        public int contaminantDetectionStatus;
        public int contaminantProtectionStatus;
        public int currentDataRole;
        public int currentMode;
        public int currentPowerRole;
        public final String portId;
        public final int supportedContaminantProtectionModes;
        public final int supportedModes;
        public boolean supportsEnableContaminantPresenceDetection;
        public boolean supportsEnableContaminantPresenceProtection;

        RawPortInfo(String portId2, int supportedModes2) {
            this.portId = portId2;
            this.supportedModes = supportedModes2;
            this.supportedContaminantProtectionModes = 0;
            this.supportsEnableContaminantPresenceProtection = false;
            this.contaminantProtectionStatus = 0;
            this.supportsEnableContaminantPresenceDetection = false;
            this.contaminantDetectionStatus = 0;
        }

        RawPortInfo(String portId2, int supportedModes2, int supportedContaminantProtectionModes2, int currentMode2, boolean canChangeMode2, int currentPowerRole2, boolean canChangePowerRole2, int currentDataRole2, boolean canChangeDataRole2, boolean supportsEnableContaminantPresenceProtection2, int contaminantProtectionStatus2, boolean supportsEnableContaminantPresenceDetection2, int contaminantDetectionStatus2) {
            this.portId = portId2;
            this.supportedModes = supportedModes2;
            this.supportedContaminantProtectionModes = supportedContaminantProtectionModes2;
            this.currentMode = currentMode2;
            this.canChangeMode = canChangeMode2;
            this.currentPowerRole = currentPowerRole2;
            this.canChangePowerRole = canChangePowerRole2;
            this.currentDataRole = currentDataRole2;
            this.canChangeDataRole = canChangeDataRole2;
            this.supportsEnableContaminantPresenceProtection = supportsEnableContaminantPresenceProtection2;
            this.contaminantProtectionStatus = contaminantProtectionStatus2;
            this.supportsEnableContaminantPresenceDetection = supportsEnableContaminantPresenceDetection2;
            this.contaminantDetectionStatus = contaminantDetectionStatus2;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.portId);
            dest.writeInt(this.supportedModes);
            dest.writeInt(this.supportedContaminantProtectionModes);
            dest.writeInt(this.currentMode);
            dest.writeByte(this.canChangeMode ? (byte) 1 : 0);
            dest.writeInt(this.currentPowerRole);
            dest.writeByte(this.canChangePowerRole ? (byte) 1 : 0);
            dest.writeInt(this.currentDataRole);
            dest.writeByte(this.canChangeDataRole ? (byte) 1 : 0);
            dest.writeBoolean(this.supportsEnableContaminantPresenceProtection);
            dest.writeInt(this.contaminantProtectionStatus);
            dest.writeBoolean(this.supportsEnableContaminantPresenceDetection);
            dest.writeInt(this.contaminantDetectionStatus);
        }
    }
}
