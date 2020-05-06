package android.view.accessibility;

import android.util.SparseArray;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

final class WeakSparseArray<E>
{
  private final ReferenceQueue<E> mRefQueue = new ReferenceQueue();
  private final SparseArray<WeakReferenceWithId<E>> mSparseArray = new SparseArray();
  
  private void removeUnreachableValues()
  {
    for (Reference localReference = this.mRefQueue.poll(); localReference != null; localReference = this.mRefQueue.poll()) {
      this.mSparseArray.remove(((WeakReferenceWithId)localReference).mId);
    }
  }
  
  public void append(int paramInt, E paramE)
  {
    removeUnreachableValues();
    this.mSparseArray.append(paramInt, new WeakReferenceWithId(paramE, this.mRefQueue, paramInt));
  }
  
  public E get(int paramInt)
  {
    removeUnreachableValues();
    Object localObject = (WeakReferenceWithId)this.mSparseArray.get(paramInt);
    if (localObject != null) {
      localObject = ((WeakReferenceWithId)localObject).get();
    } else {
      localObject = null;
    }
    return (E)localObject;
  }
  
  public void remove(int paramInt)
  {
    removeUnreachableValues();
    this.mSparseArray.remove(paramInt);
  }
  
  private static class WeakReferenceWithId<E>
    extends WeakReference<E>
  {
    final int mId;
    
    WeakReferenceWithId(E paramE, ReferenceQueue<? super E> paramReferenceQueue, int paramInt)
    {
      super(paramReferenceQueue);
      this.mId = paramInt;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/accessibility/WeakSparseArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */