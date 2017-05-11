package edu.cmu.edgecache.recog;

import edu.cmu.edgecache.LRUCache;
import edu.cmu.edgecache.predictors.LatencyEstimator;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by utsav on 3/10/17.
 */
public class PrefetchedCache<K extends Comparable<K>, V> extends AbstractRecogCache<K, V>
{
    private Map<Integer, LatencyEstimator> latencyEstimatorMap = null;
    private Set<K> prefetchedItems = new HashSet<>();
    private Map<K, Double> toPrefetch = new HashMap<>();
    private LRUCache<K, RecognizeInterface<K, V>> trainedRecognizers;

    // Latency predictor
    final static Logger logger = LoggerFactory.getLogger(PrefetchedCache.class);

    /**
     * @param recognizer The underlying recognizer to use for lookups
     */
    public PrefetchedCache(RecognizeInterface<K, V> recognizer, double[] f_k_coeffs, double[] recall_k_coeffs, int extracted_keypoints)
    {
        super(recognizer);
        latencyEstimatorMap = new HashMap<>();
        latencyEstimatorMap.put(extracted_keypoints, new LatencyEstimator(new PolynomialFunction(f_k_coeffs), new PolynomialFunction(recall_k_coeffs)));
    }

    /**
     * @param recognizer The underlying recognizer to use for lookups
     */
    public PrefetchedCache(RecognizeInterface<K, V> recognizer, Map<Integer, LatencyEstimator> latencyEstimatorMap)
    {
        super(recognizer);
        this.latencyEstimatorMap = latencyEstimatorMap;
        trainedRecognizers = new LRUCache<>(4);
    }

    /**
     *
     * @param pdf
     * @return
     */
    public synchronized Map<K, Double> updatePDF(K currentItem, Map<K, Double> pdf)
    {

        logger.debug("Clear cached items");
        prefetchedItems.clear();
        toPrefetch.clear();

        double best_latency = Double.MAX_VALUE;
        int best_size = 0;
        int best_num_descriptors = 600;

        // Order according to value
        List<Pair<K, Double>> orderedPdf = Utils.orderedMap(pdf);

        for (Integer extraction_size: latencyEstimatorMap.keySet())
        {
            Pair<Integer, Double> estimate = getBestLatency(latencyEstimatorMap.get(extraction_size), orderedPdf);

            double latency = estimate.getRight();
            int size = estimate.getLeft();

//            logger.debug(extraction_size + ": Best latency=" + latency + " for k=" + size);

            if (latency < best_latency)
            {
                best_size = size;
                best_latency = latency;
                best_num_descriptors = extraction_size;
            }
        }

        logger.debug(best_num_descriptors + ": BestLatency=" + best_latency + " for k=" + best_size);

        if(best_size > 0)
        {
            logger.debug("Add current item to training list");

            toPrefetch.put(currentItem, 1.0);
            
            if (best_size > 1)
            {
                // Use training list to find out which items are known, which need to be prefetched
                for (int i = 0; i < best_size; i++)
                {
                    Pair<K, Double> object = orderedPdf.get(i);
                    K item = object.getKey();
                    Double prob = object.getValue();
                    toPrefetch.put(item, prob);
                }
            }
        }
        else
        {
            logger.debug("Miss Penalty too low. Not fetching current or any other items");
        }

        // Update descriptor extraction
        try
        {
            recognizer.updateNumDescriptorsExtracted(best_num_descriptors);
            logger.debug("SetNumDescriptors:" + best_num_descriptors);
        } catch (IOException e)
        {
            logger.error("Error in Updating Pars", e);
        }

        // Change active recognizer
        if(!trainedRecognizers.containsKey(currentItem))
        {
            trainedRecognizers.put(currentItem, this.recognizer.newRecognizer());
        }

        this.recognizer = trainedRecognizers.get(currentItem);

        try
        {
            recognizer.updateNumDescriptorsExtracted(best_num_descriptors);
        } catch (IOException e)
        {
            logger.error("updateNumDescriptorsExtracted crashed", e);
        }

        logger.debug("Updated descriptor size");

        // Update  with known relevant items
        Set<K> set = new HashSet<>(toPrefetch.keySet());
        for (K item : set)
        {
            if (knownItems.containsKey(item))
                _put(item);
        }

        // return items to be prefetched
        // Prefetcher will insert items using put
        return new HashMap<>(toPrefetch);
    }


    private Pair<Integer, Double> getBestLatency(LatencyEstimator latencyPredictor, List<Pair<K, Double>> orderedPdf)
    {
        double best_latency = Double.MAX_VALUE;
        int best_size = 1;

        double misslatency = latencyPredictor.getMissPenalty();

        double p_cached = 0;
        int k = best_size;
        // Find best size
        for (Pair<K, Double> pdf_entry : orderedPdf)
        {
            Double prob = pdf_entry.getValue();
            if (prob > 0)
            {
                // logger.debug("PDF item:" + pdf_entry.getKey() + " prob:" + prob);
                // P(cached) is dependent on size = sum(top-k most likely items)
                p_cached += pdf_entry.getValue();
                k += 1;
                double lat = latencyPredictor.expectedLatency(k, p_cached);
//                logger.debug("k=" + k + " Latency=" + lat);
                // If latency not defined for this K, go onto next one
                if(Double.isNaN(lat))
                    continue;
                if (lat > best_latency)
                {
                    // Previous size was best size
                    break;
                }
                else
                {
                    best_latency = lat;
                    best_size = k;
                }

            } else
            {
                logger.debug("Prob=0");
                break;
            }
        }

        // Ensure that the best latency is faster than miss latency
        // If not, only cache current item
        if(best_latency > misslatency)
        {
            // check if fetching current Item is worth it
            double curent_item_latency = latencyPredictor.getF_K(best_size);
            if (curent_item_latency < misslatency)
            {
                best_size = 1;
                best_latency = curent_item_latency;
            } else
            {
                best_size = 0;
                best_latency = misslatency;
            }
        }

        return Pair.of(best_size,best_latency);
    }

    @Override
    protected synchronized void _put(K key)
    {
        if(toPrefetch.containsKey(key))
        {
            prefetchedItems.add(key);
            toPrefetch.remove(key);
        }
        // If all items have been fetched, train
        if(toPrefetch.isEmpty())
            trainCachedItems();
    }

    @Override
    protected void _init(Map<K, V> initMap)
    {
        prefetchedItems.addAll(initMap.keySet());
        logger.debug("Initializing Cache");
        this.recognizer.train(initMap);
        logger.debug("Initialized Cache");
    }

    @Override
    public void updateMissLatency(long missLatency)
    {
        for (Integer desc_num: this.latencyEstimatorMap.keySet())
        {
            latencyEstimatorMap.get(desc_num).updateMissPenalty((double) missLatency);
        }
    }

    @Override
    protected synchronized Collection<K> getCachedItems()
    {
        return prefetchedItems;
    }

    @Override
    protected void onInterval()
    {}

    /**
     * Internal training method. The recognizer optimizes training if was already trained on a subset of the cacheditems before
     */
    private void trainCachedItems()
    {
        Map<K, V> trainingMap = new HashMap<>();
        logger.debug("Training Cache with " + prefetchedItems.size() + " items");
        for (K entry : prefetchedItems)
        {
            trainingMap.put(entry, knownItems.get(entry));
        }
        logger.debug("Starting Training Cache");
        this.recognizer.train(trainingMap);
        logger.debug("Trained Cache");
    }
}
