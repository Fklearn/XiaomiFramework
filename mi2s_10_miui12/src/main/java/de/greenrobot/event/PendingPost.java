package de.greenrobot.event;

import java.util.ArrayList;
import java.util.List;

final class PendingPost {
    private static final List<PendingPost> pendingPostPool = new ArrayList();
    Object event;
    PendingPost next;
    Subscription subscription;

    private PendingPost(Object event2, Subscription subscription2) {
        this.event = event2;
        this.subscription = subscription2;
    }

    static PendingPost obtainPendingPost(Subscription subscription2, Object event2) {
        synchronized (pendingPostPool) {
            int size = pendingPostPool.size();
            if (size <= 0) {
                return new PendingPost(event2, subscription2);
            }
            PendingPost pendingPost = pendingPostPool.remove(size - 1);
            pendingPost.event = event2;
            pendingPost.subscription = subscription2;
            pendingPost.next = null;
            return pendingPost;
        }
    }

    static void releasePendingPost(PendingPost pendingPost) {
        pendingPost.event = null;
        pendingPost.subscription = null;
        pendingPost.next = null;
        synchronized (pendingPostPool) {
            if (pendingPostPool.size() < 10000) {
                pendingPostPool.add(pendingPost);
            }
        }
    }
}
