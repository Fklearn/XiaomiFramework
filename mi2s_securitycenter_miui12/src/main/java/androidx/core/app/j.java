package androidx.core.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;

public final class j implements Iterable<Intent> {

    /* renamed from: a  reason: collision with root package name */
    private final ArrayList<Intent> f699a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    private final Context f700b;

    public interface a {
        @Nullable
        Intent getSupportParentActivityIntent();
    }

    private j(Context context) {
        this.f700b = context;
    }

    @NonNull
    public static j a(@NonNull Context context) {
        return new j(context);
    }

    @NonNull
    public j a(@NonNull Activity activity) {
        Intent supportParentActivityIntent = activity instanceof a ? ((a) activity).getSupportParentActivityIntent() : null;
        if (supportParentActivityIntent == null) {
            supportParentActivityIntent = h.a(activity);
        }
        if (supportParentActivityIntent != null) {
            ComponentName component = supportParentActivityIntent.getComponent();
            if (component == null) {
                component = supportParentActivityIntent.resolveActivity(this.f700b.getPackageManager());
            }
            a(component);
            a(supportParentActivityIntent);
        }
        return this;
    }

    public j a(ComponentName componentName) {
        int size = this.f699a.size();
        try {
            Context context = this.f700b;
            while (true) {
                Intent a2 = h.a(context, componentName);
                if (a2 == null) {
                    return this;
                }
                this.f699a.add(size, a2);
                context = this.f700b;
                componentName = a2.getComponent();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TaskStackBuilder", "Bad ComponentName while traversing activity parent metadata");
            throw new IllegalArgumentException(e);
        }
    }

    @NonNull
    public j a(@NonNull Intent intent) {
        this.f699a.add(intent);
        return this;
    }

    public void a() {
        a((Bundle) null);
    }

    public void a(@Nullable Bundle bundle) {
        if (!this.f699a.isEmpty()) {
            ArrayList<Intent> arrayList = this.f699a;
            Intent[] intentArr = (Intent[]) arrayList.toArray(new Intent[arrayList.size()]);
            intentArr[0] = new Intent(intentArr[0]).addFlags(268484608);
            if (!androidx.core.content.a.a(this.f700b, intentArr, bundle)) {
                Intent intent = new Intent(intentArr[intentArr.length - 1]);
                intent.addFlags(268435456);
                this.f700b.startActivity(intent);
                return;
            }
            return;
        }
        throw new IllegalStateException("No intents added to TaskStackBuilder; cannot startActivities");
    }

    @Deprecated
    public Iterator<Intent> iterator() {
        return this.f699a.iterator();
    }
}
