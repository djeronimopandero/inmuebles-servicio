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

import com.pandero.ws.bean.CaspioToken;

public class ServiceRestTemplate {
	
	private static String urlCaspioToken = "https://c3cqk716.caspio.com/oauth/token";
	private static String caspioClientId="ae419b3a60614e25f93150df39ac6c79cfd3d4d8c8b5cc5ad0";
	private static String caspioClientSecret="f0d283be73764ddab9ef845cf834aa01648ce438593f0d7f2d";
	private static String caspioRefreshToken="759c3354ec81429ba9e709e9f9cfdb5becb24e79f3694a6892005f98b859fcbd";
//	private static String caspioKey = "QM9UsQWmUIFHb6V2w5mjfvqWb_bRQphhbKdIIVy1sTT9K-_Y7m3cW2h70wczV5ljo2KKDHrHCd4b77AvmPUyCkbEM1dObtY1nuShph_Z6qtEPIbL0hBiD-l27CILIFcbJUv77faiFTFiBLIfz63ZQBVqtaLWO5N8oyqE7VGQwdFXEPZlU43tsOalM0DTjjj1xUpvEEysYgqExvnSXyhS3sKxkqcib-U8dzDz_rG9_LQgrgUTJ_7jXzdCdpBzPdXsKUC65Y_c6VUsmG-AK_Q55c67oI6Cfe6c0BaTMq7wkeJAj5vBxjNhPgRrWEx4AiQ6Zu0qahZCuEvkt8eAU_JShfexwlgVSg2S1ih9RvhDTNNnQpnh";
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceRestTemplate.class);
	
	public static HttpHeaders getTokenHeaders(){		   
		HttpHeaders requestHeaders = new HttpHeaders();
	    requestHeaders.clear();
    	Map<String, String> headers = new HashMap<String, String>();
    	headers.put("Content-Type", "application/x-www-form-urlencoded");
		headers.put("Accept", "*/*");		
	    requestHeaders.setAll(headers);
	    
	    String authorisation = caspioClientId + ":" + caspioClientSecret;
	    byte[] encodedAuthorisation = Base64.encodeBase64(authorisation.getBytes());
	    requestHeaders.add("Authorization", "Basic " + new String(encodedAuthorisation));
	    
	    return requestHeaders;
	}
	
	public static String obtenerTokenCaspio() throws Exception{
		String caspioKey = "";
		String request = "grant_type=refresh_token&refresh_token="+caspioRefreshToken;
		HttpEntity<String> requestEntity = new HttpEntity<String>(request,getTokenHeaders());
		RestTemplate newRestTemplate = new RestTemplate();
		
		Object response = newRestTemplate.exchange(urlCaspioToken, HttpMethod.POST, requestEntity, Object.class, new Object[0]);
		String jsonResult = JsonUtil.toJson(response);	
		Map<String, Object> responseMap = JsonUtil.jsonToMap(jsonResult);
		if(responseMap!=null){
	        Object jsonResponse = responseMap.get("body");
	        if(jsonResponse!=null){        		
        		CaspioToken caspioToken = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), CaspioToken.class);
        		caspioKey = caspioToken.getAccess_token();
	        }
		}
//		System.out.println("RESPONSE: "+jsonResult);
		
		return caspioKey;
	}
	
	public static HttpHeaders getHeaders(String caspioKey){		   
		HttpHeaders requestHeaders = new HttpHeaders();
	    requestHeaders.clear();
    	Map<String, String> headers = new HashMap<String, String>();
    	headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");		
		headers.put("Access-Control-Allow-Origin", "*");
	    requestHeaders.setAll(headers);
	   
        requestHeaders.add("Authorization", "Bearer " + caspioKey);
        System.out.println("caspio key: "+caspioKey);
	    
	    return requestHeaders;
	}
		
	public static <T, E> T executeMethod(RestTemplate restTemplate, String token, String url, Class<T> responseType, HttpMethod method, 
			E value, E parameters){
		LOG.info("SERVICE URL ==> "+url);
		String request = JsonUtil.toJson(value);
		LOG.info("JSON Params:: "+parameters);
		LOG.info("JSON Resquest:: "+request);
				
		HttpEntity<E> requestEntity = null;
		if(value==null){
			Map<String, String> valueEmpty = new HashMap<String, String>();
			requestEntity = new HttpEntity(valueEmpty, getHeaders(token));
		}else{
			requestEntity = new HttpEntity(value, getHeaders(token));
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
		
	public static <T, E> T postForObject(RestTemplate restTemplate, String token, String url, Class<T> responseType, E value, E parameters)
	    throws JsonParseException, JsonMappingException, IOException {
	    return executeMethod(restTemplate, token, url, responseType, HttpMethod.POST, value, parameters);
	}
	
	public static <T, E> T getForObject(RestTemplate restTemplate, String token, String url, Class<T> responseType, E value, E parameters)
		    throws JsonParseException, JsonMappingException, IOException {
		    return executeMethod(restTemplate, token, url, responseType, HttpMethod.GET, value, parameters);
	}
	
	public static <T, E> T putForObject(RestTemplate restTemplate, String token, String url, Class<T> responseType, E value, E parameters)
		    throws JsonParseException, JsonMappingException, IOException {
		    return executeMethod(restTemplate, token, url, responseType, HttpMethod.PUT, value, parameters);
	}
	
	public static <T, E> T deleteForObject(RestTemplate restTemplate, String token, String url, Class<T> responseType, E value, E parameters)
		    throws JsonParseException, JsonMappingException, IOException {
		    return executeMethod(restTemplate, token, url, responseType, HttpMethod.DELETE, value, parameters);
	}
		
}
