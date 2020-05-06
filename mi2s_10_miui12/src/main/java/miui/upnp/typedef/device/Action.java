package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class Action implements Parcelable {
    public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() {
        public Action createFromParcel(Parcel in) {
            return new Action(in);
        }

        public Action[] newArray(int size) {
            return new Action[size];
        }
    };
    private List<Argument> arguments = new ArrayList();
    private String name;
    private Service service;

    public Service getService() {
        return this.service;
    }

    public void setService(Service service2) {
        this.service = service2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public List<Argument> getArguments() {
        return this.arguments;
    }

    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }

    public Action() {
    }

    public Action(String name2) {
        this.name = name2;
    }

    public Action(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            this.arguments.add(new Argument(in.readString(), in.readString(), in.readString()));
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeInt(this.arguments.size());
        for (Argument a : this.arguments) {
            out.writeString(a.getName());
            out.writeString(a.getDirection().toString());
            out.writeString(a.getRelatedProperty());
        }
    }
}
