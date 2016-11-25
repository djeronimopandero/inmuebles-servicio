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

import com.pandero.ws.bean.Constante;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class ConstantesServiceImpl implements ConstanteService {

	private static final Logger LOG = LoggerFactory.getLogger(ConstantesServiceImpl.class);
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
		
	@Value("${url.service.view.listaTiposDocumento}")
	private String viewListaTiposDocumentoURL;
		
	@Override
	public List<Constante> obtenerListaDocumentosIdentidad() {
		List<Constante> listaConstantes = null;
		try{
			Map<String, String> request = new HashMap<String, String>();
			
	        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,viewListaTiposDocumentoURL,Object.class,request);
	     	String response = JsonUtil.toJson(jsonResult);	     	
	        if(response!=null && !response.isEmpty()){
		        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
		        if(responseMap!=null){
		        	Object jsonResponse = responseMap.get("Result");
		        	if(jsonResponse!=null){        		
		        		List mapConstantes = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
		        		if(mapConstantes!=null && mapConstantes.size()>0){
		        			listaConstantes = new ArrayList<Constante>();
		        			for(Object bean : mapConstantes){
		        				String beanString = JsonUtil.toJson(bean);
		        				Constante constante =  JsonUtil.fromJson(beanString, Constante.class);
		        				listaConstantes.add(constante);        				
		        			}
		        			System.out.println("listaConstantes:: "+listaConstantes.size());
		        		}        		
		        	}
		        }
		    }	        
		}catch(Exception e){
			LOG.error("ERROR obtenerListaDocumentosIdentidad::",e);
			e.printStackTrace();
		}
	    
		return listaConstantes;
	}

}
