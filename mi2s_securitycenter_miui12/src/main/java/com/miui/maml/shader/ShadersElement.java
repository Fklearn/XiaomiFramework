package com.miui.maml.shader;

import android.graphics.Shader;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public final class ShadersElement {
    public static final String FILL_TAG_NAME = "FillShaders";
    public static final String STROKE_TAG_NAME = "StrokeShaders";
    private ShaderElement mShaderElement;

    public ShadersElement(Element element, ScreenElementRoot screenElementRoot) {
        loadShaderElements(element, screenElementRoot);
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x005f A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005c A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadShaderElements(org.w3c.dom.Element r5, com.miui.maml.ScreenElementRoot r6) {
        /*
            r4 = this;
            org.w3c.dom.NodeList r5 = r5.getChildNodes()
            r0 = 0
        L_0x0005:
            int r1 = r5.getLength()
            if (r0 >= r1) goto L_0x005f
            org.w3c.dom.Node r1 = r5.item(r0)
            short r2 = r1.getNodeType()
            r3 = 1
            if (r2 == r3) goto L_0x0017
            goto L_0x005c
        L_0x0017:
            org.w3c.dom.Element r1 = (org.w3c.dom.Element) r1
            java.lang.String r2 = r1.getTagName()
            java.lang.String r3 = "LinearGradient"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 == 0) goto L_0x002d
            com.miui.maml.shader.LinearGradientElement r2 = new com.miui.maml.shader.LinearGradientElement
            r2.<init>(r1, r6)
        L_0x002a:
            r4.mShaderElement = r2
            goto L_0x0057
        L_0x002d:
            java.lang.String r3 = "RadialGradient"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 == 0) goto L_0x003b
            com.miui.maml.shader.RadialGradientElement r2 = new com.miui.maml.shader.RadialGradientElement
            r2.<init>(r1, r6)
            goto L_0x002a
        L_0x003b:
            java.lang.String r3 = "SweepGradient"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 == 0) goto L_0x0049
            com.miui.maml.shader.SweepGradientElement r2 = new com.miui.maml.shader.SweepGradientElement
            r2.<init>(r1, r6)
            goto L_0x002a
        L_0x0049:
            java.lang.String r3 = "BitmapShader"
            boolean r2 = r2.equalsIgnoreCase(r3)
            if (r2 == 0) goto L_0x0057
            com.miui.maml.shader.BitmapShaderElement r2 = new com.miui.maml.shader.BitmapShaderElement
            r2.<init>(r1, r6)
            goto L_0x002a
        L_0x0057:
            com.miui.maml.shader.ShaderElement r1 = r4.mShaderElement
            if (r1 == 0) goto L_0x005c
            goto L_0x005f
        L_0x005c:
            int r0 = r0 + 1
            goto L_0x0005
        L_0x005f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.shader.ShadersElement.loadShaderElements(org.w3c.dom.Element, com.miui.maml.ScreenElementRoot):void");
    }

    public Shader getShader() {
        ShaderElement shaderElement = this.mShaderElement;
        if (shaderElement != null) {
            return shaderElement.getShader();
        }
        return null;
    }

    public void updateShader() {
        ShaderElement shaderElement = this.mShaderElement;
        if (shaderElement != null) {
            shaderElement.updateShader();
        }
    }
}
