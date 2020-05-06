package com.miui.maml.elements;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.util.Utils;
import com.miui.maml.util.net.IOUtils;
import java.io.File;
import java.io.InputStream;
import java.net.URI;

public abstract class BitmapProvider {
    private static final String LOG_TAG = "BitmapProvider";
    protected ScreenElementRoot mRoot;
    protected VersionedBitmap mVersionedBitmap = new VersionedBitmap((Bitmap) null);

    private static class AppIconProvider extends BitmapProvider {
        public static final String TAG_NAME = "ApplicationIcon";
        private String mCls;
        private boolean mNoIcon;
        private String mPkg;
        private String mSrc;

        public AppIconProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        private void parseSrc(String str) {
            StringBuilder sb;
            this.mNoIcon = false;
            Bitmap unused = this.mVersionedBitmap.mBitmap = null;
            if (!TextUtils.isEmpty(str)) {
                String[] split = str.split(",");
                if (split.length == 2) {
                    this.mPkg = split[0];
                    this.mCls = split[1];
                    return;
                } else if (split.length == 1) {
                    this.mPkg = split[0];
                    return;
                } else {
                    sb = new StringBuilder();
                }
            } else {
                sb = new StringBuilder();
            }
            sb.append("invalid src of ApplicationIcon type: ");
            sb.append(str);
            Log.e("BitmapProvider", sb.toString());
            this.mNoIcon = true;
        }

        private void tryToSetBitmap() {
            try {
                Drawable activityIcon = this.mCls != null ? this.mRoot.getContext().mContext.getPackageManager().getActivityIcon(new ComponentName(this.mPkg, this.mCls)) : this.mRoot.getContext().mContext.getPackageManager().getApplicationIcon(this.mPkg);
                if (activityIcon instanceof BitmapDrawable) {
                    this.mVersionedBitmap.setBitmap(((BitmapDrawable) activityIcon).getBitmap());
                    return;
                }
                int intrinsicWidth = activityIcon.getIntrinsicWidth();
                int intrinsicHeight = activityIcon.getIntrinsicHeight();
                Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, activityIcon.getOpacity() != -1 ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(createBitmap);
                activityIcon.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
                activityIcon.draw(canvas);
                this.mVersionedBitmap.setBitmap(createBitmap);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("BitmapProvider", "fail to get icon for src of ApplicationIcon type: " + this.mSrc);
                this.mNoIcon = true;
            }
        }

        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            if (!TextUtils.equals(str, this.mSrc)) {
                this.mSrc = str;
                parseSrc(str);
            }
            if (this.mVersionedBitmap.getBitmap() == null && !this.mNoIcon) {
                tryToSetBitmap();
            }
            return this.mVersionedBitmap;
        }

