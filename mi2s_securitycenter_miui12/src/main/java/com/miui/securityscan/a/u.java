package com.miui.securityscan.a;

class u implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f7597a;

    u(int i) {
        this.f7597a = i;
    }

    public void run() {
        String str;
        switch (this.f7597a) {
            case 1:
                str = "exit_dialog_garbage_clean";
                break;
            case 2:
                str = "exit_dialog_garbage_clean_cancel";
                break;
            case 3:
                str = "exit_dialog_garbage_clean_show";
                break;
            case 4:
                str = "exit_dialog_scan_optimize";
                break;
            case 5:
                str = "exit_dialog_scan_optimize_cancel";
                break;
            case 6:
                str = "exit_dialog_scan_optimize_show";
                break;
            case 7:
                str = "exit_dialog_release_storage";
                break;
            case 8:
                str = "exit_dialog_release_storage_cancel";
                break;
            case 9:
                str = "exit_dialog_release_storage_show";
                break;
            case 10:
                str = "exit_dialog_garbage_clean_scanresult_ok";
                break;
            case 11:
                str = "exit_dialog_garbage_clean_scanresult_cancel";
                break;
            case 12:
                str = "exit_dialog_garbage_clean_scanresult_show";
                break;
            default:
                return;
        }
        G.A(str);
    }
}
