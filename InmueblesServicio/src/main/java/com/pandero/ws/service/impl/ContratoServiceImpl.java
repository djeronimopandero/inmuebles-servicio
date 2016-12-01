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
import com.pandero.ws.bean.ContratoSAF;
import com.pandero.ws.service.ContratoService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;
import com.pandero.ws.util.Util;

@Service
public class ContratoServiceImpl implements ContratoService{

	private static final Logger LOG = LoggerFactory
			.getLogger(InversionServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${url.service.table.contrato}")
	private String tableContratoURL;

	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
	
	@Override
	public Contrato actualizarSituacionContratoCaspio(String nroContrato,
			String situacionId, String situacionNom, String fechaSituacion) throws Exception {		
		Map<String, String> request = new HashMap<String, String>();
		String situacion="";
		if(Util.esSituacionAdjudicado(situacionId)){
			situacion=Constantes.Contrato.SITUACION_ADJUDICADO;
		}else{
			situacion=Constantes.Contrato.SITUACION_NO_ADJUDICADO;
			situacionNom=Constantes.Contrato.SITUACION_NO_ADJUDICADO;
		}		
		request.put("Situacion", situacion);
		request.put("SituacionSAF", situacionNom);
		request.put("FechaAdjudicacion", fechaSituacion);
		
		String serviceWhere = "{\"where\":\"NroContrato='" + nroContrato + "'\"}";	
		String actualizarPedidoURL = tableContratoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);
		return null;
	}

	@Override
	public Contrato obtenerContratoCaspio(String nroContrato) throws Exception{
		Contrato contrato = null;
		String serviceWhere = "{\"where\":\"NroContrato='" + nroContrato + "'\"}";	
		String obtenerContratoURL = tableContratoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerContratoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapCertificados = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapCertificados!=null && mapCertificados.size()>0){
	        			for(Object bean : mapCertificados){
	        				String beanString = JsonUtil.toJson(bean);
	        				contrato =  JsonUtil.fromJson(beanString, Contrato.class);
	        			}
	        		}        		
	        	}
	        }
        }
        
		return contrato;
	}

	@Override
	public String actualizarAsociacionContrato(String nroContrato,
			String estadoAsociacion) throws Exception{
		Map<String, String> request = new HashMap<String, String>();	
		request.put("Estado", estadoAsociacion);
		
		String serviceWhere = "{\"where\":\"NroContrato='" + nroContrato + "'\"}";	
		String actualizarPedidoURL = tableContratoURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,actualizarPedidoURL,Object.class,request,serviceWhere);
		return null;
		
	}
	
	@Override
	public String crearContratoCaspio(ContratoSAF contrato) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("NroContrato", contrato.getNroContrato());
		request.put("FechaContrato", contrato.getFechaVenta());
		request.put("MontoCertificado", String.valueOf(contrato.getMontoCertificado()));
		request.put("MontoDisponible",  String.valueOf(contrato.getMontoDisponible())  );			
		request.put("AsociadoId", String.valueOf(contrato.getAsociadoId()) );			
		request.put("Situacion",  contrato.getSituacionContratoCASPIO() );			
		request.put("DiferenciaPrecio", String.valueOf(contrato.getDiferenciaPrecio()));			
		request.put("DiferenciaPrecioDisponible", String.valueOf(contrato.getDiferenciaPrecioDisponible()));			
		request.put("OtrosIngresos", String.valueOf(contrato.getOtrosIngresos()));			
		request.put("OtrosDisponibles", String.valueOf(contrato.getOtrosDisponibles()));			
		request.put("TotalDisponible", String.valueOf(contrato.getTotalDisponible()));			
		request.put("Estado", contrato.getEstado());			
		request.put("FechaAdjudicacion", contrato.getFechaAdjudicacion());			
		request.put("SituacionSAF", contrato.getSituacionContrato());			
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tableContratoURL,Object.class,request,null);	
		return "SUCCESS";
	}
	
}