        public void init(String str) {
            BitmapProvider.super.init(str);
            this.mSrc = str;
            parseSrc(str);
        }
    }

    public static class BitmapHolderProvider extends BitmapProvider {
        public static final String TAG_NAME = "BitmapHolder";
        private IBitmapHolder mBitmapHolder;
        private String mId;

        public BitmapHolderProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            IBitmapHolder iBitmapHolder = this.mBitmapHolder;
            if (iBitmapHolder != null) {
                return iBitmapHolder.getBitmap(this.mId);
            }
            return null;
        }

        public void init(String str) {
            BitmapProvider.super.init(str);
            if (!TextUtils.isEmpty(str)) {
                int indexOf = str.indexOf(46);
                if (indexOf != -1) {
                    String substring = str.substring(0, indexOf);
                    this.mId = str.substring(indexOf + 1);
                    str = substring;
                }
                ScreenElement findElement = this.mRoot.findElement(str);
                if (findElement instanceof IBitmapHolder) {
                    this.mBitmapHolder = (IBitmapHolder) findElement;
                }
            }
        }
    }

    public static class BitmapVariableProvider extends BitmapProvider {
        public static final String TAG_NAME = "BitmapVar";
        private String mCurSrc;
        private Expression mIndexExpression;
        private IndexedVariable mVar;

        public BitmapVariableProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            int i3;
            Bitmap bitmap = null;
            if (!Utils.equals(this.mCurSrc, str)) {
                this.mVar = null;
                this.mIndexExpression = null;
                if (!TextUtils.isEmpty(str)) {
                    int indexOf = str.indexOf(91);
                    int length = str.length();
                    if (indexOf != -1 && indexOf < length - 1 && str.charAt(i3) == ']') {
                        this.mIndexExpression = Expression.build(this.mRoot.getVariables(), str.substring(indexOf + 1, i3));
                    }
                    this.mVar = new IndexedVariable(this.mIndexExpression == null ? str : str.substring(0, indexOf), this.mRoot.getVariables(), false);
                }
                this.mCurSrc = str;
            }
            try {
                if (this.mVar != null) {
                    bitmap = (Bitmap) (this.mIndexExpression != null ? this.mVar.getArr((int) this.mIndexExpression.evaluate()) : this.mVar.get());
                }
            } catch (ClassCastException unused) {
                Log.w("BitmapProvider", "fail to cast as Bitmap from object: " + str);
            }
            this.mVersionedBitmap.setBitmap(bitmap);
            return this.mVersionedBitmap;
        }

        public void init(String str) {
            BitmapProvider.super.init(str);
            if (!TextUtils.isEmpty(str)) {
                this.mVar = new IndexedVariable(str, this.mRoot.getVariables(), false);
                this.mCurSrc = str;
            }
        }
    }

    private static class FileSystemProvider extends UriProvider {
        public static final String TAG_NAME = "FileSystem";

        public FileSystemProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            if (TextUtils.isEmpty(str)) {
                this.mVersionedBitmap.setBitmap((Bitmap) null);
                return this.mVersionedBitmap;
            }
            URI uri = new File(str).toURI();
            if (uri != null) {
                return super.getBitmap(uri.toString(), z, i, i2);
            }
            this.mVersionedBitmap.setBitmap((Bitmap) null);
            return this.mVersionedBitmap;
        }
    }

    public interface IBitmapHolder {
        VersionedBitmap getBitmap(String str);
    }

    private static class ResourceImageProvider extends BitmapProvider {
        public static final String TAG_NAME = "ResourceImage";
        private ResourceManager.AsyncLoadListener mAsyncLoadListener = new ResourceManager.AsyncLoadListener() {
            public void onLoadComplete(String str, ResourceManager.BitmapInfo bitmapInfo) {
                synchronized (ResourceImageProvider.this.mSrcNameLock) {
                    if (TextUtils.equals(str, ResourceImageProvider.this.mLoadingBitmapName)) {
                        Log.i("BitmapProvider", "load image async complete: " + str + " last cached " + ResourceImageProvider.this.mCachedBitmapName);
                        ResourceImageProvider.this.mVersionedBitmap.setBitmap(bitmapInfo == null ? null : bitmapInfo.mBitmap);
                        String unused = ResourceImageProvider.this.mCachedBitmapName = str;
                        ResourceImageProvider.this.mLoadingBitmapName = null;
                    } else {
                        Log.i("BitmapProvider", "load image async complete: " + str + " not equals " + ResourceImageProvider.this.mLoadingBitmapName);
                    }
                }
                ResourceImageProvider.this.mRoot.requestUpdate();
            }
        };
        /* access modifiers changed from: private */
        public String mCachedBitmapName;
        String mLoadingBitmapName;
        Object mSrcNameLock = new Object();

        public ResourceImageProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        public void finish() {
            BitmapProvider.super.finish();
            synchronized (this.mSrcNameLock) {
                this.mLoadingBitmapName = null;
                this.mCachedBitmapName = null;
                this.mVersionedBitmap.reset();
            }
        }

        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            Bitmap bitmap = this.mVersionedBitmap.getBitmap();
            if ((bitmap != null && bitmap.isRecycled()) || !TextUtils.equals(this.mCachedBitmapName, str)) {
                Bitmap bitmap2 = null;
                if (z) {
                    ResourceManager.BitmapInfo bitmapInfo = this.mRoot.getContext().mResourceManager.getBitmapInfo(str);
                    VersionedBitmap versionedBitmap = this.mVersionedBitmap;
                    if (bitmapInfo != null) {
                        bitmap2 = bitmapInfo.mBitmap;
                    }
                    versionedBitmap.setBitmap(bitmap2);
                    this.mCachedBitmapName = str;
                } else {
                    ResourceManager.BitmapInfo bitmapInfoAsync = this.mRoot.getContext().mResourceManager.getBitmapInfoAsync(str, this.mAsyncLoadListener);
                    synchronized (this.mSrcNameLock) {
                        if (bitmapInfoAsync != null) {
                            if (bitmapInfoAsync.mLoading) {
                                this.mLoadingBitmapName = str;
                            }
                        }
                        this.mVersionedBitmap.setBitmap(bitmapInfoAsync == null ? null : bitmapInfoAsync.mBitmap);
                        this.mCachedBitmapName = str;
                        this.mLoadingBitmapName = null;
                    }
                }
            }
            return this.mVersionedBitmap;
        }
    }

    private static class UriProvider extends BitmapProvider {
        public static final String TAG_NAME = "Uri";
        /* access modifiers changed from: private */
        public String mCachedBitmapUri;
        /* access modifiers changed from: private */
        public String mCurLoadingBitmapUri;
        /* access modifiers changed from: private */
        public Object mLock = new Object();

        private class LoaderAsyncTask extends AsyncTask<Object, Object, Bitmap> {
            private int mHeight = -1;
            private String mUri = null;
            private int mWidth = -1;

            public LoaderAsyncTask(String str, int i, int i2) {
                this.mUri = str;
                this.mWidth = i;
                this.mHeight = i2;
            }

            /* access modifiers changed from: protected */
            public Bitmap doInBackground(Object... objArr) {
                Bitmap bitmapFromUri = UriProvider.this.getBitmapFromUri(Uri.parse(this.mUri), this.mWidth, this.mHeight);
                if (bitmapFromUri == null) {
                    Log.w("BitmapProvider", "fail to decode bitmap: " + this.mUri);
                }
                synchronized (UriProvider.this.mLock) {
                    if (TextUtils.equals(this.mUri, UriProvider.this.mCurLoadingBitmapUri)) {
                        UriProvider.this.mVersionedBitmap.setBitmap(bitmapFromUri);
                        String unused = UriProvider.this.mCachedBitmapUri = UriProvider.this.mCurLoadingBitmapUri;
                        UriProvider.this.mRoot.requestUpdate();
                        String unused2 = UriProvider.this.mCurLoadingBitmapUri = null;
                    }
                }
                return bitmapFromUri;
            }
        }

        public UriProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        public void finish() {
            BitmapProvider.super.finish();
            synchronized (this.mLock) {
                this.mCachedBitmapUri = null;
                this.mCurLoadingBitmapUri = null;
                this.mVersionedBitmap.reset();
            }
        }

        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            Bitmap bitmap;
            VersionedBitmap versionedBitmap;
            if (TextUtils.isEmpty(str)) {
                versionedBitmap = this.mVersionedBitmap;
                bitmap = null;
            } else {
                bitmap = this.mVersionedBitmap.getBitmap();
                if ((bitmap != null && bitmap.isRecycled()) || !TextUtils.equals(this.mCachedBitmapUri, str)) {
                    synchronized (this.mLock) {
                        if (!TextUtils.equals(this.mCurLoadingBitmapUri, str) && !TextUtils.equals(this.mCachedBitmapUri, str)) {
                            this.mCurLoadingBitmapUri = str;
                            new LoaderAsyncTask(str, i, i2).execute(new Object[0]);
                        }
                    }
                }
                versionedBitmap = this.mVersionedBitmap;
            }
            versionedBitmap.setBitmap(bitmap);
            return this.mVersionedBitmap;
        }
    }

    public static class VersionedBitmap {
        /* access modifiers changed from: private */
        public Bitmap mBitmap;
        private int mVersion;

        public VersionedBitmap(Bitmap bitmap) {
            this.mBitmap = bitmap;
        }

        public static boolean equals(VersionedBitmap versionedBitmap, VersionedBitmap versionedBitmap2) {
            return versionedBitmap != null && versionedBitmap2 != null && versionedBitmap.mBitmap == versionedBitmap2.mBitmap && versionedBitmap.mVersion == versionedBitmap2.mVersion;
        }

        public Bitmap getBitmap() {
            return this.mBitmap;
        }

        public void reset() {
            this.mBitmap = null;
            this.mVersion = 0;
        }

        public void set(VersionedBitmap versionedBitmap) {
            if (versionedBitmap != null) {
                this.mBitmap = versionedBitmap.mBitmap;
                this.mVersion = versionedBitmap.mVersion;
                return;
            }
            reset();
        }

        public boolean setBitmap(Bitmap bitmap) {
            if (bitmap != this.mBitmap) {
                this.mBitmap = bitmap;
                this.mVersion++;
            }
            return bitmap != this.mBitmap;
        }

        public int updateVersion() {
            int i = this.mVersion;
            this.mVersion = i + 1;
            return i;
        }
    }

    private static class VirtualScreenProvider extends BitmapProvider {
        public static final String TAG_NAME = "VirtualScreen";
        private VirtualScreen mVirtualScreen;

        public VirtualScreenProvider(ScreenElementRoot screenElementRoot) {
            super(screenElementRoot);
        }

        public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
            VersionedBitmap versionedBitmap = this.mVersionedBitmap;
            VirtualScreen virtualScreen = this.mVirtualScreen;
            versionedBitmap.setBitmap(virtualScreen != null ? virtualScreen.getBitmap() : null);
            return this.mVersionedBitmap;
        }

        public void init(String str) {
            BitmapProvider.super.init(str);
            ScreenElement findElement = this.mRoot.findElement(str);
            if (findElement instanceof VirtualScreen) {
                this.mVirtualScreen = (VirtualScreen) findElement;
            }
        }
    }

    public BitmapProvider(ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
    }

    private static int computeSampleSize(BitmapFactory.Options options, int i) {
        int i2 = 1;
        while (true) {
            int i3 = i2 * 2;
            if (((double) i3) > Math.sqrt((((double) options.outHeight) * ((double) options.outWidth)) / ((double) i))) {
                return i2;
            }
            i2 = i3;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0070, code lost:
        r3 = r0.create(r2, r3);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.maml.elements.BitmapProvider create(com.miui.maml.ScreenElementRoot r2, java.lang.String r3) {
        /*
            java.lang.String r0 = "ResourceImage"
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x000e
            com.miui.maml.elements.BitmapProvider$ResourceImageProvider r3 = new com.miui.maml.elements.BitmapProvider$ResourceImageProvider
            r3.<init>(r2)
            return r3
        L_0x000e:
            java.lang.String r0 = "VirtualScreen"
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x001c
            com.miui.maml.elements.BitmapProvider$VirtualScreenProvider r3 = new com.miui.maml.elements.BitmapProvider$VirtualScreenProvider
            r3.<init>(r2)
            return r3
        L_0x001c:
            java.lang.String r0 = "ApplicationIcon"
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x002a
            com.miui.maml.elements.BitmapProvider$AppIconProvider r3 = new com.miui.maml.elements.BitmapProvider$AppIconProvider
            r3.<init>(r2)
            return r3
        L_0x002a:
            java.lang.String r0 = "FileSystem"
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x0038
            com.miui.maml.elements.BitmapProvider$FileSystemProvider r3 = new com.miui.maml.elements.BitmapProvider$FileSystemProvider
            r3.<init>(r2)
            return r3
        L_0x0038:
            java.lang.String r0 = "Uri"
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x0046
            com.miui.maml.elements.BitmapProvider$UriProvider r3 = new com.miui.maml.elements.BitmapProvider$UriProvider
            r3.<init>(r2)
            return r3
        L_0x0046:
            java.lang.String r0 = "BitmapHolder"
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x0054
            com.miui.maml.elements.BitmapProvider$BitmapHolderProvider r3 = new com.miui.maml.elements.BitmapProvider$BitmapHolderProvider
            r3.<init>(r2)
            return r3
        L_0x0054:
            java.lang.String r0 = "BitmapVar"
            boolean r0 = android.text.TextUtils.equals(r3, r0)
            if (r0 == 0) goto L_0x0062
            com.miui.maml.elements.BitmapProvider$BitmapVariableProvider r3 = new com.miui.maml.elements.BitmapProvider$BitmapVariableProvider
            r3.<init>(r2)
            return r3
        L_0x0062:
            com.miui.maml.ScreenContext r0 = r2.getContext()
            java.lang.String r1 = "BitmapProvider"
            com.miui.maml.ObjectFactory r0 = r0.getObjectFactory(r1)
            com.miui.maml.ObjectFactory$BitmapProviderFactory r0 = (com.miui.maml.ObjectFactory.BitmapProviderFactory) r0
            if (r0 == 0) goto L_0x0077
            com.miui.maml.elements.BitmapProvider r3 = r0.create(r2, r3)
            if (r3 == 0) goto L_0x0077
            return r3
        L_0x0077:
            com.miui.maml.elements.BitmapProvider$ResourceImageProvider r3 = new com.miui.maml.elements.BitmapProvider$ResourceImageProvider
            r3.<init>(r2)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.BitmapProvider.create(com.miui.maml.ScreenElementRoot, java.lang.String):com.miui.maml.elements.BitmapProvider");
    }

    public void finish() {
        this.mVersionedBitmap.reset();
    }

    public VersionedBitmap getBitmap(String str, boolean z, int i, int i2) {
        return this.mVersionedBitmap;
    }

    /* access modifiers changed from: protected */
    public Bitmap getBitmapFromUri(Uri uri, int i, int i2) {
        InputStream inputStream;
        InputStream inputStream2;
        InputStream inputStream3 = null;
        try {
            inputStream = this.mRoot.getContext().mContext.getContentResolver().openInputStream(uri);
            if (i <= 0 || i2 <= 0) {
                Bitmap decodeStream = BitmapFactory.decodeStream(inputStream, (Rect) null, (BitmapFactory.Options) null);
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly((InputStream) null);
                return decodeStream;
            }
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, (Rect) null, options);
                options.inSampleSize = computeSampleSize(options, i * i2);
                options.inJustDecodeBounds = false;
                options.outHeight = i2;
                options.outWidth = i;
                inputStream2 = this.mRoot.getContext().mContext.getContentResolver().openInputStream(uri);
                try {
                    Bitmap decodeStream2 = BitmapFactory.decodeStream(inputStream2, (Rect) null, options);
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(inputStream2);
                    return decodeStream2;
                } catch (Exception e) {
                    e = e;
                    try {
                        Log.d("BitmapProvider", "getBitmapFromUri Exception", e);
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(inputStream2);
                        return null;
                    } catch (Throwable th) {
                        th = th;
                        inputStream3 = inputStream2;
                        IOUtils.closeQuietly(inputStream);
                        IOUtils.closeQuietly(inputStream3);
                        throw th;
                    }
                }
            } catch (Exception e2) {
                e = e2;
                inputStream2 = null;
                Log.d("BitmapProvider", "getBitmapFromUri Exception", e);
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(inputStream2);
                return null;
            } catch (Throwable th2) {
                th = th2;
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(inputStream3);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            inputStream2 = null;
            inputStream = null;
            Log.d("BitmapProvider", "getBitmapFromUri Exception", e);
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(inputStream2);
            return null;
        } catch (Throwable th3) {
            th = th3;
            inputStream = null;
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(inputStream3);
            throw th;
        }
    }

    public void init(String str) {
        reset();
    }

    public void reset() {
    }
}
