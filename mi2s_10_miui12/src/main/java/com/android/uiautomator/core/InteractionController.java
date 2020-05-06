package com.android.uiautomator.core;

import android.app.UiAutomation;
import android.graphics.Point;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

class InteractionController {
    private static final boolean DEBUG = Log.isLoggable(LOG_TAG, 3);
    private static final String LOG_TAG = InteractionController.class.getSimpleName();
    private static final int MOTION_EVENT_INJECTION_DELAY_MILLIS = 5;
    private static final long REGULAR_CLICK_LENGTH = 100;
    private long mDownTime;
    private final KeyCharacterMap mKeyCharacterMap = KeyCharacterMap.load(-1);
    private final UiAutomatorBridge mUiAutomatorBridge;

    public InteractionController(UiAutomatorBridge bridge) {
        this.mUiAutomatorBridge = bridge;
    }

    class WaitForAnyEventPredicate implements UiAutomation.AccessibilityEventFilter {
        int mMask;

        WaitForAnyEventPredicate(int mask) {
            this.mMask = mask;
        }

        public boolean accept(AccessibilityEvent t) {
            if ((t.getEventType() & this.mMask) != 0) {
                return true;
            }
            return InteractionController.DEBUG;
        }
    }

    class EventCollectingPredicate implements UiAutomation.AccessibilityEventFilter {
        List<AccessibilityEvent> mEventsList;
        int mMask;

        EventCollectingPredicate(int mask, List<AccessibilityEvent> events) {
            this.mMask = mask;
            this.mEventsList = events;
        }

        public boolean accept(AccessibilityEvent t) {
            if ((t.getEventType() & this.mMask) == 0) {
                return InteractionController.DEBUG;
            }
            this.mEventsList.add(AccessibilityEvent.obtain(t));
            return InteractionController.DEBUG;
        }
    }

    class WaitForAllEventPredicate implements UiAutomation.AccessibilityEventFilter {
        int mMask;

        WaitForAllEventPredicate(int mask) {
            this.mMask = mask;
        }

        public boolean accept(AccessibilityEvent t) {
            int eventType = t.getEventType();
            int i = this.mMask;
            if ((eventType & i) == 0) {
                return InteractionController.DEBUG;
            }
            this.mMask = (~t.getEventType()) & i;
            if (this.mMask != 0) {
                return InteractionController.DEBUG;
            }
            return true;
        }
    }

    private AccessibilityEvent runAndWaitForEvents(Runnable command, UiAutomation.AccessibilityEventFilter filter, long timeout) {
        try {
            return this.mUiAutomatorBridge.executeCommandAndWaitForAccessibilityEvent(command, filter, timeout);
        } catch (TimeoutException e) {
            Log.w(LOG_TAG, "runAndwaitForEvent timedout waiting for events");
            return null;
        } catch (Exception e2) {
            Log.e(LOG_TAG, "exception from executeCommandAndWaitForAccessibilityEvent", e2);
            return null;
        }
    }

    public boolean sendKeyAndWaitForEvent(final int keyCode, final int metaState, int eventType, long timeout) {
        if (runAndWaitForEvents(new Runnable() {
            public void run() {
                long eventTime = SystemClock.uptimeMillis();
                KeyEvent downEvent = new KeyEvent(eventTime, eventTime, 0, keyCode, 0, metaState, -1, 0, 0, 257);
                if (InteractionController.this.injectEventSync(downEvent)) {
                    KeyEvent keyEvent = downEvent;
                    boolean unused = InteractionController.this.injectEventSync(new KeyEvent(eventTime, eventTime, 1, keyCode, 0, metaState, -1, 0, 0, 257));
                    return;
                }
            }
        }, new WaitForAnyEventPredicate(eventType), timeout) != null) {
            return true;
        }
        return DEBUG;
    }

    public boolean clickNoSync(int x, int y) {
        String str = LOG_TAG;
        Log.d(str, "clickNoSync (" + x + ", " + y + ")");
        if (!touchDown(x, y)) {
            return DEBUG;
        }
        SystemClock.sleep(REGULAR_CLICK_LENGTH);
        if (touchUp(x, y)) {
            return true;
        }
        return DEBUG;
    }

    public boolean clickAndSync(int x, int y, long timeout) {
        Log.d(LOG_TAG, String.format("clickAndSync(%d, %d)", new Object[]{Integer.valueOf(x), Integer.valueOf(y)}));
        if (runAndWaitForEvents(clickRunnable(x, y), new WaitForAnyEventPredicate(2052), timeout) != null) {
            return true;
        }
        return DEBUG;
    }

