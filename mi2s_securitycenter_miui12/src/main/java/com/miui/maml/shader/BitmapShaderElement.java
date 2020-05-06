package com.miui.maml.shader;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public class BitmapShaderElement extends ShaderElement {
    public static final String TAG_NAME = "BitmapShader";
    private Bitmap mBitmap;
    private Shader.TileMode mTileModeX;
    private Shader.TileMode mTileModeY;

    public BitmapShaderElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mBitmap = this.mRoot.getContext().mResourceManager.getBitmap(element.getAttribute("src"));
        resolveTileMode(element);
        this.mShader = new BitmapShader(this.mBitmap, this.mTileModeX, this.mTileModeY);
    }

    private void resolveTileMode(Element element) {
        String[] split = element.getAttribute("tile").split(",");
        if (split.length <= 1) {
            Shader.TileMode tileMode = this.mTileMode;
            this.mTileModeY = tileMode;
            this.mTileModeX = tileMode;
            return;
        }
        this.mTileModeX = ShaderElement.getTileMode(split[0]);
        this.mTileModeY = ShaderElement.getTileMode(split[1]);
    }

    public void onGradientStopsChanged() {
    }

    public void updateShader() {
    }

    public boolean updateShaderMatrix() {
        return false;
    }
}
