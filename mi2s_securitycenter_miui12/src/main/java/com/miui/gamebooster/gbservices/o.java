package com.miui.gamebooster.gbservices;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Parcel;
import android.provider.Settings;
import b.b.c.j.y;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.e.c;
import com.miui.gamebooster.m.C0370a;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.C0391w;
import com.miui.gamebooster.service.r;
import com.miui.maml.folme.AnimatedPropertyType;
import java.io.Closeable;
import miui.cloud.os.ServiceManager;
import miui.util.IOUtils;
import miui.util.Log;

public class o extends m {

    /* renamed from: a  reason: collision with root package name */
    private Context f4367a;

    /* renamed from: b  reason: collision with root package name */
    private r f4368b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f4369c;

    /* renamed from: d  reason: collision with root package name */
    private int f4370d = -1;
    private int e = -1;
    private int f = -1;
    private int g = -1;
    private boolean h;
    private String i;
    private String j;
    private int k;
    private int l;

    public o(Context context, r rVar) {
        this.f4367a = context;
        this.f4368b = rVar;
        Object b2 = C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE");
        if (b2 != null) {
            this.i = (String) b2;
        }
        Object b3 = C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_HDR_LEVEL");
        if (b3 != null) {
            this.j = (String) b3;
        }
        Object b4 = C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE_ENABLE_HDR");
        if (b4 != null) {
            this.k = ((Integer) b4).intValue();
        }
        Object b5 = C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE_DISABLE_EYECARE");
        if (b5 != null) {
            this.l = ((Integer) b5).intValue();
        }
    }

    private void a(int i2) {
        IBinder service = ServiceManager.getService("SurfaceFlinger");
        Log.i("CustomizedService", "updateHdrNew: flinger=" + service + "\t value=" + i2);
        if (service != null) {
            Parcel obtain = Parcel.obtain();
            try {
                obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                obtain.writeString(this.f4368b.a());
                obtain.writeInt(i2);
                service.transact(AnimatedPropertyType.RESERVE_FLOAT_4, obtain, (Parcel) null, 0);
            } catch (Exception e2) {
                Log.e("CustomizedService", "Failed to notifySurfaceFlinger", e2);
            } catch (Throwable th) {
                obtain.recycle();
                throw th;
            }
            obtain.recycle();
        }
    }

