package com.miui.optimizemanage.d;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import b.b.c.j.C;
import com.miui.optimizemanage.settings.c;
import com.miui.powercenter.utils.b;
import com.miui.powercenter.utils.j;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private static Executor f5932a;

    public static long a() {
        long b2 = c.b();
        long currentTimeMillis = System.currentTimeMillis();
        if (b2 <= 0 || currentTimeMillis <= b2) {
            return 0;
        }
        return currentTimeMillis - b2;
    }

    public static List<String> a(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        ArrayList arrayList = new ArrayList();
        for (ResolveInfo next : queryIntentActivities) {
            ActivityInfo activityInfo = next.activityInfo;
            if (!(activityInfo == null || activityInfo.packageName == null)) {
                arrayList.add(next.activityInfo.packageName);
            }
        }
        return arrayList;
    }

    public static void a(ImageView imageView, String str, int i) {
        Drawable drawable;
        Context context = imageView.getContext();
        imageView.setTag((Object) null);
        if (TextUtils.isEmpty(str)) {
            drawable = imageView.getContext().getPackageManager().getDefaultActivityIcon();
        } else if (C.b(j.a(i))) {
            drawable = C.a(context, new BitmapDrawable(context.getResources(), b.a(str)), i);
        } else {
            b.a(imageView, str);
            return;
        }
        imageView.setImageDrawable(drawable);
    }

    public static void a(ArrayList<String> arrayList, boolean z, List<Integer> list) {
        try {
            Object newInstance = Class.forName("miui.process.ProcessConfig").getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{5});
            b.b.o.g.e.a((Object) newInstance, "setWhiteList", (Class<?>[]) new Class[]{List.class}, arrayList);
            b.b.o.g.e.a((Object) newInstance, "setRemoveTaskNeeded", (Class<?>[]) new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
            b.b.o.g.e.a((Object) newInstance, "setRemovingTaskIdList", (Class<?>[]) new Class[]{List.class}, list);
            Class<?> cls = Class.forName("miui.process.ProcessManager");
            Class[] clsArr = new Class[1];
            clsArr[0] = Class.forName("miui.process.ProcessConfig");
            b.b.o.g.e.a(cls, "kill", (Class<?>[]) clsArr, newInstance);
        } catch (Exception e) {
            Log.e("Utils", "reflect error while kill process in optimizemanage", e);
        }
    }

    public static boolean a(Context context, String str) {
        if (context == null) {
            return false;
        }
        try {
            return (context.getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static synchronized Executor b() {
        Executor executor;
        synchronized (e.class) {
            if (f5932a == null) {
                f5932a = Executors.newFixedThreadPool(2);
            }
            executor = f5932a;
        }
        return executor;
    }

    public static void b(Context context) {
        new d(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
}
