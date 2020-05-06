package com.miui.maml.elements;

import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FramerateController extends ScreenElement {
    public static final String INNER_TAG = "ControlPoint";
    public static final String TAG_NAME = "FramerateController";
    private ArrayList<ControlPoint> mControlPoints = new ArrayList<>();
    private long mDelay;
    private long mLastUpdateTime;
    private Object mLock = new Object();
    private boolean mLoop;
    private long mNextUpdateInterval;
    private long mStartTime;
    private boolean mStopped;
    private String mTag;
    private long mTimeRange;

    public static class ControlPoint {
        public int mFramerate;
        public long mTime;

        public ControlPoint(Element element) {
            this.mTime = Utils.getAttrAsLongThrows(element, "time");
            this.mFramerate = Utils.getAttrAsInt(element, "frameRate", -1);
        }
    }

    public FramerateController(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mLoop = Boolean.parseBoolean(element.getAttribute("loop"));
        this.mTag = element.getAttribute("tag");
        String attribute = element.getAttribute("delay");
        if (!TextUtils.isEmpty(attribute)) {
            try {
                this.mDelay = Long.parseLong(attribute);
            } catch (NumberFormatException unused) {
                Log.w(TAG_NAME, "invalid delay attribute");
            }
        }
        NodeList elementsByTagName = element.getElementsByTagName(INNER_TAG);
        boolean z = false;
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            this.mControlPoints.add(new ControlPoint((Element) elementsByTagName.item(i)));
        }
        ArrayList<ControlPoint> arrayList = this.mControlPoints;
        this.mTimeRange = arrayList.get(arrayList.size() - 1).mTime;
        if (this.mLoop && this.mTimeRange != 0) {
            z = true;
        }
        this.mLoop = z;
    }

    private void restart(long j) {
        synchronized (this.mLock) {
            this.mStartTime = j + this.mDelay;
            this.mStopped = false;
            this.mLastUpdateTime = 0;
            this.mNextUpdateInterval = 0;
            requestUpdate();
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        if (isVisible()) {
            super.playAnim(j, j2, j3, z, z2);
            restart(j - j2);
        }
    }

    public void reset(long j) {
        super.reset(j);
        restart(j);
    }

    public void setAnim(String[] strArr) {
        show(ScreenElement.isTagEnable(strArr, this.mTag));
    }

    public long updateFramerate(long j) {
        updateVisibility();
        long j2 = Long.MAX_VALUE;
        if (!isVisible()) {
            return Long.MAX_VALUE;
        }
        synchronized (this.mLock) {
            if (this.mStopped) {
                return Long.MAX_VALUE;
            }
            long j3 = 0;
            if (this.mLastUpdateTime > 0) {
                long j4 = j - this.mLastUpdateTime;
                if (j4 >= 0 && j4 < this.mNextUpdateInterval) {
                    this.mNextUpdateInterval -= j4;
                    this.mLastUpdateTime = j;
                    long j5 = this.mNextUpdateInterval;
                    return j5;
                }
            }
            long j6 = j - this.mStartTime;
            if (j6 < 0) {
                j6 = 0;
            }
            if (this.mLoop) {
                j6 %= this.mTimeRange + 1;
            }
            for (int size = this.mControlPoints.size() - 1; size >= 0; size--) {
                ControlPoint controlPoint = this.mControlPoints.get(size);
                if (j6 >= controlPoint.mTime) {
                    requestFramerate((float) controlPoint.mFramerate);
                    if (!this.mLoop && size == this.mControlPoints.size() - 1) {
                        this.mStopped = true;
                    }
                    this.mLastUpdateTime = j;
                    if (!this.mStopped) {
                        j2 = j3 - j6;
                    }
                    this.mNextUpdateInterval = j2;
                    long j7 = this.mNextUpdateInterval;
                    return j7;
                }
                j3 = controlPoint.mTime;
            }
            return Long.MAX_VALUE;
        }
    }
}
