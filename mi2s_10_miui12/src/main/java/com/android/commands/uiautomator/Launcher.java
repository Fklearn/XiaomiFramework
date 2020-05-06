package com.android.commands.uiautomator;

public class Launcher {
    /* access modifiers changed from: private */
    public static Command[] COMMANDS = {HELP_COMMAND, new RunTestCommand(), new DumpCommand(), new EventsCommand()};
    private static Command HELP_COMMAND = new Command("help") {
        public void run(String[] args) {
            System.err.println("Usage: uiautomator <subcommand> [options]\n");
            System.err.println("Available subcommands:\n");
            for (Command command : Launcher.COMMANDS) {
                String shortHelp = command.shortHelp();
                String detailedOptions = command.detailedOptions();
                if (shortHelp == null) {
                    shortHelp = "";
                }
                if (detailedOptions == null) {
                    detailedOptions = "";
                }
                System.err.println(String.format("%s: %s", new Object[]{command.name(), shortHelp}));
                System.err.println(detailedOptions);
            }
        }

        public String detailedOptions() {
            return null;
        }

        public String shortHelp() {
            return "displays help message";
        }
    };

    public static abstract class Command {
        private String mName;

        public abstract String detailedOptions();

        public abstract void run(String[] strArr);

        public abstract String shortHelp();

        public Command(String name) {
            this.mName = name;
        }

        public String name() {
            return this.mName;
        }
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [java.lang.Object[]] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void main(java.lang.String[] r4) {
        /*
            java.lang.String r0 = "uiautomator"
            android.os.Process.setArgV0(r0)
            int r0 = r4.length
            r1 = 1
            if (r0 < r1) goto L_0x0023
            r0 = 0
            r2 = r4[r0]
            com.android.commands.uiautomator.Launcher$Command r2 = findCommand(r2)
            if (r2 == 0) goto L_0x0023
            java.lang.String[] r0 = new java.lang.String[r0]
            int r3 = r4.length
            if (r3 <= r1) goto L_0x001f
            int r3 = r4.length
            java.lang.Object[] r1 = java.util.Arrays.copyOfRange(r4, r1, r3)
            r0 = r1
            java.lang.String[] r0 = (java.lang.String[]) r0
        L_0x001f:
            r2.run(r0)
            return
        L_0x0023:
            com.android.commands.uiautomator.Launcher$Command r0 = HELP_COMMAND
            r0.run(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.commands.uiautomator.Launcher.main(java.lang.String[]):void");
    }

    private static Command findCommand(String name) {
        for (Command command : COMMANDS) {
            if (command.name().equals(name)) {
                return command;
            }
        }
        return null;
    }
}
