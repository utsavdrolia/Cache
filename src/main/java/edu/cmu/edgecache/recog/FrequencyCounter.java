package edu.cmu.edgecache.recog;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.math3.stat.Frequency;

import java.util.*;

/**
 * Created by utsav on 9/14/16.
 */
public class FrequencyCounter<K extends Comparable> extends Frequency
{
    public List<K> getOrderedEntries(int k)
    {
        List<Map.Entry<Comparable<?>, Long>> entries = IteratorUtils.toList(this.entrySetIterator());
        Collections.sort(entries, new Comparator<Map.Entry<Comparable<?>, Long>>()
        {
            @Override
            public int compare(Map.Entry<Comparable<?>, Long> o1, Map.Entry<Comparable<?>, Long> o2)
            {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        List<K> list = new ArrayList<>();
        List<Map.Entry<Comparable<?>, Long>> sublist;
        if(entries.size() > k)
            sublist = entries.subList(0, k);
        else
            sublist = entries;
        for (Map.Entry<Comparable<?>, Long> entry: sublist)
        {
            list.add((K) entry.getKey());
        }
        return list;
    }

    public long getSumFreq(Collection<K> items)
    {
        long sum = 0;
        for (K item: items)
        {
            sum += this.getCount(item);
        }
        return sum;
    }

    public Map<K, Long> getCounts(List<K> keys)
    {
        Map<K, Long> map = new LinkedHashMap<>();
        for (K key : keys)
        {
            map.put(key, this.getCount(key));
        }
        return map;
    }
}
