package miui.content.res;

import android.content.res.MiuiResources;
import android.util.Log;
import android.util.MiuiDisplayMetrics;
import com.miui.internal.content.res.ThemeDensityFallbackUtils;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import miui.content.res.ThemeResources;
import miui.telephony.phonenumber.Prefix;

public final class ThemeZipFile {
    static boolean DBG = ThemeResources.DBG;
    private static final String FUZZY_SEARCH_ICON_SUFFIX = "#*.png";
    static String TAG = "ThemeZipFile";
    public static final String THEME_FALLBACK_FILE = "theme_fallback.xml";
    public static final String THEME_VALUE_FILE = "theme_values.xml";
    public static final String THEME_VALUE_FILE_NAME = "theme_values";
    public static final String THEME_VALUE_FILE_SUFFIX = ".xml";
    private static final int sDensity = MiuiDisplayMetrics.DENSITY_DEVICE;
    private static final int[] sFallbackDensities = ThemeDensityFallbackUtils.getFallbackOrder(sDensity);
    protected static final Map<String, WeakReference<ThemeZipFile>> sThemeZipFiles = new HashMap();
    private volatile long mLastModifiedTime = -1;
    private ThemeResources.MetaData mMetaData;
    private String mPath;
    private long mUpatedTime;
    private MyZipFile mZipFile;

    protected static ThemeZipFile getThemeZipFile(ThemeResources.MetaData metaData, String componentName) {
        String path = metaData.mThemePath + componentName;
        WeakReference<ThemeZipFile> ref = sThemeZipFiles.get(path);
        ThemeZipFile themeZipFile = null;
        ThemeZipFile zipFile = ref != null ? (ThemeZipFile) ref.get() : null;
        if (zipFile == null) {
            synchronized (sThemeZipFiles) {
                WeakReference<ThemeZipFile> ref2 = sThemeZipFiles.get(path);
                if (ref2 != null) {
                    themeZipFile = (ThemeZipFile) ref2.get();
                }
                ThemeZipFile zipFile2 = themeZipFile;
                if (zipFile2 == null) {
                    zipFile2 = new ThemeZipFile(path, metaData);
                    sThemeZipFiles.put(path, new WeakReference(zipFile2));
                }
            }
        }
        return zipFile;
    }

    ThemeZipFile(String zipFilePath, ThemeResources.MetaData metaData) {
        if (DBG) {
            String str = TAG;
            Log.d(str, "create ThemeZipFile for " + zipFilePath);
        }
        this.mPath = zipFilePath;
        this.mMetaData = metaData;
    }

