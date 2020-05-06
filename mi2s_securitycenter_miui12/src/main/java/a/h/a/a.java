package a.h.a;

import androidx.annotation.NonNull;
import androidx.lifecycle.i;
import androidx.lifecycle.v;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class a {
    @NonNull
    public static <T extends i & v> a a(@NonNull T t) {
        return new b(t, ((v) t).d());
    }

    public abstract void a();

    @Deprecated
    public abstract void a(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);
}
