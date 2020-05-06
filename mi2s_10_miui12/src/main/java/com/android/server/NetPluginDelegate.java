package com.android.server;

import android.net.NetworkSpecifier;
import android.os.Handler;
import android.util.Log;
import android.util.Slog;
import java.lang.reflect.InvocationTargetException;

public class NetPluginDelegate {
    private static final boolean LOGV = true;
    private static final String TAG = "NetPluginDelegate";
    private static boolean extJarAvail = true;
    private static Object tcpBufferManagerObj = null;
    private static Class tcpBufferRelay = null;
    private static boolean vendorPropJarAvail = true;
    private static Object vendorPropManagerObj = null;
    private static Class vendorPropRelay = null;

    public static String get5GTcpBuffers(String currentTcpBuffer, NetworkSpecifier sepcifier) {
        String tcpBuffer = currentTcpBuffer;
        Slog.v(TAG, "get5GTcpBuffers");
        if (!extJarAvail || !loadConnExtJar()) {
            return currentTcpBuffer;
        }
        try {
            Object ret = tcpBufferRelay.getMethod("get5GTcpBuffers", new Class[]{String.class, NetworkSpecifier.class}).invoke(tcpBufferManagerObj, new Object[]{currentTcpBuffer, sepcifier});
            if (ret == null || !(ret instanceof String)) {
                return tcpBuffer;
            }
            return (String) ret;
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Log.w(TAG, "Failed to invoke get5GTcpBuffers()");
            e.printStackTrace();
            extJarAvail = false;
            return tcpBuffer;
        } catch (Exception e2) {
            Log.w(TAG, "Error calling get5GTcpBuffers Method on extension jar");
            e2.printStackTrace();
            extJarAvail = false;
            return tcpBuffer;
        }
    }

