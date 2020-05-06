package de.greenrobot.event;

import java.lang.reflect.Method;

final class SubscriberMethod {
    final Class<?> eventType;
    final Method method;
    String methodString;
    final ThreadMode threadMode;

    SubscriberMethod(Method method2, ThreadMode threadMode2, Class<?> eventType2) {
        this.method = method2;
        this.threadMode = threadMode2;
        this.eventType = eventType2;
    }

    public boolean equals(Object other) {
        if (!(other instanceof SubscriberMethod)) {
            return false;
        }
        checkMethodString();
        SubscriberMethod otherSubscriberMethod = (SubscriberMethod) other;
        otherSubscriberMethod.checkMethodString();
        return this.methodString.equals(otherSubscriberMethod.methodString);
    }

    private synchronized void checkMethodString() {
        if (this.methodString == null) {
            StringBuilder builder = new StringBuilder(64);
            builder.append(this.method.getDeclaringClass().getName());
            builder.append('#');
            builder.append(this.method.getName());
            builder.append('(');
            builder.append(this.eventType.getName());
            this.methodString = builder.toString();
        }
    }

    public int hashCode() {
        return this.method.hashCode();
    }
}
