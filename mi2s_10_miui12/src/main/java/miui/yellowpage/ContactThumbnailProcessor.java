package miui.yellowpage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import miui.graphics.BitmapFactory;
import miui.yellowpage.YellowPageImgLoader;

public class ContactThumbnailProcessor implements YellowPageImgLoader.Image.ImageProcessor {
    private static final int sPhotoSize = 134;
    private int mBackgroundRes;
    private Context mContext;
    private boolean mDefaultPhoto = true;
    private int mForegroundRes;
    private int mMaskRes;

    public ContactThumbnailProcessor(Context context, int forgroundRes, int backgroundRes, int maskRes) {
        this.mContext = context;
        this.mForegroundRes = forgroundRes;
        this.mBackgroundRes = backgroundRes;
        this.mMaskRes = maskRes;
    }

    public ContactThumbnailProcessor(Context context) {
        this.mContext = context;
    }

    public Bitmap processImage(Bitmap originImage) {
        if (originImage == null) {
            return null;
        }
        if (this.mDefaultPhoto) {
            return BitmapFactory.createPhoto(this.mContext, originImage, sPhotoSize);
        }
        Resources res = this.mContext.getResources();
        return BitmapFactory.composeBitmap(originImage, (Bitmap) null, res.getDrawable(this.mMaskRes), res.getDrawable(this.mForegroundRes), res.getDrawable(this.mBackgroundRes), sPhotoSize);
    }
}