    public boolean clickAndWaitForNewWindow(int x, int y, long timeout) {
        Log.d(LOG_TAG, String.format("clickAndWaitForNewWindow(%d, %d)", new Object[]{Integer.valueOf(x), Integer.valueOf(y)}));
        if (runAndWaitForEvents(clickRunnable(x, y), new WaitForAllEventPredicate(2080), timeout) != null) {
            return true;
        }
        return DEBUG;
    }

    private Runnable clickRunnable(final int x, final int y) {
        return new Runnable() {
            public void run() {
                if (InteractionController.this.touchDown(x, y)) {
                    SystemClock.sleep(InteractionController.REGULAR_CLICK_LENGTH);
                    boolean unused = InteractionController.this.touchUp(x, y);
                }
            }
        };
    }

    public boolean longTapNoSync(int x, int y) {
        if (DEBUG) {
            String str = LOG_TAG;
            Log.d(str, "longTapNoSync (" + x + ", " + y + ")");
        }
        if (!touchDown(x, y)) {
            return DEBUG;
        }
        SystemClock.sleep(this.mUiAutomatorBridge.getSystemLongPressTime());
        if (touchUp(x, y)) {
            return true;
        }
        return DEBUG;
    }

    /* access modifiers changed from: private */
    public boolean touchDown(int x, int y) {
        if (DEBUG) {
            String str = LOG_TAG;
            Log.d(str, "touchDown (" + x + ", " + y + ")");
        }
        this.mDownTime = SystemClock.uptimeMillis();
        long j = this.mDownTime;
        MotionEvent event = MotionEvent.obtain(j, j, 0, (float) x, (float) y, 1);
        event.setSource(4098);
        return injectEventSync(event);
    }

    /* access modifiers changed from: private */
    public boolean touchUp(int x, int y) {
        if (DEBUG) {
            String str = LOG_TAG;
            Log.d(str, "touchUp (" + x + ", " + y + ")");
        }
        MotionEvent event = MotionEvent.obtain(this.mDownTime, SystemClock.uptimeMillis(), 1, (float) x, (float) y, 1);
        event.setSource(4098);
        this.mDownTime = 0;
        return injectEventSync(event);
    }

    private boolean touchMove(int x, int y) {
        if (DEBUG) {
            String str = LOG_TAG;
            Log.d(str, "touchMove (" + x + ", " + y + ")");
        }
        MotionEvent event = MotionEvent.obtain(this.mDownTime, SystemClock.uptimeMillis(), 2, (float) x, (float) y, 1);
        event.setSource(4098);
        return injectEventSync(event);
    }

    public boolean scrollSwipe(int downX, int downY, int upX, int upY, int steps) {
        String str = LOG_TAG;
        Log.d(str, "scrollSwipe (" + downX + ", " + downY + ", " + upX + ", " + upY + ", " + steps + ")");
        final int i = downX;
        final int i2 = downY;
        final int i3 = upX;
        final int i4 = upY;
        final int i5 = steps;
        AnonymousClass3 r2 = new Runnable() {
            public void run() {
                InteractionController.this.swipe(i, i2, i3, i4, i5);
            }
        };
        ArrayList<AccessibilityEvent> events = new ArrayList<>();
        runAndWaitForEvents(r2, new EventCollectingPredicate(4096, events), Configurator.getInstance().getScrollAcknowledgmentTimeout());
        AccessibilityEvent event = getLastMatchingEvent(events, 4096);
        if (event == null) {
            recycleAccessibilityEvents(events);
            return DEBUG;
        }
        boolean foundEnd = DEBUG;
        if (event.getFromIndex() != -1 && event.getToIndex() != -1 && event.getItemCount() != -1) {
            foundEnd = event.getFromIndex() == 0 || event.getItemCount() - 1 == event.getToIndex();
            String str2 = LOG_TAG;
            Log.d(str2, "scrollSwipe reached scroll end: " + foundEnd);
        } else if (!(event.getScrollX() == -1 || event.getScrollY() == -1)) {
            if (downX == upX) {
                foundEnd = event.getScrollY() == 0 || event.getScrollY() == event.getMaxScrollY();
                String str3 = LOG_TAG;
                Log.d(str3, "Vertical scrollSwipe reached scroll end: " + foundEnd);
            } else if (downY == upY) {
                foundEnd = event.getScrollX() == 0 || event.getScrollX() == event.getMaxScrollX();
                String str4 = LOG_TAG;
                Log.d(str4, "Horizontal scrollSwipe reached scroll end: " + foundEnd);
            }
        }
        recycleAccessibilityEvents(events);
        if (!foundEnd) {
            return true;
        }
        return DEBUG;
    }

