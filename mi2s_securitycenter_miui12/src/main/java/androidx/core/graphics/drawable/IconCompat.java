package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.versionedparcelable.CustomVersionedParcelable;
import com.google.android.exoplayer2.C;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

public class IconCompat extends CustomVersionedParcelable {

    /* renamed from: a  reason: collision with root package name */
    static final PorterDuff.Mode f720a = PorterDuff.Mode.SRC_IN;
    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})

    /* renamed from: b  reason: collision with root package name */
    public int f721b = -1;

    /* renamed from: c  reason: collision with root package name */
    Object f722c;
    @RestrictTo({RestrictTo.a.LIBRARY})

    /* renamed from: d  reason: collision with root package name */
    public byte[] f723d = null;
    @RestrictTo({RestrictTo.a.LIBRARY})
    public Parcelable e = null;
    @RestrictTo({RestrictTo.a.LIBRARY})
    public int f = 0;
    @RestrictTo({RestrictTo.a.LIBRARY})
    public int g = 0;
    @RestrictTo({RestrictTo.a.LIBRARY})
    public ColorStateList h = null;
    PorterDuff.Mode i = f720a;
    @RestrictTo({RestrictTo.a.LIBRARY})
    public String j = null;

    @RestrictTo({RestrictTo.a.LIBRARY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IconType {
    }

    @RequiresApi(23)
    @IdRes
    @DrawableRes
    private static int a(@NonNull Icon icon) {
        if (Build.VERSION.SDK_INT >= 28) {
            return icon.getResId();
        }
        try {
            return ((Integer) icon.getClass().getMethod("getResId", new Class[0]).invoke(icon, new Object[0])).intValue();
        } catch (IllegalAccessException e2) {
            Log.e("IconCompat", "Unable to get icon resource", e2);
            return 0;
        } catch (InvocationTargetException e3) {
            Log.e("IconCompat", "Unable to get icon resource", e3);
            return 0;
        } catch (NoSuchMethodException e4) {
            Log.e("IconCompat", "Unable to get icon resource", e4);
            return 0;
        }
    }

    private static String a(int i2) {
        return i2 != 1 ? i2 != 2 ? i2 != 3 ? i2 != 4 ? i2 != 5 ? "UNKNOWN" : "BITMAP_MASKABLE" : "URI" : "DATA" : "RESOURCE" : "BITMAP";
    }

    @RequiresApi(23)
    @Nullable
    private static String b(@NonNull Icon icon) {
        if (Build.VERSION.SDK_INT >= 28) {
            return icon.getResPackage();
        }
        try {
            return (String) icon.getClass().getMethod("getResPackage", new Class[0]).invoke(icon, new Object[0]);
        } catch (IllegalAccessException e2) {
            Log.e("IconCompat", "Unable to get icon package", e2);
            return null;
        } catch (InvocationTargetException e3) {
            Log.e("IconCompat", "Unable to get icon package", e3);
            return null;
        } catch (NoSuchMethodException e4) {
            Log.e("IconCompat", "Unable to get icon package", e4);
            return null;
        }
    }

    @IdRes
    public int a() {
        if (this.f721b == -1 && Build.VERSION.SDK_INT >= 23) {
            return a((Icon) this.f722c);
        }
        if (this.f721b == 2) {
            return this.f;
        }
        throw new IllegalStateException("called getResId() on " + this);
    }

    public void a(boolean z) {
        byte[] byteArray;
        String str;
        this.j = this.i.name();
        int i2 = this.f721b;
        if (i2 != -1) {
            if (i2 != 1) {
                if (i2 == 2) {
                    str = (String) this.f722c;
                } else if (i2 == 3) {
                    byteArray = (byte[]) this.f722c;
                    this.f723d = byteArray;
                    return;
                } else if (i2 == 4) {
                    str = this.f722c.toString();
                } else if (i2 != 5) {
                    return;
                }
                byteArray = str.getBytes(Charset.forName(C.UTF16_NAME));
                this.f723d = byteArray;
                return;
            }
            if (z) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ((Bitmap) this.f722c).compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                this.f723d = byteArray;
                return;
            }
        } else if (z) {
            throw new IllegalArgumentException("Can't serialize Icon created with IconCompat#createFromIcon");
        }
        this.e = (Parcelable) this.f722c;
    }

    @NonNull
    public String b() {
        if (this.f721b == -1 && Build.VERSION.SDK_INT >= 23) {
            return b((Icon) this.f722c);
        }
        if (this.f721b == 2) {
            return ((String) this.f722c).split(":", -1)[0];
        }
        throw new IllegalStateException("called getResPackage() on " + this);
    }

    public void c() {
        Object obj;
        this.i = PorterDuff.Mode.valueOf(this.j);
        int i2 = this.f721b;
        if (i2 != -1) {
            if (i2 != 1) {
                if (i2 != 2) {
                    if (i2 == 3) {
                        obj = this.f723d;
                    } else if (i2 != 4) {
                        if (i2 != 5) {
                            return;
                        }
                    }
                }
                obj = new String(this.f723d, Charset.forName(C.UTF16_NAME));
            }
            obj = this.e;
            if (obj == null) {
                byte[] bArr = this.f723d;
                this.f722c = bArr;
                this.f721b = 3;
                this.f = 0;
                this.g = bArr.length;
                return;
            }
        } else {
            obj = this.e;
            if (obj == null) {
                throw new IllegalArgumentException("Invalid icon");
            }
        }
        this.f722c = obj;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002b, code lost:
        if (r1 != 5) goto L_0x0097;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00ab  */
    @androidx.annotation.NonNull
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String toString() {
        /*
            r4 = this;
            int r0 = r4.f721b
            r1 = -1
            if (r0 != r1) goto L_0x000c
            java.lang.Object r0 = r4.f722c
            java.lang.String r0 = java.lang.String.valueOf(r0)
            return r0
        L_0x000c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = "Icon(typ="
            r0.<init>(r1)
            int r1 = r4.f721b
            java.lang.String r1 = a((int) r1)
            r0.append(r1)
            int r1 = r4.f721b
            r2 = 1
            if (r1 == r2) goto L_0x0077
            r3 = 2
            if (r1 == r3) goto L_0x004f
            r2 = 3
            if (r1 == r2) goto L_0x0039
            r2 = 4
            if (r1 == r2) goto L_0x002e
            r2 = 5
            if (r1 == r2) goto L_0x0077
            goto L_0x0097
        L_0x002e:
            java.lang.String r1 = " uri="
            r0.append(r1)
            java.lang.Object r1 = r4.f722c
            r0.append(r1)
            goto L_0x0097
        L_0x0039:
            java.lang.String r1 = " len="
            r0.append(r1)
            int r1 = r4.f
            r0.append(r1)
            int r1 = r4.g
            if (r1 == 0) goto L_0x0097
            java.lang.String r1 = " off="
            r0.append(r1)
            int r1 = r4.g
            goto L_0x0094
        L_0x004f:
            java.lang.String r1 = " pkg="
            r0.append(r1)
            java.lang.String r1 = r4.b()
            r0.append(r1)
            java.lang.String r1 = " id="
            r0.append(r1)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            int r3 = r4.a()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1[r2] = r3
            java.lang.String r2 = "0x%08x"
            java.lang.String r1 = java.lang.String.format(r2, r1)
            r0.append(r1)
            goto L_0x0097
        L_0x0077:
            java.lang.String r1 = " size="
            r0.append(r1)
            java.lang.Object r1 = r4.f722c
            android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1
            int r1 = r1.getWidth()
            r0.append(r1)
            java.lang.String r1 = "x"
            r0.append(r1)
            java.lang.Object r1 = r4.f722c
            android.graphics.Bitmap r1 = (android.graphics.Bitmap) r1
            int r1 = r1.getHeight()
        L_0x0094:
            r0.append(r1)
        L_0x0097:
            android.content.res.ColorStateList r1 = r4.h
            if (r1 == 0) goto L_0x00a5
            java.lang.String r1 = " tint="
            r0.append(r1)
            android.content.res.ColorStateList r1 = r4.h
            r0.append(r1)
        L_0x00a5:
            android.graphics.PorterDuff$Mode r1 = r4.i
            android.graphics.PorterDuff$Mode r2 = f720a
            if (r1 == r2) goto L_0x00b5
            java.lang.String r1 = " mode="
            r0.append(r1)
            android.graphics.PorterDuff$Mode r1 = r4.i
            r0.append(r1)
        L_0x00b5:
            java.lang.String r1 = ")"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.core.graphics.drawable.IconCompat.toString():java.lang.String");
    }
}
