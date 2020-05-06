package b.b.p;

import android.util.Log;
import b.b.p.f;
import com.miui.systemAdSolution.changeSkin.IChangeSkinService;
import com.miui.systemAdSolution.common.EnumPracle;
import com.xiaomi.ad.entity.unified.UnifiedAdCommandType;

class a extends f.a<String> {
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    a(f fVar, String str) {
        super(str);
        fVar.getClass();
    }

    /* access modifiers changed from: package-private */
    public String a(IChangeSkinService iChangeSkinService, String str) {
        EnumPracle enumPracle = new EnumPracle(UnifiedAdCommandType.SET_RECOMMAND_AD_SWITCH_STATE);
        Log.d("RemoteUnifiedAdService", "exe command.the command type is " + ((UnifiedAdCommandType) enumPracle.getValue()).name() + ";the arags is " + str);
        return iChangeSkinService.exec(enumPracle, str);
    }

    /* access modifiers changed from: package-private */
    public void a(String str) {
    }
}
