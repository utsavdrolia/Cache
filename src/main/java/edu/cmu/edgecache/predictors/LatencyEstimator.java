package edu.cmu.edgecache.predictors;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created by utsav on 3/10/17.
 */
public class LatencyEstimator
{
    // f(k)
    // TODO: allow updates to f(k)
    private final PolynomialFunction f_k;

    // recall(k)
    private final PolynomialFunction recall_k;

    // Miss penalty
    private DescriptiveStatistics missLatency = new DescriptiveStatistics(10);

    public LatencyEstimator(PolynomialFunction f_k, PolynomialFunction recall_k)
    {
        this.f_k = f_k;
        this.recall_k = recall_k;
    }

    /**
     * Estimate expected latency for given cache size
     *
     * @param k Cache size
     * @param probCacheHit P(cached) for this cache size
     * @return Expected latency in milliseconds
     */
    public Double expectedLatency(int k, Double probCacheHit)
    {
//        System.out.println("----------------------------------------------------------------");
//        System.out.println("f(k)=" + f_k);
//        System.out.println("recall(k)=" + recall_k);
//        System.out.println("k=" + k);
//        System.out.println("P(cached)=" + probCacheHit);
        // 1 - recall(k)*P(cached)
        Double total_miss_rate = 1 - recall_k.value(k) * probCacheHit;
//        System.out.println("1 - recall(k)*P(cached)=" + total_miss_rate);
        // (1 - recall(k)*P(cached))*(Miss_penalty)
        Double miss_latency = total_miss_rate * this.getMissPenalty();
//        System.out.println("(1 - recall(k)*P(cached))*(Miss_penalty)=" + miss_latency);
        // f(k) + (1 - recall(k)*P(cached))*(Miss_penalty)
        Double E_L = f_k.value(k) + miss_latency;
//        System.err.println("EL=" + E_L);
//        System.out.println("----------------------------------------------------------------");

        return E_L;
    }

    public Double getMissPenalty()
    {
        return missLatency.getMean();
    }

    /** Update the miss penalty
     * @param missPenalty
     */
    public void updateMissPenalty(Double missPenalty)
    {
        this.missLatency.addValue(missPenalty);
    }
}
