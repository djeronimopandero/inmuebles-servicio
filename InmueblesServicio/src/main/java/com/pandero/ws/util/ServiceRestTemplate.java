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
	
	private static String caspioKey = "CFMbWgWKJowsdY84rfKAJlvU7If97SeQx74a7mzyhkOSPmF9uoQB1CfwqnfF4387PPtxSW3lx3hLscVcfyuIZRjKaqkGMwQP71X_CfhL-vA_NcwHRfQfXV2JSQ7_tdLI1lhWE1-qzQSNbL54XT6d1XxDmFfJ2e8kFB8Owe6ATj40Bu8ByDjGMABqklTl1HqSojzmiClDiyQekzJLMh5v2S4_P5D1MZ4MSRH4lc3XKc5I6SHZUl8HzBkj6HBJULRcYRGmxcpyJV_qNKt_yOosoYJtKG7-2uwQPpIXuwn79LWol_GuQ0T_Zdd7cj9meqvZog13jHolSvN_2aKnvGlRPgy3L57CCFDdjfABx2lRbq4dM8d4";
	
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
		
	public static <T, E> T executeMethod(RestTemplate restTemplate, String url, Class<T> responseType, HttpMethod method, 
			E value, E parameters){
		LOG.info("SERVICE URL ==> "+url);
		String request = JsonUtil.toJson(value);
		LOG.info("JSON Params:: "+parameters);
		LOG.info("JSON Resquest:: "+request);
		
		HttpEntity<E> requestEntity = null;
		if(value==null){
			Map<String, String> valueEmpty = new HashMap<String, String>();
			requestEntity = new HttpEntity(valueEmpty, getHeaders());
		}else{
			requestEntity = new HttpEntity(value, getHeaders());
		}
		
		MappingJackson2HttpMessageConverter jsonConverter=new MappingJackson2HttpMessageConverter();
		restTemplate.getMessageConverters().add(jsonConverter);
		
		ResponseEntity<T> response = null;	
		if(parameters==null){
			response = restTemplate.exchange(url, method, requestEntity, responseType, new Object[0]);
		}else{
			response = restTemplate.exchange(url, method, requestEntity, responseType, parameters);
		}
		LOG.info("JSON Response:: " + response == null ? "JSON Response:: null" : JsonUtil.toJson(response.getBody()));
		
	    return response.getBody();
	}
		
	public static <T, E> T postForObject(RestTemplate restTemplate, String url, Class<T> responseType, E value, E parameters)
	    throws JsonParseException, JsonMappingException, IOException {
	    return executeMethod(restTemplate, url, responseType, HttpMethod.POST, value, parameters);
	}
	
	public static <T, E> T getForObject(RestTemplate restTemplate, String url, Class<T> responseType, E value, E parameters)
		    throws JsonParseException, JsonMappingException, IOException {
		    return executeMethod(restTemplate, url, responseType, HttpMethod.GET, value, parameters);
	}
	
	public static <T, E> T putForObject(RestTemplate restTemplate, String url, Class<T> responseType, E value, E parameters)
		    throws JsonParseException, JsonMappingException, IOException {
		    return executeMethod(restTemplate, url, responseType, HttpMethod.PUT, value, parameters);
	}
	
	public static <T, E> T deleteForObject(RestTemplate restTemplate, String url, Class<T> responseType, E value, E parameters)
		    throws JsonParseException, JsonMappingException, IOException {
		    return executeMethod(restTemplate, url, responseType, HttpMethod.DELETE, value, parameters);
	}
		
}
