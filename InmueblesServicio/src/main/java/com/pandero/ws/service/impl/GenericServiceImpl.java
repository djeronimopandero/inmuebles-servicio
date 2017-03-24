package com.pandero.ws.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.pandero.ws.dao.impl.SpringCache;
import com.pandero.ws.service.GenericService;
import com.pandero.ws.util.Constantes;
import com.pandero.ws.util.ServiceRestTemplate;

@Service
public class GenericServiceImpl extends GenericService {
	private static final Logger LOG = LoggerFactory
			.getLogger(GarantiaServiceImpl.class);

	@Autowired
	@Qualifier("restTemplate")
	private RestTemplate restTemplate;

	@Autowired
	private SpringCache springCache;

	private static final String HTTP_UNAUTHORIZED = "401 Unauthorized";

	@Override
	public void actualizarTablaCaspio(Map<String, Object> body,
			String tableURL, String where) throws Exception {
		String tokenCaspio = springCache.cacheCaspioServiceKey();
		try {
			ServiceRestTemplate.putForObject(restTemplate, tokenCaspio,
					tableURL + Constantes.Service.URL_WHERE, Object.class,
					body, where);
		} catch (Exception ex) {
			if (HTTP_UNAUTHORIZED.equals(ex.getMessage())) {
				springCache.removeCaspioKeyCache();
				actualizarTablaCaspio(body, tableURL, where);
			} else {
				throw new Exception(ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> obtenerTablaCaspio(String tableURL,
			String where) throws Exception {
		String tokenCaspio = springCache.cacheCaspioServiceKey();
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			mapResult = (Map<String, Object>) ServiceRestTemplate.getForObject(
					restTemplate, tokenCaspio, tableURL
							+ Constantes.Service.URL_WHERE, Object.class, null,
					where);
		} catch (HttpClientErrorException ex) {
			if (HTTP_UNAUTHORIZED.equals(ex.getMessage())) {
				springCache.removeCaspioKeyCache();
				return obtenerTablaCaspio(tableURL, where);
			} else {
				throw new Exception(ex);
			}
		}
		return (List<Map<String, Object>>) mapResult.get("Result");
	}

}
