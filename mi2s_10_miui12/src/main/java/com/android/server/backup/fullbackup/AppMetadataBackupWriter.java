package com.android.server.backup.fullbackup;

import android.app.backup.FullBackup;
import android.app.backup.FullBackupDataOutput;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;
import android.os.Environment;
import android.server.am.SplitScreenReporter;
import android.util.Log;
import android.util.StringBuilderPrinter;
import com.android.internal.util.Preconditions;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.wm.ActivityTaskManagerService;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppMetadataBackupWriter {
    private final FullBackupDataOutput mOutput;
    private final PackageManager mPackageManager;

    public AppMetadataBackupWriter(FullBackupDataOutput output, PackageManager packageManager) {
        this.mOutput = output;
        this.mPackageManager = packageManager;
    }

    public void backupManifest(PackageInfo packageInfo, File manifestFile, File filesDir, boolean withApk) throws IOException {
        backupManifest(packageInfo, manifestFile, filesDir, (String) null, (String) null, withApk);
    }

    public void backupManifest(PackageInfo packageInfo, File manifestFile, File filesDir, String domain, String linkDomain, boolean withApk) throws IOException {
        File file = manifestFile;
        byte[] manifestBytes = getManifestBytes(packageInfo, withApk);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(manifestBytes);
        outputStream.close();
        file.setLastModified(0);
        FullBackup.backupToTar(packageInfo.packageName, domain, linkDomain, filesDir.getAbsolutePath(), manifestFile.getAbsolutePath(), this.mOutput);
    }

    private byte[] getManifestBytes(PackageInfo packageInfo, boolean withApk) {
        String packageName = packageInfo.packageName;
        StringBuilder builder = new StringBuilder(4096);
        StringBuilderPrinter printer = new StringBuilderPrinter(builder);
        printer.println(Integer.toString(1));
        printer.println(packageName);
        printer.println(Long.toString(packageInfo.getLongVersionCode()));
        printer.println(Integer.toString(Build.VERSION.SDK_INT));
        String installerName = this.mPackageManager.getInstallerPackageName(packageName);
        printer.println(installerName != null ? installerName : "");
        printer.println(withApk ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0");
        SigningInfo signingInfo = packageInfo.signingInfo;
        if (signingInfo == null) {
            printer.println("0");
        } else {
            Signature[] signatures = signingInfo.getApkContentsSigners();
            printer.println(Integer.toString(signatures.length));
            for (Signature sig : signatures) {
                printer.println(sig.toCharsString());
            }
        }
        return builder.toString().getBytes();
    }

    public void backupWidget(PackageInfo packageInfo, File metadataFile, File filesDir, byte[] widgetData) throws IOException {
        Preconditions.checkArgument(widgetData.length > 0, "Can't backup widget with no data.");
        String packageName = packageInfo.packageName;
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(metadataFile));
        DataOutputStream dataOutputStream = new DataOutputStream(bufferedOutputStream);
        bufferedOutputStream.write(getMetadataBytes(packageName));
        writeWidgetData(dataOutputStream, widgetData);
        bufferedOutputStream.flush();
        dataOutputStream.close();
        metadataFile.setLastModified(0);
        FullBackup.backupToTar(packageName, (String) null, (String) null, filesDir.getAbsolutePath(), metadataFile.getAbsolutePath(), this.mOutput);
    }

    private byte[] getMetadataBytes(String packageName) {
        StringBuilder builder = new StringBuilder(512);
        StringBuilderPrinter printer = new StringBuilderPrinter(builder);
        printer.println(Integer.toString(1));
        printer.println(packageName);
        return builder.toString().getBytes();
    }

    private void writeWidgetData(DataOutputStream out, byte[] widgetData) throws IOException {
        out.writeInt(UserBackupManagerService.BACKUP_WIDGET_METADATA_TOKEN);
        out.writeInt(widgetData.length);
        out.write(widgetData);
    }

    public void backupApk(PackageInfo packageInfo) {
        String appSourceDir = packageInfo.applicationInfo.getBaseCodePath();
        FullBackup.backupToTar(packageInfo.packageName, ActivityTaskManagerService.DUMP_ACTIVITIES_SHORT_CMD, (String) null, new File(appSourceDir).getParent(), appSourceDir, this.mOutput);
    }

    public void backupObb(int userId, PackageInfo packageInfo) {
        File obbDir = new Environment.UserEnvironment(userId).buildExternalStorageAppObbDirs(packageInfo.packageName)[0];
        if (obbDir != null) {
            Log.i(BackupManagerService.TAG, "obb dir: " + obbDir.getAbsolutePath());
            File[] obbFiles = obbDir.listFiles();
            if (obbFiles != null) {
                String obbDirName = obbDir.getAbsolutePath();
                for (File obb : obbFiles) {
                    FullBackup.backupToTar(packageInfo.packageName, "obb", (String) null, obbDirName, obb.getAbsolutePath(), this.mOutput);
                }
            }
        }
    }
}
