package com.android.server.coverage;

import android.os.Binder;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import org.jacoco.agent.rt.RT;

public class CoverageService extends Binder {
    public static final String COVERAGE_SERVICE = "coverage";
    public static final boolean ENABLED;

    static {
        boolean shouldEnable = true;
        try {
            Class.forName("org.jacoco.agent.rt.RT");
        } catch (ClassNotFoundException e) {
            shouldEnable = false;
        }
        ENABLED = shouldEnable;
    }

    public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
        new CoverageCommand().exec(this, in, out, err, args, callback, resultReceiver);
    }

    private static class CoverageCommand extends ShellCommand {
        private CoverageCommand() {
        }

        public int onCommand(String cmd) {
            if ("dump".equals(cmd)) {
                return onDump();
            }
            if ("reset".equals(cmd)) {
                return onReset();
            }
            return handleDefaultCommands(cmd);
        }

        public void onHelp() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("Coverage commands:");
            pw.println("  help");
            pw.println("    Print this help text.");
            pw.println("  dump [FILE]");
            pw.println("    Dump code coverage to FILE.");
            pw.println("  reset");
            pw.println("    Reset coverage information.");
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x005c, code lost:
            r5 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
            r3.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0065, code lost:
            throw r5;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int onDump() {
            /*
                r8 = this;
                java.lang.String r0 = r8.getNextArg()
                if (r0 != 0) goto L_0x0009
                java.lang.String r0 = "/data/local/tmp/coverage.ec"
                goto L_0x001f
            L_0x0009:
                java.io.File r1 = new java.io.File
                r1.<init>(r0)
                boolean r2 = r1.isDirectory()
                if (r2 == 0) goto L_0x001f
                java.io.File r2 = new java.io.File
                java.lang.String r3 = "coverage.ec"
                r2.<init>(r1, r3)
                java.lang.String r0 = r2.getAbsolutePath()
            L_0x001f:
                java.lang.String r1 = "w"
                android.os.ParcelFileDescriptor r1 = r8.openFileForSystem(r0, r1)
                r2 = -1
                if (r1 != 0) goto L_0x002a
                return r2
            L_0x002a:
                java.io.BufferedOutputStream r3 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x0066 }
                android.os.ParcelFileDescriptor$AutoCloseOutputStream r4 = new android.os.ParcelFileDescriptor$AutoCloseOutputStream     // Catch:{ IOException -> 0x0066 }
                r4.<init>(r1)     // Catch:{ IOException -> 0x0066 }
                r3.<init>(r4)     // Catch:{ IOException -> 0x0066 }
                org.jacoco.agent.rt.IAgent r4 = org.jacoco.agent.rt.RT.getAgent()     // Catch:{ all -> 0x005a }
                r5 = 0
                byte[] r4 = r4.getExecutionData(r5)     // Catch:{ all -> 0x005a }
                r3.write(r4)     // Catch:{ all -> 0x005a }
                r3.flush()     // Catch:{ all -> 0x005a }
                java.io.PrintWriter r4 = r8.getOutPrintWriter()     // Catch:{ all -> 0x005a }
                java.lang.String r6 = "Dumped coverage data to %s"
                r7 = 1
                java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x005a }
                r7[r5] = r0     // Catch:{ all -> 0x005a }
                java.lang.String r6 = java.lang.String.format(r6, r7)     // Catch:{ all -> 0x005a }
                r4.println(r6)     // Catch:{ all -> 0x005a }
                r3.close()     // Catch:{ IOException -> 0x0066 }
                return r5
            L_0x005a:
                r4 = move-exception
                throw r4     // Catch:{ all -> 0x005c }
            L_0x005c:
                r5 = move-exception
                r3.close()     // Catch:{ all -> 0x0061 }
                goto L_0x0065
            L_0x0061:
                r6 = move-exception
                r4.addSuppressed(r6)     // Catch:{ IOException -> 0x0066 }
            L_0x0065:
                throw r5     // Catch:{ IOException -> 0x0066 }
            L_0x0066:
                r3 = move-exception
                java.io.PrintWriter r4 = r8.getErrPrintWriter()
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "Failed to dump coverage data: "
                r5.append(r6)
                java.lang.String r6 = r3.getMessage()
                r5.append(r6)
                java.lang.String r5 = r5.toString()
                r4.println(r5)
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.coverage.CoverageService.CoverageCommand.onDump():int");
        }

        private int onReset() {
            RT.getAgent().reset();
            getOutPrintWriter().println("Reset coverage data");
            return 0;
        }
    }
}
