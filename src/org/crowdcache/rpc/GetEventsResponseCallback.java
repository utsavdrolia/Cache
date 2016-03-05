package org.crowdcache.rpc;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by utsav on 2/19/16.
 */
public abstract class GetEventsResponseCallback
{
    private AtomicInteger numReplies = new AtomicInteger(0);
    public abstract void done(List<Integer> result);

    protected void _done(List<Integer> val)
    {
        numReplies.incrementAndGet();
        done(val);
    }

    public Integer getNumReplies()
    {
        return numReplies.get();
    }
}
