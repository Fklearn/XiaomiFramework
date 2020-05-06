package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.Log;
import com.miui.maml.ActionCommand;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

public class FunctionElement extends ScreenElement {
    public static final String TAG_NAME = "Function";
    private boolean enable = true;
    /* access modifiers changed from: private */
    public ArrayList<ActionCommand> mCommands = new ArrayList<>();

    public FunctionElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element);
    }

    private void load(Element element) {
        if (element != null) {
            Utils.traverseXmlElementChildren(element, (String) null, new Utils.XmlTraverseListener() {
                public void onChild(Element element) {
                    ActionCommand create = ActionCommand.create(element, FunctionElement.this);
                    if (create != null) {
                        FunctionElement.this.mCommands.add(create);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    public void finish() {
        super.finish();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().finish();
        }
    }

    public void init() {
        super.init();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().init();
        }
    }

    public void pause() {
        super.pause();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
    }

    public void perform() {
        if (this.enable) {
            try {
                Iterator<ActionCommand> it = this.mCommands.iterator();
                while (it.hasNext()) {
                    it.next().perform();
                }
            } catch (StackOverflowError unused) {
                this.enable = false;
                Log.e(TAG_NAME, "Function " + this.mName + " was disabled");
            }
        }
    }

    public void resume() {
        super.resume();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().resume();
        }
    }
}
