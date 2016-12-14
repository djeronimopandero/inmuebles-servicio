package com.pandero.ws.business.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;
import com.pandero.ws.util.ServiceRestTemplate;

@Component
public class LiquidacionBusinessImpl implements LiquidacionBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(LiquidacionBusinessImpl.class);

	@Autowired
	InversionService inversionService;	
	@Autowired
	PedidoService pedidoService;
	@Autowired
	ConstanteService constanteService;
	
	@Override
	public String generarLiquidacionPorInversion(String inversionId)
			throws Exception {
		String tokenCaspio = ServiceRestTemplate.obtenerTokenCaspio();
		inversionService.setTokenCaspio(tokenCaspio);
		pedidoService.setTokenCaspio(tokenCaspio);
		constanteService.setTokenCaspio(tokenCaspio);
		
		// Obtener datos de la inversion
		Inversion inversion = inversionService.obtenerInversionCaspio(inversionId);
		
		// Obtener contratos del pedido
		
		
		
		return null;
	}
	
	private String generarLiquidacionArmada(String inversionId, int nroArmada){
		
		
		
		return null;
	}
}
