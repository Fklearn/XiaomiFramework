package com.miui.maml.elements;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.Rating;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.RequiresApi;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ButtonScreenElement;
import com.miui.maml.elements.MusicController;
import com.miui.maml.elements.MusicLyricParser;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

public class MusicControlScreenElement extends ElementGroup implements ButtonScreenElement.ButtonActionListener {
    private static final String BUTTON_MUSIC_NEXT = "music_next";
    private static final String BUTTON_MUSIC_PAUSE = "music_pause";
    private static final String BUTTON_MUSIC_PLAY = "music_play";
    private static final String BUTTON_MUSIC_PREV = "music_prev";
    private static final String ELE_MUSIC_ALBUM_COVER = "music_album_cover";
    private static final String ELE_MUSIC_DISPLAY = "music_display";
    private static final int FRAMERATE_PLAYING = 30;
    private static final String LOG_TAG = "MusicControlScreenElement";
    public static final String METADATA_KEY_LYRIC = "android.media.metadata.LYRIC";
    private static final String MIUI_PLAYER_PACKAGE_NAME = "com.miui.player";
    public static final int MUSIC_STATE_PLAY = 1;
    public static final int MUSIC_STATE_STOP = 0;
    public static final int PLAYSTATE_NONE = 0;
    public static final String TAG_NAME = "MusicControl";
    /* access modifiers changed from: private */
    public AlbumInfo mAlbumInfo = new AlbumInfo();
    private IndexedVariable mAlbumVar;
    private IndexedVariable mArtistVar;
    private IndexedVariable mArtworkVar;
    /* access modifiers changed from: private */
    public boolean mAutoShow;
    private ButtonScreenElement mButtonNext = ((ButtonScreenElement) findElement(BUTTON_MUSIC_NEXT));
    private ButtonScreenElement mButtonPause = ((ButtonScreenElement) findElement(BUTTON_MUSIC_PAUSE));
    private ButtonScreenElement mButtonPlay = ((ButtonScreenElement) findElement(BUTTON_MUSIC_PLAY));
    private ButtonScreenElement mButtonPrev = ((ButtonScreenElement) findElement(BUTTON_MUSIC_PREV));
    /* access modifiers changed from: private */
    public Bitmap mDefaultAlbumCoverBm;
    private Runnable mDelayToSetArtworkRunnable = new Runnable() {
        public void run() {
            MusicControlScreenElement musicControlScreenElement = MusicControlScreenElement.this;
            musicControlScreenElement.updateArtwork(musicControlScreenElement.mDefaultAlbumCoverBm);
        }
    };
    /* access modifiers changed from: private */
    public boolean mDisableNext;
    /* access modifiers changed from: private */
    public IndexedVariable mDisableNextVar;
    /* access modifiers changed from: private */
    public boolean mDisablePlay;
    /* access modifiers changed from: private */
    public IndexedVariable mDisablePlayVar;
    /* access modifiers changed from: private */
    public boolean mDisablePrev;
    /* access modifiers changed from: private */
    public IndexedVariable mDisablePrevVar;
    /* access modifiers changed from: private */
    public IndexedVariable mDurationVar;
    private boolean mEnableLyric;
    private boolean mEnableProgress;
    private ImageScreenElement mImageAlbumCover = ((ImageScreenElement) findElement(ELE_MUSIC_ALBUM_COVER));
    /* access modifiers changed from: private */
    public MusicLyricParser.Lyric mLyric;
    private IndexedVariable mLyricAfterVar;
    private IndexedVariable mLyricBeforeVar;
    private IndexedVariable mLyricCurrentLineProgressVar;
    private IndexedVariable mLyricCurrentVar;
    private IndexedVariable mLyricLastVar;
    private IndexedVariable mLyricNextVar;
    private IndexedVariable mLyricPrevVar;
    /* access modifiers changed from: private */
    public MediaMetadata mMetadata;
    private Context mMiuiMusicContext;
    /* access modifiers changed from: private */
    public MusicController mMusicController;
    private IndexedVariable mMusicStateVar;
    private MusicController.OnClientUpdateListener mMusicUpdateListener = new MusicController.OnClientUpdateListener() {
        private boolean mClientChanged;

        public void onClientChange() {
            this.mClientChanged = true;
            MusicControlScreenElement.this.resetAll();
            MusicControlScreenElement.this.readPackageName();
            StringBuilder sb = new StringBuilder();
            sb.append("clientChange: ");
            String str = "null";
            sb.append(MusicControlScreenElement.this.mPlayerPackageVar != null ? MusicControlScreenElement.this.mPlayerPackageVar.getString() : str);
            sb.append("/");
            if (MusicControlScreenElement.this.mPlayerClassVar != null) {
                str = MusicControlScreenElement.this.mPlayerClassVar.getString();
            }
            sb.append(str);
            Log.d(MusicControlScreenElement.LOG_TAG, sb.toString());
        }

        @RequiresApi(api = 21)
        public void onClientMetadataUpdate(MediaMetadata mediaMetadata) {
            boolean z;
            MediaMetadata unused = MusicControlScreenElement.this.mMetadata = mediaMetadata;
            if (MusicControlScreenElement.this.mMetadata != null) {
                String string = MusicControlScreenElement.this.mMetadata.getString("android.media.metadata.TITLE");
                String string2 = MusicControlScreenElement.this.mMetadata.getString("android.media.metadata.ARTIST");
                String string3 = MusicControlScreenElement.this.mMetadata.getString("android.media.metadata.ALBUM");
                Log.d(MusicControlScreenElement.LOG_TAG, "\ntitle: " + string + ", artist: " + string2 + ", album: " + string3);
                if (string == null && string2 == null && string3 == null) {
                    z = false;
                } else {
                    z = MusicControlScreenElement.this.mAlbumInfo.update(string, string2, string3);
                    MusicControlScreenElement.this.updateAlbum(string, string2, string3);
                }
                Bitmap bitmap = MusicControlScreenElement.this.mMetadata.getBitmap("android.media.metadata.ART");
                StringBuilder sb = new StringBuilder();
                sb.append("artwork: ");
                sb.append(bitmap != null ? bitmap.toString() : "null");
                Log.d(MusicControlScreenElement.LOG_TAG, sb.toString());
                boolean z2 = true;
                if (bitmap != null || z) {
                    if (bitmap == null) {
                        MusicControlScreenElement.this.delayToSetDefaultArtwork(500);
                    } else {
                        MusicControlScreenElement.this.updateArtwork(bitmap);
                    }
                }
                String string4 = MusicControlScreenElement.this.mMetadata.getString(MusicControlScreenElement.METADATA_KEY_LYRIC);
                Log.d(MusicControlScreenElement.LOG_TAG, "raw lyric: " + string4);
                MusicLyricParser.Lyric parseLyric = MusicLyricParser.parseLyric(string4);
                if (parseLyric != null) {
                    parseLyric.decorate();
                }
                if (parseLyric != null || z) {
                    MusicLyricParser.Lyric unused2 = MusicControlScreenElement.this.mLyric = parseLyric;
                    MusicControlScreenElement.this.updateLyric(parseLyric);
                }
                MusicControlScreenElement musicControlScreenElement = MusicControlScreenElement.this;
                musicControlScreenElement.requestFramerate(musicControlScreenElement.mLyric != null ? 30.0f : 0.0f);
                long j = MusicControlScreenElement.this.mMetadata.getLong("android.media.metadata.DURATION");
                long estimatedMediaPosition = MusicControlScreenElement.this.mMusicController.getEstimatedMediaPosition();
                Log.d(MusicControlScreenElement.LOG_TAG, "duration: " + j + ", position: " + estimatedMediaPosition);
                if ((j < 0 || estimatedMediaPosition < 0) && !z) {
                    z2 = false;
                }
                if (z2) {
                    MusicControlScreenElement.this.updateProgress(j, estimatedMediaPosition);
                }
                Rating rating = MusicControlScreenElement.this.mMetadata.getRating("android.media.metadata.USER_RATING");
                Log.d(MusicControlScreenElement.LOG_TAG, "rating: " + rating);
                MusicControlScreenElement.this.updateUserRating(rating);
                if (!this.mClientChanged) {
                    onClientChange();
                }
            }
        }

        public void onClientPlaybackActionUpdate(long j) {
            Log.d(MusicControlScreenElement.LOG_TAG, "transportControlFlags: " + j);
            boolean z = true;
            if (!((128 & j) != 0)) {
                MusicControlScreenElement.this.resetUserRating();
            }
            int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
            boolean unused = MusicControlScreenElement.this.mDisablePlay = i != 0 && (519 & j) == 0;
            boolean unused2 = MusicControlScreenElement.this.mDisablePrev = i != 0 && (16 & j) == 0;
            MusicControlScreenElement musicControlScreenElement = MusicControlScreenElement.this;
            if (i == 0 || (j & 32) != 0) {
                z = false;
            }
            boolean unused3 = musicControlScreenElement.mDisableNext = z;
            MusicControlScreenElement musicControlScreenElement2 = MusicControlScreenElement.this;
            if (musicControlScreenElement2.mHasName) {
                double d2 = 1.0d;
                musicControlScreenElement2.mDisablePlayVar.set(MusicControlScreenElement.this.mDisablePlay ? 1.0d : 0.0d);
                MusicControlScreenElement.this.mDisablePrevVar.set(MusicControlScreenElement.this.mDisablePrev ? 1.0d : 0.0d);
                IndexedVariable access$700 = MusicControlScreenElement.this.mDisableNextVar;
                if (!MusicControlScreenElement.this.mDisableNext) {
                    d2 = 0.0d;
                }
                access$700.set(d2);
            }
        }

        public void onClientPlaybackStateUpdate(int i) {
            onStateUpdate(i);
            Log.d(MusicControlScreenElement.LOG_TAG, "stateUpdate: " + i);
        }

        public void onSessionDestroyed() {
            if (MusicControlScreenElement.this.mAutoShow) {
                MusicControlScreenElement.this.show(false);
            }
            MusicControlScreenElement.this.onMusicStateChange(false);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x0026  */
        /* JADX WARNING: Removed duplicated region for block: B:14:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onStateUpdate(int r4) {
            /*
                r3 = this;
                r0 = 0
                r1 = 1
                if (r4 == 0) goto L_0x001e
                if (r4 == r1) goto L_0x0016
                r2 = 2
                if (r4 == r2) goto L_0x0016
                r2 = 3
                if (r4 == r2) goto L_0x000d
                goto L_0x0023
            L_0x000d:
                com.miui.maml.elements.MusicControlScreenElement r4 = com.miui.maml.elements.MusicControlScreenElement.this
                java.lang.String r0 = "state_play"
                r4.performAction(r0)
                r0 = r1
                goto L_0x0024
            L_0x0016:
                com.miui.maml.elements.MusicControlScreenElement r4 = com.miui.maml.elements.MusicControlScreenElement.this
                java.lang.String r2 = "state_stop"
                r4.performAction(r2)
                goto L_0x0024
            L_0x001e:
                com.miui.maml.elements.MusicControlScreenElement r4 = com.miui.maml.elements.MusicControlScreenElement.this
                r4.resetAll()
            L_0x0023:
                r1 = r0
            L_0x0024:
                if (r1 == 0) goto L_0x002b
                com.miui.maml.elements.MusicControlScreenElement r4 = com.miui.maml.elements.MusicControlScreenElement.this
                r4.onMusicStateChange(r0)
            L_0x002b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.MusicControlScreenElement.AnonymousClass1.onStateUpdate(int):void");
        }
    };
    /* access modifiers changed from: private */
    public boolean mNeedUpdateLyric;
    private boolean mNeedUpdateProgress;
    private boolean mNeedUpdateUserRating;
    /* access modifiers changed from: private */
    public IndexedVariable mPlayerClassVar;
    /* access modifiers changed from: private */
    public IndexedVariable mPlayerPackageVar;
    /* access modifiers changed from: private */
    public boolean mPlaying;
    /* access modifiers changed from: private */
    public IndexedVariable mPositionVar;
    private Runnable mResetMusicControllerRunable = new Runnable() {
        public void run() {
            if (MusicControlScreenElement.this.mMusicController != null) {
                MusicControlScreenElement.this.mMusicController.reset();
            }
        }
    };
    private String mSender;
    private SpectrumVisualizerScreenElement mSpectrumVisualizer = findSpectrumVisualizer(this);
    private TextScreenElement mTextDisplay = ((TextScreenElement) findElement(ELE_MUSIC_DISPLAY));
    private IndexedVariable mTitleVar;
    /* access modifiers changed from: private */
    public int mUpdateProgressInterval;
    private Runnable mUpdateProgressRunnable = new Runnable() {
        @RequiresApi(api = 21)
        public void run() {
            if (MusicControlScreenElement.this.mPlaying && MusicControlScreenElement.this.mMetadata != null) {
                long j = MusicControlScreenElement.this.mMetadata.getLong("android.media.metadata.DURATION");
                long estimatedMediaPosition = MusicControlScreenElement.this.mMusicController.getEstimatedMediaPosition();
                if (j > 0 && estimatedMediaPosition >= 0) {
                    MusicControlScreenElement.this.mDurationVar.set((double) j);
                    MusicControlScreenElement.this.mPositionVar.set((double) estimatedMediaPosition);
                    if (MusicControlScreenElement.this.mNeedUpdateLyric && MusicControlScreenElement.this.mLyric != null) {
                        MusicControlScreenElement.this.updateLyricVar(estimatedMediaPosition);
                    }
                    MusicControlScreenElement.this.requestUpdate();
                    MusicControlScreenElement.this.getContext().getHandler().postDelayed(this, (long) MusicControlScreenElement.this.mUpdateProgressInterval);
                }
            }
        }
    };
    private int mUserRatingStyle;
    private IndexedVariable mUserRatingStyleVar;
    private float mUserRatingValue;
    private IndexedVariable mUserRatingValueVar;

    private static class AlbumInfo {
        String album;
        String artist;
        String title;

        private AlbumInfo() {
        }

        /* access modifiers changed from: package-private */
        public boolean update(String str, String str2, String str3) {
            if (str != null) {
                str = str.trim();
            }
            if (str2 != null) {
                str2 = str2.trim();
            }
            if (str3 != null) {
                str3 = str3.trim();
            }
            boolean z = !TextUtils.equals(str, this.title) || !TextUtils.equals(str2, this.artist) || !TextUtils.equals(str3, this.album);
            if (z) {
                this.title = str;
                this.artist = str2;
                this.album = str3;
            }
            return z;
        }
    }

    public MusicControlScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        String str;
        setupButton(this.mButtonPrev);
        setupButton(this.mButtonNext);
        setupButton(this.mButtonPlay);
        setupButton(this.mButtonPause);
        ButtonScreenElement buttonScreenElement = this.mButtonPause;
        boolean z = false;
        if (buttonScreenElement != null) {
            buttonScreenElement.show(false);
        }
        if (this.mImageAlbumCover != null) {
            String attribute = element.getAttribute("defAlbumCover");
            if (!TextUtils.isEmpty(attribute)) {
                this.mDefaultAlbumCoverBm = getContext().mResourceManager.getBitmap(attribute);
            }
            Bitmap bitmap = this.mDefaultAlbumCoverBm;
            if (bitmap != null) {
                bitmap.setDensity(this.mRoot.getResourceDensity());
            }
        }
        this.mAutoShow = Boolean.parseBoolean(element.getAttribute("autoShow"));
        this.mEnableLyric = Boolean.parseBoolean(element.getAttribute("enableLyric"));
        this.mEnableProgress = this.mEnableLyric ? true : Boolean.parseBoolean(element.getAttribute("enableProgress"));
        this.mUpdateProgressInterval = getAttrAsInt(element, "updateProgressInterval", 1000);
        if (this.mHasName) {
            Variables variables = getVariables();
            this.mMusicStateVar = new IndexedVariable(this.mName + ".music_state", variables, true);
            this.mTitleVar = new IndexedVariable(this.mName + ".title", variables, false);
            this.mArtistVar = new IndexedVariable(this.mName + ".artist", variables, false);
            this.mAlbumVar = new IndexedVariable(this.mName + ".album", variables, false);
            if (this.mEnableLyric) {
                this.mLyricCurrentVar = new IndexedVariable(this.mName + ".lyric_current", variables, false);
                this.mLyricBeforeVar = new IndexedVariable(this.mName + ".lyric_before", variables, false);
                this.mLyricAfterVar = new IndexedVariable(this.mName + ".lyric_after", variables, false);
                this.mLyricLastVar = new IndexedVariable(this.mName + ".lyric_last", variables, false);
                this.mLyricPrevVar = new IndexedVariable(this.mName + ".lyric_prev", variables, false);
                this.mLyricNextVar = new IndexedVariable(this.mName + ".lyric_next", variables, false);
                this.mLyricCurrentLineProgressVar = new IndexedVariable(this.mName + ".lyric_current_line_progress", variables, true);
            }
            if (this.mEnableProgress) {
                this.mDurationVar = new IndexedVariable(this.mName + ".music_duration", variables, true);
                this.mPositionVar = new IndexedVariable(this.mName + ".music_position", variables, true);
            }
            this.mUserRatingStyleVar = new IndexedVariable(this.mName + ".user_rating_style", variables, true);
            this.mUserRatingValueVar = new IndexedVariable(this.mName + ".user_rating_value", variables, true);
            this.mDisablePlayVar = new IndexedVariable(this.mName + ".disable_play", variables, true);
            this.mDisablePrevVar = new IndexedVariable(this.mName + ".disable_prev", variables, true);
            this.mDisableNextVar = new IndexedVariable(this.mName + ".disable_next", variables, true);
            this.mArtworkVar = new IndexedVariable(this.mName + ".artwork", variables, false);
            this.mPlayerPackageVar = new IndexedVariable(this.mName + ".package", variables, false);
            this.mPlayerClassVar = new IndexedVariable(this.mName + ".class", variables, false);
        }
        this.mNeedUpdateLyric = this.mEnableLyric && this.mHasName;
        if (this.mEnableProgress && this.mHasName) {
            z = true;
        }
        this.mNeedUpdateProgress = z;
        this.mNeedUpdateUserRating = this.mHasName;
        try {
            this.mMiuiMusicContext = getContext().mContext.createPackageContext(MIUI_PLAYER_PACKAGE_NAME, 2);
        } catch (Exception e) {
            Log.w(LOG_TAG, "fail to get MiuiMusic preference", e);
        }
        this.mMusicController = new MusicController(getContext().mContext, getContext().getHandler());
        String rootTag = this.mRoot.getRootTag();
        this.mSender = "maml";
        if ("gadget".equalsIgnoreCase(rootTag)) {
            str = "home_widget";
        } else if ("statusbar".equalsIgnoreCase(rootTag)) {
            str = "notification_bar";
        } else if ("lockscreen".equalsIgnoreCase(rootTag)) {
            this.mSender = "lockscreen";
            return;
        } else {
            return;
        }
        this.mSender = str;
    }

    private void cancelSetDefaultArtwork() {
        getContext().getHandler().removeCallbacks(this.mDelayToSetArtworkRunnable);
    }

    /* access modifiers changed from: private */
    public void delayToSetDefaultArtwork(long j) {
        Handler handler = getContext().getHandler();
        handler.removeCallbacks(this.mDelayToSetArtworkRunnable);
        handler.postDelayed(this.mDelayToSetArtworkRunnable, j);
    }

    private SpectrumVisualizerScreenElement findSpectrumVisualizer(ElementGroup elementGroup) {
        SpectrumVisualizerScreenElement findSpectrumVisualizer;
        Iterator<ScreenElement> it = elementGroup.getElements().iterator();
        while (it.hasNext()) {
            ScreenElement next = it.next();
            if (next instanceof SpectrumVisualizerScreenElement) {
                return (SpectrumVisualizerScreenElement) next;
            }
            if ((next instanceof ElementGroup) && (findSpectrumVisualizer = findSpectrumVisualizer((ElementGroup) next)) != null) {
                return findSpectrumVisualizer;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void onMusicStateChange(boolean z) {
        if (z && this.mAutoShow && !isVisible()) {
            show(true);
        }
        this.mPlaying = z;
        IndexedVariable indexedVariable = this.mMusicStateVar;
        if (indexedVariable != null) {
            indexedVariable.set(z ? 1.0d : 0.0d);
        }
        ButtonScreenElement buttonScreenElement = this.mButtonPause;
        if (buttonScreenElement != null) {
            buttonScreenElement.show(z);
        }
        ButtonScreenElement buttonScreenElement2 = this.mButtonPlay;
        if (buttonScreenElement2 != null) {
            buttonScreenElement2.show(!z);
        }
        if (this.mNeedUpdateProgress) {
            startProgressUpdate(z, z ? 100 : 0);
        }
        SpectrumVisualizerScreenElement spectrumVisualizerScreenElement = this.mSpectrumVisualizer;
        if (spectrumVisualizerScreenElement != null) {
            spectrumVisualizerScreenElement.enableUpdate(z);
        }
        requestFramerate((!z || this.mLyric == null) ? 0.0f : 30.0f);
        requestUpdate();
        Log.d(LOG_TAG, "music state change: playing=" + z);
    }

    /* access modifiers changed from: private */
    public void resetAll() {
        updateAlbum((String) null, (String) null, (String) null);
        resetProgress();
        resetLyric();
        resetUserRating();
        updateArtwork(this.mDefaultAlbumCoverBm);
        resetPackageName();
        resetMusicState();
    }

    private void resetLyric() {
        if (this.mNeedUpdateLyric) {
            this.mLyricBeforeVar.set((Object) null);
            this.mLyricAfterVar.set((Object) null);
            this.mLyricLastVar.set((Object) null);
            this.mLyricPrevVar.set((Object) null);
            this.mLyricNextVar.set((Object) null);
            this.mLyricCurrentVar.set((Object) null);
        }
    }

    private void resetMusicState() {
        onMusicStateChange(false);
    }

    private void resetPackageName() {
        IndexedVariable indexedVariable = this.mPlayerPackageVar;
        if (indexedVariable != null) {
            indexedVariable.set((Object) null);
        }
        IndexedVariable indexedVariable2 = this.mPlayerClassVar;
        if (indexedVariable2 != null) {
            indexedVariable2.set((Object) null);
        }
    }

    private void resetProgress() {
        if (this.mNeedUpdateProgress) {
            this.mDurationVar.set(0.0d);
            this.mPositionVar.set(0.0d);
        }
        if (this.mNeedUpdateLyric) {
            this.mLyricCurrentLineProgressVar.set(0.0d);
        }
    }

    /* access modifiers changed from: private */
    public void resetUserRating() {
        if (this.mNeedUpdateUserRating) {
            this.mUserRatingStyle = 0;
            this.mUserRatingValue = 0.0f;
            this.mUserRatingStyleVar.set(0.0d);
            this.mUserRatingValueVar.set(0.0d);
        }
    }

    private boolean sendMediaKeyEvent(int i, String str) {
        int i2 = BUTTON_MUSIC_PREV.equals(str) ? 88 : BUTTON_MUSIC_NEXT.equals(str) ? 87 : (BUTTON_MUSIC_PLAY.equals(str) || BUTTON_MUSIC_PAUSE.equals(str)) ? 85 : 0;
        if (i2 == 88 && this.mDisablePrev) {
            return false;
        }
        if (i2 == 87 && this.mDisableNext) {
            return false;
        }
        if (i2 == 85 && this.mDisablePlay) {
            return false;
        }
        if (this.mMusicController.sendMediaKeyEvent(i, i2)) {
            return true;
        }
        Log.d(LOG_TAG, "fail to dispatch by media controller, send to MiuiMusic.");
        if (i == 0) {
            return true;
        }
        Intent intent = null;
        if (BUTTON_MUSIC_PLAY.equals(str) || BUTTON_MUSIC_PAUSE.equals(str)) {
            intent = new Intent("com.miui.player.musicservicecommand.togglepause");
        } else if (BUTTON_MUSIC_PREV.equals(str)) {
            intent = new Intent("com.miui.player.musicservicecommand.previous");
        } else if (BUTTON_MUSIC_NEXT.equals(str)) {
            intent = new Intent("com.miui.player.musicservicecommand.next");
        }
        if (intent == null) {
            return false;
        }
        intent.setPackage(MIUI_PLAYER_PACKAGE_NAME);
        intent.putExtra("intent_sender", this.mSender);
        this.mRoot.getContext().mContext.startService(intent);
        getContext().getHandler().postDelayed(this.mResetMusicControllerRunable, 1000);
        return true;
    }

    private void setupButton(ButtonScreenElement buttonScreenElement) {
        if (buttonScreenElement != null) {
            buttonScreenElement.setListener(this);
            buttonScreenElement.setParent(this);
        }
    }

    private void startProgressUpdate(boolean z, long j) {
        getContext().getHandler().removeCallbacks(this.mUpdateProgressRunnable);
        if (!z) {
            return;
        }
        if (j > 0) {
            getContext().getHandler().postDelayed(this.mUpdateProgressRunnable, j);
        } else {
            getContext().getHandler().post(this.mUpdateProgressRunnable);
        }
    }

    /* access modifiers changed from: private */
    public void updateAlbum(String str, String str2, String str3) {
        if (this.mHasName) {
            this.mTitleVar.set((Object) str);
            this.mArtistVar.set((Object) str2);
            this.mAlbumVar.set((Object) str3);
        }
        if (this.mTextDisplay != null) {
            if (TextUtils.isEmpty(str)) {
                str = str2;
            } else if (!TextUtils.isEmpty(str2)) {
                str = String.format("%s   %s", new Object[]{str, str2});
            }
            this.mTextDisplay.setText(str);
        }
        requestUpdate();
    }

    /* access modifiers changed from: private */
    public void updateArtwork(Bitmap bitmap) {
        cancelSetDefaultArtwork();
        if (this.mHasName) {
            this.mArtworkVar.set((Object) bitmap);
        }
        ImageScreenElement imageScreenElement = this.mImageAlbumCover;
        if (imageScreenElement != null) {
            imageScreenElement.setBitmap(bitmap);
        }
        requestUpdate();
    }

    /* access modifiers changed from: private */
    public void updateLyric(MusicLyricParser.Lyric lyric) {
        if (this.mNeedUpdateLyric) {
            if (lyric == null) {
                resetLyric();
                return;
            }
            int[] timeArr = lyric.getTimeArr();
            ArrayList<CharSequence> stringArr = lyric.getStringArr();
            MusicLyricParser.Lyric lyric2 = this.mLyric;
            if (lyric2 != null) {
                lyric2.set(timeArr, stringArr);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateLyricVar(long j) {
        this.mLyricCurrentLineProgressVar.set(this.mLyric.getLyricShot(j).percent);
        this.mLyricCurrentVar.set((Object) this.mLyric.getLine(j));
        this.mLyricBeforeVar.set((Object) this.mLyric.getBeforeLines(j));
        this.mLyricAfterVar.set((Object) this.mLyric.getAfterLines(j));
        String lastLine = this.mLyric.getLastLine(j);
        this.mLyricLastVar.set((Object) lastLine);
        this.mLyricPrevVar.set((Object) lastLine);
        this.mLyricNextVar.set((Object) this.mLyric.getNextLine(j));
    }

    /* access modifiers changed from: private */
    public void updateProgress(long j, long j2) {
        if (this.mNeedUpdateProgress) {
            if (j <= 0 || j2 < 0) {
                resetProgress();
                return;
            }
            this.mDurationVar.set((double) j);
            this.mPositionVar.set((double) j2);
            if (this.mNeedUpdateLyric) {
                if (this.mLyric != null) {
                    updateLyricVar(j2);
                } else {
                    resetLyric();
                }
            }
            requestUpdate();
            startProgressUpdate(this.mPlaying, 0);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0025, code lost:
        r3.mUserRatingValue = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        if (r4.isThumbUp() != false) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0033, code lost:
        if (r4.hasHeart() != false) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0036, code lost:
        r1 = 0.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0037, code lost:
        r3.mUserRatingValue = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0039, code lost:
        r3.mUserRatingStyleVar.set((double) r3.mUserRatingStyle);
        r3.mUserRatingValueVar.set((double) r3.mUserRatingValue);
        requestUpdate();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateUserRating(android.media.Rating r4) {
        /*
            r3 = this;
            boolean r0 = r3.mNeedUpdateUserRating
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            if (r4 != 0) goto L_0x000b
            r3.resetUserRating()
            return
        L_0x000b:
            int r0 = r4.getRatingStyle()
            r3.mUserRatingStyle = r0
            int r0 = r3.mUserRatingStyle
            r1 = 1065353216(0x3f800000, float:1.0)
            r2 = 0
            switch(r0) {
                case 1: goto L_0x002f;
                case 2: goto L_0x0028;
                case 3: goto L_0x0021;
                case 4: goto L_0x0021;
                case 5: goto L_0x0021;
                case 6: goto L_0x001c;
                default: goto L_0x0019;
            }
        L_0x0019:
            r3.mUserRatingValue = r2
            goto L_0x0039
        L_0x001c:
            float r4 = r4.getPercentRating()
            goto L_0x0025
        L_0x0021:
            float r4 = r4.getStarRating()
        L_0x0025:
            r3.mUserRatingValue = r4
            goto L_0x0039
        L_0x0028:
            boolean r4 = r4.isThumbUp()
            if (r4 == 0) goto L_0x0036
            goto L_0x0037
        L_0x002f:
            boolean r4 = r4.hasHeart()
            if (r4 == 0) goto L_0x0036
            goto L_0x0037
        L_0x0036:
            r1 = r2
        L_0x0037:
            r3.mUserRatingValue = r1
        L_0x0039:
            com.miui.maml.data.IndexedVariable r4 = r3.mUserRatingStyleVar
            int r0 = r3.mUserRatingStyle
            double r0 = (double) r0
            r4.set((double) r0)
            com.miui.maml.data.IndexedVariable r4 = r3.mUserRatingValueVar
            float r0 = r3.mUserRatingValue
            double r0 = (double) r0
            r4.set((double) r0)
            r3.requestUpdate()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.MusicControlScreenElement.updateUserRating(android.media.Rating):void");
    }

    public void finish() {
        super.finish();
        IndexedVariable indexedVariable = this.mArtworkVar;
        if (indexedVariable != null) {
            indexedVariable.set((Object) null);
        }
        this.mMusicController.unregisterListener();
        this.mMusicController.finish();
        Handler handler = getContext().getHandler();
        handler.removeCallbacks(this.mUpdateProgressRunnable);
        handler.removeCallbacks(this.mDelayToSetArtworkRunnable);
        handler.removeCallbacks(this.mResetMusicControllerRunable);
    }

    public void init() {
        super.init();
        initByPreference();
        this.mMusicController.registerListener(this.mMusicUpdateListener);
        if (this.mMusicController.isMusicActive()) {
            if (this.mAutoShow) {
                show(true);
            }
            onMusicStateChange(true);
            return;
        }
        onMusicStateChange(false);
    }

    public void initByPreference() {
        SharedPreferences sharedPreferences;
        Context context = this.mMiuiMusicContext;
        if (context != null) {
            try {
                sharedPreferences = context.getSharedPreferences("MiuiMusic", 4);
            } catch (IllegalStateException unused) {
                sharedPreferences = null;
            }
            if (sharedPreferences != null) {
                updateAlbum(sharedPreferences.getString("songName", (String) null), sharedPreferences.getString("artistName", (String) null), sharedPreferences.getString("albumName", (String) null));
                updateArtwork(this.mDefaultAlbumCoverBm);
            }
        }
    }

    public boolean onButtonDoubleClick(String str) {
        return false;
    }

    public boolean onButtonDown(String str) {
        return sendMediaKeyEvent(0, str);
    }

    public boolean onButtonLongClick(String str) {
        return false;
    }

    public boolean onButtonUp(String str) {
        if (!sendMediaKeyEvent(1, str)) {
            return false;
        }
        if (BUTTON_MUSIC_PREV.equals(str) || BUTTON_MUSIC_NEXT.equals(str)) {
            cancelSetDefaultArtwork();
            getContext().getHandler().removeCallbacks(this.mUpdateProgressRunnable);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        if (z) {
            requestUpdate();
        } else {
            requestFramerate(0.0f);
        }
    }

    public void pause() {
        super.pause();
    }

    public void ratingHeart() {
        if (this.mUserRatingStyle == 1) {
            Rating newHeartRating = Rating.newHeartRating(!(this.mUserRatingValue == 1.0f));
            this.mMusicController.rating(newHeartRating);
            updateUserRating(newHeartRating);
        }
    }

    /* access modifiers changed from: protected */
    public void readPackageName() {
        if (this.mPlayerPackageVar != null && this.mPlayerClassVar != null) {
            String clientPackageName = this.mMusicController.getClientPackageName();
            Log.d(LOG_TAG, "readPackage: " + clientPackageName);
            if (clientPackageName != null) {
                Intent launchIntentForPackage = this.mRoot.getContext().mContext.getPackageManager().getLaunchIntentForPackage(clientPackageName);
                if (launchIntentForPackage != null) {
                    ComponentName component = launchIntentForPackage.getComponent();
                    this.mPlayerPackageVar.set((Object) component.getPackageName());
                    this.mPlayerClassVar.set((Object) component.getClassName());
                    return;
                }
                this.mPlayerPackageVar.set((Object) clientPackageName);
                this.mPlayerClassVar.set((Object) null);
                Log.w(LOG_TAG, "set player info fail.");
            }
        }
    }

    public void resume() {
        super.resume();
        requestUpdate();
    }

    public void seekTo(double d2) {
        this.mMusicController.seekTo((long) (d2 * this.mDurationVar.getDouble()));
    }
}
