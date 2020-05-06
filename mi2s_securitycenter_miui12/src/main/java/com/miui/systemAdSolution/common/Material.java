package com.miui.systemAdSolution.common;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Material {
    private static final String KEY_AD_PASS_BACK = "ex";
    private static final String KEY_CLICK_MONITOR_URLS = "clickMonitorUrls";
    private static final String KEY_CUSTOM_MONITOR_URLS = "cusMoniUrls";
    private static final String KEY_EXTRA = "extra";
    private static final String KEY_ID = "id";
    private static final String KEY_RESOURCES = "resources";
    private static final String KEY_VIEW_LOG = "viewLogLevel";
    private static final String KEY_VIEW_MONITOR_URLS = "viewMonitorUrls";
    private static final String TAG = "Material";
    private static final int VIEW_LOG_LEVEL_MATERIAL = 0;
    private static final int VIEW_LOG_LEVEL_RESOURCE = 1;
    private String adPassBack;
    private String extra;
    private long id;
    private List<Resource> resources;
    private int viewLogLevel;
    private List<String> viewMonitorUrls;

    public static class Resource {
        private static final String KEY_DESCRIBE = "desc";
        private static final String KEY_LANDING_PAGE_URL = "landingPageUrl";
        private static final String KEY_RESOURCE_DATAS = "urls";
        private static final String KEY_TAG = "tag";
        private static final String KEY_TITLE = "title";
        private List<String> clickMonitorUrls;
        private Deeplink deeplink;
        private String describe;
        private String extra;
        private long id;
        private String landingPageUrl;
        private List<ResourceData> resourceDatas;
        private String tag;
        private String title;
        private List<String> viewMonitorUrls;

        public static class Deeplink {
            private static final String KEY_DEEPLINK_URL = "deeplinkUrl";
            private static final String KEY_PACKAGE_NAME = "packageName";
            private String deeplinkUrl;
            private String packageName;

            private Deeplink() {
            }

            public static Deeplink parse(JSONObject jSONObject) {
                if (jSONObject == null) {
                    return null;
                }
                String optString = jSONObject.optString(KEY_DEEPLINK_URL);
                String optString2 = jSONObject.optString(KEY_PACKAGE_NAME);
                Deeplink deeplink = new Deeplink();
                deeplink.packageName = optString2;
                deeplink.deeplinkUrl = optString;
                return deeplink;
            }

            public String getDeeplinkUrl() {
                return this.deeplinkUrl;
            }

            public String getPackageName() {
                return this.packageName;
            }

            public JSONObject serialize(JSONObject jSONObject) {
                if (jSONObject == null) {
                    jSONObject = new JSONObject();
                }
                try {
                    jSONObject.put(KEY_PACKAGE_NAME, this.packageName);
                    jSONObject.put(KEY_DEEPLINK_URL, this.deeplinkUrl);
                } catch (Exception e) {
                    Log.e(Material.TAG, "serializing the deeplink failed!", e);
                }
                return jSONObject;
            }
        }

        public static class ResourceData {
            private static final String KEY_LOCAL_PATH = "localPath";
            private static final String KEY_LOCAL_PATH_URI = "localPathUri";
            private static final String KEY_MD5 = "digest";
            private static final String KEY_URL = "url";
            private String mLocalPath;
            @Expose
            private String mLocalPathUri;
            private String md5;
            private String url;

            private ResourceData() {
            }

            public static ResourceData parse(JSONObject jSONObject) {
                if (jSONObject == null) {
                    return null;
                }
                String optString = jSONObject.optString("url");
                if (TextUtils.isEmpty(optString)) {
                    return null;
                }
                ResourceData resourceData = new ResourceData();
                resourceData.url = optString;
                resourceData.md5 = jSONObject.optString(KEY_MD5);
                String optString2 = jSONObject.optString(KEY_LOCAL_PATH);
                if (!TextUtils.isEmpty(optString2)) {
                    resourceData.mLocalPath = optString2;
                }
                String optString3 = jSONObject.optString(KEY_LOCAL_PATH_URI);
                if (!TextUtils.isEmpty(optString3)) {
                    resourceData.mLocalPathUri = optString3;
                }
                return resourceData;
            }

            public String getLocalPath() {
                return this.mLocalPath;
            }

            public String getLocalPathUri() {
                return this.mLocalPathUri;
            }

            public String getMd5() {
                return this.md5;
            }

            public String getUrl() {
                return this.url;
            }

            public JSONObject serialize() {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("url", this.url);
                    jSONObject.put(KEY_MD5, this.md5);
                    if (!TextUtils.isEmpty(this.mLocalPath)) {
                        jSONObject.put(KEY_LOCAL_PATH, this.mLocalPath);
                    }
                    if (!TextUtils.isEmpty(this.mLocalPathUri)) {
                        jSONObject.put(KEY_LOCAL_PATH_URI, this.mLocalPathUri);
                    }
                } catch (Exception e) {
                    Log.e(Material.TAG, "serializing the ResourceData failed!", e);
                }
                return jSONObject;
            }

            public void setLocalPath(String str) {
                this.mLocalPath = str;
            }

            public void setLocalPathUri(String str) {
                this.mLocalPathUri = str;
            }
        }

        private Resource() {
        }

        public static Resource parse(JSONObject jSONObject) {
            ResourceData parse;
            if (jSONObject == null) {
                return null;
            }
            Resource resource = new Resource();
            resource.id = jSONObject.optLong("id");
            resource.title = jSONObject.optString("title");
            resource.describe = jSONObject.optString(KEY_DESCRIBE);
            resource.landingPageUrl = jSONObject.optString(KEY_LANDING_PAGE_URL);
            resource.tag = jSONObject.optString(KEY_TAG);
            resource.deeplink = Deeplink.parse(jSONObject);
            resource.extra = jSONObject.optString(Material.KEY_EXTRA);
            resource.viewMonitorUrls = Material.parseMonitors(jSONObject.optJSONArray(Material.KEY_VIEW_MONITOR_URLS));
            resource.clickMonitorUrls = Material.parseMonitors(jSONObject.optJSONArray(Material.KEY_CLICK_MONITOR_URLS));
            resource.resourceDatas = new ArrayList();
            JSONArray optJSONArray = jSONObject.optJSONArray(KEY_RESOURCE_DATAS);
            if (optJSONArray != null) {
                for (int i = 0; i < optJSONArray.length(); i++) {
                    JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                    if (!(optJSONObject == null || (parse = ResourceData.parse(optJSONObject)) == null)) {
                        resource.resourceDatas.add(parse);
                    }
                }
            }
            return resource;
        }

        public List<String> getClickMonitorUrls() {
            return this.clickMonitorUrls;
        }

        public Deeplink getDeeplink() {
            return this.deeplink;
        }

        public String getDescribe() {
            return this.describe;
        }

        public String getExtra() {
            return this.extra;
        }

        public long getId() {
            return this.id;
        }

        public String getLandingPageUrl() {
            return this.landingPageUrl;
        }

        public ResourceData getResourceData(int i) {
            if (getResourceDataCount() > i) {
                return this.resourceDatas.get(i);
            }
            return null;
        }

        public int getResourceDataCount() {
            List<ResourceData> list = this.resourceDatas;
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        public List<ResourceData> getResourceDatas() {
            return this.resourceDatas;
        }

        public String getTag() {
            return this.tag;
        }

        public String getTitle() {
            return this.title;
        }

        public List<String> getViewMonitorUrls() {
            return this.viewMonitorUrls;
        }

        public JSONObject serialize() {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("id", this.id);
                jSONObject.put("title", this.title);
                jSONObject.put(KEY_DESCRIBE, this.describe);
                jSONObject.put(KEY_LANDING_PAGE_URL, this.landingPageUrl);
                jSONObject.put(KEY_TAG, this.tag);
                if (this.deeplink != null) {
                    this.deeplink.serialize(jSONObject);
                }
                jSONObject.put(Material.KEY_EXTRA, this.extra);
                JSONArray jSONArray = new JSONArray();
                if (this.viewMonitorUrls != null && !this.viewMonitorUrls.isEmpty()) {
                    for (String put : this.viewMonitorUrls) {
                        jSONArray.put(put);
                    }
                }
                jSONObject.put(Material.KEY_VIEW_MONITOR_URLS, jSONArray);
                JSONArray jSONArray2 = new JSONArray();
                if (this.clickMonitorUrls != null && !this.clickMonitorUrls.isEmpty()) {
                    for (String put2 : this.clickMonitorUrls) {
                        jSONArray2.put(put2);
                    }
                }
                jSONObject.put(Material.KEY_CLICK_MONITOR_URLS, jSONArray2);
                JSONArray jSONArray3 = new JSONArray();
                if (this.resourceDatas != null) {
                    for (ResourceData serialize : this.resourceDatas) {
                        jSONArray3.put(serialize.serialize());
                    }
                }
                jSONObject.put(KEY_RESOURCE_DATAS, jSONArray3);
            } catch (Exception e) {
                Log.e(Material.TAG, "serializing the ResourceData failed!", e);
            }
            return jSONObject;
        }

        public void setClickMonitorUrls(List<String> list) {
            this.clickMonitorUrls = list;
        }

        public void setViewMonitorUrls(List<String> list) {
            this.viewMonitorUrls = list;
        }
    }

    private Material() {
    }

    public static Material parse(JSONObject jSONObject) {
        return parse(jSONObject, false);
    }

    public static Material parse(JSONObject jSONObject, boolean z) {
        Resource parse;
        if (jSONObject == null) {
            return null;
        }
        Material material = new Material();
        material.resources = new ArrayList();
        JSONArray optJSONArray = jSONObject.optJSONArray(KEY_RESOURCES);
        if (optJSONArray != null) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                if (!(optJSONObject == null || (parse = Resource.parse(optJSONObject)) == null || (z && parse.getResourceDataCount() <= 0))) {
                    material.resources.add(parse);
                }
            }
        }
        if (material.resources.isEmpty()) {
            Log.w(TAG, "no resources in material:" + material);
            return null;
        }
        material.id = jSONObject.optLong("id");
        material.viewLogLevel = jSONObject.optInt(KEY_VIEW_LOG, 0);
        material.adPassBack = jSONObject.optString(KEY_AD_PASS_BACK);
        material.extra = jSONObject.optString(KEY_EXTRA);
        material.viewMonitorUrls = parseMonitors(jSONObject.optJSONArray(KEY_VIEW_MONITOR_URLS));
        return material;
    }

    public static List<String> parseMonitors(JSONArray jSONArray) {
        ArrayList arrayList = new ArrayList();
        if (jSONArray != null) {
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                String optString = jSONArray.optString(i);
                if (!TextUtils.isEmpty(optString) && optString.startsWith("http")) {
                    arrayList.add(optString);
                }
            }
        }
        return arrayList;
    }

    public String getAdPassBack() {
        return this.adPassBack;
    }

    public String getExtra() {
        return this.extra;
    }

    public long getId() {
        return this.id;
    }

    public Resource getResource(int i) {
        if (getResourceCount() > i) {
            return this.resources.get(i);
        }
        return null;
    }

    public int getResourceCount() {
        return this.resources.size();
    }

    public List<Resource> getResources() {
        return this.resources;
    }

    public List<String> getViewMonitorUrls() {
        return this.viewMonitorUrls;
    }

    public boolean isMaterialViewLogLevel() {
        return this.viewLogLevel == 0;
    }

    public boolean isResourceViewLogLevel() {
        return this.viewLogLevel == 1;
    }

    public JSONObject serialize() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("id", this.id);
            jSONObject.put(KEY_VIEW_LOG, this.viewLogLevel);
            jSONObject.put(KEY_AD_PASS_BACK, this.adPassBack);
            jSONObject.put(KEY_EXTRA, this.extra);
            JSONArray jSONArray = new JSONArray();
            if (this.viewMonitorUrls != null && !this.viewMonitorUrls.isEmpty()) {
                for (String put : this.viewMonitorUrls) {
                    jSONArray.put(put);
                }
            }
            jSONObject.put(KEY_VIEW_MONITOR_URLS, jSONArray);
            JSONArray jSONArray2 = new JSONArray();
            if (this.resources != null) {
                for (Resource serialize : this.resources) {
                    jSONArray2.put(serialize.serialize());
                }
            }
            jSONObject.put(KEY_RESOURCES, jSONArray2);
        } catch (Exception e) {
            Log.e(TAG, "serializing the ResourceData failed!", e);
        }
        return jSONObject;
    }

    public void setAdPassBack(String str) {
        this.adPassBack = str;
    }

    public void setViewMonitorUrls(List<String> list) {
        this.viewMonitorUrls = list;
    }
}
