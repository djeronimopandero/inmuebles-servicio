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

import com.pandero.ws.bean.PersonaCaspio;
import com.pandero.ws.bean.PersonaSAF;
import com.pandero.ws.service.PersonaService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class PersonaServiceImpl implements PersonaService {

private static final Logger LOG = LoggerFactory.getLogger(PersonaServiceImpl.class);
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
		
	@Value("${url.service.table.persona}")
	private String tablePersonaURL;
	
	@Value("${url.service.view.descargaSeguro}")
	private String descargaSeguro;
	
	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
	
	@Override
	public PersonaCaspio obtenerPersonaCaspio(String tipoDocumento, String nroDocumento) throws Exception {
		PersonaCaspio personaCaspio = null;
		String serviceWhere = "{\"where\":\"TipoDocumento='" + tipoDocumento + "' and NroDocumento='"+nroDocumento+"'\"}";	
		String obtenerPedidoURL = tablePersonaURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapPersona = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapPersona!=null && mapPersona.size()>0){
	        			for(Object bean : mapPersona){
	        				String beanString = JsonUtil.toJson(bean);
	        				personaCaspio =  JsonUtil.fromJson(beanString, PersonaCaspio.class);
	        			}
	        		}        		
	        	}
	        }
        }
        
		return personaCaspio;
	}

	@Override
	public String crearPersonaCaspio(PersonaSAF persona) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("PersonaId", String.valueOf(persona.getPersonaID()));
		request.put("TipoDocumento", persona.getTipoDocumentoID());
		request.put("NroDocumento", persona.getPersonaCodigoDocumento());
		request.put("Nombres", persona.getNombre());
		request.put("ApellidoPaterno",  persona.getApellidoPaterno() );			
		request.put("ApellidoMaterno", persona.getApellidoMaterno());			
		request.put("RazonSocial",  persona.getRazonSocial());			
		request.put("TipoPersona", persona.getTipoPersona());		
        ServiceRestTemplate.postForObject(restTemplate,tokenCaspio,tablePersonaURL,Object.class,request,null);	
		return "SUCCESS";
	}
	
	@Override
	public Map<String,Object> obtenerViewDescargaSeguroCaspio(String pedidoId) throws Exception {
		String serviceWhere = "{\"where\":\"Garantia_pedidoId='" + pedidoId + "'\"}";		
		String obtenerInversionesxPedidoURL = descargaSeguro+Constantes.Service.URL_WHERE;
		Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerInversionesxPedidoURL,Object.class,null,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);     	
     	return JsonUtil.jsonToMap(response);        
	}
	

}
