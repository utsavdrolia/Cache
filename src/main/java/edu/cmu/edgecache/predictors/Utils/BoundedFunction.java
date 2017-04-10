package edu.cmu.edgecache.predictors.Utils;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;

/**
 * Created by utsav on 4/4/17.
 */
public class BoundedFunction extends PolynomialFunction
{
    private static final String COEFFS = "coeffs";
    private final double lower;
    private final double upper;

    /**
     *
     * @param c coefficients
     * @param lower lower bound
     * @param upper upper bound
     * @throws NullArgumentException
     * @throws NoDataException
     */
    public BoundedFunction(double[] c, double lower, double upper) throws NullArgumentException, NoDataException
    {
        super(c);
        this.lower = lower;
        this.upper = upper;
    }


    @Override
    public double value(double x)
    {
        if(x < lower || x > upper)
            return Double.NaN;
        return super.value(x);
    }
}
