package miuix.preference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import androidx.annotation.Nullable;
import miui.app.AlertDialog;

/* renamed from: miuix.preference.a  reason: case insensitive filesystem */
class C0578a extends AlertDialog.Builder {

    /* renamed from: a  reason: collision with root package name */
    private AlertDialog.Builder f8893a;

    public C0578a(Context context, int i, AlertDialog.Builder builder) {
        super(context, i);
        this.f8893a = builder;
    }

    public C0578a(Context context, AlertDialog.Builder builder) {
        this(context, 0, builder);
    }

    public AlertDialog.Builder setAdapter(ListAdapter listAdapter, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setAdapter(listAdapter, onClickListener);
        return this;
    }

    public AlertDialog.Builder setCancelable(boolean z) {
        this.f8893a.setCancelable(z);
        return this;
    }

    public AlertDialog.Builder setCursor(Cursor cursor, DialogInterface.OnClickListener onClickListener, String str) {
        this.f8893a.setCursor(cursor, onClickListener, str);
        return this;
    }

    public AlertDialog.Builder setCustomTitle(@Nullable View view) {
        this.f8893a.setCustomTitle(view);
        return this;
    }

    public AlertDialog.Builder setIcon(int i) {
        this.f8893a.setIcon(i);
        return this;
    }

    public AlertDialog.Builder setIcon(@Nullable Drawable drawable) {
        this.f8893a.setIcon(drawable);
        return this;
    }

    public AlertDialog.Builder setIconAttribute(int i) {
        this.f8893a.setIconAttribute(i);
        return this;
    }

    public AlertDialog.Builder setItems(int i, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setItems(i, onClickListener);
        return this;
    }

    public AlertDialog.Builder setItems(CharSequence[] charSequenceArr, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setItems(charSequenceArr, onClickListener);
        return this;
    }

    public AlertDialog.Builder setMessage(int i) {
        this.f8893a.setMessage(i);
        return this;
    }

    public AlertDialog.Builder setMessage(@Nullable CharSequence charSequence) {
        this.f8893a.setMessage(charSequence);
        return this;
    }

    public AlertDialog.Builder setMultiChoiceItems(int i, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.f8893a.setMultiChoiceItems(i, zArr, onMultiChoiceClickListener);
        return this;
    }

    public AlertDialog.Builder setMultiChoiceItems(Cursor cursor, String str, String str2, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.f8893a.setMultiChoiceItems(cursor, str, str2, onMultiChoiceClickListener);
        return this;
    }

    public AlertDialog.Builder setMultiChoiceItems(CharSequence[] charSequenceArr, boolean[] zArr, DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener) {
        this.f8893a.setMultiChoiceItems(charSequenceArr, zArr, onMultiChoiceClickListener);
        return this;
    }

    public AlertDialog.Builder setNegativeButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setNegativeButton(i, onClickListener);
        return this;
    }

    public AlertDialog.Builder setNegativeButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setNegativeButton(charSequence, onClickListener);
        return this;
    }

    public AlertDialog.Builder setNeutralButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setNeutralButton(i, onClickListener);
        return this;
    }

    public AlertDialog.Builder setNeutralButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setNeutralButton(charSequence, onClickListener);
        return this;
    }

    public AlertDialog.Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.f8893a.setOnCancelListener(onCancelListener);
        return this;
    }

    public AlertDialog.Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.f8893a.setOnDismissListener(onDismissListener);
        return this;
    }

    public AlertDialog.Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener onItemSelectedListener) {
        this.f8893a.setOnItemSelectedListener(onItemSelectedListener);
        return this;
    }

    public AlertDialog.Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.f8893a.setOnKeyListener(onKeyListener);
        return this;
    }

    public AlertDialog.Builder setPositiveButton(int i, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setPositiveButton(i, onClickListener);
        return this;
    }

    public AlertDialog.Builder setPositiveButton(CharSequence charSequence, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setPositiveButton(charSequence, onClickListener);
        return this;
    }

    public AlertDialog.Builder setSingleChoiceItems(int i, int i2, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setSingleChoiceItems(i, i2, onClickListener);
        return this;
    }

    public AlertDialog.Builder setSingleChoiceItems(Cursor cursor, int i, String str, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setSingleChoiceItems(cursor, i, str, onClickListener);
        return this;
    }

    public AlertDialog.Builder setSingleChoiceItems(ListAdapter listAdapter, int i, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setSingleChoiceItems(listAdapter, i, onClickListener);
        return this;
    }

    public AlertDialog.Builder setSingleChoiceItems(CharSequence[] charSequenceArr, int i, DialogInterface.OnClickListener onClickListener) {
        this.f8893a.setSingleChoiceItems(charSequenceArr, i, onClickListener);
        return this;
    }

    public AlertDialog.Builder setTitle(int i) {
        this.f8893a.setTitle(i);
        return this;
    }

    public AlertDialog.Builder setTitle(@Nullable CharSequence charSequence) {
        this.f8893a.setTitle(charSequence);
        return this;
    }

    public AlertDialog.Builder setView(int i) {
        this.f8893a.setView(i);
        return this;
    }

    public AlertDialog.Builder setView(View view) {
        this.f8893a.setView(view);
        return this;
    }
}
