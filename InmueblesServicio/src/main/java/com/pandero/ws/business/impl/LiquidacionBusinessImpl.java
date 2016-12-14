package com.pandero.ws.business.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.business.LiquidacionBusiness;
import com.pandero.ws.service.ConstanteService;
import com.pandero.ws.service.InversionService;
import com.pandero.ws.service.PedidoService;

@Component
public class LiquidacionBusinessImpl implements LiquidacionBusiness{

	private static final Logger LOG = LoggerFactory.getLogger(LiquidacionBusinessImpl.class);

	@Autowired
	InversionService inversionService;
	@Autowired
	ConstanteService constanteService;
	@Autowired
	PedidoService pedidoService;

	
	@Override
	public String generarLiquidacionPorInversion(String inversionId)
			throws Exception {
		
		
		// Obtener datos de la inversion
		return null;
	}
	
	private String generarLiquidacionArmada(String inversionId, int nroArmada){
		
		
		
		return null;
	}
}
