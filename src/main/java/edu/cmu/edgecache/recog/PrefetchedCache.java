package edu.cmu.edgecache.recog;

import edu.cmu.edgecache.predictors.LatencyEstimator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by utsav on 3/10/17.
 */
public class PrefetchedCache<K extends Comparable<K>, V> extends AbstractRecogCache<K, V>
{
    private Set<K> prefetchedItems = new HashSet<>();

    // Latency predictor
    private LatencyEstimator latencyPredictor;
    final static Logger logger = LoggerFactory.getLogger(PrefetchedCache.class);

    /**
     * @param recognizer The underlying recognizer to use for lookups
     */
    public PrefetchedCache(RecognizeInterface<K, V> recognizer, double[] f_k_coeffs, double[] recall_k_coeffs)
    {
        super(recognizer);
        latencyPredictor = new LatencyEstimator(new PolynomialFunction(f_k_coeffs), new PolynomialFunction(recall_k_coeffs));
    }

    /**
     *
     * @param pdf
     * @return
     */
    public synchronized Map<K, Double> updatePDF(K currentItem, Map<K, Double> pdf)
    {
        double misslatency = latencyPredictor.getMissPenalty();
        logger.debug("Miss Penalty=" + misslatency);

        Map<K, Double> prefetchList = new HashMap<>();
        int best_size = 1;

        logger.debug("Clear cached items");
        prefetchedItems.clear();

        // First check if fetching current Item is worth it
        if(latencyPredictor.expectedLatency(best_size, 1.0) < misslatency)
        {
            logger.debug("Add current item to training list");
            List<K> training_list = new ArrayList<>();
            double p_cached = 0;
            // Order according to value
            LinkedHashMap<K, Double> orderedPdf = Utils.orderedMap(pdf);
            double best_latency = Double.MAX_VALUE;
            // Find best size
            for (Map.Entry<K, Double> pdf_entry : orderedPdf.entrySet())
            {
                Double prob = pdf_entry.getValue();
                if(prob > 0)
                {
                    // logger.debug("PDF item:" + pdf_entry.getKey() + " prob:" + prob);
                    // P(cached) is dependent on size = sum(top-k most likely items)
                    p_cached += pdf_entry.getValue();
                    best_size += 1;
                    double lat = latencyPredictor.expectedLatency(best_size, p_cached);
                    logger.debug("Predicted latency=" + lat + " for k=" + best_size);
                    if (lat > best_latency)
                    {
                        // Previous size was best size
                        best_size -= 1;
                        break;
                    }
                    best_latency = lat;
                    training_list.add(pdf_entry.getKey());
                }
                else
                {
                    logger.debug("Prob=0");
                    break;
                }
            }

            // Ensure that the best latency is faster than miss latency
            // If not, only cache current item
            if(best_latency > misslatency)
            {
                training_list.clear();
                best_size = 1;
            }
            training_list.add(currentItem);

            logger.debug("Best Size predicted=" + best_size);
            // Use training list to find out which items are known, which need to be prefetched
            for (K item : training_list)
            {
                // Update prefetchedItems with known relevant items
                if (knownItems.containsKey(item))
                    prefetchedItems.add(item);
                else
                    if (item.equals(currentItem))
                        prefetchList.put(item, 1.0);
                    else
                        prefetchList.put(item, orderedPdf.get(item));
            }
            // Train using prefetchedItems
            trainCachedItems();
        }
        else
        {
            logger.debug("Miss Penalty too low. Not fetching current or any other items");
        }
        // return items to be prefetched
        // Prefetcher will insert items using put
        return prefetchList;
    }

    @Override
    protected synchronized void _put(K key)
    {
        if(!prefetchedItems.contains(key))
        {
            prefetchedItems.add(key);
            trainCachedItems();
        }
    }

    @Override
    public void updateMissLatency(long missLatency)
    {
        latencyPredictor.updateMissPenalty((double) missLatency);
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
        for (K entry : prefetchedItems)
        {
            logger.debug("Training Cache with:" + entry);
            trainingMap.put(entry, knownItems.get(entry));
        }
        this.recognizer.train(trainingMap);
    }
}
