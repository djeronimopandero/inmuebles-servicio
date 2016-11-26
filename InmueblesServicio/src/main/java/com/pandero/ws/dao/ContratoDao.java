package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.Contrato;

public interface ContratoDao {

	public Contrato obtenerContratoSAF(String nroContrato) throws Exception;
	public List<Contrato> getListContratoAlDia()throws Exception;
}
