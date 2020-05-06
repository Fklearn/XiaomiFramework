package com.miui.analytics;

import miui.os.Build;

public class AnalyticsConstant {
    private static final String CHANNEL_DEFAULT = "default";
    private static final String CHANNEL_DEFAULT_DEVELOPMENT = "default_development";
    private static final String CHANNEL_DEFAULT_STABLE = "default_stable";
    private static final String CHANNEL_INTERNATIONAL = "international";
    private static final String CHANNEL_INTERNATIONAL_DEVELOPMENT = "international_development";
    private static final String CHANNEL_INTERNATIONAL_STABLE = "international_stable";
    static final String STATS_APP_ID = "2882303761517405262";
    static final String STATS_APP_KEY = "5971740546262";

    public static String getChannel() {
        return Build.IS_INTERNATIONAL_BUILD ? Build.IS_STABLE_VERSION ? CHANNEL_INTERNATIONAL_STABLE : Build.IS_DEVELOPMENT_VERSION ? CHANNEL_INTERNATIONAL_DEVELOPMENT : CHANNEL_INTERNATIONAL : Build.IS_STABLE_VERSION ? CHANNEL_DEFAULT_STABLE : Build.IS_DEVELOPMENT_VERSION ? CHANNEL_DEFAULT_DEVELOPMENT : "default";
    }
}
