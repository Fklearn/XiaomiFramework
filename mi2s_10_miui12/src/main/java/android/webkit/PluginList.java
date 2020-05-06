package android.webkit;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class PluginList
{
  private ArrayList<Plugin> mPlugins = new ArrayList();
  
  @Deprecated
  public void addPlugin(Plugin paramPlugin)
  {
    try
    {
      if (!this.mPlugins.contains(paramPlugin)) {
        this.mPlugins.add(paramPlugin);
      }
      return;
    }
    finally
    {
      paramPlugin = finally;
      throw paramPlugin;
    }
  }
  
  @Deprecated
  public void clear()
  {
    try
    {
      this.mPlugins.clear();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  @Deprecated
  public List getList()
  {
    try
    {
      ArrayList localArrayList = this.mPlugins;
      return localArrayList;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  @Deprecated
  public void pluginClicked(android.content.Context paramContext, int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 17	android/webkit/PluginList:mPlugins	Ljava/util/ArrayList;
    //   6: astore_3
    //   7: aload_3
    //   8: iload_2
    //   9: invokevirtual 41	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   12: checkcast 43	android/webkit/Plugin
    //   15: aload_1
    //   16: invokevirtual 47	android/webkit/Plugin:dispatchClickEvent	(Landroid/content/Context;)V
    //   19: goto +13 -> 32
    //   22: astore_1
    //   23: goto +9 -> 32
    //   26: astore_1
    //   27: aload_0
    //   28: monitorexit
    //   29: aload_1
    //   30: athrow
    //   31: astore_1
    //   32: aload_0
    //   33: monitorexit
    //   34: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	35	0	this	PluginList
    //   0	35	1	paramContext	android.content.Context
    //   0	35	2	paramInt	int
    //   6	2	3	localArrayList	ArrayList
    // Exception table:
    //   from	to	target	type
    //   7	19	22	java/lang/IndexOutOfBoundsException
    //   2	7	26	finally
    //   7	19	26	finally
    //   2	7	31	java/lang/IndexOutOfBoundsException
  }
  
  @Deprecated
  public void removePlugin(Plugin paramPlugin)
  {
    try
    {
      int i = this.mPlugins.indexOf(paramPlugin);
      if (i != -1) {
        this.mPlugins.remove(i);
      }
      return;
    }
    finally {}
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/PluginList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */