package com.pandero.ws.service;

import com.pandero.ws.bean.Desembolso;

public interface DesembolsoService {

	public String crearDesembolsoInversion(String inversionId, String nroArmada, String nroDesembolso) throws Exception;
	public Desembolso obtenerDesembolsoPorInversionArmada(String inversionId, String nroArmada) throws Exception;
	
	public void setTokenCaspio(String token);
}
