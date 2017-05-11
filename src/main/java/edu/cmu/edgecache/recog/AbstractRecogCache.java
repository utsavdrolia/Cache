package edu.cmu.edgecache.recog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This cache is different from typical caches. Given a {@link RecognizeInterface}, it looks up the given features and
 * matches it to a cached key. If nothing is found, it returns null. It is expected that the instantiating object will
 * then call {@link AbstractRecogCache#put(K, V)} to insert the the propoer K-V into the knownItems.
 * However, even at this point, recognition requests for the inserted V may not be replied correctly
 * since the underlying recognizer may not be trained yet. That depends on the extending class and how it chooses to implement
 * the {@link AbstractRecogCache#_put(Comparable)} function.
 * Extend this class and implement LFU or other such caching mechanisms.
 * Created by utsav on 9/7/16.
 */
public abstract class AbstractRecogCache<K extends Comparable<K>, V>
{
    private long INTERVAL;
    protected Map<K, V> knownItems;
    protected FrequencyCounter<K> counters;
    protected int size;
    protected RecognizeInterface<K, V> recognizer;
    private boolean isIntervalSet = false;
    private long prev_interval = 0;

    final static Logger logger = LoggerFactory.getLogger(AbstractRecogCache.class);

    /**
     *
     * @param recognizer The underlying recognizer to use for lookups
     */
    public AbstractRecogCache(RecognizeInterface<K, V> recognizer)
    {
        this.recognizer = recognizer;
        this.knownItems = new HashMap<>();
        this.counters = new FrequencyCounter<>();
    }

    /**
     * Search for Query in cache
     * @param query
     * @return The result of the lookup. Null if not found
     */
    public K get(V query)
    {
        isEmpty();
        logger.debug("Known items is empty: " + knownItems.isEmpty());
        // Check cache
        K result = recognizer.recognize(query);
        // Check if we have a result
        if(recognizer.isValid(result))
            updateCounter(result, 1);
        return result;
    }

    public boolean isEmpty()
    {
        if(getCachedItems() != null)
        {
            if(getCachedItems().isEmpty())
            {
                logger.debug("Actual Cached items is empty");
                return true;
            }
            return false;
        }
        logger.debug("Actual Cached items is null");
        return true;
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
        }
        // Call training method
        _put(key);

        if(isIntervalSet)
            if((counters.getSumFreq() - prev_interval) >= this.INTERVAL)
            {
                prev_interval = counters.getSumFreq();
                onInterval();
            }
    }

    /**
     * Initializes the cache
     * @param initMap
     */
    public void init(Map<K, V> initMap)
    {
        knownItems.putAll(initMap);
        for (K key :
                initMap.keySet())
        {
            updateCounter(key, 1);
        }
        _init(initMap);
    }

    /**
     * Initializes the cache. Implemented by extending classes
     * @param initMap
     */
    protected abstract void _init(Map<K, V> initMap);

    /**
     * @param key
     * @return true if cache has key, false otherwise
     */
    public boolean contains(K key)
    {
        return this.knownItems.containsKey(key);
    }

    /**
     *
     * @return The Items in the knownItems
     */
    public Collection<K> getAllKeys()
    {
        return this.knownItems.keySet();
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

    public int getSize()
    {
        return this.size;
    }

    public K invalid()
    {
        return recognizer.invalid();
    }

    public boolean isValid(K key)
    {
        return recognizer.isValid(key);
    }

    /**
     * Update the query counter for given key
     * @param key Key to update
     * @param value Value to update with
     */
    private synchronized void updateCounter(K key, Integer value)
    {
        counters.incrementValue(key, value);
    }

    /**
     * Caller class will need to update miss latency that the cache can use for internal calculations
     * @param missLatency
     */
    public abstract void updateMissLatency(long missLatency);

    /**
     * Set Interval if implementing classes want to be invoked at a regular interval
     * @param interval
     */
    protected void setInterval(long interval)
    {
        this.INTERVAL = interval;
        this.isIntervalSet = true;
    }

    /**
     * @return The items that are actually stored in the cache
     */
    protected abstract Collection<K> getCachedItems();

    /**
     * Called when new k-v pair is inserted in the knownItems
     * @param key
     *
     */
    protected abstract void _put(K key);

    /**
     * Called when set interval is reached
     */
    protected abstract void onInterval();
}
