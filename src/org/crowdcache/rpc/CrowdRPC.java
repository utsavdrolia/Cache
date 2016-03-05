package org.crowdcache.rpc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyrax.AppMessage;
import org.hyrax.Hyrax;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by utsav on 2/19/16.
 */
public class CrowdRPC
{
    private final Hyrax mHyrax;
    private final WorkerThread mWorkerThread;
    private final GetRequestCallback mGetReqCB;
    private Integer msgID = 0;
    private CallbackHandler mCallbackHandler;
    private ExecutorService getRequestExecutor = Executors.newSingleThreadExecutor();

    public CrowdRPC(GetRequestCallback cb)
    {
        mCallbackHandler = new CallbackHandler();
        mHyrax = new Hyrax();
        mWorkerThread = new WorkerThread();
        mGetReqCB = cb;
    }

    /**
     * Start CrowdRPC
     */
    public void start()
    {
        mHyrax.start();
        mWorkerThread.start();
    }

    /**
     * Stop CrowdRPC
     */
    public void stop()
    {
        mWorkerThread.stopNow();
        mCallbackHandler.stop();
        mHyrax.stop();
    }

    /**
     * Local thread that recvs from Hyrax
     */
    private class WorkerThread extends Thread
    {
        private boolean stopped;

        WorkerThread()
        {
            stopped = false;
        }

        // Sets stop to true and waits till thread exits
        protected void stopNow()
        {
            stopped = true;
            while(this.isAlive());
        }

        private boolean isStopped()
        {
            return stopped;
        }

        public void run()
        {
            try
            {
                while (!Thread.interrupted() && !isStopped())
                {
                    //Now receive
                    AppMessage krowdmsg = mHyrax.recv();
                    if(krowdmsg != null)
                    {
                        byte[] data = krowdmsg.getData();
                        final String cookie = krowdmsg.getCookie();
                        final Long recvdAt = krowdmsg.getRecvdAt();
                        final CrowdCacheProto.CrowdCacheMsg msg = CrowdCacheProto.CrowdCacheMsg.parseFrom(data);
                        switch (msg.getMsgType())
                        {
                            case GET_REQ:
                                getRequestExecutor.submit(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        processGetReq(msg.getVal(), msg.getId(), cookie, recvdAt);
                                    }
                                });

                                break;
                            case GET_RESP:
                                processGetResp(msg.getVal(), msg.getId());
                                break;
                        }
                    }
                    else
                        Thread.sleep(2);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            } catch (InvalidProtocolBufferException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the value for a key by querying some peers
     * @param data The data to be queried
     * @param callBack The  {@link GetResponseCallback} to be called when key-value is found
     * @param i Number of peers to query
     * @return
     */
    public int get(byte[] data, GetResponseCallback callBack, int i)
    {
        // Get new ID
        Integer id = getnextid();

        // Broadcast message
        byte[] krowdmsg = CrowdCacheProto.CrowdCacheMsg.newBuilder().
                setMsgType(CrowdCacheProto.CrowdCacheMsg.MsgType.GET_REQ).
                setId(id).
                setVal(ByteString.copyFrom(data)).
                build().toByteArray();
        mCallbackHandler.putCallback(id, callBack);
        return mHyrax.multicast(krowdmsg, i);
    }

    /**
     * Get the value for a key
     * @param data The data to be queried
     * @param callBack The  {@link GetResponseCallback} to be called when key-value is found
     */
    public int get(byte[] data, GetResponseCallback callBack)
    {
        // Get new ID
        Integer id = getnextid();

        // Broadcast message
        byte[] krowdmsg = CrowdCacheProto.CrowdCacheMsg.newBuilder().
                setMsgType(CrowdCacheProto.CrowdCacheMsg.MsgType.GET_REQ).
                setId(id).
                setVal(ByteString.copyFrom(data)).
                build().toByteArray();
        mCallbackHandler.putCallback(id, callBack);
        return mHyrax.broadcast(krowdmsg);
    }

    /**
     * Process Get response from peer
     * @param id id of request
     * @param result Retrieved value
     */
    private void processGetResp(ByteString result, int id)
    {
        mCallbackHandler.callCallback(id, result.toByteArray());
    }

    /**
     * Process Get request from peer
     * @param val Requested data
     * @param id request id
     * @param cookie Cookie for reply
     */
    private void processGetReq(ByteString val, Integer id, String cookie, Long recvdAt)
    {
        byte[] data = val.toByteArray();

        // Call the registered callback
        byte[] result = mGetReqCB._get(data, recvdAt);

        // Send back reply
        CrowdCacheProto.CrowdCacheMsg.Builder reply;
        reply = CrowdCacheProto.CrowdCacheMsg.newBuilder().
                setMsgType(CrowdCacheProto.CrowdCacheMsg.MsgType.GET_RESP).
                setId(id).
                setVal(ByteString.copyFrom(result));
        mHyrax.reply(cookie, reply.build().toByteArray());
    }

    /**
     *
     * @return Keys to be used to track requests
     */
    private Integer getnextid()
    {
        return this.msgID++;
    }
}
