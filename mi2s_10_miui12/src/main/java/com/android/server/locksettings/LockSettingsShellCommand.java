package com.android.server.locksettings;

import android.os.ShellCommand;
import android.server.am.SplitScreenReporter;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.pm.DumpState;
import java.io.PrintWriter;

class LockSettingsShellCommand extends ShellCommand {
    private static final String COMMAND_CLEAR = "clear";
    private static final String COMMAND_GET_DISABLED = "get-disabled";
    private static final String COMMAND_HELP = "help";
    private static final String COMMAND_SET_DISABLED = "set-disabled";
    private static final String COMMAND_SET_PASSWORD = "set-password";
    private static final String COMMAND_SET_PATTERN = "set-pattern";
    private static final String COMMAND_SET_PIN = "set-pin";
    private static final String COMMAND_SP = "sp";
    private static final String COMMAND_VERIFY = "verify";
    private int mCurrentUserId;
    private final LockPatternUtils mLockPatternUtils;
    private String mNew = "";
    private String mOld = "";

    LockSettingsShellCommand(LockPatternUtils lockPatternUtils) {
        this.mLockPatternUtils = lockPatternUtils;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onCommand(java.lang.String r10) {
        /*
            r9 = this;
            if (r10 != 0) goto L_0x0007
            int r0 = r9.handleDefaultCommands(r10)
            return r0
        L_0x0007:
            r0 = -1
            android.app.IActivityManager r1 = android.app.ActivityManager.getService()     // Catch:{ Exception -> 0x0112 }
            android.content.pm.UserInfo r1 = r1.getCurrentUser()     // Catch:{ Exception -> 0x0112 }
            int r1 = r1.id     // Catch:{ Exception -> 0x0112 }
            r9.mCurrentUserId = r1     // Catch:{ Exception -> 0x0112 }
            r9.parseArgs()     // Catch:{ Exception -> 0x0112 }
            com.android.internal.widget.LockPatternUtils r1 = r9.mLockPatternUtils     // Catch:{ Exception -> 0x0112 }
            boolean r1 = r1.hasSecureLockScreen()     // Catch:{ Exception -> 0x0112 }
            java.lang.String r2 = "set-disabled"
            java.lang.String r3 = "help"
            java.lang.String r4 = "get-disabled"
            r5 = 0
            r6 = 2
            r7 = 1
            if (r1 != 0) goto L_0x0067
            int r1 = r10.hashCode()     // Catch:{ Exception -> 0x0112 }
            r8 = -1473704173(0xffffffffa8290f13, float:-9.384653E-15)
            if (r1 == r8) goto L_0x004e
            r8 = 3198785(0x30cf41, float:4.482453E-39)
            if (r1 == r8) goto L_0x0046
            r8 = 75288455(0x47ccf87, float:2.971775E-36)
            if (r1 == r8) goto L_0x003e
        L_0x003d:
            goto L_0x0056
        L_0x003e:
            boolean r1 = r10.equals(r2)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x003d
            r1 = r6
            goto L_0x0057
        L_0x0046:
            boolean r1 = r10.equals(r3)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x003d
            r1 = r5
            goto L_0x0057
        L_0x004e:
            boolean r1 = r10.equals(r4)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x003d
            r1 = r7
            goto L_0x0057
        L_0x0056:
            r1 = r0
        L_0x0057:
            if (r1 == 0) goto L_0x0067
            if (r1 == r7) goto L_0x0067
            if (r1 == r6) goto L_0x0067
            java.io.PrintWriter r1 = r9.getErrPrintWriter()     // Catch:{ Exception -> 0x0112 }
            java.lang.String r2 = "The device does not support lock screen - ignoring the command."
            r1.println(r2)     // Catch:{ Exception -> 0x0112 }
            return r0
        L_0x0067:
            boolean r1 = r9.checkCredential()     // Catch:{ Exception -> 0x0112 }
            if (r1 != 0) goto L_0x006e
            return r0
        L_0x006e:
            int r1 = r10.hashCode()     // Catch:{ Exception -> 0x0112 }
            switch(r1) {
                case -2044327643: goto L_0x00c5;
                case -1473704173: goto L_0x00bd;
                case -819951495: goto L_0x00b2;
                case 3677: goto L_0x00a7;
                case 3198785: goto L_0x009e;
                case 75288455: goto L_0x0096;
                case 94746189: goto L_0x008c;
                case 1021333414: goto L_0x0081;
                case 1983832490: goto L_0x0076;
                default: goto L_0x0075;
            }     // Catch:{ Exception -> 0x0112 }
        L_0x0075:
            goto L_0x00d0
        L_0x0076:
            java.lang.String r1 = "set-pin"
            boolean r1 = r10.equals(r1)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = r6
            goto L_0x00d1
        L_0x0081:
            java.lang.String r1 = "set-password"
            boolean r1 = r10.equals(r1)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = r7
            goto L_0x00d1
        L_0x008c:
            java.lang.String r1 = "clear"
            boolean r1 = r10.equals(r1)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = 3
            goto L_0x00d1
        L_0x0096:
            boolean r1 = r10.equals(r2)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = 5
            goto L_0x00d1
        L_0x009e:
            boolean r1 = r10.equals(r3)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = 8
            goto L_0x00d1
        L_0x00a7:
            java.lang.String r1 = "sp"
            boolean r1 = r10.equals(r1)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = 4
            goto L_0x00d1
        L_0x00b2:
            java.lang.String r1 = "verify"
            boolean r1 = r10.equals(r1)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = 6
            goto L_0x00d1
        L_0x00bd:
            boolean r1 = r10.equals(r4)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = 7
            goto L_0x00d1
        L_0x00c5:
            java.lang.String r1 = "set-pattern"
            boolean r1 = r10.equals(r1)     // Catch:{ Exception -> 0x0112 }
            if (r1 == 0) goto L_0x0075
            r1 = r5
            goto L_0x00d1
        L_0x00d0:
            r1 = r0
        L_0x00d1:
            switch(r1) {
                case 0: goto L_0x00f9;
                case 1: goto L_0x00f5;
                case 2: goto L_0x00f1;
                case 3: goto L_0x00ed;
                case 4: goto L_0x00e9;
                case 5: goto L_0x00e5;
                case 6: goto L_0x00e1;
                case 7: goto L_0x00dd;
                case 8: goto L_0x00d9;
                default: goto L_0x00d4;
            }     // Catch:{ Exception -> 0x0112 }
        L_0x00d4:
            java.io.PrintWriter r1 = r9.getErrPrintWriter()     // Catch:{ Exception -> 0x0112 }
            goto L_0x00fd
        L_0x00d9:
            r9.onHelp()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00dd:
            r9.runGetDisabled()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00e1:
            r9.runVerify()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00e5:
            r9.runSetDisabled()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00e9:
            r9.runChangeSp()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00ed:
            r9.runClear()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00f1:
            r9.runSetPin()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00f5:
            r9.runSetPassword()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00f9:
            r9.runSetPattern()     // Catch:{ Exception -> 0x0112 }
            goto L_0x0111
        L_0x00fd:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0112 }
            r2.<init>()     // Catch:{ Exception -> 0x0112 }
            java.lang.String r3 = "Unknown command: "
            r2.append(r3)     // Catch:{ Exception -> 0x0112 }
            r2.append(r10)     // Catch:{ Exception -> 0x0112 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0112 }
            r1.println(r2)     // Catch:{ Exception -> 0x0112 }
        L_0x0111:
            return r5
        L_0x0112:
            r1 = move-exception
            java.io.PrintWriter r2 = r9.getErrPrintWriter()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Error while executing command: "
            r3.append(r4)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            r2.println(r3)
            java.io.PrintWriter r2 = r9.getErrPrintWriter()
            r1.printStackTrace(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsShellCommand.onCommand(java.lang.String):int");
    }

    private void runVerify() {
        getOutPrintWriter().println("Lock credential verified successfully");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x00a6, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x00a7, code lost:
        r0.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x00aa, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x009f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x00a0, code lost:
        if (r1 != null) goto L_0x00a2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onHelp() {
        /*
            r4 = this;
            java.lang.String r0 = ""
            java.io.PrintWriter r1 = r4.getOutPrintWriter()
            java.lang.String r2 = "lockSettings service commands:"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "NOTE: when lock screen is set, all commands require the --old <CREDENTIAL> argument."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  help"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Prints this help text."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  get-disabled [--old <CREDENTIAL>] [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Checks whether lock screen is disabled."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  set-disabled [--old <CREDENTIAL>] [--user USER_ID] <true|false>"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    When true, disables lock screen."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  set-pattern [--old <CREDENTIAL>] [--user USER_ID] <PATTERN>"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Sets the lock screen as pattern, using the given PATTERN to unlock."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  set-pin [--old <CREDENTIAL>] [--user USER_ID] <PIN>"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Sets the lock screen as PIN, using the given PIN to unlock."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  set-pin [--old <CREDENTIAL>] [--user USER_ID] <PASSWORD>"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Sets the lock screen as password, using the given PASSOWRD to unlock."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  sp [--old <CREDENTIAL>] [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Gets whether synthetic password is enabled."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  sp [--old <CREDENTIAL>] [--user USER_ID] <1|0>"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Enables / disables synthetic password."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  clear [--old <CREDENTIAL>] [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Clears the lock credentials."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "  verify [--old <CREDENTIAL>] [--user USER_ID]"
            r1.println(r2)     // Catch:{ all -> 0x009d }
            java.lang.String r2 = "    Verifies the lock credentials."
            r1.println(r2)     // Catch:{ all -> 0x009d }
            r1.println(r0)     // Catch:{ all -> 0x009d }
            r1.close()
            return
        L_0x009d:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x009f }
        L_0x009f:
            r2 = move-exception
            if (r1 == 0) goto L_0x00aa
            r1.close()     // Catch:{ all -> 0x00a6 }
            goto L_0x00aa
        L_0x00a6:
            r3 = move-exception
            r0.addSuppressed(r3)
        L_0x00aa:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.locksettings.LockSettingsShellCommand.onHelp():void");
    }

    private void parseArgs() {
        while (true) {
            String nextOption = getNextOption();
            String opt = nextOption;
            if (nextOption == null) {
                this.mNew = getNextArg();
                return;
            } else if ("--old".equals(opt)) {
                this.mOld = getNextArgRequired();
            } else if ("--user".equals(opt)) {
                this.mCurrentUserId = Integer.parseInt(getNextArgRequired());
            } else {
                PrintWriter errPrintWriter = getErrPrintWriter();
                errPrintWriter.println("Unknown option: " + opt);
                throw new IllegalArgumentException();
            }
        }
    }

    private void runChangeSp() {
        String str = this.mNew;
        if (str != null) {
            if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(str)) {
                this.mLockPatternUtils.enableSyntheticPassword();
                getOutPrintWriter().println("Synthetic password enabled");
            } else if ("0".equals(this.mNew)) {
                this.mLockPatternUtils.disableSyntheticPassword();
                getOutPrintWriter().println("Synthetic password disabled");
            }
        }
        getOutPrintWriter().println(String.format("SP Enabled = %b", new Object[]{Boolean.valueOf(this.mLockPatternUtils.isSyntheticPasswordEnabled())}));
    }

    private void runSetPattern() {
        String str = this.mOld;
        this.mLockPatternUtils.saveLockPattern(LockPatternUtils.stringToPattern(this.mNew), str != null ? str.getBytes() : null, this.mCurrentUserId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Pattern set to '" + this.mNew + "'");
    }

    private void runSetPassword() {
        String str = this.mNew;
        byte[] oldBytes = null;
        byte[] newBytes = str != null ? str.getBytes() : null;
        String str2 = this.mOld;
        if (str2 != null) {
            oldBytes = str2.getBytes();
        }
        this.mLockPatternUtils.saveLockPassword(newBytes, oldBytes, DumpState.DUMP_DOMAIN_PREFERRED, this.mCurrentUserId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Password set to '" + this.mNew + "'");
    }

    private void runSetPin() {
        String str = this.mNew;
        byte[] oldBytes = null;
        byte[] newBytes = str != null ? str.getBytes() : null;
        String str2 = this.mOld;
        if (str2 != null) {
            oldBytes = str2.getBytes();
        }
        this.mLockPatternUtils.saveLockPassword(newBytes, oldBytes, 131072, this.mCurrentUserId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Pin set to '" + this.mNew + "'");
    }

    private void runClear() {
        String str = this.mOld;
        this.mLockPatternUtils.clearLock(str != null ? str.getBytes() : null, this.mCurrentUserId);
        getOutPrintWriter().println("Lock credential cleared");
    }

    private void runSetDisabled() {
        boolean disabled = Boolean.parseBoolean(this.mNew);
        this.mLockPatternUtils.setLockScreenDisabled(disabled, this.mCurrentUserId);
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Lock screen disabled set to " + disabled);
    }

    private void runGetDisabled() {
        getOutPrintWriter().println(this.mLockPatternUtils.isLockScreenDisabled(this.mCurrentUserId));
    }

    private boolean checkCredential() {
        boolean result;
        boolean havePassword = this.mLockPatternUtils.isLockPasswordEnabled(this.mCurrentUserId);
        boolean havePattern = this.mLockPatternUtils.isLockPatternEnabled(this.mCurrentUserId);
        if (havePassword || havePattern) {
            if (this.mLockPatternUtils.isManagedProfileWithUnifiedChallenge(this.mCurrentUserId)) {
                getOutPrintWriter().println("Profile uses unified challenge");
                return false;
            }
            if (havePassword) {
                try {
                    result = this.mLockPatternUtils.checkPassword(this.mOld != null ? this.mOld.getBytes() : null, this.mCurrentUserId);
                } catch (LockPatternUtils.RequestThrottledException e) {
                    getOutPrintWriter().println("Request throttled");
                    return false;
                }
            } else {
                result = this.mLockPatternUtils.checkPattern(LockPatternUtils.stringToPattern(this.mOld), this.mCurrentUserId);
            }
            if (!result) {
                if (!this.mLockPatternUtils.isManagedProfileWithUnifiedChallenge(this.mCurrentUserId)) {
                    this.mLockPatternUtils.reportFailedPasswordAttempt(this.mCurrentUserId);
                }
                PrintWriter outPrintWriter = getOutPrintWriter();
                outPrintWriter.println("Old password '" + this.mOld + "' didn't match");
            } else {
                this.mLockPatternUtils.reportSuccessfulPasswordAttempt(this.mCurrentUserId);
            }
            return result;
        } else if (this.mOld.isEmpty()) {
            return true;
        } else {
            getOutPrintWriter().println("Old password provided but user has no password");
            return false;
        }
    }
}