    /* access modifiers changed from: package-private */
    public long checkUpdate() {
        if (DBG) {
            String str = TAG;
            Log.d(str, "checkUpdate for " + this.mPath);
        }
        File file = new File(this.mPath);
        long lastModified = file.lastModified();
        if (this.mLastModifiedTime != lastModified && ThemeCompatibility.isCompatibleResource(this.mPath)) {
            synchronized (this) {
                if (this.mLastModifiedTime != lastModified) {
                    this.mUpatedTime = System.currentTimeMillis();
                    clean();
                    if (lastModified != 0) {
                        if (DBG) {
                            String str2 = TAG;
                            Log.d(str2, "openZipFile for " + this.mPath);
                        }
                        try {
                            this.mZipFile = new MyZipFile(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    this.mLastModifiedTime = lastModified;
                }
            }
        }
        return this.mUpatedTime;
    }

    public boolean isValid() {
        return this.mZipFile != null;
    }

    /* access modifiers changed from: package-private */
    public boolean getThemeFile(MiuiResources.ThemeFileInfoOption info) {
        if (!this.mMetaData.mSupportFile || !isValid()) {
            return false;
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        if (!ThemeToolUtils.isEmpty(info.outFilterPath)) {
            buffer.append(info.outFilterPath);
        }
        buffer.append(info.inResourcePath);
        String path = buffer.toString();
        boolean result = getThemeFileInner(info, path);
        if (!result && path.endsWith(".9.png")) {
            buffer.move(-"9.png".length());
            buffer.append("png");
            result = getThemeFileInner(info, buffer.toString());
        }
        if (!result && path.endsWith(".webp")) {
            buffer.move(-"webp".length());
            buffer.append("png");
            result = getThemeFileInner(info, buffer.toString());
        }
        FixedSizeStringBuffer.freeBuffer(buffer);
        return result;
    }

    private boolean getThemeFileInner(MiuiResources.ThemeFileInfoOption info, String path) {
        int i = 160;
        if (getZipInputStream(info, path)) {
            if (info.inDensity != 0) {
                i = info.inDensity;
            }
            info.outDensity = i;
            return true;
        }
        String drawableTag = "/drawable";
        int drawableTagEndIndex = path.indexOf(drawableTag);
        if (drawableTagEndIndex < 0) {
            drawableTag = "/raw";
            drawableTagEndIndex = path.indexOf(drawableTag);
        }
        int i2 = 0;
        if (drawableTagEndIndex <= 0) {
            return false;
        }
        int drawableTagEndIndex2 = drawableTagEndIndex + drawableTag.length();
        String regularPath = regularDpiFallbackPath(path, drawableTagEndIndex2);
        if (path != regularPath) {
            path = regularPath;
            if (getZipInputStream(info, path)) {
                if (info.inDensity != 0) {
                    i = info.inDensity;
                }
                info.outDensity = i;
                return true;
            }
        }
        int drawablePathEndIndex = path.indexOf(47, drawableTagEndIndex2);
        if (drawablePathEndIndex < 0) {
            return false;
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.assign(path, drawableTagEndIndex2);
        boolean result = false;
        int[] iArr = sFallbackDensities;
        int length = iArr.length;
        while (true) {
            if (i2 >= length) {
                break;
            }
            int density = iArr[i2];
            if (density != info.inDensity) {
                buffer.setLength(drawableTagEndIndex2);
                buffer.append(ThemeDensityFallbackUtils.getDensitySuffix(density));
                buffer.append(path, drawablePathEndIndex, path.length());
                if (getZipInputStream(info, buffer.toString())) {
                    if (density != 0) {
                        i = density;
                    }
                    info.outDensity = i;
                    result = true;
                }
            }
            i2++;
        }
        FixedSizeStringBuffer.freeBuffer(buffer);
        return result;
    }

    private String regularDpiFallbackPath(String path, int drawableTagEndIndex) {
        int drawablePathEndIndex = path.indexOf(47, drawableTagEndIndex);
        if (drawablePathEndIndex < 0 || drawablePathEndIndex == drawableTagEndIndex + 1) {
            return path;
        }
        String dpiTag = Prefix.EMPTY;
        int dpiStartIndex = path.indexOf("dpi", drawableTagEndIndex);
        if (dpiStartIndex > 0) {
            int dpiEndIndex = "dpi".length() + dpiStartIndex;
            while (path.charAt(dpiStartIndex) != '-' && dpiStartIndex > drawableTagEndIndex) {
                dpiStartIndex--;
            }
            if (dpiStartIndex == drawableTagEndIndex && dpiEndIndex == drawablePathEndIndex) {
                return path;
            }
            dpiTag = path.substring(dpiStartIndex, dpiEndIndex);
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.assign(path, drawableTagEndIndex);
        buffer.append(dpiTag);
        buffer.append(path, drawablePathEndIndex, path.length());
        String ret = buffer.toString();
        FixedSizeStringBuffer.freeBuffer(buffer);
        return ret;
    }

    private String trimVersionPart(int dpiEndIndex, String path) {
        if (dpiEndIndex >= path.length() - 2 || path.charAt(dpiEndIndex) != '-' || path.charAt(dpiEndIndex + 1) != 'v') {
            return path;
        }
        int versionEndIndex = dpiEndIndex + 2;
        while (versionEndIndex < path.length() && (ch = path.charAt(versionEndIndex)) >= '0' && ch <= '9') {
            versionEndIndex++;
        }
        if (versionEndIndex <= dpiEndIndex + 2) {
            return path;
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.assign(path, dpiEndIndex);
        buffer.append(path, versionEndIndex, path.length());
        String path2 = buffer.toString();
        FixedSizeStringBuffer.freeBuffer(buffer);
        return path2;
    }

    /* access modifiers changed from: package-private */
    public void loadThemeConfig(ThemeResources.LoadThemeConfigCallback callback, String basePath) {
        if (isValid()) {
            FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
            buffer.assign(basePath);
            buffer.append(THEME_VALUE_FILE_NAME);
            buffer.append(THEME_VALUE_FILE_SUFFIX);
            loadThemeConfigInner(callback, buffer.toString(), ThemeResources.ConfigType.THEME_VALUES);
            buffer.move(-THEME_VALUE_FILE_SUFFIX.length());
            buffer.append(ThemeDensityFallbackUtils.getDensitySuffix(sDensity));
            buffer.append(THEME_VALUE_FILE_SUFFIX);
            loadThemeConfigInner(callback, buffer.toString(), ThemeResources.ConfigType.THEME_VALUES);
            buffer.assign(basePath);
            buffer.append(THEME_FALLBACK_FILE);
            loadThemeConfigInner(callback, buffer.toString(), ThemeResources.ConfigType.THEME_FALLBACK);
            FixedSizeStringBuffer.freeBuffer(buffer);
        }
    }

    private void loadThemeConfigInner(ThemeResources.LoadThemeConfigCallback callback, String path, ThemeResources.ConfigType type) {
        InputStream input = getZipInputStream(path);
        if (input != null) {
            callback.load(input, type);
        }
    }

    /* access modifiers changed from: package-private */
    public InputStream getZipInputStream(String path) {
        ZipEntry entry;
        if (isValid() && (entry = this.mZipFile.getEntry(path)) != null) {
            try {
                return this.mZipFile.getInputStream(entry);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean getZipInputStream(MiuiResources.ThemeFileInfoOption info, String path) {
        ZipEntry entry = null;
        try {
            if (path.endsWith(FUZZY_SEARCH_ICON_SUFFIX)) {
                String fuzzyIconName = path.substring(0, path.length() - FUZZY_SEARCH_ICON_SUFFIX.length());
                Enumeration<?> entries = this.mZipFile.entries();
                while (true) {
                    if (!entries.hasMoreElements()) {
                        break;
                    }
                    ZipEntry enumEntry = (ZipEntry) entries.nextElement();
                    if (!enumEntry.isDirectory()) {
                        if (enumEntry.getName().startsWith(fuzzyIconName)) {
                            entry = enumEntry;
                            break;
                        }
                    }
                }
            } else {
                entry = this.mZipFile.getEntry(path);
            }
            if (entry != null) {
                info.outSize = entry.getSize();
                if (info.inRequestStream) {
                    info.outInputStream = this.mZipFile.getInputStream(entry);
                    if (info.outInputStream == null) {
                        return false;
                    }
                }
                if (info.outSize > 0) {
                    return true;
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        clean();
        super.finalize();
    }

    private void clean() {
        if (DBG) {
            String str = TAG;
            Log.d(str, "clean for " + this.mPath);
        }
        MyZipFile myZipFile = this.mZipFile;
        if (myZipFile != null) {
            try {
                myZipFile.close();
            } catch (Exception e) {
            }
            this.mZipFile = null;
        }
    }

    private class MyZipFile extends ZipFile {
        HashMap<String, ZipEntry> mEntryCache = new HashMap<>(size());

        public MyZipFile(File file) throws ZipException, IOException {
            super(file);
            Enumeration<ZipEntry> entries = entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    this.mEntryCache.put(entry.getName(), entry);
                }
            }
        }

        public ZipEntry getEntry(String entryName) {
            return this.mEntryCache.get(entryName);
        }
    }
}
