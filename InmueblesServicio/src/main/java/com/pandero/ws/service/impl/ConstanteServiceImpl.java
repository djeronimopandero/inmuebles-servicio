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
import com.pandero.ws.bean.DocumentoRequisito;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.JsonUtil;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class ConstanteServiceImpl implements ConstanteService {

	private static final Logger LOG = LoggerFactory.getLogger(ConstanteServiceImpl.class);
	
	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
		
	@Value("${url.service.view.listaTiposDocumento}")
	private String viewListaTiposDocumentoURL;
	@Value("${url.service.view.listaDocumentos}")
	private String viewListaDocumentosURL;
	@Value("${url.service.table.documentoRequisito}")
	private String tableDocumentoRequisitoURL;
	@Value("${url.service.table.constantes}")
	private String tableConstantesURL;
			
	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
			
	@Override
	public List<Constante> obtenerListaDocumentosIdentidad() throws Exception {
		List<Constante> listaConstantes = null;
		Map<String, String> request = new HashMap<String, String>();
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,viewListaTiposDocumentoURL,Object.class,request,null);
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
	    
		return listaConstantes;
	}

	@Override
	public List<DocumentoRequisito> obtenerListaDocumentosPorTipoInversion(
			String tipoInversion) throws Exception {
		List<DocumentoRequisito> listaDocumentos = null;
		Map<String, String> request = new HashMap<String, String>();
		String serviceWhere = "{\"where\":\"TipoInversion='" + tipoInversion + "'\"}";	
		String obtenerDocumentoTipoInversionURL = viewListaDocumentosURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerDocumentoTipoInversionURL,Object.class,request,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
	        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapConstantes = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapConstantes!=null && mapConstantes.size()>0){
	        			listaDocumentos = new ArrayList<DocumentoRequisito>();
	        			for(Object bean : mapConstantes){
	        				String beanString = JsonUtil.toJson(bean);
	        				DocumentoRequisito constante =  JsonUtil.fromJson(beanString, DocumentoRequisito.class);
	        				listaDocumentos.add(constante);        				
	        			}
	        			System.out.println("listaDocumentos:: "+listaDocumentos.size());
	        		}        		
	        	}
	        }
	    }	
	    
		return listaDocumentos;
	}

	@Override
	public List<DocumentoRequisito> obtenerListaRequisitosPorTipoInversion(
			String tipoInversion) throws Exception {
		List<DocumentoRequisito> listaRequisitos = null;
		Map<String, String> request = new HashMap<String, String>();
		String serviceWhere = "{\"where\":\"TipoInversion='" + tipoInversion + "' and TipoDocumento='"+Constantes.DocumentoRequisito.TIPO_REQUISITO+"'\"}";	
		String obtenerRequisitosTipoInversionURL = tableDocumentoRequisitoURL+Constantes.Service.URL_WHERE;
		
        Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerRequisitosTipoInversionURL,Object.class,request,serviceWhere);
     	String response = JsonUtil.toJson(jsonResult);	     	
        if(response!=null && !response.isEmpty()){
	        Map<String, Object> responseMap = JsonUtil.jsonToMap(response);
	        if(responseMap!=null){
	        	Object jsonResponse = responseMap.get("Result");
	        	if(jsonResponse!=null){        		
	        		List mapConstantes = JsonUtil.fromJson(JsonUtil.toJson(jsonResponse), ArrayList.class);
	        		if(mapConstantes!=null && mapConstantes.size()>0){
	        			listaRequisitos = new ArrayList<DocumentoRequisito>();
	        			for(Object bean : mapConstantes){
	        				String beanString = JsonUtil.toJson(bean);
	        				DocumentoRequisito constante =  JsonUtil.fromJson(beanString, DocumentoRequisito.class);
	        				listaRequisitos.add(constante);        				
	        			}
	        			System.out.println("listaRequisitos:: "+listaRequisitos.size());
	        		}        		
	        	}
	        }
	    }	
	    
		return listaRequisitos;
	}

	@Override
	public List<Constante> obtenerListaArmadasDesembolso() throws Exception {
		List<Constante> listaConstantes = null;
		Map<String, String> request = new HashMap<String, String>();
		String serviceWhere = "{\"where\":\"TipoConstante='" + Constantes.GenLista.TIPO_ARMADAS_DESEMBOLSO + "'\"}";
		String obtenerConstantesArmadaDesembURL = tableConstantesURL+Constantes.Service.URL_WHERE;
	
		Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerConstantesArmadaDesembURL,Object.class,request,serviceWhere);
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
	        			System.out.println("listaConstantesArmadaDesemb:: "+listaConstantes.size());
	        		}        		
	        	}
	        }
	    }	
	    
		return listaConstantes;
	}

	@Override
	public List<Constante> obtenerListaTipoComprobante() throws Exception {
		List<Constante> listaConstantes = null;
		Map<String, String> request = new HashMap<String, String>();
		String serviceWhere = "{\"where\":\"TipoConstante='" + Constantes.GenLista.TIPO_TIPO_COMPROBANTE + "'\"}";
		String obtenerConstantesArmadaDesembURL = tableConstantesURL+Constantes.Service.URL_WHERE;
	
		Object jsonResult=ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,obtenerConstantesArmadaDesembURL,Object.class,request,serviceWhere);
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
	        			System.out.println("listaConstantesArmadaDesemb:: "+listaConstantes.size());
	        		}        		
	        	}
	        }
	    }	
	    
		return listaConstantes;
	}
	
}