    private void a(String str, int i2) {
        Log.d("CustomizedService", "loadFeatureFromDB : packageUid = " + i2 + " , boosterPkgName = " + str);
        Cursor cursor = null;
        try {
            cursor = C0391w.a(this.f4367a.getApplicationContext(), str, 0, i2);
            if (cursor != null && cursor.moveToFirst()) {
                this.f4370d = cursor.getInt(cursor.getColumnIndex("settings_gs"));
                this.e = cursor.getInt(cursor.getColumnIndex("settings_ts"));
                this.f = cursor.getInt(cursor.getColumnIndex("settings_edge"));
                this.g = cursor.getInt(cursor.getColumnIndex("settings_hdr"));
                boolean z = true;
                if (cursor.getInt(cursor.getColumnIndex("settings_4d")) != 1) {
                    z = false;
                }
                this.h = z;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        } catch (Throwable th) {
            IOUtils.closeQuietly((Closeable) null);
            throw th;
        }
        IOUtils.closeQuietly(cursor);
        Log.d("CustomizedService", "loadFeatureFromDB gsDBValue = " + this.f4370d + " ,  tsDBValue = " + this.e + " ,  edgeDBValue = " + this.f + "  ,hdrDBValue = " + this.g + "  , is4dSupported = " + this.h);
    }

    private void a(boolean z) {
        if (c.b()) {
            Log.i("CustomizedService", "setAudioParameters is4dSupported : " + this.h);
            if (!z || (z && this.h)) {
                try {
                    AudioManager audioManager = (AudioManager) this.f4367a.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
                    StringBuilder sb = new StringBuilder();
                    sb.append("audio_game_4D_switch=");
                    String str = "on";
                    sb.append(z ? str : "off");
                    audioManager.setParameters(sb.toString());
                    C0384o.b(this.f4367a.getContentResolver(), "audio_game_4d", z ? 1 : 0, -2);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("audio_game_4D_switch=");
                    if (!z) {
                        str = "off";
                    }
                    sb2.append(str);
                    Log.i("CustomizedService", sb2.toString());
                } catch (Exception e2) {
                    Log.i("GameBoosterService", e2.toString());
                }
            }
        }
    }

    private boolean f() {
        return y.a("ro.vendor.gcp.enable", false);
    }

    private void g() {
        if (this.i != null) {
            Settings.System.putInt(this.f4367a.getContentResolver(), this.i, 0);
        }
        if (f()) {
            a(0);
        }
    }

    private void h() {
        C0370a a2 = C0370a.a();
        C0370a.a();
        a2.d(C0370a.f4468a);
    }

    private void i() {
        int i2;
        int i3;
        String str;
        ContentResolver contentResolver;
        Log.i("CustomizedService", "setHDRFeature hdrValue : " + this.g);
        boolean s = a.s(false);
        int i4 = this.g;
        if (i4 == -1 || i4 == 0) {
            if (C0388t.h() && this.i != null) {
                Settings.System.putInt(this.f4367a.getContentResolver(), this.i, 0);
            }
            if (s && this.i != null) {
                Settings.System.putInt(this.f4367a.getContentResolver(), this.i, this.l);
            }
            if (f() && (i2 = this.g) == 0) {
                a(i2);
                return;
            }
            return;
        }
        if (!f()) {
            Log.i("CustomizedService", "updateHdrOld: hdr=" + this.g);
            if (this.i != null && this.j != null) {
                Settings.System.putInt(this.f4367a.getContentResolver(), this.i, 0);
                Settings.System.putInt(this.f4367a.getContentResolver(), this.j, this.g);
                contentResolver = this.f4367a.getContentResolver();
                str = this.i;
                int i5 = this.k;
                i3 = (s ? this.l : i5) | i5;
            } else {
                return;
            }
        } else {
            a(this.g);
            if (s && this.i != null) {
                contentResolver = this.f4367a.getContentResolver();
                str = this.i;
                i3 = this.l;
            } else {
                return;
            }
        }
        Settings.System.putInt(contentResolver, str, i3);
    }

    private void j() {
        C0370a a2 = C0370a.a();
        C0370a.a();
        int a3 = a2.a(C0370a.f4470c);
        C0370a a4 = C0370a.a();
        C0370a.a();
        int i2 = C0370a.f4470c;
        int i3 = this.f4370d;
        if (i3 == -1) {
            i3 = a3;
        }
        a4.a(i2, i3);
        C0370a a5 = C0370a.a();
        C0370a.a();
        int a6 = a5.a(C0370a.f4471d);
        C0370a a7 = C0370a.a();
        C0370a.a();
        int i4 = C0370a.f4471d;
        int i5 = this.e;
        if (i5 == -1) {
            i5 = a6;
        }
        a7.a(i4, i5);
        C0370a a8 = C0370a.a();
        C0370a.a();
        int a9 = a8.a(C0370a.e);
        C0370a a10 = C0370a.a();
        C0370a.a();
        int i6 = C0370a.e;
        int i7 = this.f;
        if (i7 == -1) {
            i7 = a9;
        }
        a10.a(i6, i7);
        Log.d("CustomizedService", "setITouchFeature gsDefaultValue = " + a3 + " ,  tsDefaultValue = " + a6 + " ,  edgeDefaultValue = " + a9);
    }

    public void a() {
        if (this.f4369c) {
            if (C0388t.q()) {
                h();
            }
            if (C0388t.h()) {
                g();
            }
            a(false);
        }
    }

    public boolean b() {
        return C0388t.d();
    }

    public void c() {
        if (this.f4369c) {
            if (C0388t.q()) {
                C0370a.a().a(C0370a.f4468a, 1);
                C0370a.a().a(C0370a.f4469b, 1);
            }
            a(this.f4368b.a(), this.f4368b.e());
            if (C0388t.q()) {
                j();
            }
            if (C0388t.h()) {
                i();
            }
            a(true);
        }
    }

    public void d() {
        this.f4369c = true;
    }

    public int e() {
        return 8;
    }
}
