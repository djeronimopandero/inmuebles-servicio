package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.Asociado;
import com.pandero.ws.bean.ContratoSAF;

public interface ContratoDao {

	public ContratoSAF obtenerContratoSAF(String nroContrato) throws Exception;	
	public Double obtenerDiferenciaPrecioPorContrato(String nroContrato)throws Exception;
	
	public List<ContratoSAF> getListContratoAlDia()throws Exception;
	public List<Asociado> obtenerAsociadosxContratoSAF(String nroContrato) throws Exception;
}
