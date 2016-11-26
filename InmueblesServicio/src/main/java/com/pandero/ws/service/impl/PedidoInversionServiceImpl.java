package com.pandero.ws.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.service.PedidoInversionService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class PedidoInversionServiceImpl implements PedidoInversionService {

	private static final Logger LOG = LoggerFactory
			.getLogger(PedidoInversionServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Value("${url.service.table.pedidoInversion}")
	private String tablePedidoInversionURL;

	public Inversion obtenerInversion(String inversionId) throws Exception {
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

}
