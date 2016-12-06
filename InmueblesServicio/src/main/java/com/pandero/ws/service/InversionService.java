package com.pandero.ws.service;

import java.util.List;

import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisitoCaspio;

public interface InversionService {

	public void setTokenCaspio(String token);
	
	public Inversion obtenerInversionCaspio(String inversionId) throws Exception;
	public String actualizarEstadoInversionCaspio(String inversionId, String estadoInversion) throws Exception;
	public String actualizarSituacionConfirmadoInversionCaspio(String inversionId, String situacionConfirmado) throws Exception;
	public List<InversionRequisitoCaspio> listInversionRequisitoPorIdInversion(String inversionId)throws Exception;
	public String actualizarEstadoInversionRequisitoCaspio(String inversionId,String estadoInversionReq) throws Exception ;
}
