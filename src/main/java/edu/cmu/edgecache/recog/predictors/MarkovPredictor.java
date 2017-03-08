package edu.cmu.edgecache.recog.predictors;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by utsav on 3/7/17.
 */
public class MarkovPredictor<T>
{
    private MarkovPredictorBase predictor;
    private BidiMap<T, Integer> states;

    public MarkovPredictor(List<T> states)
    {
        initializeMap(states);
        this.predictor = new MarkovPredictorBase(states.size());
    }

    public MarkovPredictor(List<T> states, int prior)
    {
        initializeMap(states);
        this.predictor = new MarkovPredictorBase(states.size(), prior);
    }

    private void initializeMap(List<T> states)
    {
        this.states = new DualHashBidiMap<>();
        for (T state :
                states)
        {
            this.states.put(state, this.states.size());
        }
    }

    public void updateTransition(T from_state, T to_state)
    {
        int from = states.get(from_state);
        int to = states.get(to_state);
        this.predictor.updateTransition(from, to);
    }

    public Map<T, Double> getNextPDF(T from_state)
    {
        Map<T, Double> pdf = new HashMap<>();

        int from = states.get(from_state);
        double[] probs = this.predictor.getNextPDF(from);

        for (int i = 0; i < probs.length; i++)
        {
            pdf.put(this.states.getKey(i), probs[i]);
        }

        return pdf;
    }

    public T predict(T from)
    {
        return this.states.getKey(this.predictor.predict(this.states.get(from)));
    }
}
