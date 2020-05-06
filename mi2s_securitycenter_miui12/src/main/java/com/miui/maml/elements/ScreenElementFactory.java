package com.miui.maml.elements;

import android.os.Build;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;

public class ScreenElementFactory {
    private FactoryCallback mFactoryCallback;

    public interface FactoryCallback {
        ScreenElement onCreateInstance(Element element, ScreenElementRoot screenElementRoot);
    }

    public ScreenElement createInstance(Element element, ScreenElementRoot screenElementRoot) {
        String tagName = element.getTagName();
        try {
            if (tagName.equalsIgnoreCase(ImageScreenElement.TAG_NAME)) {
                return new ImageScreenElement(element, screenElementRoot);
            }
            if (tagName.equalsIgnoreCase(GraphicsElement.TAG_NAME)) {
                return new GraphicsElement(element, screenElementRoot);
            }
            if (tagName.equalsIgnoreCase(TimepanelScreenElement.TAG_NAME)) {
                return new TimepanelScreenElement(element, screenElementRoot);
            }
            if (!tagName.equalsIgnoreCase(ImageNumberScreenElement.TAG_NAME)) {
                if (!tagName.equalsIgnoreCase(ImageNumberScreenElement.TAG_NAME1)) {
                    if (tagName.equalsIgnoreCase(TextScreenElement.TAG_NAME)) {
                        return new TextScreenElement(element, screenElementRoot);
                    }
                    if (tagName.equalsIgnoreCase("DateTime")) {
                        return new DateTimeScreenElement(element, screenElementRoot);
                    }
                    if (tagName.equalsIgnoreCase(ButtonScreenElement.TAG_NAME)) {
                        return new ButtonScreenElement(element, screenElementRoot);
                    }
                    if (Build.VERSION.SDK_INT >= 21 && tagName.equalsIgnoreCase(MusicControlScreenElement.TAG_NAME)) {
                        return new MusicControlScreenElement(element, screenElementRoot);
                    }
                    if (!tagName.equalsIgnoreCase(ElementGroup.TAG_NAME)) {
                        if (!tagName.equalsIgnoreCase("Group")) {
                            if (tagName.equalsIgnoreCase(VariableElement.TAG_NAME)) {
                                return new VariableElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(VariableArrayElement.TAG_NAME)) {
                                return new VariableArrayElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(AutoScaleElementGroup.TAG_NAME)) {
                                return new AutoScaleElementGroup(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(SpectrumVisualizerScreenElement.TAG_NAME)) {
                                return new SpectrumVisualizerScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(AdvancedSlider.TAG_NAME)) {
                                return new AdvancedSlider(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(FramerateController.TAG_NAME)) {
                                return new FramerateController(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(FolmeConfigScreenElement.TAG_NAME)) {
                                return new FolmeConfigScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(FolmeStateScreenElement.TAG_NAME)) {
                                return new FolmeStateScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase("VirtualScreen")) {
                                return new VirtualScreen(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(VirtualAnimatedScreenElement.TAG_NAME)) {
                                return new VirtualAnimatedScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(LineScreenElement.TAG_NAME)) {
                                return new LineScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(RectangleScreenElement.TAG_NAME)) {
                                return new RectangleScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(EllipseScreenElement.TAG_NAME)) {
                                return new EllipseScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(CircleScreenElement.TAG_NAME)) {
                                return new CircleScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(ArcScreenElement.TAG_NAME)) {
                                return new ArcScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(CurveScreenElement.TAG_NAME)) {
                                return new CurveScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(ListScreenElement.TAG_NAME)) {
                                return new ListScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(PaintScreenElement.TAG_NAME)) {
                                return new PaintScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(MirrorScreenElement.TAG_NAME)) {
                                return new MirrorScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(WindowScreenElement.TAG_NAME)) {
                                return new WindowScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(ScreenElementArray.TAG_NAME)) {
                                return new ScreenElementArray(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(WebViewScreenElement.TAG_NAME)) {
                                return new WebViewScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(LayerScreenElement.TAG_NAME)) {
                                return new LayerScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(GLLayerScreenElement.TAG_NAME)) {
                                return new GLLayerScreenElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(CanvasDrawerElement.TAG_NAME)) {
                                return new CanvasDrawerElement(element, screenElementRoot);
                            }
                            if (tagName.equalsIgnoreCase(FunctionElement.TAG_NAME)) {
                                return new FunctionElement(element, screenElementRoot);
                            }
                            if (this.mFactoryCallback != null) {
                                return this.mFactoryCallback.onCreateInstance(element, screenElementRoot);
                            }
                            return null;
                        }
                    }
                    return new ElementGroup(element, screenElementRoot);
                }
            }
            return new ImageNumberScreenElement(element, screenElementRoot);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Log.w("ScreenElementFactory", "fail to create element." + e);
            return null;
        }
    }

    public FactoryCallback getCallback() {
        return this.mFactoryCallback;
    }

    public void setCallback(FactoryCallback factoryCallback) {
        this.mFactoryCallback = factoryCallback;
    }
}
