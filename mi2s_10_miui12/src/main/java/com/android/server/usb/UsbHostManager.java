package com.android.server.usb;

import android.content.ComponentName;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.StatsLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.dump.DualDumpOutputStream;
import com.android.internal.util.dump.DumpUtils;
import com.android.server.net.watchlist.WatchlistLoggingHandler;
import com.android.server.usb.descriptors.UsbDescriptor;
import com.android.server.usb.descriptors.UsbDescriptorParser;
import com.android.server.usb.descriptors.UsbDeviceDescriptor;
import com.android.server.usb.descriptors.UsbInterfaceDescriptor;
import com.android.server.usb.descriptors.report.TextReportCanvas;
import com.android.server.usb.descriptors.tree.UsbDescriptorsTree;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class UsbHostManager {
    private static final boolean DEBUG = false;
    private static final int LINUX_FOUNDATION_VID = 7531;
    private static final int MAX_CONNECT_RECORDS = 32;
    private static final String TAG = UsbHostManager.class.getSimpleName();
    static final SimpleDateFormat sFormat = new SimpleDateFormat("MM-dd HH:mm:ss:SSS");
    private final ArrayMap<String, ConnectionRecord> mConnected = new ArrayMap<>();
    private final LinkedList<ConnectionRecord> mConnections = new LinkedList<>();
    private final Context mContext;
    @GuardedBy({"mSettingsLock"})
    private UsbProfileGroupSettingsManager mCurrentSettings;
    @GuardedBy({"mLock"})
    private final HashMap<String, UsbDevice> mDevices = new HashMap<>();
    private Object mHandlerLock = new Object();
    private final String[] mHostBlacklist;
    private ConnectionRecord mLastConnect;
    private final Object mLock = new Object();
    private int mNumConnects;
    private Object mSettingsLock = new Object();
    private final UsbSettingsManager mSettingsManager;
    private final UsbAlsaManager mUsbAlsaManager;
    @GuardedBy({"mHandlerLock"})
    private ComponentName mUsbDeviceConnectionHandler;

    /* access modifiers changed from: private */
    public native void monitorUsbHostBus();

    private native ParcelFileDescriptor nativeOpenDevice(String str);

    class ConnectionRecord {
        static final int CONNECT = 0;
        static final int CONNECT_BADDEVICE = 2;
        static final int CONNECT_BADPARSE = 1;
        static final int DISCONNECT = -1;
        private static final int kDumpBytesPerLine = 16;
        final byte[] mDescriptors;
        String mDeviceAddress;
        final int mMode;
        long mTimestamp = System.currentTimeMillis();

        ConnectionRecord(String deviceAddress, int mode, byte[] descriptors) {
            this.mDeviceAddress = deviceAddress;
            this.mMode = mode;
            this.mDescriptors = descriptors;
        }

        private String formatTime() {
            return new StringBuilder(UsbHostManager.sFormat.format(new Date(this.mTimestamp))).toString();
        }

        /* access modifiers changed from: package-private */
        public void dump(DualDumpOutputStream dump, String idName, long id) {
            DualDumpOutputStream dualDumpOutputStream = dump;
            long token = dump.start(idName, id);
            dump.write("device_address", 1138166333441L, this.mDeviceAddress);
            dump.write("mode", 1159641169922L, this.mMode);
            dump.write(WatchlistLoggingHandler.WatchlistEventKeys.TIMESTAMP, 1112396529667L, this.mTimestamp);
            if (this.mMode != -1) {
                UsbDescriptorParser parser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                UsbDeviceDescriptor deviceDescriptor = parser.getDeviceDescriptor();
                dump.write("manufacturer", 1120986464260L, deviceDescriptor.getVendorID());
                dump.write("product", 1120986464261L, deviceDescriptor.getProductID());
                long isHeadSetToken = dump.start("is_headset", 1146756268038L);
                dump.write("in", 1133871366145L, parser.isInputHeadset());
                dump.write("out", 1133871366146L, parser.isOutputHeadset());
                dump.end(isHeadSetToken);
            }
            dump.end(token);
        }

        /* access modifiers changed from: package-private */
        public void dumpShort(IndentingPrintWriter pw) {
            if (this.mMode != -1) {
                pw.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                UsbDescriptorParser parser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                UsbDeviceDescriptor deviceDescriptor = parser.getDeviceDescriptor();
                pw.println("manfacturer:0x" + Integer.toHexString(deviceDescriptor.getVendorID()) + " product:" + Integer.toHexString(deviceDescriptor.getProductID()));
                pw.println("isHeadset[in: " + parser.isInputHeadset() + " , out: " + parser.isOutputHeadset() + "]");
                return;
            }
            pw.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }

        /* access modifiers changed from: package-private */
        public void dumpTree(IndentingPrintWriter pw) {
            if (this.mMode != -1) {
                pw.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                UsbDescriptorParser parser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                StringBuilder stringBuilder = new StringBuilder();
                UsbDescriptorsTree descriptorTree = new UsbDescriptorsTree();
                descriptorTree.parse(parser);
                descriptorTree.report(new TextReportCanvas(parser, stringBuilder));
                stringBuilder.append("isHeadset[in: " + parser.isInputHeadset() + " , out: " + parser.isOutputHeadset() + "]");
                pw.println(stringBuilder.toString());
                return;
            }
            pw.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }

        /* access modifiers changed from: package-private */
        public void dumpList(IndentingPrintWriter pw) {
            if (this.mMode != -1) {
                pw.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                UsbDescriptorParser parser = new UsbDescriptorParser(this.mDeviceAddress, this.mDescriptors);
                StringBuilder stringBuilder = new StringBuilder();
                TextReportCanvas canvas = new TextReportCanvas(parser, stringBuilder);
                Iterator<UsbDescriptor> it = parser.getDescriptors().iterator();
                while (it.hasNext()) {
                    it.next().report(canvas);
                }
                pw.println(stringBuilder.toString());
                pw.println("isHeadset[in: " + parser.isInputHeadset() + " , out: " + parser.isOutputHeadset() + "]");
                return;
            }
            pw.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }

        /* access modifiers changed from: package-private */
        public void dumpRaw(IndentingPrintWriter pw) {
            if (this.mMode != -1) {
                pw.println(formatTime() + " Connect " + this.mDeviceAddress + " mode:" + this.mMode);
                int length = this.mDescriptors.length;
                StringBuilder sb = new StringBuilder();
                sb.append("Raw Descriptors ");
                sb.append(length);
                sb.append(" bytes");
                pw.println(sb.toString());
                int dataOffset = 0;
                for (int line = 0; line < length / 16; line++) {
                    StringBuilder sb2 = new StringBuilder();
                    int offset = 0;
                    while (offset < 16) {
                        sb2.append("0x");
                        sb2.append(String.format("0x%02X", new Object[]{Byte.valueOf(this.mDescriptors[dataOffset])}));
                        sb2.append(" ");
                        offset++;
                        dataOffset++;
                    }
                    pw.println(sb2.toString());
                }
                StringBuilder sb3 = new StringBuilder();
                while (dataOffset < length) {
                    sb3.append("0x");
                    sb3.append(String.format("0x%02X", new Object[]{Byte.valueOf(this.mDescriptors[dataOffset])}));
                    sb3.append(" ");
                    dataOffset++;
                }
                pw.println(sb3.toString());
                return;
            }
            pw.println(formatTime() + " Disconnect " + this.mDeviceAddress);
        }
    }

    public UsbHostManager(Context context, UsbAlsaManager alsaManager, UsbSettingsManager settingsManager) {
        this.mContext = context;
        this.mHostBlacklist = context.getResources().getStringArray(17236081);
        this.mUsbAlsaManager = alsaManager;
        this.mSettingsManager = settingsManager;
        String deviceConnectionHandler = context.getResources().getString(17039692);
        if (!TextUtils.isEmpty(deviceConnectionHandler)) {
            setUsbDeviceConnectionHandler(ComponentName.unflattenFromString(deviceConnectionHandler));
        }
    }

    public void setCurrentUserSettings(UsbProfileGroupSettingsManager settings) {
        synchronized (this.mSettingsLock) {
            this.mCurrentSettings = settings;
        }
    }

    private UsbProfileGroupSettingsManager getCurrentUserSettings() {
        UsbProfileGroupSettingsManager usbProfileGroupSettingsManager;
        synchronized (this.mSettingsLock) {
            usbProfileGroupSettingsManager = this.mCurrentSettings;
        }
        return usbProfileGroupSettingsManager;
    }

    public void setUsbDeviceConnectionHandler(ComponentName usbDeviceConnectionHandler) {
        synchronized (this.mHandlerLock) {
            this.mUsbDeviceConnectionHandler = usbDeviceConnectionHandler;
        }
    }

    private ComponentName getUsbDeviceConnectionHandler() {
        ComponentName componentName;
        synchronized (this.mHandlerLock) {
            componentName = this.mUsbDeviceConnectionHandler;
        }
        return componentName;
    }

    private boolean isBlackListed(String deviceAddress) {
        for (String startsWith : this.mHostBlacklist) {
            if (deviceAddress.startsWith(startsWith)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlackListed(int clazz, int subClass) {
        if (clazz == 9) {
            return true;
        }
        if (clazz == 3 && subClass == 1) {
            return true;
        }
        return false;
    }

    private void addConnectionRecord(String deviceAddress, int mode, byte[] rawDescriptors) {
        this.mNumConnects++;
        while (this.mConnections.size() >= 32) {
            this.mConnections.removeFirst();
        }
        ConnectionRecord rec = new ConnectionRecord(deviceAddress, mode, rawDescriptors);
        this.mConnections.add(rec);
        if (mode != -1) {
            this.mLastConnect = rec;
        }
        if (mode == 0) {
            this.mConnected.put(deviceAddress, rec);
        } else if (mode == -1) {
            this.mConnected.remove(deviceAddress);
        }
    }

    private void logUsbDevice(UsbDescriptorParser descriptorParser) {
        UsbDescriptorParser usbDescriptorParser = descriptorParser;
        int vid = 0;
        int pid = 0;
        String mfg = "<unknown>";
        String product = "<unknown>";
        String version = "<unknown>";
        String serial = "<unknown>";
        UsbDeviceDescriptor deviceDescriptor = descriptorParser.getDeviceDescriptor();
        if (deviceDescriptor != null) {
            vid = deviceDescriptor.getVendorID();
            pid = deviceDescriptor.getProductID();
            mfg = deviceDescriptor.getMfgString(usbDescriptorParser);
            product = deviceDescriptor.getProductString(usbDescriptorParser);
            version = deviceDescriptor.getDeviceReleaseString();
            serial = deviceDescriptor.getSerialString(usbDescriptorParser);
        }
        if (vid != LINUX_FOUNDATION_VID) {
            boolean hasAudio = descriptorParser.hasAudioInterface();
            boolean hasHid = descriptorParser.hasHIDInterface();
            boolean hasStorage = descriptorParser.hasStorageInterface();
            Slog.d(TAG, (("USB device attached: " + String.format("vidpid %04x:%04x", new Object[]{Integer.valueOf(vid), Integer.valueOf(pid)})) + String.format(" mfg/product/ver/serial %s/%s/%s/%s", new Object[]{mfg, product, version, serial})) + String.format(" hasAudio/HID/Storage: %b/%b/%b", new Object[]{Boolean.valueOf(hasAudio), Boolean.valueOf(hasHid), Boolean.valueOf(hasStorage)}));
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00d1, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean usbDeviceAdded(java.lang.String r23, int r24, int r25, byte[] r26) {
        /*
            r22 = this;
            r1 = r22
            r2 = r23
            r3 = r24
            boolean r0 = r22.isBlackListed(r23)
            r4 = 0
            if (r0 == 0) goto L_0x000e
            return r4
        L_0x000e:
            r5 = r25
            boolean r0 = r1.isBlackListed(r3, r5)
            if (r0 == 0) goto L_0x0017
            return r4
        L_0x0017:
            com.android.server.usb.descriptors.UsbDescriptorParser r0 = new com.android.server.usb.descriptors.UsbDescriptorParser
            r6 = r26
            r0.<init>((java.lang.String) r2, (byte[]) r6)
            r7 = r0
            if (r3 != 0) goto L_0x0028
            boolean r0 = r1.checkUsbInterfacesBlackListed(r7)
            if (r0 != 0) goto L_0x0028
            return r4
        L_0x0028:
            r1.logUsbDevice(r7)
            java.lang.Object r8 = r1.mLock
            monitor-enter(r8)
            java.util.HashMap<java.lang.String, android.hardware.usb.UsbDevice> r0 = r1.mDevices     // Catch:{ all -> 0x00d3 }
            java.lang.Object r0 = r0.get(r2)     // Catch:{ all -> 0x00d3 }
            if (r0 == 0) goto L_0x004e
            java.lang.String r0 = TAG     // Catch:{ all -> 0x00d3 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d3 }
            r9.<init>()     // Catch:{ all -> 0x00d3 }
            java.lang.String r10 = "device already on mDevices list: "
            r9.append(r10)     // Catch:{ all -> 0x00d3 }
            r9.append(r2)     // Catch:{ all -> 0x00d3 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x00d3 }
            android.util.Slog.w(r0, r9)     // Catch:{ all -> 0x00d3 }
            monitor-exit(r8)     // Catch:{ all -> 0x00d3 }
            return r4
        L_0x004e:
            android.hardware.usb.UsbDevice$Builder r0 = r7.toAndroidUsbDevice()     // Catch:{ all -> 0x00d3 }
            if (r0 != 0) goto L_0x0064
            java.lang.String r4 = TAG     // Catch:{ all -> 0x00d3 }
            java.lang.String r9 = "Couldn't create UsbDevice object."
            android.util.Slog.e(r4, r9)     // Catch:{ all -> 0x00d3 }
            r4 = 2
            byte[] r9 = r7.getRawDescriptors()     // Catch:{ all -> 0x00d3 }
            r1.addConnectionRecord(r2, r4, r9)     // Catch:{ all -> 0x00d3 }
            goto L_0x00d0
        L_0x0064:
            com.android.server.usb.UsbSerialReader r9 = new com.android.server.usb.UsbSerialReader     // Catch:{ all -> 0x00d3 }
            android.content.Context r10 = r1.mContext     // Catch:{ all -> 0x00d3 }
            com.android.server.usb.UsbSettingsManager r11 = r1.mSettingsManager     // Catch:{ all -> 0x00d3 }
            java.lang.String r12 = r0.serialNumber     // Catch:{ all -> 0x00d3 }
            r9.<init>(r10, r11, r12)     // Catch:{ all -> 0x00d3 }
            android.hardware.usb.UsbDevice r10 = r0.build(r9)     // Catch:{ all -> 0x00d3 }
            r9.setDevice(r10)     // Catch:{ all -> 0x00d3 }
            java.util.HashMap<java.lang.String, android.hardware.usb.UsbDevice> r11 = r1.mDevices     // Catch:{ all -> 0x00d3 }
            r11.put(r2, r10)     // Catch:{ all -> 0x00d3 }
            java.lang.String r11 = TAG     // Catch:{ all -> 0x00d3 }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d3 }
            r12.<init>()     // Catch:{ all -> 0x00d3 }
            java.lang.String r13 = "Added device "
            r12.append(r13)     // Catch:{ all -> 0x00d3 }
            r12.append(r10)     // Catch:{ all -> 0x00d3 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x00d3 }
            android.util.Slog.d(r11, r12)     // Catch:{ all -> 0x00d3 }
            android.content.ComponentName r11 = r22.getUsbDeviceConnectionHandler()     // Catch:{ all -> 0x00d3 }
            if (r11 != 0) goto L_0x009f
            com.android.server.usb.UsbProfileGroupSettingsManager r12 = r22.getCurrentUserSettings()     // Catch:{ all -> 0x00d3 }
            r12.deviceAttached(r10)     // Catch:{ all -> 0x00d3 }
            goto L_0x00a6
        L_0x009f:
            com.android.server.usb.UsbProfileGroupSettingsManager r12 = r22.getCurrentUserSettings()     // Catch:{ all -> 0x00d3 }
            r12.deviceAttachedForFixedHandler(r10, r11)     // Catch:{ all -> 0x00d3 }
        L_0x00a6:
            com.android.server.usb.UsbAlsaManager r12 = r1.mUsbAlsaManager     // Catch:{ all -> 0x00d3 }
            r12.usbDeviceAdded(r2, r10, r7)     // Catch:{ all -> 0x00d3 }
            byte[] r12 = r7.getRawDescriptors()     // Catch:{ all -> 0x00d3 }
            r1.addConnectionRecord(r2, r4, r12)     // Catch:{ all -> 0x00d3 }
            r13 = 77
            int r14 = r10.getVendorId()     // Catch:{ all -> 0x00d3 }
            int r15 = r10.getProductId()     // Catch:{ all -> 0x00d3 }
            boolean r16 = r7.hasAudioInterface()     // Catch:{ all -> 0x00d3 }
            boolean r17 = r7.hasHIDInterface()     // Catch:{ all -> 0x00d3 }
            boolean r18 = r7.hasStorageInterface()     // Catch:{ all -> 0x00d3 }
            r19 = 1
            r20 = 0
            android.util.StatsLog.write(r13, r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x00d3 }
        L_0x00d0:
            monitor-exit(r8)     // Catch:{ all -> 0x00d3 }
            r0 = 1
            return r0
        L_0x00d3:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x00d3 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usb.UsbHostManager.usbDeviceAdded(java.lang.String, int, int, byte[]):boolean");
    }

    private void usbDeviceRemoved(String deviceAddress) {
        String str = deviceAddress;
        synchronized (this.mLock) {
            UsbDevice device = this.mDevices.remove(str);
            if (device != null) {
                String str2 = TAG;
                Slog.d(str2, "Removed device at " + str + ": " + device.getProductName());
                this.mUsbAlsaManager.usbDeviceRemoved(str);
                this.mSettingsManager.usbDeviceRemoved(device);
                getCurrentUserSettings().usbDeviceRemoved(device);
                ConnectionRecord current = this.mConnected.get(str);
                addConnectionRecord(str, -1, (byte[]) null);
                if (current != null) {
                    UsbDescriptorParser parser = new UsbDescriptorParser(str, current.mDescriptors);
                    StatsLog.write(77, device.getVendorId(), device.getProductId(), parser.hasAudioInterface(), parser.hasHIDInterface(), parser.hasStorageInterface(), 0, System.currentTimeMillis() - current.mTimestamp);
                }
            } else {
                String str3 = TAG;
                Slog.d(str3, "Removed device at " + str + " was already gone");
            }
        }
    }

    public void systemReady() {
        synchronized (this.mLock) {
            new Thread((ThreadGroup) null, new Runnable() {
                public final void run() {
                    UsbHostManager.this.monitorUsbHostBus();
                }
            }, "UsbService host thread").start();
        }
    }

    public void getDeviceList(Bundle devices) {
        synchronized (this.mLock) {
            for (String name : this.mDevices.keySet()) {
                devices.putParcelable(name, this.mDevices.get(name));
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public ParcelFileDescriptor openDevice(String deviceAddress, UsbUserSettingsManager settings, String packageName, int uid) {
        ParcelFileDescriptor nativeOpenDevice;
        synchronized (this.mLock) {
            if (!isBlackListed(deviceAddress)) {
                UsbDevice device = this.mDevices.get(deviceAddress);
                if (device != null) {
                    settings.checkPermission(device, packageName, uid);
                    nativeOpenDevice = nativeOpenDevice(deviceAddress);
                } else {
                    throw new IllegalArgumentException("device " + deviceAddress + " does not exist or is restricted");
                }
            } else {
                throw new SecurityException("USB device is on a restricted bus");
            }
        }
        return nativeOpenDevice;
    }

    public void dump(DualDumpOutputStream dump, String idName, long id) {
        long token = dump.start(idName, id);
        synchronized (this.mHandlerLock) {
            if (this.mUsbDeviceConnectionHandler != null) {
                DumpUtils.writeComponentName(dump, "default_usb_host_connection_handler", 1146756268033L, this.mUsbDeviceConnectionHandler);
            }
        }
        synchronized (this.mLock) {
            for (String name : this.mDevices.keySet()) {
                com.android.internal.usb.DumpUtils.writeDevice(dump, "devices", 2246267895810L, this.mDevices.get(name));
            }
            dump.write("num_connects", 1120986464259L, this.mNumConnects);
            Iterator it = this.mConnections.iterator();
            while (it.hasNext()) {
                ((ConnectionRecord) it.next()).dump(dump, "connections", 2246267895812L);
            }
        }
        dump.end(token);
    }

    public void dumpDescriptors(IndentingPrintWriter pw, String[] args) {
        if (this.mLastConnect != null) {
            pw.println("Last Connected USB Device:");
            if (args.length <= 1 || args[1].equals("-dump-short")) {
                this.mLastConnect.dumpShort(pw);
            } else if (args[1].equals("-dump-tree")) {
                this.mLastConnect.dumpTree(pw);
            } else if (args[1].equals("-dump-list")) {
                this.mLastConnect.dumpList(pw);
            } else if (args[1].equals("-dump-raw")) {
                this.mLastConnect.dumpRaw(pw);
            }
        } else {
            pw.println("No USB Devices have been connected.");
        }
    }

    private boolean checkUsbInterfacesBlackListed(UsbDescriptorParser parser) {
        boolean shouldIgnoreDevice = false;
        Iterator<UsbDescriptor> it = parser.getDescriptors().iterator();
        while (it.hasNext()) {
            UsbDescriptor descriptor = it.next();
            if (descriptor instanceof UsbInterfaceDescriptor) {
                UsbInterfaceDescriptor iface = (UsbInterfaceDescriptor) descriptor;
                shouldIgnoreDevice = isBlackListed(iface.getUsbClass(), iface.getUsbSubclass());
                if (!shouldIgnoreDevice) {
                    break;
                }
            }
        }
        if (shouldIgnoreDevice) {
            return false;
        }
        return true;
    }
}
