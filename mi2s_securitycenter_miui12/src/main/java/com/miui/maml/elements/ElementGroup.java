package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.util.Log;
import android.view.MotionEvent;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.IndexedVariable;
import java.util.ArrayList;
import org.w3c.dom.Element;

public class ElementGroup extends AnimatedScreenElement {
    private static final String LOG_TAG = "MAML_ElementGroup";
    public static final String TAG_NAME = "ElementGroup";
    public static final String TAG_NAME1 = "Group";
    protected boolean mClip;
    protected ArrayList<ScreenElement> mElements = new ArrayList<>();
    private boolean mHovered;
    private IndexedVariable mIndexVar;
    private boolean mLayered;
    private LinearDirection mLinearDirection = LinearDirection.None;
    private boolean mTouched;

    /* renamed from: com.miui.maml.elements.ElementGroup$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection = new int[LinearDirection.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        static {
            /*
                com.miui.maml.elements.ElementGroup$LinearDirection[] r0 = com.miui.maml.elements.ElementGroup.LinearDirection.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection = r0
                int[] r0 = $SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.elements.ElementGroup$LinearDirection r1 = com.miui.maml.elements.ElementGroup.LinearDirection.Horizontal     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.elements.ElementGroup$LinearDirection r1 = com.miui.maml.elements.ElementGroup.LinearDirection.Vertical     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ElementGroup.AnonymousClass1.<clinit>():void");
        }
    }

    private enum LinearDirection {
        None,
        Horizontal,
        Vertical
    }

    private ElementGroup(ScreenElementRoot screenElementRoot, IndexedVariable indexedVariable) {
        super((Element) null, screenElementRoot);
        this.mIndexVar = indexedVariable;
    }

    public ElementGroup(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    public static ElementGroup createArrayGroup(ScreenElementRoot screenElementRoot, IndexedVariable indexedVariable) {
        return new ElementGroup(screenElementRoot, indexedVariable);
    }

    public static boolean isArrayGroup(ScreenElement screenElement) {
        return (screenElement instanceof ElementGroup) && ((ElementGroup) screenElement).isArray();
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0044  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void load(org.w3c.dom.Element r5) {
        /*
            r4 = this;
            if (r5 != 0) goto L_0x0003
            return
        L_0x0003:
            java.lang.String r0 = "clip"
            java.lang.String r0 = r4.getAttr(r5, r0)
            boolean r0 = java.lang.Boolean.parseBoolean(r0)
            r4.mClip = r0
            java.lang.String r0 = "layered"
            java.lang.String r0 = r4.getAttr(r5, r0)
            boolean r0 = java.lang.Boolean.parseBoolean(r0)
            r4.mLayered = r0
            java.lang.String r0 = "linear"
            java.lang.String r0 = r4.getAttr(r5, r0)
            java.lang.String r1 = "h"
            boolean r1 = r1.equalsIgnoreCase(r0)
            if (r1 == 0) goto L_0x002e
            com.miui.maml.elements.ElementGroup$LinearDirection r0 = com.miui.maml.elements.ElementGroup.LinearDirection.Horizontal
        L_0x002b:
            r4.mLinearDirection = r0
            goto L_0x0039
        L_0x002e:
            java.lang.String r1 = "v"
            boolean r0 = r1.equalsIgnoreCase(r0)
            if (r0 == 0) goto L_0x0039
            com.miui.maml.elements.ElementGroup$LinearDirection r0 = com.miui.maml.elements.ElementGroup.LinearDirection.Vertical
            goto L_0x002b
        L_0x0039:
            org.w3c.dom.NodeList r5 = r5.getChildNodes()
            int r0 = r5.getLength()
            r1 = 0
        L_0x0042:
            if (r1 >= r0) goto L_0x005f
            org.w3c.dom.Node r2 = r5.item(r1)
            short r2 = r2.getNodeType()
            r3 = 1
            if (r2 != r3) goto L_0x005c
            org.w3c.dom.Node r2 = r5.item(r1)
            org.w3c.dom.Element r2 = (org.w3c.dom.Element) r2
            com.miui.maml.elements.ScreenElement r2 = r4.onCreateChild(r2)
            r4.addElement(r2)
        L_0x005c:
            int r1 = r1 + 1
            goto L_0x0042
        L_0x005f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ElementGroup.load(org.w3c.dom.Element):void");
    }

    public void acceptVisitor(ScreenElementVisitor screenElementVisitor) {
        super.acceptVisitor(screenElementVisitor);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).acceptVisitor(screenElementVisitor);
        }
    }

    public void addElement(ScreenElement screenElement) {
        if (screenElement != null) {
            screenElement.setParent(this);
            this.mElements.add(screenElement);
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        int i;
        float width = getWidth();
        float height = getHeight();
        float left = getLeft(0.0f, width);
        float top = getTop(0.0f, height);
        if (!this.mLayered || width <= 0.0f || height <= 0.0f) {
            i = canvas.save();
        } else {
            i = canvas.saveLayerAlpha(left, top, left + width, top + height, getAlpha(), 31);
        }
        canvas.translate(left, top);
        if (width > 0.0f && height > 0.0f && this.mClip) {
            canvas.clipRect(0.0f, 0.0f, width, height);
        }
        doRenderChildren(canvas);
        canvas.restoreToCount(i);
    }

    /* access modifiers changed from: protected */
    public void doRenderChildren(Canvas canvas) {
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            ScreenElement screenElement = this.mElements.get(i);
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set((double) i);
            }
            screenElement.render(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        doTickChildren(j);
    }

