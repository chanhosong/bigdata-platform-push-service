package com.hhi.bigdata.platform.stompy.test.client;

/**
 * Created by SongChanHo on 2016. 3. 21..
 */

import asia.stampy.client.message.connect.ConnectMessage;
import asia.stampy.common.StampyLibrary;
import asia.stampy.common.gateway.AbstractStampyMessageGateway;
import asia.stampy.examples.loadtest.server.TestServer;
import com.hhi.bigdata.platform.stompy.test.client.listener.TestClientMessageListener;
import com.hhi.bigdata.platform.stompy.test.client.netty.NettyInitializer;

/**
 * Sends many messages to a {@link TestServer}, prints stats on the operation
 * and terminates. Run the {@link TestServer} prior to running this client.
 */
@StampyLibrary(libraryName = "stampy-examples")
public class TestClient {
    private AbstractStampyMessageGateway gateway;
    private TestClientMessageListener listener;

    /**
     * Inits the.
     *
     * @throws Exception
     *           the exception
     */
    public void init() throws Exception {
        setGateway(NettyInitializer.initialize());
        listener = new TestClientMessageListener();
        listener.setGateway(gateway);
        gateway.addMessageListener(listener, 1);

        gateway.connect();
        gateway.broadcastMessage(new ConnectMessage("localhost"));
    }

    /**
     * Gets the gateway.
     *
     * @return the gateway
     */
    public AbstractStampyMessageGateway getGateway() {
        return gateway;
    }

    /**
     * Sets the gateway.
     *
     * @param gateway
     *          the new gateway
     */
    public void setGateway(AbstractStampyMessageGateway gateway) {
        this.gateway = gateway;
    }

    /**
     * The main method.
     *
     * @param args
     *          the arguments
     */
    public static void main(String[] args) {
        TestClient client = new TestClient();
        try {
            client.init();
            client.listener.disconnect();
            client.listener.stats();
            client.getGateway().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}