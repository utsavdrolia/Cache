package edu.cmu.edgecache;

/**
 * Created by utsav on 2/9/16.
 */
public interface Cache<K extends Object, V extends Object>
{
    /**
     * Put an object into the cache
     * @param key
     * @param value
     */
    void put(K key, V value);

    /**
     * Get the {@link Cache.Result} for this key which contains the value and confidence metric
     * @param key
     * @return
     */
    Result<V> get(K key);

    public class Result<V>
    {
        public Result(Double confidence, V value)
        {
            this.confidence = confidence;
            this.value = value;
        }

        public Double confidence;
        public  V value;

        public Result(V value)
        {
            this.confidence = Double.MIN_VALUE;
            this.value = value;
        }
    }
}
