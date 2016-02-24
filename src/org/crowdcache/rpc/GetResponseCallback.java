package org.crowdcache.rpc;

import org.apache.commons.lang3.ArrayUtils;
import org.crowdcache.Cache;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by utsav on 2/19/16.
 */
public abstract class GetResponseCallback
{
    private AtomicInteger numReplies = new AtomicInteger(0);
    public abstract void done(Cache.Result<Byte[]> result);

    protected void _done(byte[] val)
    {
        numReplies.incrementAndGet();
        ByteBuffer ser = ByteBuffer.wrap(val);
        Double conf = ser.getDouble();
        byte[] res = new byte[ser.remaining()];
        ser.get(res);
        done(new Cache.Result<>(conf, ArrayUtils.toObject(res)));
    }

    public Integer getNumReplies()
    {
        return numReplies.get();
    }
}
