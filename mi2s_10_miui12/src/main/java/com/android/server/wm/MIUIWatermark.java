package com.android.server.wm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Binder;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.server.content.MiSyncConstants;

class MIUIWatermark {
    private static volatile String mIMEI;
    private static MIUIWatermark mMIUIWatermark;
    private static SurfaceControl mSurfaceControl;
    private static int mTextSize = 0;
    private static int textSize = 13;
    private boolean DEBUG = false;
    private String TAG = "Watermark";
    private final int degree = 30;
    private String mAccountName;
    private final Display mDisplay;
    private boolean mDrawNeeded;
    /* access modifiers changed from: private */
    public boolean mImeiThreadisRuning = false;
    private int mLastDH = 0;
    private int mLastDW = 0;
    private final Surface mSurface = new Surface();
    private final Paint mTextPaint;

    public static MIUIWatermark initWatermark(WindowManagerService wms) {
        if (mMIUIWatermark == null) {
            synchronized (MIUIWatermark.class) {
                if (mMIUIWatermark == null) {
                    mMIUIWatermark = init(wms);
                }
            }
        }
        return mMIUIWatermark;
    }

    MIUIWatermark(DisplayContent dc, DisplayMetrics dm, WindowManagerService wms) {
        Account account = loadAccountId(wms);
        this.mAccountName = account != null ? account.name : null;
        mIMEI = getImei(wms.mContext);
        if (this.DEBUG) {
            String str = this.TAG;
            Log.i(str, "accountName:" + this.mAccountName + "imei" + mIMEI);
        }
        this.mDisplay = dc.getDisplay();
        this.mTextPaint = new Paint(1);
        this.mTextPaint.setTextSize((float) mTextSize);
        this.mTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, 0));
        this.mTextPaint.setColor(1358954495);
        this.mTextPaint.setShadowLayer(1.0f, 2.0f, 2.0f, 1342177280);
        SurfaceControl ctrl = null;
        try {
            ctrl = dc.makeOverlay().setName("MIUIWatermarkSurface").setBufferSize(1, 1).setFormat(-3).build();
            ctrl.setLayerStack(this.mDisplay.getLayerStack());
            ctrl.setLayer(1000000);
            ctrl.setPosition(0.0f, 0.0f);
            ctrl.show();
            this.mSurface.copyFrom(ctrl);
        } catch (Surface.OutOfResourcesException e) {
            String str2 = this.TAG;
            Log.d(str2, "createrSurface e" + e);
        }
        mSurfaceControl = ctrl;
    }

    /* access modifiers changed from: package-private */
    public void positionSurface(int dw, int dh) {
        if (this.mLastDW != dw || this.mLastDH != dh) {
            this.mLastDW = dw;
            this.mLastDH = dh;
            mSurfaceControl.setBufferSize(dw, dh);
            this.mDrawNeeded = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void showWaterMarker() {
        mSurfaceControl.show();
    }

    /* access modifiers changed from: package-private */
    public void hideWaterMarker() {
        mSurfaceControl.hide();
    }

    /* access modifiers changed from: package-private */
    public void updateText(WindowManagerService wms) {
        Account account = loadAccountId(wms);
        if (account != null) {
            this.mAccountName = account.name;
        }
        String imei = getImei(wms.mContext);
        if (imei != null) {
            mIMEI = imei;
        }
        this.mDrawNeeded = true;
    }

    /* access modifiers changed from: package-private */
    public void drawIfNeeded() {
        Canvas c;
        int dw;
        int accountNameLength;
        if (this.mDrawNeeded) {
            int incDeltx = this.mLastDW;
            int dh = this.mLastDH;
            Rect dirty = new Rect(0, 0, incDeltx, dh);
            Canvas c2 = null;
            try {
                c2 = this.mSurface.lockCanvas(dirty);
            } catch (Surface.OutOfResourcesException | IllegalArgumentException e) {
            }
            if (c2 != null) {
                c2.drawColor(0, PorterDuff.Mode.CLEAR);
                double radians = Math.toRadians(30.0d);
                int x = (int) (((double) incDeltx) * 0.6d);
                int y = (int) (((double) dh) * 0.05d);
                if (incDeltx > dh) {
                    x = (int) (((double) incDeltx) * 0.3d);
                }
                int incDeltx2 = (int) (((double) mTextSize) * Math.tan(radians));
                int incDelty = mTextSize * -1;
                int columnDeltX = incDeltx / 2;
                Rect rect = dirty;
                int columnDeltY = (int) (((double) dh) * 0.139d);
                double radians2 = radians;
                int deltLine = (int) (((double) dh) * 0.24d);
                int accountNameLength2 = 10;
                c2.rotate(330.0f, (float) (incDeltx / 2), (float) (dh / 2));
                int i = 0;
                while (i < 4) {
                    String str = this.mAccountName;
                    if (str != null) {
                        accountNameLength2 = str.length();
                        dw = incDeltx;
                        c2.drawText(this.mAccountName, (float) x, (float) y, this.mTextPaint);
                        if (mIMEI != null) {
                            c2.drawText(mIMEI, (float) (x - (((mIMEI.length() - accountNameLength2) * incDeltx2) / 2)), (float) (y - incDelty), this.mTextPaint);
                        }
                    } else {
                        dw = incDeltx;
                        if (mIMEI != null) {
                            c2.drawText(mIMEI, (float) (x - (((mIMEI.length() - accountNameLength2) * incDeltx2) / 2)), (float) (y - incDelty), this.mTextPaint);
                        }
                    }
                    String str2 = this.mAccountName;
                    if (str2 != null) {
                        c2.drawText(str2, (float) (x + columnDeltX), (float) (y + columnDeltY), this.mTextPaint);
                        if (mIMEI != null) {
                            c2.drawText(mIMEI, (float) ((x + columnDeltX) - (((mIMEI.length() - accountNameLength2) * incDeltx2) / 2)), (float) ((y + columnDeltY) - incDelty), this.mTextPaint);
                        }
                    } else if (mIMEI != null) {
                        c2.drawText(mIMEI, (float) ((x + columnDeltX) - (((mIMEI.length() - accountNameLength2) * incDeltx2) / 2)), (float) ((y + columnDeltY) - incDelty), this.mTextPaint);
                    }
                    if (dh == 0) {
                        int i2 = x;
                        c = c2;
                        accountNameLength = accountNameLength2;
                        int i3 = incDeltx2;
                        int i4 = incDelty;
                    } else if (deltLine == 0) {
                        int i5 = x;
                        c = c2;
                        accountNameLength = accountNameLength2;
                        int i6 = incDeltx2;
                        int i7 = incDelty;
                    } else {
                        int incDeltx3 = incDeltx2;
                        int i8 = x;
                        x = (int) (((double) x) - ((((double) dh) * Math.tan(radians2)) / ((double) (dh / deltLine))));
                        y += deltLine;
                        i++;
                        incDeltx2 = incDeltx3;
                        incDelty = incDelty;
                        incDeltx = dw;
                        c2 = c2;
                        accountNameLength2 = accountNameLength2;
                    }
                    String str3 = this.TAG;
                    Log.d(str3, "dh: " + dh + "deltLine: " + deltLine);
                    int i9 = accountNameLength;
                }
                int i10 = incDeltx;
                c = c2;
                int dw2 = incDeltx2;
                int i11 = incDelty;
                this.mSurface.unlockCanvasAndPost(c);
            } else {
                int dw3 = incDeltx;
                Rect rect2 = dirty;
            }
            this.mDrawNeeded = false;
        }
    }

    private static void createMIUIWatermarkInTransaction(WindowManagerService wms) {
        DisplayContent displayContent = wms.getDefaultDisplayContentLocked();
        mMIUIWatermark = new MIUIWatermark(displayContent, displayContent.mRealDisplayMetrics, wms);
    }

    private static Account loadAccountId(WindowManagerService wms) {
        Account[] accounts = AccountManager.get(wms.mContext).getAccountsByType(MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE);
        if (accounts == null || accounts.length <= 0) {
            return null;
        }
        return accounts[0];
    }

    private void getImeiInfo(final Context context) {
        String imei = getImeiID(context);
        if (imei == null) {
            synchronized (this) {
                if (!this.mImeiThreadisRuning) {
                    this.mImeiThreadisRuning = true;
                    new Thread() {
                        public void run() {
                            int time = 10;
                            while (true) {
                                if (time <= 0) {
                                    break;
                                }
                                try {
                                    String imei = MIUIWatermark.getImeiID(context);
                                    if (imei != null) {
                                        MIUIWatermark.this.setImei(imei);
                                        break;
                                    } else {
                                        sleep(2000);
                                        time--;
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (Throwable th) {
                                    boolean unused = MIUIWatermark.this.mImeiThreadisRuning = false;
                                    throw th;
                                }
                            }
                            boolean unused2 = MIUIWatermark.this.mImeiThreadisRuning = false;
                        }
                    }.start();
                    return;
                }
                return;
            }
        }
        setImei(imei);
    }

    /* access modifiers changed from: private */
    public static String getImeiID(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
    }

    /* access modifiers changed from: private */
    public void setImei(String imei) {
        mIMEI = imei;
        this.mDrawNeeded = true;
    }

    private String getImei(Context context) {
        getImeiInfo(context);
        return mIMEI;
    }

    /* JADX INFO: finally extract failed */
    private static MIUIWatermark init(WindowManagerService wms) {
        if (Binder.getCallingPid() != Process.myPid()) {
            return null;
        }
        MIUIWatermark mIUIWatermark = mMIUIWatermark;
        if (mIUIWatermark != null) {
            return mIUIWatermark;
        }
        mTextSize = dp2px(wms.mContext, (float) textSize);
        wms.openSurfaceTransaction();
        try {
            createMIUIWatermarkInTransaction(wms);
            wms.closeSurfaceTransaction("createWatermarkInTransaction");
            return mMIUIWatermark;
        } catch (Throwable th) {
            wms.closeSurfaceTransaction("createWatermarkInTransaction");
            throw th;
        }
    }

    private static int dp2px(Context context, float dpValue) {
        return (int) ((dpValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
