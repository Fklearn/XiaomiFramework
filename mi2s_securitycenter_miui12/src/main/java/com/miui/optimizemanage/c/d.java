package com.miui.optimizemanage.c;

import android.util.SparseIntArray;
import android.view.View;
import com.miui.securitycenter.R;

public abstract class d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private static final SparseIntArray f5892a = new SparseIntArray();

    /* renamed from: b  reason: collision with root package name */
    protected transient int f5893b;

    static {
        f5892a.put(R.layout.om_result_top_card_layout, 0);
        f5892a.put(R.layout.card_layout_list_title, 1);
        f5892a.put(R.layout.card_layout_line, 2);
        f5892a.put(R.layout.om_result_list_item_lock_view, 3);
        f5892a.put(R.layout.card_layout_news_template_7, 4);
        f5892a.put(R.layout.om_result_function_template_1, 5);
        f5892a.put(R.layout.om_result_activity_template_1, 6);
        f5892a.put(R.layout.om_result_activity_template_3, 7);
        f5892a.put(R.layout.om_result_activity_template_4, 8);
        f5892a.put(R.layout.result_template_ad_fb, 9);
        f5892a.put(R.layout.result_template_ad_columbus, 10);
        f5892a.put(R.layout.result_template_ad_admob_context, 11);
        f5892a.put(R.layout.result_template_ad_admob_install, 12);
        f5892a.put(R.layout.result_template_ad_global_empty, 13);
    }

    public static int c() {
        return f5892a.size();
    }

    public int a() {
        return this.f5893b;
    }

    public e a(View view) {
        return new e(view);
    }

    public void a(int i) {
        this.f5893b = i;
    }

    public int b() {
        return f5892a.get(this.f5893b);
    }

    public void onClick(View view) {
    }
}
