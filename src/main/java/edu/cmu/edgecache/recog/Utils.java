package edu.cmu.edgecache.recog;

import java.util.*;

/**
 * Created by utsav on 3/10/17.
 */
public class Utils
{
    public static <K, V extends Comparable<V>> LinkedHashMap<K, V> orderedMap(Map<K, V> map)
    {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, V>>()
        {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
            {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        LinkedHashMap<K, V> ret = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry: entries)
        {
            ret.put(entry.getKey(), entry.getValue());
        }
        return ret;
    }
}
