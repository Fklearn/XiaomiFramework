package com.google.android.exoplayer2.ui;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerNotificationManager {
    public static final String ACTION_FAST_FORWARD = "com.google.android.exoplayer.ffwd";
    public static final String ACTION_NEXT = "com.google.android.exoplayer.next";
    public static final String ACTION_PAUSE = "com.google.android.exoplayer.pause";
    public static final String ACTION_PLAY = "com.google.android.exoplayer.play";
    public static final String ACTION_PREVIOUS = "com.google.android.exoplayer.prev";
    public static final String ACTION_REWIND = "com.google.android.exoplayer.rewind";
    public static final String ACTION_STOP = "com.google.android.exoplayer.stop";
    public static final int DEFAULT_FAST_FORWARD_MS = 15000;
    public static final int DEFAULT_REWIND_MS = 5000;
    private static final long MAX_POSITION_FOR_SEEK_TO_PREVIOUS = 3000;
    private int badgeIconType;
    private final String channelId;
    private int color;
    private boolean colorized;
    private final Context context;
    /* access modifiers changed from: private */
    public ControlDispatcher controlDispatcher;
    /* access modifiers changed from: private */
    public int currentNotificationTag;
    /* access modifiers changed from: private */
    @Nullable
    public final CustomActionReceiver customActionReceiver;
    /* access modifiers changed from: private */
    public final Map<String, NotificationCompat.Action> customActions;
    private int defaults;
    /* access modifiers changed from: private */
    public long fastForwardMs;
    private final IntentFilter intentFilter;
    /* access modifiers changed from: private */
    public boolean isNotificationStarted;
    /* access modifiers changed from: private */
    public int lastPlaybackState;
    /* access modifiers changed from: private */
    public final Handler mainHandler;
    private final MediaDescriptionAdapter mediaDescriptionAdapter;
    @Nullable
    private MediaSessionCompat.Token mediaSessionToken;
    private final NotificationBroadcastReceiver notificationBroadcastReceiver;
    private final int notificationId;
    @Nullable
    private NotificationListener notificationListener;
    private final NotificationManagerCompat notificationManager;
    private boolean ongoing;
    private final Map<String, NotificationCompat.Action> playbackActions;
    /* access modifiers changed from: private */
    @Nullable
    public Player player;
    private final Player.EventListener playerListener;
    private int priority;
    /* access modifiers changed from: private */
    public long rewindMs;
    @DrawableRes
    private int smallIconResourceId;
    @Nullable
    private String stopAction;
    @Nullable
    private PendingIntent stopPendingIntent;
    private boolean useChronometer;
    private boolean useNavigationActions;
    private boolean usePlayPauseActions;
    private int visibility;
    /* access modifiers changed from: private */
    public boolean wasPlayWhenReady;

    public final class BitmapCallback {
        /* access modifiers changed from: private */
        public final int notificationTag;

        private BitmapCallback(int i) {
            this.notificationTag = i;
        }

        public void onBitmap(final Bitmap bitmap) {
            if (bitmap != null) {
                PlayerNotificationManager.this.mainHandler.post(new Runnable() {
                    public void run() {
                        if (PlayerNotificationManager.this.player != null && BitmapCallback.this.notificationTag == PlayerNotificationManager.this.currentNotificationTag && PlayerNotificationManager.this.isNotificationStarted) {
                            Notification unused = PlayerNotificationManager.this.updateNotification(bitmap);
                        }
                    }
                });
            }
        }
    }

    public interface CustomActionReceiver {
        Map<String, NotificationCompat.Action> createCustomActions(Context context);

        List<String> getCustomActions(Player player);

        void onCustomAction(Player player, String str, Intent intent);
    }

    public interface MediaDescriptionAdapter {
        @Nullable
        PendingIntent createCurrentContentIntent(Player player);

        @Nullable
        String getCurrentContentText(Player player);

        String getCurrentContentTitle(Player player);

        @Nullable
        Bitmap getCurrentLargeIcon(Player player, BitmapCallback bitmapCallback);
    }

    private class NotificationBroadcastReceiver extends BroadcastReceiver {
        private final Timeline.Window window = new Timeline.Window();

        public NotificationBroadcastReceiver() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:26:0x0086, code lost:
            if (r0.isSeekable == false) goto L_0x004e;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r8, android.content.Intent r9) {
            /*
                r7 = this;
                com.google.android.exoplayer2.ui.PlayerNotificationManager r8 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.Player r8 = r8.player
                if (r8 == 0) goto L_0x0101
                com.google.android.exoplayer2.ui.PlayerNotificationManager r0 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                boolean r0 = r0.isNotificationStarted
                if (r0 != 0) goto L_0x0012
                goto L_0x0101
            L_0x0012:
                java.lang.String r0 = r9.getAction()
                java.lang.String r1 = "com.google.android.exoplayer.play"
                boolean r2 = r1.equals(r0)
                if (r2 != 0) goto L_0x00f4
                java.lang.String r2 = "com.google.android.exoplayer.pause"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x0028
                goto L_0x00f4
            L_0x0028:
                java.lang.String r1 = "com.google.android.exoplayer.ffwd"
                boolean r2 = r1.equals(r0)
                if (r2 != 0) goto L_0x00cd
                java.lang.String r2 = "com.google.android.exoplayer.rewind"
                boolean r2 = r2.equals(r0)
                if (r2 == 0) goto L_0x003a
                goto L_0x00cd
            L_0x003a:
                java.lang.String r1 = "com.google.android.exoplayer.next"
                boolean r1 = r1.equals(r0)
                r2 = -1
                r3 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
                if (r1 == 0) goto L_0x0059
                int r9 = r8.getNextWindowIndex()
                if (r9 == r2) goto L_0x0101
            L_0x004e:
                com.google.android.exoplayer2.ui.PlayerNotificationManager r0 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.ControlDispatcher r0 = r0.controlDispatcher
                r0.dispatchSeekTo(r8, r9, r3)
                goto L_0x0101
            L_0x0059:
                java.lang.String r1 = "com.google.android.exoplayer.prev"
                boolean r1 = r1.equals(r0)
                if (r1 == 0) goto L_0x0097
                com.google.android.exoplayer2.Timeline r9 = r8.getCurrentTimeline()
                int r0 = r8.getCurrentWindowIndex()
                com.google.android.exoplayer2.Timeline$Window r1 = r7.window
                r9.getWindow(r0, r1)
                int r9 = r8.getPreviousWindowIndex()
                if (r9 == r2) goto L_0x0089
                long r0 = r8.getCurrentPosition()
                r5 = 3000(0xbb8, double:1.482E-320)
                int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r0 <= 0) goto L_0x004e
                com.google.android.exoplayer2.Timeline$Window r0 = r7.window
                boolean r1 = r0.isDynamic
                if (r1 == 0) goto L_0x0089
                boolean r0 = r0.isSeekable
                if (r0 != 0) goto L_0x0089
                goto L_0x004e
            L_0x0089:
                com.google.android.exoplayer2.ui.PlayerNotificationManager r9 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.ControlDispatcher r9 = r9.controlDispatcher
                int r0 = r8.getCurrentWindowIndex()
                r9.dispatchSeekTo(r8, r0, r3)
                goto L_0x0101
            L_0x0097:
                java.lang.String r1 = "com.google.android.exoplayer.stop"
                boolean r1 = r1.equals(r0)
                if (r1 == 0) goto L_0x00af
                com.google.android.exoplayer2.ui.PlayerNotificationManager r9 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.ControlDispatcher r9 = r9.controlDispatcher
                r0 = 1
                r9.dispatchStop(r8, r0)
                com.google.android.exoplayer2.ui.PlayerNotificationManager r8 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                r8.stopNotification()
                goto L_0x0101
            L_0x00af:
                com.google.android.exoplayer2.ui.PlayerNotificationManager r1 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.ui.PlayerNotificationManager$CustomActionReceiver r1 = r1.customActionReceiver
                if (r1 == 0) goto L_0x0101
                com.google.android.exoplayer2.ui.PlayerNotificationManager r1 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                java.util.Map r1 = r1.customActions
                boolean r1 = r1.containsKey(r0)
                if (r1 == 0) goto L_0x0101
                com.google.android.exoplayer2.ui.PlayerNotificationManager r1 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.ui.PlayerNotificationManager$CustomActionReceiver r1 = r1.customActionReceiver
                r1.onCustomAction(r8, r0, r9)
                goto L_0x0101
            L_0x00cd:
                boolean r9 = r1.equals(r0)
                if (r9 == 0) goto L_0x00da
                com.google.android.exoplayer2.ui.PlayerNotificationManager r9 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                long r0 = r9.fastForwardMs
                goto L_0x00e1
            L_0x00da:
                com.google.android.exoplayer2.ui.PlayerNotificationManager r9 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                long r0 = r9.rewindMs
                long r0 = -r0
            L_0x00e1:
                com.google.android.exoplayer2.ui.PlayerNotificationManager r9 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.ControlDispatcher r9 = r9.controlDispatcher
                int r2 = r8.getCurrentWindowIndex()
                long r3 = r8.getCurrentPosition()
                long r3 = r3 + r0
                r9.dispatchSeekTo(r8, r2, r3)
                goto L_0x0101
            L_0x00f4:
                com.google.android.exoplayer2.ui.PlayerNotificationManager r9 = com.google.android.exoplayer2.ui.PlayerNotificationManager.this
                com.google.android.exoplayer2.ControlDispatcher r9 = r9.controlDispatcher
                boolean r0 = r1.equals(r0)
                r9.dispatchSetPlayWhenReady(r8, r0)
            L_0x0101:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationBroadcastReceiver.onReceive(android.content.Context, android.content.Intent):void");
        }
    }

    public interface NotificationListener {
        void onNotificationCancelled(int i);

        void onNotificationStarted(int i, Notification notification);
    }

    private class PlayerListener extends Player.DefaultEventListener {
        private PlayerListener() {
        }

        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            if (PlayerNotificationManager.this.player != null && PlayerNotificationManager.this.player.getPlaybackState() != 1) {
                PlayerNotificationManager.this.startOrUpdateNotification();
            }
        }

        public void onPlayerStateChanged(boolean z, int i) {
            if (!((PlayerNotificationManager.this.wasPlayWhenReady == z || i == 1) && PlayerNotificationManager.this.lastPlaybackState == i)) {
                PlayerNotificationManager.this.startOrUpdateNotification();
            }
            boolean unused = PlayerNotificationManager.this.wasPlayWhenReady = z;
            int unused2 = PlayerNotificationManager.this.lastPlaybackState = i;
        }

        public void onPositionDiscontinuity(int i) {
            PlayerNotificationManager.this.startOrUpdateNotification();
        }

        public void onRepeatModeChanged(int i) {
            if (PlayerNotificationManager.this.player != null && PlayerNotificationManager.this.player.getPlaybackState() != 1) {
                PlayerNotificationManager.this.startOrUpdateNotification();
            }
        }

        public void onTimelineChanged(Timeline timeline, Object obj, int i) {
            if (PlayerNotificationManager.this.player != null && PlayerNotificationManager.this.player.getPlaybackState() != 1) {
                PlayerNotificationManager.this.startOrUpdateNotification();
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Priority {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public PlayerNotificationManager(Context context2, String str, int i, MediaDescriptionAdapter mediaDescriptionAdapter2) {
        this(context2, str, i, mediaDescriptionAdapter2, (CustomActionReceiver) null);
    }

    public PlayerNotificationManager(Context context2, String str, int i, MediaDescriptionAdapter mediaDescriptionAdapter2, @Nullable CustomActionReceiver customActionReceiver2) {
        this.context = context2.getApplicationContext();
        this.channelId = str;
        this.notificationId = i;
        this.mediaDescriptionAdapter = mediaDescriptionAdapter2;
        this.customActionReceiver = customActionReceiver2;
        this.controlDispatcher = new DefaultControlDispatcher();
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.notificationManager = NotificationManagerCompat.from(context2);
        this.playerListener = new PlayerListener();
        this.notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        this.intentFilter = new IntentFilter();
        this.useNavigationActions = true;
        this.usePlayPauseActions = true;
        this.ongoing = true;
        this.colorized = true;
        this.useChronometer = true;
        this.color = 0;
        this.smallIconResourceId = R.drawable.exo_notification_small_icon;
        this.defaults = 0;
        this.priority = -1;
        this.fastForwardMs = 15000;
        this.rewindMs = DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
        this.stopAction = ACTION_STOP;
        this.badgeIconType = 1;
        this.visibility = 1;
        this.playbackActions = createPlaybackActions(context2);
        for (String addAction : this.playbackActions.keySet()) {
            this.intentFilter.addAction(addAction);
        }
        this.customActions = customActionReceiver2 != null ? customActionReceiver2.createCustomActions(context2) : Collections.emptyMap();
        for (String addAction2 : this.customActions.keySet()) {
            this.intentFilter.addAction(addAction2);
        }
        NotificationCompat.Action action = this.playbackActions.get(ACTION_STOP);
        Assertions.checkNotNull(action);
        this.stopPendingIntent = action.actionIntent;
    }

    private static Map<String, NotificationCompat.Action> createPlaybackActions(Context context2) {
        HashMap hashMap = new HashMap();
        hashMap.put(ACTION_PLAY, new NotificationCompat.Action(R.drawable.exo_notification_play, context2.getString(R.string.exo_controls_play_description), PendingIntent.getBroadcast(context2, 0, new Intent(ACTION_PLAY).setPackage(context2.getPackageName()), 268435456)));
        hashMap.put(ACTION_PAUSE, new NotificationCompat.Action(R.drawable.exo_notification_pause, context2.getString(R.string.exo_controls_pause_description), PendingIntent.getBroadcast(context2, 0, new Intent(ACTION_PAUSE).setPackage(context2.getPackageName()), 268435456)));
        hashMap.put(ACTION_STOP, new NotificationCompat.Action(R.drawable.exo_notification_stop, context2.getString(R.string.exo_controls_stop_description), PendingIntent.getBroadcast(context2, 0, new Intent(ACTION_STOP).setPackage(context2.getPackageName()), 268435456)));
        hashMap.put(ACTION_REWIND, new NotificationCompat.Action(R.drawable.exo_notification_rewind, context2.getString(R.string.exo_controls_rewind_description), PendingIntent.getBroadcast(context2, 0, new Intent(ACTION_REWIND).setPackage(context2.getPackageName()), 268435456)));
        hashMap.put(ACTION_FAST_FORWARD, new NotificationCompat.Action(R.drawable.exo_notification_fastforward, context2.getString(R.string.exo_controls_fastforward_description), PendingIntent.getBroadcast(context2, 0, new Intent(ACTION_FAST_FORWARD).setPackage(context2.getPackageName()), 268435456)));
        hashMap.put(ACTION_PREVIOUS, new NotificationCompat.Action(R.drawable.exo_notification_previous, context2.getString(R.string.exo_controls_previous_description), PendingIntent.getBroadcast(context2, 0, new Intent(ACTION_PREVIOUS).setPackage(context2.getPackageName()), 268435456)));
        hashMap.put(ACTION_NEXT, new NotificationCompat.Action(R.drawable.exo_notification_next, context2.getString(R.string.exo_controls_next_description), PendingIntent.getBroadcast(context2, 0, new Intent(ACTION_NEXT).setPackage(context2.getPackageName()), 268435456)));
        return hashMap;
    }

    public static PlayerNotificationManager createWithNotificationChannel(Context context2, String str, @StringRes int i, int i2, MediaDescriptionAdapter mediaDescriptionAdapter2) {
        NotificationUtil.createNotificationChannel(context2, str, i, 2);
        return new PlayerNotificationManager(context2, str, i2, mediaDescriptionAdapter2);
    }

    private void maybeUpdateNotification() {
        if (this.isNotificationStarted && this.player != null) {
            updateNotification((Bitmap) null);
        }
    }

    /* access modifiers changed from: private */
    public void startOrUpdateNotification() {
        if (this.player != null) {
            Notification updateNotification = updateNotification((Bitmap) null);
            if (!this.isNotificationStarted) {
                this.isNotificationStarted = true;
                this.context.registerReceiver(this.notificationBroadcastReceiver, this.intentFilter);
                NotificationListener notificationListener2 = this.notificationListener;
                if (notificationListener2 != null) {
                    notificationListener2.onNotificationStarted(this.notificationId, updateNotification);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void stopNotification() {
        if (this.isNotificationStarted) {
            this.notificationManager.cancel(this.notificationId);
            this.isNotificationStarted = false;
            this.context.unregisterReceiver(this.notificationBroadcastReceiver);
            NotificationListener notificationListener2 = this.notificationListener;
            if (notificationListener2 != null) {
                notificationListener2.onNotificationCancelled(this.notificationId);
            }
        }
    }

    /* access modifiers changed from: private */
    public Notification updateNotification(@Nullable Bitmap bitmap) {
        Notification createNotification = createNotification(this.player, bitmap);
        this.notificationManager.notify(this.notificationId, createNotification);
        return createNotification;
    }

    /* access modifiers changed from: protected */
    public Notification createNotification(Player player2, @Nullable Bitmap bitmap) {
        PendingIntent pendingIntent;
        boolean isPlayingAd = player2.isPlayingAd();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, this.channelId);
        List<String> actions = getActions(player2);
        for (int i = 0; i < actions.size(); i++) {
            String str = actions.get(i);
            NotificationCompat.Action action = (this.playbackActions.containsKey(str) ? this.playbackActions : this.customActions).get(str);
            if (action != null) {
                builder.addAction(action);
            }
        }
        NotificationCompat.MediaStyle mediaStyle = new NotificationCompat.MediaStyle();
        builder.setStyle(mediaStyle);
        MediaSessionCompat.Token token = this.mediaSessionToken;
        if (token != null) {
            mediaStyle.setMediaSession(token);
        }
        mediaStyle.setShowActionsInCompactView(getActionIndicesForCompactView(player2));
        boolean z = this.stopAction != null && !isPlayingAd;
        mediaStyle.setShowCancelButton(z);
        if (z && (pendingIntent = this.stopPendingIntent) != null) {
            builder.setDeleteIntent(pendingIntent);
            mediaStyle.setCancelButtonIntent(this.stopPendingIntent);
        }
        builder.setBadgeIconType(this.badgeIconType).setOngoing(this.ongoing).setColor(this.color).setColorized(this.colorized).setSmallIcon(this.smallIconResourceId).setVisibility(this.visibility).setPriority(this.priority).setDefaults(this.defaults);
        if (!this.useChronometer || player2.isCurrentWindowDynamic() || !player2.getPlayWhenReady() || player2.getPlaybackState() != 3) {
            builder.setShowWhen(false).setUsesChronometer(false);
        } else {
            builder.setWhen(System.currentTimeMillis() - player2.getContentPosition()).setShowWhen(true).setUsesChronometer(true);
        }
        builder.setContentTitle(this.mediaDescriptionAdapter.getCurrentContentTitle(player2));
        builder.setContentText(this.mediaDescriptionAdapter.getCurrentContentText(player2));
        if (bitmap == null) {
            MediaDescriptionAdapter mediaDescriptionAdapter2 = this.mediaDescriptionAdapter;
            int i2 = this.currentNotificationTag + 1;
            this.currentNotificationTag = i2;
            bitmap = mediaDescriptionAdapter2.getCurrentLargeIcon(player2, new BitmapCallback(i2));
        }
        if (bitmap != null) {
            builder.setLargeIcon(bitmap);
        }
        PendingIntent createCurrentContentIntent = this.mediaDescriptionAdapter.createCurrentContentIntent(player2);
        if (createCurrentContentIntent != null) {
            builder.setContentIntent(createCurrentContentIntent);
        }
        return builder.build();
    }

    /* access modifiers changed from: protected */
    public int[] getActionIndicesForCompactView(Player player2) {
        if (!this.usePlayPauseActions) {
            return new int[0];
        }
        return new int[]{(this.useNavigationActions ? 1 : 0) + ((this.fastForwardMs > 0 ? 1 : (this.fastForwardMs == 0 ? 0 : -1)) > 0 ? 1 : 0)};
    }

    /* access modifiers changed from: protected */
    public List<String> getActions(Player player2) {
        ArrayList arrayList = new ArrayList();
        if (!player2.isPlayingAd()) {
            if (this.useNavigationActions) {
                arrayList.add(ACTION_PREVIOUS);
            }
            if (this.rewindMs > 0) {
                arrayList.add(ACTION_REWIND);
            }
            if (this.usePlayPauseActions) {
                arrayList.add(player2.getPlayWhenReady() ? ACTION_PAUSE : ACTION_PLAY);
            }
            if (this.fastForwardMs > 0) {
                arrayList.add(ACTION_FAST_FORWARD);
            }
            if (this.useNavigationActions && player2.getNextWindowIndex() != -1) {
                arrayList.add(ACTION_NEXT);
            }
            CustomActionReceiver customActionReceiver2 = this.customActionReceiver;
            if (customActionReceiver2 != null) {
                arrayList.addAll(customActionReceiver2.getCustomActions(player2));
            }
            if (ACTION_STOP.equals(this.stopAction)) {
                arrayList.add(this.stopAction);
            }
        }
        return arrayList;
    }

    public final void setBadgeIconType(int i) {
        if (this.badgeIconType != i) {
            if (i == 0 || i == 1 || i == 2) {
                this.badgeIconType = i;
                maybeUpdateNotification();
                return;
            }
            throw new IllegalArgumentException();
        }
    }

    public final void setColor(int i) {
        if (this.color != i) {
            this.color = i;
            maybeUpdateNotification();
        }
    }

    public final void setColorized(boolean z) {
        if (this.colorized != z) {
            this.colorized = z;
            maybeUpdateNotification();
        }
    }

    public final void setControlDispatcher(ControlDispatcher controlDispatcher2) {
        if (controlDispatcher2 == null) {
            controlDispatcher2 = new DefaultControlDispatcher();
        }
        this.controlDispatcher = controlDispatcher2;
    }

    public final void setDefaults(int i) {
        if (this.defaults != i) {
            this.defaults = i;
            maybeUpdateNotification();
        }
    }

    public final void setFastForwardIncrementMs(long j) {
        if (this.fastForwardMs != j) {
            this.fastForwardMs = j;
            maybeUpdateNotification();
        }
    }

    public final void setMediaSessionToken(MediaSessionCompat.Token token) {
        if (!Util.areEqual(this.mediaSessionToken, token)) {
            this.mediaSessionToken = token;
            maybeUpdateNotification();
        }
    }

    public final void setNotificationListener(NotificationListener notificationListener2) {
        this.notificationListener = notificationListener2;
    }

    public final void setOngoing(boolean z) {
        if (this.ongoing != z) {
            this.ongoing = z;
            maybeUpdateNotification();
        }
    }

    public final void setPlayer(@Nullable Player player2) {
        Player player3 = this.player;
        if (player3 != player2) {
            if (player3 != null) {
                player3.removeListener(this.playerListener);
                if (player2 == null) {
                    stopNotification();
                }
            }
            this.player = player2;
            if (player2 != null) {
                this.wasPlayWhenReady = player2.getPlayWhenReady();
                this.lastPlaybackState = player2.getPlaybackState();
                player2.addListener(this.playerListener);
                if (this.lastPlaybackState != 1) {
                    startOrUpdateNotification();
                }
            }
        }
    }

    public final void setPriority(int i) {
        if (this.priority != i) {
            if (i == -2 || i == -1 || i == 0 || i == 1 || i == 2) {
                this.priority = i;
                maybeUpdateNotification();
                return;
            }
            throw new IllegalArgumentException();
        }
    }

    public final void setRewindIncrementMs(long j) {
        if (this.rewindMs != j) {
            this.rewindMs = j;
            maybeUpdateNotification();
        }
    }

    public final void setSmallIcon(@DrawableRes int i) {
        if (this.smallIconResourceId != i) {
            this.smallIconResourceId = i;
            maybeUpdateNotification();
        }
    }

    public final void setStopAction(@Nullable String str) {
        PendingIntent pendingIntent;
        Object obj;
        if (!Util.areEqual(str, this.stopAction)) {
            this.stopAction = str;
            if (ACTION_STOP.equals(str)) {
                obj = this.playbackActions.get(ACTION_STOP);
            } else if (str != null) {
                obj = this.customActions.get(str);
            } else {
                pendingIntent = null;
                this.stopPendingIntent = pendingIntent;
                maybeUpdateNotification();
            }
            Assertions.checkNotNull(obj);
            pendingIntent = ((NotificationCompat.Action) obj).actionIntent;
            this.stopPendingIntent = pendingIntent;
            maybeUpdateNotification();
        }
    }

    public final void setUseChronometer(boolean z) {
        if (this.useChronometer != z) {
            this.useChronometer = z;
            maybeUpdateNotification();
        }
    }

    public final void setUseNavigationActions(boolean z) {
        if (this.useNavigationActions != z) {
            this.useNavigationActions = z;
            maybeUpdateNotification();
        }
    }

    public final void setUsePlayPauseActions(boolean z) {
        if (this.usePlayPauseActions != z) {
            this.usePlayPauseActions = z;
            maybeUpdateNotification();
        }
    }

    public final void setVisibility(int i) {
        if (this.visibility != i) {
            if (i == -1 || i == 0 || i == 1) {
                this.visibility = i;
                maybeUpdateNotification();
                return;
            }
            throw new IllegalStateException();
        }
    }
}
