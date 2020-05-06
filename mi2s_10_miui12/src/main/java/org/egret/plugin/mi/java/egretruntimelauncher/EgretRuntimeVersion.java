package org.egret.plugin.mi.java.egretruntimelauncher;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class EgretRuntimeVersion
{
  private static final String JSON_LIBRARY = "library";
  private static final String JSON_RUNTIME = "runtime";
  private static final String JSON_URL = "url";
  private ArrayList<Library> libraryList = new ArrayList();
  
  public void fromString(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      Object localObject1 = new org/json/JSONObject;
      Object localObject2 = new org/json/JSONTokener;
      ((JSONTokener)localObject2).<init>(paramString);
      ((JSONObject)localObject1).<init>((JSONTokener)localObject2);
      localObject2 = ((JSONObject)localObject1).getJSONObject("runtime");
      paramString = ((JSONObject)localObject2).getString("url");
      localObject1 = ((JSONObject)localObject2).getJSONArray("library");
      for (int i = 0; i < ((JSONArray)localObject1).length(); i++)
      {
        localObject2 = new org/egret/plugin/mi/java/egretruntimelauncher/Library;
        ((Library)localObject2).<init>((JSONObject)((JSONArray)localObject1).get(i), paramString);
        localArrayList.add(localObject2);
      }
      this.libraryList = localArrayList;
    }
    catch (JSONException paramString)
    {
      paramString.printStackTrace();
    }
  }
  
  public ArrayList<Library> getLibraryList()
  {
    return this.libraryList;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/java/egretruntimelauncher/EgretRuntimeVersion.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */