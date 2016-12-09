package com.pandero.ws.business;

import com.pandero.ws.bean.ResultadoBean;


public interface PedidoBusiness {

	public ResultadoBean registrarNuevoPedido (String nroContrato, String usuarioSAFId) throws Exception;
	public ResultadoBean eliminarPedido(String pedidoCaspioId, String nroPedido, String usuarioSAFId) throws Exception;
	
	public ResultadoBean agregarContratoPedido(String pedidoCaspioId, String nroContrato, String usuarioSAFId) throws Exception;
	public ResultadoBean eliminarContratoPedido(String pedidoCaspioId, String nroContrato, String usuarioSAFId) throws Exception;
	
	public String generarOrdenIrrevocablePedido(String pedidoId, String usuarioSAFId, String pedidoNumero) throws Exception;
}
