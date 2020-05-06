package com.miui.applicationlock.c;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.gamebooster.globalgame.view.RoundedDrawable;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.lang.ref.SoftReference;
import miui.content.res.ThemeResources;
import miui.graphics.BitmapFactory;

public class s {

    /* renamed from: a  reason: collision with root package name */
    private static s f3322a = new s();
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public SoftReference<BitmapDrawable> f3323b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public b f3324c;

    private class a extends AsyncTask<Void, Void, BitmapDrawable> {
        private a() {
        }

        /* synthetic */ a(s sVar, r rVar) {
            this();
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public BitmapDrawable doInBackground(Void... voidArr) {
            Application d2 = Application.d();
            s sVar = s.this;
            Bitmap a2 = sVar.a(sVar.c());
            if (a2 == null) {
                Log.i("BackgroundManager", "wallpaper is null.");
                return null;
            }
            ColorMatrix colorMatrix = new ColorMatrix();
            colorMatrix.setSaturation(1.3f);
            Canvas canvas = new Canvas(a2);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            Paint paint2 = new Paint();
            paint2.setColor(RoundedDrawable.DEFAULT_BORDER_COLOR);
            paint2.setAlpha(100);
            canvas.drawBitmap(a2, 0.0f, 0.0f, paint);
            canvas.drawRect(0.0f, 0.0f, (float) a2.getWidth(), (float) a2.getHeight(), paint2);
            canvas.save();
            canvas.restore();
            return new BitmapDrawable(d2.getResources(), a2);
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public void onPostExecute(BitmapDrawable bitmapDrawable) {
            SoftReference unused = s.this.f3323b = new SoftReference(bitmapDrawable);
            if (s.this.f3324c != null) {
                s.this.f3324c.onRequestBgResult(bitmapDrawable);
            }
        }
    }

    public interface b {
        void onRequestBgResult(BitmapDrawable bitmapDrawable);
    }

    private s() {
        Application.d().registerReceiver(new r(this), new IntentFilter("com.miui.keyguard.setwallpaper"));
    }

    /* access modifiers changed from: private */
    public Bitmap a(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return BitmapFactory.fastBlur(Bitmap.createScaledBitmap(bitmap, 100, 200, false), Application.d().getResources().getDimensionPixelSize(R.dimen.applock_blur_dimen_radius));
    }

    public static synchronized s b() {
        s sVar;
        synchronized (s.class) {
            sVar = f3322a;
        }
        return sVar;
    }

    /* access modifiers changed from: private */
    public Bitmap c() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) ThemeResources.getLockWallpaperCache(Application.d());
        if (bitmapDrawable == null) {
            return null;
        }
        Bitmap bitmap = bitmapDrawable.getBitmap();
        ThemeResources.clearLockWallpaperCache();
        return bitmap;
    }

    public void a() {
        new a(this, (r) null).execute(new Void[0]);
    }

    public void a(b bVar) {
        if (bVar != null) {
            this.f3324c = bVar;
            SoftReference<BitmapDrawable> softReference = this.f3323b;
            if (softReference == null || softReference.get() == null) {
                a();
            } else {
                bVar.onRequestBgResult(this.f3323b.get());
            }
        }
    }
}
