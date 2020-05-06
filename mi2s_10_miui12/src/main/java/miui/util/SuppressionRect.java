package miui.util;

import java.util.ArrayList;

public class SuppressionRect
{
  private static final int NODE_DEFAULT = 0;
  private static final int TIME_DEFAULT = 0;
  private int bottom;
  private int left;
  private ArrayList list = new ArrayList();
  private int node;
  private int position;
  private int right;
  private int time;
  private int top;
  private int type;
  
  public SuppressionRect()
  {
    this.time = 0;
    this.node = 0;
  }
  
  public SuppressionRect(int paramInt1, int paramInt2)
  {
    this.type = paramInt1;
    this.position = paramInt2;
    this.time = 0;
    this.node = 0;
  }
  
  public int getBottom()
  {
    return this.bottom;
  }
  
  public int getLeft()
  {
    return this.left;
  }
  
  public ArrayList<Integer> getList()
  {
    if (this.list.size() != 0) {
      this.list.clear();
    }
    this.list.add(Integer.valueOf(this.type));
    this.list.add(Integer.valueOf(this.position));
    this.list.add(Integer.valueOf(this.top));
    this.list.add(Integer.valueOf(this.left));
    this.list.add(Integer.valueOf(this.right));
    this.list.add(Integer.valueOf(this.bottom));
    this.list.add(Integer.valueOf(this.time));
    this.list.add(Integer.valueOf(this.node));
    return this.list;
  }
  
  public int getNode()
  {
    return this.node;
  }
  
  public int getPosition()
  {
    return this.position;
  }
  
  public int getRight()
  {
    return this.right;
  }
  
  public int getTime()
  {
    return this.time;
  }
  
  public int getTop()
  {
    return this.top;
  }
  
  public int getType()
  {
    return this.type;
  }
  
  public void setBottom(int paramInt)
  {
    this.bottom = paramInt;
  }
  
  public void setEmpty()
  {
    setTop(0);
    setLeft(0);
    setRight(0);
    setBottom(0);
  }
  
  public void setLeft(int paramInt)
  {
    this.left = paramInt;
  }
  
  public void setNode(int paramInt)
  {
    this.node = paramInt;
  }
  
  public void setPosition(int paramInt)
  {
    this.position = paramInt;
  }
  
  public void setRight(int paramInt)
  {
    this.right = paramInt;
  }
  
  public void setTime(int paramInt)
  {
    this.time = paramInt;
  }
  
  public void setTop(int paramInt)
  {
    this.top = paramInt;
  }
  
  public void setType(int paramInt)
  {
    this.type = paramInt;
  }
  
  public void setValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this.type = paramInt1;
    this.position = paramInt2;
    this.top = paramInt3;
    this.left = paramInt4;
    this.right = paramInt5;
    this.bottom = paramInt6;
    this.time = 0;
    this.node = 0;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SuppressionRect{list=");
    localStringBuilder.append(this.list);
    localStringBuilder.append(", type=");
    localStringBuilder.append(this.type);
    localStringBuilder.append(", position=");
    localStringBuilder.append(this.position);
    localStringBuilder.append(", top=");
    localStringBuilder.append(this.top);
    localStringBuilder.append(", left=");
    localStringBuilder.append(this.left);
    localStringBuilder.append(", right=");
    localStringBuilder.append(this.right);
    localStringBuilder.append(", bottom=");
    localStringBuilder.append(this.bottom);
    localStringBuilder.append(", time=");
    localStringBuilder.append(this.time);
    localStringBuilder.append(", node=");
    localStringBuilder.append(this.node);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/SuppressionRect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */