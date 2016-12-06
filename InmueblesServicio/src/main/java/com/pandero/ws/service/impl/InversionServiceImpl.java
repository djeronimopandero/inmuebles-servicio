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
import com.pandero.ws.bean.InversionRequisitoCaspio;
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
	
	@Value("${url.service.table.inversionRequisito}")
	private String tableInversionRequisitoURL;
	
	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}

	public Inversion obtenerInversionCaspio(String inversionId) throws Exception {
		Inversion inversion = null;
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId + "\"}";		
		String obtenerInversionesxPedidoURL = viewTablaDetalleInversionURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerInversionesxPedidoURL,Object.class,null,serviceWhere);
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
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}

	public String actualizarSituacionConfirmadoInversionCaspio(String inversionId, String situacionConfirmado) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Confirmado", situacionConfirmado);		
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}

	@Override
	public List<InversionRequisitoCaspio> listInversionRequisitoPorIdInversion(String inversionId) throws Exception {
		LOG.info("###listInversionRequisitoPorIdInversion inversionId:"+inversionId);
		List<InversionRequisitoCaspio> listInversionReq=null;
		
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId + "\"}";	
		String obtenerContratosxPedidoURL = tableInversionRequisitoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerContratosxPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapInversionReq = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapInversionReq!=null && mapInversionReq.size()>0){
	        			listInversionReq = new ArrayList<InversionRequisitoCaspio>();
	        			for(Object bean : mapInversionReq){
	        				String beanString = JsonUtil.toJson(bean);
	        				InversionRequisitoCaspio inversionReq =  JsonUtil.fromJson(beanString, InversionRequisitoCaspio.class);
	        				listInversionReq.add(inversionReq);
	        			}
	        			
	        		}        		
	        	}
	        }
        }
        
		return listInversionReq;
	}
	
	@Override
	public String actualizarEstadoInversionRequisitoCaspio(String inversionId,String estadoInversionReq) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("EstadoRequisito", estadoInversionReq);		
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarInversionRequisitoURL = tableInversionRequisitoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarInversionRequisitoURL,Object.class,request,serviceWhere);	
		return null;
	}
	
}
