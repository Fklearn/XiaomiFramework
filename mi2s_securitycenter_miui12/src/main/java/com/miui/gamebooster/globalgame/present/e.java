package com.miui.gamebooster.globalgame.present;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import b.b.c.j.r;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.miui.gamebooster.globalgame.http.Result;
import com.miui.gamebooster.globalgame.module.BannerCardBean;
import com.miui.gamebooster.globalgame.module.CardType;
import com.miui.gamebooster.globalgame.module.a;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.globalgame.util.d;
import com.miui.gamebooster.i.a.c;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.viewPointwidget.b;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;

public class e implements b, Handler.Callback {

    /* renamed from: a  reason: collision with root package name */
    private static a[] f4406a;

    /* renamed from: b  reason: collision with root package name */
    private h f4407b;

    /* renamed from: c  reason: collision with root package name */
    private Handler f4408c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f4409d;
    private CountDownLatch e = new CountDownLatch(1);
    private AtomicBoolean f = new AtomicBoolean(false);
    private boolean g = false;
    private int h;
    private int i = 0;
    private Context j;

    public e(Context context, h hVar) {
        this.j = context;
        this.f4407b = hVar;
        this.f4408c = new Handler(Looper.myLooper(), this);
        Utils.q();
        this.h = na.b();
    }

    @UiThread
    public static a a(BannerCardBean bannerCardBean, @CardType.Type int i2) {
        if (f4406a == null) {
            d();
        }
        if (i2 == 8 && !com.market.sdk.utils.b.a(bannerCardBean.getGameList())) {
            if (bannerCardBean.getGameList().size() >= 17) {
                i2 = 11;
            } else if (bannerCardBean.getGameList().size() >= 5) {
                i2 = 10;
            }
        }
        return f4406a[Math.min(i2, 12)];
    }

    private boolean a(String str) {
        Result result;
        com.miui.gamebooster.globalgame.util.b.c("resultProcess:" + str);
        if (TextUtils.isEmpty(str) || (result = (Result) d.a(str, Result.class)) == null) {
            return false;
        }
        try {
            JSONArray jSONArray = new JSONArray(com.miui.gamebooster.globalgame.http.a.a(result.getHead().getTime().longValue(), result.getData()));
            ArrayList arrayList = new ArrayList();
            boolean z = false;
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                BannerCardBean bannerCardBean = new BannerCardBean(jSONArray.getJSONObject(i2));
                if (!bannerCardBean.unjson) {
                    if (!bannerCardBean.invalidData()) {
                        if (!b(bannerCardBean, i2)) {
                            if (arrayList.size() == 0) {
                                bannerCardBean.isFirst = true;
                            }
                            if (i2 == jSONArray.length() - 1) {
                                bannerCardBean.isLast = true;
                            }
                            if (!z && bannerCardBean.type == 3 && !TextUtils.isEmpty(bannerCardBean.getCover())) {
                                bannerCardBean.loadedBitmap = r.a(bannerCardBean.getCover(), new b.c.a.b.a.e(this.h, (int) (((float) this.h) * 0.65f)));
                                z = true;
                            }
                            arrayList.add(bannerCardBean);
                        }
                    }
                }
            }
            Message message = new Message();
            message.what = 0;
            message.obj = arrayList;
            this.f4408c.sendMessage(message);
            return true;
        } catch (Exception e2) {
            c.a();
            e2.printStackTrace();
            com.miui.gamebooster.globalgame.util.b.b(e2);
            return false;
        }
    }

    private boolean b(BannerCardBean bannerCardBean, int i2) {
        return i2 == 0 && bannerCardBean.type == 9;
    }

    private static void d() {
        f4406a = new a[]{new UserGuide(), new HorizontalList(), new VerticalList(), new BigPost(), new SmallPost(), new PureImage(), null, null, new H5OneRowList(), new PureTitle(), new H5TwoRowList(), new H5TorrentList()};
    }

    /* access modifiers changed from: private */
    public void e() {
        boolean a2 = a(C0382m.b("gamebooster", "feed_cache_file_name.bk", com.miui.gamebooster.globalgame.util.a.a()));
        if (!a2) {
            Utils.r();
        }
        this.f.set(a2);
        this.e.countDown();
    }

    /* access modifiers changed from: private */
    public void f() {
        String str = "";
        do {
            int i2 = this.i;
            this.i = i2 + 1;
            if (i2 > 4) {
                break;
            }
            str = com.miui.gamebooster.globalgame.http.a.a("https://api.accelerator.intl.miui.com/game/accelerator/booster/game/feed");
            com.miui.gamebooster.globalgame.util.b.c("netRequestProcess for try of :" + this.i + ",result:" + str);
        } while (TextUtils.isEmpty(str));
        boolean isEmpty = TextUtils.isEmpty(str);
        com.miui.gamebooster.i.a.b.a(this.j, !isEmpty);
        if (!isEmpty) {
            try {
                this.e.await(AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS, TimeUnit.MILLISECONDS);
                if (!this.f.get()) {
                    a(str);
                }
                C0382m.a("gamebooster", "feed_cache_file_name.bk", str, com.miui.gamebooster.globalgame.util.a.a());
                Utils.s();
            } catch (Exception e2) {
                com.miui.gamebooster.globalgame.util.b.b(e2);
            }
        }
    }

    private void g() {
        if (!this.f4409d) {
            if (!Utils.m()) {
                b.b.c.c.a.a.a(new b(this));
            } else {
                this.e.countDown();
            }
            b.b.c.c.a.a.a(new a(this));
        }
    }

    public void a() {
    }

    public void b() {
        this.g = false;
        g();
    }

    public float c() {
        return ((float) this.h) * 0.65f;
    }

    public boolean handleMessage(Message message) {
        if (!this.g && message.what == 0) {
            this.f4409d = true;
            Object obj = message.obj;
            if (obj instanceof List) {
                List list = (List) obj;
                if (com.market.sdk.utils.b.a(list)) {
                    com.miui.gamebooster.globalgame.util.b.b("empty data");
                } else {
                    this.f4407b.a(list);
                }
            }
        }
        return false;
    }

    public void onPause() {
    }

    public void onStop() {
        this.g = true;
        this.f4408c.removeCallbacksAndMessages((Object) null);
    }
}
