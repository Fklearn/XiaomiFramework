package android.content.pm;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

abstract class BaseParceledListSlice<T> implements Parcelable {
    /* access modifiers changed from: private */
    public static boolean DEBUG = false;
    private static final int MAX_IPC_SIZE = 65536;
    /* access modifiers changed from: private */
    public static String TAG = "ParceledListSlice";
    private int mInlineCountLimit = Integer.MAX_VALUE;
    /* access modifiers changed from: private */
    public final List<T> mList;

    BaseParceledListSlice(Parcel parcel, ClassLoader classLoader) {
        int readInt = parcel.readInt();
        this.mList = new ArrayList(readInt);
        if (DEBUG) {
            Log.d(TAG, "Retrieving " + readInt + " items");
        }
        if (readInt > 0) {
            Parcelable.Creator<?> readParcelableCreator = readParcelableCreator(parcel, classLoader);
            Class<?> cls = null;
            int i = 0;
            while (i < readInt && parcel.readInt() != 0) {
                Object readCreator = readCreator(readParcelableCreator, parcel, classLoader);
                if (cls == null) {
                    cls = readCreator.getClass();
                } else {
                    verifySameType(cls, readCreator.getClass());
                }
                this.mList.add(readCreator);
                if (DEBUG) {
                    String str = TAG;
                    StringBuilder sb = new StringBuilder();
                    sb.append("Read inline #");
                    sb.append(i);
                    sb.append(": ");
                    List<T> list = this.mList;
                    sb.append(list.get(list.size() - 1));
                    Log.d(str, sb.toString());
                }
                i++;
            }
            if (i < readInt) {
                IBinder readStrongBinder = parcel.readStrongBinder();
                while (i < readInt) {
                    if (DEBUG) {
                        Log.d(TAG, "Reading more @" + i + " of " + readInt + ": retriever=" + readStrongBinder);
                    }
                    Parcel obtain = Parcel.obtain();
                    Parcel obtain2 = Parcel.obtain();
                    obtain.writeInt(i);
                    try {
                        readStrongBinder.transact(1, obtain, obtain2, 0);
                        while (i < readInt && obtain2.readInt() != 0) {
                            Object readCreator2 = readCreator(readParcelableCreator, obtain2, classLoader);
                            verifySameType(cls, readCreator2.getClass());
                            this.mList.add(readCreator2);
                            if (DEBUG) {
                                String str2 = TAG;
                                StringBuilder sb2 = new StringBuilder();
                                sb2.append("Read extra #");
                                sb2.append(i);
                                sb2.append(": ");
                                List<T> list2 = this.mList;
                                sb2.append(list2.get(list2.size() - 1));
                                Log.d(str2, sb2.toString());
                            }
                            i++;
                        }
                        obtain2.recycle();
                        obtain.recycle();
                    } catch (RemoteException e) {
                        Log.w(TAG, "Failure retrieving array; only received " + i + " of " + readInt, e);
                        return;
                    }
                }
            }
        }
    }

    public BaseParceledListSlice(List<T> list) {
        this.mList = list;
    }

    private T readCreator(Parcelable.Creator<?> creator, Parcel parcel, ClassLoader classLoader) {
        return creator instanceof Parcelable.ClassLoaderCreator ? ((Parcelable.ClassLoaderCreator) creator).createFromParcel(parcel, classLoader) : creator.createFromParcel(parcel);
    }

    /* access modifiers changed from: private */
    public static void verifySameType(Class<?> cls, Class<?> cls2) {
        if (!cls2.equals(cls)) {
            throw new IllegalArgumentException("Can't unparcel type " + cls2.getName() + " in list of type " + cls.getName());
        }
    }

    public List<T> getList() {
        return this.mList;
    }

    /* access modifiers changed from: protected */
    public abstract Parcelable.Creator<?> readParcelableCreator(Parcel parcel, ClassLoader classLoader);

    public void setInlineCountLimit(int i) {
        this.mInlineCountLimit = i;
    }

    /* access modifiers changed from: protected */
    public abstract void writeElement(T t, Parcel parcel, int i);

    /* access modifiers changed from: protected */
    public abstract void writeParcelableCreator(T t, Parcel parcel);

    public void writeToParcel(Parcel parcel, final int i) {
        final int size = this.mList.size();
        parcel.writeInt(size);
        if (DEBUG) {
            Log.d(TAG, "Writing " + size + " items");
        }
        if (size > 0) {
            final Class<?> cls = this.mList.get(0).getClass();
            writeParcelableCreator(this.mList.get(0), parcel);
            int i2 = 0;
            while (i2 < size && i2 < this.mInlineCountLimit && parcel.dataSize() < 65536) {
                parcel.writeInt(1);
                T t = this.mList.get(i2);
                verifySameType(cls, t.getClass());
                writeElement(t, parcel, i);
                if (DEBUG) {
                    Log.d(TAG, "Wrote inline #" + i2 + ": " + this.mList.get(i2));
                }
                i2++;
            }
            if (i2 < size) {
                parcel.writeInt(0);
                AnonymousClass1 r2 = new Binder() {
                    /* access modifiers changed from: protected */
                    public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
                        if (i != 1) {
                            return super.onTransact(i, parcel, parcel2, i2);
                        }
                        int readInt = parcel.readInt();
                        if (BaseParceledListSlice.DEBUG) {
                            String access$100 = BaseParceledListSlice.TAG;
                            Log.d(access$100, "Writing more @" + readInt + " of " + size);
                        }
                        while (readInt < size && parcel2.dataSize() < 65536) {
                            parcel2.writeInt(1);
                            Object obj = BaseParceledListSlice.this.mList.get(readInt);
                            BaseParceledListSlice.verifySameType(cls, obj.getClass());
                            BaseParceledListSlice.this.writeElement(obj, parcel2, i);
                            if (BaseParceledListSlice.DEBUG) {
                                String access$1002 = BaseParceledListSlice.TAG;
                                Log.d(access$1002, "Wrote extra #" + readInt + ": " + BaseParceledListSlice.this.mList.get(readInt));
                            }
                            readInt++;
                        }
                        if (readInt < size) {
                            if (BaseParceledListSlice.DEBUG) {
                                String access$1003 = BaseParceledListSlice.TAG;
                                Log.d(access$1003, "Breaking @" + readInt + " of " + size);
                            }
                            parcel2.writeInt(0);
                        }
                        return true;
                    }
                };
                if (DEBUG) {
                    Log.d(TAG, "Breaking @" + i2 + " of " + size + ": retriever=" + r2);
                }
                parcel.writeStrongBinder(r2);
            }
        }
    }
}
