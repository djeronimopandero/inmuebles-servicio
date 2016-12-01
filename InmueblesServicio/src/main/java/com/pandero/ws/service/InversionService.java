package com.pandero.ws.service;

import com.pandero.ws.bean.Inversion;

public interface InversionService {

	public void setTokenCaspio(String token);
	
	public Inversion obtenerInversionCaspio(String inversionId) throws Exception;
	public String actualizarEstadoInversionCaspio(String inversionId, String estadoInversion) throws Exception;
	public String actualizarSituacionConfirmadoInversionCaspio(String inversionId, String situacionConfirmado) throws Exception;
}
