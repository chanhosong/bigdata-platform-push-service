package com.hhi.bigdata.platform.push.server.handler;

/**
 * Created by SongChanHo on 2016. 3. 18..
 */
import asia.stampy.common.StampyLibrary;
import asia.stampy.server.listener.subscription.StampyAcknowledgementHandler;

/**
 * The Class SystemAcknowledgementHandler.
 */
@StampyLibrary(libraryName = "stampy-examples")
public class SystemAcknowledgementHandler implements StampyAcknowledgementHandler {

    /*
     * (non-Javadoc)
     *
     * @see
     * asia.stampy.server.mina.subscription.StampyAcknowledgementHandler#ackReceived
     * (java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void ackReceived(String id, String receipt, String transaction) throws Exception {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * asia.stampy.server.mina.subscription.StampyAcknowledgementHandler#nackReceived
     * (java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void nackReceived(String id, String receipt, String transaction) throws Exception {

    }

    /*
     * (non-Javadoc)
     *
     * @see asia.stampy.server.mina.subscription.StampyAcknowledgementHandler#
     * noAcknowledgementReceived(java.lang.String)
     */
    @Override
    public void noAcknowledgementReceived(String id) {
        System.out.println("No acknowledgement received for " + id);
    }
}