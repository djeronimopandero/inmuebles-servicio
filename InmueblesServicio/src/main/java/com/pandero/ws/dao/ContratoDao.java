package com.pandero.ws.dao;

import com.pandero.ws.bean.Contrato;

public interface ContratoDao {

	public Contrato obtenerContratoSAF(String nroContrato) throws Exception;
}
