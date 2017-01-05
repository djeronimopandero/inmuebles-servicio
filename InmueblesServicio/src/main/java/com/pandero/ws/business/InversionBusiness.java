package com.pandero.ws.business;

import java.util.LinkedHashMap;

import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.ResultadoBean;

public interface InversionBusiness {

	public String confirmarInversion(String inversionId, String situacionConfirmado, String usuarioId) throws Exception;
	public String eliminarInversion(String inversionId) throws Exception;

	public String registrarInversionRequisitos(String inversionId) throws Exception;
	public String anularVerificacion(String inversionId) throws Exception;
	public String generarCartaObservacion(String inversionId, String usuarioSAFId)throws Exception;
	public ResultadoBean getImporteComprobante(String inversionNumero, Integer nroArmada)throws Exception;
	
	public String actualizarEstadoInversionCaspioPorNro(String nroInversion, String estadoInversion) throws Exception;
	public String generarDocumentoDesembolso(String nroInversion, String usuarioSAFId) throws Exception;
	public String getURLCancelarComprobante(String inversionId)throws Exception;
	
	public ResultadoBean verificarRegistrarFacturas(String inversionId,String nroArmada) throws Exception;
	
	public ResultadoBean enviarCargoContabilidad(String inversionId,String nroArmada,String usuario, String usuarioId) throws Exception;
	public ResultadoBean anularCargoContabilidad(String inversionId,String nroArmada,String usuarioId) throws Exception;	
	public ResultadoBean grabarComprobantes(String inversionId,String nroArmada,String usuarioId) throws Exception;
	public String recepcionarCargoContabilidad(String inversionId,String nroArmada, String fechaRecepcion,String usuarioRecepcion) throws Exception;
	
	public String envioCargoContabilidadActualizSaldo(String inversionId,String usuarioEnvio) throws Exception;
	public String anularEnvioCargoContabilidadActualizSaldo(String inversionId,String usuario) throws Exception;
	public String recepcionarCargoContabilidadActualizSaldo(String inversionId,String fechaRecepcion,String usuarioRecepcion) throws Exception;

	public LiquidacionSAF obtenerUltimaLiquidacionInversion(String nroInversion) throws Exception;
	public LinkedHashMap<String,Object> getComprobanteResumen(String inversionNumero, Integer nroArmada) throws Exception;

}
	