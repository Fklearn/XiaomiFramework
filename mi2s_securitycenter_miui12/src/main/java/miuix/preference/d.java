package miuix.preference;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DropDownPreference f8897a;

    d(DropDownPreference dropDownPreference) {
        this.f8897a = dropDownPreference;
    }

    public void run() {
        this.f8897a.f8879c.notifyDataSetChanged();
    }
}
