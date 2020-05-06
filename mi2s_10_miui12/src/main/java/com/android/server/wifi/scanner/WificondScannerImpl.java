package com.android.server.wifi.scanner;

import android.app.AlarmManager;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiScanner;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.android.server.wifi.Clock;
import com.android.server.wifi.ScanDetail;
import com.android.server.wifi.ScoringParams;
import com.android.server.wifi.WifiGbk;
import com.android.server.wifi.WifiMonitor;
import com.android.server.wifi.WifiNative;
import com.android.server.wifi.scanner.ChannelHelper;
import com.android.server.wifi.util.ScanResultUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.concurrent.GuardedBy;
import miui.app.constants.ThemeManagerConstants;

public class WificondScannerImpl extends WifiScannerImpl implements Handler.Callback {
    private static final boolean DBG = false;
    private static final int MAX_APS_PER_SCAN = 32;
    public static final int MAX_HIDDEN_NETWORK_IDS_PER_SCAN = 16;
    private static final int MAX_SCAN_BUCKETS = 16;
    private static final int SCAN_BUFFER_CAPACITY = 10;
    private static final long SCAN_TIMEOUT_MS = 15000;
    private static final String TAG = "WificondScannerImpl";
    public static final String TIMEOUT_ALARM_TAG = "WificondScannerImpl Scan Timeout";
    private final AlarmManager mAlarmManager;
    private final ChannelHelper mChannelHelper;
    private final Clock mClock;
    private final Context mContext;
    private final Handler mEventHandler;
    private final boolean mHwPnoScanSupported;
    private final String mIfaceName;
    private LastPnoScanSettings mLastPnoScanSettings = null;
    private LastScanSettings mLastScanSettings = null;
    private WifiScanner.ScanData mLatestSingleScanResult = new WifiScanner.ScanData(0, 0, new ScanResult[0]);
    private ArrayList<ScanDetail> mNativePnoScanResults;
    private ArrayList<ScanDetail> mNativeScanResults;
    @GuardedBy("mSettingsLock")
    private AlarmManager.OnAlarmListener mScanTimeoutListener;
    private final Object mSettingsLock = new Object();
    private final WifiMonitor mWifiMonitor;
    private final WifiNative mWifiNative;

    public WificondScannerImpl(Context context, String ifaceName, WifiNative wifiNative, WifiMonitor wifiMonitor, ChannelHelper channelHelper, Looper looper, Clock clock) {
        this.mContext = context;
        this.mIfaceName = ifaceName;
        this.mWifiNative = wifiNative;
        this.mWifiMonitor = wifiMonitor;
        this.mChannelHelper = channelHelper;
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_ALARM);
        this.mEventHandler = new Handler(looper, this);
        this.mClock = clock;
        this.mHwPnoScanSupported = this.mContext.getResources().getBoolean(17891580);
        wifiMonitor.registerHandler(this.mIfaceName, WifiMonitor.SCAN_FAILED_EVENT, this.mEventHandler);
        wifiMonitor.registerHandler(this.mIfaceName, WifiMonitor.PNO_SCAN_RESULTS_EVENT, this.mEventHandler);
        wifiMonitor.registerHandler(this.mIfaceName, WifiMonitor.SCAN_RESULTS_EVENT, this.mEventHandler);
    }

    public void cleanup() {
        synchronized (this.mSettingsLock) {
            stopHwPnoScan();
            this.mLastScanSettings = null;
            this.mLastPnoScanSettings = null;
            this.mWifiMonitor.deregisterHandler(this.mIfaceName, WifiMonitor.SCAN_FAILED_EVENT, this.mEventHandler);
            this.mWifiMonitor.deregisterHandler(this.mIfaceName, WifiMonitor.PNO_SCAN_RESULTS_EVENT, this.mEventHandler);
            this.mWifiMonitor.deregisterHandler(this.mIfaceName, WifiMonitor.SCAN_RESULTS_EVENT, this.mEventHandler);
        }
    }

    public boolean getScanCapabilities(WifiNative.ScanCapabilities capabilities) {
        capabilities.max_scan_cache_size = ScoringParams.Values.MAX_EXPID;
        capabilities.max_scan_buckets = 16;
        capabilities.max_ap_cache_per_scan = 32;
        capabilities.max_rssi_sample_size = 8;
        capabilities.max_scan_reporting_threshold = 10;
        return true;
    }

    public ChannelHelper getChannelHelper() {
        return this.mChannelHelper;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00d3, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startSingleScan(com.android.server.wifi.WifiNative.ScanSettings r21, com.android.server.wifi.WifiNative.ScanEventHandler r22) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r9 = r22
            r0 = 0
            if (r9 == 0) goto L_0x00d8
            if (r2 != 0) goto L_0x000d
            goto L_0x00d8
        L_0x000d:
            java.lang.Object r10 = r1.mSettingsLock
            monitor-enter(r10)
            com.android.server.wifi.scanner.WificondScannerImpl$LastScanSettings r3 = r1.mLastScanSettings     // Catch:{ all -> 0x00d5 }
            if (r3 == 0) goto L_0x001d
            java.lang.String r3 = "WificondScannerImpl"
            java.lang.String r4 = "A single scan is already running"
            android.util.Log.w(r3, r4)     // Catch:{ all -> 0x00d5 }
            monitor-exit(r10)     // Catch:{ all -> 0x00d5 }
            return r0
        L_0x001d:
            com.android.server.wifi.scanner.ChannelHelper r3 = r1.mChannelHelper     // Catch:{ all -> 0x00d5 }
            com.android.server.wifi.scanner.ChannelHelper$ChannelCollection r3 = r3.createChannelCollection()     // Catch:{ all -> 0x00d5 }
            r11 = r3
            r3 = 0
            r12 = r3
        L_0x0026:
            int r3 = r2.num_buckets     // Catch:{ all -> 0x00d5 }
            if (r0 >= r3) goto L_0x003b
            com.android.server.wifi.WifiNative$BucketSettings[] r3 = r2.buckets     // Catch:{ all -> 0x00d5 }
            r3 = r3[r0]     // Catch:{ all -> 0x00d5 }
            int r4 = r3.report_events     // Catch:{ all -> 0x00d5 }
            r4 = r4 & 2
            if (r4 == 0) goto L_0x0035
            r12 = 1
        L_0x0035:
            r11.addChannels((com.android.server.wifi.WifiNative.BucketSettings) r3)     // Catch:{ all -> 0x00d5 }
            int r0 = r0 + 1
            goto L_0x0026
        L_0x003b:
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x00d5 }
            r0.<init>()     // Catch:{ all -> 0x00d5 }
            com.android.server.wifi.WifiNative$HiddenNetwork[] r3 = r2.hiddenNetworks     // Catch:{ all -> 0x00d5 }
            if (r3 == 0) goto L_0x005c
            com.android.server.wifi.WifiNative$HiddenNetwork[] r3 = r2.hiddenNetworks     // Catch:{ all -> 0x00d5 }
            int r3 = r3.length     // Catch:{ all -> 0x00d5 }
            r4 = 16
            int r3 = java.lang.Math.min(r3, r4)     // Catch:{ all -> 0x00d5 }
            r4 = 0
        L_0x004e:
            if (r4 >= r3) goto L_0x005c
            com.android.server.wifi.WifiNative$HiddenNetwork[] r5 = r2.hiddenNetworks     // Catch:{ all -> 0x00d5 }
            r5 = r5[r4]     // Catch:{ all -> 0x00d5 }
            java.lang.String r5 = r5.ssid     // Catch:{ all -> 0x00d5 }
            r0.add(r5)     // Catch:{ all -> 0x00d5 }
            int r4 = r4 + 1
            goto L_0x004e
        L_0x005c:
            com.android.server.wifi.scanner.WificondScannerImpl$LastScanSettings r13 = new com.android.server.wifi.scanner.WificondScannerImpl$LastScanSettings     // Catch:{ all -> 0x00d5 }
            com.android.server.wifi.Clock r3 = r1.mClock     // Catch:{ all -> 0x00d5 }
            long r4 = r3.getElapsedSinceBootMillis()     // Catch:{ all -> 0x00d5 }
            r3 = r13
            r6 = r12
            r7 = r11
            r8 = r22
            r3.<init>(r4, r6, r7, r8)     // Catch:{ all -> 0x00d5 }
            r1.mLastScanSettings = r13     // Catch:{ all -> 0x00d5 }
            r3 = 0
            boolean r4 = r11.isEmpty()     // Catch:{ all -> 0x00d5 }
            if (r4 != 0) goto L_0x009d
            java.util.Set r4 = r11.getScanFreqs()     // Catch:{ all -> 0x00d5 }
            com.android.server.wifi.WifiNative r5 = r1.mWifiNative     // Catch:{ all -> 0x00d5 }
            java.lang.String r6 = r1.mIfaceName     // Catch:{ all -> 0x00d5 }
            int r7 = r2.scanType     // Catch:{ all -> 0x00d5 }
            boolean r5 = r5.scan(r6, r7, r4, r0)     // Catch:{ all -> 0x00d5 }
            r3 = r5
            if (r3 != 0) goto L_0x00a4
            java.lang.String r5 = "WificondScannerImpl"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d5 }
            r6.<init>()     // Catch:{ all -> 0x00d5 }
            java.lang.String r7 = "Failed to start scan, freqs="
            r6.append(r7)     // Catch:{ all -> 0x00d5 }
            r6.append(r4)     // Catch:{ all -> 0x00d5 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00d5 }
            android.util.Log.e(r5, r6)     // Catch:{ all -> 0x00d5 }
            goto L_0x00a4
        L_0x009d:
            java.lang.String r4 = "WificondScannerImpl"
            java.lang.String r5 = "Failed to start scan because there is no available channel to scan"
            android.util.Log.e(r4, r5)     // Catch:{ all -> 0x00d5 }
        L_0x00a4:
            if (r3 == 0) goto L_0x00c8
            com.android.server.wifi.scanner.WificondScannerImpl$1 r4 = new com.android.server.wifi.scanner.WificondScannerImpl$1     // Catch:{ all -> 0x00d5 }
            r4.<init>()     // Catch:{ all -> 0x00d5 }
            r1.mScanTimeoutListener = r4     // Catch:{ all -> 0x00d5 }
            android.app.AlarmManager r13 = r1.mAlarmManager     // Catch:{ all -> 0x00d5 }
            r14 = 2
            com.android.server.wifi.Clock r4 = r1.mClock     // Catch:{ all -> 0x00d5 }
            long r4 = r4.getElapsedSinceBootMillis()     // Catch:{ all -> 0x00d5 }
            r6 = 15000(0x3a98, double:7.411E-320)
            long r15 = r4 + r6
            java.lang.String r17 = "WificondScannerImpl Scan Timeout"
            android.app.AlarmManager$OnAlarmListener r4 = r1.mScanTimeoutListener     // Catch:{ all -> 0x00d5 }
            android.os.Handler r5 = r1.mEventHandler     // Catch:{ all -> 0x00d5 }
            r18 = r4
            r19 = r5
            r13.set(r14, r15, r17, r18, r19)     // Catch:{ all -> 0x00d5 }
            goto L_0x00d2
        L_0x00c8:
            android.os.Handler r4 = r1.mEventHandler     // Catch:{ all -> 0x00d5 }
            com.android.server.wifi.scanner.WificondScannerImpl$2 r5 = new com.android.server.wifi.scanner.WificondScannerImpl$2     // Catch:{ all -> 0x00d5 }
            r5.<init>()     // Catch:{ all -> 0x00d5 }
            r4.post(r5)     // Catch:{ all -> 0x00d5 }
        L_0x00d2:
            monitor-exit(r10)     // Catch:{ all -> 0x00d5 }
            r4 = 1
            return r4
        L_0x00d5:
            r0 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x00d5 }
            throw r0
        L_0x00d8:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Invalid arguments for startSingleScan: settings="
            r3.append(r4)
            r3.append(r2)
            java.lang.String r4 = ",eventHandler="
            r3.append(r4)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "WificondScannerImpl"
            android.util.Log.w(r4, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.scanner.WificondScannerImpl.startSingleScan(com.android.server.wifi.WifiNative$ScanSettings, com.android.server.wifi.WifiNative$ScanEventHandler):boolean");
    }

    public WifiScanner.ScanData getLatestSingleScanResults() {
        return this.mLatestSingleScanResult;
    }

    public boolean startBatchedScan(WifiNative.ScanSettings settings, WifiNative.ScanEventHandler eventHandler) {
        Log.w(TAG, "startBatchedScan() is not supported");
        return false;
    }

    public void stopBatchedScan() {
        Log.w(TAG, "stopBatchedScan() is not supported");
    }

    public void pauseBatchedScan() {
        Log.w(TAG, "pauseBatchedScan() is not supported");
    }

    public void restartBatchedScan() {
        Log.w(TAG, "restartBatchedScan() is not supported");
    }

    /* access modifiers changed from: private */
    public void handleScanTimeout() {
        synchronized (this.mSettingsLock) {
            Log.e(TAG, "Timed out waiting for scan result from wificond");
            reportScanFailure();
            this.mScanTimeoutListener = null;
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case WifiMonitor.SCAN_RESULTS_EVENT:
                cancelScanTimeout();
                pollLatestScanData();
                return true;
            case WifiMonitor.SCAN_FAILED_EVENT:
                Log.w(TAG, "Scan failed");
                cancelScanTimeout();
                reportScanFailure();
                return true;
            case WifiMonitor.PNO_SCAN_RESULTS_EVENT:
                pollLatestScanDataForPno();
                return true;
            default:
                return true;
        }
    }

    private void cancelScanTimeout() {
        synchronized (this.mSettingsLock) {
            if (this.mScanTimeoutListener != null) {
                this.mAlarmManager.cancel(this.mScanTimeoutListener);
                this.mScanTimeoutListener = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void reportScanFailure() {
        synchronized (this.mSettingsLock) {
            if (this.mLastScanSettings != null) {
                if (this.mLastScanSettings.singleScanEventHandler != null) {
                    this.mLastScanSettings.singleScanEventHandler.onScanStatus(3);
                }
                this.mLastScanSettings = null;
            }
        }
    }

    private void reportPnoScanFailure() {
        synchronized (this.mSettingsLock) {
            if (this.mLastPnoScanSettings != null) {
                if (this.mLastPnoScanSettings.pnoScanEventHandler != null) {
                    this.mLastPnoScanSettings.pnoScanEventHandler.onPnoScanFailed();
                }
                this.mLastPnoScanSettings = null;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x007f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pollLatestScanDataForPno() {
        /*
            r9 = this;
            java.lang.Object r0 = r9.mSettingsLock
            monitor-enter(r0)
            com.android.server.wifi.scanner.WificondScannerImpl$LastPnoScanSettings r1 = r9.mLastPnoScanSettings     // Catch:{ all -> 0x0080 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0080 }
            return
        L_0x0009:
            com.android.server.wifi.WifiNative r1 = r9.mWifiNative     // Catch:{ all -> 0x0080 }
            java.lang.String r2 = r9.mIfaceName     // Catch:{ all -> 0x0080 }
            java.util.ArrayList r1 = r1.getPnoScanResults(r2)     // Catch:{ all -> 0x0080 }
            r9.mNativePnoScanResults = r1     // Catch:{ all -> 0x0080 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x0080 }
            r1.<init>()     // Catch:{ all -> 0x0080 }
            r2 = 0
            r3 = 0
        L_0x001a:
            java.util.ArrayList<com.android.server.wifi.ScanDetail> r4 = r9.mNativePnoScanResults     // Catch:{ all -> 0x0080 }
            int r4 = r4.size()     // Catch:{ all -> 0x0080 }
            if (r3 >= r4) goto L_0x0047
            java.util.ArrayList<com.android.server.wifi.ScanDetail> r4 = r9.mNativePnoScanResults     // Catch:{ all -> 0x0080 }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ all -> 0x0080 }
            com.android.server.wifi.ScanDetail r4 = (com.android.server.wifi.ScanDetail) r4     // Catch:{ all -> 0x0080 }
            android.net.wifi.ScanResult r4 = r4.getScanResult()     // Catch:{ all -> 0x0080 }
            com.android.server.wifi.WifiGbk.processScanResult(r4)     // Catch:{ all -> 0x0080 }
            long r5 = r4.timestamp     // Catch:{ all -> 0x0080 }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r7
            com.android.server.wifi.scanner.WificondScannerImpl$LastPnoScanSettings r7 = r9.mLastPnoScanSettings     // Catch:{ all -> 0x0080 }
            long r7 = r7.startTime     // Catch:{ all -> 0x0080 }
            int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r7 <= 0) goto L_0x0042
            r1.add(r4)     // Catch:{ all -> 0x0080 }
            goto L_0x0044
        L_0x0042:
            int r2 = r2 + 1
        L_0x0044:
            int r3 = r3 + 1
            goto L_0x001a
        L_0x0047:
            if (r2 == 0) goto L_0x0064
            java.lang.String r3 = "WificondScannerImpl"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0080 }
            r4.<init>()     // Catch:{ all -> 0x0080 }
            java.lang.String r5 = "Filtering out "
            r4.append(r5)     // Catch:{ all -> 0x0080 }
            r4.append(r2)     // Catch:{ all -> 0x0080 }
            java.lang.String r5 = " pno scan results."
            r4.append(r5)     // Catch:{ all -> 0x0080 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0080 }
            android.util.Log.d(r3, r4)     // Catch:{ all -> 0x0080 }
        L_0x0064:
            com.android.server.wifi.scanner.WificondScannerImpl$LastPnoScanSettings r3 = r9.mLastPnoScanSettings     // Catch:{ all -> 0x0080 }
            com.android.server.wifi.WifiNative$PnoEventHandler r3 = r3.pnoScanEventHandler     // Catch:{ all -> 0x0080 }
            if (r3 == 0) goto L_0x007e
            int r3 = r1.size()     // Catch:{ all -> 0x0080 }
            android.net.wifi.ScanResult[] r3 = new android.net.wifi.ScanResult[r3]     // Catch:{ all -> 0x0080 }
            java.lang.Object[] r3 = r1.toArray(r3)     // Catch:{ all -> 0x0080 }
            android.net.wifi.ScanResult[] r3 = (android.net.wifi.ScanResult[]) r3     // Catch:{ all -> 0x0080 }
            com.android.server.wifi.scanner.WificondScannerImpl$LastPnoScanSettings r4 = r9.mLastPnoScanSettings     // Catch:{ all -> 0x0080 }
            com.android.server.wifi.WifiNative$PnoEventHandler r4 = r4.pnoScanEventHandler     // Catch:{ all -> 0x0080 }
            r4.onPnoNetworkFound(r3)     // Catch:{ all -> 0x0080 }
        L_0x007e:
            monitor-exit(r0)     // Catch:{ all -> 0x0080 }
            return
        L_0x0080:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0080 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.scanner.WificondScannerImpl.pollLatestScanDataForPno():void");
    }

    private static int getBandScanned(ChannelHelper.ChannelCollection channelCollection) {
        if (channelCollection.containsBand(7)) {
            return 7;
        }
        if (channelCollection.containsBand(3)) {
            return 3;
        }
        if (channelCollection.containsBand(6)) {
            return 6;
        }
        if (channelCollection.containsBand(2)) {
            return 2;
        }
        if (channelCollection.containsBand(4)) {
            return 4;
        }
        if (channelCollection.containsBand(1)) {
            return 1;
        }
        return 0;
    }

    private void pollLatestScanData() {
        synchronized (this.mSettingsLock) {
            if (this.mLastScanSettings != null) {
                this.mNativeScanResults = this.mWifiNative.getScanResults(this.mIfaceName);
                List<ScanResult> singleScanResults = new ArrayList<>();
                int numFilteredScanResults = 0;
                for (int i = 0; i < this.mNativeScanResults.size(); i++) {
                    ScanResult result = this.mNativeScanResults.get(i).getScanResult();
                    WifiGbk.processScanResult(result);
                    if (result.timestamp / 1000 <= this.mLastScanSettings.startTime) {
                        numFilteredScanResults++;
                    } else if (this.mLastScanSettings.singleScanFreqs.containsChannel(result.frequency)) {
                        singleScanResults.add(result);
                    }
                }
                if (numFilteredScanResults != 0) {
                    Log.d(TAG, "Filtering out " + numFilteredScanResults + " scan results.");
                }
                WifiGbk.ageBssCache();
                if (this.mLastScanSettings.singleScanEventHandler != null) {
                    if (this.mLastScanSettings.reportSingleScanFullResults) {
                        for (ScanResult scanResult : singleScanResults) {
                            this.mLastScanSettings.singleScanEventHandler.onFullScanResult(scanResult, 0);
                        }
                    }
                    Collections.sort(singleScanResults, SCAN_RESULT_SORT_COMPARATOR);
                    this.mLatestSingleScanResult = new WifiScanner.ScanData(0, 0, 0, getBandScanned(this.mLastScanSettings.singleScanFreqs), (ScanResult[]) singleScanResults.toArray(new ScanResult[singleScanResults.size()]));
                    this.mLastScanSettings.singleScanEventHandler.onScanStatus(0);
                }
                this.mLastScanSettings = null;
            }
        }
    }

    public WifiScanner.ScanData[] getLatestBatchedScanResults(boolean flush) {
        return null;
    }

    private boolean startHwPnoScan(WifiNative.PnoSettings pnoSettings) {
        return this.mWifiNative.startPnoScan(this.mIfaceName, pnoSettings);
    }

    private void stopHwPnoScan() {
        this.mWifiNative.stopPnoScan(this.mIfaceName);
    }

    private boolean isHwPnoScanRequired(boolean isConnectedPno) {
        return !isConnectedPno && this.mHwPnoScanSupported;
    }

    public boolean setHwPnoList(WifiNative.PnoSettings settings, WifiNative.PnoEventHandler eventHandler) {
        synchronized (this.mSettingsLock) {
            if (this.mLastPnoScanSettings != null) {
                Log.w(TAG, "Already running a PNO scan");
                return false;
            } else if (!isHwPnoScanRequired(settings.isConnected)) {
                return false;
            } else {
                if (startHwPnoScan(settings)) {
                    this.mLastPnoScanSettings = new LastPnoScanSettings(this.mClock.getElapsedSinceBootMillis(), settings.networkList, eventHandler);
                } else {
                    Log.e(TAG, "Failed to start PNO scan");
                    reportPnoScanFailure();
                }
                return true;
            }
        }
    }

    public boolean resetHwPnoList() {
        synchronized (this.mSettingsLock) {
            if (this.mLastPnoScanSettings == null) {
                Log.w(TAG, "No PNO scan running");
                return false;
            }
            this.mLastPnoScanSettings = null;
            stopHwPnoScan();
            return true;
        }
    }

    public boolean isHwPnoSupported(boolean isConnectedPno) {
        return isHwPnoScanRequired(isConnectedPno);
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        synchronized (this.mSettingsLock) {
            long nowMs = this.mClock.getElapsedSinceBootMillis();
            pw.println("Latest native scan results:");
            if (this.mNativeScanResults != null) {
                ScanResultUtil.dumpScanResults(pw, (List) this.mNativeScanResults.stream().map($$Lambda$WificondScannerImpl$CSjtYSyNiQ_mC6mOyQ4GpkylqY.INSTANCE).collect(Collectors.toList()), nowMs);
            }
            pw.println("Latest native pno scan results:");
            if (this.mNativePnoScanResults != null) {
                ScanResultUtil.dumpScanResults(pw, (List) this.mNativePnoScanResults.stream().map($$Lambda$WificondScannerImpl$VfxaUtYlcuU7Z28abhvk42O2k.INSTANCE).collect(Collectors.toList()), nowMs);
            }
        }
    }

    private static class LastScanSettings {
        public boolean reportSingleScanFullResults;
        public WifiNative.ScanEventHandler singleScanEventHandler;
        public ChannelHelper.ChannelCollection singleScanFreqs;
        public long startTime;

        LastScanSettings(long startTime2, boolean reportSingleScanFullResults2, ChannelHelper.ChannelCollection singleScanFreqs2, WifiNative.ScanEventHandler singleScanEventHandler2) {
            this.startTime = startTime2;
            this.reportSingleScanFullResults = reportSingleScanFullResults2;
            this.singleScanFreqs = singleScanFreqs2;
            this.singleScanEventHandler = singleScanEventHandler2;
        }
    }

    private static class LastPnoScanSettings {
        public WifiNative.PnoNetwork[] pnoNetworkList;
        public WifiNative.PnoEventHandler pnoScanEventHandler;
        public long startTime;

        LastPnoScanSettings(long startTime2, WifiNative.PnoNetwork[] pnoNetworkList2, WifiNative.PnoEventHandler pnoScanEventHandler2) {
            this.startTime = startTime2;
            this.pnoNetworkList = pnoNetworkList2;
            this.pnoScanEventHandler = pnoScanEventHandler2;
        }
    }
}
