package com.milink.api.v1;

import android.os.RemoteException;
import com.milink.api.v1.aidl.IMcsDataSource;

public class McsDataSource extends IMcsDataSource.Stub {
    MilinkClientManagerDataSource mDataSource = null;

    public void setDataSource(MilinkClientManagerDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    public String getPrevPhoto(String uri, boolean isRecyle) throws RemoteException {
        MilinkClientManagerDataSource milinkClientManagerDataSource = this.mDataSource;
        if (milinkClientManagerDataSource == null) {
            return null;
        }
        return milinkClientManagerDataSource.getPrevPhoto(uri, isRecyle);
    }

    public String getNextPhoto(String uri, boolean isRecyle) throws RemoteException {
        MilinkClientManagerDataSource milinkClientManagerDataSource = this.mDataSource;
        if (milinkClientManagerDataSource == null) {
            return null;
        }
        return milinkClientManagerDataSource.getNextPhoto(uri, isRecyle);
    }
}
