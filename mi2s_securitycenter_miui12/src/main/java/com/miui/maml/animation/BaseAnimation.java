package com.miui.maml.animation;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.CommandTriggers;
import com.miui.maml.animation.interpolater.InterpolatorHelper;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ListScreenElement;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.util.Utils;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;

public abstract class BaseAnimation {
    private static final long INFINITE_TIME = 1000000000000L;
    private static final String LOG_TAG = "BaseAnimation";
    public static final int PLAY_TO_END = -1;
    private static final String VAR_CURRENT_FRAME = "current_frame";
    private long mAnimEndTime;
    private long mAnimStartTime;
    protected String[] mAttrs;
    private double[] mCurValues;
    private IndexedVariable mCurrentFrame;
    private Expression mDelay;
    private boolean mDisable;
    private long mEndTime;
    private boolean mHasName;
    private boolean mInitPaused;
    private boolean mIsDelay;
    private boolean mIsFirstFrame;
    private boolean mIsFirstReset;
    private boolean mIsLastFrame;
    private boolean mIsLoop;
    private boolean mIsPaused;
    private boolean mIsReverse;
    private boolean mIsTimeInfinite;
    protected ArrayList<AnimationItem> mItems;
    private boolean mLoop;
    private String mName;
    private long mPauseTime;
    private long mPlayTimeRange;
    private long mRealTimeRange;
    private long mResetTime;
    protected ScreenElement mScreenElement;
    private long mStartTime;
    private String mTag;
    private CommandTriggers mTriggers;

    public static class AnimationItem {
        private BaseAnimation mAni;
        private double[] mAttrsValue;
        public Expression mDeltaTimeExp;
        public Expression[] mExps;
        public long mInitTime;
        public InterpolatorHelper mInterpolator;
        private String mName;
        private boolean mNeedEvaluate = true;
        public long mTime;

        public AnimationItem(BaseAnimation baseAnimation, Element element) {
            this.mAni = baseAnimation;
            load(element);
        }

        private void load(Element element) {
            Variables variables = this.mAni.getVariables();
            this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
            if (!TextUtils.isEmpty(this.mName)) {
                this.mAni.mScreenElement.getRoot().addAnimationItem(this.mName, this);
            }
            this.mInterpolator = InterpolatorHelper.create(variables, element);
            try {
                this.mTime = Long.parseLong(element.getAttribute("time"));
            } catch (NumberFormatException unused) {
            }
            this.mDeltaTimeExp = Expression.build(variables, element.getAttribute("dtime"));
            String[] attrs = this.mAni.getAttrs();
            if (attrs != null) {
                this.mAttrsValue = new double[attrs.length];
                this.mExps = new Expression[attrs.length];
                int length = attrs.length;
                int i = 0;
                int i2 = 0;
                while (i < length) {
                    String str = attrs[i];
                    Expression build = Expression.build(variables, element.getAttribute(str));
                    if (build == null && i2 == 0 && !MiStat.Param.VALUE.equals(str)) {
                        build = Expression.build(variables, element.getAttribute(MiStat.Param.VALUE));
                    }
                    this.mExps[i2] = build;
                    i++;
                    i2++;
                }
            }
            this.mInitTime = this.mTime;
        }

        private void reevaluate() {
            Expression[] expressionArr = this.mExps;
            if (expressionArr != null) {
                int length = expressionArr.length;
                int i = 0;
                int i2 = 0;
                while (i < length) {
                    Expression expression = expressionArr[i];
                    int i3 = i2 + 1;
                    this.mAttrsValue[i2] = expression == null ? 0.0d : expression.evaluate();
                    i++;
                    i2 = i3;
                }
            }
        }

        public boolean attrExists(int i) {
            Expression[] expressionArr = this.mExps;
            return expressionArr != null && i >= 0 && i < expressionArr.length && expressionArr[i] != null;
        }

        public void changeInterpolator(String str, String str2, String str3) {
            this.mInterpolator = new InterpolatorHelper(this.mAni.getVariables(), str, str3, str2);
        }

        public double get(int i) {
            double[] dArr = this.mAttrsValue;
            if (dArr == null || i < 0 || i >= dArr.length) {
                Log.e(BaseAnimation.LOG_TAG, "fail to get number in AnimationItem:" + i);
                return 0.0d;
            }
            if (this.mNeedEvaluate) {
                reevaluate();
                this.mNeedEvaluate = false;
            }
            return this.mAttrsValue[i];
        }

