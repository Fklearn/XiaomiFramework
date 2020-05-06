package com.miui.optimizecenter.storage.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import b.b.i.b.g;
import com.miui.optimizecenter.storage.b.e;
import com.miui.optimizecenter.storage.d.d;
import com.miui.securitycenter.R;
import java.io.File;
import java.lang.ref.WeakReference;

public class PreferenceCategoryView extends LinearLayout implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private d f5795a;

    /* renamed from: b  reason: collision with root package name */
    private com.miui.optimizecenter.storage.d.a f5796b;

    /* renamed from: c  reason: collision with root package name */
    private Resources f5797c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f5798d;
    private TextView e;
    private PreferenceItemView f;
    private PreferenceItemView g;
    private PreferenceItemSpaceView h;
    private PreferenceItemView i;
    private boolean j;
    private String k;
    private a l;
    private b m;

    public interface a {
        void a(PreferenceCategoryView preferenceCategoryView, a aVar);
    }

    private static class b extends AsyncTask<d, Integer, Pair<Long, Long>> {

        /* renamed from: a  reason: collision with root package name */
        public volatile boolean f5799a;

        /* renamed from: b  reason: collision with root package name */
        private WeakReference<PreferenceCategoryView> f5800b;

        b(PreferenceCategoryView preferenceCategoryView) {
            this.f5800b = new WeakReference<>(preferenceCategoryView);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Pair<Long, Long> doInBackground(d... dVarArr) {
            File d2 = dVarArr[0].d();
            if (d2 == null) {
                return new Pair<>(0L, 0L);
            }
            return new Pair<>(Long.valueOf(d2.getTotalSpace()), Long.valueOf(d2.getUsableSpace()));
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(Pair<Long, Long> pair) {
            PreferenceCategoryView preferenceCategoryView;
            WeakReference<PreferenceCategoryView> weakReference = this.f5800b;
            if (weakReference != null && (preferenceCategoryView = (PreferenceCategoryView) weakReference.get()) != null) {
                preferenceCategoryView.a(((Long) pair.first).longValue(), ((Long) pair.second).longValue());
                this.f5799a = true;
            }
        }
    }

    public PreferenceCategoryView(Context context) {
        this(context, (AttributeSet) null);
    }

    public PreferenceCategoryView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PreferenceCategoryView(Context context, @Nullable AttributeSet attributeSet, int i2) {
        super(context, attributeSet, i2);
        this.f5798d = false;
        this.f5797c = context.getResources();
    }

    public static PreferenceCategoryView a(Context context, ViewGroup viewGroup) {
        return (PreferenceCategoryView) LayoutInflater.from(context).inflate(R.layout.storage_pref_category, viewGroup, false);
    }

    private void a(int i2) {
        int i3;
        int i4 = R.string.sd_mount;
        if (i2 == 0 || i2 == 6) {
            this.f.setEnabled(true);
            if (this.f5798d) {
                i3 = R.string.sd_mount_summary;
                this.f.setTitle(this.f5797c.getString(i4));
                this.f.setSummary(this.f5797c.getString(i3));
                this.g.setVisibility(8);
                this.h.setVisibility(8);
            }
            i3 = R.string.usb_mount_summary;
        } else {
            this.f.setEnabled(false);
            if (this.f5798d) {
                i3 = R.string.sd_insert_summary;
                this.f.setTitle(this.f5797c.getString(i4));
                this.f.setSummary(this.f5797c.getString(i3));
                this.g.setVisibility(8);
                this.h.setVisibility(8);
            }
            i3 = R.string.usb_insert_summary;
        }
        i4 = R.string.usb_mount;
        this.f.setTitle(this.f5797c.getString(i4));
        this.f.setSummary(this.f5797c.getString(i3));
        this.g.setVisibility(8);
        this.h.setVisibility(8);
    }

    private void b() {
        int i2;
        int i3;
        this.f.setEnabled(true);
        if (this.f5798d) {
            i3 = R.string.sd_eject;
            i2 = R.string.sd_eject_summary;
        } else {
            i3 = R.string.usb_eject;
            i2 = R.string.usb_eject_summary;
        }
        this.f.setTitle(this.f5797c.getString(i3));
        this.f.setSummary(this.f5797c.getString(i2));
        this.g.setVisibility(0);
        this.h.setVisibility(0);
    }

    private void c() {
        int i2;
        PreferenceItemView preferenceItemView;
        int e2 = this.f5795a.e();
        this.f.setEnabled(true);
        if (e2 == 2 || e2 == 3) {
            b();
        } else {
            a(e2);
        }
        if (!this.j || (!"mtp".equals(this.k) && !"ptp".equals(this.k))) {
            PreferenceItemView preferenceItemView2 = this.g;
            if (preferenceItemView2 != null) {
                preferenceItemView2.setEnabled(this.f.isEnabled());
                if (this.f5798d) {
                    preferenceItemView = this.g;
                    i2 = R.string.sd_format_summary;
                } else {
                    preferenceItemView = this.g;
                    i2 = R.string.usb_format_summary;
                }
                preferenceItemView.setSummary(i2);
                return;
            }
            return;
        }
        this.f.setEnabled(false);
        if (e2 == 2 || e2 == 3) {
            this.f.setSummary(this.f5797c.getString(R.string.mtp_ptp_mode_summary));
        }
        PreferenceItemView preferenceItemView3 = this.g;
        if (preferenceItemView3 != null) {
            preferenceItemView3.setEnabled(false);
            this.g.setSummary(this.f5797c.getString(R.string.mtp_ptp_mode_summary));
        }
    }

    public void a() {
        b bVar = this.m;
        if ((bVar == null || bVar.f5799a) && this.f5795a != null) {
            this.m = new b(this);
            this.m.execute(new d[]{this.f5795a});
        }
    }

    public void a(long j2, long j3) {
        PreferenceItemSpaceView preferenceItemSpaceView = this.h;
        if (preferenceItemSpaceView != null) {
            preferenceItemSpaceView.a(j2, j3);
        }
    }

    public void a(d dVar, com.miui.optimizecenter.storage.d.a aVar, a aVar2) {
        int i2;
        int i3;
        int i4;
        int i5;
        this.f5796b = aVar;
        this.f5795a = dVar;
        this.f5798d = this.f5796b.c();
        g.a(this.e, this.f5796b.a());
        this.l = aVar2;
        Context context = getContext();
        if (getChildCount() > 2) {
            removeViews(2, getChildCount() - 1);
        }
        this.h = (PreferenceItemSpaceView) LayoutInflater.from(context).inflate(R.layout.storage_main_item_space, this, false);
        addView(this.h);
        if (this.f5798d) {
            i2 = R.string.sd_eject;
            i3 = R.string.sd_eject_summary;
        } else {
            i2 = R.string.usb_eject;
            i3 = R.string.usb_eject_summary;
        }
        this.f = PreferenceItemView.a(context, this, a.MOUNT);
        this.f.setTitle(this.f5797c.getString(i2));
        this.f.setSummary(this.f5797c.getString(i3));
        this.f.setOnClickListener(this);
        addView(this.f);
        if (this.f5798d) {
            i4 = R.string.sd_format;
            i5 = R.string.sd_format_summary;
        } else {
            i4 = R.string.usb_format;
            i5 = R.string.usb_format_summary;
        }
        this.g = PreferenceItemView.a(context, this, a.FORMAT);
        this.g.setTitle(this.f5797c.getString(i4));
        this.g.setSummary(this.f5797c.getString(i5));
        this.g.setOnClickListener(this);
        addView(this.g);
        if (this.f5798d) {
            this.i = PreferenceItemView.a(context, this, a.STORAGE_PRIORITY);
            this.i.setTitle((int) R.string.priority_storage_title);
            this.i.setSummary((int) R.string.priority_storage_summary);
            this.i.setOnClickListener(this);
            addView(this.i);
        }
        if (this.f5795a.g()) {
            this.m = new b(this);
            this.m.execute(new d[]{this.f5795a});
        }
        c();
    }

    public com.miui.optimizecenter.storage.d.a getmDiskInfoCompat() {
        return this.f5796b;
    }

    public d getmVolumeInfo() {
        return this.f5795a;
    }

    public void onClick(View view) {
        a aVar;
        a aVar2;
        if (view != this.f) {
            if (view == this.g) {
                aVar = this.l;
                if (aVar != null) {
                    aVar2 = a.FORMAT;
                } else {
                    return;
                }
            } else if (view == this.i && (aVar = this.l) != null) {
                aVar2 = a.STORAGE_PRIORITY;
            } else {
                return;
            }
            aVar.a(this, aVar2);
        } else if (this.f5795a.e() == 0) {
            e.a(getContext(), this.f5795a.c());
        } else {
            new com.miui.optimizecenter.storage.c.b(getContext(), this.f5795a).execute(new Void[0]);
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.e = (TextView) findViewById(R.id.group_title);
    }
}
