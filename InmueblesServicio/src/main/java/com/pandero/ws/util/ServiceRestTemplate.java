package com.pandero.ws.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ServiceRestTemplate {
	
	private static String caspioKey = "8fWbewLcTQwM0C_Tne114dHSDpQD2loD9pvfPDEDtA3fxU_qOvvSuhk6Jrkhp9CUG7q5mnwdNvN6T_ddIfLL50Ji7PJbr_XIv3i2cXFtYaqb_MH24iUMa9_Jk3EecwN3gM4UFvScbRGUQgdNjNvhtwgArQ7mvkx4mVQ31CQPm6-sLWPZTMrI4Cv5i0OQzO6u4BK0hBYHrkA5tlxiyfF4NlLch2Gy1VjF9EEBHAX7DW1OmKiqqYo6Wm6YC7rYsDhTId_gi-i-1leHMLWXRR7lmBRRH-G3_JMUVAA0SvHpDIhsfdp-xp12xfFOswXbCQdA3fu68_tl2Hz_ih7BUehcKUFE0GUcS-y5g6sZL91x_WRBam3v";
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceRestTemplate.class);
	
	public static HttpHeaders getHeaders(){
		   
		HttpHeaders requestHeaders = new HttpHeaders();
	    requestHeaders.clear();
    	Map<String, String> headers = new HashMap<String, String>();
    	headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");		
	    requestHeaders.setAll(headers);
	   
       byte[] encodedAuthorization = Base64.encodeBase64(caspioKey.getBytes());
       requestHeaders.add("Authorization", "Bearer " + caspioKey);
       System.out.println("caspio key: "+caspioKey);
	    

	    return requestHeaders;
	}
		
	public static <T, E> T executeMethod(RestTemplate restTemplate, String url, Class<T> responseType, HttpMethod method, E value){
		LOG.info("SERVICE URL ==> "+url);
		String request = JsonUtil.toJson(value);		
		LOG.info("JSON Resquest:: "+request);	
		
		HttpEntity<E> requestEntity = new HttpEntity(value, getHeaders());
		MappingJackson2HttpMessageConverter jsonConverter=new MappingJackson2HttpMessageConverter();
		restTemplate.getMessageConverters().add(jsonConverter);
		
		ResponseEntity<T> response = null;
		if(value instanceof String){
			response = restTemplate.exchange(url, method, requestEntity, responseType, value);
		}else{
			response = restTemplate.exchange(url, method, requestEntity, responseType, new Object[0]);
		}
		LOG.info("JSON Response:: " + response == null ? "null" : JsonUtil.toJson(response.getBody()));
		
	    return response.getBody();
	}
		
	public static <T, E> T postForObject(RestTemplate restTemplate, String url, Class<T> responseType, E value)
	    throws JsonParseException, JsonMappingException, IOException {
	    return executeMethod(restTemplate, url, responseType, HttpMethod.POST, value);
	}
	
	public static <T, E> T getForObject(RestTemplate restTemplate, String url, Class<T> responseType, E value)
		    throws JsonParseException, JsonMappingException, IOException {
		    return executeMethod(restTemplate, url, responseType, HttpMethod.GET, value);
	}
		
}
