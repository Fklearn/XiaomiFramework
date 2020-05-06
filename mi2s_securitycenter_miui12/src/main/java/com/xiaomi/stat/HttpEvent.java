package com.xiaomi.stat;

public class HttpEvent {

    /* renamed from: a  reason: collision with root package name */
    private int f8337a;

    /* renamed from: b  reason: collision with root package name */
    private long f8338b;

    /* renamed from: c  reason: collision with root package name */
    private long f8339c;

    /* renamed from: d  reason: collision with root package name */
    private String f8340d;
    private String e;

    public HttpEvent(String str, long j) {
        this(str, j, -1, (String) null);
    }

    public HttpEvent(String str, long j, int i, String str2) {
        this(str, j, 0, i, str2);
    }

    public HttpEvent(String str, long j, long j2) {
        this(str, j, j2, -1, (String) null);
    }

    public HttpEvent(String str, long j, long j2, int i) {
        this(str, j, j2, i, (String) null);
    }

    public HttpEvent(String str, long j, long j2, int i, String str2) {
        this.f8339c = 0;
        this.f8340d = str;
        this.f8338b = j;
        this.f8337a = i;
        this.e = str2;
        this.f8339c = j2;
    }

    public HttpEvent(String str, long j, String str2) {
        this(str, j, -1, str2);
    }

    public String getExceptionName() {
        return this.e;
    }

    public long getNetFlow() {
        return this.f8339c;
    }

    public int getResponseCode() {
        return this.f8337a;
    }

    public long getTimeCost() {
        return this.f8338b;
    }

    public String getUrl() {
        return this.f8340d;
    }
}