    public static void registerHandler(Handler mHandler) {
        Slog.v(TAG, "registerHandler");
        if (extJarAvail && loadConnExtJar()) {
            try {
                tcpBufferRelay.getMethod("registerHandler", new Class[]{Handler.class}).invoke(tcpBufferManagerObj, new Object[]{mHandler});
            } catch (NoSuchMethodException | SecurityException | InvocationTargetException e) {
                Log.w(TAG, "Failed to call registerHandler");
                e.printStackTrace();
                extJarAvail = false;
            } catch (Exception e2) {
                Log.w(TAG, "Error calling registerHandler Method on extension jar");
                e2.printStackTrace();
                extJarAvail = false;
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0094, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static synchronized boolean loadConnExtJar() {
        /*
            java.lang.Class<com.android.server.NetPluginDelegate> r0 = com.android.server.NetPluginDelegate.class
            monitor-enter(r0)
            java.lang.String r1 = "com.qualcomm.qti.net.connextension.TCPBufferManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0095 }
            r2.<init>()     // Catch:{ all -> 0x0095 }
            java.io.File r3 = android.os.Environment.getRootDirectory()     // Catch:{ all -> 0x0095 }
            java.lang.String r3 = r3.getAbsolutePath()     // Catch:{ all -> 0x0095 }
            r2.append(r3)     // Catch:{ all -> 0x0095 }
            java.lang.String r3 = "/framework/ConnectivityExt.jar"
            r2.append(r3)     // Catch:{ all -> 0x0095 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0095 }
            java.lang.Class r3 = tcpBufferRelay     // Catch:{ all -> 0x0095 }
            r4 = 1
            if (r3 == 0) goto L_0x0029
            java.lang.Object r3 = tcpBufferManagerObj     // Catch:{ all -> 0x0095 }
            if (r3 == 0) goto L_0x0029
            monitor-exit(r0)
            return r4
        L_0x0029:
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x0095 }
            r3.<init>(r2)     // Catch:{ all -> 0x0095 }
            boolean r3 = r3.exists()     // Catch:{ all -> 0x0095 }
            extJarAvail = r3     // Catch:{ all -> 0x0095 }
            boolean r3 = extJarAvail     // Catch:{ all -> 0x0095 }
            r5 = 0
            if (r3 != 0) goto L_0x0042
            java.lang.String r3 = "NetPluginDelegate"
            java.lang.String r4 = "ConnectivityExt jar file not present"
            android.util.Log.w(r3, r4)     // Catch:{ all -> 0x0095 }
            monitor-exit(r0)
            return r5
        L_0x0042:
            java.lang.Class r3 = tcpBufferRelay     // Catch:{ all -> 0x0095 }
            if (r3 != 0) goto L_0x0093
            java.lang.Object r3 = tcpBufferManagerObj     // Catch:{ all -> 0x0095 }
            if (r3 != 0) goto L_0x0093
            java.lang.String r3 = "NetPluginDelegate"
            java.lang.String r6 = "loading ConnectivityExt jar"
            android.util.Slog.v(r3, r6)     // Catch:{ all -> 0x0095 }
            dalvik.system.PathClassLoader r3 = new dalvik.system.PathClassLoader     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.ClassLoader r6 = java.lang.ClassLoader.getSystemClassLoader()     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            r3.<init>(r2, r6)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.String r6 = "com.qualcomm.qti.net.connextension.TCPBufferManager"
            java.lang.Class r6 = r3.loadClass(r6)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            tcpBufferRelay = r6     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.Class r6 = tcpBufferRelay     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.Object r6 = r6.newInstance()     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            tcpBufferManagerObj = r6     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.String r6 = "NetPluginDelegate"
            java.lang.String r7 = "ConnectivityExt jar loaded"
            android.util.Slog.v(r6, r7)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            goto L_0x0093
        L_0x0074:
            r3 = move-exception
            java.lang.String r4 = "NetPluginDelegate"
            java.lang.String r6 = "unable to load ConnectivityExt jar"
            android.util.Log.w(r4, r6)     // Catch:{ all -> 0x0095 }
            r3.printStackTrace()     // Catch:{ all -> 0x0095 }
            extJarAvail = r5     // Catch:{ all -> 0x0095 }
            monitor-exit(r0)
            return r5
        L_0x0084:
            r3 = move-exception
            java.lang.String r4 = "NetPluginDelegate"
            java.lang.String r6 = "Failed to find, instantiate or access ConnectivityExt jar "
            android.util.Log.w(r4, r6)     // Catch:{ all -> 0x0095 }
            r3.printStackTrace()     // Catch:{ all -> 0x0095 }
            extJarAvail = r5     // Catch:{ all -> 0x0095 }
            monitor-exit(r0)
            return r5
        L_0x0093:
            monitor-exit(r0)
            return r4
        L_0x0095:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetPluginDelegate.loadConnExtJar():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0094, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static synchronized boolean loadVendorPropJar() {
        /*
            java.lang.Class<com.android.server.NetPluginDelegate> r0 = com.android.server.NetPluginDelegate.class
            monitor-enter(r0)
            java.lang.String r1 = "com.qualcomm.qti.net.vendorpropextension.vendorPropManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0095 }
            r2.<init>()     // Catch:{ all -> 0x0095 }
            java.io.File r3 = android.os.Environment.getRootDirectory()     // Catch:{ all -> 0x0095 }
            java.lang.String r3 = r3.getAbsolutePath()     // Catch:{ all -> 0x0095 }
            r2.append(r3)     // Catch:{ all -> 0x0095 }
            java.lang.String r3 = "/framework/VendorPropExt.jar"
            r2.append(r3)     // Catch:{ all -> 0x0095 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0095 }
            java.lang.Class r3 = vendorPropRelay     // Catch:{ all -> 0x0095 }
            r4 = 1
            if (r3 == 0) goto L_0x0029
            java.lang.Object r3 = vendorPropManagerObj     // Catch:{ all -> 0x0095 }
            if (r3 == 0) goto L_0x0029
            monitor-exit(r0)
            return r4
        L_0x0029:
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x0095 }
            r3.<init>(r2)     // Catch:{ all -> 0x0095 }
            boolean r3 = r3.exists()     // Catch:{ all -> 0x0095 }
            vendorPropJarAvail = r3     // Catch:{ all -> 0x0095 }
            boolean r3 = vendorPropJarAvail     // Catch:{ all -> 0x0095 }
            r5 = 0
            if (r3 != 0) goto L_0x0042
            java.lang.String r3 = "NetPluginDelegate"
            java.lang.String r4 = "VendorPropExt jar file not present"
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0095 }
            monitor-exit(r0)
            return r5
        L_0x0042:
            java.lang.Class r3 = vendorPropRelay     // Catch:{ all -> 0x0095 }
            if (r3 != 0) goto L_0x0093
            java.lang.Object r3 = vendorPropManagerObj     // Catch:{ all -> 0x0095 }
            if (r3 != 0) goto L_0x0093
            java.lang.String r3 = "NetPluginDelegate"
            java.lang.String r6 = "loading VendorPropExt jar"
            android.util.Slog.v(r3, r6)     // Catch:{ all -> 0x0095 }
            dalvik.system.PathClassLoader r3 = new dalvik.system.PathClassLoader     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.ClassLoader r6 = java.lang.ClassLoader.getSystemClassLoader()     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            r3.<init>(r2, r6)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.String r6 = "com.qualcomm.qti.net.vendorpropextension.vendorPropManager"
            java.lang.Class r6 = r3.loadClass(r6)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            vendorPropRelay = r6     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.Class r6 = vendorPropRelay     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.Object r6 = r6.newInstance()     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            vendorPropManagerObj = r6     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            java.lang.String r6 = "NetPluginDelegate"
            java.lang.String r7 = "VendorPropExt jar loaded"
            android.util.Slog.v(r6, r7)     // Catch:{ ClassNotFoundException | IllegalAccessException | InstantiationException -> 0x0084, Exception -> 0x0074 }
            goto L_0x0093
        L_0x0074:
            r3 = move-exception
            java.lang.String r4 = "NetPluginDelegate"
            java.lang.String r6 = "unable to load vendorPropExt jar"
            android.util.Slog.e(r4, r6)     // Catch:{ all -> 0x0095 }
            r3.printStackTrace()     // Catch:{ all -> 0x0095 }
            vendorPropJarAvail = r5     // Catch:{ all -> 0x0095 }
            monitor-exit(r0)
            return r5
        L_0x0084:
            r3 = move-exception
            java.lang.String r4 = "NetPluginDelegate"
            java.lang.String r6 = "Failed to find, instantiate or access VendorPropExt jar "
            android.util.Slog.e(r4, r6)     // Catch:{ all -> 0x0095 }
            r3.printStackTrace()     // Catch:{ all -> 0x0095 }
            vendorPropJarAvail = r5     // Catch:{ all -> 0x0095 }
            monitor-exit(r0)
            return r5
        L_0x0093:
            monitor-exit(r0)
            return r4
        L_0x0095:
            r1 = move-exception
            monitor-exit(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.NetPluginDelegate.loadVendorPropJar():boolean");
    }

    public static String getConfig(String key, String currentConfigValue) {
        String configValue = currentConfigValue;
        if (!vendorPropJarAvail || !loadVendorPropJar()) {
            return configValue;
        }
        try {
            Object ret = vendorPropRelay.getMethod("getConfig", new Class[]{String.class, String.class}).invoke(vendorPropManagerObj, new Object[]{key, currentConfigValue});
            if (ret == null || !(ret instanceof String)) {
                return configValue;
            }
            return (String) ret;
        } catch (NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Slog.e(TAG, "Failed to invoke getConfig()");
            e.printStackTrace();
            vendorPropJarAvail = false;
            return configValue;
        } catch (Exception e2) {
            Slog.e(TAG, "Error calling getConfig Method on vendorpropextension jar");
            e2.printStackTrace();
            vendorPropJarAvail = false;
            return configValue;
        }
    }
}
