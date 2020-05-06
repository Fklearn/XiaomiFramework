package b.b.c.d;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import b.b.c.j.r;
import com.miui.maml.elements.FunctionElement;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.securitycenter.utils.b;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: b.b.c.d.m  reason: case insensitive filesystem */
public class C0193m extends C0185e implements Cloneable {

    /* renamed from: d  reason: collision with root package name */
    private static final ArrayList<Integer> f1689d = new ArrayList<>();
    private int e;
    private int f;
    private String g;
    private String h;
    private String i;
    private String j;
    private int k;
    private String l;
    private String m;
    private int n = -1;
    private int o = -1;
    private int p = -1;
    private boolean q = false;
    private boolean r = false;
    /* access modifiers changed from: private */
    public boolean s;
    private String t;
    private List<C0193m> u = new ArrayList();

    static {
        f1689d.add(1);
        f1689d.add(2);
        f1689d.add(4);
        f1689d.add(5);
        f1689d.add(6);
        f1689d.add(11);
        f1689d.add(16);
        f1689d.add(17);
        f1689d.add(20);
        f1689d.add(23);
        f1689d.add(24);
        f1689d.add(25);
        f1689d.add(27);
        f1689d.add(28);
        f1689d.add(34);
        f1689d.add(42);
        f1689d.add(43);
        f1689d.add(44);
        f1689d.add(45);
    }

    public C0193m() {
    }

    public C0193m(JSONObject jSONObject) {
        this.e = jSONObject.optInt("functionId");
        this.f = jSONObject.optInt("template");
        this.g = jSONObject.optString(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON);
        String a2 = n.a(this.g);
        if (!TextUtils.isEmpty(a2)) {
            this.g = a2;
        }
        this.i = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
        this.j = jSONObject.optString("summary");
        this.m = jSONObject.optString("button");
        this.k = jSONObject.optInt("type");
        this.l = jSONObject.optString(MijiaAlertModel.KEY_URL);
        String optString = jSONObject.optString("buttonColor2");
        if (!TextUtils.isEmpty(optString)) {
            try {
                this.n = Color.parseColor(optString);
                this.q = true;
            } catch (Exception e2) {
                Log.e(FunctionElement.TAG_NAME, "msg", e2);
            }
        }
        String optString2 = jSONObject.optString("btnBgColorOpenN2");
        String optString3 = jSONObject.optString("btnBgColorOpenP2");
        if (!TextUtils.isEmpty(optString2) && !TextUtils.isEmpty(optString3)) {
            try {
                this.o = Color.parseColor(optString2);
                this.p = Color.parseColor(optString3);
                this.r = true;
            } catch (Exception unused) {
            }
        }
        JSONArray optJSONArray = jSONObject.optJSONArray("images");
        if (optJSONArray != null && optJSONArray.length() > 0) {
            this.h = optJSONArray.optString(0);
        }
        this.t = jSONObject.optString("dataId");
    }

    public static C0193m a(JSONObject jSONObject) {
        int optInt = jSONObject.optInt("functionId");
        if (!n.a(optInt) || !f1689d.contains(Integer.valueOf(optInt))) {
            return null;
        }
        int optInt2 = jSONObject.optInt("template");
        if (optInt2 == 1 || optInt2 == 2 || optInt2 == 3 || optInt2 == 5) {
            return new C0193m(jSONObject);
        }
        return null;
    }

    public static void a(Context context, String str) {
        try {
            context.startActivity(new Intent(str));
        } catch (Exception e2) {
            Log.e(FunctionElement.TAG_NAME, "viewActionActivity", e2);
        }
    }

    public static void a(Context context, String str, Bundle bundle) {
        try {
            Intent intent = new Intent(str);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
        } catch (Exception e2) {
            Log.e(FunctionElement.TAG_NAME, "viewActionActivity", e2);
        }
    }

