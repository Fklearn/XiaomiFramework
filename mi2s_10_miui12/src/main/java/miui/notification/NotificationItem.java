package miui.notification;

import android.app.PendingIntent;
import android.graphics.drawable.Drawable;

public class NotificationItem {
    public String action;
    public Drawable actionIcon;
    public PendingIntent clearIntent;
    public PendingIntent clickActionIntent;
    public PendingIntent clickIntent;
    public String content;
    public Drawable icon;
    public int id;
    public String title;

    public int getId() {
        return this.id;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public String getAction() {
        return this.action;
    }

    public void setId(int id2) {
        this.id = id2;
    }

    public void setIcon(Drawable icon2) {
        this.icon = icon2;
    }

    public void setTitle(String title2) {
        this.title = title2;
    }

    public void setContent(String content2) {
        this.content = content2;
    }

    public void setAction(String action2) {
        this.action = action2;
    }

    public PendingIntent getClickIntent() {
        return this.clickIntent;
    }

    public PendingIntent getClickActionIntent() {
        return this.clickActionIntent;
    }

    public PendingIntent getClearIntent() {
        return this.clearIntent;
    }

    public void setClickIntent(PendingIntent clickIntent2) {
        this.clickIntent = clickIntent2;
    }

    public void setClickActionIntent(PendingIntent clickActionIntent2) {
        this.clickActionIntent = clickActionIntent2;
    }

    public void setClearIntent(PendingIntent clearIntent2) {
        this.clearIntent = clearIntent2;
    }

    public Drawable getActionIcon() {
        return this.actionIcon;
    }

    public void setActionIcon(Drawable actionIcon2) {
        this.actionIcon = actionIcon2;
    }
}
