package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.LiquidacionSAF;

public interface LiquidacionDao {

	public List<LiquidacionSAF> obtenerLiquidacionPorInversionSAF(String nroInversion) throws Exception;
	public List<LiquidacionSAF> obtenerLiquidacionPorPedidoSAF(String nroPedido) throws Exception;
	
	public String obtenerCorrelativoLiquidacionSAF(String pedidoId) throws Exception;
	public String registrarLiquidacionInversionSAF(LiquidacionSAF liquidacionSAF, String usuarioId) throws Exception;
	public String eliminarLiquidacionInversionSAF(String nroInversion, String nroArmada, String usuarioId) throws Exception;
	
	public String confirmarLiquidacionInversion(String nroInversion, String usuarioId) throws Exception;
}
