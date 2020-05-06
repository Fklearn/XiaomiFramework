package com.miui.gamebooster.m;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* renamed from: com.miui.gamebooster.m.h  reason: case insensitive filesystem */
public class C0377h {

    /* renamed from: a  reason: collision with root package name */
    public static final Pattern f4489a = Pattern.compile("@([^@]+)<([-]{0,}[1-9]{1}[0-9]{0,})>");

    /* renamed from: com.miui.gamebooster.m.h$a */
    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private int f4490a;

        /* renamed from: b  reason: collision with root package name */
        private int f4491b;

        /* renamed from: c  reason: collision with root package name */
        private long f4492c;

        public a(int i, int i2, long j) {
            this.f4490a = i;
            this.f4491b = i2;
            this.f4492c = j;
        }

        public int a() {
            return this.f4491b;
        }

        public int b() {
            return this.f4490a;
        }

        public long c() {
            return this.f4492c;
        }
    }

    public static String a(long j) {
        if (j >= 1073741824) {
            return String.format("%.1fG", new Object[]{Float.valueOf(((float) j) / 1.07374182E9f)});
        } else if (j >= 1048576) {
            float f = ((float) j) / 1048576.0f;
            return String.format(f > 100.0f ? "%.0fM" : "%.1fM", new Object[]{Float.valueOf(f)});
        } else if (j >= 1024) {
            float f2 = ((float) j) / 1024.0f;
            return String.format(f2 > 100.0f ? "%.0fK" : "%.1fK", new Object[]{Float.valueOf(f2)});
        } else {
            return String.format("%dB", new Object[]{Long.valueOf(j)});
        }
    }

    public static ArrayList<a> a(String str) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        ArrayList<a> arrayList = null;
        try {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            Matcher matcher = f4489a.matcher(str.toString());
            int i = 0;
            ArrayList<a> arrayList2 = new ArrayList<>();
            while (matcher.find()) {
                try {
                    int start = matcher.start();
                    int end = matcher.end();
                    if (i != start) {
                        spannableStringBuilder.append(str.subSequence(i, start));
                    }
                    int length = spannableStringBuilder.toString().length();
                    spannableStringBuilder.append("@").append(matcher.group(1));
                    arrayList2.add(new a(length, spannableStringBuilder.toString().length(), Long.valueOf(matcher.group(2)).longValue()));
                    i = end;
                } catch (Throwable th) {
                    th = th;
                    arrayList = arrayList2;
                    th.printStackTrace();
                    return arrayList;
                }
            }
            return arrayList2;
        } catch (Throwable th2) {
            th = th2;
            th.printStackTrace();
            return arrayList;
        }
    }

    public static String b(String str) {
        if (str == null) {
            return "";
        }
        if (str.contains("&nbsp;")) {
            str = str.replace("&nbsp;", " ");
        }
        if (str.contains("&amp;")) {
            str = str.replace("&amp;", "&");
        }
        if (str.contains("&lt;")) {
            str = str.replace("&lt;", "<");
        }
        if (str.contains("&gt;")) {
            str = str.replace("&gt;", ">");
        }
        if (str.contains("&quot;")) {
            str = str.replace("&quot;", "\"");
        }
        return str.contains("&#39;") ? str.replace("&#39;", "'") : str;
    }

    public static String c(String str) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(str)) {
            Matcher matcher = f4489a.matcher(str.toString());
            int i = 0;
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (i != start) {
                    sb.append(str.subSequence(i, start));
                }
                sb.toString().length();
                sb.append("@");
                sb.append(matcher.group(1));
                sb.toString().length();
                i = end;
            }
            if (i == 0) {
                sb.append(str);
            } else if (i < str.length()) {
                sb.append(str.subSequence(i, str.length()));
            }
        }
        return sb.toString();
    }
}
