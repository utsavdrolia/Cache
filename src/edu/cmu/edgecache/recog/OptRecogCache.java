package edu.cmu.edgecache.recog;

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
public class OptRecogCache<K extends Comparable, V> extends AbstractRecogCache<K, V>
{
    // Active cache
    private List<K> cachedItems = null;
    // alpha map
    private Map<K, Integer> ALPHA_MAP;
    // Alpha sum
    private Integer alpha_sum = 0;
    // f(k)
    private final PolynomialFunction f_k;
    // recall(k)
    private final PolynomialFunction recall_k;
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
        f_k = new PolynomialFunction(f_k_coeffs);
        recall_k = new PolynomialFunction(recall_k_coeffs);
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
        f_k = new PolynomialFunction(f_k_coeffs);
        recall_k = new PolynomialFunction(recall_k_coeffs);

        // Initialize everything for MAP
        for (K object:init_prob.keySet())
        {
            ALPHA_MAP.put(object, init_prob.get(object));
            alpha_sum += init_prob.get(object);
        }
        this.setInterval(INTERVAL);
    }

    @Override
    protected List<K> getCachedItems()
    {
        return cachedItems;
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
                Double EL = ExpectedLatency(k);
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
     * Estimate expected latency for given cache size
     * @param k
     * @return Expected latency in milliseconds
     */
    private Double ExpectedLatency(int k)
    {
//        System.out.println("----------------------------------------------------------------");
//        System.out.println("f(k)=" + f_k);
//        System.out.println("recall(k)=" + recall_k);
//        System.out.println("k=" + k);
        // P(cached)
        Double probCached =probabilityOfCached(k);
//        System.out.println("P(cached)=" + probCached);
        // 1 - recall(k)*P(cached)
        Double total_miss_rate = 1 - recall_k.value(k)*probCached;
//        System.out.println("1 - recall(k)*P(cached)=" + total_miss_rate);
        // (1 - recall(k)*P(cached))*(Miss_penalty)
        Double miss_latency = total_miss_rate*this.getMissPenalty();
//        System.out.println("(1 - recall(k)*P(cached))*(Miss_penalty)=" + miss_latency);
        // f(k) + (1 - recall(k)*P(cached))*(Miss_penalty)
        Double E_L = f_k.value(k) + miss_latency;
//        System.err.println("EL=" + E_L);
//        System.out.println("----------------------------------------------------------------");

        return E_L;
    }

    /**
     * @param k Cache size
     * @return Probability that a randomly selected object is stored in the cache -
     * basically the probability that the requested object is one of the top-k requests
     */
    private Double probabilityOfCached(int k)
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
