package com.miui.gamebooster.m;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.miui.gamebooster.provider.d;

public class W {

    /* renamed from: a  reason: collision with root package name */
    public static final String f4461a = "W";

    private static class a extends AsyncTask<Void, Void, Void> {

        /* renamed from: a  reason: collision with root package name */
        private int f4462a;

        /* renamed from: b  reason: collision with root package name */
        private Context f4463b;

        public a(Context context, int i) {
            this.f4462a = i;
            this.f4463b = context;
        }

        private void a() {
            int i = this.f4462a;
            if (i == 2) {
                d.b(this.f4463b);
            } else if (i == 1) {
                d.a(this.f4463b);
            }
        }

        /* access modifiers changed from: protected */
        /* renamed from: a */
        public Void doInBackground(Void... voidArr) {
            a();
            return null;
        }
    }

    public static void a(Context context) {
        if (!V.b(context.getContentResolver())) {
            d.c(context);
            V.b(context.getContentResolver(), true);
        }
        if (!V.a(context.getContentResolver())) {
            Log.i(f4461a, "restore gamebooster Data!");
            a(context, 1);
            a(context, 2);
            if (ba.c(context)) {
                Log.i(f4461a, "createSucessful");
                ba.a(context, (Boolean) true);
            }
            d.c(context);
            V.a(context.getContentResolver(), true);
        }
    }

    private static void a(Context context, int i) {
        new a(context, i).execute(new Void[0]);
    }
}