        public void reset() {
            this.mNeedEvaluate = true;
            this.mTime = this.mInitTime;
        }
    }

    public BaseAnimation(Element element, ScreenElement screenElement) {
        this(element, (String) null, MiStat.Param.VALUE, screenElement);
    }

    public BaseAnimation(Element element, String str, ScreenElement screenElement) {
        this(element, str, MiStat.Param.VALUE, screenElement);
    }

    public BaseAnimation(Element element, String str, String str2, ScreenElement screenElement) {
        this(element, str, new String[]{str2}, screenElement);
    }

    public BaseAnimation(Element element, String str, String[] strArr, ScreenElement screenElement) {
        this.mItems = new ArrayList<>();
        this.mLoop = true;
        this.mScreenElement = screenElement;
        this.mAttrs = strArr;
        this.mCurValues = new double[this.mAttrs.length];
        load(element, str);
    }

    private float getRatio(AnimationItem animationItem, long j, long j2, long j3) {
        InterpolatorHelper interpolatorHelper;
        float f = j3 == 0 ? 1.0f : ((float) (j - j2)) / ((float) j3);
        return (animationItem == null || (interpolatorHelper = animationItem.mInterpolator) == null) ? f : interpolatorHelper.get(f);
    }

    private void load(Element element, String str) {
        Object obj;
        this.mName = element.getAttribute(CloudPushConstants.XML_NAME);
        this.mHasName = !TextUtils.isEmpty(this.mName);
        Variables variables = getVariables();
        if (this.mHasName) {
            this.mCurrentFrame = new IndexedVariable(this.mName + "." + VAR_CURRENT_FRAME, variables, true);
        }
        this.mDelay = Expression.build(variables, element.getAttribute("delay"));
        this.mInitPaused = Boolean.parseBoolean(element.getAttribute("initPause"));
        this.mLoop = !"false".equalsIgnoreCase(element.getAttribute("loop"));
        this.mTag = element.getAttribute("tag");
        boolean z = false;
        Utils.traverseXmlElementChildrenTags(element, new String[]{str, ListScreenElement.ListItemElement.TAG_NAME}, new Utils.XmlTraverseListener() {
            public void onChild(Element element) {
                BaseAnimation baseAnimation = BaseAnimation.this;
                baseAnimation.mItems.add(baseAnimation.onCreateItem(baseAnimation, element));
            }
        });
        if (this.mItems.size() <= 0) {
            Log.e(LOG_TAG, "empty items");
            return;
        }
        ArrayList<AnimationItem> arrayList = this.mItems;
        if (arrayList.get(arrayList.size() - 1).mTime >= INFINITE_TIME) {
            z = true;
        }
        this.mIsTimeInfinite = z;
        if (this.mItems.size() <= 1 || !this.mIsTimeInfinite) {
            ArrayList arrayList2 = this.mItems;
            obj = arrayList2.get(arrayList2.size() - 1);
        } else {
            ArrayList arrayList3 = this.mItems;
            obj = arrayList3.get(arrayList3.size() - 2);
        }
        this.mRealTimeRange = ((AnimationItem) obj).mTime;
        Element child = Utils.getChild(element, CommandTriggers.TAG_NAME);
        if (child != null) {
            this.mTriggers = new CommandTriggers(child, this.mScreenElement);
        }
    }

    private void reevaluate() {
        int size = this.mItems.size();
        boolean z = false;
        long j = 0;
        for (int i = 0; i < size; i++) {
            AnimationItem animationItem = this.mItems.get(i);
            Expression expression = animationItem.mDeltaTimeExp;
            if (expression != null) {
                long evaluate = (long) expression.evaluate();
                if (evaluate < 0) {
                    evaluate = 0;
                }
                j += evaluate;
                animationItem.mTime = j;
            } else {
                long j2 = animationItem.mTime;
                if (j2 >= j) {
                    j = j2;
                }
            }
        }
        if (j >= INFINITE_TIME) {
            z = true;
        }
        this.mIsTimeInfinite = z;
        if (size <= 1 || !this.mIsTimeInfinite) {
            this.mRealTimeRange = j;
        } else {
            this.mRealTimeRange = this.mItems.get(size - 2).mTime;
        }
    }

