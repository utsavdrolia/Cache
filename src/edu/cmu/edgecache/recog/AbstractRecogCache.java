package edu.cmu.edgecache.recog;

import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This cache is different from typical caches. Given a {@link RecognizeInterface}, it looks up the given features and
 * matches it to a cached key. If nothing is found, it returns null. It is expected that the instantiating object will
 * then call {@link AbstractRecogCache#put(Object, Object)} to insert the the propoer K-V into the knownItems.
 * However, even at this point, recognition requests for the inserted V may not be replied correctly
 * since the underlying recognizer may not be trained yet. That depends on the extending class and how it chooses to implement
 * the {@link AbstractRecogCache#onNewItem()} function.
 * Extend this class and implement LFU or other such caching mechanisms.
 * Created by utsav on 9/7/16.
 */
public abstract class AbstractRecogCache<K extends Object, V>
{
    protected Map<K, V> knownItems;
    protected Map<K, MutableInt> counters;
    protected int size;
    protected long total_queries = 0;
    protected RecognizeInterface<K, V> recognizer;

    /**
     *
     * @param recognizer The underlying recognizer to use for lookups
     */
    public AbstractRecogCache(RecognizeInterface<K, V> recognizer)
    {
        this.recognizer = recognizer;
        this.knownItems = new HashMap<>();
        this.counters = new HashMap<>();
    }

    /**
     * Search for Query in cache
     * @param query
     * @return The result of the lookup. Null if not found
     */
    public K get(V query)
    {
        // Check cache
        K result = recognizer.recognize(query);
        // Check if we have a result
        if(recognizer.valid(result))
            updateCounter(result, 1);
        return result;
    }

    /**
     * Put training feature in cache.
     * @param feature
     * @param key
     */
    public synchronized void put(K key, V feature)
    {
        updateCounter(key, 1);
        // Check if knownItems does not have key
        if(!knownItems.containsKey(key))
        {
            // Insert in knownItems
            knownItems.put(key, feature);
            // Call training method
            onNewItem();
        }
        else if(!getCachedItems().contains(key))
            onNewItem();
    }

    /**
     * @return The items that are actually stored in the cache
     */
    protected abstract Collection<K> getCachedItems();

    /**
     * Called when new k-v pair is inserted in the knownItems
     */
    protected abstract void onNewItem();

    /**
     * Update the query counter for given key
     * @param key Key to update
     * @param value Value to update with
     */
    public synchronized void updateCounter(K key, Integer value)
    {
        if(!this.counters.containsKey(key))
            this.counters.put(key, new MutableInt(value));
        else
            this.counters.get(key).add(value);
        total_queries=+value;
    }

    /**
     * @param key
     * @return true if cache has key, false otherwise
     */
    public boolean contains(K key)
    {
        return this.knownItems.containsKey(key);
    }

    /**
     * Return Value for given Key
     * @param key
     * @return
     */
    public V getValue(K key)
    {
        return knownItems.get(key);
    }
}
