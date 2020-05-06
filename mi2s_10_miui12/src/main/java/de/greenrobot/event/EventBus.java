package de.greenrobot.event;

import android.os.Looper;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public class EventBus {
    private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();
    public static String TAG = "Event";
    static volatile EventBus defaultInstance;
    private static final Map<Class<?>, List<Class<?>>> eventTypesCache = new HashMap();
    private final AsyncPoster asyncPoster;
    private final BackgroundPoster backgroundPoster;
    private final ThreadLocal<PostingThreadState> currentPostingThreadState;
    private final boolean eventInheritance;
    private final ExecutorService executorService;
    private final boolean logNoSubscriberMessages;
    private final boolean logSubscriberExceptions;
    private final HandlerPoster mainThreadPoster;
    private final boolean sendNoSubscriberEvent;
    private final boolean sendSubscriberExceptionEvent;
    private final Map<Class<?>, Object> stickyEvents;
    private final SubscriberMethodFinder subscriberMethodFinder;
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    private final boolean throwSubscriberException;
    private final Map<Object, List<Class<?>>> typesBySubscriber;

    interface PostCallback {
        void onPostCompleted(List<SubscriberExceptionEvent> list);
    }

    public static EventBus getDefault() {
        if (defaultInstance == null) {
            synchronized (EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }
        return defaultInstance;
    }

    public static EventBusBuilder builder() {
        return new EventBusBuilder();
    }

    public static void clearCaches() {
        SubscriberMethodFinder.clearCaches();
        eventTypesCache.clear();
    }

    public EventBus() {
        this(DEFAULT_BUILDER);
    }

    EventBus(EventBusBuilder builder) {
        this.currentPostingThreadState = new ThreadLocal<PostingThreadState>() {
            /* access modifiers changed from: protected */
            public PostingThreadState initialValue() {
                return new PostingThreadState();
            }
        };
        this.subscriptionsByEventType = new HashMap();
        this.typesBySubscriber = new HashMap();
        this.stickyEvents = new ConcurrentHashMap();
        this.mainThreadPoster = new HandlerPoster(this, Looper.getMainLooper(), 10);
        this.backgroundPoster = new BackgroundPoster(this);
        this.asyncPoster = new AsyncPoster(this);
        this.subscriberMethodFinder = new SubscriberMethodFinder(builder.skipMethodVerificationForClasses);
        this.logSubscriberExceptions = builder.logSubscriberExceptions;
        this.logNoSubscriberMessages = builder.logNoSubscriberMessages;
        this.sendSubscriberExceptionEvent = builder.sendSubscriberExceptionEvent;
        this.sendNoSubscriberEvent = builder.sendNoSubscriberEvent;
        this.throwSubscriberException = builder.throwSubscriberException;
        this.eventInheritance = builder.eventInheritance;
        this.executorService = builder.executorService;
    }

    public void register(Object subscriber) {
        register(subscriber, false, 0);
    }

    public void register(Object subscriber, int priority) {
        register(subscriber, false, priority);
    }

    public void registerSticky(Object subscriber) {
        register(subscriber, true, 0);
    }

    public void registerSticky(Object subscriber, int priority) {
        register(subscriber, true, priority);
    }

    private synchronized void register(Object subscriber, boolean sticky, int priority) {
        for (SubscriberMethod subscriberMethod : this.subscriberMethodFinder.findSubscriberMethods(subscriber.getClass())) {
            subscribe(subscriber, subscriberMethod, sticky, priority);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0064, code lost:
        if (r6 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x006e, code lost:
        if (android.os.Looper.getMainLooper() != android.os.Looper.myLooper()) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0070, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0072, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0073, code lost:
        postToSubscription(r2, r6, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void subscribe(java.lang.Object r9, de.greenrobot.event.SubscriberMethod r10, boolean r11, int r12) {
        /*
            r8 = this;
            java.lang.Class<?> r0 = r10.eventType
            java.util.Map<java.lang.Class<?>, java.util.concurrent.CopyOnWriteArrayList<de.greenrobot.event.Subscription>> r1 = r8.subscriptionsByEventType
            java.lang.Object r1 = r1.get(r0)
            java.util.concurrent.CopyOnWriteArrayList r1 = (java.util.concurrent.CopyOnWriteArrayList) r1
            de.greenrobot.event.Subscription r2 = new de.greenrobot.event.Subscription
            r2.<init>(r9, r10, r12)
            if (r1 != 0) goto L_0x001d
            java.util.concurrent.CopyOnWriteArrayList r3 = new java.util.concurrent.CopyOnWriteArrayList
            r3.<init>()
            r1 = r3
            java.util.Map<java.lang.Class<?>, java.util.concurrent.CopyOnWriteArrayList<de.greenrobot.event.Subscription>> r3 = r8.subscriptionsByEventType
            r3.put(r0, r1)
            goto L_0x0023
        L_0x001d:
            boolean r3 = r1.contains(r2)
            if (r3 != 0) goto L_0x007d
        L_0x0023:
            int r3 = r1.size()
            r4 = 0
        L_0x0028:
            if (r4 > r3) goto L_0x003f
            if (r4 == r3) goto L_0x003c
            int r5 = r2.priority
            java.lang.Object r6 = r1.get(r4)
            de.greenrobot.event.Subscription r6 = (de.greenrobot.event.Subscription) r6
            int r6 = r6.priority
            if (r5 <= r6) goto L_0x0039
            goto L_0x003c
        L_0x0039:
            int r4 = r4 + 1
            goto L_0x0028
        L_0x003c:
            r1.add(r4, r2)
        L_0x003f:
            java.util.Map<java.lang.Object, java.util.List<java.lang.Class<?>>> r4 = r8.typesBySubscriber
            java.lang.Object r4 = r4.get(r9)
            java.util.List r4 = (java.util.List) r4
            if (r4 != 0) goto L_0x0054
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r4 = r5
            java.util.Map<java.lang.Object, java.util.List<java.lang.Class<?>>> r5 = r8.typesBySubscriber
            r5.put(r9, r4)
        L_0x0054:
            r4.add(r0)
            if (r11 == 0) goto L_0x007c
            java.util.Map<java.lang.Class<?>, java.lang.Object> r5 = r8.stickyEvents
            monitor-enter(r5)
            r6 = 0
            java.util.Map<java.lang.Class<?>, java.lang.Object> r7 = r8.stickyEvents     // Catch:{ all -> 0x0077 }
            java.lang.Object r6 = r7.get(r0)     // Catch:{ all -> 0x0077 }
            monitor-exit(r5)     // Catch:{ all -> 0x007a }
            if (r6 == 0) goto L_0x007c
            android.os.Looper r5 = android.os.Looper.getMainLooper()
            android.os.Looper r7 = android.os.Looper.myLooper()
            if (r5 != r7) goto L_0x0072
            r5 = 1
            goto L_0x0073
        L_0x0072:
            r5 = 0
        L_0x0073:
            r8.postToSubscription(r2, r6, r5)
            goto L_0x007c
        L_0x0077:
            r7 = move-exception
        L_0x0078:
            monitor-exit(r5)     // Catch:{ all -> 0x007a }
            throw r7
        L_0x007a:
            r7 = move-exception
            goto L_0x0078
        L_0x007c:
            return
        L_0x007d:
            de.greenrobot.event.EventBusException r3 = new de.greenrobot.event.EventBusException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Subscriber "
            r4.append(r5)
            java.lang.Class r5 = r9.getClass()
            r4.append(r5)
            java.lang.String r5 = " already registered to event "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            r3.<init>((java.lang.String) r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.subscribe(java.lang.Object, de.greenrobot.event.SubscriberMethod, boolean, int):void");
    }

    public synchronized boolean isRegistered(Object subscriber) {
        return this.typesBySubscriber.containsKey(subscriber);
    }

    private void unubscribeByEventType(Object subscriber, Class<?> eventType) {
        List<Subscription> subscriptions = this.subscriptionsByEventType.get(eventType);
        if (subscriptions != null) {
            int size = subscriptions.size();
            int i = 0;
            while (i < size) {
                Subscription subscription = subscriptions.get(i);
                if (subscription.subscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    i--;
                    size--;
                }
                i++;
            }
        }
    }

    public synchronized void unregister(Object subscriber) {
        List<Class<?>> subscribedTypes = this.typesBySubscriber.get(subscriber);
        if (subscribedTypes != null) {
            for (Class<?> eventType : subscribedTypes) {
                unubscribeByEventType(subscriber, eventType);
            }
            this.typesBySubscriber.remove(subscriber);
        } else {
            String str = TAG;
            Log.w(str, "Subscriber to unregister was not registered before: " + subscriber.getClass());
        }
    }

    public void post(Object event) {
        PostingThreadState postingState = this.currentPostingThreadState.get();
        List<Object> eventQueue = postingState.eventQueue;
        eventQueue.add(event);
        if (!postingState.isPosting) {
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if (!postingState.canceled) {
                while (!eventQueue.isEmpty()) {
                    try {
                        postSingleEvent(eventQueue.remove(0), postingState);
                    } finally {
                        postingState.isPosting = false;
                        postingState.isMainThread = false;
                    }
                }
                return;
            }
            throw new EventBusException("Internal error. Abort state was not reset");
        }
    }

    public void cancelEventDelivery(Object event) {
        PostingThreadState postingState = this.currentPostingThreadState.get();
        if (!postingState.isPosting) {
            throw new EventBusException("This method may only be called from inside event handling methods on the posting thread");
        } else if (event == null) {
            throw new EventBusException("Event may not be null");
        } else if (postingState.event != event) {
            throw new EventBusException("Only the currently handled event may be aborted");
        } else if (postingState.subscription.subscriberMethod.threadMode == ThreadMode.PostThread) {
            postingState.canceled = true;
        } else {
            throw new EventBusException(" event handlers may only abort the incoming event");
        }
    }

    public void postSticky(Object event) {
        synchronized (this.stickyEvents) {
            this.stickyEvents.put(event.getClass(), event);
        }
        post(event);
    }

    public <T> T getStickyEvent(Class<T> eventType) {
        T cast;
        synchronized (this.stickyEvents) {
            cast = eventType.cast(this.stickyEvents.get(eventType));
        }
        return cast;
    }

    public <T> T removeStickyEvent(Class<T> eventType) {
        T cast;
        synchronized (this.stickyEvents) {
            cast = eventType.cast(this.stickyEvents.remove(eventType));
        }
        return cast;
    }

    public boolean removeStickyEvent(Object event) {
        synchronized (this.stickyEvents) {
            Class<?> eventType = event.getClass();
            if (!event.equals(this.stickyEvents.get(eventType))) {
                return false;
            }
            this.stickyEvents.remove(eventType);
            return true;
        }
    }

    public void removeAllStickyEvents() {
        synchronized (this.stickyEvents) {
            this.stickyEvents.clear();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001f, code lost:
        if (r3 == null) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0025, code lost:
        if (r3.isEmpty() != false) goto L_0x0029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0027, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0029, code lost:
        r2 = r2 + 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasSubscriberForEvent(java.lang.Class<?> r7) {
        /*
            r6 = this;
            java.util.List r0 = r6.lookupAllEventTypes(r7)
            if (r0 == 0) goto L_0x0033
            int r1 = r0.size()
            r2 = 0
            r3 = 0
        L_0x000c:
            if (r2 >= r1) goto L_0x0033
            java.lang.Object r4 = r0.get(r2)
            java.lang.Class r4 = (java.lang.Class) r4
            monitor-enter(r6)
            java.util.Map<java.lang.Class<?>, java.util.concurrent.CopyOnWriteArrayList<de.greenrobot.event.Subscription>> r5 = r6.subscriptionsByEventType     // Catch:{ all -> 0x002e }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x002e }
            java.util.concurrent.CopyOnWriteArrayList r5 = (java.util.concurrent.CopyOnWriteArrayList) r5     // Catch:{ all -> 0x002e }
            r3 = r5
            monitor-exit(r6)     // Catch:{ all -> 0x002c }
            if (r3 == 0) goto L_0x0029
            boolean r5 = r3.isEmpty()
            if (r5 != 0) goto L_0x0029
            r5 = 1
            return r5
        L_0x0029:
            int r2 = r2 + 1
            goto L_0x000c
        L_0x002c:
            r5 = move-exception
            goto L_0x002f
        L_0x002e:
            r5 = move-exception
        L_0x002f:
            monitor-exit(r6)     // Catch:{ all -> 0x0031 }
            throw r5
        L_0x0031:
            r5 = move-exception
            goto L_0x002f
        L_0x0033:
            r1 = 0
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.hasSubscriberForEvent(java.lang.Class):boolean");
    }

    private void postSingleEvent(Object event, PostingThreadState postingState) throws Error {
        Class<?> eventClass = event.getClass();
        boolean subscriptionFound = false;
        if (this.eventInheritance) {
            List<Class<?>> eventTypes = lookupAllEventTypes(eventClass);
            int countTypes = eventTypes.size();
            for (int h = 0; h < countTypes; h++) {
                subscriptionFound |= postSingleEventForEventType(event, postingState, eventTypes.get(h));
            }
        } else {
            subscriptionFound = postSingleEventForEventType(event, postingState, eventClass);
        }
        if (!subscriptionFound) {
            if (this.logNoSubscriberMessages) {
                String str = TAG;
                Log.d(str, "No subscribers registered for event " + eventClass);
            }
            if (this.sendNoSubscriberEvent && eventClass != NoSubscriberEvent.class && eventClass != SubscriberExceptionEvent.class) {
                post(new NoSubscriberEvent(this, event));
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
        r3 = r1.iterator();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001c, code lost:
        if (r3.hasNext() == false) goto L_0x0044;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001e, code lost:
        r4 = r3.next();
        r10.event = r9;
        r10.subscription = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
        postToSubscription(r4, r9, r10.isMainThread);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0030, code lost:
        r10.event = null;
        r10.subscription = null;
        r10.canceled = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0038, code lost:
        if (r10.canceled == false) goto L_0x0018;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003c, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        r10.event = null;
        r10.subscription = null;
        r10.canceled = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0043, code lost:
        throw r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0046, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x000c, code lost:
        if (r1 == null) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        if (r1.isEmpty() != false) goto L_0x0046;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean postSingleEventForEventType(java.lang.Object r9, de.greenrobot.event.EventBus.PostingThreadState r10, java.lang.Class<?> r11) {
        /*
            r8 = this;
            monitor-enter(r8)
            r0 = 0
            java.util.Map<java.lang.Class<?>, java.util.concurrent.CopyOnWriteArrayList<de.greenrobot.event.Subscription>> r1 = r8.subscriptionsByEventType     // Catch:{ all -> 0x004c }
            java.lang.Object r1 = r1.get(r11)     // Catch:{ all -> 0x004c }
            java.util.concurrent.CopyOnWriteArrayList r1 = (java.util.concurrent.CopyOnWriteArrayList) r1     // Catch:{ all -> 0x004c }
            monitor-exit(r8)     // Catch:{ all -> 0x0047 }
            r2 = 0
            if (r1 == 0) goto L_0x0046
            boolean r3 = r1.isEmpty()
            if (r3 != 0) goto L_0x0046
            java.util.Iterator r3 = r1.iterator()
        L_0x0018:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0044
            java.lang.Object r4 = r3.next()
            de.greenrobot.event.Subscription r4 = (de.greenrobot.event.Subscription) r4
            r10.event = r9
            r10.subscription = r4
            r5 = 0
            boolean r6 = r10.isMainThread     // Catch:{ all -> 0x003c }
            r8.postToSubscription(r4, r9, r6)     // Catch:{ all -> 0x003c }
            boolean r6 = r10.canceled     // Catch:{ all -> 0x003c }
            r5 = r6
            r10.event = r0
            r10.subscription = r0
            r10.canceled = r2
            if (r5 == 0) goto L_0x003b
            goto L_0x0044
        L_0x003b:
            goto L_0x0018
        L_0x003c:
            r6 = move-exception
            r10.event = r0
            r10.subscription = r0
            r10.canceled = r2
            throw r6
        L_0x0044:
            r0 = 1
            return r0
        L_0x0046:
            return r2
        L_0x0047:
            r0 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x004d
        L_0x004c:
            r1 = move-exception
        L_0x004d:
            monitor-exit(r8)     // Catch:{ all -> 0x004f }
            throw r1
        L_0x004f:
            r1 = move-exception
            goto L_0x004d
        */
        throw new UnsupportedOperationException("Method not decompiled: de.greenrobot.event.EventBus.postSingleEventForEventType(java.lang.Object, de.greenrobot.event.EventBus$PostingThreadState, java.lang.Class):boolean");
    }

    /* renamed from: de.greenrobot.event.EventBus$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$de$greenrobot$event$ThreadMode = new int[ThreadMode.values().length];

        static {
            try {
                $SwitchMap$de$greenrobot$event$ThreadMode[ThreadMode.PostThread.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$de$greenrobot$event$ThreadMode[ThreadMode.MainThread.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$de$greenrobot$event$ThreadMode[ThreadMode.BackgroundThread.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$de$greenrobot$event$ThreadMode[ThreadMode.Async.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
        int i = AnonymousClass2.$SwitchMap$de$greenrobot$event$ThreadMode[subscription.subscriberMethod.threadMode.ordinal()];
        if (i == 1) {
            invokeSubscriber(subscription, event);
        } else if (i != 2) {
            if (i != 3) {
                if (i == 4) {
                    this.asyncPoster.enqueue(subscription, event);
                    return;
                }
                throw new IllegalStateException("Unknown thread mode: " + subscription.subscriberMethod.threadMode);
            } else if (isMainThread) {
                this.backgroundPoster.enqueue(subscription, event);
            } else {
                invokeSubscriber(subscription, event);
            }
        } else if (isMainThread) {
            invokeSubscriber(subscription, event);
        } else {
            this.mainThreadPoster.enqueue(subscription, event);
        }
    }

    private List<Class<?>> lookupAllEventTypes(Class<?> eventClass) {
        List<Class<?>> eventTypes;
        synchronized (eventTypesCache) {
            eventTypes = eventTypesCache.get(eventClass);
            if (eventTypes == null) {
                eventTypes = new ArrayList<>();
                for (Class<? super Object> clazz = eventClass; clazz != null; clazz = clazz.getSuperclass()) {
                    eventTypes.add(clazz);
                    addInterfaces(eventTypes, clazz.getInterfaces());
                }
                eventTypesCache.put(eventClass, eventTypes);
            }
        }
        return eventTypes;
    }

    static void addInterfaces(List<Class<?>> eventTypes, Class<?>[] interfaces) {
        for (Class<?> interfaceClass : interfaces) {
            if (!eventTypes.contains(interfaceClass)) {
                eventTypes.add(interfaceClass);
                addInterfaces(eventTypes, interfaceClass.getInterfaces());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeSubscriber(PendingPost pendingPost) {
        Object event = pendingPost.event;
        Subscription subscription = pendingPost.subscription;
        PendingPost.releasePendingPost(pendingPost);
        if (subscription.active) {
            invokeSubscriber(subscription, event);
        }
    }

    /* access modifiers changed from: package-private */
    public void invokeSubscriber(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, new Object[]{event});
        } catch (InvocationTargetException e) {
            handleSubscriberException(subscription, event, e.getCause());
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException("Unexpected exception", e2);
        }
    }

    private void handleSubscriberException(Subscription subscription, Object event, Throwable cause) {
        if (event instanceof SubscriberExceptionEvent) {
            if (this.logSubscriberExceptions) {
                String str = TAG;
                Log.e(str, "SubscriberExceptionEvent subscriber " + subscription.subscriber.getClass() + " threw an exception", cause);
                SubscriberExceptionEvent exEvent = (SubscriberExceptionEvent) event;
                String str2 = TAG;
                Log.e(str2, "Initial event " + exEvent.causingEvent + " caused exception in " + exEvent.causingSubscriber, exEvent.throwable);
            }
        } else if (!this.throwSubscriberException) {
            if (this.logSubscriberExceptions) {
                String str3 = TAG;
                Log.e(str3, "Could not dispatch event: " + event.getClass() + " to subscribing class " + subscription.subscriber.getClass(), cause);
            }
            if (this.sendSubscriberExceptionEvent) {
                post(new SubscriberExceptionEvent(this, cause, event, subscription.subscriber));
            }
        } else {
            throw new EventBusException("Invoking subscriber failed", cause);
        }
    }

    static final class PostingThreadState {
        boolean canceled;
        Object event;
        final List<Object> eventQueue = new ArrayList();
        boolean isMainThread;
        boolean isPosting;
        Subscription subscription;

        PostingThreadState() {
        }
    }

    /* access modifiers changed from: package-private */
    public ExecutorService getExecutorService() {
        return this.executorService;
    }
}
