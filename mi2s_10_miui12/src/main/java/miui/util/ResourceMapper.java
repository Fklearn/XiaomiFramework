package miui.util;

import android.content.res.Resources;
import android.util.TypedValue;

public class ResourceMapper {
    public static int resolveReference(Resources res, int id) {
        TypedValue outValue = new TypedValue();
        res.getValue(id, outValue, true);
        return outValue.resourceId == 0 ? id : outValue.resourceId;
    }
}
