package com.pandero.ws.business;


public interface InversionBusiness {

	public String confirmarInversion(String inversionId, String situacionConfirmado, String usuarioId) throws Exception;
	public String eliminarInversion(String inversionId) throws Exception;

	public String registrarInversionRequisitos(String inversionId) throws Exception;
	public void anularVerificacion(String inversionId) throws Exception;
	public void generarCartaObservacion(String inversionId, String usuarioSAFId)throws Exception;
	
}
