package b.b.c.d;

import android.view.View;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* renamed from: b.b.c.d.k  reason: case insensitive filesystem */
public class C0191k extends BaseAdapter {

    /* renamed from: a  reason: collision with root package name */
    protected ArrayList<C0185e> f1684a = new ArrayList<>();

    /* renamed from: b  reason: collision with root package name */
    protected ArrayList<Integer> f1685b = new ArrayList<>();

    /* renamed from: c  reason: collision with root package name */
    protected View.OnClickListener f1686c = new C0189i(this);

    /* renamed from: d  reason: collision with root package name */
    protected View.OnClickListener f1687d = new C0190j(this);

    public void a(C0185e eVar) {
        a(this.f1684a, eVar);
        notifyDataSetChanged();
    }

    public void a(C0185e eVar, List<C0185e> list, List<C0185e> list2) {
        int indexOf = this.f1684a.indexOf(eVar);
        if (indexOf > 0 && indexOf < this.f1684a.size() - 1) {
            for (C0185e remove : list) {
                this.f1684a.remove(remove);
            }
            for (C0185e add : list2) {
                indexOf++;
                this.f1684a.add(indexOf, add);
            }
        }
        notifyDataSetChanged();
    }

    public void a(List<C0185e> list) {
        this.f1684a.clear();
        if (list != null) {
            this.f1684a.addAll(list);
        }
        this.f1685b.clear();
        Iterator<C0185e> it = this.f1684a.iterator();
        while (it.hasNext()) {
            C0185e next = it.next();
            if (!this.f1685b.contains(Integer.valueOf(next.a()))) {
                this.f1685b.add(Integer.valueOf(next.a()));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void a(List<C0185e> list, C0185e eVar) {
        int indexOf = list.indexOf(eVar);
        if (indexOf > 0 && indexOf < list.size() - 1) {
            int i = indexOf - 1;
            int i2 = indexOf + 1;
            if (i >= 0 && i2 < list.size() && (list.get(i) instanceof p) && (list.get(i2) instanceof p)) {
                list.remove(i);
            }
        }
        list.remove(eVar);
    }

    public int getCount() {
        return this.f1684a.size();
    }

    public C0185e getItem(int i) {
        return this.f1684a.get(i);
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public int getItemViewType(int i) {
        return this.f1685b.indexOf(Integer.valueOf(getItem(i).a()));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: b.b.c.d.D} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v27, resolved type: b.b.c.d.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v29, resolved type: b.b.c.d.u} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v30, resolved type: b.b.c.d.x} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v31, resolved type: b.b.c.d.y} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v58, resolved type: b.b.c.d.G} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v138, resolved type: b.b.c.d.D} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v91, resolved type: b.b.c.d.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v92, resolved type: b.b.c.d.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v93, resolved type: b.b.c.d.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v94, resolved type: b.b.c.d.H} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: b.b.c.d.D} */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0284, code lost:
        r6.f1645a = (android.widget.TextView) r5.findViewById(com.miui.securitycenter.R.id.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x028c, code lost:
        r6.f1646b = (android.widget.TextView) r5.findViewById(com.miui.securitycenter.R.id.summary);
        r6.e = (android.widget.Button) r5.findViewById(com.miui.securitycenter.R.id.button);
        r4 = r6.e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x03d5, code lost:
        r6.setOnClickListener(r0.f1686c);
        r4.f1659a = r5.findViewById(com.miui.securitycenter.R.id.inner);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0421, code lost:
        r4.setOnClickListener(r0.f1686c);
        r6.f1659a = r5.findViewById(com.miui.securitycenter.R.id.inner);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x04fb, code lost:
        r2.setTag(r1);
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r17, android.view.View r18, android.view.ViewGroup r19) {
        /*
            r16 = this;
            r0 = r16
            b.b.c.d.e r1 = r16.getItem((int) r17)
            int r2 = r1.a()
            android.content.Context r3 = r19.getContext()
            r4 = 2131493539(0x7f0c02a3, float:1.861056E38)
            if (r18 != 0) goto L_0x0459
            r5 = 0
            android.view.View r5 = android.view.View.inflate(r3, r2, r5)
            r6 = 2131297778(0x7f0905f2, float:1.821351E38)
            r5.setTag(r6, r1)
            android.view.View$OnClickListener r6 = r0.f1687d
            r5.setOnClickListener(r6)
            r6 = 2131297019(0x7f0902fb, float:1.8211971E38)
            r7 = 2131296561(0x7f090131, float:1.8211042E38)
            r8 = 2131297740(0x7f0905cc, float:1.8213433E38)
            r9 = 2131297844(0x7f090634, float:1.8213644E38)
            if (r2 == r4) goto L_0x042d
            r10 = 2131297007(0x7f0902ef, float:1.8211947E38)
            r11 = 2131297006(0x7f0902ee, float:1.8211945E38)
            r12 = 2131296521(0x7f090109, float:1.8210961E38)
            r13 = 2131297005(0x7f0902ed, float:1.8211943E38)
            r14 = 2131297048(0x7f090318, float:1.821203E38)
            r15 = 2131296620(0x7f09016c, float:1.8211162E38)
            r4 = 2131296975(0x7f0902cf, float:1.8211882E38)
            switch(r2) {
                case 2131493444: goto L_0x03e2;
                case 2131493445: goto L_0x03ad;
                case 2131493446: goto L_0x0365;
                case 2131493447: goto L_0x033c;
                case 2131493448: goto L_0x02f7;
                case 2131493449: goto L_0x02be;
                default: goto L_0x0049;
            }
        L_0x0049:
            switch(r2) {
                case 2131493453: goto L_0x02a5;
                case 2131493454: goto L_0x02a5;
                case 2131493455: goto L_0x02a5;
                case 2131493456: goto L_0x02a5;
                case 2131493457: goto L_0x02a5;
                default: goto L_0x004c;
            }
        L_0x004c:
            r10 = 2131296520(0x7f090108, float:1.821096E38)
            switch(r2) {
                case 2131493520: goto L_0x0274;
                case 2131493521: goto L_0x0253;
                case 2131493522: goto L_0x0228;
                case 2131493523: goto L_0x0228;
                case 2131493524: goto L_0x0206;
                case 2131493525: goto L_0x018a;
                case 2131493526: goto L_0x0136;
                case 2131493527: goto L_0x0228;
                case 2131493528: goto L_0x0228;
                case 2131493529: goto L_0x0228;
                case 2131493530: goto L_0x011c;
                case 2131493531: goto L_0x0206;
                case 2131493532: goto L_0x0102;
                case 2131493533: goto L_0x00ca;
                default: goto L_0x0052;
            }
        L_0x0052:
            switch(r2) {
                case 2131493535: goto L_0x00b8;
                case 2131493536: goto L_0x0088;
                case 2131493537: goto L_0x0057;
                default: goto L_0x0055;
            }
        L_0x0055:
            goto L_0x045b
        L_0x0057:
            b.b.c.d.K r4 = new b.b.c.d.K
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1661a = r6
            r6 = 2131296676(0x7f0901a4, float:1.8211275E38)
            android.view.View r6 = r5.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1662b = r6
            r6 = 2131296499(0x7f0900f3, float:1.8210916E38)
            android.view.View r6 = r5.findViewById(r6)
            r4.f1663c = r6
            r6 = 2131296594(0x7f090152, float:1.821111E38)
            android.view.View r6 = r5.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1664d = r6
            goto L_0x045b
        L_0x0088:
            b.b.c.d.L r4 = new b.b.c.d.L
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1665a = r6
            r6 = 2131296644(0x7f090184, float:1.821121E38)
            android.view.View r6 = r5.findViewById(r6)
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            r4.f1666b = r6
            r6 = 2131297217(0x7f0903c1, float:1.8212373E38)
            android.view.View r6 = r5.findViewById(r6)
            r4.f1667c = r6
            android.view.View r6 = r5.findViewById(r7)
            android.widget.Button r6 = (android.widget.Button) r6
            r4.f1668d = r6
            android.widget.Button r4 = r4.f1668d
            goto L_0x029e
        L_0x00b8:
            b.b.c.d.J r4 = new b.b.c.d.J
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1660a = r6
            goto L_0x045b
        L_0x00ca:
            b.b.c.d.F r6 = new b.b.c.d.F
            r6.<init>()
            r5.setTag(r6)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1649a = r4
            android.view.View r4 = r5.findViewById(r9)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1650b = r4
            android.view.View r4 = r5.findViewById(r8)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1651c = r4
            r4 = 2131297308(0x7f09041c, float:1.8212557E38)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1652d = r4
            r4 = 2131298039(0x7f0906f7, float:1.821404E38)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.e = r4
            goto L_0x045b
        L_0x0102:
            b.b.c.d.C r6 = new b.b.c.d.C
            r6.<init>()
            r5.setTag(r6)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1640a = r4
            android.view.View r4 = r5.findViewById(r9)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1641b = r4
            goto L_0x045b
        L_0x011c:
            b.b.c.d.E r6 = new b.b.c.d.E
            r6.<init>()
            r5.setTag(r6)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1647c = r4
            android.view.View r4 = r5.findViewById(r10)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1648d = r4
            goto L_0x0284
        L_0x0136:
            b.b.c.d.A r4 = new b.b.c.d.A
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1633a = r6
            android.view.View r6 = r5.findViewById(r7)
            android.widget.Button r6 = (android.widget.Button) r6
            r4.f1634b = r6
            android.widget.ImageView[] r6 = r4.f1635c
            r7 = 0
            r8 = 2131296976(0x7f0902d0, float:1.8211884E38)
            android.view.View r8 = r5.findViewById(r8)
            android.widget.ImageView r8 = (android.widget.ImageView) r8
            r6[r7] = r8
            android.widget.ImageView[] r6 = r4.f1635c
            r7 = 1
            r8 = 2131296977(0x7f0902d1, float:1.8211886E38)
            android.view.View r8 = r5.findViewById(r8)
            android.widget.ImageView r8 = (android.widget.ImageView) r8
            r6[r7] = r8
            android.widget.ImageView[] r6 = r4.f1635c
            r7 = 2
            r8 = 2131296978(0x7f0902d2, float:1.8211888E38)
            android.view.View r8 = r5.findViewById(r8)
            android.widget.ImageView r8 = (android.widget.ImageView) r8
            r6[r7] = r8
            android.widget.ImageView[] r6 = r4.f1635c
            r7 = 3
            r8 = 2131296979(0x7f0902d3, float:1.821189E38)
            android.view.View r8 = r5.findViewById(r8)
            android.widget.ImageView r8 = (android.widget.ImageView) r8
            r6[r7] = r8
            android.widget.Button r4 = r4.f1634b
            goto L_0x029e
        L_0x018a:
            b.b.c.d.z r4 = new b.b.c.d.z
            r4.<init>()
            r5.setTag(r4)
            r6 = 2131296984(0x7f0902d8, float:1.82119E38)
            android.view.View r6 = r5.findViewById(r6)
            r4.f1704a = r6
            android.view.View r6 = r4.f1704a
            r7 = 2131297735(0x7f0905c7, float:1.8213423E38)
            android.view.View r6 = r6.findViewById(r7)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1707d = r6
            android.view.View r6 = r4.f1704a
            r8 = 2131297734(0x7f0905c6, float:1.8213421E38)
            android.view.View r6 = r6.findViewById(r8)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.g = r6
            android.view.View r6 = r4.f1704a
            android.view.View$OnClickListener r9 = r0.f1686c
            r6.setOnClickListener(r9)
            r6 = 2131296985(0x7f0902d9, float:1.8211902E38)
            android.view.View r6 = r5.findViewById(r6)
            r4.f1705b = r6
            android.view.View r6 = r4.f1705b
            android.view.View r6 = r6.findViewById(r7)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.e = r6
            android.view.View r6 = r4.f1705b
            android.view.View r6 = r6.findViewById(r8)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.h = r6
            android.view.View r6 = r4.f1705b
            android.view.View$OnClickListener r9 = r0.f1686c
            r6.setOnClickListener(r9)
            r6 = 2131296986(0x7f0902da, float:1.8211904E38)
            android.view.View r6 = r5.findViewById(r6)
            r4.f1706c = r6
            android.view.View r6 = r4.f1706c
            android.view.View r6 = r6.findViewById(r7)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f = r6
            android.view.View r6 = r4.f1706c
            android.view.View r6 = r6.findViewById(r8)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.i = r6
            android.view.View r4 = r4.f1706c
            android.view.View$OnClickListener r6 = r0.f1686c
            r4.setOnClickListener(r6)
            goto L_0x045b
        L_0x0206:
            b.b.c.d.B r4 = new b.b.c.d.B
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r6)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f1638c = r6
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1636a = r6
            android.view.View r6 = r5.findViewById(r8)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1637b = r6
            goto L_0x045b
        L_0x0228:
            b.b.c.d.B r4 = new b.b.c.d.B
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r6)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f1638c = r6
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1636a = r6
            android.view.View r6 = r5.findViewById(r8)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1637b = r6
            android.view.View r6 = r5.findViewById(r7)
            android.widget.Button r6 = (android.widget.Button) r6
            r4.f1639d = r6
            android.widget.Button r4 = r4.f1639d
            goto L_0x029e
        L_0x0253:
            b.b.c.d.w r6 = new b.b.c.d.w
            r6.<init>()
            r5.setTag(r6)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1647c = r4
            android.view.View r4 = r5.findViewById(r9)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1645a = r4
            android.view.View r4 = r5.findViewById(r10)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1648d = r4
            goto L_0x028c
        L_0x0274:
            b.b.c.d.v r6 = new b.b.c.d.v
            r6.<init>()
            r5.setTag(r6)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1647c = r4
        L_0x0284:
            android.view.View r4 = r5.findViewById(r9)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1645a = r4
        L_0x028c:
            android.view.View r4 = r5.findViewById(r8)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1646b = r4
            android.view.View r4 = r5.findViewById(r7)
            android.widget.Button r4 = (android.widget.Button) r4
            r6.e = r4
            android.widget.Button r4 = r6.e
        L_0x029e:
            android.view.View$OnClickListener r6 = r0.f1686c
            r4.setOnClickListener(r6)
            goto L_0x045b
        L_0x02a5:
            b.b.g.b r4 = b.b.c.d.o.a((android.view.View) r5, (b.b.c.d.C0185e) r1)
            r5.setTag(r4)
            boolean r6 = r4.j
            if (r6 == 0) goto L_0x045b
            android.view.View r6 = r4.g
            android.view.View$OnClickListener r7 = r0.f1686c
            r6.setOnClickListener(r7)
            android.view.View r4 = r4.g
            r4.bringToFront()
            goto L_0x045b
        L_0x02be:
            b.b.c.d.G r6 = new b.b.c.d.G
            r6.<init>()
            r5.setTag(r6)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1653b = r4
            android.view.View r4 = r5.findViewById(r9)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1654c = r4
            android.view.View r4 = r5.findViewById(r8)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1655d = r4
            android.view.View r4 = r5.findViewById(r7)
            android.widget.Button r4 = (android.widget.Button) r4
            r6.e = r4
            android.widget.Button r4 = r6.e
            android.view.View$OnClickListener r7 = r0.f1686c
            r4.setOnClickListener(r7)
            android.view.View r4 = r5.findViewById(r15)
            r6.f = r4
            android.view.View r4 = r6.f
            goto L_0x0421
        L_0x02f7:
            b.b.c.d.y r4 = new b.b.c.d.y
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1701b = r6
            android.view.View r6 = r5.findViewById(r8)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1702c = r6
            r6 = 2131296730(0x7f0901da, float:1.8211385E38)
            android.view.View r6 = r5.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1703d = r6
            android.view.View r6 = r5.findViewById(r13)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.e = r6
            android.view.View r6 = r5.findViewById(r11)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f = r6
            android.view.View r6 = r5.findViewById(r10)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.g = r6
            android.view.View r6 = r5.findViewById(r15)
            r4.h = r6
            android.view.View r6 = r4.h
            goto L_0x03d5
        L_0x033c:
            b.b.c.d.x r4 = new b.b.c.d.x
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r13)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f1698b = r6
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1699c = r6
            android.view.View r6 = r5.findViewById(r8)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1700d = r6
            android.view.View r6 = r5.findViewById(r15)
            r4.e = r6
            android.view.View r6 = r4.e
            goto L_0x03d5
        L_0x0365:
            b.b.c.d.u r4 = new b.b.c.d.u
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1695b = r6
            android.view.View r6 = r5.findViewById(r8)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1696c = r6
            android.view.View r6 = r5.findViewById(r13)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f1697d = r6
            android.view.View r6 = r5.findViewById(r11)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.e = r6
            android.view.View r6 = r5.findViewById(r10)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f = r6
            android.view.View r6 = r5.findViewById(r7)
            android.widget.Button r6 = (android.widget.Button) r6
            r4.g = r6
            android.widget.Button r6 = r4.g
            android.view.View$OnClickListener r7 = r0.f1686c
            r6.setOnClickListener(r7)
            android.view.View r6 = r5.findViewById(r15)
            r4.h = r6
            android.view.View r6 = r4.h
            goto L_0x03d5
        L_0x03ad:
            b.b.c.d.H r4 = new b.b.c.d.H
            r4.<init>()
            r5.setTag(r4)
            android.view.View r6 = r5.findViewById(r12)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f1658d = r6
            android.view.View r6 = r5.findViewById(r9)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1656b = r6
            android.view.View r6 = r5.findViewById(r8)
            android.widget.TextView r6 = (android.widget.TextView) r6
            r4.f1657c = r6
            android.view.View r6 = r5.findViewById(r15)
            r4.e = r6
            android.view.View r6 = r4.e
        L_0x03d5:
            android.view.View$OnClickListener r7 = r0.f1686c
            r6.setOnClickListener(r7)
            android.view.View r6 = r5.findViewById(r14)
            r4.f1659a = r6
            goto L_0x045b
        L_0x03e2:
            b.b.c.d.D r6 = new b.b.c.d.D
            r6.<init>()
            r5.setTag(r6)
            android.view.View r4 = r5.findViewById(r4)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1642b = r4
            android.view.View r4 = r5.findViewById(r9)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.f1644d = r4
            android.view.View r4 = r5.findViewById(r12)
            android.widget.ImageView r4 = (android.widget.ImageView) r4
            r6.f1643c = r4
            android.view.View r4 = r5.findViewById(r8)
            android.widget.TextView r4 = (android.widget.TextView) r4
            r6.e = r4
            android.view.View r4 = r5.findViewById(r7)
            android.widget.Button r4 = (android.widget.Button) r4
            r6.f = r4
            android.widget.Button r4 = r6.f
            android.view.View$OnClickListener r7 = r0.f1686c
            r4.setOnClickListener(r7)
            android.view.View r4 = r5.findViewById(r15)
            r6.g = r4
            android.view.View r4 = r6.g
        L_0x0421:
            android.view.View$OnClickListener r7 = r0.f1686c
            r4.setOnClickListener(r7)
            android.view.View r4 = r5.findViewById(r14)
            r6.f1659a = r4
            goto L_0x045b
        L_0x042d:
            b.b.c.d.M r4 = new b.b.c.d.M
            r4.<init>()
            r5.setTag(r4)
            android.view.View r9 = r5.findViewById(r9)
            android.widget.TextView r9 = (android.widget.TextView) r9
            r4.f1645a = r9
            android.view.View r8 = r5.findViewById(r8)
            android.widget.TextView r8 = (android.widget.TextView) r8
            r4.f1646b = r8
            android.view.View r6 = r5.findViewById(r6)
            android.widget.ImageView r6 = (android.widget.ImageView) r6
            r4.f1647c = r6
            android.view.View r6 = r5.findViewById(r7)
            android.widget.Button r6 = (android.widget.Button) r6
            r4.e = r6
            android.widget.Button r4 = r4.e
            goto L_0x029e
        L_0x0459:
            r5 = r18
        L_0x045b:
            r4 = 2131493539(0x7f0c02a3, float:1.861056E38)
            if (r2 == r4) goto L_0x0500
            switch(r2) {
                case 2131493444: goto L_0x04ee;
                case 2131493445: goto L_0x04e5;
                case 2131493446: goto L_0x04d7;
                case 2131493447: goto L_0x04ce;
                case 2131493448: goto L_0x04c5;
                case 2131493449: goto L_0x04b7;
                default: goto L_0x0463;
            }
        L_0x0463:
            switch(r2) {
                case 2131493453: goto L_0x04aa;
                case 2131493454: goto L_0x04aa;
                case 2131493455: goto L_0x04aa;
                case 2131493456: goto L_0x04aa;
                case 2131493457: goto L_0x04aa;
                default: goto L_0x0466;
            }
        L_0x0466:
            switch(r2) {
                case 2131493520: goto L_0x049e;
                case 2131493521: goto L_0x049e;
                case 2131493522: goto L_0x0495;
                case 2131493523: goto L_0x0495;
                case 2131493524: goto L_0x046c;
                case 2131493525: goto L_0x0482;
                case 2131493526: goto L_0x0479;
                case 2131493527: goto L_0x0495;
                case 2131493528: goto L_0x0495;
                case 2131493529: goto L_0x0495;
                case 2131493530: goto L_0x049e;
                case 2131493531: goto L_0x046c;
                default: goto L_0x0469;
            }
        L_0x0469:
            switch(r2) {
                case 2131493535: goto L_0x046c;
                case 2131493536: goto L_0x0470;
                case 2131493537: goto L_0x046c;
                default: goto L_0x046c;
            }
        L_0x046c:
            r2 = r17
            goto L_0x0507
        L_0x0470:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.L r2 = (b.b.c.d.L) r2
            android.widget.Button r2 = r2.f1668d
            goto L_0x04a6
        L_0x0479:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.A r2 = (b.b.c.d.A) r2
            android.widget.Button r2 = r2.f1634b
            goto L_0x04a6
        L_0x0482:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.z r2 = (b.b.c.d.z) r2
            android.view.View r4 = r2.f1704a
            r4.setTag(r1)
            android.view.View r4 = r2.f1705b
            r4.setTag(r1)
            android.view.View r2 = r2.f1706c
            goto L_0x04fb
        L_0x0495:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.B r2 = (b.b.c.d.B) r2
            android.widget.Button r2 = r2.f1639d
            goto L_0x04a6
        L_0x049e:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.E r2 = (b.b.c.d.E) r2
        L_0x04a4:
            android.widget.Button r2 = r2.e
        L_0x04a6:
            r2.setTag(r1)
            goto L_0x046c
        L_0x04aa:
            java.lang.Object r2 = r5.getTag()
            b.b.g.b r2 = (b.b.g.b) r2
            boolean r4 = r2.j
            if (r4 == 0) goto L_0x046c
            android.view.View r2 = r2.g
            goto L_0x04fb
        L_0x04b7:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.G r2 = (b.b.c.d.G) r2
            android.widget.Button r4 = r2.e
            r4.setTag(r1)
            android.view.View r2 = r2.f
            goto L_0x04fb
        L_0x04c5:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.y r2 = (b.b.c.d.y) r2
            android.view.View r2 = r2.h
            goto L_0x04fb
        L_0x04ce:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.x r2 = (b.b.c.d.x) r2
            android.view.View r2 = r2.e
            goto L_0x04fb
        L_0x04d7:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.u r2 = (b.b.c.d.u) r2
            android.widget.Button r4 = r2.g
            r4.setTag(r1)
            android.view.View r2 = r2.h
            goto L_0x04fb
        L_0x04e5:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.H r2 = (b.b.c.d.H) r2
            android.view.View r2 = r2.e
            goto L_0x04fb
        L_0x04ee:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.D r2 = (b.b.c.d.D) r2
            android.widget.Button r4 = r2.f
            r4.setTag(r1)
            android.view.View r2 = r2.g
        L_0x04fb:
            r2.setTag(r1)
            goto L_0x046c
        L_0x0500:
            java.lang.Object r2 = r5.getTag()
            b.b.c.d.M r2 = (b.b.c.d.M) r2
            goto L_0x04a4
        L_0x0507:
            r1.a(r2, r5, r3, r0)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.d.C0191k.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public int getViewTypeCount() {
        int size = this.f1685b.size();
        return size == 0 ? size + 1 : size;
    }
}
