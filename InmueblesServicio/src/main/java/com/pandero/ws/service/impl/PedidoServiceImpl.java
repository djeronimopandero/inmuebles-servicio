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
import com.pandero.ws.bean.Pedido;
import com.pandero.ws.bean.PedidoContrato;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

@Service
public class PedidoServiceImpl implements PedidoService{

	private static final Logger LOG = LoggerFactory.getLogger(InversionServiceImpl.class);
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
		
	@Value("${url.service.table.pedido}")
	private String tablePedidoURL;
	@Value("${url.service.table.pedidoContrato}")
	private String tablePedidoContratoURL;
	@Value("${url.service.view.tablaPedidoContrato}")
	private String viewTablaPedidoContratoURL;
	@Value("${url.service.view.tablaDetalleInversion}")
	private String viewTablaDetalleInversionURL;
	
	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
		
	@Override
	public String crearPedidoCaspio(String nroPedido, String asociadoId) throws Exception{
		Map<String, String> request = new HashMap<String, String>();
		request.put("AsociadoId", asociadoId);
		request.put("NroPedido", nroPedido);
		request.put("Fecha", Util.getFechaActualYYYYMMDD());
		request.put("Estado", Constantes.Pedido.ESTADO_EMITIDO);
		request.put("Producto", Constantes.Producto.PRODUCTO_INMUEBLES);			
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tablePedidoURL,Object.class,request,null);	
		return null;
	}

	@Override
	public String agregarContratoPedidoCaspio(String pedidoId, String contratoId) throws Exception{
		Map<String, String> request = new HashMap<String, String>();
		request.put("PedidoId", pedidoId);
		request.put("ContratoId", contratoId);
		request.put("Estado", "1");			
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tablePedidoContratoURL,Object.class,request,null);	
		return null;
	}
	
	@Override
	public String actualizarEstadoPedidoCaspio(String nroPedido,
			String estadoPedido) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Estado", estadoPedido);		
		
		String serviceWhere = "{\"where\":\"NroPedido='" + nroPedido + "'\"}";	
		String actualizarPedidoURL = tablePedidoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}

	@Override
	public List<Contrato> obtenerContratosxPedidoCaspio(String pedidoId) throws Exception{
		List<Contrato> listaContratos = null;	
		String serviceWhere = "{\"where\":\"PedidoId=" + pedidoId + "\"}";	
		String obtenerContratosxPedidoURL = viewTablaPedidoContratoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerContratosxPedidoURL,Object.class,null,serviceWhere);
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
        
		return listaContratos;
	}
	
	public PedidoContrato obtenerPedidoPorNroContrato(String nroContrato) throws Exception{
		PedidoContrato pedidoContrato = null;	
		String serviceWhere = "{\"where\":\"NroContrato="+nroContrato+" and PedidoContratoEstado="+"'1'"+"\"}";
		String obtenerPedidoxContratoURL = viewTablaPedidoContratoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerPedidoxContratoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapPedido = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapPedido!=null && mapPedido.size()>0){
	        			pedidoContrato = new PedidoContrato();
	        			for(Object bean : mapPedido){
	        				String beanString = JsonUtil.toJson(bean);
	        				pedidoContrato =  JsonUtil.fromJson(beanString, PedidoContrato.class);
	        			}
	        		}        		
	        	}
	        }
        }
        return pedidoContrato;
	}

	@Override
	public List<Inversion> obtenerInversionesxPedidoCaspio(String pedidoId) throws Exception{
		List<Inversion> listaInversiones = null;
		String serviceWhere = "{\"where\":\"PedidoId=" + pedidoId + "\"}";		
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
	    
		return listaInversiones;
	}

	@Override
	public Pedido obtenerPedidoCaspio(String nroPedido) throws Exception {
		Pedido pedido = null;
		String serviceWhere = "{\"where\":\"NroPedido='" + nroPedido + "'\"}";	
		String obtenerPedidoURL = tablePedidoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapPedidos = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapPedidos!=null && mapPedidos.size()>0){
	        			for(Object bean : mapPedidos){
	        				String beanString = JsonUtil.toJson(bean);
	        				pedido =  JsonUtil.fromJson(beanString, Pedido.class);
	        			}
	        		}        		
	        	}
	        }
        }
        
		return pedido;
	}
	
	@Override
	public Pedido obtenerPedidoCaspioPorId(String pedidoId) throws Exception {
		Pedido pedido = null;
		String serviceWhere = "{\"where\":\"PedidoId=" + pedidoId + "\"}";	
		String obtenerPedidoURL = tablePedidoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapPedidos = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapPedidos!=null && mapPedidos.size()>0){
	        			for(Object bean : mapPedidos){
	        				String beanString = JsonUtil.toJson(bean);
	        				pedido =  JsonUtil.fromJson(beanString, Pedido.class);
	        			}
	        		}        		
	        	}
	        }
        }
        
		return pedido;
	}

	@Override
	public String eliminarContratoPedidoCaspio(String pedidoId,
			String contratoId) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		
		String serviceWhere = "{\"where\":\"PedidoId="+pedidoId+" and ContratoId="+contratoId+"\"}";	
		String actualizarPedidoURL = tablePedidoContratoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.deleteForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}
	
}
