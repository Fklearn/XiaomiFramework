package com.miui.appmanager.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import b.b.c.j.A;
import b.b.c.j.l;
import b.b.c.j.x;
import com.miui.analytics.AnalyticsUtil;
import com.miui.appmanager.AppManageUtils;
import com.miui.appmanager.B;
import com.miui.appmanager.a.a;
import com.miui.cleanmaster.f;
import com.miui.cleanmaster.g;
import com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import com.miui.securitycenter.R;
import java.util.List;
import miui.os.Build;

public class AMMainTopView extends FrameLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final Uri f3694a = Uri.parse("mimarket://update");

    /* renamed from: b  reason: collision with root package name */
    private Context f3695b;

    /* renamed from: c  reason: collision with root package name */
    private View f3696c;

    /* renamed from: d  reason: collision with root package name */
    private View f3697d;
    private AMMainTopFunctionView e;
    private AMMainTopFunctionView f;
    private AMMainTopFunctionView g;
    private View h;
    private View i;
    private View j;
    private View k;
    private TextView l;
    private boolean m;
    private int n;

    public AMMainTopView(Context context) {
        this(context, (AttributeSet) null);
    }

    public AMMainTopView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AMMainTopView(Context context, AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        int i3;
        AMMainTopFunctionView aMMainTopFunctionView;
        boolean z = true;
        LayoutInflater.from(context).inflate(R.layout.app_manager_main_card_layout_top, this, true);
        this.f3695b = context;
        this.f3696c = findViewById(R.id.am_function_updater);
        this.f3697d = this.f3696c.findViewById(R.id.img_content);
        this.e = (AMMainTopFunctionView) findViewById(R.id.am_function_uninstall);
        this.f = (AMMainTopFunctionView) findViewById(R.id.am_function_splite);
        this.g = (AMMainTopFunctionView) findViewById(R.id.am_function_permission);
        this.k = this.f3696c.findViewById(R.id.am_red_point);
        this.l = (TextView) this.f3696c.findViewById(R.id.am_update_label);
        this.h = findViewById(R.id.am_function_perm_record);
        this.i = this.h.findViewById(R.id.img_content);
        this.j = this.h.findViewById(R.id.privacy_red_point);
        this.f3696c.setVisibility(Build.IS_INTERNATIONAL_BUILD ? d() : z ? 0 : 8);
        if (o.a(context)) {
            this.f.setVisibility(8);
            this.h.setVisibility(0);
        } else {
            if (Build.IS_INTERNATIONAL_BUILD && d()) {
                this.f.setIcon(R.drawable.am_mimarket);
                aMMainTopFunctionView = this.f;
                i3 = R.string.app_manager_find_apps;
            } else if (UserHandle.myUserId() == 0) {
                this.f.setVisibility(0);
                this.f.setIcon(R.drawable.am_bipartition);
                aMMainTopFunctionView = this.f;
                i3 = R.string.app_manager_bipartition;
            } else {
                this.f.setVisibility(8);
            }
            aMMainTopFunctionView.setTitle(i3);
        }
        this.e.setIcon(R.drawable.am_uninstall);
        this.e.setTitle(R.string.app_manager_uninstall);
        this.g.setIcon(R.drawable.am_permission);
        this.g.setTitle(R.string.app_manager_perm);
        c();
        this.f3696c.setOnClickListener(this);
        this.e.setOnClickListener(this);
        this.f.setOnClickListener(this);
        this.g.setOnClickListener(this);
        this.h.setOnClickListener(this);
    }

    private void a(Intent intent) {
        try {
            this.f3695b.startActivity(intent);
        } catch (Exception e2) {
            Log.e("AMMainTopView", "start Activity error", e2);
            AnalyticsUtil.trackException(e2);
        }
    }

    private void c() {
        l.a(this.f3696c, this.f3697d);
        AMMainTopFunctionView aMMainTopFunctionView = this.f;
        l.a((View) aMMainTopFunctionView, (View) aMMainTopFunctionView.f3692a);
        l.a(this.h, this.i);
        AMMainTopFunctionView aMMainTopFunctionView2 = this.e;
        l.a((View) aMMainTopFunctionView2, (View) aMMainTopFunctionView2.f3692a);
        AMMainTopFunctionView aMMainTopFunctionView3 = this.g;
        l.a((View) aMMainTopFunctionView3, (View) aMMainTopFunctionView3.f3692a);
    }

    private boolean d() {
        Intent intent = new Intent();
        intent.setData(f3694a);
        intent.setPackage("com.xiaomi.mipicks");
        List<ResolveInfo> queryIntentActivities = this.f3695b.getPackageManager().queryIntentActivities(intent, 1);
        return queryIntentActivities != null && !queryIntentActivities.isEmpty();
    }

    public void a(boolean z) {
        this.j.setVisibility(z ? 0 : 4);
    }

    public boolean a() {
        return this.m;
    }

    public void b() {
        int i2 = 0;
        this.l.setVisibility(this.m ? 0 : 8);
        View view = this.k;
        if (!this.m) {
            i2 = 8;
        }
        view.setVisibility(i2);
        this.l.setText(this.n + "");
    }

    public int getUpdateNum() {
        return this.n;
    }

    public void onClick(View view) {
        String str;
        String str2;
        switch (view.getId()) {
            case R.id.am_function_perm_record /*2131296392*/:
                a(AppBehaviorRecordActivity.b("app_manager"));
                this.j.setVisibility(8);
                return;
            case R.id.am_function_permission /*2131296393*/:
                Intent intent = new Intent();
                intent.setAction("miui.intent.action.LICENSE_MANAGER");
                intent.setPackage("com.miui.securitycenter");
                a(intent);
                str = "permission";
                break;
            case R.id.am_function_splite /*2131296394*/:
                Intent intent2 = new Intent();
                if (!Build.IS_INTERNATIONAL_BUILD || !d()) {
                    intent2.setAction("miui.intent.action.XSPACE_SETTING");
                    intent2.setPackage("com.miui.securitycore");
                    str2 = "replica_app";
                } else {
                    intent2.setData(Uri.parse("mimarket://home?ref=manageapp"));
                    intent2.setPackage("com.xiaomi.mipicks");
                    str2 = "mimarket";
                }
                a.b(str2);
                a(intent2);
                return;
            case R.id.am_function_uninstall /*2131296395*/:
                Intent intent3 = new Intent();
                if (Build.IS_INTERNATIONAL_BUILD) {
                    intent3.setAction("miui.intent.action.GARBAGE_UNINSTALL_APPS");
                    intent3.setPackage("com.miui.cleanmaster");
                    if (!f.a(this.f3695b)) {
                        g.b(this.f3695b, new Intent("miui.intent.action.GARBAGE_CLEANUP"));
                        str = "uninstall";
                        break;
                    }
                } else {
                    intent3.setClassName("com.xiaomi.market", "com.xiaomi.market.ui.LocalAppsActivity");
                    intent3.putExtra("back", true);
                }
                a(intent3);
                str = "uninstall";
            case R.id.am_function_updater /*2131296396*/:
                Intent intent4 = new Intent();
                intent4.putExtra("back", true);
                if (Build.IS_INTERNATIONAL_BUILD) {
                    intent4.setData(Uri.parse("mimarket://update"));
                    intent4.setPackage("com.xiaomi.mipicks");
                } else {
                    intent4.setAction("com.xiaomi.market.UPDATE_APP_LIST");
                }
                if (!x.c(this.f3695b, intent4)) {
                    A.a(this.f3695b, (int) R.string.app_not_installed_toast);
                }
                this.l.setVisibility(8);
                this.k.setVisibility(8);
                AppManageUtils.e(AppManageUtils.a(0));
                AppManageUtils.a(true);
                this.f3695b.getContentResolver().notifyChange(B.f3568a, (ContentObserver) null);
                str = "update";
                break;
            default:
                return;
        }
        a.b(str);
    }

    public void setLabelVisible(boolean z) {
        this.m = z;
    }

    public void setUpdateNum(int i2) {
        this.n = i2;
    }
}
