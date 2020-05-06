package miui.cta;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.miui.system.internal.R;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;

public class CTAConfig {
    private static final String ABBREVIATE_PREFIX = ".";
    public static final CTAConfig EMPTY = new CTAConfig();
    private static final String TAG = "CTAConfig";
    private static final String TAG_ACTIVITIES = "activities";
    private static final String TAG_ACTIVITY = "activity";
    private ActivitiesNode mActivitiesNode;
    private HashMap<String, ActivityNode> mActivityNodes;

    private CTAConfig() {
        this.mActivitiesNode = new ActivitiesNode();
        this.mActivityNodes = new HashMap<>();
    }

    public CTAConfig(Context context, XmlResourceParser parser) {
        this();
        parseConfig(context, parser);
    }

    private void parseConfig(Context context, XmlResourceParser parser) {
        AttributeSet attrs = Xml.asAttributeSet(parser);
        boolean activityParsing = false;
        try {
            int event = parser.next();
            while (event != 1) {
                if (event == 2) {
                    String name = parser.getName();
                    if (TAG_ACTIVITIES.equals(name)) {
                        parseActivities(context, attrs);
                        activityParsing = true;
                    } else if (activityParsing && TAG_ACTIVITY.equals(name)) {
                        parseActivity(context, attrs);
                    }
                } else if (event == 3) {
                    if (TAG_ACTIVITIES.equals(parser.getName())) {
                        activityParsing = false;
                    }
                    event = parser.next();
                }
                event = parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Fail to parse CTA config", e);
        } catch (IOException e2) {
            Log.e(TAG, "Fail to parse CTA config", e2);
        }
    }

    private void parseActivities(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CTAActivitiesConfig);
        this.mActivitiesNode.enabled = a.getBoolean(R.styleable.CTAActivitiesConfig_enabled, true);
        this.mActivitiesNode.optional = a.getBoolean(R.styleable.CTAActivitiesConfig_optional, false);
        this.mActivitiesNode.messageId = a.getResourceId(R.styleable.CTAActivitiesConfig_message, 0);
        this.mActivitiesNode.permission = a.getInt(R.styleable.CTAActivitiesConfig_permission, 0);
        a.recycle();
    }

    private void parseActivity(Context context, AttributeSet attrs) {
        ActivityNode node = new ActivityNode();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CTAActivityConfig);
        node.name = a.getString(R.styleable.CTAActivityConfig_name);
        if (a.hasValue(R.styleable.CTAActivityConfig_enabled)) {
            node.enabled = a.getBoolean(R.styleable.CTAActivityConfig_enabled, true);
        } else {
            node.enabled = this.mActivitiesNode.enabled;
        }
        if (a.hasValue(R.styleable.CTAActivityConfig_optional)) {
            node.optional = a.getBoolean(R.styleable.CTAActivityConfig_optional, false);
        } else {
            node.optional = this.mActivitiesNode.optional;
        }
        node.messageId = a.getResourceId(R.styleable.CTAActivityConfig_message, 0);
        node.permission = a.getInt(R.styleable.CTAActivityConfig_permission, 0);
        if (node.messageId == 0 && node.permission == 0) {
            node.messageId = this.mActivitiesNode.messageId;
            node.permission = this.mActivitiesNode.permission;
        }
        a.recycle();
        String name = node.name;
        if (!TextUtils.isEmpty(node.name) && node.name.startsWith(ABBREVIATE_PREFIX)) {
            name = context.getPackageName() + name;
        }
        this.mActivityNodes.put(name, node);
    }

    public boolean canMatch() {
        if (this.mActivitiesNode.enabled) {
            return true;
        }
        for (ActivityNode activityNode : this.mActivityNodes.values()) {
            if (activityNode.enabled && !TextUtils.isEmpty(activityNode.name)) {
                return true;
            }
        }
        return false;
    }

    public MatchResult match(Class<? extends Activity> activityClass) {
        ActivityNode node = this.mActivityNodes.get(activityClass.getName());
        if (node != null) {
            if (node.enabled) {
                return new MatchResult(node);
            }
            return null;
        } else if (this.mActivitiesNode.enabled) {
            return new MatchResult(this.mActivitiesNode);
        } else {
            return null;
        }
    }

    public static class MatchResult {
        int messageId;
        boolean optional;
        int permission;

        MatchResult(ActivitiesNode node) {
            this.optional = node.optional;
            this.messageId = node.messageId;
            this.permission = node.permission;
        }
    }

    private static class ActivityNode extends ActivitiesNode {
        String name;

        private ActivityNode() {
            super();
        }
    }

    private static class ActivitiesNode {
        boolean enabled;
        int messageId;
        boolean optional;
        int permission;

        private ActivitiesNode() {
        }
    }
}
