package com.pandero.ws.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pandero.ws.bean.Desembolso;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.service.DesembolsoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

@Service
public class DesembolsoServiceImpl implements DesembolsoService {

	private static final Logger LOG = LoggerFactory
			.getLogger(InversionServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${url.service.table.desembolso}")
	private String tableDesembolsoURL;

	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
	
	@Override
	public String crearDesembolsoInversion(String inversionId, String nroArmada, String nroDesembolso) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("InversionId", inversionId);
		request.put("NroArmada", nroArmada);
		request.put("NroDesembolso", nroDesembolso);
		request.put("FechaDesembolso",  Util.getFechaActualYYYYMMDD());		
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tableDesembolsoURL,Object.class,request,null);	
		return null;
	}

	@Override
	public Desembolso obtenerDesembolsoPorInversionArmada(String inversionId, String nroArmada)
			throws Exception {
		Desembolso desembolso = null;
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId + " and NroArmada="+nroArmada+"\"}";		
		String obtenerInversionesxPedidoURL = tableDesembolsoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerInversionesxPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
	        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapResultado = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapResultado!=null && mapResultado.size()>0){
	        			for(Object bean : mapResultado){
	        				String beanString = JsonUtil.toJson(bean);
	        				desembolso =  JsonUtil.fromJson(beanString, Desembolso.class);			
	        			}
	        		}        		
	        	}
	        }
	    }

		return desembolso;
	}


}
