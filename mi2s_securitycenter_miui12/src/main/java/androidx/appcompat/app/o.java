package androidx.appcompat.app;

import com.google.android.exoplayer2.extractor.MpegAudioHeader;

class o implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatDelegateImpl f318a;

    o(AppCompatDelegateImpl appCompatDelegateImpl) {
        this.f318a = appCompatDelegateImpl;
    }

    public void run() {
        AppCompatDelegateImpl appCompatDelegateImpl = this.f318a;
        if ((appCompatDelegateImpl.Z & 1) != 0) {
            appCompatDelegateImpl.f(0);
        }
        AppCompatDelegateImpl appCompatDelegateImpl2 = this.f318a;
        if ((appCompatDelegateImpl2.Z & MpegAudioHeader.MAX_FRAME_SIZE_BYTES) != 0) {
            appCompatDelegateImpl2.f(108);
        }
        AppCompatDelegateImpl appCompatDelegateImpl3 = this.f318a;
        appCompatDelegateImpl3.Y = false;
        appCompatDelegateImpl3.Z = 0;
    }
}
