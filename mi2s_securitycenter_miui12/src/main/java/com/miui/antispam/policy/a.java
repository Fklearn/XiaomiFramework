package com.miui.antispam.policy;

import android.content.Context;
import com.miui.antispam.policy.a.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.g;

public abstract class a {
    protected Context mContext;
    protected d mJudge;
    protected b mPc;
    protected g mPolicyDesc;

    /* renamed from: com.miui.antispam.policy.a$a  reason: collision with other inner class name */
    public class C0035a {

        /* renamed from: a  reason: collision with root package name */
        public boolean f2353a;

        /* renamed from: b  reason: collision with root package name */
        public int f2354b;

        /* renamed from: c  reason: collision with root package name */
        public String f2355c;

        public C0035a(a aVar, boolean z, int i) {
            this(z, i, "");
        }

        public C0035a(boolean z, int i, String str) {
            this.f2353a = z;
            this.f2354b = i;
            this.f2355c = str;
        }
    }

    public interface b {
        a a(g gVar);
    }

    public a(Context context, b bVar, d dVar, g gVar) {
        this.mContext = context;
        this.mPc = bVar;
        this.mJudge = dVar;
        this.mPolicyDesc = gVar;
    }

    public abstract C0035a dbQuery(e eVar);

    public abstract int getType();

    public abstract C0035a handleData(e eVar);
}
