package com.miui.maml;

import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

public class VibrateCommand extends ActionCommand {
    private static final String LOG_TAG = "VibrateCommand";
    public static final String TAG_NAME = "VibrateCommand";

    public VibrateCommand(ScreenElement screenElement, Element element) {
        super(screenElement);
    }

    /* access modifiers changed from: protected */
    public void doPerform() {
    }
}
