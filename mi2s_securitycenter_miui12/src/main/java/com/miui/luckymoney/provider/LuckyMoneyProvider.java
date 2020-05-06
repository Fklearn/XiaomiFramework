package com.miui.luckymoney.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import b.b.c.j.B;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.config.Constants;

public class LuckyMoneyProvider extends ContentProvider {
    private static final String AUTHORITY = "com.miui.luckymoney.provider";
    private static final int LUCKY_MONEY_ENABLE = 1;
    private static final int LUCKY_MONEY_FAST_OPEN = 3;
    private static final int LUCKY_MONEY_FLOAT = 2;
    private static final int LUCKY_MONEY_HB_INFO = 4;
    private static final String TABLE_COLUMN = "enable";
    private static final UriMatcher sUriMatcher = new UriMatcher(-1);
    private CommonConfig mCommonConfig;

    static {
        sUriMatcher.addURI(AUTHORITY, "lmEnable", 1);
        sUriMatcher.addURI(AUTHORITY, "lmFloat", 2);
        sUriMatcher.addURI(AUTHORITY, "lmFastOpen", 3);
        sUriMatcher.addURI(AUTHORITY, "lmInfo", 4);
    }

    private void sendConfigChangedBroadcast(String str) {
        Intent intent = new Intent(Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        intent.putExtra(Constants.KEY_CONFIG_CHANGED_FLAG, str);
        getContext().sendBroadcastAsUser(intent, B.b());
    }

    private int updateLuckyMoneyEnable(Uri uri, ContentValues contentValues) {
        if (uri == null || contentValues == null || !contentValues.containsKey(TABLE_COLUMN)) {
            return 0;
        }
        this.mCommonConfig.setXiaomiLuckyMoneyEnable(contentValues.getAsBoolean(TABLE_COLUMN).booleanValue());
        sendConfigChangedBroadcast(Constants.TYPE_LUCKY_OPEN);
        return 1;
    }

    private int updateLuckyMoneyFastOpen(Uri uri, ContentValues contentValues) {
        if (uri == null || contentValues == null || !contentValues.containsKey(TABLE_COLUMN)) {
            return 0;
        }
        this.mCommonConfig.setFastOpenEnable(contentValues.getAsBoolean(TABLE_COLUMN).booleanValue());
        sendConfigChangedBroadcast(Constants.TYPE_FAST_OPEN);
        return 1;
    }

    private int updateLuckyMoneyFloat(Uri uri, ContentValues contentValues) {
        if (uri == null || contentValues == null || !contentValues.containsKey(TABLE_COLUMN)) {
            return 0;
        }
        this.mCommonConfig.setDesktopFloatWindowEnable(contentValues.getAsBoolean(TABLE_COLUMN).booleanValue());
        sendConfigChangedBroadcast(Constants.TYPE_SHOW_FLOAT_WINDOW_BUTTON);
        return 1;
    }

    public int delete(Uri uri, String str, String[] strArr) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    public boolean onCreate() {
        this.mCommonConfig = CommonConfig.getInstance(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        Object[] objArr;
        MatrixCursor matrixCursor;
        int match = sUriMatcher.match(uri);
        if (match == 1) {
            matrixCursor = new MatrixCursor(new String[]{TABLE_COLUMN});
            objArr = new Object[]{Boolean.valueOf(this.mCommonConfig.getXiaomiLuckyMoneyEnable())};
        } else if (match == 2) {
            matrixCursor = new MatrixCursor(new String[]{TABLE_COLUMN});
            objArr = new Object[]{Boolean.valueOf(this.mCommonConfig.isDesktopFloatWindowEnable())};
        } else if (match == 3) {
            matrixCursor = new MatrixCursor(new String[]{TABLE_COLUMN});
            objArr = new Object[]{Boolean.valueOf(this.mCommonConfig.isFastOpenEnable())};
        } else if (match != 4) {
            return null;
        } else {
            MatrixCursor matrixCursor2 = new MatrixCursor(new String[]{"num_total", "money_total", "mm_money_total", "qq_money_total", "max_person", "max_group"});
            matrixCursor2.addRow(new Object[]{Long.valueOf(this.mCommonConfig.getWarningLuckyMoneyCount()), Long.valueOf(this.mCommonConfig.getReceiveTotalLuckyMoney()), Long.valueOf(this.mCommonConfig.getMMMoney()), Long.valueOf(this.mCommonConfig.getQQMoney()), this.mCommonConfig.getPersonalLuckyMaxSource(), this.mCommonConfig.getLuckyMaxSource()});
            return matrixCursor2;
        }
        matrixCursor.addRow(objArr);
        return matrixCursor;
    }

    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int match = sUriMatcher.match(uri);
        if (match == 1) {
            return updateLuckyMoneyEnable(uri, contentValues);
        }
        if (match == 2) {
            return updateLuckyMoneyFloat(uri, contentValues);
        }
        if (match != 3) {
            return 0;
        }
        return updateLuckyMoneyFastOpen(uri, contentValues);
    }
}
