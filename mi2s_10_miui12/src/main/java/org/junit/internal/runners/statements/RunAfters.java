package org.junit.internal.runners.statements;

import java.util.List;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class RunAfters extends Statement {
    private final List<FrameworkMethod> afters;
    private final Statement next;
    private final Object target;

    public RunAfters(Statement next2, List<FrameworkMethod> afters2, Object target2) {
        this.next = next2;
        this.afters = afters2;
        this.target = target2;
    }

    /*  JADX ERROR: StackOverflow in pass: MarkFinallyVisitor
        jadx.core.utils.exceptions.JadxOverflowException: 
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    public void evaluate() throws java.lang.Throwable {
        /*
            r7 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
            org.junit.runners.model.Statement r2 = r7.next     // Catch:{ all -> 0x002b }
            r2.evaluate()     // Catch:{ all -> 0x002b }
            java.util.List<org.junit.runners.model.FrameworkMethod> r2 = r7.afters
            java.util.Iterator r2 = r2.iterator()
        L_0x0011:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x002a
            java.lang.Object r3 = r2.next()
            org.junit.runners.model.FrameworkMethod r3 = (org.junit.runners.model.FrameworkMethod) r3
            java.lang.Object r4 = r7.target     // Catch:{ all -> 0x0025 }
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ all -> 0x0025 }
            r3.invokeExplosively(r4, r5)     // Catch:{ all -> 0x0025 }
            goto L_0x0029
        L_0x0025:
            r4 = move-exception
            r0.add(r4)
        L_0x0029:
            goto L_0x0011
        L_0x002a:
            goto L_0x004f
        L_0x002b:
            r2 = move-exception
            r0.add(r2)     // Catch:{ all -> 0x0053 }
            java.util.List<org.junit.runners.model.FrameworkMethod> r2 = r7.afters
            java.util.Iterator r2 = r2.iterator()
        L_0x0036:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x002a
            java.lang.Object r3 = r2.next()
            org.junit.runners.model.FrameworkMethod r3 = (org.junit.runners.model.FrameworkMethod) r3
            java.lang.Object r4 = r7.target     // Catch:{ all -> 0x004a }
            java.lang.Object[] r5 = new java.lang.Object[r1]     // Catch:{ all -> 0x004a }
            r3.invokeExplosively(r4, r5)     // Catch:{ all -> 0x004a }
            goto L_0x004e
        L_0x004a:
            r4 = move-exception
            r0.add(r4)
        L_0x004e:
            goto L_0x0036
        L_0x004f:
            org.junit.runners.model.MultipleFailureException.assertEmpty(r0)
            return
        L_0x0053:
            r2 = move-exception
            java.util.List<org.junit.runners.model.FrameworkMethod> r3 = r7.afters
            java.util.Iterator r3 = r3.iterator()
        L_0x005a:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0073
            java.lang.Object r4 = r3.next()
            org.junit.runners.model.FrameworkMethod r4 = (org.junit.runners.model.FrameworkMethod) r4
            java.lang.Object r5 = r7.target     // Catch:{ all -> 0x006e }
            java.lang.Object[] r6 = new java.lang.Object[r1]     // Catch:{ all -> 0x006e }
            r4.invokeExplosively(r5, r6)     // Catch:{ all -> 0x006e }
            goto L_0x0072
        L_0x006e:
            r5 = move-exception
            r0.add(r5)
        L_0x0072:
            goto L_0x005a
        L_0x0073:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.junit.internal.runners.statements.RunAfters.evaluate():void");
    }
}
