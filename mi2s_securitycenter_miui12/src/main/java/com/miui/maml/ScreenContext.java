package com.miui.maml;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.miui.maml.data.ContextVariables;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ScreenElementFactory;
import java.util.HashMap;

public class ScreenContext {
    public final Context mContext;
    public final ContextVariables mContextVariables;
    public final ScreenElementFactory mFactory;
    private final Handler mHandler;
    private HashMap<String, ObjectFactory> mObjectFactories;
    public final ResourceManager mResourceManager;
    public final Variables mVariables;

    public ScreenContext(Context context, ResourceLoader resourceLoader) {
        this(context, resourceLoader, new ScreenElementFactory());
    }

    public ScreenContext(Context context, ResourceLoader resourceLoader, ScreenElementFactory screenElementFactory) {
        this(context, new ResourceManager(resourceLoader), screenElementFactory);
    }

    public ScreenContext(Context context, ResourceManager resourceManager) {
        this(context, resourceManager, new ScreenElementFactory());
    }

    public ScreenContext(Context context, ResourceManager resourceManager, ScreenElementFactory screenElementFactory) {
        this(context, resourceManager, screenElementFactory, new Variables());
    }

    public ScreenContext(Context context, ResourceManager resourceManager, ScreenElementFactory screenElementFactory, Variables variables) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext != null ? applicationContext : context;
        this.mResourceManager = resourceManager;
        this.mFactory = screenElementFactory;
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mVariables = variables;
        this.mContextVariables = new ContextVariables();
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0016, code lost:
        return null;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized <T extends com.miui.maml.ObjectFactory> T getObjectFactory(java.lang.String r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            r0 = 0
            java.util.HashMap<java.lang.String, com.miui.maml.ObjectFactory> r1 = r2.mObjectFactories     // Catch:{ ClassCastException -> 0x0015, all -> 0x0012 }
            if (r1 != 0) goto L_0x0008
            r3 = r0
            goto L_0x0010
        L_0x0008:
            java.util.HashMap<java.lang.String, com.miui.maml.ObjectFactory> r1 = r2.mObjectFactories     // Catch:{ ClassCastException -> 0x0015, all -> 0x0012 }
            java.lang.Object r3 = r1.get(r3)     // Catch:{ ClassCastException -> 0x0015, all -> 0x0012 }
            com.miui.maml.ObjectFactory r3 = (com.miui.maml.ObjectFactory) r3     // Catch:{ ClassCastException -> 0x0015, all -> 0x0012 }
        L_0x0010:
            monitor-exit(r2)
            return r3
        L_0x0012:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        L_0x0015:
            monitor-exit(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ScreenContext.getObjectFactory(java.lang.String):com.miui.maml.ObjectFactory");
    }

    public boolean postDelayed(Runnable runnable, long j) {
        return this.mHandler.postDelayed(runnable, j);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final synchronized void registerObjectFactory(java.lang.String r3, com.miui.maml.ObjectFactory r4) {
        /*
            r2 = this;
            monitor-enter(r2)
            if (r4 != 0) goto L_0x000e
            java.util.HashMap<java.lang.String, com.miui.maml.ObjectFactory> r4 = r2.mObjectFactories     // Catch:{ all -> 0x0058 }
            if (r4 == 0) goto L_0x000c
            java.util.HashMap<java.lang.String, com.miui.maml.ObjectFactory> r4 = r2.mObjectFactories     // Catch:{ all -> 0x0058 }
            r4.remove(r3)     // Catch:{ all -> 0x0058 }
        L_0x000c:
            monitor-exit(r2)
            return
        L_0x000e:
            java.lang.String r0 = r4.getName()     // Catch:{ all -> 0x0058 }
            boolean r0 = r3.equals(r0)     // Catch:{ all -> 0x0058 }
            if (r0 == 0) goto L_0x0041
            java.util.HashMap<java.lang.String, com.miui.maml.ObjectFactory> r0 = r2.mObjectFactories     // Catch:{ all -> 0x0058 }
            if (r0 != 0) goto L_0x0023
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ all -> 0x0058 }
            r0.<init>()     // Catch:{ all -> 0x0058 }
            r2.mObjectFactories = r0     // Catch:{ all -> 0x0058 }
        L_0x0023:
            java.util.HashMap<java.lang.String, com.miui.maml.ObjectFactory> r0 = r2.mObjectFactories     // Catch:{ all -> 0x0058 }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x0058 }
            com.miui.maml.ObjectFactory r0 = (com.miui.maml.ObjectFactory) r0     // Catch:{ all -> 0x0058 }
            r1 = r0
        L_0x002c:
            if (r1 == 0) goto L_0x0037
            if (r1 != r4) goto L_0x0032
            monitor-exit(r2)
            return
        L_0x0032:
            com.miui.maml.ObjectFactory r1 = r1.getOld()     // Catch:{ all -> 0x0058 }
            goto L_0x002c
        L_0x0037:
            r4.setOld(r0)     // Catch:{ all -> 0x0058 }
            java.util.HashMap<java.lang.String, com.miui.maml.ObjectFactory> r0 = r2.mObjectFactories     // Catch:{ all -> 0x0058 }
            r0.put(r3, r4)     // Catch:{ all -> 0x0058 }
            monitor-exit(r2)
            return
        L_0x0041:
            java.lang.IllegalArgumentException r4 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0058 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0058 }
            r0.<init>()     // Catch:{ all -> 0x0058 }
            java.lang.String r1 = "ObjectFactory name mismatchs "
            r0.append(r1)     // Catch:{ all -> 0x0058 }
            r0.append(r3)     // Catch:{ all -> 0x0058 }
            java.lang.String r3 = r0.toString()     // Catch:{ all -> 0x0058 }
            r4.<init>(r3)     // Catch:{ all -> 0x0058 }
            throw r4     // Catch:{ all -> 0x0058 }
        L_0x0058:
            r3 = move-exception
            monitor-exit(r2)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.ScreenContext.registerObjectFactory(java.lang.String, com.miui.maml.ObjectFactory):void");
    }

    public void removeCallbacks(Runnable runnable) {
        this.mHandler.removeCallbacks(runnable);
    }
}
