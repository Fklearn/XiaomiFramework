package b.b.a.e;

import android.database.Cursor;
import android.database.CursorWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class o extends CursorWrapper {

    /* renamed from: a  reason: collision with root package name */
    private Cursor f1456a;

    /* renamed from: b  reason: collision with root package name */
    private List<a> f1457b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private int f1458c;

    public class a {

        /* renamed from: a  reason: collision with root package name */
        public String f1459a;

        /* renamed from: b  reason: collision with root package name */
        public int f1460b;

        public a() {
        }
    }

    public o(Cursor cursor, String str, Comparator<a> comparator) {
        super(cursor);
        int i = 0;
        this.f1458c = 0;
        this.f1456a = cursor;
        Cursor cursor2 = this.f1456a;
        if (cursor2 != null && cursor2.getCount() > 0) {
            this.f1456a.moveToFirst();
            while (!this.f1456a.isAfterLast()) {
                a aVar = new a();
                aVar.f1459a = cursor.getString(this.f1456a.getColumnIndex(str));
                aVar.f1460b = i;
                this.f1457b.add(aVar);
                this.f1456a.moveToNext();
                i++;
            }
            Collections.sort(this.f1457b, comparator);
        }
    }

    public int getPosition() {
        return this.f1458c;
    }

    public boolean move(int i) {
        return moveToPosition(this.f1458c + i);
    }

    public boolean moveToFirst() {
        return moveToPosition(0);
    }

    public boolean moveToLast() {
        return moveToPosition(getCount() - 1);
    }

    public boolean moveToNext() {
        return moveToPosition(this.f1458c + 1);
    }

    public boolean moveToPosition(int i) {
        if (i < 0 || i >= this.f1457b.size()) {
            if (i < 0) {
                this.f1458c = -1;
            }
            if (i > this.f1457b.size()) {
                this.f1458c = this.f1457b.size();
            }
        } else {
            this.f1458c = i;
            i = this.f1457b.get(i).f1460b;
        }
        return this.f1456a.moveToPosition(i);
    }

    public boolean moveToPrevious() {
        return moveToPosition(this.f1458c - 1);
    }
}
