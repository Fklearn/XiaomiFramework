package com.android.server;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.SystemProperties;
import android.util.Slog;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import libcore.io.IoUtils;

public final class BcbUtil {
    private static final boolean DEBUG = false;
    private static final int SOCKET_CONNECTION_MAX_RETRY = 30;
    private static final String TAG = "BcbUtil";
    private static final String UNCRYPT_SOCKET = "uncrypt";

    public static boolean clearBcb() {
        return setupOrClearBcb(false, (String) null);
    }

    public static boolean setupBcb(String command) {
        return setupOrClearBcb(true, command);
    }

    private static LocalSocket connectService() {
        LocalSocket socket = new LocalSocket();
        boolean done = false;
        int retry = 0;
        while (true) {
            if (retry >= 30) {
                break;
            }
            try {
                socket.connect(new LocalSocketAddress(UNCRYPT_SOCKET, LocalSocketAddress.Namespace.RESERVED));
                done = true;
                break;
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e2) {
                    Slog.w(TAG, "Interrupted: ", e2);
                }
                retry++;
            }
        }
        if (done) {
            return socket;
        }
        Slog.e(TAG, "Timed out connecting to uncrypt socket");
        return null;
    }

    private static boolean setupOrClearBcb(boolean isSetup, String command) {
        if (isSetup) {
            SystemProperties.set("ctl.start", "setup-bcb");
        } else {
            SystemProperties.set("ctl.start", "clear-bcb");
        }
        LocalSocket socket = connectService();
        if (socket == null) {
            Slog.e(TAG, "Failed to connect to uncrypt socket");
            return false;
        }
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            if (isSetup) {
                dos.writeInt(command.length());
                dos.writeBytes(command);
                dos.flush();
            }
            int status = dis.readInt();
            dos.writeInt(0);
            if (status == 100) {
                StringBuilder sb = new StringBuilder();
                sb.append("uncrypt ");
                sb.append(isSetup ? "setup" : "clear");
                sb.append(" bcb successfully finished.");
                Slog.i(TAG, sb.toString());
                IoUtils.closeQuietly(dis);
                IoUtils.closeQuietly(dos);
                IoUtils.closeQuietly(socket);
                return true;
            }
            Slog.e(TAG, "uncrypt failed with status: " + status);
            return false;
        } catch (IOException e) {
            Slog.e(TAG, "IOException when communicating with uncrypt: ", e);
            return false;
        } finally {
            IoUtils.closeQuietly(dis);
            IoUtils.closeQuietly(dos);
            IoUtils.closeQuietly(socket);
        }
    }
}
