package com.miui.server.enterprise;

import android.content.Context;
import android.os.Binder;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import com.miui.enterprise.signature.EnterpriseCer;
import java.util.Calendar;
import java.util.Date;

public class ServiceUtils {
    private static volatile EnterpriseManagerService sEntService;

    public static void checkPermission(Context context) {
        if (UserHandle.getAppId(Binder.getCallingUid()) != 1000) {
            if (getEntService().isSignatureVerified()) {
                EnterpriseCer cert = getEntService().getEnterpriseCert();
                Date date = Calendar.getInstance().getTime();
                if (date.before(cert.getValidFrom()) || date.after(cert.getValidTo())) {
                    throw new SecurityException("Enterprise cert out of date");
                }
                if (!(cert.permissions == null || cert.permissions.length == 0)) {
                    String currentMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
                    boolean match = false;
                    String[] strArr = cert.permissions;
                    int length = strArr.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        } else if (TextUtils.equals(currentMethod, strArr[i])) {
                            match = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (!match) {
                        throw new SecurityException("Permission denied for " + currentMethod);
                    }
                }
                Binder.clearCallingIdentity();
                return;
            }
            throw new SecurityException("No enterprise cert");
        }
    }

    private static synchronized EnterpriseManagerService getEntService() {
        EnterpriseManagerService enterpriseManagerService;
        synchronized (ServiceUtils.class) {
            if (sEntService == null) {
                sEntService = (EnterpriseManagerService) ServiceManager.getService("EnterpriseManager");
            }
            enterpriseManagerService = sEntService;
        }
        return enterpriseManagerService;
    }
}