    private void resetTime() {
        if (this.mIsFirstReset) {
            this.mIsFirstReset = false;
        }
        this.mIsLastFrame = false;
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            this.mItems.get(i).reset();
        }
        reevaluate();
        this.mAnimStartTime = transToAnimTime(this.mStartTime);
        this.mAnimEndTime = transToAnimTime(this.mEndTime);
        this.mPlayTimeRange = Math.abs(this.mAnimEndTime - this.mAnimStartTime);
    }

    private long transToAnimTime(long j) {
        return (j == -1 || j > this.mRealTimeRange) ? this.mRealTimeRange : j;
    }

    public void finish() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.finish();
        }
        int size = this.mItems.size();
        for (int i = 0; i < size; i++) {
            this.mItems.get(i).reset();
        }
        int length = this.mCurValues.length;
        for (int i2 = 0; i2 < length; i2++) {
            this.mCurValues[i2] = 0.0d;
        }
    }

    public String[] getAttrs() {
        return this.mAttrs;
    }

    public double getCurValue(int i) {
        return this.mCurValues[i];
    }

    /* access modifiers changed from: protected */
    public double getDefaultValue() {
        return 0.0d;
    }

    /* access modifiers changed from: protected */
    public double getDelayValue(int i) {
        AnimationItem item = getItem(0);
        if (item != null) {
            return item.get(i);
        }
        return 0.0d;
    }

    /* access modifiers changed from: protected */
    public AnimationItem getItem(int i) {
        if (i < 0 || i >= this.mItems.size()) {
            return null;
        }
        return this.mItems.get(i);
    }

    public String getTag() {
        return this.mTag;
    }

    /* access modifiers changed from: protected */
    public final Variables getVariables() {
        return this.mScreenElement.getVariables();
    }

    public void init() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.init();
        }
    }

    public void onAction(String str) {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.onAction(str);
        }
    }

    /* access modifiers changed from: protected */
    public AnimationItem onCreateItem(BaseAnimation baseAnimation, Element element) {
        return new AnimationItem(baseAnimation, element);
    }

    /* access modifiers changed from: protected */
    public void onTick(AnimationItem animationItem, AnimationItem animationItem2, float f) {
        if (animationItem != null || animationItem2 != null) {
            double defaultValue = getDefaultValue();
            int length = this.mAttrs.length;
            for (int i = 0; i < length; i++) {
                double d2 = animationItem == null ? defaultValue : animationItem.get(i);
                this.mCurValues[i] = d2 + ((animationItem2.get(i) - d2) * ((double) f));
            }
        }
    }

    public void pause() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.pause();
        }
    }

    public void pauseAnim(long j) {
        if (!this.mDisable && !this.mIsPaused) {
            this.mIsPaused = true;
            this.mPauseTime = j;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x004b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void playAnim(long r4, long r6, long r8, boolean r10, boolean r11) {
        /*
            r3 = this;
            boolean r0 = r3.mDisable
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            r3.mResetTime = r4
            r4 = 0
            int r0 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            r1 = -1
            if (r0 >= 0) goto L_0x0015
            int r0 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x0014
            goto L_0x0015
        L_0x0014:
            r6 = r4
        L_0x0015:
            r3.mStartTime = r6
            r3.mAnimStartTime = r6
            int r6 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x0023
            int r6 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r6 != 0) goto L_0x0022
            goto L_0x0023
        L_0x0022:
            r8 = r4
        L_0x0023:
            r3.mEndTime = r8
            r3.mAnimEndTime = r8
            r3.mIsLoop = r10
            r3.mIsDelay = r11
            long r6 = r3.mStartTime
            int r8 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
            r9 = 1
            r10 = 0
            if (r8 == 0) goto L_0x0040
            long r0 = r3.mEndTime
            int r6 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r6 < 0) goto L_0x003e
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 < 0) goto L_0x003e
            goto L_0x0040
        L_0x003e:
            r6 = r10
            goto L_0x0041
        L_0x0040:
            r6 = r9
        L_0x0041:
            r3.mIsReverse = r6
            long r6 = r3.mStartTime
            long r0 = r3.mEndTime
            int r6 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r6 != 0) goto L_0x004d
            r3.mIsLoop = r10
        L_0x004d:
            boolean r6 = r3.mIsDelay
            if (r6 == 0) goto L_0x0060
            com.miui.maml.data.Expression r6 = r3.mDelay
            if (r6 == 0) goto L_0x0060
            long r7 = r3.mResetTime
            double r7 = (double) r7
            double r0 = r6.evaluate()
            double r7 = r7 + r0
            long r6 = (long) r7
            r3.mResetTime = r6
        L_0x0060:
            r3.mIsFirstFrame = r9
            r3.mIsLastFrame = r10
            r3.mIsPaused = r10
            r3.mIsFirstReset = r9
            r3.mPlayTimeRange = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.animation.BaseAnimation.playAnim(long, long, long, boolean, boolean):void");
    }

    public void reset(long j) {
        boolean z;
        boolean z2;
        long j2;
        long j3;
        if (!this.mDisable) {
            int length = this.mAttrs.length;
            for (int i = 0; i < length; i++) {
                this.mCurValues[i] = getDelayValue(i);
            }
            if (this.mInitPaused) {
                j3 = 0;
                j2 = 0;
                z2 = false;
                z = false;
            } else {
                j3 = 0;
                j2 = -1;
                z2 = true;
                z = true;
            }
            playAnim(j, j3, j2, z2, z);
            if (this.mHasName) {
                this.mCurrentFrame.set(0.0d);
            }
            onAction("init");
        }
    }

    public void resume() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.resume();
        }
    }

    public void resumeAnim(long j) {
        if (!this.mDisable && this.mIsPaused) {
            this.mIsPaused = false;
            this.mResetTime += j - this.mPauseTime;
        }
    }

    public void setCurValue(int i, double d2) {
        this.mCurValues[i] = d2;
    }

    public void setDisable(boolean z) {
        this.mDisable = z;
    }

    public final void tick(long j) {
        AnimationItem animationItem;
        long j2;
        long j3;
        if (!this.mIsPaused && !this.mDisable) {
            long j4 = j - this.mResetTime;
            int i = 0;
            if (j4 < 0) {
                if (this.mIsFirstFrame) {
                    this.mIsFirstFrame = false;
                    j4 = 0;
                } else {
                    onTick((AnimationItem) null, (AnimationItem) null, 0.0f);
                    return;
                }
            }
            if (this.mIsFirstReset || (this.mIsLastFrame && !this.mIsTimeInfinite && this.mLoop && this.mIsLoop)) {
                resetTime();
            }
            if ((this.mIsTimeInfinite || !this.mLoop || !this.mIsLoop) && this.mIsLastFrame) {
                this.mIsPaused = true;
                this.mPauseTime = this.mResetTime + this.mPlayTimeRange;
                if (this.mHasName) {
                    this.mCurrentFrame.set((double) this.mEndTime);
                }
                onAction(TtmlNode.END);
                return;
            }
            long j5 = this.mPlayTimeRange;
            if (j4 >= j5) {
                this.mResetTime = j - (j4 % (j5 + 1));
                this.mIsLastFrame = true;
                j4 = j5;
            }
            long j6 = (this.mIsReverse ? this.mAnimStartTime - j4 : this.mAnimStartTime + j4) % (this.mRealTimeRange + 1);
            int size = this.mItems.size();
            AnimationItem animationItem2 = null;
            while (i < size) {
                AnimationItem animationItem3 = this.mItems.get(i);
                long j7 = animationItem3.mTime;
                if (j6 < j7) {
                    if (i == 0) {
                        j3 = 0;
                        long j8 = j7;
                        animationItem = null;
                        j2 = j8;
                    } else {
                        AnimationItem animationItem4 = this.mItems.get(i - 1);
                        long j9 = animationItem3.mTime;
                        long j10 = animationItem4.mTime;
                        animationItem = animationItem4;
                        j2 = j9 - j10;
                        j3 = j10;
                    }
                    onTick(animationItem, animationItem3, getRatio(animationItem, j6, j3, j2));
                    return;
                }
                i++;
                animationItem2 = animationItem3;
            }
            onTick((AnimationItem) null, animationItem2, 1.0f);
        }
    }
}
