package b.b.a.d.a;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import b.b.a.e.n;
import com.miui.antispam.ui.view.RecyclerViewExt;
import com.miui.maml.data.VariableNames;
import com.miui.securitycenter.R;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;

public final class k extends RecyclerViewExt.a<a> {
    private Context i;

    public static final class a extends RecyclerView.u {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public final TextView f1359a;

        /* renamed from: b  reason: collision with root package name */
        private final TextView f1360b;
        /* access modifiers changed from: private */

        /* renamed from: c  reason: collision with root package name */
        public final ImageView f1361c;

        private a(@NonNull View view) {
            super(view);
            this.f1359a = (TextView) view.findViewById(R.id.head1);
            this.f1360b = (TextView) view.findViewById(R.id.head2);
            this.f1361c = (ImageView) view.findViewById(R.id.sim_icon_item);
        }

        /* synthetic */ a(View view, i iVar) {
            this(view);
        }

        /* access modifiers changed from: private */
        public void a() {
            this.f1359a.setVisibility(8);
            this.f1360b.setVisibility(8);
            this.f1361c.setVisibility(8);
        }
    }

    public k(Context context) {
        super(context, (Cursor) null, 0);
        this.i = context;
    }

    private void a(TextView textView, CharSequence charSequence) {
        textView.post(new i(this, textView, charSequence));
    }

    public void a(a aVar, Cursor cursor, int i2) {
        ImageView b2;
        int i3;
        aVar.a();
        if (TelephonyManager.getDefault().isMultiSimEnabled()) {
            aVar.f1361c.setVisibility(0);
            int slotIdForSubscription = SubscriptionManager.getDefault().getSlotIdForSubscription((int) cursor.getLong(cursor.getColumnIndex("simid")));
            if (slotIdForSubscription == 0) {
                b2 = aVar.f1361c;
                i3 = R.drawable.sim1;
            } else if (1 == slotIdForSubscription) {
                b2 = aVar.f1361c;
                i3 = R.drawable.sim2;
            } else {
                b2 = aVar.f1361c;
                i3 = R.drawable.simx;
            }
            b2.setImageResource(i3);
        } else {
            aVar.f1361c.setVisibility(8);
        }
        a(aVar.f1359a, n.a(this.i, cursor.getLong(cursor.getColumnIndex(VariableNames.VAR_DATE)), false));
        aVar.itemView.setOnClickListener(new j(this));
    }

    /* access modifiers changed from: protected */
    public void b() {
    }

    @NonNull
    public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i2) {
        return new a(LayoutInflater.from(this.i).inflate(R.layout.fw_log_listitem, viewGroup, false), (i) null);
    }
}
