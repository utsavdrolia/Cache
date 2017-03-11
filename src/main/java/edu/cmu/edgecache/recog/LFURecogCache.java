package edu.cmu.edgecache.recog;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the LFU caching mechanism
 * Created by utsav on 9/8/16.
 */
public class LFURecogCache<K extends Comparable<K>, V> extends AbstractRecogCache<K,V>
{
    private List<K> cachedItems = null;

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

    /**
     * Whenever a new Item not in the {@link LFURecogCache#cachedItems} is encountered,
     * train the recognizer with {@link this#size} or lesser most popular items
     * @param key
     *
     */
    @Override
    protected void _put(K key)
    {
        if(!cachedItems.contains(key))
        {
            System.out.println("OnNewItem called");
            List<K> currentOrderedEntries = counters.getOrderedEntries(this.size);
//        System.out.println("Ordered Entries:" + counters.getCounts(currentOrderedEntries));
            boolean updatecache = false;
            if (cachedItems != null)
            {
                if (counters.getSumFreq(currentOrderedEntries) > counters.getSumFreq(cachedItems))
                    updatecache = true;
            } else
                updatecache = true;

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
            System.out.println("Cache:" + cachedItems.size());
        }
    }

    @Override
    protected void onInterval()
    {}

}
