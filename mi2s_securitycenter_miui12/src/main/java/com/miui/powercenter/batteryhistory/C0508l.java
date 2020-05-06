package com.miui.powercenter.batteryhistory;

import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.miui.powercenter.utils.i;
import java.util.Observable;
import java.util.Observer;

/* renamed from: com.miui.powercenter.batteryhistory.l  reason: case insensitive filesystem */
public class C0508l extends RecyclerView.a<a> implements Observer, da {

    /* renamed from: a  reason: collision with root package name */
    private BatteryHistoryDetailActivity f6901a;

    /* renamed from: b  reason: collision with root package name */
    private oa f6902b;

    /* renamed from: c  reason: collision with root package name */
    private ga f6903c;

    /* renamed from: d  reason: collision with root package name */
    private ha f6904d;

    /* renamed from: com.miui.powercenter.batteryhistory.l$a */
    public static class a extends RecyclerView.u {
        public a(@NonNull View view) {
            super(view);
        }

        public void a() {
        }

        public void b() {
        }
    }

    public C0508l(BatteryHistoryDetailActivity batteryHistoryDetailActivity) {
        this.f6901a = batteryHistoryDetailActivity;
    }

    public void a() {
        this.f6903c.c();
    }

    /* renamed from: a */
    public void onViewRecycled(@NonNull a aVar) {
        aVar.b();
    }

    /* renamed from: a */
    public void onBindViewHolder(@NonNull a aVar, int i) {
        aVar.a();
    }

    public void b() {
        i.a().addObserver(this);
    }

    public void c() {
        i.a().deleteObserver(this);
    }

    public int getItemCount() {
        return 3;
    }

    public int getItemViewType(int i) {
        return i;
    }

    @NonNull
    public a onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 0) {
            if (this.f6902b == null) {
                this.f6902b = new oa(viewGroup, this.f6901a);
                this.f6902b.a((da) this);
            }
            return this.f6902b;
        } else if (i == 1) {
            if (this.f6903c == null) {
                this.f6903c = new ga(viewGroup, this.f6901a);
            }
            return this.f6903c;
        } else if (i != 2) {
            return null;
        } else {
            if (this.f6904d == null) {
                this.f6904d = new ha(viewGroup, viewGroup.getContext());
            }
            return this.f6904d;
        }
    }

    public void update(Observable observable, Object obj) {
        if (obj instanceof Message) {
            Message message = (Message) obj;
            int i = message.what;
            if (i == 10002) {
                this.f6901a.m();
            } else if (i == 10003) {
                this.f6902b.a(message.arg1, message.arg2);
            } else if (i == 10005) {
                this.f6902b.a(true);
            }
        }
    }
}
