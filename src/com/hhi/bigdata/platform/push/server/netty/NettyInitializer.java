package com.hhi.bigdata.platform.push.server.netty;

/**
 * Created by SongChanHo on 2016. 3. 18..
 */

import asia.stampy.common.StampyLibrary;

import asia.stampy.common.gateway.AbstractStampyMessageGateway;
import asia.stampy.common.heartbeat.HeartbeatContainer;
import asia.stampy.common.heartbeat.StampyHeartbeatContainer;
import asia.stampy.examples.common.IDontNeedSecurity;
import asia.stampy.server.netty.ServerNettyChannelHandler;
import asia.stampy.server.netty.ServerNettyMessageGateway;
import asia.stampy.server.netty.connect.NettyConnectResponseListener;
import asia.stampy.server.netty.receipt.NettyReceiptListener;

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
     * @return the server mina message gateway
     */
    public static AbstractStampyMessageGateway initialize() {
        StampyHeartbeatContainer heartbeatContainer = new HeartbeatContainer();

        ServerNettyMessageGateway gateway = new ServerNettyMessageGateway();
        gateway.setPort(4321);

        ServerNettyChannelHandler handler = new ServerNettyChannelHandler();
        handler.setHeartbeatContainer(heartbeatContainer);
        handler.setGateway(gateway);

        gateway.setHandler(handler);

        gateway.addMessageListener(new IDontNeedSecurity());

        NettyConnectResponseListener connectResponse = new NettyConnectResponseListener();
        connectResponse.setGateway(gateway);
        gateway.addMessageListener(connectResponse);

        NettyReceiptListener receipt = new NettyReceiptListener();
        receipt.setGateway(gateway);
        gateway.addMessageListener(receipt);

        return gateway;
    }
}
