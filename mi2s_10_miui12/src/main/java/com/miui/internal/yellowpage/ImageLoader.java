package com.miui.internal.yellowpage;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.WindowManager;
import android.widget.ImageView;
import java.io.ByteArrayOutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import miui.yellowpage.Log;
import miui.yellowpage.YellowPageImgLoader;

public class ImageLoader implements Handler.Callback {
    private static final int BITMAP_CACHE_SIZE = 10485760;
    private static final int MESSAGE_REQUEST_LOAD = 2;
    private static final int MESSAGE_REQUEST_LOADED = 1;
    private static final String TAG = "ImageLoader";
    private static final int THREAD_POOL_COUNT = 6;
    private static final int UI_MODE_INIT = -1;
    private static int sDisplayHeight;
    private static int sDisplayWidth;
    private static ImageLoader sLoader;
    /* access modifiers changed from: private */
    public final Object BITMAP_CACHE_LOCK = new Object();
    /* access modifiers changed from: private */
    public final LruCache<YellowPageImgLoader.Image, BitmapHolder> mBitmapCache = new LruCache<YellowPageImgLoader.Image, BitmapHolder>(BITMAP_CACHE_SIZE) {
        /* access modifiers changed from: protected */
        public int sizeOf(YellowPageImgLoader.Image key, BitmapHolder value) {
            if (value == null || value.mBitmap == null) {
                return 0;
            }
            return value.mBitmap.getByteCount();
        }
    };
    private Context mContext;
    private int mCurrentUiMode = -1;
    private final Map<Object, Bitmap> mDefaultBitmapCache = new HashMap();
    private ExecutorService mExecutor;
    /* access modifiers changed from: private */
    public Handler mMainThreadHandler;
    private int mNetworkAccess;
    private volatile boolean mPauseLoading;
    private final Map<YellowPageImgLoader.Image, WeakReference<ImageView>> mPendingRequests = new LinkedHashMap();
    private final Map<YellowPageImgLoader.Image, LinkedList<WeakReference<ImageView>>> mRequestingImageViews = new HashMap();

    private enum State {
        NEEDED,
        LOADED,
        LOADING
    }

    private static class BitmapHolder {
        Bitmap mBitmap;
        State mState;

        private BitmapHolder() {
        }
    }

    public static synchronized ImageLoader getInstance(Context context) {
        ImageLoader imageLoader;
        synchronized (ImageLoader.class) {
            if (sLoader == null) {
                sLoader = new ImageLoader(context);
            }
            imageLoader = sLoader;
        }
        return imageLoader;
    }

    public static synchronized ImageLoader getInstance(Context context, int networkAccess) {
        ImageLoader imageLoader;
        synchronized (ImageLoader.class) {
            if (sLoader == null) {
                sLoader = new ImageLoader(context);
            }
            sLoader.mNetworkAccess = networkAccess;
            imageLoader = sLoader;
        }
        return imageLoader;
    }

    private ImageLoader(Context context) {
        this.mMainThreadHandler = new Handler(context.getMainLooper(), this);
        this.mExecutor = Executors.newFixedThreadPool(6);
        this.mContext = context.getApplicationContext();
    }

    private int getDisplayHeight() {
        if (sDisplayHeight == 0) {
            acquireScreenAttr(this.mContext);
        }
        return sDisplayHeight;
    }

    private int getDisplayWidth() {
        if (sDisplayWidth == 0) {
            acquireScreenAttr(this.mContext);
        }
        return sDisplayWidth;
    }

    private static void acquireScreenAttr(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(dm);
        sDisplayHeight = dm.heightPixels;
        sDisplayWidth = dm.widthPixels;
    }

    public void loadImage(ImageView view, YellowPageImgLoader.Image image, int defaultImageRes) {
        loadImage(view, image, (Bitmap) null, defaultImageRes);
    }

    public void loadImage(ImageView view, YellowPageImgLoader.Image image, Bitmap defaultBitmap) {
        loadImage(view, image, defaultBitmap, 0);
    }

    public byte[] loadImageBytes(YellowPageImgLoader.Image image, boolean online) {
        return loadImageBytes(image, online, 0, 0);
    }

    private void loadImage(ImageView view, YellowPageImgLoader.Image image, Bitmap defaultBitmap, int defaultImageRes) {
        if (image == null || !image.isValid()) {
            StringBuilder sb = new StringBuilder();
            sb.append("loadImage: invalid image : ");
            sb.append(image == null ? null : image.getUrl());
            Log.e(TAG, sb.toString());
            this.mPendingRequests.remove(image);
            view.setTag((Object) null);
            bindDefaultImage(view, image, defaultBitmap, defaultImageRes);
            return;
        }
        view.setTag(image);
        if (bindCachedImage(view, image) == State.LOADED) {
            this.mPendingRequests.remove(image);
            return;
        }
        bindDefaultImage(view, image, defaultBitmap, defaultImageRes);
        this.mPendingRequests.put(image, new WeakReference(view));
        if (requestLoading(view)) {
            this.mPendingRequests.remove(image);
        }
    }

