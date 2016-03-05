package org.crowdcache.rpc;

import org.apache.commons.lang3.ArrayUtils;
import org.crowdcache.Cache;

import java.nio.ByteBuffer;

/**
 * Created by utsav on 2/19/16.
 */
public abstract class GetRequestCallback
{
    abstract public Cache.Result<Byte[]> get(byte[] data, Long recvdAt);

    /**
     * Calls the implemented {@link GetRequestCallback#get(byte[], Long)} and serializes the result
     * @param data
     * @return
     */
    protected byte[] _get(byte[] data, Long recvdAt)
    {
        Cache.Result<Byte[]> result = get(data, recvdAt);
        ByteBuffer ser = ByteBuffer.allocate((Double.SIZE/8) + result.value.length);
        ser.putDouble(result.confidence);
        ser.put(ArrayUtils.toPrimitive(result.value));
        return ser.array();
    }
}
