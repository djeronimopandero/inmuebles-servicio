package com.pandero.ws.business.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.business.ContratoBusiness;
import com.pandero.ws.dao.ContratoDao;

/**
  * Proyecto: InmueblesServicio
  * @date	: 26 de nov. de 2016
  * @time	: 1:08:39 p. m.
  * @author	: Arly Fernandez.
 */
@Service
public class ContratoBusinessImpl implements ContratoBusiness{
	
	private static Logger LOGGER = LoggerFactory.getLogger(ContratoBusinessImpl.class);
	
	@Autowired
	private ContratoDao contratoDAO;
	
	/**
	 * @method	: getListContratoAlDia
	 * @date	: 26 de nov. de 2016
	 * @time	: 1:08:55 p. m.
	 * @author	: Arly Fernandez.
	 * @descripcion : Obtener los contratos con movimientos al dia	
	 */
	@Override
	public List<Contrato> getListContratoAlDia() {
		List<Contrato> listResult=null;
		try {
			listResult = contratoDAO.getListContratoAlDia();
		} catch (Exception e) {
			LOGGER.error("###getListContratoAlDia:", e);
		}
		return listResult;
	}

}
