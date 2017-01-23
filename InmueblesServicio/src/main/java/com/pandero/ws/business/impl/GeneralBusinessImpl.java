package com.pandero.ws.business.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pandero.ws.business.GeneralBusiness;
import com.pandero.ws.dao.GeneralDao;
import com.pandero.ws.util.UtilEnum;

@Component
public class GeneralBusinessImpl implements GeneralBusiness {
	
	private static final Logger LOG = LoggerFactory.getLogger(GeneralBusinessImpl.class);
	
	@Autowired
	GeneralDao generalDao;
	
	@Override
	public Double convertirSoles(Double montoDolares,String fechaEmision) throws Exception {
		LOG.info("###GeneralBusinessImpl.convertirSoles montoDolares:"+montoDolares);
		
		Double dboTipoCambio = generalDao.obtenerTipoCambio(UtilEnum.TIPO_CAMBIO.CONTABLE.getTexto(), fechaEmision);
		
		return montoDolares * dboTipoCambio;
	}

	@Override
	public Double convertirDolares(Double montoSoles,String fechaEmision) throws Exception {
		LOG.info("###GeneralBusinessImpl.convertirDolares montoSoles:"+montoSoles);
		
		Double dboTipoCambio = generalDao.obtenerTipoCambio(UtilEnum.TIPO_CAMBIO.CONTABLE.getTexto(), fechaEmision);
		return montoSoles / dboTipoCambio;
	}

}