    public Bitmap loadImageBitmap(YellowPageImgLoader.Image image, boolean online) {
        return decodeBitmap(loadImageBytes(image, online));
    }

    public byte[] loadImageBytes(YellowPageImgLoader.Image image, boolean online, int defaultRes, int timeout) {
        byte[] bytes = loadCachedImageBytes(image);
        if (bytes != null) {
            return bytes;
        }
        byte[] bytes2 = loadImage(image, online, timeout);
        if (bytes2 != null) {
            cacheBitmap(image, bytes2);
            return bytes2;
        } else if (defaultRes == 0) {
            return bytes2;
        } else {
            Log.v(TAG, "failed to load image, return default res");
            return bitmapToByteArray(((BitmapDrawable) this.mContext.getResources().getDrawable(defaultRes)).getBitmap());
        }
    }

    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private byte[] loadCachedImageBytes(YellowPageImgLoader.Image image) {
        synchronized (this.BITMAP_CACHE_LOCK) {
            BitmapHolder holder = this.mBitmapCache.get(image);
            if (holder == null || holder.mState != State.LOADED) {
                return null;
            }
            byte[] bitmapToByteArray = bitmapToByteArray(holder.mBitmap);
            return bitmapToByteArray;
        }
    }

    private State bindCachedImage(ImageView view, YellowPageImgLoader.Image image) {
        synchronized (this.BITMAP_CACHE_LOCK) {
            BitmapHolder holder = this.mBitmapCache.get(image);
            if (holder != null && holder.mState == State.LOADED) {
                bindImage(view, holder.mBitmap);
                State state = State.LOADED;
                return state;
            } else if (holder != null) {
                State state2 = holder.mState;
                return state2;
            } else {
                Log.d(TAG, "cannot get image");
                return State.NEEDED;
            }
        }
    }

    /* access modifiers changed from: private */
    public void removeFailedImage(YellowPageImgLoader.Image image) {
        synchronized (this.BITMAP_CACHE_LOCK) {
            this.mBitmapCache.remove(image);
        }
    }

    /* access modifiers changed from: private */
    public void cacheBitmap(YellowPageImgLoader.Image image, byte[] bitmapData) {
        BitmapHolder holder;
        synchronized (this.BITMAP_CACHE_LOCK) {
            if (this.mBitmapCache.get(image) == null) {
                holder = new BitmapHolder();
            } else {
                holder = this.mBitmapCache.remove(image);
            }
            inflateBitmap(image, holder, bitmapData);
            this.mBitmapCache.put(image, holder);
            if (holder.mBitmap != null) {
                holder.mState = State.LOADED;
            } else {
                holder.mState = State.NEEDED;
            }
        }
    }

    private void inflateBitmap(YellowPageImgLoader.Image image, BitmapHolder holder, byte[] raw) {
        if (raw != null) {
            try {
                Bitmap src = decodeBitmap(raw);
                if (src != null) {
                    holder.mBitmap = image.proccessImage(src);
                } else {
                    Log.e(TAG, "Can not decode bitmap bytes.");
                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "The holder's bytes should not be null");
        }
    }

    private Bitmap decodeBitmap(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        BitmapFactory.Options ops = new BitmapFactory.Options();
        ops.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, ops);
        ops.inSampleSize = calculateInSampleSize(ops, getDisplayWidth(), getDisplayHeight());
        ops.inJustDecodeBounds = false;
        ops.inPurgeable = true;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, ops);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        if (height <= reqHeight && width <= reqWidth) {
            return 1;
        }
        int heightRatio = Math.round(((float) height) / ((float) reqHeight));
        int widthRatio = Math.round(((float) width) / ((float) reqWidth));
        return heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    private boolean requestLoading(ImageView view) {
        if (this.mPauseLoading || !(view.getTag() instanceof YellowPageImgLoader.Image)) {
            return false;
        }
        YellowPageImgLoader.Image image = (YellowPageImgLoader.Image) view.getTag();
        LinkedList<WeakReference<ImageView>> imageViewList = this.mRequestingImageViews.get(image);
        if (imageViewList == null) {
            imageViewList = new LinkedList<>();
            this.mRequestingImageViews.put(image, imageViewList);
        }
        imageViewList.add(new WeakReference(view));
        this.mExecutor.execute(new LoadImageRunnable(image));
        return true;
    }

    public void pauseLoading() {
        this.mPauseLoading = true;
    }

    public void cancelRequest(ImageView view) {
        if (view != null) {
            view.setTag((Object) null);
        }
    }