    public static void a(Context context, boolean z) {
        Settings.System.getInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, 0);
        Settings.System.putInt(context.getContentResolver(), Constants.System.STATUS_BAR_SHOW_NETWORK_ASSISTANT, z ? 1 : 0);
    }

    private void a(TextView textView, TextView textView2, Button button, ImageView imageView, ImageView imageView2, boolean z) {
        int i2;
        textView.setText(this.i);
        Resources resources = button.getContext().getResources();
        if (this.k != 1 || !this.s) {
            button.setText(this.m);
        } else {
            button.setText(R.string.close);
        }
        button.setTextColor(this.q ? this.n : resources.getColor(R.color.white));
        Drawable a2 = this.r ? b.a(resources.getDimension(R.dimen.big_result_blue_button_corner_radius), this.o, this.p) : resources.getDrawable(R.drawable.scanresult_button_blue);
        if (a2 != null) {
            button.setBackground(a2);
        }
        textView2.setText(this.j);
        if (imageView != null) {
            if (z) {
                r.a(this.g, imageView, r.g);
            } else {
                r.a(this.g, imageView, t.b());
            }
        }
        if (imageView2 != null) {
            String str = this.h;
            if (str == null || str.isEmpty()) {
                i2 = 8;
            } else {
                r.a(this.h, imageView2, t.b());
                i2 = 0;
            }
            imageView2.setVisibility(i2);
        }
    }

    private void a(A a2) {
        List<String> a3 = n.a();
        a2.f1633a.setText(this.i);
        a2.f1634b.setText(this.m);
        for (int i2 = 0; i2 < 4; i2++) {
            r.a("pkg_icon://" + a3.get(i2), a2.f1635c[i2], r.f);
        }
    }

    private void a(boolean z) {
        this.s = z;
        this.f1674b.notifyDataSetChanged();
    }

    public int a() {
        int i2 = this.f;
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 5 ? R.layout.v_result_item_template_3 : R.layout.v_result_item_template_26 : R.layout.v_result_item_template_19 : R.layout.v_result_item_template_18 : R.layout.v_result_item_template_3;
    }

    public void a(int i2, View view, Context context, C0191k kVar) {
        boolean z;
        ImageView imageView;
        ImageView imageView2;
        Button button;
        TextView textView;
        TextView textView2;
        E e2;
        super.a(i2, view, context, kVar);
        int i3 = this.f;
        if (i3 == 1) {
            e2 = (E) view.getTag();
        } else if (i3 == 2) {
            v vVar = (v) view.getTag();
            textView2 = vVar.f1645a;
            textView = vVar.f1646b;
            button = vVar.e;
            imageView2 = vVar.f1647c;
            imageView = vVar.f1648d;
            z = false;
            a(textView2, textView, button, imageView2, imageView, z);
        } else if (i3 == 3) {
            e2 = (w) view.getTag();
        } else if (i3 == 5) {
            a((A) view.getTag());
            return;
        } else {
            return;
        }
        textView2 = e2.f1645a;
        textView = e2.f1646b;
        button = e2.e;
        imageView2 = e2.f1647c;
        imageView = e2.f1648d;
        z = true;
        a(textView2, textView, button, imageView2, imageView, z);
    }

    public void a(Context context) {
        AccountManager.get(context).addAccount(miui.cloud.Constants.XIAOMI_ACCOUNT_TYPE, (String) null, (String[]) null, (Bundle) null, (Activity) context, new C0192l(this), new Handler());
    }

    public int b() {
        return this.e;
    }

    public C0193m clone() {
        Object obj;
        try {
            obj = super.clone();
            try {
                return (C0193m) obj;
            } catch (Exception e2) {
                e = e2;
                Log.e(FunctionElement.TAG_NAME, "msg", e);
                return (C0193m) obj;
            }
        } catch (Exception e3) {
            e = e3;
            obj = null;
            Log.e(FunctionElement.TAG_NAME, "msg", e);
            return (C0193m) obj;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:68:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onClick(android.view.View r4) {
        /*
            r3 = this;
            int r0 = r3.e
            boolean r1 = r4 instanceof android.widget.Button
            if (r1 != 0) goto L_0x000d
            r1 = 2131296561(0x7f090131, float:1.8211042E38)
            android.view.View r4 = r4.findViewById(r1)
        L_0x000d:
            android.content.Context r4 = r4.getContext()
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r1 = 1
            if (r0 == r1) goto L_0x00e1
            r2 = 2
            if (r0 == r2) goto L_0x00d7
            r2 = 4
            if (r0 == r2) goto L_0x00d1
            r2 = 5
            if (r0 == r2) goto L_0x00c7
            r2 = 6
            if (r0 == r2) goto L_0x00c0
            r2 = 11
            if (r0 == r2) goto L_0x00b9
            r2 = 20
            if (r0 == r2) goto L_0x00b6
            r2 = 34
            if (r0 == r2) goto L_0x00a7
            r2 = 16
            if (r0 == r2) goto L_0x0092
            r2 = 17
            if (r0 == r2) goto L_0x0087
            r1 = 27
            if (r0 == r1) goto L_0x0084
            r1 = 28
            if (r0 == r1) goto L_0x0078
            switch(r0) {
                case 23: goto L_0x0073;
                case 24: goto L_0x0060;
                case 25: goto L_0x005c;
                default: goto L_0x0044;
            }
        L_0x0044:
            switch(r0) {
                case 42: goto L_0x0058;
                case 43: goto L_0x0050;
                case 44: goto L_0x004c;
                case 45: goto L_0x0048;
                default: goto L_0x0047;
            }
        L_0x0047:
            return
        L_0x0048:
            java.lang.String r0 = "miui.intent.action.GARBAGE_DEEPCLEAN_WECHAT"
            goto L_0x00d3
        L_0x004c:
            java.lang.String r0 = "miui.powercenter.intent.action.QUICK_OPTIMIZE"
            goto L_0x00d3
        L_0x0050:
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "miui.intent.action.GARBAGE_CLEANUP"
            r0.<init>(r1)
            goto L_0x007f
        L_0x0058:
            java.lang.String r0 = "com.miui.gamebooster.action.ACCESS_MAINACTIVITY"
            goto L_0x00d3
        L_0x005c:
            java.lang.String r0 = "miui.intent.action.OP_AUTO_START"
            goto L_0x00d3
        L_0x0060:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enter_homepage_way"
            java.lang.String r2 = "00002"
            r0.putString(r1, r2)
            java.lang.String r1 = "miui.intent.action.ANTI_VIRUS"
        L_0x006e:
            a(r4, r1, r0)
            goto L_0x0108
        L_0x0073:
            r3.a((android.content.Context) r4)
            goto L_0x0108
        L_0x0078:
            android.content.Intent r0 = new android.content.Intent
            java.lang.String r1 = "miui.intent.action.GARBAGE_UNINSTALL_APPS"
            r0.<init>(r1)
        L_0x007f:
            com.miui.cleanmaster.g.b(r4, r0)
            goto L_0x0108
        L_0x0084:
            java.lang.String r0 = "miui.intent.action.NETWORKASSISTANT_FIREWALL"
            goto L_0x00d3
        L_0x0087:
            boolean r4 = r3.s
            r4 = r4 ^ r1
            com.miui.securitycenter.Application r0 = com.miui.securitycenter.Application.d()
            com.miui.support.provider.a.a(r0, r4)
            goto L_0x00dd
        L_0x0092:
            boolean r4 = r3.s
            r4 = r4 ^ r1
            com.miui.securitycenter.Application r0 = com.miui.securitycenter.Application.d()
            android.content.ContentResolver r0 = r0.getContentResolver()
            if (r4 == 0) goto L_0x00a0
            goto L_0x00a1
        L_0x00a0:
            r1 = 0
        L_0x00a1:
            java.lang.String r2 = "status_bar_show_network_speed"
            android.provider.Settings.System.putInt(r0, r2, r1)
            goto L_0x00dd
        L_0x00a7:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            java.lang.String r1 = "enter_way"
            java.lang.String r2 = "00009"
            r0.putString(r1, r2)
            java.lang.String r1 = "com.miui.securitycenter.action.TRANSITION"
            goto L_0x006e
        L_0x00b6:
            java.lang.String r0 = "miui.powercenter.intent.action.BOOT_SHUTDOWN_ONTIME"
            goto L_0x00d3
        L_0x00b9:
            boolean r0 = r3.s
            r0 = r0 ^ r1
            a((android.content.Context) r4, (boolean) r0)
            goto L_0x00cd
        L_0x00c0:
            boolean r0 = r3.s
            r0 = r0 ^ r1
            com.miui.securitycenter.h.b(r4, r0)
            goto L_0x00cd
        L_0x00c7:
            boolean r0 = r3.s
            r0 = r0 ^ r1
            com.miui.securitycenter.h.e(r4, r0)
        L_0x00cd:
            r3.a((boolean) r0)
            goto L_0x0108
        L_0x00d1:
            java.lang.String r0 = "miui.intent.action.GARBAGE_DEEPCLEAN"
        L_0x00d3:
            a((android.content.Context) r4, (java.lang.String) r0)
            goto L_0x0108
        L_0x00d7:
            boolean r4 = r3.s
            r4 = r4 ^ r1
            com.miui.securitycenter.h.b((boolean) r4)
        L_0x00dd:
            r3.a((boolean) r4)
            goto L_0x0108
        L_0x00e1:
            boolean r0 = r3.s
            r0 = r0 ^ r1
            android.content.ContentResolver r1 = r4.getContentResolver()
            java.lang.String r2 = "extra_show_security_notification"
            android.provider.MiuiSettings.System.putBoolean(r1, r2, r0)
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0103 }
            java.lang.String r2 = "com.miui.securitycenter.action.NOTIFICATION_SERVICE"
            r1.<init>(r2)     // Catch:{ Exception -> 0x0103 }
            java.lang.String r2 = "com.miui.securitycenter"
            r1.setPackage(r2)     // Catch:{ Exception -> 0x0103 }
            if (r0 == 0) goto L_0x00ff
            r4.startService(r1)     // Catch:{ Exception -> 0x0103 }
            goto L_0x00cd
        L_0x00ff:
            r4.stopService(r1)     // Catch:{ Exception -> 0x0103 }
            goto L_0x00cd
        L_0x0103:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x00cd
        L_0x0108:
            java.lang.String r4 = r3.t
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0118
            int r4 = r3.e
            java.lang.String r4 = java.lang.String.valueOf(r4)
            r3.t = r4
        L_0x0118:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.d.C0193m.onClick(android.view.View):void");
    }
}
