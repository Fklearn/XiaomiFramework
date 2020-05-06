package androidx.versionedparcelable;

import android.os.Parcelable;
import androidx.annotation.RestrictTo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
public abstract class b {

    /* renamed from: a  reason: collision with root package name */
    protected final a.c.b<String, Method> f1279a;

    /* renamed from: b  reason: collision with root package name */
    protected final a.c.b<String, Method> f1280b;

    /* renamed from: c  reason: collision with root package name */
    protected final a.c.b<String, Class> f1281c;

    public b(a.c.b<String, Method> bVar, a.c.b<String, Method> bVar2, a.c.b<String, Class> bVar3) {
        this.f1279a = bVar;
        this.f1280b = bVar2;
        this.f1281c = bVar3;
    }

    private Class a(Class<? extends d> cls) {
        Class cls2 = this.f1281c.get(cls.getName());
        if (cls2 != null) {
            return cls2;
        }
        Class<?> cls3 = Class.forName(String.format("%s.%sParcelizer", new Object[]{cls.getPackage().getName(), cls.getSimpleName()}), false, cls.getClassLoader());
        this.f1281c.put(cls.getName(), cls3);
        return cls3;
    }

    private Method b(Class cls) {
        Method method = this.f1280b.get(cls.getName());
        if (method != null) {
            return method;
        }
        Class a2 = a((Class<? extends d>) cls);
        System.currentTimeMillis();
        Method declaredMethod = a2.getDeclaredMethod("write", new Class[]{cls, b.class});
        this.f1280b.put(cls.getName(), declaredMethod);
        return declaredMethod;
    }

    private Method b(String str) {
        Method method = this.f1279a.get(str);
        if (method != null) {
            return method;
        }
        System.currentTimeMillis();
        Method declaredMethod = Class.forName(str, true, b.class.getClassLoader()).getDeclaredMethod("read", new Class[]{b.class});
        this.f1279a.put(str, declaredMethod);
        return declaredMethod;
    }

    private void b(d dVar) {
        try {
            a(a((Class<? extends d>) dVar.getClass()).getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(dVar.getClass().getSimpleName() + " does not have a Parcelizer", e);
        }
    }

    public int a(int i, int i2) {
        return !a(i2) ? i : g();
    }

    public <T extends Parcelable> T a(T t, int i) {
        return !a(i) ? t : h();
    }

    public <T extends d> T a(T t, int i) {
        return !a(i) ? t : j();
    }

    /* access modifiers changed from: protected */
    public <T extends d> T a(String str, b bVar) {
        try {
            return (d) b(str).invoke((Object) null, new Object[]{bVar});
        } catch (IllegalAccessException e) {
            throw new RuntimeException("VersionedParcel encountered IllegalAccessException", e);
        } catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e2.getCause());
            }
            throw new RuntimeException("VersionedParcel encountered InvocationTargetException", e2);
        } catch (NoSuchMethodException e3) {
            throw new RuntimeException("VersionedParcel encountered NoSuchMethodException", e3);
        } catch (ClassNotFoundException e4) {
            throw new RuntimeException("VersionedParcel encountered ClassNotFoundException", e4);
        }
    }

    public CharSequence a(CharSequence charSequence, int i) {
        return !a(i) ? charSequence : f();
    }

    public String a(String str, int i) {
        return !a(i) ? str : i();
    }

    /* access modifiers changed from: protected */
    public abstract void a();

    /* access modifiers changed from: protected */
    public abstract void a(Parcelable parcelable);

    /* access modifiers changed from: protected */
    public void a(d dVar) {
        if (dVar == null) {
            a((String) null);
            return;
        }
        b(dVar);
        b b2 = b();
        a(dVar, b2);
        b2.a();
    }

    /* access modifiers changed from: protected */
    public <T extends d> void a(T t, b bVar) {
        try {
            b((Class) t.getClass()).invoke((Object) null, new Object[]{t, bVar});
        } catch (IllegalAccessException e) {
            throw new RuntimeException("VersionedParcel encountered IllegalAccessException", e);
        } catch (InvocationTargetException e2) {
            if (e2.getCause() instanceof RuntimeException) {
                throw ((RuntimeException) e2.getCause());
            }
            throw new RuntimeException("VersionedParcel encountered InvocationTargetException", e2);
        } catch (NoSuchMethodException e3) {
            throw new RuntimeException("VersionedParcel encountered NoSuchMethodException", e3);
        } catch (ClassNotFoundException e4) {
            throw new RuntimeException("VersionedParcel encountered ClassNotFoundException", e4);
        }
    }

    /* access modifiers changed from: protected */
    public abstract void a(CharSequence charSequence);

    /* access modifiers changed from: protected */
    public abstract void a(String str);

    /* access modifiers changed from: protected */
    public abstract void a(boolean z);

    public void a(boolean z, boolean z2) {
    }

    /* access modifiers changed from: protected */
    public abstract void a(byte[] bArr);

    /* access modifiers changed from: protected */
    public abstract boolean a(int i);

    public boolean a(boolean z, int i) {
        return !a(i) ? z : d();
    }

    public byte[] a(byte[] bArr, int i) {
        return !a(i) ? bArr : e();
    }

    /* access modifiers changed from: protected */
    public abstract b b();

    /* access modifiers changed from: protected */
    public abstract void b(int i);

    public void b(int i, int i2) {
        b(i2);
        c(i);
    }

    public void b(Parcelable parcelable, int i) {
        b(i);
        a(parcelable);
    }

    public void b(d dVar, int i) {
        b(i);
        a(dVar);
    }

    public void b(CharSequence charSequence, int i) {
        b(i);
        a(charSequence);
    }

    public void b(String str, int i) {
        b(i);
        a(str);
    }

    public void b(boolean z, int i) {
        b(i);
        a(z);
    }

    public void b(byte[] bArr, int i) {
        b(i);
        a(bArr);
    }

    /* access modifiers changed from: protected */
    public abstract void c(int i);

    public boolean c() {
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract boolean d();

    /* access modifiers changed from: protected */
    public abstract byte[] e();

    /* access modifiers changed from: protected */
    public abstract CharSequence f();

    /* access modifiers changed from: protected */
    public abstract int g();

    /* access modifiers changed from: protected */
    public abstract <T extends Parcelable> T h();

    /* access modifiers changed from: protected */
    public abstract String i();

    /* access modifiers changed from: protected */
    public <T extends d> T j() {
        String i = i();
        if (i == null) {
            return null;
        }
        return a(i, b());
    }
}
