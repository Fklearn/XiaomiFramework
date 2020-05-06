package com.miui.maml.elements;

import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.util.Utils;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;

public class ScreenElementArray extends ElementGroup {
    private static final String DEF_INDEX_VAR_NAME = "__i";
    public static final String TAG_NAME = "Array";

    public ScreenElementArray(Element element, final ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        final int attrAsInt = Utils.getAttrAsInt(element, "count", 0);
        String attribute = element.getAttribute("indexName");
        final IndexedVariable indexedVariable = new IndexedVariable(TextUtils.isEmpty(attribute) ? DEF_INDEX_VAR_NAME : attribute, getVariables(), true);
        Utils.traverseXmlElementChildren(element, (String) null, new Utils.XmlTraverseListener() {
            public void onChild(Element element) {
                String attr = ScreenElementArray.this.getAttr(element, CloudPushConstants.XML_NAME);
                boolean startsWith = element.getTagName().startsWith(VariableElement.TAG_NAME);
                ElementGroup elementGroup = null;
                for (int i = 0; i < attrAsInt; i++) {
                    if (startsWith) {
                        element.setAttribute("dontAddToMap", "true");
                    } else {
                        element.setAttribute("namesSuffix", "[" + i + "]");
                    }
                    ScreenElement access$001 = ScreenElementArray.super.onCreateChild(element);
                    if (access$001 != null) {
                        if (elementGroup == null) {
                            elementGroup = ElementGroup.createArrayGroup(screenElementRoot, indexedVariable);
                            elementGroup.setName(attr);
                            ScreenElementArray.this.addElement(elementGroup);
                        }
                        elementGroup.addElement(access$001);
                    }
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public ScreenElement onCreateChild(Element element) {
        return null;
    }
}
