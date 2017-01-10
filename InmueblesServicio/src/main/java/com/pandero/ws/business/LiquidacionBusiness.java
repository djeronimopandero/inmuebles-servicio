package com.pandero.ws.business;

import java.util.List;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.DetalleDiferenciaPrecio;

public interface LiquidacionBusiness {

	public List<Contrato> obtenerTablaContratosPedidoActualizado(String nroPedido) throws Exception;
	public String actualizarTablaContratosPedido(String nroPedido) throws Exception;
	
	public String generarLiquidacionPorInversion(String nroInversion, String nroArmada, String usuarioId) throws Exception;	
	public String eliminarLiquidacionInversion(String nroInversion, String nroArmada, String usuarioId) throws Exception;
	
	public String confirmarLiquidacionInversion(String nroInversion, String nroArmada, String usuarioId) throws Exception;
	public DetalleDiferenciaPrecio obtenerMontosDifPrecioInversion(String nroInversion) throws Exception;
	
	public String actualizarDesembolsoCaspio(String nroInversion, String nroArmada, String nroDesembolso) throws Exception;
}
