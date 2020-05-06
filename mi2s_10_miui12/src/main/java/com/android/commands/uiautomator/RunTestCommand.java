package com.android.commands.uiautomator;

import android.os.Bundle;
import android.util.Log;
import com.android.commands.uiautomator.Launcher;
import com.android.uiautomator.testrunner.UiAutomatorTestRunner;
import dalvik.system.DexFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RunTestCommand extends Launcher.Command {
    private static final int ARG_FAIL_INCOMPLETE_C = -2;
    private static final int ARG_FAIL_INCOMPLETE_E = -1;
    private static final int ARG_FAIL_NO_CLASS = -3;
    private static final int ARG_FAIL_RUNNER = -4;
    private static final int ARG_FAIL_UNSUPPORTED = -99;
    private static final int ARG_OK = 0;
    private static final String CLASS_PARAM = "class";
    private static final String CLASS_SEPARATOR = ",";
    private static final String DEBUG_PARAM = "debug";
    private static final String JARS_PARAM = "jars";
    private static final String JARS_SEPARATOR = ":";
    private static final String LOGTAG = RunTestCommand.class.getSimpleName();
    private static final String OUTPUT_FORMAT_KEY = "outputFormat";
    private static final String OUTPUT_SIMPLE = "simple";
    private static final String RUNNER_PARAM = "runner";
    private boolean mDebug;
    private boolean mMonkey = false;
    private final Bundle mParams = new Bundle();
    private UiAutomatorTestRunner mRunner;
    private String mRunnerClassName;
    private final List<String> mTestClasses = new ArrayList();

    public RunTestCommand() {
        super("runtest");
    }

    public void run(String[] args) {
        int ret = parseArgs(args);
        if (ret == ARG_FAIL_UNSUPPORTED) {
            System.err.println("Unsupported standalone parameter.");
            System.exit(ARG_FAIL_UNSUPPORTED);
        } else if (ret == ARG_FAIL_INCOMPLETE_C) {
            System.err.println("Incomplete '-c' parameter.");
            System.exit(ARG_FAIL_INCOMPLETE_C);
        } else if (ret == ARG_FAIL_INCOMPLETE_E) {
            System.err.println("Incomplete '-e' parameter.");
            System.exit(ARG_FAIL_INCOMPLETE_E);
        }
        if (this.mTestClasses.isEmpty()) {
            addTestClassesFromJars();
            if (this.mTestClasses.isEmpty()) {
                System.err.println("No test classes found.");
                System.exit(ARG_FAIL_NO_CLASS);
            }
        }
        getRunner().run(this.mTestClasses, this.mParams, this.mDebug, this.mMonkey);
    }

    private int parseArgs(String[] args) {
        int i = ARG_OK;
        while (true) {
            boolean z = false;
            if (i >= args.length) {
                return ARG_OK;
            }
            if (args[i].equals("-e")) {
                if (i + 2 >= args.length) {
                    return ARG_FAIL_INCOMPLETE_E;
                }
                int i2 = i + 1;
                String key = args[i2];
                i = i2 + 1;
                String value = args[i];
                if (CLASS_PARAM.equals(key)) {
                    addTestClasses(value);
                } else if (DEBUG_PARAM.equals(key)) {
                    if ("true".equals(value) || "1".equals(value)) {
                        z = true;
                    }
                    this.mDebug = z;
                } else if (RUNNER_PARAM.equals(key)) {
                    this.mRunnerClassName = value;
                } else {
                    this.mParams.putString(key, value);
                }
            } else if (args[i].equals("-c")) {
                if (i + 1 >= args.length) {
                    return ARG_FAIL_INCOMPLETE_C;
                }
                i++;
                addTestClasses(args[i]);
            } else if (args[i].equals("--monkey")) {
                this.mMonkey = true;
            } else if (!args[i].equals("-s")) {
                return ARG_FAIL_UNSUPPORTED;
            } else {
                this.mParams.putString(OUTPUT_FORMAT_KEY, OUTPUT_SIMPLE);
            }
            i++;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: com.android.uiautomator.testrunner.UiAutomatorTestRunner} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: com.android.uiautomator.testrunner.UiAutomatorTestRunner} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: com.android.uiautomator.testrunner.UiAutomatorTestRunner} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.uiautomator.testrunner.UiAutomatorTestRunner getRunner() {
        /*
            r6 = this;
            com.android.uiautomator.testrunner.UiAutomatorTestRunner r0 = r6.mRunner
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            java.lang.String r0 = r6.mRunnerClassName
            if (r0 != 0) goto L_0x0013
            com.android.uiautomator.testrunner.UiAutomatorTestRunner r0 = new com.android.uiautomator.testrunner.UiAutomatorTestRunner
            r0.<init>()
            r6.mRunner = r0
            com.android.uiautomator.testrunner.UiAutomatorTestRunner r0 = r6.mRunner
            return r0
        L_0x0013:
            r1 = 0
            r2 = -4
            java.lang.Class r0 = java.lang.Class.forName(r0)     // Catch:{ ClassNotFoundException -> 0x005e, InstantiationException -> 0x0041, IllegalAccessException -> 0x001f }
            java.lang.Object r3 = r0.newInstance()     // Catch:{ ClassNotFoundException -> 0x005e, InstantiationException -> 0x0041, IllegalAccessException -> 0x001f }
            r1 = r3
        L_0x001e:
            goto L_0x007b
        L_0x001f:
            r0 = move-exception
            java.io.PrintStream r3 = java.lang.System.err
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Constructor of runner "
            r4.append(r5)
            java.lang.String r5 = r6.mRunnerClassName
            r4.append(r5)
            java.lang.String r5 = " is not accessibile"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.println(r4)
            java.lang.System.exit(r2)
            goto L_0x007b
        L_0x0041:
            r0 = move-exception
            java.io.PrintStream r3 = java.lang.System.err
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Cannot instantiate runner: "
            r4.append(r5)
            java.lang.String r5 = r6.mRunnerClassName
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.println(r4)
            java.lang.System.exit(r2)
            goto L_0x001e
        L_0x005e:
            r0 = move-exception
            java.io.PrintStream r3 = java.lang.System.err
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Cannot find runner: "
            r4.append(r5)
            java.lang.String r5 = r6.mRunnerClassName
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.println(r4)
            java.lang.System.exit(r2)
            goto L_0x001e
        L_0x007b:
            r0 = r1
            com.android.uiautomator.testrunner.UiAutomatorTestRunner r0 = (com.android.uiautomator.testrunner.UiAutomatorTestRunner) r0     // Catch:{ ClassCastException -> 0x0081 }
            r6.mRunner = r0     // Catch:{ ClassCastException -> 0x0081 }
            return r0
        L_0x0081:
            r0 = move-exception
            java.io.PrintStream r3 = java.lang.System.err
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Specified runner is not subclass of "
            r4.append(r5)
            java.lang.Class<com.android.uiautomator.testrunner.UiAutomatorTestRunner> r5 = com.android.uiautomator.testrunner.UiAutomatorTestRunner.class
            java.lang.String r5 = r5.getSimpleName()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.println(r4)
            java.lang.System.exit(r2)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.commands.uiautomator.RunTestCommand.getRunner():com.android.uiautomator.testrunner.UiAutomatorTestRunner");
    }

    private void addTestClasses(String classes) {
        String[] classArray = classes.split(CLASS_SEPARATOR);
        int length = classArray.length;
        for (int i = ARG_OK; i < length; i++) {
            this.mTestClasses.add(classArray[i]);
        }
    }

    private void addTestClassesFromJars() {
        String jars = this.mParams.getString(JARS_PARAM);
        if (jars != null) {
            String[] jarFileNames = jars.split(JARS_SEPARATOR);
            int length = jarFileNames.length;
            for (int i = ARG_OK; i < length; i++) {
                String fileName = jarFileNames[i].trim();
                if (!fileName.isEmpty()) {
                    try {
                        DexFile dexFile = new DexFile(fileName);
                        Enumeration<String> e = dexFile.entries();
                        while (e.hasMoreElements()) {
                            String className = e.nextElement();
                            if (isTestClass(className)) {
                                this.mTestClasses.add(className);
                            }
                        }
                        dexFile.close();
                    } catch (IOException e2) {
                        String str = LOGTAG;
                        Object[] objArr = new Object[2];
                        objArr[ARG_OK] = fileName;
                        objArr[1] = e2.getMessage();
                        Log.w(str, String.format("Could not read %s: %s", objArr));
                    }
                }
            }
        }
    }

    private boolean isTestClass(String className) {
        try {
            Class<?> clazz = getClass().getClassLoader().loadClass(className);
            if (clazz.getEnclosingClass() != null) {
                return false;
            }
            return getRunner().getTestCaseFilter().accept(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public String detailedOptions() {
        return "    runtest <class spec> [options]\n    <class spec>: <JARS> < -c <CLASSES> | -e class <CLASSES> >\n      <JARS>: a list of jar files containing test classes and dependencies. If\n        the path is relative, it's assumed to be under /data/local/tmp. Use\n        absolute path if the file is elsewhere. Multiple files can be\n        specified, separated by space.\n      <CLASSES>: a list of test class names to run, separated by comma. To\n        a single method, use TestClass#testMethod format. The -e or -c option\n        may be repeated. This option is not required and if not provided then\n        all the tests in provided jars will be run automatically.\n    options:\n      --nohup: trap SIG_HUP, so test won't terminate even if parent process\n               is terminated, e.g. USB is disconnected.\n      -e debug [true|false]: wait for debugger to connect before starting.\n      -e runner [CLASS]: use specified test runner class instead. If\n        unspecified, framework default runner will be used.\n      -e <NAME> <VALUE>: other name-value pairs to be passed to test classes.\n        May be repeated.\n      -e outputFormat simple | -s: enabled less verbose JUnit style output.\n";
    }

    public String shortHelp() {
        return "executes UI automation tests";
    }
}
