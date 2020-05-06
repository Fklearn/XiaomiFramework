package com.android.providers.downloads.remote.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;

public interface IDownloadService extends IInterface {

    public static abstract class Stub extends Binder implements IDownloadService {
        public Stub() {
            attachInterface(this, "com.android.providers.downloads.remote.service.IDownloadService");
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) {
            if (i == 1) {
                parcel.enforceInterface("com.android.providers.downloads.remote.service.IDownloadService");
                boolean a2 = a(parcel.readLong(), parcel.readLong());
                parcel2.writeNoException();
                parcel2.writeInt(a2 ? 1 : 0);
                return true;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("com.android.providers.downloads.remote.service.IDownloadService");
                return true;
            }
        }
    }

    boolean a(long j, long j2);
}
