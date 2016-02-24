package org.crowdcache.rpc;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by utsav on 6/18/15.
 * Simple class to handle {@link GetResponseCallback}s in a separate thread
 */
class CallbackHandler
{
    private Map<Integer, GetResponseCallback> mCallbacks;
    private ExecutorService mExecutorService;
    CallbackHandler()
    {
        mCallbacks = new Hashtable<>();
        mExecutorService = Executors.newSingleThreadExecutor();
    }


    /**
     * Register call back
     * @param id the key for which this callback
     * @param cb The {@link GetResponseCallback}
     */
    void putCallback(Integer id, GetResponseCallback cb)
    {
        mCallbacks.put(id, cb);
    }

    /**
     * Call the callback for given key
     * @param id
     * @param val
     * @return 1 if callback will be eventually called, 0 if too many callbacks already in queue to be called, -1 if no callback registered for key
     */
    void callCallback(final Integer id, final byte[] val)
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
