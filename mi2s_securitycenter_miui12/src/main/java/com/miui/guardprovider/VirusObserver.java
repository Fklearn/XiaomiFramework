package com.miui.guardprovider;

import android.util.Log;
import b.b.b.o;
import com.miui.guardprovider.aidl.IVirusObserver;
import com.miui.guardprovider.aidl.UpdateInfo;
import com.miui.guardprovider.aidl.VirusInfo;

public class VirusObserver extends IVirusObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    protected o.d f5456a;

    /* renamed from: b  reason: collision with root package name */
    protected o.c f5457b;

    public void a(int i) {
        Log.i("VirusObserver", "onUpdateCanceled in IVirusObserver.Stub result : " + i);
    }

    public void a(int i, int i2) {
        Log.i("VirusObserver", "onUpdateProgress in IVirusObserver.Stub current : " + i + ", total : " + i2);
    }

    public void a(int i, int i2, VirusInfo[] virusInfoArr) {
        Log.i("VirusObserver", "onScanProgress in IVirusObserver.Stub : " + i);
    }

    public void a(int i, String str) {
        Log.i("VirusObserver", "onScanStartItem in IVirusObserver.Stub result : " + i + ", path : " + str);
    }

    public void a(int i, VirusInfo[] virusInfoArr) {
        Log.i("VirusObserver", "onScanFinish in IVirusObserver.Stub : " + i);
    }

    public void a(o.c cVar) {
        this.f5457b = cVar;
    }

    public void a(o.d dVar) {
        this.f5456a = dVar;
    }

    public void a(UpdateInfo updateInfo) {
        Log.i("VirusObserver", "onUpdateItemFinished in IVirusObserver.Stub result : " + updateInfo);
    }

    public void c(int i) {
        Log.i("VirusObserver", "onCheckStarted in IVirusObserver.Stub result : " + i);
    }

    public void d(int i) {
        Log.i("VirusObserver", "onScanStart in IVirusObserver.Stub result : " + i);
    }

    public void j(int i) {
        Log.i("VirusObserver", "onUpdateStart in IVirusObserver.Stub result : " + i);
    }

    public void l(int i) {
        Log.i("VirusObserver", "oncheckFinish in IVirusObserver.Stub result : " + i);
    }

    public void n(int i) {
        Log.i("VirusObserver", "onScanCancel in IVirusObserver.Stub result : " + i);
    }

    public void p(int i) {
        Log.i("VirusObserver", "onUpdateFinished in IVirusObserver.Stub errCode : " + i);
    }
}
