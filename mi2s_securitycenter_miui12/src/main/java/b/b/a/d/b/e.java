package b.b.a.d.b;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import b.b.c.c.b.l;
import b.b.c.j.i;
import com.miui.securitycenter.R;
import java.util.List;
import miui.app.ProgressDialog;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

public abstract class e extends l implements Preference.b, Preference.c {

    /* renamed from: a  reason: collision with root package name */
    protected CheckBoxPreference f1384a;

    /* renamed from: b  reason: collision with root package name */
    protected CheckBoxPreference f1385b;

    /* renamed from: c  reason: collision with root package name */
    protected TextPreference f1386c;

    /* renamed from: d  reason: collision with root package name */
    protected TextPreference f1387d;
    protected TextPreference e;
    protected TextPreference f;
    protected PreferenceCategory g;
    protected DropDownPreference h;
    protected TextPreference i;
    protected CheckBoxPreference j;
    protected TextPreference k;
    protected ProgressDialog l;
    protected Context m;
    protected String[] n;
    protected List<SubscriptionInfo> o;
    protected int p;
    private SubscriptionManager.OnSubscriptionsChangedListener q = new d(this);

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0041  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void c() {
        /*
            r4 = this;
            miui.telephony.TelephonyManager r0 = miui.telephony.TelephonyManager.getDefault()
            int r0 = r0.getPhoneCount()
            r1 = 0
            r2 = 1
            if (r0 != r2) goto L_0x0019
            miui.telephony.TelephonyManager r0 = miui.telephony.TelephonyManager.getDefault()
            boolean r0 = r0.hasIccCard()
            if (r0 == 0) goto L_0x0019
        L_0x0016:
            r4.p = r2
            goto L_0x0039
        L_0x0019:
            java.util.List r0 = b.b.a.e.n.b()
            r4.o = r0
            java.util.List<miui.telephony.SubscriptionInfo> r0 = r4.o
            if (r0 == 0) goto L_0x0037
            int r0 = r0.size()
            if (r0 != 0) goto L_0x002a
            goto L_0x0037
        L_0x002a:
            java.util.List<miui.telephony.SubscriptionInfo> r0 = r4.o
            int r0 = r0.size()
            if (r0 != r2) goto L_0x0033
            goto L_0x0016
        L_0x0033:
            r0 = 2
            r4.p = r0
            goto L_0x0039
        L_0x0037:
            r4.p = r1
        L_0x0039:
            androidx.preference.PreferenceScreen r0 = r4.getPreferenceScreen()
            int r3 = r4.p
            if (r3 == 0) goto L_0x0042
            r1 = r2
        L_0x0042:
            r0.setEnabled(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.a.d.b.e.c():void");
    }

    public void b() {
        this.f1384a.setOnPreferenceChangeListener(this);
        this.f1385b.setOnPreferenceChangeListener(this);
        this.f1386c.setOnPreferenceClickListener(this);
        this.f1387d.setOnPreferenceClickListener(this);
        this.e.setOnPreferenceClickListener(this);
        this.f.setOnPreferenceClickListener(this);
        if (a()) {
            this.h.setOnPreferenceChangeListener(this);
        } else {
            this.i.setOnPreferenceClickListener(this);
        }
        this.j.setOnPreferenceChangeListener(this);
        this.k.setOnPreferenceClickListener(this);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.m = getActivity();
        addPreferencesFromResource(a() ? R.xml.antispam_new_settings_v12 : R.xml.antispam_new_settings);
        this.f1384a = (CheckBoxPreference) findPreference("key_share_settings");
        this.f1385b = (CheckBoxPreference) findPreference("key_antispam_enable");
        this.f1386c = (TextPreference) findPreference("key_msg_intercept");
        this.f1387d = (TextPreference) findPreference("key_call_intercept");
        this.e = (TextPreference) findPreference("key_number_blacklist");
        this.f = (TextPreference) findPreference("key_number_whitelist");
        if (a()) {
            this.h = (DropDownPreference) findPreference("key_show_antispam_notification");
        } else {
            this.i = (TextPreference) findPreference("key_show_antispam_notification");
        }
        this.j = (CheckBoxPreference) findPreference("key_sms_engine_auto_update");
        this.k = (TextPreference) findPreference("key_sms_engine_manual_update");
        this.g = (PreferenceCategory) findPreference("other_settings_group");
        if (i.f()) {
            getPreferenceScreen().d(this.f1386c);
        }
        this.n = getResources().getStringArray(R.array.st_antispam_notification_show_types);
        c();
        SubscriptionManager.getDefault().addOnSubscriptionsChangedListener(this.q);
    }

    public void onDestroy() {
        super.onDestroy();
        SubscriptionManager.getDefault().removeOnSubscriptionsChangedListener(this.q);
    }
}
