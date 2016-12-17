package com.pandero.ws.business;

import java.util.List;

import com.pandero.ws.bean.Contrato;

public interface LiquidacionBusiness {

	public List<Contrato> obtenerContratosPorPedidoActualizado(String nroPedido) throws Exception;
	
	public String generarLiquidacionPorInversion(String nroInversion) throws Exception;	
	
}
