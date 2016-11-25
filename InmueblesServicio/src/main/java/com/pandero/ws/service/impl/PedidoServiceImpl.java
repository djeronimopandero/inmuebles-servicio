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
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class PedidoServiceImpl implements PedidoService{

	private static final Logger LOG = LoggerFactory.getLogger(PedidoInversionServiceImpl.class);
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
		
	@Value("${url.service.table.pedido}")
	private String tablePedidoURL;
	@Value("${url.service.view.tablaPedidoContrato}")
	private String viewTablaPedidoContratoURL;
	@Value("${url.service.view.tablaDetalleInversion}")
	private String viewTablaDetalleInversionURL;
		
	@Override
	public String crearPedidoCaspio(String asociadoId) {
		try{
			Map<String, String> request = new HashMap<String, String>();
			request.put("AsociadoId", asociadoId);
			request.put("Fecha", "2016-11-21");
			request.put("Estado", "Prueba");
			request.put("Producto", "C5 - INMUEBLES");			
	        Object response=ServiceRestTemplate.postForObject(restTemplate,tablePedidoURL,Object.class,request);	
	        
		}catch(Exception e){
			LOG.error("ERROR crearPedidoCaspio::",e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String agregarContratoPedidoCaspio(String pedidoId, String contratoId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Contrato> obtenerContratosxPedidoCaspio(String pedidoId) {
		List<Contrato> listaContratos = null;		
		try{
			String serviceWhere = "{\"where\":\"PedidoId=" + pedidoId + "\"}";	
			String obtenerContratosxPedidoURL = viewTablaPedidoContratoURL+Constantes.Service.URL_WHERE;
			
	        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,obtenerContratosxPedidoURL,Object.class,serviceWhere);
	     	String response = JsonUtil.toJson(jsonResult);	     	
	        if(response!=null && !response.isEmpty()){
	        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapCertificados = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapCertificados!=null && mapCertificados.size()>0){
	        			listaContratos = new ArrayList<Contrato>();
	        			for(Object bean : mapCertificados){
	        				String beanString = JsonUtil.toJson(bean);
	        				Contrato contrato =  JsonUtil.fromJson(beanString, Contrato.class);
	        				listaContratos.add(contrato);
	        			}
	        			System.out.println("listaContratos:: "+listaContratos.size());
	        		}        		
	        	}
	        }
        }
	        
		}catch(Exception e){
			LOG.error("ERROR obtenerDatosInversion::",e);
			e.printStackTrace();
		}
        
		return listaContratos;
	}

	@Override
	public List<Inversion> obtenerInversionesxPedidoCaspio(String pedidoId) {
		List<Inversion> listaInversiones = null;
		try{
			String serviceWhere = "{\"where\":\"PedidoId=" + pedidoId + "\"}";		
			String obtenerInversionesxPedidoURL = viewTablaDetalleInversionURL+Constantes.Service.URL_WHERE;
			
	        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,obtenerInversionesxPedidoURL,Object.class,serviceWhere);
	     	String response = JsonUtil.toJson(jsonResult);	     	
	        if(response!=null && !response.isEmpty()){
		        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
		        if(responseMap!=null){
		        	Object jsonResponse = responseMap.get("Result");
		        	if(jsonResponse!=null){        		
		        		List mapInversiones = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
		        		if(mapInversiones!=null && mapInversiones.size()>0){
		        			listaInversiones = new ArrayList<Inversion>();
		        			for(Object bean : mapInversiones){
		        				String beanString = JsonUtil.toJson(bean);
		        				Inversion inversion =  JsonUtil.fromJson(beanString, Inversion.class);
		        				listaInversiones.add(inversion);			
		        			}
		        			System.out.println("listaInversiones:: "+listaInversiones.size());
		        		}        		
		        	}
		        }
		    }	        
		}catch(Exception e){
			LOG.error("ERROR obtenerDatosInversion::",e);
			e.printStackTrace();
		}
	    
		return listaInversiones;
	}

}
