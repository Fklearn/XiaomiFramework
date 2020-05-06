package android.view.textclassifier;

import com.android.internal.util.IndentingPrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public final class TextClassificationConstants
{
  private static final String CLASSIFY_TEXT_MAX_RANGE_LENGTH = "classify_text_max_range_length";
  private static final int CLASSIFY_TEXT_MAX_RANGE_LENGTH_DEFAULT = 10000;
  private static final List<String> CONVERSATION_ACTIONS_TYPES_DEFAULT_VALUES = Arrays.asList(new String[] { "text_reply", "create_reminder", "call_phone", "open_url", "send_email", "send_sms", "track_flight", "view_calendar", "view_map", "add_contact", "copy" });
  private static final String DETECT_LANGUAGES_FROM_TEXT_ENABLED = "detect_languages_from_text_enabled";
  private static final boolean DETECT_LANGUAGES_FROM_TEXT_ENABLED_DEFAULT = true;
  private static final String ENTITY_LIST_DEFAULT = "entity_list_default";
  private static final List<String> ENTITY_LIST_DEFAULT_VALUE = Arrays.asList(new String[] { "address", "email", "phone", "url", "date", "datetime", "flight" });
  private static final String ENTITY_LIST_EDITABLE = "entity_list_editable";
  private static final String ENTITY_LIST_NOT_EDITABLE = "entity_list_not_editable";
  private static final String GENERATE_LINKS_LOG_SAMPLE_RATE = "generate_links_log_sample_rate";
  private static final int GENERATE_LINKS_LOG_SAMPLE_RATE_DEFAULT = 100;
  private static final String GENERATE_LINKS_MAX_TEXT_LENGTH = "generate_links_max_text_length";
  private static final int GENERATE_LINKS_MAX_TEXT_LENGTH_DEFAULT = 100000;
  private static final String IN_APP_CONVERSATION_ACTION_TYPES_DEFAULT = "in_app_conversation_action_types_default";
  private static final String LANG_ID_CONTEXT_SETTINGS = "lang_id_context_settings";
  private static final float[] LANG_ID_CONTEXT_SETTINGS_DEFAULT = { 20.0F, 1.0F, 0.4F };
  private static final String LANG_ID_THRESHOLD_OVERRIDE = "lang_id_threshold_override";
  private static final float LANG_ID_THRESHOLD_OVERRIDE_DEFAULT = -1.0F;
  private static final String LOCAL_TEXT_CLASSIFIER_ENABLED = "local_textclassifier_enabled";
  private static final boolean LOCAL_TEXT_CLASSIFIER_ENABLED_DEFAULT = true;
  private static final String MODEL_DARK_LAUNCH_ENABLED = "model_dark_launch_enabled";
  private static final boolean MODEL_DARK_LAUNCH_ENABLED_DEFAULT = false;
  private static final String NOTIFICATION_CONVERSATION_ACTION_TYPES_DEFAULT = "notification_conversation_action_types_default";
  private static final String SMART_LINKIFY_ENABLED = "smart_linkify_enabled";
  private static final boolean SMART_LINKIFY_ENABLED_DEFAULT = true;
  private static final String SMART_SELECTION_ENABLED = "smart_selection_enabled";
  private static final boolean SMART_SELECTION_ENABLED_DEFAULT = true;
  private static final String SMART_SELECT_ANIMATION_ENABLED = "smart_select_animation_enabled";
  private static final boolean SMART_SELECT_ANIMATION_ENABLED_DEFAULT = true;
  private static final String SMART_TEXT_SHARE_ENABLED = "smart_text_share_enabled";
  private static final boolean SMART_TEXT_SHARE_ENABLED_DEFAULT = true;
  private static final String SUGGEST_SELECTION_MAX_RANGE_LENGTH = "suggest_selection_max_range_length";
  private static final int SUGGEST_SELECTION_MAX_RANGE_LENGTH_DEFAULT = 10000;
  private static final String SYSTEM_TEXT_CLASSIFIER_ENABLED = "system_textclassifier_enabled";
  private static final boolean SYSTEM_TEXT_CLASSIFIER_ENABLED_DEFAULT = true;
  private static final String TEMPLATE_INTENT_FACTORY_ENABLED = "template_intent_factory_enabled";
  private static final boolean TEMPLATE_INTENT_FACTORY_ENABLED_DEFAULT = true;
  private static final String TRANSLATE_IN_CLASSIFICATION_ENABLED = "translate_in_classification_enabled";
  private static final boolean TRANSLATE_IN_CLASSIFICATION_ENABLED_DEFAULT = true;
  private final ConfigParser mConfigParser;
  
  public TextClassificationConstants(Supplier<String> paramSupplier)
  {
    this.mConfigParser = new ConfigParser(paramSupplier);
  }
  
  void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("TextClassificationConstants:");
    paramIndentingPrintWriter.increaseIndent();
    paramIndentingPrintWriter.printPair("classify_text_max_range_length", Integer.valueOf(getClassifyTextMaxRangeLength())).println();
    paramIndentingPrintWriter.printPair("detect_language_from_text_enabled", Boolean.valueOf(isDetectLanguagesFromTextEnabled())).println();
    paramIndentingPrintWriter.printPair("entity_list_default", getEntityListDefault()).println();
    paramIndentingPrintWriter.printPair("entity_list_editable", getEntityListEditable()).println();
    paramIndentingPrintWriter.printPair("entity_list_not_editable", getEntityListNotEditable()).println();
    paramIndentingPrintWriter.printPair("generate_links_log_sample_rate", Integer.valueOf(getGenerateLinksLogSampleRate())).println();
    paramIndentingPrintWriter.printPair("generate_links_max_text_length", Integer.valueOf(getGenerateLinksMaxTextLength())).println();
    paramIndentingPrintWriter.printPair("in_app_conversation_action_types_default", getInAppConversationActionTypes()).println();
    paramIndentingPrintWriter.printPair("lang_id_context_settings", Arrays.toString(getLangIdContextSettings())).println();
    paramIndentingPrintWriter.printPair("lang_id_threshold_override", Float.valueOf(getLangIdThresholdOverride())).println();
    paramIndentingPrintWriter.printPair("local_textclassifier_enabled", Boolean.valueOf(isLocalTextClassifierEnabled())).println();
    paramIndentingPrintWriter.printPair("model_dark_launch_enabled", Boolean.valueOf(isModelDarkLaunchEnabled())).println();
    paramIndentingPrintWriter.printPair("notification_conversation_action_types_default", getNotificationConversationActionTypes()).println();
    paramIndentingPrintWriter.printPair("smart_linkify_enabled", Boolean.valueOf(isSmartLinkifyEnabled())).println();
    paramIndentingPrintWriter.printPair("smart_select_animation_enabled", Boolean.valueOf(isSmartSelectionAnimationEnabled())).println();
    paramIndentingPrintWriter.printPair("smart_selection_enabled", Boolean.valueOf(isSmartSelectionEnabled())).println();
    paramIndentingPrintWriter.printPair("smart_text_share_enabled", Boolean.valueOf(isSmartTextShareEnabled())).println();
    paramIndentingPrintWriter.printPair("suggest_selection_max_range_length", Integer.valueOf(getSuggestSelectionMaxRangeLength())).println();
    paramIndentingPrintWriter.printPair("system_textclassifier_enabled", Boolean.valueOf(isSystemTextClassifierEnabled())).println();
    paramIndentingPrintWriter.printPair("template_intent_factory_enabled", Boolean.valueOf(isTemplateIntentFactoryEnabled())).println();
    paramIndentingPrintWriter.printPair("translate_in_classification_enabled", Boolean.valueOf(isTranslateInClassificationEnabled())).println();
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  public int getClassifyTextMaxRangeLength()
  {
    return this.mConfigParser.getInt("classify_text_max_range_length", 10000);
  }
  
  public List<String> getEntityListDefault()
  {
    return this.mConfigParser.getStringList("entity_list_default", ENTITY_LIST_DEFAULT_VALUE);
  }
  
  public List<String> getEntityListEditable()
  {
    return this.mConfigParser.getStringList("entity_list_editable", ENTITY_LIST_DEFAULT_VALUE);
  }
  
  public List<String> getEntityListNotEditable()
  {
    return this.mConfigParser.getStringList("entity_list_not_editable", ENTITY_LIST_DEFAULT_VALUE);
  }
  
  public int getGenerateLinksLogSampleRate()
  {
    return this.mConfigParser.getInt("generate_links_log_sample_rate", 100);
  }
  
  public int getGenerateLinksMaxTextLength()
  {
    return this.mConfigParser.getInt("generate_links_max_text_length", 100000);
  }
  
  public List<String> getInAppConversationActionTypes()
  {
    return this.mConfigParser.getStringList("in_app_conversation_action_types_default", CONVERSATION_ACTIONS_TYPES_DEFAULT_VALUES);
  }
  
  public float[] getLangIdContextSettings()
  {
    return this.mConfigParser.getFloatArray("lang_id_context_settings", LANG_ID_CONTEXT_SETTINGS_DEFAULT);
  }
  
  public float getLangIdThresholdOverride()
  {
    return this.mConfigParser.getFloat("lang_id_threshold_override", -1.0F);
  }
  
  public List<String> getNotificationConversationActionTypes()
  {
    return this.mConfigParser.getStringList("notification_conversation_action_types_default", CONVERSATION_ACTIONS_TYPES_DEFAULT_VALUES);
  }
  
  public int getSuggestSelectionMaxRangeLength()
  {
    return this.mConfigParser.getInt("suggest_selection_max_range_length", 10000);
  }
  
  public boolean isDetectLanguagesFromTextEnabled()
  {
    return this.mConfigParser.getBoolean("detect_languages_from_text_enabled", true);
  }
  
  public boolean isLocalTextClassifierEnabled()
  {
    return this.mConfigParser.getBoolean("local_textclassifier_enabled", true);
  }
  
  public boolean isModelDarkLaunchEnabled()
  {
    return this.mConfigParser.getBoolean("model_dark_launch_enabled", false);
  }
  
  public boolean isSmartLinkifyEnabled()
  {
    return this.mConfigParser.getBoolean("smart_linkify_enabled", true);
  }
  
  public boolean isSmartSelectionAnimationEnabled()
  {
    return this.mConfigParser.getBoolean("smart_select_animation_enabled", true);
  }
  
  public boolean isSmartSelectionEnabled()
  {
    return this.mConfigParser.getBoolean("smart_selection_enabled", true);
  }
  
  public boolean isSmartTextShareEnabled()
  {
    return this.mConfigParser.getBoolean("smart_text_share_enabled", true);
  }
  
  public boolean isSystemTextClassifierEnabled()
  {
    return this.mConfigParser.getBoolean("system_textclassifier_enabled", true);
  }
  
  public boolean isTemplateIntentFactoryEnabled()
  {
    return this.mConfigParser.getBoolean("template_intent_factory_enabled", true);
  }
  
  public boolean isTranslateInClassificationEnabled()
  {
    return this.mConfigParser.getBoolean("translate_in_classification_enabled", true);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextClassificationConstants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */