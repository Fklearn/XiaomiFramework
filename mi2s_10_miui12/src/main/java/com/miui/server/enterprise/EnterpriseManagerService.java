package com.miui.server.enterprise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.miui.Manifest;
import android.os.IBinder;
import android.os.ServiceManager;
import android.util.Slog;
import com.miui.enterprise.IEnterpriseManager;
import com.miui.enterprise.signature.EnterpriseCer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EnterpriseManagerService extends IEnterpriseManager.Stub {
    private static final String TAG = "Enterprise";
    private APNManagerService mAPNManagerService;
    private ApplicationManagerService mApplicationManagerService;
    /* access modifiers changed from: private */
    public EnterpriseCer mCert;
    private Context mContext;
    private int mCurrentUserId = 0;
    private DeviceManagerService mDeviceManagerService;
    private BroadcastReceiver mLifecycleReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                String action = intent.getAction();
                char c = 65535;
                switch (action.hashCode()) {
                    case -2061058799:
                        if (action.equals("android.intent.action.USER_REMOVED")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -755112654:
                        if (action.equals("android.intent.action.USER_STARTED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 66199991:
                        if (action.equals("com.miui.enterprise.ACTION_CERT_UPDATE")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 798292259:
                        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 959232034:
                        if (action.equals("android.intent.action.USER_SWITCHED")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                if (c == 0) {
                    EnterpriseManagerService.this.bootComplete();
                } else if (c == 1) {
                    EnterpriseManagerService.this.onUserStarted(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if (c == 2) {
                    EnterpriseManagerService.this.onUserSwitched(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if (c == 3) {
                    EnterpriseManagerService.this.onUserRemoved(intent.getIntExtra("android.intent.extra.user_handle", 0));
                } else if (c == 4) {
                    EnterpriseCer unused = EnterpriseManagerService.this.mCert = intent.getParcelableExtra("extra_ent_cert");
                }
            }
        }
    };
    private PhoneManagerService mPhoneManagerService;
    private RestrictionsManagerService mRestrictionsManagerService;
    private Map<String, IBinder> mServices = new HashMap();

    /* JADX WARNING: type inference failed for: r0v1, types: [com.miui.server.enterprise.EnterpriseManagerService, android.os.IBinder] */
    public static void init(Context context) {
        Slog.i(TAG, "Init enterprise service");
        ServiceManager.addService("EnterpriseManager", new EnterpriseManagerService(context));
    }

    private EnterpriseManagerService(Context context) {
        this.mContext = context;
        this.mAPNManagerService = new APNManagerService(context);
        this.mApplicationManagerService = new ApplicationManagerService(context);
        this.mDeviceManagerService = new DeviceManagerService(context);
        this.mPhoneManagerService = new PhoneManagerService(context);
        this.mRestrictionsManagerService = new RestrictionsManagerService(context);
        this.mServices.put("apn_manager", this.mAPNManagerService);
        this.mServices.put("application_manager", this.mApplicationManagerService);
        this.mServices.put("device_manager", this.mDeviceManagerService);
        this.mServices.put("phone_manager", this.mPhoneManagerService);
        this.mServices.put("restrictions_manager", this.mRestrictionsManagerService);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BOOT_COMPLETED");
        intentFilter.addAction("android.intent.action.USER_STARTED");
        intentFilter.addAction("android.intent.action.USER_SWITCHED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("com.miui.enterprise.ACTION_CERT_UPDATE");
        context.registerReceiver(this.mLifecycleReceiver, intentFilter);
    }

    /* access modifiers changed from: private */
    public void bootComplete() {
        this.mApplicationManagerService.bootComplete();
        this.mRestrictionsManagerService.bootComplete();
        new Thread(new Runnable() {
            public void run() {
                EnterpriseManagerService.this.loadEnterpriseCert();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void onUserStarted(int userId) {
    }

    /* access modifiers changed from: private */
    public void onUserSwitched(int userId) {
        this.mCurrentUserId = userId;
    }

    /* access modifiers changed from: private */
    public void onUserRemoved(int userId) {
    }

    /* access modifiers changed from: private */
    public void loadEnterpriseCert() {
        File certFile = new File("/data/system/entcert");
        if (certFile.exists()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(certFile);
                this.mCert = new EnterpriseCer(inputStream);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Slog.e(TAG, "Something wrong", e);
                }
            } catch (IOException e2) {
                Slog.e(TAG, "Something wrong", e2);
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e3) {
                        Slog.e(TAG, "Something wrong", e3);
                    }
                }
                throw th;
            }
        }
    }

    public boolean isSignatureVerified() {
        return this.mCert != null;
    }

    public EnterpriseCer getEnterpriseCert() {
        return this.mCert;
    }

    public IBinder getService(String serviceName) {
        checkEnterprisePermission();
        return this.mServices.get(serviceName);
    }

    private void checkEnterprisePermission() {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.ACCESS_ENTERPRISE_API, "Permission denied to access enterprise API");
    }
}
