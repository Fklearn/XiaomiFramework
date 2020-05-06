package com.xiaomi.stat;

public class NetAvailableEvent {
    public static final int RESULT_TYPE_0 = 0;
    public static final int RESULT_TYPE_1 = 1;
    public static final int RESULT_TYPE_2 = 2;

    /* renamed from: a  reason: collision with root package name */
    private String f8348a;

    /* renamed from: b  reason: collision with root package name */
    private int f8349b;

    /* renamed from: c  reason: collision with root package name */
    private int f8350c;

    /* renamed from: d  reason: collision with root package name */
    private String f8351d;
    private int e;
    private long f;
    private int g;
    private String h;

    public static final class Builder {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f8352a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public int f8353b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public int f8354c;
        /* access modifiers changed from: private */

        /* renamed from: d  reason: collision with root package name */
        public String f8355d;
        /* access modifiers changed from: private */
        public int e;
        /* access modifiers changed from: private */
        public long f;
        /* access modifiers changed from: private */
        public int g;
        /* access modifiers changed from: private */
        public String h;

        public NetAvailableEvent build() {
            return new NetAvailableEvent(this);
        }

        public Builder exception(String str) {
            this.f8355d = str;
            return this;
        }

        public Builder ext(String str) {
            this.h = str;
            return this;
        }

        public Builder flag(String str) {
            this.f8352a = str;
            return this;
        }

        public Builder requestStartTime(long j) {
            this.f = j;
            return this;
        }

        public Builder responseCode(int i) {
            this.f8353b = i;
            return this;
        }

        public Builder resultType(int i) {
            this.e = i;
            return this;
        }

        public Builder retryCount(int i) {
            this.g = i;
            return this;
        }

        public Builder statusCode(int i) {
            this.f8354c = i;
            return this;
        }
    }

    private NetAvailableEvent(Builder builder) {
        this.f8348a = builder.f8352a;
        this.f8349b = builder.f8353b;
        this.f8350c = builder.f8354c;
        this.f8351d = builder.f8355d;
        this.e = builder.e;
        this.f = builder.f;
        this.g = builder.g;
        this.h = builder.h;
    }

    public String getException() {
        return this.f8351d;
    }

    public String getExt() {
        return this.h;
    }

    public String getFlag() {
        return this.f8348a;
    }

    public long getRequestStartTime() {
        return this.f;
    }

    public int getResponseCode() {
        return this.f8349b;
    }

    public int getResultType() {
        return this.e;
    }

    public int getRetryCount() {
        return this.g;
    }

    public int getStatusCode() {
        return this.f8350c;
    }
}
