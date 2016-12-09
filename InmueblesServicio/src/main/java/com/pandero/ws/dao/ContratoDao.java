package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.ContratoSAF;

public interface ContratoDao {

	public ContratoSAF obtenerContratoSAF(String nroContrato) throws Exception;
	public List<ContratoSAF> getListContratoAlDia()throws Exception;
	public Double obtenerDiferenciaPrecioPorContrato(Integer contratoId)throws Exception;
	
}
