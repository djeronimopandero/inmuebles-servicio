package com.pandero.ws.business;

import com.pandero.ws.bean.ResultadoBean;


public interface PedidoBusiness {

	public ResultadoBean registrarNuevoPedido (String nroContrato, String usuarioSAFId) throws Exception;
	
	public void generarOrdenIrrevocablePedido(String pedidoId) throws Exception;
}
