package com.android.server.infra;

import android.content.Context;
import android.provider.Settings;
import java.io.PrintWriter;

public final class SecureSettingsServiceNameResolver implements ServiceNameResolver {
    private final Context mContext;
    private final String mProperty;

    public SecureSettingsServiceNameResolver(Context context, String property) {
        this.mContext = context;
        this.mProperty = property;
    }

    public String getDefaultServiceName(int userId) {
        return Settings.Secure.getStringForUser(this.mContext.getContentResolver(), this.mProperty, userId);
    }

    public void dumpShort(PrintWriter pw) {
        pw.print("SecureSettingsServiceNamer: prop=");
        pw.print(this.mProperty);
    }

    public void dumpShort(PrintWriter pw, int userId) {
        pw.print("defaultService=");
        pw.print(getDefaultServiceName(userId));
    }

    public String toString() {
        return "SecureSettingsServiceNameResolver[" + this.mProperty + "]";
    }
}
