package com.hhi.bigdata.platform.stompy.test.client.mina;

/**
 * Created by SongChanHo on 2016. 3. 21..
 */
import asia.stampy.client.mina.RawClientMinaHandler;
import asia.stampy.common.StampyLibrary;
import asia.stampy.common.gateway.AbstractStampyMessageGateway;
import asia.stampy.common.heartbeat.HeartbeatContainer;
import asia.stampy.common.heartbeat.StampyHeartbeatContainer;
import asia.stampy.examples.client.mina.MinaAutoTerminatingClientGateway;
import asia.stampy.examples.common.IDontNeedSecurity;

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
     * @return the client mina message gateway
     */
    public static AbstractStampyMessageGateway initialize() {
        StampyHeartbeatContainer heartbeatContainer = new HeartbeatContainer();

        MinaAutoTerminatingClientGateway gateway = new MinaAutoTerminatingClientGateway();
        gateway.setAutoShutdown(true);
        gateway.setPort(1234);
        gateway.setHost("localhost");

        RawClientMinaHandler handler = new RawClientMinaHandler();
        handler.setHeartbeatContainer(heartbeatContainer);
        handler.setGateway(gateway);

        gateway.addMessageListener(new IDontNeedSecurity());

        gateway.setHandler(handler);

        return gateway;

    }
}