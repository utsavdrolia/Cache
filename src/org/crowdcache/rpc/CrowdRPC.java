package org.crowdcache.rpc;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import org.hyrax.AppMessage;
import org.hyrax.Hyrax;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by utsav on 2/19/16.
 */
public class CrowdRPC
{
    private final Hyrax mHyrax;
    private final WorkerThread mWorkerThread;
    private final GetEventsRequestCallback mGetReqCB;
    private Integer msgID = 0;
    private CallbackHandler mCallbackHandler;
    private ExecutorService getRequestExecutor = Executors.newSingleThreadExecutor();

    public CrowdRPC(GetEventsRequestCallback cb)
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
                        final CrowdRPCProto.CrowdMsg msg = CrowdRPCProto.CrowdMsg.parseFrom(data);
                        switch (msg.getMsgType())
                        {
                            case GET_EVENTS:
                                getRequestExecutor.submit(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        processGetEventsReq(msg.getGetEventsReq(), msg.getId(), cookie, recvdAt);
                                    }
                                });

                                break;
                            case GET_EVENTS_RESP:
                                processGetResp(msg.getGetEventsResp().getEventsList(), msg.getId());
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
     * Get the list of events by querying some peers
     * @param starttime Interval start time
     * @param stoptime Interval stop time
     * @param callBack The callback to call when results are available
     * @param i Number of peers to query
     * @return Number of peers contacted
     */
    public int getEvents(Long starttime, Long stoptime, GetEventsResponseCallback callBack, int i)
    {
        // Get new ID
        Integer id = getnextid();

        // Broadcast message
        byte[] krowdmsg = CrowdRPCProto.CrowdMsg.newBuilder().
                setMsgType(CrowdRPCProto.CrowdMsg.MsgType.GET_EVENTS).
                setId(id).
                setGetEventsReq(CrowdRPCProto.GetEventsReq.newBuilder().
                        setStartTime(starttime).
                        setEndTime(stoptime).build()).
                build().toByteArray();
        mCallbackHandler.putCallback(id, callBack);
        return mHyrax.multicast(krowdmsg, i);
    }

    /**
     * Get the list of events by querying all peers
     * @param starttime Interval start time
     * @param stoptime Interval stop time
     * @param callBack The callback to call when results are available
     * @return Number of peers contacted
     */
    public int getEvents(Long starttime, Long stoptime, GetEventsResponseCallback callBack)
    {
        // Get new ID
        Integer id = getnextid();

        // Broadcast message
        byte[] krowdmsg = CrowdRPCProto.CrowdMsg.newBuilder().
                setMsgType(CrowdRPCProto.CrowdMsg.MsgType.GET_EVENTS).
                setId(id).
                setGetEventsReq(CrowdRPCProto.GetEventsReq.newBuilder().
                        setStartTime(starttime).
                        setEndTime(stoptime).build()).
                build().toByteArray();
        mCallbackHandler.putCallback(id, callBack);
        return mHyrax.broadcast(krowdmsg);
    }

    /**
     * Process Get response from peer
     * @param id id of request
     * @param result Retrieved value
     */
    private void processGetResp(List<Integer> result, int id)
    {
        mCallbackHandler.callCallback(id, result);
    }

    /**
     * Process Get Events request from peer
     * @param val Requested data
     * @param id request id
     * @param cookie Cookie for reply
     */
    private void processGetEventsReq(CrowdRPCProto.GetEventsReq val, Integer id, String cookie, Long recvdAt)
    {
        Long start = val.getStartTime();
        Long end = val.getEndTime();

        // Call the registered callback
        List<Integer> result = mGetReqCB.get(start, end, recvdAt);

        // Send back reply
        CrowdRPCProto.CrowdMsg.Builder reply;
        reply = CrowdRPCProto.CrowdMsg.newBuilder().
                setMsgType(CrowdRPCProto.CrowdMsg.MsgType.GET_EVENTS_RESP).
                setId(id).
                setGetEventsResp(CrowdRPCProto.GetEventsResp.newBuilder().
                    addAllEvents(result));
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
