package com.miui.permcenter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.miui.securitycenter.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class f extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    private static Map<String, Integer> f6098a = new HashMap();

    /* renamed from: b  reason: collision with root package name */
    private static Map<String, Integer> f6099b = new HashMap();

    /* renamed from: c  reason: collision with root package name */
    private static Map<String, Integer> f6100c = new HashMap();

    /* renamed from: d  reason: collision with root package name */
    private static Map<String, Integer> f6101d = new HashMap();
    private static Map<String, Integer> e = new HashMap();
    private Context f;
    private String[] g;
    private String[] h;
    private String[] i;
    private String[] j;
    private Set<String> k;
    private LayoutInflater l;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        public TextView f6102a;

        /* renamed from: b  reason: collision with root package name */
        public View f6103b;
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public ImageView f6104a;

        /* renamed from: b  reason: collision with root package name */
        public TextView f6105b;

        /* renamed from: c  reason: collision with root package name */
        public TextView f6106c;
    }

    static {
        Map<String, Integer> map = f6098a;
        Integer valueOf = Integer.valueOf(R.drawable.perm_phoneinfo_icon);
        map.put("android.permission.READ_PHONE_STATE", valueOf);
        f6098a.put("android.permission.READ_PRIVILEGED_PHONE_STATE", valueOf);
        Map<String, Integer> map2 = f6098a;
        Integer valueOf2 = Integer.valueOf(R.drawable.perm_phone_icon);
        map2.put("android.permission.CALL_PHONE", valueOf2);
        Map<String, Integer> map3 = f6098a;
        Integer valueOf3 = Integer.valueOf(R.drawable.perm_calllog_icon);
        map3.put("android.permission.READ_CALL_LOG", valueOf3);
        f6098a.put("android.permission.WRITE_CALL_LOG", valueOf3);
        f6098a.put("com.android.voicemail.permission.ADD_VOICEMAIL", Integer.valueOf(R.drawable.perm_mail_icon));
        f6098a.put("android.permission.USE_SIP", valueOf2);
        f6098a.put("android.permission.PROCESS_OUTGOING_CALLS", valueOf2);
        Map<String, Integer> map4 = f6098a;
        Integer valueOf4 = Integer.valueOf(R.drawable.perm_contacts_icon);
        map4.put("android.permission.READ_CONTACTS", valueOf4);
        f6098a.put("android.permission.WRITE_CONTACTS", valueOf4);
        f6098a.put("android.permission.GET_ACCOUNTS", Integer.valueOf(R.drawable.perm_account_icon));
        Map<String, Integer> map5 = f6098a;
        Integer valueOf5 = Integer.valueOf(R.drawable.perm_sms_icon);
        map5.put("android.permission.READ_SMS", valueOf5);
        f6098a.put("android.permission.SEND_SMS", valueOf5);
        f6098a.put("android.permission.SEND_MMS", valueOf5);
        f6098a.put("android.permission.READ_MMS", valueOf5);
        f6098a.put("android.permission.READ_PHONE_NUMBERS", valueOf);
        f6098a.put("android.permission.ANSWER_PHONE_CALLS", valueOf2);
        f6098a.put("android.permission.MODIFY_PHONE_STATE", valueOf2);
        Integer num = valueOf3;
        f6098a.put("android.permission.BLUETOOTH_ADMIN", Integer.valueOf(R.drawable.perm_bluetooth_icon));
        f6098a.put("android.permission.BLUETOOTH_PRIVILEGED", Integer.valueOf(R.drawable.perm_bluetooth_icon));
        f6098a.put("android.permission.CHANGE_WIFI_STATE", Integer.valueOf(R.drawable.perm_wifi_icon));
        f6098a.put("android.permission.NFC", Integer.valueOf(R.drawable.perm_nfc_icon));
        f6099b.put("android.permission.READ_PHONE_STATE", Integer.valueOf(R.string.HIPS_Perm_PhoneID_Desc));
        f6099b.put("android.permission.READ_PHONE_NUMBERS", Integer.valueOf(R.string.HIPS_Perm_PhoneID_Desc));
        if (Build.VERSION.SDK_INT >= 29) {
            f6099b.put("android.permission.READ_PHONE_STATE", Integer.valueOf(R.string.HIPS_Perm_PhoneID_Desc_Q));
            f6099b.put("android.permission.READ_PHONE_NUMBERS", Integer.valueOf(R.string.HIPS_Perm_PhoneID_Desc_Q));
        }
        f6099b.put("android.permission.ANSWER_PHONE_CALLS", Integer.valueOf(R.string.permdesc_in_calls));
        f6099b.put("android.permission.CALL_PHONE", Integer.valueOf(R.string.HIPS_Perm_Call_Desc));
        f6099b.put("android.permission.READ_CALL_LOG", Integer.valueOf(R.string.HIPS_Perm_Read_Call_Log_Desc));
        f6099b.put("android.permission.WRITE_CALL_LOG", Integer.valueOf(R.string.HIPS_Perm_CallLog_Desc));
        f6099b.put("com.android.voicemail.permission.ADD_VOICEMAIL", Integer.valueOf(R.string.HIPS_Perm_add_voicemail_Desc));
        f6099b.put("android.permission.USE_SIP", Integer.valueOf(R.string.HIPS_Perm_use_sip_Desc));
        f6099b.put("android.permission.PROCESS_OUTGOING_CALLS", Integer.valueOf(R.string.permdesc_out_calls));
        f6099b.put("android.permission.READ_CONTACTS", Integer.valueOf(R.string.HIPS_Perm_Read_Contact_Desc));
        f6099b.put("android.permission.WRITE_CONTACTS", Integer.valueOf(R.string.HIPS_Perm_Contact_Desc));
        f6099b.put("android.permission.GET_ACCOUNTS", Integer.valueOf(R.string.HIPS_Perm_get_accounts_Desc));
        f6099b.put("android.permission.READ_SMS", Integer.valueOf(R.string.HIPS_Perm_SendSMS_Desc));
        f6099b.put("android.permission.SEND_SMS", Integer.valueOf(R.string.HIPS_Perm_Read_SMS_Desc));
        f6099b.put("android.permission.BLUETOOTH_ADMIN", Integer.valueOf(R.string.HIPS_Perm_Bluetooth_Connectivity_Desc));
        f6099b.put("android.permission.BLUETOOTH_PRIVILEGED", Integer.valueOf(R.string.HIPS_Perm_Bluetooth_Connectivity_Desc));
        f6099b.put("android.permission.CHANGE_WIFI_STATE", Integer.valueOf(R.string.HIPS_Perm_WIFI_Connectivity_Desc));
        f6099b.put("android.permission.NFC", Integer.valueOf(R.string.HIPS_Perm_process_nfc_Desc));
        f6101d.put("android.permission-group.CALENDAR", Integer.valueOf(R.drawable.perm_calendar_icon));
        f6101d.put("android.permission-group.CAMERA", Integer.valueOf(R.drawable.perm_camera_icon));
        f6101d.put("android.permission-group.CONTACTS", valueOf4);
        f6101d.put("android.permission-group.LOCATION", Integer.valueOf(R.drawable.perm_location_icon));
        f6101d.put("android.permission-group.MICROPHONE", Integer.valueOf(R.drawable.perm_audio_icon));
        f6101d.put("android.permission-group.PHONE", valueOf2);
        f6101d.put("android.permission-group.SMS", valueOf5);
        f6101d.put("android.permission-group.STORAGE", Integer.valueOf(R.drawable.perm_storage_icon));
        f6101d.put("android.permission-group.CALL_LOG", num);
        f6100c.put("android.permission-group.CALENDAR", Integer.valueOf(R.string.permgroupdesc_calendar));
        f6100c.put("android.permission-group.CAMERA", Integer.valueOf(R.string.permgroupdesc_camera));
        f6100c.put("android.permission-group.CONTACTS", Integer.valueOf(R.string.permgroupdesc_contacts));
        f6100c.put("android.permission-group.LOCATION", Integer.valueOf(R.string.permgroupdesc_location));
        f6100c.put("android.permission-group.MICROPHONE", Integer.valueOf(R.string.permgroupdesc_microphone));
        f6100c.put("android.permission-group.PHONE", Integer.valueOf(R.string.permgroupdesc_phone));
        f6100c.put("android.permission-group.SENSORS", Integer.valueOf(R.string.permgroupdesc_sensors));
        f6100c.put("android.permission-group.SMS", Integer.valueOf(R.string.permgroupdesc_sms));
        f6100c.put("android.permission-group.STORAGE", Integer.valueOf(R.string.permgroupdesc_storage));
        f6100c.put("android.permission-group.CALL_LOG", Integer.valueOf(R.string.permgroupdesc_calllog));
        f6100c.put("android.permission-group.ACTIVITY_RECOGNITION", Integer.valueOf(R.string.permgroupdesc_recognition));
        e.put("android.permission.SEND_MMS", Integer.valueOf(R.string.system_permission_perm_send_mms));
        e.put("android.permission.READ_MMS", Integer.valueOf(R.string.system_permission_perm_read_mms));
        e.put("android.permission.READ_PHONE_NUMBERS", Integer.valueOf(R.string.system_permission_perm_read_numbers));
        e.put("android.permission.ANSWER_PHONE_CALLS", Integer.valueOf(R.string.system_permission_perm_answer_call));
        e.put("android.permission.MODIFY_PHONE_STATE", Integer.valueOf(R.string.system_permission_perm_answer_call));
    }

    public f(Context context, String[] strArr, String[] strArr2, String[] strArr3) {
        this(context, strArr, strArr2, strArr3, strArr3);
    }

    public f(Context context, String[] strArr, String[] strArr2, String[] strArr3, String[] strArr4) {
        this.f = context;
        this.g = strArr;
        this.h = strArr2;
        this.i = strArr3;
        this.j = strArr4;
        this.k = new HashSet();
        Set<String> set = this.k;
        if (set != null) {
            Collections.addAll(set, strArr3);
        }
        this.l = LayoutInflater.from(context);
    }

    private String a(int i2) {
        String[] strArr = this.j;
        if (strArr == null || strArr.length == 0) {
            return this.h[i2 - 1];
        }
        String[] strArr2 = this.h;
        if (strArr2 == null || strArr2.length == 0) {
            return this.j[i2 - 1];
        }
        if (i2 > 0 && i2 < strArr2.length + 1) {
            return strArr2[i2 - 1];
        }
        String[] strArr3 = this.h;
        if (i2 > strArr3.length + 2) {
            return this.j[(i2 - strArr3.length) - 3];
        }
        return null;
    }

    public int getCount() {
        int length;
        String[] strArr = this.i;
        if (strArr == null || strArr.length == 0) {
            length = this.g.length;
        } else {
            String[] strArr2 = this.g;
            if (strArr2 != null && strArr2.length != 0) {
                return strArr2.length + strArr.length + 3;
            }
            length = this.i.length;
        }
        return length + 1;
    }

    public Object getItem(int i2) {
        String[] strArr = this.i;
        if (strArr == null || strArr.length == 0) {
            return this.g[i2 - 1];
        }
        String[] strArr2 = this.g;
        if (strArr2 == null || strArr2.length == 0) {
            return this.i[i2 - 1];
        }
        if (i2 > 0 && i2 < strArr2.length + 1) {
            return strArr2[i2 - 1];
        }
        String[] strArr3 = this.g;
        if (i2 > strArr3.length + 2) {
            return this.i[(i2 - strArr3.length) - 3];
        }
        return null;
    }

    public long getItemId(int i2) {
        return (long) i2;
    }

    public int getItemViewType(int i2) {
        String[] strArr;
        if (i2 == 0 || ((strArr = this.g) != null && i2 == strArr.length + 2)) {
            return 1;
        }
        String[] strArr2 = this.g;
        return (strArr2 == null || i2 != strArr2.length + 1) ? 0 : 2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x008f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0201 A[ADDED_TO_REGION, Catch:{ Exception -> 0x01fc }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r12, android.view.View r13, android.view.ViewGroup r14) {
        /*
            r11 = this;
            int r0 = r11.getItemViewType(r12)
            r1 = 2
            r2 = 0
            r3 = 1
            r4 = 0
            if (r13 != 0) goto L_0x006f
            if (r0 != 0) goto L_0x0041
            com.miui.permcenter.f$b r13 = new com.miui.permcenter.f$b
            r13.<init>()
            android.view.LayoutInflater r5 = r11.l
            r6 = 2131493030(0x7f0c00a6, float:1.8609529E38)
            android.view.View r14 = r5.inflate(r6, r14, r4)
            r5 = 2131296975(0x7f0902cf, float:1.8211882E38)
            android.view.View r5 = r14.findViewById(r5)
            android.widget.ImageView r5 = (android.widget.ImageView) r5
            r13.f6104a = r5
            r5 = 2131297844(0x7f090634, float:1.8213644E38)
            android.view.View r5 = r14.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r13.f6105b = r5
            r5 = 2131297740(0x7f0905cc, float:1.8213433E38)
            android.view.View r5 = r14.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r13.f6106c = r5
            r14.setTag(r13)
            r10 = r2
            r2 = r13
            goto L_0x008a
        L_0x0041:
            if (r0 == r3) goto L_0x0049
            if (r0 != r1) goto L_0x0046
            goto L_0x0049
        L_0x0046:
            r14 = r13
            r13 = r2
            goto L_0x008b
        L_0x0049:
            com.miui.permcenter.f$a r13 = new com.miui.permcenter.f$a
            r13.<init>()
            android.view.LayoutInflater r5 = r11.l
            r6 = 2131493031(0x7f0c00a7, float:1.860953E38)
            android.view.View r14 = r5.inflate(r6, r14, r4)
            r5 = 2131297094(0x7f090346, float:1.8212123E38)
            android.view.View r5 = r14.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            r13.f6102a = r5
            r5 = 2131297087(0x7f09033f, float:1.821211E38)
            android.view.View r5 = r14.findViewById(r5)
            r13.f6103b = r5
            r14.setTag(r13)
            goto L_0x008b
        L_0x006f:
            java.lang.Object r14 = r13.getTag()
            boolean r14 = r14 instanceof com.miui.permcenter.f.b
            if (r14 == 0) goto L_0x0082
            java.lang.Object r14 = r13.getTag()
            com.miui.permcenter.f$b r14 = (com.miui.permcenter.f.b) r14
            r10 = r14
            r14 = r13
            r13 = r2
            r2 = r10
            goto L_0x008b
        L_0x0082:
            java.lang.Object r14 = r13.getTag()
            com.miui.permcenter.f$a r14 = (com.miui.permcenter.f.a) r14
            r10 = r14
            r14 = r13
        L_0x008a:
            r13 = r10
        L_0x008b:
            r5 = 8
            if (r0 != 0) goto L_0x01ff
            if (r2 == 0) goto L_0x01ff
            java.lang.Object r13 = r11.getItem(r12)     // Catch:{ Exception -> 0x01fc }
            java.lang.String r13 = (java.lang.String) r13     // Catch:{ Exception -> 0x01fc }
            boolean r0 = android.text.TextUtils.isEmpty(r13)     // Catch:{ Exception -> 0x01fc }
            if (r0 == 0) goto L_0x009e
            return r14
        L_0x009e:
            java.lang.String r0 = r11.a(r12)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r1 = r2.f6105b     // Catch:{ Exception -> 0x01fc }
            r1.setText(r13)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r1 = r2.f6106c     // Catch:{ Exception -> 0x01fc }
            r1.setText(r0)     // Catch:{ Exception -> 0x01fc }
            android.widget.ImageView r1 = r2.f6104a     // Catch:{ Exception -> 0x01fc }
            r6 = 2131232107(0x7f08056b, float:1.8080314E38)
            r1.setImageResource(r6)     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r1 = e     // Catch:{ Exception -> 0x01fc }
            boolean r1 = r1.containsKey(r13)     // Catch:{ Exception -> 0x01fc }
            if (r1 == 0) goto L_0x00e5
            android.widget.ImageView r1 = r2.f6104a     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r6 = f6098a     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r6 = r6.get(r13)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ Exception -> 0x01fc }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x01fc }
            r1.setImageResource(r6)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r1 = r2.f6105b     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r6 = e     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r6 = r6.get(r13)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ Exception -> 0x01fc }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x01fc }
            r1.setText(r6)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r1 = r2.f6106c     // Catch:{ Exception -> 0x01fc }
            r1.setText(r0)     // Catch:{ Exception -> 0x01fc }
            goto L_0x01b3
        L_0x00e5:
            java.util.Map<java.lang.String, java.lang.Integer> r1 = f6101d     // Catch:{ Exception -> 0x01fc }
            boolean r1 = r1.containsKey(r13)     // Catch:{ Exception -> 0x01fc }
            if (r1 == 0) goto L_0x012a
            android.content.Context r1 = r11.f     // Catch:{ Exception -> 0x01fc }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ Exception -> 0x01fc }
            android.content.pm.PermissionGroupInfo r1 = r1.getPermissionGroupInfo(r13, r4)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r6 = r2.f6105b     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r7 = f6100c     // Catch:{ Exception -> 0x01fc }
            java.lang.String r8 = r1.name     // Catch:{ Exception -> 0x01fc }
            boolean r7 = r7.containsKey(r8)     // Catch:{ Exception -> 0x01fc }
            if (r7 == 0) goto L_0x0112
            java.util.Map<java.lang.String, java.lang.Integer> r7 = f6100c     // Catch:{ Exception -> 0x01fc }
            java.lang.String r1 = r1.name     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r1 = r7.get(r1)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ Exception -> 0x01fc }
            int r1 = r1.intValue()     // Catch:{ Exception -> 0x01fc }
            goto L_0x0114
        L_0x0112:
            int r1 = r1.descriptionRes     // Catch:{ Exception -> 0x01fc }
        L_0x0114:
            r6.setText(r1)     // Catch:{ Exception -> 0x01fc }
            android.widget.ImageView r1 = r2.f6104a     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r6 = f6101d     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r6 = r6.get(r13)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ Exception -> 0x01fc }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x01fc }
            r1.setImageResource(r6)     // Catch:{ Exception -> 0x01fc }
            goto L_0x01b3
        L_0x012a:
            android.content.Context r1 = r11.f     // Catch:{ Exception -> 0x01fc }
            android.content.pm.PackageManager r1 = r1.getPackageManager()     // Catch:{ Exception -> 0x01fc }
            android.content.pm.PermissionInfo r1 = r1.getPermissionInfo(r13, r4)     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r6 = f6099b     // Catch:{ Exception -> 0x01fc }
            boolean r6 = r6.containsKey(r13)     // Catch:{ Exception -> 0x01fc }
            if (r6 == 0) goto L_0x014e
            android.widget.TextView r6 = r2.f6105b     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r7 = f6099b     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r7 = r7.get(r13)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r7 = (java.lang.Integer) r7     // Catch:{ Exception -> 0x01fc }
            int r7 = r7.intValue()     // Catch:{ Exception -> 0x01fc }
            r6.setText(r7)     // Catch:{ Exception -> 0x01fc }
            goto L_0x017a
        L_0x014e:
            android.content.Context r6 = r11.f     // Catch:{ Exception -> 0x01fc }
            android.content.pm.PackageManager r6 = r6.getPackageManager()     // Catch:{ Exception -> 0x01fc }
            java.lang.String r7 = r1.group     // Catch:{ Exception -> 0x01fc }
            android.content.pm.PermissionGroupInfo r6 = r6.getPermissionGroupInfo(r7, r4)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r7 = r2.f6105b     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r8 = f6100c     // Catch:{ Exception -> 0x01fc }
            java.lang.String r9 = r6.name     // Catch:{ Exception -> 0x01fc }
            boolean r8 = r8.containsKey(r9)     // Catch:{ Exception -> 0x01fc }
            if (r8 == 0) goto L_0x0175
            java.util.Map<java.lang.String, java.lang.Integer> r8 = f6100c     // Catch:{ Exception -> 0x01fc }
            java.lang.String r6 = r6.name     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r6 = r8.get(r6)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ Exception -> 0x01fc }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x01fc }
            goto L_0x0177
        L_0x0175:
            int r6 = r6.descriptionRes     // Catch:{ Exception -> 0x01fc }
        L_0x0177:
            r7.setText(r6)     // Catch:{ Exception -> 0x01fc }
        L_0x017a:
            java.util.Map<java.lang.String, java.lang.Integer> r6 = f6098a     // Catch:{ Exception -> 0x01fc }
            java.lang.String r7 = r1.name     // Catch:{ Exception -> 0x01fc }
            boolean r6 = r6.containsKey(r7)     // Catch:{ Exception -> 0x01fc }
            if (r6 == 0) goto L_0x0198
            android.widget.ImageView r6 = r2.f6104a     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r7 = f6098a     // Catch:{ Exception -> 0x01fc }
            java.lang.String r1 = r1.name     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r1 = r7.get(r1)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ Exception -> 0x01fc }
            int r1 = r1.intValue()     // Catch:{ Exception -> 0x01fc }
        L_0x0194:
            r6.setImageResource(r1)     // Catch:{ Exception -> 0x01fc }
            goto L_0x01b3
        L_0x0198:
            java.util.Map<java.lang.String, java.lang.Integer> r6 = f6101d     // Catch:{ Exception -> 0x01fc }
            java.lang.String r7 = r1.group     // Catch:{ Exception -> 0x01fc }
            boolean r6 = r6.containsKey(r7)     // Catch:{ Exception -> 0x01fc }
            if (r6 == 0) goto L_0x01b3
            android.widget.ImageView r6 = r2.f6104a     // Catch:{ Exception -> 0x01fc }
            java.util.Map<java.lang.String, java.lang.Integer> r7 = f6101d     // Catch:{ Exception -> 0x01fc }
            java.lang.String r1 = r1.group     // Catch:{ Exception -> 0x01fc }
            java.lang.Object r1 = r7.get(r1)     // Catch:{ Exception -> 0x01fc }
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch:{ Exception -> 0x01fc }
            int r1 = r1.intValue()     // Catch:{ Exception -> 0x01fc }
            goto L_0x0194
        L_0x01b3:
            if (r12 == r3) goto L_0x01d5
            java.lang.String[] r1 = r11.g     // Catch:{ Exception -> 0x01fc }
            if (r1 == 0) goto L_0x01c0
            java.lang.String[] r1 = r11.g     // Catch:{ Exception -> 0x01fc }
            int r1 = r1.length     // Catch:{ Exception -> 0x01fc }
            int r1 = r1 + 3
            if (r12 == r1) goto L_0x01d5
        L_0x01c0:
            android.content.Context r12 = r11.f     // Catch:{ Exception -> 0x01fc }
            android.content.res.Resources r12 = r12.getResources()     // Catch:{ Exception -> 0x01fc }
            r1 = 2131167366(0x7f070886, float:1.7949004E38)
            int r12 = r12.getDimensionPixelSize(r1)     // Catch:{ Exception -> 0x01fc }
            int r1 = r14.getPaddingBottom()     // Catch:{ Exception -> 0x01fc }
        L_0x01d1:
            r14.setPadding(r4, r12, r4, r1)     // Catch:{ Exception -> 0x01fc }
            goto L_0x01e7
        L_0x01d5:
            android.content.Context r12 = r11.f     // Catch:{ Exception -> 0x01fc }
            android.content.res.Resources r12 = r12.getResources()     // Catch:{ Exception -> 0x01fc }
            r1 = 2131167344(0x7f070870, float:1.7948959E38)
            int r12 = r12.getDimensionPixelSize(r1)     // Catch:{ Exception -> 0x01fc }
            int r1 = r14.getPaddingBottom()     // Catch:{ Exception -> 0x01fc }
            goto L_0x01d1
        L_0x01e7:
            java.util.Set<java.lang.String> r12 = r11.k     // Catch:{ Exception -> 0x01fc }
            boolean r12 = r12.contains(r13)     // Catch:{ Exception -> 0x01fc }
            if (r12 == 0) goto L_0x0289
            boolean r12 = r13.equals(r0)     // Catch:{ Exception -> 0x01fc }
            if (r12 == 0) goto L_0x0289
            android.widget.TextView r12 = r2.f6106c     // Catch:{ Exception -> 0x01fc }
            r12.setVisibility(r5)     // Catch:{ Exception -> 0x01fc }
            goto L_0x0289
        L_0x01fc:
            r12 = move-exception
            goto L_0x0282
        L_0x01ff:
            if (r13 == 0) goto L_0x0273
            if (r0 != r3) goto L_0x0273
            android.widget.TextView r0 = r13.f6102a     // Catch:{ Exception -> 0x01fc }
            r0.setVisibility(r4)     // Catch:{ Exception -> 0x01fc }
            android.view.View r0 = r13.f6103b     // Catch:{ Exception -> 0x01fc }
            r0.setVisibility(r5)     // Catch:{ Exception -> 0x01fc }
            java.lang.String[] r0 = r11.g     // Catch:{ Exception -> 0x01fc }
            java.lang.String r1 = ")"
            java.lang.String r2 = "("
            if (r0 == 0) goto L_0x0249
            java.lang.String[] r0 = r11.g     // Catch:{ Exception -> 0x01fc }
            int r0 = r0.length     // Catch:{ Exception -> 0x01fc }
            if (r0 == 0) goto L_0x0249
            if (r12 != 0) goto L_0x0249
            android.widget.TextView r12 = r13.f6102a     // Catch:{ Exception -> 0x01fc }
            r0 = 2131757713(0x7f100a91, float:1.914637E38)
            r12.setText(r0)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r12 = r13.f6102a     // Catch:{ Exception -> 0x01fc }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01fc }
            r13.<init>()     // Catch:{ Exception -> 0x01fc }
            r13.append(r2)     // Catch:{ Exception -> 0x01fc }
            android.content.Context r0 = r11.f     // Catch:{ Exception -> 0x01fc }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x01fc }
            r2 = 2131757714(0x7f100a92, float:1.9146372E38)
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x01fc }
            r13.append(r0)     // Catch:{ Exception -> 0x01fc }
            r13.append(r1)     // Catch:{ Exception -> 0x01fc }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x01fc }
        L_0x0245:
            r12.append(r13)     // Catch:{ Exception -> 0x01fc }
            goto L_0x0289
        L_0x0249:
            android.widget.TextView r12 = r13.f6102a     // Catch:{ Exception -> 0x01fc }
            r0 = 2131757090(0x7f100822, float:1.9145106E38)
            r12.setText(r0)     // Catch:{ Exception -> 0x01fc }
            android.widget.TextView r12 = r13.f6102a     // Catch:{ Exception -> 0x01fc }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01fc }
            r13.<init>()     // Catch:{ Exception -> 0x01fc }
            r13.append(r2)     // Catch:{ Exception -> 0x01fc }
            android.content.Context r0 = r11.f     // Catch:{ Exception -> 0x01fc }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x01fc }
            r2 = 2131757091(0x7f100823, float:1.9145108E38)
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x01fc }
            r13.append(r0)     // Catch:{ Exception -> 0x01fc }
            r13.append(r1)     // Catch:{ Exception -> 0x01fc }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x01fc }
            goto L_0x0245
        L_0x0273:
            if (r13 == 0) goto L_0x0289
            if (r0 != r1) goto L_0x0289
            android.widget.TextView r12 = r13.f6102a     // Catch:{ Exception -> 0x01fc }
            r12.setVisibility(r5)     // Catch:{ Exception -> 0x01fc }
            android.view.View r12 = r13.f6103b     // Catch:{ Exception -> 0x01fc }
            r12.setVisibility(r4)     // Catch:{ Exception -> 0x01fc }
            goto L_0x0289
        L_0x0282:
            java.lang.String r13 = "PermDescAdapter"
            java.lang.String r0 = "permission not found exception!"
            android.util.Log.e(r13, r0, r12)
        L_0x0289:
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.f.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public int getViewTypeCount() {
        return 3;
    }
}
