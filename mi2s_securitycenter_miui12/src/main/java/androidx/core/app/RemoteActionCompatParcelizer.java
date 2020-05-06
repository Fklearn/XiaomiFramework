package androidx.core.app;

import android.app.PendingIntent;
import android.os.Parcelable;
import androidx.annotation.RestrictTo;
import androidx.core.graphics.drawable.IconCompat;
import androidx.versionedparcelable.b;
import androidx.versionedparcelable.d;

@RestrictTo({RestrictTo.a.LIBRARY})
public class RemoteActionCompatParcelizer {
    public static RemoteActionCompat read(b bVar) {
        RemoteActionCompat remoteActionCompat = new RemoteActionCompat();
        remoteActionCompat.f678a = (IconCompat) bVar.a(remoteActionCompat.f678a, 1);
        remoteActionCompat.f679b = bVar.a(remoteActionCompat.f679b, 2);
        remoteActionCompat.f680c = bVar.a(remoteActionCompat.f680c, 3);
        remoteActionCompat.f681d = (PendingIntent) bVar.a(remoteActionCompat.f681d, 4);
        remoteActionCompat.e = bVar.a(remoteActionCompat.e, 5);
        remoteActionCompat.f = bVar.a(remoteActionCompat.f, 6);
        return remoteActionCompat;
    }

    public static void write(RemoteActionCompat remoteActionCompat, b bVar) {
        bVar.a(false, false);
        bVar.b((d) remoteActionCompat.f678a, 1);
        bVar.b(remoteActionCompat.f679b, 2);
        bVar.b(remoteActionCompat.f680c, 3);
        bVar.b((Parcelable) remoteActionCompat.f681d, 4);
        bVar.b(remoteActionCompat.e, 5);
        bVar.b(remoteActionCompat.f, 6);
    }
}
