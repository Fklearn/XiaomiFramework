package com.miui.maml;

import android.content.Context;
import com.miui.maml.util.ZipResourceLoader;
import java.io.File;

public class ScreenElementRootFactory {

    public static class Parameter {
        /* access modifiers changed from: private */
        public Context mContext;
        /* access modifiers changed from: private */
        public String mPath;
        /* access modifiers changed from: private */
        public ResourceLoader mResourceLoader;

        public Parameter(Context context, ResourceLoader resourceLoader) {
            if (context != null) {
                this.mContext = context.getApplicationContext();
            }
            this.mResourceLoader = resourceLoader;
        }

        public Parameter(Context context, String str) {
            if (context != null) {
                this.mContext = context.getApplicationContext();
            }
            this.mPath = str;
        }
    }

    public static ScreenElementRoot create(Parameter parameter) {
        Context access$000 = parameter.mContext;
        if (access$000 != null) {
            ResourceLoader access$100 = parameter.mResourceLoader;
            String access$200 = parameter.mPath;
            if (access$100 == null && access$200 != null && new File(access$200).exists()) {
                access$100 = new ZipResourceLoader(access$200).setLocal(access$000.getResources().getConfiguration().locale);
            }
            ResourceLoader resourceLoader = access$100;
            if (resourceLoader == null) {
                return null;
            }
            return new ScreenElementRoot(new ScreenContext(access$000, (ResourceManager) new LifecycleResourceManager(resourceLoader, 3600000, 360000)));
        }
        throw new NullPointerException();
    }
}
