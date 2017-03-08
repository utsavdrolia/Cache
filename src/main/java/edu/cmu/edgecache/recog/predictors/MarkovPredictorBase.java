package edu.cmu.edgecache.recog.predictors;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.MathArrays;

/**
 * Created by utsav on 3/7/17.
 */
public class MarkovPredictorBase
{
    /**
     * Row = from, Column = to
     */
    private RealMatrix transitionMatrix;

    /**
     * @param numStates Number of states
     */
    public MarkovPredictorBase(int numStates)
    {
        this(MatrixUtils.createRealMatrix(numStates, numStates));
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
    public MarkovPredictorBase(int numStates, int uniform_prior)
    {
        this(numStates);
        this.transitionMatrix = this.transitionMatrix.scalarAdd(uniform_prior);
    }

    public void updateTransition(int from_id, int to_id)
    {
        this.transitionMatrix.addToEntry(from_id, to_id, 1.0);
    }

    public double[] getNextPDF(int from_id)
    {
        return MathArrays.normalizeArray(this.transitionMatrix.getRow(from_id), 1.0);
    }

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
