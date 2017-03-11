package edu.cmu.edgecache.recog;

import edu.cmu.edgecache.predictors.LatencyEstimator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Optimized LFU Cache for image recognition- an adaptive LFU cache that measures latencies and adapts by changing cache size
 * Created by utsav on 9/14/16.
 */
public class OptRecogCache<K extends Comparable<K>, V> extends AbstractRecogCache<K, V>
{
    // Active cache
    private List<K> cachedItems = null;
    // alpha map
    private Map<K, Integer> ALPHA_MAP;
    // Alpha sum
    private Integer alpha_sum = 0;

    // Latency predictor
    private LatencyEstimator latencyPredictor;
    // Update interval
    private final static long INTERVAL = 50;
    // Search width
    private final static int SEARCH_WIDTH = 10;
    // Debug
    private final static boolean DEBUG = true;

    /**
     * @param recognizer The underlying recognizer to use for lookups
     * @param f_k_coeffs f(k) coefficients
     * @param recall_k_coeffs recall(k) coefficients
     * @param all_objects Total number of objects that can possibly be requested
     */
    public OptRecogCache(RecognizeInterface<K, V> recognizer, double[] f_k_coeffs, double[] recall_k_coeffs, Collection<K> all_objects)
    {
        super(recognizer);
        this.size = 0;
        latencyPredictor = new LatencyEstimator(new PolynomialFunction(f_k_coeffs), new PolynomialFunction(recall_k_coeffs));
        ALPHA_MAP = new HashMap<>();
        // Initialize everything to 1 for MAP
        for (K object:all_objects)
        {
            ALPHA_MAP.put(object, 1);
            alpha_sum += 1;
        }
        this.setInterval(INTERVAL);
    }

    /**
     * @param recognizer The underlying recognizer to use for lookups
     * @param f_k_coeffs f(k) coefficients
     * @param recall_k_coeffs recall(k) coefficients
     * @param init_prob Initial probabilities of requests from previous runs
     */
    public OptRecogCache(RecognizeInterface<K, V> recognizer, double[] f_k_coeffs, double[] recall_k_coeffs, Map<K, Integer> init_prob)
    {
        super(recognizer);
        latencyPredictor = new LatencyEstimator(new PolynomialFunction(f_k_coeffs), new PolynomialFunction(recall_k_coeffs));

        // Initialize everything for MAP
        for (K object:init_prob.keySet())
        {
            ALPHA_MAP.put(object, init_prob.get(object));
            alpha_sum += init_prob.get(object);
        }
        this.setInterval(INTERVAL);
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
    protected void _put(K key)
    {
        // No need to implement. Drop it. Instead this cache is renewed in intervals
    }

    /**
     * Runs on a miss and whenever set Interval is reached
     */
    @Override
    protected void onInterval()
    {
        try
        {
//            System.out.println("OnNewItem called");

            Double minLatency = Double.MAX_VALUE;
            int best_size = this.size;
            // Search for minimum latency
            for (int k = max(0, this.size - SEARCH_WIDTH); k < min(this.size + SEARCH_WIDTH, knownItems.size()); k++)
            {
                Double EL = latencyPredictor.expectedLatency(k, probabilityOfCacheHit(k));
                if (EL < minLatency)
                {
                    best_size = k;
                    minLatency = EL;
                }
            }
            this.size = best_size;
            cachedItems = counters.getOrderedEntries(this.size);
            if (cachedItems != null)
            {
                Map<K, V> trainingMap = new HashMap<>();
                for (K entry : cachedItems)
                {
                    trainingMap.put(entry, knownItems.get(entry));
                }
                this.recognizer.train(trainingMap);


                System.out.println("CacheSize:" + cachedItems.size());
//                System.out.println("CacheItems:" + counters.getCounts(cachedItems));
            }
        } catch (RuntimeException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param k Cache size
     * @return Probability that a randomly selected object is stored in the cache -
     * basically the probability that the requested object is one of the top-k requests
     */
    private Double probabilityOfCacheHit(int k)
    {
        Double probability = 0.0;
        List<K> orderedEntries = counters.getOrderedEntries(k);
        for (K entry:orderedEntries)
        {
            probability += probabilityOfRequest(entry);
        }
        return probability;
    }

    /**
     * MAP Estimate of P(Request)
     * @param entry Request
     * @return P(Request)
     */
    private Double probabilityOfRequest(K entry)
    {
        return ((double) (counters.getCount(entry) + ALPHA_MAP.get(entry))) / (counters.getSumFreq() + alpha_sum);
    }
}
