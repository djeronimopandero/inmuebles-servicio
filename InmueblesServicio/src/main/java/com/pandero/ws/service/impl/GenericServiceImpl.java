package com.pandero.ws.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pandero.ws.service.GenericService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class GenericServiceImpl extends GenericService{
	private static final Logger LOG = LoggerFactory.getLogger(GarantiaServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;
	
	String tokenCaspio = "";
	public void setTokenCaspio(String token){
		tokenCaspio = token;
	}
	
	@Override
	public void actualizarTablaCaspio(Map<String,Object> body, String tableURL, String where) throws Exception{			
		tableURL = tableURL+Constantes.Service.URL_WHERE;	
        ServiceRestTemplate.putForObject(restTemplate,tokenCaspio,tableURL,Object.class,body,where);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String,Object>> obtenerTablaCaspio(String tableURL, String where) throws Exception{			
		tableURL = tableURL+Constantes.Service.URL_WHERE;	
		Map<String,Object> mapResult=(Map<String,Object>)ServiceRestTemplate.getForObject(restTemplate,tokenCaspio,tableURL,Object.class,null,where);
		return (List<Map<String,Object>>)mapResult.get("Result");
	}
	
}
