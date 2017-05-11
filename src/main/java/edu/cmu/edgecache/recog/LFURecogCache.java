package edu.cmu.edgecache.recog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Implements the LFU caching mechanism
 * Created by utsav on 9/8/16.
 */
public class LFURecogCache<K extends Comparable<K>, V> extends AbstractRecogCache<K,V>
{
    private List<K> cachedItems = null;
    private final static Logger logger = LoggerFactory.getLogger(LFURecogCache.class);


    public LFURecogCache(RecognizeInterface<K, V> recognizer, int size)
    {
        super(recognizer);
        this.size = size;
    }

    @Override
    public void updateMissLatency(long missLatency)
    {
        // No use of measuring latency here. Drop it like its hot
    }

    @Override
    protected Collection<K> getCachedItems()
    {
        return cachedItems;
    }

    @Override
    protected void _init(Map<K, V> initMap)
    {
        cachedItems = new ArrayList<>(initMap.keySet());
        this.recognizer.train(initMap);
        logger.debug("Cache:" + cachedItems.size());
    }

    /**
     * Whenever a new Item not in the {@link LFURecogCache#cachedItems} is encountered,
     * train the recognizer with {@link this#size} or lesser most popular items
     * @param key
     *
     */
    @Override
    protected void _put(K key)
    {
        logger.debug("_put called with key:" + key.toString());
        List<K> currentOrderedEntries = counters.getOrderedEntries(this.size);
        boolean updatecache = false;
        if (cachedItems == null)
            updatecache = true;
        else if(!cachedItems.contains(key))
        {
            if (counters.getSumFreq(currentOrderedEntries) > counters.getSumFreq(cachedItems))
                updatecache = true;
        }

        // If new sum is greater than previous sum, update cache
        if (updatecache)
        {
            cachedItems = currentOrderedEntries;
            Map<K, V> trainingMap = new HashMap<>();
            for (K entry : currentOrderedEntries)
            {
                trainingMap.put(entry, knownItems.get(entry));
            }
            this.recognizer.train(trainingMap);
        }
        logger.debug("Cache:" + cachedItems.size());
    }

    @Override
    protected void onInterval()
    {}

}
