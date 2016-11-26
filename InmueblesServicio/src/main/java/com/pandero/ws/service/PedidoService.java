package com.pandero.ws.service;

import java.util.List;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;
import com.pandero.ws.bean.Pedido;


public interface PedidoService {

	public String crearPedidoCaspio(String nroPedido, String asociadoId) throws Exception;
	public String agregarContratoPedidoCaspio(String pedidoId, String contratoId) throws Exception;
	public String actualizarEstadoPedidoCaspio(String nroPedido, String estadoPedido) throws Exception;
	
	public Pedido obtenerPedidoCaspio(String nroPedido) throws Exception;
	public List<Contrato> obtenerContratosxPedidoCaspio(String pedidoId) throws Exception;
	public List<Inversion> obtenerInversionesxPedidoCaspio(String pedidoId) throws Exception;
}
