package com.hhi.bigdata.platform.push.client;

import com.hhi.vaas.platform.middleware.client.rest.APIGatewayClient;
import com.hhi.vaas.platform.middleware.client.websocket.EventHandler;
import com.hhi.vaas.platform.middleware.client.websocket.WebSocketClient;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.type.TypeReference;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyStore;

public class PushClient {
	private APIGatewayClient apiGatewayClient;
	private WebSocketClient websocketClient;
	
	/** API Endpoints */
	private String tokenApiUrl = "https://10.100.16.82:8343/token";
	private String cepApiUrl = "http://10.100.16.82:8380/cep/1.0";
	//private String registerApiUrl = "http://10.100.16.51:8380/register/v1.0";
	private String registerApiUrl = "http://10.100.16.82:8080/auth/api/registCertificate_file";
	private String pushApiUrl = "ws://10.100.16.82:9000/websocket";

	/** App Info */
	private String confFileName = "apiinfo.json";
	private String registerAppName = "hhivaasdev3";
	private String dplFileName = "hhivaasdev3_dpl.txt";
	private String jksFileName = "hhivaasdev3.jks";
	private String keyFileName = "hhivaasdev3_privateKey.key";
	private String passPhrase = "changeit";
	
	/** Push rule name which is created beforehand */
	private String sensorRuleName = "springxd";
	
	/** Push rule name which is created beforehand */
	private String alarmRuleName = "springxdalarm001";
	
	/** 3rd Party username and password for authentication which is given at installation time */
	private static String username; 
	private static String password; 
	
	/** 3rd Party consumerKey and consumerSecret for authentication which is given at installation time */
	private static String consumerKey;
	private static String consumerSecret;
	
	/**
	 * Create a instance of APIGatewayClient to invoke REST APIs via API Gateway
	 */
	public void initClient() {
		apiGatewayClient = new APIGatewayClient(tokenApiUrl, username, password, consumerKey, consumerSecret);
	}
	
