package b.b.b.c;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.OvershootInterpolator;
import b.b.b.a.b;
import com.miui.antivirus.result.N;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.securitycenter.R;
import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import miui.util.IOUtils;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private static final Uri f1491a = Uri.parse("content://com.miui.voiceassist.xiaoai.manager.provider/ui/appearance_status");

    /* renamed from: b  reason: collision with root package name */
    private Context f1492b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public View f1493c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public N f1494d;
    private AnimatorSet e;
    private AnimatorSet f;
    /* access modifiers changed from: private */
    public boolean g;
    /* access modifiers changed from: private */
    public boolean h;
    /* access modifiers changed from: private */
    public AtomicBoolean i = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public AtomicLong j = new AtomicLong(0);
    /* access modifiers changed from: private */
    public boolean k = true;
    /* access modifiers changed from: private */
    public a l;
    private ContentObserver m = new b(this, new Handler());

    private static class a extends Handler {

        /* renamed from: a  reason: collision with root package name */
        private WeakReference<e> f1495a;

        public a(e eVar) {
            this.f1495a = new WeakReference<>(eVar);
        }

        public void handleMessage(Message message) {
            e eVar;
            if (message.what == 1 && (eVar = (e) this.f1495a.get()) != null) {
                Log.d("SidekickHelper", "Timed Dismiss Sidekick Guide");
                eVar.b();
            }
        }
    }

    public e(Context context) {
        this.f1492b = context;
    }

    public static void a(Context context) {
        Log.d("SidekickHelper", "sendExitOpToSidekick: Exit Sidekick");
        Intent intent = new Intent();
        intent.setAction("com.miui.voiceassist.query.exit");
        intent.putExtra("voice_assist_start_from_key", "com.miui.securitycenter.security_scan");
        intent.setClassName("com.miui.voiceassist", "com.xiaomi.voiceassistant.SpeechQueryService");
        context.startService(intent);
    }

    public static void a(Context context, String str) {
        Log.d("SidekickHelper", "sendQueryToSidekick: Call Sidekick");
        b.a.b();
        Intent intent = new Intent();
        intent.setAction("com.miui.voiceassist.query");
        intent.putExtra("voice_assist_start_from_key", "com.miui.securitycenter.security_scan");
        intent.putExtra("assist_query", str);
        intent.putExtra("assist_text_shown", 1);
        intent.setClassName("com.miui.voiceassist", "com.xiaomi.voiceassistant.SpeechQueryService");
        context.startService(intent);
    }

    /* access modifiers changed from: private */
    public boolean j() {
        boolean z = false;
        Cursor cursor = null;
        try {
            cursor = this.f1492b.getContentResolver().query(f1491a, (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor != null && cursor.moveToFirst()) {
                z = "show".equals(cursor.getString(cursor.getColumnIndex("appearance_status")));
            }
        } catch (Exception e2) {
            Log.e("SidekickHelper", "Sidekick provider error: " + e2);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        return z;
    }

    public void a() {
        AnimatorSet animatorSet = this.f;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.e;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.l.removeMessages(1);
    }

    public void a(boolean z) {
        this.k = z;
    }

    public boolean a(View view, N n) {
        if (view == null || n == null) {
            return false;
        }
        this.f1493c = ((ViewStub) view.findViewById(R.id.view_stub_sidekick)).inflate();
        this.f1494d = n;
        this.l = new a(this);
        return this.f1493c != null;
    }

    public void b() {
        this.l.removeMessages(1);
        if (e()) {
            AnimatorSet animatorSet = this.e;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            if (this.f == null) {
                this.f = new AnimatorSet();
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f1493c, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{1.0f, 0.0f});
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.f1493c, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{1.0f, 0.9f});
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.f1493c, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{1.0f, 0.9f});
                this.f.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3});
                this.f.setDuration(300);
                this.f.addListener(new d(this));
                this.f.start();
            }
        }
    }

    public void c() {
        if (this.f1492b != null) {
            if (f() && (this.i.get() || this.j.get() == 0)) {
                a(this.f1492b.getApplicationContext());
            }
            this.i.set(false);
            this.h = false;
        }
    }

    public boolean d() {
        return this.i.get() || System.currentTimeMillis() - this.j.get() < 600;
    }

    public boolean e() {
        View view = this.f1493c;
        return view != null && view.getVisibility() == 0;
    }

    public boolean f() {
        return this.h;
    }

    public void g() {
        Context context = this.f1492b;
        if (context != null) {
            try {
                context.getContentResolver().registerContentObserver(f1491a, true, this.m);
            } catch (Exception e2) {
                Log.e("SidekickHelper", e2.toString());
            }
        }
    }

    public boolean h() {
        View view = this.f1493c;
        if (view == null || this.f1494d == null) {
            return false;
        }
        view.setVisibility(0);
        this.f1493c.findViewById(R.id.sidekick_click_area).setOnClickListener(new c(this));
        this.f1494d.a(this.f1493c);
        b.a.c();
        AnimatorSet animatorSet = this.e;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = this.f;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.f = null;
        }
        this.e = new AnimatorSet();
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.f1493c, AnimatedProperty.PROPERTY_NAME_ALPHA, new float[]{0.0f, 1.0f});
        ofFloat.setDuration(200);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.f1493c, AnimatedProperty.PROPERTY_NAME_SCALE_X, new float[]{0.9f, 1.0f});
        ofFloat2.setDuration(400);
        ofFloat2.setInterpolator(new OvershootInterpolator());
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.f1493c, AnimatedProperty.PROPERTY_NAME_SCALE_Y, new float[]{0.9f, 1.0f});
        ofFloat3.setDuration(400);
        ofFloat3.setInterpolator(new OvershootInterpolator());
        this.e.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3});
        this.e.start();
        g.a(this.f1492b).a(System.currentTimeMillis());
        this.l.sendEmptyMessageDelayed(1, (long) (this.f1494d.h() * 1000));
        return true;
    }

    public void i() {
        Context context = this.f1492b;
        if (context != null) {
            try {
                context.getContentResolver().unregisterContentObserver(this.m);
            } catch (Exception e2) {
                Log.e("SidekickHelper", e2.toString());
            }
        }
    }
}
