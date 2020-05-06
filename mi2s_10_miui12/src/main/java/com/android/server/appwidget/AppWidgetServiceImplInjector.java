package com.android.server.appwidget;

import android.content.Context;
import android.os.Binder;
import android.provider.Settings;
import com.android.server.appwidget.AppWidgetServiceImpl;
import com.google.android.collect.Sets;
import java.util.List;
import java.util.Set;

public class AppWidgetServiceImplInjector {
    public static final String ENABLED_WIDGETS = "enabled_widgets";
    private static String TAG = AppWidgetServiceImplInjector.class.getSimpleName();

    public static void updateWidgetPackagesLocked(Context ctx, List<AppWidgetServiceImpl.Provider> mProviders, int userId) {
        Set<String> pkgSet = Sets.newHashSet();
        int N = mProviders.size();
        for (int index = 0; index < N; index++) {
            AppWidgetServiceImpl.Provider provider = mProviders.get(index);
            if (provider.getUserId() == userId && provider.widgets.size() > 0) {
                pkgSet.add(provider.info.provider.getPackageName());
            }
        }
        StringBuilder sb = null;
        for (String pkg : pkgSet) {
            if (sb == null) {
                sb = new StringBuilder();
            } else {
                sb.append(':');
            }
            sb.append(pkg);
        }
        long identity = Binder.clearCallingIdentity();
        Settings.Secure.putStringForUser(ctx.getContentResolver(), ENABLED_WIDGETS, sb != null ? sb.toString() : "", userId);
        Binder.restoreCallingIdentity(identity);
    }
}
