package com.pandero.ws.service;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.ContratoSAF;

public interface ContratoService {

	public void setTokenCaspio(String token);
	
	public Contrato obtenerContratoCaspio(String nroContrato) throws Exception;
	public Contrato actualizarSituacionContratoCaspio(String nroContrato, String situacionId, String situacionNom, String fechaSituacion) throws Exception;
	public String actualizarAsociacionContrato(String nroContrato, String estadoAsociacion) throws Exception;
	public String crearContratoCaspio(ContratoSAF contrato) throws Exception;
}
