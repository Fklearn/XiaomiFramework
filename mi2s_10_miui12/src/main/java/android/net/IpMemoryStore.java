package android.net;

import android.content.Context;
import android.net.IIpMemoryStoreCallbacks;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class IpMemoryStore extends IpMemoryStoreClient {
    private static final String TAG = IpMemoryStore.class.getSimpleName();
    /* access modifiers changed from: private */
    public final CompletableFuture<IIpMemoryStore> mService = new CompletableFuture<>();
    private final AtomicReference<CompletableFuture<IIpMemoryStore>> mTailNode = new AtomicReference<>(this.mService);

    public IpMemoryStore(Context context) {
        super(context);
        getNetworkStackClient().fetchIpMemoryStore(new IIpMemoryStoreCallbacks.Stub() {
            public void onIpMemoryStoreFetched(IIpMemoryStore memoryStore) {
                IpMemoryStore.this.mService.complete(memoryStore);
            }

            public int getInterfaceVersion() {
                return 3;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void runWhenServiceReady(Consumer<IIpMemoryStore> cb) throws ExecutionException {
        this.mTailNode.getAndUpdate(new UnaryOperator(cb) {
            private final /* synthetic */ Consumer f$0;

            {
                this.f$0 = r1;
            }

            public final Object apply(Object obj) {
                return ((CompletableFuture) obj).handle(new BiFunction(this.f$0) {
                    private final /* synthetic */ Consumer f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final Object apply(Object obj, Object obj2) {
                        return IpMemoryStore.lambda$runWhenServiceReady$0(this.f$0, (IIpMemoryStore) obj, (Throwable) obj2);
                    }
                });
            }
        });
    }

    static /* synthetic */ IIpMemoryStore lambda$runWhenServiceReady$0(Consumer cb, IIpMemoryStore store, Throwable exception) {
        if (exception != null) {
            Log.wtf(TAG, "Error fetching IpMemoryStore", exception);
            return store;
        }
        try {
            cb.accept(store);
        } catch (Exception e) {
            String str = TAG;
            Log.wtf(str, "Exception occured: " + e.getMessage());
        }
        return store;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public NetworkStackClient getNetworkStackClient() {
        return NetworkStackClient.getInstance();
    }

    public static IpMemoryStore getMemoryStore(Context context) {
        return new IpMemoryStore(context);
    }
}
