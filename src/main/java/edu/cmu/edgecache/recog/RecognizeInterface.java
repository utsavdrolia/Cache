package edu.cmu.edgecache.recog;

import java.io.IOException;
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
     * Tells us if a result is isValid
     * @param result
     * @return
     */
    boolean isValid(K result);

    public K invalid();

    /**
     * Change the number of points being extracted from images
     * @param num_descriptors
     */
    public void updateNumDescriptorsExtracted(int num_descriptors) throws IOException;

    public RecognizeInterface<K,V> newRecognizer();
}
