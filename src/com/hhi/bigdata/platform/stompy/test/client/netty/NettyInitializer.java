package com.hhi.bigdata.platform.stompy.test.client.netty;

/**
 * Created by SongChanHo on 2016. 3. 21..
 */
import asia.stampy.client.netty.ClientNettyChannelHandler;
import asia.stampy.common.StampyLibrary;
import asia.stampy.common.gateway.AbstractStampyMessageGateway;
import asia.stampy.common.heartbeat.HeartbeatContainer;
import asia.stampy.common.heartbeat.StampyHeartbeatContainer;
import asia.stampy.examples.client.netty.NettyAutoTerminatingClientGateway;
import asia.stampy.examples.common.IDontNeedSecurity;

/**
 * This class programmatically initializes the Stampy classes required for this
 * example. It is expected that a DI framework such as <a
 * href="http://www.springsource.org/">Spring</a> or <a
 * href="http://code.google.com/p/google-guice/">Guice</a> will be used to
 * perform this task.
 */
@StampyLibrary(libraryName = "stampy-examples")
public class NettyInitializer {

    /**
     * Initialize.
     *
     * @return the client mina message gateway
     */
    public static AbstractStampyMessageGateway initialize() {
        StampyHeartbeatContainer heartbeatContainer = new HeartbeatContainer();

        NettyAutoTerminatingClientGateway gateway = new NettyAutoTerminatingClientGateway();
        gateway.setAutoShutdown(true);
        gateway.setPort(4321);
        gateway.setHost("localhost");

        ClientNettyChannelHandler handler = new ClientNettyChannelHandler();
        handler.setHeartbeatContainer(heartbeatContainer);
        handler.setGateway(gateway);

        gateway.addMessageListener(new IDontNeedSecurity());

        gateway.setHandler(handler);

        return gateway;

    }
}