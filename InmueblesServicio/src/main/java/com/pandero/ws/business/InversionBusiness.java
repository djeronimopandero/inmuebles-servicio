package com.pandero.ws.business;

import com.pandero.ws.bean.DetalleDiferenciaPrecio;
import com.pandero.ws.bean.ResultadoBean;

public interface InversionBusiness {

	public String confirmarInversion(String inversionId, String situacionConfirmado, String usuarioId) throws Exception;
	public String eliminarInversion(String inversionId) throws Exception;

	public String registrarInversionRequisitos(String inversionId) throws Exception;
	public String anularVerificacion(String inversionId) throws Exception;
	public String generarCartaObservacion(String inversionId, String usuarioSAFId)throws Exception;
	public ResultadoBean getImporteComprobante(String inversionNumero, Integer nroArmada)throws Exception;
	
	public String actualizarEstadoInversionCaspioPorNro(String nroInversion, String estadoInversion) throws Exception;
	public String getURLCancelarComprobante(String inversionId)throws Exception;

}
