package b.d.b.c;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncAdapterType;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

public class g {
    public static List<String> a(Context context, Account account) {
        ArrayList arrayList = new ArrayList();
        if (!(context == null || account == null)) {
            for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypes()) {
                if (syncAdapterType.isUserVisible() && TextUtils.equals(account.type, syncAdapterType.accountType)) {
                    String str = syncAdapterType.authority;
                    if (context.getPackageManager().resolveContentProvider(str, 128) != null) {
                        arrayList.add(str);
                    }
                }
            }
        }
        return arrayList;
    }
}
