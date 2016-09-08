package edu.cmu.edgecache.recog;

import java.util.*;

/**
 * Implements the LFU caching mechanism
 * Created by utsav on 9/8/16.
 */
public class LFURecogCache<K, V> extends AbstractRecogCache<K,V>
{
    public LFURecogCache(RecognizeInterface<K, V> recognizer, int size)
    {
        super(recognizer);
        this.size = size;
    }

    /**
     * Whenever a new Item is encountered, train the recognizer with {@link this#size} or lesser most popular items
     */
    @Override
    protected void onNewItem()
    {
        System.out.println("New Item Inserted in Cache");
        List<Map.Entry<K, Integer>> orderedEntries = getOrderedEntries();
        System.out.println("Ordered Entries:" + orderedEntries.toString());
        Map<K, V> trainingMap = new HashMap<>();
        for (Map.Entry<K , Integer> entry : orderedEntries)
        {
            trainingMap.put(entry.getKey(), knownItems.get(entry.getKey()));
        }
        this.recognizer.train(trainingMap);
    }

    /**
     * Returns an ordered list of most popular keys with maximum {@link this#size} items.
     * @return an ordered list of most popular keys
     */
    private List<Map.Entry<K, Integer>> getOrderedEntries()
    {
        List<Map.Entry<K,Integer>> entries = new ArrayList<>(counters.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, Integer>>()
        {
            @Override
            public int compare(Map.Entry<K, Integer> o1, Map.Entry<K, Integer> o2)
            {
                return o2.getValue() - o1.getValue();
            }
        });

        if(entries.size() > this.size)
            return entries.subList(0, size);
        else
            return entries;
    }

}