	/**
	 * Create a CEP vdm sensor event rule for 3rd party
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean createSensorEventRule() throws Exception {
		if (apiGatewayClient == null) {
			initClient();
		}
		
		String apiUrl = cepApiUrl + "/createSensorRule";
		
		StringBuilder sb = new StringBuilder();
		sb.append("Mechanical/*").append(",");
		sb.append("Hull/*").append(",");
		sb.append("Navigational/*").append(",");
//		sb.append("Mechanical/Machinery/DieselEngine/MainEngine/*").append(",");
//		sb.append("Mechanical/Machinery/DieselEngine/GECommonDevice/*").append(",");
//		sb.append("Mechanical/Machinery/DieselEngine/GeneratorEngine1/*").append(",");
//		sb.append("Mechanical/Machinery/SteeringGear/*").append(",");
//		sb.append("Mechanical/Machinery/Shaft/*").append(",");
//		sb.append("Mechanical/Pressurised/Boiler/AuxBoiler1/*").append(",");
//		sb.append("Mechanical/Pressurised/Boiler/AuxBoiler2/*").append(",");
//		sb.append("Mechanical/Pressurised/SteamGenerator/*").append(",");
//		sb.append("Hull/Compartments/TankOverview/*").append(",");
//		sb.append("Hull/Compartments/CargoTanks/*").append(",");
//		sb.append("Hull/Bottom/Bilge/*").append(",");
//		sb.append("Hull/Stability/LoadCondition/*").append(",");
//		sb.append("Navigational/Measurement/PositioningDevice/GPSSensor*").append(",");
//		sb.append("Navigational/Measurement/HeadingDevice/GyroSensor*").append(",");
//		sb.append("Navigational/Measurement/SDMEDevice/*").append(",");
//		sb.append("Navigational/Measurement/AuxiliaryDevice/*").append(",");
//		sb.append("Navigational/Control/PropulsionDevice/*").append(",");
//		sb.append("Navigational/Control/SteeringDevice/*").append(",");
//		sb.append("Environmental/Measurement/ClimateSensor/AirSensor*").append(",");
		sb.append("Cargo/CargoManagement/CargoTanks/CargoTank*");
		
        String fullPaths = sb.toString();
		
		Integer timeWindow = 10;

		StringBuilder contents = new StringBuilder();
		contents.append("ruleName=").append(URLEncoder.encode(sensorRuleName, "UTF-8")).append("&");
		contents.append("fullPaths=").append(URLEncoder.encode(fullPaths, "UTF-8")).append("&");
		contents.append("timeWindow=").append(timeWindow);
		
		Response response = apiGatewayClient.postRequest(apiUrl, contents.toString().getBytes());
		
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			System.err.println("createSensorEventRule() is done successfully.");
			return true;
		} else {
			System.err.println("createSensorEventRule() is failed. Reason : " + response.readEntity(String.class));
			return false;
		}
	}
	
	/**
	 * Delete a CEP vdm sensor event rule for 3rd party
	 * 
	 * @param ruleName
	 * @throws Exception
	 */
	public void deleteSensorEventRules(String ruleName) throws Exception {
		if (apiGatewayClient == null) {
			initClient();
		}
		
		String apiUrl = cepApiUrl + "/deleteSensorRule?ruleName=" + ruleName;
		
		Response response = apiGatewayClient.deleteRequest(apiUrl);
		
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			System.err.println("deleteSensorEventRules() is done successfully.");
		} else {
			System.err.println("deleteSensorEventRules() is failed.");
		}
	}
	
	/**
	 * Get event data using WebSocket
	 * 
	 * @throws URISyntaxException
	 * @throws InterruptedException 
	 */

	public void getDataUsingWebSocket(String endpointURI, String ruleName) throws URISyntaxException, InterruptedException {
		final boolean ackMode = true;
		
		EventHandler handler = new EventHandler() {
			@Override
			public void handleMessageEvent(String msg) {
				/** describe something to do */
				System.err.println("Received Message via WebSocket : " + msg);

				/** in case of ackMode is enable, invoke sendAck or sendNack to receive next message */
				sendAck();
				//sendNack();
			}

			@Override
			public void handleCloseEvent(int statusCode, String reason) { 
				/** describe something to do */ 
				System.err.println("Status Code : " + statusCode + ", Reason : " + reason);
			}

			@Override
			public void handleErrorEvent(Throwable t) { /** describe something to do */ }
		};
		
		websocketClient = new WebSocketClient(new URI(endpointURI), username, password, ruleName, ackMode, handler);
		websocketClient.start();
		
		/**
		 * Thread will not be stopped. If you want to stop the websocketClient thread, invoke close() manually.
		 * {@link com.hhi.vaas.platform.sample.SampleAPIGatewayClient#closeWebSocket()}.
		 */
	}
	
	/**
	 * Get an event data using REST API
	 * 
	 * @throws Exception
	 */
	public void getDataUsingRest(String url) throws Exception {
		if (apiGatewayClient == null) {
			initClient();
		}
		
		Response response = apiGatewayClient.getRequest(url);
		
		/** in case of HTTP Resopnse is 200 OK */
		if (response.getStatus() == Response.Status.OK.getStatusCode()) {
			String msg = response.readEntity(String.class);
			System.err.println("Received Message via REST API : " + msg);
		} else {
			System.err.println("HttpResonse : " + response);
		}
	}

	/**
	 * Close a websocket session
	 */
	public void closeWebSocket() {
		if (websocketClient != null) {
			websocketClient.close();
		}
	}
	
	/**
	 * <pre>
	 * Register 3rd party S/W
	 * </pre>
	 * @throws Exception
	 */
	public void registerSoftware() throws Exception {
		File file = new File(PushClient.class.getResource("/").getFile(), confFileName);
		
		APIInfo apiInfo = null;
		if (!file.exists()) {
			// Not registered in VDIP
			
			// Loading Signed DPL
			URI dplFile = PushClient.class.getResource("/" + dplFileName).toURI();
			
			boolean usingKeyStore = true;
			
			if (usingKeyStore) {
				// Loading keyStore
				KeyStore keyStore  = KeyStore.getInstance(KeyStore.getDefaultType());
				keyStore.load(PushClient.class.getResourceAsStream("/" + jksFileName), passPhrase.toCharArray());
				
				// Register 3rd Party S/W Certificate and get API information
				apiInfo = RegisterUtil.registCertificate(registerApiUrl, registerAppName, keyStore, passPhrase, dplFile);
			} else {
				// Loading privateKey
				URI keyFile = PushClient.class.getResource("/" + keyFileName).toURI();
				
				// Register 3rd Party S/W Certificate and get API information
				apiInfo = RegisterUtil.registCertificate(registerApiUrl, registerAppName, keyFile, dplFile);
			}
			
			if (apiInfo != null) {
				System.out.println("[" + registerAppName + "] has been registered successfully.");
				System.out.println(apiInfo);
				
				username = apiInfo.getUsername();
				password = apiInfo.getPassword();
				consumerKey = apiInfo.getConsumerKey();
				consumerSecret = apiInfo.getConsumerSecret();
				
				IOUtils.write(RegisterUtil.mapper.writeValueAsString(apiInfo), new FileOutputStream(file));
			} else {
				System.out.println("[" + registerAppName + "] already registered but access info doesn't exist.");
			}
		} else {
			// Already registered in VDIP
			System.out.println("apiinfo.json file does exist.");
			
			String info = IOUtils.toString(file.toURI());
			apiInfo = RegisterUtil.mapper.readValue(info, new TypeReference<APIInfo>(){});
			
			username = apiInfo.getUsername();
			password = apiInfo.getPassword();
			consumerKey = apiInfo.getConsumerKey();
			consumerSecret = apiInfo.getConsumerSecret();
		}
	}
	
	/**
	 * <pre>
	 * Sensor Data Test
	 * </pre>
	 * @throws Exception
	 */
	public void sensorTest(int duration) throws Exception {
		// Register 3rd Party S/W Certificate and get API information
		registerSoftware();
		
		// 1. Create a CEP event rule for 3rd party
		boolean result = createSensorEventRule();
		
		if (result) {
			System.out.println("\n:+:+:+:+ Waiting for seconds to refresh CEP event modules :+:+:+:+");
			int cnt = 0;
			while (cnt++ < 15) {
				Thread.sleep(1000);
				System.out.print(".");
			}
			System.out.println("");
			
			// 2. Get an event data using REST API
//			getDataUsingRest(cepApiUrl + "/getSensorData?ruleName=" + sensorRuleName);
			
			// 3. Get event data using WebScoket
			getDataUsingWebSocket(pushApiUrl + "/sensor", sensorRuleName);//"ws://10.100.16.82:9000/websocket"/sensor/springxd;
			
			// 4. Close a WebSocket Session
			Thread.sleep(duration * 60 * 1000);
			closeWebSocket();
			
			deleteSensorEventRules(sensorRuleName);
		}
	}
	
	public void run() throws Exception{
		PushClient sample = new PushClient();
		
		// sensor data test
		sample.sensorTest(2);
	}
}