    private AccessibilityEvent getLastMatchingEvent(List<AccessibilityEvent> events, int type) {
        for (int x = events.size(); x > 0; x--) {
            AccessibilityEvent event = events.get(x - 1);
            if (event.getEventType() == type) {
                return event;
            }
        }
        return null;
    }

    private void recycleAccessibilityEvents(List<AccessibilityEvent> events) {
        for (AccessibilityEvent event : events) {
            event.recycle();
        }
        events.clear();
    }

    public boolean swipe(int downX, int downY, int upX, int upY, int steps) {
        return swipe(downX, downY, upX, upY, steps, DEBUG);
    }

    public boolean swipe(int downX, int downY, int upX, int upY, int steps, boolean drag) {
        int i = upX;
        int i2 = upY;
        int swipeSteps = steps;
        if (swipeSteps == 0) {
            swipeSteps = 1;
        }
        double xStep = ((double) (i - downX)) / ((double) swipeSteps);
        double yStep = ((double) (i2 - downY)) / ((double) swipeSteps);
        boolean ret = touchDown(downX, downY);
        if (drag) {
            SystemClock.sleep(this.mUiAutomatorBridge.getSystemLongPressTime());
        }
        for (int i3 = 1; i3 < swipeSteps; i3++) {
            ret &= touchMove(((int) (((double) i3) * xStep)) + downX, ((int) (((double) i3) * yStep)) + downY);
            if (!ret) {
                break;
            }
            SystemClock.sleep(5);
        }
        if (drag) {
            SystemClock.sleep(REGULAR_CLICK_LENGTH);
        }
        return ret & touchUp(i, i2);
    }

    public boolean swipe(Point[] segments, int segmentSteps) {
        int swipeSteps = segmentSteps;
        if (segmentSteps == 0) {
            segmentSteps = 1;
        }
        if (segments.length == 0) {
            return DEBUG;
        }
        boolean ret = touchDown(segments[0].x, segments[0].y);
        for (int seg = 0; seg < segments.length; seg++) {
            if (seg + 1 < segments.length) {
                double xStep = ((double) (segments[seg + 1].x - segments[seg].x)) / ((double) segmentSteps);
                double yStep = ((double) (segments[seg + 1].y - segments[seg].y)) / ((double) segmentSteps);
                int i = 1;
                while (true) {
                    if (i >= swipeSteps) {
                        double d = yStep;
                        double yStep2 = xStep;
                        break;
                    }
                    ret &= touchMove(segments[seg].x + ((int) (((double) i) * xStep)), segments[seg].y + ((int) (((double) i) * yStep)));
                    if (!ret) {
                        double d2 = yStep;
                        double yStep3 = xStep;
                        break;
                    }
                    SystemClock.sleep(5);
                    i++;
                }
            }
        }
        return ret & touchUp(segments[segments.length - 1].x, segments[segments.length - 1].y);
    }

    public boolean sendText(String text) {
        if (DEBUG) {
            Log.d(LOG_TAG, "sendText (" + text + ")");
        }
        KeyEvent[] events = this.mKeyCharacterMap.getEvents(text.toCharArray());
        if (events == null) {
            return true;
        }
        long keyDelay = Configurator.getInstance().getKeyInjectionDelay();
        for (KeyEvent event2 : events) {
            if (!injectEventSync(KeyEvent.changeTimeRepeat(event2, SystemClock.uptimeMillis(), 0))) {
                return DEBUG;
            }
            SystemClock.sleep(keyDelay);
        }
        return true;
    }

    public boolean sendKey(int keyCode, int metaState) {
        if (DEBUG) {
            String str = LOG_TAG;
            Log.d(str, "sendKey (" + keyCode + ", " + metaState + ")");
        } else {
            int i = keyCode;
            int i2 = metaState;
        }
        long eventTime = SystemClock.uptimeMillis();
        KeyEvent downEvent = new KeyEvent(eventTime, eventTime, 0, keyCode, 0, metaState, -1, 0, 0, 257);
        if (injectEventSync(downEvent)) {
            KeyEvent keyEvent = downEvent;
            if (injectEventSync(new KeyEvent(eventTime, eventTime, 1, keyCode, 0, metaState, -1, 0, 0, 257))) {
                return true;
            }
            return DEBUG;
        }
        return DEBUG;
    }

    public void setRotationRight() {
        this.mUiAutomatorBridge.setRotation(3);
    }

