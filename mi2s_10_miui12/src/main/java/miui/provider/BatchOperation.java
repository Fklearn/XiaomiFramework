package miui.provider;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

public final class BatchOperation {
    public static final int BATCH_EXECUTE_SIZE = 100;
    private final String mAuthority;
    private final ArrayList<ContentProviderOperation> mOperations = new ArrayList<>();
    private final ContentResolver mResolver;

    public BatchOperation(ContentResolver resolver, String authority) {
        this.mResolver = resolver;
        this.mAuthority = authority;
    }

    public int size() {
        return this.mOperations.size();
    }

    public void add(ContentProviderOperation cpo) {
        this.mOperations.add(cpo);
    }

    public ContentResolver getContentResolver() {
        return this.mResolver;
    }

    public Uri execute() {
        Uri result = null;
        if (this.mOperations.size() == 0) {
            return null;
        }
        try {
            ContentProviderResult[] results = this.mResolver.applyBatch(this.mAuthority, this.mOperations);
            if (results != null && results.length > 0) {
                result = results[0].uri;
            }
        } catch (OperationApplicationException e1) {
            Log.e("BatchOperation", "storing contact data failed", e1);
        } catch (RemoteException e2) {
            Log.e("BatchOperation", "storing contact data failed", e2);
        }
        this.mOperations.clear();
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<ContentProviderOperation> it = this.mOperations.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString() + "\n");
        }
        return sb.toString();
    }
}
