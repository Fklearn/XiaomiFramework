package androidx.core.provider;

import a.d.e.f;
import android.util.Base64;
import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import java.util.List;

public final class a {

    /* renamed from: a  reason: collision with root package name */
    private final String f736a;

    /* renamed from: b  reason: collision with root package name */
    private final String f737b;

    /* renamed from: c  reason: collision with root package name */
    private final String f738c;

    /* renamed from: d  reason: collision with root package name */
    private final List<List<byte[]>> f739d;
    private final int e = 0;
    private final String f = (this.f736a + "-" + this.f737b + "-" + this.f738c);

    public a(@NonNull String str, @NonNull String str2, @NonNull String str3, @NonNull List<List<byte[]>> list) {
        f.a(str);
        this.f736a = str;
        f.a(str2);
        this.f737b = str2;
        f.a(str3);
        this.f738c = str3;
        f.a(list);
        this.f739d = list;
    }

    @Nullable
    public List<List<byte[]>> a() {
        return this.f739d;
    }

    @ArrayRes
    public int b() {
        return this.e;
    }

    @RestrictTo({RestrictTo.a.LIBRARY_GROUP_PREFIX})
    public String c() {
        return this.f;
    }

    @NonNull
    public String d() {
        return this.f736a;
    }

    @NonNull
    public String e() {
        return this.f737b;
    }

    @NonNull
    public String f() {
        return this.f738c;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FontRequest {mProviderAuthority: " + this.f736a + ", mProviderPackage: " + this.f737b + ", mQuery: " + this.f738c + ", mCertificates:");
        for (int i = 0; i < this.f739d.size(); i++) {
            sb.append(" [");
            List list = this.f739d.get(i);
            for (int i2 = 0; i2 < list.size(); i2++) {
                sb.append(" \"");
                sb.append(Base64.encodeToString((byte[]) list.get(i2), 0));
                sb.append("\"");
            }
            sb.append(" ]");
        }
        sb.append("}");
        sb.append("mCertificatesArray: " + this.e);
        return sb.toString();
    }
}
