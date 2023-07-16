package com.wb.springframework.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author WangBing
 * @date 2023/6/18 17:34
 */
public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;


    private static final int MAXIMUM_CONCURRENCY_LEVEL = 1 << 16;

    private static final ReferenceType DEFAULT_REFERENCE_TYPE = ReferenceType.SOFT;

    private static final int MAXIMUM_SEGMENT_SIZE = 1 << 30;

    private final Segment[] segments;

    private final float loadFactor;

    private final ReferenceType referenceType;

    private final int shift;

    private volatile Set<Map.Entry<K, V>> entrySet;

    public ConcurrentReferenceHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, DEFAULT_CONCURRENCY_LEVEL, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, int concurrencyLevel) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
    }

    @SuppressWarnings("unchecked")
    public ConcurrentReferenceHashMap(
            int initialCapacity, float loadFactor, int concurrencyLevel, ReferenceType referenceType) {
        this.loadFactor = loadFactor;
        this.shift = calculateShift(concurrencyLevel, MAXIMUM_CONCURRENCY_LEVEL);
        int size = 1 << this.shift;
        this.referenceType = referenceType;
        int roundedUpSegmentCapacity = (int) ((initialCapacity + size - 1L) / size);
        int initialSize = 1 << calculateShift(roundedUpSegmentCapacity, MAXIMUM_SEGMENT_SIZE);
        Segment[] segments = (Segment[]) Array.newInstance(Segment.class, size);
        int resizeThreshold = (int) (initialSize * getLoadFactor());
        for (int i = 0 ; i < segments.length; i++) {
            segments[i] = new Segment(initialSize, resizeThreshold);
        }
        this.segments = segments;
    }

    protected static int calculateShift(int minimumValue, int maximumValue) {
        int shift = 0;
        int value = 1;
        while (value < minimumValue && value < maximumValue) {
            value <<= 1;
            shift++;
        }
        return shift;
    }

    protected final float getLoadFactor() {
        return this.loadFactor;
    }

    protected enum Restructure {
        WHEN_NECESSARY, NEVER
    }


    @Override
    public V get(Object key) {
        Entry<K, V> entry = getEntryIfAvailable(key);
        return null != entry ? entry.getValue() : null;
    }

    private Entry<K, V> getEntryIfAvailable(Object key) {
        Reference<K, V> ref = getReference(key, Restructure.WHEN_NECESSARY);
        return ref != null ? ref.get() : null;
    }

    protected final Reference<K, V> getReference(Object key, Restructure restructure) {
        int hash = getHash(key);
        return getSegmentForHash(hash).getReference(key, hash, restructure);
    }

    private Segment getSegmentForHash(int hash) {
        return this.segments[(hash >>> (32 - this.shift)) & (this.segments.length - 1)];
    }

    protected int getHash(Object o) {
        int hash = o != null ? o.hashCode() : 0;
        hash += (hash << 15) ^ 0xffffcd7d;
        hash ^= (hash >>> 10);
        hash += (hash << 3);
        hash ^= (hash >>> 6);
        hash += (hash << 2) + (hash << 14);
        hash ^= (hash >>> 16);
        return hash;
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return ConcurrentMap.super.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, true);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return put(key, value, false);
    }

    private V put(final K key, final V value, final boolean overwriteExisting) {
        return doTask(key, new Task<V>(TaskOption.RESTRUCTURE_AFTER, TaskOption.RESIZE) {
            @Override
            protected V execute(Reference<K, V> ref, Entry<K, V> entry, Entries entries) {
                if (entry != null) {
                    V oldValue = entry.getValue();
                    if(overwriteExisting) {
                        entry.setValue(value);
                    }
                    return oldValue;
                }
                Assert.state(null != entries, "No entries segment");
                entries.add(value);
                return null;
            }
        });
    }

    private <T> T doTask(Object key, Task<T> task) {
        int hash = getHash(key);
        return getSegmentForHash(hash).doTask(hash, key, task);
    }

    @Override
    public V remove(Object key) {
        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, final V oldValue, final V newValue) {
        return false;
    }

    @Override
    public V replace(K key, final V value) {
        return null;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = new EntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }

    private class EntryIterator implements Iterator<Map.Entry<K, V>> {

        private int segmentIndex;
        private int referenceIndex;
        private Reference<K, V>[] references;
        private Reference<K, V> reference;
        private Entry<K, V> next;
        private Entry<K, V> last;
        public EntryIterator() {
            moveToNextSegment();
        }

        @Override
        public boolean hasNext() {
            getNextIfNecessary();
            return this.next != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            getNextIfNecessary();
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.last = this.next;
            this.next = null;
            return this.last;
        }
        private void getNextIfNecessary() {
            while (this.next == null) {
                moveToNextReference();
                if (this.reference == null) {
                    return ;
                }
                this.next = this.reference.get();
            }
        }
        private void moveToNextReference() {
            if (this.reference != null) {
                this.reference = this.reference.getNext();
            }
            while (this.reference == null && this.references != null) {
                if (this.referenceIndex >= this.references.length) {
                    moveToNextSegment();
                    this.referenceIndex = 0;
                } else {
                    this.reference = this.references[this.referenceIndex];
                    this.referenceIndex++;
                }
            }
        }
        private void moveToNextSegment() {
            this.reference = null;
            this.references = null;
            if (this.segmentIndex < ConcurrentReferenceHashMap.this.segments.length) {
                this.references = ConcurrentReferenceHashMap.this.segments[this.segmentIndex].references;
                this.segmentIndex++;
            }
        }

        @Override
        public void remove() {
            ConcurrentReferenceHashMap.this.remove(this.last.getKey());
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                Reference<K, V> ref = ConcurrentReferenceHashMap.this.getReference(entry.getKey(), Restructure.NEVER);
                Entry<K, V> otherEntry = (ref != null ? ref.get() : null);
                if (otherEntry != null) {
                    return ObjectUtils.nullSafeEquals(otherEntry.getValue(), otherEntry.getValue());
                }
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?>) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                return ConcurrentReferenceHashMap.this.remove(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    public enum ReferenceType {
        SOFT, WEAK
    }

    protected ReferenceManager createReferenceManager() {
        return new ReferenceManager();
    }

    private abstract class Task<T> {
        private final EnumSet<TaskOption> options;


        public Task(TaskOption... options) {
            this.options = (options.length == 0 ? EnumSet.noneOf(TaskOption.class) : EnumSet.of(options[0], options));
        }

        public boolean hasOption(TaskOption option) {
            return this.options.contains(option);
        }

        protected T execute(Reference<K, V> ref, Entry<K, V> entry, Entries entries) {
            return execute(ref, entry);
        }

        protected T execute(Reference<K, V> ref, Entry<K, V> entry) {
            return null;
        }
    }

    private abstract class Entries {
        public abstract void add(V value);
    }

    private enum TaskOption {
        RESTRUCTURE_BEFORE, RESTRUCTURE_AFTER, SKIP_IF_EMPTY, RESIZE
    }

    protected final class Segment extends ReentrantLock {

        private final ReferenceManager referenceManager;
        private final int initialSize;

        private volatile Reference<K, V>[] references;

        private volatile int count = 0;

        private int resizeThreshold;

        public Segment(int initialSize, int resizeThreshold) {
            this.referenceManager = createReferenceManager();
            this.initialSize = initialSize;
            this.references = createReferenceArray(initialSize);
            this.resizeThreshold = resizeThreshold;
        }

        @SuppressWarnings({"unchecked"})
        private Reference<K, V>[] createReferenceArray(int size) {
            return new Reference[size];
        }

        public Reference<K, V> getReference(Object key, int hash, Restructure restructure) {
            if (restructure == Restructure.WHEN_NECESSARY) {
                restructureIfNecessary(false);
            }
            if (this.count == 0) {
                return null;
            }
            Reference<K, V>[] references = this.references;
            int index = getIndex(hash, references);
            Reference<K, V> head = references[index];
            return findInChain(head, key, hash);
        }

        public <T> T doTask(final int hash, final Object key, final Task<T> task) {
            boolean resize = task.hasOption(TaskOption.RESIZE);
            if (task.hasOption(TaskOption.RESTRUCTURE_BEFORE)) {
                restructureIfNecessary(resize);
            }
            if (task.hasOption(TaskOption.SKIP_IF_EMPTY) && this.count == 0) {
                return task.execute(null, null, null);
            }
            lock();
            try {
                final int index = getIndex(hash, this.references);
                final Reference<K, V> head = this.references[index];
                Reference<K, V> ref = findInChain(head, key, hash);
                Entry<K, V> entry = ref != null ? ref.get() : null;
                Entries entries = new Entries() {
                    @Override
                    public void add(V value) {
                        @SuppressWarnings("unchecked")
                        Entry<K, V> newEntry = new Entry<>((K) key, value);
                        Reference<K, V> newReference = Segment.this.referenceManager.createReference(newEntry, hash, head);
                        Segment.this.references[index] = newReference;
                        Segment.this.count++;
                    }
                };
                return task.execute(ref, entry, entries);
            } finally {
                unlock();
                if (task.hasOption(TaskOption.RESTRUCTURE_AFTER)) {
                    restructureIfNecessary(resize);
                }
            }
        }

        private Reference<K, V> findInChain(Reference<K, V> ref, Object key, int hash) {
            Reference<K, V> currRef = ref;
            while (currRef != null) {
                if (currRef.getHash() == hash) {
                    Entry<K, V> entry = currRef.get();
                    if (entry != null) {
                        K entryKey = entry.getKey();
                        if (ObjectUtils.nullSafeEquals(entryKey, key)) {
                            return currRef;
                        }
                    }
                }
                currRef = currRef.getNext();
            }
            return null;
        }

        private int getIndex(int hash, Reference<K, V>[] references) {
            return hash & (references.length - 1);
        }

        public void clear() {
            if (this.count == 0) {
                return ;
            }
            lock();
            try {
                this.references = createReferenceArray(this.initialSize);
                this.resizeThreshold = (int) (this.references.length * getLoadFactor());
                this.count = 0;
            } finally {
                unlock();
            }
        }

        private void restructureIfNecessary(boolean allowResize) {
            int currCount = this.count;
            boolean needsResize = (currCount > 0 && currCount >= this.resizeThreshold);
            Reference<K, V> ref = this.referenceManager.pollForPurge();
            if (ref != null || (needsResize && allowResize)) {
                lock();
                try {
                    int countAfterRestructure = this.count;
                    Set<Reference<K, V>> toPurge = Collections.emptySet();
                    if (ref != null) {
                        toPurge = new HashSet<>();
                        while (ref != null) {
                            toPurge.add(ref);
                            ref = this.referenceManager.pollForPurge();
                        }
                    }
                    countAfterRestructure -= toPurge.size();
                    needsResize = (countAfterRestructure > 0 && countAfterRestructure >= this.resizeThreshold);
                    boolean resizing = false;
                    int restructureSize = this.references.length;
                    if (allowResize && needsResize && restructureSize < MAXIMUM_SEGMENT_SIZE) {
                        restructureSize <<= 1;
                        resizing = true;
                    }
                    Reference<K, V>[] restructured = resizing ? createReferenceArray(restructureSize) : this.references;
                    for (int i = 0 ; i < this.references.length; i++) {
                        ref = this.references[i];
                        if (!resizing) {
                            restructured[i] = null;
                        }
                        while (ref != null) {
                            if (!toPurge.contains(ref)) {
                                Entry<K, V> entry = ref.get();
                                if (null != entry) {
                                    int index = getIndex(ref.getHash(), restructured);
                                    restructured[index] = this.referenceManager.createReference(
                                            entry, ref.getHash(), restructured[index]
                                    );
                                }
                            }
                            ref = ref.getNext();
                        }
                    }

                    if (resizing) {
                        this.references = restructured;
                        this.resizeThreshold = (int) (this.references.length * getLoadFactor());
                    }
                    this.count = Math.max(countAfterRestructure, 0);
                } finally {
                    unlock();
                }
            }
        }
    }

    protected class ReferenceManager {

        private final ReferenceQueue<Entry<K, V>> queue = new ReferenceQueue<>();

        public Reference<K, V> createReference(Entry<K, V> entry, int hash, Reference<K, V> next) {
            if (ConcurrentReferenceHashMap.this.referenceType == ReferenceType.WEAK) {
                return new WeakEntryReference<>(entry, hash, next, this.queue);
            }
            return new SoftEntryReference<>(entry, hash, next, this.queue);
        }

        @SuppressWarnings("unchecked")
        public Reference<K, V> pollForPurge() {
            return (Reference<K, V>) this.queue.poll();
        }
    }

    private static final class SoftEntryReference<K, V> extends SoftReference<Entry<K, V>> implements Reference<K, V> {

        private final int hash;

        private final Reference<K, V> nextReference;

        public SoftEntryReference(Entry<K, V> entry, int hash, Reference<K, V> next, ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        @Override
        public void release() {
            enqueue();
            clear();
        }
    }

    private static final class WeakEntryReference<K, V> extends WeakReference<Entry<K, V>> implements Reference<K, V> {

        private final int hash;

        private final Reference<K, V> nextReference;

        public WeakEntryReference(Entry<K, V> entry, int hash, Reference<K, V> next,
                                  ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        @Override
        public int getHash() {
            return this.hash;
        }

        @Override
        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        @Override
        public void release() {
            enqueue();
            clear();
        }
    }

    protected interface Reference<K, V> {
        Entry<K, V> get();

        int getHash();

        Reference<K, V> getNext();

        void release();
    }

    protected static final class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private volatile V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V previous = this.value;
            this.value = value;
            return previous;
        }

        @Override
        public String toString() {
            return (this.key + "=" + this.value);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Map.Entry)) {
                return false;
            }
            Map.Entry otherEntry = (Map.Entry) other;
            return ObjectUtils.nullSafeEquals(getKey(), otherEntry.getKey()) &&
                    ObjectUtils.nullSafeEquals(getValue(), otherEntry.getValue());
        }

        @Override
        public int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.key)
                    ^ ObjectUtils.nullSafeHashCode(this.value);
        }
    }

}
