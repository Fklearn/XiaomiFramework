package androidx.appcompat.widget;

import a.d.e.f;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

final class F {
    @NonNull

    /* renamed from: a  reason: collision with root package name */
    private TextView f477a;
    @Nullable

    /* renamed from: b  reason: collision with root package name */
    private TextClassifier f478b;

    F(@NonNull TextView textView) {
        f.a(textView);
        this.f477a = textView;
    }

    @RequiresApi(api = 26)
    @NonNull
    public TextClassifier a() {
        TextClassifier textClassifier = this.f478b;
        if (textClassifier != null) {
            return textClassifier;
        }
        TextClassificationManager textClassificationManager = (TextClassificationManager) this.f477a.getContext().getSystemService(TextClassificationManager.class);
        return textClassificationManager != null ? textClassificationManager.getTextClassifier() : TextClassifier.NO_OP;
    }

    @RequiresApi(api = 26)
    public void a(@Nullable TextClassifier textClassifier) {
        this.f478b = textClassifier;
    }
}
