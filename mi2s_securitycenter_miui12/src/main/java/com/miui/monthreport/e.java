package com.miui.monthreport;

import android.net.Uri;
import miui.os.Build;

public final class e {

    /* renamed from: a  reason: collision with root package name */
    public static final Uri f5638a = Uri.parse("content://com.miui.monthreport/report_json");

    /* renamed from: b  reason: collision with root package name */
    private static final String f5639b = (Build.IS_INTERNATIONAL_BUILD ? "https://data.sec.intl.miui.com" : "https://data.sec.miui.com");

    /* renamed from: c  reason: collision with root package name */
    public static final String f5640c = (f5639b + "/data/upload");

    /* renamed from: d  reason: collision with root package name */
    private static final String f5641d = (Build.IS_INTERNATIONAL_BUILD ? "https://api.sec.intl.miui.com" : "https://api.sec.miui.com");
    public static final String e = (f5641d + "/data/check");
}
