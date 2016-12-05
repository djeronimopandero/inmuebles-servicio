package com.pandero.ws.business;

public interface InversionBusiness {

	public String confirmarInversion(String inversionId, String situacionConfirmado) throws Exception;
	public String eliminarInversion(String inversionId) throws Exception;

}
