package com.hhi.bigdata.platform.push.server.listener;

/**
 * Created by SongChanHo on 2016. 3. 18..
 */

import asia.stampy.common.StampyLibrary;
import asia.stampy.common.gateway.HostPort;
import asia.stampy.common.gateway.StampyMessageListener;
import asia.stampy.common.message.StampyMessage;
import asia.stampy.common.message.StompMessageType;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The listener interface for receiving testServerMessage events. The class that
 * is interested in processing a testServerMessage event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addTestServerMessageListener<code> method. When
 * the testServerMessage event occurs, that object's appropriate
 * method is invoked.
 *
 * see TestServerMessageEvent
 */
@StampyLibrary(libraryName = "stampy-examples")
public class TestServerMessageListener implements StampyMessageListener {

    private Map<HostPort, AtomicInteger> acks = new ConcurrentHashMap<HostPort, AtomicInteger>();

    private boolean connect = false;
    private boolean disconnect = false;

    private long start;
    private long end;

    /*
     * (non-Javadoc)
     *
     * @see
     * asia.stampy.common.mina.StampyMinaMessageListener#messageReceived(asia.
     * stampy.common.message.StampyMessage,
     * org.apache.mina.core.session.IoSession, asia.stampy.common.HostPort)
     */
    @Override
    public void messageReceived(StampyMessage<?> message, HostPort hostPort) throws Exception {
        switch (message.getMessageType()) {
            case ACK:
                getAckCounter(hostPort).getAndIncrement();
                break;
            case CONNECT:
                connect = true;
                start = System.nanoTime();
                break;
            case DISCONNECT:
                disconnect = true;
                end = System.nanoTime();
                stats(getAckCounter(hostPort));
                acks.remove(hostPort);
                break;
            default:
                System.out.println("Unexpected message " + message.getMessageType());
                break;

        }
    }

    private AtomicInteger getAckCounter(HostPort hostPort) {
        AtomicInteger ai = acks.get(hostPort);
        if(ai == null) {
            ai = new AtomicInteger();
            acks.put(hostPort, ai);
        }

        return ai;
    }

    private void stats(AtomicInteger ai) {
        System.out.println("# of acks: " + ai.get());
        System.out.println("Connect message? " + connect);
        System.out.println("Disconnect message? " + disconnect);
        long diff = end - start;
        System.out.println("Nano time elapsed: " + diff);
        BigDecimal bd = new BigDecimal(diff);
        int divisor = ai.get() * 2 * 1000;
        bd = bd.divide(new BigDecimal(divisor), 7, BigDecimal.ROUND_HALF_UP);
        System.out.println("Micro seconds per message: " + bd.doubleValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * asia.stampy.common.mina.StampyMinaMessageListener#isForMessage(asia.stampy
     * .common.message.StampyMessage)
     */
    @Override
    public boolean isForMessage(StampyMessage<?> message) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see asia.stampy.common.mina.StampyMinaMessageListener#getMessageTypes()
     */
    @Override
    public StompMessageType[] getMessageTypes() {
        return StompMessageType.values();
    }

}