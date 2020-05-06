package miui.content.res;

import android.app.MiuiThemeHelper;
import android.content.res.MiuiResources;
import android.content.res.Resources;
import android.util.Log;
import com.android.internal.util.XmlUtils;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.util.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ThemeValues {
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PACKAGE = "package";
    private static final String DIVIDER = "|";
    private static final String TAG = "ThemeValues";
    private static final String TAG_ITEM = "item";
    private static final String TRUE = "true";
    public HashMap<Integer, int[]> mIntegerArrays = new HashMap<>();
    public HashMap<Integer, Integer> mIntegers = new HashMap<>();
    public HashMap<Integer, String[]> mStringArrays = new HashMap<>();
    public HashMap<Integer, String> mStrings = new HashMap<>();

    public void putAll(ThemeValues values) {
        this.mIntegers.putAll(values.mIntegers);
        this.mStrings.putAll(values.mStrings);
        this.mIntegerArrays.putAll(values.mIntegerArrays);
        this.mStringArrays.putAll(values.mStringArrays);
    }

    public boolean isEmpty() {
        return this.mIntegers.isEmpty() && this.mStrings.isEmpty() && this.mIntegerArrays.isEmpty() && this.mStringArrays.isEmpty();
    }

    public void mergeNewDefaultValueIfNeed(MiuiResources resource, String pkgName) {
        List<ThemeDefinition.NewDefaultValue> list = ThemeCompatibility.getNewDefaultValueList(pkgName);
        if (list != null) {
            try {
                StringBuilder notFoundBuilder = new StringBuilder();
                StringBuilder duplicateBuilder = new StringBuilder();
                for (ThemeDefinition.NewDefaultValue tmp : list) {
                    int id = getIdentifier(resource, tmp.mResType, tmp.mResName, pkgName);
                    if (id <= 0) {
                        notFoundBuilder.append(DIVIDER);
                        notFoundBuilder.append(tmp.toString());
                    } else {
                        if (!this.mStrings.containsKey(Integer.valueOf(id))) {
                            if (!this.mIntegers.containsKey(Integer.valueOf(id))) {
                                Object value = parseResourceNonArrayValue(tmp.mResType, tmp.mResValue);
                                if (value != null) {
                                    if (tmp.mResType == ThemeDefinition.ResourceType.STRING) {
                                        this.mStrings.put(Integer.valueOf(id), (String) value);
                                    } else {
                                        this.mIntegers.put(Integer.valueOf(id), (Integer) value);
                                    }
                                }
                            }
                        }
                        duplicateBuilder.append(DIVIDER);
                        duplicateBuilder.append(tmp.toString());
                    }
                }
                if (notFoundBuilder.length() != 0) {
                    Log.d(TAG, "can not find newDefValue: " + notFoundBuilder.toString());
                }
                if (duplicateBuilder.length() != 0) {
                    Log.d(TAG, "customized theme has contain this value: " + duplicateBuilder.toString());
                }
            } catch (Exception e) {
            }
        }
    }

    public static ThemeValues parseThemeValues(MiuiResources resource, InputStream is, String defPkgName) {
        Document document;
        DocumentBuilder builder;
        DocumentBuilderFactory factory;
        Object value;
        Map saveMap;
        ThemeValues ret = new ThemeValues();
        try {
            DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder2 = factory2.newDocumentBuilder();
            try {
                Document document2 = builder2.parse(new BufferedInputStream(is, 8192));
                NodeList list = document2.getDocumentElement().getChildNodes();
                short s = 1;
                int index = list.getLength() - 1;
                while (index >= 0) {
                    Node temp = list.item(index);
                    if (temp.getNodeType() != s) {
                        MiuiResources miuiResources = resource;
                        String str = defPkgName;
                        factory = factory2;
                        builder = builder2;
                        document = document2;
                    } else {
                        Element element = (Element) temp;
                        String name = element.getAttribute("name");
                        if (ThemeToolUtils.isEmpty(name)) {
                            MiuiResources miuiResources2 = resource;
                            String str2 = defPkgName;
                            factory = factory2;
                            builder = builder2;
                            document = document2;
                        } else {
                            ThemeDefinition.ResourceType resType = ThemeDefinition.ResourceType.getType(element.getNodeName());
                            if (resType == ThemeDefinition.ResourceType.NONE) {
                                MiuiResources miuiResources3 = resource;
                                String str3 = defPkgName;
                                factory = factory2;
                                builder = builder2;
                                document = document2;
                            } else {
                                try {
                                    if (ignoreResourceValue(defPkgName, resType, name)) {
                                        MiuiResources miuiResources4 = resource;
                                        factory = factory2;
                                        builder = builder2;
                                        document = document2;
                                    } else {
                                        String pkg = element.getAttribute(ATTR_PACKAGE);
                                        if (ThemeToolUtils.isEmpty(pkg)) {
                                            pkg = defPkgName;
                                        }
                                        try {
                                            List<Integer> idList = getIdentifierWithFallback(resource, resType, name, pkg);
                                            if (idList.isEmpty()) {
                                                factory = factory2;
                                                builder = builder2;
                                                document = document2;
                                            } else {
                                                factory = factory2;
                                                if (resType == ThemeDefinition.ResourceType.INTEGER_ARRAY) {
                                                    saveMap = ret.mIntegerArrays;
                                                    builder = builder2;
                                                    value = parseResourceArrayValue(resType, element);
                                                } else if (resType == ThemeDefinition.ResourceType.STRING_ARRAY) {
                                                    saveMap = ret.mStringArrays;
                                                    builder = builder2;
                                                    value = parseResourceArrayValue(resType, element);
                                                } else if (resType == ThemeDefinition.ResourceType.STRING) {
                                                    Map saveMap2 = ret.mStrings;
                                                    DocumentBuilder documentBuilder = builder2;
                                                    value = parseResourceNonArrayValue(resType, element.getTextContent());
                                                    saveMap = saveMap2;
                                                    builder = documentBuilder;
                                                } else {
                                                    Map saveMap3 = ret.mIntegers;
                                                    DocumentBuilder documentBuilder2 = builder2;
                                                    value = parseResourceNonArrayValue(resType, element.getTextContent());
                                                    saveMap = saveMap3;
                                                    builder = documentBuilder2;
                                                }
                                                document = document2;
                                                saveIdentifierMap(saveMap, idList, value);
                                            }
                                        } catch (Exception e) {
                                            e = e;
                                            try {
                                                e.printStackTrace();
                                                IOUtils.closeQuietly(is);
                                                return ret;
                                            } catch (Throwable th) {
                                                th = th;
                                                IOUtils.closeQuietly(is);
                                                throw th;
                                            }
                                        }
                                    }
                                } catch (Exception e2) {
                                    e = e2;
                                    MiuiResources miuiResources5 = resource;
                                    e.printStackTrace();
                                    IOUtils.closeQuietly(is);
                                    return ret;
                                } catch (Throwable th2) {
                                    th = th2;
                                    MiuiResources miuiResources6 = resource;
                                    IOUtils.closeQuietly(is);
                                    throw th;
                                }
                            }
                        }
                    }
                    index--;
                    factory2 = factory;
                    builder2 = builder;
                    document2 = document;
                    s = 1;
                }
                MiuiResources miuiResources7 = resource;
                String str4 = defPkgName;
                DocumentBuilderFactory documentBuilderFactory = factory2;
                DocumentBuilder documentBuilder3 = builder2;
                Document document3 = document2;
            } catch (Exception e3) {
                e = e3;
                MiuiResources miuiResources8 = resource;
                String str5 = defPkgName;
                e.printStackTrace();
                IOUtils.closeQuietly(is);
                return ret;
            } catch (Throwable th3) {
                th = th3;
                MiuiResources miuiResources9 = resource;
                String str6 = defPkgName;
                IOUtils.closeQuietly(is);
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            MiuiResources miuiResources10 = resource;
            InputStream inputStream = is;
            String str52 = defPkgName;
            e.printStackTrace();
            IOUtils.closeQuietly(is);
            return ret;
        } catch (Throwable th4) {
            th = th4;
            MiuiResources miuiResources11 = resource;
            InputStream inputStream2 = is;
            String str62 = defPkgName;
            IOUtils.closeQuietly(is);
            throw th;
        }
        IOUtils.closeQuietly(is);
        return ret;
    }

    private static Object parseResourceArrayValue(ThemeDefinition.ResourceType resType, Element element) {
        NodeList arrayItems = element.getElementsByTagName(TAG_ITEM);
        if (arrayItems != null) {
            int length = arrayItems.getLength();
            int size = length;
            if (length != 0) {
                ArrayList<String> array = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    array.add(arrayItems.item(i).getTextContent());
                }
                if (resType == ThemeDefinition.ResourceType.INTEGER_ARRAY) {
                    int[] integerArray = new int[size];
                    for (int i2 = 0; i2 < size; i2++) {
                        integerArray[i2] = Integer.valueOf(array.get(i2)).intValue();
                    }
                    return integerArray;
                } else if (resType == ThemeDefinition.ResourceType.STRING_ARRAY) {
                    return array.toArray(new String[size]);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private static Object parseResourceNonArrayValue(ThemeDefinition.ResourceType resType, String value) {
        if (ThemeToolUtils.isEmpty(value)) {
            return null;
        }
        String value2 = value.trim();
        if (resType == ThemeDefinition.ResourceType.BOOLEAN) {
            return Integer.valueOf(TRUE.equals(value2) ? 1 : 0);
        }
        if (resType == ThemeDefinition.ResourceType.COLOR || resType == ThemeDefinition.ResourceType.INTEGER || resType == ThemeDefinition.ResourceType.DRAWABLE) {
            return Integer.valueOf(XmlUtils.convertValueToUnsignedInt(value2, 0));
        }
        if (resType == ThemeDefinition.ResourceType.DIMEN) {
            return MiuiThemeHelper.parseDimension(value2);
        }
        if (resType == ThemeDefinition.ResourceType.STRING) {
            return value2;
        }
        return null;
    }

    private static void saveIdentifierMap(Map saveMap, List keyList, Object value) {
        if (value != null) {
            for (Object key : keyList) {
                saveMap.put(key, value);
            }
        }
    }

    private static int getIdentifier(Resources res, ThemeDefinition.ResourceType resType, String resName, String pkgName) {
        String type;
        if (resType == ThemeDefinition.ResourceType.INTEGER_ARRAY || resType == ThemeDefinition.ResourceType.STRING_ARRAY) {
            type = "array";
        } else {
            type = resType.toString();
        }
        int id = res.getIdentifier(resName, type, pkgName);
        if (id != 0 || !ThemeResources.MIUI_PACKAGE.equals(pkgName)) {
            return id;
        }
        int id2 = res.getIdentifier(resName, type, "miui.system");
        if (id2 == 0) {
            return res.getIdentifier(resName, type, "android.miui");
        }
        return id2;
    }

    private static List<Integer> getIdentifierWithFallback(Resources res, ThemeDefinition.ResourceType resType, String resName, String pkgName) {
        int id;
        List<Integer> ret = new ArrayList<>();
        int id2 = getIdentifier(res, resType, resName, pkgName);
        if (id2 > 0) {
            ret.add(Integer.valueOf(id2));
        }
        List<ThemeDefinition.FallbackInfo> fallbackList = ThemeCompatibility.getFallbackList(pkgName);
        if (fallbackList != null) {
            for (ThemeDefinition.FallbackInfo fallback : fallbackList) {
                if (fallback.mResType == resType && fallback.mResFallbackPkgName == null && resName.equals(fallback.mResFallbackName) && (id = getIdentifier(res, resType, fallback.mResOriginalName, pkgName)) > 0) {
                    ret.add(Integer.valueOf(id));
                }
            }
        }
        return ret;
    }

    private static boolean ignoreResourceValue(String pkgName, ThemeDefinition.ResourceType resType, String resName) {
        if (resType != ThemeDefinition.ResourceType.COLOR || !ThemeResources.FRAMEWORK_PACKAGE.equals(pkgName) || !resName.startsWith("statusbar_content")) {
            return false;
        }
        return isOldVersionComponentTheme(pkgName);
    }

    private static boolean isOldVersionComponentTheme(String pkgName) {
        return new File(ThemeResources.THEME_VERSION_COMPATIBILITY_PATH + pkgName).exists();
    }
}
