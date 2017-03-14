package edu.cmu.edgecache.predictors;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Meant to work as a wrapper around {@link MarkovPredictorBase}.
 * Manages mapping states to integer IDs and back.
 * State type - {@link T} must be hashable.
 * As of now only implements 1st order Markov chain. Can be extended to estimate higher models, without changing {@link MarkovPredictorBase}
 * Created by utsav on 3/7/17.
 */
public class MarkovPredictor<T>
{
    private MarkovPredictorBase predictor;
    private BidiMap<T, Integer> stateIDMap;
    final static Logger logger = LoggerFactory.getLogger(MarkovPredictor.class);

    /**
     * Create predictor with no priors
     * @param states
     */
    public MarkovPredictor(List<T> states)
    {
        initializeMap(states);
        this.predictor = new MarkovPredictorBase(states.size());
    }

    /**
     * Predictor with priors
     * @param states
     * @param prior
     */
    public MarkovPredictor(List<T> states, double prior)
    {
        initializeMap(states);
        this.predictor = new MarkovPredictorBase(states.size(), prior);
    }

    /**
     * Create the map from states to integer IDs
     * @param states
     */
    private void initializeMap(List<T> states)
    {
        this.stateIDMap = new DualHashBidiMap<>();
        for (T state :
                states)
        {
            logger.debug("Adding state to Predictor:" + state.toString());
            this.stateIDMap.put(state, this.stateIDMap.size());
        }
    }

    /**
     * Increment transition
     * @param from_state
     * @param to_state
     */
    public void incrementTransition(T from_state, T to_state)
    {
        int from = stateIDMap.get(from_state);
        int to = stateIDMap.get(to_state);
        this.predictor.incrementTransition(from, to);
    }

    /**
     * Distribution over next states
     * @param from_state
     * @return {@link Map<T,Double>} A map of state to probability of occurrence
     */
    public Map<T, Double> getNextPDF(T from_state)
    {
        Map<T, Double> pdf = new HashMap<>();

        int from = stateIDMap.get(from_state);
        logger.debug("From State:" + from_state + " maps to ID:" + from);
        double[] probs = this.predictor.getNextPDF(from);

        for (int i = 0; i < probs.length; i++)
        {
            pdf.put(this.stateIDMap.getKey(i), probs[i]);
        }

        return pdf;
    }

    /**
     * Predict most likely next state
     * @param from
     * @return
     */
    public T predict(T from)
    {
        return this.stateIDMap.getKey(this.predictor.predict(this.stateIDMap.get(from)));
    }
}