    public void resumeLoading() {
        this.mPauseLoading = false;
        Iterator<YellowPageImgLoader.Image> iterator = this.mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            ImageView imageView = this.mPendingRequests.get(iterator.next()).get();
            if (imageView == null) {
                iterator.remove();
            } else if (requestLoading(imageView)) {
                iterator.remove();
            }
        }
    }

    public boolean handleMessage(Message msg) {
        int i = msg.what;
        if (i == 1) {
            onRequestLoaded(msg);
            return false;
        } else if (i != 2) {
            return false;
        } else {
            onRequestLoad(msg);
            return false;
        }
    }

    private void onRequestLoad(Message msg) {
        ImageView view;
        if (msg.obj != null && (view = (ImageView) ((WeakReference) msg.obj).get()) != null) {
            requestLoading(view);
        }
    }

    private void onRequestLoaded(Message msg) {
        if (msg.obj != null) {
            YellowPageImgLoader.Image image = (YellowPageImgLoader.Image) msg.obj;
            synchronized (this.BITMAP_CACHE_LOCK) {
                List<WeakReference<ImageView>> imageViewList = this.mRequestingImageViews.get(image);
                for (Reference<ImageView> ref : imageViewList) {
                    ImageView imageView = ref.get();
                    if (imageView != null) {
                        if (!this.mPendingRequests.containsKey(image) && image.equals(imageView.getTag())) {
                            BitmapHolder holder = this.mBitmapCache.get(image);
                            if (holder == null || holder.mState != State.LOADED) {
                                Log.d(TAG, "handleMessage:image " + image + " was garbage collected");
                                Message message = Message.obtain();
                                message.what = 2;
                                message.obj = new WeakReference(imageView);
                                this.mMainThreadHandler.sendMessage(message);
                            } else {
                                bindImage(imageView, holder.mBitmap);
                                Log.d(TAG, "handleMessage:ImageView with image " + image + " bound");
                            }
                        }
                    }
                }
                imageViewList.clear();
            }
        }
    }

    private class LoadImageRunnable implements Runnable {
        private YellowPageImgLoader.Image mImage;

        public LoadImageRunnable(YellowPageImgLoader.Image image) {
            this.mImage = image;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0046, code lost:
            r0 = com.miui.internal.yellowpage.ImageLoader.access$300(r4.this$0, r4.mImage, true);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x004f, code lost:
            if (r0 == null) goto L_0x005e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0051, code lost:
            com.miui.internal.yellowpage.ImageLoader.access$400(r4.this$0, r4.mImage, r0);
            notifyBindImage(r4.mImage);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x005e, code lost:
            com.miui.internal.yellowpage.ImageLoader.access$500(r4.this$0, r4.mImage);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r4 = this;
                com.miui.internal.yellowpage.ImageLoader r0 = com.miui.internal.yellowpage.ImageLoader.this
                java.lang.Object r0 = r0.BITMAP_CACHE_LOCK
                monitor-enter(r0)
                com.miui.internal.yellowpage.ImageLoader r1 = com.miui.internal.yellowpage.ImageLoader.this     // Catch:{ all -> 0x0066 }
                android.util.LruCache r1 = r1.mBitmapCache     // Catch:{ all -> 0x0066 }
                miui.yellowpage.YellowPageImgLoader$Image r2 = r4.mImage     // Catch:{ all -> 0x0066 }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0066 }
                com.miui.internal.yellowpage.ImageLoader$BitmapHolder r1 = (com.miui.internal.yellowpage.ImageLoader.BitmapHolder) r1     // Catch:{ all -> 0x0066 }
                if (r1 == 0) goto L_0x002c
                com.miui.internal.yellowpage.ImageLoader$State r2 = r1.mState     // Catch:{ all -> 0x0066 }
                com.miui.internal.yellowpage.ImageLoader$State r3 = com.miui.internal.yellowpage.ImageLoader.State.LOADING     // Catch:{ all -> 0x0066 }
                if (r2 != r3) goto L_0x001f
                monitor-exit(r0)     // Catch:{ all -> 0x0066 }
                return
            L_0x001f:
                com.miui.internal.yellowpage.ImageLoader$State r2 = r1.mState     // Catch:{ all -> 0x0066 }
                com.miui.internal.yellowpage.ImageLoader$State r3 = com.miui.internal.yellowpage.ImageLoader.State.LOADED     // Catch:{ all -> 0x0066 }
                if (r2 != r3) goto L_0x002c
                miui.yellowpage.YellowPageImgLoader$Image r2 = r4.mImage     // Catch:{ all -> 0x0066 }
                r4.notifyBindImage(r2)     // Catch:{ all -> 0x0066 }
                monitor-exit(r0)     // Catch:{ all -> 0x0066 }
                return
            L_0x002c:
                if (r1 != 0) goto L_0x0035
                com.miui.internal.yellowpage.ImageLoader$BitmapHolder r2 = new com.miui.internal.yellowpage.ImageLoader$BitmapHolder     // Catch:{ all -> 0x0066 }
                r3 = 0
                r2.<init>()     // Catch:{ all -> 0x0066 }
                r1 = r2
            L_0x0035:
                com.miui.internal.yellowpage.ImageLoader$State r2 = com.miui.internal.yellowpage.ImageLoader.State.LOADING     // Catch:{ all -> 0x0066 }
                r1.mState = r2     // Catch:{ all -> 0x0066 }
                com.miui.internal.yellowpage.ImageLoader r2 = com.miui.internal.yellowpage.ImageLoader.this     // Catch:{ all -> 0x0066 }
                android.util.LruCache r2 = r2.mBitmapCache     // Catch:{ all -> 0x0066 }
                miui.yellowpage.YellowPageImgLoader$Image r3 = r4.mImage     // Catch:{ all -> 0x0066 }
                r2.put(r3, r1)     // Catch:{ all -> 0x0066 }
                monitor-exit(r0)     // Catch:{ all -> 0x0066 }
                com.miui.internal.yellowpage.ImageLoader r0 = com.miui.internal.yellowpage.ImageLoader.this
                miui.yellowpage.YellowPageImgLoader$Image r1 = r4.mImage
                r2 = 1
                byte[] r0 = r0.loadImage(r1, r2)
                if (r0 == 0) goto L_0x005e
                com.miui.internal.yellowpage.ImageLoader r1 = com.miui.internal.yellowpage.ImageLoader.this
                miui.yellowpage.YellowPageImgLoader$Image r2 = r4.mImage
                r1.cacheBitmap(r2, r0)
                miui.yellowpage.YellowPageImgLoader$Image r1 = r4.mImage
                r4.notifyBindImage(r1)
                goto L_0x0065
            L_0x005e:
                com.miui.internal.yellowpage.ImageLoader r1 = com.miui.internal.yellowpage.ImageLoader.this
                miui.yellowpage.YellowPageImgLoader$Image r2 = r4.mImage
                r1.removeFailedImage(r2)
            L_0x0065:
                return
            L_0x0066:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0066 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.internal.yellowpage.ImageLoader.LoadImageRunnable.run():void");
        }

        private void notifyBindImage(YellowPageImgLoader.Image image) {
            Message msg = ImageLoader.this.mMainThreadHandler.obtainMessage(1);
            msg.obj = image;
            ImageLoader.this.mMainThreadHandler.sendMessage(msg);
        }
    }

    private void bindImage(ImageView view, Bitmap bitmap) {
        if (view != null && bitmap != null) {
            view.setImageBitmap(bitmap);
        }
    }

    private void bindDefaultImage(ImageView view, YellowPageImgLoader.Image image, Bitmap defaultBitmap, int defaultResId) {
        int uiMode = getCurrentUiMode(this.mContext);
        Object cacheKey = getDefaultImageKey(defaultBitmap, defaultResId);
        if (this.mCurrentUiMode != uiMode) {
            this.mDefaultBitmapCache.remove(cacheKey);
            this.mCurrentUiMode = uiMode;
        }
        Bitmap cachedDefaultBitmap = this.mDefaultBitmapCache.get(cacheKey);
        if (cachedDefaultBitmap == null) {
            if (defaultBitmap != null) {
                try {
                    cachedDefaultBitmap = image.proccessImage(defaultBitmap);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            } else if (defaultResId != 0) {
                cachedDefaultBitmap = image.proccessImage(((BitmapDrawable) this.mContext.getResources().getDrawable(defaultResId)).getBitmap());
            }
            if (cachedDefaultBitmap != null) {
                this.mDefaultBitmapCache.put(getDefaultImageKey(defaultBitmap, defaultResId), cachedDefaultBitmap);
            }
        }
        view.setImageBitmap(cachedDefaultBitmap);
    }

    private static Object getDefaultImageKey(Bitmap defaultBitmap, int defaultResId) {
        return defaultBitmap != null ? defaultBitmap : Integer.valueOf(defaultResId);
    }

    /* access modifiers changed from: private */
    public byte[] loadImage(YellowPageImgLoader.Image image, boolean online) {
        return loadImage(image, online, 0);
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0228, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0229, code lost:
        r3 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x022c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:?, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0230, code lost:
        if (r5 != null) goto L_0x0232;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0236, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0237, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:?, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x0242, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0243, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x024e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x024f, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:?, code lost:
        r8.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x025a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x025b, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0266, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:?, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x026a, code lost:
        if (r5 != null) goto L_0x026c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0270, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x0271, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:?, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x027c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x027d, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x0288, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x0289, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:?, code lost:
        r8.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x0294, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0295, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x02de, code lost:
        if (r5 != null) goto L_0x02e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x02e4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x02e5, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x02ea, code lost:
        if (r6 != null) goto L_0x02ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:?, code lost:
        r6.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x02f0, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x02f1, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x02f6, code lost:
        if (r7 != null) goto L_0x02f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:?, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x02fc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x02fd, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0302, code lost:
        if (r8 != null) goto L_0x0304;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:?, code lost:
        r8.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0308, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0309, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x030e, code lost:
        if (r4 != null) goto L_0x0310;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:?, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0314, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0315, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x031a, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:?, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x015b, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:?, code lost:
        r0.printStackTrace();
        r1.mContext.getContentResolver().query(android.net.Uri.withAppendedPath(miui.yellowpage.YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_CLOUD, android.net.Uri.encode(r2)).buildUpon().appendQueryParameter("fileName", r19.getName()).build(), (java.lang.String[]) null, (java.lang.String) null, (java.lang.String[]) null, (java.lang.String) null);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x022c A[ExcHandler: Exception (r0v23 'e' java.lang.Exception A[CUSTOM_DECLARE]), PHI: r6 r7 r8 
      PHI: (r6v5 'bos' java.io.ByteArrayOutputStream) = (r6v2 'bos' java.io.ByteArrayOutputStream), (r6v7 'bos' java.io.ByteArrayOutputStream), (r6v7 'bos' java.io.ByteArrayOutputStream), (r6v7 'bos' java.io.ByteArrayOutputStream) binds: [B:7:0x002b, B:12:0x0041, B:77:0x015c, B:50:0x0100] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r7v3 'is' java.io.FileInputStream) = (r7v0 'is' java.io.FileInputStream), (r7v0 'is' java.io.FileInputStream), (r7v7 'is' java.io.FileInputStream), (r7v8 'is' java.io.FileInputStream) binds: [B:7:0x002b, B:12:0x0041, B:77:0x015c, B:50:0x0100] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r8v3 'cloudImage' android.content.res.AssetFileDescriptor) = (r8v0 'cloudImage' android.content.res.AssetFileDescriptor), (r8v0 'cloudImage' android.content.res.AssetFileDescriptor), (r8v6 'cloudImage' android.content.res.AssetFileDescriptor), (r8v7 'cloudImage' android.content.res.AssetFileDescriptor) binds: [B:7:0x002b, B:12:0x0041, B:77:0x015c, B:50:0x0100] A[DONT_GENERATE, DONT_INLINE], Splitter:B:7:0x002b] */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x023e A[SYNTHETIC, Splitter:B:145:0x023e] */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x024a A[SYNTHETIC, Splitter:B:150:0x024a] */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0256 A[SYNTHETIC, Splitter:B:155:0x0256] */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0262 A[SYNTHETIC, Splitter:B:160:0x0262] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x0266 A[ExcHandler: IOException (r0v14 'e' java.io.IOException A[CUSTOM_DECLARE]), PHI: r6 r7 r8 
      PHI: (r6v4 'bos' java.io.ByteArrayOutputStream) = (r6v2 'bos' java.io.ByteArrayOutputStream), (r6v7 'bos' java.io.ByteArrayOutputStream), (r6v7 'bos' java.io.ByteArrayOutputStream), (r6v7 'bos' java.io.ByteArrayOutputStream) binds: [B:7:0x002b, B:12:0x0041, B:77:0x015c, B:50:0x0100] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r7v2 'is' java.io.FileInputStream) = (r7v0 'is' java.io.FileInputStream), (r7v0 'is' java.io.FileInputStream), (r7v7 'is' java.io.FileInputStream), (r7v8 'is' java.io.FileInputStream) binds: [B:7:0x002b, B:12:0x0041, B:77:0x015c, B:50:0x0100] A[DONT_GENERATE, DONT_INLINE]
      PHI: (r8v2 'cloudImage' android.content.res.AssetFileDescriptor) = (r8v0 'cloudImage' android.content.res.AssetFileDescriptor), (r8v0 'cloudImage' android.content.res.AssetFileDescriptor), (r8v6 'cloudImage' android.content.res.AssetFileDescriptor), (r8v7 'cloudImage' android.content.res.AssetFileDescriptor) binds: [B:7:0x002b, B:12:0x0041, B:77:0x015c, B:50:0x0100] A[DONT_GENERATE, DONT_INLINE], Splitter:B:7:0x002b] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0278 A[SYNTHETIC, Splitter:B:171:0x0278] */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x0284 A[SYNTHETIC, Splitter:B:176:0x0284] */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0290 A[SYNTHETIC, Splitter:B:181:0x0290] */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x029c A[SYNTHETIC, Splitter:B:186:0x029c] */
    /* JADX WARNING: Removed duplicated region for block: B:247:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:248:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private byte[] loadImage(miui.yellowpage.YellowPageImgLoader.Image r19, boolean r20, int r21) {
        /*
            r18 = this;
            r1 = r18
            java.lang.String r2 = r19.getUrl()
            boolean r0 = android.text.TextUtils.isEmpty(r2)
            r3 = 0
            if (r0 == 0) goto L_0x000e
            return r3
        L_0x000e:
            r4 = 0
            android.content.Context r0 = r1.mContext     // Catch:{ Exception -> 0x031b }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ Exception -> 0x031b }
            android.net.Uri r5 = miui.yellowpage.YellowPageContract.ImageLookup.CONTENT_URI_IMAGE     // Catch:{ Exception -> 0x031b }
            java.lang.String r6 = r19.getName()     // Catch:{ Exception -> 0x031b }
            android.net.Uri r5 = android.net.Uri.withAppendedPath(r5, r6)     // Catch:{ Exception -> 0x031b }
            java.lang.String r6 = "w"
            android.content.res.AssetFileDescriptor r0 = r0.openAssetFileDescriptor(r5, r6)     // Catch:{ Exception -> 0x031b }
            r4 = r0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r0.<init>()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r6 = r0
            long r9 = r4.getLength()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r11 = 0
            int r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            r9 = -1
            r10 = 4096(0x1000, float:5.74E-42)
            r13 = 0
            if (r0 != 0) goto L_0x018b
            if (r20 == 0) goto L_0x018b
            android.net.Uri r0 = miui.yellowpage.YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_CLOUD     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r14 = java.net.URLEncoder.encode(r2)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri r0 = android.net.Uri.withAppendedPath(r0, r14)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri$Builder r0 = r0.buildUpon()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r14 = r0
            java.lang.String r0 = "timeout"
            java.lang.String r15 = java.lang.String.valueOf(r21)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r14.appendQueryParameter(r0, r15)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r0 = "overwrite_network_access"
            int r15 = r1.mNetworkAccess     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r15 = java.lang.String.valueOf(r15)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r14.appendQueryParameter(r0, r15)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            android.content.Context r0 = r1.mContext     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri r15 = r14.build()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r3 = "r"
            android.content.res.AssetFileDescriptor r0 = r0.openAssetFileDescriptor(r15, r3)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r8 = r0
            if (r8 == 0) goto L_0x011e
            long r16 = r8.getLength()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            int r0 = (r16 > r11 ? 1 : (r16 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x0081
            goto L_0x011e
        L_0x0081:
            java.io.FileInputStream r0 = r8.createInputStream()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r7 = r0
            byte[] r0 = new byte[r10]     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r3 = r13
        L_0x0089:
            int r10 = r7.read(r0)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r3 = r10
            if (r10 == r9) goto L_0x0094
            r6.write(r0, r13, r3)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            goto L_0x0089
        L_0x0094:
            byte[] r9 = r6.toByteArray()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r0.<init>()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r10 = r0
            r0 = 1
            r10.inJustDecodeBounds = r0     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            int r0 = r9.length     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            android.graphics.BitmapFactory.decodeByteArray(r9, r13, r0, r10)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r0 = r10.outMimeType     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            if (r0 != 0) goto L_0x0100
            java.lang.String r0 = r10.outMimeType     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r11 = "image"
            boolean r0 = r0.startsWith(r11)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            if (r0 == 0) goto L_0x0100
            int r0 = r9.length     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            if (r0 <= 0) goto L_0x00fe
            java.io.FileOutputStream r0 = r4.createOutputStream()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r5 = r0
            r5.write(r9)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r5.flush()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r5.close()     // Catch:{ IOException -> 0x00cb }
            goto L_0x00d1
        L_0x00cb:
            r0 = move-exception
            r11 = r0
            r0 = r11
            r0.printStackTrace()
        L_0x00d1:
            r6.close()     // Catch:{ IOException -> 0x00d6 }
            goto L_0x00dc
        L_0x00d6:
            r0 = move-exception
            r11 = r0
            r0 = r11
            r0.printStackTrace()
        L_0x00dc:
            r7.close()     // Catch:{ IOException -> 0x00e1 }
            goto L_0x00e7
        L_0x00e1:
            r0 = move-exception
            r11 = r0
            r0 = r11
            r0.printStackTrace()
        L_0x00e7:
            r8.close()     // Catch:{ IOException -> 0x00ec }
            goto L_0x00f2
        L_0x00ec:
            r0 = move-exception
            r11 = r0
            r0 = r11
            r0.printStackTrace()
        L_0x00f2:
            r4.close()     // Catch:{ IOException -> 0x00f7 }
            goto L_0x00fd
        L_0x00f7:
            r0 = move-exception
            r11 = r0
            r0 = r11
            r0.printStackTrace()
        L_0x00fd:
            return r9
        L_0x00fe:
            goto L_0x01ec
        L_0x0100:
            java.io.IOException r0 = new java.io.IOException     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r11.<init>()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r12 = "Invalid mime type ["
            r11.append(r12)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r12 = r10.outMimeType     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r11.append(r12)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r12 = "]"
            r11.append(r12)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r11 = r11.toString()     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            r0.<init>(r11)     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
            throw r0     // Catch:{ FileNotFoundException -> 0x015b, IOException -> 0x0266, Exception -> 0x022c }
        L_0x011e:
            if (r5 == 0) goto L_0x012b
            r5.close()     // Catch:{ IOException -> 0x0125 }
            goto L_0x012b
        L_0x0125:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x012b:
            r6.close()     // Catch:{ IOException -> 0x0130 }
            goto L_0x0136
        L_0x0130:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0136:
            if (r7 == 0) goto L_0x0142
            r7.close()     // Catch:{ IOException -> 0x013c }
            goto L_0x0142
        L_0x013c:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0142:
            if (r8 == 0) goto L_0x014e
            r8.close()     // Catch:{ IOException -> 0x0148 }
            goto L_0x014e
        L_0x0148:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x014e:
            r4.close()     // Catch:{ IOException -> 0x0153 }
            goto L_0x0159
        L_0x0153:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0159:
            r3 = 0
            return r3
        L_0x015b:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri r3 = miui.yellowpage.YellowPageContract.ImageLookup.CONTENT_URI_IMAGE_CLOUD     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r9 = android.net.Uri.encode(r2)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri r3 = android.net.Uri.withAppendedPath(r3, r9)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri$Builder r9 = r3.buildUpon()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            java.lang.String r10 = "fileName"
            java.lang.String r11 = r19.getName()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri$Builder r9 = r9.appendQueryParameter(r10, r11)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            android.net.Uri r11 = r9.build()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            android.content.Context r3 = r1.mContext     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            android.content.ContentResolver r10 = r3.getContentResolver()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r10.query(r11, r12, r13, r14, r15)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            goto L_0x01ec
        L_0x018b:
            long r14 = r4.getLength()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            int r0 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r0 <= 0) goto L_0x01ec
            java.io.FileInputStream r0 = r4.createInputStream()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r7 = r0
            byte[] r0 = new byte[r10]     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r3 = r0
            r0 = r13
        L_0x019c:
            int r10 = r7.read(r3)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r11 = r10
            if (r10 == r9) goto L_0x01a8
            r6.write(r3, r13, r11)     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            r0 = r11
            goto L_0x019c
        L_0x01a8:
            int r0 = r6.size()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            if (r0 <= 0) goto L_0x01ec
            byte[] r9 = r6.toByteArray()     // Catch:{ FileNotFoundException -> 0x02a1, IOException -> 0x0266, Exception -> 0x022c }
            if (r5 == 0) goto L_0x01be
            r5.close()     // Catch:{ IOException -> 0x01b8 }
            goto L_0x01be
        L_0x01b8:
            r0 = move-exception
            r10 = r0
            r0 = r10
            r0.printStackTrace()
        L_0x01be:
            r6.close()     // Catch:{ IOException -> 0x01c3 }
            goto L_0x01c9
        L_0x01c3:
            r0 = move-exception
            r10 = r0
            r0 = r10
            r0.printStackTrace()
        L_0x01c9:
            r7.close()     // Catch:{ IOException -> 0x01ce }
            goto L_0x01d4
        L_0x01ce:
            r0 = move-exception
            r10 = r0
            r0 = r10
            r0.printStackTrace()
        L_0x01d4:
            if (r8 == 0) goto L_0x01e0
            r8.close()     // Catch:{ IOException -> 0x01da }
            goto L_0x01e0
        L_0x01da:
            r0 = move-exception
            r10 = r0
            r0 = r10
            r0.printStackTrace()
        L_0x01e0:
            r4.close()     // Catch:{ IOException -> 0x01e5 }
            goto L_0x01eb
        L_0x01e5:
            r0 = move-exception
            r10 = r0
            r0 = r10
            r0.printStackTrace()
        L_0x01eb:
            return r9
        L_0x01ec:
            if (r5 == 0) goto L_0x01f8
            r5.close()     // Catch:{ IOException -> 0x01f2 }
            goto L_0x01f8
        L_0x01f2:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x01f8:
            r6.close()     // Catch:{ IOException -> 0x01fd }
            goto L_0x0203
        L_0x01fd:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0203:
            if (r7 == 0) goto L_0x020f
            r7.close()     // Catch:{ IOException -> 0x0209 }
            goto L_0x020f
        L_0x0209:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x020f:
            if (r8 == 0) goto L_0x021b
            r8.close()     // Catch:{ IOException -> 0x0215 }
            goto L_0x021b
        L_0x0215:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x021b:
            r4.close()     // Catch:{ IOException -> 0x0221 }
        L_0x021f:
            goto L_0x02dc
        L_0x0221:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
            goto L_0x021f
        L_0x0228:
            r0 = move-exception
            r3 = r0
            goto L_0x02de
        L_0x022c:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0228 }
            if (r5 == 0) goto L_0x023c
            r5.close()     // Catch:{ IOException -> 0x0236 }
            goto L_0x023c
        L_0x0236:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x023c:
            if (r6 == 0) goto L_0x0248
            r6.close()     // Catch:{ IOException -> 0x0242 }
            goto L_0x0248
        L_0x0242:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0248:
            if (r7 == 0) goto L_0x0254
            r7.close()     // Catch:{ IOException -> 0x024e }
            goto L_0x0254
        L_0x024e:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0254:
            if (r8 == 0) goto L_0x0260
            r8.close()     // Catch:{ IOException -> 0x025a }
            goto L_0x0260
        L_0x025a:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0260:
            if (r4 == 0) goto L_0x02dc
            r4.close()     // Catch:{ IOException -> 0x0221 }
            goto L_0x021f
        L_0x0266:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0228 }
            if (r5 == 0) goto L_0x0276
            r5.close()     // Catch:{ IOException -> 0x0270 }
            goto L_0x0276
        L_0x0270:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0276:
            if (r6 == 0) goto L_0x0282
            r6.close()     // Catch:{ IOException -> 0x027c }
            goto L_0x0282
        L_0x027c:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x0282:
            if (r7 == 0) goto L_0x028e
            r7.close()     // Catch:{ IOException -> 0x0288 }
            goto L_0x028e
        L_0x0288:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x028e:
            if (r8 == 0) goto L_0x029a
            r8.close()     // Catch:{ IOException -> 0x0294 }
            goto L_0x029a
        L_0x0294:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x029a:
            if (r4 == 0) goto L_0x02dc
            r4.close()     // Catch:{ IOException -> 0x0221 }
            goto L_0x021f
        L_0x02a1:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0228 }
            if (r5 == 0) goto L_0x02b1
            r5.close()     // Catch:{ IOException -> 0x02ab }
            goto L_0x02b1
        L_0x02ab:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x02b1:
            if (r6 == 0) goto L_0x02bd
            r6.close()     // Catch:{ IOException -> 0x02b7 }
            goto L_0x02bd
        L_0x02b7:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x02bd:
            if (r7 == 0) goto L_0x02c9
            r7.close()     // Catch:{ IOException -> 0x02c3 }
            goto L_0x02c9
        L_0x02c3:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x02c9:
            if (r8 == 0) goto L_0x02d5
            r8.close()     // Catch:{ IOException -> 0x02cf }
            goto L_0x02d5
        L_0x02cf:
            r0 = move-exception
            r3 = r0
            r0 = r3
            r0.printStackTrace()
        L_0x02d5:
            if (r4 == 0) goto L_0x02dc
            r4.close()     // Catch:{ IOException -> 0x0221 }
            goto L_0x021f
        L_0x02dc:
            r3 = 0
            return r3
        L_0x02de:
            if (r5 == 0) goto L_0x02ea
            r5.close()     // Catch:{ IOException -> 0x02e4 }
            goto L_0x02ea
        L_0x02e4:
            r0 = move-exception
            r9 = r0
            r0 = r9
            r0.printStackTrace()
        L_0x02ea:
            if (r6 == 0) goto L_0x02f6
            r6.close()     // Catch:{ IOException -> 0x02f0 }
            goto L_0x02f6
        L_0x02f0:
            r0 = move-exception
            r9 = r0
            r0 = r9
            r0.printStackTrace()
        L_0x02f6:
            if (r7 == 0) goto L_0x0302
            r7.close()     // Catch:{ IOException -> 0x02fc }
            goto L_0x0302
        L_0x02fc:
            r0 = move-exception
            r9 = r0
            r0 = r9
            r0.printStackTrace()
        L_0x0302:
            if (r8 == 0) goto L_0x030e
            r8.close()     // Catch:{ IOException -> 0x0308 }
            goto L_0x030e
        L_0x0308:
            r0 = move-exception
            r9 = r0
            r0 = r9
            r0.printStackTrace()
        L_0x030e:
            if (r4 == 0) goto L_0x031a
            r4.close()     // Catch:{ IOException -> 0x0314 }
            goto L_0x031a
        L_0x0314:
            r0 = move-exception
            r9 = r0
            r0 = r9
            r0.printStackTrace()
        L_0x031a:
            throw r3
        L_0x031b:
            r0 = move-exception
            java.lang.String r3 = "ImageLoader"
            java.lang.String r5 = "The yellowpage provider's image can not be written now"
            miui.yellowpage.Log.e(r3, r5, r0)
            r3 = 0
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.internal.yellowpage.ImageLoader.loadImage(miui.yellowpage.YellowPageImgLoader$Image, boolean, int):byte[]");
    }

    public static int getCurrentUiMode(Context context) {
        Resources resources;
        Configuration configuration;
        if (context == null || (resources = context.getApplicationContext().getResources()) == null || (configuration = resources.getConfiguration()) == null) {
            return -1;
        }
        return configuration.uiMode & 48;
    }
}
