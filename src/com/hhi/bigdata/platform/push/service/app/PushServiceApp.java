package com.hhi.bigdata.platform.push.service.app;

public class PushServiceApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new PushClient().run();
			new PushServer().run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
