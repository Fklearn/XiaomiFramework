package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.ArrayMap;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class FolmeStateScreenElement extends ScreenElement {
    public static final String TAG_NAME = "FolmeState";
    private ArrayMap<String, Expression> mAttrs = new ArrayMap<>();

    public FolmeStateScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            Node item = attributes.item(i);
            Expression build = Expression.build(getVariables(), item.getNodeValue());
            if (build != null) {
                this.mAttrs.put(item.getNodeName(), build);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    public ArrayMap<String, Expression> getAttrs() {
        return this.mAttrs;
    }

    public boolean isVisible() {
        return false;
    }

    public void tick(long j) {
    }
}
