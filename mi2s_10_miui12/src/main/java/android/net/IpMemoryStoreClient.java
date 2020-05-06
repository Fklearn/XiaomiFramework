package android.net;

import android.content.Context;
import android.net.ipmemorystore.Blob;
import android.net.ipmemorystore.NetworkAttributes;
import android.net.ipmemorystore.OnBlobRetrievedListener;
import android.net.ipmemorystore.OnL2KeyResponseListener;
import android.net.ipmemorystore.OnNetworkAttributesRetrievedListener;
import android.net.ipmemorystore.OnSameL3NetworkResponseListener;
import android.net.ipmemorystore.OnStatusListener;
import android.net.ipmemorystore.SameL3NetworkResponse;
import android.net.ipmemorystore.Status;
import android.os.RemoteException;
import android.util.Log;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public abstract class IpMemoryStoreClient {
    private static final String TAG = IpMemoryStoreClient.class.getSimpleName();
    private final Context mContext;

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws RemoteException;
    }

    /* access modifiers changed from: protected */
    public abstract void runWhenServiceReady(Consumer<IIpMemoryStore> consumer) throws ExecutionException;

    public IpMemoryStoreClient(Context context) {
        if (context != null) {
            this.mContext = context;
            return;
        }
        throw new IllegalArgumentException("missing context");
    }

    private void ignoringRemoteException(ThrowingRunnable r) {
        ignoringRemoteException("Failed to execute remote procedure call", r);
    }

    private void ignoringRemoteException(String message, ThrowingRunnable r) {
        try {
            r.run();
        } catch (RemoteException e) {
            Log.e(TAG, message, e);
        }
    }

    public /* synthetic */ void lambda$storeNetworkAttributes$1$IpMemoryStoreClient(String l2Key, NetworkAttributes attributes, OnStatusListener listener, IIpMemoryStore service) {
        ignoringRemoteException(new ThrowingRunnable(l2Key, attributes, listener) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ NetworkAttributes f$2;
            private final /* synthetic */ OnStatusListener f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                IIpMemoryStore.this.storeNetworkAttributes(this.f$1, this.f$2.toParcelable(), OnStatusListener.toAIDL(this.f$3));
            }
        });
    }

    public void storeNetworkAttributes(String l2Key, NetworkAttributes attributes, OnStatusListener listener) {
        try {
            runWhenServiceReady(new Consumer(l2Key, attributes, listener) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ NetworkAttributes f$2;
                private final /* synthetic */ OnStatusListener f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$storeNetworkAttributes$1$IpMemoryStoreClient(this.f$1, this.f$2, this.f$3, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException e) {
            ignoringRemoteException("Error storing network attributes", new ThrowingRunnable() {
                public final void run() {
                    OnStatusListener.this.onComplete(new Status(-5));
                }
            });
        }
    }

    public /* synthetic */ void lambda$storeBlob$4$IpMemoryStoreClient(String l2Key, String clientId, String name, Blob data, OnStatusListener listener, IIpMemoryStore service) {
        ignoringRemoteException(new ThrowingRunnable(l2Key, clientId, name, data, listener) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ Blob f$4;
            private final /* synthetic */ OnStatusListener f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                IIpMemoryStore.this.storeBlob(this.f$1, this.f$2, this.f$3, this.f$4, OnStatusListener.toAIDL(this.f$5));
            }
        });
    }

    public void storeBlob(String l2Key, String clientId, String name, Blob data, OnStatusListener listener) {
        try {
            runWhenServiceReady(new Consumer(l2Key, clientId, name, data, listener) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ Blob f$4;
                private final /* synthetic */ OnStatusListener f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$storeBlob$4$IpMemoryStoreClient(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException e) {
            ignoringRemoteException("Error storing blob", new ThrowingRunnable() {
                public final void run() {
                    OnStatusListener.this.onComplete(new Status(-5));
                }
            });
        }
    }

    public void findL2Key(NetworkAttributes attributes, OnL2KeyResponseListener listener) {
        try {
            runWhenServiceReady(new Consumer(attributes, listener) {
                private final /* synthetic */ NetworkAttributes f$1;
                private final /* synthetic */ OnL2KeyResponseListener f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$findL2Key$7$IpMemoryStoreClient(this.f$1, this.f$2, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException e) {
            ignoringRemoteException("Error finding L2 Key", new ThrowingRunnable() {
                public final void run() {
                    OnL2KeyResponseListener.this.onL2KeyResponse(new Status(-5), (String) null);
                }
            });
        }
    }

    public /* synthetic */ void lambda$findL2Key$7$IpMemoryStoreClient(NetworkAttributes attributes, OnL2KeyResponseListener listener, IIpMemoryStore service) {
        ignoringRemoteException(new ThrowingRunnable(attributes, listener) {
            private final /* synthetic */ NetworkAttributes f$1;
            private final /* synthetic */ OnL2KeyResponseListener f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                IIpMemoryStore.this.findL2Key(this.f$1.toParcelable(), OnL2KeyResponseListener.toAIDL(this.f$2));
            }
        });
    }

    public void isSameNetwork(String l2Key1, String l2Key2, OnSameL3NetworkResponseListener listener) {
        try {
            runWhenServiceReady(new Consumer(l2Key1, l2Key2, listener) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ OnSameL3NetworkResponseListener f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$isSameNetwork$10$IpMemoryStoreClient(this.f$1, this.f$2, this.f$3, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException e) {
            ignoringRemoteException("Error checking for network sameness", new ThrowingRunnable() {
                public final void run() {
                    OnSameL3NetworkResponseListener.this.onSameL3NetworkResponse(new Status(-5), (SameL3NetworkResponse) null);
                }
            });
        }
    }

    public /* synthetic */ void lambda$isSameNetwork$10$IpMemoryStoreClient(String l2Key1, String l2Key2, OnSameL3NetworkResponseListener listener, IIpMemoryStore service) {
        ignoringRemoteException(new ThrowingRunnable(l2Key1, l2Key2, listener) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ OnSameL3NetworkResponseListener f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                IIpMemoryStore.this.isSameNetwork(this.f$1, this.f$2, OnSameL3NetworkResponseListener.toAIDL(this.f$3));
            }
        });
    }

    public /* synthetic */ void lambda$retrieveNetworkAttributes$13$IpMemoryStoreClient(String l2Key, OnNetworkAttributesRetrievedListener listener, IIpMemoryStore service) {
        ignoringRemoteException(new ThrowingRunnable(l2Key, listener) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ OnNetworkAttributesRetrievedListener f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                IIpMemoryStore.this.retrieveNetworkAttributes(this.f$1, OnNetworkAttributesRetrievedListener.toAIDL(this.f$2));
            }
        });
    }

    public void retrieveNetworkAttributes(String l2Key, OnNetworkAttributesRetrievedListener listener) {
        try {
            runWhenServiceReady(new Consumer(l2Key, listener) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ OnNetworkAttributesRetrievedListener f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$retrieveNetworkAttributes$13$IpMemoryStoreClient(this.f$1, this.f$2, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException e) {
            ignoringRemoteException("Error retrieving network attributes", new ThrowingRunnable() {
                public final void run() {
                    OnNetworkAttributesRetrievedListener.this.onNetworkAttributesRetrieved(new Status(-5), (String) null, (NetworkAttributes) null);
                }
            });
        }
    }

    public /* synthetic */ void lambda$retrieveBlob$16$IpMemoryStoreClient(String l2Key, String clientId, String name, OnBlobRetrievedListener listener, IIpMemoryStore service) {
        ignoringRemoteException(new ThrowingRunnable(l2Key, clientId, name, listener) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ OnBlobRetrievedListener f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                IIpMemoryStore.this.retrieveBlob(this.f$1, this.f$2, this.f$3, OnBlobRetrievedListener.toAIDL(this.f$4));
            }
        });
    }

    public void retrieveBlob(String l2Key, String clientId, String name, OnBlobRetrievedListener listener) {
        try {
            runWhenServiceReady(new Consumer(l2Key, clientId, name, listener) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ String f$3;
                private final /* synthetic */ OnBlobRetrievedListener f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$retrieveBlob$16$IpMemoryStoreClient(this.f$1, this.f$2, this.f$3, this.f$4, (IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException e) {
            ignoringRemoteException("Error retrieving blob", new ThrowingRunnable() {
                public final void run() {
                    OnBlobRetrievedListener.this.onBlobRetrieved(new Status(-5), (String) null, (String) null, (Blob) null);
                }
            });
        }
    }

    public void factoryReset() {
        try {
            runWhenServiceReady(new Consumer() {
                public final void accept(Object obj) {
                    IpMemoryStoreClient.this.lambda$factoryReset$19$IpMemoryStoreClient((IIpMemoryStore) obj);
                }
            });
        } catch (ExecutionException m) {
            Log.e(TAG, "Error executing factory reset", m);
        }
    }

    public /* synthetic */ void lambda$factoryReset$19$IpMemoryStoreClient(IIpMemoryStore service) {
        ignoringRemoteException(new ThrowingRunnable() {
            public final void run() {
                IIpMemoryStore.this.factoryReset();
            }
        });
    }
}
