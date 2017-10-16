package com.pandero.ws.business;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pandero.ws.bean.Inversion;
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
	public Map<String,Object> generarDocumentoDesembolso(String nroInversion, String nroArmada, String usuarioSAFId) throws Exception;
	public String getURLCancelarComprobante(String inversionId)throws Exception;
	
	public ResultadoBean grabarComprobantes(String inversionId,String nroArmada,String usuarioId) throws Exception;
	public ResultadoBean verificarRegistrarFacturas(String inversionId,String nroArmada) throws Exception;
	
	public ResultadoBean enviarCargoContabilidad(String inversionId,String nroArmada,String usuario, String usuarioId) throws Exception;
	public ResultadoBean anularCargoContabilidad(String inversionId,String nroArmada,String usuarioId) throws Exception;		
	public String recepcionarCargoContabilidad(String inversionId,String nroArmada, String fechaRecepcion,String usuarioRecepcion) throws Exception;
	public String anularRecepcionCargoContabilidad(String inversionId, String nroArmada, String usuario)  throws Exception ;
	
	public String envioCargoContabilidadActualizSaldo(String inversionId,String usuarioEnvio) throws Exception;
	public String anularEnvioCargoContabilidadActualizSaldo(String inversionId,String usuario) throws Exception;
	public String recepcionarCargoContabilidadActualizSaldo(String inversionId,String fechaRecepcion,String usuarioRecepcion) throws Exception;
	public String anularRecepcionCargoContabilidadActualizSaldo(String inversionId, String usuario) throws Exception;
	
	public LiquidacionSAF obtenerUltimaLiquidacionInversionPorId(String inversionId) throws Exception;
	public LinkedHashMap<String,Object> getComprobanteResumen(String inversionNumero, Integer nroArmada) throws Exception;
	
	public boolean validarImporteComprobantesNoExcedaInversion(String inversionId, Integer nroArmada, Double importeIngresar)throws Exception;
	public ResultadoBean actualizarInversionMonto(Map<String,Object> params)throws Exception;

	public String generarActaEntrega(String nroInversion, String usuarioSAFId) throws Exception;
	
	public Map<String,Object> getLiquidacionDesembolso(Map<String,Object> params) throws Exception;
	public Map<String,Object> verificarConfirmacionEntrega(Map<String,Object> params)throws Exception;
	public Map<String,Object> confirmacionEntrega(Map<String,Object> params)throws Exception;
	
	public ResultadoBean actualizarInmuebleInversionHipotecado(String nroInversion, String check) throws Exception;
	public ResultadoBean crearCreditoGarantia(String nroInversion, String usuarioId) throws Exception;
	public ResultadoBean eliminarCreditoGarantia(String nroInversion, String usuarioId) throws Exception;
	
	public String validarInversionesRetiroAdjudicacion(String nroPedido) throws Exception;
	
	public void enviarCorreoVerificacion(String inversionId) throws Exception;

	public void enviarTest(String inversionId) throws Exception;
	
	public void enviarCorreoDesembolsoExcepcional(String inversionId,String nroArmada,Map<String,Object> params) throws Exception;
	
	public void enviarCorreoRegistroDesembolso(String nroInversion, String nroArmada ,String parcial) throws Exception;
	
	public List<Map<String,Object>> getListaInversionPorNroPedido(String nroPedido) throws Exception;

}
	
