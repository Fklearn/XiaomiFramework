package miui.yellowpage;

@Deprecated
public class YellowPageMipub {
    private String mMipubId;
    private String mThumbnailName;
    private long mYpId;
    private String mYpName;

    public YellowPageMipub(String mipubId, String ypName, long ypId, String thumbnailName) {
        this.mMipubId = mipubId;
        this.mYpName = ypName;
        this.mYpId = ypId;
        this.mThumbnailName = thumbnailName;
    }

    public String getMipubId() {
        return this.mMipubId;
    }

    public String getYpName() {
        return this.mYpName;
    }

    public long getYpId() {
        return this.mYpId;
    }

    public String getThumbnailName() {
        return this.mThumbnailName;
    }
}
