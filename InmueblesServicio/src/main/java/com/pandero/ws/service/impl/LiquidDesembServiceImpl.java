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
import com.pandero.ws.service.LiquidDesembService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

@Service
public class LiquidDesembServiceImpl implements LiquidDesembService {

	private static final Logger LOG = LoggerFactory
			.getLogger(InversionServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${url.service.table.liquidacionDesembolso}")
	private String tableLiquidacionDesembolsoURL;

	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
	
	@Override
	public String registrarLiquidacionInversion(String inversionId, String nroArmada, String nroLiquidacion) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("InversionId", inversionId);
		request.put("NroArmada", nroArmada);
		request.put("NroLiquidacion", nroLiquidacion);
		request.put("FechaLiquidacion",  Util.getFechaActualYYYYMMDD());	
		request.put("Estado", Constantes.Inversion.ESTADO_LIQUIDADO);
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tableLiquidacionDesembolsoURL,Object.class,request,null);	
		return null;
	}
	
	public String registrarDesembolsoInversion(String inversionId, String nroArmada, String nroDesembolso) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("NroDesembolso", nroDesembolso);
		request.put("FechaDesembolso",  Util.getFechaActualYYYYMMDD());	
		request.put("Estado", Constantes.Inversion.ESTADO_DESEMBOLSADO);		
		
		String serviceWhere = "{\"where\":\"InversionId="+inversionId+" and NroArmada="+nroArmada+"\"}";	
		String registrarDesembolsoURL = tableLiquidacionDesembolsoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,registrarDesembolsoURL,Object.class,request,serviceWhere);	
		return null;
	}

	@Override
	public Desembolso obtenerLiquidacionDesembolsoPorInversionArmada(String inversionId, String nroArmada)
			throws Exception {
		Desembolso desembolso = null;
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId + " and NroArmada="+nroArmada+"\"}";		
		String obtenerInversionesxPedidoURL = tableLiquidacionDesembolsoURL+Constantes.Service.URL_WHERE;
		
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

	@Override
	public String eliminarLiquidacionInversion(String inversionId,
			String nroArmada) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		
		String serviceWhere = "{\"where\":\"InversionId="+inversionId+" and NroArmada="+nroArmada+"\"}";	
		String eliminarLiquidacionURL = tableLiquidacionDesembolsoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.deleteForObject(restTemplate,tokenCaspio,eliminarLiquidacionURL,Object.class,request,serviceWhere);	
		return null;
	}

	public String actualizarEstadoLiquDesembInversion(String inversionId, String nroArmada, String estado) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Estado", estado);		
		
		String serviceWhere = "{\"where\":\"InversionId="+inversionId+" and NroArmada="+nroArmada+"\"}";	
		String registrarDesembolsoURL = tableLiquidacionDesembolsoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,registrarDesembolsoURL,Object.class,request,serviceWhere);	
		return null;
	}
}
