package android.widget;

import android.database.DataSetObserver;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;

class ExpandableListConnector
  extends BaseAdapter
  implements Filterable
{
  private final DataSetObserver mDataSetObserver = new MyDataSetObserver();
  private ArrayList<GroupMetadata> mExpGroupMetadataList = new ArrayList();
  private ExpandableListAdapter mExpandableListAdapter;
  private int mMaxExpGroupCount = Integer.MAX_VALUE;
  private int mTotalExpChildrenCount;
  
  public ExpandableListConnector(ExpandableListAdapter paramExpandableListAdapter)
  {
    setExpandableListAdapter(paramExpandableListAdapter);
  }
  
  private void refreshExpGroupMetadataList(boolean paramBoolean1, boolean paramBoolean2)
  {
    ArrayList localArrayList = this.mExpGroupMetadataList;
    int i = localArrayList.size();
    int j = 0;
    this.mTotalExpChildrenCount = 0;
    int k = i;
    GroupMetadata localGroupMetadata;
    int i2;
    if (paramBoolean2)
    {
      m = 0;
      n = i - 1;
      while (n >= 0)
      {
        localGroupMetadata = (GroupMetadata)localArrayList.get(n);
        int i1 = findGroupPosition(localGroupMetadata.gId, localGroupMetadata.gPos);
        i2 = i;
        int i3 = m;
        if (i1 != localGroupMetadata.gPos)
        {
          k = i;
          if (i1 == -1)
          {
            localArrayList.remove(n);
            k = i - 1;
          }
          localGroupMetadata.gPos = i1;
          i2 = k;
          i3 = m;
          if (m == 0)
          {
            i3 = 1;
            i2 = k;
          }
        }
        n--;
        i = i2;
        m = i3;
      }
      k = i;
      if (m != 0)
      {
        Collections.sort(localArrayList);
        k = i;
      }
    }
    int m = 0;
    i = 0;
    int n = j;
    while (i < k)
    {
      localGroupMetadata = (GroupMetadata)localArrayList.get(i);
      if ((localGroupMetadata.lastChildFlPos != -1) && (!paramBoolean1)) {
        i2 = localGroupMetadata.lastChildFlPos - localGroupMetadata.flPos;
      } else {
        i2 = this.mExpandableListAdapter.getChildrenCount(localGroupMetadata.gPos);
      }
      this.mTotalExpChildrenCount += i2;
      n += localGroupMetadata.gPos - m;
      m = localGroupMetadata.gPos;
      localGroupMetadata.flPos = n;
      n += i2;
      localGroupMetadata.lastChildFlPos = n;
      i++;
    }
  }
  
  public boolean areAllItemsEnabled()
  {
    return this.mExpandableListAdapter.areAllItemsEnabled();
  }
  
  boolean collapseGroup(int paramInt)
  {
    ExpandableListPosition localExpandableListPosition = ExpandableListPosition.obtain(2, paramInt, -1, -1);
    PositionMetadata localPositionMetadata = getFlattenedPos(localExpandableListPosition);
    localExpandableListPosition.recycle();
    if (localPositionMetadata == null) {
      return false;
    }
    boolean bool = collapseGroup(localPositionMetadata);
    localPositionMetadata.recycle();
    return bool;
  }
  
  boolean collapseGroup(PositionMetadata paramPositionMetadata)
  {
    if (paramPositionMetadata.groupMetadata == null) {
      return false;
    }
    this.mExpGroupMetadataList.remove(paramPositionMetadata.groupMetadata);
    refreshExpGroupMetadataList(false, false);
    notifyDataSetChanged();
    this.mExpandableListAdapter.onGroupCollapsed(paramPositionMetadata.groupMetadata.gPos);
    return true;
  }
  
  boolean expandGroup(int paramInt)
  {
    ExpandableListPosition localExpandableListPosition = ExpandableListPosition.obtain(2, paramInt, -1, -1);
    PositionMetadata localPositionMetadata = getFlattenedPos(localExpandableListPosition);
    localExpandableListPosition.recycle();
    boolean bool = expandGroup(localPositionMetadata);
    localPositionMetadata.recycle();
    return bool;
  }
  
  boolean expandGroup(PositionMetadata paramPositionMetadata)
  {
    if (paramPositionMetadata.position.groupPos >= 0)
    {
      if (this.mMaxExpGroupCount == 0) {
        return false;
      }
      if (paramPositionMetadata.groupMetadata != null) {
        return false;
      }
      if (this.mExpGroupMetadataList.size() >= this.mMaxExpGroupCount)
      {
        localGroupMetadata = (GroupMetadata)this.mExpGroupMetadataList.get(0);
        int i = this.mExpGroupMetadataList.indexOf(localGroupMetadata);
        collapseGroup(localGroupMetadata.gPos);
        if (paramPositionMetadata.groupInsertIndex > i) {
          paramPositionMetadata.groupInsertIndex -= 1;
        }
      }
      GroupMetadata localGroupMetadata = GroupMetadata.obtain(-1, -1, paramPositionMetadata.position.groupPos, this.mExpandableListAdapter.getGroupId(paramPositionMetadata.position.groupPos));
      this.mExpGroupMetadataList.add(paramPositionMetadata.groupInsertIndex, localGroupMetadata);
      refreshExpGroupMetadataList(false, false);
      notifyDataSetChanged();
      this.mExpandableListAdapter.onGroupExpanded(localGroupMetadata.gPos);
      return true;
    }
    throw new RuntimeException("Need group");
  }
  
  int findGroupPosition(long paramLong, int paramInt)
  {
    int i = this.mExpandableListAdapter.getGroupCount();
    if (i == 0) {
      return -1;
    }
    if (paramLong == Long.MIN_VALUE) {
      return -1;
    }
    paramInt = Math.min(i - 1, Math.max(0, paramInt));
    long l = SystemClock.uptimeMillis();
    int j = paramInt;
    int k = paramInt;
    int m = 0;
    ExpandableListAdapter localExpandableListAdapter = getAdapter();
    if (localExpandableListAdapter == null) {
      return -1;
    }
    while (SystemClock.uptimeMillis() <= l + 100L)
    {
      if (localExpandableListAdapter.getGroupId(paramInt) == paramLong) {
        return paramInt;
      }
      int n = 1;
      int i1;
      if (k == i - 1) {
        i1 = 1;
      } else {
        i1 = 0;
      }
      if (j != 0) {
        n = 0;
      }
      if ((i1 != 0) && (n != 0)) {
        break;
      }
      if ((n == 0) && ((m == 0) || (i1 != 0)))
      {
        if ((i1 != 0) || ((m == 0) && (n == 0)))
        {
          j--;
          paramInt = j;
          m = 1;
        }
      }
      else
      {
        k++;
        paramInt = k;
        m = 0;
      }
    }
    return -1;
  }
  
  ExpandableListAdapter getAdapter()
  {
    return this.mExpandableListAdapter;
  }
  
  public int getCount()
  {
    return this.mExpandableListAdapter.getGroupCount() + this.mTotalExpChildrenCount;
  }
  
  ArrayList<GroupMetadata> getExpandedGroupMetadataList()
  {
    return this.mExpGroupMetadataList;
  }
  
  public Filter getFilter()
  {
    ExpandableListAdapter localExpandableListAdapter = getAdapter();
    if ((localExpandableListAdapter instanceof Filterable)) {
      return ((Filterable)localExpandableListAdapter).getFilter();
    }
    return null;
  }
  
  PositionMetadata getFlattenedPos(ExpandableListPosition paramExpandableListPosition)
  {
    Object localObject = this.mExpGroupMetadataList;
    int i = ((ArrayList)localObject).size();
    int j = 0;
    int k = i - 1;
    int m = 0;
    if (i == 0) {
      return PositionMetadata.obtain(paramExpandableListPosition.groupPos, paramExpandableListPosition.type, paramExpandableListPosition.groupPos, paramExpandableListPosition.childPos, null, 0);
    }
    while (j <= k)
    {
      i = (k - j) / 2 + j;
      GroupMetadata localGroupMetadata = (GroupMetadata)((ArrayList)localObject).get(i);
      if (paramExpandableListPosition.groupPos > localGroupMetadata.gPos)
      {
        j = i + 1;
        m = i;
      }
      else if (paramExpandableListPosition.groupPos < localGroupMetadata.gPos)
      {
        k = i - 1;
        m = i;
      }
      else
      {
        m = i;
        if (paramExpandableListPosition.groupPos == localGroupMetadata.gPos)
        {
          if (paramExpandableListPosition.type == 2) {
            return PositionMetadata.obtain(localGroupMetadata.flPos, paramExpandableListPosition.type, paramExpandableListPosition.groupPos, paramExpandableListPosition.childPos, localGroupMetadata, i);
          }
          if (paramExpandableListPosition.type == 1) {
            return PositionMetadata.obtain(localGroupMetadata.flPos + paramExpandableListPosition.childPos + 1, paramExpandableListPosition.type, paramExpandableListPosition.groupPos, paramExpandableListPosition.childPos, localGroupMetadata, i);
          }
          return null;
        }
      }
    }
    if (paramExpandableListPosition.type != 2) {
      return null;
    }
    if (j > m)
    {
      localObject = (GroupMetadata)((ArrayList)localObject).get(j - 1);
      return PositionMetadata.obtain(((GroupMetadata)localObject).lastChildFlPos + (paramExpandableListPosition.groupPos - ((GroupMetadata)localObject).gPos), paramExpandableListPosition.type, paramExpandableListPosition.groupPos, paramExpandableListPosition.childPos, null, j);
    }
    if (k < m)
    {
      j = k + 1;
      localObject = (GroupMetadata)((ArrayList)localObject).get(j);
      return PositionMetadata.obtain(((GroupMetadata)localObject).flPos - (((GroupMetadata)localObject).gPos - paramExpandableListPosition.groupPos), paramExpandableListPosition.type, paramExpandableListPosition.groupPos, paramExpandableListPosition.childPos, null, j);
    }
    return null;
  }
  
  public Object getItem(int paramInt)
  {
    PositionMetadata localPositionMetadata = getUnflattenedPos(paramInt);
    Object localObject;
    if (localPositionMetadata.position.type == 2)
    {
      localObject = this.mExpandableListAdapter.getGroup(localPositionMetadata.position.groupPos);
    }
    else
    {
      if (localPositionMetadata.position.type != 1) {
        break label78;
      }
      localObject = this.mExpandableListAdapter.getChild(localPositionMetadata.position.groupPos, localPositionMetadata.position.childPos);
    }
    localPositionMetadata.recycle();
    return localObject;
    label78:
    throw new RuntimeException("Flat list position is of unknown type");
  }
  
  public long getItemId(int paramInt)
  {
    PositionMetadata localPositionMetadata = getUnflattenedPos(paramInt);
    long l1 = this.mExpandableListAdapter.getGroupId(localPositionMetadata.position.groupPos);
    long l2;
    if (localPositionMetadata.position.type == 2)
    {
      l2 = this.mExpandableListAdapter.getCombinedGroupId(l1);
    }
    else
    {
      if (localPositionMetadata.position.type != 1) {
        break label106;
      }
      l2 = this.mExpandableListAdapter.getChildId(localPositionMetadata.position.groupPos, localPositionMetadata.position.childPos);
      l2 = this.mExpandableListAdapter.getCombinedChildId(l1, l2);
    }
    localPositionMetadata.recycle();
    return l2;
    label106:
    throw new RuntimeException("Flat list position is of unknown type");
  }
  
  public int getItemViewType(int paramInt)
  {
    PositionMetadata localPositionMetadata = getUnflattenedPos(paramInt);
    ExpandableListPosition localExpandableListPosition = localPositionMetadata.position;
    Object localObject = this.mExpandableListAdapter;
    if ((localObject instanceof HeterogeneousExpandableList))
    {
      localObject = (HeterogeneousExpandableList)localObject;
      if (localExpandableListPosition.type == 2) {
        paramInt = ((HeterogeneousExpandableList)localObject).getGroupType(localExpandableListPosition.groupPos);
      } else {
        paramInt = ((HeterogeneousExpandableList)localObject).getChildType(localExpandableListPosition.groupPos, localExpandableListPosition.childPos) + ((HeterogeneousExpandableList)localObject).getGroupTypeCount();
      }
    }
    else if (localExpandableListPosition.type == 2)
    {
      paramInt = 0;
    }
    else
    {
      paramInt = 1;
    }
    localPositionMetadata.recycle();
    return paramInt;
  }
  
  PositionMetadata getUnflattenedPos(int paramInt)
  {
    Object localObject = this.mExpGroupMetadataList;
    int i = ((ArrayList)localObject).size();
    int j = 0;
    int k = i - 1;
    int m = 0;
    if (i == 0) {
      return PositionMetadata.obtain(paramInt, 2, paramInt, -1, null, 0);
    }
    while (j <= k)
    {
      i = (k - j) / 2 + j;
      GroupMetadata localGroupMetadata = (GroupMetadata)((ArrayList)localObject).get(i);
      if (paramInt > localGroupMetadata.lastChildFlPos)
      {
        j = i + 1;
        m = i;
      }
      else if (paramInt < localGroupMetadata.flPos)
      {
        k = i - 1;
        m = i;
      }
      else
      {
        if (paramInt == localGroupMetadata.flPos) {
          return PositionMetadata.obtain(paramInt, 2, localGroupMetadata.gPos, -1, localGroupMetadata, i);
        }
        m = i;
        if (paramInt <= localGroupMetadata.lastChildFlPos)
        {
          j = localGroupMetadata.flPos;
          return PositionMetadata.obtain(paramInt, 1, localGroupMetadata.gPos, paramInt - (j + 1), localGroupMetadata, i);
        }
      }
    }
    if (j > m)
    {
      localObject = (GroupMetadata)((ArrayList)localObject).get(j - 1);
      m = ((GroupMetadata)localObject).lastChildFlPos;
      k = ((GroupMetadata)localObject).gPos;
      k = paramInt - m + k;
    }
    else
    {
      if (k >= m) {
        break label265;
      }
      j = k + 1;
      localObject = (GroupMetadata)((ArrayList)localObject).get(j);
      m = ((GroupMetadata)localObject).gPos;
      k = ((GroupMetadata)localObject).flPos;
      k = m - (k - paramInt);
    }
    return PositionMetadata.obtain(paramInt, 2, k, -1, null, j);
    label265:
    throw new RuntimeException("Unknown state");
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    PositionMetadata localPositionMetadata = getUnflattenedPos(paramInt);
    if (localPositionMetadata.position.type == 2)
    {
      paramView = this.mExpandableListAdapter.getGroupView(localPositionMetadata.position.groupPos, localPositionMetadata.isExpanded(), paramView, paramViewGroup);
    }
    else
    {
      int i = localPositionMetadata.position.type;
      boolean bool = true;
      if (i != 1) {
        break label121;
      }
      if (localPositionMetadata.groupMetadata.lastChildFlPos != paramInt) {
        bool = false;
      }
      paramView = this.mExpandableListAdapter.getChildView(localPositionMetadata.position.groupPos, localPositionMetadata.position.childPos, bool, paramView, paramViewGroup);
    }
    localPositionMetadata.recycle();
    return paramView;
    label121:
    throw new RuntimeException("Flat list position is of unknown type");
  }
  
  public int getViewTypeCount()
  {
    Object localObject = this.mExpandableListAdapter;
    if ((localObject instanceof HeterogeneousExpandableList))
    {
      localObject = (HeterogeneousExpandableList)localObject;
      return ((HeterogeneousExpandableList)localObject).getGroupTypeCount() + ((HeterogeneousExpandableList)localObject).getChildTypeCount();
    }
    return 2;
  }
  
  public boolean hasStableIds()
  {
    return this.mExpandableListAdapter.hasStableIds();
  }
  
  public boolean isEmpty()
  {
    ExpandableListAdapter localExpandableListAdapter = getAdapter();
    boolean bool;
    if (localExpandableListAdapter != null) {
      bool = localExpandableListAdapter.isEmpty();
    } else {
      bool = true;
    }
    return bool;
  }
  
  public boolean isEnabled(int paramInt)
  {
    PositionMetadata localPositionMetadata = getUnflattenedPos(paramInt);
    ExpandableListPosition localExpandableListPosition = localPositionMetadata.position;
    boolean bool;
    if (localExpandableListPosition.type == 1) {
      bool = this.mExpandableListAdapter.isChildSelectable(localExpandableListPosition.groupPos, localExpandableListPosition.childPos);
    } else {
      bool = true;
    }
    localPositionMetadata.recycle();
    return bool;
  }
  
  public boolean isGroupExpanded(int paramInt)
  {
    for (int i = this.mExpGroupMetadataList.size() - 1; i >= 0; i--) {
      if (((GroupMetadata)this.mExpGroupMetadataList.get(i)).gPos == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public void setExpandableListAdapter(ExpandableListAdapter paramExpandableListAdapter)
  {
    ExpandableListAdapter localExpandableListAdapter = this.mExpandableListAdapter;
    if (localExpandableListAdapter != null) {
      localExpandableListAdapter.unregisterDataSetObserver(this.mDataSetObserver);
    }
    this.mExpandableListAdapter = paramExpandableListAdapter;
    paramExpandableListAdapter.registerDataSetObserver(this.mDataSetObserver);
  }
  
  void setExpandedGroupMetadataList(ArrayList<GroupMetadata> paramArrayList)
  {
    if (paramArrayList != null)
    {
      ExpandableListAdapter localExpandableListAdapter = this.mExpandableListAdapter;
      if (localExpandableListAdapter != null)
      {
        int i = localExpandableListAdapter.getGroupCount();
        for (int j = paramArrayList.size() - 1; j >= 0; j--) {
          if (((GroupMetadata)paramArrayList.get(j)).gPos >= i) {
            return;
          }
        }
        this.mExpGroupMetadataList = paramArrayList;
        refreshExpGroupMetadataList(true, false);
        return;
      }
    }
  }
  
  public void setMaxExpGroupCount(int paramInt)
  {
    this.mMaxExpGroupCount = paramInt;
  }
  
  static class GroupMetadata
    implements Parcelable, Comparable<GroupMetadata>
  {
    public static final Parcelable.Creator<GroupMetadata> CREATOR = new Parcelable.Creator()
    {
      public ExpandableListConnector.GroupMetadata createFromParcel(Parcel paramAnonymousParcel)
      {
        return ExpandableListConnector.GroupMetadata.obtain(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt(), paramAnonymousParcel.readLong());
      }
      
      public ExpandableListConnector.GroupMetadata[] newArray(int paramAnonymousInt)
      {
        return new ExpandableListConnector.GroupMetadata[paramAnonymousInt];
      }
    };
    static final int REFRESH = -1;
    int flPos;
    long gId;
    int gPos;
    int lastChildFlPos;
    
    static GroupMetadata obtain(int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      GroupMetadata localGroupMetadata = new GroupMetadata();
      localGroupMetadata.flPos = paramInt1;
      localGroupMetadata.lastChildFlPos = paramInt2;
      localGroupMetadata.gPos = paramInt3;
      localGroupMetadata.gId = paramLong;
      return localGroupMetadata;
    }
    
    public int compareTo(GroupMetadata paramGroupMetadata)
    {
      if (paramGroupMetadata != null) {
        return this.gPos - paramGroupMetadata.gPos;
      }
      throw new IllegalArgumentException();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.flPos);
      paramParcel.writeInt(this.lastChildFlPos);
      paramParcel.writeInt(this.gPos);
      paramParcel.writeLong(this.gId);
    }
  }
  
  protected class MyDataSetObserver
    extends DataSetObserver
  {
    protected MyDataSetObserver() {}
    
    public void onChanged()
    {
      ExpandableListConnector.this.refreshExpGroupMetadataList(true, true);
      ExpandableListConnector.this.notifyDataSetChanged();
    }
    
    public void onInvalidated()
    {
      ExpandableListConnector.this.refreshExpGroupMetadataList(true, true);
      ExpandableListConnector.this.notifyDataSetInvalidated();
    }
  }
  
  public static class PositionMetadata
  {
    private static final int MAX_POOL_SIZE = 5;
    private static ArrayList<PositionMetadata> sPool = new ArrayList(5);
    public int groupInsertIndex;
    public ExpandableListConnector.GroupMetadata groupMetadata;
    public ExpandableListPosition position;
    
    private static PositionMetadata getRecycledOrCreate()
    {
      synchronized (sPool)
      {
        if (sPool.size() > 0)
        {
          localPositionMetadata = (PositionMetadata)sPool.remove(0);
          localPositionMetadata.resetState();
          return localPositionMetadata;
        }
        PositionMetadata localPositionMetadata = new android/widget/ExpandableListConnector$PositionMetadata;
        localPositionMetadata.<init>();
        return localPositionMetadata;
      }
    }
    
    static PositionMetadata obtain(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ExpandableListConnector.GroupMetadata paramGroupMetadata, int paramInt5)
    {
      PositionMetadata localPositionMetadata = getRecycledOrCreate();
      localPositionMetadata.position = ExpandableListPosition.obtain(paramInt2, paramInt3, paramInt4, paramInt1);
      localPositionMetadata.groupMetadata = paramGroupMetadata;
      localPositionMetadata.groupInsertIndex = paramInt5;
      return localPositionMetadata;
    }
    
    private void resetState()
    {
      ExpandableListPosition localExpandableListPosition = this.position;
      if (localExpandableListPosition != null)
      {
        localExpandableListPosition.recycle();
        this.position = null;
      }
      this.groupMetadata = null;
      this.groupInsertIndex = 0;
    }
    
    public boolean isExpanded()
    {
      boolean bool;
      if (this.groupMetadata != null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void recycle()
    {
      resetState();
      synchronized (sPool)
      {
        if (sPool.size() < 5) {
          sPool.add(this);
        }
        return;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/ExpandableListConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */