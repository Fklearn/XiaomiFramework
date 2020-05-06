package com.google.android.exoplayer2.audio;

import com.google.android.exoplayer2.util.Assertions;
import java.nio.ShortBuffer;
import java.util.Arrays;

final class Sonic {
    private static final int AMDF_FREQUENCY = 4000;
    private static final int MAXIMUM_PITCH = 400;
    private static final int MINIMUM_PITCH = 65;
    private final int channelCount;
    private final short[] downSampleBuffer;
    private short[] inputBuffer;
    private int inputFrameCount;
    private final int inputSampleRateHz;
    private int maxDiff;
    private final int maxPeriod;
    private final int maxRequiredFrameCount = (this.maxPeriod * 2);
    private int minDiff;
    private final int minPeriod;
    private int newRatePosition;
    private int oldRatePosition;
    private short[] outputBuffer;
    private int outputFrameCount;
    private final float pitch;
    private short[] pitchBuffer;
    private int pitchFrameCount;
    private int prevMinDiff;
    private int prevPeriod;
    private final float rate;
    private int remainingInputToCopyFrameCount;
    private final float speed;

    public Sonic(int i, int i2, float f, float f2, int i3) {
        this.inputSampleRateHz = i;
        this.channelCount = i2;
        this.speed = f;
        this.pitch = f2;
        this.rate = ((float) i) / ((float) i3);
        this.minPeriod = i / MAXIMUM_PITCH;
        this.maxPeriod = i / 65;
        int i4 = this.maxRequiredFrameCount;
        this.downSampleBuffer = new short[i4];
        this.inputBuffer = new short[(i4 * i2)];
        this.outputBuffer = new short[(i4 * i2)];
        this.pitchBuffer = new short[(i4 * i2)];
    }

