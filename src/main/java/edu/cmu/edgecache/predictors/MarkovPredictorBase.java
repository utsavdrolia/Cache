package edu.cmu.edgecache.predictors;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.MathArrays;

/**
 * Simple markov predictor. Contains a transition matrix and provides functions to update it and predict most likely next state.
 * Also provides {@link MarkovPredictorBase#getNextPDF(int)} to get a distribution over the next possible states.
 * Does NOT manage mapping states to integer ID and back.
 * Created by utsav on 3/7/17.
 */
public class MarkovPredictorBase
{
    /**
     * Row = from, Column = to
     */
    private RealMatrix transitionMatrix;

    /**
     * @param numStates Number of states - will create a square transition matrix
     */
    public MarkovPredictorBase(int numStates)
    {
        this(MatrixUtils.createRealMatrix(numStates, numStates));
    }

    /**
     * @param numStates_from Number of states from which transitions originate -
     *                       note this can be used to create higher order markov chains by representing a sequence as a "super state"
     * @param numState_to Number of states to which transitions occur - generally the total number of total states
     */
    public MarkovPredictorBase(int numStates_from, int numState_to)
    {
        this(MatrixUtils.createRealMatrix(numStates_from, numState_to));
    }

    /**
     * @param numStates_from Number of states from which transitions originate -
     *                       note this can be used to create higher order markov chains by representing a sequence as a "super state"
     * @param numState_to Number of states to which transitions occur - generally the total number of total states
     */
    public MarkovPredictorBase(int numStates_from, int numState_to, double uniform_prior)
    {
        this(MatrixUtils.createRealMatrix(numStates_from, numState_to));
        this.transitionMatrix = this.transitionMatrix.scalarAdd(uniform_prior);
    }

    /**
     * @param mat Priors for transition matrix - counts not probabilities
     */
    public MarkovPredictorBase(RealMatrix mat)
    {
        this.transitionMatrix = mat;
    }

    /**
     * @param numStates Number of states
     * @param uniform_prior Prior for transition matrix - will be applied to all transitions
     */
    public MarkovPredictorBase(int numStates, double uniform_prior)
    {
        this(numStates);
        this.transitionMatrix = this.transitionMatrix.scalarAdd(uniform_prior);
    }

    /**
     * Increment the transition count
     * @param from_id Originating state
     * @param to_id Destination state
     */
    public void incrementTransition(int from_id, int to_id)
    {
        this.addToTransition(from_id, to_id, 1.0);
    }

    /**
     * Increment the transition count
     * @param from_id Originating state
     * @param to_id Destination state
     */
    public void addToTransition(int from_id, int to_id, double add)
    {
        this.transitionMatrix.addToEntry(from_id, to_id, add);
    }

    /**
     * Returns the PDF over the next states given current state
     * @param from_id
     * @return
     */
    public double[] getNextPDF(int from_id)
    {
        return MathArrays.normalizeArray(this.transitionMatrix.getRow(from_id), 1.0);
    }

    /**
     * Predict the most likely next state
     * @param from_id
     * @return
     */
    public int predict(int from_id)
    {
        double[] to_counts = this.transitionMatrix.getRow(from_id);
        double max = 0;
        int index = -1;
        for (int i = 0; i < to_counts.length; i++)
        {
            if(to_counts[i] > max)
            {
                max = to_counts[i];
                index = i;
            }
        }
        return index;
    }

}
