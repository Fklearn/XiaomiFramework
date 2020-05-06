package b.c.a.b.a.a;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class e<E> extends AbstractQueue<E> implements a<E>, Serializable {

    /* renamed from: a  reason: collision with root package name */
    transient c<E> f1953a;

    /* renamed from: b  reason: collision with root package name */
    transient c<E> f1954b;

    /* renamed from: c  reason: collision with root package name */
    private transient int f1955c;

    /* renamed from: d  reason: collision with root package name */
    private final int f1956d;
    final ReentrantLock e;
    private final Condition f;
    private final Condition g;

    private abstract class a implements Iterator<E> {

        /* renamed from: a  reason: collision with root package name */
        c<E> f1957a;

        /* renamed from: b  reason: collision with root package name */
        E f1958b;

        /* renamed from: c  reason: collision with root package name */
        private c<E> f1959c;

        a() {
            ReentrantLock reentrantLock = e.this.e;
            reentrantLock.lock();
            try {
                this.f1957a = b();
                this.f1958b = this.f1957a == null ? null : this.f1957a.f1961a;
            } finally {
                reentrantLock.unlock();
            }
        }

        private c<E> b(c<E> cVar) {
            while (true) {
                c<E> a2 = a(cVar);
                if (a2 == null) {
                    return null;
                }
                if (a2.f1961a != null) {
                    return a2;
                }
                if (a2 == cVar) {
                    return b();
                }
                cVar = a2;
            }
        }

        /* access modifiers changed from: package-private */
        public abstract c<E> a(c<E> cVar);

        /* access modifiers changed from: package-private */
        public void a() {
            ReentrantLock reentrantLock = e.this.e;
            reentrantLock.lock();
            try {
                this.f1957a = b(this.f1957a);
                this.f1958b = this.f1957a == null ? null : this.f1957a.f1961a;
            } finally {
                reentrantLock.unlock();
            }
        }

        /* access modifiers changed from: package-private */
        public abstract c<E> b();

        public boolean hasNext() {
            return this.f1957a != null;
        }

        public E next() {
            c<E> cVar = this.f1957a;
            if (cVar != null) {
                this.f1959c = cVar;
                E e = this.f1958b;
                a();
                return e;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            c<E> cVar = this.f1959c;
            if (cVar != null) {
                this.f1959c = null;
                ReentrantLock reentrantLock = e.this.e;
                reentrantLock.lock();
                try {
                    if (cVar.f1961a != null) {
                        e.this.a(cVar);
                    }
                } finally {
                    reentrantLock.unlock();
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private class b extends e<E>.a {
        private b() {
            super();
        }

        /* access modifiers changed from: package-private */
        public c<E> a(c<E> cVar) {
            return cVar.f1963c;
        }

        /* access modifiers changed from: package-private */
        public c<E> b() {
            return e.this.f1953a;
        }
    }

    static final class c<E> {

        /* renamed from: a  reason: collision with root package name */
        E f1961a;

        /* renamed from: b  reason: collision with root package name */
        c<E> f1962b;

        /* renamed from: c  reason: collision with root package name */
        c<E> f1963c;

        c(E e) {
            this.f1961a = e;
        }
    }

    public e() {
        this(Integer.MAX_VALUE);
    }

    public e(int i) {
        this.e = new ReentrantLock();
        this.f = this.e.newCondition();
        this.g = this.e.newCondition();
        if (i > 0) {
            this.f1956d = i;
            return;
        }
        throw new IllegalArgumentException();
    }

    private E a() {
        c<E> cVar = this.f1953a;
        if (cVar == null) {
            return null;
        }
        c<E> cVar2 = cVar.f1963c;
        E e2 = cVar.f1961a;
        cVar.f1961a = null;
        cVar.f1963c = cVar;
        this.f1953a = cVar2;
        if (cVar2 == null) {
            this.f1954b = null;
        } else {
            cVar2.f1962b = null;
        }
        this.f1955c--;
        this.g.signal();
        return e2;
    }

    private E b() {
        c<E> cVar = this.f1954b;
        if (cVar == null) {
            return null;
        }
        c<E> cVar2 = cVar.f1962b;
        E e2 = cVar.f1961a;
        cVar.f1961a = null;
        cVar.f1962b = cVar;
        this.f1954b = cVar2;
        if (cVar2 == null) {
            this.f1953a = null;
        } else {
            cVar2.f1963c = null;
        }
        this.f1955c--;
        this.g.signal();
        return e2;
    }

    private boolean b(c<E> cVar) {
        if (this.f1955c >= this.f1956d) {
            return false;
        }
        c<E> cVar2 = this.f1953a;
        cVar.f1963c = cVar2;
        this.f1953a = cVar;
        if (this.f1954b == null) {
            this.f1954b = cVar;
        } else {
            cVar2.f1962b = cVar;
        }
        this.f1955c++;
        this.f.signal();
        return true;
    }

    private boolean c(c<E> cVar) {
        if (this.f1955c >= this.f1956d) {
            return false;
        }
        c<E> cVar2 = this.f1954b;
        cVar.f1962b = cVar2;
        this.f1954b = cVar;
        if (this.f1953a == null) {
            this.f1953a = cVar;
        } else {
            cVar2.f1963c = cVar;
        }
        this.f1955c++;
        this.f.signal();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void a(c<E> cVar) {
        c<E> cVar2 = cVar.f1962b;
        c<E> cVar3 = cVar.f1963c;
        if (cVar2 == null) {
            a();
        } else if (cVar3 == null) {
            b();
        } else {
            cVar2.f1963c = cVar3;
            cVar3.f1962b = cVar2;
            cVar.f1961a = null;
            this.f1955c--;
            this.g.signal();
        }
    }

    public boolean add(E e2) {
        addLast(e2);
        return true;
    }

    public void addLast(E e2) {
        if (!offerLast(e2)) {
            throw new IllegalStateException("Deque full");
        }
    }

    public void clear() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            c<E> cVar = this.f1953a;
            while (cVar != null) {
                cVar.f1961a = null;
                c<E> cVar2 = cVar.f1963c;
                cVar.f1962b = null;
                cVar.f1963c = null;
                cVar = cVar2;
            }
            this.f1954b = null;
            this.f1953a = null;
            this.f1955c = 0;
            this.g.signalAll();
        } finally {
            reentrantLock.unlock();
        }
    }

    public boolean contains(Object obj) {
        if (obj == null) {
            return false;
        }
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            for (c<E> cVar = this.f1953a; cVar != null; cVar = cVar.f1963c) {
                if (obj.equals(cVar.f1961a)) {
                    return true;
                }
            }
            reentrantLock.unlock();
            return false;
        } finally {
            reentrantLock.unlock();
        }
    }

    public int drainTo(Collection<? super E> collection) {
        return drainTo(collection, Integer.MAX_VALUE);
    }

    public int drainTo(Collection<? super E> collection, int i) {
        if (collection == null) {
            throw new NullPointerException();
        } else if (collection != this) {
            ReentrantLock reentrantLock = this.e;
            reentrantLock.lock();
            try {
                int min = Math.min(i, this.f1955c);
                for (int i2 = 0; i2 < min; i2++) {
                    collection.add(this.f1953a.f1961a);
                    a();
                }
                return min;
            } finally {
                reentrantLock.unlock();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public E element() {
        return getFirst();
    }

    public E getFirst() {
        E peekFirst = peekFirst();
        if (peekFirst != null) {
            return peekFirst;
        }
        throw new NoSuchElementException();
    }

    public Iterator<E> iterator() {
        return new b();
    }

    public boolean offer(E e2, long j, TimeUnit timeUnit) {
        return offerLast(e2, j, timeUnit);
    }

    public boolean offerFirst(E e2) {
        if (e2 != null) {
            c cVar = new c(e2);
            ReentrantLock reentrantLock = this.e;
            reentrantLock.lock();
            try {
                return b(cVar);
            } finally {
                reentrantLock.unlock();
            }
        } else {
            throw new NullPointerException();
        }
    }

    public boolean offerLast(E e2) {
        if (e2 != null) {
            c cVar = new c(e2);
            ReentrantLock reentrantLock = this.e;
            reentrantLock.lock();
            try {
                return c(cVar);
            } finally {
                reentrantLock.unlock();
            }
        } else {
            throw new NullPointerException();
        }
    }

    public boolean offerLast(E e2, long j, TimeUnit timeUnit) {
        boolean z;
        if (e2 != null) {
            c cVar = new c(e2);
            long nanos = timeUnit.toNanos(j);
            ReentrantLock reentrantLock = this.e;
            reentrantLock.lockInterruptibly();
            while (true) {
                try {
                    if (c(cVar)) {
                        z = true;
                        break;
                    } else if (nanos <= 0) {
                        z = false;
                        break;
                    } else {
                        nanos = this.g.awaitNanos(nanos);
                    }
                } finally {
                    reentrantLock.unlock();
                }
            }
            return z;
        }
        throw new NullPointerException();
    }

    public E peek() {
        return peekFirst();
    }

    public E peekFirst() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            return this.f1953a == null ? null : this.f1953a.f1961a;
        } finally {
            reentrantLock.unlock();
        }
    }

    public E poll() {
        return pollFirst();
    }

    public E poll(long j, TimeUnit timeUnit) {
        return pollFirst(j, timeUnit);
    }

    public E pollFirst() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            return a();
        } finally {
            reentrantLock.unlock();
        }
    }

    public E pollFirst(long j, TimeUnit timeUnit) {
        long nanos = timeUnit.toNanos(j);
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lockInterruptibly();
        while (true) {
            try {
                E a2 = a();
                if (a2 != null) {
                    reentrantLock.unlock();
                    return a2;
                } else if (nanos <= 0) {
                    return null;
                } else {
                    nanos = this.f.awaitNanos(nanos);
                }
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    public void put(E e2) {
        putLast(e2);
    }

    public void putLast(E e2) {
        if (e2 != null) {
            c cVar = new c(e2);
            ReentrantLock reentrantLock = this.e;
            reentrantLock.lock();
            while (!c(cVar)) {
                try {
                    this.g.await();
                } finally {
                    reentrantLock.unlock();
                }
            }
            return;
        }
        throw new NullPointerException();
    }

    public int remainingCapacity() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            return this.f1956d - this.f1955c;
        } finally {
            reentrantLock.unlock();
        }
    }

    public boolean remove(Object obj) {
        return removeFirstOccurrence(obj);
    }

    public E removeFirst() {
        E pollFirst = pollFirst();
        if (pollFirst != null) {
            return pollFirst;
        }
        throw new NoSuchElementException();
    }

    public boolean removeFirstOccurrence(Object obj) {
        if (obj == null) {
            return false;
        }
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            for (c<E> cVar = this.f1953a; cVar != null; cVar = cVar.f1963c) {
                if (obj.equals(cVar.f1961a)) {
                    a(cVar);
                    return true;
                }
            }
            reentrantLock.unlock();
            return false;
        } finally {
            reentrantLock.unlock();
        }
    }

    public int size() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            return this.f1955c;
        } finally {
            reentrantLock.unlock();
        }
    }

    public E take() {
        return takeFirst();
    }

    public E takeFirst() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        while (true) {
            try {
                E a2 = a();
                if (a2 != null) {
                    return a2;
                }
                this.f.await();
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    public Object[] toArray() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            Object[] objArr = new Object[this.f1955c];
            int i = 0;
            c<E> cVar = this.f1953a;
            while (cVar != null) {
                int i2 = i + 1;
                objArr[i] = cVar.f1961a;
                cVar = cVar.f1963c;
                i = i2;
            }
            return objArr;
        } finally {
            reentrantLock.unlock();
        }
    }

    public <T> T[] toArray(T[] tArr) {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            int length = tArr.length;
            T[] tArr2 = tArr;
            if (length < this.f1955c) {
                tArr2 = (Object[]) Array.newInstance(tArr.getClass().getComponentType(), this.f1955c);
            }
            int i = 0;
            c<E> cVar = this.f1953a;
            while (cVar != null) {
                tArr2[i] = cVar.f1961a;
                cVar = cVar.f1963c;
                i++;
            }
            if (tArr2.length > i) {
                tArr2[i] = null;
            }
            return tArr2;
        } finally {
            reentrantLock.unlock();
        }
    }

    public String toString() {
        ReentrantLock reentrantLock = this.e;
        reentrantLock.lock();
        try {
            c<E> cVar = this.f1953a;
            if (cVar == null) {
                return "[]";
            }
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            while (true) {
                E e2 = cVar.f1961a;
                if (e2 == this) {
                    e2 = "(this Collection)";
                }
                sb.append(e2);
                cVar = cVar.f1963c;
                if (cVar == null) {
                    sb.append(']');
                    String sb2 = sb.toString();
                    reentrantLock.unlock();
                    return sb2;
                }
                sb.append(',');
                sb.append(' ');
            }
        } finally {
            reentrantLock.unlock();
        }
    }
}
