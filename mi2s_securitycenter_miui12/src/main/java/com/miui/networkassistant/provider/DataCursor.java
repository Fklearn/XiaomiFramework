package com.miui.networkassistant.provider;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import java.util.ArrayList;

public class DataCursor implements Cursor {
    private String[] mColumns;
    private int mCurrentRow = 0;
    private ArrayList<DataRow> mRows = new ArrayList<>();

    public static class DataEntry {
        private Double doubleValue;
        private Long longValue;
        private String strValue;

        public DataEntry(double d2) {
            this.doubleValue = Double.valueOf(d2);
        }

        public DataEntry(float f) {
            this.doubleValue = Double.valueOf((double) f);
        }

        public DataEntry(int i) {
            this.longValue = Long.valueOf((long) i);
        }

        public DataEntry(long j) {
            this.longValue = Long.valueOf(j);
        }

        public DataEntry(String str) {
            this.strValue = str;
        }

        public void clear() {
            this.strValue = null;
            this.doubleValue = null;
            this.longValue = null;
        }

        public double getDouble() {
            Double d2 = this.doubleValue;
            if (d2 != null) {
                return d2.doubleValue();
            }
            Long l = this.longValue;
            if (l != null) {
                l.doubleValue();
                return 0.0d;
            }
            String str = this.strValue;
            if (str == null) {
                return 0.0d;
            }
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException unused) {
                return 0.0d;
            }
        }

        public float getFloat() {
            Double d2 = this.doubleValue;
            if (d2 != null) {
                return d2.floatValue();
            }
            Long l = this.longValue;
            if (l != null) {
                l.floatValue();
                return 0.0f;
            }
            String str = this.strValue;
            if (str == null) {
                return 0.0f;
            }
            try {
                return Float.parseFloat(str);
            } catch (NumberFormatException unused) {
                return 0.0f;
            }
        }

