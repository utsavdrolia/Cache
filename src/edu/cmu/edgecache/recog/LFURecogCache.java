package edu.cmu.edgecache.recog;

import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;

/**
 * Implements the LFU caching mechanism
 * Created by utsav on 9/8/16.
 */
public class LFURecogCache<K, V> extends AbstractRecogCache<K,V>
{
    private Map<K, MutableInt> cachedItems;

    public LFURecogCache(RecognizeInterface<K, V> recognizer, int size)
    {
        super(recognizer);
        this.size = size;
        cachedItems = new HashMap<>(this.size, 2);
    }

    @Override
    protected Collection<K> getCachedItems()
    {
        return this.cachedItems.keySet();
    }

    /**
     * Whenever a new Item not in the {@link LFURecogCache#cachedItems} is encountered,
     * train the recognizer with {@link this#size} or lesser most popular items
     */
    @Override
    protected void onNewItem()
    {
        System.out.println("OnNewItem called");
        List<Map.Entry<K, MutableInt>> orderedEntries = getOrderedEntries();
        System.out.println("Ordered Entries:" + orderedEntries.toString());

        int previous_sum = 0;
        for (Map.Entry<K , MutableInt> entry : cachedItems.entrySet())
        {
            previous_sum+=entry.getValue().getValue();
        }

        int new_sum = 0;
        for (Map.Entry<K , MutableInt> entry : orderedEntries)
        {
            new_sum+=entry.getValue().getValue();
        }
        // If new sum is same as previous sum, no change is needed
        if(new_sum > previous_sum)
        {
            cachedItems.clear();
            Map<K, V> trainingMap = new HashMap<>();
            for (Map.Entry<K , MutableInt> entry : orderedEntries)
            {
                cachedItems.put(entry.getKey(), counters.get(entry.getKey()));
                trainingMap.put(entry.getKey(), knownItems.get(entry.getKey()));
            }
            this.recognizer.train(trainingMap);
        }
        System.out.println("Cached Entries:" + cachedItems.toString());
    }

    /**
     * Returns an ordered list of most popular keys with maximum {@link this#size} items.
     * @return an ordered list of most popular keys
     */
    private List<Map.Entry<K, MutableInt>> getOrderedEntries()
    {
        List<Map.Entry<K,MutableInt>> entries = new ArrayList<>(counters.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, MutableInt>>()
        {
            @Override
            public int compare(Map.Entry<K, MutableInt> o1, Map.Entry<K, MutableInt> o2)
            {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        if(entries.size() > this.size)
            return entries.subList(0, size);
        else
            return entries;
    }

}
