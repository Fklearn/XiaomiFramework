package com.miui.internal.yellowpage;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import miui.yellowpage.YellowPageContract;
import miui.yellowpage.YellowPageImgLoader;

public class YellowPageAvatar extends YellowPageImgLoader.Image {
    private static final String TAG = "YellowPageAvatar";
    private String mAvatarName;
    private Context mContext;
    private YellowPageAvatarFormat mFormat;

    public enum YellowPageAvatarFormat {
        PHOTO_NUMBER,
        THUMBNAIL_NUMBER,
        PHOTO_YID,
        THUMBNAIL_YID,
        PHOTO_NAME,
        THUMBNAIL_NAME
    }

    public YellowPageAvatar(Context context, String url, YellowPageAvatarFormat format) {
        super(url);
        this.mContext = context;
        this.mFormat = format;
    }

    public String getUrl() {
        getName();
        return this.mAvatarName;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof YellowPageAvatar)) {
            return false;
        }
        YellowPageAvatar avatar = (YellowPageAvatar) o;
        if (this.mFormat != avatar.mFormat || !TextUtils.equals(this.mUrl, avatar.mUrl)) {
            return false;
        }
        return true;
    }

    public String getName() {
        if (TextUtils.isEmpty(this.mAvatarName)) {
            if (this.mFormat == YellowPageAvatarFormat.THUMBNAIL_NUMBER || this.mFormat == YellowPageAvatarFormat.PHOTO_NUMBER) {
                this.mAvatarName = getAvatarUrlByNumber(this.mContext, this.mUrl, this.mFormat);
            } else if (this.mFormat == YellowPageAvatarFormat.PHOTO_YID || this.mFormat == YellowPageAvatarFormat.THUMBNAIL_YID) {
                this.mAvatarName = getAvatarUrlById(this.mContext, this.mUrl, this.mFormat);
            } else {
                this.mAvatarName = this.mUrl;
            }
        }
        if (!TextUtils.isEmpty(this.mAvatarName)) {
            return YellowPageImgLoader.getDataSha1Digest(this.mAvatarName.getBytes());
        }
        return null;
    }

    private static String getAvatarUrlByNumber(Context context, String number, YellowPageAvatarFormat format) {
        String avatarColumn;
        if (format == YellowPageAvatarFormat.PHOTO_NUMBER) {
            avatarColumn = YellowPageContract.PhoneLookup.PHOTO_URL;
        } else {
            avatarColumn = YellowPageContract.PhoneLookup.THUMBNAIL_URL;
        }
        Cursor c = context.getContentResolver().query(Uri.withAppendedPath(YellowPageContract.PhoneLookup.CONTENT_URI, number), new String[]{avatarColumn}, (String) null, (String[]) null, (String) null);
        String name = null;
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    name = c.getString(0);
                }
            } finally {
                c.close();
            }
        }
        return name;
    }

    private static String getAvatarUrlById(Context context, String yid, YellowPageAvatarFormat format) {
        String avatarColumn;
        if (format == YellowPageAvatarFormat.PHOTO_YID) {
            avatarColumn = YellowPageContract.PhoneLookup.PHOTO_URL;
        } else {
            avatarColumn = YellowPageContract.PhoneLookup.THUMBNAIL_URL;
        }
        Cursor c = context.getContentResolver().query(YellowPageContract.PhoneLookup.CONTENT_URI, new String[]{avatarColumn}, "yid = " + yid, (String[]) null, (String) null);
        String name = null;
        if (c != null) {
            try {
                if (c.moveToFirst()) {
                    name = c.getString(0);
                }
            } finally {
                c.close();
            }
        }
        return name;
    }
}
