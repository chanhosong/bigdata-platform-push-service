package com.hhi.bigdata.platform.push.server.mina;

/**
 * Created by SongChanHo on 2016. 3. 18..
 */
import asia.stampy.common.StampyLibrary;
import asia.stampy.common.gateway.AbstractStampyMessageGateway;
import asia.stampy.common.heartbeat.HeartbeatContainer;
import asia.stampy.common.heartbeat.StampyHeartbeatContainer;
import asia.stampy.examples.common.IDontNeedSecurity;
import asia.stampy.server.mina.RawServerMinaHandler;
import asia.stampy.server.mina.ServerMinaMessageGateway;
import asia.stampy.server.mina.connect.MinaConnectResponseListener;
import asia.stampy.server.mina.receipt.MinaReceiptListener;

/**
 * This class programmatically initializes the Stampy classes required for this
 * example. It is expected that a DI framework such as <a
 * href="http://www.springsource.org/">Spring</a> or <a
 * href="http://code.google.com/p/google-guice/">Guice</a> will be used to
 * perform this task.
 */
@StampyLibrary(libraryName = "stampy-examples")
public class MinaInitializer {

    /**
     * Initialize.
     *
     * @return the server mina message gateway
     */
    public static AbstractStampyMessageGateway initialize() {
        StampyHeartbeatContainer heartbeatContainer = new HeartbeatContainer();

        ServerMinaMessageGateway gateway = new ServerMinaMessageGateway();
        gateway.setPort(4321);

        RawServerMinaHandler handler = new RawServerMinaHandler();
        handler.setHeartbeatContainer(heartbeatContainer);
        handler.setGateway(gateway);

        gateway.setHandler(handler);

        gateway.addMessageListener(new IDontNeedSecurity());

        MinaConnectResponseListener connectResponse = new MinaConnectResponseListener();
        connectResponse.setGateway(gateway);
        gateway.addMessageListener(connectResponse);

        MinaReceiptListener receipt = new MinaReceiptListener();
        receipt.setGateway(gateway);
        gateway.addMessageListener(receipt);

        return gateway;
    }
}