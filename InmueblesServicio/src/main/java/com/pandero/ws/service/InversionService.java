package com.pandero.ws.service;

import java.util.List;
import java.util.Map;

import com.pandero.ws.bean.ComprobanteCaspio;
import com.pandero.ws.bean.Garantia;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.InversionRequisito;

public interface InversionService {

	public void setTokenCaspio(String token);
	
	public Inversion obtenerInversionCaspioPorId(String inversionId) throws Exception;
	public Inversion obtenerInversionCaspioPorNro(String nroInversion) throws Exception;
	
	public String actualizarEstadoInversionCaspio(String inversionId, String estadoInversion) throws Exception;	
	public String actualizarEstadoInversionCaspioPorNro(String nroInversion, String estadoInversion) throws Exception;
	public String actualizarEstadoInversionLiquidadoPorNro(String nroInversion, String nroLiquidacion, String estadoInversion) throws Exception;
	public String actualizarSituacionConfirmadoInversionCaspio(String inversionId, String situacionConfirmado) throws Exception;

	public String crearRequisitoInversion(String inversionId, String requisitoId) throws Exception;
	public List<InversionRequisito> obtenerRequisitosPorInversion(String inversionId) throws Exception;
	public String actualizarEstadoInversionRequisitoCaspio(String inversionId,String estadoInversionReq) throws Exception;
	
	public List<Inversion> listarPedidoInversionPorPedidoId(String pedidoId) throws Exception;
	
	public List<ComprobanteCaspio> getComprobantes(Integer inversionId, Integer nroArmada) throws Exception;
	
	public String actualizarComprobanteEnvioCartaContabilidad(String inversionId,String nroArmada,String fechaEnvio,String usuarioEnvio, String estado) throws Exception;
	public String recepcionarCargoContabilidad(String inversionId,String nroArmada, String fechaRecepcion,String usuarioRecepcion) throws Exception;
	public String anularRecepcionCargoContabilidad(String inversionId,String nroArmada, String usuario) throws Exception;

	public String envioCargoContabilidadActualizSaldo(String inversionId,String fechaEnvio,String usuarioEnvio) throws Exception;
	public String recepcionarCargoContabilidadActualizSaldo(String inversionId,String fechaRecepcion,String usuarioRecepcion) throws Exception;
	public String actualizarInversionGarantiaHipotecado(Garantia garantiaCaspio) throws Exception;
}
