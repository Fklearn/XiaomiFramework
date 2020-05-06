package com.android.server.pm;

import android.content.pm.IPackageDeleteObserver2;
import android.os.RemoteException;

class PackageManagerServiceInjectorProxy {
    PackageManagerServiceInjectorProxy() {
    }

    static void returnPackageDeletedResultToObserver(Object observer, String packageName, int result) throws RemoteException {
        if (observer != null && (observer instanceof IPackageDeleteObserver2)) {
            ((IPackageDeleteObserver2) observer).onPackageDeleted(packageName, result, (String) null);
        }
    }
}
