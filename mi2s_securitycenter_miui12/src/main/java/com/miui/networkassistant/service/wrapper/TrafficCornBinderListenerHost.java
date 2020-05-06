package com.miui.networkassistant.service.wrapper;

import com.miui.networkassistant.model.TrafficUsedStatus;
import com.miui.networkassistant.service.ITrafficCornBinderListener;
import java.lang.ref.WeakReference;

public abstract class TrafficCornBinderListenerHost {
    ITrafficCornBinderListener mTrafficCornBinderListener = new TrafficCornBinderListenerStub();

    private static class TrafficCornBinderListenerStub extends ITrafficCornBinderListener.Stub {
        private WeakReference<TrafficCornBinderListenerHost> mHost;

        private TrafficCornBinderListenerStub(TrafficCornBinderListenerHost trafficCornBinderListenerHost) {
            this.mHost = new WeakReference<>(trafficCornBinderListenerHost);
        }

        public void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus) {
            TrafficCornBinderListenerHost trafficCornBinderListenerHost;
            WeakReference<TrafficCornBinderListenerHost> weakReference = this.mHost;
            if (weakReference != null && (trafficCornBinderListenerHost = (TrafficCornBinderListenerHost) weakReference.get()) != null) {
                trafficCornBinderListenerHost.onTrafficCorrected(trafficUsedStatus);
            }
        }
    }

    public ITrafficCornBinderListener getStub() {
        return this.mTrafficCornBinderListener;
    }

    public abstract void onTrafficCorrected(TrafficUsedStatus trafficUsedStatus);
}
