package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import com.miui.maml.CommandTriggers;
import com.miui.maml.FramerateTokenList;
import com.miui.maml.NotifierManager;
import com.miui.maml.RendererController;
import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.StylesManager;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.util.StyleHelper;
import com.miui.maml.util.Utils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import org.w3c.dom.Element;

public abstract class ScreenElement {
    public static final String ACTUAL_H = "actual_h";
    public static final String ACTUAL_W = "actual_w";
    public static final String ACTUAL_X = "actual_x";
    public static final String ACTUAL_Y = "actual_y";
    private static final String LOG_TAG = "MAML ScreenElement";
    public static final String VISIBILITY = "visibility";
    public static final int VISIBILITY_FALSE = 0;
    public static final int VISIBILITY_TRUE = 1;
    private IndexedVariable mActualHeightVar;
    private IndexedVariable mActualWidthVar;
    protected Align mAlign;
    protected AlignV mAlignV;
    protected ArrayList<BaseAnimation> mAnimations;
    protected RendererController mAvailableController;
    protected String mCategory;
    private float mCurFramerate;
    private FramerateTokenList.FramerateToken mFramerateToken;
    protected boolean mHasName;
    private boolean mInitShow = true;
    private boolean mIsVisible = true;
    protected String mName;
    protected ElementGroup mParent;
    protected ScreenElementRoot mRoot;
    private boolean mShow = true;
    protected StylesManager.Style mStyle;
    protected CommandTriggers mTriggers;
    private Expression mVisibilityExpression;
    private IndexedVariable mVisibilityVar;

