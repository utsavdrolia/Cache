package edu.cmu.edgecache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by utsav on 2/9/16.
 */
public class LRUCache<K extends Object, V extends Object> extends LinkedHashMap<K, V>
{
    private Integer size;
    public LRUCache(Integer size)
    {
        super(size,5.0f, true);
        this.size = size;
    }

    public LRUCache(Map<K, V> input, Integer size)
    {
        super(size,5.0f, true);
        this.size = size;
        this.putAll(input);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> entry)
    {
        return size() > size;
    }
}
