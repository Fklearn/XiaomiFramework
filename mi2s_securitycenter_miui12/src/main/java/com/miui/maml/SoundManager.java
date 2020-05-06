package com.miui.maml;

import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.MemoryFile;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.permission.PermissionManager;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import miui.content.res.ThemeResources;
import miui.util.IOUtils;

public class SoundManager implements SoundPool.OnLoadCompleteListener {
    private static final String ADVANCE = "advance/";
    private static final String LOCKSCREEN_AUDIO = "lockscreen_audio/";
    private static final String LOG_TAG = "MamlSoundManager";
    private static final int MAX_FILE_SIZE = 524288;
    private static final int MAX_STREAMS = 8;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public Object mInitSignal = new Object();
    /* access modifiers changed from: private */
    public boolean mInitialized;
    private HashMap<Integer, SoundOptions> mPendingSoundMap = new HashMap<>();
    private ArrayList<Integer> mPlayingSoundMap = new ArrayList<>();
    private ResourceManager mResourceManager;
    /* access modifiers changed from: private */
    public SoundPool mSoundPool;
    private HashMap<String, Integer> mSoundPoolMap = new HashMap<>();

    /* renamed from: com.miui.maml.SoundManager$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$SoundManager$Command = new int[Command.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        static {
            /*
                com.miui.maml.SoundManager$Command[] r0 = com.miui.maml.SoundManager.Command.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$com$miui$maml$SoundManager$Command = r0
                int[] r0 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.miui.maml.SoundManager$Command r1 = com.miui.maml.SoundManager.Command.Play     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x001f }
                com.miui.maml.SoundManager$Command r1 = com.miui.maml.SoundManager.Command.Pause     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x002a }
                com.miui.maml.SoundManager$Command r1 = com.miui.maml.SoundManager.Command.Resume     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ NoSuchFieldError -> 0x0035 }
                com.miui.maml.SoundManager$Command r1 = com.miui.maml.SoundManager.Command.Stop     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.SoundManager.AnonymousClass2.<clinit>():void");
        }
    }

    public enum Command {
        Play,
        Pause,
        Resume,
        Stop;

        public static Command parse(String str) {
            return "pause".equals(str) ? Pause : "resume".equals(str) ? Resume : "stop".equals(str) ? Stop : Play;
        }
    }

    public static class SoundOptions {
        public boolean mKeepCur;
        public boolean mLoop;
        public float mVolume;

        public SoundOptions(boolean z, boolean z2, float f) {
            this.mKeepCur = z;
            this.mLoop = z2;
            float f2 = 0.0f;
            if (f >= 0.0f) {
                f2 = 1.0f;
                if (f <= 1.0f) {
                    this.mVolume = f;
                    return;
                }
            }
            this.mVolume = f2;
        }
    }

    public SoundManager(ScreenContext screenContext) {
        this.mResourceManager = screenContext.mResourceManager;
        this.mHandler = screenContext.getHandler();
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x0041 */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0041 A[LOOP:0: B:8:0x0041->B:18:0x0041, LOOP_START, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void init() {
        /*
            r4 = this;
            boolean r0 = r4.mInitialized
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            long r0 = r0.getId()
            android.os.Handler r2 = r4.mHandler
            android.os.Looper r2 = r2.getLooper()
            java.lang.Thread r2 = r2.getThread()
            long r2 = r2.getId()
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0034
            android.media.SoundPool r0 = new android.media.SoundPool
            r1 = 8
            r2 = 3
            r3 = 100
            r0.<init>(r1, r2, r3)
            r4.mSoundPool = r0
            android.media.SoundPool r0 = r4.mSoundPool
            r0.setOnLoadCompleteListener(r4)
            r0 = 1
            r4.mInitialized = r0
            goto L_0x0046
        L_0x0034:
            android.os.Handler r0 = r4.mHandler
            com.miui.maml.SoundManager$1 r1 = new com.miui.maml.SoundManager$1
            r1.<init>()
            r0.post(r1)
            java.lang.Object r0 = r4.mInitSignal
            monitor-enter(r0)
        L_0x0041:
            boolean r1 = r4.mInitialized     // Catch:{ all -> 0x004d }
            if (r1 == 0) goto L_0x0047
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
        L_0x0046:
            return
        L_0x0047:
            java.lang.Object r1 = r4.mInitSignal     // Catch:{ InterruptedException -> 0x0041 }
            r1.wait()     // Catch:{ InterruptedException -> 0x0041 }
            goto L_0x0041
        L_0x004d:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004d }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.SoundManager.init():void");
    }

    private synchronized int playSoundImp(int i, SoundOptions soundOptions) {
        int play;
        if (this.mSoundPool == null) {
            return 0;
        }
        if (!soundOptions.mKeepCur) {
            stopAllPlaying();
        }
        try {
            synchronized (this.mPlayingSoundMap) {
                play = this.mSoundPool.play(i, soundOptions.mVolume, soundOptions.mVolume, 1, soundOptions.mLoop ? -1 : 0, 1.0f);
                this.mPlayingSoundMap.add(Integer.valueOf(play));
            }
            return play;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.toString());
            return 0;
        }
    }

    public void onLoadComplete(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            playSoundImp(i, this.mPendingSoundMap.get(Integer.valueOf(i)));
        }
        this.mPendingSoundMap.remove(Integer.valueOf(i));
    }

    public void pause() {
        stopAllPlaying();
    }

    public synchronized int playSound(String str, SoundOptions soundOptions) {
        Integer num;
        if (!this.mInitialized) {
            init();
        }
        if (this.mSoundPool == null) {
            return 0;
        }
        Integer num2 = this.mSoundPoolMap.get(str);
        if (num2 == null) {
            if (Build.VERSION.SDK_INT < 26) {
                MemoryFile file = this.mResourceManager.getFile(str);
                if (file == null) {
                    Log.e(LOG_TAG, "the sound does not exist: " + str);
                    return 0;
                } else if (file.length() > MAX_FILE_SIZE) {
                    Log.w(LOG_TAG, String.format("the sound file is larger than %d KB: %s", new Object[]{512, str}));
                    return 0;
                } else {
                    num = Integer.valueOf(this.mSoundPool.load(HideSdkDependencyUtils.MemoryFile_getFileDescriptor(file), 0, (long) file.length(), 1));
                    this.mSoundPoolMap.put(str, num);
                    file.close();
                }
            } else {
                File file2 = new File(ThemeResources.THEME_MAGIC_PATH + LOCKSCREEN_AUDIO + ADVANCE + str);
                if (!file2.exists()) {
                    Log.e(LOG_TAG, "the sound does not exist: " + str);
                    return 0;
                } else if (file2.length() > PermissionManager.PERM_ID_SENDMMS) {
                    Log.w(LOG_TAG, String.format("the sound file is larger than %d KB: %s", new Object[]{512, str}));
                    return 0;
                } else {
                    ParcelFileDescriptor parcelFileDescriptor = null;
                    try {
                        parcelFileDescriptor = ParcelFileDescriptor.open(file2, 268435456);
                        if (parcelFileDescriptor != null) {
                            num2 = Integer.valueOf(this.mSoundPool.load(parcelFileDescriptor.getFileDescriptor(), 0, file2.length(), 1));
                            this.mSoundPoolMap.put(str, num2);
                        }
                        num = num2;
                    } catch (IOException e) {
                        num = num2;
                        try {
                            Log.e(LOG_TAG, "fail to load sound. ", e);
                        } catch (Throwable th) {
                            IOUtils.closeQuietly((Closeable) null);
                            throw th;
                        }
                    }
                    IOUtils.closeQuietly(parcelFileDescriptor);
                }
            }
            this.mPendingSoundMap.put(num, soundOptions);
            return 0;
        }
        return playSoundImp(num2.intValue(), soundOptions);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0046, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0048, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void playSound(int r2, com.miui.maml.SoundManager.Command r3) {
        /*
            r1 = this;
            monitor-enter(r1)
            boolean r0 = r1.mInitialized     // Catch:{ all -> 0x0049 }
            if (r0 != 0) goto L_0x0008
            r1.init()     // Catch:{ all -> 0x0049 }
        L_0x0008:
            android.media.SoundPool r0 = r1.mSoundPool     // Catch:{ all -> 0x0049 }
            if (r0 == 0) goto L_0x0047
            if (r2 > 0) goto L_0x000f
            goto L_0x0047
        L_0x000f:
            int[] r0 = com.miui.maml.SoundManager.AnonymousClass2.$SwitchMap$com$miui$maml$SoundManager$Command     // Catch:{ all -> 0x0049 }
            int r3 = r3.ordinal()     // Catch:{ all -> 0x0049 }
            r3 = r0[r3]     // Catch:{ all -> 0x0049 }
            r0 = 1
            if (r3 == r0) goto L_0x0045
            r0 = 2
            if (r3 == r0) goto L_0x0040
            r0 = 3
            if (r3 == r0) goto L_0x003a
            r0 = 4
            if (r3 == r0) goto L_0x0024
            goto L_0x0045
        L_0x0024:
            android.media.SoundPool r3 = r1.mSoundPool     // Catch:{ all -> 0x0049 }
            r3.stop(r2)     // Catch:{ all -> 0x0049 }
            java.util.ArrayList<java.lang.Integer> r3 = r1.mPlayingSoundMap     // Catch:{ all -> 0x0049 }
            monitor-enter(r3)     // Catch:{ all -> 0x0049 }
            java.util.ArrayList<java.lang.Integer> r0 = r1.mPlayingSoundMap     // Catch:{ all -> 0x0037 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0037 }
            r0.remove(r2)     // Catch:{ all -> 0x0037 }
            monitor-exit(r3)     // Catch:{ all -> 0x0037 }
            goto L_0x0045
        L_0x0037:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0037 }
            throw r2     // Catch:{ all -> 0x0049 }
        L_0x003a:
            android.media.SoundPool r3 = r1.mSoundPool     // Catch:{ all -> 0x0049 }
            r3.resume(r2)     // Catch:{ all -> 0x0049 }
            goto L_0x0045
        L_0x0040:
            android.media.SoundPool r3 = r1.mSoundPool     // Catch:{ all -> 0x0049 }
            r3.pause(r2)     // Catch:{ all -> 0x0049 }
        L_0x0045:
            monitor-exit(r1)
            return
        L_0x0047:
            monitor-exit(r1)
            return
        L_0x0049:
            r2 = move-exception
            monitor-exit(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.SoundManager.playSound(int, com.miui.maml.SoundManager$Command):void");
    }

    public synchronized void release() {
        if (this.mInitialized) {
            stopAllPlaying();
            if (this.mSoundPool != null) {
                this.mSoundPoolMap.clear();
                this.mSoundPool.setOnLoadCompleteListener((SoundPool.OnLoadCompleteListener) null);
                this.mSoundPool.release();
                this.mSoundPool = null;
            }
            this.mInitialized = false;
        }
    }

    /* access modifiers changed from: protected */
    public void stopAllPlaying() {
        if (!this.mPlayingSoundMap.isEmpty()) {
            synchronized (this.mPlayingSoundMap) {
                if (this.mSoundPool != null) {
                    Iterator<Integer> it = this.mPlayingSoundMap.iterator();
                    while (it.hasNext()) {
                        this.mSoundPool.stop(it.next().intValue());
                    }
                }
                this.mPlayingSoundMap.clear();
            }
        }
    }
}
