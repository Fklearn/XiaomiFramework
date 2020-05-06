package miui.content.res;

import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.miui.internal.content.res.ThemeDefinition;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.util.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ThemeCompatibilityLoader {
    private static final String COMPATIBILITY_FILE_NAME = "theme_compatibility.xml";
    public static final String DATA_THEME_COMPATIBILITY_PATH = "/data/system/theme_config/theme_compatibility.xml";
    public static final String SYSTEM_THEME_COMPATIBILITY_PATH = "/system/media/theme/theme_compatibility.xml";
    private static final String TAG = "ThemeCompatibility";
    private static final String TAG_ITEM = "item";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_PROPERTY1 = "property1";
    private static final String TAG_PROPERTY2 = "property2";
    private static final String TAG_PROPERTY3 = "property3";
    private static final String TAG_PROPERTYEXTRA = "propertyExtra";
    private static final String TAG_RESOURCE_TYPE = "resourceType";
    private static final String TAG_VERSION = "version";
    private static int sVersionInt = -1;

    static List<ThemeDefinition.CompatibilityInfo> loadConfig() {
        List<ThemeDefinition.CompatibilityInfo> ret = new ArrayList<>();
        Log.d(TAG, "START loading theme compatibility config.");
        try {
            Document document = getConfigDocumentTree();
            if (document != null) {
                NodeList list = document.getDocumentElement().getChildNodes();
                int index = list.getLength() - 1;
                while (true) {
                    if (index < 0) {
                        break;
                    }
                    Node temp = list.item(index);
                    if (temp.getNodeType() == 1) {
                        Element element = (Element) temp;
                        String eleName = element.getNodeName();
                        if ("version".equals(eleName)) {
                            sVersionInt = XmlUtils.convertValueToUnsignedInt(element.getTextContent(), -1);
                            if (sVersionInt < 0) {
                                break;
                            }
                        } else {
                            ThemeDefinition.CompatibilityType type = ThemeDefinition.CompatibilityType.getType(eleName);
                            if (type != ThemeDefinition.CompatibilityType.NONE) {
                                NodeList nodeList = element.getElementsByTagName(TAG_ITEM);
                                if (nodeList != null) {
                                    int size = nodeList.getLength();
                                    for (int i = 0; i < size; i++) {
                                        ThemeDefinition.CompatibilityInfo info = parseCompatibilityInfo(type, nodeList.item(i));
                                        if (info != null) {
                                            ret.add(info);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    index--;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Invalid item format: " + e.toString());
            e.printStackTrace();
        }
        if (sVersionInt < 0) {
            ret.clear();
        }
        Log.d(TAG, "END loading: version=" + sVersionInt + " size=" + ret.size());
        return ret;
    }

    public static int getVersion(String path) {
        BufferedReader cin = null;
        Object obj = "<version>";
        Object obj2 = "</version>";
        try {
            cin = new BufferedReader(new FileReader(path));
            while (true) {
                String readLine = cin.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                String line2 = line.trim();
                if (line2.startsWith("<version>") && line2.endsWith("</version>")) {
                    int parseInt = Integer.parseInt(line2.substring("<version>".length(), line2.length() - "</version>".length()));
                    IOUtils.closeQuietly(cin);
                    return parseInt;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "getVersion(): " + path + "  " + e.toString());
            e.printStackTrace();
        } catch (Throwable th) {
            IOUtils.closeQuietly((Reader) null);
            throw th;
        }
        IOUtils.closeQuietly(cin);
        return -1;
    }

    /* JADX INFO: finally extract failed */
    private static Document getConfigDocumentTree() {
        int updateVersion = getVersion(DATA_THEME_COMPATIBILITY_PATH);
        int romVersion = getVersion(SYSTEM_THEME_COMPATIBILITY_PATH);
        String[] prefPath = new String[2];
        int i = 0;
        if (updateVersion > romVersion) {
            prefPath[0] = DATA_THEME_COMPATIBILITY_PATH;
            prefPath[1] = SYSTEM_THEME_COMPATIBILITY_PATH;
        } else {
            prefPath[0] = SYSTEM_THEME_COMPATIBILITY_PATH;
        }
        Log.d(TAG, "getConfigDocumentTree(): " + romVersion + " vs " + updateVersion);
        int length = prefPath.length;
        while (i < length) {
            String path = prefPath[i];
            try {
                Log.d(TAG, "    parse file: " + path);
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputStream is = new BufferedInputStream(new FileInputStream(path), 8192);
                Document parse = builder.parse(is);
                IOUtils.closeQuietly(is);
                return parse;
            } catch (Exception e) {
                Log.d(TAG, "    invalid file format: " + path + " -- " + e.toString());
                e.printStackTrace();
                IOUtils.closeQuietly((InputStream) null);
                i++;
            } catch (Throwable th) {
                IOUtils.closeQuietly((InputStream) null);
                throw th;
            }
        }
        return null;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: com.miui.internal.content.res.ThemeDefinition$NewDefaultValue} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: com.miui.internal.content.res.ThemeDefinition$NewDefaultValue} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: com.miui.internal.content.res.ThemeDefinition$NewDefaultValue} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: com.miui.internal.content.res.ThemeDefinition$FallbackInfo} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: com.miui.internal.content.res.ThemeDefinition$NewDefaultValue} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.miui.internal.content.res.ThemeDefinition.CompatibilityInfo parseCompatibilityInfo(com.miui.internal.content.res.ThemeDefinition.CompatibilityType r13, org.w3c.dom.Node r14) {
        /*
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            org.w3c.dom.NamedNodeMap r6 = r14.getAttributes()
            int r7 = r6.getLength()
            int r7 = r7 + -1
        L_0x0010:
            if (r7 < 0) goto L_0x007e
            org.w3c.dom.Node r8 = r6.item(r7)
            java.lang.String r9 = r8.getNodeName()
            java.lang.String r10 = "package"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x0027
            java.lang.String r0 = r8.getNodeValue()
            goto L_0x007b
        L_0x0027:
            java.lang.String r9 = r8.getNodeName()
            java.lang.String r10 = "resourceType"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x0038
            java.lang.String r1 = r8.getNodeValue()
            goto L_0x007b
        L_0x0038:
            java.lang.String r9 = r8.getNodeName()
            java.lang.String r10 = "property1"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x0049
            java.lang.String r2 = r8.getNodeValue()
            goto L_0x007b
        L_0x0049:
            java.lang.String r9 = r8.getNodeName()
            java.lang.String r10 = "property2"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x005a
            java.lang.String r3 = r8.getNodeValue()
            goto L_0x007b
        L_0x005a:
            java.lang.String r9 = r8.getNodeName()
            java.lang.String r10 = "property3"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x006b
            java.lang.String r4 = r8.getNodeValue()
            goto L_0x007b
        L_0x006b:
            java.lang.String r9 = r8.getNodeName()
            java.lang.String r10 = "propertyExtra"
            boolean r9 = r10.equals(r9)
            if (r9 == 0) goto L_0x007b
            java.lang.String r5 = r8.getNodeValue()
        L_0x007b:
            int r7 = r7 + -1
            goto L_0x0010
        L_0x007e:
            r7 = 0
            com.miui.internal.content.res.ThemeDefinition$CompatibilityType r8 = com.miui.internal.content.res.ThemeDefinition.CompatibilityType.FALLBACK
            if (r13 != r8) goto L_0x00cf
            com.miui.internal.content.res.ThemeDefinition$FallbackInfo r8 = new com.miui.internal.content.res.ThemeDefinition$FallbackInfo
            r8.<init>()
            r8.mResPkgName = r0
            com.miui.internal.content.res.ThemeDefinition$ResourceType r9 = com.miui.internal.content.res.ThemeDefinition.ResourceType.getType(r1)
            r8.mResType = r9
            r8.mResOriginalName = r2
            r8.mResFallbackName = r3
            boolean r9 = com.miui.internal.content.res.ThemeToolUtils.isEmpty(r4)
            if (r9 != 0) goto L_0x00a4
            boolean r9 = r4.equals(r0)
            if (r9 == 0) goto L_0x00a2
            goto L_0x00a4
        L_0x00a2:
            r9 = r4
            goto L_0x00a5
        L_0x00a4:
            r9 = 0
        L_0x00a5:
            r8.mResFallbackPkgName = r9
            java.util.List r9 = splitItemString(r5)
            boolean r10 = r9.isEmpty()
            if (r10 != 0) goto L_0x00cd
            r10 = 5
            int r11 = r9.size()
            int r10 = java.lang.Math.min(r10, r11)
            java.lang.String[] r10 = new java.lang.String[r10]
            r11 = 0
        L_0x00bd:
            int r12 = r10.length
            if (r11 >= r12) goto L_0x00cb
            java.lang.Object r12 = r9.get(r11)
            java.lang.String r12 = (java.lang.String) r12
            r10[r11] = r12
            int r11 = r11 + 1
            goto L_0x00bd
        L_0x00cb:
            r8.mResPreferredConfigs = r10
        L_0x00cd:
            r7 = r8
            goto L_0x00e6
        L_0x00cf:
            com.miui.internal.content.res.ThemeDefinition$CompatibilityType r8 = com.miui.internal.content.res.ThemeDefinition.CompatibilityType.NEW_DEF_VALUE
            if (r13 != r8) goto L_0x00e6
            com.miui.internal.content.res.ThemeDefinition$NewDefaultValue r8 = new com.miui.internal.content.res.ThemeDefinition$NewDefaultValue
            r8.<init>()
            r8.mResPkgName = r0
            com.miui.internal.content.res.ThemeDefinition$ResourceType r9 = com.miui.internal.content.res.ThemeDefinition.ResourceType.getType(r1)
            r8.mResType = r9
            r8.mResName = r2
            r8.mResValue = r3
            r7 = r8
            goto L_0x00e7
        L_0x00e6:
        L_0x00e7:
            if (r7 == 0) goto L_0x010a
            boolean r8 = r7.isValid()
            if (r8 != 0) goto L_0x010a
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "Invalid compatibility info: "
            r8.append(r9)
            java.lang.String r9 = r7.toString()
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            java.lang.String r9 = "ThemeCompatibility"
            android.util.Log.d(r9, r8)
            r7 = 0
        L_0x010a:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.content.res.ThemeCompatibilityLoader.parseCompatibilityInfo(com.miui.internal.content.res.ThemeDefinition$CompatibilityType, org.w3c.dom.Node):com.miui.internal.content.res.ThemeDefinition$CompatibilityInfo");
    }

    private static List<String> splitItemString(String item) {
        List<String> ret = new ArrayList<>();
        String item2 = item != null ? item.trim() : null;
        if (item2 != null && !item2.startsWith("#")) {
            for (String tmp : item2.split(" |\t")) {
                if (tmp != null) {
                    String tmp2 = tmp.trim();
                    if (!tmp2.isEmpty()) {
                        ret.add(tmp2);
                    }
                }
            }
        }
        return ret;
    }
}
