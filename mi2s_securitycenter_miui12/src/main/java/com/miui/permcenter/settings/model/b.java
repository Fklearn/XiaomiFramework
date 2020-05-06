package com.miui.permcenter.settings.model;

import com.miui.permcenter.settings.model.d;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;
import java.util.HashMap;

class b implements d.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DangerPermissionPreference f6543a;

    b(DangerPermissionPreference dangerPermissionPreference) {
        this.f6543a = dangerPermissionPreference;
    }

    public void a(HashMap<Long, Integer> hashMap) {
        if (hashMap != null) {
            int intValue = hashMap.containsKey(32L) ? hashMap.get(32L).intValue() : 0;
            int intValue2 = hashMap.containsKey(8L) ? hashMap.get(8L).intValue() : 0;
            int intValue3 = hashMap.containsKey(16L) ? hashMap.get(16L).intValue() : 0;
            int intValue4 = hashMap.containsKey(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER)) ? hashMap.get(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER)).intValue() : 0;
            int intValue5 = hashMap.containsKey(Long.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE)) ? hashMap.get(Long.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE)).intValue() : 0;
            this.f6543a.f6533a.setText(intValue == 0 ? this.f6543a.mContext.getResources().getString(R.string.privacy_permission_summary_none) : this.f6543a.mContext.getResources().getQuantityString(R.plurals.privacy_permission_summary, intValue, new Object[]{String.valueOf(intValue)}));
            this.f6543a.f6534b.setText(intValue2 == 0 ? this.f6543a.mContext.getResources().getString(R.string.privacy_permission_summary_none) : this.f6543a.mContext.getResources().getQuantityString(R.plurals.privacy_permission_summary, intValue2, new Object[]{String.valueOf(intValue2)}));
            this.f6543a.f6535c.setText(intValue3 == 0 ? this.f6543a.mContext.getResources().getString(R.string.privacy_permission_summary_none) : this.f6543a.mContext.getResources().getQuantityString(R.plurals.privacy_permission_summary, intValue3, new Object[]{String.valueOf(intValue3)}));
            this.f6543a.f6536d.setText(intValue4 == 0 ? this.f6543a.mContext.getResources().getString(R.string.privacy_permission_summary_none) : this.f6543a.mContext.getResources().getQuantityString(R.plurals.privacy_permission_summary, intValue4, new Object[]{String.valueOf(intValue4)}));
            this.f6543a.e.setText(intValue5 == 0 ? this.f6543a.mContext.getResources().getString(R.string.privacy_permission_summary_none) : this.f6543a.mContext.getResources().getQuantityString(R.plurals.privacy_permission_summary, intValue5, new Object[]{String.valueOf(intValue5)}));
        }
    }
}
