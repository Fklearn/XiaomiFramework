package miui.cloud.backup;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import miui.accounts.ExtraAccountManager;
import miui.app.backup.FullBackupAgent;
import miui.cloud.backup.data.DataPackage;
import miui.cloud.backup.data.SettingItem;
import miui.util.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsBackupHelper {
    private static final String KEY_DATA = "data";
    private static final String KEY_VERSION = "version";
    private static final String TAG = "SettingsBackup";

    private SettingsBackupHelper() {
    }

    private static boolean isSettingsBackupEnabled(Account account) {
        if (account == null) {
            return false;
        }
        return ContentResolver.getSyncAutomatically(account, SettingsBackupConsts.SETTINGS_BACKUP_AUTHORITY);
    }

    @Deprecated
    public static void requestBackupSettings(Context context) {
        Account account = ExtraAccountManager.getXiaomiAccount(context);
        if (isSettingsBackupEnabled(account)) {
            requestSettingsBackupManualSync(account, (String) null);
        }
    }

    private static void requestSettingsBackupManualSync(Account account, String packageName) {
        requestManualSync(account, SettingsBackupConsts.SETTINGS_BACKUP_AUTHORITY, packageName);
    }

    private static void requestManualSync(Account account, String authority, String packageName) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean("expedited", true);
        if (packageName != null) {
            settingsBundle.putString(SettingsBackupConsts.EXTRA_PACKAGE_NAME, packageName);
        }
        ContentResolver.requestSync(account, authority, settingsBundle);
    }

    @Deprecated
    public static void backupSettings(Context context, ParcelFileDescriptor data, ICloudBackup settingsCloudBackup) throws IOException {
        DataPackage dataPackage = new DataPackage();
        settingsCloudBackup.onBackupSettings(context, dataPackage);
        JSONObject settingsJson = new JSONObject();
        JSONArray settingsArray = new JSONArray();
        Collection<SettingItem<?>> settingItems = dataPackage.getDataItems().values();
        FileOutputStream output = null;
        if (settingItems != null) {
            try {
                for (SettingItem<?> settingItem : settingItems) {
                    settingsArray.put(settingItem.toJson());
                }
                settingsJson.put(SettingsBackupConsts.EXTRA_PACKAGE_NAME, context.getPackageName());
                settingsJson.put("version", settingsCloudBackup.getCurrentVersion(context));
                settingsJson.put("data", settingsArray);
            } catch (IOException e) {
                Log.e("SettingsBackup", "IOException in backupSettings", e);
            } catch (JSONException e2) {
                Log.e("SettingsBackup", "JSONException in backupSettings", e2);
            } catch (Throwable th) {
                IOUtils.closeQuietly((OutputStream) null);
                throw th;
            }
        }
        output = new FileOutputStream(data.getFileDescriptor());
        output.write(settingsJson.toString().getBytes("utf-8"));
        output.flush();
        output.close();
        IOUtils.closeQuietly(output);
    }

    public static void backupSettings(Context context, ParcelFileDescriptor data, ICloudBackup settingsCloudBackup, FullBackupAgent agent) throws IOException {
        DataPackage dataPackage = new DataPackage();
        settingsCloudBackup.onBackupSettings(context, dataPackage);
        JSONObject settingsJson = new JSONObject();
        JSONArray settingsArray = new JSONArray();
        Collection<SettingItem<?>> settingItems = dataPackage.getDataItems().values();
        FileOutputStream output = null;
        if (settingItems != null) {
            try {
                for (SettingItem<?> settingItem : settingItems) {
                    settingsArray.put(settingItem.toJson());
                }
                settingsJson.put(SettingsBackupConsts.EXTRA_PACKAGE_NAME, context.getPackageName());
                settingsJson.put("version", settingsCloudBackup.getCurrentVersion(context));
                settingsJson.put("data", settingsArray);
            } catch (IOException e) {
                Log.e("SettingsBackup", "IOException in backupSettings", e);
            } catch (JSONException e2) {
                Log.e("SettingsBackup", "JSONException in backupSettings", e2);
            } catch (Throwable th) {
                IOUtils.closeQuietly((OutputStream) null);
                throw th;
            }
        }
        output = new FileOutputStream(data.getFileDescriptor());
        output.write(settingsJson.toString().getBytes("utf-8"));
        output.flush();
        output.close();
        IOUtils.closeQuietly(output);
        for (String path : dataPackage.getFileItems().keySet()) {
            agent.addAttachedFile(path);
        }
    }

    public static void restoreSettings(Context context, ParcelFileDescriptor data, ICloudBackup settingsCloudBackup) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(data.getFileDescriptor()));
            StringBuilder stringBuilder = new StringBuilder();
            String ls = System.getProperty("line.separator");
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            JSONObject settingsJson = new JSONObject(stringBuilder.toString());
            if (settingsJson.length() > 0) {
                int backupVersion = settingsJson.optInt("version");
                JSONArray settingsArray = settingsJson.optJSONArray("data");
                DataPackage dataPackage = new DataPackage();
                if (settingsArray != null) {
                    for (int i = 0; i < settingsArray.length(); i++) {
                        JSONObject itemJson = settingsArray.optJSONObject(i);
                        if (itemJson != null) {
                            SettingItem<?> settingItem = SettingItem.fromJson(itemJson);
                            dataPackage.addAbstractDataItem(settingItem.key, settingItem);
                        }
                    }
                }
                settingsCloudBackup.onRestoreSettings(context, dataPackage, backupVersion);
            }
        } catch (IOException e) {
            Log.e("SettingsBackup", "IOException in restoreSettings", e);
        } catch (JSONException e2) {
            Log.e("SettingsBackup", "JSONException in restoreSettings", e2);
        } catch (Throwable th) {
            IOUtils.closeQuietly((Reader) null);
            throw th;
        }
        IOUtils.closeQuietly(reader);
    }

    public static void restoreOneFile(String path, ParcelFileDescriptor data) {
        InputStream fileInputStream = null;
        OutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(data.getFileDescriptor());
            new File(path.substring(0, path.lastIndexOf(File.separator))).mkdirs();
            fileOutputStream = new FileOutputStream(new File(path));
            byte[] buffer = new byte[1024];
            while (true) {
                int read = fileInputStream.read(buffer);
                int length = read;
                if (read <= 0) {
                    break;
                }
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            Log.e("SettingsBackup", "FileNotFoundException in restoreFiles: " + path, e);
        } catch (IOException e2) {
            Log.e("SettingsBackup", "IOException in restoreFiles: " + path, e2);
        } catch (Throwable th) {
            IOUtils.closeQuietly((InputStream) null);
            IOUtils.closeQuietly((OutputStream) null);
            throw th;
        }
        IOUtils.closeQuietly(fileInputStream);
        IOUtils.closeQuietly(fileOutputStream);
    }

    public static void restoreFiles(DataPackage dataPackage) {
        for (Map.Entry<String, ParcelFileDescriptor> entry : dataPackage.getFileItems().entrySet()) {
            restoreOneFile(entry.getKey(), entry.getValue());
        }
    }
}
