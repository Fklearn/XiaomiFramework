package com.miui.earthquakewarning.soundplay;

import android.content.Context;
import android.media.SoundPool;
import android.os.Handler;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlaySound {
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public int mInterval = 485;
    /* access modifiers changed from: private */
    public List<Integer> mList = new ArrayList();
    private HashMap<Integer, Integer> mMusicId = new HashMap<>();
    /* access modifiers changed from: private */
    public Runnable mRunnable;
    /* access modifiers changed from: private */
    public int mSoundId;
    /* access modifiers changed from: private */
    public SoundPool mSoundPool = new SoundPool(13, 3, 100);

    public PlaySound(Context context) {
        this.mMusicId.put(1, Integer.valueOf(this.mSoundPool.load(context, R.raw.one, 1)));
        this.mMusicId.put(2, Integer.valueOf(this.mSoundPool.load(context, R.raw.two, 1)));
        this.mMusicId.put(3, Integer.valueOf(this.mSoundPool.load(context, R.raw.three, 1)));
        this.mMusicId.put(4, Integer.valueOf(this.mSoundPool.load(context, R.raw.four, 1)));
        this.mMusicId.put(5, Integer.valueOf(this.mSoundPool.load(context, R.raw.five, 1)));
        this.mMusicId.put(6, Integer.valueOf(this.mSoundPool.load(context, R.raw.six, 1)));
        this.mMusicId.put(7, Integer.valueOf(this.mSoundPool.load(context, R.raw.seven, 1)));
        this.mMusicId.put(8, Integer.valueOf(this.mSoundPool.load(context, R.raw.eight, 1)));
        this.mMusicId.put(9, Integer.valueOf(this.mSoundPool.load(context, R.raw.nine, 1)));
        this.mMusicId.put(10, Integer.valueOf(this.mSoundPool.load(context, R.raw.ten, 1)));
        this.mMusicId.put(11, Integer.valueOf(this.mSoundPool.load(context, R.raw.non, 1)));
        this.mMusicId.put(12, Integer.valueOf(this.mSoundPool.load(context, R.raw.di, 1)));
        this.mMusicId.put(13, Integer.valueOf(this.mSoundPool.load(context, R.raw.didi, 1)));
        this.mMusicId.put(14, Integer.valueOf(this.mSoundPool.load(context, R.raw.wu, 1)));
    }

    private void addToPlayList(int i, int i2) {
        List<Integer> list;
        int i3;
        int i4;
        HashMap<Integer, Integer> hashMap;
        List<Integer> list2;
        int i5;
        HashMap<Integer, Integer> hashMap2;
        List<Integer> list3;
        List<Integer> list4;
        int i6;
        this.mList.clear();
        boolean z = false;
        while (i2 > -11) {
            if (i2 <= 0) {
                list = this.mList;
                i3 = this.mMusicId.get(14);
            } else {
                boolean z2 = true;
                if (i2 > 0 && i2 <= 10) {
                    this.mList.add(this.mMusicId.get(Integer.valueOf(i2)));
                    if (i != 0) {
                        if (1 == i) {
                            list = this.mList;
                            i3 = 12;
                        } else {
                            if (2 == i) {
                                this.mList.add(13);
                            }
                            i2--;
                        }
                    }
                } else if (i2 <= 99) {
                    int i7 = i2 / 10;
                    int i8 = i2 % 10;
                    if (z) {
                        if (i == 0) {
                            list4 = this.mList;
                            i6 = 11;
                        } else if (1 == i) {
                            list4 = this.mList;
                            i6 = 12;
                        } else {
                            if (2 == i) {
                                list4 = this.mList;
                                i6 = 13;
                            }
                            this.mList.add(11);
                            z2 = false;
                        }
                        list4.add(i6);
                        this.mList.add(11);
                        z2 = false;
                    } else {
                        if (1 == i7) {
                            list2 = this.mList;
                            hashMap = this.mMusicId;
                            i4 = 10;
                        } else {
                            list2 = this.mList;
                            hashMap = this.mMusicId;
                            i4 = Integer.valueOf(i7);
                        }
                        list2.add(hashMap.get(i4));
                        if (i8 == 0) {
                            list3 = this.mList;
                            hashMap2 = this.mMusicId;
                            i5 = 10;
                        } else {
                            list3 = this.mList;
                            hashMap2 = this.mMusicId;
                            i5 = Integer.valueOf(i8);
                        }
                        list3.add(hashMap2.get(i5));
                    }
                    z = z2;
                    i2--;
                } else {
                    this.mList.add(11);
                }
                list = this.mList;
                i3 = 11;
            }
            list.add(i3);
            i2--;
        }
    }

    private void playSound() {
        this.mRunnable = new Runnable() {
            public void run() {
                long j;
                Handler handler;
                int size = PlaySound.this.mList.size();
                if (size == 0) {
                    PlaySound.this.mHandler.removeCallbacks(PlaySound.this.mRunnable);
                    return;
                }
                PlaySound playSound = PlaySound.this;
                int unused = playSound.mSoundId = playSound.mSoundPool.play(((Integer) PlaySound.this.mList.get(0)).intValue(), 1.0f, 1.0f, 0, 0, 1.0f);
                PlaySound.this.mList.remove(0);
                if (size > 11) {
                    handler = PlaySound.this.mHandler;
                    j = (long) PlaySound.this.mInterval;
                } else {
                    handler = PlaySound.this.mHandler;
                    j = 1280;
                }
                handler.postDelayed(this, j);
            }
        };
        this.mHandler.postDelayed(this.mRunnable, 0);
    }

    public void play(int i, float f) {
        addToPlayList(f <= 3.0f ? 0 : f <= 5.0f ? 1 : 2, i);
        playSound();
    }

    public void release() {
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null) {
            soundPool.release();
        }
    }

    public void stop() {
        this.mList.clear();
        this.mSoundPool.stop(this.mSoundId);
        this.mHandler.removeCallbacks(this.mRunnable);
    }
}
