package com.miui.networkassistant.netdiagnose;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.c;
import com.miui.activityutil.o;
import com.miui.luckymoney.config.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miui.cloud.CloudPushConstants;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class APNManager {
    private static final String ALL_CARRIERS_URI = "content://telephony/carriers";
    public static final int APN_FAIL_MODIFIED = 1;
    public static final int APN_FAIL_PREFERRED_WAP = 2;
    public static final int APN_PASS = 0;
    public static final String PARTNER_APNS_PATH = "etc/apns-conf.xml";
    private static final String PREFERRED_APN_SUBID_URI = "content://telephony/carriers/preferapn/subId";
    private static final String PREFERRED_APN_URI = "content://telephony/carriers/preferapn";
    private static final String RESTORE_CARRIERS_URI = "content://telephony/carriers/restore";
    private static final String TAG = "NetworkDiagnostics_APNManager";
    private Context mContext;
    private Uri mCreateUri = Uri.parse(ALL_CARRIERS_URI);
    private Uri mPreferapnUri = Uri.parse(PREFERRED_APN_URI);
    private Uri mRestoreapnUri = Uri.parse(RESTORE_CARRIERS_URI);

    public APNManager(Context context) {
        this.mContext = context;
    }

    private void beginDocument(XmlPullParser xmlPullParser, String str) {
        c.a.a("com.android.internal.util.XmlUtils").b("beginDocument", new Class[]{XmlPullParser.class, String.class}, xmlPullParser, str);
    }

    private HashMap<String, String> getApnRowFromXml(XmlPullParser xmlPullParser) {
        XmlPullParser xmlPullParser2 = xmlPullParser;
        String str = "mvno_match_data";
        if (!"apn".equals(xmlPullParser.getName())) {
            return null;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        String str2 = "mvno_type";
        try {
            String attributeValue = xmlPullParser2.getAttributeValue((String) null, "mcc");
            String str3 = "bearer";
            String attributeValue2 = xmlPullParser2.getAttributeValue((String) null, "mnc");
            hashMap.put("numeric", attributeValue + attributeValue2);
            hashMap.put(CloudPushConstants.XML_NAME, xmlPullParser2.getAttributeValue((String) null, Constants.JSON_KEY_CARRIER));
            hashMap.put("apn", xmlPullParser2.getAttributeValue((String) null, "apn"));
            hashMap.put("user", xmlPullParser2.getAttributeValue((String) null, "user"));
            hashMap.put("server", xmlPullParser2.getAttributeValue((String) null, "server"));
            hashMap.put("password", xmlPullParser2.getAttributeValue((String) null, "password"));
            hashMap.put("proxy", xmlPullParser2.getAttributeValue((String) null, "proxy"));
            hashMap.put("port", xmlPullParser2.getAttributeValue((String) null, "port"));
            hashMap.put("mmsproxy", xmlPullParser2.getAttributeValue((String) null, "mmsproxy"));
            hashMap.put("mmsport", xmlPullParser2.getAttributeValue((String) null, "mmsport"));
            hashMap.put("mmsc", xmlPullParser2.getAttributeValue((String) null, "mmsc"));
            hashMap.put("type", xmlPullParser2.getAttributeValue((String) null, "type"));
            String attributeValue3 = xmlPullParser2.getAttributeValue((String) null, "authtype");
            if (attributeValue3 == null) {
                attributeValue3 = "-1";
            }
            hashMap.put("authtype", attributeValue3);
            String str4 = str3;
            String attributeValue4 = xmlPullParser2.getAttributeValue((String) null, str4);
            if (attributeValue4 == null) {
                hashMap.put(str4, o.f2309a);
            } else {
                hashMap.put(str4, attributeValue4);
            }
            String str5 = str2;
            hashMap.put(str5, xmlPullParser2.getAttributeValue((String) null, str5));
            String str6 = str;
            hashMap.put(str6, xmlPullParser2.getAttributeValue((String) null, str6));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0144, code lost:
        if (r15 != null) goto L_0x0146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0146, code lost:
        r15.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0152, code lost:
        if (r15 != null) goto L_0x0146;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0155, code lost:
        r0 = r14.entrySet().iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0161, code lost:
        if (r0.hasNext() == false) goto L_0x017e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0163, code lost:
        r1 = r0.next();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0175, code lost:
        if (android.text.TextUtils.equals((java.lang.CharSequence) r1.getValue(), "") == false) goto L_0x017c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0177, code lost:
        r1.setValue((java.lang.Object) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x017e, code lost:
        return r14;
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0182  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.HashMap<java.lang.String, java.lang.String> getCurrentApnDetail() {
        /*
            r23 = this;
            java.lang.String r0 = "mvno_type"
            java.lang.String r1 = "bearer"
            java.lang.String r2 = "authtype"
            java.lang.String r3 = "type"
            java.lang.String r4 = "mmsc"
            java.lang.String r5 = "mmsport"
            java.lang.String r6 = "mmsproxy"
            java.lang.String r7 = "port"
            java.lang.String r8 = "proxy"
            java.lang.String r9 = "password"
            java.lang.String r10 = "server"
            java.lang.String r11 = "user"
            java.lang.String r12 = "apn"
            java.lang.String r13 = "name"
            int r14 = com.miui.networkassistant.utils.TelephonyUtil.getDefaultDataSubscriptionId()
            boolean r15 = com.miui.networkassistant.utils.DeviceUtil.IS_DUAL_CARD
            if (r15 == 0) goto L_0x0038
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r16 = r0
            java.lang.String r0 = "content://telephony/carriers/preferapn/subId/"
            r15.append(r0)
            r15.append(r14)
            java.lang.String r0 = r15.toString()
            goto L_0x003c
        L_0x0038:
            r16 = r0
            java.lang.String r0 = "content://telephony/carriers/preferapn"
        L_0x003c:
            android.net.Uri r0 = android.net.Uri.parse(r0)
            r18 = r0
            java.util.HashMap r14 = new java.util.HashMap
            r14.<init>()
            r15 = r23
            android.content.Context r0 = r15.mContext     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            android.content.ContentResolver r17 = r0.getContentResolver()     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            r19 = 0
            r20 = 0
            r21 = 0
            java.lang.String r22 = "name ASC"
            android.database.Cursor r15 = r17.query(r18, r19, r20, r21, r22)     // Catch:{ Exception -> 0x014d, all -> 0x014a }
            if (r15 == 0) goto L_0x0144
            int r0 = r15.getCount()     // Catch:{ Exception -> 0x0142 }
            if (r0 <= 0) goto L_0x0144
            r15.moveToFirst()     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = "mcc"
            int r0 = r15.getColumnIndex(r0)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r17 = r1
            java.lang.String r1 = "mnc"
            int r1 = r15.getColumnIndex(r1)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r1 = r15.getString(r1)     // Catch:{ Exception -> 0x0142 }
            r18 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0142 }
            r2.<init>()     // Catch:{ Exception -> 0x0142 }
            r2.append(r0)     // Catch:{ Exception -> 0x0142 }
            r2.append(r1)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x0142 }
            java.lang.String r1 = "numeric"
            r14.put(r1, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r13)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r13, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r12)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r12, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r11)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r11, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r10)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r10, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r9)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r9, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r8)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r8, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r7)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r7, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r6)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r6, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r5)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r5, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r4)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r4, r0)     // Catch:{ Exception -> 0x0142 }
            int r0 = r15.getColumnIndex(r3)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = r15.getString(r0)     // Catch:{ Exception -> 0x0142 }
            r14.put(r3, r0)     // Catch:{ Exception -> 0x0142 }
            r0 = r18
            int r1 = r15.getColumnIndex(r0)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r1 = r15.getString(r1)     // Catch:{ Exception -> 0x0142 }
            r14.put(r0, r1)     // Catch:{ Exception -> 0x0142 }
            r0 = r17
            int r1 = r15.getColumnIndex(r0)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r1 = r15.getString(r1)     // Catch:{ Exception -> 0x0142 }
            r14.put(r0, r1)     // Catch:{ Exception -> 0x0142 }
            r0 = r16
            int r1 = r15.getColumnIndex(r0)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r1 = r15.getString(r1)     // Catch:{ Exception -> 0x0142 }
            r14.put(r0, r1)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r0 = "mvno_match_data"
            java.lang.String r1 = "mvno_match_data"
            int r1 = r15.getColumnIndex(r1)     // Catch:{ Exception -> 0x0142 }
            java.lang.String r1 = r15.getString(r1)     // Catch:{ Exception -> 0x0142 }
            r14.put(r0, r1)     // Catch:{ Exception -> 0x0142 }
            goto L_0x0144
        L_0x0142:
            r0 = move-exception
            goto L_0x014f
        L_0x0144:
            if (r15 == 0) goto L_0x0155
        L_0x0146:
            r15.close()
            goto L_0x0155
        L_0x014a:
            r0 = move-exception
            r15 = 0
            goto L_0x0180
        L_0x014d:
            r0 = move-exception
            r15 = 0
        L_0x014f:
            r0.printStackTrace()     // Catch:{ all -> 0x017f }
            if (r15 == 0) goto L_0x0155
            goto L_0x0146
        L_0x0155:
            java.util.Set r0 = r14.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x015d:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x017e
            java.lang.Object r1 = r0.next()
            java.util.Map$Entry r1 = (java.util.Map.Entry) r1
            java.lang.Object r2 = r1.getValue()
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            java.lang.String r3 = ""
            boolean r2 = android.text.TextUtils.equals(r2, r3)
            if (r2 == 0) goto L_0x017c
            r2 = 0
            r1.setValue(r2)
            goto L_0x015d
        L_0x017c:
            r2 = 0
            goto L_0x015d
        L_0x017e:
            return r14
        L_0x017f:
            r0 = move-exception
        L_0x0180:
            if (r15 == 0) goto L_0x0185
            r15.close()
        L_0x0185:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.APNManager.getCurrentApnDetail():java.util.HashMap");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0092, code lost:
        if (r6 != null) goto L_0x004b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0029  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0075 A[Catch:{ FileNotFoundException -> 0x0076, Exception -> 0x0056, all -> 0x0053, all -> 0x004f }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x009c A[LOOP:0: B:42:0x0096->B:44:0x009c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00a9 A[SYNTHETIC, Splitter:B:47:0x00a9] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00b0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.ArrayList<java.util.HashMap<java.lang.String, java.lang.String>> loadApns(java.lang.String r9) {
        /*
            r8 = this;
            java.lang.String r0 = "'"
            java.lang.String r1 = "apns"
            java.lang.String r2 = "NetworkDiagnostics_APNManager"
            r3 = 0
            android.content.Context r4 = r8.mContext     // Catch:{ Exception -> 0x0020, all -> 0x001c }
            android.content.res.XmlResourceParser r4 = miui.securitycenter.utils.SecurityCenterHelper.getApnsXml(r4)     // Catch:{ Exception -> 0x0020, all -> 0x001c }
            r8.beginDocument(r4, r1)     // Catch:{ Exception -> 0x001a }
            java.util.ArrayList r5 = r8.loadApnsFromXml(r4, r9)     // Catch:{ Exception -> 0x001a }
            if (r4 == 0) goto L_0x002d
            r4.close()
            goto L_0x002d
        L_0x001a:
            r5 = move-exception
            goto L_0x0022
        L_0x001c:
            r9 = move-exception
            r4 = r3
            goto L_0x00ae
        L_0x0020:
            r5 = move-exception
            r4 = r3
        L_0x0022:
            java.lang.String r6 = "Got exception while loading APN database."
            android.util.Log.e(r2, r6, r5)     // Catch:{ all -> 0x00ad }
            if (r4 == 0) goto L_0x002c
            r4.close()
        L_0x002c:
            r5 = r3
        L_0x002d:
            java.io.File r4 = new java.io.File
            java.io.File r6 = android.os.Environment.getRootDirectory()
            java.lang.String r7 = "etc/apns-conf.xml"
            r4.<init>(r6, r7)
            java.io.FileReader r6 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x0076, Exception -> 0x0056, all -> 0x0053 }
            r6.<init>(r4)     // Catch:{ FileNotFoundException -> 0x0076, Exception -> 0x0056, all -> 0x0053 }
            org.xmlpull.v1.XmlPullParser r7 = android.util.Xml.newPullParser()     // Catch:{ FileNotFoundException -> 0x0077, Exception -> 0x0051 }
            r7.setInput(r6)     // Catch:{ FileNotFoundException -> 0x0077, Exception -> 0x0051 }
            r8.beginDocument(r7, r1)     // Catch:{ FileNotFoundException -> 0x0077, Exception -> 0x0051 }
            java.util.ArrayList r3 = r8.loadApnsFromXml(r7, r9)     // Catch:{ FileNotFoundException -> 0x0077, Exception -> 0x0051 }
        L_0x004b:
            r6.close()     // Catch:{ IOException -> 0x0095 }
            goto L_0x0095
        L_0x004f:
            r9 = move-exception
            goto L_0x00a7
        L_0x0051:
            r9 = move-exception
            goto L_0x0058
        L_0x0053:
            r9 = move-exception
            r6 = r3
            goto L_0x00a7
        L_0x0056:
            r9 = move-exception
            r6 = r3
        L_0x0058:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x004f }
            r1.<init>()     // Catch:{ all -> 0x004f }
            java.lang.String r7 = "Exception while parsing '"
            r1.append(r7)     // Catch:{ all -> 0x004f }
            java.lang.String r4 = r4.getAbsolutePath()     // Catch:{ all -> 0x004f }
            r1.append(r4)     // Catch:{ all -> 0x004f }
            r1.append(r0)     // Catch:{ all -> 0x004f }
            java.lang.String r0 = r1.toString()     // Catch:{ all -> 0x004f }
            android.util.Log.e(r2, r0, r9)     // Catch:{ all -> 0x004f }
            if (r6 == 0) goto L_0x0095
            goto L_0x004b
        L_0x0076:
            r6 = r3
        L_0x0077:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x004f }
            r9.<init>()     // Catch:{ all -> 0x004f }
            java.lang.String r1 = "File not found: '"
            r9.append(r1)     // Catch:{ all -> 0x004f }
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ all -> 0x004f }
            r9.append(r1)     // Catch:{ all -> 0x004f }
            r9.append(r0)     // Catch:{ all -> 0x004f }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x004f }
            android.util.Log.e(r2, r9)     // Catch:{ all -> 0x004f }
            if (r6 == 0) goto L_0x0095
            goto L_0x004b
        L_0x0095:
            r9 = 0
        L_0x0096:
            int r0 = r3.size()
            if (r9 >= r0) goto L_0x00a6
            java.lang.Object r0 = r3.get(r9)
            r5.add(r0)
            int r9 = r9 + 1
            goto L_0x0096
        L_0x00a6:
            return r5
        L_0x00a7:
            if (r6 == 0) goto L_0x00ac
            r6.close()     // Catch:{ IOException -> 0x00ac }
        L_0x00ac:
            throw r9
        L_0x00ad:
            r9 = move-exception
        L_0x00ae:
            if (r4 == 0) goto L_0x00b3
            r4.close()
        L_0x00b3:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.APNManager.loadApns(java.lang.String):java.util.ArrayList");
    }

    private ArrayList<HashMap<String, String>> loadApnsFromXml(XmlPullParser xmlPullParser, String str) {
        HashMap<String, String> apnRowFromXml;
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        if (xmlPullParser == null) {
            return arrayList;
        }
        while (true) {
            try {
                nextElement(xmlPullParser);
                if (xmlPullParser.getEventType() == 1) {
                    break;
                }
                String str2 = xmlPullParser.getAttributeValue((String) null, "mcc") + xmlPullParser.getAttributeValue((String) null, "mnc");
                String attributeValue = xmlPullParser.getAttributeValue((String) null, "type");
                if (TextUtils.equals(str, str2) && !TextUtils.equals(attributeValue, "mms") && (apnRowFromXml = getApnRowFromXml(xmlPullParser)) != null) {
                    arrayList.add(apnRowFromXml);
                }
            } catch (XmlPullParserException e) {
                Log.e(TAG, "Got XmlPullParserException while loading apns.", e);
            }
        }
        return arrayList;
    }

    private void nextElement(XmlPullParser xmlPullParser) {
        c.a.a("com.android.internal.util.XmlUtils").b("nextElement", new Class[]{XmlPullParser.class}, xmlPullParser);
    }

    public boolean currentApnIsModified(String str, String str2) {
        String str3 = str + str2;
        if (TextUtils.isEmpty(str3)) {
            return false;
        }
        HashMap<String, String> currentApnDetail = getCurrentApnDetail();
        ArrayList<HashMap<String, String>> loadApns = loadApns(str3);
        if (currentApnDetail == null || loadApns == null || currentApnDetail.size() == 0) {
            if (currentApnDetail == null) {
                Log.i(TAG, "curApn is null!!");
            }
            if (loadApns == null) {
                Log.i(TAG, "origApns is null!!");
            }
            if (currentApnDetail.size() == 0) {
                Log.i(TAG, "curApn size is zero!!");
            }
            return false;
        }
        Log.i(TAG, "curApn =" + currentApnDetail);
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= loadApns.size()) {
                return true;
            }
            Log.i(TAG, "origApns[" + i + "]=" + loadApns.get(i));
            HashMap hashMap = loadApns.get(i);
            if (currentApnDetail.size() != hashMap.size()) {
                Log.i(TAG, "currentApnIsModified size1=" + currentApnDetail.size() + " size2=" + hashMap.size());
            } else {
                Iterator<String> it = currentApnDetail.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String next = it.next();
                    String str4 = currentApnDetail.get(next);
                    String str5 = (String) hashMap.get(next);
                    if (!TextUtils.equals(str4, str5)) {
                        Log.i(TAG, "currentApnIsModified val=" + str4 + " val2=" + str5);
                        z = false;
                        break;
                    }
                }
                if (z) {
                    return false;
                }
            }
            i++;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0080 A[DONT_GENERATE] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getCheckApnResult() {
        /*
            r10 = this;
            int r0 = com.miui.networkassistant.utils.TelephonyUtil.getDefaultDataSubscriptionId()
            java.lang.String r1 = "NetworkDiagnostics_APNManager"
            java.lang.String r2 = "isValidApn enter"
            android.util.Log.i(r1, r2)
            boolean r2 = com.miui.networkassistant.utils.DeviceUtil.IS_DUAL_CARD
            if (r2 == 0) goto L_0x0021
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "content://telephony/carriers/preferapn/subId/"
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0023
        L_0x0021:
            java.lang.String r0 = "content://telephony/carriers/preferapn"
        L_0x0023:
            android.net.Uri r0 = android.net.Uri.parse(r0)
            r3 = r0
            java.lang.String r0 = "none"
            r8 = 0
            r9 = -1
            android.content.Context r2 = r10.mContext     // Catch:{ Exception -> 0x0074 }
            android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ Exception -> 0x0074 }
            r4 = 0
            r5 = 0
            r6 = 0
            java.lang.String r7 = "name ASC"
            android.database.Cursor r8 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0074 }
            if (r8 == 0) goto L_0x006a
            int r2 = r8.getCount()     // Catch:{ Exception -> 0x0074 }
            if (r2 <= 0) goto L_0x006a
            boolean r2 = r8.moveToFirst()     // Catch:{ Exception -> 0x0074 }
            if (r2 == 0) goto L_0x006a
            java.lang.String r2 = "_id"
            int r2 = r8.getColumnIndexOrThrow(r2)     // Catch:{ Exception -> 0x0074 }
            int r2 = r8.getInt(r2)     // Catch:{ Exception -> 0x0074 }
            java.lang.String r3 = "apn"
            int r3 = r8.getColumnIndexOrThrow(r3)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r0 = r8.getString(r3)     // Catch:{ Exception -> 0x0068 }
            java.lang.String r3 = "edited"
            int r3 = r8.getColumnIndexOrThrow(r3)     // Catch:{ Exception -> 0x0068 }
            int r3 = r8.getInt(r3)     // Catch:{ Exception -> 0x0068 }
            goto L_0x006c
        L_0x0068:
            r3 = move-exception
            goto L_0x0076
        L_0x006a:
            r2 = r9
            r3 = r2
        L_0x006c:
            if (r8 == 0) goto L_0x0084
            r8.close()
            goto L_0x0084
        L_0x0072:
            r0 = move-exception
            goto L_0x00c2
        L_0x0074:
            r3 = move-exception
            r2 = r9
        L_0x0076:
            r3.printStackTrace()     // Catch:{ all -> 0x0072 }
            java.lang.String r3 = "isValidApn -exception"
            android.util.Log.e(r1, r3)     // Catch:{ all -> 0x0072 }
            if (r8 == 0) goto L_0x0083
            r8.close()
        L_0x0083:
            r3 = r9
        L_0x0084:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "isValidApn idField "
            r4.append(r5)
            r4.append(r2)
            java.lang.String r2 = ",apnField="
            r4.append(r2)
            r4.append(r0)
            java.lang.String r2 = ",editStatusField="
            r4.append(r2)
            r4.append(r3)
            java.lang.String r2 = r4.toString()
            android.util.Log.i(r1, r2)
            r2 = 1
            if (r3 != r2) goto L_0x00b1
            java.lang.String r0 = "isValidApn apn has been edited!!!"
            android.util.Log.i(r1, r0)
            goto L_0x00c1
        L_0x00b1:
            java.lang.String r2 = "net"
            int r0 = r0.indexOf(r2)
            if (r0 != r9) goto L_0x00c0
            java.lang.String r0 = "isValidApn apn is not net apn !!!"
            android.util.Log.i(r1, r0)
            r2 = 2
            goto L_0x00c1
        L_0x00c0:
            r2 = 0
        L_0x00c1:
            return r2
        L_0x00c2:
            if (r8 == 0) goto L_0x00c7
            r8.close()
        L_0x00c7:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.APNManager.getCheckApnResult():int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0048, code lost:
        if (r7 != null) goto L_0x004a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x004a, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0054, code lost:
        if (r7 == null) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0057, code lost:
        android.util.Log.i(TAG, "getCurrentApn: apn= " + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x006d, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getCurrentApn() {
        /*
            r8 = this;
            int r0 = com.miui.networkassistant.utils.TelephonyUtil.getDefaultDataSubscriptionId()
            boolean r1 = com.miui.networkassistant.utils.DeviceUtil.IS_DUAL_CARD
            if (r1 == 0) goto L_0x001a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "content://telephony/carriers/preferapn/subId/"
            r1.append(r2)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x001c
        L_0x001a:
            java.lang.String r0 = "content://telephony/carriers/preferapn"
        L_0x001c:
            android.net.Uri r0 = android.net.Uri.parse(r0)
            r2 = r0
            java.lang.String r0 = "none"
            r7 = 0
            android.content.Context r1 = r8.mContext     // Catch:{ Exception -> 0x0050 }
            android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ Exception -> 0x0050 }
            r3 = 0
            r4 = 0
            r5 = 0
            java.lang.String r6 = "name ASC"
            android.database.Cursor r7 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0050 }
            if (r7 == 0) goto L_0x0048
            int r1 = r7.getCount()     // Catch:{ Exception -> 0x0050 }
            if (r1 <= 0) goto L_0x0048
            r7.moveToFirst()     // Catch:{ Exception -> 0x0050 }
            java.lang.String r1 = "apn"
            int r1 = r7.getColumnIndex(r1)     // Catch:{ Exception -> 0x0050 }
            java.lang.String r0 = r7.getString(r1)     // Catch:{ Exception -> 0x0050 }
        L_0x0048:
            if (r7 == 0) goto L_0x0057
        L_0x004a:
            r7.close()
            goto L_0x0057
        L_0x004e:
            r0 = move-exception
            goto L_0x006e
        L_0x0050:
            r1 = move-exception
            r1.printStackTrace()     // Catch:{ all -> 0x004e }
            if (r7 == 0) goto L_0x0057
            goto L_0x004a
        L_0x0057:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "getCurrentApn: apn= "
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "NetworkDiagnostics_APNManager"
            android.util.Log.i(r2, r1)
            return r0
        L_0x006e:
            if (r7 == 0) goto L_0x0073
            r7.close()
        L_0x0073:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.APNManager.getCurrentApn():java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0034, code lost:
        if (r2 != null) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0036, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0040, code lost:
        if (r2 == null) goto L_0x0043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0043, code lost:
        return r9;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getPreferApnId() {
        /*
            r10 = this;
            java.lang.String r0 = "_id"
            java.lang.String r1 = "1"
            android.content.Context r2 = r10.mContext
            android.content.ContentResolver r3 = r2.getContentResolver()
            r2 = 0
            r9 = -1
            android.net.Uri r4 = r10.mCreateUri     // Catch:{ Exception -> 0x003c }
            java.lang.String[] r5 = new java.lang.String[]{r0}     // Catch:{ Exception -> 0x003c }
            java.lang.String r6 = "current=?"
            java.lang.String[] r7 = new java.lang.String[]{r1}     // Catch:{ Exception -> 0x003c }
            r8 = 0
            android.database.Cursor r2 = r3.query(r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x003c }
            if (r2 == 0) goto L_0x0034
            int r1 = r2.getCount()     // Catch:{ Exception -> 0x003c }
            if (r1 <= 0) goto L_0x0034
            boolean r1 = r2.moveToFirst()     // Catch:{ Exception -> 0x003c }
            if (r1 == 0) goto L_0x0034
            int r0 = r2.getColumnIndex(r0)     // Catch:{ Exception -> 0x003c }
            int r0 = r2.getInt(r0)     // Catch:{ Exception -> 0x003c }
            r9 = r0
        L_0x0034:
            if (r2 == 0) goto L_0x0043
        L_0x0036:
            r2.close()
            goto L_0x0043
        L_0x003a:
            r0 = move-exception
            goto L_0x0044
        L_0x003c:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x003a }
            if (r2 == 0) goto L_0x0043
            goto L_0x0036
        L_0x0043:
            return r9
        L_0x0044:
            if (r2 == 0) goto L_0x0049
            r2.close()
        L_0x0049:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.APNManager.getPreferApnId():int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0032, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003c, code lost:
        if (r7 == null) goto L_0x003f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x003f, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0030, code lost:
        if (r7 != null) goto L_0x0032;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isExistAPNForCurrentSim(java.lang.String r9, java.lang.String r10) {
        /*
            r8 = this;
            android.content.Context r0 = r8.mContext
            android.content.ContentResolver r1 = r0.getContentResolver()
            r0 = 0
            r7 = 0
            android.net.Uri r2 = r8.mCreateUri     // Catch:{ Exception -> 0x0038 }
            java.lang.String r3 = "apn"
            java.lang.String r4 = "name"
            java.lang.String[] r3 = new java.lang.String[]{r3, r4}     // Catch:{ Exception -> 0x0038 }
            java.lang.String r4 = "mcc=? and mnc=?"
            r5 = 2
            java.lang.String[] r5 = new java.lang.String[r5]     // Catch:{ Exception -> 0x0038 }
            r5[r0] = r9     // Catch:{ Exception -> 0x0038 }
            r9 = 1
            r5[r9] = r10     // Catch:{ Exception -> 0x0038 }
            r6 = 0
            android.database.Cursor r7 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x0038 }
            if (r7 == 0) goto L_0x0030
            int r10 = r7.getCount()     // Catch:{ Exception -> 0x0038 }
            if (r10 <= 0) goto L_0x0030
            boolean r10 = r7.moveToFirst()     // Catch:{ Exception -> 0x0038 }
            if (r10 == 0) goto L_0x0030
            r0 = r9
        L_0x0030:
            if (r7 == 0) goto L_0x003f
        L_0x0032:
            r7.close()
            goto L_0x003f
        L_0x0036:
            r9 = move-exception
            goto L_0x0040
        L_0x0038:
            r9 = move-exception
            r9.printStackTrace()     // Catch:{ all -> 0x0036 }
            if (r7 == 0) goto L_0x003f
            goto L_0x0032
        L_0x003f:
            return r0
        L_0x0040:
            if (r7 == 0) goto L_0x0045
            r7.close()
        L_0x0045:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.netdiagnose.APNManager.isExistAPNForCurrentSim(java.lang.String, java.lang.String):boolean");
    }

    public void restoreAPN() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.delete(this.mRestoreapnUri, (String) null, (String[]) null);
        contentResolver.delete(this.mPreferapnUri, (String) null, (String[]) null);
    }
}
