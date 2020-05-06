package com.miui.gamebooster.a;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import b.b.c.j.B;
import b.b.c.j.r;
import b.c.a.b.d;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.miui.gamebooster.globalgame.present.g;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.j.c;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.model.C0397c;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.model.f;
import com.miui.gamebooster.view.n;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class x extends PagerAdapter implements com.miui.gamebooster.j.b, Handler.Callback, f {

    /* renamed from: a  reason: collision with root package name */
    private HashMap<String, b> f4071a;

    /* renamed from: b  reason: collision with root package name */
    private List<C0397c> f4072b;

    /* renamed from: c  reason: collision with root package name */
    private Context f4073c;

    /* renamed from: d  reason: collision with root package name */
    private a f4074d;
    private Map<String, String> e = new HashMap();
    private Map<String, String> f = new HashMap();
    private Handler g;
    private final boolean h;
    @DrawableRes
    private final int i;
    private final boolean j;
    private final d k;
    private int l = 0;

    public interface a {
        void a(@Nullable C0397c cVar, int i, boolean z);

        void c(int i);

        void d(int i);
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        View f4075a;

        /* renamed from: b  reason: collision with root package name */
        View f4076b;

        /* renamed from: c  reason: collision with root package name */
        ImageView f4077c;

        /* renamed from: d  reason: collision with root package name */
        View f4078d;

        public b a(View view) {
            this.f4075a = view;
            this.f4076b = view.findViewById(R.id.startIndicator);
            this.f4077c = (ImageView) view.findViewById(R.id.icon);
            this.f4078d = view.findViewById(R.id.endIndicator);
            return this;
        }
    }

    @SuppressLint({"UseSparseArrays"})
    public x(Context context, a aVar) {
        this.f4073c = context.getApplicationContext();
        this.f4071a = new HashMap<>();
        this.f4074d = aVar;
        this.g = new Handler(Looper.myLooper(), this);
        this.h = com.miui.gamebooster.j.a.a();
        this.i = this.h ? R.drawable.gb_game_gallery_def_sqaure : R.drawable.gb_game_gallery_def;
        this.j = na.c();
        this.k = g(this.i);
    }

    public static ImageView a(b bVar) {
        if (bVar == null) {
            return null;
        }
        return bVar.f4077c;
    }

    private static String a(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("game_boost_app_urls", "");
    }

    private String a(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return null;
        }
        return applicationInfo.packageName + "$" + applicationInfo.uid;
    }

    private void a(Context context, String str, ImageView imageView, @NonNull Runnable runnable) {
        if (str.startsWith("AppStore")) {
            str = String.format("http://t14.market.mi-img.com/download/%s/a.jpg", new Object[]{str});
        }
        com.miui.gamebooster.globalgame.util.b.a((Object) str);
        g.a(context, str, imageView, this.k, runnable);
    }

    private static void a(Context context, Map<String, String> map) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("game_boost_app_urls", com.miui.gamebooster.globalgame.util.d.a((Object) map)).apply();
    }

    private void a(ImageView imageView, String str, String str2) {
        Map<String, String> a2;
        C0325c cVar = new C0325c(this, imageView, str2);
        if (this.e.isEmpty() && (a2 = com.miui.gamebooster.globalgame.util.d.a(a(this.f4073c))) != null && !a2.isEmpty()) {
            this.e.putAll(a2);
        }
        if (TextUtils.isEmpty(this.e.get(str)) || c()) {
            cVar.run();
            return;
        }
        imageView.setScaleType(this.h ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
        a(this.f4073c, this.e.get(str), imageView, cVar);
    }

    private void a(a aVar, boolean z, int i2) {
        if (aVar != null) {
            if (z) {
                aVar.c(i2);
            } else {
                aVar.d(i2);
            }
        }
    }

    public static void a(b bVar, boolean z) {
        if (bVar != null) {
            Utils.b(z, bVar.f4076b, bVar.f4078d, bVar.f4075a);
        }
    }

    private void a(Object obj) {
        try {
            this.e.putAll((Map) obj);
            a(this.f4073c, this.e);
            a(this.e);
        } catch (Exception e2) {
            com.miui.gamebooster.globalgame.util.b.b(e2);
        }
    }

    private String b(ApplicationInfo applicationInfo) {
        if (applicationInfo == null) {
            return "";
        }
        return (B.c(applicationInfo.uid) == 999 ? "pkg_icon_xspace://" : "pkg_icon://").concat(applicationInfo.packageName);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000e, code lost:
        r0 = r3.indexOf("$");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String b(java.lang.String r3) {
        /*
            r2 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 != 0) goto L_0x0019
            java.lang.String r0 = "$"
            boolean r1 = r3.contains(r0)
            if (r1 == 0) goto L_0x0019
            int r0 = r3.indexOf(r0)
            if (r0 <= 0) goto L_0x0019
            r1 = 0
            java.lang.String r3 = r3.substring(r1, r0)
        L_0x0019:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.a.x.b(java.lang.String):java.lang.String");
    }

    private boolean c() {
        return !h.i();
    }

    private d g(@DrawableRes int i2) {
        d.a aVar = new d.a();
        aVar.a(true);
        aVar.b(true);
        aVar.c(true);
        aVar.c(i2);
        aVar.a(i2);
        aVar.b(i2);
        return aVar.a();
    }

    private boolean h(int i2) {
        List<C0397c> list = this.f4072b;
        return list == null || i2 > list.size() - 1 || this.f4072b.get(i2) == null || this.f4072b.get(i2).b() == null || TextUtils.isEmpty(this.f4072b.get(i2).b().packageName);
    }

    public int a(String str) {
        if (TextUtils.isEmpty(str) || Utils.a(this.f4072b)) {
            return 0;
        }
        if ("add_game_fake_pkg_name".equals(str)) {
            return this.l;
        }
        int size = this.f4072b.size();
        for (int i2 = 0; i2 < size; i2++) {
            C0398d dVar = this.f4072b.get(i2);
            if (dVar != null && dVar.b() != null && !TextUtils.isEmpty(dVar.d()) && str.equals(a(dVar.b()))) {
                return i2;
            }
        }
        return 0;
    }

    public C0397c a(int i2) {
        List<C0397c> list = this.f4072b;
        if (list == null || i2 > list.size() - 1) {
            return null;
        }
        return this.f4072b.get(i2);
    }

    public void a() {
        ImageView imageView;
        this.f4074d = null;
        this.g.removeCallbacksAndMessages((Object) null);
        if (this.f4071a.size() != 0) {
            for (b next : this.f4071a.values()) {
                if (!(next == null || (imageView = next.f4077c) == null)) {
                    Utils.a(imageView);
                }
            }
            this.f4071a.clear();
        }
    }

    public /* synthetic */ void a(ImageView imageView, String str) {
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        r.a(str, imageView, r.f, this.f4073c.getResources().getDrawable(this.i));
    }

    public /* synthetic */ void a(C0397c cVar, int i2) {
        a aVar = this.f4074d;
        if (aVar != null) {
            aVar.a(cVar, i2, true);
        }
    }

    public /* synthetic */ void a(List list) {
        String a2 = com.miui.gamebooster.j.a.a(list);
        com.miui.gamebooster.globalgame.util.b.a((Object) a2);
        boolean z = false;
        if (!TextUtils.isEmpty(a2)) {
            try {
                Object a3 = ((c) com.miui.gamebooster.globalgame.util.d.a(a2, c.class)).a();
                if (a3 instanceof Map) {
                    Message message = new Message();
                    message.what = TsExtractor.TS_STREAM_TYPE_AC3;
                    message.obj = a3;
                    this.g.sendMessage(message);
                    z = true;
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                com.miui.gamebooster.globalgame.util.b.b(e2);
            }
        }
        if (!z) {
            com.miui.gamebooster.i.a.c.a();
        }
    }

    public void a(Map<String, String> map) {
        ImageView a2;
        if (!Utils.a(this.f4071a) && !c()) {
            for (Map.Entry<String, b> key : this.f4071a.entrySet()) {
                String str = (String) key.getKey();
                String b2 = b(str);
                if (map.containsKey(b2) && (a2 = a(this.f4071a.get(str))) != null && !TextUtils.isEmpty(str) && this.f.containsKey(str) && this.f.get(str) != null) {
                    a(a2, b2, this.f.get(str));
                }
            }
        }
    }

    public CharSequence b(int i2) {
        List<C0397c> list = this.f4072b;
        if (list == null || i2 > list.size() - 1 || this.f4072b.get(i2) == null) {
            return null;
        }
        return this.f4072b.get(i2).d();
    }

    public List<C0397c> b() {
        List<C0397c> list = this.f4072b;
        return list == null ? new ArrayList() : list;
    }

    public void b(List<C0397c> list) {
        this.f4072b = new ArrayList();
        ArrayList arrayList = new ArrayList();
        for (C0397c cVar : new ArrayList(list)) {
            ApplicationInfo b2 = cVar.b();
            if (!(cVar == null || b2 == null || TextUtils.isEmpty(b2.packageName))) {
                String a2 = a(b2);
                arrayList.add(b2.packageName);
                this.f.put(a2, b(b2));
                this.f4072b.add(cVar);
            }
        }
        this.f4072b.add((Object) null);
        this.l = this.f4072b.size() - 1;
        com.miui.gamebooster.globalgame.util.b.c("in Adapter of position: Str 0,data:" + this.f4072b.get(0));
        notifyDataSetChanged();
        if (!c()) {
            b.b.c.c.a.a.a(new C0324b(this, arrayList));
        }
    }

    @Nullable
    public String c(int i2) {
        List<C0397c> list = this.f4072b;
        if (list == null || i2 > list.size() - 1 || this.f4072b.get(i2) == null || this.f4072b.get(i2).b() == null) {
            return null;
        }
        return a(this.f4072b.get(i2).b());
    }

    public /* synthetic */ void d(int i2) {
        a(this.f4074d, !this.j, i2);
    }

    public void destroyItem(ViewGroup viewGroup, int i2, Object obj) {
        if (obj instanceof View) {
            View view = (View) obj;
            if (view instanceof ImageView) {
                Utils.a((ImageView) view);
            } else if (view instanceof ViewGroup) {
                Utils.a((ViewGroup) view);
            }
            Utils.b(view);
        }
    }

    public /* synthetic */ void e(int i2) {
        a(this.f4074d, this.j, i2);
    }

    public b f(int i2) {
        Object obj;
        if (!h(i2)) {
            C0397c cVar = this.f4072b.get(i2);
            if (cVar == null) {
                return null;
            }
            obj = this.f4071a.get(a(cVar.b()));
        } else if (!this.f4071a.containsKey((Object) null)) {
            return null;
        } else {
            obj = this.f4071a.get((Object) null);
        }
        return (b) obj;
    }

    public int getCount() {
        List<C0397c> list = this.f4072b;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public int getItemPosition(Object obj) {
        return -2;
    }

    public boolean handleMessage(Message message) {
        if (message.what != 129) {
            return false;
        }
        a(message.obj);
        return false;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i2) {
        b bVar;
        String str;
        C0397c cVar = null;
        if (h(i2)) {
            bVar = this.f4071a.get((Object) null);
            str = null;
        } else {
            C0397c cVar2 = this.f4072b.get(i2);
            if (cVar2 != null) {
                str = a(cVar2.b());
                bVar = this.f4071a.containsKey(str) ? this.f4071a.get(str) : null;
            } else {
                str = null;
                bVar = null;
            }
        }
        com.miui.gamebooster.globalgame.util.b.c("in Adapter of position:" + i2 + ",pkgName:" + str + ",data:" + this.f4072b.get(i2));
        if (bVar == null) {
            View inflate = LayoutInflater.from(this.f4073c).inflate(R.layout.gb_game_gallery, viewGroup, false);
            b bVar2 = new b();
            bVar2.a(inflate);
            n.a(bVar2.f4075a, bVar2.f4077c);
            this.f4071a.put(str, bVar2);
            bVar = bVar2;
        }
        Utils.b(bVar.f4075a);
        viewGroup.addView(bVar.f4075a);
        if (TextUtils.isEmpty(str)) {
            bVar.f4077c.setImageResource(this.h ? R.drawable.gb_add_game_big_square : R.drawable.gb_add_game_big_rectangle);
        } else {
            cVar = this.f4072b.get(i2);
            ApplicationInfo b2 = cVar.b();
            a(bVar.f4077c, b2.packageName, b(b2));
        }
        Utils.a((Runnable) new C0327e(this, cVar, i2), bVar.f4077c);
        Utils.a((Runnable) new lambda(this, i2), bVar.f4076b);
        Utils.a((Runnable) new C0326d(this, i2), bVar.f4078d);
        return bVar.f4075a;
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }
}
