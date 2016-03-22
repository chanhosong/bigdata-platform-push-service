package com.hhi.bigdata.platform.push.service.app;

import com.hhi.bigdata.platform.push.server.PushServer;

public class PushServiceApp {

	public static void main(String[] args) {
	// TODO Auto-generated method stub
		try {
			new PushServer().init();
//			new PushClient().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