    /* access modifiers changed from: protected */
    public void doTickChildren(long j) {
        float f;
        int size = this.mElements.size();
        float f2 = 0.0f;
        float f3 = 0.0f;
        for (int i = 0; i < size; i++) {
            ScreenElement screenElement = this.mElements.get(i);
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set((double) i);
            }
            screenElement.tick(j);
            if (this.mLinearDirection != LinearDirection.None && (screenElement instanceof AnimatedScreenElement) && screenElement.isVisible()) {
                AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) screenElement;
                int i2 = AnonymousClass1.$SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection[this.mLinearDirection.ordinal()];
                if (i2 == 1) {
                    float marginLeft = f2 + animatedScreenElement.getMarginLeft();
                    animatedScreenElement.setX((double) marginLeft);
                    f2 = marginLeft + animatedScreenElement.getWidth() + animatedScreenElement.getMarginRight();
                    f = animatedScreenElement.getHeight();
                    if (f3 >= f) {
                    }
                } else if (i2 == 2) {
                    float marginTop = f2 + animatedScreenElement.getMarginTop();
                    animatedScreenElement.setY((double) marginTop);
                    f2 = marginTop + animatedScreenElement.getHeight() + animatedScreenElement.getMarginBottom();
                    f = animatedScreenElement.getWidth();
                    if (f3 >= f) {
                    }
                }
                f3 = f;
            }
        }
        int i3 = AnonymousClass1.$SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection[this.mLinearDirection.ordinal()];
        if (i3 == 1) {
            double d2 = (double) f2;
            setWidth(d2);
            double d3 = (double) f3;
            setHeight(d3);
            setActualWidth(descale(d2));
            setActualHeight(descale(d3));
        } else if (i3 == 2) {
            double d4 = (double) f2;
            setHeight(d4);
            double d5 = (double) f3;
            setWidth(d5);
            setActualHeight(descale(d4));
            setActualWidth(descale(d5));
        }
    }

    public ScreenElement findElement(String str) {
        ScreenElement findElement = super.findElement(str);
        if (findElement != null) {
            return findElement;
        }
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            ScreenElement findElement2 = this.mElements.get(i).findElement(str);
            if (findElement2 != null) {
                return findElement2;
            }
        }
        return null;
    }

    public void finish() {
        super.finish();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            try {
                this.mElements.get(i).finish();
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                e.printStackTrace();
            }
        }
    }

    public ScreenElement getChild(int i) {
        if (i < 0 || i >= this.mElements.size()) {
            return null;
        }
        return this.mElements.get(i);
    }

    public ArrayList<ScreenElement> getElements() {
        return this.mElements;
    }

    /* access modifiers changed from: protected */
    public float getParentLeft() {
        float left = getLeft();
        ElementGroup elementGroup = this.mParent;
        return left + (elementGroup == null ? 0.0f : elementGroup.getParentLeft());
    }

    /* access modifiers changed from: protected */
    public float getParentTop() {
        float top = getTop();
        ElementGroup elementGroup = this.mParent;
        return top + (elementGroup == null ? 0.0f : elementGroup.getParentTop());
    }

    public int getSize() {
        return this.mElements.size();
    }

    public void init() {
        super.init();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set((double) i);
            }
            this.mElements.get(i).init();
        }
    }

    public boolean isArray() {
        return this.mIndexVar != null;
    }

    public boolean isLayered() {
        return this.mLayered;
    }

    /* access modifiers changed from: protected */
    public ScreenElement onCreateChild(Element element) {
        return getContext().mFactory.createInstance(element, this.mRoot);
    }

    public boolean onHover(MotionEvent motionEvent) {
        boolean z = false;
        if (!isVisible()) {
            return false;
        }
        boolean z2 = touched(motionEvent.getX(), motionEvent.getY());
        if (this.mClip && !z2) {
            if (!this.mHovered) {
                return false;
            }
            motionEvent.setAction(10);
        }
        boolean z3 = true;
        int size = this.mElements.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            }
            ScreenElement screenElement = this.mElements.get(size);
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set((double) size);
            }
            if (screenElement.onHover(motionEvent)) {
                z = true;
                break;
            }
            size--;
        }
        if (!z) {
            z3 = super.onHover(motionEvent);
        }
        this.mHovered = z3;
        return this.mHovered;
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x007d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouch(android.view.MotionEvent r10) {
        /*
            r9 = this;
            boolean r0 = r9.isVisible()
            r1 = 0
            if (r0 != 0) goto L_0x0008
            return r1
        L_0x0008:
            int r0 = r10.getAction()
            float r2 = r10.getX()
            float r3 = r10.getY()
            boolean r2 = r9.touched(r2, r3)
            boolean r3 = r9.mClip
            if (r3 == 0) goto L_0x0027
            if (r2 != 0) goto L_0x0027
            boolean r2 = r9.mTouched
            if (r2 != 0) goto L_0x0023
            return r1
        L_0x0023:
            r2 = 3
            r10.setAction(r2)
        L_0x0027:
            java.util.ArrayList<com.miui.maml.elements.ScreenElement> r2 = r9.mElements
            int r2 = r2.size()
            com.miui.maml.ScreenElementRoot r3 = r9.mRoot
            int r3 = r3.version()
            r4 = 2
            r5 = 1
            if (r3 < r4) goto L_0x0039
            r3 = r5
            goto L_0x003a
        L_0x0039:
            r3 = r1
        L_0x003a:
            if (r3 == 0) goto L_0x0059
            int r2 = r2 - r5
        L_0x003d:
            if (r2 < 0) goto L_0x0077
            java.util.ArrayList<com.miui.maml.elements.ScreenElement> r3 = r9.mElements
            java.lang.Object r3 = r3.get(r2)
            com.miui.maml.elements.ScreenElement r3 = (com.miui.maml.elements.ScreenElement) r3
            com.miui.maml.data.IndexedVariable r4 = r9.mIndexVar
            if (r4 == 0) goto L_0x004f
            double r6 = (double) r2
            r4.set((double) r6)
        L_0x004f:
            boolean r3 = r3.onTouch(r10)
            if (r3 == 0) goto L_0x0056
            goto L_0x0072
        L_0x0056:
            int r2 = r2 + -1
            goto L_0x003d
        L_0x0059:
            r3 = r1
        L_0x005a:
            if (r3 >= r2) goto L_0x0077
            java.util.ArrayList<com.miui.maml.elements.ScreenElement> r4 = r9.mElements
            java.lang.Object r4 = r4.get(r3)
            com.miui.maml.elements.ScreenElement r4 = (com.miui.maml.elements.ScreenElement) r4
            com.miui.maml.data.IndexedVariable r6 = r9.mIndexVar
            if (r6 == 0) goto L_0x006c
            double r7 = (double) r3
            r6.set((double) r7)
        L_0x006c:
            boolean r4 = r4.onTouch(r10)
            if (r4 == 0) goto L_0x0074
        L_0x0072:
            r1 = r5
            goto L_0x0077
        L_0x0074:
            int r3 = r3 + 1
            goto L_0x005a
        L_0x0077:
            r10.setAction(r0)
            if (r1 == 0) goto L_0x007d
            goto L_0x0081
        L_0x007d:
            boolean r5 = super.onTouch(r10)
        L_0x0081:
            r9.mTouched = r5
            boolean r10 = r9.mTouched
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ElementGroup.onTouch(android.view.MotionEvent):boolean");
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).updateVisibility();
        }
    }

    public void pause() {
        super.pause();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).pause();
        }
    }

    public void pauseAnim(long j) {
        super.pauseAnim(j);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).pauseAnim(j);
        }
    }

    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set((double) i);
            }
            this.mElements.get(i).playAnim(j, j2, j3, z, z2);
        }
    }

    public void removeAllElements() {
        this.mElements.clear();
        requestUpdate();
    }

    public void removeElement(ScreenElement screenElement) {
        this.mElements.remove(screenElement);
        requestUpdate();
    }

    public void reset(long j) {
        super.reset(j);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).reset(j);
        }
    }

    public void resume() {
        super.resume();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).resume();
        }
    }

    public void resumeAnim(long j) {
        super.resumeAnim(j);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).resumeAnim(j);
        }
    }

    public void setAnim(String[] strArr) {
        super.setAnim(strArr);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set((double) i);
            }
            this.mElements.get(i).setAnim(strArr);
        }
    }

    public void setClip(boolean z) {
        this.mClip = z;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).setColorFilter(colorFilter);
        }
    }

    public void showCategory(String str, boolean z) {
        super.showCategory(str, z);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).showCategory(str, z);
        }
    }
}
