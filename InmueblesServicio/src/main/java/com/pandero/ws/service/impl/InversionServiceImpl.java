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

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class InversionServiceImpl implements InversionService {

	private static final Logger LOG = LoggerFactory
			.getLogger(InversionServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${url.service.table.pedidoInversion}")
	private String tablePedidoInversionURL;
	@Value("${url.service.view.tablaDetalleInversion}")
	private String viewTablaDetalleInversionURL;

	public Inversion obtenerInversionCaspio(String inversionId) throws Exception {
		Inversion inversion = null;
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId + "\"}";		
		String obtenerInversionesxPedidoURL = viewTablaDetalleInversionURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,obtenerInversionesxPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
	        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapInversiones = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapInversiones!=null && mapInversiones.size()>0){
	        			for(Object bean : mapInversiones){
	        				String beanString = JsonUtil.toJson(bean);
	        				inversion =  JsonUtil.fromJson(beanString, Inversion.class);			
	        			}
	        		}        		
	        	}
	        }
	    }

		return inversion;
	}

	@Override
	public String actualizarEstadoInversionCaspio(String inversionId,
			String estadoInversion) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Estado", estadoInversion);		
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}

}
