package com.android.server.wm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.GraphicBuffer;
import android.graphics.Rect;
import android.util.Slog;
import com.android.server.wm.nano.WindowManagerProtos;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class TaskSnapshotLoader {
    private static final String TAG = "WindowManager";
    private final TaskSnapshotPersister mPersister;

    TaskSnapshotLoader(TaskSnapshotPersister persister) {
        this.mPersister = persister;
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.TaskSnapshot loadTask(int taskId, int userId, boolean reducedResolution) {
        File file;
        String str;
        int i = taskId;
        int i2 = userId;
        File protoFile = this.mPersister.getProtoFile(i, i2);
        if (reducedResolution) {
            file = this.mPersister.getReducedResolutionBitmapFile(i, i2);
        } else {
            file = this.mPersister.getBitmapFile(i, i2);
        }
        File bitmapFile = file;
        if (bitmapFile == null || !protoFile.exists() || !bitmapFile.exists()) {
            return null;
        }
        try {
            WindowManagerProtos.TaskSnapshotProto proto = WindowManagerProtos.TaskSnapshotProto.parseFrom(Files.readAllBytes(protoFile.toPath()));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.HARDWARE;
            Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.getPath(), options);
            if (bitmap == null) {
                Slog.w("WindowManager", "Failed to load bitmap: " + bitmapFile.getPath());
                return null;
            }
            GraphicBuffer buffer = bitmap.createGraphicBufferHandle();
            if (buffer == null) {
                Slog.w("WindowManager", "Failed to retrieve gralloc buffer for bitmap: " + bitmapFile.getPath());
                return null;
            }
            ComponentName topActivityComponent = ComponentName.unflattenFromString(proto.topActivityComponent);
            float scale = Float.compare(proto.scale, 0.0f) != 0 ? proto.scale : reducedResolution ? this.mPersister.getReducedScale() : 1.0f;
            BitmapFactory.Options options2 = options;
            WindowManagerProtos.TaskSnapshotProto taskSnapshotProto = proto;
            str = "WindowManager";
            try {
                ActivityManager.TaskSnapshot taskSnapshot = new ActivityManager.TaskSnapshot(topActivityComponent, buffer, bitmap.getColorSpace(), proto.orientation, new Rect(proto.insetLeft, proto.insetTop, proto.insetRight, proto.insetBottom), reducedResolution, scale, proto.isRealSnapshot, proto.windowingMode, proto.systemUiVisibility, proto.isTranslucent);
                return taskSnapshot;
            } catch (IOException e) {
                Slog.w(str, "Unable to load task snapshot data for taskId=" + i);
                return null;
            }
        } catch (IOException e2) {
            str = "WindowManager";
            Slog.w(str, "Unable to load task snapshot data for taskId=" + i);
            return null;
        }
    }
}
