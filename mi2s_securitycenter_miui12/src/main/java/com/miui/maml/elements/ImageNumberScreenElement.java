package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

public class ImageNumberScreenElement extends ImageScreenElement {
    public static final String TAG_NAME = "ImageNumber";
    public static final String TAG_NAME1 = "ImageChars";
    private String LOG_TAG = "ImageNumberScreenElement";
    private int mBmpHeight;
    private int mBmpWidth;
    private Bitmap mCachedBmp;
    private Canvas mCachedCanvas;
    private ArrayList<CharName> mNameMap;
    private Expression mNumExpression;
    private String mOldSrc;
    private double mPreNumber = Double.MIN_VALUE;
    private String mPreStr;
    private int mSpace;
    private Expression mSpaceExpression;
    private Expression mStrExpression;
    private String mStrValue;

    private class CharName {
        public char ch;
        public String name;

        public CharName(char c2, String str) {
            this.ch = c2;
            this.name = str;
        }
    }

    public ImageNumberScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mNumExpression = Expression.build(getVariables(), getAttr(element, "number"));
        this.mStrExpression = Expression.build(getVariables(), getAttr(element, "string"));
        this.mSpaceExpression = Expression.build(getVariables(), getAttr(element, "space"));
        String attr = getAttr(element, "charNameMap");
        if (!TextUtils.isEmpty(attr)) {
            this.mNameMap = new ArrayList<>();
            for (String str : attr.split(",")) {
                this.mNameMap.add(new CharName(str.charAt(0), str.substring(1)));
            }
        }
    }

    private String charToStr(char c2) {
        ArrayList<CharName> arrayList = this.mNameMap;
        if (arrayList != null) {
            Iterator<CharName> it = arrayList.iterator();
            while (it.hasNext()) {
                CharName next = it.next();
                if (next.ch == c2) {
                    return next.name;
                }
            }
        }
        return c2 == '.' ? "dot" : String.valueOf(c2);
    }

    private Bitmap getNumberBitmap(String str, String str2) {
        return getContext().mResourceManager.getBitmap(Utils.addFileNameSuffix(str, str2));
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        String str;
        super.doTick(j);
        if (this.mNumExpression != null || this.mStrExpression != null || this.mStrValue != null) {
            String src = getSrc();
            boolean z = !TextUtils.equals(src, this.mOldSrc);
            Expression expression = this.mNumExpression;
            if (expression != null) {
                double evaluate = evaluate(expression);
                if (evaluate != this.mPreNumber || z) {
                    this.mPreNumber = evaluate;
                    str = Utils.doubleToString(evaluate);
                } else {
                    return;
                }
            } else if (this.mStrExpression == null && this.mStrValue == null) {
                str = null;
            } else {
                String str2 = this.mStrValue;
                if (str2 == null) {
                    str2 = evaluateStr(this.mStrExpression);
                }
                if (!TextUtils.equals(str2, this.mPreStr) || z) {
                    this.mPreStr = str2;
                    str = str2;
                } else {
                    return;
                }
            }
            Bitmap bitmap = this.mCachedBmp;
            if (bitmap != null) {
                bitmap.eraseColor(0);
            }
            this.mOldSrc = src;
            this.mBmpWidth = 0;
            int length = str != null ? str.length() : 0;
            for (int i = 0; i < length; i++) {
                Bitmap numberBitmap = getNumberBitmap(src, charToStr(str.charAt(i)));
                if (numberBitmap == null) {
                    Log.e(this.LOG_TAG, "Fail to get bitmap for number " + String.valueOf(str.charAt(i)));
                    return;
                }
                int width = this.mBmpWidth + numberBitmap.getWidth();
                int height = numberBitmap.getHeight();
                Bitmap bitmap2 = this.mCachedBmp;
                int width2 = bitmap2 == null ? 0 : bitmap2.getWidth();
                Bitmap bitmap3 = this.mCachedBmp;
                int height2 = bitmap3 == null ? 0 : bitmap3.getHeight();
                if (width > width2 || height > height2) {
                    Bitmap bitmap4 = this.mCachedBmp;
                    if (width > width2) {
                        int i2 = length - i;
                        width2 = this.mBmpWidth + (numberBitmap.getWidth() * i2) + (this.mSpace * (i2 - 1));
                    }
                    if (height <= height2) {
                        height = height2;
                    }
                    this.mBmpHeight = height;
                    this.mCachedBmp = Bitmap.createBitmap(width2, height, Bitmap.Config.ARGB_8888);
                    this.mCachedBmp.setDensity(numberBitmap.getDensity());
                    this.mCurrentBitmap.setBitmap(this.mCachedBmp);
                    this.mCachedCanvas = new Canvas(this.mCachedBmp);
                    if (bitmap4 != null) {
                        this.mCachedCanvas.drawBitmap(bitmap4, 0.0f, 0.0f, (Paint) null);
                    }
                }
                this.mCachedCanvas.drawBitmap(numberBitmap, (float) this.mBmpWidth, 0.0f, (Paint) null);
                this.mBmpWidth += numberBitmap.getWidth();
                if (i < length - 1) {
                    this.mBmpWidth += this.mSpace;
                }
            }
            this.mCurrentBitmap.updateVersion();
            updateBitmapVars();
        } else if (this.mCachedBmp != null) {
            this.mCachedBmp = null;
            this.mPreStr = null;
            this.mCurrentBitmap.setBitmap((Bitmap) null);
            updateBitmapVars();
        }
    }

    public void finish() {
        super.finish();
        this.mPreNumber = Double.MIN_VALUE;
        this.mPreStr = null;
    }

    /* access modifiers changed from: protected */
    public int getBitmapHeight() {
        return this.mBmpHeight;
    }

    /* access modifiers changed from: protected */
    public int getBitmapWidth() {
        return this.mBmpWidth;
    }

    public void init() {
        super.init();
        Expression expression = this.mSpaceExpression;
        this.mSpace = expression == null ? 0 : (int) scale(expression.evaluate());
        this.mCurrentBitmap.setBitmap(this.mCachedBmp);
    }

    public void setValue(double d2) {
        setValue(Utils.doubleToString(d2));
    }

    public void setValue(String str) {
        this.mStrValue = str;
        requestUpdate();
    }

    /* access modifiers changed from: protected */
    public void updateBitmap(boolean z) {
        this.mCurrentBitmap.setBitmap(this.mCachedBmp);
        updateBitmapVars();
    }
}
