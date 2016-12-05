package com.pandero.ws.business;

import com.pandero.ws.bean.Inversion;

public interface InversionBusiness {

	public String confirmarInversion(String inversionId, String situacionConfirmado) throws Exception;
	public String eliminarInversion(String inversionId) throws Exception;

	public String registrarInversionRequisitos(String inversionId) throws Exception;
}
