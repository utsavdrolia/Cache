package edu.cmu.edgecache.predictors.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by utsav on 4/5/17.
 */
public class BoundedFunctionUtils
{
    public static final String COEFFS = "coeffs";
    public static final String UPPER = "upper";
    public static final String LOWER = "lower";

    public static BoundedFunction readFromJSON(String path) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> objectMap = new HashMap<>();
        objectMap = mapper.readValue(new File(path), objectMap.getClass());

        double[] coeffs = ArrayUtils.toPrimitive((Double[]) objectMap.get(COEFFS));
        double upper = (double) objectMap.get(UPPER);
        double lower = (double) objectMap.get(LOWER);

        return new BoundedFunction(coeffs, lower, upper);
    }
}
