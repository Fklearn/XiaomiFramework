package com.miui.maml;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public class FramerateTokenList {
    private static final String LOG_TAG = "FramerateTokenList";
    private float mCurFramerate;
    /* access modifiers changed from: private */
    public FramerateChangeListener mFramerateChangeListener;
    private ArrayList<FramerateToken> mList = new ArrayList<>();

    public interface FramerateChangeListener {
        void onFrameRateChage(float f, float f2);
    }

    public class FramerateToken {
        public float mFramerate;
        public String mName;

        public FramerateToken(String str) {
            this.mName = str;
        }

        public float getFramerate() {
            return this.mFramerate;
        }

        public void requestFramerate(float f) {
            if (this.mFramerate != f) {
                if (FramerateTokenList.this.mFramerateChangeListener != null) {
                    FramerateTokenList.this.mFramerateChangeListener.onFrameRateChage(this.mFramerate, f);
                }
                this.mFramerate = f;
                FramerateTokenList.this.onChange();
            }
        }
    }

    public FramerateTokenList() {
    }

    public FramerateTokenList(FramerateChangeListener framerateChangeListener) {
        this.mFramerateChangeListener = framerateChangeListener;
    }

    /* access modifiers changed from: private */
    public void onChange() {
        float f;
        synchronized (this.mList) {
            Iterator<FramerateToken> it = this.mList.iterator();
            f = 0.0f;
            while (it.hasNext()) {
                FramerateToken next = it.next();
                if (next.mFramerate > f) {
                    f = next.mFramerate;
                }
            }
        }
        this.mCurFramerate = f;
        Log.d(LOG_TAG, "Frame rate changed, current frame rate is " + this.mCurFramerate);
    }

    public void clear() {
        synchronized (this.mList) {
            this.mList.clear();
        }
    }

    public FramerateToken createToken(String str) {
        Log.d(LOG_TAG, "createToken: " + str);
        FramerateToken framerateToken = new FramerateToken(str);
        synchronized (this.mList) {
            this.mList.add(framerateToken);
        }
        return framerateToken;
    }

    public float getFramerate() {
        return this.mCurFramerate;
    }

    public void removeToken(FramerateToken framerateToken) {
        synchronized (this.mList) {
            this.mList.remove(framerateToken);
        }
    }
}
