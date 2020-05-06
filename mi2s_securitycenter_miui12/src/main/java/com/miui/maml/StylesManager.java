package com.miui.maml;

import com.miui.maml.util.Utils;
import java.util.HashMap;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class StylesManager {
    /* access modifiers changed from: private */
    public HashMap<String, Style> mStyles = new HashMap<>();

    public class Style {
        public static final String TAG = "Style";
        private Style base;
        private HashMap<String, String> mAttrs = new HashMap<>();
        public String name;

        public Style(Element element) {
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                String nodeName = item.getNodeName();
                String nodeValue = item.getNodeValue();
                if (nodeName.equals(CloudPushConstants.XML_NAME)) {
                    this.name = nodeValue;
                } else if (nodeName.equals("base")) {
                    this.base = (Style) StylesManager.this.mStyles.get(nodeValue);
                } else {
                    this.mAttrs.put(nodeName, nodeValue);
                }
            }
        }

        public String getAttr(String str) {
            String str2 = this.mAttrs.get(str);
            if (str2 != null) {
                return str2;
            }
            Style style = this.base;
            if (style != null) {
                return style.getAttr(str);
            }
            return null;
        }
    }

    public StylesManager(Element element) {
        Utils.traverseXmlElementChildren(element, Style.TAG, new Utils.XmlTraverseListener() {
            public void onChild(Element element) {
                Style style = new Style(element);
                StylesManager.this.mStyles.put(style.name, style);
            }
        });
    }

    public Style getStyle(String str) {
        return this.mStyles.get(str);
    }
}
