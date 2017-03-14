package edu.cmu.edgecache.recog;

import edu.cmu.edgecache.predictors.LatencyEstimator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import java.util.*;

/**
 * Created by utsav on 3/10/17.
 */
public class PrefetchedCache<K extends Comparable<K>, V> extends AbstractRecogCache<K, V>
{
    private Set<K> cachedItems = new HashSet<>();

    // Latency predictor
    private LatencyEstimator latencyPredictor;

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
    public synchronized Collection<K> updatePDF(Map<K, Double> pdf)
    {
        // Order according to value
        LinkedHashMap<K, Double> orderedPdf = Utils.orderedMap(pdf);
        List<K> training_list = new ArrayList<>();
        List<K> prefetchList = new ArrayList<>();
        Double minLatency = Double.MAX_VALUE;
        int best_size = 0;
        double p_cached = 0;
        double best_latency = Double.MAX_VALUE;
        // Find best size
        for (Map.Entry<K, Double> pdf_entry :
                orderedPdf.entrySet())
        {
            // P(cached) is dependent on size = sum(top-k most likely items)
            p_cached += pdf_entry.getValue();
            best_size += 1;
            if(latencyPredictor.expectedLatency(best_size, p_cached) > best_latency)
            {
                // Previous size was best size
                break;
            }
            training_list.add(pdf_entry.getKey());
        }

        // clear cached items
        cachedItems.clear();
        // Use training list to find out which items are known, which need to be prefetched
        for (K item :
                training_list)
        {
            // Update cachedItems with known relevant items
            if(knownItems.containsKey(item))
                cachedItems.add(item);
            else
                prefetchList.add(item);
        }

        // Train using cachedItems
        trainCachedItems();
        // return items to be prefetched
        // Prefetcher will insert items using put
        return prefetchList;
    }

    @Override
    protected synchronized void _put(K key)
    {
        if(!cachedItems.contains(key))
        {
            cachedItems.add(key);
            trainCachedItems();
        }
    }

    @Override
    public void updateMissLatency(long missLatency)
    {
        latencyPredictor.updateMissPenalty((double) missLatency);
    }

    @Override
    protected Collection<K> getCachedItems()
    {
        return cachedItems;
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
        for (K entry : cachedItems)
        {
            trainingMap.put(entry, knownItems.get(entry));
        }
        this.recognizer.train(trainingMap);
    }
}
