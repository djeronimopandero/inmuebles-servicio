package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;

public interface ContratoDao {

	public Contrato obtenerContratoSAF(String nroContrato) throws Exception;
	public List<ContratoSAF> getListContratoAlDia()throws Exception;
}
