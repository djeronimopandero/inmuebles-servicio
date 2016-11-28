package com.pandero.ws.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.util.Constantes;
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

	public Inversion obtenerInversionCaspio(String inversionId) throws Exception {
		Inversion pedidoInversion = null;
		String serviceWhere = "{\"where\":\"InversionId=" + inversionId
				+ "\"}";
		String obtenerDatosInversionURL = tablePedidoInversionURL
				+ Constantes.Service.URL_WHERE;
		System.out.println("obtenerDatosInversionURL:: "
				+ obtenerDatosInversionURL);

		Object response = ServiceRestTemplate.getForObject(restTemplate,
				obtenerDatosInversionURL, Object.class, null, serviceWhere);

		return pedidoInversion;
	}

	@Override
	public String actualizarEstadoInversionCaspio(String inversionId,
			String estadoInversion) throws Exception {
		Map<String, String> request = new HashMap<String, String>();
		request.put("Estado", estadoInversion);		
		
		String serviceWhere = "{\"where\":\"InversionId='" + inversionId + "'\"}";	
		String actualizarPedidoURL = tablePedidoInversionURL+Constantes.Service.URL_WHERE;
		
        ServiceRestTemplate.putForObject(restTemplate,actualizarPedidoURL,Object.class,request,serviceWhere);	
		return null;
	}

}
