package org.crowdcache.rpc;

import java.util.List;

/**
 * Created by utsav on 2/19/16.
 */
public abstract class GetEventsRequestCallback
{
    /**
     * Calls the implemented {@link GetEventsRequestCallback#get} and serializes the result
     * @param starttime
     * @param stoptime
     * @param recvdAt
     * @return
     */
    abstract public List<Integer> get(Long starttime, Long stoptime, Long recvdAt);
}
