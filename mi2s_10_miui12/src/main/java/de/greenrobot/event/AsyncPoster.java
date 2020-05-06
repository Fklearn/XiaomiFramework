package de.greenrobot.event;

class AsyncPoster implements Runnable {
    private final EventBus eventBus;
    private final PendingPostQueue queue = new PendingPostQueue();

    AsyncPoster(EventBus eventBus2) {
        this.eventBus = eventBus2;
    }

    public void enqueue(Subscription subscription, Object event) {
        this.queue.enqueue(PendingPost.obtainPendingPost(subscription, event));
        this.eventBus.getExecutorService().execute(this);
    }

    public void run() {
        PendingPost pendingPost = this.queue.poll();
        if (pendingPost != null) {
            this.eventBus.invokeSubscriber(pendingPost);
            return;
        }
        throw new IllegalStateException("No pending post available");
    }
}
