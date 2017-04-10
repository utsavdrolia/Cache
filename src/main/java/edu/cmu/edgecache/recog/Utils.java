package edu.cmu.edgecache.recog;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by utsav on 3/10/17.
 */
public class Utils
{
    public static <K, V extends Comparable<V>> List<Pair<K, V>> orderedMap(Map<K, V> map)
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
        List<Pair<K, V>> ret = new ArrayList<>();
        for (Map.Entry<K, V> entry: entries)
        {
            ret.add(Pair.of(entry.getKey(), entry.getValue()));
        }
        return ret;
    }
}
