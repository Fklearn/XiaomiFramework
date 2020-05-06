package miui.accounts;

import android.accounts.Account;
import android.accounts.OnAccountsUpdateListener;
import android.os.Bundle;

public interface MiuiOnAccountsUpdateListener extends OnAccountsUpdateListener {
    void onPostAccountUpdated(Account account, int i, Bundle bundle);

    void onPreAccountUpdated(Account account, int i, Bundle bundle);
}
