package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.animation.SourcesAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.BitmapProvider;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.util.TextFormatter;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.graphics.BitmapFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ImageScreenElement extends AnimatedScreenElement implements BitmapProvider.IBitmapHolder {
    private static final String LOG_TAG = "ImageScreenElement";
    public static final String MASK_TAG_NAME = "Mask";
    private static final double PI = 3.1415926535897d;
    public static final String TAG_NAME = "Image";
    private static final String VAR_BMP_HEIGHT = "bmp_height";
    private static final String VAR_BMP_WIDTH = "bmp_width";
    private static final String VAR_HAS_BITMAP = "has_bitmap";
    private boolean mAntiAlias;
    protected BitmapProvider.VersionedBitmap mBitmap = new BitmapProvider.VersionedBitmap((Bitmap) null);
    private BitmapProvider mBitmapProvider;
    private Bitmap mBlurBitmap;
    private int mBlurRadius;
    private IndexedVariable mBmpSizeHeightVar;
    private IndexedVariable mBmpSizeWidthVar;
    protected BitmapProvider.VersionedBitmap mCurrentBitmap = new BitmapProvider.VersionedBitmap((Bitmap) null);
    private Rect mDesRect = new Rect();
    private Expression mExpH;
    private Expression mExpSrcH;
    private Expression mExpSrcW;
    private Expression mExpSrcX;
    private Expression mExpSrcY;
    private Expression mExpW;
    private int mH = -1;
    private IndexedVariable mHasBitmapVar;
    private boolean mHasSrcRect;
    private boolean mHasWidthAndHeight;
    private boolean mLoadAsync;
    private Paint mMaskPaint = new Paint();
    private ArrayList<Mask> mMasks;
    private int mMeshHeight;
    private float[] mMeshVerts;
    private int mMeshWidth;
    protected Paint mPaint = new Paint();
    private boolean mPendingBlur;
    private int mRawBlurRadius;
    private boolean mRetainWhenInvisible;
    private pair<Double, Double> mRotateXYpair;
    private SourcesAnimation mSources;
    private String mSrc;
    private TextFormatter mSrcFormatter;
    private int mSrcH;
    private Expression mSrcIdExpression;
    private Rect mSrcRect;
    private int mSrcW;
    private int mSrcX;
    private int mSrcY;
    private int mW = -1;
    private Expression mXfermodeNumExp;

    private class Mask extends ImageScreenElement {
        private boolean mAlignAbsolute;

        public Mask(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot);
            if (getAttr(element, "align").equalsIgnoreCase("absolute")) {
                this.mAlignAbsolute = true;
            }
        }

        /* access modifiers changed from: protected */
        public void doRender(Canvas canvas) {
        }

        public final boolean isAlignAbsolute() {
            return this.mAlignAbsolute;
        }
    }

    private static class pair<T1, T2> {
        public T1 p1;
        public T2 p2;

        private pair() {
        }
    }

    public ImageScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private void load(Element element) {
        if (element != null) {
            Variables variables = getVariables();
            this.mSrcFormatter = TextFormatter.fromElement(variables, element, "src", "srcFormat", "srcParas", "srcExp", "srcFormatExp");
            this.mSrcIdExpression = Expression.build(variables, getAttr(element, "srcid"));
            this.mAntiAlias = !getAttr(element, "antiAlias").equals("false");
            this.mPaint.setFilterBitmap(this.mAntiAlias);
            this.mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            this.mMaskPaint.setFilterBitmap(this.mAntiAlias);
            this.mExpSrcX = Expression.build(variables, getAttr(element, "srcX"));
            this.mExpSrcY = Expression.build(variables, getAttr(element, "srcY"));
            this.mExpSrcW = Expression.build(variables, getAttr(element, "srcW"));
            this.mExpSrcH = Expression.build(variables, getAttr(element, "srcH"));
            this.mExpW = Expression.build(variables, getAttr(element, AnimatedProperty.PROPERTY_NAME_W));
            this.mExpH = Expression.build(variables, getAttr(element, AnimatedProperty.PROPERTY_NAME_H));
            if (!(this.mExpSrcW == null || this.mExpSrcH == null)) {
                this.mHasSrcRect = true;
                this.mSrcRect = new Rect();
            }
            if (!(this.mExpH == null || this.mExpW == null)) {
                this.mHasWidthAndHeight = true;
            }
            this.mRawBlurRadius = getAttrAsInt(element, "blur", 0);
            loadMesh(element);
            this.mXfermodeNumExp = Expression.build(variables, getAttr(element, "xfermodeNum"));
            if (this.mXfermodeNumExp == null) {
                this.mPaint.setXfermode(new PorterDuffXfermode(Utils.getPorterDuffMode(getAttr(element, "xfermode"))));
            }
            boolean parseBoolean = Boolean.parseBoolean(getAttr(element, "useVirtualScreen"));
            String attr = getAttr(element, "srcType");
            if (parseBoolean) {
                attr = "VirtualScreen";
            }
            setSrcType(attr);
            this.mLoadAsync = Boolean.parseBoolean(getAttr(element, "loadAsync"));
            this.mRetainWhenInvisible = Boolean.parseBoolean(getAttr(element, "retainWhenInvisible"));
            if (this.mHasName) {
                this.mBmpSizeWidthVar = new IndexedVariable(this.mName + "." + VAR_BMP_WIDTH, variables, true);
                this.mBmpSizeHeightVar = new IndexedVariable(this.mName + "." + VAR_BMP_HEIGHT, variables, true);
                this.mHasBitmapVar = new IndexedVariable(this.mName + "." + VAR_HAS_BITMAP, variables, true);
            }
            loadMask(element);
        }
    }

    private void loadMask(Element element) {
        if (this.mMasks == null) {
            this.mMasks = new ArrayList<>();
        }
        this.mMasks.clear();
        NodeList elementsByTagName = element.getElementsByTagName(MASK_TAG_NAME);
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            this.mMasks.add(new Mask((Element) elementsByTagName.item(i), this.mRoot));
        }
    }

    private void renderWithMask(Canvas canvas, Mask mask, int i, int i2) {
        double d2;
        double d3;
        Canvas canvas2 = canvas;
        int i3 = i;
        int i4 = i2;
        Bitmap bitmap = getContext().mResourceManager.getBitmap(mask.getSrc());
        if (bitmap != null) {
            canvas.save();
            double x = (double) mask.getX();
            double y = (double) mask.getY();
            float rotation = mask.getRotation();
            if (mask.isAlignAbsolute()) {
                float rotation2 = getRotation();
                if (rotation2 == 0.0f) {
                    d2 = x - ((double) i3);
                    d3 = y - ((double) i4);
                } else {
                    float f = rotation - rotation2;
                    double d4 = (((double) rotation2) * PI) / 180.0d;
                    float pivotX = getPivotX();
                    float pivotY = getPivotY();
                    if (this.mRotateXYpair == null) {
                        this.mRotateXYpair = new pair<>();
                    }
                    double d5 = (double) pivotY;
                    rotateXY((double) pivotX, d5, d4, this.mRotateXYpair);
                    double doubleValue = ((double) i3) + ((Double) this.mRotateXYpair.p1).doubleValue();
                    double doubleValue2 = ((double) i4) + ((Double) this.mRotateXYpair.p2).doubleValue();
                    rotateXY(descale((double) mask.getPivotX()), descale((double) mask.getPivotY()), (((double) mask.getRotation()) * PI) / 180.0d, this.mRotateXYpair);
                    double scale = (x + ((double) scale(((Double) this.mRotateXYpair.p1).doubleValue()))) - doubleValue;
                    double scale2 = (y + ((double) scale(((Double) this.mRotateXYpair.p2).doubleValue()))) - doubleValue2;
                    double sqrt = Math.sqrt((scale * scale) + (scale2 * scale2));
                    double asin = Math.asin(scale / sqrt);
                    double d6 = scale2 > 0.0d ? d4 + asin : (d4 + PI) - asin;
                    d2 = sqrt * Math.sin(d6);
                    d3 = sqrt * Math.cos(d6);
                    rotation = f;
                }
                x = d2 - ((double) getX());
                y = d3 - ((double) getY());
            } else {
                double d7 = y;
            }
            canvas2.rotate(rotation, (float) (((double) mask.getPivotX()) + x + ((double) i3)), (float) (((double) mask.getPivotY()) + y + ((double) i4)));
            int i5 = (int) x;
            int i6 = (int) y;
            int round = Math.round(mask.getWidth());
            if (round < 0) {
                round = bitmap.getWidth();
            }
            int round2 = Math.round(mask.getHeight());
            if (round2 < 0) {
                round2 = bitmap.getHeight();
            }
            int i7 = round2;
            int i8 = i5 + i3;
            int i9 = i6 + i4;
            this.mDesRect.set(i8, i9, round + i8, i7 + i9);
            this.mMaskPaint.setAlpha(mask.getAlpha());
            canvas2.drawBitmap(bitmap, (Rect) null, this.mDesRect, this.mMaskPaint);
            canvas.restore();
        }
    }

    private void rotateXY(double d2, double d3, double d4, pair<Double, Double> pair2) {
        double sqrt = Math.sqrt((d2 * d2) + (d3 * d3));
        Object valueOf = Double.valueOf(0.0d);
        if (sqrt > 0.0d) {
            double acos = (PI - Math.acos(d2 / sqrt)) - d4;
            pair2.p1 = Double.valueOf(d2 + (Math.cos(acos) * sqrt));
            pair2.p2 = Double.valueOf(d3 - (sqrt * Math.sin(acos)));
            return;
        }
        pair2.p1 = valueOf;
        pair2.p2 = valueOf;
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        int i;
        int i2;
        int i3;
        Canvas canvas2 = canvas;
        Bitmap bitmap = this.mCurrentBitmap.getBitmap();
        if (bitmap != null) {
            if (this.mPendingBlur) {
                if (!(this.mBlurBitmap != null && bitmap.getWidth() == this.mBlurBitmap.getWidth() && bitmap.getHeight() == this.mBlurBitmap.getHeight())) {
                    this.mBlurBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                }
                this.mPendingBlur = false;
                this.mBlurBitmap = BitmapFactory.fastBlur(bitmap, this.mBlurBitmap, this.mBlurRadius);
            }
            Bitmap bitmap2 = this.mBlurBitmap;
            Bitmap bitmap3 = (bitmap2 == null || this.mBlurRadius <= 0) ? bitmap : bitmap2;
            this.mPaint.setAlpha(getAlpha());
            int density = canvas.getDensity();
            canvas2.setDensity(0);
            float width = getWidth();
            float height = getHeight();
            float width2 = super.getWidth();
            float height2 = super.getHeight();
            if (width != 0.0f && height != 0.0f) {
                int left = (int) getLeft(0.0f, width);
                int top = (int) getTop(0.0f, height);
                canvas.save();
                if (this.mMasks.size() != 0) {
                    float f = (float) left;
                    float f2 = (float) top;
                    float ceil = (float) (((int) Math.ceil((double) width)) + left);
                    float f3 = ceil;
                    float f4 = f2;
                    float f5 = f;
                    int i4 = top;
                    int i5 = left;
                    canvas.saveLayer(f, f2, f3, (float) (((int) Math.ceil((double) height)) + top), this.mPaint, 31);
                    if (width2 > 0.0f || height2 > 0.0f || this.mSrcRect != null) {
                        i2 = i5;
                        i = i4;
                        this.mDesRect.set(i2, i, i2 + ((int) width), i + ((int) height));
                        Rect rect = this.mSrcRect;
                        if (rect != null) {
                            int i6 = this.mSrcX;
                            int i7 = this.mSrcY;
                            rect.set(i6, i7, this.mSrcW + i6, this.mSrcH + i7);
                        }
                        canvas2.drawBitmap(bitmap3, this.mSrcRect, this.mDesRect, this.mPaint);
                    } else {
                        canvas2.drawBitmap(bitmap3, f5, f4, this.mPaint);
                        i = i4;
                        i2 = i5;
                    }
                    Iterator<Mask> it = this.mMasks.iterator();
                    while (it.hasNext()) {
                        renderWithMask(canvas2, it.next(), i2, i);
                    }
                    canvas.restore();
                } else if (bitmap3.getNinePatchChunk() != null) {
                    NinePatch ninePatch = getContext().mResourceManager.getNinePatch(getSrc());
                    if (ninePatch != null) {
                        this.mDesRect.set(left, top, (int) (((float) left) + width), (int) (((float) top) + height));
                        ninePatch.draw(canvas2, this.mDesRect, this.mPaint);
                    } else {
                        Log.e(LOG_TAG, "the image contains ninepatch chunk but couldn't get NinePatch object: " + getSrc());
                    }
                } else if (width2 > 0.0f || height2 > 0.0f || this.mSrcRect != null) {
                    this.mDesRect.set(left, top, (int) (((float) left) + width), (int) (((float) top) + height));
                    Rect rect2 = this.mSrcRect;
                    if (rect2 != null) {
                        int i8 = this.mSrcX;
                        int i9 = this.mSrcY;
                        rect2.set(i8, i9, this.mSrcW + i8, this.mSrcH + i9);
                    }
                    canvas2.drawBitmap(bitmap3, this.mSrcRect, this.mDesRect, this.mPaint);
                } else {
                    int i10 = this.mMeshWidth;
                    if (i10 <= 0 || (i3 = this.mMeshHeight) <= 0) {
                        canvas2.drawBitmap(bitmap3, (float) left, (float) top, this.mPaint);
                    } else {
                        canvas.drawBitmapMesh(bitmap3, i10, i3, this.mMeshVerts, 0, (int[]) null, 0, this.mPaint);
                    }
                }
                canvas.restore();
                canvas2.setDensity(density);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        if (isVisible()) {
            TextFormatter textFormatter = this.mSrcFormatter;
            this.mSrc = textFormatter != null ? textFormatter.getText() : null;
            ArrayList<Mask> arrayList = this.mMasks;
            if (arrayList != null) {
                Iterator<Mask> it = arrayList.iterator();
                while (it.hasNext()) {
                    it.next().doTick(j);
                }
            }
            Expression expression = this.mXfermodeNumExp;
            if (expression != null) {
                this.mPaint.setXfermode(new PorterDuffXfermode(Utils.getPorterDuffMode((int) expression.evaluate())));
            }
            if (this.mHasSrcRect) {
                this.mSrcX = (int) scale(evaluate(this.mExpSrcX));
                this.mSrcY = (int) scale(evaluate(this.mExpSrcY));
                this.mSrcW = (int) scale(evaluate(this.mExpSrcW));
                this.mSrcH = (int) scale(evaluate(this.mExpSrcH));
            }
            if (this.mHasWidthAndHeight) {
                this.mW = (int) scale(evaluate(this.mExpW));
                this.mH = (int) scale(evaluate(this.mExpH));
            }
            if (this.mTintChanged) {
                this.mPaint.setColorFilter(this.mTintFilter);
            }
            updateBitmap(this.mLoadAsync);
        }
    }

    public void finish() {
        super.finish();
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.finish();
        }
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().finish();
            }
        }
        this.mBitmap.reset();
        this.mCurrentBitmap.reset();
        this.mBlurBitmap = null;
    }

    public BitmapProvider.VersionedBitmap getBitmap(String str) {
        return this.mCurrentBitmap;
    }

    /* access modifiers changed from: protected */
    public BitmapProvider.VersionedBitmap getBitmap(boolean z) {
        if (this.mBitmap.getBitmap() != null) {
            return this.mBitmap;
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            return bitmapProvider.getBitmap(getSrc(), !z, this.mW, this.mH);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public int getBitmapHeight() {
        Bitmap bitmap = this.mCurrentBitmap.getBitmap();
        if (bitmap != null) {
            return bitmap.getHeight();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getBitmapWidth() {
        Bitmap bitmap = this.mCurrentBitmap.getBitmap();
        if (bitmap != null) {
            return bitmap.getWidth();
        }
        return 0;
    }

    public float getHeight() {
        float height = super.getHeight();
        if (height >= 0.0f) {
            return height;
        }
        return (float) (this.mHasSrcRect ? this.mSrcH : getBitmapHeight());
    }

    public final String getSrc() {
        Expression expression;
        SourcesAnimation sourcesAnimation = this.mSources;
        String src = sourcesAnimation != null ? sourcesAnimation.getSrc() : this.mSrc;
        return (src == null || (expression = this.mSrcIdExpression) == null) ? src : Utils.addFileNameSuffix(src, String.valueOf((long) expression.evaluate()));
    }

    public float getWidth() {
        float width = super.getWidth();
        if (width >= 0.0f) {
            return width;
        }
        return (float) (this.mHasSrcRect ? this.mSrcW : getBitmapWidth());
    }

    public float getX() {
        float x = super.getX();
        SourcesAnimation sourcesAnimation = this.mSources;
        return sourcesAnimation != null ? x + scale(sourcesAnimation.getX()) : x;
    }

    public float getY() {
        float y = super.getY();
        SourcesAnimation sourcesAnimation = this.mSources;
        return sourcesAnimation != null ? y + scale(sourcesAnimation.getY()) : y;
    }

    public void init() {
        super.init();
        TextFormatter textFormatter = this.mSrcFormatter;
        this.mSrc = textFormatter != null ? textFormatter.getText() : null;
        this.mBitmap.reset();
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().init();
            }
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.init(getSrc());
        }
        if (isVisible()) {
            updateBitmap(this.mLoadAsync);
        }
        this.mBlurRadius = (int) scale((double) this.mRawBlurRadius);
        if (this.mBlurRadius > 0) {
            this.mPendingBlur = true;
        }
    }

    /* access modifiers changed from: protected */
    public void loadMesh(Element element) {
        String attr = getAttr(element, "mesh");
        int indexOf = attr.indexOf(",");
        if (indexOf != -1) {
            try {
                this.mMeshWidth = Integer.parseInt(attr.substring(0, indexOf));
                this.mMeshHeight = Integer.parseInt(attr.substring(indexOf + 1));
            } catch (NumberFormatException unused) {
                Log.w(LOG_TAG, "Invalid mesh format:" + attr);
            }
            if (this.mMeshWidth != 0 && this.mMeshHeight != 0) {
                String attr2 = getAttr(element, "meshVertsArr");
                Object obj = getVariables().get(attr2);
                if (obj == null || !(obj instanceof float[])) {
                    this.mMeshHeight = 0;
                    this.mMeshWidth = 0;
                    Log.w(LOG_TAG, "Invalid meshVertsArr:" + attr2 + "  undifined or not float[] type");
                    return;
                }
                this.mMeshVerts = (float[]) obj;
            }
        }
    }

    /* access modifiers changed from: protected */
    public BaseAnimation onCreateAnimation(String str, Element element) {
        if (!SourcesAnimation.TAG_NAME.equals(str)) {
            return super.onCreateAnimation(str, element);
        }
        SourcesAnimation sourcesAnimation = new SourcesAnimation(element, this);
        this.mSources = sourcesAnimation;
        return sourcesAnimation;
    }

    /* access modifiers changed from: protected */
    public void onSetAnimBefore() {
        super.onSetAnimBefore();
        this.mSources = null;
    }

    /* access modifiers changed from: protected */
    public void onSetAnimEnable(BaseAnimation baseAnimation) {
        if (baseAnimation instanceof SourcesAnimation) {
            this.mSources = (SourcesAnimation) baseAnimation;
        } else {
            super.onSetAnimEnable(baseAnimation);
        }
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        if (z) {
            updateBitmap(this.mLoadAsync);
        } else if (!this.mRetainWhenInvisible) {
            BitmapProvider bitmapProvider = this.mBitmapProvider;
            if (bitmapProvider != null) {
                bitmapProvider.finish();
            }
            this.mCurrentBitmap.reset();
        }
    }

    public void pause() {
        super.pause();
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().pause();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void pauseAnim(long j) {
        super.pauseAnim(j);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().pauseAnim(j);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().playAnim(j, j2, j3, z, z2);
            }
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.reset();
        }
    }

    public void reset(long j) {
        super.reset(j);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().reset(j);
            }
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.reset();
        }
        if (this.mBlurRadius > 0) {
            this.mPendingBlur = true;
        }
    }

    public void resume() {
        super.resume();
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().resume();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void resumeAnim(long j) {
        super.resumeAnim(j);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().resumeAnim(j);
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != this.mBitmap.getBitmap()) {
            this.mBitmap.setBitmap(bitmap);
            updateBitmap(this.mLoadAsync);
            requestUpdate();
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }

    public void setSrc(String str) {
        TextFormatter textFormatter = this.mSrcFormatter;
        if (textFormatter != null) {
            textFormatter.setText(str);
        }
    }

    public void setSrcId(double d2) {
        Expression expression = this.mSrcIdExpression;
        if (expression == null || !(expression instanceof Expression.NumberExpression)) {
            this.mSrcIdExpression = new Expression.NumberExpression(String.valueOf(d2));
        } else {
            ((Expression.NumberExpression) expression).setValue(d2);
        }
    }

    public void setSrcType(String str) {
        this.mBitmapProvider = BitmapProvider.create(this.mRoot, str);
    }

    /* access modifiers changed from: protected */
    public void updateBitmap(boolean z) {
        BitmapProvider.VersionedBitmap bitmap = getBitmap(z);
        if (this.mBlurRadius > 0 && !BitmapProvider.VersionedBitmap.equals(bitmap, this.mCurrentBitmap)) {
            this.mPendingBlur = true;
        }
        this.mCurrentBitmap.set(bitmap);
        updateBitmapVars();
    }

    /* access modifiers changed from: protected */
    public void updateBitmapVars() {
        if (this.mHasName) {
            this.mBmpSizeWidthVar.set(descale((double) getBitmapWidth()));
            this.mBmpSizeHeightVar.set(descale((double) getBitmapHeight()));
            this.mHasBitmapVar.set(this.mCurrentBitmap.getBitmap() != null ? 1.0d : 0.0d);
        }
    }
}
