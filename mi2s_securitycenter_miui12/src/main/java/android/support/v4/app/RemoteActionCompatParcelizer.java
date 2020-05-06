package android.support.v4.app;

import androidx.annotation.RestrictTo;
import androidx.core.app.RemoteActionCompat;
import androidx.versionedparcelable.b;

@RestrictTo({RestrictTo.a.f222a})
public final class RemoteActionCompatParcelizer extends androidx.core.app.RemoteActionCompatParcelizer {
    public static RemoteActionCompat read(b bVar) {
        return androidx.core.app.RemoteActionCompatParcelizer.read(bVar);
    }

    public static void write(RemoteActionCompat remoteActionCompat, b bVar) {
        androidx.core.app.RemoteActionCompatParcelizer.write(remoteActionCompat, bVar);
    }
}
