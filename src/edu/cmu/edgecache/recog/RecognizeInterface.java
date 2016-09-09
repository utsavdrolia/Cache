package edu.cmu.edgecache.recog;

import java.util.Map;

/**
 * Created by utsav on 9/7/16.
 */
public interface RecognizeInterface<K extends Object, V>
{
    /**
     * Recognize the value V and return the ID/key. or null if not found.
     * @param value
     * @return
     */
    public K recognize(V value);

    /**
     * Train the underlying recognizer
     * @param trainingMap
     */
    public void train(Map<K, V> trainingMap);

    /**
     * Tells us if a result is valid
     * @param result
     * @return
     */
    boolean valid(K result);
}
