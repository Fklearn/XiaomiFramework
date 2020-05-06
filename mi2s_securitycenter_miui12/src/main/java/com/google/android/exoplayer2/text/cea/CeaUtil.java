package com.google.android.exoplayer2.text.cea;

import android.util.Log;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;

public final class CeaUtil {
    private static final int COUNTRY_CODE = 181;
    private static final int PAYLOAD_TYPE_CC = 4;
    private static final int PROVIDER_CODE_ATSC = 49;
    private static final int PROVIDER_CODE_DIRECTV = 47;
    private static final String TAG = "CeaUtil";
    private static final int USER_DATA_TYPE_CODE = 3;
    private static final int USER_ID_DTG1 = Util.getIntegerCodeForString("DTG1");
    private static final int USER_ID_GA94 = Util.getIntegerCodeForString("GA94");

    private CeaUtil() {
    }

    public static void consume(long j, ParsableByteArray parsableByteArray, TrackOutput[] trackOutputArr) {
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        TrackOutput[] trackOutputArr2 = trackOutputArr;
        while (parsableByteArray.bytesLeft() > 1) {
            int readNon255TerminatedValue = readNon255TerminatedValue(parsableByteArray);
            int readNon255TerminatedValue2 = readNon255TerminatedValue(parsableByteArray);
            int position = parsableByteArray.getPosition() + readNon255TerminatedValue2;
            if (readNon255TerminatedValue2 == -1 || readNon255TerminatedValue2 > parsableByteArray.bytesLeft()) {
                Log.w(TAG, "Skipping remainder of malformed SEI NAL unit.");
                position = parsableByteArray.limit();
            } else if (readNon255TerminatedValue == 4 && readNon255TerminatedValue2 >= 8) {
                int readUnsignedByte = parsableByteArray.readUnsignedByte();
                int readUnsignedShort = parsableByteArray.readUnsignedShort();
                int readInt = readUnsignedShort == 49 ? parsableByteArray.readInt() : 0;
                int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                if (readUnsignedShort == 47) {
                    parsableByteArray2.skipBytes(1);
                }
                boolean z = readUnsignedByte == COUNTRY_CODE && (readUnsignedShort == 49 || readUnsignedShort == 47) && readUnsignedByte2 == 3;
                if (readUnsignedShort == 49) {
                    z &= readInt == USER_ID_GA94 || readInt == USER_ID_DTG1;
                }
                if (z) {
                    parsableByteArray2.skipBytes(1);
                    int readUnsignedByte3 = (parsableByteArray.readUnsignedByte() & 31) * 3;
                    int position2 = parsableByteArray.getPosition();
                    for (TrackOutput trackOutput : trackOutputArr2) {
                        parsableByteArray2.setPosition(position2);
                        trackOutput.sampleData(parsableByteArray2, readUnsignedByte3);
                        trackOutput.sampleMetadata(j, 1, readUnsignedByte3, 0, (TrackOutput.CryptoData) null);
                    }
                }
            }
            parsableByteArray2.setPosition(position);
        }
    }

    private static int readNon255TerminatedValue(ParsableByteArray parsableByteArray) {
        int i = 0;
        while (parsableByteArray.bytesLeft() != 0) {
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            i += readUnsignedByte;
            if (readUnsignedByte != 255) {
                return i;
            }
        }
        return -1;
    }
}
