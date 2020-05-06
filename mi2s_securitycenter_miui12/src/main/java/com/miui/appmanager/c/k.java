package com.miui.appmanager.c;

import android.util.SparseIntArray;
import android.view.View;
import com.miui.securitycenter.R;

public abstract class k implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    public static final SparseIntArray f3650a = new SparseIntArray();

    /* renamed from: b  reason: collision with root package name */
    protected transient int f3651b;

    static {
        f3650a.put(R.layout.app_manager_card_layout_title, 0);
        f3650a.put(R.layout.app_manager_card_layout_line, 1);
        f3650a.put(R.layout.app_manager_list_item, 2);
        f3650a.put(R.layout.app_manager_sort_title, 3);
        f3650a.put(R.layout.app_manager_adv_horizontal, 4);
        f3650a.put(R.layout.app_manager_search_prompt_layout, 5);
        f3650a.put(R.layout.app_manager_listitem_header_view, 6);
        f3650a.put(R.layout.result_template_ad_fb, 7);
        f3650a.put(R.layout.result_template_ad_columbus, 8);
        f3650a.put(R.layout.result_template_ad_admob_context, 9);
        f3650a.put(R.layout.result_template_ad_admob_install, 10);
        f3650a.put(R.layout.result_template_ad_global_empty, 11);
        f3650a.put(R.layout.am_card_layout_top, 12);
    }

    public k(int i) {
        this.f3651b = i;
    }

    public static int a(int i) {
        return f3650a.keyAt(f3650a.indexOfValue(i));
    }

    public int a() {
        return f3650a.get(this.f3651b);
    }

    public void onClick(View view) {
    }
}
