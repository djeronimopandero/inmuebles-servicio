package com.pandero.ws.business;

import java.util.Map;

import com.pandero.ws.bean.ResultadoBean;


public interface PedidoBusiness {

	public ResultadoBean registrarNuevoPedido (String nroContrato, String usuarioSAFId) throws Exception;
	public ResultadoBean eliminarPedido(String pedidoCaspioId, String nroPedido, String usuarioSAFId) throws Exception;
	
	public String agregarContratoPedido(String pedidoCaspioId, String nroContrato, String usuarioSAFId, String contratoId) throws Exception;
	public String eliminarContratoPedido(String pedidoCaspioId, String nroContrato, String usuarioSAFId) throws Exception;
	
	public String generarOrdenIrrevocablePedido(String pedidoId, String usuarioSAFId, String pedidoNumero) throws Exception;
	public Map<String,Object> contratoPedidoEnEvaluacionCrediticia(Map<String,Object> params) throws Exception;
}
