/* Copyright (C) 2015~ Hyundai Heavy Industries. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Hyundai Heavy Industries
 * You shall not disclose such Confidential Information and shall use it only 
 * in accordance with the terms of the license agreement
 * you entered into with Hyundai Heavy Industries.
 *
 * Revision History
 * Author			Date				Description
 * ---------------	----------------	------------
 * Sang-cheon Park	2015. 10. 8.		First Draft.
 */
package com.hhi.bigdata.platform.push.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

/**
 * <pre>
 * Register utility class for 3rd Party's certificate
 * </pre>
 * @author Sang-cheon Park
 * @version 1.0
 */
public class RegisterUtil {
	
	public static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * <pre>
	 * 3rd party에서 app 등록을 위한 요청 API 
	 * </pre>
	 * @param webUrl
	 * @param appName
	 * @param keyFile
	 * @param dplFile
	 * @return
	 * @throws Exception
	 */
	public static APIInfo registCertificate(String webUrl, String appName, URI keyFile, URI dplFile) throws Exception {
		return registCertificate(webUrl, appName, getPrivateKey(keyFile), dplFile);
	}
	//end of registCertificate()

	/**
	 * <pre>
	 * 3rd party에서 app 등록을 위한 요청 API 
	 * </pre>
	 * @param webUrl
	 * @param appName
	 * @param keyFile
	 * @param dplFile
	 * @return
	 * @throws Exception
	 */
	public static APIInfo registCertificate(String webUrl, String appName, KeyStore keystore, String password, URI dplFile) throws Exception {
		return registCertificate(webUrl, appName, getPrivateKey(keystore, password), dplFile);
	}
	//end of registCertificate()

	/**
	 * <pre>
	 * 3rd party에서 app 등록을 위한 요청 API 
	 * </pre>
	 * @param webUrl
	 * @param appName
	 * @param keyFile
	 * @param dplFile
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static APIInfo registCertificate(String webUrl, String appName, PrivateKey key, URI dplFile) throws Exception {
		String result = null;

		/*
		 * url :   api url + "/{appName}"
		 * param : appName - 3rd party app name
		 *         appNameEnc - 3rd party app name을 HHI에서 발급받은 private key로 인코딩한 문자열 (byte[]를 base-64로 인코딩)
		 *         dpl - HHI에서 발급받은 dpl file
		 */
		Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
		WebTarget webTarget = client.target(webUrl + "/" + appName);

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("dpl", new File(dplFile), MediaType.APPLICATION_OCTET_STREAM_TYPE);

		multiPart.bodyPart(new FormDataBodyPart("appNameEnc", getAppNameEnc(key, appName)));
		multiPart.bodyPart(fileDataBodyPart);

		Response response = webTarget.request().post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA));
		result = response.readEntity(String.class);
		
		System.err.println("result : " + result);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map = mapper.readValue(result, new TypeReference<HashMap<String, Object>>(){});
		
		if (map.get("message") != null) {
			throw new Exception((String) map.get("message"));
		}
		
		APIInfo apiInfo = null;
		Map<String, String> apiInfoMap = (Map<String, String>)map.get("apiInfo");
		if (apiInfoMap != null && !apiInfoMap.isEmpty()) {
			apiInfo = new APIInfo();
			
			apiInfo.setUsername(apiInfoMap.get("username"));
			apiInfo.setPassword(apiInfoMap.get("password"));
			apiInfo.setConsumerKey(apiInfoMap.get("consumerKey"));
			apiInfo.setConsumerSecret(apiInfoMap.get("consumerSecret"));
		}
		
		return apiInfo;
	}
	//end of registCertificate()
	
	/**
	 * <pre>
	 * private key 파일로 app name을 인코딩한 문자열 return
	 * </pre>
	 * @return
	 * @throws Exception
	 */
	private static String getAppNameEnc(PrivateKey privKey, String appName) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, privKey);
		 
		return Base64.encodeBase64String(cipher.doFinal(appName.getBytes()));
	}
	//end of getAppNameEnc()
	
	/**
	 * <pre>
	 * create a SSLSocketFactory instance with given parameters
	 * </pre>
	 * @param keystore
	 * @param password
	 * @return
	 * @throws IOException
	 */
	private static PrivateKey getPrivateKey(URI keyFile) throws Exception {
		InputStream privKeyIs = new FileInputStream(new File(keyFile));
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(IOUtils.toByteArray(privKeyIs));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		 
		return keyFactory.generatePrivate(keySpec);
	}
	//end of getSocketFactory()
	
	/**
	 * <pre>
	 * create a SSLSocketFactory instance with given parameters
	 * </pre>
	 * @param keystore
	 * @param password
	 * @return
	 * @throws IOException
	 */
	private static PrivateKey getPrivateKey(KeyStore keystore, String password) throws Exception {
        Key key = null;
        
        // List the aliases
        Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = (String) aliases.nextElement();
            
            if (keystore.isKeyEntry(alias)) {
            	key = keystore.getKey(alias, password.toCharArray());
            }
        }
        
        return (PrivateKey) key;
	}
	//end of getSocketFactory()
}
//end of RegisterUtil.java