package miui.content.res;

import android.util.Log;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeCompatibility {
    private static final Map<String, List<ThemeDefinition.NewDefaultValue>> COMPATIBILITY_DEFAULTVALUE = new HashMap();
    private static final Map<String, List<ThemeDefinition.FallbackInfo>> COMPATIBILITY_FALLBACKS = new HashMap();
    private static final String DISABLE_MIUI_THEME_MECHANISM = "/data/system/theme_config/theme_disable";
    private static final Map<String, List<ThemeDefinition.FallbackInfo>> MIUI_OPTIMIZATION_FALLBACK = new HashMap();
    private static final boolean sThemeEnabled = (!new File(DISABLE_MIUI_THEME_MECHANISM).exists());

    static {
        if (sThemeEnabled) {
            for (ThemeDefinition.CompatibilityInfo info : ThemeCompatibilityLoader.loadConfig()) {
                if (info.mCompatibilityType == ThemeDefinition.CompatibilityType.FALLBACK) {
                    ThemeDefinition.FallbackInfo tmp = (ThemeDefinition.FallbackInfo) info;
                    String pkgName = tmp.mResPkgName;
                    List<ThemeDefinition.FallbackInfo> list = COMPATIBILITY_FALLBACKS.get(pkgName);
                    if (list == null) {
                        list = new ArrayList<>();
                        COMPATIBILITY_FALLBACKS.put(pkgName, list);
                    }
                    list.add(tmp);
                } else if (info.mCompatibilityType == ThemeDefinition.CompatibilityType.NEW_DEF_VALUE) {
                    ThemeDefinition.NewDefaultValue tmp2 = (ThemeDefinition.NewDefaultValue) info;
                    String pkgName2 = tmp2.mResPkgName;
                    List<ThemeDefinition.NewDefaultValue> list2 = COMPATIBILITY_DEFAULTVALUE.get(pkgName2);
                    if (list2 == null) {
                        list2 = new ArrayList<>();
                        COMPATIBILITY_DEFAULTVALUE.put(pkgName2, list2);
                    }
                    list2.add(tmp2);
                }
            }
            List<ThemeDefinition.FallbackInfo> miuiFallback = COMPATIBILITY_FALLBACKS.get(ThemeResources.MIUI_PACKAGE);
            if (miuiFallback != null) {
                for (ThemeDefinition.FallbackInfo fallback : miuiFallback) {
                    String key = combineFallbackInfoKey(fallback.mResType, fallback.mResOriginalName);
                    List<ThemeDefinition.FallbackInfo> list3 = MIUI_OPTIMIZATION_FALLBACK.get(key);
                    if (list3 == null) {
                        list3 = new ArrayList<>();
                        MIUI_OPTIMIZATION_FALLBACK.put(key, list3);
                    }
                    list3.add(fallback);
                }
                return;
            }
            return;
        }
        Log.d("ThemeCompatibility", "theme disabled flag has been checked!!!");
    }

    public static List<ThemeDefinition.FallbackInfo> getFallbackList(String pkgName) {
        return COMPATIBILITY_FALLBACKS.get(pkgName);
    }

    public static List<ThemeDefinition.FallbackInfo> getMayFilterFallbackList(String pkgName, ThemeDefinition.ResourceType mayFilterByType, String mayFilterByOriginPath) {
        if (!ThemeResources.MIUI_PACKAGE.equals(pkgName)) {
            return COMPATIBILITY_FALLBACKS.get(pkgName);
        }
        return MIUI_OPTIMIZATION_FALLBACK.get(combineFallbackInfoKey(mayFilterByType, ThemeToolUtils.getNameFromPath(mayFilterByOriginPath)));
    }

    public static List<ThemeDefinition.NewDefaultValue> getNewDefaultValueList(String pkgName) {
        return COMPATIBILITY_DEFAULTVALUE.get(pkgName);
    }

    private static String combineFallbackInfoKey(ThemeDefinition.ResourceType type, String originName) {
        int index = originName.indexOf(".");
        if (index < 0) {
            index = originName.length();
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.append(type.toString());
        buffer.append("/");
        buffer.append(originName, 0, index);
        String ret = buffer.toString();
        FixedSizeStringBuffer.freeBuffer(buffer);
        return ret;
    }

    public static boolean isThemeEnabled() {
        return sThemeEnabled;
    }

    public static boolean isCompatibleResource(String resourceThemePath) {
        if (!resourceThemePath.startsWith("/data/system/theme/") || !new File(resourceThemePath).exists() || new File(ThemeResources.THEME_VERSION_COMPATIBILITY_PATH).exists()) {
            return true;
        }
        return false;
    }
}
