package org.egret.plugin.mi.java.egretruntimelauncher;

import org.json.JSONObject;

public class Library
{
  private static final String JSON_LIBRARY_CHECKSUM = "md5";
  private static final String JSON_ZIP_CHECKSUM = "zip";
  private static final String JSON_ZIP_NAME = "name";
  private String libraryCheckSum;
  private String libraryName;
  private String url;
  private String zipCheckSum;
  private String zipName;
  
  public Library(JSONObject paramJSONObject, String paramString)
  {
    try
    {
      this.zipName = paramJSONObject.getString("name");
      this.libraryCheckSum = paramJSONObject.getString("md5");
      this.zipCheckSum = paramJSONObject.getString("zip");
      if (this.zipName == null)
      {
        this.libraryName = null;
        return;
      }
      this.url = getUrlBy(paramString, this.zipName);
      int i = this.zipName.lastIndexOf(".zip");
      if (i < 0) {
        paramJSONObject = null;
      } else {
        paramJSONObject = this.zipName.substring(0, i);
      }
      this.libraryName = paramJSONObject;
    }
    catch (Exception paramJSONObject)
    {
      paramJSONObject.printStackTrace();
      this.zipName = null;
      this.libraryName = null;
      this.libraryCheckSum = null;
      this.zipCheckSum = null;
    }
  }
  
  private String getUrlBy(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return null;
    }
    if (paramString1.endsWith("/"))
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString1);
      localStringBuilder.append(paramString2);
      return localStringBuilder.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString1);
    localStringBuilder.append("/");
    localStringBuilder.append(paramString2);
    return localStringBuilder.toString();
  }
  
  public String getLibraryCheckSum()
  {
    return this.libraryCheckSum;
  }
  
  public String getLibraryName()
  {
    return this.libraryName;
  }
  
  public String getUrl()
  {
    return this.url;
  }
  
  public String getZipCheckSum()
  {
    return this.zipCheckSum;
  }
  
  public String getZipName()
  {
    return this.zipName;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/egret/plugin/mi/java/egretruntimelauncher/Library.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */