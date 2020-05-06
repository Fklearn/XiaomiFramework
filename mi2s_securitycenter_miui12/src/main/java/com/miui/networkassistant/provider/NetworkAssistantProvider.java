package com.miui.networkassistant.provider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.f;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.activityutil.o;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.firewall.BackgroundPolicyService;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.FirewallRule;
import com.miui.networkassistant.model.VirtualNotiInfo;
import com.miui.networkassistant.provider.DataCursor;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.service.FirewallService;
import com.miui.networkassistant.service.IFirewallBinder;
import com.miui.networkassistant.service.ITrafficManageBinder;
import com.miui.networkassistant.service.tm.TrafficManageService;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.correction.ITrafficCorrection;
import com.miui.networkassistant.traffic.statistic.NaTrafficStats;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.traffic.statistic.StatisticAppTraffic;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.BitmapUtil;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.FormatBytesUtil;
import com.miui.networkassistant.utils.MiSimUtil;
import com.miui.networkassistant.utils.NotiStatusIconHelper;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.UsageStateUtil;
import com.miui.networkassistant.utils.VirtualSimUtil;
import com.miui.securitycenter.R;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.provider.ExtraNetwork;

public class NetworkAssistantProvider extends ContentProvider {
    private static final int BILL_PACKAGE_DETAIL_CODE = 256;
    public static final String BILL_PACKAGE_DETAIL_STR = "bill_detail";
    private static final int CALL_TIME_PACKAGE_DETAIL_CODE = 257;
    public static final String CALL_TIME_PACKAGE_DETAIL_STR = "calltime_detail";
    public static final String DATAUSAGE_NOTI_STATUS_STR = "datausage_noti_status";
    public static final String DATAUSAGE_STATUS_DETAILED_STR = "datausage_status_detailed";
    public static final String DATAUSAGE_STATUS_IMSI_STR = "datausage_status/*";
    public static final String DATAUSAGE_STATUS_STR = "datausage_status";
    private static final int DATA_USAGE_NOTI_STATUS_CODE = 49;
    private static final int DATA_USAGE_STATUS_CODE = 48;
    private static final int DATA_USAGE_STATUS_DETAILED_CODE = 260;
    private static final int FAIL = 0;
    private static final int FIREWALL_BACKGROUND_RESTRICT_STATUS_CODE = 39;
    public static final String FIREWALL_BACKGROUND_RESTRICT_STR = "firewall_background_restrict";
    private static final int FIREWALL_PACKAGENAME_CODE = 32;
    public static final String FIREWALL_PACKAGENAME_STR = "firewall/*";
    private static final int MOBILE_FIREWALL_PACKAGENAME_CODE = 35;
    public static final String MOBILE_FIREWALL_PACKAGENAME_STR = "mobile_firewall/*/*";
    private static final int MOBILE_RESTRICT_STATUS_CODE = 38;
    public static final String MOBILE_RESTRICT_STR = "mobile_restrict";
    private static final int NA_SETTINGS_INFO_STATUS_CODE = 64;
    public static final String NA_SETTINGS_INFO_STATUS_STR = "na_settings_info";
    private static final int SMS_CORRECTION_CODE = 258;
    public static final String SMS_CORRECTION_STR = "sms_correction";
    private static final int SUCCESS = 1;
    private static final String TAG = "NAProvider";
    private static final int TEMP_MOBILE_FIREWALL_PACKAGENAME_CODE = 33;
    public static final String TEMP_MOBILE_FIREWALL_PACKAGENAME_STR = "temp_mobile_firewall/*/*";
    private static final int TEMP_WIFI_FIREWALL_PACKAGENAME_CODE = 34;
    public static final String TEMP_WIFI_FIREWALL_PACKAGENAME_STR = "temp_wifi_firewall/*";
    private static final int TETHERING_LIMIT_ENABLED_CODE = 259;
    public static final String TETHERING_LIMIT_ENABLED_STR = "tethering_limit";
    private static final int TRAFFIC_DISTRIBUTION_CODE = 16;
    private static final int TRAFFIC_DISTRIBUTION_ID_CODE = 17;
    public static final String TRAFFIC_DISTRIBUTION_ID_STR = "traffic_distribution/#";
    public static final String TRAFFIC_DISTRIBUTION_STR = "traffic_distribution";
    private static final int TRAFFIC_PURCHASE_CODE = 96;
    private static final int TRAFFIC_PURCHASE_CONFIG_CODE = 144;
    public static final String TRAFFIC_PURCHASE_CONFIG_STR = "traffic_purchase_config";
    public static final String TRAFFIC_PURCHASE_STATUS_DEFAULT_STR = "na_traffic_purchase";
    public static final String TRAFFIC_PURCHASE_STATUS_STR = "na_traffic_purchase/*/*";
    private static final int TRAFFIC_STATS_CODE = 80;
    public static final String TRAFFIC_STATS_STR = "na_traffic_stats";
    public static final String TRAFFIC_STATS_UID_STR = "na_traffic_stats/*";
    public static final String TRAFFIC_USED_ALL_LIST_PARAM_STR = "top_usage_app/*";
    private static final int TRAFFIC_USED_APP_LIST_CODE = 145;
    private static final int WIFI_FIREWALL_PACKAGENAME_CODE = 36;
    public static final String WIFI_FIREWALL_PACKAGENAME_STR = "wifi_firewall/*";
    private static final int WLAN_RESTRICT_STATUS_CODE = 37;
    public static final String WLAN_RESTRICT_STR = "wlan_restrict";
    private static HashMap<String, String> sTrafficsProjectionMap = new HashMap<>();
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    private String mCurrentActiveIface = null;
    private f.a mCurrentNetworkState = f.a.Inited;
    private SQLiteDatabase mDb = null;
    /* access modifiers changed from: private */
    public IFirewallBinder mFirewallBinder;
    private ServiceConnection mFirewallServiceConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IFirewallBinder unused = NetworkAssistantProvider.this.mFirewallBinder = IFirewallBinder.Stub.asInterface(iBinder);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            IFirewallBinder unused = NetworkAssistantProvider.this.mFirewallBinder = null;
        }
    };
    private boolean mIsRecord = false;
    private boolean mNeedSetToZero = true;
    private DBHelper mOpenHelper;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkAssistantProvider.this.refreshActiveIfaceIfNeed();
        }
    };
    /* access modifiers changed from: private */
    public ITrafficManageBinder mTrafficManageBinder;
    private ServiceConnection mTrafficManageConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ITrafficManageBinder unused = NetworkAssistantProvider.this.mTrafficManageBinder = ITrafficManageBinder.Stub.asInterface(iBinder);
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ITrafficManageBinder unused = NetworkAssistantProvider.this.mTrafficManageBinder = null;
        }
    };
    private Object mTrafficStatsLock = new Object();

    static {
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "traffic_distribution", 16);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, TRAFFIC_DISTRIBUTION_ID_STR, 17);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, FIREWALL_PACKAGENAME_STR, 32);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, TEMP_MOBILE_FIREWALL_PACKAGENAME_STR, 33);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, TEMP_WIFI_FIREWALL_PACKAGENAME_STR, 34);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, MOBILE_FIREWALL_PACKAGENAME_STR, 35);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, WIFI_FIREWALL_PACKAGENAME_STR, 36);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "wlan_restrict", 37);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "mobile_restrict", 38);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "firewall_background_restrict", 39);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "datausage_status", 48);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "datausage_status_detailed", DATA_USAGE_STATUS_DETAILED_CODE);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "datausage_noti_status", 49);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, DATAUSAGE_STATUS_IMSI_STR, 48);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "na_settings_info", 64);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "na_traffic_stats", 80);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, TRAFFIC_STATS_UID_STR, 80);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "na_traffic_purchase", 96);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, TRAFFIC_PURCHASE_STATUS_STR, 96);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "traffic_purchase_config", TRAFFIC_PURCHASE_CONFIG_CODE);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, TRAFFIC_USED_ALL_LIST_PARAM_STR, TRAFFIC_USED_APP_LIST_CODE);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "bill_detail", BILL_PACKAGE_DETAIL_CODE);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "calltime_detail", CALL_TIME_PACKAGE_DETAIL_CODE);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "sms_correction", SMS_CORRECTION_CODE);
        sUriMatcher.addURI(ProviderConstant.AUTHORITY, "tethering_limit", TETHERING_LIMIT_ENABLED_CODE);
        sTrafficsProjectionMap.put("_id", "_id");
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.FROM_PKGNAME, ProviderConstant.TrafficDistributionColumns.FROM_PKGNAME);
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.TO_PKGNAME, ProviderConstant.TrafficDistributionColumns.TO_PKGNAME);
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.MOBILE_RXBYTES, ProviderConstant.TrafficDistributionColumns.MOBILE_RXBYTES);
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.MOBILE_TXBYTES, ProviderConstant.TrafficDistributionColumns.MOBILE_TXBYTES);
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.WIFI_RXBYTES, ProviderConstant.TrafficDistributionColumns.WIFI_RXBYTES);
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.WIFI_TXBYTES, ProviderConstant.TrafficDistributionColumns.WIFI_TXBYTES);
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.IMSI, ProviderConstant.TrafficDistributionColumns.IMSI);
        sTrafficsProjectionMap.put(ProviderConstant.TrafficDistributionColumns.STORAGE_TIME, ProviderConstant.TrafficDistributionColumns.STORAGE_TIME);
    }

    public NetworkAssistantProvider() {
        Log.i(TAG, "constructor");
    }

    private void bindFirewallService() {
        g.a(getContext(), new Intent(getContext(), FirewallService.class), this.mFirewallServiceConn, 1, B.k());
    }

    private void bindTrafficManageService() {
        g.a(getContext(), new Intent(getContext(), TrafficManageService.class), this.mTrafficManageConnection, 1, B.k());
    }

    private boolean checkParams(Uri uri) {
        return (uri == null || this.mFirewallBinder == null || TextUtils.isEmpty(getPackageNameFromUri(uri))) ? false : true;
    }

    private boolean checkParams(Uri uri, ContentValues contentValues) {
        if (contentValues != null) {
            return checkParams(uri);
        }
        return false;
    }

    private int checkSlotNum(int i) {
        return (i == 0 || i == 1) ? i : Sim.getCurrentActiveSlotNum();
    }

    private void constructCursorByRestrictPackages(DataCursor dataCursor, List<String> list) {
        ArrayList<String> arrayList = new ArrayList<>(list);
        List<String> recentApps = UsageStateUtil.getRecentApps(getContext());
        recentApps.retainAll(arrayList);
        arrayList.removeAll(recentApps);
        for (String dataEntry : recentApps) {
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(dataEntry)));
        }
        for (String dataEntry2 : arrayList) {
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(dataEntry2)));
        }
    }

    private Cursor doQueryDB(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        int match = sUriMatcher.match(uri);
        if (match == 16) {
            sQLiteQueryBuilder.setTables("traffic_distribution");
            sQLiteQueryBuilder.setProjectionMap(sTrafficsProjectionMap);
        } else if (match == 17) {
            sQLiteQueryBuilder.setTables("traffic_distribution");
            sQLiteQueryBuilder.setProjectionMap(sTrafficsProjectionMap);
            sQLiteQueryBuilder.appendWhere("_id=" + uri.getPathSegments().get(1));
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (TextUtils.isEmpty(str2)) {
            str2 = ProviderConstant.TrafficDistributionColumns.DEFAULT_SORT_ORDER;
        }
        String str3 = str2;
        if (this.mDb == null) {
            this.mDb = this.mOpenHelper.getWritableDatabase();
        }
        Cursor query = sQLiteQueryBuilder.query(this.mDb, strArr, str, strArr2, (String) null, (String) null, str3);
        query.setNotificationUri(getContext().getContentResolver(), uri);
        return query;
    }

    private String getPackageNameFromUri(Uri uri) {
        if (uri == null) {
            return null;
        }
        List<String> pathSegments = uri.getPathSegments();
        String str = pathSegments.get(pathSegments.size() - 1);
        String fragment = uri.getFragment();
        if (!TextUtils.isEmpty(fragment)) {
            str = String.format("%s#%s", new Object[]{str, fragment});
        }
        if (!TextUtils.isEmpty(str)) {
            return str;
        }
        return null;
    }

    private int getSlotNum(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log.i(TAG, "parse slot num exception", e);
            return 0;
        }
    }

    private Cursor queryBillPackageDetail(Uri uri) {
        SimUserInfo instance;
        if (uri == null) {
            return null;
        }
        DataCursor dataCursor = new DataCursor("package_total", "package_used", "package_remained", "slot_num", "package_setted");
        SimUserInfo instance2 = SimUserInfo.getInstance(getContext(), 0);
        if (instance2 != null && instance2.hasImsi() && instance2.isSimInserted()) {
            long billPackageTotal = instance2.getBillPackageTotal();
            long billPackageRemained = instance2.isBillPackageEffective() ? instance2.getBillPackageRemained() : -1;
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(billPackageTotal), new DataCursor.DataEntry(billPackageTotal > 0 ? billPackageTotal - billPackageRemained : -1), new DataCursor.DataEntry(billPackageRemained), new DataCursor.DataEntry(0), new DataCursor.DataEntry(String.valueOf(instance2.isOperatorSetted() && instance2.isTotalDataUsageSetted()))));
        }
        if (!DeviceUtil.IS_DUAL_CARD || (instance = SimUserInfo.getInstance(getContext(), 1)) == null || !instance.hasImsi() || !instance.isSimInserted()) {
            return dataCursor;
        }
        long billPackageTotal2 = instance.getBillPackageTotal();
        long billPackageRemained2 = instance.isBillPackageEffective() ? instance.getBillPackageRemained() : -1;
        dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(billPackageTotal2), new DataCursor.DataEntry(billPackageTotal2 > 0 ? billPackageTotal2 - billPackageRemained2 : -1), new DataCursor.DataEntry(billPackageRemained2), new DataCursor.DataEntry(1), new DataCursor.DataEntry(String.valueOf(instance.isOperatorSetted() && instance.isTotalDataUsageSetted()))));
        return dataCursor;
    }

    private Cursor queryCallTimePackageDetail(Uri uri) {
        SimUserInfo instance;
        if (uri == null) {
            return null;
        }
        DataCursor dataCursor = new DataCursor("package_total", "package_used", "package_remained", "slot_num", "package_setted");
        SimUserInfo instance2 = SimUserInfo.getInstance(getContext(), 0);
        if (instance2 != null && instance2.hasImsi() && instance2.isSimInserted()) {
            long callTimePackageTotal = instance2.getCallTimePackageTotal();
            long callTimePackageRemained = instance2.getCallTimePackageRemained();
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(callTimePackageTotal), new DataCursor.DataEntry(callTimePackageTotal > 0 ? callTimePackageTotal - callTimePackageRemained : -1), new DataCursor.DataEntry(callTimePackageRemained), new DataCursor.DataEntry(0), new DataCursor.DataEntry(String.valueOf(instance2.isOperatorSetted() && instance2.isTotalDataUsageSetted()))));
        }
        if (!DeviceUtil.IS_DUAL_CARD || (instance = SimUserInfo.getInstance(getContext(), 1)) == null || !instance.hasImsi() || !instance.isSimInserted()) {
            return dataCursor;
        }
        long callTimePackageTotal2 = instance.getCallTimePackageTotal();
        long callTimePackageRemained2 = instance.getCallTimePackageRemained();
        dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(callTimePackageTotal2), new DataCursor.DataEntry(callTimePackageTotal2 > 0 ? callTimePackageTotal2 - callTimePackageRemained2 : -1), new DataCursor.DataEntry(callTimePackageRemained2), new DataCursor.DataEntry(1), new DataCursor.DataEntry(String.valueOf(instance.isOperatorSetted() && instance.isTotalDataUsageSetted()))));
        return dataCursor;
    }

    private Cursor queryDataUsageNotiStatus(Uri uri) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        int i;
        Context context;
        String str7;
        String str8;
        String str9;
        Context context2 = getContext();
        int currentMobileSlotNum = DeviceUtil.IS_DUAL_CARD ? SimCardHelper.getInstance(getContext()).getCurrentMobileSlotNum() : 0;
        if (MiSimUtil.isMiSimEnable(context2, currentMobileSlotNum)) {
            VirtualNotiInfo parseNotificationInfo = VirtualSimUtil.parseNotificationInfo(context2);
            if (parseNotificationInfo == null) {
                return null;
            }
            str4 = parseNotificationInfo.getAction();
            str3 = parseNotificationInfo.getAction();
            str5 = context2.getString(R.string.status_bar_network_flow_content_virtual_card, new Object[]{parseNotificationInfo.getTodayUsedTraffic(), parseNotificationInfo.getMonthUsedTraffic()});
            str2 = parseNotificationInfo.getAcitionDesc();
            str = parseNotificationInfo.getIconUri();
        } else {
            DataCursor dataCursor = (DataCursor) queryDataUsageStatus(uri);
            if (dataCursor == null) {
                return null;
            }
            SimUserInfo instance = SimUserInfo.getInstance(getContext(), currentMobileSlotNum);
            long billPackageRemained = instance.getBillPackageRemained();
            Intent intent = new Intent(Constants.App.ACTION_NETWORK_ASSISTANT_TRAFFIC_PURCHASE);
            Bundle bundle = new Bundle();
            bundle.putString("bundle_key_purchase_from", "100003");
            intent.putExtra("bundle_key_com", bundle);
            intent.addFlags(268435456);
            boolean isTrafficPurchaseSupported = ExtraNetwork.isTrafficPurchaseSupported(context2);
            long j = dataCursor.getLong(dataCursor.getColumnIndex("total_limit"));
            long j2 = dataCursor.getLong(dataCursor.getColumnIndex("month_used"));
            long j3 = dataCursor.getLong(dataCursor.getColumnIndex("today_used"));
            String str10 = "";
            Intent intent2 = new Intent("miui.intent.action.NETWORKASSISTANT_ENTRANCE");
            intent2.addFlags(335544320);
            String uri2 = intent2.toUri(1);
            if (instance.isNotLimitCardEnable()) {
                String string = context2.getString(R.string.status_bar_network_flow_content_not_setting, new Object[]{FormatBytesUtil.formatBytes(context2, j3), FormatBytesUtil.formatBytes(context2, j2)});
                String format = String.format(context2.getString(R.string.status_bar_network_flow_phone_balance), new Object[]{Double.valueOf(((double) billPackageRemained) / 100.0d)});
                String uri3 = intent2.toUri(1);
                if (billPackageRemained <= Long.MIN_VALUE) {
                    str5 = string;
                    str3 = uri3;
                    str6 = str10;
                } else {
                    str6 = format;
                    str5 = string;
                    str3 = uri3;
                }
                i = 100;
            } else if (instance.isTotalDataUsageSetted()) {
                if (isTrafficPurchaseSupported) {
                    String string2 = context2.getString(R.string.status_bar_network_flow_purchase);
                    str7 = intent.toUri(1);
                    context = context2;
                    str9 = string2;
                } else {
                    context = context2;
                    String format2 = String.format(context2.getString(R.string.status_bar_network_flow_phone_balance), new Object[]{Double.valueOf(((double) billPackageRemained) / 100.0d)});
                    str7 = intent2.toUri(1);
                    str9 = billPackageRemained <= Long.MIN_VALUE ? str10 : format2;
                }
                long j4 = j - j2;
                if (j4 <= 0) {
                    str8 = context.getString(R.string.status_bar_network_flow_exceed_content, new Object[]{FormatBytesUtil.formatBytes(context, j3), String.valueOf(context.getResources().getColor(R.color.status_bar_network_flow_exceed_color)), FormatBytesUtil.formatBytes(context, -j4)});
                    i = 0;
                } else {
                    String string3 = context.getString(R.string.status_bar_network_flow_content, new Object[]{FormatBytesUtil.formatBytes(context, j3), FormatBytesUtil.formatBytes(context, j4)});
                    int i2 = 100 - ((int) (((((double) j2) * 1.0d) / ((double) j)) * 100.0d));
                    if (i2 > 100) {
                        i2 = 100;
                    }
                    if (i2 < 0) {
                        i2 = 0;
                    }
                    int i3 = i2;
                    str8 = string3;
                    i = i3;
                }
                str3 = str7;
            } else {
                Context context3 = context2;
                i = -1;
                str5 = context3.getString(R.string.status_bar_network_flow_content_not_setting, new Object[]{FormatBytesUtil.formatBytes(context3, j3), FormatBytesUtil.formatBytes(context3, j2)});
                str6 = context3.getString(R.string.status_bar_network_flow_setup_data_plan);
                str3 = intent2.toUri(1);
            }
            str2 = !Locale.getDefault().getLanguage().equals(Locale.CHINA.getLanguage()) ? str10 : str6;
            try {
                File file = new File(getContext().getCacheDir(), "na_files");
                BitmapUtil.saveDrawableResToFile(getContext(), file, "tmp.png", NotiStatusIconHelper.getIconByLevel(i));
                Uri uriForFile = FileProvider.getUriForFile(getContext(), ProviderConstant.AUTHORITY_FILE, new File(file, "tmp.png"));
                str = uriForFile.toString();
                getContext().grantUriPermission("com.android.systemui", uriForFile, 1);
            } catch (Exception e) {
                Log.e(TAG, "FileProvider Exception");
                if (!this.mIsRecord) {
                    this.mIsRecord = true;
                    File file2 = new File(getContext().getCacheDir().getPath() + "/na_files/tmp.png");
                    AnalyticsHelper.recordThrowable(e, "exist file:" + file2.exists());
                }
                str = str10;
            }
            str4 = uri2;
        }
        DataCursor dataCursor2 = new DataCursor(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_TEXT1, ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ACTION1, ProviderConstant.DataUsageNotiStatusColumns.COLUMN_TEXT2, ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ACTION2, ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
        dataCursor2.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(str5), new DataCursor.DataEntry(str4), new DataCursor.DataEntry(str2), new DataCursor.DataEntry(str3), new DataCursor.DataEntry(str)));
        return dataCursor2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x0092  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.database.Cursor queryDataUsageStatus(android.net.Uri r17) {
        /*
            r16 = this;
            r1 = r16
            if (r17 == 0) goto L_0x00ff
            com.miui.networkassistant.service.ITrafficManageBinder r0 = r1.mTrafficManageBinder
            if (r0 == 0) goto L_0x00ff
            boolean r0 = com.miui.networkassistant.utils.DeviceUtil.IS_DUAL_CARD
            if (r0 == 0) goto L_0x0019
            android.content.Context r0 = r16.getContext()
            com.miui.networkassistant.dual.SimCardHelper r0 = com.miui.networkassistant.dual.SimCardHelper.getInstance(r0)
            int r0 = r0.getCurrentMobileSlotNum()
            goto L_0x001a
        L_0x0019:
            r0 = 0
        L_0x001a:
            android.content.Context r3 = r16.getContext()
            com.miui.networkassistant.config.SimUserInfo r3 = com.miui.networkassistant.config.SimUserInfo.getInstance((android.content.Context) r3, (int) r0)
            if (r3 == 0) goto L_0x00ff
            boolean r4 = r3.isSimInserted()
            if (r4 == 0) goto L_0x00ff
            boolean r4 = r3.hasImsi()
            if (r4 == 0) goto L_0x00ff
            r4 = 1
            r5 = 0
            boolean r7 = r3.isLeisureDataUsageEffective()     // Catch:{ RemoteException -> 0x007d }
            if (r7 == 0) goto L_0x004d
            boolean r7 = com.miui.networkassistant.traffic.statistic.LeisureTrafficHelper.isLeisureTime(r3)     // Catch:{ RemoteException -> 0x007d }
            if (r7 == 0) goto L_0x004d
            long r7 = r3.getLeisureDataUsageTotal()     // Catch:{ RemoteException -> 0x007d }
            com.miui.networkassistant.service.ITrafficManageBinder r9 = r1.mTrafficManageBinder     // Catch:{ RemoteException -> 0x007a }
            long[] r9 = r9.getCorrectedNormalAndLeisureMonthTotalUsed(r0)     // Catch:{ RemoteException -> 0x007a }
            r10 = r9[r4]     // Catch:{ RemoteException -> 0x007a }
            r9 = r10
            goto L_0x0061
        L_0x004d:
            boolean r7 = r3.isNotLimitCardEnable()     // Catch:{ RemoteException -> 0x007d }
            if (r7 != 0) goto L_0x005a
            com.miui.networkassistant.service.ITrafficManageBinder r7 = r1.mTrafficManageBinder     // Catch:{ RemoteException -> 0x007d }
            long r7 = r7.getCurrentMonthTotalPackage(r0)     // Catch:{ RemoteException -> 0x007d }
            goto L_0x005b
        L_0x005a:
            r7 = r5
        L_0x005b:
            com.miui.networkassistant.service.ITrafficManageBinder r9 = r1.mTrafficManageBinder     // Catch:{ RemoteException -> 0x007a }
            long r9 = r9.getCorrectedNormalMonthDataUsageUsed(r0)     // Catch:{ RemoteException -> 0x007a }
        L_0x0061:
            com.miui.networkassistant.service.ITrafficManageBinder r11 = r1.mTrafficManageBinder     // Catch:{ RemoteException -> 0x0077 }
            long r11 = r11.getTodayDataUsageUsed(r0)     // Catch:{ RemoteException -> 0x0077 }
            int r0 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r0 < 0) goto L_0x006c
            goto L_0x006d
        L_0x006c:
            r7 = r5
        L_0x006d:
            float r0 = r3.getDataUsageWarning()     // Catch:{ RemoteException -> 0x0075 }
            float r5 = (float) r7
            float r0 = r0 * r5
            long r5 = (long) r0
            goto L_0x0088
        L_0x0075:
            r0 = move-exception
            goto L_0x0081
        L_0x0077:
            r0 = move-exception
            r11 = r5
            goto L_0x0081
        L_0x007a:
            r0 = move-exception
            r9 = r5
            goto L_0x0080
        L_0x007d:
            r0 = move-exception
            r7 = r5
            r9 = r7
        L_0x0080:
            r11 = r9
        L_0x0081:
            java.lang.String r13 = "NAProvider"
            java.lang.String r14 = "query data usage "
            android.util.Log.i(r13, r14, r0)
        L_0x0088:
            java.util.List r0 = r17.getPathSegments()
            int r0 = r0.size()
            if (r0 <= r4) goto L_0x00b3
            java.util.List r0 = r17.getPathSegments()
            java.lang.Object r0 = r0.get(r4)
            java.lang.String r0 = (java.lang.String) r0
            java.lang.String r13 = "securitycenter"
            boolean r13 = android.text.TextUtils.equals(r0, r13)
            if (r13 == 0) goto L_0x00b0
            android.content.Context r0 = r16.getContext()
            com.miui.networkassistant.traffic.purchase.CooperationManager.isTrafficPurchaseAvailable(r0, r3, r4)
            boolean r0 = r3.isNATipsEnable()
            goto L_0x00b4
        L_0x00b0:
            android.text.TextUtils.isEmpty(r0)
        L_0x00b3:
            r0 = 0
        L_0x00b4:
            com.miui.networkassistant.provider.DataCursor r3 = new com.miui.networkassistant.provider.DataCursor
            java.lang.String r13 = "total_limit"
            java.lang.String r14 = "month_used"
            java.lang.String r15 = "today_used"
            java.lang.String r4 = "month_warning"
            java.lang.String r2 = "purchase_tips_enable"
            java.lang.String[] r2 = new java.lang.String[]{r13, r14, r15, r4, r2}
            r3.<init>(r2)
            com.miui.networkassistant.provider.DataCursor$DataRow r2 = new com.miui.networkassistant.provider.DataCursor$DataRow
            r4 = 5
            com.miui.networkassistant.provider.DataCursor$DataEntry[] r4 = new com.miui.networkassistant.provider.DataCursor.DataEntry[r4]
            com.miui.networkassistant.provider.DataCursor$DataEntry r13 = new com.miui.networkassistant.provider.DataCursor$DataEntry
            r13.<init>((long) r7)
            r7 = 0
            r4[r7] = r13
            com.miui.networkassistant.provider.DataCursor$DataEntry r7 = new com.miui.networkassistant.provider.DataCursor$DataEntry
            r7.<init>((long) r9)
            r8 = 1
            r4[r8] = r7
            r7 = 2
            com.miui.networkassistant.provider.DataCursor$DataEntry r8 = new com.miui.networkassistant.provider.DataCursor$DataEntry
            r8.<init>((long) r11)
            r4[r7] = r8
            r7 = 3
            com.miui.networkassistant.provider.DataCursor$DataEntry r8 = new com.miui.networkassistant.provider.DataCursor$DataEntry
            r8.<init>((long) r5)
            r4[r7] = r8
            r5 = 4
            com.miui.networkassistant.provider.DataCursor$DataEntry r6 = new com.miui.networkassistant.provider.DataCursor$DataEntry
            java.lang.String r0 = java.lang.String.valueOf(r0)
            r6.<init>((java.lang.String) r0)
            r4[r5] = r6
            r2.<init>(r4)
            r3.addRow(r2)
            goto L_0x0100
        L_0x00ff:
            r3 = 0
        L_0x0100:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.provider.NetworkAssistantProvider.queryDataUsageStatus(android.net.Uri):android.database.Cursor");
    }

    private Cursor queryDataUsageStatusDetailed(Uri uri) {
        Context context = getContext();
        String callingPackage = getCallingPackage();
        Log.d(TAG, "queryDataUsageStatusDetailed packageName:" + callingPackage);
        if (context == null || uri == null || this.mTrafficManageBinder == null) {
            Log.e(TAG, "object have null");
            return null;
        }
        DataCursor dataCursor = new DataCursor("total_limit", "month_used", "today_used", ProviderConstant.DataUsageStatusDetailedColumns.SIM_SLOT, ProviderConstant.DataUsageStatusDetailedColumns.TRAFFIC_NAME, ProviderConstant.DataUsageStatusDetailedColumns.TRAFFIC_VALUE, ProviderConstant.DataUsageStatusDetailedColumns.TRAFFIC_UNIT, ProviderConstant.DataUsageStatusDetailedColumns.TRAFFIC_ICON, ProviderConstant.DataUsageStatusDetailedColumns.TRAFFIC_TIME, ProviderConstant.DataUsageStatusDetailedColumns.CLICK_ACTION, ProviderConstant.DataUsageStatusDetailedColumns.BILL_NAME, ProviderConstant.DataUsageStatusDetailedColumns.BILL_VALUE, ProviderConstant.DataUsageStatusDetailedColumns.BILL_UNIT, ProviderConstant.DataUsageStatusDetailedColumns.BILL_ICON, ProviderConstant.DataUsageStatusDetailedColumns.PACKAGE_TYPE);
        int i = 0;
        for (int i2 = 0; i2 < TelephonyUtil.getSimCount(); i2++) {
            SimUserInfo instance = SimUserInfo.getInstance(getContext(), i2);
            if (instance != null && instance.isSimInserted() && instance.hasImsi()) {
                i++;
                long[] trafficBaseInfo = NetworkAssistantProviderHelper.getTrafficBaseInfo(instance, this.mTrafficManageBinder);
                String[] trafficTextInfo = NetworkAssistantProviderHelper.getTrafficTextInfo(context, instance, trafficBaseInfo, callingPackage);
                String[] billTextInfo = NetworkAssistantProviderHelper.getBillTextInfo(context, instance, callingPackage);
                dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(trafficBaseInfo[0]), new DataCursor.DataEntry(trafficBaseInfo[1]), new DataCursor.DataEntry(trafficBaseInfo[2]), new DataCursor.DataEntry(i2), new DataCursor.DataEntry(trafficTextInfo[0]), new DataCursor.DataEntry(trafficTextInfo[1]), new DataCursor.DataEntry(trafficTextInfo[2]), new DataCursor.DataEntry(trafficTextInfo[3]), new DataCursor.DataEntry(trafficTextInfo[4]), new DataCursor.DataEntry(trafficTextInfo[5]), new DataCursor.DataEntry(billTextInfo[0]), new DataCursor.DataEntry(billTextInfo[1]), new DataCursor.DataEntry(billTextInfo[2]), new DataCursor.DataEntry(billTextInfo[3]), new DataCursor.DataEntry(instance.getBrand())));
            }
        }
        if (i == 0) {
            String[] noSimIcon = NetworkAssistantProviderHelper.getNoSimIcon(context, callingPackage);
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(0), new DataCursor.DataEntry(0), new DataCursor.DataEntry(0), new DataCursor.DataEntry(0), new DataCursor.DataEntry(context.getString(R.string.traffic_provider_no_sim)), new DataCursor.DataEntry("--"), new DataCursor.DataEntry(FormatBytesUtil.getMBString(context)), new DataCursor.DataEntry(noSimIcon[0]), new DataCursor.DataEntry((String) o.f2309a), new DataCursor.DataEntry(NetworkAssistantProviderHelper.toUriIntent("miui.intent.action.NETWORKASSISTANT_ENTRANCE", 0)), new DataCursor.DataEntry(context.getString(R.string.traffic_provider_no_sim)), new DataCursor.DataEntry("--"), new DataCursor.DataEntry(context.getString(R.string.yuan)), new DataCursor.DataEntry(noSimIcon[1]), new DataCursor.DataEntry(-2)));
        }
        return dataCursor;
    }

    private Cursor queryFirewallBackgroundRestrictPackage(Uri uri) {
        DataCursor dataCursor = new DataCursor("package_name");
        BackgroundPolicyService instance = BackgroundPolicyService.getInstance(getContext());
        ArrayList<AppInfo> filteredAppInfosList = AppMonitorWrapper.getInstance(getContext()).getFilteredAppInfosList();
        if (filteredAppInfosList == null) {
            return dataCursor;
        }
        ArrayList arrayList = new ArrayList();
        for (AppInfo next : filteredAppInfosList) {
            if (B.a(next.uid) >= 10000 && !PreSetGroup.isPrePolicyPackage(next.packageName.toString()) && instance.isAppRestrictBackground(next.packageName.toString(), next.uid)) {
                arrayList.add(next.packageName.toString());
            }
        }
        constructCursorByRestrictPackages(dataCursor, arrayList);
        return dataCursor;
    }

    private Cursor queryMobileRestrictPackage(Uri uri) {
        DataCursor dataCursor = new DataCursor("package_name");
        if (this.mFirewallBinder == null) {
            return dataCursor;
        }
        try {
            constructCursorByRestrictPackages(dataCursor, this.mFirewallBinder.getMobileRestrictPackages(SimCardHelper.getInstance(getContext()).getCurrentMobileSlotNum()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return dataCursor;
    }

    private Cursor queryMobileRule(Uri uri) {
        DataCursor dataCursor = new DataCursor("package_name", "mobile_rule");
        if (!checkParams(uri)) {
            return dataCursor;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        int checkSlotNum = checkSlotNum(Integer.parseInt(uri.getPathSegments().get(1)));
        Log.i(TAG, String.format("queryMobileRule packageName:%s", new Object[]{packageNameFromUri}));
        try {
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(packageNameFromUri), new DataCursor.DataEntry(this.mFirewallBinder.getMobileRule(packageNameFromUri, checkSlotNum).value())));
        } catch (RemoteException e) {
            Log.i(TAG, "queryMobileRule", e);
        }
        return dataCursor;
    }

    private Cursor queryNASettingsInfoStatus(Uri uri) {
        if (!(uri == null || this.mTrafficManageBinder == null)) {
            int currentMobileSlotNum = DeviceUtil.IS_DUAL_CARD ? SimCardHelper.getInstance(getContext()).getCurrentMobileSlotNum() : 0;
            SimUserInfo instance = SimUserInfo.getInstance(getContext(), currentMobileSlotNum);
            if (instance != null && instance.hasImsi() && instance.isSimInserted()) {
                DataCursor dataCursor = new DataCursor(ProviderConstant.NASettingsInfoColumns.OPERATOR_SETTED, ProviderConstant.NASettingsInfoColumns.CORRECTION_TIME, ProviderConstant.NASettingsInfoColumns.TRAFFIC_SAVING_STARTED, ProviderConstant.NASettingsInfoColumns.SHOW_STATUS_BAR_SETTED, ProviderConstant.NASettingsInfoColumns.NEEDED_TRAFFIC_PURCHASE, ProviderConstant.NASettingsInfoColumns.OVERSEA_VERSION, ProviderConstant.NASettingsInfoColumns.TRAFFIC_SAVING_ENABLED, ProviderConstant.NASettingsInfoColumns.AUTO_TRAFFIC_CORRECTION, ProviderConstant.NASettingsInfoColumns.TC_DIAGNOSTIC);
                boolean isOperatorSetted = instance.isOperatorSetted();
                long dataUsageCorrectedTime = instance.getDataUsageCorrectedTime();
                try {
                    int i = Settings.System.getInt(getContext().getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 0);
                    boolean isNeededPurchasePkg = this.mTrafficManageBinder.isNeededPurchasePkg(currentMobileSlotNum);
                    boolean isOversea = instance.isOversea();
                    boolean isDataUsageAutoCorrectionOn = (!isOperatorSetted || !instance.isSupportCorrection() || instance.isDataRoaming()) ? true : instance.isDataUsageAutoCorrectionOn();
                    if (MiSimUtil.isMiSimEnable(getContext(), currentMobileSlotNum)) {
                        isOperatorSetted = true;
                    }
                    dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(String.valueOf(isOperatorSetted)), new DataCursor.DataEntry(dataUsageCorrectedTime), new DataCursor.DataEntry(String.valueOf(true)), new DataCursor.DataEntry(i), new DataCursor.DataEntry(String.valueOf(isNeededPurchasePkg)), new DataCursor.DataEntry(String.valueOf(isOversea)), new DataCursor.DataEntry(String.valueOf(false)), new DataCursor.DataEntry(String.valueOf(isDataUsageAutoCorrectionOn)), new DataCursor.DataEntry(String.valueOf(false))));
                    return dataCursor;
                } catch (RemoteException e) {
                    Log.i(TAG, "queryNASettingsInfoStatus exception", e);
                    return dataCursor;
                }
            }
        }
        return null;
    }

    private Cursor queryTempMobileRule(Uri uri) {
        DataCursor dataCursor = new DataCursor("package_name", ProviderConstant.TempMobileFirewallColumns.FIREWALL_TEMP_MOBILE_RULE);
        if (!checkParams(uri)) {
            return dataCursor;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        int checkSlotNum = checkSlotNum(Integer.parseInt(uri.getPathSegments().get(1)));
        Log.i(TAG, String.format("queryTempMobileRule packageName:%s", new Object[]{packageNameFromUri}));
        try {
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(packageNameFromUri), new DataCursor.DataEntry(this.mFirewallBinder.getTempMobileRule(packageNameFromUri, checkSlotNum).value())));
        } catch (RemoteException e) {
            Log.i(TAG, "queryTempMobileRule", e);
        }
        return dataCursor;
    }

    private Cursor queryTempWifiRule(Uri uri) {
        DataCursor dataCursor = new DataCursor("package_name", ProviderConstant.TempWifiFirewallColumns.FIREWALL_TEMP_WIFI_RULE);
        if (!checkParams(uri)) {
            return dataCursor;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        Log.i(TAG, String.format("queryTempWifiRule packageName:%s", new Object[]{packageNameFromUri}));
        try {
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(packageNameFromUri), new DataCursor.DataEntry(this.mFirewallBinder.getTempWifiRule(packageNameFromUri).value())));
        } catch (RemoteException e) {
            Log.i(TAG, "queryTempWifiRule", e);
        }
        return dataCursor;
    }

    private Cursor queryTetheringLimitEnable(Uri uri) {
        if (uri == null) {
            return null;
        }
        DataCursor dataCursor = new DataCursor("tethering_limit_enabled");
        dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(String.valueOf(CommonConfig.getInstance(getContext()).getTetheringLimitEnabled()))));
        return dataCursor;
    }

    private Cursor queryTopUsedAppList(Uri uri) {
        if (uri == null) {
            return null;
        }
        int currentMobileSlotNum = DeviceUtil.IS_DUAL_CARD ? SimCardHelper.getInstance(getContext()).getCurrentMobileSlotNum() : 0;
        DataCursor dataCursor = new DataCursor("_id", "package_name", ProviderConstant.TrafficUsedAppListColumns.TRAFFIC_USED);
        if (uri.getPathSegments().size() <= 0) {
            return dataCursor;
        }
        try {
            int parseInt = Integer.parseInt(uri.getPathSegments().get(1));
            SimUserInfo instance = SimUserInfo.getInstance(getContext(), currentMobileSlotNum);
            if (instance == null || !instance.hasImsi() || !instance.isSimInserted()) {
                return dataCursor;
            }
            Map<Long, String> todayDataUsageAppMapByDec = new StatisticAppTraffic(getContext(), instance.getImsi()).getTodayDataUsageAppMapByDec(getContext());
            if (todayDataUsageAppMapByDec.size() <= 0) {
                return dataCursor;
            }
            if (todayDataUsageAppMapByDec.size() <= parseInt) {
                parseInt = todayDataUsageAppMapByDec.size();
            }
            Iterator<Map.Entry<Long, String>> it = todayDataUsageAppMapByDec.entrySet().iterator();
            int i = 0;
            while (it.hasNext() && i < parseInt) {
                Map.Entry next = it.next();
                dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(i), new DataCursor.DataEntry((String) next.getValue()), new DataCursor.DataEntry(((Long) next.getKey()).longValue())));
                i++;
            }
            return dataCursor;
        } catch (NumberFormatException unused) {
            return dataCursor;
        }
    }

    private Cursor queryTrafficPurchaseConfig(Uri uri) {
        if (uri == null) {
            return null;
        }
        DataCursor dataCursor = new DataCursor(ProviderConstant.TrafficPurchaseConfigColumns.FIRST_ENTER_CONFIG);
        dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(String.valueOf(CommonConfig.getInstance(getContext()).getFirstEnterTrafficPurchaseDeclare()))));
        return dataCursor;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0069 A[Catch:{ RemoteException -> 0x0082 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x006b A[Catch:{ RemoteException -> 0x0082 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.database.Cursor queryTrafficPurchaseStatus(android.net.Uri r6) {
        /*
            r5 = this;
            if (r6 == 0) goto L_0x008b
            com.miui.networkassistant.service.ITrafficManageBinder r0 = r5.mTrafficManageBinder
            if (r0 == 0) goto L_0x008b
            java.util.List r0 = r6.getPathSegments()
            int r0 = r0.size()
            r1 = 0
            r2 = 1
            if (r0 <= r2) goto L_0x0036
            java.util.List r0 = r6.getPathSegments()
            java.lang.Object r0 = r0.get(r2)
            java.lang.String r0 = (java.lang.String) r0
            java.util.List r6 = r6.getPathSegments()
            r3 = 2
            java.lang.Object r6 = r6.get(r3)
            java.lang.String r6 = (java.lang.String) r6
            if (r0 == 0) goto L_0x0047
            java.lang.String r3 = "slotId"
            boolean r0 = r0.equals(r3)
            if (r0 == 0) goto L_0x0047
            int r6 = r5.getSlotNum(r6)
            goto L_0x0048
        L_0x0036:
            boolean r6 = com.miui.networkassistant.utils.DeviceUtil.IS_DUAL_CARD
            if (r6 == 0) goto L_0x0047
            android.content.Context r6 = r5.getContext()
            com.miui.networkassistant.dual.SimCardHelper r6 = com.miui.networkassistant.dual.SimCardHelper.getInstance(r6)
            int r6 = r6.getCurrentMobileSlotNum()
            goto L_0x0048
        L_0x0047:
            r6 = r1
        L_0x0048:
            com.miui.networkassistant.provider.DataCursor r0 = new com.miui.networkassistant.provider.DataCursor
            java.lang.String r3 = "traffic_purchase_enabled"
            java.lang.String[] r3 = new java.lang.String[]{r3}
            r0.<init>(r3)
            android.content.Context r3 = r5.getContext()     // Catch:{ RemoteException -> 0x0082 }
            com.miui.networkassistant.config.SimUserInfo r3 = com.miui.networkassistant.config.SimUserInfo.getInstance((android.content.Context) r3, (int) r6)     // Catch:{ RemoteException -> 0x0082 }
            com.miui.networkassistant.service.ITrafficManageBinder r4 = r5.mTrafficManageBinder     // Catch:{ RemoteException -> 0x0082 }
            boolean r6 = r4.isNeededPurchasePkg(r6)     // Catch:{ RemoteException -> 0x0082 }
            if (r6 == 0) goto L_0x006b
            boolean r6 = com.miui.networkassistant.traffic.statistic.LeisureTrafficHelper.isLeisureTime(r3)     // Catch:{ RemoteException -> 0x0082 }
            if (r6 != 0) goto L_0x006b
            r6 = r2
            goto L_0x006c
        L_0x006b:
            r6 = r1
        L_0x006c:
            com.miui.networkassistant.provider.DataCursor$DataRow r3 = new com.miui.networkassistant.provider.DataCursor$DataRow     // Catch:{ RemoteException -> 0x0082 }
            com.miui.networkassistant.provider.DataCursor$DataEntry[] r2 = new com.miui.networkassistant.provider.DataCursor.DataEntry[r2]     // Catch:{ RemoteException -> 0x0082 }
            com.miui.networkassistant.provider.DataCursor$DataEntry r4 = new com.miui.networkassistant.provider.DataCursor$DataEntry     // Catch:{ RemoteException -> 0x0082 }
            java.lang.String r6 = java.lang.String.valueOf(r6)     // Catch:{ RemoteException -> 0x0082 }
            r4.<init>((java.lang.String) r6)     // Catch:{ RemoteException -> 0x0082 }
            r2[r1] = r4     // Catch:{ RemoteException -> 0x0082 }
            r3.<init>(r2)     // Catch:{ RemoteException -> 0x0082 }
            r0.addRow(r3)     // Catch:{ RemoteException -> 0x0082 }
            goto L_0x008c
        L_0x0082:
            r6 = move-exception
            java.lang.String r1 = "NAProvider"
            java.lang.String r2 = "queryTrafficPurchaseStatus"
            android.util.Log.i(r1, r2, r6)
            goto L_0x008c
        L_0x008b:
            r0 = 0
        L_0x008c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.provider.NetworkAssistantProvider.queryTrafficPurchaseStatus(android.net.Uri):android.database.Cursor");
    }

    private Cursor queryTrafficStats(Uri uri) {
        long j;
        long j2;
        String str = null;
        if (uri == null) {
            return null;
        }
        if (uri.getPathSegments().size() > 1) {
            str = uri.getPathSegments().get(1);
        }
        if (TextUtils.isEmpty(str) || !TextUtils.isDigitsOnly(str)) {
            synchronized (this.mTrafficStatsLock) {
                j2 = 0;
                if (this.mNeedSetToZero) {
                    this.mNeedSetToZero = false;
                    j = 0;
                } else if (this.mCurrentActiveIface == null) {
                    j2 = TrafficStats.getTotalTxBytes();
                    j = TrafficStats.getTotalRxBytes();
                    Log.e(TAG, "mCurrentActiveIface is null");
                } else {
                    j2 = NaTrafficStats.getTxBytes(this.mCurrentActiveIface);
                    j = NaTrafficStats.getRxBytes(this.mCurrentActiveIface);
                }
            }
        } else {
            int parseInt = Integer.parseInt(str);
            j2 = TrafficStats.getUidTxBytes(parseInt);
            j = TrafficStats.getUidRxBytes(parseInt);
        }
        DataCursor dataCursor = new DataCursor("total_tx_byte", "total_rx_byte");
        dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(j2), new DataCursor.DataEntry(j)));
        return dataCursor;
    }

    private Cursor queryWifiRule(Uri uri) {
        DataCursor dataCursor = new DataCursor("package_name", "wifi_rule");
        if (!checkParams(uri)) {
            return dataCursor;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        Log.i(TAG, String.format("queryWifiRule packageName:%s", new Object[]{packageNameFromUri}));
        try {
            dataCursor.addRow(new DataCursor.DataRow(new DataCursor.DataEntry(packageNameFromUri), new DataCursor.DataEntry(this.mFirewallBinder.getWifiRule(packageNameFromUri).value())));
        } catch (RemoteException e) {
            Log.i(TAG, "queryWifiFirewallState", e);
        }
        return dataCursor;
    }

    private Cursor queryWlanRestrictPackage(Uri uri) {
        DataCursor dataCursor = new DataCursor("package_name");
        IFirewallBinder iFirewallBinder = this.mFirewallBinder;
        if (iFirewallBinder == null) {
            return dataCursor;
        }
        try {
            constructCursorByRestrictPackages(dataCursor, iFirewallBinder.getWifiRestrictPackages());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return dataCursor;
    }

    /* access modifiers changed from: private */
    public void refreshActiveIfaceIfNeed() {
        synchronized (this.mTrafficStatsLock) {
            f.a c2 = f.c(getContext());
            if (this.mCurrentNetworkState != c2) {
                this.mNeedSetToZero = true;
                this.mCurrentNetworkState = c2;
                Log.i(TAG, String.format("network switch to %s", new Object[]{c2.toString()}));
                this.mCurrentActiveIface = f.b(getContext());
            }
        }
    }

    private void registerNetworkReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.CONNECTIVITY_ACTION_IMMEDIATE);
        getContext().registerReceiver(this.mReceiver, intentFilter);
    }

    private int setMobileRuleByPkgName(Uri uri, ContentValues contentValues) {
        if (!checkParams(uri, contentValues)) {
            return 0;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        Boolean asBoolean = contentValues.getAsBoolean("mobile_rule");
        int checkSlotNum = checkSlotNum(contentValues.getAsInteger(ProviderConstant.MobileFirewallColumns.FIREWALL_MOBILE_RULE_SLOTNUM).intValue());
        String asString = contentValues.getAsString("source_package_name");
        AnalyticsHelper.trackSetMobileFirewallRule(asString, asBoolean.booleanValue());
        Log.i(TAG, String.format("setMobileRuleByPkgName packageName:%s, slotnum:%s, isRestrict:%s, sourcePackage:%s", new Object[]{packageNameFromUri, Integer.valueOf(checkSlotNum), asBoolean, asString}));
        try {
            return this.mFirewallBinder.setMobileRule(packageNameFromUri, asBoolean.booleanValue() ? FirewallRule.Restrict : FirewallRule.Allow, checkSlotNum) ? 1 : 0;
        } catch (RemoteException e) {
            Log.i(TAG, "set mobile rule", e);
            return 0;
        }
    }

    private int setNASettingsInfoStatus(Uri uri, ContentValues contentValues) {
        int intValue;
        int i = 0;
        if (uri == null || contentValues == null || this.mTrafficManageBinder == null) {
            return 0;
        }
        if (contentValues.containsKey(ProviderConstant.NASettingsInfoColumns.SHOW_STATUS_BAR_SETTED) && (intValue = contentValues.getAsInteger(ProviderConstant.NASettingsInfoColumns.SHOW_STATUS_BAR_SETTED).intValue()) == 1) {
            Settings.System.putInt(getContext().getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, intValue);
        }
        if (!contentValues.containsKey(ProviderConstant.NASettingsInfoColumns.AUTO_TRAFFIC_CORRECTION)) {
            return 1;
        }
        boolean booleanValue = contentValues.getAsBoolean(ProviderConstant.NASettingsInfoColumns.AUTO_TRAFFIC_CORRECTION).booleanValue();
        if (DeviceUtil.IS_DUAL_CARD) {
            i = SimCardHelper.getInstance(getContext()).getCurrentMobileSlotNum();
        }
        SimUserInfo.getInstance(getContext(), i).toggleDataUsageAutoCorrection(booleanValue);
        return 1;
    }

    private int setTempMobileRuleByPkgName(Uri uri, ContentValues contentValues) {
        if (!checkParams(uri, contentValues)) {
            return 0;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        int checkSlotNum = checkSlotNum(contentValues.getAsInteger(ProviderConstant.TempMobileFirewallColumns.FIREWALL_TEMP_MOBILE_RULE_SLOTNUM).intValue());
        Boolean asBoolean = contentValues.getAsBoolean(ProviderConstant.TempMobileFirewallColumns.FIREWALL_TEMP_MOBILE_RULE);
        String asString = contentValues.getAsString("source_package_name");
        if (TextUtils.isEmpty(asString)) {
            Log.i(TAG, "srcPkgName must not be empty");
            return 0;
        }
        Log.i(TAG, String.format("setTempMobileRuleByPkgName packageName:%s, slotnum:%s, isRestrict:%s, srcPkgName:%s", new Object[]{packageNameFromUri, Integer.valueOf(checkSlotNum), asBoolean, asString}));
        try {
            return this.mFirewallBinder.setTempMobileRule(packageNameFromUri, asBoolean.booleanValue() ? FirewallRule.Restrict : FirewallRule.Allow, asString, checkSlotNum) ? 1 : 0;
        } catch (RemoteException e) {
            Log.i(TAG, "set temp mobile rule", e);
            return 0;
        }
    }

    private int setTempWifiRuleByPkgName(Uri uri, ContentValues contentValues) {
        if (!checkParams(uri, contentValues)) {
            return 0;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        Boolean asBoolean = contentValues.getAsBoolean(ProviderConstant.TempWifiFirewallColumns.FIREWALL_TEMP_WIFI_RULE);
        String asString = contentValues.getAsString("source_package_name");
        if (TextUtils.isEmpty(asString)) {
            Log.i(TAG, "srcPkgName must not be empty");
            return 0;
        }
        Log.i(TAG, String.format("setTempWifiRuleByPkgName packageName:%s, isRestrict:%s, srcPkgName:%s", new Object[]{packageNameFromUri, asBoolean, asString}));
        try {
            return this.mFirewallBinder.setTempWifiRule(packageNameFromUri, asBoolean.booleanValue() ? FirewallRule.Restrict : FirewallRule.Allow, asString) ? 1 : 0;
        } catch (RemoteException e) {
            Log.i(TAG, "set temp wifi rule", e);
            return 0;
        }
    }

    private int setTetheringLimitEnabled(Uri uri, ContentValues contentValues) {
        if (uri == null || contentValues == null || !contentValues.containsKey("tethering_limit_enabled")) {
            return 0;
        }
        CommonConfig.getInstance(getContext()).setTetheringLimitEnabled(contentValues.getAsBoolean("tethering_limit_enabled").booleanValue());
        return 1;
    }

    private int setTrafficPurchaseConfig(Uri uri, ContentValues contentValues) {
        int i = 0;
        if (uri == null || contentValues == null) {
            return 0;
        }
        if (contentValues.containsKey(ProviderConstant.TrafficPurchaseConfigColumns.FIRST_ENTER_CONFIG)) {
            CommonConfig.getInstance(getContext()).setFirstEnterTrafficPurchaseDeclare(contentValues.getAsBoolean(ProviderConstant.TrafficPurchaseConfigColumns.FIRST_ENTER_CONFIG).booleanValue());
            i = 1;
        }
        if (!contentValues.containsKey("slot_num") || !contentValues.containsKey(ProviderConstant.TrafficPurchaseConfigColumns.TRAFFIC_ALERT)) {
            return i;
        }
        SimUserInfo.getInstance(getContext(), contentValues.getAsInteger("slot_num").intValue()).setPackageChangeUpdateTime(0);
        return 1;
    }

    private int setWifiRuleByPkgName(Uri uri, ContentValues contentValues) {
        if (!checkParams(uri, contentValues)) {
            return 0;
        }
        String packageNameFromUri = getPackageNameFromUri(uri);
        Boolean asBoolean = contentValues.getAsBoolean("wifi_rule");
        String asString = contentValues.getAsString("source_package_name");
        AnalyticsHelper.trackSetWlanFirewallRule(asString, asBoolean.booleanValue());
        Log.i(TAG, String.format("seWifiRuleByPkgName packageName:%s, isRestrict:%s, sourcePackage:%s", new Object[]{packageNameFromUri, asBoolean, asString}));
        try {
            return this.mFirewallBinder.setWifiRule(packageNameFromUri, asBoolean.booleanValue() ? FirewallRule.Restrict : FirewallRule.Allow) ? 1 : 0;
        } catch (RemoteException e) {
            Log.i(TAG, "set wifi rule", e);
            return 0;
        }
    }

    private int startCorrection(Uri uri, ContentValues contentValues) {
        String str;
        if (this.mTrafficManageBinder == null || uri == null || contentValues == null) {
            return 0;
        }
        try {
            int intValue = contentValues.getAsInteger(Sim.SIM_SLOT_NUM_TAG).intValue();
            int intValue2 = contentValues.getAsInteger(ITrafficCorrection.KEY_CORRECTION_TYPE).intValue();
            String asString = contentValues.getAsString(AnimatedTarget.STATE_TAG_FROM);
            String format = String.format("%s_%s", new Object[]{asString, Integer.valueOf(intValue)});
            SimUserInfo instance = SimUserInfo.getInstance(getContext(), intValue);
            Log.i(TAG, String.format("type:%s,key:%s", new Object[]{Integer.valueOf(intValue2), format}));
            if (System.currentTimeMillis() > instance.getCorrectionSourceUpdateTime(format)) {
                instance.setCorrectionSourceUpdateTime(format, DateUtil.getTodayTimeMillis() + 86400000);
                this.mTrafficManageBinder.startCorrection(true, intValue, false, intValue2);
                return 1;
            }
            Log.i(TAG, String.format("%s request more times.", new Object[]{asString}));
            return 0;
        } catch (RemoteException e) {
            e = e;
            str = "startCorrection RemoteException ";
            Log.i(TAG, str, e);
            return 0;
        } catch (NullPointerException e2) {
            e = e2;
            str = "startCorrection NullPointerException";
            Log.i(TAG, str, e);
            return 0;
        }
    }

    private void unRegisterNetworkReceiver() {
        getContext().unregisterReceiver(this.mReceiver);
    }

    public int delete(Uri uri, String str, String[] strArr) {
        int i;
        String str2;
        if (this.mDb == null) {
            this.mDb = this.mOpenHelper.getWritableDatabase();
        }
        int match = sUriMatcher.match(uri);
        if (match == 16) {
            i = this.mDb.delete("traffic_distribution", str, strArr);
        } else if (match == 17) {
            SQLiteDatabase sQLiteDatabase = this.mDb;
            StringBuilder sb = new StringBuilder();
            sb.append("_id=");
            sb.append(uri.getPathSegments().get(1));
            if (!TextUtils.isEmpty(str)) {
                str2 = " AND (" + str + ')';
            } else {
                str2 = "";
            }
            sb.append(str2);
            i = sQLiteDatabase.delete("traffic_distribution", sb.toString(), strArr);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        return i;
    }

    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        if (match == 16) {
            return ProviderConstant.CONTENT_TYPE;
        }
        if (match == 17) {
            return ProviderConstant.CONTENT_ITEM_TYPE;
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        if (this.mDb == null) {
            this.mDb = this.mOpenHelper.getWritableDatabase();
        }
        if (sUriMatcher.match(uri) == 16) {
            long insert = this.mDb.insert("traffic_distribution", (String) null, contentValues);
            if (insert > 0) {
                Uri withAppendedId = ContentUris.withAppendedId(ProviderConstant.TrafficDistributionColumns.CONTENT_URI, insert);
                getContext().getContentResolver().notifyChange(withAppendedId, (ContentObserver) null);
                return withAppendedId;
            }
            throw new SQLException("Failed to insert row into " + uri);
        }
        throw new IllegalArgumentException("Unknown URI " + uri);
    }

    public boolean onCreate() {
        Log.i(TAG, "onCreate");
        bindFirewallService();
        bindTrafficManageService();
        registerNetworkReceiver();
        this.mOpenHelper = new DBHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        int match = sUriMatcher.match(uri);
        if (match == 16 || match == 17) {
            return doQueryDB(uri, strArr, str, strArr2, str2);
        }
        if (match == 48) {
            return queryDataUsageStatus(uri);
        }
        if (match == 49) {
            return queryDataUsageNotiStatus(uri);
        }
        if (match == 64) {
            return queryNASettingsInfoStatus(uri);
        }
        if (match == 80) {
            return queryTrafficStats(uri);
        }
        if (match == 96) {
            return queryTrafficPurchaseStatus(uri);
        }
        if (match == TRAFFIC_PURCHASE_CONFIG_CODE) {
            return queryTrafficPurchaseConfig(uri);
        }
        if (match == TRAFFIC_USED_APP_LIST_CODE) {
            return queryTopUsedAppList(uri);
        }
        if (match == BILL_PACKAGE_DETAIL_CODE) {
            return queryBillPackageDetail(uri);
        }
        if (match == CALL_TIME_PACKAGE_DETAIL_CODE) {
            return queryCallTimePackageDetail(uri);
        }
        if (match == TETHERING_LIMIT_ENABLED_CODE) {
            return queryTetheringLimitEnable(uri);
        }
        if (match == DATA_USAGE_STATUS_DETAILED_CODE) {
            return queryDataUsageStatusDetailed(uri);
        }
        switch (match) {
            case 33:
                return queryTempMobileRule(uri);
            case 34:
                return queryTempWifiRule(uri);
            case 35:
                return queryMobileRule(uri);
            case 36:
                return queryWifiRule(uri);
            case 37:
                return queryWlanRestrictPackage(uri);
            case 38:
                return queryMobileRestrictPackage(uri);
            case 39:
                return queryFirewallBackgroundRestrictPackage(uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int i;
        String str2;
        if (this.mDb == null) {
            this.mDb = this.mOpenHelper.getWritableDatabase();
        }
        int match = sUriMatcher.match(uri);
        if (match == 16) {
            i = this.mDb.update("traffic_distribution", contentValues, str, strArr);
        } else if (match == 17) {
            SQLiteDatabase sQLiteDatabase = this.mDb;
            StringBuilder sb = new StringBuilder();
            sb.append("_id=");
            sb.append(uri.getPathSegments().get(1));
            if (!TextUtils.isEmpty(str)) {
                str2 = " AND (" + str + ')';
            } else {
                str2 = "";
            }
            sb.append(str2);
            i = sQLiteDatabase.update("traffic_distribution", contentValues, sb.toString(), strArr);
        } else if (match == 64) {
            i = setNASettingsInfoStatus(uri, contentValues);
        } else if (match == TRAFFIC_PURCHASE_CONFIG_CODE) {
            i = setTrafficPurchaseConfig(uri, contentValues);
        } else if (match == SMS_CORRECTION_CODE) {
            i = startCorrection(uri, contentValues);
        } else if (match != TETHERING_LIMIT_ENABLED_CODE) {
            switch (match) {
                case 33:
                    i = setTempMobileRuleByPkgName(uri, contentValues);
                    break;
                case 34:
                    i = setTempWifiRuleByPkgName(uri, contentValues);
                    break;
                case 35:
                    i = setMobileRuleByPkgName(uri, contentValues);
                    break;
                case 36:
                    i = setWifiRuleByPkgName(uri, contentValues);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
        } else {
            i = setTetheringLimitEnabled(uri, contentValues);
        }
        getContext().getContentResolver().notifyChange(uri, (ContentObserver) null);
        return i;
    }
}