    public void setRotationLeft() {
        this.mUiAutomatorBridge.setRotation(1);
    }

    public void setRotationNatural() {
        this.mUiAutomatorBridge.setRotation(0);
    }

    public void freezeRotation() {
        this.mUiAutomatorBridge.setRotation(-1);
    }

    public void unfreezeRotation() {
        this.mUiAutomatorBridge.setRotation(-2);
    }

    public boolean wakeDevice() throws RemoteException {
        if (isScreenOn()) {
            return DEBUG;
        }
        sendKey(26, 0);
        return true;
    }

    public boolean sleepDevice() throws RemoteException {
        if (!isScreenOn()) {
            return DEBUG;
        }
        sendKey(26, 0);
        return true;
    }

    public boolean isScreenOn() throws RemoteException {
        return this.mUiAutomatorBridge.isScreenOn();
    }

    /* access modifiers changed from: private */
    public boolean injectEventSync(InputEvent event) {
        return this.mUiAutomatorBridge.injectInputEvent(event, true);
    }

    private int getPointerAction(int motionEnvent, int index) {
        return (index << 8) + motionEnvent;
    }

    public boolean performMultiPointerGesture(MotionEvent.PointerCoords[]... touches) {
        MotionEvent.PointerCoords[][] pointerCoordsArr = touches;
        if (pointerCoordsArr.length >= 2) {
            int maxSteps = 0;
            for (int x = 0; x < pointerCoordsArr.length; x++) {
                maxSteps = maxSteps < pointerCoordsArr[x].length ? pointerCoordsArr[x].length : maxSteps;
            }
            MotionEvent.PointerProperties[] properties = new MotionEvent.PointerProperties[pointerCoordsArr.length];
            MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[pointerCoordsArr.length];
            for (int x2 = 0; x2 < pointerCoordsArr.length; x2++) {
                MotionEvent.PointerProperties prop = new MotionEvent.PointerProperties();
                prop.id = x2;
                prop.toolType = 1;
                properties[x2] = prop;
                pointerCoords[x2] = pointerCoordsArr[x2][0];
            }
            long downTime = SystemClock.uptimeMillis();
            MotionEvent.PointerCoords[] pointerCoords2 = pointerCoords;
            MotionEvent event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 0, 1, properties, pointerCoords, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
            boolean ret = true & injectEventSync(event);
            MotionEvent motionEvent = event;
            int x3 = 1;
            while (x3 < pointerCoordsArr.length) {
                MotionEvent event2 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), getPointerAction(MOTION_EVENT_INJECTION_DELAY_MILLIS, x3), x3 + 1, properties, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
                ret &= injectEventSync(event2);
                x3++;
                MotionEvent motionEvent2 = event2;
            }
            int i = 1;
            while (i < maxSteps - 1) {
                for (int x4 = 0; x4 < pointerCoordsArr.length; x4++) {
                    if (pointerCoordsArr[x4].length > i) {
                        pointerCoords2[x4] = pointerCoordsArr[x4][i];
                    } else {
                        pointerCoords2[x4] = pointerCoordsArr[x4][pointerCoordsArr[x4].length - 1];
                    }
                }
                MotionEvent event3 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 2, pointerCoordsArr.length, properties, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
                ret &= injectEventSync(event3);
                SystemClock.sleep(5);
                i++;
                MotionEvent motionEvent3 = event3;
            }
            for (int x5 = 0; x5 < pointerCoordsArr.length; x5++) {
                pointerCoords2[x5] = pointerCoordsArr[x5][pointerCoordsArr[x5].length - 1];
            }
            int x6 = 1;
            while (x6 < pointerCoordsArr.length) {
                MotionEvent event4 = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), getPointerAction(6, x6), x6 + 1, properties, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0);
                ret &= injectEventSync(event4);
                x6++;
                MotionEvent motionEvent4 = event4;
            }
            Log.i(LOG_TAG, "x " + pointerCoords2[0].x);
            return ret & injectEventSync(MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), 1, 1, properties, pointerCoords2, 0, 0, 1.0f, 1.0f, 0, 0, 4098, 0));
        }
        throw new IllegalArgumentException("Must provide coordinates for at least 2 pointers");
    }

    public boolean toggleRecentApps() {
        return this.mUiAutomatorBridge.performGlobalAction(3);
    }

    public boolean openNotification() {
        return this.mUiAutomatorBridge.performGlobalAction(4);
    }

    public boolean openQuickSettings() {
        return this.mUiAutomatorBridge.performGlobalAction(MOTION_EVENT_INJECTION_DELAY_MILLIS);
    }
}
