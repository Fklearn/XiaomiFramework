package miui.app.constants;

import miui.os.Environment;
import miui.os.FileUtils;

public interface ResourceBrowserConstants {
    public static final String ACTION_PICK_RESOURCE = "miui.intent.action.PICK_RESOURCE";
    public static final String CONFIG_PATH = (MIUI_PATH + ".config/");
    public static final String MAML_CONFIG_PATH = (CONFIG_PATH + "maml/");
    public static final String MIUI_PATH = FileUtils.normalizeDirectoryName(Environment.getExternalStorageMiuiDirectory().getAbsolutePath());
    public static final String REQUEST_CURRENT_USING_PATH = "REQUEST_CURRENT_USING_PATH";
    public static final String REQUEST_TRACK_ID = "REQUEST_TRACK_ID";
    public static final String RESPONSE_PICKED_RESOURCE = "RESPONSE_PICKED_RESOURCE";
    public static final String RESPONSE_TRACK_ID = "RESPONSE_TRACK_ID";
}
