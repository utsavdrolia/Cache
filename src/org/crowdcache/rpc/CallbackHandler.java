package org.crowdcache.rpc;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by utsav on 6/18/15.
 * Simple class to handle {@link GetEventsResponseCallback}s in a separate thread
 */
class CallbackHandler
{
    private Map<Integer, GetEventsResponseCallback> mCallbacks;
    private ExecutorService mExecutorService;
    CallbackHandler()
    {
        mCallbacks = new Hashtable<>();
        mExecutorService = Executors.newSingleThreadExecutor();
    }


    /**
     * Register call back
     * @param id the key for which this callback
     * @param cb The {@link GetEventsResponseCallback}
     */
    void putCallback(Integer id, GetEventsResponseCallback cb)
    {
        mCallbacks.put(id, cb);
    }

    /**
     * Call the callback for given key
     * @param id
     * @param val
     */
    void callCallback(final Integer id, final List<Integer> val)
    {
        if(mCallbacks.containsKey(id))
        {
            mExecutorService.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    mCallbacks.get(id)._done(val);
                }
            });
        }
    }

    /**
     * Stop this CallBack Handler
     */
    void stop()
    {
        mExecutorService.shutdown();
    }

}
