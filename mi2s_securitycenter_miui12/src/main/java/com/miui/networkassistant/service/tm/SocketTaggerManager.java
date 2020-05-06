package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import b.b.c.j.B;
import com.google.gson.Gson;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.utils.INetUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import miui.security.SecurityManager;

class SocketTaggerManager {
    private static final int MIN_TAG_ID = 1;
    private static final int MSG_SAVE_TAGS = 0;
    private static final String PKG_TO_TAG_MAP_FILE_PATH = ("/data/system/users/" + B.j() + "/pkgtags");
    /* access modifiers changed from: private */
    public static final String SOCKET_TAGGER_SERVER_SOCKET_FD_NAME = ("mtagd" + B.j());
    /* access modifiers changed from: private */
    public static final String TAG = "SocketTaggerManager";
    /* access modifiers changed from: private */
    public Context mContext;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 0) {
                SocketTaggerManager.this.saveTagMap();
            }
        }
    };
    private SecurityManager mSecurityManager;
    /* access modifiers changed from: private */
    public TaggerData mTaggerData;

    private class SocketTaggerServerThread extends Thread {
        LocalServerSocket mLocalServerSocket;
        LocalSocket mLocalSocket;
        Credentials mPeerCredentials;
        private volatile boolean mRunning = true;
        private SecurityManager mSecurityManager;
        OutputStream mServerOutStream;

        public SocketTaggerServerThread() {
            this.mSecurityManager = (SecurityManager) SocketTaggerManager.this.mContext.getSystemService("security");
        }

        private synchronized void closeServerSocketIfNeeded() {
            try {
                if (this.mLocalServerSocket != null) {
                    this.mLocalServerSocket.close();
                    this.mLocalServerSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        private void shutdownSocket() {
            LocalSocket localSocket = this.mLocalSocket;
            if (localSocket != null) {
                try {
                    localSocket.shutdownInput();
                    this.mLocalSocket.shutdownOutput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    this.mLocalSocket.close();
                    this.mLocalSocket = null;
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

        public void destory() {
            this.mRunning = false;
            try {
                Thread.sleep(1000);
                closeServerSocketIfNeeded();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {
            int i;
            Log.i(SocketTaggerManager.TAG, "SocketTaggerServerThread started");
            try {
                this.mLocalServerSocket = new LocalServerSocket(SocketTaggerManager.SOCKET_TAGGER_SERVER_SOCKET_FD_NAME);
            } catch (Exception e) {
                Log.e(SocketTaggerManager.TAG, "mServerSocketThread" + e.toString());
            }
            if (this.mLocalServerSocket == null) {
                Log.e(SocketTaggerManager.TAG, "mLocalServerSocket create failed!");
                return;
            }
            Log.i(SocketTaggerManager.TAG, "mLocalServerSocket created");
            while (this.mRunning) {
                try {
                    this.mLocalSocket = this.mLocalServerSocket.accept();
                    this.mPeerCredentials = this.mLocalSocket.getPeerCredentials();
                    this.mServerOutStream = this.mLocalSocket.getOutputStream();
                    this.mLocalSocket.setSoTimeout(1000);
                    int pid = this.mPeerCredentials.getPid();
                    int uid = this.mPeerCredentials.getUid();
                    String packageNameByPid = this.mSecurityManager.getPackageNameByPid(pid);
                    if (!SocketTaggerManager.this.mTaggerData.uidToMaxTagMap.containsKey(Integer.valueOf(uid)) || ((Integer) SocketTaggerManager.this.mTaggerData.uidToMaxTagMap.get(Integer.valueOf(uid))).intValue() <= 1 || !SocketTaggerManager.this.mTaggerData.pkgToTagMap.containsKey(packageNameByPid)) {
                        i = 0;
                    } else {
                        i = ((Integer) SocketTaggerManager.this.mTaggerData.pkgToTagMap.get(packageNameByPid)).intValue();
                        Log.i(SocketTaggerManager.TAG, String.format("tag_socket pkg:%s, uid:%d, pid:%d, tag:0x%s", new Object[]{packageNameByPid, Integer.valueOf(uid), Integer.valueOf(pid), Integer.valueOf(i)}));
                    }
                    try {
                        this.mServerOutStream.write(INetUtil.htonlBytes(i));
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    shutdownSocket();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            closeServerSocketIfNeeded();
            Log.i(SocketTaggerManager.TAG, "SocketTaggerServerThread destoried");
        }
    }

    private static class TaggerData {
        /* access modifiers changed from: private */
        public HashMap<String, Integer> pkgToTagMap = new HashMap<>();
        /* access modifiers changed from: private */
        public HashMap<Integer, Integer> uidToMaxTagMap = new HashMap<>();

        TaggerData() {
        }
    }

    SocketTaggerManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mSecurityManager = (SecurityManager) this.mContext.getSystemService("security");
        loadTagMap();
        new SocketTaggerServerThread().start();
    }

    /* access modifiers changed from: private */
    public synchronized void saveTagMap() {
        String json = new Gson().toJson((Object) this.mTaggerData);
        Log.i(TAG, "saveTagMap");
        this.mSecurityManager.putSystemDataStringFile(PKG_TO_TAG_MAP_FILE_PATH, json);
    }

    private void saveTagMapDelay() {
        if (this.mHandler.hasMessages(0)) {
            this.mHandler.removeMessages(0);
        }
        this.mHandler.sendEmptyMessageDelayed(0, 10000);
    }

    /* access modifiers changed from: package-private */
    public synchronized void loadTagMap() {
        String readSystemDataStringFile = this.mSecurityManager.readSystemDataStringFile(PKG_TO_TAG_MAP_FILE_PATH);
        this.mTaggerData = readSystemDataStringFile != null ? (TaggerData) new Gson().fromJson(readSystemDataStringFile, TaggerData.class) : new TaggerData();
    }

    /* access modifiers changed from: package-private */
    public synchronized void setTagId(AppInfo appInfo) {
        int i;
        if (this.mTaggerData.pkgToTagMap.containsKey(appInfo.packageName.toString())) {
            i = ((Integer) this.mTaggerData.pkgToTagMap.get(appInfo.packageName.toString())).intValue();
        } else {
            i = this.mTaggerData.uidToMaxTagMap.containsKey(Integer.valueOf(appInfo.uid)) ? ((Integer) this.mTaggerData.uidToMaxTagMap.get(Integer.valueOf(appInfo.uid))).intValue() + 1 : 1;
            this.mTaggerData.uidToMaxTagMap.put(Integer.valueOf(appInfo.uid), Integer.valueOf(i));
            this.mTaggerData.pkgToTagMap.put(appInfo.packageName.toString(), Integer.valueOf(i));
            saveTagMapDelay();
        }
        appInfo.tagId = i;
    }
}
