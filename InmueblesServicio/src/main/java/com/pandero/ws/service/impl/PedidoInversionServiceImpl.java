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

	public Inversion obtenerDatosInversion(String inversionId) {
		Inversion pedidoInversion = null;
		try {
			String serviceWhere = "{\"where\":\"InversionId=" + inversionId
					+ "\"}";
			String obtenerDatosInversionURL = tablePedidoInversionURL
					+ Constantes.Service.URL_WHERE;
			System.out.println("obtenerDatosInversionURL:: "
					+ obtenerDatosInversionURL);

			Object response = ServiceRestTemplate.getForObject(restTemplate,
					obtenerDatosInversionURL, Object.class, serviceWhere);

		} catch (Exception e) {
			LOG.error("ERROR obtenerDatosInversion::", e);
			e.printStackTrace();
		}

		return pedidoInversion;
	}

}
