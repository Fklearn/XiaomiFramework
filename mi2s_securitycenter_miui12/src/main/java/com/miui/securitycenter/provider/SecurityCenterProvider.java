package com.miui.securitycenter.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import b.b.b.d.n;
import com.miui.appmanager.C0322e;
import com.miui.securitycenter.h;
import com.miui.securitycenter.utils.a;
import com.miui.securitycenter.utils.d;
import com.miui.securityscan.i.l;

public class SecurityCenterProvider extends ContentProvider {

    /* renamed from: a  reason: collision with root package name */
    private static final UriMatcher f7508a = new UriMatcher(-1);

    /* renamed from: b  reason: collision with root package name */
    private String f7509b;

    static {
        f7508a.addURI("com.miui.securitycenter.provider", "hideapp", 1);
        f7508a.addURI("com.miui.securitycenter.provider", "allownetwork", 2);
        f7508a.addURI("com.miui.securitycenter.provider", "getserinum", 3);
    }

    private int a(Uri uri, ContentValues contentValues) {
        if (uri == null || contentValues == null || !contentValues.containsKey("isAllowNetwork")) {
            return 0;
        }
        boolean booleanValue = contentValues.getAsBoolean("isAllowNetwork").booleanValue();
        int callingUid = Binder.getCallingUid();
        if (callingUid == 1000 || booleanValue) {
            l.a(getContext(), booleanValue);
            return 1;
        }
        throw new SecurityException("illegal calling uid :" + callingUid);
    }

    public Bundle call(String str, String str2, Bundle bundle) {
        if ("readContextFile".equals(str)) {
            return n.a(getContext(), str2, bundle);
        }
        if ("openInstallerFile".equals(str)) {
            return C0322e.a(getContext(), bundle);
        }
        return null;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        int match = f7508a.match(uri);
        if (match == 1) {
            boolean c2 = a.c();
            boolean d2 = a.d();
            MatrixCursor matrixCursor = new MatrixCursor(new String[]{"isFunctionOpen", "isHide"});
            matrixCursor.addRow(new Object[]{Integer.valueOf(d2 ? 1 : 0), Integer.valueOf(c2 ? 1 : 0)});
            return matrixCursor;
        } else if (match == 2) {
            MatrixCursor matrixCursor2 = new MatrixCursor(new String[]{"isAllow"});
            matrixCursor2.addRow(new Object[]{Integer.valueOf(h.i() ? 1 : 0)});
            return matrixCursor2;
        } else if (match != 3) {
            return null;
        } else {
            if (this.f7509b == null) {
                this.f7509b = d.c();
            }
            String a2 = d.a();
            MatrixCursor matrixCursor3 = new MatrixCursor(new String[]{"seriNum", "lockState"});
            matrixCursor3.addRow(new Object[]{this.f7509b, a2});
            return matrixCursor3;
        }
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        if (f7508a.match(uri) != 2) {
            return 0;
        }
        return a(uri, contentValues);
    }
}
