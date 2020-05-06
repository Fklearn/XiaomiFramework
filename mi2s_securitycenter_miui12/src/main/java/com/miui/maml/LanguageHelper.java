package com.miui.maml;

import com.miui.maml.data.Variables;
import com.xiaomi.stat.MiStat;
import miui.cloud.CloudPushConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LanguageHelper {
    private static final String COMPATIBLE_STRING_ROOT_TAG = "strings";
    private static final String DEFAULT_STRING_FILE_PATH = "strings/strings.xml";
    private static final String LOG_TAG = "LanguageHelper";
    private static final String STRING_FILE_PATH = "strings/strings.xml";
    private static final String STRING_ROOT_TAG = "resources";
    private static final String STRING_TAG = "string";

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean load(java.util.Locale r5, com.miui.maml.ResourceManager r6, com.miui.maml.data.Variables r7) {
        /*
            r0 = 0
            java.lang.String r1 = "strings/strings.xml"
            if (r5 == 0) goto L_0x001e
            java.lang.String r2 = r5.toString()
            java.lang.String r2 = com.miui.maml.util.Utils.addFileNameSuffix(r1, r2)
            boolean r3 = r6.resourceExists(r2)
            if (r3 != 0) goto L_0x001c
            java.lang.String r5 = r5.getLanguage()
            java.lang.String r5 = com.miui.maml.util.Utils.addFileNameSuffix(r1, r5)
            goto L_0x001f
        L_0x001c:
            r5 = r2
            goto L_0x001f
        L_0x001e:
            r5 = r0
        L_0x001f:
            boolean r2 = r6.resourceExists(r5)
            r3 = 0
            java.lang.String r4 = "LanguageHelper"
            if (r2 != 0) goto L_0x0035
            boolean r5 = r6.resourceExists(r1)
            if (r5 != 0) goto L_0x0034
            java.lang.String r5 = "no available string resources to load."
            android.util.Log.i(r4, r5)
            return r3
        L_0x0034:
            r5 = r1
        L_0x0035:
            javax.xml.parsers.DocumentBuilderFactory r1 = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            javax.xml.parsers.DocumentBuilder r1 = r1.newDocumentBuilder()     // Catch:{ Exception -> 0x0056 }
            java.io.InputStream r0 = r6.getInputStream(r5)     // Catch:{ Exception -> 0x0056 }
            org.w3c.dom.Document r5 = r1.parse(r0)     // Catch:{ Exception -> 0x0056 }
            if (r0 == 0) goto L_0x004f
            r0.close()     // Catch:{ Exception -> 0x004b }
            goto L_0x004f
        L_0x004b:
            r6 = move-exception
            r6.printStackTrace()
        L_0x004f:
            boolean r5 = setVariables(r5, r7)
            return r5
        L_0x0054:
            r5 = move-exception
            goto L_0x0069
        L_0x0056:
            r5 = move-exception
            java.lang.String r5 = r5.getMessage()     // Catch:{ all -> 0x0054 }
            android.util.Log.e(r4, r5)     // Catch:{ all -> 0x0054 }
            if (r0 == 0) goto L_0x0068
            r0.close()     // Catch:{ Exception -> 0x0064 }
            goto L_0x0068
        L_0x0064:
            r5 = move-exception
            r5.printStackTrace()
        L_0x0068:
            return r3
        L_0x0069:
            if (r0 == 0) goto L_0x0073
            r0.close()     // Catch:{ Exception -> 0x006f }
            goto L_0x0073
        L_0x006f:
            r6 = move-exception
            r6.printStackTrace()
        L_0x0073:
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.LanguageHelper.load(java.util.Locale, com.miui.maml.ResourceManager, com.miui.maml.data.Variables):boolean");
    }

    private static boolean setVariables(Document document, Variables variables) {
        boolean z;
        NodeList elementsByTagName = document.getElementsByTagName(STRING_ROOT_TAG);
        if (elementsByTagName.getLength() <= 0) {
            elementsByTagName = document.getElementsByTagName(COMPATIBLE_STRING_ROOT_TAG);
            if (elementsByTagName.getLength() <= 0) {
                return false;
            }
            z = false;
        } else {
            z = true;
        }
        NodeList elementsByTagName2 = ((Element) elementsByTagName.item(0)).getElementsByTagName(STRING_TAG);
        for (int i = 0; i < elementsByTagName2.getLength(); i++) {
            Element element = (Element) elementsByTagName2.item(i);
            variables.put(element.getAttribute(CloudPushConstants.XML_NAME), (Object) (z ? element.getTextContent() : element.getAttribute(MiStat.Param.VALUE)).replaceAll("\\\\", ""));
        }
        return true;
    }
}
