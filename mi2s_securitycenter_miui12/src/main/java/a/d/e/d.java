package a.d.e;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface d<T> {
    @Nullable
    T acquire();

    boolean release(@NonNull T t);
}
