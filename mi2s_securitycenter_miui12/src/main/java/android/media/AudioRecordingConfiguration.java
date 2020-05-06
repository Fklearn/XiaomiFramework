package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.PrintWriter;
import java.util.Objects;

public final class AudioRecordingConfiguration implements Parcelable {
    public static final Parcelable.Creator<AudioRecordingConfiguration> CREATOR = new Parcelable.Creator<AudioRecordingConfiguration>() {
        public AudioRecordingConfiguration createFromParcel(Parcel parcel) {
            return new AudioRecordingConfiguration(parcel);
        }

        public AudioRecordingConfiguration[] newArray(int i) {
            return new AudioRecordingConfiguration[i];
        }
    };
    private static final String TAG = new String("AudioRecordingConfiguration");
    private final String mClientPackageName;
    private final int mClientSource;
    private final int mClientUid;
    private final int mPatchHandle;
    private final int mSessionId;

    public AudioRecordingConfiguration(int i, int i2, int i3, AudioFormat audioFormat, AudioFormat audioFormat2, int i4, String str) {
        this.mClientUid = i;
        this.mSessionId = i2;
        this.mClientSource = i3;
        this.mPatchHandle = i4;
        this.mClientPackageName = str;
    }

    private AudioRecordingConfiguration(Parcel parcel) {
        this.mSessionId = parcel.readInt();
        this.mClientSource = parcel.readInt();
        this.mPatchHandle = parcel.readInt();
        this.mClientPackageName = parcel.readString();
        this.mClientUid = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void dump(PrintWriter printWriter) {
    }

    public int getClientAudioSessionId() {
        return this.mSessionId;
    }

    public int getClientAudioSource() {
        return this.mClientSource;
    }

    public String getClientPackageName() {
        return this.mClientPackageName;
    }

    public int getClientUid() {
        return this.mClientUid;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{Integer.valueOf(this.mSessionId), Integer.valueOf(this.mClientSource)});
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSessionId);
        parcel.writeInt(this.mClientSource);
        parcel.writeInt(this.mPatchHandle);
        parcel.writeString(this.mClientPackageName);
        parcel.writeInt(this.mClientUid);
    }
}
