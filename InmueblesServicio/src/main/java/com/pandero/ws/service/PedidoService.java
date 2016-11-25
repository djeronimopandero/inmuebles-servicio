package com.pandero.ws.service;

import java.util.List;

import com.pandero.ws.bean.Contrato;
import com.pandero.ws.bean.Inversion;


public interface PedidoService {

	public String crearPedidoCaspio(String asociadoId);
	public String agregarContratoPedidoCaspio(String pedidoId, String contratoId);
	
	public List<Contrato> obtenerContratosxPedidoCaspio(String pedidoId);
	public List<Inversion> obtenerInversionesxPedidoCaspio(String pedidoId);
}