        public int getInt() {
            Long l = this.longValue;
            if (l != null) {
                return l.intValue();
            }
            Double d2 = this.doubleValue;
            if (d2 != null) {
                return d2.intValue();
            }
            String str = this.strValue;
            if (str == null) {
                return 0;
            }
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException unused) {
                return 0;
            }
        }

        public long getLong() {
            Long l = this.longValue;
            if (l != null) {
                return l.longValue();
            }
            Double d2 = this.doubleValue;
            if (d2 != null) {
                return d2.longValue();
            }
            String str = this.strValue;
            if (str == null) {
                return 0;
            }
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException unused) {
                return 0;
            }
        }

        public short getShort() {
            return (short) getInt();
        }

        public String getString() {
            String str = this.strValue;
            if (str != null) {
                return str;
            }
            Double d2 = this.doubleValue;
            if (d2 != null) {
                return d2.toString();
            }
            Long l = this.longValue;
            if (l != null) {
                return l.toString();
            }
            return null;
        }

        public int getType() {
            if (this.strValue != null) {
                return 3;
            }
            if (this.doubleValue != null) {
                return 2;
            }
            return this.longValue != null ? 1 : 0;
        }

        public boolean isNull() {
            return getType() == 0;
        }

        public void set(double d2) {
            clear();
            this.doubleValue = Double.valueOf(d2);
        }

        public void set(float f) {
            clear();
            this.doubleValue = Double.valueOf((double) f);
        }

        public void set(int i) {
            clear();
            this.longValue = Long.valueOf((long) i);
        }

        public void set(long j) {
            clear();
            this.longValue = Long.valueOf(j);
        }

        public void set(String str) {
            clear();
            this.strValue = str;
        }
    }

    public static class DataRow {
        private DataEntry[] mEntrys;

        public DataRow(DataEntry... dataEntryArr) {
            this.mEntrys = dataEntryArr;
        }

        public int getCount() {
            return this.mEntrys.length;
        }

        public double getDouble(int i) {
            return this.mEntrys[i].getDouble();
        }

        public float getFloat(int i) {
            return this.mEntrys[i].getFloat();
        }

        public int getInt(int i) {
            return this.mEntrys[i].getInt();
        }

        public long getLong(int i) {
            return this.mEntrys[i].getLong();
        }

        public short getShort(int i) {
            return this.mEntrys[i].getShort();
        }

        public String getString(int i) {
            return this.mEntrys[i].getString();
        }

        public int getType(int i) {
            return this.mEntrys[i].getType();
        }

        public boolean isNull(int i) {
            DataEntry[] dataEntryArr = this.mEntrys;
            if (dataEntryArr[i] == null) {
                return true;
            }
            return dataEntryArr[i].isNull();
        }
    }

    public DataCursor(String... strArr) {
        this.mColumns = strArr;
    }

    private DataRow getCurrentRow() {
        return this.mRows.get(this.mCurrentRow);
    }

    public void addRow(DataRow dataRow) {
        if (dataRow.getCount() == this.mColumns.length) {
            this.mRows.add(dataRow);
            return;
        }
        throw new IllegalArgumentException("column count does not match");
    }

    public void close() {
        this.mRows.clear();
    }

    public void copyStringToBuffer(int i, CharArrayBuffer charArrayBuffer) {
    }

    public void deactivate() {
    }

    public byte[] getBlob(int i) {
        return null;
    }

    public int getColumnCount() {
        return this.mColumns.length;
    }

    public int getColumnIndex(String str) {
        try {
            return getColumnIndexOrThrow(str);
        } catch (IllegalArgumentException unused) {
            return -1;
        }
    }

    public int getColumnIndexOrThrow(String str) {
        int i = 0;
        while (true) {
            String[] strArr = this.mColumns;
            if (i >= strArr.length) {
                throw new IllegalArgumentException();
            } else if (strArr[i].equals(str)) {
                return i;
            } else {
                i++;
            }
        }
    }

    public String getColumnName(int i) {
        return this.mColumns[i];
    }

    public String[] getColumnNames() {
        return this.mColumns;
    }

    public int getCount() {
        return this.mRows.size();
    }

    public double getDouble(int i) {
        return getCurrentRow().getDouble(i);
    }

    public Bundle getExtras() {
        return null;
    }

    public float getFloat(int i) {
        return getCurrentRow().getFloat(i);
    }

    public int getInt(int i) {
        return getCurrentRow().getInt(i);
    }

    public long getLong(int i) {
        return getCurrentRow().getLong(i);
    }

    public Uri getNotificationUri() {
        return null;
    }

    public int getPosition() {
        return this.mCurrentRow;
    }

    public short getShort(int i) {
        return getCurrentRow().getShort(i);
    }

    public String getString(int i) {
        return getCurrentRow().getString(i);
    }

    public int getType(int i) {
        return getCurrentRow().getType(i);
    }

    public boolean getWantsAllOnMoveCalls() {
        return false;
    }

    public boolean isAfterLast() {
        return this.mCurrentRow >= this.mRows.size();
    }

    public boolean isBeforeFirst() {
        return this.mCurrentRow < 0;
    }

    public boolean isClosed() {
        return this.mRows.size() == 0;
    }

    public boolean isFirst() {
        return this.mCurrentRow == 0;
    }

    public boolean isLast() {
        return this.mCurrentRow == this.mRows.size() - 1;
    }

    public boolean isNull(int i) {
        return getCurrentRow().isNull(i);
    }

    public boolean move(int i) {
        return moveToPosition(this.mCurrentRow + i);
    }

    public boolean moveToFirst() {
        this.mCurrentRow = 0;
        return this.mRows.size() > 0;
    }

    public boolean moveToLast() {
        this.mCurrentRow = this.mRows.size() - 1;
        return this.mRows.size() > 0;
    }

    public boolean moveToNext() {
        return move(1);
    }

    public boolean moveToPosition(int i) {
        this.mCurrentRow = i;
        int i2 = this.mCurrentRow;
        if (i2 <= -1) {
            this.mCurrentRow = -1;
            return false;
        } else if (i2 < this.mRows.size()) {
            return true;
        } else {
            this.mCurrentRow = this.mRows.size();
            return false;
        }
    }

    public boolean moveToPrevious() {
        return move(this.mCurrentRow - 1);
    }

    public void registerContentObserver(ContentObserver contentObserver) {
    }

    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
    }

    public void removeRow(DataRow dataRow) {
        this.mRows.remove(dataRow);
    }

    public boolean requery() {
        return true;
    }

    public Bundle respond(Bundle bundle) {
        return null;
    }

    public void setExtras(Bundle bundle) {
    }

    public void setNotificationUri(ContentResolver contentResolver, Uri uri) {
    }

    public void unregisterContentObserver(ContentObserver contentObserver) {
    }

    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
    }
}
