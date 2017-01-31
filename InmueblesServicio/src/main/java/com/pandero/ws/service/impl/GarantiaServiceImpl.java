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

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Garantia;
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.Seguro;
import com.pandero.ws.service.GarantiaService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class GarantiaServiceImpl implements GarantiaService{

	private static final Logger LOG = LoggerFactory.getLogger(GarantiaServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${url.service.table.garantia}")
	private String tableGarantiaURL;
	@Value("${url.service.table.seguro}")
	private String tableSeguroURL;
	
	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
	
	@Override
	public Garantia obtenerGarantiaPorId(String garantiaId) throws Exception {
		Garantia garantia = null;
		String serviceWhere = "{\"where\":\"idGarantia=" + garantiaId + "\"}";	
		String obtenerGarantiaURL = tableGarantiaURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerGarantiaURL,Object.class,null,serviceWhere);
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
	        				garantia =  JsonUtil.fromJson(beanString, Garantia.class);
	        				if(garantia!=null){
	        				}
	        			}
	        		}        		
	        	}
	        }
        }
        
		return garantia;
	}

	@Override
	public List<Garantia> obtenerGarantiasPorPedido(String pedidoId)
			throws Exception {
		List<Garantia> listaGarantias = null;	
		String serviceWhere = "{\"where\":\"pedidoId=" + pedidoId + "\"}";	
		String obtenerGarantiasxPedidoURL = tableGarantiaURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerGarantiasxPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapResultado = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapResultado!=null && mapResultado.size()>0){
	        			listaGarantias = new ArrayList<Garantia>();
	        			for(Object bean : mapResultado){
	        				String beanString = JsonUtil.toJson(bean);
	        				Garantia garantia =  JsonUtil.fromJson(beanString, Garantia.class);
	        				listaGarantias.add(garantia);
	        			}
	        			System.out.println("listaGarantias:: "+listaGarantias.size());
	        		}        		
	        	}
	        }
        }
        
		return listaGarantias;
	}

	@Override
	public String eliminarGarantiaPorId(String garantiaId) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		
		String serviceWhere = "{\"where\":\"idGarantia="+garantiaId+"\"}";	
		String eliminarGarantiaURL = tableGarantiaURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.deleteForObject(restTemplate,tokenCaspio,eliminarGarantiaURL,Object.class,request,serviceWhere);	
		return null;
	}

	@Override
	public String eliminarSeguro(String serviceWhere) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		
		String eliminarGarantiaURL = tableSeguroURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.deleteForObject(restTemplate,tokenCaspio,eliminarGarantiaURL,Object.class,request,serviceWhere);	
		return null;
	}
	
	
	@Override
	public List<Seguro> obtenerSegurosPorGarantiaId(String garantiaId)
			throws Exception {
		List<Seguro> listaSeguros = null;	
		String serviceWhere = "{\"where\":\"idGarantia=" + garantiaId + "\"}";	
		String obtenerSegurosxGarantiaURL = tableSeguroURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerSegurosxGarantiaURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapResultado = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapResultado!=null && mapResultado.size()>0){
	        			listaSeguros = new ArrayList<Seguro>();
	        			for(Object bean : mapResultado){
	        				String beanString = JsonUtil.toJson(bean);
	        				Seguro seguro =  JsonUtil.fromJson(beanString, Seguro.class);
	        				listaSeguros.add(seguro);
	        			}
	        			System.out.println("listaSeguros:: "+listaSeguros.size());
	        		}        		
	        	}
	        }
        }
        
		return listaSeguros;
	}
	
	@Override
	public String crearGarantiaInversionCaspio(Map<String, String> request)
			throws Exception {		
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tableGarantiaURL,Object.class,request,null);			
		return null;
	}
	
	
	@Override
	public String anularSeguroCaspio(Map<String,Object> params) throws Exception {		
		String serviceWhere = "{\"where\":\"creditoSeguroIdSAF="+params.get("seguroId")+"\"}";		
		String actualizarGarantiaURL = tableSeguroURL+Constantes.Service.URL_WHERE;
		params.remove("seguroId");
		params.remove("usuarioId");		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarGarantiaURL,Object.class,params,serviceWhere);	
		return null;
	}
	

}
