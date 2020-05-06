package miui.content.res;

import android.content.res.MiuiResources;
import java.io.InputStream;

public class ThemeResourcesEmpty extends ThemeResources {
    public static ThemeResourcesEmpty sInstance = new ThemeResourcesEmpty();

    public interface LoadThemeValuesCallback {
    }

    protected ThemeResourcesEmpty() {
        super((ThemeResources) null, (MiuiResources) null, "FakeForEmpty", THEME_PATHS[0]);
    }

    public long checkUpdate() {
        return 0;
    }

    public InputStream getThemeStream(String name, long[] size) {
        return null;
    }

    public boolean hasThemeFile(String path) {
        return false;
    }

    public void mergeThemeValues(String filterKey, ThemeValues values) {
    }
}
