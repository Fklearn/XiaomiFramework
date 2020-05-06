package com.miui.maml.util;

import com.miui.maml.ResourceLoader;
import java.io.InputStream;
import miui.content.res.ThemeResources;
import miui.content.res.ThemeResourcesSystem;

public class FancyIconResourceLoader extends ResourceLoader {
    private String mRelatviePathBaseIcons;

    public FancyIconResourceLoader(String str) {
        this.mRelatviePathBaseIcons = str;
    }

    public InputStream getInputStream(String str, long[] jArr) {
        ThemeResourcesSystem system = ThemeResources.getSystem();
        return system.getIconStream(this.mRelatviePathBaseIcons + str, jArr);
    }

    public boolean resourceExists(String str) {
        ThemeResourcesSystem system = ThemeResources.getSystem();
        return system.hasIcon(this.mRelatviePathBaseIcons + str);
    }
}
