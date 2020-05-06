package com.miui.optimizecenter.widget.storage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import b.b.i.b.e;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.cleanmaster.g;
import com.miui.optimizecenter.storage.k;
import com.miui.securitycenter.R;
import com.miui.securityscan.i.i;
import java.util.HashMap;

public enum b {
    STORAGE_TOTAL(R.drawable.strorage_column_t_0, R.drawable.strorage_column_m_0, R.drawable.strorage_column_b_0, R.color.storage_pie_line_0, 0, (int) null),
    STORAGE_OTHER(R.drawable.strorage_column_t_8, R.drawable.strorage_column_m_8, R.drawable.strorage_column_b_8, R.color.storage_pie_line_8, R.string.storage_item_other_name, (int) null),
    STORAGE_APP_DATA(R.drawable.strorage_column_t_7, R.drawable.strorage_column_m_7, R.drawable.strorage_column_b_7, R.color.storage_pie_line_7, R.string.storage_item_appdata_name, "miui.intent.action.STORAGE_APP_INFO_LIST"),
    STORAGE_IMAGE(R.drawable.strorage_column_t_6, R.drawable.strorage_column_m_6, R.drawable.strorage_column_b_6, R.color.storage_pie_line_6, R.string.storage_item_picture_name, "com.android.fileexplorer.export.VIEW_HOME"),
    STORAGE_VOICE(R.drawable.strorage_column_t_5, R.drawable.strorage_column_m_5, R.drawable.strorage_column_b_5, R.color.storage_pie_line_5, R.string.storage_item_audio_name, "com.android.fileexplorer.export.VIEW_HOME"),
    STORAGE_VIDEO(R.drawable.strorage_column_t_4, R.drawable.strorage_column_m_4, R.drawable.strorage_column_b_4, R.color.storage_pie_line_4, R.string.storage_item_video_name, "com.android.fileexplorer.export.VIEW_HOME"),
    STORAGE_APK(R.drawable.strorage_column_t_3, R.drawable.strorage_column_m_3, R.drawable.strorage_column_b_3, R.color.storage_pie_line_3, R.string.storage_item_apk_name, "miui.intent.action.GARBAGE_APK_MANAGE"),
    STORAGE_FILE(R.drawable.strorage_column_t_2, R.drawable.strorage_column_m_2, R.drawable.strorage_column_b_2, R.color.storage_pie_line_2, R.string.storage_item_doc_name, "com.android.fileexplorer.export.VIEW_HOME"),
    STORAGE_SYSTEM(R.drawable.strorage_column_t_1, R.drawable.strorage_column_m_1, R.drawable.strorage_column_b_1, R.color.storage_pie_line_1, R.string.storage_item_system_name, (int) null),
    CLENER(0, 0, 0, R.color.storage_pie_cleaner, R.string.storage_item_clean_name, "miui.intent.action.GARBAGE_CLEANUP");
    
    int l;
    int m;
    int n;
    int o;
    int p;
    String q;
    HashMap<String, String> r;

    private b(int i, int i2, int i3, int i4, int i5, String str) {
        this.r = new HashMap<>();
        this.l = i;
        this.m = i2;
        this.n = i3;
        this.o = i4;
        this.p = i5;
        this.q = str;
    }

    public String a(Context context) {
        return context.getResources().getString(this.p);
    }

    public void a(int i) {
        this.p = i;
    }

    public void a(String str) {
        this.q = str;
    }

    public int b(Context context) {
        return context.getResources().getColor(this.o);
    }

    public boolean c(Context context) {
        if (TextUtils.isEmpty(this.q)) {
            return false;
        }
        if (!TextUtils.equals(this.q, "com.android.fileexplorer.export.VIEW_HOME") || k.a(context)) {
            return i.a(context, new Intent(this.q));
        }
        return false;
    }

    public void d(Context context) {
        String str;
        if (!TextUtils.isEmpty(this.q)) {
            Intent intent = new Intent(this.q);
            if (TextUtils.equals("miui.intent.action.GARBAGE_CLEANUP", this.q)) {
                g.b(context, intent);
                e.b("cleanNowEntry");
            } else if (i.a(context, intent)) {
                if (TextUtils.equals(this.q, "com.android.fileexplorer.export.VIEW_HOME")) {
                    int i = a.f5851a[ordinal()];
                    if (i == 1) {
                        str = MimeTypes.BASE_TYPE_VIDEO;
                    } else if (i == 2) {
                        intent.putExtra("extraTabName", "music");
                        str = MimeTypes.BASE_TYPE_AUDIO;
                    } else if (i != 3) {
                        if (i == 4) {
                            intent.putExtra("extraTabName", "picture");
                            str = "image";
                        }
                        context.startActivity(intent);
                    } else {
                        str = "doc";
                    }
                    intent.putExtra("extraTabName", str);
                } else if (STORAGE_APK == this) {
                    str = "apk";
                } else {
                    if (STORAGE_APP_DATA == this) {
                        str = "appData";
                    }
                    context.startActivity(intent);
                }
                e.b(str);
                try {
                    context.startActivity(intent);
                } catch (Exception unused) {
                }
            }
        }
    }
}