    private void adjustRate(float f, int i) {
        int i2;
        int i3;
        if (this.outputFrameCount != i) {
            int i4 = this.inputSampleRateHz;
            int i5 = (int) (((float) i4) / f);
            while (true) {
                if (i5 <= 16384 && i4 <= 16384) {
                    break;
                }
                i5 /= 2;
                i4 /= 2;
            }
            moveNewSamplesToPitchBuffer(i);
            int i6 = 0;
            while (true) {
                int i7 = this.pitchFrameCount;
                boolean z = true;
                if (i6 < i7 - 1) {
                    while (true) {
                        i2 = this.oldRatePosition;
                        int i8 = (i2 + 1) * i5;
                        i3 = this.newRatePosition;
                        if (i8 <= i3 * i4) {
                            break;
                        }
                        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, 1);
                        int i9 = 0;
                        while (true) {
                            int i10 = this.channelCount;
                            if (i9 >= i10) {
                                break;
                            }
                            this.outputBuffer[(this.outputFrameCount * i10) + i9] = interpolate(this.pitchBuffer, (i10 * i6) + i9, i4, i5);
                            i9++;
                        }
                        this.newRatePosition++;
                        this.outputFrameCount++;
                    }
                    this.oldRatePosition = i2 + 1;
                    if (this.oldRatePosition == i4) {
                        this.oldRatePosition = 0;
                        if (i3 != i5) {
                            z = false;
                        }
                        Assertions.checkState(z);
                        this.newRatePosition = 0;
                    }
                    i6++;
                } else {
                    removePitchFrames(i7 - 1);
                    return;
                }
            }
        }
    }

    private void changeSpeed(float f) {
        int skipPitchPeriod;
        int i = this.inputFrameCount;
        if (i >= this.maxRequiredFrameCount) {
            int i2 = 0;
            do {
                if (this.remainingInputToCopyFrameCount > 0) {
                    skipPitchPeriod = copyInputToOutput(i2);
                } else {
                    int findPitchPeriod = findPitchPeriod(this.inputBuffer, i2);
                    skipPitchPeriod = ((double) f) > 1.0d ? findPitchPeriod + skipPitchPeriod(this.inputBuffer, i2, f, findPitchPeriod) : insertPitchPeriod(this.inputBuffer, i2, f, findPitchPeriod);
                }
                i2 += skipPitchPeriod;
            } while (this.maxRequiredFrameCount + i2 <= i);
            removeProcessedInputFrames(i2);
        }
    }

    private int copyInputToOutput(int i) {
        int min = Math.min(this.maxRequiredFrameCount, this.remainingInputToCopyFrameCount);
        copyToOutput(this.inputBuffer, i, min);
        this.remainingInputToCopyFrameCount -= min;
        return min;
    }

    private void copyToOutput(short[] sArr, int i, int i2) {
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, i2);
        int i3 = this.channelCount;
        System.arraycopy(sArr, i * i3, this.outputBuffer, this.outputFrameCount * i3, i3 * i2);
        this.outputFrameCount += i2;
    }

    private void downSampleInput(short[] sArr, int i, int i2) {
        int i3 = this.maxRequiredFrameCount / i2;
        int i4 = this.channelCount;
        int i5 = i2 * i4;
        int i6 = i * i4;
        for (int i7 = 0; i7 < i3; i7++) {
            int i8 = 0;
            for (int i9 = 0; i9 < i5; i9++) {
                i8 += sArr[(i7 * i5) + i6 + i9];
            }
            this.downSampleBuffer[i7] = (short) (i8 / i5);
        }
    }

    private short[] ensureSpaceForAdditionalFrames(short[] sArr, int i, int i2) {
        int length = sArr.length;
        int i3 = this.channelCount;
        int i4 = length / i3;
        return i + i2 <= i4 ? sArr : Arrays.copyOf(sArr, (((i4 * 3) / 2) + i2) * i3);
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0059  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int findPitchPeriod(short[] r7, int r8) {
        /*
            r6 = this;
            int r0 = r6.inputSampleRateHz
            r1 = 4000(0xfa0, float:5.605E-42)
            r2 = 1
            if (r0 <= r1) goto L_0x0009
            int r0 = r0 / r1
            goto L_0x000a
        L_0x0009:
            r0 = r2
        L_0x000a:
            int r1 = r6.channelCount
            if (r1 != r2) goto L_0x0019
            if (r0 != r2) goto L_0x0019
            int r0 = r6.minPeriod
            int r1 = r6.maxPeriod
        L_0x0014:
            int r7 = r6.findPitchPeriodInRange(r7, r8, r0, r1)
            goto L_0x004c
        L_0x0019:
            r6.downSampleInput(r7, r8, r0)
            short[] r1 = r6.downSampleBuffer
            int r3 = r6.minPeriod
            int r3 = r3 / r0
            int r4 = r6.maxPeriod
            int r4 = r4 / r0
            r5 = 0
            int r1 = r6.findPitchPeriodInRange(r1, r5, r3, r4)
            if (r0 == r2) goto L_0x004b
            int r1 = r1 * r0
            int r0 = r0 * 4
            int r3 = r1 - r0
            int r1 = r1 + r0
            int r0 = r6.minPeriod
            if (r3 >= r0) goto L_0x0036
            goto L_0x0037
        L_0x0036:
            r0 = r3
        L_0x0037:
            int r3 = r6.maxPeriod
            if (r1 <= r3) goto L_0x003c
            r1 = r3
        L_0x003c:
            int r3 = r6.channelCount
            if (r3 != r2) goto L_0x0041
            goto L_0x0014
        L_0x0041:
            r6.downSampleInput(r7, r8, r2)
            short[] r7 = r6.downSampleBuffer
            int r7 = r6.findPitchPeriodInRange(r7, r5, r0, r1)
            goto L_0x004c
        L_0x004b:
            r7 = r1
        L_0x004c:
            int r8 = r6.minDiff
            int r0 = r6.maxDiff
            boolean r8 = r6.previousPeriodBetter(r8, r0)
            if (r8 == 0) goto L_0x0059
            int r8 = r6.prevPeriod
            goto L_0x005a
        L_0x0059:
            r8 = r7
        L_0x005a:
            int r0 = r6.minDiff
            r6.prevMinDiff = r0
            r6.prevPeriod = r7
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.audio.Sonic.findPitchPeriod(short[], int):int");
    }

    private int findPitchPeriodInRange(short[] sArr, int i, int i2, int i3) {
        int i4 = i * this.channelCount;
        int i5 = 1;
        int i6 = 0;
        int i7 = 255;
        int i8 = 0;
        while (i2 <= i3) {
            int i9 = 0;
            for (int i10 = 0; i10 < i2; i10++) {
                i9 += Math.abs(sArr[i4 + i10] - sArr[(i4 + i2) + i10]);
            }
            if (i9 * i8 < i5 * i2) {
                i8 = i2;
                i5 = i9;
            }
            if (i9 * i7 > i6 * i2) {
                i7 = i2;
                i6 = i9;
            }
            i2++;
        }
        this.minDiff = i5 / i8;
        this.maxDiff = i6 / i7;
        return i8;
    }

    private int insertPitchPeriod(short[] sArr, int i, float f, int i2) {
        int i3;
        if (f < 0.5f) {
            i3 = (int) ((((float) i2) * f) / (1.0f - f));
        } else {
            this.remainingInputToCopyFrameCount = (int) ((((float) i2) * ((2.0f * f) - 1.0f)) / (1.0f - f));
            i3 = i2;
        }
        int i4 = i2 + i3;
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, i4);
        int i5 = this.channelCount;
        System.arraycopy(sArr, i * i5, this.outputBuffer, this.outputFrameCount * i5, i5 * i2);
        overlapAdd(i3, this.channelCount, this.outputBuffer, this.outputFrameCount + i2, sArr, i + i2, sArr, i);
        this.outputFrameCount += i4;
        return i3;
    }

    private short interpolate(short[] sArr, int i, int i2, int i3) {
        short s = sArr[i];
        short s2 = sArr[i + this.channelCount];
        int i4 = this.newRatePosition * i2;
        int i5 = this.oldRatePosition;
        int i6 = i5 * i3;
        int i7 = (i5 + 1) * i3;
        int i8 = i7 - i4;
        int i9 = i7 - i6;
        return (short) (((s * i8) + ((i9 - i8) * s2)) / i9);
    }

    private void moveNewSamplesToPitchBuffer(int i) {
        int i2 = this.outputFrameCount - i;
        this.pitchBuffer = ensureSpaceForAdditionalFrames(this.pitchBuffer, this.pitchFrameCount, i2);
        short[] sArr = this.outputBuffer;
        int i3 = this.channelCount;
        System.arraycopy(sArr, i * i3, this.pitchBuffer, this.pitchFrameCount * i3, i3 * i2);
        this.outputFrameCount = i;
        this.pitchFrameCount += i2;
    }

    private static void overlapAdd(int i, int i2, short[] sArr, int i3, short[] sArr2, int i4, short[] sArr3, int i5) {
        for (int i6 = 0; i6 < i2; i6++) {
            int i7 = (i4 * i2) + i6;
            int i8 = (i5 * i2) + i6;
            int i9 = (i3 * i2) + i6;
            for (int i10 = 0; i10 < i; i10++) {
                sArr[i9] = (short) (((sArr2[i7] * (i - i10)) + (sArr3[i8] * i10)) / i);
                i9 += i2;
                i7 += i2;
                i8 += i2;
            }
        }
    }

    private boolean previousPeriodBetter(int i, int i2) {
        return i != 0 && this.prevPeriod != 0 && i2 <= i * 3 && i * 2 > this.prevMinDiff * 3;
    }

    private void processStreamInput() {
        int i = this.outputFrameCount;
        float f = this.speed;
        float f2 = this.pitch;
        float f3 = f / f2;
        float f4 = this.rate * f2;
        double d2 = (double) f3;
        if (d2 > 1.00001d || d2 < 0.99999d) {
            changeSpeed(f3);
        } else {
            copyToOutput(this.inputBuffer, 0, this.inputFrameCount);
            this.inputFrameCount = 0;
        }
        if (f4 != 1.0f) {
            adjustRate(f4, i);
        }
    }

    private void removePitchFrames(int i) {
        if (i != 0) {
            short[] sArr = this.pitchBuffer;
            int i2 = this.channelCount;
            System.arraycopy(sArr, i * i2, sArr, 0, (this.pitchFrameCount - i) * i2);
            this.pitchFrameCount -= i;
        }
    }

    private void removeProcessedInputFrames(int i) {
        int i2 = this.inputFrameCount - i;
        short[] sArr = this.inputBuffer;
        int i3 = this.channelCount;
        System.arraycopy(sArr, i * i3, sArr, 0, i3 * i2);
        this.inputFrameCount = i2;
    }

    private int skipPitchPeriod(short[] sArr, int i, float f, int i2) {
        int i3;
        if (f >= 2.0f) {
            i3 = (int) (((float) i2) / (f - 1.0f));
        } else {
            this.remainingInputToCopyFrameCount = (int) ((((float) i2) * (2.0f - f)) / (f - 1.0f));
            i3 = i2;
        }
        this.outputBuffer = ensureSpaceForAdditionalFrames(this.outputBuffer, this.outputFrameCount, i3);
        overlapAdd(i3, this.channelCount, this.outputBuffer, this.outputFrameCount, sArr, i, sArr, i + i2);
        this.outputFrameCount += i3;
        return i3;
    }

    public void flush() {
        this.inputFrameCount = 0;
        this.outputFrameCount = 0;
        this.pitchFrameCount = 0;
        this.oldRatePosition = 0;
        this.newRatePosition = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.prevPeriod = 0;
        this.prevMinDiff = 0;
        this.minDiff = 0;
        this.maxDiff = 0;
    }

    public int getFramesAvailable() {
        return this.outputFrameCount;
    }

    public void getOutput(ShortBuffer shortBuffer) {
        int min = Math.min(shortBuffer.remaining() / this.channelCount, this.outputFrameCount);
        shortBuffer.put(this.outputBuffer, 0, this.channelCount * min);
        this.outputFrameCount -= min;
        short[] sArr = this.outputBuffer;
        int i = this.channelCount;
        System.arraycopy(sArr, min * i, sArr, 0, this.outputFrameCount * i);
    }

    public void queueEndOfStream() {
        int i;
        int i2 = this.inputFrameCount;
        float f = this.speed;
        float f2 = this.pitch;
        int i3 = this.outputFrameCount + ((int) ((((((float) i2) / (f / f2)) + ((float) this.pitchFrameCount)) / (this.rate * f2)) + 0.5f));
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, i2, (this.maxRequiredFrameCount * 2) + i2);
        int i4 = 0;
        while (true) {
            i = this.maxRequiredFrameCount;
            int i5 = this.channelCount;
            if (i4 >= i * 2 * i5) {
                break;
            }
            this.inputBuffer[(i5 * i2) + i4] = 0;
            i4++;
        }
        this.inputFrameCount += i * 2;
        processStreamInput();
        if (this.outputFrameCount > i3) {
            this.outputFrameCount = i3;
        }
        this.inputFrameCount = 0;
        this.remainingInputToCopyFrameCount = 0;
        this.pitchFrameCount = 0;
    }

    public void queueInput(ShortBuffer shortBuffer) {
        int remaining = shortBuffer.remaining();
        int i = this.channelCount;
        int i2 = remaining / i;
        this.inputBuffer = ensureSpaceForAdditionalFrames(this.inputBuffer, this.inputFrameCount, i2);
        shortBuffer.get(this.inputBuffer, this.inputFrameCount * this.channelCount, ((i * i2) * 2) / 2);
        this.inputFrameCount += i2;
        processStreamInput();
    }
}
