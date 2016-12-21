package com.pandero.ws.dao;

import java.util.List;

import com.pandero.ws.bean.LiquidacionSAF;

public interface LiquidacionDao {

	public List<LiquidacionSAF> obtenerLiquidacionPorInversionSAF(String nroInversion) throws Exception;
	public List<LiquidacionSAF> obtenerLiquidacionPorPedidoSAF(String nroPedido) throws Exception;
	
	public String obtenerCorrelativoLiquidacion(String pedidoId) throws Exception;
	public String registrarLiquidacionInversionSAF(LiquidacionSAF liquidacionSAF, String usuarioId) throws Exception;
}
