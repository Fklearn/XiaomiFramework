package com.miui.server;

import android.net.LocalSocket;
import android.util.Slog;
import java.io.IOException;
import java.io.OutputStream;

abstract class UsbManagerConnect implements Runnable {
    static final int BUFFER_SIZE = 4096;
    private static final String TAG = "UsbManagerConnect";
    protected final int MSG_LOCAL = 1;
    protected final int MSG_SHARE_NET = 2;
    protected final int MSG_TO_PC = 0;
    OutputStream mOutputStream;
    LocalSocket mSocket;

    /* access modifiers changed from: package-private */
    public abstract void listenToSocket() throws IOException;

    UsbManagerConnect() {
    }

    /* access modifiers changed from: package-private */
    public synchronized void closeSocket() {
        if (this.mOutputStream != null) {
            try {
                this.mOutputStream.close();
            } catch (IOException ex) {
                Slog.e(TAG, "Failed closing output stream: " + ex);
            }
        }
        if (this.mSocket != null) {
            try {
                this.mSocket.close();
            } catch (IOException ex2) {
                Slog.e(TAG, "Failed closing socket: " + ex2);
            }
        }
        return;
    }

    /* access modifiers changed from: package-private */
    public synchronized void sendResponse(String msg) {
        if (this.mOutputStream != null) {
            try {
                this.mOutputStream.write(msg.getBytes());
            } catch (IOException ex) {
                Slog.e(TAG, "Failed to write response:", ex);
            }
        }
        return;
    }

    /* access modifiers changed from: package-private */
    public synchronized void sendResponse(int msgType, int msgId, byte[] byteMsg) {
        if (!(this.mOutputStream == null || byteMsg == null)) {
            try {
                this.mOutputStream.write(String.format("%04x", new Object[]{Integer.valueOf(msgId)}).getBytes());
                this.mOutputStream.write(String.format("%04x", new Object[]{Integer.valueOf(msgType)}).getBytes());
                this.mOutputStream.write(String.format("%08x", new Object[]{Integer.valueOf(byteMsg.length)}).getBytes());
                this.mOutputStream.write(byteMsg);
                this.mOutputStream.flush();
            } catch (IOException ex) {
                Slog.e(TAG, "Failed to write response:", ex);
            }
        }
        return;
    }

    /* access modifiers changed from: package-private */
    public String getErrMsg(String functionName, String reason) {
        return "FAIL, function name: " + functionName + ", reason: " + reason;
    }

    /* access modifiers changed from: package-private */
    public byte[] getLargeMsg(byte[] msg) {
        byte[] msgLenByte = String.format("%08x", new Object[]{Integer.valueOf(msg.length)}).getBytes();
        byte[] largeMsg = new byte[(msgLenByte.length + msg.length)];
        System.arraycopy(msgLenByte, 0, largeMsg, 0, msgLenByte.length);
        System.arraycopy(msg, 0, largeMsg, 0 + msgLenByte.length, msg.length);
        return largeMsg;
    }
}
