package de.greenrobot.event;

final class PendingPostQueue {
    private PendingPost head;
    private PendingPost tail;

    PendingPostQueue() {
    }

    /* access modifiers changed from: package-private */
    public synchronized void enqueue(PendingPost pendingPost) {
        if (pendingPost != null) {
            try {
                if (this.tail != null) {
                    this.tail.next = pendingPost;
                    this.tail = pendingPost;
                } else if (this.head == null) {
                    this.tail = pendingPost;
                    this.head = pendingPost;
                } else {
                    throw new IllegalStateException("Head present, but no tail");
                }
                notifyAll();
            } catch (Throwable th) {
                throw th;
            }
        } else {
            throw new NullPointerException("null cannot be enqueued");
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized PendingPost poll() {
        PendingPost pendingPost;
        pendingPost = this.head;
        if (this.head != null) {
            this.head = this.head.next;
            if (this.head == null) {
                this.tail = null;
            }
        }
        return pendingPost;
    }

    /* access modifiers changed from: package-private */
    public synchronized PendingPost poll(int maxMillisToWait) throws InterruptedException {
        if (this.head == null) {
            wait((long) maxMillisToWait);
        }
        return poll();
    }
}
