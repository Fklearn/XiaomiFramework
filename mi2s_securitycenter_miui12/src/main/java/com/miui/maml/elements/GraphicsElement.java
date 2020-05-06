package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ActionCommand;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class GraphicsElement extends AnimatedScreenElement {
    public static final int LINEAR_GRADIENT = 1;
    public static final String LOG_TAG = "MAML_Graphics";
    public static final int RADIAL_GRADIENT = 2;
    public static final String TAG_NAME = "Graphics";
    private Callbacks mCallbacks;
    private Canvas mCanvas;
    private Rect mCanvasBounds = new Rect();
    private float mCurrentX;
    private float mCurrentY;
    protected Paint mFillPaint = new Paint();
    private float mInitRawHeight;
    private float mInitRawWidth;
    private int mLastAlpha;
    private PorterDuff.Mode mMode = PorterDuff.Mode.SRC_OVER;
    private Path mPath = new Path();
    private FunctionElement mRenderListener;
    protected Paint mStrokePaint = new Paint();
    private PorterDuffXfermode mXferMode;
    private Expression mXfermodeNumExp;

    private static class Callbacks {
        public static final String TAG_NAME = "OnDraw";
        private ArrayList<ActionCommand> mCommands = new ArrayList<>();

        public Callbacks(Element element, ScreenElement screenElement) {
            ActionCommand create;
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i).getNodeType() == 1 && (create = ActionCommand.create((Element) childNodes.item(i), screenElement)) != null) {
                    this.mCommands.add(create);
                }
            }
        }

        public void finish() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().finish();
            }
        }

        public void init() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().init();
            }
        }

        public void pause() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().pause();
            }
        }

        public void perform() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().perform();
            }
        }

        public void resume() {
            Iterator<ActionCommand> it = this.mCommands.iterator();
            while (it.hasNext()) {
                it.next().resume();
            }
        }
    }

    private static class GraphicsMatrix extends Matrix {
        public boolean mChanged;
        public float[] mParm;

        private GraphicsMatrix() {
            this.mParm = new float[4];
        }
    }

    private static class GraphicsShader {
        public int[] mColors;
        /* access modifiers changed from: private */
        public String mMatrixName;
        public Shader mShader;
        /* access modifiers changed from: private */
        public int mShaderType;
        public float[] mStops;
        /* access modifiers changed from: private */
        public Shader.TileMode mTileMode;

        private GraphicsShader() {
            this.mColors = new int[0];
            this.mStops = new float[0];
            this.mShaderType = -1;
        }
    }

    public GraphicsElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        Element child = Utils.getChild(element, Callbacks.TAG_NAME);
        if (child != null) {
            this.mCallbacks = new Callbacks(child, this);
        }
        this.mXfermodeNumExp = Expression.build(getVariables(), getAttr(element, "xfermodeNum"));
        String attr = getAttr(element, "xfermode");
        if (this.mXfermodeNumExp == null && !TextUtils.isEmpty(attr)) {
            this.mMode = Utils.getPorterDuffMode(attr);
            this.mXferMode = new PorterDuffXfermode(this.mMode);
            this.mFillPaint.setXfermode(this.mXferMode);
            this.mStrokePaint.setXfermode(this.mXferMode);
        }
    }

    private GraphicsShader getGraphicsShader(int i, int[] iArr, float[] fArr, String str, String str2, int i2) {
        boolean z;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        if (i2 >= 0 && i2 < Shader.TileMode.values().length) {
            tileMode = Shader.TileMode.values()[i2];
        }
        Object obj = getVariables().get(str2);
        if (obj == null) {
            obj = new GraphicsShader();
            z = true;
            getVariables().put(str2, obj);
        } else {
            z = false;
        }
        if (!(obj instanceof GraphicsShader)) {
            return null;
        }
        GraphicsShader graphicsShader = (GraphicsShader) obj;
        if (!z) {
            z = isShaderParmsChanged(graphicsShader, i, iArr, fArr, str, tileMode);
        }
        boolean z2 = z;
        if (z2 && !resetShader(graphicsShader, i, iArr, fArr, str, tileMode)) {
            return null;
        }
        resetMatrixIfNecessary(z2, graphicsShader, i, str);
        return graphicsShader;
    }

    private boolean isShaderParmsChanged(GraphicsShader graphicsShader, int i, int[] iArr, float[] fArr, String str, Shader.TileMode tileMode) {
        if (graphicsShader.mShader == null || i != graphicsShader.mShaderType || iArr.length != graphicsShader.mColors.length || (((fArr == null || graphicsShader.mStops == null) && (fArr != null || graphicsShader.mStops != null)) || tileMode != graphicsShader.mTileMode || !str.equals(graphicsShader.mMatrixName))) {
            return true;
        }
        for (int i2 = 0; i2 < iArr.length; i2++) {
            if (graphicsShader.mColors[i2] != iArr[i2]) {
                return true;
            }
            if (fArr != null && graphicsShader.mStops[i2] != fArr[i2]) {
                return true;
            }
        }
        return false;
    }

    private boolean isValid() {
        if (Looper.getMainLooper() == Looper.myLooper() && this.mCanvas != null) {
            return true;
        }
        Log.e(LOG_TAG, "Call maml graphics api not in onDraw callback");
        return false;
    }

    private void resetMatrixIfNecessary(boolean z, GraphicsShader graphicsShader, int i, String str) {
        Object obj = getVariables().get(str);
        if (obj != null && (obj instanceof GraphicsMatrix) && graphicsShader.mShader != null) {
            GraphicsMatrix graphicsMatrix = (GraphicsMatrix) obj;
            if (z || graphicsMatrix.mChanged) {
                graphicsMatrix.reset();
                if (i == 1) {
                    graphicsMatrix.setPolyToPoly(new float[]{0.0f, 0.0f, 1.0f, 1.0f}, 0, graphicsMatrix.mParm, 0, 2);
                } else if (i == 2) {
                    float[] fArr = graphicsMatrix.mParm;
                    graphicsMatrix.preTranslate(-fArr[0], -fArr[1]);
                    float[] fArr2 = graphicsMatrix.mParm;
                    graphicsMatrix.setScale(fArr2[2], -fArr2[3]);
                    float[] fArr3 = graphicsMatrix.mParm;
                    graphicsMatrix.postTranslate(fArr3[0], fArr3[1]);
                }
                graphicsMatrix.mChanged = false;
                graphicsShader.mShader.setLocalMatrix(graphicsMatrix);
            }
        }
    }

    private boolean resetShader(GraphicsShader graphicsShader, int i, int[] iArr, float[] fArr, String str, Shader.TileMode tileMode) {
        GraphicsShader graphicsShader2 = graphicsShader;
        int i2 = i;
        if (i2 == 1) {
            graphicsShader2.mShader = new LinearGradient(0.0f, 0.0f, 1.0f, 1.0f, iArr, fArr, tileMode);
        } else if (i2 == 2) {
            graphicsShader2.mShader = new RadialGradient(0.0f, 0.0f, 1.0f, iArr, fArr, tileMode);
        } else {
            Log.e(LOG_TAG, "wrong shader type " + i);
            return false;
        }
        graphicsShader2.mColors = (int[]) iArr.clone();
        graphicsShader2.mStops = fArr != null ? (float[]) fArr.clone() : null;
        String unused = graphicsShader.mMatrixName = str;
        int unused2 = graphicsShader.mShaderType = i;
        Shader.TileMode unused3 = graphicsShader.mTileMode = tileMode;
        return true;
    }

    private void setColorFilterInternal(ColorFilter colorFilter) {
        this.mFillPaint.setColorFilter(colorFilter);
        this.mStrokePaint.setColorFilter(colorFilter);
    }

    public void beginFill(int i) {
        this.mFillPaint.setShader((Shader) null);
        this.mFillPaint.setColor(i);
    }

    public void beginGradientFill(int i, int[] iArr, float[] fArr, String str, String str2, int i2) {
        GraphicsShader graphicsShader;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && (graphicsShader = getGraphicsShader(i, iArr, fArr, str, str2, i2)) != null) {
            this.mFillPaint.setShader(graphicsShader.mShader);
        }
    }

    public void createOrUpdateGradientBox(float f, float f2, float f3, float f4, String str) {
        if (!TextUtils.isEmpty(str)) {
            Object obj = getVariables().get(str);
            if (obj == null) {
                obj = new GraphicsMatrix();
                getVariables().put(str, obj);
            }
            if (obj instanceof GraphicsMatrix) {
                GraphicsMatrix graphicsMatrix = (GraphicsMatrix) obj;
                float[] fArr = graphicsMatrix.mParm;
                if (fArr[0] != f || fArr[1] != f2 || fArr[2] != f3 || fArr[3] != f4) {
                    float[] fArr2 = graphicsMatrix.mParm;
                    fArr2[0] = f;
                    fArr2[1] = f2;
                    fArr2[2] = f3;
                    fArr2[3] = f4;
                    graphicsMatrix.mChanged = true;
                }
            }
        }
    }

    public void cubicCurveTo(float f, float f2, float f3, float f4, float f5, float f6) {
        if (isValid()) {
            this.mPath.rewind();
            this.mPath.moveTo(this.mCurrentX, this.mCurrentY);
            this.mPath.cubicTo(f, f2, f3, f4, f5, f6);
            this.mCanvas.drawPath(this.mPath, this.mStrokePaint);
            this.mCurrentX = f5;
            this.mCurrentY = f6;
        }
    }

    public void curveTo(float f, float f2, float f3, float f4) {
        if (isValid()) {
            this.mPath.rewind();
            this.mPath.moveTo(this.mCurrentX, this.mCurrentY);
            this.mPath.quadTo(f, f2, f3, f4);
            this.mCanvas.drawPath(this.mPath, this.mStrokePaint);
            this.mCurrentX = f3;
            this.mCurrentY = f4;
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
        int width = (int) getWidth();
        int height = (int) getHeight();
        canvas.getClipBounds(this.mCanvasBounds);
        Rect rect = this.mCanvasBounds;
        int i = rect.right - rect.left;
        int i2 = rect.bottom - rect.top;
        if (i > 0 && i < width) {
            width = i;
        }
        if (i2 > 0 && i2 < height) {
            height = i2;
        }
        Rect rect2 = this.mCanvasBounds;
        canvas.clipRect(rect2.left, rect2.top, width, height);
        this.mCurrentX = 0.0f;
        this.mCurrentY = 0.0f;
        this.mCanvas = canvas;
        Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.perform();
        }
        FunctionElement functionElement = this.mRenderListener;
        if (functionElement != null) {
            functionElement.perform();
        }
        this.mCanvas = null;
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        PorterDuff.Mode porterDuffMode;
        super.doTick(j);
        if (isVisible()) {
            Expression expression = this.mXfermodeNumExp;
            if (!(expression == null || (porterDuffMode = Utils.getPorterDuffMode((int) expression.evaluate())) == this.mMode)) {
                this.mMode = porterDuffMode;
                this.mXferMode = new PorterDuffXfermode(this.mMode);
                this.mFillPaint.setXfermode(this.mXferMode);
                this.mStrokePaint.setXfermode(this.mXferMode);
            }
            int i = this.mLastAlpha;
            int i2 = this.mAlpha;
            if (i != i2) {
                this.mFillPaint.setAlpha(i2);
                this.mStrokePaint.setAlpha(this.mAlpha);
                this.mLastAlpha = this.mAlpha;
            }
            if (this.mTintChanged) {
                setColorFilterInternal(this.mTintFilter);
            }
        }
    }

    public void drawCircle(float f, float f2, float f3) {
        if (isValid()) {
            this.mCanvas.drawCircle(f, f2, f3, this.mFillPaint);
            if (this.mStrokePaint.getStrokeWidth() > 0.0f) {
                this.mCanvas.drawCircle(f, f2, f3 + (this.mStrokePaint.getStrokeWidth() / 2.0f), this.mStrokePaint);
            }
        }
    }

    public void drawEllipse(float f, float f2, float f3, float f4) {
        if (Build.VERSION.SDK_INT >= 21 && isValid() && f3 >= 0.0f && f4 >= 0.0f) {
            float f5 = f4 / 2.0f;
            float f6 = f2 - f5;
            float f7 = f2 + f5;
            float f8 = f3 / 2.0f;
            float f9 = f - f8;
            float f10 = f + f8;
            this.mCanvas.drawOval(f9, f6, f10, f7, this.mFillPaint);
            float strokeWidth = this.mStrokePaint.getStrokeWidth();
            if (strokeWidth > 0.0f) {
                float f11 = strokeWidth / 2.0f;
                this.mCanvas.drawOval(f9 - f11, f6 - f11, f10 + f11, f7 + f11, this.mStrokePaint);
            }
        }
    }

    public void drawRect(float f, float f2, float f3, float f4) {
        drawRoundRect(f, f2, f3, f4, 0.0f, 0.0f);
    }

    public void drawRoundRect(float f, float f2, float f3, float f4, float f5, float f6) {
        if (Build.VERSION.SDK_INT >= 21 && isValid() && f3 >= 0.0f && f4 >= 0.0f) {
            float f7 = f2 + f4;
            float f8 = f + f3;
            this.mCanvas.drawRoundRect(f, f2, f8, f7, f5, f6, this.mFillPaint);
            float strokeWidth = this.mStrokePaint.getStrokeWidth();
            if (strokeWidth > 0.0f) {
                float f9 = strokeWidth / 2.0f;
                this.mCanvas.drawRoundRect(f - f9, f2 - f9, f8 + f9, f7 + f9, f5, f6, this.mStrokePaint);
            }
        }
    }

    public void finish() {
        super.finish();
        Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.finish();
        }
    }

    public float getScaleX() {
        float widthRaw = getWidthRaw();
        float f = this.mInitRawWidth;
        return (f <= 0.0f || widthRaw <= 0.0f) ? super.getScaleX() : (widthRaw / f) * super.getScaleX();
    }

    public float getScaleY() {
        float heightRaw = getHeightRaw();
        float f = this.mInitRawHeight;
        return (f <= 0.0f || heightRaw <= 0.0f) ? super.getScaleY() : (heightRaw / f) * super.getScaleY();
    }

    public void init() {
        super.init();
        this.mInitRawWidth = getWidthRaw();
        this.mInitRawHeight = getHeightRaw();
        this.mFillPaint.setStyle(Paint.Style.FILL);
        this.mStrokePaint.setStyle(Paint.Style.STROKE);
        this.mFillPaint.setAntiAlias(true);
        this.mStrokePaint.setAntiAlias(true);
        Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.init();
        }
        this.mLastAlpha = evaluateAlpha();
    }

    public void lineGradientStyle(int i, int[] iArr, float[] fArr, String str, String str2, int i2) {
        GraphicsShader graphicsShader;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && (graphicsShader = getGraphicsShader(i, iArr, fArr, str, str2, i2)) != null) {
            this.mStrokePaint.setShader(graphicsShader.mShader);
        }
    }

    public void lineStyle(float f, int i, int i2, int i3, float f2) {
        Paint.Cap cap = Paint.Cap.ROUND;
        if (i2 >= 0 && i2 < Paint.Cap.values().length) {
            cap = Paint.Cap.values()[i2];
        }
        Paint.Join join = Paint.Join.ROUND;
        if (i3 >= 0 && i3 < Paint.Join.values().length) {
            join = Paint.Join.values()[i2];
        }
        this.mStrokePaint.setShader((Shader) null);
        if (f >= 0.0f) {
            this.mStrokePaint.setStrokeWidth(f);
        }
        this.mStrokePaint.setColor(i);
        this.mStrokePaint.setStrokeCap(cap);
        this.mStrokePaint.setStrokeJoin(join);
        if (f2 > 0.0f) {
            this.mStrokePaint.setStrokeMiter(f2);
        }
    }

    public void lineTo(float f, float f2) {
        if (isValid()) {
            this.mCanvas.drawLine(this.mCurrentX, this.mCurrentY, f, f2, this.mStrokePaint);
            this.mCurrentX = f;
            this.mCurrentY = f2;
        }
    }

    public void moveTo(float f, float f2) {
        if (isValid()) {
            this.mCurrentX = f;
            this.mCurrentY = f2;
        }
    }

    public void pause() {
        super.pause();
        Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.pause();
        }
    }

    public void resume() {
        super.resume();
        Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.resume();
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        setColorFilterInternal(colorFilter);
    }

    public void setRenderListener(FunctionElement functionElement) {
        this.mRenderListener = functionElement;
    }
}
