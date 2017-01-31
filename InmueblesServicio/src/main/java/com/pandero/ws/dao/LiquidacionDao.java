package com.pandero.ws.dao;

import java.util.List;
import java.util.Map;

import com.pandero.ws.bean.ConceptoLiquidacion;
import com.pandero.ws.bean.LiquidacionSAF;
import com.pandero.ws.bean.SolicitudAutorizacion;

public interface LiquidacionDao {

	public List<LiquidacionSAF> obtenerLiquidacionesPorInversionSAF(String nroInversion) throws Exception;
	public List<LiquidacionSAF> obtenerLiquidacionPorPedidoSAF(String nroPedido) throws Exception;	
	public List<LiquidacionSAF> obtenerLiquidacionPorInversionArmada(String nroInversion, String nroArmada) throws Exception;
	
	public String obtenerCorrelativoLiquidacionSAF(String pedidoId) throws Exception;
	public String registrarLiquidacionInversionSAF(LiquidacionSAF liquidacionSAF, String usuarioId) throws Exception;
	public String eliminarLiquidacionInversionSAF(String nroInversion, String nroArmada, String usuarioId) throws Exception;	
	public String confirmarLiquidacionInversion(String nroInversion, String usuarioId) throws Exception;
	public String eliminarConformidadInversion(String nroInversion, String usuarioId) throws Exception;
	
	public List<ConceptoLiquidacion> obtenerConceptosLiquidacion(String nroInversion) throws Exception;
	public List<SolicitudAutorizacion> obtenerSolicitudesAutorizacionPorInversion(String nroInversion) throws Exception;
	
	public Map<String,Object> executeProcedure(Map<String,Object> parameters, String procedureName);
}
