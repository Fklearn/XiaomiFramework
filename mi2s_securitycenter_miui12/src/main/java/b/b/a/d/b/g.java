package b.b.a.d.b;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import com.miui.securitycenter.R;

class g implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f1389a;

    g(i iVar) {
        this.f1389a = iVar;
    }

    public void onClick(View view) {
        new AlertDialog.Builder(this.f1389a.f1394c).setTitle(R.string.dlg_no_block_ok).setMessage(R.string.dlg_no_block).setPositiveButton(R.string.dlg_no_block_ok, new f(this)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).show();
    }
}
