package com.miui.securitycenter.cloudbackup;

import com.xiaomi.settingsdk.backup.CloudBackupServiceBase;
import com.xiaomi.settingsdk.backup.ICloudBackup;

public class SettingsCloudBackupService extends CloudBackupServiceBase {
    /* access modifiers changed from: protected */
    public ICloudBackup getBackupImpl() {
        return new k();
    }
}
