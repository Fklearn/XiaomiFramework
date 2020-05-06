package com.miui.warningcenter.mijia;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

public class MijiaPlaySound {
    private Context mContext;
    /* access modifiers changed from: private */
    public int mSoundId;
    private SoundPool mSoundPool;

    public MijiaPlaySound(Context context) {
        this.mContext = context;
    }

    public void playSound(int i) {
        SoundPool soundPool;
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);
            AudioAttributes.Builder builder2 = new AudioAttributes.Builder();
            builder2.setLegacyStreamType(3);
            builder.setAudioAttributes(builder2.build());
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(1, 1, 5);
        }
        this.mSoundPool = soundPool;
        this.mSoundPool.load(this.mContext, i, 1);
        this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                int unused = MijiaPlaySound.this.mSoundId = soundPool.play(1, 1.0f, 1.0f, 0, -1, 1.0f);
            }
        });
    }

    public void release() {
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null) {
            soundPool.release();
        }
    }

    public void stop() {
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null) {
            soundPool.stop(this.mSoundId);
        }
    }
}