    /* renamed from: com.miui.maml.elements.ScreenElement$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ScreenElement$Align = new int[Align.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ScreenElement$AlignV = new int[AlignV.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(11:0|1|2|3|(2:5|6)|7|9|10|11|12|14) */
        /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0032 */
        static {
            /*
                com.miui.maml.elements.ScreenElement$Align[] r0 = com.miui.maml.elements.ScreenElement.Align.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$elements$ScreenElement$Align = r0
                r0 = 1
                int[] r1 = $SwitchMap$com$miui$maml$elements$ScreenElement$Align     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.elements.ScreenElement$Align r2 = com.miui.maml.elements.ScreenElement.Align.CENTER     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r1[r2] = r0     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                r1 = 2
                int[] r2 = $SwitchMap$com$miui$maml$elements$ScreenElement$Align     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.elements.ScreenElement$Align r3 = com.miui.maml.elements.ScreenElement.Align.RIGHT     // Catch:{ NoSuchFieldError -> 0x001f }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2[r3] = r1     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                com.miui.maml.elements.ScreenElement$AlignV[] r2 = com.miui.maml.elements.ScreenElement.AlignV.values()
                int r2 = r2.length
                int[] r2 = new int[r2]
                $SwitchMap$com$miui$maml$elements$ScreenElement$AlignV = r2
                int[] r2 = $SwitchMap$com$miui$maml$elements$ScreenElement$AlignV     // Catch:{ NoSuchFieldError -> 0x0032 }
                com.miui.maml.elements.ScreenElement$AlignV r3 = com.miui.maml.elements.ScreenElement.AlignV.CENTER     // Catch:{ NoSuchFieldError -> 0x0032 }
                int r3 = r3.ordinal()     // Catch:{ NoSuchFieldError -> 0x0032 }
                r2[r3] = r0     // Catch:{ NoSuchFieldError -> 0x0032 }
            L_0x0032:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ScreenElement$AlignV     // Catch:{ NoSuchFieldError -> 0x003c }
                com.miui.maml.elements.ScreenElement$AlignV r2 = com.miui.maml.elements.ScreenElement.AlignV.BOTTOM     // Catch:{ NoSuchFieldError -> 0x003c }
                int r2 = r2.ordinal()     // Catch:{ NoSuchFieldError -> 0x003c }
                r0[r2] = r1     // Catch:{ NoSuchFieldError -> 0x003c }
            L_0x003c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ScreenElement.AnonymousClass2.<clinit>():void");
        }
    }

    protected enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    protected enum AlignV {
        TOP,
        CENTER,
        BOTTOM
    }

    public ScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
        if (!(element == null || screenElementRoot == null)) {
            this.mStyle = screenElementRoot.getStyle(element.getAttribute(TtmlNode.TAG_STYLE));
        }
        load(element);
    }

    protected static boolean isTagEnable(String[] strArr, String str) {
        return strArr == null ? TextUtils.isEmpty(str) : Utils.arrayContains(strArr, str);
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void load(org.w3c.dom.Element r5) {
        /*
            r4 = this;
            if (r5 != 0) goto L_0x0003
            return
        L_0x0003:
            java.lang.String r0 = "category"
            java.lang.String r0 = r4.getAttr(r5, r0)
            r4.mCategory = r0
            java.lang.String r0 = "name"
            java.lang.String r0 = r4.getAttr(r5, r0)
            r4.mName = r0
            java.lang.String r0 = r4.mName
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r1 = 1
            r0 = r0 ^ r1
            r4.mHasName = r0
            boolean r0 = r4.mHasName
            if (r0 == 0) goto L_0x005a
            java.lang.String r0 = "namesSuffix"
            java.lang.String r0 = r4.getAttr(r5, r0)
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0040
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = r4.mName
            r2.append(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r4.mName = r0
        L_0x0040:
            java.lang.String r0 = "dontAddToMap"
            java.lang.String r0 = r4.getAttr(r5, r0)
            boolean r0 = java.lang.Boolean.parseBoolean(r0)
            if (r0 != 0) goto L_0x005a
            com.miui.maml.ScreenElementRoot r0 = r4.getRoot()
            java.lang.String r2 = r4.mName
            java.lang.ref.WeakReference r3 = new java.lang.ref.WeakReference
            r3.<init>(r4)
            r0.addElement(r2, r3)
        L_0x005a:
            java.lang.String r0 = "visibility"
            java.lang.String r0 = r4.getAttr(r5, r0)
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x0087
            java.lang.String r2 = "false"
            boolean r2 = r0.equalsIgnoreCase(r2)
            if (r2 == 0) goto L_0x0072
            r0 = 0
            r4.mInitShow = r0
            goto L_0x0087
        L_0x0072:
            java.lang.String r2 = "true"
            boolean r2 = r0.equalsIgnoreCase(r2)
            if (r2 == 0) goto L_0x007d
            r4.mInitShow = r1
            goto L_0x0087
        L_0x007d:
            com.miui.maml.data.Variables r1 = r4.getVariables()
            com.miui.maml.data.Expression r0 = com.miui.maml.data.Expression.build(r1, r0)
            r4.mVisibilityExpression = r0
        L_0x0087:
            com.miui.maml.elements.ScreenElement$Align r0 = com.miui.maml.elements.ScreenElement.Align.LEFT
            r4.mAlign = r0
            java.lang.String r0 = "align"
            java.lang.String r0 = r4.getAttr(r5, r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x009d
            java.lang.String r0 = "alignH"
            java.lang.String r0 = r4.getAttr(r5, r0)
        L_0x009d:
            java.lang.String r1 = "right"
            boolean r1 = r0.equalsIgnoreCase(r1)
            java.lang.String r2 = "center"
            if (r1 == 0) goto L_0x00ac
            com.miui.maml.elements.ScreenElement$Align r0 = com.miui.maml.elements.ScreenElement.Align.RIGHT
        L_0x00a9:
            r4.mAlign = r0
            goto L_0x00c0
        L_0x00ac:
            java.lang.String r1 = "left"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x00b7
            com.miui.maml.elements.ScreenElement$Align r0 = com.miui.maml.elements.ScreenElement.Align.LEFT
            goto L_0x00a9
        L_0x00b7:
            boolean r0 = r0.equalsIgnoreCase(r2)
            if (r0 == 0) goto L_0x00c0
            com.miui.maml.elements.ScreenElement$Align r0 = com.miui.maml.elements.ScreenElement.Align.CENTER
            goto L_0x00a9
        L_0x00c0:
            com.miui.maml.elements.ScreenElement$AlignV r0 = com.miui.maml.elements.ScreenElement.AlignV.TOP
            r4.mAlignV = r0
            java.lang.String r0 = "alignV"
            java.lang.String r0 = r4.getAttr(r5, r0)
            java.lang.String r1 = "bottom"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x00d7
            com.miui.maml.elements.ScreenElement$AlignV r0 = com.miui.maml.elements.ScreenElement.AlignV.BOTTOM
        L_0x00d4:
            r4.mAlignV = r0
            goto L_0x00eb
        L_0x00d7:
            java.lang.String r1 = "top"
            boolean r1 = r0.equalsIgnoreCase(r1)
            if (r1 == 0) goto L_0x00e2
            com.miui.maml.elements.ScreenElement$AlignV r0 = com.miui.maml.elements.ScreenElement.AlignV.TOP
            goto L_0x00d4
        L_0x00e2:
            boolean r0 = r0.equalsIgnoreCase(r2)
            if (r0 == 0) goto L_0x00eb
            com.miui.maml.elements.ScreenElement$AlignV r0 = com.miui.maml.elements.ScreenElement.AlignV.CENTER
            goto L_0x00d4
        L_0x00eb:
            r4.loadTriggers(r5)
            r4.loadAnimations(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ScreenElement.load(org.w3c.dom.Element):void");
    }

    private void loadAnimations(Element element) {
        Utils.traverseXmlElementChildren(element, (String) null, new Utils.XmlTraverseListener() {
            public void onChild(Element element) {
                BaseAnimation onCreateAnimation;
                String nodeName = element.getNodeName();
                if (nodeName.endsWith("Animation") && (onCreateAnimation = ScreenElement.this.onCreateAnimation(nodeName, element)) != null) {
                    ScreenElement screenElement = ScreenElement.this;
                    if (screenElement.mAnimations == null) {
                        screenElement.mAnimations = new ArrayList<>();
                    }
                    ScreenElement.this.mAnimations.add(onCreateAnimation);
                }
            }
        });
    }

    private void setVisibilityVar(boolean z) {
        if (this.mHasName) {
            if (this.mVisibilityVar == null) {
                this.mVisibilityVar = new IndexedVariable(this.mName + "." + "visibility", getContext().mVariables, true);
            }
            this.mVisibilityVar.set(z ? 1.0d : 0.0d);
        }
    }

    public void acceptVisitor(ScreenElementVisitor screenElementVisitor) {
        screenElementVisitor.visit(this);
    }

    public FramerateTokenList.FramerateToken createToken(String str) {
        RendererController rendererController = getRendererController();
        if (rendererController == null) {
            return null;
        }
        return rendererController.createToken(str);
    }

    /* access modifiers changed from: protected */
    public final double descale(double d2) {
        return d2 / ((double) this.mRoot.getScale());
    }

    /* access modifiers changed from: protected */
    public abstract void doRender(Canvas canvas);

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).tick(j);
            }
        }
    }

    /* access modifiers changed from: protected */
    public final double evaluate(Expression expression) {
        if (expression == null) {
            return 0.0d;
        }
        return expression.evaluate();
    }

    /* access modifiers changed from: protected */
    public final String evaluateStr(Expression expression) {
        if (expression == null) {
            return null;
        }
        return expression.evaluateStr();
    }

    public ScreenElement findElement(String str) {
        String str2 = this.mName;
        if (str2 == null || !str2.equals(str)) {
            return null;
        }
        return this;
    }

    public void finish() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.finish();
        }
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).finish();
            }
        }
    }

    /* access modifiers changed from: protected */
    public String getAttr(Element element, String str) {
        return StyleHelper.getAttr(element, str, this.mStyle);
    }

    /* access modifiers changed from: protected */
    public float getAttrAsFloat(Element element, String str, float f) {
        try {
            return Float.parseFloat(getAttr(element, str));
        } catch (NumberFormatException unused) {
            return f;
        }
    }

    /* access modifiers changed from: protected */
    public int getAttrAsInt(Element element, String str, int i) {
        try {
            return Integer.parseInt(getAttr(element, str));
        } catch (NumberFormatException unused) {
            return i;
        }
    }

    /* access modifiers changed from: protected */
    public long getAttrAsLong(Element element, String str, long j) {
        try {
            return Long.parseLong(getAttr(element, str));
        } catch (NumberFormatException unused) {
            return j;
        }
    }

    public ScreenContext getContext() {
        return this.mRoot.getContext();
    }

    /* access modifiers changed from: protected */
    public final float getFramerate() {
        FramerateTokenList.FramerateToken framerateToken = this.mFramerateToken;
        if (framerateToken == null) {
            return 0.0f;
        }
        return framerateToken.getFramerate();
    }

    /* access modifiers changed from: protected */
    public float getLeft(float f, float f2) {
        if (f2 <= 0.0f) {
            return f;
        }
        int i = AnonymousClass2.$SwitchMap$com$miui$maml$elements$ScreenElement$Align[this.mAlign.ordinal()];
        if (i == 1) {
            f2 /= 2.0f;
        } else if (i != 2) {
            return f;
        }
        return f - f2;
    }

    public String getName() {
        return this.mName;
    }

    /* access modifiers changed from: protected */
    public final NotifierManager getNotifierManager() {
        return NotifierManager.getInstance(getContext().mContext);
    }

    public ElementGroup getParent() {
        return this.mParent;
    }

    public RendererController getRendererController() {
        ElementGroup elementGroup = this.mParent;
        if (elementGroup != null) {
            return elementGroup.getRendererController();
        }
        return null;
    }

    public ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    /* access modifiers changed from: protected */
    public float getTop(float f, float f2) {
        if (f2 <= 0.0f) {
            return f;
        }
        int i = AnonymousClass2.$SwitchMap$com$miui$maml$elements$ScreenElement$AlignV[this.mAlignV.ordinal()];
        if (i == 1) {
            f2 /= 2.0f;
        } else if (i != 2) {
            return f;
        }
        return f - f2;
    }

    public final Variables getVariables() {
        return getContext().mVariables;
    }

    public void init() {
        this.mShow = this.mInitShow;
        FramerateTokenList.FramerateToken framerateToken = this.mFramerateToken;
        if (framerateToken != null) {
            removeToken(framerateToken);
        }
        this.mFramerateToken = null;
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.init();
        }
        setAnim((String[]) null);
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).init();
            }
        }
        updateVisibility();
        setVisibilityVar(isVisible());
        performAction("init");
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r4.mVisibilityExpression;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0013, code lost:
        r0 = r4.mParent;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isVisibleInner() {
        /*
            r4 = this;
            boolean r0 = r4.mShow
            if (r0 == 0) goto L_0x0020
            com.miui.maml.data.Expression r0 = r4.mVisibilityExpression
            if (r0 != 0) goto L_0x0009
            goto L_0x0013
        L_0x0009:
            double r0 = r0.evaluate()
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0020
        L_0x0013:
            com.miui.maml.elements.ElementGroup r0 = r4.mParent
            if (r0 != 0) goto L_0x0018
            goto L_0x001e
        L_0x0018:
            boolean r0 = r0.isVisible()
            if (r0 == 0) goto L_0x0020
        L_0x001e:
            r0 = 1
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ScreenElement.isVisibleInner():boolean");
    }

    /* access modifiers changed from: protected */
    public void loadTriggers(Element element) {
        Element child = Utils.getChild(element, CommandTriggers.TAG_NAME);
        if (child != null) {
            this.mTriggers = new CommandTriggers(child, this);
        }
    }

    /* access modifiers changed from: protected */
    public BaseAnimation onCreateAnimation(String str, Element element) {
        return null;
    }

    public boolean onHover(MotionEvent motionEvent) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onSetAnimBefore() {
    }

    /* access modifiers changed from: protected */
    public void onSetAnimEnable(BaseAnimation baseAnimation) {
    }

    public boolean onTouch(MotionEvent motionEvent) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(boolean z) {
        float f;
        setVisibilityVar(z);
        if (!z) {
            this.mCurFramerate = getFramerate();
            f = 0.0f;
        } else {
            f = this.mCurFramerate;
        }
        requestFramerate(f);
    }

    public void pause() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.pause();
        }
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).pause();
            }
        }
    }

    public final void pauseAnim() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        pauseAnim(elapsedRealtime);
        doTick(elapsedRealtime);
    }

    /* access modifiers changed from: protected */
    public void pauseAnim(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).pauseAnim(j);
            }
        }
    }

    public void performAction(String str) {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null && str != null) {
            commandTriggers.onAction(str);
            requestUpdate();
        }
    }

    public final void playAnim() {
        playAnim(0, -1, true, true);
    }

    /* access modifiers changed from: protected */
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).playAnim(j, j2, j3, z, z2);
            }
        }
    }

    public final void playAnim(long j, long j2, boolean z, boolean z2) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        playAnim(elapsedRealtime, j, j2, z, z2);
        doTick(elapsedRealtime);
    }

    /* access modifiers changed from: protected */
    public final void postInMainThread(Runnable runnable) {
        getContext().getHandler().post(runnable);
    }

    public void postRunnable(Runnable runnable) {
        RendererController rendererController = this.mRoot.getRendererController();
        if (rendererController != null) {
            rendererController.postRunnable(runnable);
        }
    }

    public void postRunnableAtFrontOfQueue(Runnable runnable) {
        RendererController rendererController = this.mRoot.getRendererController();
        if (rendererController != null) {
            rendererController.postRunnableAtFrontOfQueue(runnable);
        }
    }

    public void removeToken(FramerateTokenList.FramerateToken framerateToken) {
        RendererController rendererController = getRendererController();
        if (rendererController != null) {
            rendererController.removeToken(framerateToken);
        }
    }

    public void render(Canvas canvas) {
        updateVisibility();
        if (isVisible()) {
            doRender(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public final void requestFramerate(float f) {
        if (f >= 0.0f) {
            if (this.mFramerateToken == null) {
                if (f != 0.0f) {
                    this.mFramerateToken = createToken(toString());
                } else {
                    return;
                }
            }
            if (this.mFramerateToken != null) {
                float systemFrameRate = this.mRoot.getSystemFrameRate();
                FramerateTokenList.FramerateToken framerateToken = this.mFramerateToken;
                if (f > systemFrameRate) {
                    f = systemFrameRate;
                }
                framerateToken.requestFramerate(f);
            }
        }
    }

    public void requestUpdate() {
        RendererController rendererController = getRendererController();
        if (rendererController != null) {
            rendererController.requestUpdate();
        }
    }

    public final void reset() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        reset(elapsedRealtime);
        doTick(elapsedRealtime);
    }

    public void reset(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).reset(j);
            }
        }
    }

    public void resume() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.resume();
        }
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).resume();
            }
        }
    }

    public final void resumeAnim() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        resumeAnim(elapsedRealtime);
        doTick(elapsedRealtime);
    }

    /* access modifiers changed from: protected */
    public void resumeAnim(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).resumeAnim(j);
            }
        }
    }

    /* access modifiers changed from: protected */
    public final float scale(double d2) {
        return (float) (d2 * ((double) this.mRoot.getScale()));
    }

    /* access modifiers changed from: protected */
    public void setActualHeight(double d2) {
        if (this.mHasName) {
            if (this.mActualHeightVar == null) {
                this.mActualHeightVar = new IndexedVariable(this.mName + "." + ACTUAL_H, getVariables(), true);
            }
            this.mActualHeightVar.set(d2);
        }
    }

    /* access modifiers changed from: protected */
    public void setActualWidth(double d2) {
        if (this.mHasName) {
            if (this.mActualWidthVar == null) {
                this.mActualWidthVar = new IndexedVariable(this.mName + "." + ACTUAL_W, getVariables(), true);
            }
            this.mActualWidthVar.set(d2);
        }
    }

    public void setAnim(String[] strArr) {
        if (this.mAnimations != null) {
            onSetAnimBefore();
            int size = this.mAnimations.size();
            for (int i = 0; i < size; i++) {
                BaseAnimation baseAnimation = this.mAnimations.get(i);
                boolean isTagEnable = isTagEnable(strArr, baseAnimation.getTag());
                baseAnimation.setDisable(!isTagEnable);
                if (isTagEnable) {
                    onSetAnimEnable(baseAnimation);
                }
            }
        }
    }

    public final void setAnimations(String str) {
        setAnim((TextUtils.isEmpty(str) || ".".equals(str)) ? null : str.split(","));
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setName(String str) {
        getRoot().removeElement(this.mName);
        this.mName = str;
        getRoot().addElement(str, new WeakReference(this));
    }

    public void setParent(ElementGroup elementGroup) {
        this.mParent = elementGroup;
    }

    public void show(boolean z) {
        this.mShow = z;
        updateVisibility();
        requestUpdate();
    }

    public void showCategory(String str, boolean z) {
        String str2 = this.mCategory;
        if (str2 != null && str2.equals(str)) {
            show(z);
        }
    }

    public void tick(long j) {
        updateVisibility();
        if (isVisible()) {
            doTick(j);
        }
    }

    /* access modifiers changed from: protected */
    public void updateVisibility() {
        boolean isVisibleInner = isVisibleInner();
        if (this.mIsVisible != isVisibleInner) {
            this.mIsVisible = isVisibleInner;
            onVisibilityChange(this.mIsVisible);
            if (isVisibleInner) {
                doTick(SystemClock.elapsedRealtime());
            }
        }
    }
}
