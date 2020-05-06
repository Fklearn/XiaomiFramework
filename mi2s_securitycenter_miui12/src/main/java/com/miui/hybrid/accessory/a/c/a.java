package com.miui.hybrid.accessory.a.c;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public int f5479a;

    /* renamed from: b  reason: collision with root package name */
    public Map<String, String> f5480b = new HashMap();

    /* renamed from: c  reason: collision with root package name */
    byte[] f5481c;

    /* renamed from: d  reason: collision with root package name */
    String f5482d;

    public boolean a() {
        return this.f5479a == 200;
    }

    public byte[] b() {
        return this.f5481c;
    }

    public String c() {
        byte[] bArr = this.f5481c;
        if (bArr == null) {
            return null;
        }
        if (this.f5482d == null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bArr)));
            try {
                StringBuffer stringBuffer = new StringBuffer();
                String property = System.getProperty("line.separator");
                for (String readLine = bufferedReader.readLine(); readLine != null; readLine = bufferedReader.readLine()) {
                    stringBuffer.append(readLine);
                    stringBuffer.append(property);
                }
                this.f5482d = stringBuffer.toString();
            } catch (IOException e) {
                com.miui.hybrid.accessory.a.b.a.b("HttpResponse", e.getMessage(), e);
            }
        }
        return this.f5482d;
    }

    public String toString() {
        return String.format("resCode = %1$d, headers = %2$s, response = %3$s", new Object[]{Integer.valueOf(this.f5479a), this.f5480b.toString(), c()});
    }
}
