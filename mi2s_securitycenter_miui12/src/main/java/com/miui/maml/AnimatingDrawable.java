package com.miui.maml;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.miui.maml.MamlDrawable;

public class AnimatingDrawable extends MamlDrawable {
    private static final String QUIET_IMAGE_NAME = "quietImage.png";
    private static final String TAG = "Maml.AnimatingDrawable";
    public static final int TIME_FANCY_CACHE = 0;
    private String mClassName;
    private Context mContext;
    private FancyDrawable mFancyDrawable;
    private final Object mLock = new Object();
    private String mPackageName;
    private Drawable mQuietDrawable;
    private ResourceManager mResourceManager;
    private UserHandle mUser;

    static final class AnimatingDrawableState extends MamlDrawable.MamlDrawableState {
        private String mClassName;
        private Context mContext;
        private String mPackageName;
        private ResourceManager mResourceManager;
        private UserHandle mUser;

        public AnimatingDrawableState(Context context, String str, String str2, ResourceManager resourceManager, UserHandle userHandle) {
            this.mContext = context;
            this.mResourceManager = resourceManager;
            this.mPackageName = str;
            this.mClassName = str2;
            this.mUser = userHandle;
        }

        /* access modifiers changed from: protected */
        public MamlDrawable createDrawable() {
            return new AnimatingDrawable(this.mContext, this.mPackageName, this.mClassName, this.mResourceManager, this.mUser);
        }
    }

    public AnimatingDrawable(Context context, String str, String str2, ResourceManager resourceManager, UserHandle userHandle) {
        this.mContext = context;
        this.mResourceManager = resourceManager;
        this.mPackageName = str;
        this.mClassName = str2;
        this.mUser = userHandle;
        init();
    }

    private void init() {
        this.mState = new AnimatingDrawableState(this.mContext, this.mPackageName, this.mClassName, this.mResourceManager, this.mUser);
        Display defaultDisplay = ((WindowManager) this.mContext.getSystemService("window")).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        ResourceManager resourceManager = this.mResourceManager;
        resourceManager.setExtraResource("den" + displayMetrics.densityDpi, displayMetrics.densityDpi);
        this.mQuietDrawable = this.mResourceManager.getDrawable(this.mContext.getResources(), QUIET_IMAGE_NAME);
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            setIntrinsicSize(drawable.getIntrinsicWidth(), this.mQuietDrawable.getIntrinsicHeight());
            this.mQuietDrawable = this.mQuietDrawable.mutate();
            Drawable drawable2 = this.mQuietDrawable;
            drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), this.mQuietDrawable.getIntrinsicHeight());
            ColorFilter colorFilter = this.mColorFilter;
            if (colorFilter != null) {
                this.mQuietDrawable.setColorFilter(colorFilter);
                return;
            }
            return;
        }
        Log.e(TAG, "mQuietDrwable is null! package/class=" + this.mPackageName + "/" + this.mClassName);
    }

    public void clear() {
        synchronized (this.mLock) {
            this.mFancyDrawable = null;
        }
    }

    /* access modifiers changed from: protected */
    public void drawIcon(Canvas canvas) {
        String str;
        try {
            int save = canvas.save();
            canvas.translate((float) getBounds().left, (float) getBounds().top);
            canvas.scale(((float) this.mWidth) / ((float) this.mIntrinsicWidth), ((float) this.mHeight) / ((float) this.mIntrinsicHeight), 0.0f, 0.0f);
            this.mQuietDrawable.draw(canvas);
            canvas.restoreToCount(save);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            str = e.toString();
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            str = e2.toString();
        }
        Log.e(TAG, str);
    }

    public Drawable getFancyDrawable() {
        return this.mFancyDrawable;
    }

    public int getOpacity() {
        return -3;
    }

    public Drawable getStartDrawable() {
        synchronized (this.mLock) {
            prepareFancyDrawable();
            if (this.mFancyDrawable == null) {
                return null;
            }
            Drawable startDrawable = this.mFancyDrawable.getStartDrawable();
            return startDrawable;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0027, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void prepareFancyDrawable() {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            com.miui.maml.FancyDrawable r1 = r8.mFancyDrawable     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x0009:
            android.content.Context r2 = r8.mContext     // Catch:{ all -> 0x0028 }
            java.lang.String r3 = r8.mPackageName     // Catch:{ all -> 0x0028 }
            java.lang.String r4 = r8.mClassName     // Catch:{ all -> 0x0028 }
            r5 = 0
            android.os.UserHandle r7 = r8.mUser     // Catch:{ all -> 0x0028 }
            android.graphics.drawable.Drawable r1 = com.miui.maml.util.AppIconsHelper.getFancyIconDrawable(r2, r3, r4, r5, r7)     // Catch:{ all -> 0x0028 }
            boolean r2 = r1 instanceof com.miui.maml.FancyDrawable     // Catch:{ all -> 0x0028 }
            if (r2 == 0) goto L_0x0026
            com.miui.maml.FancyDrawable r1 = (com.miui.maml.FancyDrawable) r1     // Catch:{ all -> 0x0028 }
            r8.mFancyDrawable = r1     // Catch:{ all -> 0x0028 }
            com.miui.maml.FancyDrawable r1 = r8.mFancyDrawable     // Catch:{ all -> 0x0028 }
            android.graphics.ColorFilter r2 = r8.mColorFilter     // Catch:{ all -> 0x0028 }
            r1.setColorFilter(r2)     // Catch:{ all -> 0x0028 }
        L_0x0026:
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            return
        L_0x0028:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.AnimatingDrawable.prepareFancyDrawable():void");
    }

    public void sendCommand(String str) {
        FancyDrawable fancyDrawable = this.mFancyDrawable;
        if (fancyDrawable != null) {
            fancyDrawable.getRoot().onCommand(str);
        }
    }

    public void setAlpha(int i) {
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Log.d(TAG, "setColorFilter");
        Drawable drawable = this.mQuietDrawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        Drawable drawable2 = this.mBadgeDrawable;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter);
        }
        FancyDrawable fancyDrawable = this.mFancyDrawable;
        if (fancyDrawable != null) {
            fancyDrawable.setColorFilter(colorFilter);
        }
    }
}
