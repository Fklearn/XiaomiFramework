package com.miui.internal.content.res;

import android.content.res.MiuiResources;
import com.miui.internal.content.res.ThemeDefinition;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import miui.util.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ThemeFallback {
    private static final String ATTR_NAME = "name";
    private static final String ATTR_PACKAGE = "package";
    public HashMap<String, ThemeDefinition.FallbackInfo> mFallbackInfoMap = new HashMap<>();

    public static ThemeFallback parseThemeFallback(MiuiResources resource, InputStream is, String defPkgName) {
        Document document;
        DocumentBuilder builder;
        DocumentBuilderFactory factory;
        ThemeFallback ret = new ThemeFallback();
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
                        String str = defPkgName;
                        factory = factory2;
                        builder = builder2;
                        document = document2;
                    } else {
                        Element element = (Element) temp;
                        String name = element.getAttribute("name");
                        if (ThemeToolUtils.isEmpty(name)) {
                            String str2 = defPkgName;
                            factory = factory2;
                            builder = builder2;
                            document = document2;
                        } else {
                            ThemeDefinition.ResourceType resType = ThemeDefinition.ResourceType.getType(element.getNodeName());
                            if (resType == ThemeDefinition.ResourceType.NONE) {
                                String str3 = defPkgName;
                                factory = factory2;
                                builder = builder2;
                                document = document2;
                            } else {
                                String pkg = element.getAttribute(ATTR_PACKAGE);
                                if (ThemeToolUtils.isEmpty(pkg)) {
                                    pkg = defPkgName;
                                }
                                if (resType == ThemeDefinition.ResourceType.DRAWABLE) {
                                    String value = element.getTextContent();
                                    if (!ThemeToolUtils.isEmpty(value)) {
                                        String str4 = defPkgName;
                                        factory = factory2;
                                        ThemeDefinition.FallbackInfo f = new ThemeDefinition.FallbackInfo();
                                        try {
                                            f.mResPkgName = str4;
                                            builder = builder2;
                                            f.mResType = ThemeDefinition.ResourceType.DRAWABLE;
                                            f.mResOriginalName = name.trim();
                                            f.mResFallbackName = value.trim();
                                            f.mResFallbackPkgName = pkg;
                                            document = document2;
                                            ret.mFallbackInfoMap.put(f.mResOriginalName, f);
                                        } catch (Exception e) {
                                            e = e;
                                        }
                                    } else {
                                        String str5 = defPkgName;
                                        factory = factory2;
                                        builder = builder2;
                                        document = document2;
                                    }
                                } else {
                                    String str6 = defPkgName;
                                    factory = factory2;
                                    builder = builder2;
                                    document = document2;
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
                String str7 = defPkgName;
                DocumentBuilderFactory documentBuilderFactory = factory2;
                DocumentBuilder documentBuilder = builder2;
                Document document3 = document2;
            } catch (Exception e2) {
                e = e2;
                String str8 = defPkgName;
                try {
                    e.printStackTrace();
                    IOUtils.closeQuietly(is);
                    return ret;
                } catch (Throwable th) {
                    th = th;
                    IOUtils.closeQuietly(is);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                String str9 = defPkgName;
                IOUtils.closeQuietly(is);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            InputStream inputStream = is;
            String str82 = defPkgName;
            e.printStackTrace();
            IOUtils.closeQuietly(is);
            return ret;
        } catch (Throwable th3) {
            th = th3;
            InputStream inputStream2 = is;
            String str92 = defPkgName;
            IOUtils.closeQuietly(is);
            throw th;
        }
        IOUtils.closeQuietly(is);
        return ret;
    }
}
