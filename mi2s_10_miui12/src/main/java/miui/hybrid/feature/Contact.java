package miui.hybrid.feature;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.android.server.wifi.scanner.ChannelHelper;
import java.util.Map;
import miui.hybrid.HybridFeature;
import miui.hybrid.LifecycleListener;
import miui.hybrid.NativeInterface;
import miui.hybrid.Request;
import miui.hybrid.Response;
import miui.provider.Notes;
import miui.telephony.PhoneNumberUtils;
import miui.telephony.phonenumber.Prefix;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Contact implements HybridFeature {
    private static final String ACTION_LOOKUP = "lookup";
    private static final String ACTION_PICK_PHONE_NUMBER = "pickPhoneNumber";
    private static final String KEY_CONTACT_LIST = "contactList";
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final String KEY_PHONE_LABEL = "label";
    private static final String KEY_PHONE_NUMBER = "number";
    private static final String KEY_PHONE_TYPE = "type";
    private static final String PACKAGE_NAME_CONTACTS = "com.android.contacts";
    private static final String[] PROJECTION_PHONE = {"display_name", Notes.Data.DATA4, "data2", "data3"};
    private static final int REQUEST_CODE_PICK = 1;

    public void setParams(Map<String, String> map) {
    }

    public Response invoke(Request request) {
        if (TextUtils.equals(request.getAction(), ACTION_PICK_PHONE_NUMBER)) {
            return pickPhoneNumber(request);
        }
        if (TextUtils.equals(request.getAction(), ACTION_LOOKUP)) {
            return lookupContact(request);
        }
        return new Response(204, "no such action");
    }

    private Response lookupContact(final Request request) {
        try {
            String phoneNumber = normalizePhoneNumber(new JSONObject(request.getRawParams()).getString("phoneNumber"));
            final ContentResolver resolver = request.getNativeInterface().getActivity().getContentResolver();
            new AsyncTask<String, Void, JSONObject>() {
                /* access modifiers changed from: protected */
                public JSONObject doInBackground(String... params) {
                    return Contact.getContactsByPhoneNumber(resolver, params[0]);
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(JSONObject result) {
                    request.getCallback().callback(new Response(0, result));
                }
            }.execute(new String[]{phoneNumber});
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return new Response(204, "param not valid");
        }
    }

    /* access modifiers changed from: private */
    public static JSONObject getContactsByPhoneNumber(ContentResolver resolver, String phoneNumber) {
        JSONArray contactList = new JSONArray();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI.buildUpon().appendPath(phoneNumber).build(), PROJECTION_PHONE, (String) null, (String[]) null, (String) null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    JSONObject contact = new JSONObject();
                    contact.put(KEY_DISPLAY_NAME, cursor.getString(cursor.getColumnIndex("display_name")));
                    contact.put("number", cursor.getString(cursor.getColumnIndex(Notes.Data.DATA4)));
                    contact.put("type", cursor.getString(cursor.getColumnIndex("data2")));
                    contact.put("label", cursor.getString(cursor.getColumnIndex("data3")));
                    contactList.put(contact);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable th) {
                    cursor.close();
                    throw th;
                }
            }
            cursor.close();
        }
        JSONObject result = new JSONObject();
        try {
            result.put(KEY_CONTACT_LIST, contactList);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return result;
    }

    private static String normalizePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return Prefix.EMPTY;
        }
        PhoneNumberUtils.PhoneNumber pn = PhoneNumberUtils.PhoneNumber.parse(phoneNumber);
        String normalizedPhoneNumber = pn.getNormalizedNumber(false, true);
        pn.recycle();
        return normalizedPhoneNumber;
    }

    private Response pickPhoneNumber(final Request request) {
        final NativeInterface nativeInterface = request.getNativeInterface();
        Activity activity = nativeInterface.getActivity();
        nativeInterface.addLifecycleListener(new LifecycleListener() {
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                nativeInterface.removeLifecycleListener(this);
                if (requestCode != 1) {
                    return;
                }
                if (resultCode == -1) {
                    final ContentResolver resolver = request.getNativeInterface().getActivity().getContentResolver();
                    new AsyncTask<Uri, Void, JSONObject>() {
                        /* access modifiers changed from: protected */
                        public JSONObject doInBackground(Uri... params) {
                            return Contact.getContactPhoneNumber(resolver, params[0]);
                        }

                        /* access modifiers changed from: protected */
                        public void onPostExecute(JSONObject result) {
                            request.getCallback().callback(new Response(0, result));
                        }
                    }.execute(new Uri[]{data.getData()});
                } else if (resultCode == 0) {
                    request.getCallback().callback(new Response(100, "cancel"));
                } else {
                    request.getCallback().callback(new Response(ChannelHelper.SCAN_PERIOD_PER_CHANNEL_MS));
                }
            }
        });
        Intent intent = new Intent("android.intent.action.PICK", ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        intent.setType("vnd.android.cursor.dir/phone_v2");
        intent.setPackage("com.android.contacts");
        activity.startActivityForResult(intent, 1);
        return null;
    }

    /* access modifiers changed from: private */
    public static JSONObject getContactPhoneNumber(ContentResolver resolver, Uri uri) {
        Cursor cursor = resolver.query(uri, PROJECTION_PHONE, (String) null, (String[]) null, (String) null);
        JSONObject result = new JSONObject();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    result.put(KEY_DISPLAY_NAME, cursor.getString(cursor.getColumnIndex("display_name")));
                    result.put("number", cursor.getString(cursor.getColumnIndex(Notes.Data.DATA4)));
                    result.put("type", cursor.getString(cursor.getColumnIndex("data2")));
                    result.put("label", cursor.getString(cursor.getColumnIndex("data3")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Throwable th) {
                cursor.close();
                throw th;
            }
            cursor.close();
        }
        return result;
    }

    public HybridFeature.Mode getInvocationMode(Request request) {
        if (TextUtils.equals(request.getAction(), ACTION_LOOKUP) || TextUtils.equals(request.getAction(), ACTION_PICK_PHONE_NUMBER)) {
            return HybridFeature.Mode.CALLBACK;
        }
        return null;
    }
}
