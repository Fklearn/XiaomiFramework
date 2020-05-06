package com.miui.maml.shader;

import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.util.ColorParser;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class ShaderElement {
    private static final String LOG_TAG = "ShaderElement";
    protected GradientStops mGradientStops = new GradientStops();
    protected ScreenElementRoot mRoot;
    protected Shader mShader;
    protected Matrix mShaderMatrix = new Matrix();
    protected Shader.TileMode mTileMode;
    protected float mX;
    protected Expression mXExp;
    protected float mY;
    protected Expression mYExp;

    protected final class GradientStop {
        public static final String TAG_NAME = "GradientStop";
        private ColorParser mColorParser;
        private Expression mPositionExp;

        public GradientStop(Element element, ScreenElementRoot screenElementRoot) {
            this.mColorParser = ColorParser.fromElement(ShaderElement.this.mRoot.getVariables(), element);
            this.mPositionExp = Expression.build(ShaderElement.this.mRoot.getVariables(), element.getAttribute("position"));
            if (this.mPositionExp == null) {
                Log.e(TAG_NAME, "lost position attribute.");
            }
        }

        public int getColor() {
            return this.mColorParser.getColor();
        }

        public float getPosition() {
            return (float) this.mPositionExp.evaluate();
        }
    }

    protected final class GradientStops {
        private int[] mColors;
        protected ArrayList<GradientStop> mGradientStopArr = new ArrayList<>();
        private float[] mPositions;

        protected GradientStops() {
        }

        public void add(GradientStop gradientStop) {
            this.mGradientStopArr.add(gradientStop);
        }

        public int[] getColors() {
            return this.mColors;
        }

        public float[] getPositions() {
            return this.mPositions;
        }

        public void init() {
            this.mColors = new int[size()];
            this.mPositions = new float[size()];
        }

        public int size() {
            return this.mGradientStopArr.size();
        }

        public void update() {
            boolean z = false;
            for (int i = 0; i < size(); i++) {
                int color = this.mGradientStopArr.get(i).getColor();
                if (color != this.mColors[i]) {
                    z = true;
                }
                this.mColors[i] = color;
                float position = this.mGradientStopArr.get(i).getPosition();
                if (position != this.mPositions[i]) {
                    z = true;
                }
                this.mPositions[i] = position;
            }
            if (z) {
                ShaderElement.this.onGradientStopsChanged();
            }
        }
    }

    public ShaderElement(Element element, ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
        Variables variables = getVariables();
        this.mXExp = Expression.build(variables, element.getAttribute(AnimatedProperty.PROPERTY_NAME_X));
        this.mYExp = Expression.build(variables, element.getAttribute(AnimatedProperty.PROPERTY_NAME_Y));
        this.mTileMode = getTileMode(element.getAttribute("tile"));
        if (!element.getTagName().equalsIgnoreCase(BitmapShaderElement.TAG_NAME)) {
            loadGradientStops(element, screenElementRoot);
        }
    }

    public static Shader.TileMode getTileMode(String str) {
        return TextUtils.isEmpty(str) ? Shader.TileMode.CLAMP : str.equalsIgnoreCase("mirror") ? Shader.TileMode.MIRROR : str.equalsIgnoreCase("repeat") ? Shader.TileMode.REPEAT : Shader.TileMode.CLAMP;
    }

    private void loadGradientStops(Element element, ScreenElementRoot screenElementRoot) {
        NodeList elementsByTagName = element.getElementsByTagName(GradientStop.TAG_NAME);
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            this.mGradientStops.add(new GradientStop((Element) elementsByTagName.item(i), screenElementRoot));
        }
        if (this.mGradientStops.size() <= 0) {
            Log.e(LOG_TAG, "lost gradient stop.");
        } else {
            this.mGradientStops.init();
        }
    }

    public Shader getShader() {
        return this.mShader;
    }

    /* access modifiers changed from: protected */
    public Variables getVariables() {
        return this.mRoot.getVariables();
    }

    public float getX() {
        Expression expression = this.mXExp;
        return (float) ((expression != null ? expression.evaluate() : 0.0d) * ((double) this.mRoot.getScale()));
    }

    public float getY() {
        Expression expression = this.mYExp;
        return (float) ((expression != null ? expression.evaluate() : 0.0d) * ((double) this.mRoot.getScale()));
    }

    public abstract void onGradientStopsChanged();

    public void updateShader() {
        this.mGradientStops.update();
        if (updateShaderMatrix()) {
            this.mShader.setLocalMatrix(this.mShaderMatrix);
        }
    }

    public abstract boolean updateShaderMatrix();
}
